package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.api.role.RoleService;
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
    private RoleService roleService;

    @Autowired
    private StudyDataManager studyDataManager;

    @Autowired
    private OntologyService ontologyService;

    private List<String[]> rowsToWrite = new ArrayList<>();

    private static final String MISSING_VALUE_STRING = "missing";
    private Integer datasetId;

    /**
     * Return list of factor names in given dataset
     *
     * @param dataset - dataset to extract factor names from
     * @return list of factor names
     */
    List<String> generateFactorColumnsList(final DataSet dataset) {

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

    /**
     * Return list of variate names of given dataset. Only include variates that were selected in SSA
     *
     * @param dataset           - dataset to extract variate names from
     * @param breedingViewInput - contains list of variates chosen
     * @return list of selected variate names
     */
    List<String> generateVariateColumnsList(final DataSet dataset, final BreedingViewInput breedingViewInput) {

        final List<String> variateColumns = new ArrayList<>();
        final List<DMSVariableType> variateVariableTypes = dataset.getVariableTypes().getVariates().getVariableTypes();

        for (final DMSVariableType variate : variateVariableTypes) {

            final String variateName = variate.getLocalName();

            // Include only the selected traits and covariates
            if (breedingViewInput.getVariatesSelectionMap().get(variateName).booleanValue()) {
                variateColumns.add(variateName);
            }
            if (breedingViewInput.getCovariatesSelectionMap().get(variateName).booleanValue()) {
                variateColumns.add(variateName);
            }

        }

        return variateColumns;
    }

    /**
     * Exports the experiments of a Trial to a CSV file.
     *
     * @param filename                  - name of CSV file to be generated
     * @param selectedEnvironmentFactor - name of factor selected that will uniquely identify each environment in dataset
     * @param selectedEnvironments      - list of environments to generate observations for
     * @param breedingViewInput         - contains configurations for exporting dataset
     */
    public void exportToCSVForBreedingView(final String filename, final String selectedEnvironmentFactor, final List<String> selectedEnvironments,
                                           final BreedingViewInput breedingViewInput) {

        final DataSet dataset = this.studyDataManager.getDataSet(this.datasetId);
        if (dataset == null) {
            return;
        }

        // Consolidate factors and traits of dataset as column headers
        final List<String> factorColumns = this.generateFactorColumnsList(dataset);
        final List<String> variateColumns = this.generateVariateColumnsList(dataset, breedingViewInput);
        final List<String> columnNames = new ArrayList<>();
        columnNames.addAll(factorColumns);
        columnNames.addAll(variateColumns);

        // FIXME See if this can be removed. This was a hack for old (pre BMS 3.0) datasets that did not have REP variable
        if (this.isDummyRepVariableUsed(breedingViewInput)) {
            columnNames.add(DatasetExporter.DUMMY_REPLICATES);
        }

        // Add column for selected environment factor if not in list of columnNames generated from factors and variates
        // and selected environment factor is not TRIAL INSTANCE (eg. LOCATION_NAME)
        boolean selectedEnvFactorInColumnList = true;
        if (!selectedEnvironmentFactor.equalsIgnoreCase(breedingViewInput.getTrialInstanceName()) && !columnNames.contains(selectedEnvironmentFactor)) {
            columnNames.add(selectedEnvironmentFactor);
            selectedEnvFactorInColumnList = false;
        }

        this.rowsToWrite = new ArrayList<>();
        // Set column names as first row to be written in file
        this.getRowsToWrite().add(this.sanitizeColumnNames(columnNames));

        // Generate rows for experiments of selected environments
        this.generateExperimentRows(selectedEnvironments, breedingViewInput, factorColumns, variateColumns, selectedEnvironmentFactor,
                selectedEnvFactorInColumnList);

        // Create CSV file
        this.writeCSVFile(filename);

    }

    void generateExperimentRows(final List<String> selectedEnvironments, final BreedingViewInput breedingViewInput,
                                final List<String> factorColumns, final List<String> variateColumns, final String selectedEnvironmentFactor, final boolean trialEnvFactorInColumnList) {

        // If selected environment factor is not TRIAL_INSTANCE, create a map of location id, name for selected environments
        final Map<Integer, String> selectEnvironmentsMap = new HashMap<>();
        if (!selectedEnvironmentFactor.equalsIgnoreCase(breedingViewInput.getTrialInstanceName())) {
            for (final SeaEnvironmentModel model : breedingViewInput.getSelectedEnvironments()) {
                selectEnvironmentsMap.put(model.getLocationId(), model.getEnvironmentName());
            }
        }

        final List<Experiment> experiments = this.studyDataManager.getExperiments(this.datasetId, 0, Integer.MAX_VALUE);
        for (final Experiment experiment : experiments) {
            // Only include experiments that are in selected trial instances/environment(s)
            final boolean experimentIsInSelectedEnvironments =
                    this.isExperimentInSelectedEnvironments(breedingViewInput, selectedEnvironments, experiment);

            if (experimentIsInSelectedEnvironments) {
                final List<String> rowValues = new ArrayList<>();
                this.populateRowWithFactorValuesFromExperiment(factorColumns, rowValues, experiment, breedingViewInput);
                this.populateRowWithVariateValuesFromExperiment(variateColumns, rowValues, experiment);

                // add "1" value for REP variable if dummy REPLICATES column was used
                if (this.isDummyRepVariableUsed(breedingViewInput)) {
                    rowValues.add("1");
                }

                // If selected environment factor is not TRIAL_INSTANCE (eg. LOCATION_NAME), it's possible to have "," (comma)
                // Replace any comma with ";" since extra comma will cause wrong data alignment in CSV file
                if (!trialEnvFactorInColumnList) {
                    rowValues.add(selectEnvironmentsMap.get(experiment.getLocationId()).trim().replace(",", ";"));
                }

                this.getRowsToWrite().add(rowValues.toArray(new String[0]));
            }
        }
    }

    boolean isDummyRepVariableUsed(final BreedingViewInput breedingViewInput) {
        return !ExperimentDesignType.P_REP.getBvName().equals(breedingViewInput.getDesignType())
                && !ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getBvName().equals(breedingViewInput.getDesignType())
                && DatasetExporter.DUMMY_REPLICATES.equals(breedingViewInput.getReplicatesFactorName());
    }

    /**
     * Checks TRIAL_INSTANCE value of experiment and returns true if it belongs to one of selected trial environments
     *
     * @param breedingViewInput
     * @param selectedEnvironment
     * @param experiment
     * @return
     */
    boolean isExperimentInSelectedEnvironments(final BreedingViewInput breedingViewInput, final List<String> selectedEnvironment,
                                               final Experiment experiment) {
        for (final Variable factorVariable : experiment.getFactors().getVariables()) {
            if (factorVariable.getVariableType().getLocalName().trim().equalsIgnoreCase(breedingViewInput.getTrialInstanceName())
                    && selectedEnvironment.contains(factorVariable.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Replaces all "invalid characters" in a column names with an underscore.
     * Only alphanumeric (a-z A-Z), dash (-), underscore (_) and percentage (%) characters are allowed in Breeding View.
     *
     * @return array of column names with invalid characters replaced with underscore
     */
    String[] sanitizeColumnNames(final List<String> columnNames) {

        final List<String> sanitized = new ArrayList<>();

        for (final String column : columnNames) {
            sanitized.add(BreedingViewUtil.trimAndSanitizeName(column));
        }

        return sanitized.toArray(new String[0]);

    }

    protected void writeCSVFile(final String filename) {
        try {
            final File csvFile = new File(filename);
            final CSVWriter csvWriter =
                    new CSVWriter(new FileWriter(csvFile), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
            csvWriter.writeAll(this.getRowsToWrite());
            csvWriter.flush();
            csvWriter.close();
        } catch (final Exception e) {
            DatasetExporter.LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Adds the values of the factors variables from the given experiment to list of values for row.
     *
     * @param factorColumns     - list of factor column names
     * @param experimentRowData - list of values for the experiment row
     * @param currentExperiment - experiment to extract factor values from
     * @param breedingViewInput - contains configurations for exporting dataset
     */
    void populateRowWithFactorValuesFromExperiment(final List<String> factorColumns, final List<String> experimentRowData,
                                                   final Experiment currentExperiment, final BreedingViewInput breedingViewInput) {

        // Create map of factors for given experiment as it is possible for dataset factor not to be present for experiment
        // (eg. FIELDMAP_COLUMN, FIELDMAP_RANGE. OBS_UNIT_ID)
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

                            // breedingViewInput.getReplicates() is null if the study design is Augmented Design.
                            if (value.isEmpty() && breedingViewInput.getReplicates() != null && factorVariable.getVariableType().getLocalName()
                                    .equalsIgnoreCase(breedingViewInput.getReplicates().getName())) {
                                final Variable variable = factorsOfExperimentsMap.get(breedingViewInput.getBlocks().getName());
                                if (variable != null) {
                                    value = variable.getValue().trim();
                                } else {
                                    value = "";
                                }

                            } else if (factorVariable.getVariableType().getLocalName().equalsIgnoreCase(TermId.ENTRY_TYPE.name())) {
                                value = factorVariable.getActualValue();
                            }

                        } else {
                            value = value.trim();
                        }

                    }
                    experimentRowData.add(value);
                }

                // Special Case
            } else if (TermId.OBS_UNIT_ID.name().equals(factorName)) {
                experimentRowData.add(currentExperiment.getObsUnitId());

                // If dataset factor is not in current experiment (eg. FIELDMAP_COLUMN, FIELDMAP_RANGE), write blank value
                // So as to avoid wrong alignment of subsequent row data to proper column
            } else {
                experimentRowData.add("");
            }

        }

    }

    /**
     * Adds the values of the variate variables from the Experiment to the target experiment row data list array.
     *
     * @param variateColumns    - list of variate column names
     * @param experimentRowData - list of values for the experiment row
     * @param currentExperiment - experiment to extract variate observations from
     */
    void populateRowWithVariateValuesFromExperiment(final List<String> variateColumns, final List<String> experimentRowData,
                                                    final Experiment currentExperiment) {

        for (final String variateColumnName : variateColumns) {

            final Variable variateVariable = currentExperiment.getVariates().findByLocalName(variateColumnName);
            if (variateVariable != null) {
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
                        final List<ValueReference> possibleValues = this.ontologyService
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

    public List<String[]> getRowsToWrite() {
        return this.rowsToWrite;
    }

    public RoleService getRoleService() {
        return this.roleService;
    }

    public void setRoleService(final RoleService roleService) {
        this.roleService = roleService;
    }


    public void setDatasetId(final Integer datasetId) {
        this.datasetId = datasetId;
    }

}
