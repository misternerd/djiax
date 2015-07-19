package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the USERNAME information element. From RFC 5456:
 * 
 * The purpose of the USERNAME information element is to specify the
 * identity of the user participating in an IAX message exchange.  It
 * carries UTF-8-encoded data.
 * 
 * The USERNAME information element MAY be sent with IAX NEW, AUTHREQ,
 * REGREQ, REGAUTH, or REGACK messages, or any time a peer needs to
 * identify a user.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x06     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * :     UTF-8-encoded USERNAME    :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Username extends InformationElement
{

	public static final byte TYPE = 0x06;

	private String username;


	public Username(String username) throws UnsupportedEncodingException
	{
		super();
		this.username = username;
		this.data = username.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public Username(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.username = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.USERNAME;
	}


	public String getUsername()
	{
		return username;
	}


	@Override
	public String toString()
	{
		return String.format("Username(username=%s)",  username);
	}

}