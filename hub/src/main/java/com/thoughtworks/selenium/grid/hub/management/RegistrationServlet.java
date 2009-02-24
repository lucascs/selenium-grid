package com.thoughtworks.selenium.grid.hub.management;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

/**
 * Servlet used by Selenium Remote Control to register themselves to the grid.
 *
 * @author Philippe Hanrigou
 */
public class RegistrationServlet extends RegistrationManagementServlet {

    private static final Log LOGGER = LogFactory.getLog(RegistrationServlet.class);
	private final DynamicRemoteControlPool pool;

    public RegistrationServlet(DynamicRemoteControlPool pool) {
		this.pool = pool;
	}
    
    @Override
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("Registering new remote control...");
        final RemoteControlProxy newRemoteControl = RemoteControlParser.parse(request);
        pool.register(newRemoteControl);
        LOGGER.info("Registered " + newRemoteControl);
        writeSuccessfulResponse(response);
    }


}
