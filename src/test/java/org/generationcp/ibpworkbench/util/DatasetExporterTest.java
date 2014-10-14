package org.generationcp.ibpworkbench.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.ibpworkbench.util.DatasetExporterException;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DatasetExporterTest {

	@Mock
	private static ManagerFactory factory;

	@Mock
	private static StudyDataManager manager;
	
	@Mock
	private static WorkbenchDataManager workbenchDataManager;

	@Mock
	private BreedingViewInput bvInput;

	@Mock
	private DataSet dataSet;

	@Mock
	private Experiment experiment;

	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExportToCSVForBreedingView_Default() {

		Term numeric = new Term();
		numeric.setName(DatasetExporter.NUMERIC_VARIABLE);

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add("1");

		List<VariableType> factors = new ArrayList<VariableType>();
		VariableType trial = new VariableType();
		trial.setLocalName("SITE_NO");
		trial.setRank(1);
		trial.setStandardVariable(createStardardVariable(PhenotypicType.TRIAL_ENVIRONMENT,
				"SITE_NO", numeric));
		factors.add(trial);
		List<VariableType> variates = new ArrayList<VariableType>();
		VariableType epp = new VariableType();
		epp.setLocalName("EPP");
		epp.setRank(1);
		epp.setStandardVariable(createStardardVariable(PhenotypicType.VARIATE, "EPP", numeric));
		variates.add(epp);
		VariableType ph = new VariableType();
		ph.setLocalName("PH");
		ph.setRank(1);
		ph.setStandardVariable(createStardardVariable(PhenotypicType.VARIATE, "PH", numeric));
		variates.add(ph);
		VariableType earh = new VariableType();
		earh.setLocalName("EARH");
		earh.setRank(1);
		earh.setStandardVariable(createStardardVariable(PhenotypicType.VARIATE, "EARH", numeric));
		variates.add(earh);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		variatesActiveState.put("SITE_NO", true);
		variatesActiveState.put("EARH", false);
		variatesActiveState.put("EPP", true);
		variatesActiveState.put("PH", true);
		

		List<Experiment> experiments = new ArrayList<Experiment>();
		experiments.add(experiment);

		List<Variable> factorVariables = new ArrayList<Variable>();
		Variable siteNoVariable = new Variable();
		siteNoVariable.setValue("1");
		siteNoVariable.setVariableType(trial);
		factorVariables.add(siteNoVariable);

		List<Variable> variateVariables = new ArrayList<Variable>();
		Variable eppVariable = new Variable();
		eppVariable.setValue("76.223");
		eppVariable.setVariableType(epp);
		variateVariables.add(eppVariable);
		Variable phVariable = new Variable();
		phVariable.setValue("7.5");
		phVariable.setVariableType(ph);
		variateVariables.add(phVariable);
		Variable earhVariable = new Variable();
		earhVariable.setValue("1111.0");
		earhVariable.setVariableType(earh);
		variateVariables.add(earhVariable);

		try {
			when(manager.getDataSet(anyInt())).thenReturn(dataSet);
			when(manager.getExperiments(anyInt(), anyInt(), anyInt())).thenReturn(experiments);

			when(experiment.getFactors()).thenReturn(mock(VariableList.class));
			when(experiment.getFactors().getVariables()).thenReturn(factorVariables);
			when(experiment.getVariates()).thenReturn(mock(VariableList.class));
			when(experiment.getVariates().findByLocalName("EPP")).thenReturn(eppVariable);
			when(experiment.getVariates().findByLocalName("PH")).thenReturn(phVariable);
			when(experiment.getVariates().findByLocalName("EARH")).thenReturn(earhVariable);
			when(experiment.getVariates().getVariables()).thenReturn(variateVariables);

			when(dataSet.getVariableTypes()).thenReturn(mock(VariableTypeList.class));
			when(dataSet.getVariableTypes().getFactors()).thenReturn(mock(VariableTypeList.class));
			when(dataSet.getVariableTypes().getVariates()).thenReturn(mock(VariableTypeList.class));
			when(dataSet.getVariableTypes().getFactors().getVariableTypes()).thenReturn(factors);
			when(dataSet.getVariableTypes().getVariates().getVariableTypes()).thenReturn(variates);

			when(bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			when(bvInput.getReplicates()).thenReturn(mock(Replicates.class));
			when(bvInput.getReplicates().getName()).thenReturn("REP");
			
			when(workbenchDataManager.getWorkbenchSetting()).thenReturn(mock(WorkbenchSetting.class));
			when(workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(manager, 1, 1);
		exporter.setWorkbenchDataManager(workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView("datasetExporterTest.csv", "SITE_NO",
					selectedEnvironments, bvInput);
		} catch (DatasetExporterException e) {

			fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		//header
		assertEquals("SITE_NO", tableItems.get(0)[0]);
		assertEquals("EPP", tableItems.get(0)[1]);
		assertEquals("PH", tableItems.get(0)[2]);
		assertFalse(ArrayUtils.contains(tableItems.get(0), "EARH"));
		
		//data
		assertEquals("1", tableItems.get(1)[0]);
		assertEquals("76.223", tableItems.get(1)[1]);
		assertEquals("7.5", tableItems.get(1)[2]);
		
		assertEquals("SITE_NO", headerAliasMap.get("SITE_NO"));
		assertEquals("EPP", headerAliasMap.get("EPP"));
		assertEquals("PH", headerAliasMap.get("PH"));
		assertNull(headerAliasMap.get("EARH"));

	}

	@Test
	public void testExportToCSVForBreedingView_NoExistingRecordsForSelectedEnvironment() {

		Term numeric = new Term();
		numeric.setName(DatasetExporter.NUMERIC_VARIABLE);

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add("2");

		List<VariableType> factors = new ArrayList<VariableType>();
		VariableType trial = new VariableType();
		trial.setLocalName("SITE_NO");
		trial.setRank(1);
		trial.setStandardVariable(createStardardVariable(PhenotypicType.TRIAL_ENVIRONMENT,
				"SITE_NO", numeric));
		factors.add(trial);
		List<VariableType> variates = new ArrayList<VariableType>();
		VariableType epp = new VariableType();
		epp.setLocalName("EPP");
		epp.setRank(1);
		epp.setStandardVariable(createStardardVariable(PhenotypicType.VARIATE, "EPP", numeric));
		variates.add(epp);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		variatesActiveState.put("SITE_NO", true);
		variatesActiveState.put("EPP", true);

		List<Experiment> experiments = new ArrayList<Experiment>();
		experiments.add(experiment);

		List<Variable> factorVariables = new ArrayList<Variable>();
		Variable siteNoVariable = new Variable();
		siteNoVariable.setValue("1");
		siteNoVariable.setVariableType(trial);
		factorVariables.add(siteNoVariable);

		List<Variable> variateVariables = new ArrayList<Variable>();
		Variable eppVariable = new Variable();
		eppVariable.setValue("76.223");
		eppVariable.setVariableType(epp);
		variateVariables.add(eppVariable);

		try {
			when(manager.getDataSet(anyInt())).thenReturn(dataSet);
			when(manager.getExperiments(anyInt(), anyInt(), anyInt())).thenReturn(experiments);

			when(experiment.getFactors()).thenReturn(mock(VariableList.class));
			when(experiment.getFactors().getVariables()).thenReturn(factorVariables);
			when(experiment.getVariates()).thenReturn(mock(VariableList.class));
			when(experiment.getVariates().findByLocalName("EPP")).thenReturn(eppVariable);
			when(experiment.getVariates().getVariables()).thenReturn(variateVariables);

			when(dataSet.getVariableTypes()).thenReturn(mock(VariableTypeList.class));
			when(dataSet.getVariableTypes().getFactors()).thenReturn(mock(VariableTypeList.class));
			when(dataSet.getVariableTypes().getVariates()).thenReturn(mock(VariableTypeList.class));
			when(dataSet.getVariableTypes().getFactors().getVariableTypes()).thenReturn(factors);
			when(dataSet.getVariableTypes().getVariates().getVariableTypes()).thenReturn(variates);

			when(bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			when(bvInput.getReplicates()).thenReturn(mock(Replicates.class));
			when(bvInput.getReplicates().getName()).thenReturn("REP");
			
			when(workbenchDataManager.getWorkbenchSetting()).thenReturn(mock(WorkbenchSetting.class));
			when(workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(manager, 1, 1);
		exporter.setWorkbenchDataManager(workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView("datasetExporterTest.csv", "SITE_NO",
					selectedEnvironments, bvInput);
		} catch (DatasetExporterException e) {

			fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		assertEquals("SITE_NO", tableItems.get(0)[0]);
		assertEquals("EPP", tableItems.get(0)[1]);
		assertEquals(1, tableItems.size());

		assertEquals("SITE_NO", headerAliasMap.get("SITE_NO"));
		assertEquals("EPP", headerAliasMap.get("EPP"));

	}

	@Test
	public void testExportToCSVForBreedingView_TraitNamesWithSpecialCharactersAndWhiteSpace() {

		String testFactorName = "SITE_NO%#!@";
		String testVariateName = "EPP@ ";

		Term numeric = new Term();
		numeric.setName(DatasetExporter.NUMERIC_VARIABLE);

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add("1");

		List<VariableType> factors = new ArrayList<VariableType>();
		VariableType trial = new VariableType();
		trial.setLocalName(testFactorName);
		trial.setRank(1);
		trial.setStandardVariable(createStardardVariable(PhenotypicType.TRIAL_ENVIRONMENT,
				testFactorName, numeric));
		factors.add(trial);
		List<VariableType> variates = new ArrayList<VariableType>();
		VariableType epp = new VariableType();
		epp.setLocalName(testVariateName);
		epp.setRank(1);
		epp.setStandardVariable(createStardardVariable(PhenotypicType.VARIATE, testVariateName,
				numeric));
		variates.add(epp);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		variatesActiveState.put(testFactorName, true);
		variatesActiveState.put(testVariateName, true);

		List<Experiment> experiments = new ArrayList<Experiment>();
		experiments.add(experiment);

		List<Variable> factorVariables = new ArrayList<Variable>();
		Variable siteNoVariable = new Variable();
		siteNoVariable.setValue("1");
		siteNoVariable.setVariableType(trial);
		factorVariables.add(siteNoVariable);

		List<Variable> variateVariables = new ArrayList<Variable>();
		Variable eppVariable = new Variable();
		eppVariable.setValue("76.223");
		eppVariable.setVariableType(epp);
		variateVariables.add(eppVariable);

		try {
			when(manager.getDataSet(anyInt())).thenReturn(dataSet);
			when(manager.getExperiments(anyInt(), anyInt(), anyInt())).thenReturn(experiments);

			when(experiment.getFactors()).thenReturn(mock(VariableList.class));
			when(experiment.getFactors().getVariables()).thenReturn(factorVariables);
			when(experiment.getVariates()).thenReturn(mock(VariableList.class));
			when(experiment.getVariates().findByLocalName(testVariateName)).thenReturn(eppVariable);
			when(experiment.getVariates().getVariables()).thenReturn(variateVariables);

			when(dataSet.getVariableTypes()).thenReturn(mock(VariableTypeList.class));
			when(dataSet.getVariableTypes().getFactors()).thenReturn(mock(VariableTypeList.class));
			when(dataSet.getVariableTypes().getVariates()).thenReturn(mock(VariableTypeList.class));
			when(dataSet.getVariableTypes().getFactors().getVariableTypes()).thenReturn(factors);
			when(dataSet.getVariableTypes().getVariates().getVariableTypes()).thenReturn(variates);

			when(bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			when(bvInput.getReplicates()).thenReturn(mock(Replicates.class));
			when(bvInput.getReplicates().getName()).thenReturn("REP");
			
			when(workbenchDataManager.getWorkbenchSetting()).thenReturn(mock(WorkbenchSetting.class));
			when(workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(manager, 1, 1);
		exporter.setWorkbenchDataManager(workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView("datasetExporterTest.csv", testFactorName,
					selectedEnvironments, bvInput);
		} catch (DatasetExporterException e) {

			fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		assertEquals("SITE_NO%_", tableItems.get(0)[0]);
		assertEquals("EPP_", tableItems.get(0)[1]);
		assertEquals("1", tableItems.get(1)[0]);
		assertEquals("76.223", tableItems.get(1)[1]);

		assertEquals("SITE_NO%#!@", headerAliasMap.get("SITE_NO%_"));
		assertEquals("EPP@ ", headerAliasMap.get("EPP_"));

	}

	private StandardVariable createStardardVariable(PhenotypicType type, String name, Term dataType) {

		StandardVariable stdVar = new StandardVariable();
		stdVar.setPhenotypicType(type);
		stdVar.setName(name);
		stdVar.setDataType(dataType);
		return stdVar;
	}

}
