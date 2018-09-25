package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.util.StudyUtil;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnit44Runner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnit44Runner.class)
public class SingleSiteAnalysisPanelTest {

	private DataSet currentDataset;
	private Study currentStudy;
	private StudyUtil studyUtil;

	@Mock
	private VariableTableComponent germplasmDescriptorsComponent;

	@Mock
	private VariableTableComponent variatesTableComponent;

	@Mock
	private VariableTableComponent covariatesTableComponent;

	@Mock
	private VerticalLayout studyDetailsContainer;

	@Mock
	private VerticalLayout germplasmDescriptorTableLayout;

	@Mock
	private VerticalLayout traitTableLayout;

	@Mock
	private VerticalLayout covariateTableLayout;

	private SingleSiteAnalysisPanel singleSiteAnalysisPanel;

	@Before
	public void setup() throws Exception {
		this.studyUtil = StudyUtil.getInstance();
		this.currentStudy = this.studyUtil.createStudyTestData();
		this.currentDataset = this.studyUtil.createDatasetTestData();
		final Project currentProject = new Project();

		final ManagerFactoryProvider managerFactoryProvider = Mockito.mock(ManagerFactoryProvider.class);
		final StudyDataManager studyDataManager = Mockito.mock(StudyDataManager.class);
		final ManagerFactory managerFactory = Mockito.mock(ManagerFactory.class);

		Mockito.when(managerFactoryProvider.getManagerFactoryForProject(currentProject)).thenReturn(managerFactory);
		Mockito.when(managerFactory.getNewStudyDataManager()).thenReturn(studyDataManager);
		Mockito.when(studyDataManager.getDataSet(StudyUtil.DATASET_ID)).thenReturn(this.currentDataset);
		Mockito.when(studyDataManager.getStudy(this.currentDataset.getStudyId())).thenReturn(this.currentStudy);

		this.singleSiteAnalysisPanel = new SingleSiteAnalysisPanel(null);
		this.singleSiteAnalysisPanel.setStudyDataManager(studyDataManager);
		this.singleSiteAnalysisPanel.setCurrentProject(currentProject);
		this.singleSiteAnalysisPanel.setGermplasmDescriptorsComponent(germplasmDescriptorsComponent);
		this.singleSiteAnalysisPanel.setVariatesTableComponent(variatesTableComponent);
		this.singleSiteAnalysisPanel.setCovariatesTableComponent(covariatesTableComponent);
		this.singleSiteAnalysisPanel.setStudyDetailsContainer(studyDetailsContainer);
		this.singleSiteAnalysisPanel.setGermplasmDescriptorTableLayout(germplasmDescriptorTableLayout);
		this.singleSiteAnalysisPanel.setTraitTableLayout(traitTableLayout);
		this.singleSiteAnalysisPanel.setCovariateTableLayout(covariateTableLayout);
	}

	@Test
	public void testShowStudyDetails() {

		this.singleSiteAnalysisPanel.showStudyDetails(StudyUtil.DATASET_ID);

		// check if the data set details are correct

		Assert.assertNotNull("A study should be selected.", this.singleSiteAnalysisPanel.getCurrentStudy());
		Assert.assertNotNull("The selected dataset should have a name", this.singleSiteAnalysisPanel.getCurrentDatasetName());
		Assert.assertNotNull("The selected dataset should have an id", this.singleSiteAnalysisPanel.getCurrentDataSetId());

		Assert.assertEquals("The dataset name should be " + this.currentDataset.getName(), this.currentDataset.getName(),
				this.singleSiteAnalysisPanel.getCurrentDatasetName());
		Assert.assertEquals("The dataset id should be " + this.currentDataset.getId(), Integer.toString(this.currentDataset.getId()),
				Integer.toString(this.singleSiteAnalysisPanel.getCurrentDataSetId()));

		Mockito.verify(germplasmDescriptorsComponent).loadData(Mockito.anyList());
		Mockito.verify(variatesTableComponent).loadData(Mockito.anyList());
		Mockito.verify(covariatesTableComponent).loadData(Mockito.anyList());

	}

	@Test
	public void testFilterDatasetAndStudyVariables() {

		final DMSVariableType datasetVariable =
				DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(TermId.DATASET_TYPE);
		datasetVariable.getStandardVariable().setPhenotypicType(PhenotypicType.DATASET);
		final DMSVariableType studyVariable =
				DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(TermId.STUDY_DATA_TYPE);
		studyVariable.getStandardVariable().setPhenotypicType(PhenotypicType.STUDY);
		final DMSVariableType germplasmVariable =
				DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(TermId.ENTRY_NO);
		germplasmVariable.getStandardVariable().setPhenotypicType(PhenotypicType.GERMPLASM);

		final List<DMSVariableType> variableTypeList = new ArrayList<>();
		variableTypeList.add(datasetVariable);
		variableTypeList.add(studyVariable);
		variableTypeList.add(germplasmVariable);

		final List<DMSVariableType> result = this.singleSiteAnalysisPanel.filterDatasetAndStudyVariables(variableTypeList);

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(germplasmVariable, result.get(0));
	}

	@Test
	public void testReset() {

		this.singleSiteAnalysisPanel.reset();

		Mockito.verify(germplasmDescriptorTableLayout).removeAllComponents();
		Mockito.verify(traitTableLayout).removeAllComponents();
		Mockito.verify(covariateTableLayout).removeAllComponents();
		Mockito.verify(studyDetailsContainer).removeAllComponents();

		Mockito.verify(germplasmDescriptorTableLayout).addComponent(Mockito.any(VariableTableComponent.class));
		Mockito.verify(traitTableLayout).addComponent(Mockito.any(VariableTableComponent.class));
		Mockito.verify(covariateTableLayout).addComponent(Mockito.any(VariableTableComponent.class));
		Mockito.verify(studyDetailsContainer).addComponent(Mockito.any(SingleSiteAnalysisStudyDetailsComponent.class));

		Mockito.verify(germplasmDescriptorTableLayout, Mockito.never()).addComponent(this.germplasmDescriptorsComponent);
		Mockito.verify(traitTableLayout, Mockito.never()).addComponent(this.variatesTableComponent);
		Mockito.verify(covariateTableLayout, Mockito.never()).addComponent(this.covariatesTableComponent);
	}

	@Test
	public void testInitalizeTableComponents() {

		this.singleSiteAnalysisPanel.initalizeTableComponents();

		Assert.assertNotSame(this.germplasmDescriptorsComponent, this.singleSiteAnalysisPanel.getGermplasmDescriptorsComponent());
		Assert.assertNotSame(this.variatesTableComponent, this.singleSiteAnalysisPanel.getVariatesTableComponent());
		Assert.assertNotSame(this.covariatesTableComponent, this.singleSiteAnalysisPanel.getCovariatesTableComponent());
		Assert.assertTrue(this.singleSiteAnalysisPanel.getVariatesTableComponent()
				.getSelectionChangedListener() instanceof SingleSiteAnalysisPanel.VariateTableSelectionChangedListener);
		Assert.assertTrue(this.singleSiteAnalysisPanel.getVariatesTableComponent()
				.getSelectAllChangedListener() instanceof SingleSiteAnalysisPanel.VariateTableSelectAllChangedListener);
	}

	@Test
	public void testVariatesTableSelectionChangedSomeItemsAreSelected() {

		final Button btnNext = Mockito.mock(Button.class);
		this.singleSiteAnalysisPanel.setBtnNext(btnNext);
		Mockito.when(this.variatesTableComponent.someItemsAreSelected()).thenReturn(true);
		final VariableTableComponent.SelectionChangedListener listener =
				this.singleSiteAnalysisPanel.new VariateTableSelectionChangedListener();

		final VariableTableItem variableTableItem = new VariableTableItem();
		variableTableItem.setActive(true);
		variableTableItem.setId(1);

		listener.onSelectionChanged(variableTableItem);

		Mockito.verify(this.covariatesTableComponent).toggleCheckbox(variableTableItem.getId(), false, variableTableItem.getActive());
		Mockito.verify(btnNext).setEnabled(true);

	}

	@Test
	public void testVariatesTableSelectionChangedNoItemSelected() {

		final Button btnNext = Mockito.mock(Button.class);
		this.singleSiteAnalysisPanel.setBtnNext(btnNext);
		Mockito.when(this.variatesTableComponent.someItemsAreSelected()).thenReturn(false);
		final VariableTableComponent.SelectionChangedListener listener =
				this.singleSiteAnalysisPanel.new VariateTableSelectionChangedListener();

		final VariableTableItem variableTableItem = new VariableTableItem();
		variableTableItem.setActive(false);
		variableTableItem.setId(1);

		listener.onSelectionChanged(variableTableItem);

		Mockito.verify(this.covariatesTableComponent).toggleCheckbox(variableTableItem.getId(), false, variableTableItem.getActive());
		Mockito.verify(btnNext).setEnabled(false);

	}

	@Test
	public void testCovariatesTableSelectionChangedCovariateItemIsUnchecked() {

		final Button btnNext = Mockito.mock(Button.class);
		this.singleSiteAnalysisPanel.setBtnNext(btnNext);
		Mockito.when(this.variatesTableComponent.someItemsAreSelected()).thenReturn(true);
		final VariableTableComponent.SelectionChangedListener listener =
				this.singleSiteAnalysisPanel.new CovariateTableSelectionChangedListener();

		final VariableTableItem variableTableItem = new VariableTableItem();
		variableTableItem.setActive(false);
		variableTableItem.setId(1);

		listener.onSelectionChanged(variableTableItem);

		Mockito.verify(this.variatesTableComponent).toggleCheckbox(variableTableItem.getId(), true, false);
		Mockito.verify(btnNext).setEnabled(true);

	}

	@Test
	public void testCovariatesTableSelectionChangedCovariateItemIsChecked() {

		final Button btnNext = Mockito.mock(Button.class);
		this.singleSiteAnalysisPanel.setBtnNext(btnNext);
		Mockito.when(this.variatesTableComponent.someItemsAreSelected()).thenReturn(true);
		final VariableTableComponent.SelectionChangedListener listener =
				this.singleSiteAnalysisPanel.new CovariateTableSelectionChangedListener();

		final VariableTableItem variableTableItem = new VariableTableItem();
		variableTableItem.setActive(true);
		variableTableItem.setId(1);

		listener.onSelectionChanged(variableTableItem);

		Mockito.verify(this.variatesTableComponent, Mockito.times(0)).toggleCheckbox(variableTableItem.getId(), true, false);
		Mockito.verify(btnNext).setEnabled(true);

	}

	@Test
	public void testVariatesTableSelectAllChangedAllItemsAreSelected() {

		final Button btnNext = Mockito.mock(Button.class);
		this.singleSiteAnalysisPanel.setBtnNext(btnNext);
		Mockito.when(this.variatesTableComponent.someItemsAreSelected()).thenReturn(true);
		final VariableTableComponent.SelectAllChangedListener listener =
				this.singleSiteAnalysisPanel.new VariateTableSelectAllChangedListener();

		listener.onSelectionChanged(true);

		Mockito.verify(btnNext).setEnabled(true);

	}

	@Test
	public void testVariatesTableSelectAllChangedNoItemSelected() {

		final Button btnNext = Mockito.mock(Button.class);
		this.singleSiteAnalysisPanel.setBtnNext(btnNext);
		Mockito.when(this.variatesTableComponent.someItemsAreSelected()).thenReturn(false);
		final VariableTableComponent.SelectAllChangedListener listener =
				this.singleSiteAnalysisPanel.new VariateTableSelectAllChangedListener();

		listener.onSelectionChanged(false);

		Mockito.verify(btnNext).setEnabled(false);
		Mockito.verify(covariatesTableComponent).resetAllCheckbox();

	}

}
