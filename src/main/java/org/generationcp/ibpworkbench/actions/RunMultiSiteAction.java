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
import org.generationcp.middleware.manager.ManagerFactory;
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
	
	private StudyDataManager studyDataManager;
	
	private ManagerFactory managerFactory;
	
	private MultiSiteParameters multiSiteParameters;
	
	private GxeTable gxeTable;
	
	private Table selectTraitsTable;
	
	public RunMultiSiteAction(ManagerFactory managerFactory,StudyDataManager studyDataManager, GxeTable gxeTable, Table selectTraitsTable,  MultiSiteParameters multiSiteParameters){
		
		this.studyDataManager = studyDataManager;
		this.managerFactory = managerFactory;
		this.gxeTable = gxeTable;
		this.selectTraitsTable = selectTraitsTable;
		this.multiSiteParameters = multiSiteParameters;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		final ClickEvent buttonClickEvent = event;
		
		try{
			breedingViewTool = workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
		}catch(MiddlewareQueryException ex){
			LOG.error(ex.getMessage(), ex);
		}
		
		try {
			
			GxeInput gxeInput;
			gxeInput = generateInputFiles();
			
			if (Boolean.parseBoolean(isServerApp)){
				
				String outputFilename = multiSiteParameters.getStudy().getName() + ".zip";
				List<String> filenameList = new ArrayList<>();
				filenameList.add(gxeInput.getDestXMLFilePath());
				filenameList.add(gxeInput.getSourceCSVFilePath());
				filenameList.add(gxeInput.getSourceCSVSummaryStatsFilePath());
				
				ZipUtil.zipIt(outputFilename, filenameList);
				
				downloadInputFile(new File(outputFilename), IBPWorkbenchApplication.get());
				
			}else{
				launchBV(gxeInput.getDestXMLFilePath(), buttonClickEvent.getComponent().getWindow());
			}
			
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
		
	}
	
	private GxeInput generateInputFiles() throws MiddlewareQueryException {
		
		
		Project project = multiSiteParameters.getProject();
		Study study = multiSiteParameters.getStudy();
		
		GxeInput gxeInput =  new GxeInput(project, "", 0, 0, "", "", "", "");
		String inputDir = toolUtil.getInputDirectoryForTool(project, breedingViewTool);
		String inputFileName = String.format("%s_%s_%s", project.getProjectName().trim(), gxeTable.getMeansDataSetId(), gxeTable.getMeansDataSet().getName());
		
		GxeEnvironment gxeEnv = gxeTable.getGxeEnvironment();
		List<Trait> selectedTraits = getSelectedTraits();
		
		File datasetExportFile = null;
		datasetExportFile = GxeUtility.exportGxEDatasetToBreadingViewCsv(gxeTable.getMeansDataSet(), gxeTable.getExperiments(),gxeTable.getEnvironmentName(), multiSiteParameters.getSelectedEnvGroupFactorName() ,  multiSiteParameters.getSelectedGenotypeFactorName() , gxeEnv,  selectedTraits, project);
		
		try{
			
			DataSet trialDataSet;
			List<DataSet> dataSets = studyDataManager.getDataSetsByType(study.getId(), DataSetType.SUMMARY_DATA);
			if (dataSets.size() > 0) {
				trialDataSet = dataSets.get(0);
			}else{
				trialDataSet = DatasetUtil.getTrialDataSet(studyDataManager, study.getId());
				
			}
			List<Experiment> trialExperiments = studyDataManager.getExperiments(trialDataSet.getId(), 0, Integer.MAX_VALUE);
			File summaryStatsFile = GxeUtility.exportTrialDatasetToSummaryStatsCsv(trialDataSet, trialExperiments, gxeTable.getEnvironmentName(),  selectedTraits, project);
			gxeInput.setSourceCSVSummaryStatsFilePath(summaryStatsFile.getAbsolutePath());
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
        gxeInput.setSourceCSVFilePath(datasetExportFile.getAbsolutePath());
        gxeInput.setDestXMLFilePath(String.format("%s\\%s.xml", inputDir, inputFileName));
		gxeInput.setTraits(selectedTraits);
		gxeInput.setEnvironment(gxeEnv);
		gxeInput.setSelectedEnvironments(gxeTable.getSelectedEnvironments());
		gxeInput.setEnvironmentGroup(multiSiteParameters.getSelectedEnvGroupFactorName());
		
	
		Genotypes genotypes = new Genotypes();
		if (!StringUtils.isNullOrEmpty(multiSiteParameters.getSelectedGenotypeFactorName())) {
			genotypes.setName(multiSiteParameters.getSelectedGenotypeFactorName());
		}else{
			genotypes.setName("G!");
		}

		gxeInput.setGenotypes(genotypes);
		gxeInput.setEnvironmentName(gxeTable.getEnvironmentName());
		gxeInput.setBreedingViewProjectName(project.getProjectName());
		
		GxeUtility.generateXmlFieldBook(gxeInput);
			
		return gxeInput;
	}

	private void launchBV(String projectFilePath, final Window windowSource) {
			
		File absoluteToolFile = new File(breedingViewTool.getPath()).getAbsoluteFile();

        try {
			ProcessBuilder pb = new ProcessBuilder(absoluteToolFile.getAbsolutePath(), "-project=", projectFilePath);
			pb.start();

            MessageNotifier.showMessage(windowSource, "GxE files saved", "Successfully generated the means dataset and xml input files for breeding view.");
        }
        catch (IOException e) {
        	LOG.error(e.getMessage(), e);
            MessageNotifier.showMessage(windowSource, "Cannot launch " + absoluteToolFile.getName(), "But it successfully created GxE Excel and XML input file for the breeding_view!");
        }
            
		managerFactory.close();
	}
	
	protected List<Trait> getSelectedTraits(){
		List<Trait> selectedTraits = new ArrayList<Trait>();
		Iterator<?> itr = selectTraitsTable.getItem(1).getItemPropertyIds().iterator();
		
		while (itr.hasNext()){
			Object propertyId = itr.next();
			CheckBox cb = (CheckBox)selectTraitsTable.getItem(1).getItemProperty(propertyId).getValue();
			if ((Boolean)cb.getValue()){
				Trait t = new Trait();
				t.setName(propertyId.toString());
				t.setActive(true);
				selectedTraits.add(t);
			}
		}
		
		return selectedTraits;
	}
	
	protected void downloadInputFile(File file, Application application){
		
		FileResource fr = new FileResource(file, application) {
            private static final long serialVersionUID = 765143030552676513L;
            @Override
            public DownloadStream getStream() {
                DownloadStream ds;
                try {
                    ds = new DownloadStream(new FileInputStream(
                            getSourceFile()), getMIMEType(), getFilename());

                    ds.setParameter("Content-Disposition", "attachment; filename="+getFilename());
                    ds.setCacheTime(getCacheTime());
                    return ds;

                } catch (FileNotFoundException e) {
                	LOG.error(e.getMessage(), e);
                    return null;
                }
            }
        };

        application.getMainWindow().open(fr);
	}
	
	
}
