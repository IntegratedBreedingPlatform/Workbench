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
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDetailsForBreedingViewWindow;
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
    
    private SelectDetailsForBreedingViewWindow source;
    
    public RunBreedingViewAction(SelectDetailsForBreedingViewWindow selectDetailsForBreedingViewWindow) {
        
        this.source = selectDetailsForBreedingViewWindow;
        
    }

    private static final Logger LOG = LoggerFactory.getLogger(RunBreedingViewAction.class);
    
    @Override
    public void buttonClick(final ClickEvent event) {
        
        BreedingViewInput breedingViewInput = this.source.getBreedingViewInput();
        
        DatasetExporter datasetExporter = new DatasetExporter(source.getManagerFactory().getNewStudyDataManager(), null, breedingViewInput.getDatasetId());
        try {
			HashMap<Integer, String> variateColumns = datasetExporter.exportToFieldBookCSVUsingIBDBv2(breedingViewInput.getSourceXLSFilePath(),  (String) this.source.getSelEnvForAnalysis().getValue());
			breedingViewInput.setVariateColumns(variateColumns);
        } catch (DatasetExporterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        String newVal = source.getSelEnvFactor().getValue() + "-" + source.getSelEnvForAnalysis().getValue();
        source.getTxtNameForAnalysisEnv().setValue(newVal);
        //txtNameForAnalysisEnv
        
        String analysisProjectName = (String) this.source.getTxtAnalysisName().getValue();
        if(StringUtils.isNullOrEmpty(analysisProjectName)){
            event.getComponent().getWindow().showNotification("Please enter an Analysis Name.", Notification.TYPE_ERROR_MESSAGE);
            return;
        } else{
            breedingViewInput.setBreedingViewProjectName(analysisProjectName);
        }
        
        String envFactor = (String) this.source.getSelEnvFactor().getValue();
        if(!StringUtils.isNullOrEmpty(envFactor)){
            Environment environment = new Environment();
            environment.setName(envFactor.trim());
            
            String envForAnalysis = (String) this.source.getSelEnvForAnalysis().getValue();
            if(StringUtils.isNullOrEmpty(envForAnalysis)){
                event.getComponent().getWindow().showNotification("Please select environment for analysis.", Notification.TYPE_ERROR_MESSAGE);
                return;
            } else{
                if(envForAnalysis.contains("-")){
                    StringTokenizer tokenizer = new StringTokenizer(envForAnalysis, "-");
                    if(tokenizer.hasMoreTokens()){
                        envForAnalysis = tokenizer.nextToken().trim();
                    }
                }
                
                EnvironmentLabel label = new EnvironmentLabel();
                label.setName(envForAnalysis);
                label.setTrial(envForAnalysis);
                label.setSubset(true);
                
                environment.setLabel(label);
                breedingViewInput.setEnvironment(environment);
            }
        } else{
            breedingViewInput.setEnvironment(null);
        }
        
        String envName = (String) this.source.getTxtNameForAnalysisEnv().getValue();
        if(StringUtils.isNullOrEmpty(envName)){
            event.getComponent().getWindow().showNotification("Please specify name for analysis environment.", Notification.TYPE_ERROR_MESSAGE);
            return;
        } else{
            breedingViewInput.setEnvironmentName(envName);
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
        
        /**
        try {
			final DataSet sourceDataSet = source.getManagerFactory().getNewStudyDataManager().getDataSet(breedingViewInput.getDatasetId());
			final DataSet meansDataSet = source.getManagerFactory().getNewStudyDataManager().getDataSet(breedingViewInput.getOutputDatasetId());
			
			if (sourceDataSet != null && meansDataSet != null){
				
				if (checkColumnsChanged(sourceDataSet, meansDataSet)){
					  ConfirmDialog.show(source.getParent(), "Delete Means Dataset",  
				        		"Do you want to delete the existing means dataset?", 
				        		"Delete", 
				        		"No", 
				        		new ConfirmDialog.Listener() {
										@Override
										public void onClose(ConfirmDialog dialog) {
											if (dialog.isConfirmed()){
												try {
													source.getManagerFactory().getNewStudyDataManager().deleteDataSet(meansDataSet.getId());
													source.getBreedingViewInput().setOutputDatasetId(0);
												} catch (ConfigException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												} catch (MiddlewareQueryException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												
											}
												
											launchBV(event);
										}
					        		}
				        );		
				}else{
					launchBV(event);
				}
				
				
			}else{
				launchBV(event);
			}
			
			
			
			
			
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
       
		launchBV(event);
      	
        
       
	
        
       
    }   
    
    private void launchBV(ClickEvent event){
    	
    	 BreedingViewXMLWriter breedingViewXMLWriter;
         BreedingViewInput breedingViewInput = this.source.getBreedingViewInput();
    	 
         try {
             breedingViewXMLWriter = new BreedingViewXMLWriter(breedingViewInput);
             breedingViewXMLWriter.writeProjectXML();
             
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
             
         event.getComponent().getWindow().getParent().removeWindow(this.source);
    	
    	
    }
    
    
    
    private boolean checkColumnsChanged(DataSet sourceDataSet,DataSet meansDataSet){
    	
    	List<Integer> numericTypes = new ArrayList<Integer>();
 
         
         numericTypes.add(TermId.NUMERIC_VARIABLE.getId());
         numericTypes.add(TermId.MIN_VALUE.getId());
         numericTypes.add(TermId.MAX_VALUE.getId());
         numericTypes.add(TermId.DATE_VARIABLE.getId());
         numericTypes.add(TermId.NUMERIC_DBID_VARIABLE.getId());
    	
    	
    	
    	List<String> header1 = new ArrayList<String>();
    	List<String> header2 = new ArrayList<String>();
    	for (VariableType var : sourceDataSet.getVariableTypes().getVariates().getVariableTypes()){
    		if (numericTypes.contains(var.getStandardVariable().getDataType().getId())){
    			header1.add((var.getLocalName().trim() + "_Means").toLowerCase());
    		}
    		
    	}
    	for (VariableType var : meansDataSet.getVariableTypes().getVariates().getVariableTypes()){
    		if (!var.getStandardVariable().getMethod().getName().equalsIgnoreCase("error estimate"))
    		header2.add(var.getLocalName().trim().toLowerCase());
    	}
    	Collections.sort(header1);
    	Collections.sort(header2);
    	
    	return !header2.equals(header1);
    	
    }
}
