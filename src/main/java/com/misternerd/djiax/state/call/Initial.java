package com.misternerd.djiax.state.call;

import com.misternerd.djiax.Call;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.state.AbstractCallState;

/**
 * The initial state is the first state a call is in. From here, only a NEW
 * message will lead somewhere.
 */
public class Initial extends AbstractCallState
{

	public Initial(Call call)
	{
		super(call);
	}


	@Override
	public void receiveFrame(FullFrame frame)
	{
		super.receiveFrame(frame);
	}

}