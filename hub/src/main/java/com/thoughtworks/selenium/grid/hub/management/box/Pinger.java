package com.thoughtworks.selenium.grid.hub.management.box;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.HttpClient;
import com.thoughtworks.selenium.grid.Response;

public class Pinger implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(Pinger.class);
	private static final int SECONDS = 1000;
	private final BoxPool pool;

	public Pinger(BoxPool pool) {
		this.pool = pool;
	}

	public void run() {
		while(true) {
			for (Box box : pool.getBoxes()) {
				try {
					Response response = new HttpClient().get("http://" + box.host() + ":" + box.port() + "/ping");
					if (response.statusCode() == 200) {
						box.setUp();
					} else {
						box.setDown();
					}
				} catch (Exception e) {
					// should live forever
					LOGGER.warn(e);
				}
			}
			try {
				Thread.sleep(60*SECONDS);
			} catch (InterruptedException e) {
				LOGGER.warn(e);
			}
		}
	}

}
