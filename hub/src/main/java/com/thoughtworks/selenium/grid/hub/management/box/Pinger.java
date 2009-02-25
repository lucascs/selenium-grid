package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.HttpClient;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

/**
 * Will ping all RemoteControls from remoteControlPool and all Box from boxPool, every minute.
 * If a Box is not reachable, its status is set OFFLINE and all its remote controls will be
 * unregistered from the remoteControlPool. When he gets reachable again, its remote controls will be
 * registered.
 * If a Remote Control is not reachable, it will be unregistered from the remoteControlPool.
 * 
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 *
 */
public class Pinger implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(Pinger.class);
	private static final int SECONDS = 1000;
	private final BoxPool boxPool;
	private final DynamicRemoteControlPool remoteControlPool;

	public Pinger(DynamicRemoteControlPool remoteControlPool, BoxPool boxPool) {
		this.remoteControlPool = remoteControlPool;
		this.boxPool = boxPool;
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
		ArrayList<RemoteControlProxy> rcs = new ArrayList<RemoteControlProxy>();
		rcs.addAll(remoteControlPool.availableRemoteControls());
		rcs.addAll(remoteControlPool.reservedRemoteControls());
		for (RemoteControlProxy proxy : rcs) {
			try {
				new HttpClient().get("http://" + proxy.host() + ":" + proxy.port());
			} catch (IOException e) {
				LOGGER.warn(proxy + " is down", e);
				remoteControlPool.unregister(proxy);
			}
		}
	}
	
	private void pingBoxes() {
		for (Box box : boxPool.getBoxes()) {
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
			box.setDown(remoteControlPool);
		}
	}

}
