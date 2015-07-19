package com.misternerd.djiax.state.peer;

import com.misternerd.djiax.IaxPeer;
import com.misternerd.djiax.state.AbstractClientPeerState;

/**
 * This state is final and means that the client has no valid credentials. Once
 * in this state, the Peer should be shut down.
 */
public class NoAuth extends AbstractClientPeerState
{

	public NoAuth(IaxPeer peer)
	{
		super(peer);
	}

}
