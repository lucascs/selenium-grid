package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.management.RegistrationManagementServlet;

/**
 * 
 * @author Lucas Cavalcanti
 * @author Guilherme Silveira
 *
 */
public class BoxRegistrationServlet extends RegistrationManagementServlet {

    private static final Log LOGGER = LogFactory.getLog(BoxRegistrationServlet.class);
	private final BoxPool pool;
	private final File boxFile;

    public BoxRegistrationServlet(BoxPool pool, File boxFile) {
		this.pool = pool;
		this.boxFile = boxFile;
	}

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {

        LOGGER.info("Registering new Box...");
        Box box = new Box(request.getParameter("host"), Integer.parseInt(request.getParameter("port")));
        pool.register(box);
        pool.saveTo(boxFile);
        LOGGER.info("Registered " + box);
        writeSuccessfulResponse(response);
    }


}
