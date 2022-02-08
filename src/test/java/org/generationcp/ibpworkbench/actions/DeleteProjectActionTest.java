package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteProjectActionTest {

	public static final String UNIQUE_ID = "2187348324-873438650-2834732";
	public static final String DELETE_PROJECT_LINK = "Delete Project Link";
	public static final String DELETE_PROGRAM_CONFIRM = "Delete Program Confirm";
	public static final String YES = "Yes";
	public static final String NO = "No";
	public static final String PROJECT_NAME = "ProjectName";

	@Mock
	private Window window;

	@Mock
	private WorkbenchDataManager manager;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private LocationDataManager locationDataManager;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private GermplasmListManager germplasmListManager;

	@Mock
	private ProgramService programService;

	@InjectMocks
	private DeleteProjectAction deleteProjectAction;

	private Project project;

	@Before
	public void init() {

		project = new Project();
		project.setUniqueID(UNIQUE_ID);
		project.setProjectName(PROJECT_NAME);

		when(contextUtil.getProjectInContext()).thenReturn(project);
	}

	@Test
	public void testDoAction() {

		when(messageSource.getMessage(Message.DELETE_PROJECT_LINK)).thenReturn(DELETE_PROJECT_LINK);
		when(messageSource.getMessage(Message.DELETE_PROGRAM_CONFIRM, PROJECT_NAME)).thenReturn(DELETE_PROGRAM_CONFIRM);
		when(messageSource.getMessage(Message.YES)).thenReturn(YES);
		when(messageSource.getMessage(Message.NO)).thenReturn(NO);

		deleteProjectAction.doAction(window, null, false);

		final ArgumentCaptor<ConfirmDialog> captor = ArgumentCaptor.forClass(ConfirmDialog.class);
		verify(window).addWindow(captor.capture());

		final ConfirmDialog confirmDialog = captor.getValue();

		assertEquals(DELETE_PROJECT_LINK, confirmDialog.getCaption());
		assertEquals(DELETE_PROGRAM_CONFIRM, confirmDialog.getMessage());

	}

	@Test
	public void testDeleteProgram() {

		final List<ProgramFavorite> favoriteLocations = new ArrayList<>();
		final List<ProgramFavorite> favoriteMethods = new ArrayList<>();

		when(germplasmDataManager.getProgramFavorites(ProgramFavorite.FavoriteType.LOCATION, project.getUniqueID()))
				.thenReturn(favoriteLocations);
		when(germplasmDataManager.getProgramFavorites(ProgramFavorite.FavoriteType.METHODS, project.getUniqueID()))
				.thenReturn(favoriteMethods);

		deleteProjectAction.deleteProgram(project);

		verify(germplasmDataManager, Mockito.times(2)).deleteProgramFavorites(ArgumentMatchers.<List<ProgramFavorite>>any());
		verify(studyDataManager).deleteProgramStudies(project.getUniqueID());
		verify(germplasmListManager).deleteGermplasmListsByProgram(project.getUniqueID());

	}

}
