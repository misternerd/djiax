package com.misternerd.djiax.call;

import com.misternerd.djiax.util.MediaFormat;

/**
 * An interface to send audio to a call and receive audio data from there. A
 * call can send data to multiple objects and receive data from all of them.
 * This can only happen while the call is in up state.
 */
public interface AudioListener
{

	/**
	 * The call uses this function to set the audio to running or not running.
	 * This enables the listener to switch audio on or off.
	 */
	public void callListenerSetAudioRunning(boolean running);


	/**
	 * This gets called when there is new data available on the call. The media
	 * format gets returned as well.
	 */
	public void callListenerReceivedAudioData(byte[] data, MediaFormat codec);

}