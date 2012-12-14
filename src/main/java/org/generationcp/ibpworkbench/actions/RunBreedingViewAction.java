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

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDetailsForBreedingViewWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriter;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriterException;
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
    
    private SelectDetailsForBreedingViewWindow selectDetailsForBreedingViewWindow;
    
    public RunBreedingViewAction(SelectDetailsForBreedingViewWindow selectDetailsForBreedingViewWindow) {
        
        this.selectDetailsForBreedingViewWindow = selectDetailsForBreedingViewWindow;
        
    }

    private static final Logger LOG = LoggerFactory.getLogger(RunBreedingViewAction.class);
    
    @Override
    public void buttonClick(ClickEvent event) {
        
        //IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        BreedingViewInput breedingViewInput = selectDetailsForBreedingViewWindow.getBreedingViewInput();
        
        if (StringUtils.isNullOrEmpty(breedingViewInput.getEnvironmentName())) {
            
            breedingViewInput.setEnvironmentName((String)selectDetailsForBreedingViewWindow.getTxtEnvironment().getValue());
            
        }
        
        if (StringUtils.isNullOrEmpty(breedingViewInput.getVersion())) {
            
            breedingViewInput.setVersion((String)selectDetailsForBreedingViewWindow.getTxtVersion().getValue());
            
        }
        
        if (StringUtils.isNullOrEmpty(breedingViewInput.getDesignType())) {
            
            breedingViewInput.setDesignType((String)selectDetailsForBreedingViewWindow.getSelDesignType().getValue());
            
        }
        
        String blockName = null;
        
        if (breedingViewInput.getBlocks() == null) {
        
            Blocks blocks = new Blocks();
            blocks.setName((String)selectDetailsForBreedingViewWindow.getTxtBlocks().getValue());
            breedingViewInput.setBlocks(blocks);
                    
        } else {
            
            blockName = breedingViewInput.getBlocks().getName();
            
            if (StringUtils.isNullOrEmpty(blockName)) {
                
                breedingViewInput.getBlocks().setName((String)selectDetailsForBreedingViewWindow.getTxtBlocks().getValue());
                
            }
            
        }
        
        String replicateName = null;
        
        if (breedingViewInput.getReplicates() == null) {
        
            Replicates replicates = new Replicates();
            replicates.setName((String)selectDetailsForBreedingViewWindow.getTxtReplicates().getValue());
            breedingViewInput.setReplicates(replicates);
                    
        } else {
            
            replicateName = breedingViewInput.getReplicates().getName();
            
            if (StringUtils.isNullOrEmpty(replicateName)) {
                
                breedingViewInput.getReplicates().setName((String)selectDetailsForBreedingViewWindow.getTxtReplicates().getValue());
                
            }
            
        }
        
        if (StringUtils.isNullOrEmpty(replicateName)) {
            
            Replicates replicates = new Replicates();
            replicates.setName((String)selectDetailsForBreedingViewWindow.getTxtReplicates().getValue());
            
            breedingViewInput.setReplicates(replicates);
            
        }
        
        if (StringUtils.isNullOrEmpty(breedingViewInput.getDesignType())) {
            
            
            
            breedingViewInput.setDesignType((String)selectDetailsForBreedingViewWindow.getSelDesignType().getValue());
            
        }
        
        
        if (!StringUtils.isNullOrEmpty(breedingViewInput.getEnvironmentName())
                && !StringUtils.isNullOrEmpty(breedingViewInput.getVersion())
                && !StringUtils.isNullOrEmpty(breedingViewInput.getDesignType())
                && !StringUtils.isNullOrEmpty(breedingViewInput.getBlocks().getName())
                && !StringUtils.isNullOrEmpty(breedingViewInput.getReplicates().getName())
                ){
            
            LOG.info("Yes!");
            
            BreedingViewXMLWriter breedingViewXMLWriter;
            
            try {
                
                breedingViewXMLWriter = new BreedingViewXMLWriter(breedingViewInput);
                
                breedingViewXMLWriter.writeProjectXML();
                
                File absoluteToolFile = new File(selectDetailsForBreedingViewWindow.getTool().getPath()).getAbsoluteFile();
                
                Runtime runtime = Runtime.getRuntime();
                
                LOG.info(breedingViewInput.toString());
                
                LOG.info(absoluteToolFile.getAbsolutePath() + " -project=\"" +  breedingViewInput.getDestXMLFilePath() + "\"");
                
                runtime.exec(absoluteToolFile.getAbsolutePath() + " -project=\"" +  breedingViewInput.getDestXMLFilePath() + "\"");
                
            } catch (BreedingViewXMLWriterException e) {
                MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
            } catch (IOException e) {
                MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
            }
            
            event.getComponent().getWindow().getParent().removeWindow(selectDetailsForBreedingViewWindow);
        
        } else {
            
            LOG.info("No!");
            
            event.getComponent().getWindow().showNotification("Please Fill All Required Fields and/or Selections.", Notification.TYPE_ERROR_MESSAGE);
            
        }


    }
   
}
