package com.thoughtworks.selenium.grid.hub.management.console.mvc;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Mock;
import org.junit.Test;

public class ControllerTest extends UsingClassMock {

    @Test
    public void renderAsTextHTMLContentType() throws IOException {
        final Mock response = mock(HttpServletResponse.class);
        final Mock templateResolver = mock(TemplateResolver.class);
        final Mock template = mock(Template.class);
        final Controller controller;

        templateResolver.stubs("get").will(returnValue(template));
        template.stubs("render").will(returnValue(""));
        response.stubs("getWriter").will(returnValue(mock(PrintWriter.class)));

        response.expects("setContentType").with("text/html");
        controller = new Controller();
        controller.render(new Page(""), (HttpServletResponse) response);
        verifyMocks();
    }

    @Test
    public void renderSetCharacterEncodingAsUTF8() throws IOException {
        final Mock response = mock(HttpServletResponse.class);
        final Mock templateResolver = mock(TemplateResolver.class);
        final Mock template = mock(Template.class);
        final Controller controller;

        templateResolver.stubs("get").will(returnValue(template));
        template.stubs("render").will(returnValue(""));
        response.stubs("getWriter").will(returnValue(mock(PrintWriter.class)));

        response.expects("setCharacterEncoding").with("UTF-8");
        controller = new Controller();
        controller.render(new Page(""), (HttpServletResponse) response);
        verifyMocks();
    }

    @Test
    public void renderWriteRenderedTemplateContentInResponse() throws IOException {
        final Controller stubbedController;
        final Mock templateResolver;
        final Mock template;
        final Mock response;
        final StringWriter writer;
        final Page page;

        templateResolver = mock(TemplateResolver.class);
        response = mock(HttpServletResponse.class);
        template = mock(Template.class);
        writer = new StringWriter();

        page = new Page("template name");
        templateResolver.expects("get").with("template name").will(returnValue(template));
        template.expects("render").with(page.assigns()).will(returnValue("rendered content"));
        response.stubs("getWriter").will(returnValue(new PrintWriter(writer)));

        stubbedController = new Controller() {
            @Override
			protected TemplateResolver templateResolver() {
                return (TemplateResolver) templateResolver;
            }
        };
        stubbedController.render(page, (HttpServletResponse) response);
        assertEquals("rendered content", writer.toString());
        verifyMocks();
    }

}
