package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the LANGUAGE information element. From RFC 5456:
 * 
 * The purpose of the LANGUAGE information element is to indicate the
 * language in which the transmitting peer would like the remote peer to
 * send signaling information.  It carries UTF-8-encoded data and tags
 * should be selected per [RFC5646] and [RFC4647].
 * 
 * The LANGUAGE information element MAY be sent with an IAX NEW message.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x0a     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * :     UTF-8-encoded LANGUAGE    :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Language extends InformationElement
{

	public static final byte TYPE = 0x0A;

	private String language;


	public Language(String language) throws UnsupportedEncodingException
	{
		super();
		this.language = language;
		this.data = language.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public Language(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.language = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.LANGUAGE;
	}


	public String getLanguage()
	{
		return language;
	}


	@Override
	public String toString()
	{
		return String.format("Language(language=%s)", language);
	}

}