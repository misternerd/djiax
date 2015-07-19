package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the VERSION information element. From RFC 5456:
 * 
 * The purpose of the VERSION information element is to indicate the
 * protocol version the peer is using.  Peers at each end of a call MUST
 * use the same protocol version.  Currently, the only supported version
 * is 2.  The data field of the VERSION information element is 2 octets
 * long.
 * 
 * 
 * The CAPABILITY information element is sent with IAX NEW messages if
 * appropriate for the CODEC negotiation method the peer is using.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x0b     |      0x02     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |            0x0002             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Version extends InformationElement
{

	public static final byte TYPE = 0x0B;

	private int version = 0x0002;


	public Version(short version) throws InvalidArgumentException
	{
		super();

		if (version != 0x0002)
		{
			throw new InvalidArgumentException(String.format("Version=%d is not supported yet!", version));
		}

		ByteBuffer buffer = new ByteBuffer(2);
		buffer.put16bits(version);

		this.version = version;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public Version(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.version = buffer.get16bits();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new InvalidArgumentException(e);
		}

		// debug: invalid version
		if (version != 0x0002)
		{
			throw new InvalidArgumentException(String.format("Version=%d is not supported yet!", version));
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.VERSION;
	}


	public int getVersion()
	{
		return version;
	}


	@Override
	public String toString()
	{
		return String.format("Version(version=%d)", version);
	}

}