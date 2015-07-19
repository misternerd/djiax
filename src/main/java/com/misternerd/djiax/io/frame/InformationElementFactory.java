package com.misternerd.djiax.io.frame;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.exception.InvalidInformationElementException;
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

public class InformationElementFactory
{

	public static InformationElement createFromType(short type, byte[] data) 
			throws InvalidInformationElementException, InvalidArgumentException
	{
		switch (type)
		{
			case CalledNumber.TYPE:
			{
				return new CalledNumber(data);
			}
			case CallingNumber.TYPE:
			{
				return new CallingNumber(data);
			}
			case CallingAni.TYPE:
			{
				return new CallingAni(data);
			}
			case CallingName.TYPE:
			{
				return new CallingName(data);
			}
			case CalledContext.TYPE:
			{
				return new CalledContext(data);
			}
			case Username.TYPE:
			{
				return new Username(data);
			}
			case Capability.TYPE:
			{
				return new Capability(data);
			}
			case Format.TYPE:
			{
				return new Format(data);
			}
			case Language.TYPE:
			{
				return new Language(data);
			}
			case Version.TYPE:
			{
				return new Version(data);
			}
			case Adsipce.TYPE:
			{
				return new Adsipce(data);
			}
			case Authmethods.TYPE:
			{
				return new Authmethods(data);
			}
			case Challenge.TYPE:
			{
				return new Challenge(data);
			}
			case Md5Result.TYPE:
			{
				return new Md5Result(data);
			}
			case ApparentAddr.TYPE:
			{
				return new ApparentAddr(data);
			}
			case Refresh.TYPE:
			{
				return new Refresh(data);
			}
			case Cause.TYPE:
			{
				return new Cause(data);
			}
			case IaxUnknown.TYPE:
			{
				return new IaxUnknown(data);
			}
			case Datetime.TYPE:
			{
				return new Datetime(data);
			}
			case CallingPres.TYPE:
			{
				return new CallingPres(data);
			}
			case CallingTon.TYPE:
			{
				return new CallingTon(data);
			}
			case SamplingRate.TYPE:
			{
				return new SamplingRate(data);
			}
			case CauseCode.TYPE:
			{
				return new CauseCode(data);
			}
			case RrJitter.TYPE:
			{
				return new RrJitter(data);
			}
			case RrLoss.TYPE:
			{
				return new RrLoss(data);
			}
			case RrPkts.TYPE:
			{
				return new RrPkts(data);
			}
			case RrDelay.TYPE:
			{
				return new RrDelay(data);
			}
			case RrDropped.TYPE:
			{
				return new RrDropped(data);
			}
			case RrOoo.TYPE:
			{
				return new RrOoo(data);
			}
			default:
			{
				throw new InvalidInformationElementException(type);
			}
		}
	}

}
