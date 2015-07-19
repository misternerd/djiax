package com.misternerd.djiax.io.frame.full;

import java.util.HashMap;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;

public enum ControlFrameSubclass
{

	/**
	 * Proprietary class used when we get an invalid request from server.
	 */
	INVALID(0x00),
	/**
	 * The call has been hungup at the remote end
	 */
	HANGUP(0x01),
	/**
	 * Remote end is ringing (ring-back)
	 */
	RINGING(0x03),
	/**
	 * Remote end has answered
	 */
	ANSWER(0x04),
	/**
	 * Remote end is busy
	 */
	BUSY(0x05),
	/**
	 * The call is congested
	 */
	CONGESTION(0x08),
	/**
	 * Flash hook
	 */
	FLASH_HOOK(0x09),
	/**
	 * Device-specific options are being transmitted.
	 */
	OPTION(0x0b),
	/**
	 * Key Radio
	 */
	KEY_RADIO(0x0c),
	/**
	 * Unkey Radio
	 */
	UNKEY_RADIO(0x0d),
	/**
	 * Call is in progress
	 */
	PROGRESS(0x0e),
	/**
	 * Call is proceeding
	 */
	PROCEEDING(0x0f),
	/**
	 * Call is placed on hold
	 */
	HOLD(0x10),
	/**
	 * Call is taken off hold
	 */
	UNHOLD(0x11);

	private static final HashMap<Long, ControlFrameSubclass> lookup = new HashMap<>();

	static
	{
		for (ControlFrameSubclass subclass : ControlFrameSubclass.values())
		{
			lookup.put(subclass.subclass, subclass);
		}
	}

	private long subclass;


	private ControlFrameSubclass(int subclass)
	{
		this.subclass = subclass;
	}


	public long getSubclass()
	{
		return subclass;
	}


	public static ControlFrameSubclass reverse(long subclass) throws EnumReverseElementNotFoundException
	{
		if (lookup.containsKey(subclass))
		{
			return lookup.get(subclass);
		}

		throw new EnumReverseElementNotFoundException(subclass);
	}

}