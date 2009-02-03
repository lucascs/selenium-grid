package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.HttpClient;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.hub.HubRegistry;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

/**
 * 
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 *
 */
public class Pinger implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(Pinger.class);
	private static final int SECONDS = 1000;
	private final BoxPool pool;

	public Pinger(BoxPool pool) {
		this.pool = pool;
	}

	public void run() {
		while(true) {
			pingBoxes();
			pingRemotes();
			try {
				Thread.sleep(60*SECONDS);
			} catch (InterruptedException e) {
				LOGGER.warn(e);
			}
		}
	}

	private void pingRemotes() {
		DynamicRemoteControlPool rcPool = HubRegistry.registry().remoteControlPool();
		ArrayList<RemoteControlProxy> rcs = new ArrayList<RemoteControlProxy>();
		rcs.addAll(rcPool.availableRemoteControls());
		rcs.addAll(rcPool.reservedRemoteControls());
		for (RemoteControlProxy proxy : rcs) {
			try {
				new HttpClient().get("http://" + proxy.host() + ":" + proxy.port());
			} catch (IOException e) {
				LOGGER.warn(proxy + " is down", e);
				rcPool.unregister(proxy);
			}
		}
	}
	
	private void pingBoxes() {
		for (Box box : pool.getBoxes()) {
			try {
				Response response = new HttpClient().get("http://" + box.host() + ":" + box.port() + "/ping");
				if (response.statusCode() == 200) {
					box.setUp();
					continue;
				}
			} catch (Exception e) {
				// should live forever
				LOGGER.warn(e);
			}
			box.setDown();
		}
	}

}
