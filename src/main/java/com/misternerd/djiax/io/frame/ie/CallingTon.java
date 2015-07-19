package com.misternerd.djiax.io.frame.ie;

import java.util.HashMap;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CALLINGTON information element. From RFC 5456:
 * 
 * The purpose of the CALLINGTON information element is to indicate the
 * calling type of number of a caller, according to ITU-T Recommendation
 * Q.931 specifications.  The data field is 1 octet long and contains
 * data from the table below.
 * 
 * The CALLINGTON information element MUST be sent with IAX NEW
 * messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x27     |      0x01     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Calling TON  |
 * +-+-+-+-+-+-+-+-+
 *
 * The following table lists valid calling type of number values from
 * ITU-T Recommendation Q.931:
 * 	
 * 	  +-------+-------------------------+
 * 	  | VALUE | DESCRIPTION             |
 * 	  +-------+-------------------------+
 * 	  | 0x00  | Unknown                 |
 * 	  |       |                         |
 * 	  | 0x10  | International Number    |
 * 	  |       |                         |
 * 	  | 0x20  | National Number         |
 * 	  |       |                         |
 * 	  | 0x30  | Network Specific Number |
 * 	  |       |                         |
 * 	  | 0x40  | Subscriber Number       |
 * 	  |       |                         |
 * 	  | 0x60  | Abbreviated Number      |
 * 	  |       |                         |
 * 	  | 0x70  | Reserved for extension  |
 * 	  +-------+-------------------------+
 */
public class CallingTon extends InformationElement
{

	public static final byte TYPE = 0x27;


	public static enum TonValue
	{
		UNKNOWN(0x00), INTERNATIONAL_NUMBER(0x10), 
		NATIONAL_NUMBER(0x20), NETWORK_SPECIFIC_NUMBER(0x30), 
		SUBSCRIBER_NUMBER(0x40), ABBREVIATED_NUMBER(0x60), 
		RESERVED_FOR_EXTENSION(0x70);

		private static final HashMap<Byte, TonValue> lookup = new HashMap<>();

		static
		{
			for (TonValue value : TonValue.values())
			{
				lookup.put(value.type, value);
			}
		}

		byte type;


		private TonValue(int type)
		{
			this.type = (byte) type;
		}


		public static TonValue reverse(byte type) throws EnumReverseElementNotFoundException
		{
			if (lookup.containsKey(type))
			{
				return lookup.get(type);
			}

			throw new EnumReverseElementNotFoundException(type);
		}
	}

	private TonValue value;


	public CallingTon(TonValue value)
	{
		super();
		this.value = value;
		this.data = new byte[] { value.type };
		this.dataLength = 1;
	}


	public CallingTon(byte[] data) throws InvalidArgumentException
	{
		super(data);

		if (this.data.length != 1)
		{
			throw new InvalidArgumentException("Need exactly one byte!");
		}

		try
		{
			this.value = TonValue.reverse(this.data[0]);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CALLINGTON;
	}


	public TonValue getPresentation()
	{
		return value;
	}


	@Override
	public String toString()
	{
		return String.format("CallingTon(value=%s)", value);
	}

}