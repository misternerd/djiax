package com.misternerd.djiax.io.frame;

import com.misternerd.djiax.PeerConstants;
import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Full frames can send signaling or media data.  Generally full frames
 * are used to control initiation, setup, and termination of an IAX
 * call, but they can also be used to carry stream data (though this is
 * generally not optimal).
 * 
 * Full frames are sent reliably, so all full frames require an
 * immediate acknowledgment upon receipt.  This acknowledgment can be
 * explicit via an 'ACK' message (see Section 8.4) or implicit based
 * upon receipt of an appropriate response to the full frame issued.
 * 
 * The standard full frame header length is 12 octets.
 * 
 *                         1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |1|     Source Call Number      |R|   Destination Call Number   |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                            time-stamp                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    OSeqno     |    ISeqno     |   Frame Type  |C|  Subclass   |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                                                               |
 * :                             Data                              :
 * |                                                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 */
public class FullFrame extends FrameBase
{

	protected boolean retransmitted;

	protected int retransmitCount;

	protected long nextRetransmitTimestamp;

	protected long frameGeneratedTimestamp;

	/**
	 * This 15-bit value specifies the call number the transmitting client uses
	 * to reference the call at the remote peer. This number is the same as the
	 * remote peer's source call number. The destination call number uniquely
	 * identifies a call on the remote peer. The source call number uniquely
	 * identifies the call on the local peer.
	 */
	protected short destinationCallNumber;

	/**
	 * A 32-bit (unsigned) timestamp in milliseconds since the first
	 * transmission of the call
	 */
	protected long timestamp;

	protected short oSeqNo;

	protected short iSeqNo;

	protected FullFrameType type;

	/**
	 * If set to true, the subclass field is interpreted as a power of 2,
	 * otherwise as a 7-bit unsigned integer
	 */
	protected boolean cBit;

	/**
	 * 7-bit, if c-bit is set interpreted as a power of, otherwise as an int
	 */
	protected long subclass;

	/**
	 * Contains additional data exceeding the 12 bytes header
	 */
	protected byte[] data;


	protected FullFrame()
	{
		super();
	}


	protected FullFrame(short sourceCallNumber, boolean retransmitted, short destinationCallNumber, long timestamp, short oSeqNumber, short iSeqNumber, FullFrameType fullFrameType, long subclass)
	{
		super(sourceCallNumber);

		this.retransmitted = retransmitted;
		this.destinationCallNumber = destinationCallNumber;
		this.timestamp = timestamp;
		this.oSeqNo = oSeqNumber;
		this.iSeqNo = iSeqNumber;
		this.type = fullFrameType;
		this.subclass = subclass;
		this.frameGeneratedTimestamp = System.currentTimeMillis();

		// subclass>127? Then we need to set the c-bit
		this.cBit = (subclass > 127) ? true : false;
	}


	/**
	 * This constructor looks at the first 12 bytes, everything beyond is part
	 * of the actual frame class.
	 */
	public FullFrame(byte[] data, int dataLength) throws InvalidArgumentException
	{
		if (dataLength < 12)
		{
			throw new InvalidArgumentException("Length " + dataLength + " is too short for FullFrame (min is 12)");
		}

		//sourceCallNumber: 2 bytes
		int tmpInt = (data[0] & 0x7F);
		tmpInt = tmpInt << 8;
		this.sourceCallNumber = (short) (tmpInt + (data[1] & 0xFF));

		this.retransmitted = ((data[2] & 0x80) != 0) ? true : false;

		// destination call number
		tmpInt = (data[2] & 0x7F);
		tmpInt = tmpInt << 8;
		this.destinationCallNumber = (short) (tmpInt + (data[3] & 0xFF));

		this.timestamp = ((data[4] << 24) & 0xFF000000) + ((data[5] << 16) & 0xFF0000) + ((data[6] << 8) & 0xFF00) + (data[7] & 0xFF);
		this.oSeqNo = data[8];
		this.iSeqNo = data[9];

		// frametype
		try
		{
			this.type = FullFrameType.getFrameTypeByNumber(data[10]);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException(e);
		}

		this.cBit = ((data[11] & 0x80) == 0) ? false : true;
		this.subclass = (data[11] & 0x7F);

		// if c-bit is set, subclass is interpreted differently
		if (cBit == true)
		{
			this.subclass = 1L << (subclass & 0x1F);
		}

		if (dataLength > 12)
		{
			this.data = new byte[dataLength - 12];
			System.arraycopy(data, 12, this.data, 0, this.data.length);
		}
		else
		{
			this.data = new byte[0];
		}

		this.frameGeneratedTimestamp = System.currentTimeMillis();
	}


	/**
	 * Construct a FullFrame from a FullFrame. Seems a bit paradox, but enables
	 * a seamless cast from FullFrame to the subclass frametypes.
	 */
	protected FullFrame(FullFrame parent)
	{
		this.retransmitted = parent.retransmitted;
		this.retransmitCount = parent.retransmitCount;
		this.sourceCallNumber = parent.sourceCallNumber;
		this.destinationCallNumber = parent.destinationCallNumber;
		this.timestamp = parent.timestamp;
		this.oSeqNo = parent.oSeqNo;
		this.iSeqNo = parent.iSeqNo;
		this.type = parent.type;
		this.subclass = parent.subclass;
		this.cBit = parent.cBit;
		this.data = parent.data;
	}


	public boolean isRetransmitted()
	{
		return retransmitted;
	}


	public void setRetransmitted(boolean retransmitted)
	{
		this.retransmitted = retransmitted;
	}


	public int getRetransmitCount()
	{
		return retransmitCount;
	}


	public void setRetransmitCount(int retransmitCount)
	{
		this.retransmitCount = retransmitCount;
	}


	public void incRetransmitCount()
	{
		retransmitCount++;
	}


	public long getNextRetransmitTimestamp()
	{
		return nextRetransmitTimestamp;
	}


	public void updateNextRetransmitTimestamp()
	{
		if(retransmitCount < 1)
		{
			this.nextRetransmitTimestamp = System.currentTimeMillis() 
					+ (PeerConstants.FRAME_RETRANSMIT_TIMEOUT_IN_MSECS  * 2);
		}
		else
		{
			this.nextRetransmitTimestamp = System.currentTimeMillis() 
				+ (PeerConstants.FRAME_RETRANSMIT_TIMEOUT_IN_MSECS ^ (long) retransmitCount);
		}
	}


	public long getFullTimestamp()
	{
		return frameGeneratedTimestamp;
	}


	public short getDestinationCallNumber()
	{
		return destinationCallNumber;
	}


	public void setDestinationCallNumber(short destinationCallNumber)
	{
		this.destinationCallNumber = destinationCallNumber;
	}


	public long getTimestamp()
	{
		return timestamp;
	}


	public void setTimestamp(int timestamp)
	{
		this.timestamp = timestamp;
	}


	public short getOSeqNo()
	{
		return oSeqNo;
	}


	public void setOSeqNo(byte oSeqNo)
	{
		this.oSeqNo = oSeqNo;
	}


	public short getISeqNo()
	{
		return iSeqNo;
	}


	public void setISeqNo(byte iSeqNo)
	{
		this.iSeqNo = iSeqNo;
	}


	public FullFrameType getFrameType()
	{
		return type;
	}


	public void setFrameType(FullFrameType _frameType)
	{
		this.type = _frameType;
	}


	public boolean isCBit()
	{
		return cBit;
	}


	public void setCBit(boolean cBit)
	{
		this.cBit = cBit;
	}


	public long getSubclass()
	{
		return subclass;
	}


	public void setSubclass(int subclass)
	{
		this.subclass = subclass;
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
		ByteBuffer buffer = new ByteBuffer(12);

		// F-bit & source call number (2 bytes)
		buffer.put16bits(0x8000 + (sourceCallNumber & 0x7FFF));

		// r-bit & destination call number (2 bytes)
		buffer.put16bits((((retransmitted == true) ? 0x8000 : 0)) + (destinationCallNumber & 0x7FFF));

		buffer.put32bits(timestamp);
		buffer.put8bits(oSeqNo);
		buffer.put8bits(iSeqNo);
		buffer.put8bits(type.getType());

		// re-calculate subclass in power format
		if (cBit == true)
		{
			// we need 2^i=_subclass
			for (int i = 0; i < 31; i++)
			{
				if (((1 << i) & subclass) != 0)
				{
					this.subclass = i;
					break;
				}
			}
		}

		// C-bit & subclass (1 byte)
		buffer.put8bits((short) ((cBit == true) ? 0x80 : 0 + (subclass & 0x7F)));

		return buffer.getBuffer();
	}


	@Override
	public String toString()
	{
		return String.format("FullFrame(type=%s, srcCall=%d, dstCall=%d, iSeq=%d, oSeq=%d, ts=%d)", 
				type, sourceCallNumber, destinationCallNumber, iSeqNo, oSeqNo, timestamp);
	}

}
