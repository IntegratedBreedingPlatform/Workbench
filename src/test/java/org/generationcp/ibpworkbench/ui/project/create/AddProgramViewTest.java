package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import junit.framework.Assert;
import org.junit.Test;

public class AddProgramViewTest {

	private final AddProgramView addProgramView = new AddProgramView();

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

		this.addProgramView.setIsSingleUserOnly("false");

		this.addProgramView.addProgramMembersTab(tabSheet, programMembersContainer);

		// Verify that programMembersContainer is added in tabSheet
		Assert.assertEquals(1, tabSheet.getComponentCount());
		Assert.assertFalse(tabSheet.getTab(programMembersContainer).isClosable());
		Assert.assertEquals("Members", tabSheet.getTab(programMembersContainer).getCaption());
		Assert.assertTrue(programMembersContainer.isVisible());

	}

}
