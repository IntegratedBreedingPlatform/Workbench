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

	@Mock
	private WorkbenchMainView workbenchMainView;

	@Mock
	private ContextUtil contextUtil;

	@InjectMocks
	private HomeAction homeAction;

	@Before
	public void init() {

		final Project project = new Project();
		project.setProjectName("ProjectName");

		when(this.contextUtil.getProjectInContext()).thenReturn(project);

	}

	@Test
	public void testDoAction() {

		this.homeAction.doAction(this.workbenchMainView, null, false);

		final ArgumentCaptor<WorkbenchDashboard> captor = ArgumentCaptor.forClass(WorkbenchDashboard.class);

		verify(this.workbenchMainView).showContent(captor.capture());
	}

}
