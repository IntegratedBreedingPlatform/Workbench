
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SingleSiteAnalysisPanelTest {

	private SingleSiteAnalysisPanel singleSiteAnalysisPanel;
	private DataSet currentDataset;
	private Study currentStudy;
	private StudyUtil studyUtil;

	@Before
	public void setup() throws Exception {
		this.studyUtil = StudyUtil.getInstance();
		this.currentStudy = this.studyUtil.createStudyTestData();
		this.currentDataset = this.studyUtil.createDatasetTestData();
		Project currentProject = new Project();

		ManagerFactoryProvider managerFactoryProvider = Mockito.mock(ManagerFactoryProvider.class);
		StudyDataManager studyDataManager = Mockito.mock(StudyDataManager.class);
		ManagerFactory managerFactory = Mockito.mock(ManagerFactory.class);

		Mockito.when(managerFactoryProvider.getManagerFactoryForProject(currentProject)).thenReturn(managerFactory);
		Mockito.when(managerFactory.getNewStudyDataManager()).thenReturn(studyDataManager);
		Mockito.when(studyDataManager.getDataSet(StudyUtil.DATASET_ID)).thenReturn(this.currentDataset);
		Mockito.when(studyDataManager.getStudy(this.currentDataset.getStudyId())).thenReturn(this.currentStudy);

		this.singleSiteAnalysisPanel = new SingleSiteAnalysisPanel(currentProject, null);
		this.singleSiteAnalysisPanel.setStudyDataManager(studyDataManager);
		this.singleSiteAnalysisPanel.setManagerFactoryProvider(managerFactoryProvider);
		this.singleSiteAnalysisPanel.instantiateComponents();
	}

	@Test
	public void testShowDatasetVariatesDetails() {

		this.singleSiteAnalysisPanel.showDatasetVariatesDetails(StudyUtil.DATASET_ID);

		// check if the data set details are correct

		Assert.assertNotNull("A study should be selected.", this.singleSiteAnalysisPanel.getCurrentStudy());
		Assert.assertNotNull("The selected dataset should have a name", this.singleSiteAnalysisPanel.getCurrentDatasetName());
		Assert.assertNotNull("The selected dataset should have an id", this.singleSiteAnalysisPanel.getCurrentDataSetId());

		Assert.assertEquals("The dataset name should be " + this.currentDataset.getName(), this.currentDataset.getName(),
				this.singleSiteAnalysisPanel.getCurrentDatasetName());
		Assert.assertEquals("The dataset id should be " + this.currentDataset.getId(), Integer.toString(this.currentDataset.getId()),
				Integer.toString(this.singleSiteAnalysisPanel.getCurrentDataSetId()));

		List<FactorModel> factors = this.singleSiteAnalysisPanel.getFactorList();
		Assert.assertNotNull("The dataset should contain factors", factors);

		VariableTypeList currentFactors = this.currentDataset.getVariableTypes().getFactors();
		this.removeDatasetTypes(currentFactors);

		Assert.assertEquals("The dataset should have " + currentFactors.size() + " factors", currentFactors.size(), factors.size());

		List<VariateModel> variates = this.singleSiteAnalysisPanel.getVariateList();
		VariableTypeList currentVariates = this.currentDataset.getVariableTypes().getVariates();
		Assert.assertNotNull("The dataset should contain variates", currentVariates);
		Assert.assertEquals("The dataset should have " + currentVariates.size() + " variates", currentVariates.size(), variates.size());

	}

	@Test
	public void testShowDatasetVariatesDetailsWithSelectedVariatesByDefault() {
		this.singleSiteAnalysisPanel.showDatasetVariatesDetails(StudyUtil.DATASET_ID);

		Map<String, Boolean> variatesCheckboxState = this.singleSiteAnalysisPanel.getVariatesCheckboxState();
		List<VariateModel> variates = this.singleSiteAnalysisPanel.getVariateList();

		for (VariateModel vm : variates) {
			boolean isSelected = variatesCheckboxState.get(vm.getName());
			if (vm.getActive()) {
				Assert.assertTrue("Active variates are selected by default", isSelected);
			}
			if (vm.isNumericCategoricalVariate()) {
				Assert.assertTrue("Numeric categorical variates are selected by default", isSelected);
			}
			if (vm.isNonNumeric()) {
				Assert.assertFalse("Non-numeric variates are not selected by default", isSelected);
			}
		}
	}

	private void removeDatasetTypes(VariableTypeList currentFactors) {
		Iterator<VariableType> variableTypeIterator = currentFactors.getVariableTypes().iterator();
		while (variableTypeIterator.hasNext()) {
			VariableType variableType = variableTypeIterator.next();
			if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET) {
				variableTypeIterator.remove();
			}
		}
	}

	@Test
	public void testTransformVariableTypeToVariateModel() {
		List<VariableType> variates = this.currentDataset.getVariableTypes().getVariates().getVariableTypes();
		for (VariableType variate : variates) {
			VariateModel vm = this.singleSiteAnalysisPanel.transformVariableTypeToVariateModel(variate);
			Assert.assertEquals(Integer.toString(variate.getRank()), Integer.toString(vm.getId()));
			Assert.assertEquals(variate.getLocalName(), vm.getName());
			Assert.assertEquals(variate.getLocalDescription(), vm.getDescription());
			Assert.assertEquals(variate.getStandardVariable().getScale().getName(), vm.getScname());
			Assert.assertEquals(Integer.toString(variate.getStandardVariable().getScale().getId()), Integer.toString(vm.getScaleid()));
			Assert.assertEquals(variate.getStandardVariable().getMethod().getName(), vm.getTmname());
			Assert.assertEquals(Integer.toString(variate.getStandardVariable().getMethod().getId()), Integer.toString(vm.getTmethid()));
			Assert.assertEquals(variate.getStandardVariable().getProperty().getName(), vm.getTrname());
			Assert.assertEquals(Integer.toString(variate.getStandardVariable().getProperty().getId()), Integer.toString(vm.getTraitid()));
			Assert.assertEquals(variate.getStandardVariable().getDataType().getName(), vm.getDatatype());
			if (variate.getStandardVariable().isNumeric()) {
				Assert.assertTrue(vm.getActive());
				if (variate.getStandardVariable().isNumericCategoricalVariate()) {
					Assert.assertTrue(vm.isNumericCategoricalVariate());
				}
			} else {
				Assert.assertTrue(vm.isNonNumeric());
			}
		}

	}
}
