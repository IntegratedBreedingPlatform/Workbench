/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Kevin L. Manansala
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.ibpworkbench.util;

import org.generationcp.commons.breedingview.xml.SSAParameters;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.sea.xml.*;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


@Configurable
public class BreedingViewXMLWriter implements InitializingBean, Serializable{

    private static final long serialVersionUID = 8844276834893749854L;

    private static final Logger LOG = LoggerFactory.getLogger(BreedingViewXMLWriter.class);
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SessionData sessionData;

    @Value("${web.api.url}")
    private String webApiUrl;

    private BreedingViewInput breedingViewInput;
    
    private List<Integer> numericTypes;
    private List<Integer> characterTypes;
    

    public BreedingViewXMLWriter(BreedingViewInput breedingViewInput) {
        
        this.breedingViewInput = breedingViewInput;
        
        numericTypes = new ArrayList<Integer>();
        characterTypes =  new ArrayList<Integer>();
        
        numericTypes.add(TermId.NUMERIC_VARIABLE.getId());
        numericTypes.add(TermId.MIN_VALUE.getId());
        numericTypes.add(TermId.MAX_VALUE.getId());
        numericTypes.add(TermId.DATE_VARIABLE.getId());
        numericTypes.add(TermId.NUMERIC_DBID_VARIABLE.getId());
        
        characterTypes.add(TermId.CHARACTER_VARIABLE.getId());
        characterTypes.add(TermId.CHARACTER_DBID_VARIABLE.getId());
        characterTypes.add(1128);
        characterTypes.add(1130);
        
    }
 
	public void writeProjectXML() throws BreedingViewXMLWriterException {
		DataFile data = new DataFile();
        data.setName(breedingViewInput.getSourceXLSFilePath());
        BreedingViewProject project = getBreedingViewProject();
        SSAParameters ssaParameters = getSSAParameters();
        BreedingViewSession bvSession = getBreedingViewSession(
        		project,data,ssaParameters);
        createProjectXMLFile(bvSession);
        removePreviousDatastore(ssaParameters.getOutputDirectory());
	}
	
	private void createProjectXMLFile(BreedingViewSession bvSession) throws BreedingViewXMLWriterException {
		//prepare the writing of the xml
        Marshaller marshaller = getMarshaller();
        //write the xml
    	String filePath = breedingViewInput.getDestXMLFilePath();
    	try{
        	new File(new File(filePath).getParent()).mkdirs();
            FileWriter fileWriter = new FileWriter(filePath);
            LOG.debug(filePath);
            marshaller.marshal(bvSession, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch(Exception ex){
            throw new BreedingViewXMLWriterException("Error with writing xml to: " + filePath + ": " + ex.getMessage(), ex);
        }
	}


	private Marshaller getMarshaller() throws BreedingViewXMLWriterException {
		JAXBContext context = null;
        Marshaller marshaller = null;
        try{
            context = JAXBContext.newInstance(BreedingViewSession.class, Pipelines.class, Environments.class, Pipeline.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch(JAXBException ex){
            throw new BreedingViewXMLWriterException("Error with opening JAXB context and marshaller: "
                    + ex.getMessage(), ex);
        }
        return marshaller;
	}


	private BreedingViewSession getBreedingViewSession(
			BreedingViewProject project, DataFile data, SSAParameters ssaParameters) {
		BreedingViewSession bvSession = new BreedingViewSession();
        bvSession.setBreedingViewProject(project);
        bvSession.setDataFile(data);
        bvSession.setIbws(ssaParameters);
        return bvSession;
	}


	private BreedingViewProject getBreedingViewProject() {
		Environments environments = getEnvironments();
		Design design = getDesign();
        Traits traits = getTraits();
		DataConfiguration dataConfiguration = getDataConfiguration(environments,design,traits);
        Pipelines pipelines = getPipelines(dataConfiguration);
		BreedingViewProject project = new BreedingViewProject();
        project.setName(breedingViewInput.getBreedingViewAnalysisName());
        project.setVersion("1.2");
        project.setPipelines(pipelines);
        return project;
	}


	private Pipelines getPipelines(DataConfiguration dataConfiguration) {
		Pipelines pipelines = new Pipelines();
        Pipeline pipeline = new Pipeline();
        pipeline.setType("SEA");
        pipeline.setDataConfiguration(dataConfiguration);
        pipelines.add(pipeline);
        return pipelines;
	}


	private SSAParameters getSSAParameters() throws BreedingViewXMLWriterException {
		SSAParameters ssaParameters = new SSAParameters();
    	ssaParameters.setWebApiUrl(getWebApiUrl());
	    ssaParameters.setStudyId(breedingViewInput.getStudyId());
	    ssaParameters.setInputDataSetId(breedingViewInput.getDatasetId());
	    ssaParameters.setOutputDataSetId(breedingViewInput.getOutputDatasetId());
	
	    Project workbenchProject = getLastOpenedProject();
	    if(workbenchProject != null) {
	        ssaParameters.setWorkbenchProjectId(workbenchProject.getProjectId());
	    }
	    try{
	        String installationDirectory = getInstallationDirectory();
	        String outputDirectory = String.format("%s/workspace/%s/breeding_view/output", installationDirectory, workbenchProject.getProjectName());
	        ssaParameters.setOutputDirectory(outputDirectory);
	    } catch(MiddlewareQueryException ex){
	        throw new BreedingViewXMLWriterException("Error with getting installation directory: " + breedingViewInput.getDatasetId()
	                + ": " + ex.getMessage(), ex);
	    }
	    return ssaParameters;
	}


	protected Project getLastOpenedProject() {
		return sessionData.getLastOpenedProject();
	}

	protected String getInstallationDirectory() throws MiddlewareQueryException {
		return workbenchDataManager.getWorkbenchSetting().getInstallationDirectory();
	}

	protected String getWebApiUrl() {
		String url = webApiUrl + "?restartApplication";
		url += ToolUtil.getWorkbenchContextParameters();
		return url;
	}

	private Environments getEnvironments() {
		Environments environments = new Environments();
        environments.setName(breedingViewInput.getEnvironment().getName().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
        
        for( SeaEnvironmentModel s : breedingViewInput.getSelectedEnvironments()){
        	org.generationcp.commons.sea.xml.Environment env = new org.generationcp.commons.sea.xml.Environment();
        	env.setName(s.getEnvironmentName().replace(",",";"));
        	env.setActive(true);
        	if (s.getActive()) environments.add(env);
        }
        return environments;
	}


	private Design getDesign() {
		Design design = new Design();
        design.setType(breedingViewInput.getDesignType());
        
        if (breedingViewInput.getBlocks() != null)
        	breedingViewInput.getBlocks().setName(breedingViewInput.getBlocks().getName().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
        design.setBlocks(breedingViewInput.getBlocks());
        
        if (breedingViewInput.getReplicates() != null)
        	breedingViewInput.getReplicates().setName(breedingViewInput.getReplicates().getName().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
        design.setReplicates(breedingViewInput.getReplicates());
        
        if (breedingViewInput.getColumns() != null)
        	breedingViewInput.getColumns().setName(breedingViewInput.getColumns().getName().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
        design.setColumns(breedingViewInput.getColumns());
        
        if (breedingViewInput.getRows() != null)
        	breedingViewInput.getRows().setName(breedingViewInput.getRows().getName().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
        design.setRows(breedingViewInput.getRows());
        
        if (breedingViewInput.getPlot() != null)
        	breedingViewInput.getPlot().setName(breedingViewInput.getPlot().getName().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
        design.setPlot(breedingViewInput.getPlot());
        return design;
	}


	private DataConfiguration getDataConfiguration(
			Environments environments, 
			Design design,
			Traits traits) {
		
		DataConfiguration dataConfiguration = new DataConfiguration();
        dataConfiguration.setEnvironments(environments);
        dataConfiguration.setDesign(design);
        
        if (breedingViewInput.getGenotypes() != null){
        	breedingViewInput.getGenotypes().setName(breedingViewInput.getGenotypes().getName().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
        	breedingViewInput.getGenotypes().setEntry(breedingViewInput.getGenotypes().getEntry().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
        }
        dataConfiguration.setGenotypes(breedingViewInput.getGenotypes());
        dataConfiguration.setTraits(traits);
        return dataConfiguration;
	}


	private Traits getTraits() {
		Traits traits = new Traits();
		SortedSet<String> keys = new TreeSet<String>(breedingViewInput.getVariatesActiveState().keySet());
		for (String key : keys){
			if(breedingViewInput.getVariatesActiveState().get(key)){
				Trait trait = new Trait();
	            trait.setName(key.replaceAll("[^a-zA-Z0-9-_%']+", "_"));
	            trait.setActive(true);
	            traits.add(trait);
			}
		}
		return traits;
	}


	protected void removePreviousDatastore(String outputDirectory) {
		File dataStoreFile = new File(outputDirectory+"/Datastore.qsv");
		if(dataStoreFile.exists()) {
			dataStoreFile.delete();
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		//do nothing
	}
}
