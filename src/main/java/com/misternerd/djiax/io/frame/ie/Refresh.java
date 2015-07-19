package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the REFRESH information element. From RFC 5456:
 * 
 * The purpose of the REFRESH information element is to indicate the
 * number of seconds before an event expires.  Its data field is 2
 * octets long.
 * 
 * The REFRESH information element is used with IAX REGREQ, REGACK, and
 * DPREP messages.  When sent with a REGREQ, it is a request that the
 * peer maintaining the registration set the timeout to REFRESH seconds.
 * When sent with a DPREP or REGACK, it is informational and tells a
 * remote peer when the local peer will no longer consider the event
 * valid.  The REFRESH sent with a DPREP tells a peer how long it SHOULD
 * store the received dialplan response.
 * 
 * If the REFRESH information element is not received with a DPREP, the
 * expiration of the cache data is assumed to be 10 minutes.  If the
 * REFRESH information element is not received with a REGACK,
 * registration expiration is assumed to occur after 60 seconds.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x13     |      0x02     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  2 octets specifying refresh  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Refresh extends InformationElement
{

	public static final byte TYPE = 0x13;

	private int refresh;


	public Refresh(short refresh)
	{
		super();
		
		ByteBuffer buffer = new ByteBuffer(2);
		buffer.put16bits(refresh);
		
		this.refresh = refresh;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public Refresh(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.refresh = buffer.get16bits();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.REFRESH;
	}


	public int getRefresh()
	{
		return refresh;
	}


	@Override
	public String toString()
	{
		return String.format("Refresh(refresh=%d)", refresh);
	}

}