package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the RR DELAY information element. From RFC 5456:
 * 
 * The purpose of the RR DELAY information element is to indicate the
 * maximum playout delay for a call, per [RFC3550].  The data field is 2
 * octets long and specifies the number of milliseconds a frame may be
 * delayed before it MUST be discarded.
 * 
 * The RR DELAY information element MAY be sent with IAX PONG messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x31     |      0x02     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    Maximum Playout Delay      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class RrDelay extends InformationElement
{

	public static final byte TYPE = 0x31;

	private long delay;


	public RrDelay(int delay)
	{
		super();

		ByteBuffer buffer = new ByteBuffer(4);
		buffer.put16bits(delay);

		this.delay = delay;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public RrDelay(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.delay = buffer.get16bits();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.RR_DELAY;
	}


	public long getDelay()
	{
		return delay;
	}


	@Override
	public String toString()
	{
		return String.format("RrDelay(delay=%d)", delay);
	}

}