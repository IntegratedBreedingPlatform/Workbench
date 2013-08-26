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

import java.io.FileWriter;
import java.io.Serializable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.generationcp.commons.breedingview.xml.BreedingViewProjectType;
import org.generationcp.commons.gxe.xml.GxeData;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxePhenotypic;
import org.generationcp.commons.gxe.xml.GxeProject;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


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
       
    	//final Project workbenchProject = IBPWorkbenchApplication.get().getSessionData().getLastOpenedProject();
  
        //final ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(workbenchProject);
        
        //final StudyDataManager studyDataManager = managerFactory.getNewStudyDataManager();
        
        //Create object to be serialized
        GxePhenotypic phenotypic = new GxePhenotypic();
        
        GxeData data = new GxeData();
        if (gxeInput.getSourceXLSFilePath() != "" && gxeInput.getSourceXLSFilePath() != null){
        	data.setFieldBookFile(gxeInput.getSourceXLSFilePath());
        }else{
        	data.setCsvFile(gxeInput.getSourceCSVFilePath());
        }
        
        
        phenotypic.setFieldbook(data);
        
        phenotypic.setTraits(gxeInput.getTraits());
   
        GxeEnvironment gxeEnv = gxeInput.getEnvironment();
        gxeEnv.setName(gxeInput.getEnvironmentName());
        phenotypic.setEnvironments(gxeEnv);
        
        phenotypic.setGenotypes(gxeInput.getGenotypes());
        
        GxeProject project = new GxeProject();
        project.setName(gxeInput.getBreedingViewProjectName());
        project.setVersion("1.01");
        project.setPhenotypic(phenotypic);

        
        BreedingViewProjectType t = new BreedingViewProjectType();
        t.setType("GxE analysis");
        project.setType(t);
        
        
        //prepare the writing of the xml
        JAXBContext context = null;
        Marshaller marshaller = null;
        try{
            context = JAXBContext.newInstance(GxeProject.class);
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
            marshaller.marshal(project, fileWriter);
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
