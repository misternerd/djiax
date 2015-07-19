package com.misternerd.djiax.io.frame;

import com.misternerd.djiax.util.ByteBuffer;

/**
 * This is the common denominator of all IAX frames. Currently, this library
 * only supports FullFrame and MiniFrame (no MetaFrames), so what's similar is
 * the starting F bit and the following source call number that identifies the
 * target.
 */
public abstract class FrameBase
{

	/**
	 * This 15-bit value specifies the call number the transmitting client uses
	 * to identify this call. The source call number for an active call MUST NOT
	 * be in use by another call on the same client. Call numbers MAY be reused
	 * once a call is no longer active, i.e., either when there is positive
	 * acknowledgment that the call has been destroyed or when all possible
	 * timeouts for the call have expired.
	 */
	protected short sourceCallNumber;


	protected FrameBase()
	{
	}


	public FrameBase(short sourceCallNumber)
	{
		this.sourceCallNumber = sourceCallNumber;
	}


	public FrameBase(byte[] data)
	{
		// MetaFrame (16 bits 0)
		if (data[0] == 0x00 && data[1] == 0x00)
		{
			this.sourceCallNumber = 0;
		}
		else
		{
			// first 7 bits of sourceCallNumber
			int tmp = (data[0] & 0x7F);

			tmp = tmp << 8;

			// second 8 bits of sourceCallNumber
			this.sourceCallNumber = (short) (tmp + (data[1] & 0xFF));
		}
	}


	public short getSourceCallNumber()
	{
		return sourceCallNumber;
	}


	public byte[] serialize() throws IndexOutOfBoundsException
	{
		// 2 bytes: F-bit + 15 bit sourceCallNumber
		ByteBuffer buffer = new ByteBuffer(2);

		buffer.put16bits(
				// F-bit first (bitmask 1000000000000000)
				(((this instanceof FullFrame) ? 1 : 0) & 0x8000)
				// add sourceCallNumber (bitmask 111111111111111)
				+ (sourceCallNumber & 0x7FFF));

		return buffer.getBuffer();
	}

}