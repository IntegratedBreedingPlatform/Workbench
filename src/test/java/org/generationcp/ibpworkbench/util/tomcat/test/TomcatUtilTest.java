
package org.generationcp.ibpworkbench.util.tomcat.test;

import java.io.IOException;
import java.util.List;

import org.generationcp.commons.tomcat.util.TomcatUtil;
import org.generationcp.commons.tomcat.util.WebAppStatus;
import org.generationcp.commons.tomcat.util.WebAppStatusInfo;
import org.junit.Test;

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
		
	}

	@Test
	public void test() {
		// THIS IS A BLANK METHOD USED TO PROVIDE A TEST METHOD FOR THIS CLASS
		// TODO : IMPLEMENT AN ACTUAL TEST!
	}
}
