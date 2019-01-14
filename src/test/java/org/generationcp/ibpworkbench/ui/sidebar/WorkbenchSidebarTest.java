package org.generationcp.ibpworkbench.ui.sidebar;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchSidebarTest {

	@Mock
	private Component parent;

	@Mock
	private Window window;

	@Mock
	private Tree sidebarTree;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private WorkbenchSidebarPresenter presenter;

	@Mock
	private PlatformTransactionManager transactionManager;

	private Project project;

	private WorkbenchSidebar workbenchSidebar;

	@Before
	public void init() {

		project = new Project();

		workbenchSidebar = new WorkbenchSidebar();
		workbenchSidebar.setSidebarTree(sidebarTree);
		workbenchSidebar.setContextUtil(contextUtil);
		workbenchSidebar.setPresenter(presenter);
		workbenchSidebar.setTransactionManager(transactionManager);

		when(contextUtil.getProjectInContext()).thenReturn(project);
	}

	@Test
	public void testTreeClickListener() {

		final ItemClickEvent event = mock(ItemClickEvent.class);
		final ActionListener actionListener = mock(ActionListener.class);

		final WorkbenchSidebar.TreeItem treeItem = new WorkbenchSidebar().new TreeItem("1", "caption", null);

		when(event.getComponent()).thenReturn(parent);
		when(parent.getWindow()).thenReturn(window);

		when(event.getItemId()).thenReturn(treeItem);

		final WorkbenchSidebar mockWorkbenchSidebar = mock(WorkbenchSidebar.class);
		when(mockWorkbenchSidebar.doCollapse(treeItem)).thenReturn(false);
		when(mockWorkbenchSidebar.getLinkActions(treeItem.getId(), project)).thenReturn(actionListener);

		final WorkbenchSidebar.TreeItemClickListener listener = workbenchSidebar.new TreeItemClickListener(mockWorkbenchSidebar);
		listener.itemClick(event);

		verify(presenter).updateProjectLastOpenedDate();
		verify(actionListener).doAction(window, "/" + treeItem.getId(), true);

	}

}
