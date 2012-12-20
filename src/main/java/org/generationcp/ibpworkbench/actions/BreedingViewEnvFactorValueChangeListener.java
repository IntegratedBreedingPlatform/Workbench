package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDetailsForBreedingViewWindow;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;


public class BreedingViewEnvFactorValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SelectDetailsForBreedingViewWindow source;
    
    public BreedingViewEnvFactorValueChangeListener(SelectDetailsForBreedingViewWindow source){
        this.source = source;
    }
    
    @Override
    public void valueChange(ValueChangeEvent event) {
        source.populateChoicesForEnvForAnalysis();
    }

}
