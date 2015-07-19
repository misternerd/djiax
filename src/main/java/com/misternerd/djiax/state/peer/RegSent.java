package com.misternerd.djiax.state.peer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.IaxPeer;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.state.AbstractClientPeerState;

/**
 * In this state, the peer awaits a server response to the REGREQ. Either a
 * REGAUTH (requires credentials) or a REGACK is expected.
 */
public class RegSent extends AbstractClientPeerState
{
	
	private static final Logger logger = LoggerFactory.getLogger(RegSent.class);
	
	private int authSentCounter;


	public RegSent(IaxPeer peer)
	{
		super(peer);
		this.authSentCounter = 0;
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
						handleRegAckFrame(iaxFrame);
						return;
					}
					case REGAUTH:
					{
						// only authorize for 10 times before failing
						if (authSentCounter++ > 10)
						{
							logger.error("Could not register to server, it seems our credentials are invalid!");
							peer.setPeerState(new NoAuth(peer));
							return;
						}
						
						handleRegAuthFrame(iaxFrame);
						return;
					}
					case REGREJ:
					{
						handleRegRejFrame(iaxFrame);
						peer.setPeerState(new Rejected(peer));
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
			logger.warn("Error handling frame={} with exception={}", frame, e);
		}
	}

}