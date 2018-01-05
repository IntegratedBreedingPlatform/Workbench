package org.generationcp.ibpworkbench.util.tomcat.test;

import org.generationcp.commons.tomcat.util.TomcatUtil;
import org.generationcp.commons.tomcat.util.WebAppStatus;
import org.generationcp.commons.tomcat.util.WebAppStatusInfo;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TomcatUtilTest {

	public static void main(final String[] args) throws IOException {
		final String managerUrl = "http://tomcat:tomcat@localhost:18080/manager/text";
		final String username = "tomcat";
		final String password = "tomcat";

		final TomcatUtil tomcatUtil = new TomcatUtil(managerUrl, username, password);

		final WebAppStatusInfo statusInfo = tomcatUtil.getWebAppStatus();
		final List<WebAppStatus> webAppStatusList = statusInfo.asList();
		for (final WebAppStatus status : webAppStatusList) {
			System.out.println(status);
		}

	}

	@Test
	public void test() {
		// THIS IS A BLANK METHOD USED TO PROVIDE A TEST METHOD FOR THIS CLASS
		// TODO : IMPLEMENT AN ACTUAL TEST!
	}
}
