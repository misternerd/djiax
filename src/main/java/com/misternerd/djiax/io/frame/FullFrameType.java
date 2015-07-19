package com.misternerd.djiax.io.frame;

import java.util.HashMap;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;

public enum FullFrameType
{

	/**
	 * The frame carries a single digit of DTMF (Dual Tone Multi-Frequency).
	 */
	DTMF(0x01),
	/**
	 * The frame carries voice data.
	 */
	VOICE(0x02),
	/**
	 * The frame carries video data.
	 */
	VIDEO(0x03),
	/**
	 * The frame carries session control data, i.e., it refers to control of a
	 * device connected to an IAX endpoint.
	 */
	CONTROL(0x04),
	/**
	 * Frames with the Null value MUST NOT be transmitted.
	 */
	NULL(0x05),
	/**
	 * The frame carries control data that provides IAX protocol-specific
	 * endpoint management.
	 */
	IAXCONTROL(0x06),
	/**
	 * The frame carries a non-control text message in UTF-8 format.
	 */
	TEXT(0x07),
	/**
	 * The frame carries a single image.
	 */
	IMAGE(0x08),
	/**
	 * The frame carries HTML data.
	 */
	HTML(0x09),
	/**
	 * The frame carries comfort noise.
	 */
	COMFORTNOISE(0x0A);

	private static final HashMap<Short, FullFrameType> lookup = new HashMap<Short, FullFrameType>();

	static
	{
		for (FullFrameType type : FullFrameType.values())
		{
			lookup.put(type.frameType, type);
		}
	}

	private short frameType;


	private FullFrameType(int frameType)
	{
		this.frameType = (short) frameType;
	}


	public short getType()
	{
		return frameType;
	}


	public static FullFrameType getFrameTypeByNumber(short type) throws EnumReverseElementNotFoundException
	{
		if (lookup.containsKey(type))
		{
			return lookup.get(type);
		}

		throw new EnumReverseElementNotFoundException(type);
	}

}