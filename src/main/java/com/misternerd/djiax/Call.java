package com.misternerd.djiax;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.call.AudioListener;
import com.misternerd.djiax.call.CallObserver;
import com.misternerd.djiax.exception.CallException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.exception.InvalidMediaFormatException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.MiniFrame;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.full.IaxFrameSubclass;
import com.misternerd.djiax.io.frame.full.VoiceFrame;
import com.misternerd.djiax.io.frame.ie.CalledNumber;
import com.misternerd.djiax.io.frame.ie.CallingName;
import com.misternerd.djiax.io.frame.ie.Capability;
import com.misternerd.djiax.io.frame.ie.Format;
import com.misternerd.djiax.io.frame.ie.SamplingRate;
import com.misternerd.djiax.io.frame.ie.Username;
import com.misternerd.djiax.io.frame.ie.Version;
import com.misternerd.djiax.state.AbstractCallState;
import com.misternerd.djiax.state.call.Initial;
import com.misternerd.djiax.state.call.Waiting;
import com.misternerd.djiax.util.MediaFormat;
import com.misternerd.djiax.util.MediaFormat.FormatType;

/**
 * This class represents an IAX2 call leg between two peers. The call is
 * associated with a peer, through which it receives and sends all data traffic.
 */
public class Call implements Runnable
{
	
	private static final Logger logger = LoggerFactory.getLogger(Call.class);

	private String callName;

	private IaxPeer peer;

	private short sourceCallNumber;

	private short destinationCallNumber;

	private String calledNumber;

	private MediaFormat[] preferredAudioCodecs;

	private MediaFormat usedAudioCodec;

	private short samplingRate;

	private AbstractCallState abstractCallState;

	private long timestampCall;

	private long timestampLastFrameReceived = 0;

	/**
	 * Inbound sequence number, increases incrementally as Full Frames are
	 * received. At any time, it represents the next expected inbound stream
	 * sequence number.
	 */
	private AtomicInteger iSeqNumber = new AtomicInteger();

	private AtomicInteger oSeqNumber = new AtomicInteger();

	private Hashtable<Long, FullFrame> frameQueueAwaitingAck = new Hashtable<>();

	private Hashtable<Long, FullFrame> frameQueueAwaitingReply = new Hashtable<>();

	private Queue<FullFrame> inboundFullFrames = new LinkedList<>();
	
	private Queue<MiniFrame> inboundMiniFrames = new LinkedList<>();

	private MiniFrame voiceTransmitFrame;

	private boolean running;

	private CallObserver callObserver;

	private AudioListener audioListener;

	private boolean audioRunning;


	/**
	 * Create a new call. The call must be associated with a peer, the number to
	 * dial must be specified and a list of supported audio codecs (in
	 * descending preference) must be provided.

	 * @param audioCodecs List ordered by priority, first one is highest
	 * @param samplingRate The sampling rate in hz
	 */
	public Call(IaxPeer peer, short sourceCallNumber, String number, MediaFormat[] audioCodecs, short samplingRate) 
	{
		this.callName = String.format("peer-%d-call-%d", peer.getSourceCallNumber(), sourceCallNumber);
		this.peer = peer;
		this.sourceCallNumber = sourceCallNumber;
		this.destinationCallNumber = 0;
		this.calledNumber = number;
		this.preferredAudioCodecs = audioCodecs;
		this.usedAudioCodec = audioCodecs[0];
		this.samplingRate = samplingRate;
		this.abstractCallState = new Initial(this);
		this.timestampCall = System.currentTimeMillis() - 5;
	}


	/**
	 * Once the call has been created, it can be started via this method. This
	 * will send the initial frame to the peer and put the call in waiting
	 * state.
	 */
	public void callStart() throws CallException
	{
		this.running = true;
		peer.startCallThreadForCall(this);
		setCallState(new Waiting(this));

		try
		{
			IaxFrame newFrame = new IaxFrame(sourceCallNumber, false, (short) 0, this.getTimestampFull(), this.getOSeqNoAndIncrement(), this.getISeqNo(), IaxFrameSubclass.NEW);
			newFrame.addInformationElement(new Version((short) 2));
			newFrame.addInformationElement(new CallingName(peer.getPeerConfiguration().username));
			newFrame.addInformationElement(new Format(preferredAudioCodecs[0]));
			newFrame.addInformationElement(new Capability(preferredAudioCodecs));
			newFrame.addInformationElement(new SamplingRate(samplingRate));
			newFrame.addInformationElement(new Username(peer.getPeerConfiguration().username));
			newFrame.addInformationElement(new CalledNumber(calledNumber));
			
			this.sendFrame(newFrame, false, true);
		}
		catch (InvalidArgumentException | UnsupportedEncodingException e)
		{
			logger.error("Failed to create new call with exception:", e);
			throw new CallException("Failed to start call");
		}

	}


	/**
	 * Stop the current call. Retry- and Ping-Timer will be stopped and a hangup
	 * frame sent to the server.
	 */
	public void callStop()
	{
		this.running = false;

		setCallState(new Waiting(this));
		sendFrame(new IaxFrame(sourceCallNumber, false, destinationCallNumber, this.getTimestampFull(), this.getOSeqNoAndIncrement(), this.getISeqNo(), IaxFrameSubclass.HANGUP), true, false);

		peer.callStopped(this);

		if (callObserver != null)
		{
			callObserver.callHangup(this);
		}
	}


	/**
	 * This gets called by state to let the call know it has been stopped. We
	 * need to perform some cleanup here, like stopping timers or clearing the
	 * queue.
	 */
	public void callStoppedByState()
	{
		this.running = false;
		setAudioRunning(false);

		peer.callStopped(this);

		if (callObserver != null)
		{
			callObserver.callHangup(this);
		}
	}


	@Override
	public void run()
	{
		Thread.currentThread().setName(callName + "-ReceiverThread");

		long retransmitLastRun = System.currentTimeMillis();
		long pingLastRun = System.currentTimeMillis() + 5000;
		long timeUsed = System.currentTimeMillis();
		long timeSleep = PeerConstants.CALL_THREAD_SLEEP_TIME;

		try
		{
			while (running == true)
			{
				handleIncomingMiniFrames(4);
				handleOneIncomingFullFrame();
				
				if (retransmitLastRun + PeerConstants.CALL_TIME_BETWEEN_RETRANSMITS < System.currentTimeMillis())
				{
					retransmitFrames();
					retransmitLastRun = System.currentTimeMillis();
				}
				
				if (pingLastRun + PeerConstants.CALL_TIME_BETWEEN_PINGS < System.currentTimeMillis())
				{
					sendFrame(new IaxFrame(sourceCallNumber, false, destinationCallNumber, getTimestampFull(), 
					getOSeqNoAndIncrement(), getISeqNo(), IaxFrameSubclass.PING), false, true);
					pingLastRun = System.currentTimeMillis();
				}

				timeUsed = System.currentTimeMillis() - timeUsed;
				timeSleep = PeerConstants.CALL_THREAD_SLEEP_TIME - timeUsed;

				if (timeSleep > 0)
				{
					Thread.sleep(timeSleep);
				}

				timeUsed = System.currentTimeMillis();
			}
		}
		catch (InterruptedException e)
		{
		}
		
		logger.debug("Main thread for call={} is exiting", callName);
	}


	private void handleIncomingMiniFrames(int framesToHandle)
	{
		for(int i = 0; i < framesToHandle; i++)
		{
			MiniFrame miniFrame = inboundMiniFrames.poll();
			
			if(miniFrame == null)
			{
				return;
			}
			
			if (audioListener != null)
			{
				audioListener.callListenerReceivedAudioData(miniFrame.getData(), usedAudioCodec);
			}
		}
	}


	private void handleOneIncomingFullFrame()
	{
		FullFrame fullFrame = inboundFullFrames.poll();

		if (fullFrame != null)
		{
			boolean receivedInOrder = false;

			if (fullFrame instanceof IaxFrame)
			{
				IaxFrame iaxFrame = (IaxFrame) fullFrame;

				if (iaxFrame.getIaxClass() == IaxFrameSubclass.ACK)
				{
					frameQueueAwaitingAck.remove(iaxFrame.getTimestamp());
					receivedInOrder = true;
				}
				// The following don't need to change the sequence counter
				else if (iaxFrame.getIaxClass() == IaxFrameSubclass.INVAL || iaxFrame.getIaxClass() == IaxFrameSubclass.TXACC 
						|| iaxFrame.getIaxClass() == IaxFrameSubclass.TXCNT || iaxFrame.getIaxClass() == IaxFrameSubclass.VNAK)
				{
					receivedInOrder = true;
				}
				else
				{
					if (iaxFrame.getTimestamp() < timestampLastFrameReceived)
					{
						receivedInOrder = false;
					}
					else
					{
						receivedInOrder = true;
						iSeqNumber.incrementAndGet();
					}
				}
			}
			else
			{
				if (fullFrame.getTimestamp() < timestampLastFrameReceived)
				{
					receivedInOrder = false;
				}
				else
				{
					receivedInOrder = true;
					iSeqNumber.incrementAndGet();
				}
			}

			if (!receivedInOrder)
			{
				// notify peer via VNAK
				sendFrame(
						new IaxFrame(fullFrame.getSourceCallNumber(), false, fullFrame.getDestinationCallNumber(), 
								fullFrame.getTimestamp(), getOSeqNoAndIncrement(), (short)iSeqNumber.get(), IaxFrameSubclass.VNAK),
						true, false);

				logger.debug("Received out of order frame {} with sequenceNumber<{}", fullFrame, iSeqNumber);

				return;
			}

			abstractCallState.receiveFrame(fullFrame);
		}
	}


	private void retransmitFrames()
	{
		retransmitFramesAwaitingAck();
		retransmitFramesAwaitingReply();
	}


	protected void retransmitFramesAwaitingAck()
	{
		Enumeration<Long> retryKeys = frameQueueAwaitingAck.keys();

		while (retryKeys.hasMoreElements())
		{
			long retryFrameTimestamp = retryKeys.nextElement();
			FullFrame fullFrame = frameQueueAwaitingAck.get(retryFrameTimestamp);

			if (fullFrame.getNextRetransmitTimestamp() > System.currentTimeMillis())
			{
				continue;
			}

			fullFrame.setRetransmitted(true);

			//either transmitted too often or retransmit timeout
			if (fullFrame.getRetransmitCount() >= PeerConstants.TRANSMISSION_MAX_RETRIES
				|| fullFrame.getFullTimestamp() + PeerConstants.TRANSMISSION_RETRY_MAX_MSECS <= System.currentTimeMillis())
			{
				logger.error("No ACK for frame={} after {} retries and {} msecs", 
						new Object[]{fullFrame, fullFrame.getRetransmitCount(), (System.currentTimeMillis() - fullFrame.getFullTimestamp())});

				// TODO should the call stop here?
				frameQueueAwaitingAck.remove(retryFrameTimestamp);
			}
			else
			{
				logger.debug("Retransmitting ACK #{} for frame={}", fullFrame.getRetransmitCount(), fullFrame);

				sendFrame(fullFrame, false, false);

				fullFrame.updateNextRetransmitTimestamp();
				fullFrame.incRetransmitCount();
			}
		}
	}


	protected void retransmitFramesAwaitingReply()
	{
		Enumeration<Long> replyKeys = frameQueueAwaitingReply.keys();

		while (replyKeys.hasMoreElements())
		{
			long retryFrameTimestamp = replyKeys.nextElement();
			FullFrame fullFrame = frameQueueAwaitingReply.get(retryFrameTimestamp);

			if (fullFrame.getNextRetransmitTimestamp() > System.currentTimeMillis())
			{
				continue;
			}

			fullFrame.setRetransmitted(true);

			// either retransmitted too often or retransmit timeout
			if (fullFrame.getRetransmitCount() >= PeerConstants.TRANSMISSION_MAX_RETRIES
				|| fullFrame.getFullTimestamp() + PeerConstants.TRANSMISSION_RETRY_MAX_MSECS <= System.currentTimeMillis())
			{
				logger.warn("No REPLY for frame={} after {} retries and {} msecs", 
					new Object[]{fullFrame, fullFrame.getRetransmitCount(), (System.currentTimeMillis() - fullFrame.getFullTimestamp())});

				// TODO should the call stop here?
				frameQueueAwaitingReply.remove(fullFrame.getTimestamp());
			}
			else
			{
				logger.debug("Retransmitting REPLY #{} for frame={}", fullFrame.getRetransmitCount(), fullFrame);

				sendFrame(fullFrame, false, false);

				fullFrame.updateNextRetransmitTimestamp();
				fullFrame.incRetransmitCount();
			}
		}
	}


	public IaxPeer getPeer()
	{
		return peer;
	}


	public short getISeqNo()
	{
		return (short)iSeqNumber.get();
	}


	public void resetISeqNo()
	{
		iSeqNumber.set(0);
	}


	public short getOSeqNoAndIncrement()
	{
		return (short)oSeqNumber.getAndIncrement();
	}


	/**
	 * Retrieve the outbound sequence number without tampering it (for ACK).
	 */
	public short getOSeqNoUnchanged()
	{
		return (short)oSeqNumber.get();
	}


	public void resetOSeqNo()
	{
		oSeqNumber.set(0);
	}


	public short getSourceCallNumber()
	{
		return sourceCallNumber;
	}


	public short getDestinationCallNumber()
	{
		return destinationCallNumber;
	}


	public void setDestinationCallNumber(short destinationCallNumber) throws IllegalStateException
	{
		if (this.destinationCallNumber != 0)
		{
			logger.error("Tried setting sourceCallNumber={} for callName={}, already has one", destinationCallNumber, callName);
			throw new IllegalStateException("Failed to set new destinationCallNumber, call already has one");
		}

		this.destinationCallNumber = destinationCallNumber;
	}


	public AbstractCallState getCallState()
	{
		return abstractCallState;
	}


	public synchronized void setCallState(AbstractCallState state)
	{
		this.abstractCallState = state;
	}


	/**
	 * Sets the codec this call takes place in. This must be an audio codec and
	 * should only be called from Up state.
	 */
	public void setCodec(MediaFormat usedAudioCodec) throws InvalidMediaFormatException
	{
		if (usedAudioCodec == null || usedAudioCodec.getType() != FormatType.AUDIO)
		{
			throw new InvalidMediaFormatException(usedAudioCodec);
		}

		this.usedAudioCodec = usedAudioCodec;
	}


	public void setCallObserver(CallObserver callObserver)
	{
		this.callObserver = callObserver;
	}


	public CallObserver getCallObserver()
	{
		return callObserver;
	}


	public void setAudioListener(AudioListener audioListener)
	{
		this.audioListener = audioListener;
	}


	public AudioListener getAudioListener()
	{
		return audioListener;
	}


	/**
	 * Writes a chunk of audio data into this call. The data needs to be in the
	 * correct format, since no transcoding takes place.
	 */
	public void writeAudioData(byte[] data) throws IOException, IllegalStateException, InvalidArgumentException
	{
		if (audioRunning == false)
		{
			throw new IllegalStateException("The call " + sourceCallNumber + " is currently not accepting audio.");
		}

		// first audio frame needs to be a full frame
		if (voiceTransmitFrame == null)
		{
			this.sendFrame(new VoiceFrame(sourceCallNumber, false, destinationCallNumber, this.getTimestampFull(), this.getOSeqNoAndIncrement(), this.getISeqNo(), usedAudioCodec, data), true, false);

			voiceTransmitFrame = new MiniFrame();
			voiceTransmitFrame.setSourceCallNumber(sourceCallNumber);
		}
		// preceeding frames can be mini frames
		else
		{
			voiceTransmitFrame.setTimestamp(this.getTimestampMini());
			voiceTransmitFrame.setData(data);

			peer.sendFrame(voiceTransmitFrame);
		}
	}


	public void receiveFrame(FullFrame frame)
	{
		inboundFullFrames.add(frame);
	}


	public void receiveFrame(MiniFrame frame)
	{
		inboundMiniFrames.add(frame);
	}


	/**
	 * Sends a FullFrame to the server. The second parameter defines, if this
	 * frame must be ack'd. If the second parameter is true, the frame will be
	 * retried until it has either been ack'd or the maximum retry count has
	 * been reached.
	 */
	public void sendFrame(FullFrame frame, boolean requiresAck, boolean requiresReply)
	{
		if (peer.sendFrame(frame, false, false))
		{
			if (requiresAck == true)
			{
				frameQueueAwaitingAck.put(frame.getTimestamp(), frame);
			}
			else if (requiresReply == true)
			{
				frameQueueAwaitingReply.put(frame.getSubclass(), frame);
			}
		}
	}


	/**
	 * This will set a frame of a specific subclass as replied, thus removing it
	 * from the list of frames to retransmit.
	 */
	public void setFrameReplied(long subclass)
	{
		if (frameQueueAwaitingReply.containsKey(subclass))
		{
			frameQueueAwaitingReply.remove(subclass);
		}
	}


	/**
	 * This returns the time in millis since this peer has registered. It is
	 * used for sending FullFrames.
	 */
	public long getTimestampFull()
	{
		return (System.currentTimeMillis() - timestampCall);
	}


	/**
	 * Timestamp for miniframes, carries the lower 16 bits of the calls full
	 * timestamp.
	 */
	public int getTimestampMini()
	{
		return (int) ((System.currentTimeMillis() - timestampCall) & 0xFFFF);
	}


	public boolean isAudioRunning()
	{
		return audioRunning;
	}


	public void setAudioRunning(boolean audioRunning)
	{
		this.audioRunning = audioRunning;
		audioListener.callListenerSetAudioRunning(audioRunning);
	}


	@Override
	public String toString()
	{
		return String.format("Call(callName=%s, calledNumber=%s)", callName, calledNumber);
	}

}
