package com.thoughtworks.selenium.grid.hub.management.box;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Matcher;
import org.jbehave.core.mock.Mock;
import org.junit.Test;

import com.thoughtworks.selenium.grid.hub.Hub;

public class BoxStartupServletTest extends UsingClassMock {

	@Test
	public void testStartingABoxWillStartTheirRemoteControlsAndWriteTheFile() throws Exception {
		
		Mock pool = mock(BoxPool.class);
		File file = new File(".");
		BoxStartupServlet servlet = new BoxStartupServlet((BoxPool) pool, file);
		
		Mock request = mock(HttpServletRequest.class);
		request.expects("getParameter").with("host").will(returnValue("localhost"));
		request.expects("getParameter").with("port").will(returnValue("1234"));
		request.expects("getParameter").with("quantity").will(returnValue("3"));
		request.expects("getParameter").with("hubURL").will(returnValue("someURL"));
		request.expects("getParameter").with("portStart").will(returnValue("1000"));
		request.expects("getParameter").with("environment").will(returnValue("someEnvironment"));
		
		Mock box = mock(Box.class);
		pool.expects("load").with(eq(new Box("localhost", 1234))).will(returnValue(box));
		
		box.expects("startRemoteControls").with(new Matcher[] {eq(3), eq("someEnvironment"), eq(1000),
				eq(new Hub("someURL"))}).will(returnValue(true));
		
		pool.expects("saveTo").with(sameInstanceAs(file));
		Mock response = mock(HttpServletResponse.class);
		response.stubs("getWriter").will(returnValue(mock(PrintWriter.class)));
		servlet.process((HttpServletRequest) request, (HttpServletResponse) response);
	}
}
