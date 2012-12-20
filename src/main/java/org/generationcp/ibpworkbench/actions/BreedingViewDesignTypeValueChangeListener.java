package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDetailsForBreedingViewWindow;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;


public class BreedingViewDesignTypeValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SelectDetailsForBreedingViewWindow source;
    
    public BreedingViewDesignTypeValueChangeListener(SelectDetailsForBreedingViewWindow source){
        this.source = source;
    }
    
    @Override
    public void valueChange(ValueChangeEvent event) {
        String value = (String) event.getProperty().getValue();
        
        if(value.equals(DesignType.ROW_COLUMN_DESIGN.getName())){
            this.source.getSelColumnFactor().setEnabled(true);
            this.source.getSelRowFactor().setEnabled(true);
            this.source.getSelBlocks().setEnabled(false);
        } else if(value.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName())){
            this.source.getSelColumnFactor().setEnabled(false);
            this.source.getSelRowFactor().setEnabled(false);
            this.source.getSelBlocks().setEnabled(true);
        } else if(value.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName())){
            this.source.getSelColumnFactor().setEnabled(false);
            this.source.getSelRowFactor().setEnabled(false);
            this.source.getSelBlocks().setEnabled(false);
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
