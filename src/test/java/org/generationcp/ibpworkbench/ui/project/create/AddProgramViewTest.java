package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddProgramViewTest {

	private final AddProgramView addProgramView = new AddProgramView();

	@Before
	public void init() {

		this.addProgramView.initializeComponents();
		this.addProgramView.setIsSingleUserOnly("false");

	}

	@Test
	public void testAddProgramMembersTabSingleUser() {

		final TabSheet tabSheet = new TabSheet();
		final VerticalLayout programMembersContainer = new VerticalLayout();
		programMembersContainer.setVisible(false);

		this.addProgramView.setIsSingleUserOnly("true");

		this.addProgramView.addProgramMembersTab(tabSheet, programMembersContainer);

		// Verify that programMembersContainer is not added in tabSheet
		Assert.assertEquals(0, tabSheet.getComponentCount());
		Assert.assertFalse(programMembersContainer.isVisible());

	}

	@Test
	public void testAddProgramMembersTabNotSingleUser() {

		final TabSheet tabSheet = new TabSheet();
		final VerticalLayout programMembersContainer = new VerticalLayout();
		programMembersContainer.setVisible(false);

		this.addProgramView.addProgramMembersTab(tabSheet, programMembersContainer);

		// Verify that programMembersContainer is added in tabSheet
		Assert.assertEquals(1, tabSheet.getComponentCount());
		Assert.assertFalse(tabSheet.getTab(programMembersContainer).isClosable());
		Assert.assertEquals("Members", tabSheet.getTab(programMembersContainer).getCaption());
		Assert.assertTrue(programMembersContainer.isVisible());

	}

	@Test
	public void testInitializeLayoutNotSingleUser() {

		this.addProgramView.initializeLayout();

		// Verify that the following tabs are added
		final TabSheet.Tab basicDetails = this.addProgramView.getTabSheet().getTab(this.addProgramView.getBasicDetailsContainer());
		Assert.assertNotNull(basicDetails);
		Assert.assertFalse(basicDetails.isClosable());

		final TabSheet.Tab programMembers = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMembersContainer());
		Assert.assertNotNull(programMembers);
		Assert.assertFalse(programMembers.isClosable());

		final TabSheet.Tab programLocations = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramLocationsContainer());
		Assert.assertNotNull(programLocations);
		Assert.assertFalse(programLocations.isClosable());
		Assert.assertFalse(programLocations.isEnabled());

		final TabSheet.Tab programMethods = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMethodsContainer());
		Assert.assertNotNull(programMethods);
		Assert.assertFalse(programMethods.isClosable());
		Assert.assertFalse(programMethods.isEnabled());

	}

	@Test
	public void testInitializeLayoutSingleUser() {

		this.addProgramView.setIsSingleUserOnly("true");

		this.addProgramView.initializeLayout();

		// Verify that the following tabs are added
		final TabSheet.Tab basicDetails = this.addProgramView.getTabSheet().getTab(this.addProgramView.getBasicDetailsContainer());
		Assert.assertNotNull(basicDetails);
		Assert.assertFalse(basicDetails.isClosable());

		final TabSheet.Tab programLocations = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramLocationsContainer());
		Assert.assertNotNull(programLocations);
		Assert.assertFalse(programLocations.isClosable());
		Assert.assertFalse(programLocations.isEnabled());

		final TabSheet.Tab programMethods = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMethodsContainer());
		Assert.assertNotNull(programMethods);
		Assert.assertFalse(programMethods.isClosable());
		Assert.assertFalse(programMethods.isEnabled());

		// Verify that the Program Members tab is not added
		Assert.assertNull(this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMembersContainer()));

	}

}
