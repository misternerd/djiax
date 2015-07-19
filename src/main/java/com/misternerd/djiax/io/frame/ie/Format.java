package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;
import com.misternerd.djiax.util.MediaFormat;

/**
 * Implements the FORMAT information element. From RFC 5456:
 * 
 * The purpose of the FORMAT information element is to indicate a single
 * preferred media CODEC.  When sent with a NEW message, the indicated
 * CODEC is the desired CODEC an IAX peer wishes to use for a call.
 * When sent with an ACCEPT message, it indicates the actual CODEC that
 * has been selected for the call.  Its data is represented in a 4-octet
 * bitmask according to Section 8.7.  Only one CODEC MUST be specified
 * in the FORMAT information element.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x09     |      0x04     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   FORMAT according to Media   |
 * | Format Subclass Values Table  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Format extends InformationElement
{

	public static final byte TYPE = 0x09;

	private MediaFormat format;


	public Format(MediaFormat format)
	{
		super();
		
		ByteBuffer buffer = new ByteBuffer(4);
		buffer.put32bits(format.getFormat());
		
		this.format = format;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public Format(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.format = MediaFormat.reverse(buffer.get32bits());
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.FORMAT;
	}


	public MediaFormat getFormat()
	{
		return format;
	}


	@Override
	public String toString()
	{
		return String.format("Format(format=%s)", format);
	}

}