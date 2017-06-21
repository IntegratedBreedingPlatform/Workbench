package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.ObjectUtil;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.service.api.OntologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import au.com.bytecode.opencsv.CSVWriter;

@Configurable
public class DatasetExporter {

	public static final String NUMERIC_VARIABLE = "Numeric variable";
	public static final String DUMMY_REPLICATES = "_REPLICATES_";

	private static final Logger LOG = LoggerFactory.getLogger(DatasetExporter.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;
	private final StudyDataManager studyDataManager;
	private final Integer datasetId;
	private final List<String[]> tableItems;
	private final Map<String, Integer> columnsMap = new HashMap<>();
	private final Map<String, String> headerNameAliasMap = new HashMap<>();
	private int observationSheetColumnIndex;

	private static final String MISSING_VALUE_STRING = "missing";
	private final OntologyService ontologyService;

	public DatasetExporter(final StudyDataManager studyDataManager, final OntologyService ontologyService, final Integer studyId,
			final Integer datasetId) {
		this.studyDataManager = studyDataManager;
		this.ontologyService = ontologyService;
		this.datasetId = datasetId;
		this.tableItems = new ArrayList<>();
	}

	private DataSet getDataSet(final int dataSetId) {

		final DataSet dataset;
		try {
			dataset = this.studyDataManager.getDataSet(this.datasetId);
			return dataset;
		} catch (final MiddlewareException ex) {
			DatasetExporter.LOG.error(ex.getMessage(), ex);
		}

		return null;
	}

	protected Map<String, Integer> generateFactorColumnsMap(final DataSet dataset) {

		final Map<String, Integer> factorsColumnsMap = new HashMap<>();
		final List<DMSVariableType> factorVariableTypes = dataset.getVariableTypes().getFactors().getVariableTypes();

		for (final DMSVariableType factor : factorVariableTypes) {

			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET
					|| factor.getStandardVariable().getPhenotypicType() == PhenotypicType.STUDY) {
				continue;
			}

			String factorName = factor.getLocalName();
			if (factorName != null) {
				factorName = factorName.trim();
			}

			if (!factorsColumnsMap.containsKey(factorName) && !"STUDY".equals(factorName)) {
				// add entry to columns mapping
				factorsColumnsMap.put(factorName, Integer.valueOf(this.observationSheetColumnIndex));
				this.observationSheetColumnIndex++;
			}
		}

		return factorsColumnsMap;

	}

	protected Map<String, Integer> generateVariateColumnsMap(final DataSet dataset, final BreedingViewInput breedingViewInput) {

		final Map<String, Integer> variateColumnsMap = new HashMap<>();
		final List<DMSVariableType> variateVariableTypes = dataset.getVariableTypes().getVariates().getVariableTypes();

		for (final DMSVariableType variate : variateVariableTypes) {

			final String variateName = variate.getLocalName();

			// get only the selected traits
			if (breedingViewInput.getVariatesActiveState().get(variateName).booleanValue()) {
				// add entry to columns mapping
				variateColumnsMap.put(variateName, Integer.valueOf(this.observationSheetColumnIndex));
				this.observationSheetColumnIndex++;
			}

		}

		return variateColumnsMap;
	}

	private List<String> generateRowHeader(final Map<String, String> headerNameAliasMap) {

		final List<String> keys = new ArrayList<>(this.columnsMap.keySet());

		// Sort keys by values.
		final Map<String, Integer> langForComp = this.columnsMap;
		Collections.sort(keys, new Comparator<Object>() {

			@Override
			public int compare(final Object left, final Object right) {
				final String leftKey = (String) left;
				final String rightKey = (String) right;

				final Integer leftValue = langForComp.get(leftKey);
				final Integer rightValue = langForComp.get(rightKey);
				return leftValue.compareTo(rightValue);
			}
		});

		final List<String> rowHeader = new ArrayList<>();
		for (final Iterator<String> i = keys.iterator(); i.hasNext(); ) {
			final String k = i.next();
			final Integer columnIndex = this.columnsMap.get(k).intValue();
			if (columnIndex >= 0) {
				final String nameSanitized = BreedingViewUtil.trimAndSanitizeName(k);
				rowHeader.add(nameSanitized);
				headerNameAliasMap.put(nameSanitized, k);
			}
		}

		return rowHeader;

	}

	public void exportToCSVForBreedingView(final String filename, final String selectedFactor, final List<String> selectedEnvironment,
			final BreedingViewInput breedingViewInput) throws DatasetExporterException {

		final Map<Integer, String> selectEnvironmentsMap = new HashMap<>();
		if (!selectedFactor.equalsIgnoreCase(breedingViewInput.getTrialInstanceName())) {
			for (final SeaEnvironmentModel model : breedingViewInput.getSelectedEnvironments()) {
				selectEnvironmentsMap.put(model.getLocationId(), model.getEnvironmentName());
			}
		}

		final DataSet dataset = this.getDataSet(this.datasetId);

		if (dataset == null) {
			return;
		}

		this.observationSheetColumnIndex = 0;

		final Map<String, Integer> factorColumnsMap = this.generateFactorColumnsMap(dataset);
		final Map<String, Integer> variateColumnsMap = this.generateVariateColumnsMap(dataset, breedingViewInput);

		this.columnsMap.putAll(factorColumnsMap);
		this.columnsMap.putAll(variateColumnsMap);

		if (!breedingViewInput.getDesignType().equals(DesignType.P_REP_DESIGN.getName()) && !breedingViewInput.getDesignType()
				.equals(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName()) && DatasetExporter.DUMMY_REPLICATES
				.equals(breedingViewInput.getReplicatesFactorName())) {
			this.columnsMap.put(DatasetExporter.DUMMY_REPLICATES, Integer.valueOf(this.observationSheetColumnIndex));
			this.observationSheetColumnIndex++;
		}

		boolean trialEnvironmentFactorAlreadyExists = true;
		if (!selectedFactor.equalsIgnoreCase(breedingViewInput.getTrialInstanceName()) && !this.columnsMap.containsKey(selectedFactor)) {
			this.columnsMap.put(selectedFactor, Integer.valueOf(this.observationSheetColumnIndex));
			this.observationSheetColumnIndex++;
			trialEnvironmentFactorAlreadyExists = false;
		}

		List<Experiment> experiments = new ArrayList<>();

		try {
			experiments = this.studyDataManager.getExperiments(this.datasetId, 0, Integer.MAX_VALUE);
		} catch (final Exception ex) {
			DatasetExporter.LOG.error(ex.getMessage(), ex);
		}

		final List<String> rowHeader = this.generateRowHeader(this.headerNameAliasMap);
		this.getTableItems().add(rowHeader.toArray(new String[0]));

		for (final Experiment experiment : experiments) {

			boolean outerBreak = true;
			for (final Variable factorVariables1 : experiment.getFactors().getVariables()) {
				if (factorVariables1.getVariableType().getLocalName().trim().equalsIgnoreCase(breedingViewInput.getTrialInstanceName())
						&& selectedEnvironment.contains(factorVariables1.getValue())) {
					outerBreak = false;
				}
			}
			if (outerBreak) {
				continue;
			}

			final List<String> row = new ArrayList<>();

			this.processFactors(factorColumnsMap, row, experiment, breedingViewInput);

			this.processVariates(variateColumnsMap, row, experiment, breedingViewInput, this.ontologyService);

			if (!breedingViewInput.getDesignType().equals(DesignType.P_REP_DESIGN.getName()) && !breedingViewInput.getDesignType()
					.equals(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName()) && DatasetExporter.DUMMY_REPLICATES
					.equals(breedingViewInput.getReplicatesFactorName())) {
				row.add("1");
			}

			if (!trialEnvironmentFactorAlreadyExists) {
				row.add(selectEnvironmentsMap.get(experiment.getLocationId()).trim().replace(",", ";"));
			}

			this.getTableItems().add(row.toArray(new String[0]));
		}

		this.serializeHeaderAliasMap();

		this.writeCSVFile(filename);

	}

	protected void serializeHeaderAliasMap() {
		try {
			final String tempFolder =
					String.format("%s\\temp", this.getWorkbenchDataManager().getWorkbenchSetting().getInstallationDirectory());
			new File(tempFolder).mkdir();
			final String fileName = tempFolder + "\\mapping.ser";
			new ObjectUtil<Map<String, String>>().serializeObject(this.headerNameAliasMap, fileName);
		} catch (final Exception e) {
			DatasetExporter.LOG.error(e.getMessage(), e);
		}
	}

	protected void writeCSVFile(final String filename) {
		try {
			final File csvFile = new File(filename);
			final CSVWriter csvWriter =
					new CSVWriter(new FileWriter(csvFile), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
			csvWriter.writeAll(this.getTableItems());
			csvWriter.flush();
			csvWriter.close();
		} catch (final Exception e) {
			DatasetExporter.LOG.error(e.getMessage(), e);
		}
	}

	protected void processFactors(final Map<String, Integer> factorColumnsMap, final List<String> rowContent,
			final Experiment currentExperiment, final BreedingViewInput breedingViewInput) {
		final Map<Short, String> rowContentMap = new TreeMap<>();
		final List<Variable> factorsOfExperiments = currentExperiment.getFactors().getVariables();
		final Map<String, Variable> factorsOfExperimentsMap = new HashMap<>();
		for (final Variable factorVariable : factorsOfExperiments) {
			factorsOfExperimentsMap.put(factorVariable.getVariableType().getLocalName().trim(), factorVariable);
		}

		final Map<Integer, String> sortedColumnsByColumnIndex = sortColumnsMapByColumnIndex(factorColumnsMap);

		for (final Map.Entry<Integer, String> entry : sortedColumnsByColumnIndex.entrySet()) {

			final short columnIndex = entry.getKey().shortValue();

			if (factorsOfExperimentsMap.containsKey(entry.getValue())) {

				final Variable factorVariable = factorsOfExperimentsMap.get(entry.getValue());

				if (factorVariable.getVariableType().getStandardVariable().getDataType().getName()
						.equals(DatasetExporter.NUMERIC_VARIABLE)) {
					double elemValue = 0;
					if (factorVariable.getValue() != null) {

						if (factorVariable.getValue().isEmpty() && factorVariable.getVariableType().getLocalName()
								.equalsIgnoreCase(breedingViewInput.getReplicates().getName())) {
							final Variable variable = factorsOfExperimentsMap.get(breedingViewInput.getBlocks().getName());
							if (variable != null) {
								rowContentMap.put(columnIndex, variable.getValue().trim());
							} else {
								rowContentMap.put(columnIndex, "");
							}

						} else {
							try {
								elemValue = Double.valueOf(factorVariable.getValue());

								if (elemValue == Double.valueOf("-1E+36")) {
									rowContentMap.put(columnIndex, "");
								} else {
									rowContentMap.put(columnIndex, String.valueOf(factorVariable.getValue()));
								}
							} catch (final NumberFormatException ex) {
								String value = factorVariable.getValue();
								if (value != null) {
									value = value.trim();
								}
								rowContentMap.put(columnIndex, value);
							}
						}

					} else {
						final String nullValue = null;
						rowContentMap.put(columnIndex, nullValue);
					}
				} else {
					String value = factorVariable.getValue();
					if (value != null) {
						if (factorVariable.getVariableType().getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
								|| factorVariable.getVariableType().getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
							value = value.trim().replace(",", ";");

							if (value.isEmpty() && factorVariable.getVariableType().getLocalName()
									.equalsIgnoreCase(breedingViewInput.getReplicates().getName())) {
								final Variable variable = factorsOfExperimentsMap.get(breedingViewInput.getBlocks().getName());
								if (variable != null) {
									value = variable.getValue().trim();
								} else {
									value = "";
								}

							}

						} else {
							value = value.trim();
						}

					}
					rowContentMap.put(columnIndex, value);
				}

			} else if (TermId.PLOT_ID.name().equals(entry.getValue())) {

				// special case
				rowContentMap.put(columnIndex, currentExperiment.getPlotId());

			} else {

				rowContentMap.put(columnIndex, "");
			}

		}

		if (rowContentMap.size() > 0) {
			rowContent.addAll(rowContentMap.values());
		}
	}

	Map<Integer, String> sortColumnsMapByColumnIndex(final Map<String, Integer> columnsMap) {

		final Map<Integer, String> sortedMap = new TreeMap<>();
		// The value in columnsMap is the index of the column.
		for (final Map.Entry<String, Integer> entry : columnsMap.entrySet()) {
			sortedMap.put(entry.getValue(), entry.getKey());
		}

		return sortedMap;

	}

	protected void processVariates(final Map<String, Integer> variateColumnsMap, final List<String> rowContent,
			final Experiment currentExperiment, final BreedingViewInput breedingViewInput, final OntologyService ontologyService) {

		final Map<Integer, String> sortedColumnsByIndex = sortColumnsMapByColumnIndex(variateColumnsMap);

		for (final Map.Entry<Integer, String> entry : sortedColumnsByIndex.entrySet()) {

			String variateName = "";
			final Variable variateVariable = currentExperiment.getVariates().findByLocalName(entry.getValue());
			if (variateVariable != null) {
				variateName = variateVariable.getVariableType().getLocalName();

				final Integer columnIndexInteger = this.columnsMap.get(variateName);
				if (columnIndexInteger != null) {
					final short columnIndex = columnIndexInteger.shortValue();
					if (columnIndex >= 0) {

						if (variateVariable.getVariableType().getStandardVariable().getDataType().getName()
								.equals(DatasetExporter.NUMERIC_VARIABLE)) {
							double elemValue = 0;
							if (variateVariable.getValue() != null) {
								try {
									elemValue = Double.valueOf(variateVariable.getValue());

									if (elemValue == Double.valueOf("-1E+36")) {
										rowContent.add("");
									} else {
										rowContent.add(String.valueOf(elemValue));
									}

								} catch (final NumberFormatException ex) {
									String value = variateVariable.getValue();
									if (value != null) {
										value = value.trim();
									}
									rowContent.add(value);
								}
							} else {

								rowContent.add("");
							}
						} else if (variateVariable.getVariableType().getStandardVariable().getDataType().getId()
								== TermId.CATEGORICAL_VARIABLE.getId()) {
							String value = variateVariable.getActualValue();

							if (value == null) {
								value = "";
							} else {
								final List<ValueReference> possibleValues = ontologyService
										.getDistinctStandardVariableValues(variateVariable.getVariableType().getStandardVariable().getId());
								if (DatasetExporter.MISSING_VALUE_STRING.equals(value) && this
										.isCategoricalValueOutOfBounds(value, possibleValues)) {
									value = "";
								}
							}

							rowContent.add(value);
						} else {
							String value = variateVariable.getActualValue();
							if (value != null) {
								value = value.trim();
							} else {
								value = "";
							}
							rowContent.add(value);
						}
					}
				}

			} else {
				rowContent.add("");
			}
		}
	}

	protected boolean isCategoricalValueOutOfBounds(final String value, final List<ValueReference> possibleValues) {

		for (final ValueReference ref : possibleValues) {
			if (ref.getName().equals(value) || ref.getKey().equals(value)) {
				return false;
			}
		}

		return true;
	}

	public List<String[]> getTableItems() {
		return this.tableItems;
	}

	public Map<String, String> getHeaderNameAliasMap() {
		return this.headerNameAliasMap;
	}

	public WorkbenchDataManager getWorkbenchDataManager() {
		return this.workbenchDataManager;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

}
