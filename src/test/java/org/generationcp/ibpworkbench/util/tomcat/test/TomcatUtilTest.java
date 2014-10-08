package org.generationcp.ibpworkbench.util.tomcat.test;

import org.generationcp.ibpworkbench.util.tomcat.TomcatUtil;
import org.generationcp.ibpworkbench.util.tomcat.WebAppStatus;
import org.generationcp.ibpworkbench.util.tomcat.WebAppStatusInfo;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

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

    @Test
    public void test() {
        // THIS IS A BLANK METHOD USED TO PROVIDE A TEST METHOD FOR THIS CLASS
        // TODO : IMPLEMENT AN ACTUAL TEST!
    }
}
