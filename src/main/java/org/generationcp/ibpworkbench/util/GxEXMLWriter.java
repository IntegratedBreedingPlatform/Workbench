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

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.generationcp.commons.breedingview.xml.BreedingViewProject;
import org.generationcp.commons.breedingview.xml.BreedingViewProjectType;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Phenotypic;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Table;


@Configurable
public class GxEXMLWriter implements InitializingBean, Serializable{

    private static final long serialVersionUID = 8866276834893749854L;

    private final static Logger LOG = LoggerFactory.getLogger(GxEXMLWriter.class);
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    public GxEXMLWriter() {
   
    }
    
    public void writeProjectXML(final Table table) throws GxEXMLWriterException{
       
    	final Project workbenchProject = IBPWorkbenchApplication.get().getSessionData().getLastOpenedProject();
  
        final ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(workbenchProject);
        
        final StudyDataManager studyDataManager = managerFactory.getStudyDataManager();
        
        
        String installationDirectory = "";
		try {
			installationDirectory = workbenchDataManager.getWorkbenchSetting().getInstallationDirectory();
		} catch (final MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        final String outputDirectory = String.format("%s\\workspace\\%s-%s\\gxe\\output", installationDirectory, workbenchProject.getProjectId(), workbenchProject.getProjectName());
        final String outputFileName = String.format("%s\\samplegxe.xml", outputDirectory);
        
        //Create object to be serialized
        Phenotypic phenotypic = new Phenotypic();
        
        ArrayList<Trait> traits =  new ArrayList<Trait>();
        for (Integer x = 1; x < 6; x++) {
        Trait t = new Trait();	
        t.setName("Trait" + x);
        t.setActive(true);
        traits.add(t);
        }
       
        phenotypic.setTraits(traits);
        
        Environment e = new Environment();
        e.setName("E");
        phenotypic.setEnvironments(e);
        
        Genotypes g = new Genotypes();
        g.setName("G");
        phenotypic.setGenotypes(g);
        
        BreedingViewProject project = new BreedingViewProject();
        project.setName("F2Maize");
        project.setVersion("1.01");
        project.setPhenotypic(phenotypic);
        
        BreedingViewProjectType t = new BreedingViewProjectType();
        t.setType("GxE Analysis");
        project.setType(t);
        
        
        //prepare the writing of the xml
        JAXBContext context = null;
        Marshaller marshaller = null;
        try{
            context = JAXBContext.newInstance(BreedingViewProject.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch(final JAXBException ex){
            throw new GxEXMLWriterException("Error with opening JAXB context and marshaller: "
                    + ex.getMessage(), ex);
        }
        
        //write the xml
        try{
        	
        	new File(outputDirectory).mkdirs();
            final FileWriter fileWriter = new FileWriter(outputFileName);
            marshaller.marshal(project, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch(final Exception ex){
            throw new GxEXMLWriterException(String.format("Error with writing xml to: %s : %s" , "" ,ex.getMessage()), ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
