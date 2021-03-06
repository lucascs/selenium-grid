package com.thoughtworks.selenium.grid.hub;

import java.io.File;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.thoughtworks.selenium.grid.configuration.HubConfiguration;
import com.thoughtworks.selenium.grid.hub.management.LifecycleManagerServlet;
import com.thoughtworks.selenium.grid.hub.management.RegistrationServlet;
import com.thoughtworks.selenium.grid.hub.management.UnregistrationServlet;
import com.thoughtworks.selenium.grid.hub.management.box.BoxPool;
import com.thoughtworks.selenium.grid.hub.management.box.BoxRegistrationServlet;
import com.thoughtworks.selenium.grid.hub.management.box.BoxStartupServlet;
import com.thoughtworks.selenium.grid.hub.management.box.BoxUnregistrationServlet;
import com.thoughtworks.selenium.grid.hub.management.box.Pinger;
import com.thoughtworks.selenium.grid.hub.management.console.ConsoleServlet;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;

/**
 * Self contained Selenium Grid Hub. Uses Jetty to as a standalone web application.
 */
public class HubServer {

    public static void main(String[] args) throws Exception {
        final HubRegistry registry = new HubRegistry();
        final HubConfiguration configuration = registry.gridConfiguration().getHub();
        final DynamicRemoteControlPool remoteControlPool = registry.remoteControlPool();
		final EnvironmentManager environmentManager = registry.environmentManager();
		final File boxFile = registry.boxFile();
		final BoxPool pool = registry.boxPool();
		
		final Server server = new Server(configuration.getPort());
		
		final ContextHandlerCollection contexts = new ContextHandlerCollection();
		server.setHandler(contexts);
		
		final Context root = new Context(contexts, "/", Context.SESSIONS);
		root.addServlet(new ServletHolder(new HubServlet(remoteControlPool, environmentManager)), "/selenium-server/driver/*");
        root.addServlet(new ServletHolder(new ConsoleServlet(environmentManager, remoteControlPool, pool)), "/console");
        root.addServlet(new ServletHolder(new RegistrationServlet(remoteControlPool)), "/registration-manager/register");
        root.addServlet(new ServletHolder(new UnregistrationServlet(remoteControlPool)), "/registration-manager/unregister");
        root.addServlet(new ServletHolder(new LifecycleManagerServlet(registry.lifecycleManager())), "/lifecycle-manager");

        root.addServlet(new ServletHolder(new BoxRegistrationServlet(pool, boxFile)), "/box-registration-manager/register");
        root.addServlet(new ServletHolder(new BoxUnregistrationServlet(pool, boxFile)), "/box-registration-manager/unregister");
        root.addServlet(new ServletHolder(new BoxStartupServlet(pool, boxFile)), "/box-registration-manager/start");

        Thread thread = new Thread(new Pinger(remoteControlPool, pool));
        thread.setDaemon(false);
		thread.start();
        server.start();
        server.join();
    }

}