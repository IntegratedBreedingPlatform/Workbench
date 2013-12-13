/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.EnvironmentLabel;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.ibtools.breedingview.select.SelectDetailsForBreedingViewPanel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriter;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriterException;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.ibpworkbench.util.DatasetExporterException;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.StringUtils;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

/**
 * 
 * @author Jeffrey Morales
 *
 */
public class RunBreedingViewAction implements ClickListener {
    private static final long serialVersionUID = 1L;
    
    private final static Logger log = LoggerFactory.getLogger(RunBreedingViewAction.class);
    
    private SelectDetailsForBreedingViewPanel source;
    
    public RunBreedingViewAction(SelectDetailsForBreedingViewPanel selectDetailsForBreedingViewWindow) {
        
        this.source = selectDetailsForBreedingViewWindow;
        
    }

    private static final Logger LOG = LoggerFactory.getLogger(RunBreedingViewAction.class);
    
    @Override
    public void buttonClick(final ClickEvent event) {
        
        BreedingViewInput breedingViewInput = this.source.getBreedingViewInput();
        
        breedingViewInput.setSelectedEnvironments(source.getSelectedEnvironments());
        
        String analysisProjectName = (String) this.source.getTxtAnalysisName().getValue();
        if(StringUtils.isNullOrEmpty(analysisProjectName)){
            event.getComponent().getWindow().showNotification("Please enter an Analysis Name.", Notification.TYPE_ERROR_MESSAGE);
            return;
        } else{
            breedingViewInput.setBreedingViewProjectName(analysisProjectName);
        }
        
        String envFactor = (String) this.source.getSelEnvFactor().getValue();
        
        if (StringUtils.isNullOrEmpty(envFactor)){
        	event.getComponent().getWindow().showNotification("Please select an environment factor.", Notification.TYPE_ERROR_MESSAGE);
        	return;
        }
        
        
        if(!StringUtils.isNullOrEmpty(envFactor)){
            Environment environment = new Environment();
            environment.setName(envFactor.trim());
            
            if(breedingViewInput.getSelectedEnvironments().size() == 0){
                event.getComponent().getWindow().showNotification("Please select environment for analysis.", Notification.TYPE_ERROR_MESSAGE);
                return;
            } else{
               
                breedingViewInput.setEnvironment(environment);
                
            }
        } else{
            breedingViewInput.setEnvironment(null);
        }
                
        String designType = (String) this.source.getSelDesignType().getValue();
        if(StringUtils.isNullOrEmpty(designType)){
            event.getComponent().getWindow().showNotification("Please specify design type.", Notification.TYPE_ERROR_MESSAGE);
            return;
        } else{
            breedingViewInput.setDesignType(designType);
        }
        
        String replicates = (String) this.source.getSelReplicates().getValue();
        if(StringUtils.isNullOrEmpty(replicates)){
            if(designType.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName())){
                event.getComponent().getWindow().showNotification("Please specify replicates factor.", Notification.TYPE_ERROR_MESSAGE);
                return;
            } else{
                breedingViewInput.setReplicates(null);
            }
        } else{
            Replicates reps = new Replicates();
            reps.setName(replicates.trim());
            breedingViewInput.setReplicates(reps);
            
            if(designType.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName())){
                breedingViewInput.setDesignType(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
            } else if(designType.equals(DesignType.ROW_COLUMN_DESIGN.getName())){
                breedingViewInput.setDesignType(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());
            }
        }
        
        String blocksName = (String) this.source.getSelBlocks().getValue();
        if(StringUtils.isNullOrEmpty(blocksName)){
            if(this.source.getSelBlocks().isEnabled()){
                event.getComponent().getWindow().showNotification("Please specify incomplete block factor.", Notification.TYPE_ERROR_MESSAGE);
                return;
            } else{
                breedingViewInput.setBlocks(null);
            }
        } else{
            Blocks blocks = new Blocks();
            blocks.setName(blocksName.trim());
            breedingViewInput.setBlocks(blocks);
        }
        
        String rowName = (String) this.source.getSelRowFactor().getValue();
        if(StringUtils.isNullOrEmpty(rowName)){
            if(this.source.getSelRowFactor().isEnabled()){
                event.getComponent().getWindow().showNotification("Please specify row factor.", Notification.TYPE_ERROR_MESSAGE);
                return;
            } else{
                breedingViewInput.setRows(null);
            }
        } else{
            Rows rows = new Rows();
            rows.setName(rowName.trim());
            breedingViewInput.setRows(rows);
        }
        
        String columnName = (String) this.source.getSelColumnFactor().getValue();
        if(StringUtils.isNullOrEmpty(columnName)){
            if(this.source.getSelColumnFactor().isEnabled()){
                event.getComponent().getWindow().showNotification("Please specify column factor.", Notification.TYPE_ERROR_MESSAGE);
                return;
            } else{
                breedingViewInput.setColumns(null);
            }
        } else{
            Columns columns = new Columns();
            columns.setName(columnName.trim());
            breedingViewInput.setColumns(columns);
        }
        
        String genotypesName = (String) this.source.getSelGenotypes().getValue();
        if(StringUtils.isNullOrEmpty(genotypesName)){
            event.getComponent().getWindow().showNotification("Please specify Genotypes factor.", Notification.TYPE_ERROR_MESSAGE);
            return;
        } else{
           
            String entry  = "";
			try {
				entry = source.getManagerFactory().getNewStudyDataManager().getLocalNameByStandardVariableId(breedingViewInput.getDatasetId(), 8230);
			} catch (ConfigException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			Genotypes genotypes = new Genotypes();
	        genotypes.setName(genotypesName.trim());
            genotypes.setEntry(entry);
            breedingViewInput.setGenotypes(genotypes);
        }
        
        
        DatasetExporter datasetExporter = new DatasetExporter(source.getManagerFactory().getNewStudyDataManager(), null, breedingViewInput.getDatasetId());
        
        try {
			//HashMap<Integer, String> variateColumns = datasetExporter.exportToFieldBookCSVUsingIBDBv2(breedingViewInput.getSourceXLSFilePath(), (String) this.source.getSelEnvFactor().getValue(), (String) this.source.getSelEnvForAnalysis().getValue());
        	List<String> selectedEnvironments = new ArrayList<String>();
        	for (SeaEnvironmentModel m : breedingViewInput.getSelectedEnvironments()){
        		selectedEnvironments.add(m.getEnvironmentName());
        	}
        	
        	HashMap<Integer, String> variateColumns = datasetExporter.exportToFieldBookCSVUsingIBDBv2(breedingViewInput.getSourceXLSFilePath(), (String) this.source.getSelEnvFactor().getValue(), selectedEnvironments);
        	breedingViewInput.setVariateColumns(variateColumns);
        } catch (DatasetExporterException e1) {
			e1.printStackTrace();
		}
       
		launchBV(event);
      	    
       
    }   
    
    private void launchBV(ClickEvent event){
    	
    	 BreedingViewXMLWriter breedingViewXMLWriter;
         BreedingViewInput breedingViewInput = this.source.getBreedingViewInput();
    	 
         try {
             breedingViewXMLWriter = new BreedingViewXMLWriter(breedingViewInput);
             breedingViewXMLWriter.writeProjectXMLV2();
             
             File absoluteToolFile = new File(this.source.getTool().getPath()).getAbsoluteFile();
             Runtime runtime = Runtime.getRuntime();
             LOG.info(breedingViewInput.toString());
             LOG.info(absoluteToolFile.getAbsolutePath() + " -project=\"" +  breedingViewInput.getDestXMLFilePath() + "\"");
             runtime.exec(absoluteToolFile.getAbsolutePath() + " -project=\"" +  breedingViewInput.getDestXMLFilePath() + "\"");
             
         } catch (BreedingViewXMLWriterException e) {
             log.debug("Cannot write Breeding View input XML", e);
             
             MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
         } catch (IOException e) {
             log.debug("Cannot write Breeding View input XML", e);
             
             MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
         }
            	
    	
    }
    
  
}
