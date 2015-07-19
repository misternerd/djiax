package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CAUSE information element. From RFC 5456:
 * 
 * The purpose of the CAUSE information element is to indicate the
 * reason an event occurred.  It carries a description of the CAUSE of
 * the event as UTF-8-encoded data.  Notification of the event itself is
 * handled at the message level.
 * 
 * The CAUSE information element SHOULD be sent with IAX HANGUP, REJECT,
 * REGREJ, and TXREJ messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x16     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * :  UTF-8-encoded CAUSE of event :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Cause extends InformationElement
{

	public static final byte TYPE = 0x16;

	private String cause;


	public Cause(String cause) throws UnsupportedEncodingException
	{
		super();
		this.cause = cause;
		this.data = cause.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public Cause(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.cause = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CAUSE;
	}


	public String getCause()
	{
		return cause;
	}


	@Override
	public String toString()
	{
		return String.format("Cause(cause=%s)", cause);
	}

}