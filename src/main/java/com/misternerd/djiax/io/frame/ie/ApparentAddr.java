package com.misternerd.djiax.io.frame.ie;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.InformationElement;
import com.misternerd.djiax.io.frame.InformationElementType;
import com.misternerd.djiax.util.ByteBuffer;

/**
 * Implements the APPARENT ADDR information element. From RFC 5456:
 * 
 * The purpose of the APPARENT ADDR information element is to indicate
 * the perceived network connection information used to reach a peer,
 * which may differ from the actual address when the peer is behind NAT.
 * The APPARENT ADDR IE is populated using the source address values of
 * the UDP and IP headers in the IAX message to which this response is
 * generated.  The data field of the APPARENT ADDR information element
 * is the same as the POSIX sockaddr struct for the address family in
 * use (i.e., sockaddr_in for IPv4, sockaddr_in6 for IPv6).  The data
 * length depends on the type of address being represented.
 * 
 * The APPARENT ADDR information element MUST be sent with IAX TXREQ and
 * REGACK messages.  When used with a TXREQ message, the APPARENT ADDR
 * MUST specify the address of the peer to which the local peer is
 * trying to transfer its end of the connection.  When used with a
 * REGACK message, the APPARENT ADDR MUST specify the address it uses to
 * reach the peer (which may be different than the address the peer
 * perceives itself as in the case of NAT or multi-homed peer machines).
 * 
 * The data field of the APPARENT ADDR information element is the same
 * as the Linux struct sockaddr_in: two octets for the address family,
 * two octets for the port number, four octets for the IPv4 address, and
 * 8 octets of padding consisting of all bits set to 0.  Thus, the total
 * length of the APPARENT ADDR information element is 18 octets.
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x12     |  Data Length  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |        sockaddr struct        |
 * :   for address family in use   :
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 * The following diagram demonstrates the APPARENT ADDR format for an
 * IPv4 address:
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x12     |      0x10     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |            0x0200             | <- Address family (INET)
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |            0x11d9             | <- Portno (default 4569)
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      32-bit IP address        |
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                               |
 * |      8 octets of all 0s       |
 * |   (padding in sockaddr_in)    |
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * The following diagram demonstrates the APPARENT ADDR format for an
 * IPv6 address:
 * 
 *                      1
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      0x12     |      0x1C     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |            0x0A00             | <- Address family (INET6)
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |            0x11d9             | <- Portno (default 4569)
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |           32 bits             | <- Flow information
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      128-bit IP address       | <- Ip6 Address
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |           32 bits             | <- Scope ID
 * |                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class ApparentAddr extends InformationElement
{

	public static final byte TYPE = 0x12;

	private InetAddress apparentAddr;

	private int port;


	public ApparentAddr(Inet4Address apparentAddr, short port)
	{
		super();

		ByteBuffer buffer = new ByteBuffer(18);
		buffer.put16bits(0x0200);
		buffer.put16bits(port);
		buffer.putByteArray(apparentAddr.getAddress());

		// add 8 bytes of padding (all 0)
		for (int i = 0; i < 8; i++)
		{
			buffer.put8bits((byte) 0);
		}

		this.apparentAddr = apparentAddr;
		this.port = port;
		this.data = buffer.getBuffer();
		this.dataLength = (byte) data.length;
	}

	//TODO Integrate constructor for IPv6

	public ApparentAddr(byte[] data) throws InvalidArgumentException
	{
		super(data);

		ByteBuffer buffer = new ByteBuffer(this.data);
		int sinFamily = buffer.get16bits();

		switch (sinFamily)
		{
			// IPv4
			case 0x0200:
			{
				this.port = buffer.get16bits();

				byte[] tmpIp = new byte[4];
				for (int i = 0; i < 4; i++)
				{
					tmpIp[i] = (byte) buffer.get8bits();
				}

				try
				{
					this.apparentAddr = Inet4Address.getByAddress(tmpIp);
				}
				catch (UnknownHostException e)
				{
					throw new InvalidArgumentException(e);
				}

				break;
			}
			// IPv6
			case 0x0A00:
			{
				throw new InvalidArgumentException("IPv6 not supported yet!");
			}
			default:
			{
				throw new InvalidArgumentException("Unsupported SIN family: " + sinFamily);
			}
		}
	}


	@Override
	public InformationElementType getType()
	{
		return InformationElementType.APPARENT_ADDR;
	}


	public InetAddress getApparentAddr()
	{
		return apparentAddr;
	}


	public int getPort()
	{
		return port;
	}


	@Override
	public String toString()
	{
		return String.format("ApparentAddr(ip=%s, port=%d)", apparentAddr, port);
	}

}