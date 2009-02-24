package com.thoughtworks.selenium.grid.hub.management.console;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.selenium.grid.hub.EnvironmentManager;
import com.thoughtworks.selenium.grid.hub.management.box.BoxPool;
import com.thoughtworks.selenium.grid.hub.management.console.mvc.Controller;
import com.thoughtworks.selenium.grid.hub.management.console.mvc.Page;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;


public class ConsoleController extends Controller {

    private final BoxPool pool;
	private final EnvironmentManager environmentManager;
	private final DynamicRemoteControlPool remoteControlPool;

	public ConsoleController(EnvironmentManager environmentManager, DynamicRemoteControlPool remoteControlPool, BoxPool pool) {
		this.environmentManager = environmentManager;
		this.remoteControlPool = remoteControlPool;
		this.pool = pool;
    }

    public void process(HttpServletResponse response) throws ServletException, IOException {
        final Page page = list();
        render(page, response);
    }

    public Page list() {
        final Page page;

        page = new Page("index.html");
        page.set("environments", environmentManager.environments());
        page.set("availableRemoteControls", remoteControlPool.availableRemoteControls());
        page.set("reservedRemoteControls", remoteControlPool.reservedRemoteControls());
        page.set("boxes", pool.getBoxes());
        return page;
    }

}
