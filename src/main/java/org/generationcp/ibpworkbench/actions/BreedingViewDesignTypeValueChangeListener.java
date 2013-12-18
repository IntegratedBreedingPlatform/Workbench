package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.ibpworkbench.ui.ibtools.breedingview.select.SelectDetailsForBreedingViewPanel;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;


public class BreedingViewDesignTypeValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SelectDetailsForBreedingViewPanel source;
    
    public BreedingViewDesignTypeValueChangeListener(SelectDetailsForBreedingViewPanel source){
        this.source = source;
    }
    
    @Override
    public void valueChange(ValueChangeEvent event) {
        String value = (String) event.getProperty().getValue();
        
        if (value == null) {
        	
        	this.source.getSelColumnFactor().setVisible(false);
        	this.source.getLblSpecifyColumnFactor().setVisible(false);
            this.source.getSelRowFactor().setVisible(false);
            this.source.getLblSpecifyRowFactor().setVisible(false);
            this.source.getSelBlocks().setVisible(false);
            this.source.getLblBlocks().setVisible(false);
        	
        	return;
        }
        
        if(value.equals(DesignType.ROW_COLUMN_DESIGN.getName())){
        	/**
            this.source.getSelColumnFactor().setEnabled(true);
            this.source.getSelRowFactor().setEnabled(true);
            this.source.getSelBlocks().setEnabled(false);
            **/
        	
        	 this.source.getSelColumnFactor().setVisible(true);
        	 this.source.getLblSpecifyColumnFactor().setVisible(true);
             this.source.getSelRowFactor().setVisible(true);
             this.source.getLblSpecifyRowFactor().setVisible(true);
             this.source.getSelBlocks().setVisible(false);
             this.source.getLblBlocks().setVisible(false);
             
             
        } else if(value.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName())){
            /**this.source.getSelColumnFactor().setEnabled(false);
            this.source.getSelRowFactor().setEnabled(false);
            this.source.getSelBlocks().setEnabled(true);
            **/
        
        	this.source.getSelColumnFactor().setVisible(false);
        	this.source.getLblSpecifyColumnFactor().setVisible(false);
            this.source.getSelRowFactor().setVisible(false);
            this.source.getLblSpecifyRowFactor().setVisible(false);
            this.source.getSelBlocks().setVisible(true);
            this.source.getLblBlocks().setVisible(true);
            
        } else if(value.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName())){
            /**
        	this.source.getSelColumnFactor().setEnabled(false);
            this.source.getSelRowFactor().setEnabled(false);
            this.source.getSelBlocks().setEnabled(false);
            **/
        	
        	this.source.getSelColumnFactor().setVisible(false);
        	this.source.getLblSpecifyColumnFactor().setVisible(false);
            this.source.getSelRowFactor().setVisible(false);
            this.source.getLblSpecifyRowFactor().setVisible(false);
            this.source.getSelBlocks().setVisible(false);
            this.source.getLblBlocks().setVisible(false);
            
        } else if(value.equals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName())){
            this.source.getSelColumnFactor().setEnabled(false);
            this.source.getSelRowFactor().setEnabled(false);
            this.source.getSelBlocks().setEnabled(true);
        } else if(value.equals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName())){
            this.source.getSelColumnFactor().setEnabled(true);
            this.source.getSelRowFactor().setEnabled(true);
            this.source.getSelBlocks().setEnabled(false);
        }
    }

}
