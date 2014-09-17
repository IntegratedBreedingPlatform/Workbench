package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;


public class BreedingViewEnvNameForAnalysisValueChangeListener implements ValueChangeListener{

    private static final long serialVersionUID = -6425208753343322313L;

    SingleSiteAnalysisDetailsPanel source;
    
    public BreedingViewEnvNameForAnalysisValueChangeListener(SingleSiteAnalysisDetailsPanel source){
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
				MessageNotifier.showRequiredFieldError(source.getParent().getWindow(),"The selected environment factor and its value is not a valid selection for breeding view.");
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
