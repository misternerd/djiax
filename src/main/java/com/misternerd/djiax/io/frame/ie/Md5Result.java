package com.misternerd.djiax.io.frame.ie;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the MD5 RESULT information element. From RFC 5456:
 * 
 * The purpose of the MD5 RESULT information element is to offer an MD5
 * response to an authentication CHALLENGE.  It carries the UTF-8-
 * encoded challenge result.  The MD5 Result value is computed by taking
 * the MD5 [RFC1321] digest of the challenge string and the password
 * string.
 * 
 * The MD5 RESULT information element MAY be sent with IAX AUTHREP and
 * REGREQ messages if an AUTHREQ or REGAUTH and appropriate CHALLENGE
 * has been received.  This information element MUST NOT be sent except
 * in response to a CHALLENGE.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |     0x10      |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * :    UTF-8-encoded MD5 Result   :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Md5Result extends InformationElement
{

	public static final byte TYPE = 0x10;
	
	public static MessageDigest messageDigest;

	static
	{
		try
		{
			messageDigest = MessageDigest.getInstance("MD5");
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to init MD5-Digest", e);
		}
	}

	private String md5Result;


	public Md5Result(String challenge, String password) throws UnsupportedEncodingException
	{
		super();
		this.md5Result = generateHashForChallengeWithPasswort(challenge, password);
		this.data = md5Result.getBytes();
		this.dataLength = (byte) data.length;
	}


	public Md5Result(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			this.md5Result = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.MD5_RESULT;
	}


	public String getMd5Result()
	{
		return md5Result;
	}


	@Override
	public String toString()
	{
		return String.format("Md5Result(md5Result=%s)", md5Result);
	}


	private String generateHashForChallengeWithPasswort(String challenge, String password) throws UnsupportedEncodingException
	{
		byte[] challengeBytes = challenge.getBytes("UTF-8");
		byte[] passwordBytes = password.getBytes("UTF-8");

		// concatenate challenge with password
		byte[] bothBytes = new byte[challenge.length() + password.length()];
		System.arraycopy(challengeBytes, 0, bothBytes, 0, challengeBytes.length);
		System.arraycopy(passwordBytes, 0, bothBytes, challengeBytes.length, passwordBytes.length);

		messageDigest.update(bothBytes);
		byte[] result = messageDigest.digest();

		StringBuilder sb = new StringBuilder();
		
		for (int i = 0, j = result.length; i <j; i++)
		{
			String hex = Integer.toHexString(0xFF & result[i]);
			if (hex.length() == 1)
			{
				sb.append("0");
			}

			sb.append(hex);
		}

		String result1 = sb.toString();
		return result1;
	}

}