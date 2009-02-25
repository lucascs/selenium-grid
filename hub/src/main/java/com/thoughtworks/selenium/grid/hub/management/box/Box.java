package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.HttpClient;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.hub.Hub;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;
/**
 * Holds the information of a Box Agent Server. Registers and Unregisters Remote Controls.
 * A Box Agent Server must be running in order to register Remote controls.
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
	private transient HttpClient client;
	
	public static enum Status {
		ONLINE, OFFLINE
	}
	public Box(String host, int port) {
		this(host, port, new HttpClient());
	}

	Box(String host, int port, HttpClient client) {
		this.host = host;
		this.port = port;
		this.defaultStartPort = 5555;
		this.defaultQuantity = 1;
		this.defaultEnvironment = "*firefox";
		this.status = Status.OFFLINE;
		this.client = client;
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

	Status status() {
		return this.status;
	}
	void setDefaultHubUrl(String url) {
		this.defaultHubUrl = url;
	}
	public void setUp() {
		if (!Status.ONLINE.equals(this.status) && defaultHubUrl != null) {
			startRemoteControls(defaultQuantity, defaultEnvironment, defaultStartPort, new Hub(defaultHubUrl));
		}
		this.status = Status.ONLINE;
	}
	public void setDown(DynamicRemoteControlPool pool) {
		killRemotes(pool);
		this.status = Status.OFFLINE;
	}
	
	private void killRemotes(DynamicRemoteControlPool pool) {
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
		this.defaultStartPort = portStart;
		this.defaultQuantity = n;
		this.defaultEnvironment = environment;
		this.defaultHubUrl = hub.url();
		try {
			String url = String.format("http://%s:%d/remote-control-manager/start?host=%s&quantity=%d&environment=%s&" +
					"hubURL=%s&portStart=%d",
					host, port, host, n, environment, hub.url(), portStart);
			Response response = client().get(url);
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
	
	List<Integer> getRcPorts() {
		if (rcPorts == null) {
			rcPorts = new ArrayList<Integer>();
		}
		return rcPorts;
	}
	
	HttpClient client() {
		if (client == null) {
			client = new HttpClient();
		}
		return client;
	}

	void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Box other = (Box) obj;
		if (host == null) {
			if (other.host != null) {
				return false;
			}
		} else if (!host.equals(other.host)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		return true;
	}

}
