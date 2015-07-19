package com.misternerd.djiax.state.call;


import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.Call;
import com.misternerd.djiax.PeerConstants;
import com.misternerd.djiax.exception.InformationElementNotFoundException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.full.IaxFrameSubclass;
import com.misternerd.djiax.io.frame.ie.Cause;
import com.misternerd.djiax.io.frame.ie.CauseCode;
import com.misternerd.djiax.io.frame.ie.Challenge;
import com.misternerd.djiax.io.frame.ie.Md5Result;
import com.misternerd.djiax.io.frame.ie.Refresh;
import com.misternerd.djiax.io.frame.ie.Username;
import com.misternerd.djiax.state.AbstractCallState;
import com.misternerd.djiax.util.IaxUtil;

/**
 * This state waits for the first response from the server. It handles
 * AUTHREQ/HANGUP/REJECT frames.
 */
public class Waiting extends AbstractCallState
{

	private static final Logger logger = LoggerFactory.getLogger(Waiting.class);

	private int authSentCounter;


	public Waiting(Call call)
	{
		super(call);
		this.authSentCounter = 0;
	}


	@Override
	public void receiveFrame(FullFrame frame)
	{
		if (frame instanceof IaxFrame)
		{
			IaxFrame iaxFrame = (IaxFrame) frame;

			switch (iaxFrame.getIaxClass())
			{
				case ACCEPT:
				{
					handleCallAccept(frame);
					return;
				}
				case AUTHREQ:
				{
					handleCallAuthRequested(frame, iaxFrame);
					return;
				}
				case REJECT:
				{
					handleCallRejected(frame, iaxFrame);
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


	private void handleCallAccept(FullFrame frame)
	{
		call.setCallState(new Linked(call));

		call.setFrameReplied(IaxFrameSubclass.AUTHREP.getSubclass());
		call.setFrameReplied(IaxFrameSubclass.NEW.getSubclass());

		sendAckForFullFrame(frame);
	}


	private void handleCallAuthRequested(FullFrame frame, IaxFrame iaxFrame)
	{
		call.setFrameReplied(IaxFrameSubclass.NEW.getSubclass());

		if (authSentCounter++ > 10)
		{
			logger.error("Server did not accept AUTHREQ after 10 retries for call " + call.getSourceCallNumber());
			call.setCallState(new Initial(call));
			return;
		}

		try
		{
			Challenge challenge = (Challenge) iaxFrame.getInformationElement(InformationElementType.CHALLENGE);
			
			IaxFrame reply = new IaxFrame(call.getSourceCallNumber(), false, frame.getSourceCallNumber(), 
					call.getTimestampFull(), call.getOSeqNoAndIncrement(), call.getISeqNo(), IaxFrameSubclass.AUTHREP);
			reply.addInformationElement(new Username(call.getPeer().getPeerConfiguration().username));
			reply.addInformationElement(new Refresh(PeerConstants.PEER_REGISTRATION_REFRESH));
			reply.addInformationElement(new Md5Result(challenge.getChallengeData(), 
					call.getPeer().getPeerConfiguration().password));
			
			call.sendFrame(reply, false, true);
		}
		catch (InformationElementNotFoundException | UnsupportedEncodingException e)
		{
			logger.error("Failed to get challenge from Server, not sending a MD5 result");
		}
	}


	private void handleCallRejected(FullFrame frame, IaxFrame iaxFrame)
	{
		call.setCallState(new Initial(call));
		call.setFrameReplied(IaxFrameSubclass.REGREQ.getSubclass());

		sendAckForFullFrame(frame);

		call.callStoppedByState();

		try
		{
			Cause cause = IaxUtil.getCauseFromIaxFrame(iaxFrame);
			CauseCode causeCode = IaxUtil.getCauseCodeFromIaxFrame(iaxFrame);
	
			logger.warn("New call with sourceCallNumber={} has been rejected with code={} and cause={}",
					new Object[]{call.getSourceCallNumber(), causeCode.getCause(), cause.getCause()});
		}
		catch(UnsupportedEncodingException e)
		{
			logger.error("Cannot decode cause with exception:", e);
		}
	}

}