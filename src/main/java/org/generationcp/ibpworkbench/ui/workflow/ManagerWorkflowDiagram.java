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

package org.generationcp.ibpworkbench.ui.workflow;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction.WindowEnums;
import org.generationcp.ibpworkbench.actions.DeleteProjectAction;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.OpenProgramLocationsAction;
import org.generationcp.ibpworkbench.actions.OpenProgramMethodsAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction.WindowEnum;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.ibpworkbench.ui.project.create.OpenUpdateProjectPageAction;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class ManagerWorkflowDiagram extends Panel implements WorkflowConstants, InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	// this is in pixels and used for layouting
	private static final int WORKFLOW_STEP_HEIGHT = 255;
	private static final int WORKFLOW_STEP_WIDTH = 270;
	private static final int EXTRA_SPACE_BETWEEN_COMPONENTS = 10;

	private final boolean workflowPreview;

	private Project project;
	private Role role;

	private Label dashboardTitle;

	private Label administrationTitle;
	private Label genoTypingTitle;
	private Label projectPlanningTitle;
	private Label breedingManagementTitle;
	private Label analysisPipelineTitle;
	private Label decisionSupportTitle;

	// links for tools
	private Button browseGermplasmButton;
	private Button membersButton;
	private Button browseStudiesButton;
	private Button browseGermplasmListsButton;
	private Button gdmsButton;
	private Button mbdtButton;
	private Button breedingViewButton;
	private Button breedingViewSingleSiteAnalysisCentralButton;
	private Button breedingViewSingleSiteAnalysisLocalButton;
	private Button breedingViewGxeAnalysisLocalButton;
	private Button fieldbookButton;
	private Button optimasButton;
	private Button nurseryManagerButton;
	private Button projectLocationButton;
	private Button crossStudyBrowserButton;
	private Button projectMethodsButton;
	private Button createTemplatesButton;
	private Button deleteProjectButton;
	private Button crossStudyBrowserButton2;

	private Button mainHeadToHeadButton;
	private Button mainHeadToHeadButton2;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Button backupIBDBButton;
	private Button restoreIBDBButton;

	private Button breedingViewMultiSiteAnalysisButton;

	private Button manageGermplasmListsButton;

	private Button breedingManagerButton;

	private Button makeCrossesButton;

	private Button breedingPlannerButton;

	private Button germplasmImportButton;

	private Button userToolsButton;

	private Button germplasmImportButton2;

	private Button datasetImporterBtn;
	private Button updateProjectButton;

	private Button queryForAdaptedGermplasmButton;
	private Button queryForAdaptedGermplasmButton2;

	private Button breedingManagerListManager;
	private Button ontologyBrowserFBBtn;
	private Button metaAnalysisBtn;

	public ManagerWorkflowDiagram(boolean workflowPreview, Project project, Role role) {
		this.workflowPreview = workflowPreview;

		if (!workflowPreview) {
			this.project = project;
			this.role = role;
		}
	}

	@Override
	public void afterPropertiesSet() {
		this.assemble();
	}

	protected void initializeComponents() {
		// dashboard title
		this.dashboardTitle = new Label();
		this.dashboardTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		this.administrationTitle = new Label("Administration & Configuration");
		this.administrationTitle.setStyleName("gcp-section-title-large");

		this.genoTypingTitle = new Label("Genotyping");
		this.genoTypingTitle.setStyleName("gcp-section-title-large");

		this.projectPlanningTitle = new Label("Program Planning");
		this.projectPlanningTitle.setStyleName("gcp-section-title-large");

		this.breedingManagementTitle = new Label("Breeding Management");
		this.breedingManagementTitle.setStyleName("gcp-section-title-large");

		this.analysisPipelineTitle = new Label("Analysis Pipeline");
		this.analysisPipelineTitle.setStyleName("gcp-section-title-large");

		this.decisionSupportTitle = new Label("Decision Support");
		this.decisionSupportTitle.setStyleName("gcp-section-title-large");

		this.membersButton = new Button("Members");
		this.membersButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.membersButton.setSizeUndefined();
		this.membersButton.setDescription(this.messageSource.getMessage(Message.MEMBERS_LINK_DESC));

		this.updateProjectButton = new Button("Update Program Details");
		this.updateProjectButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.updateProjectButton.setSizeUndefined();
		this.updateProjectButton.setDescription("Click to update program details and workflows.");

		this.breedingPlannerButton = new Button(this.messageSource.getMessage(Message.BREEDING_PLANNER_LINK));
		this.breedingPlannerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.breedingPlannerButton.setSizeUndefined();
		this.breedingPlannerButton.setDescription("Click to launch the freestanding Breeding Planner application.");

		this.crossStudyBrowserButton = new Button(this.messageSource.getMessage(Message.HEAD_TO_HEAD_LAUNCH));
		this.crossStudyBrowserButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.crossStudyBrowserButton.setSizeUndefined();
		this.crossStudyBrowserButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_CROSS_STUDY_BROWSER));

		this.crossStudyBrowserButton2 = new Button(this.messageSource.getMessage(Message.HEAD_TO_HEAD_LAUNCH));
		this.crossStudyBrowserButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.crossStudyBrowserButton2.setSizeUndefined();
		this.crossStudyBrowserButton2.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_CROSS_STUDY_BROWSER));

		this.browseGermplasmButton = new Button("Germplasm Browser");
		this.browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.browseGermplasmButton.setSizeUndefined();
		this.browseGermplasmButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_GERMPLASM_BROWSER));

		this.browseStudiesButton = new Button("Study Browser");
		this.browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.browseStudiesButton.setSizeUndefined();
		this.browseStudiesButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_STUDY_BROWSER));

		this.browseGermplasmListsButton = new Button("Germplasm List Browser");
		this.browseGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.browseGermplasmListsButton.setSizeUndefined();
		this.browseGermplasmListsButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_GERMPLASM_LIST_BROWSER));

		this.manageGermplasmListsButton = new Button("List Manager");
		this.manageGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.manageGermplasmListsButton.setSizeUndefined();
		this.manageGermplasmListsButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_LIST_MANAGER));

		this.breedingViewButton = new Button("Breeding View Standalone (all analysis)");
		this.breedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link" + " gcp-linkwrap");
		this.breedingViewButton.setSizeUndefined();
		this.breedingViewButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW));

		this.breedingViewSingleSiteAnalysisCentralButton = new Button("Single-Site Analysis for Central Datasets");
		this.breedingViewSingleSiteAnalysisCentralButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.breedingViewSingleSiteAnalysisCentralButton.setSizeUndefined();
		this.breedingViewSingleSiteAnalysisCentralButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));

		this.breedingViewSingleSiteAnalysisLocalButton = new Button("Single-Site Analysis for Local Datasets");
		this.breedingViewSingleSiteAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.breedingViewSingleSiteAnalysisLocalButton.setSizeUndefined();
		this.breedingViewSingleSiteAnalysisLocalButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));

		this.breedingViewGxeAnalysisLocalButton = new Button("GxE Analysis");
		this.breedingViewGxeAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.breedingViewGxeAnalysisLocalButton.setSizeUndefined();
		this.breedingViewGxeAnalysisLocalButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));

		this.breedingViewMultiSiteAnalysisButton = new Button();
		this.breedingViewMultiSiteAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.breedingViewMultiSiteAnalysisButton.setSizeUndefined();
		this.breedingViewMultiSiteAnalysisButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_MULTI_SITE_ANALYSIS));

		this.gdmsButton = new Button("GDMS");
		this.gdmsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.gdmsButton.setSizeUndefined();
		this.gdmsButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_GDMS));

		this.mbdtButton = new Button("MBDT");
		this.mbdtButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.mbdtButton.setSizeUndefined();
		this.mbdtButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_MBDT));

		this.fieldbookButton = new Button("Trial Manager");
		this.fieldbookButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.fieldbookButton.setSizeUndefined();
		this.fieldbookButton.setDescription("Click to launch Fieldbook on Trial Manager View");

		this.optimasButton = new Button("OptiMAS");
		this.optimasButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.optimasButton.setSizeUndefined();
		this.optimasButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_OPTIMAS));

		this.nurseryManagerButton = new Button("Nursery Manager");
		this.nurseryManagerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.nurseryManagerButton.setSizeUndefined();
		this.nurseryManagerButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_NURSERY_MANAGER));

		this.germplasmImportButton = new Button("IBFB Germplasm Import");
		this.germplasmImportButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.germplasmImportButton.setSizeUndefined();
		this.germplasmImportButton.setDescription("Click to launch Fieldbook on Germplasm Import View.");

		this.germplasmImportButton2 = new Button("Germplasm Import");
		this.germplasmImportButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.germplasmImportButton2.setSizeUndefined();
		this.germplasmImportButton2.setDescription("Click to launch the Germplasm Import View.");

		this.projectMethodsButton = new Button("Program Methods");
		this.projectMethodsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.projectMethodsButton.setSizeUndefined();
		this.projectMethodsButton.setDescription(this.messageSource.getMessage(Message.PROJECT_METHODS_DESC));

		this.projectLocationButton = new Button("Program Locations");
		this.projectLocationButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.projectLocationButton.setSizeUndefined();
		this.projectLocationButton.setDescription(this.messageSource.getMessage(Message.PROJECT_LOCATIONS_DESC));

		this.backupIBDBButton = new Button(this.messageSource.getMessage(Message.BACKUP_IBDB_LINK));
		this.backupIBDBButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.backupIBDBButton.setSizeUndefined();
		this.backupIBDBButton.setDescription(this.messageSource.getMessage(Message.BACKUP_IBDB_LINK_DESC));

		this.restoreIBDBButton = new Button(this.messageSource.getMessage(Message.RESTORE_IBDB_LINK));
		this.restoreIBDBButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.restoreIBDBButton.setSizeUndefined();
		this.restoreIBDBButton.setDescription(this.messageSource.getMessage(Message.RESTORE_IBDB_LINK_DESC));

		this.breedingManagerButton = new Button(this.messageSource.getMessage(Message.MANAGE_NURSERIES));
		this.breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.breedingManagerButton.setSizeUndefined();
		this.breedingManagerButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_MANAGER));

		this.makeCrossesButton = new Button(this.messageSource.getMessage(Message.CROSS_MANAGER_LINK));
		this.makeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.makeCrossesButton.setSizeUndefined();
		this.makeCrossesButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_CROSSING_MANAGER));

		this.createTemplatesButton = new Button(this.messageSource.getMessage(Message.NURSERY_TEMPLATE));
		this.createTemplatesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.createTemplatesButton.setSizeUndefined();
		this.createTemplatesButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_NURSERY_TEMPLATE));

		this.userToolsButton = new Button(this.messageSource.getMessage(Message.TOOL_USERS));
		this.userToolsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.userToolsButton.setSizeUndefined();
		this.userToolsButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_USER_TOOLS));

		this.deleteProjectButton = new Button(this.messageSource.getMessage(Message.DELETE_PROJECT_LINK));
		this.deleteProjectButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.deleteProjectButton.setSizeUndefined();

		this.mainHeadToHeadButton = new Button(this.messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
		this.mainHeadToHeadButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.mainHeadToHeadButton.setSizeUndefined();
		this.mainHeadToHeadButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

		this.mainHeadToHeadButton2 = new Button(this.messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
		this.mainHeadToHeadButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.mainHeadToHeadButton2.setSizeUndefined();
		this.mainHeadToHeadButton2.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

		this.queryForAdaptedGermplasmButton = new Button(this.messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
		this.queryForAdaptedGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.queryForAdaptedGermplasmButton.setSizeUndefined();
		this.queryForAdaptedGermplasmButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));

		this.queryForAdaptedGermplasmButton2 = new Button(this.messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
		this.queryForAdaptedGermplasmButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.queryForAdaptedGermplasmButton2.setSizeUndefined();
		this.queryForAdaptedGermplasmButton2.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));

		this.breedingManagerListManager = new Button(this.messageSource.getMessage(Message.BREEDING_MANAGER_LIST_MANAGER));
		this.breedingManagerListManager.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.breedingManagerListManager.setSizeUndefined();
		this.breedingManagerListManager
				.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_MANAGER_LIST_MANAGER));

		this.datasetImporterBtn = new Button("Data Import Tool");
		this.datasetImporterBtn.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.datasetImporterBtn.setSizeUndefined();
		this.datasetImporterBtn.setDescription("Click to launch the Data Import Tool.");

		this.ontologyBrowserFBBtn = new Button("Ontology Browser");
		this.ontologyBrowserFBBtn.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.ontologyBrowserFBBtn.setSizeUndefined();
		this.ontologyBrowserFBBtn.setDescription("Click to launch Fieldbook on Ontology Browser view");

		this.metaAnalysisBtn = new Button("Meta Analysis of Field Trials for Local Datasets");
		this.metaAnalysisBtn.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
		this.metaAnalysisBtn.setSizeUndefined();
		this.metaAnalysisBtn.setDescription("Click to launch Meta Analysis of Field Trial Tool");

	}

	protected void initializeLayout() {
		this.setSizeFull();
		this.setScrollable(true);
		this.setContent(this.layoutWorkflowArea());
	}

	protected ComponentContainer layoutWorkflowArea() {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setMargin(false);
		layout.setWidth("850px");
		layout.setHeight("600px");

		String extraSpace = ManagerWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + "px";
		int top = 10;
		String topInPixels = "";

		// the steps on the first column
		Component populationManagementArea = this.layoutAdministration();
		layout.addComponent(populationManagementArea, "top:" + extraSpace + ";");

		top = top + ManagerWorkflowDiagram.WORKFLOW_STEP_HEIGHT + ManagerWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component fieldTrialArea = this.layoutProjectPlanning();
		layout.addComponent(fieldTrialArea, "top:" + topInPixels + ";");

		// the steps on the second column
		top = ManagerWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		int left = ManagerWorkflowDiagram.WORKFLOW_STEP_WIDTH + ManagerWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		String leftInPixels = left + "px";

		Component projectPlanningArea = this.layoutBreedingManagement();
		layout.addComponent(projectPlanningArea, "top:" + topInPixels + "; left:" + leftInPixels);

		top = top + ManagerWorkflowDiagram.WORKFLOW_STEP_HEIGHT + ManagerWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component genotypingArea = this.layoutDataManagement();
		layout.addComponent(genotypingArea, "top:" + topInPixels + "; left:" + leftInPixels);

		// the steps on the third column
		top = ManagerWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		left =
				ManagerWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + ManagerWorkflowDiagram.WORKFLOW_STEP_WIDTH
						+ ManagerWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + ManagerWorkflowDiagram.WORKFLOW_STEP_WIDTH;
		leftInPixels = left + "px";

		Component progenySelectionArea = this.layoutAnalysisPipeline();
		layout.addComponent(progenySelectionArea, "top:" + topInPixels + "; left:" + leftInPixels);

		top = top + ManagerWorkflowDiagram.WORKFLOW_STEP_HEIGHT + ManagerWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component projectCompletionArea = this.layoutDecisionSupport();
		layout.addComponent(projectCompletionArea, "top:" + topInPixels + "; left:" + leftInPixels);

		final VerticalLayout rootContainer = new VerticalLayout();
		rootContainer.setMargin(new Layout.MarginInfo(false, true, true, true));
		rootContainer.setSpacing(false);
		rootContainer.setSizeUndefined();
		if (!this.workflowPreview) {
			Label header = new Label();
			header.setStyleName(Bootstrap.Typography.H1.styleName());
			header.setValue(this.role.getLabel());
			rootContainer.addComponent(header);

		}
		rootContainer.addComponent(layout);

		return rootContainer;
	}

	protected Component layoutProjectPlanning() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.projectPlanningTitle);
		layout.setComponentAlignment(this.projectPlanningTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.projectPlanningTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.ontologyBrowserFBBtn);
		this.ontologyBrowserFBBtn.setHeight("20px");
		layout.setComponentAlignment(this.ontologyBrowserFBBtn, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.ontologyBrowserFBBtn, 0);

		layout.addComponent(this.breedingManagerListManager);
		this.breedingManagerListManager.setHeight("20px");
		layout.setComponentAlignment(this.breedingManagerListManager, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingManagerListManager, 0);

		layout.addComponent(this.breedingPlannerButton);
		this.breedingPlannerButton.setHeight("20px");
		layout.setComponentAlignment(this.breedingPlannerButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingPlannerButton, 0);

		layout.addComponent(this.browseStudiesButton);
		this.browseStudiesButton.setHeight("20px");
		layout.setComponentAlignment(this.browseStudiesButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.browseStudiesButton, 0);

		layout.addComponent(this.mainHeadToHeadButton);
		this.mainHeadToHeadButton.setHeight("20px");
		layout.setComponentAlignment(this.mainHeadToHeadButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.mainHeadToHeadButton, 0);

		layout.addComponent(this.queryForAdaptedGermplasmButton);
		this.queryForAdaptedGermplasmButton.setHeight("20px");
		layout.setComponentAlignment(this.queryForAdaptedGermplasmButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.queryForAdaptedGermplasmButton, 0);

		return layout;
	}

	protected Component layoutAdministration() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.administrationTitle);
		layout.setComponentAlignment(this.administrationTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.administrationTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("5px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.membersButton);
		this.membersButton.setHeight("20px");
		layout.setComponentAlignment(this.membersButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.membersButton, 0);

		layout.addComponent(this.updateProjectButton);
		this.updateProjectButton.setHeight("20px");
		layout.setComponentAlignment(this.updateProjectButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.updateProjectButton, 0);

		layout.addComponent(this.backupIBDBButton);
		this.backupIBDBButton.setHeight("20px");
		layout.setComponentAlignment(this.backupIBDBButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.backupIBDBButton, 0);

		layout.addComponent(this.restoreIBDBButton);
		this.restoreIBDBButton.setHeight("20px");
		layout.setComponentAlignment(this.restoreIBDBButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.restoreIBDBButton, 0);

		layout.addComponent(this.projectMethodsButton);
		this.projectMethodsButton.setHeight("20px");
		layout.setComponentAlignment(this.projectMethodsButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.projectMethodsButton, 0);

		layout.addComponent(this.projectLocationButton);
		this.projectLocationButton.setHeight("20px");
		layout.setComponentAlignment(this.projectLocationButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.projectLocationButton, 0);

		layout.addComponent(this.userToolsButton);
		this.userToolsButton.setHeight("20px");
		layout.setComponentAlignment(this.userToolsButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.userToolsButton, 0);

		layout.addComponent(this.deleteProjectButton);
		this.deleteProjectButton.setHeight("20px");
		layout.setComponentAlignment(this.deleteProjectButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.deleteProjectButton, 0);

		layout.addComponent(this.datasetImporterBtn);
		this.datasetImporterBtn.setHeight("20px");
		layout.setComponentAlignment(this.datasetImporterBtn, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.datasetImporterBtn, 0);

		return layout;
	}

	protected Component layoutDataManagement() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.genoTypingTitle);
		layout.setComponentAlignment(this.genoTypingTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.genoTypingTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.fieldbookButton);
		this.fieldbookButton.setHeight("20px");
		layout.setComponentAlignment(this.fieldbookButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.fieldbookButton, 0);

		layout.addComponent(this.gdmsButton);
		layout.setComponentAlignment(this.gdmsButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.gdmsButton, 0);

		return layout;
	}

	protected Component layoutBreedingManagement() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.breedingManagementTitle);
		layout.setComponentAlignment(this.breedingManagementTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingManagementTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.germplasmImportButton);
		this.germplasmImportButton.setHeight("20px");
		layout.setComponentAlignment(this.germplasmImportButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.germplasmImportButton, 0);

		layout.addComponent(this.germplasmImportButton2);
		this.germplasmImportButton2.setHeight("20px");
		layout.setComponentAlignment(this.germplasmImportButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.germplasmImportButton2, 0);

		layout.addComponent(this.makeCrossesButton);
		this.makeCrossesButton.setHeight("20px");
		layout.setComponentAlignment(this.makeCrossesButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.makeCrossesButton, 0);

		layout.addComponent(this.nurseryManagerButton);
		layout.setComponentAlignment(this.nurseryManagerButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.nurseryManagerButton, 0);

		return layout;
	}

	protected Component layoutAnalysisPipeline() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.analysisPipelineTitle);
		layout.setComponentAlignment(this.analysisPipelineTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.analysisPipelineTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight(new Integer(ManagerWorkflowDiagram.WORKFLOW_STEP_HEIGHT - 135).toString());
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.breedingViewSingleSiteAnalysisLocalButton);
		this.breedingViewSingleSiteAnalysisLocalButton.setHeight("20px");
		layout.setComponentAlignment(this.breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewSingleSiteAnalysisLocalButton, 0);

		layout.addComponent(this.breedingViewMultiSiteAnalysisButton);
		this.breedingViewMultiSiteAnalysisButton.setHeight("20px");
		layout.setComponentAlignment(this.breedingViewMultiSiteAnalysisButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewMultiSiteAnalysisButton, 0);

		layout.addComponent(this.metaAnalysisBtn);
		this.metaAnalysisBtn.setHeight("20px");
		layout.setComponentAlignment(this.metaAnalysisBtn, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.metaAnalysisBtn, 0);

		layout.addComponent(this.breedingViewButton);
		layout.setComponentAlignment(this.breedingViewButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewButton, 0);

		return layout;
	}

	protected Component layoutDecisionSupport() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.decisionSupportTitle);
		layout.setComponentAlignment(this.decisionSupportTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.decisionSupportTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.mbdtButton);
		this.mbdtButton.setHeight("20px");
		layout.setComponentAlignment(this.mbdtButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.mbdtButton, 0);

		layout.addComponent(this.optimasButton);
		this.optimasButton.setHeight("20px");
		layout.setComponentAlignment(this.optimasButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.optimasButton, 0);

		layout.addComponent(this.mainHeadToHeadButton2);
		this.mainHeadToHeadButton2.setHeight("20px");
		layout.setComponentAlignment(this.mainHeadToHeadButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.mainHeadToHeadButton2, 0);

		layout.addComponent(this.queryForAdaptedGermplasmButton2);
		this.queryForAdaptedGermplasmButton2.setHeight("20px");
		layout.setComponentAlignment(this.queryForAdaptedGermplasmButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.queryForAdaptedGermplasmButton2, 0);

		return layout;
	}

	protected Component createPanel(String caption, String... buttonCaptions) {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		Label titleLabel = new Label(caption);
		titleLabel.setStyleName("gcp-section-title");
		titleLabel.setSizeUndefined();

		layout.addComponent(titleLabel);
		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER);
		layout.setExpandRatio(titleLabel, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		for (String buttonCaption : buttonCaptions) {
			Button button = new Button(buttonCaption);
			button.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");

			layout.addComponent(button);
			layout.setComponentAlignment(button, Alignment.TOP_CENTER);
			layout.setExpandRatio(button, 0);
		}

		return layout;
	}

	protected void configureWorkflowStepLayout(VerticalLayout layout) {
		layout.setWidth(ManagerWorkflowDiagram.WORKFLOW_STEP_WIDTH + "px");
		layout.setHeight(ManagerWorkflowDiagram.WORKFLOW_STEP_HEIGHT + "px");
		layout.setStyleName("gcp-workflow-step");
		layout.setMargin(false, false, true, false);
	}

	protected void initializeActions() {
		if (!this.workflowPreview) {
			this.mainHeadToHeadButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));
			this.mainHeadToHeadButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));
			this.browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_BROWSER));
			this.browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
			this.browseGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_LIST_BROWSER));
			this.crossStudyBrowserButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.HEAD_TO_HEAD_BROWSER));
			this.crossStudyBrowserButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.HEAD_TO_HEAD_BROWSER));
			this.breedingPlannerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_PLANNER)); // TODO: change this to run
																												// breeding planner tool
			this.gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
			this.mbdtButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MBDT));
			this.breedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));
			this.fieldbookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB));
			this.germplasmImportButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.IBFB_GERMPLASM_IMPORT));
			this.germplasmImportButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_IMPORT));
			this.optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));
			this.manageGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
			this.nurseryManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));
			this.makeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));
			this.breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));
			this.createTemplatesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_TEMPLATE_WIZARD));
			this.datasetImporterBtn.addListener(new LaunchWorkbenchToolAction(ToolEnum.DATASET_IMPORTER));
			this.queryForAdaptedGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
			this.queryForAdaptedGermplasmButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
			this.breedingManagerListManager.addListener(new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN));

			this.breedingViewSingleSiteAnalysisCentralButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW, this.project,
					WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));
			this.breedingViewSingleSiteAnalysisLocalButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW, this.project,
					WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));

			this.breedingViewMultiSiteAnalysisButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_GXE, this.project, null));
			this.membersButton.addListener(new ChangeWindowAction(WindowEnums.MEMBER, this.project));

			this.backupIBDBButton.addListener(new ChangeWindowAction(WindowEnums.RECOVERY, this.project));
			this.restoreIBDBButton.addListener(new ChangeWindowAction(WindowEnums.RECOVERY, this.project));
			this.userToolsButton.addListener(new OpenWindowAction(WindowEnum.USER_TOOLS, this.project));

			this.updateProjectButton.addListener(new OpenUpdateProjectPageAction());
			this.projectLocationButton.addListener(new OpenProgramLocationsAction(this.project));
			this.projectMethodsButton.addListener(new OpenProgramMethodsAction(this.project));
			this.deleteProjectButton.addListener(new DeleteProjectAction());

			this.ontologyBrowserFBBtn.addListener(new LaunchWorkbenchToolAction(ToolEnum.ONTOLOGY_BROWSER_FIELDBOOK_WEB));
			this.metaAnalysisBtn.addListener(new ChangeWindowAction(WindowEnums.BV_META_ANALYSIS, this.project, null));
		}
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();

		this.initializeLabels();

		if (this.workflowPreview) {
			this.setStyleName("gcp-removelink");
		}

	}

	private void initializeLabels() {
		this.messageSource.setValue(this.administrationTitle, Message.ADMINISTRATION_TITLE);
		this.messageSource.setCaption(this.membersButton, Message.MEMBERS_LINK);
		this.messageSource.setCaption(this.backupIBDBButton, Message.BACKUP_IBDB_LINK);
		this.messageSource.setCaption(this.restoreIBDBButton, Message.RESTORE_IBDB_LINK);
		this.messageSource.setCaption(this.projectMethodsButton, Message.PROJECT_METHODS_LINK);
		this.messageSource.setCaption(this.projectLocationButton, Message.PROJECT_LOCATIONS_LINK);

		this.messageSource.setValue(this.projectPlanningTitle, Message.PROJECT_PLANNING);
		this.messageSource.setCaption(this.browseGermplasmButton, Message.GERMPLASM_BROWSER_LINK);
		this.messageSource.setCaption(this.browseStudiesButton, Message.STUDY_BROWSER_LINK);
		this.messageSource.setCaption(this.browseGermplasmListsButton, Message.GERMPLASM_LIST_BROWSER_LINK);

		this.messageSource.setValue(this.analysisPipelineTitle, Message.ANALYSIS_PIPELINE_TITLE);
		this.messageSource.setCaption(this.breedingViewSingleSiteAnalysisLocalButton, Message.SINGLE_SITE_ANALYSIS_LOCAL_LINK);
		this.messageSource.setCaption(this.breedingViewSingleSiteAnalysisCentralButton, Message.SINGLE_SITE_ANALYSIS_CENTRAL_LINK);
		this.messageSource.setCaption(this.breedingViewMultiSiteAnalysisButton, Message.MULTI_SITE_ANALYSIS_MANAGER);
		this.messageSource.setCaption(this.breedingViewButton, Message.BREEDING_VIEW_QTL);
		this.messageSource.setValue(this.genoTypingTitle, Message.DATA_MANAGEMENT_TITLE);
		this.messageSource.setCaption(this.gdmsButton, Message.GDMS_LINK);

		this.messageSource.setValue(this.breedingManagementTitle, Message.BREEDING_MANAGEMENT_TITLE);
		this.messageSource.setCaption(this.nurseryManagerButton, Message.NURSERY_MANAGER_LINK);

		this.messageSource.setValue(this.decisionSupportTitle, Message.DECISION_SUPPORT_TITLE);
		this.messageSource.setCaption(this.optimasButton, Message.OPTIMAS);
		this.messageSource.setCaption(this.mbdtButton, Message.MBDT_LINK);

		this.messageSource.setCaption(this.deleteProjectButton, Message.DELETE_PROJECT_LINK);
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		if (this.workflowPreview) {
			this.messageSource.setValue(this.dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "MENU");
		} else {
			this.messageSource.setValue(this.dashboardTitle, Message.PROJECT_TITLE, this.project.getProjectName());
		}
	}
}
