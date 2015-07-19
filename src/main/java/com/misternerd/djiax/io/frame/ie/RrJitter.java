package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the RR JITTER information element. From RFC 5456:
 * 
 * The purpose of the Receiver Report (RR) JITTER information element is
 * to indicate the received jitter on a call, per [RFC3550].  The data
 * field is 4 octets long and carries the current measured jitter.
 * 
 * The RR JITTER information element MAY be sent with IAX PONG messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x2e     |      0x04     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Received Jitter       |
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class RrJitter extends InformationElement
{

	public static final byte TYPE = 0x2E;

	private long jitter;


	public RrJitter(long jitter)
	{
		super();
		
		ByteBuffer buffer = new ByteBuffer(4);
		buffer.put32bits(jitter);

		this.jitter = jitter;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public RrJitter(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.jitter = buffer.get32bits();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.RR_JITTER;
	}


	public long getJitter()
	{
		return jitter;
	}


	@Override
	public String toString()
	{
		return String.format("RrJitter(jitter=%d)", jitter);
	}

}