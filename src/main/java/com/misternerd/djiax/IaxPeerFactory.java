package com.misternerd.djiax;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.exception.NoPeerAvailableException;

public class IaxPeerFactory
{

	private static final Logger logger = LoggerFactory.getLogger(IaxPeerFactory.class);

	private static final Map<Short, IaxPeer> activePeers = new HashMap<>();

	private static Short nextPeerSourceCallNumber = 1;

	private static int maxNumberOfCallsPerPeer = 50;


	public static synchronized IaxPeer createNewPeer(String host, int port, String username, String password, 
			IaxClientObserver peerObserver) throws IOException, NoPeerAvailableException
	{
		for (int j = 0; j < PeerConstants.PEER_MAX_SOURCE_CALL_NUMBER; j++)
		{
			nextPeerSourceCallNumber++;

			if (nextPeerSourceCallNumber > PeerConstants.PEER_MAX_SOURCE_CALL_NUMBER)
			{
				nextPeerSourceCallNumber = 1;
			}

			if (!activePeers.containsKey(nextPeerSourceCallNumber))
			{
				IaxPeer iaxPeer = new IaxPeer(host, port, username, password, maxNumberOfCallsPerPeer, 
						peerObserver, nextPeerSourceCallNumber);
				activePeers.put(nextPeerSourceCallNumber, iaxPeer);

				return iaxPeer;
			}
		}

		logger.error("Failed to create new IaxPeer after {} tries, stopped at index {}", 
				PeerConstants.PEER_MAX_SOURCE_CALL_NUMBER, nextPeerSourceCallNumber);

		throw new NoPeerAvailableException();
	}


	protected synchronized void removePeer(IaxPeer peer)
	{
		IaxPeer peerInMap = activePeers.get(peer.getSourceCallNumber());

		if (peerInMap == null || peer != peerInMap)
		{
			logger.warn("Tried to remove peer with sourceCallNumber={}, but is not in list!", peer.getSourceCallNumber());
		}

		activePeers.remove(peer.getSourceCallNumber());
	}


	public static void setMaxNumberOfCallsPerPeer(int numberOfCalls)
	{
		maxNumberOfCallsPerPeer = numberOfCalls;
	}

}
