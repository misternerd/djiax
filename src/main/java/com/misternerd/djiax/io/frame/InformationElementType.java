package com.misternerd.djiax.io.frame;

import java.util.HashMap;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;
import com.misternerd.djiax.io.frame.ie.Adsipce;
import com.misternerd.djiax.io.frame.ie.ApparentAddr;
import com.misternerd.djiax.io.frame.ie.Authmethods;
import com.misternerd.djiax.io.frame.ie.CalledContext;
import com.misternerd.djiax.io.frame.ie.CalledNumber;
import com.misternerd.djiax.io.frame.ie.CallingAni;
import com.misternerd.djiax.io.frame.ie.CallingName;
import com.misternerd.djiax.io.frame.ie.CallingNumber;
import com.misternerd.djiax.io.frame.ie.CallingPres;
import com.misternerd.djiax.io.frame.ie.CallingTon;
import com.misternerd.djiax.io.frame.ie.Capability;
import com.misternerd.djiax.io.frame.ie.Cause;
import com.misternerd.djiax.io.frame.ie.CauseCode;
import com.misternerd.djiax.io.frame.ie.Challenge;
import com.misternerd.djiax.io.frame.ie.Datetime;
import com.misternerd.djiax.io.frame.ie.Dnid;
import com.misternerd.djiax.io.frame.ie.Format;
import com.misternerd.djiax.io.frame.ie.IaxUnknown;
import com.misternerd.djiax.io.frame.ie.Language;
import com.misternerd.djiax.io.frame.ie.Md5Result;
import com.misternerd.djiax.io.frame.ie.Refresh;
import com.misternerd.djiax.io.frame.ie.RrDelay;
import com.misternerd.djiax.io.frame.ie.RrDropped;
import com.misternerd.djiax.io.frame.ie.RrJitter;
import com.misternerd.djiax.io.frame.ie.RrLoss;
import com.misternerd.djiax.io.frame.ie.RrOoo;
import com.misternerd.djiax.io.frame.ie.RrPkts;
import com.misternerd.djiax.io.frame.ie.SamplingRate;
import com.misternerd.djiax.io.frame.ie.Username;
import com.misternerd.djiax.io.frame.ie.Version;

public enum InformationElementType
{
	/**
	 * Number/extension being called
	 */
	CALLED_NUMBER(0x01, CalledNumber.class),
	/**
	 * Calling number
	 */
	CALLING_NUMBER(0x02, CallingNumber.class),
	/**
	 * Calling number ANI for billing
	 */
	CALLING_ANI(0x03, CallingAni.class),
	/**
	 * Name of caller
	 */
	CALLING_NAME(0x04, CallingName.class),
	/**
	 * Context for number
	 */
	CALLED_CONTEXT(0x05, CalledContext.class),
	/**
	 * Username (peer or user) for authentication
	 */
	USERNAME(0x06, Username.class),
	/**
	 * Password for authentication not supported anymore
	 */
	PASSWORD(0x07, null),
	/**
	 * Actual CODEC capability
	 */
	CAPABILITY(0x08, Capability.class),
	/**
	 * Desired CODEC format
	 */
	FORMAT(0x09, Format.class),
	/**
	 * Desired language
	 */
	LANGUAGE(0x0a, Language.class),
	/**
	 * Protocol version
	 */
	VERSION(0x0b, Version.class),
	/**
	 * CPE ADSI capability
	 */
	ADSICPE(0x0c, Adsipce.class),
	/**
	 * Originally dialed DNID not supported yet
	 */
	DNID(0x0d, Dnid.class),
	/**
	 * Authentication method(s)
	 */
	AUTHMETHODS(0x0e, Authmethods.class),
	/**
	 * Challenge data for MD5/RSA
	 */
	CHALLENGE(0x0f, Challenge.class),
	/**
	 * MD5 challenge result
	 */
	MD5_RESULT(0x10, Md5Result.class),
	/**
	 * RSA challenge result not supported yet
	 */
	RSA_RESULT(0x11, null),
	/**
	 * Apparent address of peer
	 */
	APPARENT_ADDR(0x12, ApparentAddr.class),
	/**
	 * When to refresh registration
	 */
	REFRESH(0x13, Refresh.class),
	/**
	 * Dialplan status not supported yet
	 */
	DPSTATUS(0x14, null),
	/**
	 * Call number of peer not supported yet
	 */
	CALLNO(0x15, null),
	/**
	 * Cause
	 */
	CAUSE(0x16, Cause.class),
	/**
	 * Unknown IAX command
	 */
	IAX_UNKNOWN(0x17, IaxUnknown.class),
	/**
	 * How many messages waiting not supported yet
	 */
	MSGCOUNT(0x18, null),
	/**
	 * Request auto-answering not supported yet
	 */
	AUTOANSWER(0x19, null),
	/**
	 * Request musiconhold with QUELCH not supported yet
	 */
	MUSICONHOLD(0x1a, null),
	/**
	 * Transfer Request Identifier not supported yet
	 */
	TRANSFERID(0x1b, null),
	/**
	 * Referring DNIS not supported yet
	 */
	RDNIS(0x1c, null),
	/**
	 * Date/Time
	 */
	DATETIME(0x1f, Datetime.class),
	/**
	 * Calling presentation
	 */
	CALLINGPRES(0x26, CallingPres.class),
	/**
	 * Calling type of number
	 */
	CALLINGTON(0x27, CallingTon.class),
	/**
	 * Calling transit network select not supported yet
	 */
	CALLINGTNS(0x28, null),
	/**
	 * Supported sampling rates
	 */
	SAMPLINGRATE(0x29, SamplingRate.class),
	/**
	 * Hangup cause
	 */
	CAUSECODE(0x2a, CauseCode.class),
	/**
	 * Encryption format not supported yet
	 */
	ENCRYPTION(0x2b, null),
	/**
	 * Reserved for future Use not supported yet
	 */
	ENCKEY(0x2c, null),
	/**
	 * CODEC Negotiation not supported yet (use CAPABILITY & FORMAT instead)
	 */
	CODEC_PREFS(0x2d, null),
	/**
	 * Received jitter, as in RFC 3550 not supported yet
	 */
	RR_JITTER(0x2e, RrJitter.class),
	/**
	 * Received loss, as in RFC 3550 not supported yet
	 */
	RR_LOSS(0x2f, RrLoss.class),
	/**
	 * Received frames not supported yet
	 */
	RR_PKTS(0x30, RrPkts.class),
	/**
	 * Max playout delay for received frames in ms not supported yet
	 */
	RR_DELAY(0x31, RrDelay.class),
	/**
	 * Dropped frames (presumably by jitter buffer) not supported yet
	 */
	RR_DROPPED(0x32, RrDropped.class),
	/**
	 * Frames received Out of Order not supported yet
	 */
	RR_OOO(0x33, RrOoo.class),
	/**
	 * OSP Token Block not supported yet
	 */
	OSPTOKEN(0x34, null);

	private static final HashMap<Short, InformationElementType> lookup = new HashMap<>();

	static
	{
		for (InformationElementType informationElementType : InformationElementType.values())
		{
			lookup.put(informationElementType.getType(), informationElementType);
		}
	}

	private short type;

	private Class<? extends InformationElement> cls;


	InformationElementType(int type, Class<? extends InformationElement> cls)
	{
		this.type = (byte) type;
		this.cls = cls;
	}


	public static InformationElementType reverse(short type) throws EnumReverseElementNotFoundException
	{
		if (lookup.containsKey(type))
		{
			return lookup.get(type);
		}

		throw new EnumReverseElementNotFoundException(type);
	}


	public short getType()
	{
		return type;
	}


	public Class<? extends InformationElement> getCls()
	{
		return cls;
	}

}