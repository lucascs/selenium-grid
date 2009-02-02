package com.thoughtworks.selenium.grid.hub.management.console;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.selenium.grid.hub.HubRegistry;
import com.thoughtworks.selenium.grid.hub.management.box.BoxPool;
import com.thoughtworks.selenium.grid.hub.management.console.mvc.Controller;
import com.thoughtworks.selenium.grid.hub.management.console.mvc.Page;


public class ConsoleController extends Controller {

    private final BoxPool pool;

	public ConsoleController(HubRegistry registry, BoxPool pool) {
        super(registry);
		this.pool = pool;
    }

    public void process(HttpServletResponse response) throws ServletException, IOException {
        final Page page = list();
        render(page, response);
    }

    public Page list() {
        final Page page;

        page = new Page("index.html");
        page.set("environments", registry().environmentManager().environments());
        page.set("availableRemoteControls", registry().remoteControlPool().availableRemoteControls());
        page.set("reservedRemoteControls", registry().remoteControlPool().reservedRemoteControls());
        page.set("boxes", pool.getBoxes());
        return page;
    }

}
