package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.Hub;
import com.thoughtworks.selenium.grid.hub.management.RegistrationManagementServlet;

/**
 * Starts the Remote Controls from a given Box.
 * 
 * Request parameters:
 * <ul>
 * 	<li><b>host:</b> Box Host 
 * 	<li><b>port:</b> Box port 
 * 	<li><b>hubURL:</b> URL where HubServer is running 
 * 	<li><b>quantity:</b> Number of Remote Controls to be started from this box 
 * 	<li><b>portStart:</b> First port to register Remote controls. Registered Remote Controls ports
 * 	will be at range [portStart, ... , portStart + quantity]</li>
 *  <li><b>environment:</b> Environment to use in all Remote controls
 * </ul>
 * 
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 *
 */
public class BoxStartupServlet extends RegistrationManagementServlet {

    private static final Log LOGGER = LogFactory.getLog(BoxStartupServlet.class);
	private final BoxPool pool;
	private final File boxFile;

    public BoxStartupServlet(BoxPool pool, File boxFile) {
		this.pool = pool;
		this.boxFile = boxFile;
	}

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {

        LOGGER.info("Starting new remote controls...");
        Box box = new Box(request.getParameter("host"), Integer.parseInt(request.getParameter("port")));
        box = pool.load(box);
        String hubURL = request.getParameter("hubURL");
        int n = Integer.parseInt(request.getParameter("quantity"));
		int portStart = Integer.parseInt(request.getParameter("portStart"));
		// the hub object should be created on server startup reading a config information
		boolean started = box.startRemoteControls(n, request.getParameter("environment"), portStart, new Hub(hubURL));
		pool.saveTo(boxFile);
		if (started) {
	        LOGGER.info("Started " + n + " remote controls @ " + box);
	        writeSuccessfulResponse(response);
		} else {
			throw new IllegalStateException("Unable to start Remotes @ " + box);
		}
    }


}
