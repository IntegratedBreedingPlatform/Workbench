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

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private OntologyService ontologyService;

	private final Integer datasetId;
	private final List<String[]> tableItems = new ArrayList<>();
	private final List<String> columns = new ArrayList<>();
	private final Map<String, String> headerNameAliasMap = new HashMap<>();

	private static final String MISSING_VALUE_STRING = "missing";

	public DatasetExporter(final Integer datasetId) {
		this.datasetId = datasetId;
	}

	protected List<String> generateFactorColumnsList(final DataSet dataset) {

		final List<String> factorsColumns = new ArrayList<>();
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

			if (!factorsColumns.contains(factorName) && !"STUDY".equals(factorName)) {
				// add entry to columns mapping
				factorsColumns.add(factorName);
			}
		}

		return factorsColumns;

	}

	protected List<String> generateVariateColumnsList(final DataSet dataset, final BreedingViewInput breedingViewInput) {

		final List<String> variateColumns = new ArrayList<>();
		final List<DMSVariableType> variateVariableTypes = dataset.getVariableTypes().getVariates().getVariableTypes();

		for (final DMSVariableType variate : variateVariableTypes) {

			final String variateName = variate.getLocalName();

			// get only the selected traits
			if (breedingViewInput.getVariatesActiveState().get(variateName).booleanValue()) {
				// add entry to columns mapping
				variateColumns.add(variateName);
			}

		}

		return variateColumns;
	}

	/**
	 * Exports the observation of a Trial to a CSV file.
	 *
	 * @param filename
	 * @param selectedFactor
	 * @param selectedEnvironment
	 * @param breedingViewInput
	 * @throws DatasetExporterException
	 */
	public void exportToCSVForBreedingView(final String filename, final String selectedFactor, final List<String> selectedEnvironment,
			final BreedingViewInput breedingViewInput) throws DatasetExporterException {

		final Map<Integer, String> selectEnvironmentsMap = new HashMap<>();
		if (!selectedFactor.equalsIgnoreCase(breedingViewInput.getTrialInstanceName())) {
			for (final SeaEnvironmentModel model : breedingViewInput.getSelectedEnvironments()) {
				selectEnvironmentsMap.put(model.getLocationId(), model.getEnvironmentName());
			}
		}

		final DataSet dataset = this.studyDataManager.getDataSet(this.datasetId);

		if (dataset == null) {
			return;
		}

		final List<String> factorColumns = this.generateFactorColumnsList(dataset);
		final List<String> variateColumns = this.generateVariateColumnsList(dataset, breedingViewInput);

		this.columns.addAll(factorColumns);
		this.columns.addAll(variateColumns);

		if (!breedingViewInput.getDesignType().equals(DesignType.P_REP_DESIGN.getName()) && !breedingViewInput.getDesignType()
				.equals(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName()) && DatasetExporter.DUMMY_REPLICATES
				.equals(breedingViewInput.getReplicatesFactorName())) {
			this.columns.add(DatasetExporter.DUMMY_REPLICATES);
		}

		boolean trialEnvironmentFactorAlreadyExists = true;
		if (!selectedFactor.equalsIgnoreCase(breedingViewInput.getTrialInstanceName()) && !this.columns.contains(selectedFactor)) {
			this.columns.add(selectedFactor);
			trialEnvironmentFactorAlreadyExists = false;
		}

		List<Experiment> experiments = new ArrayList<>();

		try {
			experiments = this.studyDataManager.getExperiments(this.datasetId, 0, Integer.MAX_VALUE);
		} catch (final Exception ex) {
			DatasetExporter.LOG.error(ex.getMessage(), ex);
		}

		// The first item in the table should be the header
		// Add the columns list to the table
		this.getTableItems().add(sanitizeColumnNames(columns));

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

			this.populateRowWithFactorValuesFromExperiment(factorColumns, row, experiment, breedingViewInput);

			this.populateRowWithVariateValuesFromExperiment(variateColumns, row, experiment, this.ontologyService);

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

	private String[] sanitizeColumnNames(final List<String> columns) {

		List<String> sanitized = new ArrayList<>();

		for (String column : columns) {
			sanitized.add(BreedingViewUtil.trimAndSanitizeName(column));
		}

		return sanitized.toArray(new String[0]);

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

	/**
	 * Adds the values of the factors variables from the Experiment to the target experiment row data list array.
	 *
	 * @param factorColumnsMap
	 * @param experimentRowData
	 * @param currentExperiment
	 * @param breedingViewInput
	 */
	protected void populateRowWithFactorValuesFromExperiment(final List<String> factorColumns, final List<String> experimentRowData,
			final Experiment currentExperiment, final BreedingViewInput breedingViewInput) {

		final List<Variable> factorsOfExperiments = currentExperiment.getFactors().getVariables();
		final Map<String, Variable> factorsOfExperimentsMap = new HashMap<>();
		for (final Variable factorVariable : factorsOfExperiments) {
			factorsOfExperimentsMap.put(factorVariable.getVariableType().getLocalName().trim(), factorVariable);
		}

		for (final String factorName : factorColumns) {

			if (factorsOfExperimentsMap.containsKey(factorName)) {

				final Variable factorVariable = factorsOfExperimentsMap.get(factorName);

				if (factorVariable.getVariableType().getStandardVariable().getDataType().getName()
						.equals(DatasetExporter.NUMERIC_VARIABLE)) {
					double elemValue = 0;
					if (factorVariable.getValue() != null) {

						if (factorVariable.getValue().isEmpty() && factorVariable.getVariableType().getLocalName()
								.equalsIgnoreCase(breedingViewInput.getReplicates().getName())) {
							final Variable variable = factorsOfExperimentsMap.get(breedingViewInput.getBlocks().getName());
							if (variable != null) {
								experimentRowData.add(variable.getValue().trim());
							} else {
								experimentRowData.add("");
							}

						} else {
							try {
								elemValue = Double.valueOf(factorVariable.getValue());

								if (elemValue == Double.valueOf("-1E+36")) {
									experimentRowData.add("");
								} else {
									experimentRowData.add(String.valueOf(factorVariable.getValue()));
								}
							} catch (final NumberFormatException ex) {
								String value = factorVariable.getValue();
								if (value != null) {
									value = value.trim();
								}
								experimentRowData.add(value);
							}
						}

					} else {
						final String nullValue = null;
						experimentRowData.add(nullValue);
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
					experimentRowData.add(value);
				}

			} else if (TermId.PLOT_ID.name().equals(factorName)) {

				// special case
				experimentRowData.add(currentExperiment.getPlotId());

			} else {

				experimentRowData.add("");
			}

		}

	}

	/**
	 * Adds the values of the variate variables from the Experiment to the target experiment row data list array.
	 *
	 * @param variateColumnsMap
	 * @param experimentRowData
	 * @param currentExperiment
	 * @param ontologyService
	 */
	protected void populateRowWithVariateValuesFromExperiment(final List<String> variateColumns, final List<String> experimentRowData,
			final Experiment currentExperiment, final OntologyService ontologyService) {

		for (final String variateColumnName : variateColumns) {

			final Variable variateVariable = currentExperiment.getVariates().findByLocalName(variateColumnName);
			if (variateVariable != null) {

				if (columns.contains(variateColumnName)) {

					if (variateVariable.getVariableType().getStandardVariable().getDataType().getName()
							.equals(DatasetExporter.NUMERIC_VARIABLE)) {
						double elemValue = 0;
						if (variateVariable.getValue() != null) {
							try {
								elemValue = Double.valueOf(variateVariable.getValue());

								if (elemValue == Double.valueOf("-1E+36")) {
									experimentRowData.add("");
								} else {
									experimentRowData.add(String.valueOf(elemValue));
								}

							} catch (final NumberFormatException ex) {
								String value = variateVariable.getValue();
								if (value != null) {
									value = value.trim();
								}
								experimentRowData.add(value);
							}
						} else {

							experimentRowData.add("");
						}
					} else if (variateVariable.getVariableType().getStandardVariable().getDataType().getId() == TermId.CATEGORICAL_VARIABLE
							.getId()) {
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

						experimentRowData.add(value);
					} else {
						String value = variateVariable.getActualValue();
						if (value != null) {
							value = value.trim();
						} else {
							value = "";
						}
						experimentRowData.add(value);
					}

				}

			} else {
				experimentRowData.add("");
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
