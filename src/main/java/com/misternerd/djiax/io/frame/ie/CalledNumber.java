package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CALLED NUMBER information element. From RFC 5456:
 * 
 * The purpose of the CALLED NUMBER information element is to indicate
 * the number or extension being called.  It carries UTF-8-encoded data.
 * The CALLED NUMBER information element MUST use UTF-8 encoding and not
 * numeric data because destinations are not limited to E.164 numbers
 * ([E164]), national numbers, or even digits.  It is possible for a
 * number or extension to include non-numeric characters.  The CALLED
 * NUMBER IE MAY contain a SIP URI, [RFC3261] or a URI in any other
 * format.  The ability to serve a CALLED NUMBER is server dependent.
 * 
 * The CALLED NUMBER information element is generally sent with IAX NEW,
 * DPREQ, DPREP, DIAL, and TRANSFER messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x01     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * :  UTF-8-encoded CALLED NUMBER  :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class CalledNumber extends InformationElement
{

	public static final byte TYPE = 0x01;

	private String calledNumber;


	public CalledNumber(String number) throws UnsupportedEncodingException
	{
		super();
		this.calledNumber = number;
		this.data = number.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public CalledNumber(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.calledNumber = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CALLED_NUMBER;
	}


	public String getNumber()
	{
		return calledNumber;
	}


	@Override
	public String toString()
	{
		return String.format("CalledNumber(calledNumber=%s)", calledNumber);
	}

}