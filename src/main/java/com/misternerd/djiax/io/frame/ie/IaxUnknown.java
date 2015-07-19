package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the IAX UNKNOWN information element. From RFC 5456:
 * 
 * The purpose of the IAX UNKNOWN information element is to indicate
 * that a received IAX command was unknown or unrecognized.  The 1-octet
 * data field contains the subclass of the received frame that was
 * unrecognized.
 * 
 * The IAX UNKNOWN information element MUST be sent with IAX UNSUPPORT
 * messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x17     |      0x01     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Rec'd Subclass|
 * +-+-+-+-+-+-+-+-+
 */
public class IaxUnknown extends InformationElement
{

	public static final byte TYPE = 0x17;

	private short subclass;


	public IaxUnknown(byte subclass)
	{
		super();
		this.subclass = subclass;
		this.data = new byte[] { subclass };
		this.dataLength = 1;
	}


	public IaxUnknown(byte[] data) throws InvalidArgumentException
	{
		super(data);

		ByteBuffer buffer = new ByteBuffer(this.data);
		this.subclass = buffer.get8bits();
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.IAX_UNKNOWN;
	}


	public short getSubclass()
	{
		return subclass;
	}


	@Override
	public String toString()
	{
		return String.format("IaxUnknown(subclass=%d)", subclass);
	}

}