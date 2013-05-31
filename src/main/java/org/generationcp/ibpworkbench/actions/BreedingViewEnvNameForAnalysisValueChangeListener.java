package org.generationcp.ibpworkbench.actions;

import java.util.StringTokenizer;

import org.generationcp.ibpworkbench.comp.common.ConfirmDialog;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDetailsForBreedingViewWindow;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.domain.TrialEnvironment;
import org.generationcp.middleware.v2.domain.TrialEnvironments;

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
        
        if (value == null) return;
        
        TrialEnvironments trialEnvironments;
		try {
			trialEnvironments = source.getManagerFactory().getNewStudyDataManager().getTrialEnvironmentsInDataset(source.getBreedingViewInput().getDatasetId());
			TrialEnvironment trialEnv = trialEnvironments.findOnlyOneByLocalName(source.getSelEnvFactor().getValue().toString(), value);
			
			if (trialEnv == null){
				ConfirmDialog.show(source.getParent(), "Environment Factor Validation",  "The selected environment factor and its value is not a valid selection for breeding view.", "Okay", null, new ConfirmDialog.Listener() {
					
					@Override
					public void onClose(ConfirmDialog dialog) {
						// TODO Auto-generated method stub
						source.getSelEnvForAnalysis().select(null);
					}
				});			
				
			}
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //environment, env value
       
        
        /**StringTokenizer tokenizer = new StringTokenizer(value, "-");
        if(tokenizer.hasMoreTokens()){
            String temp = tokenizer.nextToken();
            
            if(tokenizer.hasMoreTokens()){
                String envName = tokenizer.nextToken().trim();
                source.getTxtNameForAnalysisEnv().setValue(envName);
            }
        }**/
    }

}
