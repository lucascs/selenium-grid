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
        final ContextHandlerCollection contexts;
        final HubConfiguration configuration;
        final Server server;
        final Context root;

        configuration = HubRegistry.registry().gridConfiguration().getHub();
        server = new Server(configuration.getPort());

        contexts = new ContextHandlerCollection();
        server.setHandler(contexts);

        root = new Context(contexts, "/", Context.SESSIONS);
//        root.setResourceBase("./");
//        root.addHandler(new ResourceHandler());
        
        HubRegistry registry = HubRegistry.registry();
        DynamicRemoteControlPool remoteControlPool = registry.remoteControlPool();
		EnvironmentManager environmentManager = registry.environmentManager();
		BoxPool pool = new BoxPool(remoteControlPool);
		File boxFile = new File("boxes.xml");
		if (boxFile.exists()) {
			pool.loadFrom(boxFile);
		}
		
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