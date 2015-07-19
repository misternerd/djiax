package com.misternerd.djiax;

/**
 * This class only contains constants used throughout the programm.
 */
public final class PeerConstants
{

	/**
	 * The maximum source call number to be used by this peer.
	 */
	public static final short PEER_MAX_SOURCE_CALL_NUMBER = 1000;

	/**
	 * We'll try to use this period (seconds) until out registration times out.
	 */
	public static final short PEER_REGISTRATION_REFRESH = 60;

	/**
	 * How often this do we retry our credentials when we got a REGREJ?
	 * <em>-1</em> is indefinitely, <em>0</em> turns retrying of.
	 */
	public static final int REGISTRATION_REJECTED_NUMBER_OF_RETRIES = 1;

	/**
	 * Number of seconds to wait between rejected credentials & retry.
	 */
	public static final int REGISTRATION_REJECTED_RETRY_WAIT = 10;

	/**
	 * The maximum number of msecs that we'll retry to send frames.
	 */
	public static final long TRANSMISSION_RETRY_MAX_MSECS = 10000;

	/**
	 * Maximum number of times a frame should be resent.
	 */
	public static final int TRANSMISSION_MAX_RETRIES = 4;
	
	public static final long FRAME_RETRANSMIT_TIMEOUT_IN_MSECS = 1000;
	
	public static final long CALL_THREAD_SLEEP_TIME = 5;
	
	public static final long CALL_TIME_BETWEEN_RETRANSMITS = 1000;
	
	public static final long CALL_TIME_BETWEEN_PINGS = 20000;

}
