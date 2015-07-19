package com.misternerd.djiax.io.frame.full;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.FullFrameType;

/**
 * The frame carries comfort noise. The subclass is the level of comfort noise
 * in -dBov.
 */
public class ComfortNoiseFrame extends FullFrame
{

	public static final short TYPE = 0x0A;


	protected ComfortNoiseFrame()
	{
		super();
	}


	public ComfortNoiseFrame(short sourceCallNumber, boolean retransmitted, short destinationCallNumber, 
			long timestamp, short oSeqNumber, short iSeqNumber, int subclass)
	{
		super(sourceCallNumber, retransmitted, destinationCallNumber, 
				timestamp, oSeqNumber, iSeqNumber, FullFrameType.COMFORTNOISE, subclass);
	}


	public ComfortNoiseFrame(byte[] data, int dataLength) throws InvalidArgumentException
	{
		super(data, dataLength);

		if (this.getFrameType() != FullFrameType.COMFORTNOISE)
		{
			throw new InvalidArgumentException("Tried to construct a ComfortNoiseFrame from type " + this.getFrameType());
		}
	}


	public ComfortNoiseFrame(FullFrame parent) throws InvalidArgumentException
	{
		super(parent);

		if (type.getType() != TYPE)
		{
			throw new InvalidArgumentException("Tried to construct frame of type " + TYPE + " out of type " + type);
		}
	}


	public int getLevel()
	{
		return (int) subclass;
	}


	public void setLevel(int level)
	{
		this.subclass = level;
	}

}