package com.misternerd.djiax.state.peer;

import java.io.UnsupportedEncodingException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.IaxPeer;
import com.misternerd.djiax.PeerConstants;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.full.IaxFrameSubclass;
import com.misternerd.djiax.io.frame.ie.Refresh;
import com.misternerd.djiax.io.frame.ie.Username;
import com.misternerd.djiax.state.AbstractClientPeerState;

/**
 * If the peer receives a REGREJ, the client cannot register, thus reaching this
 * state.
 */
public class Rejected extends AbstractClientPeerState
{
	
	private class AuthRetryTask extends TimerTask
	{

		@Override
		public void run()
		{
			if (PeerConstants.REGISTRATION_REJECTED_NUMBER_OF_RETRIES == -1 || authRetriedCount++ < PeerConstants.REGISTRATION_REJECTED_NUMBER_OF_RETRIES)
			{
				peer.resetLastRegisteredTimestamp();
				peer.resetISeqNo();
				peer.resetOSeqNo();

				IaxFrame frame = createRegistrationFrame();

				peer.sendFrame(frame, true, false);
			}
			else
			{
				logger.warn("Could not register at server after {}retries, exiting.", authRetriedCount);

				retryTask.cancel();
				peer.stopClient();
			}
		}

		private IaxFrame createRegistrationFrame()
		{
			try
			{
				// registration frame with timestamp = 0
				IaxFrame frame = new IaxFrame(peer.getSourceCallNumber(), false, (short) 0, 0, 
						peer.getOSeqNo(), peer.getISeqNo(), IaxFrameSubclass.REGREQ);
				frame.addInformationElement(new Username(peer.getPeerConfiguration().username));
				frame.addInformationElement(new Refresh(PeerConstants.PEER_REGISTRATION_REFRESH));
				return frame;
			}
			catch(UnsupportedEncodingException e)
			{
				logger.error("Failed to send REGREQ to server:", e);
				return null;
			}
		}

	}

	private static final Logger logger = LoggerFactory.getLogger(Rejected.class);

	private int authRetriedCount;

	private TimerTask retryTask;

	
	public Rejected(IaxPeer clientPeer)
	{
		super(clientPeer);

		if (PeerConstants.REGISTRATION_REJECTED_NUMBER_OF_RETRIES != 0)
		{
			this.authRetriedCount = 0;
			this.retryTask = new AuthRetryTask();
			peer.getRetransmitTimer().schedule(retryTask, PeerConstants.REGISTRATION_REJECTED_RETRY_WAIT * 1000, PeerConstants.REGISTRATION_REJECTED_RETRY_WAIT * 1000);
		}
	}


	@Override
	public void clear() throws Throwable
	{
		super.clear();
		retryTask.cancel();
	}


	/**
	 * If we received a REGAUTH, we need to send a REGREQ with credentials. 
	 * If we get a REGACK, we can move on to Registered.
	 */
	@Override
	public void receiveFrame(FullFrame frame)
	{
		try
		{
			if (frame instanceof IaxFrame)
			{
				IaxFrame iaxFrame = (IaxFrame) frame;

				if (peer.getPeerConfiguration().getServerSourceCallNumber() == null)
				{
					peer.getPeerConfiguration().setServerSourceCallNumber(iaxFrame.getSourceCallNumber());
				}

				switch (iaxFrame.getIaxClass())
				{
					case REGACK:
					{
						if (retryTask != null)
						{
							retryTask.cancel();
						}
						
						handleRegAckFrame(iaxFrame);
						return;
					}
					case REGAUTH:
					{
						handleRegAuthFrame(iaxFrame);
						return;
					}
					case REGREJ:
					{
						handleRegRejFrame(iaxFrame);
						return;
					}
					default:
					{
						break;
					}
				}
			}

			super.receiveFrame(frame);
		}
		catch (Exception e)
		{
			logger.warn("Error handling frame " + frame, e);
		}
	}

}
