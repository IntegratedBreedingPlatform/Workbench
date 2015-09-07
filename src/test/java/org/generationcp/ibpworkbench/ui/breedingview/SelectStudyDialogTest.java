
package org.generationcp.ibpworkbench.ui.breedingview;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SelectStudyDialogTest {
	@Mock
	private Project currentProject;

	@Mock
	private StudyDataManager studyDataManager;

	@InjectMocks
	private SelectStudyDialog dialog =
			new SelectStudyDialog(Mockito.mock(Window.class), Mockito.mock(Component.class), currentProject);

	private static final String UNIQUE_ID = "12345";

	@Test
	public void testCreateStudyTreeTable() throws MiddlewareQueryException {
		Mockito.when(currentProject.getUniqueID()).thenReturn(UNIQUE_ID);
		dialog.createStudyTreeTable();
		Mockito.verify(studyDataManager, Mockito.times(1)).getRootFolders(UNIQUE_ID);
	}
}
