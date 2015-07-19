package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the ADSICPE information element. From RFC 5456:
 * 
 * The purpose of the ADSICPE information element is to indicate the CPE
 * (Customer Premises Equipment) ADSI (Analog Display Services
 * Interface) capability.  The data field of the ADSICPE information
 * element is 2 octets long.
 * 
 * The ADSICPE information element MAY be sent with an IAX NEW message.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x0c     |      0x02     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       ADSICPE Capability      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Adsipce extends InformationElement
{

	public static final byte TYPE = 0x0C;

	private int adsipce;


	public Adsipce(short adsipce)
	{
		super();

		ByteBuffer buffer = new ByteBuffer(2);
		buffer.put16bits(adsipce);
	
		this.adsipce = adsipce;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public Adsipce(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.adsipce = buffer.get16bits();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.ADSICPE;
	}


	public int getAdsipce()
	{
		return adsipce;
	}


	public String toString()
	{
		return String.format("Adsipce(adsipce=%d)", adsipce);
	}

}