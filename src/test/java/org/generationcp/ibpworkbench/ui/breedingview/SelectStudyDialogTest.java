package org.generationcp.ibpworkbench.ui.breedingview;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Test;
import org.mockito.Mockito;

public class SelectStudyDialogTest {
    @Test
    public void testCreateStudyTreeTable() throws MiddlewareQueryException {
        StudyDataManagerImpl studyDataManager = Mockito.mock(StudyDataManagerImpl.class);
        Project project = Mockito.mock(Project.class);
        String uniqueId = "12345";
        Mockito.when(project.getUniqueID()).thenReturn(uniqueId);
        SelectStudyDialog dialog = new SelectStudyDialog(Mockito.mock(Window.class), Mockito.mock(Component.class), studyDataManager, project);
        dialog.createStudyTreeTable();
        Mockito.verify(studyDataManager, Mockito.times(1)).getRootFolders(uniqueId);
    }
}
