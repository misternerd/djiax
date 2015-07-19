package com.misternerd.djiax.io.frame;

import com.misternerd.djiax.exception.InvalidArgumentException;

/**
 * Mini Frames are so named because their header is a minimal 4 octets. Mini
 * Frames carry no control or signaling data; their sole purpose is to carry a
 * media stream on an already-established IAX call. They are sent unreliably.
 * This decision was made because VoIP calls typically can miss several frames
 * without significant degradation in call quality while the incurred overhead
 * in ensuring reliability increases bandwidth requirements and decreases
 * throughput. Further, because voice calls are typically sent in real time,
 * lost frames are too old to be reintegrated into the audio stream by the time
 * they can be retransmitted.
 */
public class MiniFrame extends FrameBase
{
	/**
	 * 16-bit timestamp, lower 16 bits of peer's full 32 bit timestamp. The
	 * 16-bit time-stamp wraps after 65.536 seconds, at which point a full frame
	 * SHOULD be sent to notify the remote peer that its time-stamp has been
	 * reset. A call MUST continue to send mini frames starting with time-stamp
	 * 0 even if acknowledgment of the resynchronization is not received.
	 */
	private int timestamp;

	private byte[] data;


	public MiniFrame()
	{
	}


	public MiniFrame(short sourceCallNumber, int timestamp, byte[] data)
	{
		super(sourceCallNumber);
		this.timestamp = timestamp;
		this.data = data;
	}


	public MiniFrame(byte[] data, int length) throws InvalidArgumentException
	{
		if (data.length < 5)
		{
			throw new InvalidArgumentException("Length " + data.length + " is too short for MiniFrame (min is 5)");
		}

		// 2 bytes sourceCallNumber, 2 bytes timestamp, rest is data
		this.sourceCallNumber = (short) (((data[0] & 0x7F) << 8) + (data[1] & 0xFF));
		this.timestamp = ((data[2] & 0xFF) << 8) + (data[3] & 0xFF);
		this.data = new byte[length - 4];
		System.arraycopy(data, 4, this.data, 0, length - 4);
	}


	public int getTimestamp()
	{
		return timestamp;
	}


	public void setTimestamp(int timestamp)
	{
		this.timestamp = timestamp;
	}


	public short getSourceCallNumber()
	{
		return sourceCallNumber;
	}


	public void setSourceCallNumber(short sourceCallNumber)
	{
		this.sourceCallNumber = sourceCallNumber;
	}


	public byte[] getData()
	{
		return data;
	}


	public void setData(byte[] data)
	{
		this.data = data;
	}


	public byte[] serialize()
	{
		// 4 bytes header + data bytes
		byte[] result = new byte[4 + data.length];

		// 1. byte: 0 (no full frame) + upper 7 bits of source call number
		result[0] = (byte) ((sourceCallNumber & 0x7F00) >> 8);

		// 2. byte: lower 8 bits of source call number
		result[1] = (byte) (sourceCallNumber & 0xFF);

		// 3. byte: upper 8 bits of timestamp
		result[2] = (byte) ((timestamp & 0xFF00) >> 8);

		// 4. byte: lower 8 bits of timestamp
		result[3] = (byte) (timestamp & 0xFF);

		System.arraycopy(data, 0, result, 4, data.length);

		return result;
	}


	@Override
	public String toString()
	{
		return String.format("MiniFrame(srcCall=%d, timestamp=%d, size=%d)", sourceCallNumber, timestamp, data.length);
	}

}