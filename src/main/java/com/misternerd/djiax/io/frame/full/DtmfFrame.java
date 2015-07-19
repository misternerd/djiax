package com.misternerd.djiax.io.frame.full;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.FullFrameType;

/**
 * The frame carries a single digit of DTMF (Dual Tone Multi-Frequency). For
 * DTMF frames, the subclass is the actual DTMF digit carried by the frame.
 */
public class DtmfFrame extends FullFrame
{

	public static final short TYPE = 0x01;


	protected DtmfFrame()
	{
	}


	public DtmfFrame(short sourceCallNumber, boolean retransmitted, short destinationCallNumber, 
			long timestamp, short oSeqNumber, short iSeqNumber, int digit)
	{
		super(sourceCallNumber, retransmitted, destinationCallNumber, 
				timestamp, oSeqNumber, iSeqNumber, FullFrameType.DTMF, digit);
	}


	public DtmfFrame(byte[] data, int dataLength) throws InvalidArgumentException
	{
		super(data, dataLength);

		if (this.getFrameType() != FullFrameType.DTMF)
		{
			throw new InvalidArgumentException("Tried to construct a DtmfFrame from type " + this.getFrameType());
		}
	}


	public DtmfFrame(FullFrame parent) throws InvalidArgumentException
	{
		super(parent);

		if (type.getType() != TYPE)
		{
			throw new InvalidArgumentException("Tried to construct frame of type " + TYPE + " out of type " + type);
		}
	}


	public int getDigit()
	{
		return (int) subclass;
	}


	public void setDigit(int digit)
	{
		this.subclass = digit;
	}


	@Override
	public String toString()
	{
		return String.format("DtmfFrame(srcCall=%d, dstCall=%d, iSeq=%d, oSeq=%d, ts=%d, digit=%d)", 
				sourceCallNumber, destinationCallNumber, iSeqNo, oSeqNo, timestamp, subclass);
	}

}