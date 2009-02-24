package com.thoughtworks.selenium.grid.hub.management;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Mock;
import org.junit.Test;

import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

public class UnregistrationServletTest extends UsingClassMock {

    @Test
    public void registerSubmittedRemoteControlPool() throws IOException {
        final Mock expectedResponse = mock(HttpServletResponse.class);
        final Mock remoteControlPool = mock(DynamicRemoteControlPool.class);
        final UnregistrationServlet servlet = new UnregistrationServlet((DynamicRemoteControlPool) remoteControlPool) {
            @Override
			protected void writeSuccessfulResponse(HttpServletResponse response) throws IOException {
            	ensureThat(response, sameInstanceAs(expectedResponse));
            }
        };

        final Mock request = mock(HttpServletRequest.class);
        final RemoteControlProxy expectedRemoteControl = new RemoteControlProxy("a host", 24, "an environment", 1, null);
        request.stubs("getParameter").with("host").will(returnValue(expectedRemoteControl.host()));
        request.stubs("getParameter").with("port").will(returnValue("" + expectedRemoteControl.port()));
        request.stubs("getParameter").with("environment").will(returnValue(expectedRemoteControl.environment()));

        remoteControlPool.expects("unregister").with(eq(expectedRemoteControl)).will(returnValue(true));

        servlet.process((HttpServletRequest) request, (HttpServletResponse) expectedResponse);
        verifyMocks();
    }
}