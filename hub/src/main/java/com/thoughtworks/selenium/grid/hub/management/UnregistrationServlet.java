package com.thoughtworks.selenium.grid.hub.management;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

/**
 * Servlet used by Selenium Remote Controls to unregister themselves to the Hub.
 */
public class UnregistrationServlet extends RegistrationManagementServlet {

    private static final Log logger = LogFactory.getLog(UnregistrationServlet.class);
	private final DynamicRemoteControlPool pool;

    public UnregistrationServlet(DynamicRemoteControlPool pool) {
		this.pool = pool;
	}
    @Override
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Unregistering remote control...");
        final RemoteControlProxy newRemoteControl = RemoteControlParser.parse(request);
        pool.unregister(newRemoteControl);
        logger.info("Unregistered " + newRemoteControl);
        writeSuccessfulResponse(response);
    }

}
