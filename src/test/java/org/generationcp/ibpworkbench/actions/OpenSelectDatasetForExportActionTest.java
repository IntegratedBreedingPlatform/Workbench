/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.util.FileNameGenerator;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.VariableTableItem;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public class OpenSelectDatasetForExportActionTest {

	private static final String STUDY_NAME = "STUDY 88";

	private static final String BV_VERSION = "1.7";

	private static final int STUDY_ID = 4526;

	private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;

	@Mock
	private SingleSiteAnalysisPanel singleSiteAnalysisPanel;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;

	@Mock
	private ClickEvent clickEvent;

	@Mock
	private Component component;

	@Mock
	private VariableTypeList summaryVariables;

	@Mock
	private VariableTypeList studyVariables;

	@Mock
	private VariableTypeList factors;

	@Mock
	private List<DMSVariableType> factorVariableTypes;

	@Mock
	private List<DMSVariableType> trialVariableTypes;

	@Mock
	private DataSet summaryDataset;

	@Mock
	private DataSet studyDataset;

	@Mock
	private Study study;

	@Captor
	private ArgumentCaptor<Component> componentCaptor;
	private Project project;
	private Tool bvTool;

	public static final String DATASET_NAME = "TEST\\ /:*?'\"<>|[]{},.?~`!@#$%^&()-=_+-PLOTDATA";
	public static final String SANITIZED_DATASET_NAME = "TEST_ _-_-PLOTDATA";
	public static final Integer DATASET_ID = 99;
	public static final String INPUT_DIRECTORY = "workspace/input";
	public static final String OBJECTIVE = RandomStringUtils.randomAlphabetic(20);
	public static final String DESCRIPTION = RandomStringUtils.randomAlphabetic(20);

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.project = ProjectTestDataInitializer.createProject();

		this.openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(this.singleSiteAnalysisPanel);
		this.openSelectDatasetForExportAction.setWorkbenchDataManager(this.workbenchDataManager);
		this.openSelectDatasetForExportAction.setInstallationDirectoryUtil(this.installationDirectoryUtil);
		this.openSelectDatasetForExportAction.setStudyDataManager(this.studyDataManager);
		this.openSelectDatasetForExportAction.setMessageSource(this.messageSource);

		Mockito.doReturn(STUDY_ID).when(this.study).getId();
		Mockito.doReturn(STUDY_NAME).when(this.study).getName();
		Mockito.doReturn(DESCRIPTION).when(this.study).getDescription();
		Mockito.doReturn(OBJECTIVE).when(this.study).getObjective();
		Mockito.doReturn(this.project).when(this.singleSiteAnalysisPanel).getCurrentProject();
		this.openSelectDatasetForExportAction.setProject(this.project);
		Mockito.doReturn(this.study).when(this.singleSiteAnalysisPanel).getCurrentStudy();
		this.openSelectDatasetForExportAction.setStudy(this.study);
		Mockito.doReturn(DATASET_ID).when(this.singleSiteAnalysisPanel).getCurrentDataSetId();
		this.openSelectDatasetForExportAction.setDataSetId(DATASET_ID);
		Mockito.doReturn(DATASET_NAME).when(this.singleSiteAnalysisPanel).getCurrentDatasetName();
		this.openSelectDatasetForExportAction.setDatasetName(DATASET_NAME);

		final Map<String, Boolean> variatesSelectionMap = new HashMap<>();
		final Map<String, Boolean> covariatesSelectionMap = new HashMap<>();
		variatesSelectionMap.put("Variable1", true);
		covariatesSelectionMap.put("Variable2", true);

		when(this.singleSiteAnalysisPanel.getVariatesSelectionMap()).thenReturn(variatesSelectionMap);
		when(this.singleSiteAnalysisPanel.getCovariatesSelectionMap()).thenReturn(covariatesSelectionMap);

		Mockito.doReturn(this.summaryDataset).when(this.studyDataManager).getDataSet(DATASET_ID);
		Mockito.doReturn(this.summaryVariables).when(this.summaryDataset).getVariableTypes();
		Mockito.doReturn(this.factors).when(this.summaryVariables).getFactors();
		Mockito.doReturn(this.factorVariableTypes).when(this.factors).getVariableTypes();
		Mockito.doReturn(this.studyDataset).when(this.studyDataManager)
				.findOneDataSetByType(Matchers.eq(STUDY_ID), Matchers.anyInt());
		Mockito.doReturn(this.studyVariables).when(this.studyDataset).getVariableTypes();
		Mockito.doReturn(this.trialVariableTypes).when(this.studyVariables).getVariableTypes();

		this.bvTool = new Tool();
		this.bvTool.setVersion(BV_VERSION);
		Mockito.doReturn(this.bvTool).when(this.workbenchDataManager).getToolWithName(Matchers.anyString());
		Mockito.doReturn(INPUT_DIRECTORY).when(this.installationDirectoryUtil)
				.getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);
		Mockito.doReturn(this.component).when(this.clickEvent).getComponent();
	}

	@Test
	public void testCheckIfNumericCategoricalVarAreIncluded() {
		final List<VariableTableItem> variateList = new ArrayList<VariableTableItem>();
		final Map<String, Boolean> variatesCheckboxState = new HashMap<String, Boolean>();
		this.createVariateListWithStateTestData(variateList, variatesCheckboxState);
		Assert.assertTrue("Numerical categorical variates if selected can be included",
				this.openSelectDatasetForExportAction.checkIfNumericCategoricalVarAreIncluded(variateList, variatesCheckboxState));
	}

	@Test
	public void testCheckIfNonNumericVarAreIncluded() {
		final List<VariableTableItem> variateList = new ArrayList<VariableTableItem>();
		final Map<String, Boolean> variatesCheckboxState = new HashMap<String, Boolean>();
		this.createVariateListWithStateTestData(variateList, variatesCheckboxState);
		Assert.assertFalse("Non-numeric variates cannot be included",
				this.openSelectDatasetForExportAction.checkIfNonNumericVarAreIncluded(variateList, variatesCheckboxState));
	}

	@Test
	public void testPopulateAnalysisName() {

		final BreedingViewInput breedingViewInput = new BreedingViewInput();
		this.openSelectDatasetForExportAction.populateAnalysisName(breedingViewInput, DATASET_NAME);
		Assert.assertTrue(
				breedingViewInput.getBreedingViewAnalysisName().contains("SSA analysis of " + SANITIZED_DATASET_NAME + "  (run at "));
	}

	@Test
	public void testPopulateProjectNameAndFilePaths() {

		final BreedingViewInput breedingViewInput = new BreedingViewInput();
		this.openSelectDatasetForExportAction.populateProjectNameAndFilePaths(breedingViewInput, this.project, INPUT_DIRECTORY);

		Assert.assertEquals(this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME,
				breedingViewInput.getBreedingViewProjectName());
		Assert.assertTrue("XML has valid file name format", FileNameGenerator.isValidFileNameFormat(breedingViewInput.getDestXMLFilePath(), FileNameGenerator.XML_DATE_TIME_PATTERN));
		Assert.assertTrue("CSV has valid file name format", FileNameGenerator.isValidFileNameFormat(breedingViewInput.getSourceXLSFilePath(), FileNameGenerator.CSV_DATE_TIME_PATTERN));
	}

	@Test
	public void testPopulateProjectNameAndFilePathsForServerAppConfig() {

		final BreedingViewInput breedingViewInput = new BreedingViewInput();
		this.openSelectDatasetForExportAction.populateProjectNameAndFilePaths(breedingViewInput, this.project, INPUT_DIRECTORY);

		Assert.assertEquals(this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME,
				breedingViewInput.getBreedingViewProjectName());
		Assert.assertEquals(FileNameGenerator.generateFileName(INPUT_DIRECTORY + File.separator + this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME ,"xml"),
				breedingViewInput.getDestXMLFilePath());
		Assert.assertEquals(FileNameGenerator.generateFileName(this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME,"csv"),
				breedingViewInput.getSourceXLSFilePath());
	}

	@Test
	public void testButtonClick() {
		this.openSelectDatasetForExportAction.buttonClick(this.clickEvent);

		Mockito.verify(this.workbenchDataManager).getToolWithName(ToolName.BREEDING_VIEW.getName());
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);
		Mockito.verify(this.studyDataManager).findOneDataSetByType(Matchers.eq(STUDY_ID), Matchers.anyInt());

		Assert.assertTrue(this.componentCaptor.getValue() instanceof SingleSiteAnalysisDetailsPanel);
		final SingleSiteAnalysisDetailsPanel ssaDetailsPanel = (SingleSiteAnalysisDetailsPanel) this.componentCaptor.getValue();
		Assert.assertEquals(this.bvTool, ssaDetailsPanel.getTool());
		Assert.assertEquals(this.project, ssaDetailsPanel.getProject());
		Assert.assertEquals(this.singleSiteAnalysisPanel, ssaDetailsPanel.getSelectDatasetForBreedingViewPanel());
		Assert.assertEquals(this.factorVariableTypes, ssaDetailsPanel.getFactorsInDataset());
		Assert.assertEquals(this.trialVariableTypes, ssaDetailsPanel.getTrialVariablesInDataset());

		final BreedingViewInput bvInput = ssaDetailsPanel.getBreedingViewInput();
		Assert.assertEquals(this.project, bvInput.getProject());
		Assert.assertEquals(STUDY_ID, bvInput.getStudyId().intValue());
		Assert.assertEquals(DATASET_ID, bvInput.getDatasetId());
		Assert.assertEquals(DATASET_NAME, bvInput.getDatasetName());
		Assert.assertEquals(STUDY_NAME, bvInput.getDatasetSource());
		Assert.assertEquals(OBJECTIVE, bvInput.getObjective());
		Assert.assertEquals(DESCRIPTION, bvInput.getDescription());
		Assert.assertEquals(BV_VERSION, bvInput.getVersion());
		Assert.assertEquals(ProjectType.FIELD_TRIAL.getName(), bvInput.getProjectType());
		Assert.assertEquals(0, bvInput.getOutputDatasetId().intValue());
		Assert.assertEquals(FileNameGenerator.generateFileName(INPUT_DIRECTORY + File.separator + this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME,"xml"),
				bvInput.getDestXMLFilePath());
		Assert.assertEquals(FileNameGenerator.generateFileName(this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME, "csv"),
				bvInput.getSourceXLSFilePath());
		Assert.assertEquals( FileNameGenerator.generateFileName(this.project.getProjectName() + "_99_" + SANITIZED_DATASET_NAME ,"csv"),
				bvInput.getSourceXLSFilePath());
		Assert.assertTrue(bvInput.getBreedingViewAnalysisName().contains("SSA analysis of " + SANITIZED_DATASET_NAME + "  (run at "));
		Assert.assertTrue(bvInput.getVariatesSelectionMap().get("Variable1"));
		Assert.assertTrue(bvInput.getCovariatesSelectionMap().get("Variable2"));

	}

	@Test
	public void testValidateInputNoSelectedStudy() {

		when(this.singleSiteAnalysisPanel.getCurrentStudy()).thenReturn(null);
		Assert.assertFalse(this.openSelectDatasetForExportAction.validateInput(this.clickEvent, STUDY_ID,DATASET_ID, DATASET_NAME));
	}

	@Test
	public void testValidateInputNoSelectedDataset() {
		Assert.assertFalse(this.openSelectDatasetForExportAction.validateInput(this.clickEvent, STUDY_ID, null, null));
	}

	@Test
	public void testValidateVariateTableIncludesNonNumericVariables() {

		final List<VariableTableItem> variateList = new ArrayList<VariableTableItem>();
		final Map<String, Boolean> variatesSelectionMap = new HashMap<String, Boolean>();

		final VariableTableItem trait = new VariableTableItem();
		trait.setActive(true);
		trait.setNumericCategoricalVariate(true);
		trait.setName("SomeTrait1");
		trait.setNonNumeric(true);
		variateList.add(trait);
		variatesSelectionMap.put(trait.getName(), trait.getActive());

		when(this.singleSiteAnalysisPanel.getVariateList()).thenReturn(variateList);
		when(this.singleSiteAnalysisPanel.getVariatesSelectionMap()).thenReturn(variatesSelectionMap);
		when(this.singleSiteAnalysisPanel.getCovariatesSelectionMap()).thenReturn(new HashMap<String, Boolean>());

		Assert.assertFalse(this.openSelectDatasetForExportAction.validateInput(this.clickEvent, STUDY_ID,DATASET_ID, DATASET_NAME));
		Mockito.verify(this.messageSource).getMessage(Message.INVALID_INPUT);
		Mockito.verify(this.messageSource).getMessage(Message.SSA_NON_NUMERIC_CATEGORICAL_VAR_ERROR);
	}

	@Test
	public void testValidateCovariateTableIncludesNonNumericVariables() {

		final List<VariableTableItem> variateList = new ArrayList<VariableTableItem>();
		final Map<String, Boolean> covariatesSelectionMap = new HashMap<String, Boolean>();

		final VariableTableItem covariate = new VariableTableItem();
		covariate.setActive(true);
		covariate.setNumericCategoricalVariate(true);
		covariate.setName("SomeTrait1");
		covariate.setNonNumeric(true);
		variateList.add(covariate);
		covariatesSelectionMap.put(covariate.getName(), covariate.getActive());

		when(this.singleSiteAnalysisPanel.getVariateList()).thenReturn(variateList);
		when(this.singleSiteAnalysisPanel.getVariatesSelectionMap()).thenReturn(new HashMap<String, Boolean>());
		when(this.singleSiteAnalysisPanel.getCovariatesSelectionMap()).thenReturn(covariatesSelectionMap);

		Assert.assertFalse(this.openSelectDatasetForExportAction.validateInput(this.clickEvent, STUDY_ID,DATASET_ID, DATASET_NAME));
		Mockito.verify(this.messageSource).getMessage(Message.INVALID_INPUT);
		Mockito.verify(this.messageSource).getMessage(Message.SSA_NON_NUMERIC_CATEGORICAL_VAR_ERROR);
	}

	private void createVariateListWithStateTestData(final List<VariableTableItem> variateList,
			final Map<String, Boolean> variatesSelectionMap) {

		final VariableTableItem variableTableItem = new VariableTableItem();
		variableTableItem.setActive(true);
		variableTableItem.setNumericCategoricalVariate(true);
		variableTableItem.setName("SomeTrait1");
		variateList.add(variableTableItem);
		variatesSelectionMap.put(variableTableItem.getName(), variableTableItem.getActive());

	}

	@Test
	public void testValidateVariateTableIncludesNumericCategoricalVariables() {

		final List<VariableTableItem> variateList = new ArrayList<VariableTableItem>();
		final Map<String, Boolean> variatesSelectionMap = new HashMap<String, Boolean>();

		final VariableTableItem trait = new VariableTableItem();
		trait.setActive(true);
		trait.setNumericCategoricalVariate(true);
		trait.setName("SomeTrait1");
		trait.setNonNumeric(false);
		trait.setNumericCategoricalVariate(true);
		variateList.add(trait);
		variatesSelectionMap.put(trait.getName(), trait.getActive());

		when(this.singleSiteAnalysisPanel.getVariateList()).thenReturn(variateList);
		when(this.singleSiteAnalysisPanel.getVariatesSelectionMap()).thenReturn(variatesSelectionMap);
		when(this.singleSiteAnalysisPanel.getCovariatesSelectionMap()).thenReturn(new HashMap<String, Boolean>());

		Assert.assertTrue(this.openSelectDatasetForExportAction.validateInput(this.clickEvent, STUDY_ID,DATASET_ID, DATASET_NAME));
		Mockito.verify(this.messageSource).getMessage(Message.WARNING);
		Mockito.verify(this.messageSource).getMessage(Message.SSA_NUMERIC_CATEGORICAL_VAR_WARNING);
	}

	@Test
	public void testValidateVariateTableSuccess() {

		final List<VariableTableItem> variateList = new ArrayList<VariableTableItem>();
		final Map<String, Boolean> variatesSelectionMap = new HashMap<String, Boolean>();

		final VariableTableItem trait = new VariableTableItem();
		trait.setActive(true);
		trait.setNumericCategoricalVariate(true);
		trait.setName("SomeTrait1");
		trait.setNonNumeric(false);
		trait.setNumericCategoricalVariate(false);
		variateList.add(trait);
		variatesSelectionMap.put(trait.getName(), trait.getActive());

		when(this.singleSiteAnalysisPanel.getVariateList()).thenReturn(variateList);
		when(this.singleSiteAnalysisPanel.getVariatesSelectionMap()).thenReturn(variatesSelectionMap);
		when(this.singleSiteAnalysisPanel.getCovariatesSelectionMap()).thenReturn(new HashMap<String, Boolean>());

		Assert.assertTrue(this.openSelectDatasetForExportAction.validateInput(this.clickEvent, STUDY_ID,DATASET_ID, DATASET_NAME));
		Mockito.verifyZeroInteractions(this.messageSource);

	}
}
