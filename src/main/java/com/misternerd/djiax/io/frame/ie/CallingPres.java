package com.misternerd.djiax.io.frame.ie;

import java.util.HashMap;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CALLINGPRES information element. From RFC 5456:
 * 
 * The purpose of the CALLINGPRES information element is to indicate the
 * calling presentation of a caller.  The data field is 1 octet long and
 * contains a value from the table below.
 * 
 * The CALLINGPRES information element MUST be sent with IAX NEW
 * messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x26     |      0x01     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Calling Pres. |
 * +-+-+-+-+-+-+-+-+
 *
 * The following table lists valid calling presentation values:
 * 	
 * 	  +------+--------------------------------------+
 * 	  | FLAG | PRESENTATION                         |
 * 	  +------+--------------------------------------+
 * 	  | 0x00 | Allowed user/number not screened     |
 * 	  |      |                                      |
 * 	  | 0x01 | Allowed user/number passed screen    |
 * 	  |      |                                      |
 * 	  | 0x02 | Allowed user/number failed screen    |
 * 	  |      |                                      |
 * 	  | 0x03 | Allowed network number               |
 * 	  |      |                                      |
 * 	  | 0x20 | Prohibited user/number not screened  |
 * 	  |      |                                      |
 * 	  | 0x21 | Prohibited user/number passed screen |
 * 	  |      |                                      |
 * 	  | 0x22 | Prohibited user/number failed screen |
 * 	  |      |                                      |
 * 	  | 0x23 | Prohibited network number            |
 * 	  |      |                                      |
 * 	  | 0x43 | Number not available                 |
 * 	  +------+--------------------------------------+
 */
public class CallingPres extends InformationElement
{
	public static final byte TYPE = 0x26;

	public static enum PresentationValue
	{
		ALLOWED_USER_NUMBER_NOT_SCREENED(0x00), ALLOWED_USER_NUMBER_PASSED_SCREEN(0x01), 
		ALLOWED_USER_NUMBER_FAILED_SCREEN(0x02), ALLOWED_NETWORK_NUMBER(0x03), 
		PROHIBITED_USER_NUMBER_NOT_SCREENED(0x20), PROHIBITED_USER_NUMBER_PASSED_SCREEN(0x21), 
		PROHIBITED_USER_NUMBER_FAILED_SCREEN(0x22), PROHIBITED_NETWORK_NUMBER(0x23), 
		NUMBER_NOT_AVAILABLE(0x43);

		private static final HashMap<Byte, PresentationValue> lookup = new HashMap<>();

		static
		{
			for (PresentationValue value : PresentationValue.values())
			{
				lookup.put(value.type, value);
			}
		}

		byte type;


		private PresentationValue(int type)
		{
			this.type = (byte) type;
		}


		public static PresentationValue reverse(byte type) throws EnumReverseElementNotFoundException
		{
			if (lookup.containsKey(type))
			{
				return lookup.get(type);
			}

			throw new EnumReverseElementNotFoundException(type);
		}
	}

	private PresentationValue presentation;


	public CallingPres(PresentationValue presentation)
	{
		super();
		this.presentation = presentation;
		this.data = new byte[] { presentation.type };
		this.dataLength = 1;
	}


	public CallingPres(byte[] data) throws InvalidArgumentException
	{
		super(data);

		if (this.data.length != 1)
		{
			throw new InvalidArgumentException("Need exactly one byte!");
		}

		try
		{
			this.presentation = PresentationValue.reverse(this.data[0]);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CALLINGPRES;
	}


	public PresentationValue getPresentation()
	{
		return presentation;
	}


	@Override
	public String toString()
	{
		return String.format("CallingPres(presentation=%s)", presentation);
	}

}