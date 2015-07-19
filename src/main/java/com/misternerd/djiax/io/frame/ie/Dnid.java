package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the DNID information element. From RFC 5456:
 * 
 * The purpose of the DNID information element is to indicate the Dialed
 * Number ID, which may differ from the 'called number'.  It carries
 * UTF-8-encoded data.
 * 
 * The DNID information element MAY be sent with an IAX NEW message.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x0d     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * :    UTF-8-encoded DNID Data    :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Dnid extends InformationElement
{

	public static final byte TYPE = 0x0D;

	private String dnid;


	public Dnid(String dnid) throws UnsupportedEncodingException
	{
		super();
		this.dnid = dnid;
		this.data = dnid.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public Dnid(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.dnid = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.DNID;
	}


	public String getDnid()
	{
		return dnid;
	}


	@Override
	public String toString()
	{
		return String.format("Dnid(dnid=%s)", dnid);
	}

}