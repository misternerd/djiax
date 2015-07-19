package com.misternerd.djiax.io.frame.full;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.FullFrameType;
import com.misternerd.djiax.util.MediaFormat;
import com.misternerd.djiax.util.MediaFormat.FormatType;

/**
 * The frame carries video data. The subclass specifies the video format of the
 * data.
 */
public class VideoFrame extends FullFrame
{

	public static final short TYPE = 0x03;

	private MediaFormat format;


	protected VideoFrame()
	{
		super();
	}


	public VideoFrame(short sourceCallNumber, boolean retransmitted, short destinationCallNumber, 
			long timestamp, short oSeqNumber, short iSeqNumber, MediaFormat format, byte[] data)
			throws InvalidArgumentException
	{
		super(sourceCallNumber, retransmitted, destinationCallNumber, timestamp, 
				oSeqNumber, iSeqNumber, FullFrameType.VIDEO, format.getFormat());

		if (format.getType() != FormatType.VIDEO)
		{
			throw new InvalidArgumentException("This is not a video format: " + format);
		}

		this.format = format;
		this.data = data;
	}


	public VideoFrame(byte[] data, int dataLength) throws InvalidArgumentException
	{
		super(data, dataLength);

		if (this.getFrameType() != FullFrameType.VIDEO)
		{
			throw new InvalidArgumentException("Tried to construct a VideoFrame from type " + this.getFrameType());
		}

		try
		{
			this.format = MediaFormat.reverse(subclass);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException("The codec " + subclass + " is currently not supported!");
		}

		if (format.getType() != FormatType.VIDEO)
		{
			throw new InvalidArgumentException("This is not a video format: " + format);
		}
	}


	public VideoFrame(FullFrame parent) throws InvalidArgumentException
	{
		super(parent);

		if (type.getType() != TYPE)
		{
			throw new InvalidArgumentException("Tried to construct frame of type " + TYPE + " out of type " + type);
		}

		try
		{
			this.format = MediaFormat.reverse(subclass);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException(e);
		}

		if (format.getType() != FormatType.VIDEO)
		{
			throw new InvalidArgumentException("This is not a video format: " + format);
		}
	}


	public MediaFormat getFormat()
	{
		return format;
	}


	public void setFormat(MediaFormat format) throws InvalidArgumentException
	{
		if (format.getType() != FormatType.VIDEO)
		{
			throw new InvalidArgumentException("This is not a video format: " + format);
		}

		this.format = format;
		this.subclass = format.getFormat();
	}


	public byte[] getData()
	{
		return data;
	}


	public void setData(byte[] data)
	{
		this.data = data;
	}


	@Override
	public byte[] serialize()
	{
		byte[] fullFrame = super.serialize();
		byte[] result = new byte[fullFrame.length + data.length];

		System.arraycopy(fullFrame, 0, result, 0, fullFrame.length);

		if (data.length > 0)
		{
			System.arraycopy(data, 0, result, fullFrame.length, data.length);
		}

		return result;
	}

	
	@Override
	public String toString()
	{
		return String.format("VideoFrame(srcCall=%d, dstCall=%d, iSeq=%d, oSeq=%d, ts=%d, format=%s)", 
				sourceCallNumber, destinationCallNumber, iSeqNo, oSeqNo, timestamp, format);
	}

}