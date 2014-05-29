package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;


public class BreedingViewReplicatesValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SingleSiteAnalysisDetailsPanel source;
    
    public BreedingViewReplicatesValueChangeListener(SingleSiteAnalysisDetailsPanel source){
        this.source = source;
    }
    
    @Override
    public void valueChange(ValueChangeEvent event) {
        Object value = event.getProperty().getValue();
        
        if(value != null){
            String selectedDesignType = (String) this.source.getSelDesignType().getValue();
            
            if(selectedDesignType != null){
                if(selectedDesignType.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName())){
                    this.source.getSelDesignType().setValue(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
                } else if(selectedDesignType.equals(DesignType.ROW_COLUMN_DESIGN.getName())){
                    this.source.getSelDesignType().setValue(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());
                }
            }
        }
    }

}
