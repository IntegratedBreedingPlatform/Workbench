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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ApplicationMetaData;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.database.IBDBGenerator;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;

@Configurable
public class SaveNewProjectAction implements ClickListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(SaveNewProjectAction.class);
    private static final long serialVersionUID = 1L;
   
    private Form newProjectForm;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public SaveNewProjectAction(Form newProjectForm) {
        this.newProjectForm = newProjectForm;
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        newProjectForm.commit();
        
        boolean isGenerationSuccess = false;

        @SuppressWarnings("unchecked")
        BeanItem<Project> projectBean = (BeanItem<Project>) newProjectForm.getItemDataSource();
        Project project = projectBean.getBean();
        
        project.setUserId(ApplicationMetaData.getUserData().getUserid());
        
        //workbenchDataManager.get
        
        //TODO: Verify the try-catch flow
        try {
            workbenchDataManager.saveOrUpdateProject(project);
        } catch (QueryException e) {
            LOG.error("Error encountered while trying to save the project.", e);
            MessageNotifier.showError(event.getComponent().getWindow(), 
                    messageSource.getMessage(Message.DATABASE_ERROR), 
                    messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));
            return;
        }
        
        try {
        	
            IBDBGenerator generator = new IBDBGenerator(project.getCropType().toString(), project.getProjectId());
            isGenerationSuccess = generator.generateDatabase();
        } catch (InternationalizableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //System.out.printf("%d %s %s %s", project.getProjectId(), project.getProjectName(), project.getTargetDueDate(), project.getTemplate().getTemplateId());
        LOG.info(project.getProjectId() + "  " + project.getProjectName() + " " + project.getTargetDueDate() + " " + project.getTemplate().getTemplateId());
        LOG.info("IBDB Local Generation Successful?: " + isGenerationSuccess);
        
        // go back to dashboard
        HomeAction home = new HomeAction();
        home.buttonClick(event);
    }
}
