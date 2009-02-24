package com.thoughtworks.selenium.grid.hub.management.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Mock;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.grid.hub.Environment;
import com.thoughtworks.selenium.grid.hub.EnvironmentManager;
import com.thoughtworks.selenium.grid.hub.management.box.Box;
import com.thoughtworks.selenium.grid.hub.management.box.BoxPool;
import com.thoughtworks.selenium.grid.hub.management.console.mvc.Page;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

public class ConsoleControllerTest extends UsingClassMock {

	private ConsoleController controller;
	private Mock environmentManager;
	private Mock remoteControlPool;
	private Mock boxPool;

	@Before
	public void setUp() {
		environmentManager = mock(EnvironmentManager.class);
		remoteControlPool = mock(DynamicRemoteControlPool.class);
		boxPool = mock(BoxPool.class);
		
		controller = new ConsoleController((EnvironmentManager)environmentManager, 
				(DynamicRemoteControlPool)remoteControlPool, (BoxPool)boxPool);
	}
    @Test
    public void listPageTemplateIsIndex() {
    	ensureThat(controller.list().template(), is("index.html"));
        verifyMocks();
    }

    @Test
    public void listSetAvailableRemoteControlAssignFromRegistry() {
        final List<RemoteControlProxy> expectedRemoteControls = Arrays.asList(new RemoteControlProxy("", 0, "", 1, null));
        remoteControlPool.stubs("availableRemoteControls").will(returnValue(expectedRemoteControls));

        ensureThat(controller.list().assigns().get("availableRemoteControls"), is(expectedRemoteControls));
        verifyMocks();
    }

    @Test
    public void listSetReservedRemoteControlAssignFromRegistry() {
        final List<RemoteControlProxy> expectedRemoteControls = Arrays.asList(new RemoteControlProxy("", 0, "", 1, null));
        remoteControlPool.stubs("reservedRemoteControls").will(returnValue(expectedRemoteControls));
        
        ensureThat(controller.list().assigns().get("reservedRemoteControls"), is(expectedRemoteControls));
        verifyMocks();
    }

    @Test
    public void listSetEnvironmentsAssignFromRegistry() {
        final List<Environment> expectedEnvironments = Arrays.asList(new Environment("", ""));
        environmentManager.expects("environments").will(returnValue(expectedEnvironments));

        ensureThat(controller.list().assigns().get("environments"), is(expectedEnvironments));
        verifyMocks();
    }

    @Test
    public void processRendersThePageReturnedByTheListAction() throws IOException, ServletException {
        final Page expectedPage = new Page("");
        final Mock expectedResponse = mock(HttpServletResponse.class);

        final ConsoleController controller = new ConsoleController((EnvironmentManager)environmentManager, 
        		(DynamicRemoteControlPool)remoteControlPool, (BoxPool)boxPool) {

            @Override
			public Page list() {
                return expectedPage;
            }

            @Override
			public void render(Page page, HttpServletResponse response) throws IOException {
            	ensureThat(page, is(expectedPage));
            	ensureThat(response, sameInstanceAs(expectedResponse));
            }
        };

        controller.process((HttpServletResponse) expectedResponse);
        verifyMocks();
    }

    @Test
    public void rendersIndexPage() throws IOException {
        final Mock response = mock(HttpServletResponse.class);
        response.stubs("getWriter").will(returnValue(mock(PrintWriter.class)));
        
        final RemoteControlProxy[] remoteControls = new RemoteControlProxy[]{new RemoteControlProxy("a host", 0, "an environment", 1, null)};
        
        final Page page = new Page("index.html");
        page.set("environments", new Environment[] { new Environment("a environment", "a browser")});
        page.set("availableRemoteControls", remoteControls);
        page.set("reservedRemoteControls", remoteControls);
        page.set("boxes", new ArrayList<Box>());

        controller.render(page, (HttpServletResponse) response);
    }

}
