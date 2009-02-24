package com.thoughtworks.selenium.grid.hub.management;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Mock;
import org.junit.Test;

public class LifecyleManagerServletTest extends UsingClassMock {

    @Test
    public void shutdownHubWhenPostingAShutdownAction() throws IOException, ServletException {
        final Mock lifecyleManager = mock(LifecycleManager.class);
        
        final LifecycleManagerServlet servlet = new LifecycleManagerServlet((LifecycleManager) lifecyleManager);
        
        final Mock request = mock(HttpServletRequest.class);
        request.stubs("getParameter").with("action").will(returnValue("shutdown"));
        
        lifecyleManager.expects("shutdown");

        servlet.doPost((HttpServletRequest) request, (HttpServletResponse) mock(HttpServletResponse.class));
        verifyMocks();        
    }

    @Test
    public void doNotShutdownHubWhenPostedActionIsNotShutdown() throws IOException, ServletException {
        final Mock lifecyleManager = mock(LifecycleManager.class);
        
        final LifecycleManagerServlet servlet = new LifecycleManagerServlet((LifecycleManager) lifecyleManager);

        final Mock request = mock(HttpServletRequest.class);
        request.stubs("getParameter").with("action").will(returnValue("random action"));
        
        lifecyleManager.expects("shutdown").never();

        servlet.doPost((HttpServletRequest) request, (HttpServletResponse) mock(HttpServletResponse.class));
        verifyMocks();
    }

}
