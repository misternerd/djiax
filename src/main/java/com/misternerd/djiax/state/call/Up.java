package com.misternerd.djiax.state.call;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.Call;
import com.misternerd.djiax.exception.InvalidMediaFormatException;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.full.VoiceFrame;
import com.misternerd.djiax.state.AbstractCallState;
import com.misternerd.djiax.util.MediaFormat;

/**
 * In this state the call is completely set up and voice frames are flowing from
 * end to end. This state will only be left if we receive a hangup or if we want
 * to end the call.
 */
public class Up extends AbstractCallState
{

	private static final Logger logger = LoggerFactory.getLogger(Up.class);

	public Up(Call call)
	{
		super(call);
	}


	@Override
	public void receiveFrame(FullFrame frame)
	{
		if (frame instanceof VoiceFrame)
		{
			VoiceFrame voiceFrame = (VoiceFrame) frame;

			sendAckForFullFrame(frame);

			try
			{
				call.setCodec(voiceFrame.getFormat());
			}
			catch (InvalidMediaFormatException e)
			{
				logger.warn("Failed setting codec, invalid media format:", e);
			}

			byte[] data = voiceFrame.getData();
			if (data != null)
			{
				MediaFormat format = voiceFrame.getFormat();
				call.getAudioListener().callListenerReceivedAudioData(data, format);
			}

			return;
		}

		super.receiveFrame(frame);
	}

}