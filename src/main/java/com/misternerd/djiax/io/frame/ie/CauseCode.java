package com.misternerd.djiax.io.frame.ie;

import java.util.HashMap;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;

/**
 * Implements the CAUSECODE information element. From RFC 5456:
 * 
 * The purpose of the CAUSECODE information element is to indicate the
 * reason a call was REJECTed or HANGUPed.  It derives from ITU-T
 * Recommendation Q.931.  The data field is one octet long and contains
 * an entry from the table below.
 * 
 * The CAUSECODE information element SHOULD be sent with IAX HANGUP,
 * REJECT, REGREJ, and TXREJ messages.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x2a), //     0x01     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Cause Code  |
 * +-+-+-+-+-+-+-+-+
 */
public class CauseCode extends InformationElement
{
	/**
	 * This defines the identifier for this kind of information element.
	 */
	public static final byte TYPE = 0x2a;

	/**
	 * This lists the allowed values for presentation service.
	 */
	public static enum Cause
	{
		/**
		 * This might mean a fault in the dialplan.
		 */
		UNKNOWN_CODE(0),
		UNASSIGNED_NUMBER(1), NO_ROUTE_TO_NETWORK(2), NO_ROUTE_TO_DESTINATION(3), 
		CHANNEL_UNACCEPTABLE(6), CALL_AWARDED_AND_DELIVERED(7), NORMAL_CALL_CLEARING(16), 
		USER_BUSY(17), NO_USER_RESPONSE(18), NO_ANSWER(19), 
		UNASSIGNED_20(20), CALL_REJECTED(21), NUMBER_CHANGED(22), 
		DESTINATION_OUT_OF_ORDER(27), INVALID_NUMBER_FORMAT(28), 
		FACILITY_REJECTED(29), RESPONSE_TO_STATUS_ENQUIRY(30), 
		NORMAL_UNSPECIFIED(31), NO_CHANNEL_AVAILABLE(34), NETWORK_OUT_OF_ORDER(38), 
		TEMPORAY_FAILURE(41), SWITCH_CONGESTION(42), ACCESS_INFORMATION_DISCARDED(43), 
		REQUESTED_CHANNEL_NOT_AVAILABLE(44), PREEMPTED(45), RESOURCE_UNAVAILABLE(47), 
		FACILITY_NOT_SUBSCRIBED(50), OUTGOING_CALL_BARRED(52), INCOMING_CALL_BARRED(54), 
		BEARER_CAPABILITY_NOT_AUTHORIZED(57), BEARER_CAPABILITY_NOT_AVAILABLE(58), 
		SERVICE_OR_OPTION_NOT_AVAILABLE(63), BEARER_CAPABILITY_NOT_IMPLEMENTED(65), 
		CHANNEL_TYPE_NOT_IMPLEMENTED(66), FACILITY_NOT_IMPLEMENTED(69), 
		ONLY_RESTRICTED_BEARER_CAPABILITY_AVAILABLE(70), SERVICE_NOT_AVAILABLE(79), 
		INVALID_CALL_REFERENCE(81), IDENTIFIED_CHANNEL_NOT_EXISTENT(82), SUSPENDED_CALL_EXISTS(83), 
		CALL_IDENTITY_IN_USE(84), NO_CALL_SUSPENDED(85), CALL_CLEARED(86), 
		INCOMPATIBLE_DESTINATION(88), INVALID__TRANSIT_NETWORK_SELECTION(91), INVALID_MESSAGE(95), 
		MANDATORY_INFORMATION_ELEMENT_MISSING(96), MESSAGE_TYPE_NONEXISTENT(97), 
		MESSAGE_NOT_COMPATIBLE(98), INFORMATION_ELEMENT_NONEXISTENT(99), 
		INVALID_INFORMATION_ELEMENT_CONTENTS(100), MESSAGE_NOT_COMPATIBLE_WITH_CALLSTATE(101), 
		RECOVERY_ON_TIMER_EXPIRATION(102), MANDATORY_INFORMATION_ELEMENT_LENGTH_ERROR(103), 
		PROTOCOL_ERROR(111), INTERNETWORKING(127);

		private static final HashMap<Byte, Cause> lookup = new HashMap<>();

		static
		{
			for (Cause value : Cause.values())
			{
				lookup.put(value.code, value);
			}
		}

		public byte code;


		private Cause(int code)
		{
			this.code = (byte) code;
		}


		public static Cause reverse(byte code) throws EnumReverseElementNotFoundException
		{
			if (lookup.containsKey(code))
			{
				return lookup.get(code);
			}

			throw new EnumReverseElementNotFoundException(code);
		}
	}

	private Cause cause;


	public CauseCode(Cause cause)
	{
		super();
		this.cause = cause;
		this.data = new byte[] { cause.code };
		this.dataLength = 1;
	}


	public CauseCode(byte[] data) throws InvalidArgumentException
	{
		super(data);

		if (this.data.length != 1)
		{
			throw new InvalidArgumentException("Need exactly one byte!");
		}

		try
		{
			this.cause = Cause.reverse(this.data[0]);
		}
		catch (EnumReverseElementNotFoundException e)
		{
			throw new InvalidArgumentException(e);
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.CAUSECODE;
	}


	public Cause getCause()
	{
		return cause;
	}


	@Override
	public String toString()
	{
		return String.format("CauseCode(cause=%s)", cause);
	}

}