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
import org.generationcp.commons.breedingview.xml.Fieldbook;
import org.generationcp.commons.breedingview.xml.Phenotypic;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Variate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


@Configurable
public class BreedingViewXMLWriter implements InitializingBean, Serializable{

    private static final long serialVersionUID = 8844276834893749854L;

    private final static Logger LOG = LoggerFactory.getLogger(BreedingViewXMLWriter.class);
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    private BreedingViewInput breedingViewInput;

    public BreedingViewXMLWriter(BreedingViewInput breedingViewInput) {
        
        this.breedingViewInput = breedingViewInput;
        
    }
    
    public void writeProjectXML() throws BreedingViewXMLWriterException{
        LOG.info("This Ran!: " + breedingViewInput.toString());
        
        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(breedingViewInput.getProject());
        
        StudyDataManager studyDataManager = managerFactory.getStudyDataManager();
        
        //get the variates of the dataset, the names of the numeric ones will be included in the xml
        List<Variate> variates = null;
        try{
            variates = studyDataManager.getVariatesByRepresentationId(breedingViewInput.getDatasetId());
        } catch(MiddlewareQueryException ex){
            throw new BreedingViewXMLWriterException("Error with getting variates of dataset with id: " + breedingViewInput.getDatasetId()
                                                     + ": " + ex.getMessage(), ex);
        }

        //create List of Trait XML elements from List of Variate objects
        List<Trait> traits = new ArrayList<Trait>();
        for(Variate variate : variates){
            //only numeric variates are used
            if(variate.getDataType().equals("N")){
                Trait trait = new Trait();
                trait.setName(variate.getName().trim());
                trait.setActive(true);
                traits.add(trait);
            }
        }
        
        //create Fieldbook element
        Fieldbook fieldbook = new Fieldbook();
        fieldbook.setFile(breedingViewInput.getSourceXLSFilePath());
        
        //create the Phenotypic element
        Phenotypic phenotypic = new Phenotypic();
        phenotypic.setTraits(traits);
        phenotypic.setEnvironments(breedingViewInput.getEnvironment());
        phenotypic.setBlocks(breedingViewInput.getBlocks());
        phenotypic.setReplicates(breedingViewInput.getReplicates());
        phenotypic.setRows(breedingViewInput.getRows());
        phenotypic.setColumns(breedingViewInput.getColumns());
        phenotypic.setGenotypes(breedingViewInput.getGenotypes());
        phenotypic.setFieldbook(fieldbook);
        
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
