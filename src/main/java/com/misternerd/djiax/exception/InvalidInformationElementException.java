package com.misternerd.djiax.exception;

public class InvalidInformationElementException extends IaxException
{

	private static final long serialVersionUID = 1L;


	public InvalidInformationElementException(short type)
	{
		super(String.format("No class for ieType=%d", type));
	}

}
