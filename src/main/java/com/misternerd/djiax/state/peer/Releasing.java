package com.misternerd.djiax.state.peer;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.IaxPeer;
import com.misternerd.djiax.PeerConstants;
import com.misternerd.djiax.exception.InformationElementNotFoundException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.full.IaxFrameSubclass;
import com.misternerd.djiax.io.frame.ie.Challenge;
import com.misternerd.djiax.io.frame.ie.Md5Result;
import com.misternerd.djiax.io.frame.ie.Refresh;
import com.misternerd.djiax.io.frame.ie.Username;
import com.misternerd.djiax.state.AbstractClientPeerState;

/**
 * State is reached from Registered by sending REGREL. From here, either
 * Unregistered or NoAuth can be reached.
 */
public class Releasing extends AbstractClientPeerState
{

	private static final Logger logger = LoggerFactory.getLogger(Releasing.class);


	public Releasing(IaxPeer peer)
	{
		super(peer);
	}


	/**
	 * We expect either a REGAUTH or an ACK.
	 */
	@Override
	public void receiveFrame(FullFrame frame)
	{
		if (frame instanceof IaxFrame)
		{
			IaxFrame iaxFrame = (IaxFrame) frame;

			switch (iaxFrame.getIaxClass())
			{
				case ACK:
				{
					handleReleasingAckFrame(iaxFrame);
					return;
				}
				case REGACK:
				{
					handleReleasingRegAckFrame(iaxFrame);
					return;
				}
				case REGAUTH:
				{
					handleReleasingRegAuthFrame(frame, iaxFrame);
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


	protected void handleReleasingRegAuthFrame(FullFrame frame, IaxFrame iaxFrame)
	{
		peer.setFrameReplied(IaxFrameSubclass.REGREL.getSubclass());

		long regrelTimestamp = peer.getLastRegisteredTimestamp();

		IaxFrame reply = new IaxFrame(peer.getSourceCallNumber(), false, frame.getSourceCallNumber(), regrelTimestamp, iaxFrame.getISeqNo(), (short) (iaxFrame.getOSeqNo() + 1),
				IaxFrameSubclass.REGREL);
		try
		{
			reply.addInformationElement(new Username(peer.getPeerConfiguration().username));
			Challenge challenge = (Challenge) iaxFrame.getInformationElement(InformationElementType.CHALLENGE);
			reply.addInformationElement(new Md5Result(challenge.getChallengeData(), peer.getPeerConfiguration().password));
		}
		catch (InformationElementNotFoundException | UnsupportedEncodingException e)
		{
			logger.error("Failed to get challenge from Server, don't sending a MD5 result");
		}
		reply.addInformationElement(new Refresh(PeerConstants.PEER_REGISTRATION_REFRESH));

		peer.sendFrame(reply, false, true);
		peer.getPeerConfiguration().setRegRelTimestamp(regrelTimestamp);
	}


	protected void handleReleasingRegAckFrame(IaxFrame iaxFrame)
	{
		peer.setFrameReplied(IaxFrameSubclass.REGREL.getSubclass());
		peer.setPeerState(new Unregistered(peer));

		logger.info("Peer={} has successfully unregistered from server by REGACK.", peer.getPeerName());

		sendAckForFullFrame(iaxFrame);
	}


	protected void handleReleasingAckFrame(IaxFrame iaxFrame)
	{
		long regrelSentTimestamp = peer.getPeerConfiguration().getRegRelTimestamp();

		if (iaxFrame.getTimestamp() == regrelSentTimestamp)
		{
			logger.info("Peer={} has successfully unregistered from server by ACK", peer.getPeerName());
			peer.setFrameReplied(IaxFrameSubclass.REGREL.getSubclass());
			peer.setPeerState(new Unregistered(peer));
		}
	}

}