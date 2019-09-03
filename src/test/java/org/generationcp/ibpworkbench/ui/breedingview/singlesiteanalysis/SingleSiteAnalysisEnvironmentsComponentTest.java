
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.when;

public class SingleSiteAnalysisEnvironmentsComponentTest {

	private static final int NO_OF_ENVS = 5;
	private static final int DATASET_ID = 3;
	private static final int STUDY_ID = 1;
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final String LOCATION_NAME = "LOCATION_NAME";
	private static final String LOC_ID = "LOC_ID";
	private static final String LOC_NAME = "LOC_NAME";
	private static final String EXPT_DESIGN = "EXPT_DESIGN";
	private static final String[] TRIAL_ENV_FACTORS =
			{SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE, SingleSiteAnalysisEnvironmentsComponentTest.LOC_ID,
					SingleSiteAnalysisEnvironmentsComponentTest.LOC_NAME, SingleSiteAnalysisEnvironmentsComponentTest.EXPT_DESIGN};

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private SingleSiteAnalysisDetailsPanel ssaDetailsPanel;

	private SingleSiteAnalysisEnvironmentsComponent ssaEnvironmentsComponent;

	private BreedingViewInput input;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.ssaEnvironmentsComponent = new SingleSiteAnalysisEnvironmentsComponent(this.ssaDetailsPanel);
		this.ssaEnvironmentsComponent.setMessageSource(this.messageSource);
		this.ssaEnvironmentsComponent.setStudyDataManager(this.studyDataManager);

		this.input = new BreedingViewInput();
		this.input.setStudyId(STUDY_ID);
		this.input.setDatasetId(DATASET_ID);
		Mockito.doReturn(this.input).when(this.ssaDetailsPanel).getBreedingViewInput();

		final List<DMSVariableType> trialVariables = this.createStudyVariables();
		Mockito.when(this.ssaDetailsPanel.getTrialVariablesInDataset())
				.thenReturn(DMSVariableTypeTestDataInitializer.createDMSVariableTypeList());
		Mockito.when(this.ssaDetailsPanel.getTrialVariablesInDataset()).thenReturn(trialVariables);
		
		this.ssaEnvironmentsComponent.instantiateComponents();
		this.ssaEnvironmentsComponent.addListeners();
	}

	@Test
	public void testListenersAdded() {
		final ValueChangeListener footerCheckBoxListener = this.ssaEnvironmentsComponent.getFooterCheckBoxListener();
		Assert.assertNotNull(footerCheckBoxListener);
		final Collection<?> footerCheckboxListeners =
				this.ssaEnvironmentsComponent.getFooterCheckBox().getListeners(Property.ValueChangeEvent.class);
		Assert.assertNotNull(footerCheckboxListeners);
		Assert.assertTrue(footerCheckboxListeners.size() == 1);
		Assert.assertEquals(footerCheckBoxListener, footerCheckboxListeners.iterator().next());

		final Collection<?> environmentFactorsListeners =
				this.ssaEnvironmentsComponent.getSelEnvFactor().getListeners(Property.ValueChangeEvent.class);
		Assert.assertNotNull(environmentFactorsListeners);
		Assert.assertTrue(environmentFactorsListeners.size() == 1);
	}

	@Test
	public void testPopulateChoicesForEnvForAnalysis() {
		final Select selEnvFactor = new Select();
		selEnvFactor.addItem(SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE);
		selEnvFactor.setValue(SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE);
		this.ssaEnvironmentsComponent.setSelEnvFactor(selEnvFactor);
		this.ssaEnvironmentsComponent.setFooterCheckBox(new CheckBox("Select All", false));
		this.ssaEnvironmentsComponent.setEnvironmentsCheckboxState(new HashMap<String, Boolean>());
		Mockito.when(this.studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.TRIAL_INSTANCE_FACTOR.getId()))
				.thenReturn(TRIAL_INSTANCE);
		Mockito.when(this.ssaDetailsPanel.getTrialVariablesInDataset())
				.thenReturn(DMSVariableTypeTestDataInitializer.createDMSVariableTypeList());

		final TrialEnvironments environments = new TrialEnvironments();
		final TrialEnvironment environment = Mockito.mock(TrialEnvironment.class);
		when(environment.getId()).thenReturn(1);
		environments.add(environment);

		final VariableList variableList = Mockito.mock(VariableList.class);
		when(environment.getVariables()).thenReturn(variableList);
		final Variable trialInstance = new Variable();
		trialInstance.setValue("1");
		when(variableList.findByLocalName(SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE)).thenReturn(trialInstance);
		when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(environments);
		when(this.studyDataManager.getLocalNameByStandardVariableId(Matchers.anyInt(), Matchers.anyInt()))
				.thenReturn(SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE);

		this.ssaEnvironmentsComponent.populateChoicesForEnvForAnalysis();
		Mockito.verify(this.studyDataManager).getLocalNameByStandardVariableId(DATASET_ID, TermId.TRIAL_INSTANCE_FACTOR.getId());
		Assert.assertFalse("The footer checkbox value should be false", this.ssaEnvironmentsComponent.getFooterCheckBox().booleanValue());
		Assert.assertEquals("The environment check box state's size should be 0", 0,
				this.ssaEnvironmentsComponent.getEnvironmentsCheckboxState().size());
		Assert.assertEquals("The trial instance name should be TRIAL_INSTANCE", SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE,
				this.input.getTrialInstanceName());
	}

	@Test
	public void testPopulateChoicesForEnvironmentFactor() {
		final String pleaseChooseOption = "Please Choose";
		Mockito.doReturn(pleaseChooseOption).when(this.messageSource).getMessage(Message.PLEASE_CHOOSE);
		this.ssaEnvironmentsComponent.instantiateComponents();

		this.ssaEnvironmentsComponent.populateChoicesForEnvironmentFactor();
		// "Please Choose" was added as dropdown item
		final Select selEnvFactor = this.ssaEnvironmentsComponent.getSelEnvFactor();
		Assert.assertEquals("Dropdown should return fixed # of env factors",
				SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_ENV_FACTORS.length + 1, selEnvFactor.getItemIds().size());
		for (final Object id : selEnvFactor.getItemIds()) {
			final String localName = (String) id;
			Assert.assertTrue(ArrayUtils.contains(SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_ENV_FACTORS, localName)
					|| pleaseChooseOption.equals(localName));
		}
	}

	 @Test
	 public void testPopulateEnvironmentSelectionTableWithTrialEnvironments() {
		 final Table table = new Table();
		 table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		 table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");
		
		 final TrialEnvironments environments = new TrialEnvironments();
		 final TrialEnvironment environment = Mockito.mock(TrialEnvironment.class);
		 when(environment.getId()).thenReturn(1);
		 environments.add(environment);
		
		 final VariableList variableList = Mockito.mock(VariableList.class);
		 when(environment.getVariables()).thenReturn(variableList);
		 final Variable trialInstance = new Variable();
		 trialInstance.setValue("1");
		 when(variableList.findByLocalName(SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE)).thenReturn(trialInstance);
		 final Variable locationVariable = new Variable();
		 locationVariable.setValue("Africa Rice Center");
		 when(variableList.findByLocalName(SingleSiteAnalysisEnvironmentsComponentTest.LOCATION_NAME)).thenReturn(locationVariable);
		 when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(environments);
		
		this.ssaEnvironmentsComponent.populateEnvironmentSelectionTableWithTrialEnvironments(table,
				SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE, SingleSiteAnalysisEnvironmentsComponentTest.LOCATION_NAME);
		 final BeanItemContainer<SeaEnvironmentModel> container = (BeanItemContainer<SeaEnvironmentModel>) table.getContainerDataSource();
		 final SeaEnvironmentModel bean = container.getIdByIndex(0);
		 Assert.assertFalse("The active value should be false", bean.getActive());
		 Assert.assertEquals("The environment name should be Africa Rice Center", "Africa Rice Center", bean.getEnvironmentName());
		 Assert.assertEquals("The study no should be 1", "1", bean.getTrialno());
		 Assert.assertEquals("The location id should be 1", "1", bean.getLocationId().toString());
	 }
	
	 @Test
	 public void testPopulateEnvironmentSelectionTableSelectedFactorIsLocationID() {
		 final Table table = new Table();
		 table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		 table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");
		
		 final TrialEnvironments environments = new TrialEnvironments();
		 final TrialEnvironment environment = Mockito.mock(TrialEnvironment.class);
		 when(environment.getId()).thenReturn(1);
		 environments.add(environment);
		
		 final VariableList variableList = Mockito.mock(VariableList.class);
		 when(environment.getVariables()).thenReturn(variableList);
		 final Variable trialInstance = new Variable();
		 trialInstance.setValue("1");
		 when(variableList.findByLocalName(SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE)).thenReturn(trialInstance);
		 final Variable locationIDVariable = new Variable();
		 locationIDVariable.setValue("100");
		 when(variableList.findByLocalName(SingleSiteAnalysisEnvironmentsComponentTest.LOCATION_NAME)).thenReturn(locationIDVariable);
		 when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(environments);
		
		 final BiMap<String, String> locationIdToNameMap = HashBiMap.create();
		 locationIdToNameMap.put("100", "Agua Fria");
		
		 when(this.studyDataManager.isLocationIdVariable(STUDY_ID,
		 SingleSiteAnalysisEnvironmentsComponentTest.LOCATION_NAME)).thenReturn(true);
		 when(this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(STUDY_ID)).thenReturn(locationIdToNameMap);
		
		 this.ssaEnvironmentsComponent.populateEnvironmentSelectionTableWithTrialEnvironments(table,
		 SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE,
		 SingleSiteAnalysisEnvironmentsComponentTest.LOCATION_NAME);
		 final BeanItemContainer<SeaEnvironmentModel> container = (BeanItemContainer<SeaEnvironmentModel>) table.getContainerDataSource();
		 final SeaEnvironmentModel bean = container.getIdByIndex(0);
		 Assert.assertFalse("The active value should be false", bean.getActive());
		 Assert.assertEquals("The environment name should be Agua Fria", "Agua Fria", bean.getEnvironmentName());
		 Assert.assertEquals("The study no should be 1", "1", bean.getTrialno());
		 Assert.assertEquals("The location id should be 1", "1", bean.getLocationId().toString());
	 }
	
	 @Test
	 public void testAdjustEnvironmentSelectionTableWhereTrialInstanceFactorNotSelectedEnvFactor() {
		 final Table table = new Table();
		 table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		 table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");
		 table.addContainerProperty(SingleSiteAnalysisDetailsPanel.ENVIRONMENT_NAME, String.class, "");
		
		 this.ssaEnvironmentsComponent.adjustEnvironmentSelectionTable(table, SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE,
		 SingleSiteAnalysisEnvironmentsComponentTest.LOCATION_NAME);
		 Assert.assertEquals("There should be 3 visible columns", 3, table.getVisibleColumns().length);
		 Assert.assertEquals("There should be 3 column headers", 3, table.getColumnHeaders().length);
		 Assert.assertEquals("Select column's width should be 45.", 45, table.getColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN));
		 Assert.assertEquals("Study No's width should be 60.", 60, table.getColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN));
		 Assert.assertEquals("Environment Names's width should be 500.", 500,
		 table.getColumnWidth(SingleSiteAnalysisDetailsPanel.ENVIRONMENT_NAME));
		 Assert.assertEquals("Table's width should be 90.0.", "90.0", String.valueOf(table.getWidth()));
	 }
	
	 @Test
	 public void testAdjustEnvironmentSelectionTableWhereTrialInstanceFactorIsTheSelectedEnvFactor() {
		 final Table table = new Table();
		 table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		 table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");
		
		 this.ssaEnvironmentsComponent.adjustEnvironmentSelectionTable(table, SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE,
		 SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE);
		 Assert.assertEquals("There should be 2 visible columns", 2, table.getVisibleColumns().length);
		 Assert.assertEquals("There should be 2 column headers", 2, table.getColumnHeaders().length);
		 Assert.assertEquals("Select column's width should be 45.", 45, table.getColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN));
		 Assert.assertEquals("Study No's width should be -1.", -1, table.getColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN));
		 Assert.assertEquals("Table's width should be 45.0.", "45.0", String.valueOf(table.getWidth()));
	 }
	 
	 @Test
	 public void testEnvironmentContainsValidDataForAnalysis() {
		 final SeaEnvironmentModel bean = new SeaEnvironmentModel();
		 bean.setTrialno(String.valueOf(STUDY_ID));
		 this.ssaEnvironmentsComponent.environmentContainsValidDataForAnalysis(bean);
		 Mockito.verify(this.ssaDetailsPanel).environmentContainsValidDataForAnalysis(bean);
	 }
	 
	 @Test
	 public void testGetSelectedEnvironmentsWhenNoneSelected() {
		 this.populateTableWithTestEnvironments();
		 Assert.assertTrue(this.ssaEnvironmentsComponent.getSelectedEnvironments().isEmpty());
	 }
	 
	 @Test
	 public void testGetSelectedEnvironments() {
		 this.populateTableWithTestEnvironments();
		 // Select first two environments
		 final Iterator<?> envsIterator = this.ssaEnvironmentsComponent.getTblEnvironmentSelection().getContainerDataSource().getItemIds().iterator();
		 final SeaEnvironmentModel env1 = (SeaEnvironmentModel)envsIterator.next();
		 env1.setActive(true);
		 final SeaEnvironmentModel env2 = (SeaEnvironmentModel)envsIterator.next();
		 env2.setActive(true);
		 
		 final List<SeaEnvironmentModel> selectedEnvs = this.ssaEnvironmentsComponent.getSelectedEnvironments();
		 Assert.assertTrue(selectedEnvs.size() == 2);
		 Assert.assertEquals(env1, selectedEnvs.get(0));
		 Assert.assertEquals(env2, selectedEnvs.get(1));
	 }

	private void populateTableWithTestEnvironments() {
		final Table envTable = this.ssaEnvironmentsComponent.getTblEnvironmentSelection();
		final BeanItemContainer<SeaEnvironmentModel> container = new BeanItemContainer<>(SeaEnvironmentModel.class);
		for (int i = 0; i < NO_OF_ENVS; i++) {
			final SeaEnvironmentModel bean = new SeaEnvironmentModel();
			bean.setActive(false);
			bean.setEnvironmentName("ENVIRONMENT " + i);
			bean.setTrialno(String.valueOf(100 + i));
			bean.setLocationId(i + 1);
			container.addBean(bean);
		}	
		envTable.setContainerDataSource(container);
	}
	 
	@Test
	public void testGetInvalidEnvironments() {
		this.populateTableWithTestEnvironments();
		Mockito.doReturn(true).when(this.ssaDetailsPanel).environmentContainsValidDataForAnalysis(Matchers.any(SeaEnvironmentModel.class));
		List<String> invalidEnvironments = this.ssaEnvironmentsComponent.getInvalidEnvironments(true);
		Assert.assertTrue(invalidEnvironments.isEmpty());
		
		final Iterator<?> envsIterator = this.ssaEnvironmentsComponent.getTblEnvironmentSelection().getContainerDataSource().getItemIds().iterator();
		final SeaEnvironmentModel env1 = (SeaEnvironmentModel)envsIterator.next();
		final SeaEnvironmentModel env2 = (SeaEnvironmentModel)envsIterator.next();
		env1.setActive(true);
		env2.setActive(true);
		Mockito.doReturn(false).when(this.ssaDetailsPanel).environmentContainsValidDataForAnalysis(env1);
		Mockito.doReturn(false).when(this.ssaDetailsPanel).environmentContainsValidDataForAnalysis(env2);
		invalidEnvironments = this.ssaEnvironmentsComponent.getInvalidEnvironments(true);
		Assert.assertTrue(invalidEnvironments.size() == 2);
		Assert.assertEquals(env1.getEnvironmentName(), invalidEnvironments.get(0));
		Assert.assertEquals(env2.getEnvironmentName(), invalidEnvironments.get(1));
	}
	
	private List<DMSVariableType> createStudyVariables() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		final StandardVariable trialInstanceVar = new StandardVariable();
		trialInstanceVar.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		trialInstanceVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		trialInstanceVar.setProperty(new Term(1, SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE,
				SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE));
		factors.add(new DMSVariableType(SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE,
				SingleSiteAnalysisEnvironmentsComponentTest.TRIAL_INSTANCE, trialInstanceVar, 1));

		final StandardVariable exptDesignVar = new StandardVariable();
		exptDesignVar.setId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		exptDesignVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		exptDesignVar.setProperty(new Term(1, "EXPERIMENTAL DESIGN", "EXPERIMENTAL DESIGN"));
		factors.add(new DMSVariableType(SingleSiteAnalysisEnvironmentsComponentTest.EXPT_DESIGN,
				SingleSiteAnalysisEnvironmentsComponentTest.EXPT_DESIGN, exptDesignVar, 2));

		final StandardVariable locNameVar = new StandardVariable();
		locNameVar.setId(TermId.SITE_NAME.getId());
		locNameVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locNameVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(new DMSVariableType(SingleSiteAnalysisEnvironmentsComponentTest.LOC_NAME,
				SingleSiteAnalysisEnvironmentsComponentTest.LOC_NAME, locNameVar, 3));

		final StandardVariable locIDVar = new StandardVariable();
		locIDVar.setId(TermId.LOCATION_ID.getId());
		locIDVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locIDVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(new DMSVariableType(SingleSiteAnalysisEnvironmentsComponentTest.LOC_ID,
				SingleSiteAnalysisEnvironmentsComponentTest.LOC_ID, locIDVar, 4));

		final StandardVariable datasetNameVar = new StandardVariable();
		datasetNameVar.setId(TermId.DATASET_NAME.getId());
		datasetNameVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetNameVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType("DATASET_NAME", "DATASET_NAME", datasetNameVar, 5));

		final StandardVariable datasetTitleVar = new StandardVariable();
		datasetTitleVar.setId(TermId.DATASET_NAME.getId());
		datasetTitleVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTitleVar.setProperty(new Term(1, "DATASET TITLE", "DATASET TITLE"));
		factors.add(new DMSVariableType("DATASET TITLE", "DATASET TITLE", datasetTitleVar, 6));

		final StandardVariable datasetTypeVar = new StandardVariable();
		datasetTypeVar.setId(TermId.DATASET_NAME.getId());
		datasetTypeVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTypeVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType("DATASET_TYPE", "DATASET_TYPE", datasetTypeVar, 7));

		return factors;
	}

}
