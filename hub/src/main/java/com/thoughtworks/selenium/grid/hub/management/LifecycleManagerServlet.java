package com.thoughtworks.selenium.grid.hub.management;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet in charge of shutting down the Hub.
 */
public class LifecycleManagerServlet extends HubServlet {

    private static final Log LOGGER = LogFactory.getLog(LifecycleManagerServlet.class);
	private final LifecycleManager lifecycleManager;

    public LifecycleManagerServlet(LifecycleManager manager) {
		this.lifecycleManager = manager;
	}
    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String action;

        action = request.getParameter("action");
        LOGGER.info("Requesting life cycle manager action : '" + action + "'");
        if ("shutdown".equals(action)) {
            lifecycleManager.shutdown();
        }
    }

}
