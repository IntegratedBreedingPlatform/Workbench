/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.window;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.workflow.ConventionalBreedingWorkflowDiagram;
import org.generationcp.ibpworkbench.ui.workflow.MabcWorkflowDiagram;
import org.generationcp.ibpworkbench.ui.workflow.ManagerWorkflowDiagram;
import org.generationcp.ibpworkbench.ui.workflow.MarsWorkflowDiagram;
import org.generationcp.ibpworkbench.ui.workflow.MasWorkflowDiagram;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.themes.Reindeer;

@Configurable
public class WorkflowPreviewWindow extends BaseSubWindow implements InitializingBean {

	private static final long serialVersionUID = 753669483110384734L;

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowPreviewWindow.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private final Integer roleId;
	private Role role;

	public WorkflowPreviewWindow(Integer roleId) {
		this.roleId = roleId;

		// set as modal window, other components are disabled while window is open
		this.setModal(true);

		// define window size, set as not resizable
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setResizable(false);
		this.setDraggable(false);
		// center window within the browser
		this.center();
	}

	protected void initializeComponents() {

		try {
			this.role = this.workbenchDataManager.getRoleById(this.roleId);
		} catch (MiddlewareQueryException e1) {
			WorkflowPreviewWindow.LOG.error("QueryException", e1);
			MessageNotifier.showError(this, this.messageSource.getMessage(Message.DATABASE_ERROR),
					"<br />" + this.messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
			return;
		}
		this.setCaption("Workflow Preview: " + this.role.getName());

		String workflowName = this.role.getWorkflowTemplate().getName();
		if (workflowName.equals("MARS")) {
			MarsWorkflowDiagram marsWorkflowDiagram = new MarsWorkflowDiagram(true, null, null);
			marsWorkflowDiagram.setDebugId("marsWorkflowDiagram");
			this.setHeight(marsWorkflowDiagram.getHeight(), marsWorkflowDiagram.getHeightUnits());
			this.setWidth(marsWorkflowDiagram.getWidth(), marsWorkflowDiagram.getWidthUnits());
			this.setContent(marsWorkflowDiagram);
		} else if (workflowName.equals("MAS")) {
			MasWorkflowDiagram masWorkflowDiagram = new MasWorkflowDiagram(true, null, null);
			masWorkflowDiagram.setDebugId("masWorkflowDiagram");
			this.setHeight(masWorkflowDiagram.getHeight(), masWorkflowDiagram.getHeightUnits());
			this.setWidth(masWorkflowDiagram.getWidth(), masWorkflowDiagram.getWidthUnits());
			this.setContent(masWorkflowDiagram);
		} else if (workflowName.equals("Manager")) {
			ManagerWorkflowDiagram managerWorkflowDiagram = new ManagerWorkflowDiagram(true, null, null);
			managerWorkflowDiagram.setDebugId("managerWorkflowDiagram");
			this.setHeight(managerWorkflowDiagram.getHeight(), managerWorkflowDiagram.getHeightUnits());
			this.setWidth(managerWorkflowDiagram.getWidth(), managerWorkflowDiagram.getWidthUnits());
			this.setContent(managerWorkflowDiagram);
		} else if (workflowName.equals("MABC")) {
			MabcWorkflowDiagram mabcWorkflowDiagram = new MabcWorkflowDiagram(true, null, null);
			mabcWorkflowDiagram.setDebugId("mabcWorkflowDiagram");
			this.setHeight(mabcWorkflowDiagram.getHeight(), mabcWorkflowDiagram.getHeightUnits());
			this.setWidth(mabcWorkflowDiagram.getWidth(), mabcWorkflowDiagram.getWidthUnits());
			this.setContent(mabcWorkflowDiagram);
		} else if (workflowName.equals("CB")) {
			ConventionalBreedingWorkflowDiagram cbWorkflowDiagram = new ConventionalBreedingWorkflowDiagram(true, null, null);
			cbWorkflowDiagram.setDebugId("cbWorkflowDiagram");
			this.setHeight(cbWorkflowDiagram.getHeight(), cbWorkflowDiagram.getHeightUnits());
			this.setWidth(cbWorkflowDiagram.getWidth(), cbWorkflowDiagram.getWidthUnits());
			this.setContent(cbWorkflowDiagram);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeComponents();
	}
}
