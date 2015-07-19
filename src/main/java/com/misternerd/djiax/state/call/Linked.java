package com.misternerd.djiax.state.call;

import com.misternerd.djiax.Call;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.full.ControlFrame;
import com.misternerd.djiax.state.AbstractCallState;

/**
 * The linked state handles messages after switching here from initial state and
 * waits until either the call is set up or for some reason not answered.
 */
public class Linked extends AbstractCallState
{

	public Linked(Call call)
	{
		super(call);
	}


	@Override
	public void receiveFrame(FullFrame frame)
	{
		if (frame instanceof ControlFrame)
		{
			ControlFrame controlFrame = (ControlFrame) frame;

			switch (controlFrame.getControlFrameSubclass())
			{
				case ANSWER:
				{
					call.setAudioRunning(true);
					call.setCallState(new Up(call));
					sendAckForFullFrame(frame);

					if (call.getCallObserver() != null)
					{
						call.getCallObserver().callAnswered(call);
					}

					return;
				}
				case BUSY:
				{
					call.setCallState(new Initial(call));
					sendAckForFullFrame(frame);

					if (call.getCallObserver() != null)
					{
						call.getCallObserver().callBusy(call);
					}

					return;
				}
				case CONGESTION:
				{
					call.setCallState(new Initial(call));
					sendAckForFullFrame(frame);

					if (call.getCallObserver() != null)
					{
						call.getCallObserver().callCongestion(call);
					}

					return;
				}
				case PROCEEDING:
				{
					sendAckForFullFrame(frame);

					if (call.getCallObserver() != null)
					{
						call.getCallObserver().callProceeding(call);
					}

					return;
				}
				case RINGING:
				{
					sendAckForFullFrame(frame);

					if (call.getCallObserver() != null)
					{
						call.getCallObserver().callRinging(call);
					}

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

}