package com.misternerd.djiax.io.frame.full;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.exception.InformationElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.exception.InvalidInformationElementException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.FullFrameType;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementFactory;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Frames of type 'IAX' are used to provide management of IAX endpoints. They
 * handle IAX signaling (e.g., call setup, maintenance, and tear- down). They
 * MAY also handle direct transmission of media data, but this is not optimal
 * for VoIP calls. They do not carry session- specific control (e.g., device
 * state), as this is the purpose of Control Frames.
 */
public class IaxFrame extends FullFrame
{

	public static final short TYPE = 0x06;

	private static final Logger logger = LoggerFactory.getLogger(IaxFrame.class);

	private IaxFrameSubclass iaxSublass;

	private LinkedHashMap<InformationElementType, InformationElement> informationElements;

	private int informationElementsDataLength;


	protected IaxFrame()
	{
	}


	public IaxFrame(short sourceCallNumber, boolean retransmitted, short destinationCallNumber, long timestamp, 
			short oSeqNumber, short iSeqNumber, IaxFrameSubclass subclass)
	{
		super(sourceCallNumber, retransmitted, destinationCallNumber, timestamp, 
				oSeqNumber, iSeqNumber, FullFrameType.IAXCONTROL, subclass.getSubclass());

		this.iaxSublass = subclass;
		this.informationElements = new LinkedHashMap<>();
		this.informationElementsDataLength = 0;
	}


	public IaxFrame(byte[] data, int dataLength) throws InvalidArgumentException
	{
		super(data, dataLength);

		if (this.getFrameType() != FullFrameType.IAXCONTROL)
		{
			throw new InvalidArgumentException("Tried to construct an IaxFrame from type " + this.getFrameType());
		}

		try
		{
			this.iaxSublass = IaxFrameSubclass.reverse((byte) subclass);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException("The IaxFrame subclass " + subclass + " is not (yet) supported");
		}

		this.informationElements = new LinkedHashMap<>();
		this.informationElementsDataLength = 0;

		if (this.data.length > 0)
		{
			parseInformationElements();
		}
	}


	public IaxFrame(FullFrame parent) throws InvalidArgumentException
	{
		super(parent);

		if (type.getType() != TYPE)
		{
			throw new InvalidArgumentException("Tried to construct frame of type " + TYPE + " out of type " + type);
		}

		try
		{
			iaxSublass = IaxFrameSubclass.reverse((byte) subclass);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException("The IaxFrame subclass " + subclass + " is not (yet) supported");
		}

		this.informationElements = new LinkedHashMap<>();
		this.informationElementsDataLength = 0;

		if (this.data.length > 0)
		{
			parseInformationElements();
		}
	}


	public IaxFrameSubclass getIaxClass()
	{
		return iaxSublass;
	}


	/**
	 * If an element of the same type is already set, it gets overwritten.
	 */
	public void addInformationElement(InformationElement ie)
	{
		informationElements.put(ie.getType(), ie);
		informationElementsDataLength += ie.getDataLength();
	}


	public InformationElement getInformationElement(InformationElementType informationElementType) 
			throws InformationElementNotFoundException
	{
		if (!informationElements.containsKey(informationElementType))
		{
			throw new InformationElementNotFoundException(informationElementType);
		}

		return informationElements.get(informationElementType);
	}


	public LinkedHashMap<InformationElementType, InformationElement> getInformationElements()
	{
		return informationElements;
	}


	@Override
	public byte[] serialize() throws IndexOutOfBoundsException
	{
		byte[] header = super.serialize();
		byte[] result;

		if (!informationElements.isEmpty())
		{
			// header + IEs header length (2 bytes each) +  IEs data length
			result = new byte[header.length + (informationElements.size() * 2) + informationElementsDataLength];

			Iterator<Entry<InformationElementType, InformationElement>> it = informationElements.entrySet().iterator();
			int offset = header.length;

			while (it.hasNext())
			{
				InformationElement ie = it.next().getValue();
				System.arraycopy(ie.serialize(), 0, result, offset, ie.getDataLength() + 2);
				offset += 2 + ie.getDataLength();
			}
		}
		else
		{
			result = header;
		}

		System.arraycopy(header, 0, result, 0, header.length);

		return result;
	}


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("IaxFrame(srcCall=%d, dstCall=%d, iSeq=%d, oSeq=%d, ts=%d, iaxSubclass=%s, IEs=", 
				sourceCallNumber, destinationCallNumber, iSeqNo, oSeqNo, timestamp, iaxSublass));

		if (!informationElements.isEmpty())
		{

			Iterator<Entry<InformationElementType, InformationElement>> it = informationElements.entrySet().iterator();

			while (it.hasNext())
			{
				sb.append(it.next().getKey()).append(",");
			}
		}
		else
		{
			sb.append("<none>");
		}
		
		sb.append(")");

		return sb.toString();
	}


	private void parseInformationElements()
	{
		int offset = 0;

		for(int dataLength = data.length; offset + 1 < dataLength; )
		{
			short ieType = data[offset];
			short ieSize = data[offset + 1];

			if (offset + ieSize >= dataLength)
			{
				logger.warn("Could not parse ie as offset={}, ieSize={} exceed dataSize={}",
					new Object[]{offset, ieSize, dataLength});
				break;
			}

			if (ieSize < 0)
			{
				logger.warn("Tried parsing IE element with declared size={}, too small, so far parsed {} elements for frame={}", 
						new Object[]{ieSize, informationElements.size(), this});
				break;
			}

			try
			{
				byte[] ieData = new byte[ieSize + 2];
				System.arraycopy(data, offset, ieData, 0, ieSize + 2);

				// 1 byte-type + 1 bytes length + n bytes data)
				offset += 2 + ieSize;

				InformationElement ieObject = InformationElementFactory.createFromType(ieType, ieData);
				informationElements.put(ieObject.getType(), ieObject);
				informationElementsDataLength += ieObject.getDataLength();
			}
			catch (InvalidInformationElementException | InvalidArgumentException e)
			{
				logger.debug("Found unsupported ieType={}, skipping {} bytes for frame={}", 
						new Object[]{ieType, 2 + ieSize, this});
				offset += 2 + ieSize;
			}
		}
	}

}