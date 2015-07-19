package com.misternerd.djiax;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.io.frame.FullFrame;

class FrameRetransmitTask extends TimerTask
{

	private static final Logger logger = LoggerFactory.getLogger(FrameRetransmitTask.class);

	private IaxPeer iaxPeer;

	private PeerSocketThread peerSocket;

	private IaxClientObserver peerObserver;

	protected Hashtable<Long, FullFrame> framesAwaitingAckQueue;

	protected Hashtable<Long, FullFrame> framesAwaitingReplyQueue;

	private boolean threadRunning;


	public FrameRetransmitTask(IaxPeer iaxPeer, PeerSocketThread peerSocket, IaxClientObserver peerObserver)
	{
		super();
		this.iaxPeer = iaxPeer;
		this.peerSocket = peerSocket;
		this.peerObserver = peerObserver;
		this.framesAwaitingAckQueue = new Hashtable<>();
		this.framesAwaitingReplyQueue = new Hashtable<>();
		this.threadRunning = true;
	}


	public void stopThread()
	{
		this.threadRunning = false;
	}


	@Override
	public void run()
	{
		if (threadRunning == false)
		{
			return;
		}

		try
		{
			handleFramesAwaitingAck();
			handleFramesAwaitingReply();
		}
		catch (IOException e)
		{
			logger.warn("Failed to send retrying frame:", e);
		}
	}


	private void handleFramesAwaitingAck() throws IOException
	{
		Iterator<Entry<Long, FullFrame>> it1 = framesAwaitingAckQueue.entrySet().iterator();

		while (it1.hasNext())
		{
			FullFrame retryFrame = it1.next().getValue();

			if (retryFrame.getNextRetransmitTimestamp() > System.currentTimeMillis())
			{
				continue;
			}

			retryFrame.setRetransmitted(true);

			if (retryFrame.getRetransmitCount() >= PeerConstants.TRANSMISSION_MAX_RETRIES)
			{
				logger.error("Did not receive an ACK for frame={} after {} retries", retryFrame, retryFrame.getRetransmitCount() + " retries!");

				framesAwaitingAckQueue.remove(retryFrame.getTimestamp());
			}
			else if (retryFrame.getFullTimestamp() + PeerConstants.TRANSMISSION_RETRY_MAX_MSECS <= System.currentTimeMillis())
			{
				logger.error("Did not receive an ACK for frame={} after {} msecs", retryFrame, (System.currentTimeMillis() - retryFrame.getFullTimestamp()));

				peerObserver.iaxClientOnRetransmitError(iaxPeer, retryFrame);
				framesAwaitingAckQueue.remove(retryFrame.getTimestamp());
			}
			else
			{
				peerSocket.sendFrame(retryFrame);

				retryFrame.updateNextRetransmitTimestamp();
				retryFrame.setRetransmitCount(retryFrame.getRetransmitCount() + 1);
			}
		}
	}


	private void handleFramesAwaitingReply() throws IOException
	{
		Iterator<Entry<Long, FullFrame>> it2 = framesAwaitingReplyQueue.entrySet().iterator();

		while (it2.hasNext())
		{
			FullFrame retryFrame = it2.next().getValue();

			if (retryFrame.getNextRetransmitTimestamp() > System.currentTimeMillis())
			{
				continue;
			}

			retryFrame.setRetransmitted(true);

			if (retryFrame.getRetransmitCount() >= PeerConstants.TRANSMISSION_MAX_RETRIES)
			{
				logger.error("Did not receive a REPLY for frame={} after {} retries", retryFrame, retryFrame.getRetransmitCount());

				framesAwaitingReplyQueue.remove(retryFrame.getTimestamp());

				peerObserver.iaxClientOnReplyError(iaxPeer, retryFrame);
			}
			// over the max time limit
			else if (retryFrame.getFullTimestamp() + PeerConstants.TRANSMISSION_RETRY_MAX_MSECS <= System.currentTimeMillis())
			{
				logger.error("Did not receive a REPLY for frame={} after {} msecs ", retryFrame, (System.currentTimeMillis() - retryFrame.getFullTimestamp()));

				framesAwaitingReplyQueue.remove(retryFrame.getTimestamp());
				// TODO here, the peer should stop
			}
			else
			{
				peerSocket.sendFrame(retryFrame);
				retryFrame.updateNextRetransmitTimestamp();
				retryFrame.setRetransmitCount(retryFrame.getRetransmitCount() + 1);
			}
		}
	}

}