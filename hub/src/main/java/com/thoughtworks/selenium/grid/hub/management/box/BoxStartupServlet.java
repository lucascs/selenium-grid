package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.Hub;
import com.thoughtworks.selenium.grid.hub.management.RegistrationManagementServlet;

/**
 * 
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 *
 */
public class BoxStartupServlet extends RegistrationManagementServlet {

    private static final Log LOGGER = LogFactory.getLog(BoxStartupServlet.class);
	private final BoxPool pool;

    public BoxStartupServlet(BoxPool pool) {
		this.pool = pool;
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
		if (started) {
	        LOGGER.info("Started " + n + " remote controls @ " + box);
	        writeSuccessfulResponse(response);
		} else {
			throw new IllegalStateException("Unable to start Remotes @ " + box);
		}
    }


}
