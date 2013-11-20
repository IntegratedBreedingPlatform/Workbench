package org.generationcp.ibpworkbench.util.tomcat.test;

import java.io.IOException;
import java.util.List;

import org.generationcp.ibpworkbench.util.tomcat.TomcatUtil;
import org.generationcp.ibpworkbench.util.tomcat.WebAppStatus;
import org.generationcp.ibpworkbench.util.tomcat.WebAppStatusInfo;

public class TomcatUtilTest {

    public static void main(String[] args) throws IOException {
        String managerUrl = "http://tomcat:tomcat@localhost:18080/manager/text";
        String username = "tomcat";
        String password = "tomcat";
        
        TomcatUtil tomcatUtil = new TomcatUtil(managerUrl, username, password);
        
        WebAppStatusInfo statusInfo = tomcatUtil.getWebAppStatus();
        List<WebAppStatus> webAppStatusList = statusInfo.asList();
        for (WebAppStatus status : webAppStatusList) {
            System.out.println(status);
        }
        
        String contextPath = "/GermplasmStudyBrowser";
        if (statusInfo.isRunning(contextPath)) {
            tomcatUtil.stopWebApp(contextPath);
        }
        else {
            tomcatUtil.startWebApp(contextPath);
        }
    }
}
