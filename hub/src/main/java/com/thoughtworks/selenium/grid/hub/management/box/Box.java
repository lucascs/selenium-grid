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
/**
 * 
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 *
 */
public class Box {

	private static final Log LOGGER = LogFactory.getLog(Box.class);
	private final String host;
	private final int port;
	private int defaultStartPort;
	private int defaultQuantity;
	private transient Status status;
	private transient List<Integer> rcPorts = new ArrayList<Integer>();
	private String defaultEnvironment;
	private String defaultHubUrl;
	
	public static enum Status {
		ONLINE, OFFLINE
	}
	public Box(String host, int port) {
		this.host = host;
		this.port = port;
		this.defaultStartPort = 5555;
		this.defaultQuantity = 1;
		this.defaultEnvironment = "*firefox";
		this.status = Status.ONLINE;
	}

	public int quantity() {
		return defaultQuantity;
	}
	
	public String environment() {
		if (defaultEnvironment == null) {
			defaultEnvironment = "*firefox";
		}
		return defaultEnvironment;
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
		if (!Status.ONLINE.equals(this.status) && defaultHubUrl != null) {
			startRemoteControls(defaultQuantity, defaultEnvironment, defaultStartPort, new Hub(defaultHubUrl));
		}
		this.status = Status.ONLINE;
	}
	public void setDown() {
		killRemotes();
		this.status = Status.OFFLINE;
	}
	
	private void killRemotes() {
		DynamicRemoteControlPool pool = HubRegistry.registry().remoteControlPool();
		List<RemoteControlProxy> rcs = new ArrayList<RemoteControlProxy>();
		rcs.addAll(pool.availableRemoteControls());
		rcs.addAll(pool.reservedRemoteControls());
		
		for(RemoteControlProxy proxy : rcs) {
			for (Integer port : getRcPorts()) {
				if (proxy.host().equals(host()) && proxy.port() == port) {
					pool.unregister(proxy);
					continue;
				}
			}
		}
		getRcPorts().clear();
	}

	public boolean up() {
		return this.status == Status.ONLINE;
	}
	
	public boolean startRemoteControls(int n, String environment, int portStart, Hub hub) {
		HttpClient client = new HttpClient();
		this.defaultStartPort = portStart;
		this.defaultQuantity = n;
		this.defaultEnvironment = environment;
		this.defaultHubUrl = hub.url();
		try {
			String url = String.format("http://%s:%d/remote-control-manager/start?host=%s&quantity=%d&environment=%s&" +
					"hubURL=%s&portStart=%d",
					host, port, host, n, environment, hub.url(), portStart);
			Response response = client.get(url);
			boolean successful = response.statusCode() == 200;
			if (successful) {
				for (int i = 0; i < n; i++) {
					getRcPorts().add(portStart + i);
				}
			}
			return successful; 
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
	}
	
	private List<Integer> getRcPorts() {
		if (rcPorts == null) {
			rcPorts = new ArrayList<Integer>();
		}
		return rcPorts;
	}

}
