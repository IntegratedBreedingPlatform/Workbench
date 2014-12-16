package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.GridLayout;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;


public class BreedingViewDesignTypeValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SingleSiteAnalysisDetailsPanel source;
    
    public BreedingViewDesignTypeValueChangeListener(SingleSiteAnalysisDetailsPanel source){
        this.source = source;
    }
    
    @Override
    public void valueChange(ValueChangeEvent event) {
        String value = (String) event.getProperty().getValue();
        
        
        if (value == null) {
        	
        	GridLayout gLayout = new GridLayout(2,2);
            gLayout.setColumnExpandRatio(0, 0);
            gLayout.setColumnExpandRatio(1, 1);
            gLayout.setWidth("100%");
            gLayout.setSpacing(true);
            gLayout.addStyleName("marginTop10");
        	
        	source.getBlockRowColumnContainer().removeAllComponents();
        	gLayout.addComponent(this.source.getLblSpecifyGenotypesHeader(), 0, 0, 1, 0);
        	gLayout.addComponent(this.source.getLblGenotypes(), 0, 1);
        	gLayout.addComponent(this.source.getSelGenotypes(), 1, 1);
        	source.getBlockRowColumnContainer().addComponent(gLayout);
        	return;
        }
        
        if(value.equals(DesignType.ROW_COLUMN_DESIGN.getName())){
        	
        	GridLayout gLayout = new GridLayout(2,4);
            gLayout.setColumnExpandRatio(0, 0);
            gLayout.setColumnExpandRatio(1, 1);
            gLayout.setWidth("100%");
            gLayout.setSpacing(true);
            gLayout.addStyleName("marginTop10");
        	
        	source.getBlockRowColumnContainer().removeAllComponents();
        	gLayout.addComponent(this.source.getLblSpecifyColumnFactor(), 0, 0);
        	gLayout.addComponent(this.source.getSelColumnFactor(), 1, 0);
        	gLayout.addComponent(this.source.getLblSpecifyRowFactor(), 0, 1);
        	gLayout.addComponent(this.source.getSelRowFactor(), 1, 1);
        	gLayout.addComponent(this.source.getLblSpecifyGenotypesHeader(), 0, 2, 1, 2);
        	gLayout.addComponent(this.source.getLblGenotypes(), 0, 3);
        	gLayout.addComponent(this.source.getSelGenotypes(), 1, 3);
        	source.getBlockRowColumnContainer().addComponent(gLayout);
        	
        	if (source.getSelReplicates().isEnabled() == false
        			|| source.getSelReplicates().getItemIds().size() == 0){
        		
        		for (Object itemId : source.getSelBlocks().getItemIds()){
        			source.getSelReplicates().addItem(itemId);
        			source.getSelReplicates().setItemCaption(itemId,"REPLICATES");
        			source.getSelReplicates().select(itemId);
        			source.getSelReplicates().setEnabled(true);
        		}
        	}
        
        } else if(value.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName())){
        	
        	source.displayIncompleteBlockDesignElements();
        	
            
        } else if(value.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName())){
        	
        	GridLayout gLayout = new GridLayout(2,2);
            gLayout.setColumnExpandRatio(0, 0);
            gLayout.setColumnExpandRatio(1, 1);
            gLayout.setWidth("100%");
            gLayout.setSpacing(true);
            gLayout.addStyleName("marginTop10");
            
        	source.getBlockRowColumnContainer().removeAllComponents();
        	gLayout.addComponent(this.source.getLblSpecifyGenotypesHeader(), 0, 0, 1, 0);
        	gLayout.addComponent(this.source.getLblGenotypes(), 0, 1);
        	gLayout.addComponent(this.source.getSelGenotypes(), 1, 1);
        	source.getBlockRowColumnContainer().addComponent(gLayout);
        	
        	if (source.getSelReplicates().isEnabled() == false
        			|| source.getSelReplicates().getItemIds().size() == 0){
        		
        		for (Object itemId : source.getSelBlocks().getItemIds()){
        			source.getSelReplicates().addItem(itemId);
        			source.getSelReplicates().setItemCaption(itemId,"REPLICATES");
        			source.getSelReplicates().select(itemId);
        			source.getSelReplicates().setEnabled(true);
        		}
        	}
            
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
