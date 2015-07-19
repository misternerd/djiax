package com.misternerd.djiax.util;

import java.io.UnsupportedEncodingException;

import com.misternerd.djiax.exception.InformationElementNotFoundException;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.ie.Cause;
import com.misternerd.djiax.io.frame.ie.CauseCode;

public class IaxUtil
{

	public static CauseCode getCauseCodeFromIaxFrame(IaxFrame iaxFrame)
	{
		try
		{
			return (CauseCode) iaxFrame.getInformationElement(InformationElementType.CAUSECODE);
		}
		catch (InformationElementNotFoundException e)
		{
			return new CauseCode(CauseCode.Cause.SERVICE_OR_OPTION_NOT_AVAILABLE);
		}
	}


	public static Cause getCauseFromIaxFrame(IaxFrame iaxFrame) throws UnsupportedEncodingException
	{
		try
		{
			return (Cause) iaxFrame.getInformationElement(InformationElementType.CAUSE);
		}
		catch (InformationElementNotFoundException e)
		{
			return new Cause("Unknown cause since IE not present");
		}
	}

}
