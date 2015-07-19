package com.misternerd.djiax.io.frame.full;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.FullFrameType;

/**
 * The frame carries HTML data. All text frames have a subclass of 0.
 */
public class HtmlFrame extends FullFrame
{

	public static final short TYPE = 0x09;

	public static enum HtmlFrameSubclass
	{
		SEND_URL(0x01),
		DATA_FRAME(0x03),
		BEGINNING_FRAME(0x04),
		END_FRAME(0x05),
		LOAD_COMPLETE(0x08),
		HTML_UNSUPPORTED(0x09),
		LINK_URL(0x0b),
		UNLINK_URL(0x0c),
		REJECT_LINK_URL(0x0d);

		short _subclass;

		HtmlFrameSubclass(int subclass)
		{
			this._subclass = (byte) subclass;
		}
	}

	private String html;


	protected HtmlFrame()
	{
		super();
	}


	public HtmlFrame(short sourceCallNumber, boolean retransmitted, short destinationCallNumber, 
			long timestamp, short oSeqNumber, short iSeqNumber, String html)
	{
		super(sourceCallNumber, retransmitted, destinationCallNumber, timestamp, oSeqNumber, iSeqNumber, FullFrameType.HTML, 0);
		this.html = html;
	}


	public HtmlFrame(byte[] data, int dataLength) throws InvalidArgumentException
	{
		super(data, dataLength);

		if (this.getFrameType() != FullFrameType.HTML)
		{
			throw new InvalidArgumentException("Tried to construct a HtmlFrame from type " + this.getFrameType());
		}

		try
		{
			this.html = new String(data, 12, data.length - 12, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException("Failed decoding HTML", e);
		}
	}


	public HtmlFrame(FullFrame parent) throws InvalidArgumentException
	{
		super(parent);

		if (type.getType() != TYPE)
		{
			throw new InvalidArgumentException("Tried to construct frame of type " + TYPE + " out of type " + type);
		}

		try
		{
			this.html = new String(data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InvalidArgumentException("Cannot decode HTML", e);
		}
	}


	public String getHtml()
	{
		return html;
	}


	public void setHtml(String html)
	{
		this.html = html;
	}


	@Override
	public byte[] serialize()
	{
		byte[] fullFrame = super.serialize();
		byte[] text = html.getBytes();
		byte[] result = new byte[fullFrame.length + text.length];

		System.arraycopy(fullFrame, 0, result, 0, fullFrame.length);
		System.arraycopy(text, 0, result, fullFrame.length, text.length);

		return result;
	}


	@Override
	public String toString()
	{
		return String.format("HtmlFrame(srcCall=%d, dstCall=%d, iSeq=%d, oSeq=%d, ts=%d, html=%s)", 
				sourceCallNumber, destinationCallNumber, iSeqNo, oSeqNo, timestamp, html);
	}
}