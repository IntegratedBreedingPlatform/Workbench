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

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.BreedingViewProject;
import org.generationcp.commons.breedingview.xml.BreedingViewProjectType;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.breedingview.xml.Fieldbook;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Phenotypic;
import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.Variate;


public class BreedingViewXmlWriter implements Serializable{

    private static final long serialVersionUID = 8844276834893749854L;

    public static void write(StudyDataManager studyDataManager, String filename, String projectName, String version
            , ProjectType projectType, DesignType designType, String environmentName, Integer datasetId
            , String fieldbookFilePath) throws BreedingViewXmlWriterException{
        
        //get the variates of the dataset, the names of the numeric ones will be included in the xml
        List<Variate> variates = null;
        try{
            variates = studyDataManager.getVariatesByRepresentationId(datasetId);
        } catch(MiddlewareQueryException ex){
            throw new BreedingViewXmlWriterException("Error with getting variates of dataset with id: " + datasetId
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
        
        //create Genotypes element with name = GID
        Genotypes genotypes = new Genotypes();
        genotypes.setName("GID");
        
        //create Blocks element
        Blocks blocks = null;
        try{
            org.generationcp.middleware.pojos.Trait blockTrait = studyDataManager.getBlockTrait();
            if(blockTrait != null){
                Factor blockFactor = studyDataManager.getFactorOfDatasetByTraitid(datasetId, blockTrait.getTraitId());
                if(blockFactor != null){
                    blocks = new Blocks();
                    blocks.setName(blockFactor.getName());
                }
            }
        } catch(MiddlewareQueryException ex){
            throw new BreedingViewXmlWriterException("Error with getting BLOCK factor of dataset with id: " + datasetId
                    + ": " + ex.getMessage(), ex);
        }
        
        //create Replicates element
        Replicates replicates = null;
        try{
            org.generationcp.middleware.pojos.Trait repTrait = studyDataManager.getReplicationTrait();
            if(repTrait != null){
                Factor repFactor = studyDataManager.getFactorOfDatasetByTraitid(datasetId, repTrait.getTraitId());
                if(repFactor != null){
                    replicates = new Replicates();
                    replicates.setName(repFactor.getName());
                }
            }
        } catch(MiddlewareQueryException ex){
            throw new BreedingViewXmlWriterException("Error with getting REPLICATION factor of dataset with id: " + datasetId
                    + ": " + ex.getMessage(), ex);
        }
        
        //create Fieldbook element
        Fieldbook fieldbook = new Fieldbook();
        fieldbook.setFile(fieldbookFilePath);
        
        //create the Phenotypic element
        Phenotypic phenotypic = new Phenotypic();
        phenotypic.setTraits(traits);
        if(blocks != null){
            phenotypic.setBlocks(blocks);
        }
        if(replicates != null){
            phenotypic.setReplicates(replicates);
        }
        phenotypic.setGenotypes(genotypes);
        phenotypic.setFieldbook(fieldbook);
        
        //create the ProjectType element
        BreedingViewProjectType projectTypeElem = new BreedingViewProjectType();
        projectTypeElem.setDesign(designType.getName());
        projectTypeElem.setType(projectType.getName());
        projectTypeElem.setEnvname(environmentName);
        
        //create the Breeding View project element
        BreedingViewProject project = new BreedingViewProject();
        project.setName(projectName);
        project.setVersion(version);
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
            throw new BreedingViewXmlWriterException("Error with opening JAXB context and marshaller: "
                    + ex.getMessage(), ex);
        }
        
        //write the xml
        try{
            FileWriter fileWriter = new FileWriter(filename);
            marshaller.marshal(project, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch(Exception ex){
            throw new BreedingViewXmlWriterException("Error with writing xml to: " + filename + ": " + ex.getMessage(), ex);
        }
    }
}
