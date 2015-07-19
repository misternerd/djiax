package com.misternerd.djiax.util;

import java.util.HashMap;

import com.misternerd.djiax.exception.EnumReverseElementNotFoundException;

/**
 * Contains enumerations listing the available audio-, video- and image formats.
 * Also defines the common interface for all media formats, that each enum
 * implements.
 */
public enum MediaFormat
{
	/*
	 * AUDIO
	 */
	G7231(0x00000001L, FormatType.AUDIO),
	GSM_FULL_RATE(0x00000002L, FormatType.AUDIO),
	G711_MULAW(0x00000004L, FormatType.AUDIO),
	G711_ALAW(0x00000008L, FormatType.AUDIO),
	G726(0x00000010L, FormatType.AUDIO),
	ADPCM(0x00000020L, FormatType.AUDIO),
	LE_16_BIT_LINEAR(0x00000040L, FormatType.AUDIO),
	LPC10(0x00000080L, FormatType.AUDIO),
	G729(0x00000100L, FormatType.AUDIO),
	SPEEX(0x00000200L, FormatType.AUDIO),
	ILBC(0x00000400L, FormatType.AUDIO),
	G7262(0x00000800L, FormatType.AUDIO),
	G722(0x00001000L, FormatType.AUDIO),
	AMR(0x00002000L, FormatType.AUDIO),

	/*
	 * VIDEO
	 */
	H261(0x00040000L, FormatType.VIDEO),
	H263(0x00080000L, FormatType.VIDEO),
	H263P(0x00100000L, FormatType.VIDEO),
	H264(0x00200000L, FormatType.VIDEO),

	/*
	 * IMAGE
	 */
	JPEG(0x00010000L, FormatType.IMAGE),
	PNG(0x00020000L, FormatType.IMAGE);

	public enum FormatType
	{
		AUDIO, VIDEO, IMAGE
	}

	private static final HashMap<Long, MediaFormat> lookup = new HashMap<>();

	static
	{
		for (MediaFormat format : MediaFormat.values())
		{
			lookup.put(format.format, format);
		}
	}

	private long format;

	private FormatType type;


	MediaFormat(long format, FormatType type)
	{
		this.format = format;
		this.type = type;
	}


	public long getFormat()
	{
		return format;
	}


	public FormatType getType()
	{
		return type;
	}


	public static MediaFormat reverse(long type) throws EnumReverseElementNotFoundException
	{
		if (lookup.containsKey(type))
		{
			return lookup.get(type);
		}

		throw new EnumReverseElementNotFoundException(type);
	}

}