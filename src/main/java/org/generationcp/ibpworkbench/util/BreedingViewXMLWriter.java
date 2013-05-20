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

import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.generationcp.commons.breedingview.xml.BreedingViewProject;
import org.generationcp.commons.breedingview.xml.BreedingViewProjectType;
import org.generationcp.commons.breedingview.xml.Data;
import org.generationcp.commons.breedingview.xml.Phenotypic;
import org.generationcp.commons.breedingview.xml.SSAParameters;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Variate;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;


@Configurable
public class BreedingViewXMLWriter implements InitializingBean, Serializable{

    private static final long serialVersionUID = 8844276834893749854L;

    private final static Logger LOG = LoggerFactory.getLogger(BreedingViewXMLWriter.class);
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
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
    
    public void writeProjectXML() throws BreedingViewXMLWriterException{
        LOG.info("This Ran!: " + breedingViewInput.toString());
        
        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(breedingViewInput.getProject());
        
        StudyDataManager studyDataManager = managerFactory.getNewStudyDataManager();
        
        //get the variates of the dataset, the names of the numeric ones will be included in the xml
       VariableTypeList variates = null;
        try{
            variates = studyDataManager.getDataSet(breedingViewInput.getDatasetId()).getVariableTypes().getVariates();
        } catch(MiddlewareQueryException ex){
            throw new BreedingViewXMLWriterException("Error with getting variates of dataset with id: " + breedingViewInput.getDatasetId()
                                                     + ": " + ex.getMessage(), ex);
        }

        //create List of Trait XML elements from List of Variate objects
        List<Trait> traits = new ArrayList<Trait>();
        for(VariableType variate : variates.getVariableTypes()){
            //only numeric variates are used
            if(numericTypes.contains(variate.getStandardVariable().getDataType().getId())){
                Trait trait = new Trait();
                trait.setName(variate.getLocalName().trim());
                trait.setActive(true);
                traits.add(trait);
            }
        }
        
        //create Fieldbook element
        Data data = new Data();
        data.setFieldBookFile(breedingViewInput.getSourceXLSFilePath());
        
        //create the Phenotypic element
        Phenotypic phenotypic = new Phenotypic();
        phenotypic.setTraits(traits);
        phenotypic.setEnvironments(breedingViewInput.getEnvironment());
        phenotypic.setBlocks(breedingViewInput.getBlocks());
        phenotypic.setReplicates(breedingViewInput.getReplicates());
        phenotypic.setRows(breedingViewInput.getRows());
        phenotypic.setColumns(breedingViewInput.getColumns());
        phenotypic.setGenotypes(breedingViewInput.getGenotypes());
        phenotypic.setFieldbook(data);
        
        SSAParameters ssaParameters = new SSAParameters();
        ssaParameters.setWebApiUrl(webApiUrl);
        ssaParameters.setStudyId(breedingViewInput.getStudyId());
        ssaParameters.setInputDataSetId(breedingViewInput.getDatasetId());
        ssaParameters.setOutputDataSetId(0);

        Project workbenchProject = IBPWorkbenchApplication.get().getSessionData().getLastOpenedProject();
        if(workbenchProject != null) {
            ssaParameters.setWorkbenchProjectId(workbenchProject.getProjectId());
        }
        try{
            String installationDirectory = workbenchDataManager.getWorkbenchSetting().getInstallationDirectory();
            String outputDirectory = String.format("%s/workspace/%s-%s/breeding_view/output", installationDirectory, workbenchProject.getProjectId(), workbenchProject.getProjectName());
            ssaParameters.setOutputDirectory(outputDirectory);
        } catch(MiddlewareQueryException ex){
            throw new BreedingViewXMLWriterException("Error with getting installation directory: " + breedingViewInput.getDatasetId()
                    + ": " + ex.getMessage(), ex);
        }

        //create the ProjectType element
        BreedingViewProjectType projectTypeElem = new BreedingViewProjectType();
        projectTypeElem.setDesign(breedingViewInput.getDesignType());
        projectTypeElem.setType(breedingViewInput.getProjectType());
        projectTypeElem.setEnvname(breedingViewInput.getEnvironmentName());
        
        //create the Breeding View project element
        BreedingViewProject project = new BreedingViewProject();
        project.setName(breedingViewInput.getBreedingViewProjectName());
        project.setVersion(breedingViewInput.getVersion());
        project.setType(projectTypeElem);
        project.setPhenotypic(phenotypic);
        project.setSsaParameters(ssaParameters);
        
        //prepare the writing of the xml
        JAXBContext context = null;
        Marshaller marshaller = null;
        try{
            context = JAXBContext.newInstance(BreedingViewProject.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch(JAXBException ex){
            throw new BreedingViewXMLWriterException("Error with opening JAXB context and marshaller: "
                    + ex.getMessage(), ex);
        }
        
        //write the xml
        try{
            FileWriter fileWriter = new FileWriter(breedingViewInput.getDestXMLFilePath());
            marshaller.marshal(project, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch(Exception ex){
            throw new BreedingViewXMLWriterException("Error with writing xml to: " + breedingViewInput.getDestXMLFilePath() + ": " + ex.getMessage(), ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
