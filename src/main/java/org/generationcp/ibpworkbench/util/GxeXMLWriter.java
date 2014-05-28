/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * @author Sir Aldrin Batac
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.ibpworkbench.util;

import org.generationcp.commons.breedingview.xml.BreedingViewProjectType;
import org.generationcp.commons.breedingview.xml.SSAParameters;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeData;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxePhenotypic;
import org.generationcp.commons.gxe.xml.GxeProject;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.sea.xml.*;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileWriter;
import java.io.Serializable;


@Configurable
public class GxeXMLWriter implements InitializingBean, Serializable{

    private static final long serialVersionUID = 8866276834893749854L;

    private final static Logger LOG = LoggerFactory.getLogger(GxeXMLWriter.class);
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    private GxeInput gxeInput;

    public GxeXMLWriter(GxeInput gxeInput) {
    	this.gxeInput = gxeInput;
    }
    
    public void writeProjectXML() throws GxeXMLWriterException{
        
    	Traits traits = new Traits();
        for( Trait t : gxeInput.getTraits()){
        	t.setBlues(t.getName());
        	t.setBlups(t.getName().replace("_Means", "_BLUPs"));
        	t.setName(t.getName());
            traits.add(t);
        }
        
        //create DataFile element
        DataFile data = new DataFile();
        data.setName(gxeInput.getSourceCSVFilePath());
        data.setSummarystats(gxeInput.getSourceCSVSummaryStatsFilePath());
        
        Environments environments = new Environments();
        environments.setName(gxeInput.getEnvironmentName());
        environments.setEnvironments(gxeInput.getSelectedEnvironments());
        
        for (Environment e : environments.getEnvironments()){
        	e.setName(e.getName().replace(",", ";"));
        }
        
        //create the DataConfiguration element
        DataConfiguration dataConfiguration = new DataConfiguration();
        dataConfiguration.setName("GxE Analysis");
        dataConfiguration.setEnvironments(environments);
        dataConfiguration.setGenotypes(gxeInput.getGenotypes());
        dataConfiguration.setTraits(traits);
        dataConfiguration.setHeritabilities(gxeInput.getHeritabilities());
        
        if (!gxeInput.getEnvironmentGroup().equalsIgnoreCase("Analyze all")){
        	MegaEnvironment megaEnv = new MegaEnvironment();
        	MegaEnvironments megaEnvs = new MegaEnvironments();
        	megaEnv.setActive(true);
        	megaEnv.setName(gxeInput.getEnvironmentGroup());
        	megaEnvs.add(megaEnv);
        	dataConfiguration.setMegaEnvironments(megaEnvs);
        }else{
        	MegaEnvironment megaEnv = new MegaEnvironment();
        	MegaEnvironments megaEnvs = new MegaEnvironments();
        	megaEnv.setActive(false);
        	megaEnv.setName("Analyze all");
        	megaEnvs.add(megaEnv);
        	dataConfiguration.setMegaEnvironments(megaEnvs);
        }
        
        
        Pipelines pipelines = new Pipelines();
        Pipeline pipeline = new Pipeline();
        pipeline.setType("GXE");
        pipeline.setDataConfiguration(dataConfiguration);
        pipelines.add(pipeline);
        
        
        //create the Breeding View project element
        org.generationcp.commons.sea.xml.BreedingViewProject project = new org.generationcp.commons.sea.xml.BreedingViewProject();
        project.setName(gxeInput.getBreedingViewProjectName());
        project.setVersion("1.2");
        project.setPipelines(pipelines);
        
        
        BreedingViewSession bvSession = new BreedingViewSession();
        bvSession.setBreedingViewProject(project);
        bvSession.setDataFile(data);
        bvSession.setIbws(new SSAParameters());
        
        //prepare the writing of the xml
        JAXBContext context = null;
        Marshaller marshaller = null;
        try{
            context = JAXBContext.newInstance(BreedingViewSession.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch(final JAXBException ex){
            throw new GxeXMLWriterException("Error with opening JAXB context and marshaller: "
                    + ex.getMessage(), ex);
        }
        
        //write the xml
        try{
        	
        	//new File(outputDirectory).mkdirs();
            final FileWriter fileWriter = new FileWriter(gxeInput.getDestXMLFilePath());
            marshaller.marshal(bvSession, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch(final Exception ex){
            throw new GxeXMLWriterException(String.format("Error with writing xml to: %s : %s" , "" ,ex.getMessage()), ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
