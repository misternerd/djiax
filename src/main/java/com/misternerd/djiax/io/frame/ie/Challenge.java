package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CHALLENGE information element. From RFC 5456:
 * 
 * The purpose of the CHALLENGE information element is to offer the MD5
 * or RSA challenge to be used for authentication.  It carries the
 * actual UTF-8-encoded challenge data.
 * 
 * The CHALLENGE information element MUST be sent with IAX AUTHREQ and
 * REGAUTH messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x0f     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * :  UTF-8-encoded Challenge Data :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Challenge extends InformationElement
{

	public static final byte TYPE = 0x0F;

	private String challengeData;


	public Challenge(String challengeData) throws UnsupportedEncodingException
	{
		super();
		this.challengeData = challengeData;
		this.data = challengeData.getBytes("UTF-8");
		this.dataLength = (byte) data.length;
	}


	public Challenge(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.challengeData = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CHALLENGE;
	}


	public String getChallengeData()
	{
		return challengeData;
	}


	@Override
	public String toString()
	{
		return String.format("Challenge(challengeData=%s)", challengeData);
	}

}