package org.generationcp.ibpworkbench;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import com.vaadin.terminal.gwt.server.ApplicationServlet;

public class Launcher {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setResourceBase("./src/main/webapp");
        
        ServletHolder ibpworkbench = new ServletHolder(new ApplicationServlet());
        ibpworkbench.setInitParameter("application", "org.generationcp.ibpworkbench.IBPWorkbenchApplication");

        context.addServlet(ibpworkbench, "/ibpworkbench/*");
        context.addServlet(ibpworkbench, "/VAADIN/*");
        server.setHandler(context);
        server.start();
    }
}
