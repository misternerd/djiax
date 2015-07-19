package com.misternerd.djiax.state.peer;

import com.misternerd.djiax.IaxPeer;
import com.misternerd.djiax.state.AbstractClientPeerState;

/**
 * At first, the peer is in an unregistered state. From here, it should usually
 * try to send a REGREQ to register with the server. Also, a releasing message
 * will return the peer to this state.
 */
public class Unregistered extends AbstractClientPeerState
{

	public Unregistered(IaxPeer peer)
	{
		super(peer);
	}

}
