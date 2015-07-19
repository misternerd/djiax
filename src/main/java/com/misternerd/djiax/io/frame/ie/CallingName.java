package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CALLING NAME information element. From RFC 5456:
 * 
 * The purpose of the CALLING NAME information element is to indicate
 * the calling name of the transmitting peer.  It carries UTF-8-encoded
 * data.
 *  
 * The CALLING NAME information element is usually sent with IAX NEW
 * messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x04     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * :  UTF-8-encoded CALLING NAME   :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class CallingName extends InformationElement
{

	public static final byte TYPE = 0x04;

	private String callingName;


	public CallingName(String name) throws UnsupportedEncodingException
	{
		super();
		this.callingName = name;
		this.data = name.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public CallingName(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.callingName = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CALLING_NAME;
	}


	public String getCallingName()
	{
		return callingName;
	}


	@Override
	public String toString()
	{
		return String.format("CallingName(callingName=%s)", callingName);
	}

}