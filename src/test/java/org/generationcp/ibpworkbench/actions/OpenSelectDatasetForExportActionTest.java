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

import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.StudyUtil;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class OpenSelectDatasetForExportActionTest {

	private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;
	private SingleSiteAnalysisPanel singleSiteAnalysisPanel;
	private StudyUtil studyUtil;

	public static final String DATASET_NAME = "TEST\\ /:*?'\"<>|[]{},.?~`!@#$%^&()-=_+-PLOTDATA";
	public static final Integer DATASET_ID = 99;
	public static final String PROJECT_NAME = "PROJECT NAME";
	public static final String INPUT_DIRECTORY = "workspace/input";

	@Before
	public void setup() {

		final Project project = this.createProject();
		final Study study = this.createStudy();

		this.studyUtil = StudyUtil.getInstance();
		this.singleSiteAnalysisPanel = new SingleSiteAnalysisPanel(project, null);
		this.openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(this.singleSiteAnalysisPanel);

		openSelectDatasetForExportAction.setProject(project);
		openSelectDatasetForExportAction.setStudy(study);
		openSelectDatasetForExportAction.setDataSetId(DATASET_ID);
		openSelectDatasetForExportAction.setDatasetName(DATASET_NAME);

	}

	@Test
	public void testCheckIfNumericCategoricalVarAreIncluded() {
		final List<VariateModel> variateList = new ArrayList<VariateModel>();
		final Map<String, Boolean> variatesCheckboxState = new HashMap<String, Boolean>();
		this.createVariateListWithStateTestData(variateList, variatesCheckboxState);
		Assert.assertTrue("Numerical categorical variates if selected can be included",
				this.openSelectDatasetForExportAction.checkIfNumericCategoricalVarAreIncluded(variateList, variatesCheckboxState));
	}

	@Test
	public void testCheckIfNonNumericVarAreIncluded() {
		final List<VariateModel> variateList = new ArrayList<VariateModel>();
		final Map<String, Boolean> variatesCheckboxState = new HashMap<String, Boolean>();
		this.createVariateListWithStateTestData(variateList, variatesCheckboxState);
		Assert.assertFalse("Non-numeric variates cannot be included",
				this.openSelectDatasetForExportAction.checkIfNonNumericVarAreIncluded(variateList, variatesCheckboxState));
	}

	@Test
	public void testPopulateAnalysisName() {

		final BreedingViewInput breedingViewInput = new BreedingViewInput();
		this.openSelectDatasetForExportAction.populateAnalysisName(breedingViewInput, DATASET_NAME);
		Assert.assertTrue(breedingViewInput.getBreedingViewAnalysisName().contains("SSA analysis of TEST_ _-_-PLOTDATA  (run at "));

	}

	@Test
	public void testPopulateProjectNameAndFilePaths() {

		final BreedingViewInput breedingViewInput = new BreedingViewInput();
		this.openSelectDatasetForExportAction.populateProjectNameAndFilePaths(breedingViewInput, this.createProject(), INPUT_DIRECTORY);

		Assert.assertEquals("PROJECT NAME_99_TEST_ _-_-PLOTDATA", breedingViewInput.getBreedingViewProjectName());
		Assert.assertEquals(INPUT_DIRECTORY + "/PROJECT NAME_99_TEST_ _-_-PLOTDATA.xml", breedingViewInput.getDestXMLFilePath());
		Assert.assertEquals(INPUT_DIRECTORY + "/PROJECT NAME_99_TEST_ _-_-PLOTDATA.csv", breedingViewInput.getSourceXLSFilePath());

	}

	private Project createProject() {

		final Project project = new Project();
		project.setProjectName(PROJECT_NAME);
		return project;

	}

	private Study createStudy() {

		final Study study = new Study();
		return study;
	}

	private void createVariateListWithStateTestData(final List<VariateModel> variateList, final Map<String, Boolean> variatesCheckboxState) {
		final VariableTypeList variates = this.studyUtil.createVariateVarsTestData();
		for (final DMSVariableType variate : variates.getVariates().getVariableTypes()) {
			final VariateModel vm = this.singleSiteAnalysisPanel.transformVariableTypeToVariateModel(variate);
			variateList.add(vm);
			variatesCheckboxState.put(vm.getName(), vm.getActive());
		}
	}

}
