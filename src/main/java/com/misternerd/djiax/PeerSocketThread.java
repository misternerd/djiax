package com.misternerd.djiax;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.misternerd.djiax.exception.InvalidArgumentException;
import com.misternerd.djiax.io.frame.FrameBase;
import com.misternerd.djiax.io.frame.FullFrame;
import com.misternerd.djiax.io.frame.MiniFrame;
import com.misternerd.djiax.io.frame.full.ComfortNoiseFrame;
import com.misternerd.djiax.io.frame.full.ControlFrame;
import com.misternerd.djiax.io.frame.full.DtmfFrame;
import com.misternerd.djiax.io.frame.full.HtmlFrame;
import com.misternerd.djiax.io.frame.full.IaxFrame;
import com.misternerd.djiax.io.frame.full.ImageFrame;
import com.misternerd.djiax.io.frame.full.TextFrame;
import com.misternerd.djiax.io.frame.full.VideoFrame;
import com.misternerd.djiax.io.frame.full.VoiceFrame;

class PeerSocketThread
{

	private static final Logger logger = LoggerFactory.getLogger(PeerSocketThread.class);

	private IaxPeer peer;

	private PeerConfiguration peerConfiguration;

	private boolean threadRunning;

	private DatagramChannel udpChannel;

	private ExecutorService executorService;


	public PeerSocketThread(IaxPeer peer, PeerConfiguration peerConfiguration) throws IOException
	{
		this.peer = peer;
		this.peerConfiguration = peerConfiguration;
		this.threadRunning = true;
		this.executorService = Executors.newFixedThreadPool(peerConfiguration.maxNumberOfCalls / 2);
		this.udpChannel = DatagramChannel.open();
		udpChannel.configureBlocking(false);
	}


	public void stopThread()
	{
		this.threadRunning = false;
		executorService.shutdown();
	}


	public void start()
	{
		for (int i = 0; i < peerConfiguration.maxNumberOfCalls; i++)
		{
			Runnable worker = new WorkerThread(i);
			executorService.execute(worker);
		}
	}


	public void sendFrame(FrameBase frame) throws IOException
	{
		final byte[] frameData = frame.serialize();
		ByteBuffer buffer = ByteBuffer.allocate(frameData.length);
		buffer.clear();
		buffer.put(frameData);
		buffer.flip();

		udpChannel.send(buffer, new InetSocketAddress(peerConfiguration.serverAddress, peerConfiguration.serverPort));
	}


	private class WorkerThread implements Runnable
	{

		private ByteBuffer buffer = ByteBuffer.allocate(10240);

		private int receivedBytes;

		private byte[] receivedData;

		private short subclass;

		private FullFrame fullFrame;

		private MiniFrame miniFrame;

		private int id;


		public WorkerThread(int id)
		{
			this.id = id;
		}


		@Override
		public void run()
		{
			Thread.currentThread().setName(String.format("Peer-%s-receiver-%d", peer.getPeerName(), id));

			long sleepTime = 5;
			boolean hadWorkToDo;

			while (threadRunning)
			{
				hadWorkToDo = false;

				try
				{
					buffer.clear();

					if (udpChannel.receive(buffer) != null)
					{
						receivedBytes = buffer.position();
						receivedData = buffer.array();

						handleReceivedData();
						hadWorkToDo = true;
					}

					if (hadWorkToDo)
					{
						sleepTime--;
					}
					else
					{
						sleepTime++;
					}

					if (sleepTime > 0)
					{
						Thread.sleep(5);
					}
				}
				catch (ClosedChannelException e)
				{
					logger.error("Received ClosedChannelException, exiting");
				}
				catch (IOException | InvalidArgumentException | InterruptedException e)
				{
					logger.warn("Caught exception in worker thread:", e);
				}
			}

			logger.debug("Worker Thread exiting");
		}


		private void handleReceivedData() throws InvalidArgumentException
		{
			// MetaFrame (first 16 bits will always be zero)
			if (receivedData[0] == 0x0 && receivedData[1] == 0x0)
			{
				// TODO Implement MetaFrames
				logger.warn("MetaFrames are not supported yet!");
				return;
			}
			else if ((receivedData[0] & 0x80) != 0)
			{
				handleFullFrame();
			}
			else
			{
				handleMiniFrame();
			}
		}


		protected void handleFullFrame() throws InvalidArgumentException
		{
			if (receivedData.length < 12)
			{
				logger.warn("Received FullFrame with only {} bytes, expected 12 bytes minimum, discarding", receivedData.length);
				return;
			}

			subclass = (short) (receivedData[10]);

			switch (subclass)
			{
				case ComfortNoiseFrame.TYPE:
				{
					fullFrame = new ComfortNoiseFrame(receivedData, receivedBytes);
					break;
				}
				case ControlFrame.TYPE:
				{
					fullFrame = new ControlFrame(receivedData, receivedBytes);
					break;
				}
				case DtmfFrame.TYPE:
				{
					fullFrame = new DtmfFrame(receivedData, receivedBytes);
					break;
				}
				case HtmlFrame.TYPE:
				{
					fullFrame = new HtmlFrame(receivedData, receivedBytes);
					break;
				}
				case IaxFrame.TYPE:
				{
					fullFrame = new IaxFrame(receivedData, receivedBytes);

					break;
				}
				case ImageFrame.TYPE:
				{
					fullFrame = new ImageFrame(receivedData, receivedBytes);
					break;
				}
				case TextFrame.TYPE:
				{
					fullFrame = new TextFrame(receivedData, receivedBytes);
					break;
				}
				case VideoFrame.TYPE:
				{
					fullFrame = new VideoFrame(receivedData, receivedBytes);
					break;
				}
				case VoiceFrame.TYPE:
				{
					fullFrame = new VoiceFrame(receivedData, receivedBytes);
					break;
				}
				default:
				{
					logger.warn("Invalid FullFrame type " + subclass + ", discarding");
					return;
				}
			}

			peer.handleIncomingFullFrame(fullFrame);
		}


		protected void handleMiniFrame() throws InvalidArgumentException
		{
			miniFrame = new MiniFrame(receivedData, receivedBytes);
			peer.handleIncomingMiniFrame(miniFrame);
		}
	}

}