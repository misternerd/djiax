package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CALLED CONTEXT information element. From RFC 5456:
 * 
 * The purpose of the CALLED CONTEXT information element is to indicate
 * the context (or partition) of the remote peer's dialplan that the
 * CALLED NUMBER is interpreted.  It carries UTF-8-encoded data.
 * 
 * The CALLED CONTEXT information element MAY be sent with IAX NEW or
 * TRANSFER messages, though it is not required.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x05     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * : UTF-8-encoded CALLED CONTEXT  :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class CalledContext extends InformationElement
{

	public static final byte TYPE = 0x05;

	private String calledContext;


	public CalledContext(String context) throws UnsupportedEncodingException
	{
		super();
		this.calledContext = context;
		this.data = context.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public CalledContext(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.calledContext = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CALLED_CONTEXT;
	}


	public String getCalledContext()
	{
		return calledContext;
	}


	@Override
	public String toString()
	{
		return String.format("CalledContext(calledContext=%s)", calledContext);
	}

}