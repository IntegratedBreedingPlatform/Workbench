package org.generationcp.ibpworkbench.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebLoginForwardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String loginUrl = request.getParameter("login_url");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (loginUrl == null || loginUrl.trim().length() == 0) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String loadHtml = "<html>\r\n"
                        + "<head><title></title></head>\r\n"
                        + "<script type=\"text/javascript\" src=\"/ibpworkbench/VAADIN/themes/gcp-default/jquery-1.7.2.js\"></script>\r\n"
                        + "<div style=\"display: none\">\r\n"
                        + "<form id=\"loginForm\" name=\"LoginForm\" method=\"post\" action=\"%s\">\r\n"
                        + "<input type=\"text\" name=\"uname\" value=\"%s\">\r\n"
                        + "<input type=\"text\" name=\"password\" value=\"%s\">\r\n"
                        + "<input type=\"submit\" value=\"Submit\" name=\"login\">\r\n"
                        + "</form>\r\n"
                        + "</div>\r\n"
                        + "<script type=\"text/javascript\">\r\n"
                        + "jQuery(\"#loginForm\").submit();\r\n"
                        + "</script>\r\n"
                        + "</html>\r\n";
        
        String html = String.format(loadHtml, loginUrl, username, password);
        
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(html.length());
        response.getOutputStream().write(html.getBytes());
        response.getOutputStream().flush();
    }
}
