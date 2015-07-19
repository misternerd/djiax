package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the RR DROPPED information element. From RFC 5456:
 * 
 * The purpose of the RR DROPPED information element is to indicate the
 * total number of dropped frames for a call, per [RFC3550].  The data
 * field is 4 octets long and carries the number of frames dropped.
 * 
 * The RR DROPPED information element MAY be sent with IAX PONG
 * messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x32     |      0x04     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Total Frames Dropped     |
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class RrDropped extends InformationElement
{

	public static final byte TYPE = 0x32;

	private long dropped;


	public RrDropped(long dropped)
	{
		super();

		ByteBuffer buffer = new ByteBuffer(4);
		buffer.put32bits(dropped);

		this.dropped = dropped;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public RrDropped(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.dropped = buffer.get32bits();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.RR_DROPPED;
	}


	public long getDropped()
	{
		return dropped;
	}


	@Override
	public String toString()
	{
		return String.format("RrDropped(dropped=%d)", dropped);
	}

}