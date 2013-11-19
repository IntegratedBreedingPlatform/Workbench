package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDetailsForBreedingViewPanel;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;


public class BreedingViewEnvFactorValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SelectDetailsForBreedingViewPanel source;
    
    public BreedingViewEnvFactorValueChangeListener(SelectDetailsForBreedingViewPanel source){
        this.source = source;
    }
    
    @Override
    public void valueChange(ValueChangeEvent event) {
        source.populateChoicesForEnvForAnalysis();
    }

}
