package com.misternerd.djiax.io.frame.ie;

import java.util.Calendar;
import java.util.TimeZone;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the DATETIME information element. From RFC 5456:
 * 
 * The DATETIME information element indicates the time a message is
 * sent.  This differs from the header time-stamp because that time-
 * stamp begins at 0 for each call, while the DATETIME is a call-
 * independent value representing the actual real-world time.  The data
 * field of a DATETIME information element is four octets long and
 * stores the time as follows: the 5 least significant bits are seconds,
 * the next 6 least significant bits are minutes, the next least
 * significant 5 bits are hours, the next least significant 5 bits are
 * the day of the month, the next least significant 4 bits are the
 * month, and the most significant 7 bits are the year.  The year is
 * offset from 2000, and the month is a 1-based index (i.e., January ==
 * 1, February == 2, etc.).  The timezone of the clock MUST be UTC to
 * avoid confusion between the peers.
 * 
 * The DATETIME information element SHOULD be sent with IAX NEW and
 * REGACK messages.  However, it is strictly informational.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x1f     |      0x04     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |     year    | month |   day   |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  hours  |  minutes  | seconds |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class Datetime extends InformationElement
{

	public static final byte TYPE = 0x1F;

	private Calendar date;


	public Datetime(Calendar date)
	{
		super();
		this.date = date;

		// make sure the timezone is UTC
		date.setTimeZone(TimeZone.getTimeZone("UTC"));

		ByteBuffer buffer = new ByteBuffer(4);
		short tmpValue = 0;

		// day, month, year: 2 bytes
		tmpValue += ((date.get(Calendar.YEAR) - 2000) & 0xFE00);
		tmpValue += (date.get(Calendar.MONTH) & 0x1E0);
		tmpValue += (date.get(Calendar.DAY_OF_MONTH) & 0x1F);
		buffer.put16bits(tmpValue);

		tmpValue = 0;

		// hours (5 bits), minutes (6 bits), seconds (5 bits)
		tmpValue += (date.get(Calendar.HOUR_OF_DAY) & 0xF800);
		tmpValue += (date.get(Calendar.MINUTE) & 0x7E0);
		tmpValue += (date.get(Calendar.SECOND) & 0x1F);
		buffer.put16bits(tmpValue);

		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}


	public Datetime(byte[] data) throws InvalidArgumentException
	{
		super(data);

		ByteBuffer buffer = new ByteBuffer(this.data);
		this.date = Calendar.getInstance();

		// first 16 bits: year, month, day
		int tmpValue = buffer.get16bits();
		int tmpField = ((tmpValue & 0xFE00) >> 9) + 2000;
		date.set(Calendar.YEAR, tmpField);
		tmpField = (tmpValue & 0x1E0) >> 5;
		date.set(Calendar.MONTH, tmpField - 1);
		tmpField = (tmpValue & 0x1F);
		date.set(Calendar.DAY_OF_MONTH, tmpField);

		// second 16 bits: hour, minutes, seconds
		tmpValue = buffer.get16bits();
		tmpField = (tmpValue & 0xF800) >> 11;
		date.set(Calendar.HOUR_OF_DAY, tmpField);
		tmpField = (tmpValue & 0x7E0) >> 5;
		date.set(Calendar.MINUTE, tmpField);
		tmpField = (tmpValue & 0x1F);
		date.set(Calendar.SECOND, tmpField);
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.DATETIME;
	}


	public Calendar getDatetime()
	{
		return date;
	}


	@Override
	public String toString()
	{
		return String.format("Datetime(date=%s)", date);
	}

}