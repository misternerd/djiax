package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the AUTHMETHODS information element. From RFC 5456:
 * 
 * The purpose of the AUTHMETHODS information element is to indicate the
 * authentication methods a peer accepts.  It is sent as a bitmask two
 * octets long.  The table below lists the valid authentication methods.
 * 
 * The AUTHMETHODS information element MUST be sent with IAX AUTHREQ and
 * REGAUTH messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x0e     |      0x02     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Valid Authentication Methods |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * The following table lists valid values for authentication:
 * 
 *                 +--------+--------------------------+
 *                 | METHOD | DESCRIPTION              |
 *                 +--------+--------------------------+
 *                 | 0x0001 | Reserved (was Plaintext) |
 *                 |        |                          |
 *                 | 0x0002 | MD5                      |
 *                 |        |                          |
 *                 | 0x0004 | RSA                      |
 *                 +--------+--------------------------+
 */
public class Authmethods extends InformationElement
{

	public static final byte TYPE = 0x0E;

	public static final short METHOD_MD5 = 0x0002;

	public static final short METHOD_RSA = 0x0004;

	private int authmethod;


	public Authmethods(short authmethod)
	{
		super();
		this.authmethod = authmethod;

		ByteBuffer buffer = new ByteBuffer(2);
		buffer.put16bits(authmethod);
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public Authmethods(byte[] data) throws InvalidArgumentException
	{
		super(data);

		ByteBuffer buffer = new ByteBuffer(this.data);
		this.authmethod = buffer.get16bits();
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.AUTHMETHODS;
	}


	public int getAuthmethod()
	{
		return authmethod;
	}


	@Override
	public String toString()
	{
		return String.format("Authmethods(authmethod=%d)", authmethod);
	}

}