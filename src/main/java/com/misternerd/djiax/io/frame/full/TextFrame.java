package com.misternerd.djiax.io.frame.full;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.FullFrameType;

/**
 * The frame carries a non-control text message in UTF-8 [RFC3629] format. All
 * text frames have a subclass of 0.
 */
public class TextFrame extends FullFrame
{

	public static final short TYPE = 0x07;

	private String text;


	protected TextFrame()
	{
		super();
	}


	public TextFrame(short sourceCallNumber, boolean retransmitted, short destinationCallNumber, 
			long timestamp, short oSeqNumber, short iSeqNumber, String text)
	{
		super(sourceCallNumber, retransmitted, destinationCallNumber, 
				timestamp, oSeqNumber, iSeqNumber, FullFrameType.TEXT, 0);

		this.text = text;
	}


	public TextFrame(byte[] data, int dataLength) throws InvalidArgumentException
	{
		super(data, dataLength);

		if (this.getFrameType() != FullFrameType.TEXT)
		{
			throw new InvalidArgumentException("Tried to construct a TextFrame from type " + this.getFrameType());
		}

		try
		{
			this.text = new String(this.data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	public TextFrame(FullFrame parent) throws InvalidArgumentException
	{
		super(parent);

		if (type.getType() != TYPE)
		{
			throw new InvalidArgumentException("Tried to construct frame of type " + TYPE + " out of type " + type);
		}

		try
		{
			this.text = new String(data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	public String getText()
	{
		return text;
	}


	public void setText(String text)
	{
		this.text = text;
	}


	@Override
	public byte[] serialize()
	{
		byte[] fullFrame = super.serialize();
		byte[] text = this.text.getBytes();
		byte[] result = new byte[fullFrame.length + text.length];

		System.arraycopy(fullFrame, 0, result, 0, fullFrame.length);
		System.arraycopy(text, 0, result, fullFrame.length, text.length);

		return result;
	}


	@Override
	public String toString()
	{
		return String.format("TextFrame(srcCall=%d, dstCall=%d, iSeq=%d, oSeq=%d, ts=%d, text=%s)", 
				sourceCallNumber, destinationCallNumber, iSeqNo, oSeqNo, timestamp, text);
	}

}