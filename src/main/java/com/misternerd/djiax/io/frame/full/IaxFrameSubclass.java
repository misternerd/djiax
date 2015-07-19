package com.misternerd.djiax.io.frame.full;

import java.util.HashMap;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;

public enum IaxFrameSubclass
{
	/**
	 * Initiate a new call
	 */
	NEW(0x01),
	/**
	 * Ping request
	 */
	PING(0x02),
	/**
	 * Ping or poke reply
	 */
	PONG(0x03),
	/**
	 * Explicit acknowledgment
	 */
	ACK(0x04),
	/**
	 * Initiate call tear-down
	 */
	HANGUP(0x05),
	/**
	 * Reject a call
	 */
	REJECT(0x06),
	/**
	 * Accept a call
	 */
	ACCEPT(0x07),
	/**
	 * Authentication request
	 */
	AUTHREQ(0x08),
	/**
	 * Authentication reply
	 */
	AUTHREP(0x09),
	/**
	 * Invalid message
	 */
	INVAL(0x0a),
	/**
	 * Lag request
	 */
	LAGRQ(0x0b),
	/**
	 * Lag reply
	 */
	LAGRP(0x0c),
	/**
	 * Registration request
	 */
	REGREQ(0x0d),
	/**
	 * Registration authentication
	 */
	REGAUTH(0x0e),
	/**
	 * Registration acknowledgement
	 */
	REGACK(0x0f),
	/**
	 * Registration reject
	 */
	REGREJ(0x10),
	/**
	 * Registration release
	 */
	REGREL(0x11),
	/**
	 * Video/Voice retransmit request
	 */
	VNAK(0x12),
	/**
	 * Dialplan request
	 */
	DPREQ(0x13),
	/**
	 * Dialplan reply
	 */
	DPREP(0x14),
	/**
	 * Dial
	 */
	DIAL(0x15),
	/**
	 * Transfer request
	 */
	TXREQ(0x16),
	/**
	 * Transfer connect
	 */
	TXCNT(0x17),
	/**
	 * Transfer accept
	 */
	TXACC(0x18),
	/**
	 * Transfer ready
	 */
	TXREADY(0x19),
	/**
	 * Transfer release
	 */
	TXREL(0x1a),
	/**
	 * Transfer reject
	 */
	TXREJ(0x1b),
	/**
	 * Halt audio/video [media] transmission
	 */
	QUELCH(0x1c),
	/**
	 * Resume audio/video [media] transmission
	 */
	UNQUELCH(0x1d),
	/**
	 * Poke request
	 */
	POKE(0x1e),
	/**
	 * Message waiting indication
	 */
	MWI(0x20),
	/**
	 * Unsupported message
	 */
	UNSUPPORT(0x21),
	/**
	 * Remote transfer request
	 */
	TRANSFER(0x22);

	private static final HashMap<Short, IaxFrameSubclass> lookup = new HashMap<>();

	static
	{
		for (IaxFrameSubclass subclass : IaxFrameSubclass.values())
		{
			lookup.put(subclass.subclass, subclass);
		}
	}

	private short subclass;


	private IaxFrameSubclass(int subclass)
	{
		this.subclass = (byte) subclass;
	}


	public short getSubclass()
	{
		return subclass;
	}


	public static IaxFrameSubclass reverse(short subclass) throws EnumReverseElementNotFoundException
	{
		if (lookup.containsKey(subclass))
		{
			return lookup.get(subclass);
		}

		throw new EnumReverseElementNotFoundException(subclass);
	}

}