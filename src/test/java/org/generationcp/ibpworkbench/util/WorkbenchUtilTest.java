package org.generationcp.ibpworkbench.util;

import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Test;

import junit.framework.Assert;

public class WorkbenchUtilTest {
	private static final String HASHED_PASSWORD = "$2a$10$ycdBiYL8X9NcDmamrqdcY./n8EiBqejjMMLPYGULev.iNtJrWimwe";
	private static final String PASSWORD = "password";

	@Test
	public void testIsPasswordEqualToUsername() throws Exception {
		WorkbenchUser user = new WorkbenchUser();
		user.setName(PASSWORD);
		user.setPassword(HASHED_PASSWORD);

		Assert.assertTrue(WorkbenchUtil.isPasswordEqualToUsername(user));
	}
}
