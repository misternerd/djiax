package com.misternerd.djiax;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.exception.PeerException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.MiniFrame;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.full.IaxFrameSubclass;
import com.misternerd.djiax.io.frame.ie.Refresh;
import com.misternerd.djiax.io.frame.ie.Username;
import com.misternerd.djiax.state.AbstractClientPeerState;
import com.misternerd.djiax.state.peer.RegSent;
import com.misternerd.djiax.state.peer.Releasing;
import com.misternerd.djiax.state.peer.Unregistered;
import com.misternerd.djiax.util.MediaFormat;

/**
 * This class shapes an IAX2 client. A client is only able to connect to a
 * server, but cannot work with incoming connections.
 */
public class IaxPeer
{

	private static final Logger logger = LoggerFactory.getLogger(IaxPeer.class);

	private String peerName;

	private short peerSourceCallNumber;

	/**
	 * This represents the next expected inbound stream
	 * sequence number.
	 */
	private Short inboundSeqNumber = 0;

	private Short outboundSeqNumber = 0;

	private AbstractClientPeerState peerState;

	private long peerLastRegisteredTimestamp;

	private long lastFrameReceivedTimestamp;

	private IaxClientObserver peerObserver;

	private PeerConfiguration peerConfiguration;

	private Call[] activeCalls;

	private Hashtable<Short, Call> callByDestinationNumberLookup;

	private FrameRetransmitTask frameRetransmitTask;

	private Timer peerRetransmitTimer;

	private PeerSocketThread socketReceiverThread;

	private long nextSourceCallNumber = 0;

	private int numberOfActiveCalls = 0;
	
	private ExecutorService callExecutorService;


	protected IaxPeer(String host, int port, String username, String password,
			int maxNumberOfCalls, IaxClientObserver peerObserver, short peerSourceCallNumber)
			throws IOException
	{
		InetAddress inetAddress = InetAddress.getByName(host);

		this.peerName = String.format("peer-%d", peerSourceCallNumber);
		this.peerObserver = peerObserver;
		this.peerSourceCallNumber = peerSourceCallNumber;
		this.activeCalls = new Call[maxNumberOfCalls];
		this.callByDestinationNumberLookup = new Hashtable<Short, Call>();
		this.peerConfiguration = new PeerConfiguration(peerName, username, password, inetAddress, port, maxNumberOfCalls);
		this.peerLastRegisteredTimestamp = System.currentTimeMillis() - 5;
		this.peerRetransmitTimer = new Timer(peerName + "-timer");
		this.socketReceiverThread = new PeerSocketThread(this, peerConfiguration);
		this.frameRetransmitTask = new FrameRetransmitTask(this, socketReceiverThread, peerObserver);
		this.callExecutorService = Executors.newFixedThreadPool(maxNumberOfCalls + 1);
	}
	
	
	public void connect() throws PeerException
	{
		socketReceiverThread.start();
		peerRetransmitTimer.schedule(frameRetransmitTask, 1000, 1000);
		
		setPeerState(new Unregistered(this));

		try
		{
			setPeerState(new RegSent(this));
			
			IaxFrame frame = new IaxFrame(peerSourceCallNumber, false, (short) 0, this.getLastRegisteredTimestamp(), this.getOSeqNo(), this.getISeqNo(), IaxFrameSubclass.REGREQ);
			frame.addInformationElement(new Username(peerConfiguration.username));
			frame.addInformationElement(new Refresh(PeerConstants.PEER_REGISTRATION_REFRESH));
			
			sendFrame(frame, false, true);
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error("Failed to send REGREQ frame to server:", e);
			throw new PeerException("Failed to connect to server");
		}
	}


	public void handleIncomingMiniFrame(MiniFrame miniFrame)
	{
		if (miniFrame == null)
		{
			return;
		}

		Call call = callByDestinationNumberLookup.get(miniFrame.getSourceCallNumber());

		if (call != null)
		{
			call.receiveFrame(miniFrame);
		}
	}


	public void handleIncomingFullFrame(FullFrame receivedFrame)
	{
		if (receivedFrame == null)
		{
			return;
		}

		if (receivedFrame.getDestinationCallNumber() < PeerConstants.PEER_MAX_SOURCE_CALL_NUMBER)
		{
			handleIncomingFullFrameForPeer(receivedFrame);
		}
		else
		{
			handleIncomingFullFrameForCall(receivedFrame);
		}
	}


	protected void handleIncomingFullFrameForPeer(FullFrame receivedFrame)
	{
		boolean receivedInOrder;
		if (receivedFrame instanceof IaxFrame)
		{
			IaxFrame iaxFrame = (IaxFrame) receivedFrame;

			if (iaxFrame.getIaxClass() == IaxFrameSubclass.ACK)
			{
				if (frameRetransmitTask.framesAwaitingAckQueue.containsKey(iaxFrame.getTimestamp()))
				{
					frameRetransmitTask.framesAwaitingAckQueue.remove(iaxFrame.getTimestamp());
				}
				else
				{
					logger.info("Received ACK frame for non-existing frame with timestamp={}", iaxFrame.getTimestamp());
				}

				receivedInOrder = true;
			}
			else if (iaxFrame.getIaxClass() == IaxFrameSubclass.INVAL || iaxFrame.getIaxClass() == IaxFrameSubclass.TXACC || 
					iaxFrame.getIaxClass() == IaxFrameSubclass.TXCNT || iaxFrame.getIaxClass() == IaxFrameSubclass.VNAK)
			{
				receivedInOrder = true;
			}
			// other frames: check if they were received in order
			else
			{
				// not received in order
				if (iaxFrame.getTimestamp() <= lastFrameReceivedTimestamp)
				{
					receivedInOrder = false;
				}
				else
				{
					receivedInOrder = true;

					synchronized (inboundSeqNumber)
					{
						inboundSeqNumber++;
					}
				}
			}
		}
		// not an IAX frame
		else
		{
			if (receivedFrame.getTimestamp() <= lastFrameReceivedTimestamp)
			{
				receivedInOrder = false;
			}
			else
			{
				receivedInOrder = true;

				synchronized (inboundSeqNumber)
				{
					inboundSeqNumber++;
				}
			}
		}

		if (receivedInOrder == false)
		{
			logger.debug("Discarding out of order frame={} while inboundSequenceNumber={} ", receivedFrame, inboundSeqNumber);

			this.sendFrame(new IaxFrame(receivedFrame.getSourceCallNumber(), false, receivedFrame.getDestinationCallNumber(), receivedFrame.getTimestamp(), this.getOSeqNo(), inboundSeqNumber,
					IaxFrameSubclass.VNAK), true, false);

			return;
		}

		peerState.receiveFrame(receivedFrame);
	}


	protected void handleIncomingFullFrameForCall(FullFrame receivedFrame)
	{
		int callIndex = receivedFrame.getDestinationCallNumber() - PeerConstants.PEER_MAX_SOURCE_CALL_NUMBER;

		if (callIndex >= 0 && callIndex < peerConfiguration.maxNumberOfCalls)
		{
			Call call = activeCalls[callIndex];

			if (call != null)
			{
				if (call.getDestinationCallNumber() == 0)
				{
					if(receivedFrame instanceof IaxFrame)
					{
						call.setDestinationCallNumber(((IaxFrame) receivedFrame).getSourceCallNumber());
						callByDestinationNumberLookup.put(((IaxFrame) receivedFrame).getSourceCallNumber(), call);
					}
					else
					{
						logger.warn("Received frame for call={} which has no destination number!", call);
					}
				}
				
				call.receiveFrame(receivedFrame);
			}
			else
			{
				logger.warn("Received frame={} for unknown callIndex={}", receivedFrame, callIndex);
			}
		}
		else
		{
			logger.info("Received a FullFrame={} for call with invalid callNumber={}", receivedFrame, receivedFrame.getDestinationCallNumber());
		}
	}
	

	public synchronized void stopClient()
	{
		this.socketReceiverThread.stopThread();
		this.frameRetransmitTask.stopThread();

		for (Call call : activeCalls)
		{
			if (call != null)
			{
				call.callStop();
			}
		}

		peerRetransmitTimer.cancel();
		peerObserver.iaxClientOnDisconnect(this);
	}


	public short getISeqNo()
	{
		synchronized (inboundSeqNumber)
		{
			return inboundSeqNumber;
		}
	}


	public void resetISeqNo()
	{
		synchronized (inboundSeqNumber)
		{
			inboundSeqNumber = 0;
		}
	}


	public short getOSeqNo()
	{
		synchronized (outboundSeqNumber)
		{
			if (outboundSeqNumber + 1 < 0)
			{
				outboundSeqNumber = 0;
			}

			return outboundSeqNumber++;
		}
	}


	public void resetOSeqNo()
	{
		synchronized (outboundSeqNumber)
		{
			outboundSeqNumber = 0;
		}
	}


	public String getPeerName()
	{
		return peerName;
	}


	public AbstractClientPeerState getPeerState()
	{
		return peerState;
	}


	public synchronized void setPeerState(AbstractClientPeerState peerState)
	{
		try
		{
			if (this.peerState != null)
			{
				this.peerState.clear();
			}
		}
		catch (Throwable e)
		{
			logger.warn("Error clearing peer state:", e);
		}

		this.peerState = peerState;
	}


	/**
	 * Sends a FullFrame to the server. The second parameter defines, if this
	 * frame must be ack'd. If the second parameter is true, the frame will be
	 * retried until it has either been ack'd or the maximum retry count has
	 * been reached.
	 */
	public boolean sendFrame(FullFrame frame, boolean requiresAck, boolean requiresReply)
	{
		try
		{
			socketReceiverThread.sendFrame(frame);

			frame.updateNextRetransmitTimestamp();

			if (requiresAck == true)
			{
				frameRetransmitTask.framesAwaitingAckQueue.put(frame.getTimestamp(), frame);
			}
			else if (requiresReply == true)
			{
				frameRetransmitTask.framesAwaitingReplyQueue.put(frame.getSubclass(), frame);
			}

			return true;
		}
		catch (Exception e)
		{
			logger.warn("Could not send a FullFrame packet to the server: ", e);
			return false;
		}
	}


	public boolean sendFrame(MiniFrame frame)
	{
		try
		{
			socketReceiverThread.sendFrame(frame);
			return true;
		}
		catch (Exception e)
		{
			logger.warn("Could not send a MiniFrame packet to the server: ", e);
			return false;
		}
	}
	

	/**
	 * This will set a frame of a specific subclass as replied, thus removing it
	 * from the list of frames to retransmit.
	 */
	public void setFrameReplied(long subclass)
	{
		if (frameRetransmitTask.framesAwaitingReplyQueue.containsKey(subclass))
		{
			frameRetransmitTask.framesAwaitingReplyQueue.remove(subclass);
		}
		else
		{
			logger.warn("Cannot remove replied frame with subclass={} from queue with {} items", subclass, frameRetransmitTask.framesAwaitingReplyQueue.size());
		}
	}


	public void setFrameAckd(long subclass)
	{
		frameRetransmitTask.framesAwaitingAckQueue.remove(subclass);
	}


	public long getLastRegisteredTimestamp()
	{
		return (System.currentTimeMillis() - peerLastRegisteredTimestamp);
	}


	public void resetLastRegisteredTimestamp()
	{
		peerLastRegisteredTimestamp = System.currentTimeMillis() - 5;
	}


	public void unregisterFromServer() throws PeerException
	{
		this.setPeerState(new Releasing(this));

		try
		{
			long regrelTimestamp = this.getLastRegisteredTimestamp();
			IaxFrame regrelFrame = new IaxFrame(peerSourceCallNumber, false, (short) 0, regrelTimestamp, (short) 0, (short) 0, IaxFrameSubclass.REGREL);
			regrelFrame.addInformationElement(new Username(peerConfiguration.username));
	
			this.sendFrame(regrelFrame, false, true);
			
			peerConfiguration.setRegRelTimestamp(regrelTimestamp);
		}
		catch(UnsupportedEncodingException e)
		{
			logger.error("Failed to unregister from server", e);
			throw new PeerException("Failed to unregister from server");
		}
	}


	/**
	 * Request a new call. The call will only be created but not started yet. If
	 * this returns null, the call could not be created because of the size
	 * limit.
	 */
	public Call createCall(String number, MediaFormat[] audioCodecs)
	{
		synchronized (activeCalls)
		{
			if (numberOfActiveCalls >= peerConfiguration.maxNumberOfCalls)
			{
				logger.warn("Cannot create call, activeCalls={}, maximum={}", numberOfActiveCalls, peerConfiguration.maxNumberOfCalls);
				return null;
			}

			int callIndex;

			for (int i = 0; i < peerConfiguration.maxNumberOfCalls; i++)
			{
				callIndex = (int) (nextSourceCallNumber++ % peerConfiguration.maxNumberOfCalls);

				if (activeCalls[callIndex] == null)
				{
					short sourceCallNumber = (short) (callIndex + PeerConstants.PEER_MAX_SOURCE_CALL_NUMBER);
					activeCalls[callIndex] = new Call(this, sourceCallNumber, number, audioCodecs, (short) 8);
					numberOfActiveCalls++;

					return activeCalls[callIndex];
				}
			}

			logger.warn("Failed to create call, numberOfActiveCalls={}, max={}", numberOfActiveCalls, peerConfiguration.maxNumberOfCalls);

			return null;
		}
	}


	public int getNumberOfActiveCalls()
	{
		synchronized (activeCalls)
		{
			return numberOfActiveCalls;
		}
	}


	public Timer getRetransmitTimer()
	{
		return peerRetransmitTimer;
	}


	public short getSourceCallNumber()
	{
		return peerSourceCallNumber;
	}


	/**
	 * A call should call this so the peer nows that it can remove the call and
	 * re-assign the number.
	 */
	protected void callStopped(Call call)
	{
		callByDestinationNumberLookup.remove(call.getDestinationCallNumber());

		synchronized (activeCalls)
		{
			int callIndex = call.getSourceCallNumber() - PeerConstants.PEER_MAX_SOURCE_CALL_NUMBER;

			if (callIndex >= 0 && callIndex < peerConfiguration.maxNumberOfCalls && activeCalls[callIndex] == call)
			{
				activeCalls[callIndex] = null;
				numberOfActiveCalls--;

				return;
			}
		}

		logger.warn("Cannot remove call={} from open calls, non-existent", call);
	}


	public PeerConfiguration getPeerConfiguration()
	{
		return peerConfiguration;
	}


	public void startCallThreadForCall(Call call)
	{
		callExecutorService.submit(call);
	}

}