/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.comp.form.AddBreedingMethodForm;
import org.generationcp.ibpworkbench.comp.window.AddBreedingMethodsWindow;
import org.generationcp.ibpworkbench.model.BreedingMethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class SaveNewBreedingMethodAction implements ClickListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(SaveNewBreedingMethodAction.class);
    private static final long serialVersionUID = 1L;
   
    private AddBreedingMethodForm newBreedingMethodForm;
    
    private AddBreedingMethodsWindow window;

    public SaveNewBreedingMethodAction(AddBreedingMethodForm newBreedingMethodForm, AddBreedingMethodsWindow window) {
        this.newBreedingMethodForm = newBreedingMethodForm;
        this.window = window;
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        newBreedingMethodForm.commit();

        @SuppressWarnings("unchecked")
        BeanItem<BreedingMethodModel> breedingMethodBean = (BeanItem<BreedingMethodModel>) newBreedingMethodForm.getItemDataSource();
        BreedingMethodModel breedingMethod = breedingMethodBean.getBean();
        
        newBreedingMethodForm.commit();
        
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        if (!app.getSessionData().getUniqueBreedingMethods().contains(breedingMethod.getMethodName())){
        
            app.getSessionData().getUniqueBreedingMethods().add(breedingMethod.getMethodName());
        
            Integer nextKey = app.getSessionData().getProjectBreedingMethodData().keySet().size();
            
            nextKey = nextKey*-1;
        
            BreedingMethodModel newBreedingMethod = new BreedingMethodModel();
        
            newBreedingMethod.setMethodName(breedingMethod.getMethodName());
            newBreedingMethod.setMethodDescription(breedingMethod.getMethodDescription());
            newBreedingMethod.setMethodCode(breedingMethod.getMethodCode());
            newBreedingMethod.setMethodType(breedingMethod.getMethodType());
        
            newBreedingMethod.setMethodId(nextKey);
        
            app.getSessionData().getProjectBreedingMethodData().put(nextKey, newBreedingMethod);
            
            newBreedingMethod = null;
            
            LOG.info(app.getSessionData().getProjectBreedingMethodData().toString());
        // go back to dashboard
        //HomeAction home = new HomeAction();
        //home.buttonClick(event);
            
            newBreedingMethodForm.commit();
            
            window.getParent().removeWindow(window);
        
        }
        
    }
}
