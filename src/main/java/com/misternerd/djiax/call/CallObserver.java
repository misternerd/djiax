package com.misternerd.djiax.call;

import com.misternerd.djiax.Call;

/**
 * A call observer retrieves information about all the important events and
 * states a call encounters. This enables the listener to handle specific
 * situations or react to events. A call can have multiple observers.
 */
public interface CallObserver
{

	/**
	 * The call is in Linked state and is proceeding.
	 */
	public void callProceeding(Call call);


	/**
	 * The call is in Linked state and is ringing.
	 */
	public void callRinging(Call call);


	/**
	 * The call has been answered and is now in Up state.
	 */
	public void callAnswered(Call call);


	/**
	 * The line to the requested peer is congested.
	 */
	public void callCongestion(Call call);


	/**
	 * Called when a call has been stopped (either by remote or local).
	 */
	public void callHangup(Call call);


	/**
	 * The other line is busy.
	 */
	public void callBusy(Call call);

}