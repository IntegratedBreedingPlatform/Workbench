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

package org.generationcp.ibpworkbench.ui.dashboard;

import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.ProjectTableCellStyleGenerator;
import org.generationcp.ibpworkbench.ui.dashboard.listener.LaunchProgramAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class WorkbenchDashboard extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDashboard.class);
	private static final long serialVersionUID = 1L;

	private Table programsTable;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	public static final String PROGRAM_NAME_COLUMN_ID = "Workbench Dashboard Program Name Column Id";
	public static final String CROP_NAME_COLUMN_ID = "Workbench Dashboard Crop Name Column Id";
	public static final String BUTTON_LIST_MANAGER_COLUMN_ID = "Workbench Dashboard List Manager Button Column Id";

	private List<Project> programs = null;

	public WorkbenchDashboard() {
		super();
	}

	@Override
	public void afterPropertiesSet() {
		this.assemble();
	}

	public void initializeComponents() {
		this.initializeProjectTable();
	}

	private void initializeProjectTable() {
		this.programsTable = new Table();
		// react at once when something is selected
		this.programsTable.setImmediate(true);
		this.programsTable.setSelectable(true);
		this.programsTable.setStyleName("gcp-tblproject");

		this.programsTable.addContainerProperty(WorkbenchDashboard.PROGRAM_NAME_COLUMN_ID, String.class, null);
		this.programsTable.addContainerProperty(WorkbenchDashboard.CROP_NAME_COLUMN_ID, String.class, null);
		this.programsTable.addContainerProperty(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID, Button.class, null);

		this.programsTable.setColumnHeader(WorkbenchDashboard.PROGRAM_NAME_COLUMN_ID, "PROGRAM NAME");
		this.programsTable.setColumnHeader(WorkbenchDashboard.CROP_NAME_COLUMN_ID, "CROP");
		this.programsTable.setColumnHeader(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID, "LAUNCH");

		this.programsTable.setColumnExpandRatio(WorkbenchDashboard.PROGRAM_NAME_COLUMN_ID, 1.0F);
		this.programsTable.setColumnWidth(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID, 55);

		this.programsTable.setColumnCollapsingAllowed(false);
		this.programsTable.setCellStyleGenerator(new ProjectTableCellStyleGenerator(this.programsTable, null));

	}

	protected void initializeLayout() {
		this.setSizeUndefined();
		this.setMargin(new MarginInfo(false, true, true, true));
		this.setWidth("100%");

		this.setTitleContent();

		this.addComponent(this.layoutProjectTableArea());
	}

	private void setTitleContent() {
		HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setDebugId("titleLayout");
		titleLayout.setSpacing(true);

		Label toolTitle = new Label(this.messageSource.getMessage(Message.DASHBOARD));
		toolTitle.setDebugId("toolTitle");
		toolTitle.setContentMode(Label.CONTENT_XHTML);
		toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		toolTitle.setDebugId("vaadin-home-lbl");
		toolTitle.setWidth("150px");

		titleLayout.addComponent(toolTitle);
		titleLayout.addComponent(new HelpButton(HelpModule.DASHBOARD, "Go to Dashboard Tutorial"));

		this.addComponent(titleLayout);
	}

	/**
	 * Populates Programs table and selects last opened program by user (if any)
	 */
	protected void initializeData() {
		// Get the list of Projects

		Project lastOpenedProgram = null;

		try {
			User currentUser = this.sessionData.getUserData();
			this.programs = this.workbenchDataManager.getProjectsByUser(currentUser);
			lastOpenedProgram = this.workbenchDataManager.getLastOpenedProject(currentUser.getUserid());
		} catch (MiddlewareQueryException e) {
			WorkbenchDashboard.LOG.error("Exception", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}

		// set the Project Table data source
		BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
		projectContainer.setBeanIdProperty("projectName");

		for (int i = this.programs.size() - 1; i >= 0; i--) {
			Project project = this.programs.get(i);

			Button button = new Button("<span class='glyphicon glyphicon-play'></span>");
			button.setDebugId("button");
			button.setHtmlContentAllowed(true);
			button.setData(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID);
			button.setStyleName(Bootstrap.Buttons.LINK.styleName() + " launch");
			button.setWidth("26px");
			button.setHeight("26px");
			button.addListener(new LaunchProgramAction(project));
			button.setEnabled(true);

			// capitalization done on CSS
			this.programsTable.addItem(new Object[] {project.getProjectName(), project.getCropType().getCropName(), button},
					project);
		}

		if (lastOpenedProgram != null) {
			this.programsTable.select(lastOpenedProgram);
			this.sessionData.setLastOpenedProject(lastOpenedProgram);
			this.sessionData.setSelectedProject(lastOpenedProgram);
		}

	}

	protected void initializeActions() {
		this.programsTable.addListener(new LaunchProgramAction());

	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeData();
		this.initializeActions();
	}

	private Component layoutProjectTableArea() {
		final HorizontalSplitPanel root = new HorizontalSplitPanel();
		root.setDebugId("rootProjectTableArea");
		root.setSplitPosition(300, Sizeable.UNITS_PIXELS, true);
		root.setStyleName(Reindeer.SPLITPANEL_SMALL + " gcp-program-table-area");

		final VerticalLayout programArea = new VerticalLayout();
		programArea.setDebugId("programArea");
		programArea.setSizeFull();
		programArea.setMargin(new MarginInfo(false, true, false, false));

		final HorizontalLayout programHeaderArea = new HorizontalLayout();
		programHeaderArea.setDebugId("programHeaderArea");
		programHeaderArea.setWidth("100%");
		
		final Label programLabel = new Label(this.messageSource.getMessage(Message.PROGRAMS_LABEL));
		programLabel.setDebugId("programLbl");
		programLabel.setStyleName(Bootstrap.Typography.H2.styleName());

		final Label programDescriptionLabel = new Label(this.messageSource.getMessage(Message.PROGRAM_TABLE_TOOLTIP));
		programDescriptionLabel.setDebugId("programDescLbl");
		programDescriptionLabel.setStyleName(Bootstrap.Typography.H6.styleName());

		final Button addProgramButton =
				new Button("<span class='glyphicon glyphicon-plus' style='right: 4px'></span> "
						+ this.messageSource.getMessage(Message.ADD_A_PROGRAM));
		addProgramButton.setHtmlContentAllowed(true);
		addProgramButton.addListener(new OpenNewProjectAction());
		addProgramButton.addStyleName(Bootstrap.Buttons.INFO.styleName());
		addProgramButton.setWidth("145px");

		programHeaderArea.addComponent(programLabel);

		final HorizontalLayout headerContainer = new HorizontalLayout();
		headerContainer.setDebugId("headerContainer");
		headerContainer.setSizeUndefined();
		headerContainer.setSpacing(true);

		final Embedded headerImg = new Embedded(null, new ThemeResource("images/programs.png"));
		headerImg.setDebugId("headerImg");
		headerImg.setStyleName("header-img");

		headerContainer.addComponent(headerImg);
		headerContainer.addComponent(programLabel);
		headerContainer.addComponent(programDescriptionLabel);

		headerContainer.setComponentAlignment(headerImg, Alignment.BOTTOM_LEFT);
		headerContainer.setComponentAlignment(programLabel, Alignment.BOTTOM_LEFT);
		headerContainer.setComponentAlignment(programDescriptionLabel, Alignment.BOTTOM_LEFT);

		programHeaderArea.addComponent(headerContainer);
		programHeaderArea.addComponent(addProgramButton);
		programHeaderArea.setComponentAlignment(addProgramButton, Alignment.MIDDLE_LEFT);
		programHeaderArea.setExpandRatio(headerContainer, 1.0F);

		this.programsTable.setSizeFull();
		this.programsTable.setStyleName("program-tab");

		programArea.addComponent(programHeaderArea);
		programArea.addComponent(this.programsTable);
		programArea.setExpandRatio(this.programsTable, 1.0F);

		final VerticalLayout previewArea = new VerticalLayout();
		previewArea.setDebugId("previewArea");
		previewArea.setStyleName("preview-area");
		previewArea.setSizeFull();
		previewArea.setMargin(new MarginInfo(true, false, false, false));

		root.setFirstComponent(programArea);
		root.setSecondComponent(previewArea);
		root.setHeight("400px");

		return root;
	}

	@Override
	public void attach() {
		super.attach();

		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.programsTable.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;

			@Override
			public String generateDescription(Component source, Object itemId, Object propertyId) {
				return WorkbenchDashboard.this.messageSource.getMessage(Message.PROGRAM_TABLE_TOOLTIP);
			}
		});
	}

	// hacky hack hack
	public void initializeDashboardContents(Project selectProgram) {

		// set this program as selected in dashboard
		if (selectProgram != null) {
			this.programsTable.select(selectProgram);
		}

	}

}
