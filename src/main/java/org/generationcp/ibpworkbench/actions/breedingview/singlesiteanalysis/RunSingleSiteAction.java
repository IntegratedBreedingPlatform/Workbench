/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.google.common.base.Strings;
import com.mysql.jdbc.StringUtils;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.ColPos;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.RowPos;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.FileNameGenerator;
import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.commons.util.ZipUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriter;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriterException;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeffrey Morales
 */
@Configurable
public class RunSingleSiteAction implements ClickListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(RunSingleSiteAction.class);

	private SingleSiteAnalysisDetailsPanel source;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private ContextUtil contextUtil;

	private ZipUtil zipUtil = new ZipUtil();
	private DatasetExporter datasetExporter = new DatasetExporter();
	private BreedingViewXMLWriter breedingViewXMLWriter = new BreedingViewXMLWriter();

	public RunSingleSiteAction(final SingleSiteAnalysisDetailsPanel selectDetailsForBreedingViewWindow) {
		this.source = selectDetailsForBreedingViewWindow;
	}

	@Override
	public void buttonClick(final ClickEvent event) {

		final Window window = event.getComponent().getWindow();
		final BreedingViewInput breedingViewInput = this.source.getBreedingViewInput();

		final long environmentNameDistinctCount =
			this.source.getSelectedEnvironments().stream().map(SeaEnvironmentModel::getEnvironmentName).distinct().count();
		if (environmentNameDistinctCount != this.source.getSelectedEnvironments().size()) {
			MessageNotifier.showError(this.source.getWindow(), this.messageSource.getMessage(Message.ERROR),
				this.messageSource.getMessage(Message.SSA_EXPORT_DUPLICATE_ENVIRONMENT_ERROR));
		} else {
			this.download(window, breedingViewInput);
		}

	}

	private void download(final Window window, final BreedingViewInput breedingViewInput) {

		breedingViewInput.setSelectedEnvironments(this.source.getSelectedEnvironments());

		if (this.validateDesignInput(window, breedingViewInput)) {

			this.populateBreedingViewInputFromUserInput(breedingViewInput);

			this.exportData(breedingViewInput);

			this.writeProjectXML(window, breedingViewInput);

			final List<String> filenameList = new ArrayList<>();
			filenameList.add(breedingViewInput.getDestXMLFilePath());
			filenameList.add(breedingViewInput.getSourceXLSFilePath());

			final String outputFilename =
				BreedingViewUtil.sanitizeNameAlphaNumericOnly(FileNameGenerator.generateFileName(breedingViewInput.getDatasetSource()));
			try {
				final String finalZipfileName =
					this.zipUtil.zipIt(outputFilename, filenameList, this.contextUtil.getProjectInContext(), ToolName.BV_SSA);
				this.downloadInputFile(new File(finalZipfileName), outputFilename);
			} catch (final IOException e) {
				RunSingleSiteAction.LOG.error("Error creating zip file " + outputFilename + ZipUtil.ZIP_EXTENSION, e);
				this.showErrorMessage(this.source.getApplication().getMainWindow(), "Error creating zip file.", "");
			}

		}
	}

	/**
	 * Generates the CSV input file to be used in Breeding View application.
	 *
	 * @param breedingViewInput
	 */
	void exportData(final BreedingViewInput breedingViewInput) {

		this.datasetExporter.setDatasetId(breedingViewInput.getDatasetId());

		final List<String> selectedEnvironments = new ArrayList<String>();
		for (final SeaEnvironmentModel m : breedingViewInput.getSelectedEnvironments()) {
			selectedEnvironments.add(m.getTrialno());
		}

		this.datasetExporter.exportToCSVForBreedingView(breedingViewInput.getSourceXLSFilePath(), this.source.getSelEnvFactorValue(),
			selectedEnvironments, breedingViewInput);
	}

	/**
	 * Populate the necessary data in BreedingViewInput that will be used to build the XML Input for Breeding View
	 *
	 * @param breedingViewInput
	 */
	void populateBreedingViewInputFromUserInput(final BreedingViewInput breedingViewInput) {

		// TODO: Move the creation of breeding view xml objects in BreedingViewXMLWriter.

		final ExperimentDesignType designType = ExperimentDesignType.getExperimentDesignTypeByBVName(this.source.getSelDesignTypeValue());
		breedingViewInput.setDesignType(designType.getBvName());

		breedingViewInput.setBreedingViewAnalysisName(this.source.getTxtAnalysisNameValue());

		breedingViewInput.setEnvironment(this.createEnvironment(this.source.getSelEnvFactorValue()));

		breedingViewInput.setReplicates(this.createReplicates(designType, this.source.getSelReplicatesValue()));

		breedingViewInput.setReplicatesFactorName(this.source.getSelReplicatesValue());

		breedingViewInput.setBlocks(this.createBlocks(this.source.getSelBlocksValue()));

		this.populateRowAndColumn(designType, breedingViewInput);

		this.populateRowPosAndColPos(designType, breedingViewInput);

		breedingViewInput.setGenotypes(this.createGenotypes(breedingViewInput.getDatasetId(), this.source.getSelGenotypesValue()));

		breedingViewInput.setPlot(this.createPlot(breedingViewInput.getDatasetId()));

	}

	void populateRowPosAndColPos(final ExperimentDesignType designType, final BreedingViewInput breedingViewInput) {
		final String columnValue = this.source.getSelColumnFactorValue();
		final String rowValue = this.source.getSelRowFactorValue();
		// Do not generate RowPos and ColPos tags for Row-Col Design type
		if (!StringUtils.isNullOrEmpty(rowValue) && !StringUtils.isNullOrEmpty(columnValue)
			&& !ExperimentDesignType.ROW_COL.equals(designType)) {
			breedingViewInput.setColPos(this.createColPos(columnValue));
			breedingViewInput.setRowPos(this.createRowPos(rowValue));
		} else {
			breedingViewInput.setColPos(null);
			breedingViewInput.setRowPos(null);
		}

	}

	void populateRowAndColumn(final ExperimentDesignType designType, final BreedingViewInput breedingViewInput) {

		if (designType.getId() == ExperimentDesignType.ROW_COL.getId()) {

			breedingViewInput.setColumns(this.createColumns(this.source.getSelColumnFactorValue()));
			breedingViewInput.setRows(this.createRows(this.source.getSelRowFactorValue()));

		} else {

			breedingViewInput.setColumns(null);
			breedingViewInput.setRows(null);
		}

	}

	Environment createEnvironment(final String environmentFactor) {

		final Environment environment = new Environment();
		environment.setName(BreedingViewUtil.trimAndSanitizeName(environmentFactor));
		return environment;

	}

	Replicates createReplicates(final ExperimentDesignType designType, final String replicatesFactor) {

		if (designType.equals(ExperimentDesignType.P_REP) || designType.equals(ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK)) {

			// Do not include the replicates factor if the design type is P-rep and Augmented Randomized design.
			return null;

		} else if (!StringUtils.isNullOrEmpty(replicatesFactor)) {
			final Replicates reps = new Replicates();
			reps.setName(BreedingViewUtil.trimAndSanitizeName(replicatesFactor));
			return reps;
		} else {

			// We need the replicates factor in performing analysis. If it is not available in a study,
			// blocks factor can be used as as substitute. But if both replicates factor and blocks factor are not available,
			// the system wouldn't be able to run the analysis. When this happens we should create a dummy replicates factor (in xml and csv
			// input)
			// so that the system can still proceed with analysis.

			final Replicates reps = new Replicates();
			reps.setName(DatasetExporter.DUMMY_REPLICATES);
			return reps;
		}

	}

	Rows createRows(final String rowFactor) {

		// TODO: We should not return null objects.

		if (!StringUtils.isNullOrEmpty(rowFactor)) {
			final Rows rows = new Rows();
			rows.setName(BreedingViewUtil.trimAndSanitizeName(rowFactor));
			return rows;
		} else {
			return null;
		}

	}

	Columns createColumns(final String columnFactor) {

		if (!StringUtils.isNullOrEmpty(columnFactor)) {
			final Columns columns = new Columns();
			columns.setName(BreedingViewUtil.trimAndSanitizeName(columnFactor));
			return columns;
		} else {
			return null;
		}

	}

	RowPos createRowPos(final String rowPosFactor) {

		if (!StringUtils.isNullOrEmpty(rowPosFactor)) {
			final RowPos rowPos = new RowPos();
			rowPos.setName(BreedingViewUtil.trimAndSanitizeName(rowPosFactor));
			return rowPos;
		} else {
			return null;
		}

	}

	ColPos createColPos(final String colPosFactor) {

		if (!StringUtils.isNullOrEmpty(colPosFactor)) {
			final ColPos colPos = new ColPos();
			colPos.setName(BreedingViewUtil.trimAndSanitizeName(colPosFactor));
			return colPos;
		} else {
			return null;
		}

	}

	Blocks createBlocks(final String blocksFactor) {

		if (!StringUtils.isNullOrEmpty(blocksFactor)) {
			final Blocks blocks = new Blocks();
			blocks.setName(BreedingViewUtil.trimAndSanitizeName(blocksFactor));
			return blocks;
		} else {
			return null;
		}

	}

	Plot createPlot(final int datasetId) {

		String plotNoFactor = this.studyDataManager.getLocalNameByStandardVariableId(datasetId, TermId.PLOT_NO.getId());

		if (Strings.isNullOrEmpty(plotNoFactor)) {
			plotNoFactor = this.studyDataManager.getLocalNameByStandardVariableId(datasetId, TermId.PLOT_NNO.getId());
		}

		if (!Strings.isNullOrEmpty(plotNoFactor)) {
			final Plot plot = new Plot();
			plot.setName(BreedingViewUtil.trimAndSanitizeName(plotNoFactor));
			return plot;
		} else {
			return null;
		}

	}

	Genotypes createGenotypes(final int datasetId, final String genotypesFactor) {

		final String entryNoFactor = this.studyDataManager.getLocalNameByStandardVariableId(datasetId, TermId.ENTRY_NO.getId());

		final Genotypes genotypes = new Genotypes();
		genotypes.setName(BreedingViewUtil.trimAndSanitizeName(genotypesFactor));
		genotypes.setEntry(entryNoFactor);

		return genotypes;

	}

	/**
	 * Validates the user input from Single-Site Analysis' Design Details form Returns true if the all inputs are valid, otherwise false.
	 *
	 * @param window
	 * @param breedingViewInput
	 * @return
	 */
	boolean validateDesignInput(final Window window, final BreedingViewInput breedingViewInput) {

		final String analysisProjectName = this.source.getTxtAnalysisNameValue();
		final String environmentFactor = this.source.getSelEnvFactorValue();
		final String designType = this.source.getSelDesignTypeValue();
		final String replicatesFactor = this.source.getSelReplicatesValue();
		final String blocksFactor = this.source.getSelBlocksValue();
		final String columnFactor = this.source.getSelColumnFactorValue();
		final String rowFactor = this.source.getSelRowFactorValue();
		final String genotypeFactor = this.source.getSelGenotypesValue();

		if (StringUtils.isNullOrEmpty(analysisProjectName)) {
			this.showErrorMessage(window, "Please enter an Analysis Name.", "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(environmentFactor)) {
			this.showErrorMessage(window, this.messageSource.getMessage(Message.SSA_SELECT_ENVIRONMENT_FACTOR_WARNING), "");
			return false;
		}

		if (breedingViewInput.getSelectedEnvironments().isEmpty()) {
			this.showErrorMessage(window, this.messageSource.getMessage(Message.SSA_SELECT_ENVIRONMENT_FACTOR_WARNING), "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(designType)) {
			this.showErrorMessage(window, "Please specify design type.", "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(replicatesFactor) && designType.equals(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName())
			&& this.source.replicateFactorEnabled()) {
			this.showErrorMessage(window, "Please specify replicates factor.", "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(blocksFactor) && (designType.equals(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName())
			|| designType.equals(ExperimentDesignType.P_REP.getBvName()))) {
			this.showErrorMessage(window, "Please specify incomplete block factor.", "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(columnFactor) && designType.equals(ExperimentDesignType.ROW_COL.getBvName())) {
			this.showErrorMessage(window, "Please specify column factor.", "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(rowFactor) && designType.equals(ExperimentDesignType.ROW_COL.getBvName())) {
			this.showErrorMessage(window, "Please specify row factor.", "");
			return false;
		}

		if (!StringUtils.isNullOrEmpty(rowFactor) && StringUtils.isNullOrEmpty(columnFactor)
			|| StringUtils.isNullOrEmpty(rowFactor) && !StringUtils.isNullOrEmpty(columnFactor)) {
			this.showErrorMessage(window, "Row and Column factors must be specified together.", "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(genotypeFactor)) {
			this.showErrorMessage(window, "Please specify Genotypes factor.", "");
			return false;
		}

		return true;
	}

	public void showErrorMessage(final Window window, final String title, final String description) {
		MessageNotifier.showError(window, title, description);
	}

	void writeProjectXML(final Window window, final BreedingViewInput breedingViewInput) {
		// write the XML input for breeding view
		this.breedingViewXMLWriter.setBreedingViewInput(breedingViewInput);

		try {
			this.breedingViewXMLWriter.writeProjectXML();
		} catch (final BreedingViewXMLWriterException e) {
			RunSingleSiteAction.LOG.debug("Cannot write Breeding View input XML", e);

			this.showErrorMessage(window, e.getMessage(), "");
		}

	}

	private void downloadInputFile(final File file, final String filename) {
		final VaadinFileDownloadResource fileDownloadResource =
			new VaadinFileDownloadResource(file, filename + ZipUtil.ZIP_EXTENSION, this.source.getApplication());
		this.source.getWindow().open(fileDownloadResource);
	}

	public void setSource(final SingleSiteAnalysisDetailsPanel source) {
		this.source = source;
	}

	public void setZipUtil(final ZipUtil zipUtil) {
		this.zipUtil = zipUtil;
	}

}
