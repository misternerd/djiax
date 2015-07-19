package com.misternerd.djiax.io.frame.full;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.FullFrameType;

/**
 * The frame carries session control data, i.e., it refers to control of a
 * device connected to an IAX endpoint.
 */
public class ControlFrame extends FullFrame
{

	public static final short TYPE = 0x04;

	private static final Logger logger = LoggerFactory.getLogger(ControlFrame.class);

	private ControlFrameSubclass controlSubclass;


	protected ControlFrame()
	{
		super();
	}


	public ControlFrame(short sourceCallNumber, boolean retransmitted, short destinationCallNumber, 
			long timestamp, short oSeqNumber, short iSeqNumber, ControlFrameSubclass controlSubclass)
	{
		super(sourceCallNumber, retransmitted, destinationCallNumber, 
				timestamp, oSeqNumber, iSeqNumber, FullFrameType.CONTROL, controlSubclass.getSubclass());
		this.controlSubclass = controlSubclass;
	}


	public ControlFrame(byte[] data, int dataLength) throws InvalidArgumentException
	{
		super(data, dataLength);

		if (this.getFrameType() != FullFrameType.CONTROL)
		{
			throw new InvalidArgumentException("Tried to construct a ControlFrame from type " + this.getFrameType());
		}

		try
		{
			this.controlSubclass = ControlFrameSubclass.reverse(subclass);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			this.controlSubclass = ControlFrameSubclass.INVALID;
		}
	}


	public ControlFrame(FullFrame parent) throws InvalidArgumentException
	{
		super(parent);

		if (type.getType() != TYPE)
		{
			throw new InvalidArgumentException("Tried to construct frame of type " + TYPE + " out of type " + type);
		}

		try
		{
			this.controlSubclass = ControlFrameSubclass.reverse(subclass);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			this.controlSubclass = ControlFrameSubclass.INVALID;
			logger.error("Received unknown subclass while creating from FullFrame: " + subclass);
		}
	}


	public ControlFrameSubclass getControlFrameSubclass()
	{
		return controlSubclass;
	}


	public void setControlFrameSubclass(ControlFrameSubclass subclass)
	{
		this.controlSubclass = subclass;
		this.subclass = subclass.getSubclass();
	}


	@Override
	public String toString()
	{
		return String.format("ControlFrame(srcCall=%d, dstCall=%d, iSeq=%d, oSeq=%d, ts=%d, controlSubclass=%s)", 
				sourceCallNumber, destinationCallNumber, iSeqNo, oSeqNo, timestamp, controlSubclass);
	}

}