package org.generationcp.ibpworkbench.actions;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class OpenWindowActionTest {
	private String cutOffDate;
	private OpenWindowAction openWindowAction;
	
	@Before
	public void setUp(){
		openWindowAction = new OpenWindowAction();
	}
	@Test
	public void testGetCutOffDate(){		
		cutOffDate = "March 31, 2015";
		Assert.assertEquals("Should return true when the cut off date is the same as March 31, 2015", cutOffDate, openWindowAction.getCutOffDate());		
	}
	
	@Test
	public void testGetCutOffDateReturnFalseWhenNotTheSameDate(){
		cutOffDate = "March 15, 2015";
		Assert.assertFalse("Should return false when the cut off date is not the same as March 31, 2015", cutOffDate.equalsIgnoreCase(openWindowAction.getCutOffDate()));		
	}
}
