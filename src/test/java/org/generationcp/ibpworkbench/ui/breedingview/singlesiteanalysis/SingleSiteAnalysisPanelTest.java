package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.util.StudyUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;

public class SingleSiteAnalysisPanelTest {
	
	private SingleSiteAnalysisPanel singleSiteAnalysisPanel;
	private DataSet currentDataset;
	private Study currentStudy;
	private StudyUtil studyUtil;
	
	@Before
	public void setup() throws Exception {
		studyUtil = StudyUtil.getInstance();
		currentStudy = studyUtil.createStudyTestData();
		currentDataset = studyUtil.createDatasetTestData();
		Project currentProject = new Project();
		
		ManagerFactoryProvider managerFactoryProvider = mock(ManagerFactoryProvider.class);
		StudyDataManager studyDataManager = mock(StudyDataManager.class);
		ManagerFactory managerFactory = mock(ManagerFactory.class);
		
		when(managerFactoryProvider.getManagerFactoryForProject(currentProject)).thenReturn(managerFactory);
		when(managerFactory.getNewStudyDataManager()).thenReturn(studyDataManager);
		when(studyDataManager.getDataSet(StudyUtil.DATASET_ID)).thenReturn(currentDataset);
		when(studyDataManager.getStudy(currentDataset.getStudyId())).thenReturn(currentStudy);
		
		singleSiteAnalysisPanel = new SingleSiteAnalysisPanel(currentProject, null);
		singleSiteAnalysisPanel.setManagerFactory(managerFactory);
		singleSiteAnalysisPanel.setStudyDataManager(studyDataManager);
		singleSiteAnalysisPanel.setManagerFactoryProvider(managerFactoryProvider);
		singleSiteAnalysisPanel.instantiateComponents();
	}
	
	
	@Test
	public void testShowDatasetVariatesDetails() {
		
		singleSiteAnalysisPanel.showDatasetVariatesDetails(StudyUtil.DATASET_ID);
		
		//check if the data set details are correct
		
		assertNotNull("A study should be selected.",singleSiteAnalysisPanel.getCurrentStudy());
		assertNotNull("The selected dataset should have a name",singleSiteAnalysisPanel.getCurrentDatasetName());
		assertNotNull("The selected dataset should have an id",singleSiteAnalysisPanel.getCurrentDataSetId());
		
		assertEquals("The dataset name should be "+currentDataset.getName(),
				currentDataset.getName(),
				singleSiteAnalysisPanel.getCurrentDatasetName());
		assertEquals("The dataset id should be "+currentDataset.getId(),
				Integer.toString(currentDataset.getId()),
				Integer.toString(singleSiteAnalysisPanel.getCurrentDataSetId()));
		
		List<FactorModel> factors = singleSiteAnalysisPanel.getFactorList();
		assertNotNull("The dataset should contain factors",factors);
		
		VariableTypeList currentFactors = currentDataset.getVariableTypes().getFactors();
		removeDatasetTypes(currentFactors);
		
		assertEquals("The dataset should have "+currentFactors.size()+" factors",
				currentFactors.size(),factors.size());
		
		List<VariateModel> variates = singleSiteAnalysisPanel.getVariateList();
		VariableTypeList currentVariates = currentDataset.getVariableTypes().getVariates();
		assertNotNull("The dataset should contain variates",currentVariates);
		assertEquals("The dataset should have "+currentVariates.size()+" variates",
				currentVariates.size(),variates.size());
		
	}
	
	@Test
	public void testShowDatasetVariatesDetailsWithSelectedVariatesByDefault() {
		singleSiteAnalysisPanel.showDatasetVariatesDetails(StudyUtil.DATASET_ID);
		
		Map<String, Boolean> variatesCheckboxState = singleSiteAnalysisPanel.getVariatesCheckboxState();
		List<VariateModel> variates = singleSiteAnalysisPanel.getVariateList();
		
		for (VariateModel vm : variates) {
        	boolean isSelected = variatesCheckboxState.get(vm.getName());
        	if(vm.getActive()) {
        		assertTrue("Active variates are selected by default",isSelected);
        	}
        	if(vm.isNumericCategoricalVariate()) {
        		assertTrue("Numeric categorical variates are selected by default",isSelected);
        	}
        	if (vm.isNonNumeric()){
        		assertFalse("Non-numeric variates are not selected by default",isSelected);
        	}
		}
	}


	private void removeDatasetTypes(VariableTypeList currentFactors) {
		Iterator<VariableType> variableTypeIterator = currentFactors.getVariableTypes().iterator();
		while (variableTypeIterator.hasNext()) {
			VariableType variableType = variableTypeIterator.next();
			if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET ) {
				variableTypeIterator.remove();
			}
		}
	}
	
	@Test
	public void testTransformVariableTypeToVariateModel() {
		List<VariableType> variates = currentDataset.getVariableTypes().getVariates().getVariableTypes();
		for (VariableType variate : variates) {
			VariateModel vm = singleSiteAnalysisPanel.transformVariableTypeToVariateModel(variate);
			assertEquals(Integer.toString(variate.getRank()),Integer.toString(vm.getId()));
			assertEquals(variate.getLocalName(),vm.getName());
			assertEquals(variate.getLocalDescription(),vm.getDescription());
			assertEquals(variate.getStandardVariable().getScale().getName(),vm.getScname());
			assertEquals(Integer.toString(variate.getStandardVariable().getScale().getId()),
					Integer.toString(vm.getScaleid()));
			assertEquals(variate.getStandardVariable().getMethod().getName(),vm.getTmname());
			assertEquals(Integer.toString(variate.getStandardVariable().getMethod().getId()),
					Integer.toString(vm.getTmethid()));
			assertEquals(variate.getStandardVariable().getProperty().getName(),vm.getTrname());
			assertEquals(Integer.toString(variate.getStandardVariable().getProperty().getId()),
					Integer.toString(vm.getTraitid()));
			assertEquals(variate.getStandardVariable().getDataType().getName(),vm.getDatatype());
	    	if (variate.getStandardVariable().isNumeric()){
	    		assertTrue(vm.getActive());
	    		if(variate.getStandardVariable().isNumericCategoricalVariate()) {
	    			assertTrue(vm.isNumericCategoricalVariate());
	    		}
	    	} else {
	    		assertTrue(vm.isNonNumeric());
	    	}
		}
		
	}
}

