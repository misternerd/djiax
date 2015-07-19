djiax
=====

djiax is an IAX2 (Inter-Asterisk eXchange Version 2, RFC 5456) client library written in Java. It currently only implements the parts necessary for the client peer, which means incoming calls are not supported.
In contrast to other IAX2 libraries, it focuses on servers that need to send and receive raw audio data directly (e.g. to/from PSTN), tangle with them (overlay, re-encode etc) and send them on to a client that does the actual presentation to the user.

Usage
-----

	// First, we need a peer. This is a client peer, in my setup it always connects to an Asterisk server.
	// The observer gets called when the connection changes, e.g. connect/disconnect/transmit errors
	IaxPeer iaxPeer = IaxPeerFactory.createNewPeer(iaxServerHostname, iaxServerPort, iaxUsername, iaxServerPassword, peerObserver);
	
	// The call needs a dialstring (anything that Asterisk can match in the context you provided for the peer)
	// I always choose uncompressed audio data as below, since this is easiest to handle. But if you connect to a remote Asterisk, 
	// you might want to use compression.
	Call call = iaxPeer.createCall(dialString, new MediaFormat[] { MediaFormat.LE_16_BIT_LINEAR })
	
	// the call observer gets called for stuff like call connected/busy/hangup
	call.setCallObserver(new CallObserver(){...});
	
	// the audio listener receives incoming voice data as it arrives
	call.setAudioListener(new AudioListener(){...});
	
	// this needs to be done after call setup (listeners etc) are complete
	call.callStart();
	
	// sending audio data as raw bytes, the format must match, though.
	// Watch out for correct endianess!
	call.writeAudioData(new byte[]{ 0x50});

	// stop the call, e.g. hangup 
	call.callStop();

About
-----

A couple years back, I worked on a project where I needed to hook up a Java server to the PTSN somehow and overlay the audio data with small fragments on my own. After that, the audio data from the PSTN should be Speex encoded and sent over WebSockets to a client.
The easiest solution at the time seemed the IAX2 protocol, which only makes use of a single UDP port for transmitting call control messages as well as audio data. Since all Java-based IAX2 libraries I found at the time were based on the assumption that you wanted to record audio data from a mic and play it through the speaker, I decided to write my own based on the RFC. I only needed outgoing calls and always connected to the same Asterisk server on the same box as the Java machine. So I choose uncompressed (RAW) audio data as my preferred codec. The system has been working well since 2011, but there still are some bugs in there that haven't been worth fixing.
