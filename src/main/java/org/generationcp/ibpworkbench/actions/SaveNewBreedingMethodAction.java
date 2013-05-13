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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.comp.ProjectBreedingMethodsPanel;
import org.generationcp.ibpworkbench.comp.form.AddBreedingMethodForm;
import org.generationcp.ibpworkbench.comp.window.AddBreedingMethodsWindow;
import org.generationcp.ibpworkbench.model.BreedingMethodModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * 
 * @author Jeffrey Morales
 * 
 */

@Configurable
public class SaveNewBreedingMethodAction implements ClickListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(SaveNewBreedingMethodAction.class);
    private static final long serialVersionUID = 1L;
   
    private AddBreedingMethodForm newBreedingMethodForm;
    
    private AddBreedingMethodsWindow window;
    
    private ProjectBreedingMethodsPanel projectBreedingMethodsPanel;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    public SaveNewBreedingMethodAction(AddBreedingMethodForm newBreedingMethodForm, AddBreedingMethodsWindow window, ProjectBreedingMethodsPanel projectBreedingMethodsPanel) {
        this.newBreedingMethodForm = newBreedingMethodForm;
        this.window = window;
        this.projectBreedingMethodsPanel = projectBreedingMethodsPanel;
        
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
        
            Integer nextKey = app.getSessionData().getProjectBreedingMethodData().keySet().size() + 1;
            
            nextKey = nextKey*-1;
        
            BreedingMethodModel newBreedingMethod = new BreedingMethodModel();
        
            newBreedingMethod.setMethodName(breedingMethod.getMethodName());
            newBreedingMethod.setMethodDescription(breedingMethod.getMethodDescription());
            newBreedingMethod.setMethodCode(breedingMethod.getMethodCode());
            newBreedingMethod.setMethodGroup(breedingMethod.getMethodGroup());
            newBreedingMethod.setMethodType(breedingMethod.getMethodType());
        
            newBreedingMethod.setMethodId(nextKey);
        
            app.getSessionData().getProjectBreedingMethodData().put(nextKey, newBreedingMethod);
            
            LOG.info(app.getSessionData().getProjectBreedingMethodData().toString());
            
            newBreedingMethodForm.commit();
            
            Method newMethod=new Method();
            newMethod.setMid(newBreedingMethod.getMethodId());
            newMethod.setMname(newBreedingMethod.getMethodName());
            newMethod.setMdesc(newBreedingMethod.getMethodDescription());
            newMethod.setMcode(newBreedingMethod.getMethodCode());
            newMethod.setMgrp(newBreedingMethod.getMethodGroup());
            newMethod.setMtype(newBreedingMethod.getMethodType());
            newMethod.setUser(app.getSessionData().getUserData().getUserid());
            newMethod.setLmid(newBreedingMethod.getMethodId());
            newMethod.setGeneq(newBreedingMethod.getMethodId());
            newMethod.setMattr(0);
            newMethod.setMprgn(0);
            newMethod.setReference(0);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            
            newMethod.setMdate(Integer.parseInt(sdf.format(new Date())));
            newMethod.setMfprg(0);
            
            ManagerFactory managerFactory = projectBreedingMethodsPanel.getManagerFactory();
            
            try {
            	 managerFactory.getGermplasmDataManager().addMethod(newMethod);
            	
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            projectBreedingMethodsPanel.getSelect().addItem(newMethod);
            projectBreedingMethodsPanel.getSelect().setItemCaption(newMethod, newMethod.getMname());
            projectBreedingMethodsPanel.getSelect().select(newMethod);
            projectBreedingMethodsPanel.getSelect().setValue(newMethod);
            
            
         
            User user = app.getSessionData().getUserData();
            Project currentProject = app.getSessionData().getLastOpenedProject();
            ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, "Project Methods", "Added new Breeding Method ("+ newMethod.getMname() + ")", user, new Date());
            try {
				workbenchDataManager.addProjectActivity(projAct);
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            
            newBreedingMethod = null;
            window.getParent().removeWindow(window);
        
        }
        
    }
}
