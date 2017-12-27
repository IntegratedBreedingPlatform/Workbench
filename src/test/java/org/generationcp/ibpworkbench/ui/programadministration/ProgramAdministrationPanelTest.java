
package org.generationcp.ibpworkbench.ui.programadministration;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.TabSheet;

import junit.framework.Assert;

public class ProgramAdministrationPanelTest {

	private static final String PROGRAM_SUMMARY = "Program Summary";

	private static final String SYSTEM_LABELS = "System Labels";

	private static final String METHODS = "Methods";

	private static final String LOCATIONS = "Locations";

	private static final String MEMBERS = "Members";

	private static final String BASIC_DETAILS = "Basic Details";

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@InjectMocks
	private ProgramAdministrationPanel programAdminPanel;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		// Setup Mocks
		Mockito.doReturn(ProgramAdministrationPanelTest.BASIC_DETAILS).when(this.messageSource).getMessage(Message.BASIC_DETAILS_LABEL);
		Mockito.doReturn(ProgramAdministrationPanelTest.MEMBERS).when(this.messageSource).getMessage(Message.PROGRAM_MEMBERS);
		Mockito.doReturn(ProgramAdministrationPanelTest.LOCATIONS).when(this.messageSource).getMessage(Message.PROGRAM_LOCATIONS);
		Mockito.doReturn(ProgramAdministrationPanelTest.METHODS).when(this.messageSource).getMessage(Message.BREEDING_METHODS_LABEL);
		Mockito.doReturn(ProgramAdministrationPanelTest.SYSTEM_LABELS).when(this.messageSource).getMessage("SYSTEM_LABELS");
		Mockito.doReturn(ProgramAdministrationPanelTest.PROGRAM_SUMMARY).when(this.messageSource).getMessage("PROGRAM_SUMMARY");

		// Initialize UI components
		this.programAdminPanel.initializeComponents();
	}

	@Test
	public void testInitializeLayout() {
		// Call method to test
		this.programAdminPanel.initializeLayout();

		final TabSheet adminTabSheet = this.programAdminPanel.getTabSheet();
		Assert.assertEquals(6, adminTabSheet.getComponentCount());
		Assert.assertEquals(ProgramAdministrationPanelTest.BASIC_DETAILS, adminTabSheet.getTab(0).getCaption());
		Assert.assertEquals(ProgramAdministrationPanelTest.MEMBERS, adminTabSheet.getTab(1).getCaption());
		Assert.assertEquals(ProgramAdministrationPanelTest.LOCATIONS, adminTabSheet.getTab(2).getCaption());
		Assert.assertEquals(ProgramAdministrationPanelTest.METHODS, adminTabSheet.getTab(3).getCaption());
		Assert.assertEquals(ProgramAdministrationPanelTest.SYSTEM_LABELS, adminTabSheet.getTab(4).getCaption());
		Assert.assertEquals(ProgramAdministrationPanelTest.PROGRAM_SUMMARY, adminTabSheet.getTab(5).getCaption());
	}

	@Test
	public void testAddProgramMembersTabSingleUser() {

		final TabSheet tabSheet = new TabSheet();
		final ProgramMembersPanel programMembersPanel = Mockito.mock(ProgramMembersPanel.class);

		this.programAdminPanel.setIsSingleUserOnly("true");

		this.programAdminPanel.addProgramMembersTab(tabSheet, programMembersPanel);

		// Verify that programMembersPanel is not added in tabSheet
		Assert.assertEquals(0, tabSheet.getComponentCount());



	}

	@Test
	public void testAddProgramMembersTabNotSingleUser() {

		final TabSheet tabSheet = new TabSheet();
		final ProgramMembersPanel programMembersPanel = Mockito.mock(ProgramMembersPanel.class);

		this.programAdminPanel.setIsSingleUserOnly("false");

		this.programAdminPanel.addProgramMembersTab(tabSheet, programMembersPanel);

		// Verify that programMembersPanel is added in tabSheet
		Assert.assertEquals(1, tabSheet.getComponentCount());
		Assert.assertFalse(tabSheet.getTab(programMembersPanel).isClosable());
		Assert.assertEquals(MEMBERS, tabSheet.getTab(programMembersPanel).getCaption());

	}

}
