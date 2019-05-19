package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.ibpworkbench.util.StudyUtil;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
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

		final StudyDataManager studyDataManager = Mockito.mock(StudyDataManager.class);

		Mockito.when(studyDataManager.getDataSet(StudyUtil.DATASET_ID)).thenReturn(this.currentDataset);
		Mockito.when(studyDataManager.getStudy(this.currentDataset.getStudyId())).thenReturn(this.currentStudy);

		this.singleSiteAnalysisPanel = new SingleSiteAnalysisPanel(null);
		this.singleSiteAnalysisPanel.setStudyDataManager(studyDataManager);
		this.singleSiteAnalysisPanel.setCurrentProject(currentProject);
		this.singleSiteAnalysisPanel.setGermplasmDescriptorsComponent(this.germplasmDescriptorsComponent);
		this.singleSiteAnalysisPanel.setVariatesTableComponent(this.variatesTableComponent);
		this.singleSiteAnalysisPanel.setCovariatesTableComponent(this.covariatesTableComponent);
		this.singleSiteAnalysisPanel.setStudyDetailsContainer(this.studyDetailsContainer);
		this.singleSiteAnalysisPanel.setGermplasmDescriptorTableLayout(this.germplasmDescriptorTableLayout);
		this.singleSiteAnalysisPanel.setTraitTableLayout(this.traitTableLayout);
		this.singleSiteAnalysisPanel.setCovariateTableLayout(this.covariateTableLayout);
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

		Mockito.verify(this.germplasmDescriptorsComponent).loadData(ArgumentMatchers.<List<DMSVariableType>>any());
		Mockito.verify(this.variatesTableComponent).loadData(ArgumentMatchers.<List<DMSVariableType>>any());
		Mockito.verify(this.covariatesTableComponent).loadData(ArgumentMatchers.<List<DMSVariableType>>any());

	}

	@Test
	public void testFilterDatasetAndStudyVariables() {

		final DMSVariableType studyVariable =
			DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(TermId.STUDY_DATA_TYPE);
		studyVariable.getStandardVariable().setPhenotypicType(PhenotypicType.STUDY);
		final DMSVariableType germplasmVariable =
			DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(TermId.ENTRY_NO);
		germplasmVariable.getStandardVariable().setPhenotypicType(PhenotypicType.GERMPLASM);
		final DMSVariableType treatmentVariable =
			DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(TermId.TREATMENT_MEAN);
		treatmentVariable.getStandardVariable().setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		treatmentVariable.setTreatmentLabel("label");

		final List<DMSVariableType> variableTypeList = Arrays.asList(studyVariable, germplasmVariable, treatmentVariable);

		final List<DMSVariableType> result =
			this.singleSiteAnalysisPanel.filterDatasetAndStudyAndTreatmentFactorVariables(variableTypeList);

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(germplasmVariable, result.get(0));
	}

	@Test
	public void testReset() {

		this.singleSiteAnalysisPanel.reset();

		Mockito.verify(this.germplasmDescriptorTableLayout).removeAllComponents();
		Mockito.verify(this.traitTableLayout).removeAllComponents();
		Mockito.verify(this.covariateTableLayout).removeAllComponents();
		Mockito.verify(this.studyDetailsContainer).removeAllComponents();

		Mockito.verify(this.germplasmDescriptorTableLayout).addComponent(Mockito.any(VariableTableComponent.class));
		Mockito.verify(this.traitTableLayout).addComponent(Mockito.any(VariableTableComponent.class));
		Mockito.verify(this.covariateTableLayout).addComponent(Mockito.any(VariableTableComponent.class));
		Mockito.verify(this.studyDetailsContainer).addComponent(Mockito.any(SingleSiteAnalysisStudyDetailsComponent.class));

		Mockito.verify(this.germplasmDescriptorTableLayout, Mockito.never()).addComponent(this.germplasmDescriptorsComponent);
		Mockito.verify(this.traitTableLayout, Mockito.never()).addComponent(this.variatesTableComponent);
		Mockito.verify(this.covariateTableLayout, Mockito.never()).addComponent(this.covariatesTableComponent);
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
		Mockito.verify(this.covariatesTableComponent).resetAllCheckbox();

	}

}
