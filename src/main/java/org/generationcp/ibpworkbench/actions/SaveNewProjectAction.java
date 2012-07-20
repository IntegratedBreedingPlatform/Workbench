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

import org.generationcp.ibpworkbench.ApplicationMetaData;
import org.generationcp.middleware.manager.WorkbenchManagerFactory;
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
    private WorkbenchManagerFactory workbenchManagerFactory;

    public SaveNewProjectAction(Form newProjectForm) {
        this.newProjectForm = newProjectForm;
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        newProjectForm.commit();

        @SuppressWarnings("unchecked")
        BeanItem<Project> projectBean = (BeanItem<Project>) newProjectForm.getItemDataSource();
        Project project = projectBean.getBean();
        
        project.setUserId(ApplicationMetaData.getUserData().getUserid());
        
        workbenchManagerFactory.getWorkBenchDataManager().saveOrUpdateProject(project);
        //System.out.printf("%d %s %s %s", project.getProjectId(), project.getProjectName(), project.getTargetDueDate(), project.getTemplate().getTemplateId());
        LOG.info(project.getProjectId() + "  " + project.getProjectName() + " " + project.getTargetDueDate() + " " + project.getTemplate().getTemplateId());
        
        // go back to dashboard
        HomeAction home = new HomeAction();
        home.buttonClick(event);
    }
}
