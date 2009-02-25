package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.management.RegistrationManagementServlet;

/**
 * Unregisters a box from the BoxPool
 * 
 * Request parameters:
 * <ul>
 * 	<li><b>host:</b> Box Host</li> 
 * 	<li><b>port:</b> Box port</li> 
 * </ul>
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 *
 */
public class BoxUnregistrationServlet extends RegistrationManagementServlet {

    private static final Log LOGGER = LogFactory.getLog(BoxUnregistrationServlet.class);
	private final BoxPool pool;
	private final File boxFile;

    public BoxUnregistrationServlet(BoxPool pool, File boxFile) {
		this.pool = pool;
		this.boxFile = boxFile;
	}

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {

        LOGGER.info("Registering new Box...");
        Box box = new Box(request.getParameter("host"), Integer.parseInt(request.getParameter("port")));
        pool.unregister(box);
        pool.saveTo(boxFile);
        LOGGER.info("Registered " + box);
        writeSuccessfulResponse(response);
    }


}
