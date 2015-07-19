package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CALLED NUMBER information element. From RFC 5456:
 * 
 * The purpose of the CALLING NUMBER information element is to indicate
 * the number or extension of the calling entity to the remote peer.  It
 * carries UTF-8-encoded data.
 * 
 * The CALLING NUMBER information element is usually sent with IAX NEW
 * messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x02     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * : UTF-8-encoded CALLING NUMBER  :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class CallingNumber extends InformationElement
{

	public static final byte TYPE = 0x02;

	private String callingNumber;


	public CallingNumber(String number) throws UnsupportedEncodingException
	{
		super();
		this.callingNumber = number;
		this.data = number.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public CallingNumber(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.callingNumber = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CALLING_NUMBER;
	}


	public String getNumber()
	{
		return callingNumber;
	}


	@Override
	public String toString()
	{
		return String.format("CallingNumber(calledContext=%s)", callingNumber);
	}

}