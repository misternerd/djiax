package com.misternerd.djiax.io.frame.ie;

import java.util.Vector;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;
import com.misternerd.djiax.util.MediaFormat;

/**
 * Implements the CAPABILITY information element. From RFC 5456:
 * 
 * The purpose of the CAPABILITY information element is to indicate the
 * media CODEC capabilities of an IAX peer.  Its data is represented in
 * a 4-octet bitmask according to Section 8.7.  Multiple CODECs MAY be
 * specified by logically OR'ing them into the CAPABILITY information
 * element.
 * 
 * The CAPABILITY information element is sent with IAX NEW messages if
 * appropriate for the CODEC negotiation method the peer is using.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x08     |      0x04     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | CAPABILITY according to Media |
 * | Format Subclass Values Table  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Capability extends InformationElement
{

	public static final byte TYPE = 0x08;

	private MediaFormat[] capabilites;


	public Capability(MediaFormat[] capabilites)
	{
		super();

		this.capabilites = capabilites;

		int capBin = 0x0;

		for (MediaFormat format : capabilites)
		{
			capBin += format.getFormat();
		}

		ByteBuffer buffer = new ByteBuffer(4);
		buffer.put32bits(capBin);
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public Capability(byte[] data) throws InvalidArgumentException
	{
		super(data);

		ByteBuffer buffer = new ByteBuffer(this.data);

		long capBin = buffer.get32bits();
		Vector<MediaFormat> capabilites = new Vector<MediaFormat>();

		for (MediaFormat format : MediaFormat.values())
		{
			if ((format.getFormat() & capBin) != 0)
			{
				capabilites.add(format);
			}
		}

		this.capabilites = capabilites.toArray(new MediaFormat[capabilites.size()]);
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CAPABILITY;
	}


	public MediaFormat[] getCapabilites()
	{
		return capabilites;
	}


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Capability(capabilites=");
		
		if(capabilites.length > 0)
		{
			for (MediaFormat format : capabilites)
			{
				sb.append(format).append(", ");
			}
		}
		else
		{
			sb.append("<none>");
		}
		
		sb.append(")");

		return sb.toString();
	}

}