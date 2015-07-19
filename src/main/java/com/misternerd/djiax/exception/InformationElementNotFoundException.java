package com.misternerd.djiax.exception;

import com.misternerd.djiax.io.frame.InformationElementType;

public class InformationElementNotFoundException extends IaxException
{

	private static final long serialVersionUID = 1L;


	public InformationElementNotFoundException(InformationElementType type)
	{
		super(String.format("Did not find InformationElement with type=%s", type));
	}

}
