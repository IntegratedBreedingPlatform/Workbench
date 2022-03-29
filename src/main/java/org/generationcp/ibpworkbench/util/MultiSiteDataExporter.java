package org.generationcp.ibpworkbench.util;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.constant.AppConstants;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.FileNameGenerator;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.WorkbenchContentApp;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configurable
public class MultiSiteDataExporter {

	protected static final String SUMMARY_STATS = "_SummaryStats";

	private static final Logger LOG = LoggerFactory.getLogger(MultiSiteDataExporter.class);

	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	@Autowired
	private StudyDataManager studyDataManager;

	@Resource
	private OntologyVariableService ontologyVariableService;

	public void generateXmlFieldBook(final GxeInput gxeInput) {
		try {
			final GxeXMLWriter writer = new GxeXMLWriter(gxeInput);
			writer.writeProjectXML();

		} catch (final GxeXMLWriterException e) {
			MultiSiteDataExporter.LOG.error("Error writng GxE XML file", e);
		}
	}

	public String exportMeansDatasetToCsv(final String inputFileName, final MultiSiteParameters multiSiteParameters,
		final List<Experiment> experiments, final String environmentName, final GxeEnvironment gxeEnv,
		final List<Trait> selectedTraits, final WorkbenchContentApp workbenchApplication) {

		final Project currentProject = multiSiteParameters.getProject();
		final String environmentGroup = multiSiteParameters.getSelectedEnvGroupFactorName();
		final String genotypeName = multiSiteParameters.getSelectedGenotypeFactorName();

		if (currentProject == null) {
			throw new IllegalArgumentException("current project is null");
		}

		final List<String[]> tableItems = new ArrayList<String[]>();

		final Map<String, Integer> traitToColNoMap = new HashMap<>();

		int i = 0, j = 0;
		// create header row
		final List<String> headerRow = new ArrayList<>();
		// site no && site code insert to columnMap
		if (environmentName != null && !environmentName.isEmpty()) {
			traitToColNoMap.put(environmentName, j);
			headerRow.add(BreedingViewUtil.sanitizeName(environmentName));
			j++;
		}

		if (!environmentGroup.equalsIgnoreCase(environmentName) && environmentGroup != null && !environmentGroup.isEmpty() && !"None"
			.equalsIgnoreCase(environmentGroup)) {
			traitToColNoMap.put(environmentGroup, j);
			headerRow.add(BreedingViewUtil.sanitizeName(environmentGroup));
			j++;
		}

		traitToColNoMap.put(genotypeName, j);
		headerRow.add(BreedingViewUtil.sanitizeName(genotypeName));
		j++;

		for (final Trait trait : selectedTraits) {

			traitToColNoMap.put(trait.getName(), j);
			headerRow.add(j, BreedingViewUtil.sanitizeName(trait.getName()));
			j++;
		}

		final String[] headerRowArr = new String[headerRow.size()];
		tableItems.add(i, headerRow.toArray(headerRowArr));

		i++;

		final List<String> gxeEnvLabels = new ArrayList<String>();
		for (final GxeEnvironmentLabel env : gxeEnv.getLabels()) {
			gxeEnvLabels.add(env.getName());
		}

		final int studyId = multiSiteParameters.getStudy().getId();
		final boolean isEnvironmentFactorALocationIdVariable = this.studyDataManager.isLocationIdVariable(studyId, environmentName);
		final Map<String, String> locationNameMap = this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId);

		boolean hasMissingMean = false;
		// create table content
		for (final Experiment experiment : experiments) {
			final String[] row = new String[headerRow.size()];

			// site no && site code insert to columnMap
			if (environmentName != null && !environmentName.isEmpty()) {
				Variable var = experiment.getFactors().findByLocalName(environmentName);

				if (var == null) {
					var = experiment.getVariates().findByLocalName(environmentName);
				}

				if (var != null && var.getValue() != null) {

					String variableValue = var.getValue();

					if (var.getVariableType().getLocalName().equalsIgnoreCase(environmentName) && isEnvironmentFactorALocationIdVariable) {
						variableValue = locationNameMap.get(variableValue);
					}

					if (!gxeEnvLabels.contains(variableValue)) {
						continue;
					}

					row[traitToColNoMap.get(environmentName)] = variableValue.replace(",", ";");

				}
			}

			for (final Entry<String, Integer> traitMapEntry : traitToColNoMap.entrySet()) {

				Variable var = experiment.getFactors().findByLocalName(traitMapEntry.getKey());

				if (var == null) {
					var = experiment.getVariates().findByLocalName(traitMapEntry.getKey());
				}

				if (var != null && !(var.getVariableType().getLocalName().equalsIgnoreCase(environmentName)
					&& isEnvironmentFactorALocationIdVariable)) {
					if (var.getValue() != null && !var.getValue().trim().matches("\\-1(\\.0+)?(E|e)(\\+36)")) {
						row[traitMapEntry.getValue()] = var.getValue().replace(",", ";");
					}
				}
				if (!hasMissingMean && row[traitMapEntry.getValue()] == null) {
					hasMissingMean = true;
				}
			}

			tableItems.add(i, row);

			i++;
		}
		if (hasMissingMean) {
			MessageNotifier
				.showWarning(workbenchApplication.getMainWindow(), "Warning", "Some traits have missing mean values for some locations.");
		}
		return this.writeToCsvFile(inputFileName, currentProject, tableItems, false);
	}

	public String exportSummaryStatisticsToCsvFile(final int studyId, final String inputFileName,
		final String environmentName, final List<Trait> selectedTraits, final Project currentProject) {

		if (currentProject == null) {
			throw new IllegalArgumentException("current project is null");
		}

		final List<String[]> tableItems = new ArrayList<>();
		final DataSet plotDataset =
			this.studyDataManager.findOneDataSetByType(studyId, DatasetTypeEnum.PLOT_DATA.getId());
		final DataSet summaryStatsDataSet =
			this.studyDataManager.findOneDataSetByType(studyId, DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId());
		final List<Experiment> experiments = this.studyDataManager.getExperiments(summaryStatsDataSet.getId(), 0, Integer.MAX_VALUE);

		final String[] header =
			new String[] {
				environmentName, "Trait", "NumValues", "NumMissing", "Mean", "Variance", "SD", "Min", "Max", "Range",
				"Median", "LowerQuartile", "UpperQuartile", "MeanRep", "MinRep", "MaxRep", "MeanSED", "MinSED", "MaxSED", "MeanLSD",
				"MinLSD", "MaxLSD", "CV", "Heritability", "WaldStatistic", "WaldDF", "Pvalue"

			};

		tableItems.add(header);

		final boolean isEnvironmentFactorALocationIdVariable = this.studyDataManager.isLocationIdVariable(studyId, environmentName);
		final Map<String, String> locationNameMap = this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId);

		if (CollectionUtils.isNotEmpty(experiments)) {
			final Map<String, Integer> methodsIdsMap = new CaseInsensitiveMap();
			for (final DMSVariableType variable : experiments.get(0).getVariates().getVariableTypes().getVariableTypes()) {
				methodsIdsMap.putIfAbsent(variable.getStandardVariable().getMethod().getName(),
					variable.getStandardVariable().getMethod().getId());
			}
			final Map<String, Integer> traitVariableIdsMap =
				new CaseInsensitiveMap(plotDataset.getVariableTypes().getVariates().getVariableTypes().stream().collect(
					Collectors.toMap(DMSVariableType::getLocalName, DMSVariableType::getId)));
			final MultiKeyMap analysisMethodsOfTraits =
				this.ontologyVariableService.getAnalysisMethodsOfTraits(new ArrayList<>(traitVariableIdsMap.values()),
					new ArrayList<>(methodsIdsMap.values()));

			for (final Experiment experiment : experiments) {

				final Map<Integer, Variable> analysisVariableValuesMap = experiment.getVariates().getVariables().stream()
					.collect(Collectors.toMap(v -> v.getVariableType().getId(), Function.identity()));

				for (final Trait trait : selectedTraits) {

					final List<String> row = new ArrayList<>();

					final Variable factorVariable = experiment.getFactors().findByLocalName(environmentName);
					String envValue = factorVariable.getValue();
					if (factorVariable.getVariableType().getLocalName().equalsIgnoreCase(environmentName)
						&& isEnvironmentFactorALocationIdVariable) {
						envValue = locationNameMap.get(factorVariable.getValue());
					}

					String traitValue = BreedingViewUtil.sanitizeName(trait.getName());
					if (envValue != null) {
						envValue = envValue.replaceAll(",", ";");
					}
					if (traitValue != null) {
						traitValue = traitValue.replaceAll(",", ";");
					}
					row.add(envValue);
					row.add(traitValue);

					for (int i = 2; i < header.length; i++) {
						final String traitName = trait.getName().replace("_Means", "");
						final Integer variableId = traitVariableIdsMap.get(traitName);
						final Integer methodId = methodsIdsMap.get(header[i]);
						final Integer analsisVariableId = (Integer) analysisMethodsOfTraits.get(variableId, methodId);

						if (analysisVariableValuesMap.containsKey(analsisVariableId)) {
							row.add(analysisVariableValuesMap.get(analsisVariableId).getValue());
						} else {
							row.add("");
						}
					}
					tableItems.add(row.toArray(new String[0]));
				}

			}
		}
		return this.writeToCsvFile(inputFileName, currentProject, tableItems, true);
	}

	String writeToCsvFile(final String inputFileName, final Project currentProject, final List<String[]> tableItems,
		final boolean isSummaryStatsFile) {
		final File csvFile = this.getCsvFileInWorkbenchDirectory(currentProject, inputFileName, isSummaryStatsFile);

		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(new FileWriter(csvFile), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
			csvWriter.writeAll(tableItems);
			csvWriter.flush();
			csvWriter.close();

			return csvFile.getAbsolutePath();

		} catch (final IOException e) {
			MultiSiteDataExporter.LOG.warn(e.getMessage(), e);
			return null;
		}
	}

	File getCsvFileInWorkbenchDirectory(final Project currentProject, final String inputFileName, final boolean isSummaryStatsFile) {

		this.installationDirectoryUtil
			.createWorkspaceDirectoriesForProject(currentProject.getCropType().getCropName(), currentProject.getProjectName());
		final String directory = this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(currentProject, ToolName.BREEDING_VIEW);
		final StringBuilder sb = new StringBuilder(inputFileName);
		if (isSummaryStatsFile) {
			sb.append(MultiSiteDataExporter.SUMMARY_STATS);
		}
		final String fileName = FileNameGenerator.generateFileName(sb.toString(), AppConstants.EXPORT_CSV_SUFFIX.getString());

		return new File(directory + File.separator + fileName);
	}

	public void setInstallationDirectoryUtil(final InstallationDirectoryUtil installationDirectoryUtil) {
		this.installationDirectoryUtil = installationDirectoryUtil;
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	protected void setOntologyVariableService(final OntologyVariableService ontologyVariableService) {
		this.ontologyVariableService = ontologyVariableService;
	}
}
