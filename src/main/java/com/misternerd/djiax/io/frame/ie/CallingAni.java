package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CALLING ANI information element. From RFC 5456:
 * 
 * The purpose of the CALLING ANI information element is to indicate the
 * calling number ANI (Automatic Number Identification) for billing.  It
 * carries UTF-8-encoded data.
 * 
 * The CALLING ANI information element MAY be sent with an IAX NEW
 * message, but it is not required.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x03     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * : UTF-8-encoded CALLING ANI     :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class CallingAni extends InformationElement
{

	public static final byte TYPE = 0x03;

	private String callingAni;


	public CallingAni(String ani) throws UnsupportedEncodingException
	{
		super();
		this.callingAni = ani;
		this.data = ani.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public CallingAni(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.callingAni = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CALLING_ANI;
	}


	public String getAni()
	{
		return callingAni;
	}


	@Override
	public String toString()
	{
		return String.format("CallingAni(calledContext=%s)", callingAni);
	}

}