package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Mock;
import org.junit.Test;

public class BoxRegistrationServletTest extends UsingClassMock {

	@Test
	public void testRegisteringABoxWillPutItInBoxPoolAndWriteTheFile() throws Exception {
		Mock pool = mock(BoxPool.class);
		File file = new File(".");
		
		BoxRegistrationServlet servlet = new BoxRegistrationServlet((BoxPool) pool, file);

		Mock request = mock(HttpServletRequest.class);
		request.expects("getParameter").with("host").will(returnValue("localhost"));
		request.expects("getParameter").with("port").will(returnValue("1234"));
		pool.expects("register").with(eq(new Box("localhost", 1234)));
		pool.expects("saveTo").with(sameInstanceAs(file));
		
		Mock response = mock(HttpServletResponse.class);
		response.stubs("getWriter").will(returnValue(mock(PrintWriter.class)));
		
		servlet.process((HttpServletRequest)request, (HttpServletResponse) response);
		
		verifyMocks();
	}
}
