package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the RR LOSS information element. From RFC 5456:
 * 
 * The purpose of the RR LOSS information element is to indicate the
 * number of lost frames on a call, per [RFC3550].  The data field is 4
 * octets long and carries the percentage of frames lost in the first
 * octet, and the count of lost frames in the next 3 octets.
 * 
 * The RR LOSS information element MAY be sent with IAX PONG messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x2f     |      0x04     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x2f     |      0x04     |
 * +-+-+-+-+-+-+-+-+  Loss Count   |
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class RrLoss extends InformationElement
{

	public static final byte TYPE = 0x2F;

	private short lossPercentage;

	private int lossCount;


	public RrLoss(short lossPercentage, int lossCount)
	{
		super();

		ByteBuffer buffer = new ByteBuffer(4);
		buffer.put8bits(lossPercentage);
		buffer.put24bits(lossCount);

		this.lossPercentage = lossPercentage;
		this.lossCount = lossCount;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public RrLoss(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.lossPercentage = buffer.get8bits();
			this.lossCount = buffer.get24bits();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.RR_LOSS;
	}


	/**
	 * Loss percentage as 1 byte unsigned integer.
	 */
	public short getLossPercentage()
	{
		return lossPercentage;
	}


	/**
	 * Loss count as a 3 byte unsigned integer.
	 */
	public int getLossCount()
	{
		return lossCount;
	}


	@Override
	public String toString()
	{
		return String.format("RrLoss(percentage=%d, count=%d)", lossCount, lossPercentage);
	}

}