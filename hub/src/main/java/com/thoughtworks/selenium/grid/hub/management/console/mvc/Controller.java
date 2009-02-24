package com.thoughtworks.selenium.grid.hub.management.console.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Web Controller processing HTTP requests.
 *
 * @author: Philippe Hanrigou
 */
public class Controller {

    public void render(Page page, HttpServletResponse response) throws IOException {
        final Template template;
        final String content;

        template = templateResolver().get(page.template());
        content = template.render(page.assigns());
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(content);
    }

    protected TemplateResolver templateResolver() {
      return new TemplateResolver(getClass());
    }

}
