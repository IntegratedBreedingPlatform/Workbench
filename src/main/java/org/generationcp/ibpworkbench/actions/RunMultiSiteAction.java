package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.FileNameGenerator;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.commons.util.ZipUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.WorkbenchContentApp;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.GxeTable;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.MultiSiteDataExporter;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.unbescape.html.HtmlEscape;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Configurable
public class RunMultiSiteAction implements ClickListener {

	private static final long serialVersionUID = -7090745965019240566L;

	private static final Logger LOG = LoggerFactory.getLogger(RunMultiSiteAction.class);

	private Tool breedingViewTool;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private StudyDataManager studyDataManager;

	@Autowired
	private ContextUtil contextUtil;

	private WorkbenchContentApp workbenchApplication;

	private MultiSiteDataExporter multiSiteDataExporter = new MultiSiteDataExporter();

	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

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
		this.workbenchApplication = WorkbenchContentApp.get();
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		final ClickEvent buttonClickEvent = event;

		this.breedingViewTool = this.workbenchDataManager.getToolWithName(ToolName.BREEDING_VIEW.getName());

		final GxeInput gxeInput;
		gxeInput = this.generateInputFiles();

		this.zipInputFilesAndDownload(gxeInput, buttonClickEvent.getComponent().getWindow());
	}

	protected void zipInputFilesAndDownload(final GxeInput gxeInput, final Window window) {
		final List<String> filenameList = new ArrayList<>();
		filenameList.add(gxeInput.getDestXMLFilePath());
		filenameList.add(gxeInput.getSourceCSVFilePath());
		filenameList.add(gxeInput.getSourceCSVSummaryStatsFilePath());

		final String studyName = HtmlEscape.unescapeHtml(this.multiSiteParameters.getStudy().getName());
		final String outputFilename = BreedingViewUtil.sanitizeNameAlphaNumericOnly(FileNameGenerator.generateFileName(studyName));

		try {
			final String finalZipfileName =
				this.zipUtil.zipIt(outputFilename, filenameList, this.contextUtil.getProjectInContext(), ToolName.BV_GXE);
			this.downloadInputFile(new File(finalZipfileName), outputFilename, window);
		} catch (final IOException e) {
			RunMultiSiteAction.LOG.error("Error creating zip file " + outputFilename + ZipUtil.ZIP_EXTENSION, e);
			MessageNotifier.showMessage(this.workbenchApplication.getMainWindow(), "Error creating zip file.", "");
		}
	}

	protected GxeInput generateInputFiles() {

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

		final String inputDir =
			this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(multiSiteParameters.getProject(), ToolName.BREEDING_VIEW);
		final String inputFileName = this.generateInputFileName(multiSiteParameters.getProject());

		gxeInput.setDestXMLFilePath(inputDir + File.separator + FileNameGenerator.generateFileName(inputFileName, "xml"));

		this.multiSiteDataExporter.generateXmlFieldBook(gxeInput);

	}

	/**
	 * Exports the Means Dataset and Summary Stats data into CSV files. These are the input files required for running Multi-Site Analysis
	 * in Breeding View.
	 *
	 * @param multiSiteParameters
	 * @param gxeInput
	 * @param gxeEnvironment
	 * @param selectedTraits
	 */
	void exportDataFiles(
		final MultiSiteParameters multiSiteParameters, final GxeInput gxeInput, final GxeEnvironment gxeEnvironment,
		final List<Trait> selectedTraits) {

		final int studyId = multiSiteParameters.getStudy().getId();

		final String inputFileName = this.generateInputFileName(multiSiteParameters.getProject());

		final String meansDataFilePath = this.multiSiteDataExporter
			.exportMeansDatasetToCsv(inputFileName, multiSiteParameters, this.gxeTable.getExperiments(),
				this.gxeTable.getEnvironmentName(), gxeEnvironment, selectedTraits, this.workbenchApplication);

		final String summaryStatsDataFilePath = this.multiSiteDataExporter
			.exportSummaryStatisticsToCsvFile(studyId, inputFileName,
				this.gxeTable.getEnvironmentName(), selectedTraits, multiSiteParameters.getProject());

		gxeInput.setSourceCSVSummaryStatsFilePath(summaryStatsDataFilePath);
		gxeInput.setSourceCSVFilePath(meansDataFilePath);

	}

	GxeInput createGxeInput(
		final MultiSiteParameters multiSiteParameters, final GxeEnvironment gxeEnvironment,
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

	String generateInputFileName(final Project project) {

		final String sanitizedProjectName = BreedingViewUtil.sanitizeNameAlphaNumericOnly(project.getProjectName().trim());

		final String meansDataSetName = HtmlEscape.unescapeHtml(this.gxeTable.getMeansDataSet().getName());
		final String sanitizedMeansDataSetName = BreedingViewUtil.sanitizeNameAlphaNumericOnly(meansDataSetName);

		return String.format("%s_%s_%s", sanitizedProjectName, this.gxeTable.getMeansDataSetId(), sanitizedMeansDataSetName);

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

	void downloadInputFile(final File file, final String filename, final Window window) {
		final VaadinFileDownloadResource fileDownloadResource =
			new VaadinFileDownloadResource(file, filename + ZipUtil.ZIP_EXTENSION, this.workbenchApplication);
		window.open(fileDownloadResource);
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
