package com.misternerd.djiax.io.frame;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Abstract parent class of information elements, also contains enum
 * with all valid elements. From RFC 5456:
 * 
 * IAX messages sent as Full Frames MAY carry information elements to
 * specify user- or call-specific data.  Information elements are
 * appended to a frame header in its data field.  Zero, one, or multiple
 * information elements MAY be included with any IAX message.
 * 
 * Information elements are coded as follows:
 * 
 *    The first octet of any information element consists of the "IE"
 *    field.  The IE field is an identification number that defines the
 *    particular information element.  [...]
 * 
 *    The second octet of any information element is the "data length"
 *    field.  It specifies the length in octets of the information
 *    element's data field.
 *    
 *    The remaining octet(s) of an information element contain the
 *    actual data being transmitted.  The representation of the data is
 *    dependent on the particular information element as identified by
 *    its "IE" field.  Some information elements carry binary data, some
 *    carry UTF-8 [RFC3629] data, and some have no data field at all.
 *    Elements that carry UTF-8 MUST prepare strings as per [RFC3454]
 *    and [RFC3491], so that illegal characters, case folding, and other
 *    characters properties are handled and compared properly.  The data
 *    representation for each information element is described below.
 * 
 * The following table specifies the Information Element Binary Format:
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      IE        | Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * :             DATA              :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+   
 *
 */
public abstract class InformationElement
{

	protected short dataLength;

	protected byte[] data;


	protected InformationElement()
	{
		this.data = new byte[0];
		this.dataLength = 0;
	}


	protected InformationElement(byte[] data) throws InvalidArgumentException
	{
		try
		{
			ByteBuffer buffer = new ByteBuffer(data);
			InformationElementType type = InformationElementType.reverse(buffer.get8bits());
			
			if(type != getType())
			{
				throw new InvalidArgumentException(String.format("Tried to create information element of type={} out of type={}", getType(), type));
			}
			
			this.dataLength = buffer.get8bits();
			this.data = buffer.getByteArray();
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	public abstract InformationElementType getType();


	public short getTypeRaw()
	{
		return getType().getType();
	}


	public short getDataLength()
	{
		return dataLength;
	}


	public byte[] getData()
	{
		return data;
	}


	public byte[] serialize()
	{
		ByteBuffer buffer = new ByteBuffer(2 + dataLength);

		buffer.put8bits(getTypeRaw());
		buffer.put8bits(dataLength);

		if (dataLength > 0)
		{
			buffer.putByteArray(data);
		}

		return buffer.getBuffer();
	}


	@Override
	public abstract String toString();

}
