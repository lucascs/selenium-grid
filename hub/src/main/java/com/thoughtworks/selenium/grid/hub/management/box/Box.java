package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.HttpClient;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.hub.Hub;
import com.thoughtworks.selenium.grid.hub.HubRegistry;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

public class Box {

	private static final Log LOGGER = LogFactory.getLog(Box.class);
	private final String host;
	private final int port;
	private int defaultStartPort;
	private int defaultQuantity;
	private Status status;
	
	public static enum Status {
		ONLINE, OFFLINE
	}
	public Box(String host, int port) {
		this.host = host;
		this.port = port;
		this.defaultStartPort = 5555;
		this.defaultQuantity = 1;
		this.status = Status.ONLINE;
	}

	public int quantity() {
		return defaultQuantity;
	}
	public int startPort() {
		return defaultStartPort;
	}
	public String host() {
		return host;
	}
	
	public int port() {
		return port;
	}

	public void setUp() {
		this.status = Status.ONLINE;
	}
	public void setDown() {
		killRemotes();
		this.status = Status.OFFLINE;
	}
	
	private void killRemotes() {
		DynamicRemoteControlPool pool = HubRegistry.registry().remoteControlPool();
		List<RemoteControlProxy> downRCs = new ArrayList<RemoteControlProxy>();
		for(RemoteControlProxy proxy : pool.availableRemoteControls()) {
			if (proxy.host().equals(host()) && proxy.port() == port()) {
				downRCs.add(proxy);
			}
		}
		for(RemoteControlProxy proxy : pool.reservedRemoteControls()) {
			if (proxy.host().equals(host()) && proxy.port() == port()) {
				downRCs.add(proxy);
			}
		}
		for (RemoteControlProxy proxy : downRCs) {
			pool.unregister(proxy);
		}
		
	}

	public boolean up() {
		return this.status == Status.ONLINE;
	}
	
	public boolean startRemoteControls(int n, String environment, int portStart, Hub hub) {
		HttpClient client = new HttpClient();
		this.defaultStartPort = portStart;
		this.defaultQuantity = n;
		try {
			String url = String.format("http://%s:%d/remote-control-manager/start?host=%s&quantity=%d&environment=%s&" +
					"hubURL=%s&portStart=%d",
					host, port, host, n, environment, hub.url(), portStart);
			Response response = client.get(url);
			return response.statusCode() == 200; 
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
	}
	
}
