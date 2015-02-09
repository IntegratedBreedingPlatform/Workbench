package org.generationcp.ibpworkbench.actions;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class OpenWindowActionTest {
	private static final String ACTUAL_CUTOFF_DATE = "December 31, 2015";
	private String cutOffDate;
	private OpenWindowAction openWindowAction;
	
	@Before
	public void setUp(){
		openWindowAction = new OpenWindowAction();
	}
	@Test
	public void testGetCutOffDate(){		
		cutOffDate = ACTUAL_CUTOFF_DATE;
		Assert.assertEquals("Should return true when the cut off date is the same as " + ACTUAL_CUTOFF_DATE, cutOffDate, openWindowAction.getCutOffDate());		
	}
	
	@Test
	public void testGetCutOffDateReturnFalseWhenNotTheSameDate(){
		cutOffDate = "December 15, 2015";
		Assert.assertFalse("Should return false when the cut off date is not the same as " + ACTUAL_CUTOFF_DATE, cutOffDate.equalsIgnoreCase(openWindowAction.getCutOffDate()));		
	}
}
