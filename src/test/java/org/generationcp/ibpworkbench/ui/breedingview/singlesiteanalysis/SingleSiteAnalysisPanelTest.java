
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.util.StudyUtil;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

		this.singleSiteAnalysisPanel = new SingleSiteAnalysisPanel(currentProject);
		this.singleSiteAnalysisPanel.setStudyDataManager(studyDataManager);
		this.singleSiteAnalysisPanel.instantiateComponents();
		this.singleSiteAnalysisPanel.layoutComponents();
	}

	@Test
	public void testShowStudyDetails() {


		final VariableTableComponent germplasmDescriptorTableComponent = Mockito.mock(VariableTableComponent.class);
		final VariableTableComponent variatesTableComponent = Mockito.mock(VariableTableComponent.class);
		final VariableTableComponent covariatesTableCompoment = Mockito.mock(VariableTableComponent.class);

		this.singleSiteAnalysisPanel.setGermplasmDescriptorsComponent(germplasmDescriptorTableComponent);
		this.singleSiteAnalysisPanel.setVariatesTableComponent(variatesTableComponent);
		this.singleSiteAnalysisPanel.setCovariatesTableComponent(covariatesTableCompoment);

		this.singleSiteAnalysisPanel.showStudyDetails(StudyUtil.DATASET_ID);

		// check if the data set details are correct

		Assert.assertNotNull("A study should be selected.", this.singleSiteAnalysisPanel.getCurrentStudy());
		Assert.assertNotNull("The selected dataset should have a name", this.singleSiteAnalysisPanel.getCurrentDatasetName());
		Assert.assertNotNull("The selected dataset should have an id", this.singleSiteAnalysisPanel.getCurrentDataSetId());

		Assert.assertEquals("The dataset name should be " + this.currentDataset.getName(), this.currentDataset.getName(),
				this.singleSiteAnalysisPanel.getCurrentDatasetName());
		Assert.assertEquals("The dataset id should be " + this.currentDataset.getId(), Integer.toString(this.currentDataset.getId()),
				Integer.toString(this.singleSiteAnalysisPanel.getCurrentDataSetId()));

		Mockito.verify(germplasmDescriptorTableComponent).loadData(Mockito.anyList());
		Mockito.verify(variatesTableComponent).loadData(Mockito.anyList());
		Mockito.verify(covariatesTableCompoment).loadData(Mockito.anyList());


	}

	private void removeDatasetTypes(VariableTypeList currentFactors) {
		Iterator<DMSVariableType> variableTypeIterator = currentFactors.getVariableTypes().iterator();
		while (variableTypeIterator.hasNext()) {
			DMSVariableType variableType = variableTypeIterator.next();
			if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET) {
				variableTypeIterator.remove();
			}
		}
	}

}
