/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.actions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.util.StudyUtil;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;

public class OpenSelectDatasetForExportActionTest {
    
	private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;
	private SingleSiteAnalysisPanel singleSiteAnalysisPanel; 
	private StudyUtil studyUtil;
	
	@Before
	public void setup() {
		studyUtil = StudyUtil.getInstance();
		Project currentProject = new Project();
		singleSiteAnalysisPanel = new SingleSiteAnalysisPanel(currentProject, null);
		openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(singleSiteAnalysisPanel);
	}
	
	@Test
	public void testCheckIfNumericCategoricalVarAreIncluded() {
		List<VariateModel> variateList = new ArrayList<VariateModel>();
		Map<String, Boolean> variatesCheckboxState = new HashMap<String, Boolean>();
		createVariateListWithStateTestData(variateList,variatesCheckboxState);
		assertTrue("Numerical categorical variates if selected can be included",
				openSelectDatasetForExportAction.checkIfNumericCategoricalVarAreIncluded(
				variateList,variatesCheckboxState));
	}

    private void createVariateListWithStateTestData(
			List<VariateModel> variateList,
			Map<String, Boolean> variatesCheckboxState) {
    	VariableTypeList variates = studyUtil.createVariateVarsTestData();
		for (VariableType variate : variates.getVariates().getVariableTypes()) {
			 VariateModel vm = singleSiteAnalysisPanel.transformVariableTypeToVariateModel(variate);
			 variateList.add(vm);
			 variatesCheckboxState.put(vm.getName(), vm.getActive());
		}
	}

	@Test
	public void testCheckIfNonNumericVarAreIncluded() {
		List<VariateModel> variateList = new ArrayList<VariateModel>();
		Map<String, Boolean> variatesCheckboxState = new HashMap<String, Boolean>();
		createVariateListWithStateTestData(variateList,variatesCheckboxState);
		assertFalse("Non-numeric variates cannot be included",
				openSelectDatasetForExportAction.checkIfNonNumericVarAreIncluded(
				variateList,variatesCheckboxState));
	}
}
