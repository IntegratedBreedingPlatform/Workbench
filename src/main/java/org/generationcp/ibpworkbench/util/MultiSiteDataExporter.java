package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;

import au.com.bytecode.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class MultiSiteDataExporter {

	private static final Logger LOG = LoggerFactory.getLogger(MultiSiteDataExporter.class);
	
	@Autowired
	private WorkbenchDataManager workbenchDataManager;
	
	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	/**
	 * Generates GxE Multi-site analysis XML data, stored in workpace directory
	 *
	 * @return void
	 */

	public void generateXmlFieldBook(final GxeInput gxeInput) {
		try {
			final GxeXMLWriter writer = new GxeXMLWriter(gxeInput);
			writer.writeProjectXML();

		} catch (final GxeXMLWriterException e) {
			MultiSiteDataExporter.LOG.error("Error writng GxE XML file", e);
		}
	}

	public String exportMeansDatasetToCsv(final String inputFileName, final MultiSiteParameters multiSiteParameters,
			final DataSet gxeDataset, final List<Experiment> experiments, final String environmentName, final GxeEnvironment gxeEnv,
			final List<Trait> selectedTraits) {

		Project currentProject = multiSiteParameters.getProject();
		String environmentGroup = multiSiteParameters.getSelectedEnvGroupFactorName();
		String genotypeName = multiSiteParameters.getSelectedGenotypeFactorName();

		if (currentProject == null) {
			throw new IllegalArgumentException("current project is null");
		}

		final List<String[]> tableItems = new ArrayList<String[]>();

		final Map<String, Integer> traitToColNoMap = new Hashtable<String, Integer>();

		int i = 0, j = 0;
		// create header row
		final List<String> headerRow = new ArrayList<String>();
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
					if (!gxeEnvLabels.contains(var.getValue())) {
						continue;
					}
					row[traitToColNoMap.get(environmentName)] = var.getValue().replace(",", ";");
				}
			}

			for (final Entry<String, Integer> traitMapEntry : traitToColNoMap.entrySet()) {
				Variable var = experiment.getFactors().findByLocalName(traitMapEntry.getKey());

				if (var == null) {
					var = experiment.getVariates().findByLocalName(traitMapEntry.getKey());
				}

				if (var != null && var.getValue() != null && !var.getValue().trim().matches("\\-1(\\.0+)?(E|e)(\\+36)")) {
					row[traitMapEntry.getValue()] = var.getValue().replace(",", ";");
				}

			}

			tableItems.add(i, row);

			i++;
		}

		try {
			final String dir = this.getWorkspaceInputDirectoryPath(currentProject);
			final File csvFile = new File(dir + File.separator + inputFileName + ".csv");

			final CSVWriter csvWriter =
					new CSVWriter(new FileWriter(csvFile), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
			csvWriter.writeAll(tableItems);
			csvWriter.flush();
			csvWriter.close();

			return csvFile.getAbsolutePath();
		} catch (final IOException e) {
			MultiSiteDataExporter.LOG.warn(e.getMessage(), e);
			return null;
		}
	}

	public String exportTrialDatasetToSummaryStatsCsv(final String inputFileName, final DataSet trialDataSet,
			final List<Experiment> experiments, final String environmentName, final List<Trait> selectedTraits,
			final Project currentProject) {

		if (currentProject == null) {
			throw new IllegalArgumentException("current project is null");
		}

		final List<String[]> tableItems = new ArrayList<String[]>();

		final String[] header =
				new String[] {environmentName, "Trait", "NumValues", "NumMissing", "Mean", "Variance", "SD", "Min", "Max", "Range",
						"Median", "LowerQuartile", "UpperQuartile", "MeanRep", "MinRep", "MaxRep", "MeanSED", "MinSED", "MaxSED", "MeanLSD",
						"MinLSD", "MaxLSD", "CV", "Heritability", "WaldStatistic", "WaldDF", "Pvalue"

				};

		tableItems.add(header);

		for (final Experiment exp : experiments) {

			final Map<String, Variable> map = exp.getVariatesMap();

			for (final Trait trait : selectedTraits) {

				final List<String> row = new ArrayList<String>();
				String envValue = exp.getFactors().findByLocalName(environmentName).getValue();
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
					boolean existsFlag = false;
					for (final Variable variable : map.values()) {
						if (variable.getVariableType().getLocalName().equals(trait.getName().replace("_Means", "") + "_" + header[i])) {
							row.add(variable.getValue());
							existsFlag = true;
							break;
						}
					}
					if (!existsFlag) {
						row.add("");
					}
				}

				tableItems.add(row.toArray(new String[0]));
			}

		}
		final String dir = this.getWorkspaceInputDirectoryPath(currentProject);
		final File csvFile = new File(dir + File.separator + inputFileName + "_SummaryStats.csv");

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

	private String getWorkspaceInputDirectoryPath(final Project currentProject) {
		final Tool breedingViewTool = this.workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
		return this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(currentProject, breedingViewTool);
	}

}
