package com.misternerd.djiax.state;


import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.IaxPeer;
import com.misternerd.djiax.PeerConstants;
import com.misternerd.djiax.exception.InformationElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.io.frame.MiniFrame;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.full.IaxFrameSubclass;
import com.misternerd.djiax.io.frame.ie.ApparentAddr;
import com.misternerd.djiax.io.frame.ie.Cause;
import com.misternerd.djiax.io.frame.ie.CauseCode;
import com.misternerd.djiax.io.frame.ie.Challenge;
import com.misternerd.djiax.io.frame.ie.Datetime;
import com.misternerd.djiax.io.frame.ie.Md5Result;
import com.misternerd.djiax.io.frame.ie.Refresh;
import com.misternerd.djiax.io.frame.ie.Username;
import com.misternerd.djiax.state.peer.Registered;
import com.misternerd.djiax.util.IaxUtil;

/**
 * This interface defines a client peer's state. The IAX2 protocol is state
 * based, thus all the states are defined as separate classes.
 */
public abstract class AbstractClientPeerState
{
	private static final Logger logger = LoggerFactory.getLogger(AbstractClientPeerState.class);

	protected IaxPeer peer;


	protected AbstractClientPeerState(IaxPeer peer)
	{
		this.peer = peer;
	}


	/**
	 * Called by client peer to stop all tasks within this state.
	 */
	public void clear() throws Throwable
	{
	}


	public void receiveFrame(FullFrame frame)
	{
		if (frame instanceof IaxFrame)
		{
			IaxFrame iaxFrame = (IaxFrame) frame;

			switch (iaxFrame.getIaxClass())
			{
				case ACK:
				{
					peer.setFrameAckd(iaxFrame.getSubclass());
					break;
				}
				case ACCEPT:
				{
					sendAckForFullFrame(frame);
					break;
				}
				case HANGUP:
				{
					logger.warn("Need to implement HANGUP event for client");

					sendAckForFullFrame(frame);
					break;
				}
				case LAGRP:
				{
					logger.debug("Current lag to server is {}", (peer.getLastRegisteredTimestamp() - iaxFrame.getTimestamp()));

					peer.setFrameReplied(IaxFrameSubclass.LAGRQ.getSubclass());
					sendAckForFullFrame(frame);
					break;
				}
				case LAGRQ:
				{
					peer.sendFrame(new IaxFrame(frame.getDestinationCallNumber(), false, frame.getSourceCallNumber(), frame.getTimestamp(), peer.getOSeqNo(), peer.getISeqNo(),
							IaxFrameSubclass.LAGRP), true, false);
					break;
				}
				// PING & POKE require a PONG
				case PING:
				case POKE:
				{
					peer.sendFrame(new IaxFrame(peer.getSourceCallNumber(), false, frame.getSourceCallNumber(), frame.getTimestamp(), (short) 0, (short) 1, IaxFrameSubclass.PONG), true, false);
					break;
				}
				// PONG just requires an ACK
				case PONG:
				{
					peer.setFrameReplied(IaxFrameSubclass.PING.getSubclass());
					peer.setFrameReplied(IaxFrameSubclass.POKE.getSubclass());
					sendAckForFullFrame(frame);
					break;
				}
				// if this slips here because of packet loss, send an ACK back
				case REGACK:
				{
					sendAckForFullFrame(frame);
					break;
				}
				default:
				{
					logger.warn("Received unhandled frame={} in state={}", frame, getClass());
				}
			}
		}
		else
		{
			logger.warn("Received unhandled non-IAX frame={}", frame);
		}
	}


	public void receiveFrame(MiniFrame frame)
	{
		logger.error("Received a MiniFrame in state={} with frame={}", getClass(), frame);
	}


	protected void sendAckForFullFrame(FullFrame frame)
	{
		peer.sendFrame(new IaxFrame(frame.getDestinationCallNumber(), false, frame.getSourceCallNumber(), 
				frame.getTimestamp(), frame.getOSeqNo(), frame.getISeqNo(), IaxFrameSubclass.ACK), false, false);
	}


	protected void handleRegAckFrame(IaxFrame iaxFrame)
	{
		peer.setFrameReplied(IaxFrameSubclass.REGREQ.getSubclass());
		peer.setPeerState(new Registered(peer));
		peer.getPeerConfiguration().setServerSourceCallNumber(iaxFrame.getSourceCallNumber());

		saveServerDatetimeToConfig(iaxFrame);
		saveServerRefreshToConfig(iaxFrame);
		saveServerApparentAddressToConfig(iaxFrame);

		sendAckForFullFrame(iaxFrame);
	}


	protected void handleRegAuthFrame(IaxFrame iaxFrame) 
			throws InformationElementNotFoundException, InvalidArgumentException, UnsupportedEncodingException
	{
		peer.setFrameReplied(IaxFrameSubclass.REGREQ.getSubclass());
		IaxFrame reply = new IaxFrame(peer.getSourceCallNumber(), false, iaxFrame.getSourceCallNumber(), peer.getLastRegisteredTimestamp(), peer.getOSeqNo(), peer.getISeqNo(), IaxFrameSubclass.REGREQ);
		reply.addInformationElement(new Username(peer.getPeerConfiguration().username));
		Challenge challenge = (Challenge) iaxFrame.getInformationElement(InformationElementType.CHALLENGE);
		reply.addInformationElement(new Refresh(PeerConstants.PEER_REGISTRATION_REFRESH));
		reply.addInformationElement(new Md5Result(challenge.getChallengeData(), peer.getPeerConfiguration().password));

		peer.sendFrame(reply, false, true);
	}


	protected void handleRegRejFrame(IaxFrame iaxFrame) throws UnsupportedEncodingException
	{
		peer.setFrameReplied(IaxFrameSubclass.REGREQ.getSubclass());

		Cause cause = IaxUtil.getCauseFromIaxFrame(iaxFrame);
		CauseCode causeCode = IaxUtil.getCauseCodeFromIaxFrame(iaxFrame);

		logger.warn("Registration has been rejected by server with code={} and info={}", causeCode.getCause(), cause.getCause());

		sendAckForFullFrame(iaxFrame);

		
	}


	protected void saveServerApparentAddressToConfig(IaxFrame iaxFrame)
	{
		try
		{
			ApparentAddr tmp = (ApparentAddr) iaxFrame.getInformationElement(InformationElementType.APPARENT_ADDR);
			peer.getPeerConfiguration().setServerApparentAddr(tmp);
		}
		catch (InformationElementNotFoundException e)
		{
		}
	}


	protected void saveServerRefreshToConfig(IaxFrame iaxFrame)
	{
		try
		{
			Refresh tmp = (Refresh) iaxFrame.getInformationElement(InformationElementType.REFRESH);
			peer.getPeerConfiguration().setServerRefresh(tmp.getRefresh());
		}
		catch (InformationElementNotFoundException e)
		{
		}
	}


	protected void saveServerDatetimeToConfig(IaxFrame iaxFrame)
	{
		try
		{
			Datetime tmp = (Datetime) iaxFrame.getInformationElement(InformationElementType.DATETIME);
			peer.getPeerConfiguration().setServerDatetime(tmp);
		}
		catch (InformationElementNotFoundException e)
		{
		}
	}

}
