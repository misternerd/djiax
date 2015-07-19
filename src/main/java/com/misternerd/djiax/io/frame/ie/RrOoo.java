package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the RR OOO information element. From RFC 5456:
 * 
 * The purpose of the RR OOO information element is to indicate the
 * number of frames received out of order for a call, per [RFC3550].
 * The data field is 4 octets long and carries the number of frames
 * received out of order.
 * 
 * The RR OOO information element MAY be sent with IAX PONG messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x33     |      0x04     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Frames Received       |
 * |          Out of Order         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class RrOoo extends InformationElement
{

	public static final byte TYPE = 0x33;

	private long count;


	public RrOoo(long count)
	{
		super();

		ByteBuffer buffer = new ByteBuffer(4);
		buffer.put32bits(count);

		this.count = count;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public RrOoo(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.count = buffer.get32bits();
		}
		catch (Exception e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.RR_OOO;
	}


	public long getCount()
	{
		return count;
	}


	@Override
	public String toString()
	{
		return String.format("RrOoo(count=%d)", count);
	}

}