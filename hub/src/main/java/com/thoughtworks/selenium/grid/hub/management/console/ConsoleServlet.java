package com.thoughtworks.selenium.grid.hub.management.console;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.EnvironmentManager;
import com.thoughtworks.selenium.grid.hub.management.box.BoxPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;

/**
 * Gateway to Selenium Farm.
 *
 * @author Philippe Hanrigou
 */
public class ConsoleServlet extends HttpServlet {

    private static final Log logger = LogFactory.getLog(ConsoleServlet.class);
	private final BoxPool pool;
	private final DynamicRemoteControlPool remoteControlPool;
	private final EnvironmentManager environmentManager;

    public ConsoleServlet(EnvironmentManager environmentManager, DynamicRemoteControlPool remoteControlPool, BoxPool pool) {
		this.environmentManager = environmentManager;
		this.remoteControlPool = remoteControlPool;
		this.pool = pool;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("Rendering console...");
        controller().process(response);
    }

    protected ConsoleController controller() {
        return new ConsoleController(environmentManager, remoteControlPool, pool);
    }
}