package com.misternerd.djiax.exception;

public class EnumReverseElementNotFoundException extends IaxException
{

	private static final long serialVersionUID = 1L;

	
	public EnumReverseElementNotFoundException(long subclass)
	{
		super(String.format("Reverse element  for %d not found", subclass));
	}
}
