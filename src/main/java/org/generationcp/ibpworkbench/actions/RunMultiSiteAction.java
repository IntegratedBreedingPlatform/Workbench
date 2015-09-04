
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
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.GxeTable;
import org.generationcp.ibpworkbench.util.DatasetUtil;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.GxeUtility;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.ibpworkbench.util.ZipUtil;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Study;
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

		try {
			this.breedingViewTool = this.workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
		} catch (MiddlewareQueryException ex) {
			RunMultiSiteAction.LOG.error(ex.getMessage(), ex);
		}

		try {

			GxeInput gxeInput;
			gxeInput = this.generateInputFiles();

			if (Boolean.parseBoolean(this.isServerApp)) {

				this.zipInputFilesAndDownload(gxeInput);

			} else {
				this.launchBV(gxeInput.getDestXMLFilePath(), buttonClickEvent.getComponent().getWindow());
			}

		} catch (MiddlewareQueryException e) {
			RunMultiSiteAction.LOG.error(e.getMessage(), e);
		}

	}

	protected void zipInputFilesAndDownload(GxeInput gxeInput) {
		String outputFilename = this.multiSiteParameters.getStudy().getName() + ".zip";
		List<String> filenameList = new ArrayList<>();
		filenameList.add(gxeInput.getDestXMLFilePath());
		filenameList.add(gxeInput.getSourceCSVFilePath());
		filenameList.add(gxeInput.getSourceCSVSummaryStatsFilePath());

		ZipUtil.zipIt(outputFilename, filenameList);

		this.downloadInputFile(new File(outputFilename), IBPWorkbenchApplication.get());
	}

	protected GxeInput generateInputFiles() throws MiddlewareQueryException {

		Project project = this.multiSiteParameters.getProject();
		Study study = this.multiSiteParameters.getStudy();

		GxeInput gxeInput = new GxeInput(project, "", 0, 0, "", "", "", "");
		String inputDir = this.toolUtil.getInputDirectoryForTool(project, this.breedingViewTool);
		String inputFileName =
				String.format("%s_%s_%s", project.getProjectName().trim(), this.gxeTable.getMeansDataSetId(), this.gxeTable
						.getMeansDataSet().getName());

		GxeEnvironment gxeEnv = this.gxeTable.getGxeEnvironment();
		List<Trait> selectedTraits = this.getSelectedTraits();

		File datasetExportFile = null;
		datasetExportFile =
				GxeUtility.exportGxEDatasetToBreadingViewCsv(this.gxeTable.getMeansDataSet(), this.gxeTable.getExperiments(),
						this.gxeTable.getEnvironmentName(), this.multiSiteParameters.getSelectedEnvGroupFactorName(),
						this.multiSiteParameters.getSelectedGenotypeFactorName(), gxeEnv, selectedTraits, project);

		try {

			DataSet trialDataSet;
			List<DataSet> dataSets = this.studyDataManager.getDataSetsByType(study.getId(), DataSetType.SUMMARY_DATA);
			if (!dataSets.isEmpty()) {
				trialDataSet = dataSets.get(0);
			} else {
				trialDataSet = DatasetUtil.getTrialDataSet(this.studyDataManager, study.getId());

			}
			List<Experiment> trialExperiments = this.studyDataManager.getExperiments(trialDataSet.getId(), 0, Integer.MAX_VALUE);
			File summaryStatsFile =
					GxeUtility.exportTrialDatasetToSummaryStatsCsv(trialDataSet, trialExperiments, this.gxeTable.getEnvironmentName(),
							selectedTraits, project);
			gxeInput.setSourceCSVSummaryStatsFilePath(summaryStatsFile.getAbsolutePath());

		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		}

		gxeInput.setSourceCSVFilePath(datasetExportFile.getAbsolutePath());
		gxeInput.setDestXMLFilePath(String.format("%s\\%s.xml", inputDir, inputFileName));
		gxeInput.setTraits(selectedTraits);
		gxeInput.setEnvironment(gxeEnv);
		gxeInput.setSelectedEnvironments(this.gxeTable.getSelectedEnvironments());
		gxeInput.setEnvironmentGroup(this.multiSiteParameters.getSelectedEnvGroupFactorName());

		Genotypes genotypes = new Genotypes();
		if (!StringUtils.isNullOrEmpty(this.multiSiteParameters.getSelectedGenotypeFactorName())) {
			genotypes.setName(this.multiSiteParameters.getSelectedGenotypeFactorName());
		} else {
			genotypes.setName("G!");
		}

		gxeInput.setGenotypes(genotypes);
		gxeInput.setEnvironmentName(this.gxeTable.getEnvironmentName());
		gxeInput.setBreedingViewProjectName(project.getProjectName());

		GxeUtility.generateXmlFieldBook(gxeInput);

		return gxeInput;
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
