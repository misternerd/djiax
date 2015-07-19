package com.misternerd.djiax.state;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.Call;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.full.IaxFrameSubclass;
import com.misternerd.djiax.state.call.Initial;

/**
 * This interface defines a call's state. The IAX2 protocol is state base, thus
 * all the states are defined as seperate classes.
 */
public abstract class AbstractCallState
{

	private static final Logger logger = LoggerFactory.getLogger(AbstractCallState.class);

	protected Call call;


	protected AbstractCallState(Call call)
	{
		this.call = call;
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
					return;
				}
				case HANGUP:
				{
					sendAckForFullFrame(frame);

					call.setCallState(new Initial(call));
					call.callStoppedByState();

					if (call.getCallObserver() != null)
					{
						call.getCallObserver().callHangup(call);
					}

					return;
				}
				case LAGRP:
				{
					logger.debug("Current lag to server is {}", (call.getTimestampFull() - iaxFrame.getTimestamp()));
					call.setFrameReplied(IaxFrameSubclass.LAGRQ.getSubclass());
					sendAckForFullFrame(frame);
					break;
				}
				case LAGRQ:
				{
					call.sendFrame(
							new IaxFrame(frame.getDestinationCallNumber(), false, frame.getSourceCallNumber(), 
								frame.getTimestamp(), call.getOSeqNoAndIncrement(), call.getISeqNo(), IaxFrameSubclass.LAGRP), true, false);
					break;
				}
				case PING:
				case POKE:
				{
					call.sendFrame(new IaxFrame(call.getSourceCallNumber(), false, call.getDestinationCallNumber(), 
						frame.getTimestamp(), call.getOSeqNoAndIncrement(), call.getISeqNo(), IaxFrameSubclass.PONG), true, false);

					break;
				}
				case PONG:
				{
					call.setFrameReplied(IaxFrameSubclass.PING.getSubclass());
					call.setFrameReplied(IaxFrameSubclass.POKE.getSubclass());
					sendAckForFullFrame(frame);
					break;
				}
				default:
				{
					logger.warn("Abstract implementation cannot handle frame={}", frame);
				}
			}
		}

		sendAckForFullFrame(frame);
	}


	protected void sendAckForFullFrame(FullFrame frame)
	{
		call.sendFrame(new IaxFrame(frame.getDestinationCallNumber(), false, frame.getSourceCallNumber(), 
				frame.getTimestamp(), call.getOSeqNoUnchanged(), frame.getOSeqNo(), IaxFrameSubclass.ACK), false, false);
	}

}