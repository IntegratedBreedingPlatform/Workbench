package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.GxeTable;
import org.generationcp.middleware.util.DatasetUtil;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.MultiSiteDataExporter;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.ibpworkbench.util.ZipUtil;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import org.unbescape.html.HtmlEscape;

@Configurable
public class RunMultiSiteAction implements ClickListener {

	private static final long serialVersionUID = -7090745965019240566L;

	private static final Logger LOG = LoggerFactory.getLogger(RunMultiSiteAction.class);

	private Tool breedingViewTool;

	@Value("${workbench.is.server.app}")
	private boolean isServerApp;

	@Resource
	private ToolUtil toolUtil;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private StudyDataManager studyDataManager;

	private IBPWorkbenchApplication workbenchApplication;

	private MultiSiteDataExporter multiSiteDataExporter = new MultiSiteDataExporter();

	private MultiSiteParameters multiSiteParameters;

	private GxeTable gxeTable;

	private Table selectTraitsTable;

	private ZipUtil zipUtil = new ZipUtil();

	public RunMultiSiteAction() {
		// for unit testing
	}

	public RunMultiSiteAction(final GxeTable gxeTable, final Table selectTraitsTable, final MultiSiteParameters multiSiteParameters) {
		this.gxeTable = gxeTable;
		this.selectTraitsTable = selectTraitsTable;
		this.multiSiteParameters = multiSiteParameters;
		this.workbenchApplication = IBPWorkbenchApplication.get();
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		final ClickEvent buttonClickEvent = event;

		this.breedingViewTool = this.workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());

		final GxeInput gxeInput;
		gxeInput = this.generateInputFiles();

		if (this.isServerApp) {

			this.zipInputFilesAndDownload(gxeInput);

		} else {

			this.launchBV(gxeInput.getDestXMLFilePath(), buttonClickEvent.getComponent().getWindow());
		}

	}

	protected void zipInputFilesAndDownload(final GxeInput gxeInput) {

		final String studyName = HtmlEscape.unescapeHtml(this.multiSiteParameters.getStudy().getName());

		final String outputFilename = BreedingViewUtil.sanitizeNameAlphaNumericOnly(studyName) + ".zip";

		final List<String> filenameList = new ArrayList<>();
		filenameList.add(gxeInput.getDestXMLFilePath());
		filenameList.add(gxeInput.getSourceCSVFilePath());
		filenameList.add(gxeInput.getSourceCSVSummaryStatsFilePath());

		this.zipUtil.zipIt(outputFilename, filenameList);

		this.downloadInputFile(new File(outputFilename), workbenchApplication);
	}

	protected GxeInput generateInputFiles() throws MiddlewareQueryException {

		final GxeEnvironment gxeEnvironment = this.gxeTable.getGxeEnvironment();
		final List<Trait> selectedTraits = this.getSelectedTraits();

		final GxeInput gxeInput = this.createGxeInput(this.multiSiteParameters, gxeEnvironment, selectedTraits);

		this.exportDataFiles(this.multiSiteParameters, gxeInput, gxeEnvironment, selectedTraits);

		this.exportMultiSiteProjectFile(this.multiSiteParameters, gxeInput);

		return gxeInput;
	}

	/**
	 * Exports the Breeding View project file for Multi-Site Analysis.
	 *
	 * @param gxeInput
	 */
	void exportMultiSiteProjectFile(final MultiSiteParameters multiSiteParameters, final GxeInput gxeInput) {

		final String inputDir = this.toolUtil.getInputDirectoryForTool(multiSiteParameters.getProject(), this.breedingViewTool);
		final String inputFileName = this.generateInputFileName(multiSiteParameters.getProject());

		gxeInput.setDestXMLFilePath(inputDir + File.separator + inputFileName + ".xml");

		multiSiteDataExporter.generateXmlFieldBook(gxeInput);

	}

	/**
	 * Exports the Means Dataset and Summary Stats data into CSV files. These are the input files required for running Multi-Site Analsysis in Breeding View.
	 *
	 * @param multiSiteParameters
	 * @param gxeInput
	 * @param gxeEnvironment
	 * @param selectedTraits
	 */
	void exportDataFiles(final MultiSiteParameters multiSiteParameters, final GxeInput gxeInput, final GxeEnvironment gxeEnvironment,
			final List<Trait> selectedTraits) {
		
		final String inputFileName = this.generateInputFileName(multiSiteParameters.getProject());

		final String meansDataFilePath = multiSiteDataExporter
				.exportMeansDatasetToCsv(inputFileName, multiSiteParameters, this.gxeTable.getMeansDataSet(),
						this.gxeTable.getExperiments(), this.gxeTable.getEnvironmentName(), gxeEnvironment, selectedTraits);

		final DataSet summaryStatsDataSet = this.getSummaryStatsDataSet(multiSiteParameters.getStudy().getId());

		final String summaryStatsDataFilePath = multiSiteDataExporter.exportTrialDatasetToSummaryStatsCsv(inputFileName, summaryStatsDataSet,
				this.getSummaryStatsExperiments(summaryStatsDataSet.getId()), this.gxeTable.getEnvironmentName(), selectedTraits,
				multiSiteParameters.getProject());

		gxeInput.setSourceCSVSummaryStatsFilePath(summaryStatsDataFilePath);
		gxeInput.setSourceCSVFilePath(meansDataFilePath);

	}

	GxeInput createGxeInput(final MultiSiteParameters multiSiteParameters, final GxeEnvironment gxeEnvironment,
			final List<Trait> selectedTraits) {

		final GxeInput gxeInput = new GxeInput(multiSiteParameters.getProject(), "", 0, 0, "", "", "", "");

		gxeInput.setTraits(selectedTraits);
		gxeInput.setEnvironment(gxeEnvironment);
		gxeInput.setSelectedEnvironments(this.gxeTable.getSelectedEnvironments());
		gxeInput.setEnvironmentGroup(multiSiteParameters.getSelectedEnvGroupFactorName());

		final Genotypes genotypes = new Genotypes();
		genotypes.setName(multiSiteParameters.getSelectedGenotypeFactorName());

		gxeInput.setGenotypes(genotypes);
		gxeInput.setEnvironmentName(this.gxeTable.getEnvironmentName());
		gxeInput.setBreedingViewProjectName(multiSiteParameters.getProject().getProjectName());

		return gxeInput;

	}

	List<Experiment> getSummaryStatsExperiments(final int dataSetId) {

		return this.studyDataManager.getExperiments(dataSetId, 0, Integer.MAX_VALUE);

	}

	DataSet getSummaryStatsDataSet(final int studyId) {

		final DataSet summaryStatsDataSet;
		final List<DataSet> dataSets = this.studyDataManager.getDataSetsByType(studyId, DataSetType.SUMMARY_DATA);
		if (!dataSets.isEmpty()) {
			summaryStatsDataSet = dataSets.get(0);
		} else {
			summaryStatsDataSet = DatasetUtil.getTrialDataSet(this.studyDataManager, studyId);

		}

		return summaryStatsDataSet;
	}

	String generateInputFileName(final Project project) {

		final String sanitizedProjectName = BreedingViewUtil.sanitizeNameAlphaNumericOnly(project.getProjectName().trim());

		final String meansDataSetName = HtmlEscape.unescapeHtml(this.gxeTable.getMeansDataSet().getName());
		final String sanitizedMeansDataSetName = BreedingViewUtil.sanitizeNameAlphaNumericOnly(meansDataSetName);

		return String.format("%s_%s_%s", sanitizedProjectName, this.gxeTable.getMeansDataSetId(), sanitizedMeansDataSetName);

	}

	protected void launchBV(final String projectFilePath, final Window windowSource) {

		final File absoluteToolFile = new File(this.breedingViewTool.getPath()).getAbsoluteFile();

		try {
			final ProcessBuilder pb = new ProcessBuilder(absoluteToolFile.getAbsolutePath(), "-project=", projectFilePath);
			pb.start();

			MessageNotifier.showMessage(windowSource, "GxE files saved",
					"Successfully generated the means dataset and xml input files for breeding view.");
		} catch (final IOException e) {
			RunMultiSiteAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showMessage(windowSource, "Cannot launch " + absoluteToolFile.getName(),
					"But it successfully created GxE Excel and XML input file for the breeding_view!");
		}

	}

	protected List<Trait> getSelectedTraits() {
		final List<Trait> selectedTraits = new ArrayList<Trait>();
		final Iterator<?> itr = this.selectTraitsTable.getItem(1).getItemPropertyIds().iterator();

		while (itr.hasNext()) {
			final Object propertyId = itr.next();
			final CheckBox cb = (CheckBox) this.selectTraitsTable.getItem(1).getItemProperty(propertyId).getValue();
			if ((Boolean) cb.getValue()) {
				final Trait t = new Trait();
				t.setName(propertyId.toString());
				t.setActive(true);
				selectedTraits.add(t);
			}
		}

		return selectedTraits;
	}

	void downloadInputFile(final File file, final Application application) {

		final FileResource fr = new FileResource(file, application) {

			private static final long serialVersionUID = 765143030552676513L;

			@Override
			public DownloadStream getStream() {
				final DownloadStream ds;
				try {
					ds = new DownloadStream(new FileInputStream(this.getSourceFile()), this.getMIMEType(), this.getFilename());

					ds.setParameter("Content-Disposition", "attachment; filename=" + this.getFilename());
					ds.setCacheTime(this.getCacheTime());
					return ds;

				} catch (final FileNotFoundException e) {
					RunMultiSiteAction.LOG.error(e.getMessage(), e);
					return null;
				}
			}
		};

		application.getMainWindow().open(fr);
	}

	protected void setIsServerApp(final boolean isServerApp) {
		this.isServerApp = isServerApp;
	}

	protected void setSelectTraitsTable(final Table selectTraitsTable) {
		this.selectTraitsTable = selectTraitsTable;
	}

	protected void setBreedingViewTool(final Tool breedingViewTool) {
		this.breedingViewTool = breedingViewTool;
	}

	protected void setMultiSiteParameters(final MultiSiteParameters multiSiteParameters) {
		this.multiSiteParameters = multiSiteParameters;
	}

	protected Table getSelectTraitsTable() {
		return this.selectTraitsTable;
	}
}
