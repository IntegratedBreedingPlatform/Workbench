package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;


public class BreedingViewEnvFactorValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SingleSiteAnalysisDetailsPanel source;
    
    public BreedingViewEnvFactorValueChangeListener(SingleSiteAnalysisDetailsPanel source){
        this.source = source;
    }
    
    @Override
    public void valueChange(ValueChangeEvent event) {
        source.populateChoicesForEnvForAnalysis();
    }

}
