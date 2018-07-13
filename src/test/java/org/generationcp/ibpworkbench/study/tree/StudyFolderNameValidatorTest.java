package org.generationcp.ibpworkbench.study.tree;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Window;

public class StudyFolderNameValidatorTest {
	private static final String PROGRAM_UUID = "abcd-efgh-189";

	private static final String ROOT_FOLDER_NAME = "Studies";
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private Window window;
	
	@Mock
	private ContextUtil contextUtil;
	
	@InjectMocks
	private StudyFolderNameValidator validator;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final Project project = ProjectTestDataInitializer.createProject();
		project.setUniqueID(PROGRAM_UUID);
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();
	}
	
	@Test
	public void testIsValidNameInputReturnsFalseforEmptyString() {
		Assert.assertFalse("Expected to return false for empty string.", this.validator.isValidNameInput("", this.window));
	}

	@Test
	public void testIsValidNameInputReturnsFalseforStudyNameWithLongNames() {
		final String studyName =
				"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium,"
						+ " totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. "
						+ "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos "
						+ "qui ratione voluptatem sequi nesciunt.";
		Assert.assertFalse("Expected to return false for study name length > 255.",
				this.validator.isValidNameInput(studyName, this.window));
	}

	@Test
	public void testIsValidNameInputReturnsFalseforExistingStudyName() {
		final String itemName = "Sample Folder Name";
		Mockito.when(this.studyDataManager.checkIfProjectNameIsExistingInProgram(Matchers.eq(itemName), Matchers.anyString()))
				.thenReturn(true);
		Assert.assertFalse("Expected to return false for existing study name",
				this.validator.isValidNameInput(itemName, this.window));
	}

	@Test
	public void testIsValidNameInputReturnsFalseforUsingRootFolderName() {
		final String itemName = ROOT_FOLDER_NAME;
		Mockito.when(this.messageSource.getMessage(Message.STUDIES)).thenReturn(ROOT_FOLDER_NAME);
		Assert.assertFalse("Expected to return false for using root folder name.",
				this.validator.isValidNameInput(itemName, this.window));
	}


}
