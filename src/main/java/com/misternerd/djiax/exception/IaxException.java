package com.misternerd.djiax.exception;

public abstract class IaxException extends Exception
{

	private static final long serialVersionUID = 1L;


	public IaxException()
	{
		super();
	}


	public IaxException(String message)
	{
		super();
	}


	public IaxException(Throwable tr)
	{
		super(tr);
	}


	public IaxException(String Message, Throwable tr)
	{
		super(tr);
	}

}
