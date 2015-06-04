
package org.generationcp.ibpworkbench.ui.workflow;

import org.generationcp.MessageResourceUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.VaadinComponentsUtil;
import org.generationcp.commons.vaadin.ui.VaadinComponentsUtil.VaadinComponentFieldType;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MarsWorkflowDiagramTest {

	private static MarsWorkflowDiagram workflow;
	private static final SimpleResourceBundleMessageSource messageSource = MessageResourceUtil.getMessageResource();

	@BeforeClass
	public static void setUp() {
		MarsWorkflowDiagramTest.workflow =
				new MarsWorkflowDiagram(true, MarsWorkflowDiagramTest.getProjectTestData(), MarsWorkflowDiagramTest.getRoleTestData());
		MarsWorkflowDiagramTest.workflow.setMessageSource(MarsWorkflowDiagramTest.messageSource);
		MarsWorkflowDiagramTest.workflow.afterPropertiesSet();
	}

	public static Project getProjectTestData() {
		Project project = new Project();
		project.setProjectId(1L);
		return project;
	}

	public static Role getRoleTestData() {
		Role role = new Role();
		role.setName(Role.MANAGER_ROLE_NAME);
		role.setRoleId(5);
		role.setWorkflowTemplate(new WorkflowTemplate(5L));
		role.setLabelOrder("5");
		role.setLabel("Access all tools with a menu interface (MENU)");
		return role;
	}

	@Test
	public void testCheckIfGDMSIsDisplayed() {
		String caption = MarsWorkflowDiagramTest.messageSource.getMessage(Message.GENOTYPIC_DATA_BROWSER_LINK);
		boolean isFound =
				VaadinComponentsUtil.findComponent(MarsWorkflowDiagramTest.workflow.getContent(), VaadinComponentFieldType.CAPTION,
						caption, null);
		Assert.assertTrue(caption + " is not found", isFound);
	}

}
