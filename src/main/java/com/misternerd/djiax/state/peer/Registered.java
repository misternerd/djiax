package com.misternerd.djiax.state.peer;

import java.io.UnsupportedEncodingException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.IaxPeer;
import com.misternerd.djiax.PeerConstants;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.full.IaxFrameSubclass;
import com.misternerd.djiax.io.frame.ie.Refresh;
import com.misternerd.djiax.io.frame.ie.Username;
import com.misternerd.djiax.state.AbstractClientPeerState;

/**
 * The peer changes from RegSend into this state by retrieving a REGACK. A
 * REGREQ leads back to RegSent, a REGREL to Releasing.
 */
public class Registered extends AbstractClientPeerState
{
	
	private class RegistrationRefreshTask extends TimerTask
	{
		
		@Override
		public void run()
		{
			peer.resetISeqNo();
			peer.resetOSeqNo();

			try
			{
				IaxFrame registrationFrame = new IaxFrame(peer.getSourceCallNumber(), false, (short) 0, 
						peer.getLastRegisteredTimestamp(), peer.getOSeqNo(), peer.getISeqNo(), IaxFrameSubclass.REGREQ);
				registrationFrame.addInformationElement(new Username(peer.getPeerConfiguration().username));
				registrationFrame.addInformationElement(new Refresh(PeerConstants.PEER_REGISTRATION_REFRESH));
	
				peer.sendFrame(registrationFrame, false, true);
			}
			catch(UnsupportedEncodingException e)
			{
				logger.error("Failed to send REGREQ to server:", e);
			}

			peer.setPeerState(new RegSent(peer));
		}
		
	}

	private static final Logger logger = LoggerFactory.getLogger(Registered.class);
	
	private TimerTask registrationRefreshTask;


	public Registered(IaxPeer clientPeer)
	{
		super(clientPeer);

		int refreshValue = peer.getPeerConfiguration().getServerRefresh();
		this.registrationRefreshTask = new RegistrationRefreshTask();
		peer.getRetransmitTimer().schedule(registrationRefreshTask, refreshValue * 1000);
	}


	@Override
	public void clear() throws Throwable
	{
		super.clear();
		registrationRefreshTask.cancel();
	}

}
