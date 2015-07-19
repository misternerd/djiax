package com.misternerd.djiax;

import com.misternerd.djiax.io.frame.FrameBase;

/**
 * This interface shapes a peer observer for client peers. For each important
 * event a client peer receives, it will inform all its observers.
 */
public interface IaxClientObserver
{

	/**
	 * Called when the client successfully connects to the server.
	 */
	public void iaxClientOnConnect(IaxPeer client);


	/**
	 * Called when the client cannot connect to server anymore. This is NOT
	 * called when the client renews its credentials.
	 */
	public void iaxClientOnDisconnect(IaxPeer client);


	/**
	 * Called when the client fails to send a frame for X times.
	 */
	public void iaxClientOnRetransmitError(IaxPeer client, FrameBase frame);


	/**
	 * Called when a frame did not receive a reply on the umpteenth time.
	 */
	public void iaxClientOnReplyError(IaxPeer client, FrameBase frame);

}
