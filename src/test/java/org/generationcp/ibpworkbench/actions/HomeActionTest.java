package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HomeActionTest {

	public static final String PROJECT_NAME = "ProjectName";
	@Mock
	private WorkbenchMainView workbenchMainView;

	@Mock
	private ContextUtil contextUtil;

	@InjectMocks
	private HomeAction homeAction;

	private Project project;

	@Before
	public void init() {

		project = new Project();
		project.setProjectName(PROJECT_NAME);

		when(contextUtil.getProjectInContext()).thenReturn(project);

	}

	@Test
	public void testDoAction() {

		homeAction.doAction(workbenchMainView, null, false);

		final ArgumentCaptor<WorkbenchDashboard> captor = ArgumentCaptor.forClass(WorkbenchDashboard.class);

		verify(workbenchMainView).showContent(captor.capture());
		verify(workbenchMainView).addTitle(PROJECT_NAME);

	}

}
