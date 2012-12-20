package org.generationcp.ibpworkbench.actions;

import java.util.StringTokenizer;

import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDetailsForBreedingViewWindow;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;


public class BreedingViewEnvNameForAnalysisValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SelectDetailsForBreedingViewWindow source;
    
    public BreedingViewEnvNameForAnalysisValueChangeListener(SelectDetailsForBreedingViewWindow source){
        this.source = source;
    }
    
    @Override
    public void valueChange(ValueChangeEvent event) {
        String value = (String) event.getProperty().getValue();
        
        StringTokenizer tokenizer = new StringTokenizer(value, "-");
        if(tokenizer.hasMoreTokens()){
            String temp = tokenizer.nextToken();
            
            if(tokenizer.hasMoreTokens()){
                String envName = tokenizer.nextToken().trim();
                source.getTxtNameForAnalysisEnv().setValue(envName);
            }
        }
    }

}
