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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.actions.OpenSelectProjectForStudyAndDatasetViewAction;
import org.generationcp.ibpworkbench.actions.ShowProjectDetailAction;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.ProjectTableCellStyleGenerator;
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainClickListener;
import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.summaryview.SummaryView;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ItemClickEvent;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class WorkbenchDashboard extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDashboard.class);
	private static final long serialVersionUID = 1L;

	private Table tblProject;

	private Project currentProject;

	private Button selectDatasetForBreedingViewButton;

	private TabSheet previewTab;

	private GermplasmListPreview germplasmListPreview;
	private NurseryListPreview nurseryListPreview;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	private Project lastOpenedProject;

	public static final String PROGRAM_NAME_COLUMN_ID = "Workbench Dashboard Program Name Column Id";
	public static final String CROP_NAME_COLUMN_ID = "Workbench Dashboard Crop Name Column Id";
	public static final String BUTTON_LIST_MANAGER_COLUMN_ID = "Workbench Dashboard List Manager Button Column Id";

	private List<Project> projects = null;
	private SummaryView summaryView;
	private Button lasSelectedProjectButton = null;

	public WorkbenchDashboard() {
		super();
	}

	@Override
	public void afterPropertiesSet() {
		this.assemble();
	}

	public void initializeComponents() {
		this.selectDatasetForBreedingViewButton = new Button("View Studies and Datasets");
		this.selectDatasetForBreedingViewButton.setWidth("200px");

		this.initializeProjectTable();
		this.initializePreviewTable();
	}

	private void initializeProjectTable() {
		// project table components
		this.tblProject = new Table() {

			private static final long serialVersionUID = 1L;

			@Override
			protected String formatPropertyValue(Object rowId, Object colId, Property property) {
				if (property.getType() == Date.class) {
					SimpleDateFormat sdf = DateUtil.getSimpleDateFormat(DateUtil.FRONTEND_DATE_FORMAT);
					return property.getValue() == null ? "" : sdf.format((Date) property.getValue());
				}

				return super.formatPropertyValue(rowId, colId, property);
			}
		};
		// react at once when something is selected
		this.tblProject.setImmediate(true);
		this.tblProject.setSelectable(true);
		this.tblProject.setStyleName("gcp-tblproject");

		this.tblProject.addContainerProperty(WorkbenchDashboard.PROGRAM_NAME_COLUMN_ID, String.class, null);
		this.tblProject.addContainerProperty(WorkbenchDashboard.CROP_NAME_COLUMN_ID, String.class, null);
		this.tblProject.addContainerProperty(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID, Button.class, null);

		this.tblProject.setColumnHeader(WorkbenchDashboard.PROGRAM_NAME_COLUMN_ID, "PROGRAM NAME");
		this.tblProject.setColumnHeader(WorkbenchDashboard.CROP_NAME_COLUMN_ID, "CROP");
		this.tblProject.setColumnHeader(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID, "LAUNCH");

		this.tblProject.setColumnExpandRatio(WorkbenchDashboard.PROGRAM_NAME_COLUMN_ID, 1.0F);
		this.tblProject.setColumnWidth(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID, 55);

		this.tblProject.setColumnCollapsingAllowed(false);
		this.tblProject.setCellStyleGenerator(new ProjectTableCellStyleGenerator(this.tblProject, null));

	}

	private void initializePreviewTable() {
		this.germplasmListPreview = new GermplasmListPreview(null);
		this.nurseryListPreview = new NurseryListPreview(null);

		this.previewTab = new TabSheet();
		this.previewTab.setHeight(100, Sizeable.UNITS_PERCENTAGE);
		this.previewTab.addTab(this.germplasmListPreview, "Lists");

		this.previewTab.addTab(this.nurseryListPreview, "Nurseries & Trials");

		this.previewTab.setImmediate(true);
	}

	protected void initializeLayout() {
		this.setSizeUndefined();
		this.setMargin(new MarginInfo(false, true, true, true));
		this.setWidth("100%");

		this.setTitleContent();

		this.addComponent(this.layoutProjectTableArea());
		this.addComponent(this.layoutProjectDetailArea());
	}

	private void setTitleContent() {
		HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setSpacing(true);

		Label toolTitle = new Label(this.messageSource.getMessage(Message.DASHBOARD));
		toolTitle.setContentMode(Label.CONTENT_XHTML);
		toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		toolTitle.setDebugId("vaadin-home-lbl");
		toolTitle.setWidth("80px");

		titleLayout.addComponent(toolTitle);
		titleLayout.addComponent(new HelpButton(HelpModule.DASHBOARD, "Go to Dashboard Tutorial"));

		this.addComponent(titleLayout);
	}

	protected void initializeData() {
		// TODO: Verify the try-catch flow
		// Get the list of Projects

		this.lastOpenedProject = null;

		try {
			User currentUser = this.sessionData.getUserData();
			this.projects = this.workbenchDataManager.getProjectsByUser(currentUser);
			this.lastOpenedProject = this.workbenchDataManager.getLastOpenedProject(currentUser.getUserid());
		} catch (MiddlewareQueryException e) {
			WorkbenchDashboard.LOG.error("Exception", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}

		this.sessionData.setLastOpenedProject(this.lastOpenedProject);

		if (this.currentProject == null) {
			this.currentProject = this.lastOpenedProject;
		}

		this.sessionData.setSelectedProject(this.currentProject);

		// set the Project Table data source
		BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
		projectContainer.setBeanIdProperty("projectName");

		int i = 0;
		Project project;
		for (i = this.projects.size() - 1; i >= 0; i--) {
			project = this.projects.get(i);

			Button button = new Button("<span class='glyphicon glyphicon-play'></span>");
			button.setHtmlContentAllowed(true);
			button.setData(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID);
			button.setStyleName(Bootstrap.Buttons.LINK.styleName() + " launch");
			button.setWidth("26px");
			button.setHeight("26px");
			button.addListener(new DashboardMainClickListener(this, project.getProjectId()));
			button.setEnabled(false);

			Long lastOpenedProjectId = this.lastOpenedProject == null ? null : this.lastOpenedProject.getProjectId();
			boolean sameProject =
					lastOpenedProjectId == null ? project.getProjectId() == null : lastOpenedProjectId.equals(project.getProjectId());

			if (sameProject) {
				WorkbenchDashboard.this.lasSelectedProjectButton = button;

				button.setEnabled(true);
			}

			// capitalization done on CSS
			this.tblProject.addItem(new Object[] {project.getProjectName(), project.getCropType().getCropName(), button},
					project.getProjectId());
		}

		if (this.lastOpenedProject != null) {
			this.tblProject.select(this.lastOpenedProject.getProjectId());
		}

	}

	protected void initializeActions() {

		OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction =
				new OpenSelectProjectForStudyAndDatasetViewAction(null);
		this.selectDatasetForBreedingViewButton.addListener(openSelectDatasetForBreedingViewAction);

		this.tblProject.addListener(new ItemClickEvent.ItemClickListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = -7560323455772265841L;

			@Override
			public void itemClick(ItemClickEvent event) {
				Object selectedButton =
						WorkbenchDashboard.this.tblProject.getItem(event.getItemId())
								.getItemProperty(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID).getValue();

				// disable previously selected button

				if (WorkbenchDashboard.this.lasSelectedProjectButton != null) {
					WorkbenchDashboard.this.lasSelectedProjectButton.setEnabled(false);
				}

				if (selectedButton instanceof Button && selectedButton != null) {
					((Button) selectedButton).setEnabled(true);
					WorkbenchDashboard.this.lasSelectedProjectButton = (Button) selectedButton;

					if (event.isDoubleClick()) {

						// hack manual trigger button
						Map vars = new HashMap();
						vars.put("state", true);
						((Button) selectedButton).changeVariables(this, vars);

					}
				}
			}
		});

		this.tblProject.addListener(new ShowProjectDetailAction(this.tblProject, this.summaryView, this.selectDatasetForBreedingViewButton,
				openSelectDatasetForBreedingViewAction, this.currentProject, this.germplasmListPreview, this.nurseryListPreview,
				this.previewTab, this.projects));

	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeData();
		this.initializeActions();
	}

	private Component layoutProjectTableArea() {
		final HorizontalSplitPanel root = new HorizontalSplitPanel();
		root.setSplitPosition(300, Sizeable.UNITS_PIXELS, true);
		root.setStyleName(Reindeer.SPLITPANEL_SMALL + " gcp-program-table-area");

		final VerticalLayout programArea = new VerticalLayout();
		programArea.setSizeFull();
		programArea.setMargin(new MarginInfo(false, true, false, false));

		final HorizontalLayout programHeaderArea = new HorizontalLayout();
		programHeaderArea.setWidth("100%");
		final Label programLbl = new Label(this.messageSource.getMessage(Message.PROGRAMS_LABEL));
		programLbl.setStyleName(Bootstrap.Typography.H2.styleName());

		final Label programDescLbl = new Label(this.messageSource.getMessage(Message.PROGRAM_TABLE_TOOLTIP));
		programDescLbl.setStyleName(Bootstrap.Typography.H6.styleName());

		final Button addProgramBtn =
				new Button("<span class='glyphicon glyphicon-plus' style='right: 4px'></span> "
						+ this.messageSource.getMessage(Message.ADD_A_PROGRAM));
		addProgramBtn.setHtmlContentAllowed(true);
		addProgramBtn.addListener(new OpenNewProjectAction());
		addProgramBtn.addStyleName(Bootstrap.Buttons.INFO.styleName());
		addProgramBtn.setWidth("145px");

		programHeaderArea.addComponent(programLbl);

		final HorizontalLayout headerContainer = new HorizontalLayout();
		headerContainer.setSizeUndefined();
		headerContainer.setSpacing(true);

		final Embedded headerImg = new Embedded(null, new ThemeResource("images/programs.png"));
		headerImg.setStyleName("header-img");

		headerContainer.addComponent(headerImg);
		headerContainer.addComponent(programLbl);
		headerContainer.addComponent(programDescLbl);

		headerContainer.setComponentAlignment(headerImg, Alignment.BOTTOM_LEFT);
		headerContainer.setComponentAlignment(programLbl, Alignment.BOTTOM_LEFT);
		headerContainer.setComponentAlignment(programDescLbl, Alignment.BOTTOM_LEFT);

		programHeaderArea.addComponent(headerContainer);
		programHeaderArea.addComponent(addProgramBtn);
		programHeaderArea.setComponentAlignment(addProgramBtn, Alignment.MIDDLE_LEFT);
		programHeaderArea.setExpandRatio(headerContainer, 1.0F);

		this.tblProject.setSizeFull();
		this.tblProject.setStyleName("program-tab");

		programArea.addComponent(programHeaderArea);
		programArea.addComponent(this.tblProject);
		programArea.setExpandRatio(this.tblProject, 1.0F);

		final VerticalLayout previewArea = new VerticalLayout();
		previewArea.setStyleName("preview-area");
		previewArea.setSizeFull();
		previewArea.setMargin(new MarginInfo(true, false, false, false));

		previewArea.addComponent(this.previewTab);
		this.previewTab.addStyleName("preview-tab");

		root.setFirstComponent(programArea);
		root.setSecondComponent(previewArea);
		root.setHeight("400px");

		return root;
	}

	private Component layoutProjectDetailArea() {
		this.summaryView = new SummaryView();

		return this.summaryView;
	}

	@Override
	public void attach() {
		super.attach();

		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setColumnHeader(this.tblProject, "startDate", Message.START_DATE);
		this.messageSource.setColumnHeader(this.tblProject, "projectName", Message.PROJECT);
		this.messageSource.setColumnHeader(this.tblProject, "action", Message.ACTION);
		this.messageSource.setColumnHeader(this.tblProject, "status", Message.STATUS);
		this.messageSource.setColumnHeader(this.tblProject, "owner", Message.OWNER);

		this.tblProject.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;

			@Override
			public String generateDescription(Component source, Object itemId, Object propertyId) {
				return WorkbenchDashboard.this.messageSource.getMessage(Message.PROGRAM_TABLE_TOOLTIP);
			}
		});
	}

	public Project getCurrentProject() {
		return this.currentProject;
	}

	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}

	// hacky hack hack
	public ShowProjectDetailAction initializeDashboardContents(Project selectProgram) {

		// set this program as selected in dashboard
		if (selectProgram != null) {
			this.tblProject.select(selectProgram.getProjectId());
		}

		// update other pards
		return new ShowProjectDetailAction(this.tblProject, this.summaryView, this.selectDatasetForBreedingViewButton,
				new OpenSelectProjectForStudyAndDatasetViewAction(null), this.lastOpenedProject, this.germplasmListPreview,
				this.nurseryListPreview, this.previewTab, this.projects);
	}

}
