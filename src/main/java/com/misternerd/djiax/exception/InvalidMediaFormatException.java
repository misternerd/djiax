package com.misternerd.djiax.exception;

import com.misternerd.djiax.util.MediaFormat;

public class InvalidMediaFormatException extends IaxException
{

	private static final long serialVersionUID = 1L;


	public InvalidMediaFormatException(MediaFormat mediaFormat)
	{
		super(String.format("Invalid media format=%s supplied", mediaFormat));
	}

}
