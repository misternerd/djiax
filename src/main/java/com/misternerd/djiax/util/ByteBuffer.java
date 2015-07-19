package com.misternerd.djiax.util;

/**
 * This ByteBuffer is taken from the njiax library. It is especially crafted for
 * the use in an IAX library as it allows access to certain bits or bytes.
 */
public class ByteBuffer
{
	private final static int BITS_IN_A_BYTE = 8;

	private final static int BYTE_MASK_LOWER_BYTE = 0xff;

	public final static int BYTES_IN_8BITS = 1;

	public final static int BYTES_IN_16BITS = 2;

	public final static int BYTES_IN_24BITS = 2;

	public final static int BYTES_IN_32BITS = 4;

	private int currentBufferPosition;

	private byte[] buffer;


	public ByteBuffer(int bufferSize)
	{
		if (bufferSize > 0)
		{
			this.buffer = new byte[bufferSize];
			this.currentBufferPosition = 0;
		}
		else
		{
			throw new IllegalArgumentException("ByteBuffer's length isn't greater than zero");
		}
	}


	/**
	 * Construct a byte buffer from a given byte array.
	 * 
	 * @param buffer
	 *            Byte array to initialize the buffer.
	 */
	public ByteBuffer(byte[] buffer)
	{
		this.buffer = buffer;
		this.currentBufferPosition = 0;
	}


	public int getNumberOfBytesFree()
	{
		return buffer.length - currentBufferPosition;
	}


	public boolean hasFreeSpaceAvailable()
	{
		return currentBufferPosition <= buffer.length;
	}


	public byte[] getBuffer()
	{
		return buffer;
	}


	/**
	 * Gets the next 8 bits in the buffer.
	 */
	public short get8bits() throws IndexOutOfBoundsException
	{
		if (currentBufferPosition + BYTES_IN_8BITS <= buffer.length)
		{
			short result = buffer[currentBufferPosition];
			currentBufferPosition += BYTES_IN_8BITS;
			return result;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Gets 8 bits from the buffer given a position.
	 */
	public short get8bits(int pos) throws IndexOutOfBoundsException
	{
		if (pos + BYTES_IN_8BITS <= buffer.length)
		{
			short result = buffer[pos];
			return result;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores 8 bits in the buffer.
	 */
	public void put8bits(short value) throws IndexOutOfBoundsException
	{
		if (currentBufferPosition + BYTES_IN_8BITS <= buffer.length)
		{
			buffer[currentBufferPosition] = (byte) value;
			currentBufferPosition += BYTES_IN_8BITS;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores 8 bits in the buffer in a given position. Postion must be smaller
	 * than buffer size minus 1.
	 */
	public void put8bits(short value, int pos) throws IndexOutOfBoundsException
	{
		if (pos + BYTES_IN_8BITS <= buffer.length)
		{
			buffer[pos] = (byte) value;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Gets the next 16 bits in the buffer.
	 */
	public int get16bits() throws IndexOutOfBoundsException
	{
		if (currentBufferPosition + BYTES_IN_16BITS <= buffer.length)
		{
			int result = 0;

			for (int i = 0, j = BYTES_IN_16BITS - 1; i < BYTES_IN_16BITS; i++, j--)
			{
				result += (buffer[currentBufferPosition + i] & BYTE_MASK_LOWER_BYTE) << (j * BITS_IN_A_BYTE);
			}

			currentBufferPosition += BYTES_IN_16BITS;

			return result;
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Gets 16 bits from the buffer given a position.
	 */
	public int get16bits(int pos) throws IndexOutOfBoundsException
	{
		if (pos + BYTES_IN_16BITS <= buffer.length)
		{
			int result = 0;

			for (int i = 0, j = BYTES_IN_16BITS - 1; i < BYTES_IN_16BITS; i++, j--)
			{
				result += (buffer[pos + i] & BYTE_MASK_LOWER_BYTE) << (j * BITS_IN_A_BYTE);
			}

			return result;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores 16 bits in the buffer.
	 */
	public void put16bits(int value) throws IndexOutOfBoundsException
	{
		if (currentBufferPosition + BYTES_IN_16BITS <= buffer.length)
		{
			for (int i = 0, j = BYTES_IN_16BITS - 1; i < BYTES_IN_16BITS; i++, j--)
			{
				buffer[currentBufferPosition + i] = (byte) (value >> (j * BITS_IN_A_BYTE) & BYTE_MASK_LOWER_BYTE);
			}

			currentBufferPosition += BYTES_IN_16BITS;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores 16 bits in the buffer in a given position. Postion must be smaller
	 * than buffer size minus 2.
	 */
	public void put16bits(int value, int pos) throws IndexOutOfBoundsException
	{
		if (pos + BYTES_IN_16BITS <= buffer.length)
		{
			for (int i = 0, j = BYTES_IN_16BITS - 1; i < BYTES_IN_16BITS; i++, j--)
			{
				buffer[pos + i] = (byte) (value >> (j * BITS_IN_A_BYTE) & BYTE_MASK_LOWER_BYTE);
			}
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Gets the next 24 bits in the buffer.
	 */
	public int get24bits() throws IndexOutOfBoundsException
	{
		if (currentBufferPosition + BYTES_IN_24BITS <= buffer.length)
		{
			int result = 0;

			for (int i = 0, j = BYTES_IN_24BITS - 1; i < BYTES_IN_24BITS; i++, j--)
			{
				result += (buffer[currentBufferPosition + i] & BYTE_MASK_LOWER_BYTE) << (j * BITS_IN_A_BYTE);
			}

			currentBufferPosition += BYTES_IN_24BITS;

			return result;
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Gets 24 bits from the buffer given a position.
	 */
	public int get24bits(int pos) throws IndexOutOfBoundsException
	{
		if (pos + BYTES_IN_24BITS <= buffer.length)
		{
			int result = 0;

			for (int i = 0, j = BYTES_IN_24BITS - 1; i < BYTES_IN_24BITS; i++, j--)
			{
				result += (buffer[pos + i] & BYTE_MASK_LOWER_BYTE) << (j * BITS_IN_A_BYTE);
			}

			return result;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores 24 bits in the buffer.
	 */
	public void put24bits(int value) throws IndexOutOfBoundsException
	{
		if (currentBufferPosition + BYTES_IN_24BITS <= buffer.length)
		{
			for (int i = 0, j = BYTES_IN_24BITS - 1; i < BYTES_IN_24BITS; i++, j--)
			{
				buffer[currentBufferPosition + i] = (byte) (value >> (j * BITS_IN_A_BYTE) & BYTE_MASK_LOWER_BYTE);
			}

			currentBufferPosition += BYTES_IN_24BITS;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores 24 bits in the buffer in a given position. Postion must be smaller
	 * than buffer size minus 2.
	 */
	public void put24bits(int value, int pos) throws IndexOutOfBoundsException
	{
		if (pos + BYTES_IN_24BITS <= buffer.length)
		{
			for (int i = 0, j = BYTES_IN_24BITS - 1; i < BYTES_IN_24BITS; i++, j--)
			{
				buffer[pos + i] = (byte) (value >> (j * BITS_IN_A_BYTE) & BYTE_MASK_LOWER_BYTE);
			}
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Gets the next 32 bits in the buffer.
	 */
	public long get32bits() throws IndexOutOfBoundsException
	{
		if (currentBufferPosition + BYTES_IN_32BITS <= buffer.length)
		{
			long result = 0;

			for (int i = 0, j = BYTES_IN_32BITS - 1; i < BYTES_IN_32BITS; i++, j--)
			{
				result += (long) ((buffer[currentBufferPosition + i] & BYTE_MASK_LOWER_BYTE) << (j * BITS_IN_A_BYTE));
			}

			currentBufferPosition += BYTES_IN_32BITS;

			return result;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Gets 32 bits from the buffer given a position.
	 */
	public long get32bits(int pos) throws IndexOutOfBoundsException
	{
		if (pos + BYTES_IN_32BITS <= buffer.length)
		{
			long result = 0;

			for (int i = 0, j = BYTES_IN_32BITS - 1; i < BYTES_IN_32BITS; i++, j--)
			{
				result += (long) ((buffer[pos + i] & BYTE_MASK_LOWER_BYTE) << (j * BITS_IN_A_BYTE));
			}

			return result;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores 32 bits in the buffer.
	 */
	public void put32bits(long value) throws IndexOutOfBoundsException
	{
		if (currentBufferPosition + BYTES_IN_32BITS <= buffer.length)
		{
			for (int i = 0, j = BYTES_IN_32BITS - 1; i < BYTES_IN_32BITS; i++, j--)
			{
				buffer[currentBufferPosition + i] = (byte) (value >> (j * BITS_IN_A_BYTE) & BYTE_MASK_LOWER_BYTE);
			}

			currentBufferPosition += BYTES_IN_32BITS;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores 32 bits in the buffer at a given position. Postion must be smaller
	 * than buffer size minus SIZE_32BITS.
	 */
	public void put32bits(long value, int pos) throws IndexOutOfBoundsException
	{
		if (pos + BYTES_IN_32BITS <= buffer.length)
		{
			for (int i = 0, j = BYTES_IN_32BITS - 1; i < BYTES_IN_32BITS; i++, j--)
			{
				buffer[pos + i] = (byte) (value >> (j * BITS_IN_A_BYTE) & BYTE_MASK_LOWER_BYTE);
			}
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Gets the unread bytes from the buffer.
	 */
	public byte[] getByteArray() throws IndexOutOfBoundsException
	{
		if (currentBufferPosition <= buffer.length)
		{
			byte[] result = new byte[buffer.length - currentBufferPosition];
			System.arraycopy(buffer, currentBufferPosition, result, 0, result.length);
			currentBufferPosition += result.length;

			return result;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Gets the bytes from pos to buffer end.
	 */
	public byte[] getByteArray(int pos) throws IndexOutOfBoundsException
	{
		if (pos <= buffer.length)
		{
			byte[] result = new byte[buffer.length - pos];
			System.arraycopy(buffer, pos, result, 0, result.length);
			return result;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores a byte array in the buffer, beginning at the first position
	 */
	public void putByteArray(byte byteArray[]) throws IndexOutOfBoundsException
	{
		if (currentBufferPosition + byteArray.length <= buffer.length)
		{
			System.arraycopy(byteArray, 0, buffer, currentBufferPosition, byteArray.length);
			currentBufferPosition += byteArray.length;
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}


	/**
	 * Stores a byte array in a given position of the buffer. The position plus
	 * the byte array length must be smaller than the buffer size.
	 */
	public void putByteArray(byte byteArray[], int pos) throws IndexOutOfBoundsException
	{
		if (pos + byteArray.length <= buffer.length)
		{
			System.arraycopy(byteArray, 0, buffer, pos, byteArray.length);
		}
		else
		{
			throw new IndexOutOfBoundsException("ByteBuffer index of bound");
		}
	}

}