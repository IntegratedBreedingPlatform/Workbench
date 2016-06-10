/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.ColPos;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.RowPos;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.commons.tomcat.util.TomcatUtil;
import org.generationcp.commons.tomcat.util.WebAppStatusInfo;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.Util;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.exception.ConfigurationChangeException;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriter;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriterException;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.ibpworkbench.util.DatasetExporterException;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.ibpworkbench.util.ZipUtil;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.service.api.OntologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Strings;
import com.mysql.jdbc.StringUtils;
import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

/**
 * 
 * @author Jeffrey Morales
 * 
 */
@Configurable
public class RunSingleSiteAction implements ClickListener {

	private static final String ERROR = "ERROR: ";

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(RunSingleSiteAction.class);

	private SingleSiteAnalysisDetailsPanel source;

	private Project project;

	@Value("${bv.web.url}")
	private String bvWebUrl;

	@Value("${workbench.is.server.app}")
	private String isServerApp;

	@Autowired
	private ToolUtil toolUtil;

	@Resource
	private TomcatUtil tomcatUtil;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private OntologyService ontologyService;

	public RunSingleSiteAction(final SingleSiteAnalysisDetailsPanel selectDetailsForBreedingViewWindow, final Project project) {
		this.source = selectDetailsForBreedingViewWindow;
		this.project = project;
	}

	@Override
	public void buttonClick(final ClickEvent event) {

		final Window window = event.getComponent().getWindow();
		final BreedingViewInput breedingViewInput = this.source.getBreedingViewInput();

		breedingViewInput.setSelectedEnvironments(this.source.getSelectedEnvironments());

		if (this.validateDesignInput(window, breedingViewInput)) {

			this.populateBreedingViewInputFromUserInput(breedingViewInput);

			this.exportData(breedingViewInput);

			this.writeProjectXML(window, breedingViewInput);

			if (Boolean.parseBoolean(this.isServerApp)) {

				final String outputFilename = breedingViewInput.getDatasetSource() + ".zip";
				final List<String> filenameList = new ArrayList<>();
				filenameList.add(breedingViewInput.getDestXMLFilePath());
				filenameList.add(breedingViewInput.getSourceXLSFilePath());

				ZipUtil.zipIt(outputFilename, filenameList);

				this.downloadInputFile(new File(outputFilename), this.source.getApplication());

			} else {

				this.launchBV(event);
			}

		}

	}

	/**
	 * Generates the CSV input file to be used in Breeding View application.
	 * 
	 * @param breedingViewInput
	 */
	void exportData(final BreedingViewInput breedingViewInput) {

		final DatasetExporter datasetExporter =
				new DatasetExporter(this.studyDataManager, this.ontologyService, null, breedingViewInput.getDatasetId());

		try {

			final List<String> selectedEnvironments = new ArrayList<String>();
			for (final SeaEnvironmentModel m : breedingViewInput.getSelectedEnvironments()) {
				selectedEnvironments.add(m.getTrialno());
			}

			datasetExporter.exportToCSVForBreedingView(breedingViewInput.getSourceXLSFilePath(), (String) this.source.getSelEnvFactor()
					.getValue(), selectedEnvironments, breedingViewInput);

		} catch (final DatasetExporterException e) {
			RunSingleSiteAction.LOG.error(RunSingleSiteAction.ERROR, e);
		}

	}

	/**
	 * Populate the necessary data in BreedingViewInput that will be used to build the XML Input for Breeding View
	 * 
	 * @param breedingViewInput
	 */
	void populateBreedingViewInputFromUserInput(final BreedingViewInput breedingViewInput) {

		breedingViewInput.setBreedingViewAnalysisName(this.source.getTxtAnalysisNameValue());

		breedingViewInput.setEnvironment(this.createEnvironment(this.source.getSelEnvFactorValue()));

		breedingViewInput.setReplicates(this.createReplicates(this.source.getSelDesignTypeValue(), this.source.getSelReplicatesValue()));

		DesignType designType = DesignType.getDesignTypeByName(this.source.getSelDesignTypeValue());
		breedingViewInput.setDesignType(designType.resolveDesignTypeNameForBreedingView());

		breedingViewInput.setBlocks(this.createBlocks(this.source.getSelBlocksValue()));

		populateRowAndColumn(designType, breedingViewInput);

		populateRowPosAndColPos(designType, breedingViewInput);

		breedingViewInput.setGenotypes(this.createGenotypes(breedingViewInput.getDatasetId(), this.source.getSelGenotypesValue()));

		breedingViewInput.setPlot(this.createPlot(breedingViewInput.getDatasetId()));

	}

	void populateRowPosAndColPos(DesignType designType, BreedingViewInput breedingViewInput) {

		if (designType == DesignType.P_REP_DESIGN) {

			breedingViewInput.setColPos(this.createColPos(this.source.getSelColumnFactorValue()));
			breedingViewInput.setRowPos(this.createRowPos(this.source.getSelRowFactorValue()));

		} else {

			breedingViewInput.setColPos(null);
			breedingViewInput.setRowPos(null);
		}


	}

	void populateRowAndColumn(DesignType designType, BreedingViewInput breedingViewInput) {

		if (designType == DesignType.ROW_COLUMN_DESIGN) {

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

	Replicates createReplicates(final String designType, final String replicatesFactor) {

		if (designType.equals(DesignType.P_REP_DESIGN.getName())) {

			// Do not include the replicates factor if the design type is P-rep.
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

		if (StringUtils.isNullOrEmpty(replicatesFactor) && designType.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName())
				&& this.source.getSelReplicates().isEnabled()) {
			this.showErrorMessage(window, "Please specify replicates factor.", "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(blocksFactor)
				&& (designType.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()) || designType.equals(DesignType.P_REP_DESIGN.getName()))) {
			this.showErrorMessage(window, "Please specify incomplete block factor.", "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(columnFactor) && designType.equals(DesignType.ROW_COLUMN_DESIGN.getName())) {
			this.showErrorMessage(window, "Please specify column factor.", "");
			return false;
		}

		if (StringUtils.isNullOrEmpty(rowFactor) && designType.equals(DesignType.ROW_COLUMN_DESIGN.getName())) {
			this.showErrorMessage(window, "Please specify row factor.", "");
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

		BreedingViewXMLWriter breedingViewXMLWriter;

		// write the XML input for breeding view
		breedingViewXMLWriter = new BreedingViewXMLWriter(breedingViewInput);

		try {
			breedingViewXMLWriter.writeProjectXML();
		} catch (final BreedingViewXMLWriterException e) {
			RunSingleSiteAction.LOG.debug("Cannot write Breeding View input XML", e);

			this.showErrorMessage(window, e.getMessage(), "");
		}

	}

	private void launchBV(final ClickEvent event) {

		final BreedingViewInput breedingViewInput = this.source.getBreedingViewInput();

		try {
			// when launching BreedingView, update the web service tool first
			final Tool webServiceTool = new Tool();
			webServiceTool.setToolName("ibpwebservice");
			webServiceTool.setPath(this.bvWebUrl);
			webServiceTool.setToolType(ToolType.WEB);
			this.updateToolConfiguration(event.getButton().getWindow(), webServiceTool);

			// launch breeding view
			final File absoluteToolFile = new File(this.source.getTool().getPath()).getAbsoluteFile();

			final ProcessBuilder pb =
					new ProcessBuilder(absoluteToolFile.getAbsolutePath(), "-project=", breedingViewInput.getDestXMLFilePath());
			pb.start();

		} catch (final IOException e) {
			RunSingleSiteAction.LOG.debug("Cannot write Breeding View input XML", e);

			this.showErrorMessage(event.getComponent().getWindow(), e.getMessage(), "");
		}

	}

	private boolean updateToolConfiguration(final Window window, final Tool tool) {
		final Project currentProject = this.project;

		final String url = tool.getPath();

		// update the configuration of the tool
		boolean changedConfig = false;
		try {
			changedConfig = this.toolUtil.updateToolConfigurationForProject(tool, currentProject);
		} catch (final ConfigurationChangeException e1) {
			RunSingleSiteAction.LOG.error(RunSingleSiteAction.ERROR, e1);
			this.showErrorMessage(window, "Cannot update configuration for tool: " + tool.getToolName(),
					"<br />" + this.messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
			return false;
		}

		final boolean webTool = Util.isOneOf(tool.getToolType(), ToolType.WEB_WITH_LOGIN, ToolType.WEB);

		WebAppStatusInfo statusInfo = null;
		String contextPath = null;
		String localWarPath = null;
		try {
			statusInfo = this.tomcatUtil.getWebAppStatus();
			if (webTool) {
				contextPath = TomcatUtil.getContextPathFromUrl(url);
				localWarPath = TomcatUtil.getLocalWarPathFromUrl(url);

			}
		} catch (final Exception e1) {
			RunSingleSiteAction.LOG.error(RunSingleSiteAction.ERROR, e1);
			this.showErrorMessage(window, "Cannot get webapp status.",
					"<br />" + this.messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
			return false;
		}

		if (webTool) {
			try {
				final boolean deployed = statusInfo.isDeployed(contextPath);
				final boolean running = statusInfo.isRunning(contextPath);

				if (changedConfig || !running) {
					if (!deployed) {
						// deploy the webapp
						this.tomcatUtil.deployLocalWar(contextPath, localWarPath);
					} else if (running) {
						// reload the webapp
						this.tomcatUtil.reloadWebApp(contextPath);
					} else {
						// start the webapp
						this.tomcatUtil.startWebApp(contextPath);
					}
				}
			} catch (final Exception e) {
				RunSingleSiteAction.LOG.error(RunSingleSiteAction.ERROR, e);
				this.showErrorMessage(window, "Cannot load tool: " + tool.getToolName(),
						"<br />" + this.messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
				return false;
			}
		}

		return true;
	}

	private void downloadInputFile(final File file, final Application application) {

		final FileResource fr = new FileResource(file, application) {

			private static final long serialVersionUID = 765143030552676513L;

			@Override
			public DownloadStream getStream() {
				DownloadStream ds;
				try {
					ds = new DownloadStream(new FileInputStream(this.getSourceFile()), this.getMIMEType(), this.getFilename());

					ds.setParameter("Content-Disposition", "attachment; filename=" + this.getFilename());
					ds.setCacheTime(this.getCacheTime());
					return ds;

				} catch (final FileNotFoundException e) {
					RunSingleSiteAction.LOG.error(e.getMessage(), e);
					return null;
				}
			}
		};

		application.getMainWindow().open(fr);
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setProject(final Project project) {
		this.project = project;
	}

	public void setSource(final SingleSiteAnalysisDetailsPanel source) {
		this.source = source;
	}

}
