package com.misternerd.djiax;

import java.net.InetAddress;

import com.misternerd.djiax.io.frame.ie.ApparentAddr;
import com.misternerd.djiax.io.frame.ie.Datetime;
import com.misternerd.djiax.io.frame.ie.Refresh;

public class PeerConfiguration
{

	public final String peerName;
	
	public final String username;

	public final String password;

	public final InetAddress serverAddress;

	public final int serverPort;
	
	public final int maxNumberOfCalls;

	private long regRelTimestamp;

	private Short serverSourceCallNumber;

	private Datetime serverDatetime;

	private int serverRefresh;

	private ApparentAddr serverApparentAddr;


	public PeerConfiguration(String peerName, String username, String password, 
			InetAddress serverAddress, int serverPort, int maxNumberOfCalls)
	{
		this.peerName = peerName;
		this.username = username;
		this.password = password;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.maxNumberOfCalls = maxNumberOfCalls;
		this.serverRefresh = PeerConstants.PEER_REGISTRATION_REFRESH;
	}


	public long getRegRelTimestamp()
	{
		return regRelTimestamp;
	}


	public void setRegRelTimestamp(long regRelTimestamp)
	{
		this.regRelTimestamp = regRelTimestamp;
	}


	public Short getServerSourceCallNumber()
	{
		return serverSourceCallNumber;
	}


	public void setServerSourceCallNumber(Short serverSourceCallNumber)
	{
		this.serverSourceCallNumber = serverSourceCallNumber;
	}


	public Datetime getServerDatetime()
	{
		return serverDatetime;
	}


	public void setServerDatetime(Datetime serverDatetime)
	{
		this.serverDatetime = serverDatetime;
	}


	public int getServerRefresh()
	{
		return serverRefresh;
	}


	public void setServerRefresh(int serverRefresh)
	{
		this.serverRefresh = serverRefresh;
	}


	public ApparentAddr getServerApparentAddr()
	{
		return serverApparentAddr;
	}


	public void setServerApparentAddr(ApparentAddr serverApparentAddr)
	{
		this.serverApparentAddr = serverApparentAddr;
	}

}
