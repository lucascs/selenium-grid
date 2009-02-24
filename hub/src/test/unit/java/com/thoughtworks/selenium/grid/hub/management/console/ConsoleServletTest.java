package com.thoughtworks.selenium.grid.hub.management.console;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Mock;
import org.junit.Test;


public class ConsoleServletTest extends UsingClassMock {

    @Test
    public void doGetCallsListActionOnConsoleController() throws IOException, ServletException {
        final Mock response = mock(HttpServletResponse.class);
        
        final Mock controller = mock(ConsoleController.class);
        
        final ConsoleServlet servlet = new ConsoleServlet(null, null, null) {
            @Override
			protected ConsoleController controller() {
                return (ConsoleController) controller;
            }
        };

        controller.expects("process").with(response);

        servlet.doGet(null, (HttpServletResponse) response);
        verifyMocks();
    }

}
