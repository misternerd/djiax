package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the RR PKTS information element. From RFC 5456:
 * 
 * The purpose of the RR PKTS information element is to indicate the
 * total number of frames received on a call, per [RFC3550].  The data
 * field is 4 octets long and carries the count of frames received.
 * 
 * The RR PKTS information element MAY be sent with IAX PONG messages.
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
public class RrPkts extends InformationElement
{

	public static final byte TYPE = 0x30;

	private long pkts;


	public RrPkts(long packets)
	{
		super();

		ByteBuffer buffer = new ByteBuffer(4);
		buffer.put32bits(packets);

		this.pkts = packets;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public RrPkts(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.pkts = buffer.get32bits();
		}
		catch (Exception e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.RR_PKTS;
	}


	public long getPkts()
	{
		return pkts;
	}


	@Override
	public String toString()
	{
		return String.format("RrPkts(pkts=%d)", pkts);
	}

}