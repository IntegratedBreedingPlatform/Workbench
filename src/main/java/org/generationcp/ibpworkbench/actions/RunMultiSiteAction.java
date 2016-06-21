
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
import org.generationcp.ibpworkbench.util.GxeUtility;
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

import com.mysql.jdbc.StringUtils;
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
	private String isServerApp;

	@Resource
	private ToolUtil toolUtil;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private StudyDataManager studyDataManager;

	private final MultiSiteParameters multiSiteParameters;

	private final GxeTable gxeTable;

	private final Table selectTraitsTable;

	public RunMultiSiteAction(GxeTable gxeTable, Table selectTraitsTable,
			MultiSiteParameters multiSiteParameters) {
		this.gxeTable = gxeTable;
		this.selectTraitsTable = selectTraitsTable;
		this.multiSiteParameters = multiSiteParameters;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		final ClickEvent buttonClickEvent = event;

		this.breedingViewTool = this.workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());


		GxeInput gxeInput;
		gxeInput = this.generateInputFiles();

		if (Boolean.parseBoolean(this.isServerApp)) {

			this.zipInputFilesAndDownload(gxeInput);

		} else {

			this.launchBV(gxeInput.getDestXMLFilePath(), buttonClickEvent.getComponent().getWindow());
		}

	}

	protected void zipInputFilesAndDownload(GxeInput gxeInput) {

		String studyName = HtmlEscape.unescapeHtml(this.multiSiteParameters.getStudy().getName());

		String outputFilename = BreedingViewUtil.sanitizeNameAlphaNumericOnly(studyName) + ".zip";

		List<String> filenameList = new ArrayList<>();
		filenameList.add(gxeInput.getDestXMLFilePath());
		filenameList.add(gxeInput.getSourceCSVFilePath());
		filenameList.add(gxeInput.getSourceCSVSummaryStatsFilePath());

		ZipUtil.zipIt(outputFilename, filenameList);

		this.downloadInputFile(new File(outputFilename), IBPWorkbenchApplication.get());
	}

	protected GxeInput generateInputFiles() throws MiddlewareQueryException {

		GxeEnvironment gxeEnvironment = this.gxeTable.getGxeEnvironment();
		List<Trait> selectedTraits = this.getSelectedTraits();

		GxeInput gxeInput = this.createGxeInput(this.multiSiteParameters, gxeEnvironment, selectedTraits);

		this.exportDataFiles(this.multiSiteParameters, gxeInput, gxeEnvironment, selectedTraits);

		this.exportMultiSiteProjectFile(gxeInput);

		return gxeInput;
	}

	/**
	 * Exports the Breeding View project file for Multi-Site Analysis.
	 * @param gxeInput
	 */
	void exportMultiSiteProjectFile(GxeInput gxeInput) {

		String inputDir = this.toolUtil.getInputDirectoryForTool(multiSiteParameters.getProject(), this.breedingViewTool);
		String inputFileName = this.generateInputFileName(multiSiteParameters.getProject());

		gxeInput.setDestXMLFilePath(inputDir + File.separator + inputFileName + ".xml");

		GxeUtility.generateXmlFieldBook(gxeInput);

	}

	/**
	 * Exports the Means Dataset and Summary Stats data into CSV files. These are the input files required for running Multi-Site Analsysis in Breeding View.
	 * @param multiSiteParameters
	 * @param gxeInput
	 * @param gxeEnvironment
	 * @param selectedTraits
	 */
	void exportDataFiles(MultiSiteParameters multiSiteParameters, GxeInput gxeInput,  GxeEnvironment gxeEnvironment, List<Trait> selectedTraits) {

		String inputDir = this.toolUtil.getInputDirectoryForTool(multiSiteParameters.getProject(), this.breedingViewTool);
		String inputFileName = this.generateInputFileName(multiSiteParameters.getProject());

		File meansDataFile = GxeUtility.exportMeansDatasetToCsv(inputFileName, multiSiteParameters, this.gxeTable.getMeansDataSet(), this.gxeTable.getExperiments(),
				this.gxeTable.getEnvironmentName(), gxeEnvironment, selectedTraits);

		DataSet summaryStatsDataSet = this.getSummaryStatsDataSet(multiSiteParameters.getStudy().getId());

		File summaryStatsDataFile =
				GxeUtility.exportTrialDatasetToSummaryStatsCsv(inputFileName, summaryStatsDataSet, this.getSummaryStatsExperiments(summaryStatsDataSet.getId()), this.gxeTable.getEnvironmentName(),
						selectedTraits, multiSiteParameters.getProject());

		gxeInput.setSourceCSVSummaryStatsFilePath(summaryStatsDataFile.getAbsolutePath());
		gxeInput.setSourceCSVFilePath(meansDataFile.getAbsolutePath());

	}

	GxeInput createGxeInput(MultiSiteParameters multiSiteParameters, final GxeEnvironment gxeEnvironment, final List<Trait> selectedTraits) {

		GxeInput gxeInput = new GxeInput(multiSiteParameters.getProject(), "", 0, 0, "", "", "", "");

		gxeInput.setTraits(selectedTraits);
		gxeInput.setEnvironment(gxeEnvironment);
		gxeInput.setSelectedEnvironments(this.gxeTable.getSelectedEnvironments());
		gxeInput.setEnvironmentGroup(multiSiteParameters.getSelectedEnvGroupFactorName());

		Genotypes genotypes = new Genotypes();
		if (!StringUtils.isNullOrEmpty(multiSiteParameters.getSelectedGenotypeFactorName())) {
			genotypes.setName(multiSiteParameters.getSelectedGenotypeFactorName());
		} else {
			genotypes.setName("G!");
		}

		gxeInput.setGenotypes(genotypes);
		gxeInput.setEnvironmentName(this.gxeTable.getEnvironmentName());
		gxeInput.setBreedingViewProjectName(multiSiteParameters.getProject().getProjectName());

		return gxeInput;

	}

	List<Experiment> getSummaryStatsExperiments(final int dataSetId) {

		return this.studyDataManager.getExperiments(dataSetId, 0, Integer.MAX_VALUE);

	}

	DataSet getSummaryStatsDataSet(int studyId) {

		DataSet summaryStatsDataSet;
		List<DataSet> dataSets = this.studyDataManager.getDataSetsByType(studyId, DataSetType.SUMMARY_DATA);
		if (!dataSets.isEmpty()) {
			summaryStatsDataSet = dataSets.get(0);
		} else {
			summaryStatsDataSet = DatasetUtil.getTrialDataSet(this.studyDataManager, studyId);

		}

		return summaryStatsDataSet;
	}

	String generateInputFileName(Project project) {

		String sanitizedProjectName = BreedingViewUtil.sanitizeNameAlphaNumericOnly(project.getProjectName().trim());

		String meansDataSetName = HtmlEscape.unescapeHtml(this.gxeTable.getMeansDataSet().getName());
		String sanitizedMeansDataSetName = BreedingViewUtil.sanitizeNameAlphaNumericOnly(meansDataSetName);

		return String.format("%s_%s_%s", sanitizedProjectName , this.gxeTable.getMeansDataSetId(), sanitizedMeansDataSetName);

	}

	protected void launchBV(String projectFilePath, final Window windowSource) {

		File absoluteToolFile = new File(this.breedingViewTool.getPath()).getAbsoluteFile();

		try {
			ProcessBuilder pb = new ProcessBuilder(absoluteToolFile.getAbsolutePath(), "-project=", projectFilePath);
			pb.start();

			MessageNotifier.showMessage(windowSource, "GxE files saved",
					"Successfully generated the means dataset and xml input files for breeding view.");
		} catch (IOException e) {
			RunMultiSiteAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showMessage(windowSource, "Cannot launch " + absoluteToolFile.getName(),
					"But it successfully created GxE Excel and XML input file for the breeding_view!");
		}

	}

	protected List<Trait> getSelectedTraits() {
		List<Trait> selectedTraits = new ArrayList<Trait>();
		Iterator<?> itr = this.selectTraitsTable.getItem(1).getItemPropertyIds().iterator();

		while (itr.hasNext()) {
			Object propertyId = itr.next();
			CheckBox cb = (CheckBox) this.selectTraitsTable.getItem(1).getItemProperty(propertyId).getValue();
			if ((Boolean) cb.getValue()) {
				Trait t = new Trait();
				t.setName(propertyId.toString());
				t.setActive(true);
				selectedTraits.add(t);
			}
		}

		return selectedTraits;
	}

	private void downloadInputFile(File file, Application application) {

		FileResource fr = new FileResource(file, application) {

			private static final long serialVersionUID = 765143030552676513L;

			@Override
			public DownloadStream getStream() {
				DownloadStream ds;
				try {
					ds = new DownloadStream(new FileInputStream(this.getSourceFile()), this.getMIMEType(), this.getFilename());

					ds.setParameter("Content-Disposition", "attachment; filename=" + this.getFilename());
					ds.setCacheTime(this.getCacheTime());
					return ds;

				} catch (FileNotFoundException e) {
					RunMultiSiteAction.LOG.error(e.getMessage(), e);
					return null;
				}
			}
		};

		application.getMainWindow().open(fr);
	}

	protected void setIsServerApp(String isServerApp) {
		this.isServerApp = isServerApp;
	}

}
