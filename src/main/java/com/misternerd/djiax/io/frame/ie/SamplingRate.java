package com.misternerd.djiax.io.frame.ie;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the SAMPLINGRATE information element. From RFC 5456:
 * 
 * The purpose of the SAMPLINGRATE information element is to specify to
 * a remote IAX peer the sampling rate in hertz of the audio data being
 * the peer will use when sending data.  Its data field is 2 octets
 * long.
 * 
 * If the SAMPLINGRATE information element is not specified, a default
 * sampling rate of 8 kHz may be assumed.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x29     |      0x02     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |     Sampling Rate in Hertz    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class SamplingRate extends InformationElement
{

	public static final byte TYPE = 0x29;

	private int samplingRate;


	public SamplingRate(short samplingRate)
	{
		super();

		ByteBuffer buffer = new ByteBuffer(2);
		buffer.put16bits(samplingRate);

		this.samplingRate = samplingRate;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}

	public SamplingRate(byte[] data) throws InvalidArgumentException
	{
		super(data);

		try
		{
			ByteBuffer buffer = new ByteBuffer(this.data);
			this.samplingRate = buffer.get16bits();
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.SAMPLINGRATE;
	}


	public int getSamplingRate()
	{
		return samplingRate;
	}


	@Override
	public String toString()
	{
		return String.format("SamplingRate(samplingRate=%d)", samplingRate);
	}

}