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
package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.workflow.*;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class WorkflowPreviewWindow extends BaseSubWindow implements InitializingBean {
    
    private static final long serialVersionUID = 753669483110384734L;
    
    private static final Logger LOG = LoggerFactory.getLogger(WorkflowPreviewWindow.class);
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Integer roleId;
    private Role role;
    
    //private VerticalLayout layout;
    
    public WorkflowPreviewWindow(Integer roleId) {
        this.roleId = roleId;
        
        // set as modal window, other components are disabled while window is open
        setModal(true);

        // define window size, set as not resizable
        this.addStyleName(Reindeer.WINDOW_LIGHT);
        setResizable(false);
        setDraggable(false);
        // center window within the browser
        center();
    }
    
    protected void initializeComponents() {
        
        try {
            this.role = workbenchDataManager.getRoleById(roleId);
        } catch (MiddlewareQueryException e1) {
            LOG.error("QueryException", e1);
            MessageNotifier.showError(this, messageSource.getMessage(Message.DATABASE_ERROR),
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return;
        }
        setCaption("Workflow Preview: " + role.getName());
        
        String workflowName = role.getWorkflowTemplate().getName();
        if (workflowName.equals("MARS")) {
            MarsWorkflowDiagram marsWorkflowDiagram = new MarsWorkflowDiagram(true, null,null);
            setHeight(marsWorkflowDiagram.getHeight(), marsWorkflowDiagram.getHeightUnits());
            setWidth(marsWorkflowDiagram.getWidth(), marsWorkflowDiagram.getWidthUnits());
            setContent(marsWorkflowDiagram);
        }
        else if (workflowName.equals("MAS")) {
            MasWorkflowDiagram masWorkflowDiagram = new MasWorkflowDiagram(true, null,null);
            setHeight(masWorkflowDiagram.getHeight(), masWorkflowDiagram.getHeightUnits());
            setWidth(masWorkflowDiagram.getWidth(), masWorkflowDiagram.getWidthUnits());
            setContent(masWorkflowDiagram);
        }
        else if (workflowName.equals("Manager")) {
            ManagerWorkflowDiagram managerWorkflowDiagram = new ManagerWorkflowDiagram(true, null, null);
            setHeight(managerWorkflowDiagram.getHeight(), managerWorkflowDiagram.getHeightUnits());
            setWidth(managerWorkflowDiagram.getWidth(), managerWorkflowDiagram.getWidthUnits());
            setContent(managerWorkflowDiagram);
        }
        else if (workflowName.equals("MABC")) {
            MabcWorkflowDiagram mabcWorkflowDiagram = new MabcWorkflowDiagram(true, null,null);
            setHeight(mabcWorkflowDiagram.getHeight(), mabcWorkflowDiagram.getHeightUnits());
            setWidth(mabcWorkflowDiagram.getWidth(), mabcWorkflowDiagram.getWidthUnits());
            setContent(mabcWorkflowDiagram);
        }
        else if (workflowName.equals("CB")) {
            ConventionalBreedingWorkflowDiagram cbWorkflowDiagram = new ConventionalBreedingWorkflowDiagram(true, null,null);
            setHeight(cbWorkflowDiagram.getHeight(), cbWorkflowDiagram.getHeightUnits());
            setWidth(cbWorkflowDiagram.getWidth(), cbWorkflowDiagram.getWidthUnits());
            setContent(cbWorkflowDiagram);
        }
    }

    protected void initializeLayout() {
        
    }
    
    protected void initializeActions() {
        
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub
        assemble();
    }
}

