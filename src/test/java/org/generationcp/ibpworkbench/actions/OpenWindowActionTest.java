
package org.generationcp.ibpworkbench.actions;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class OpenWindowActionTest {

	private static final String ACTUAL_CUTOFF_DATE = "December 31, 2015";
	private String cutOffDate;
	private OpenWindowAction openWindowAction;

	@Before
	public void setUp() {
		this.openWindowAction = new OpenWindowAction();
	}

	@Test
	public void testGetCutOffDate() {
		this.cutOffDate = OpenWindowActionTest.ACTUAL_CUTOFF_DATE;
		Assert.assertEquals("Should return true when the cut off date is the same as " + OpenWindowActionTest.ACTUAL_CUTOFF_DATE,
				this.cutOffDate, this.openWindowAction.getCutOffDate());
	}

	@Test
	public void testGetCutOffDateReturnFalseWhenNotTheSameDate() {
		this.cutOffDate = "December 15, 2015";
		Assert.assertFalse("Should return false when the cut off date is not the same as " + OpenWindowActionTest.ACTUAL_CUTOFF_DATE,
				this.cutOffDate.equalsIgnoreCase(this.openWindowAction.getCutOffDate()));
	}
}
