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
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class MabcWorkflowDiagram extends Panel implements WorkflowConstants, InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	// this is in pixels and used for layouting
	private static final int WORKFLOW_STEP_HEIGHT = 125;
	private static final int WORKFLOW_STEP_EXTRA_HEIGHT = 275;
	private static final int PROJECT_PLANNING_HEIGHT = 250;
	private static final int STATISTICAL_ANALYSIS_HEIGHT = 150;
	private static final int FIELD_TRIAL_MANAGEMENT_HEIGHT = 100;
	private static final int WORKFLOW_STEP_WIDTH = 270;
	private static final int EXTRA_SPACE_BETWEEN_COMPONENTS = 10;
	private static final int ARROW_IMAGE_HEIGHT = 30;
	private static final int ARROW_IMAGE_WIDTH = 40;
	private static final String FIRST_COLUMN_LEFT_FOR_ARROWS = "135px";
	private static final String DOWN_ARROW_THEME_RESOURCE = "../gcp-default/images/blc-arrow-d.png";
	private static final String TWO_HEADED_ARROW_THEME_RESOURCE = "../gcp-default/images/blc-arrow-lr.png";

	private static final String GCP_WORKFLOW_LINK = " gcp-workflow-link";

	private static final String GCP_SECTION_TITLE_LARGE = "gcp-section-title-large";

	private final boolean workflowPreview;

	private Project project;

	private Label dashboardTitle;

	private Label projectPlanningTitle;
	private Label backcrossingTitle;
	private Label fieldTrialManagementTitle;

	private Label genotypingTitle;
	private Label statisticalAnalysisTitle;
	private Label breedingDecisionTitle;

	// links for tools
	// GCP-6394
	private Button browseStudiesButton;
	private Button gdmsButton;
	private Button breedingViewButton;
	private Button breedingViewSingleSiteAnalysisCentralButton;
	private Button breedingViewSingleSiteAnalysisLocalButton;
	private Button fieldbookButton;
	private Button optimasButton;
	private Button mbdtButton;
	private Button browseGenotypingDataButton;
	private Button breedingManagerButton;
	private Button makeCrossesButton;

	private Embedded downArrowImage1;
	private Embedded downArrowImage2;
	private Embedded downArrowImage3;
	private Embedded downArrowImage4;
	private Embedded twoHeadedArrowImage;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Button manageGermplasmListsButton;

	private Button breedingViewMultiSiteAnalysisButton;

	private final Role role;

	private Button mainHeadToHeadButton;
	private Button mainHeadToHeadButton2;

	// GCP-6394
	private Button ontologyBrowserButton;
	private Button metaAnalysisButton;

	private Button breedingPlannerButton;

	private Button germplasmImportButton;

	private Button germplasmImportButton2;

	private Button queryForAdaptedGermplasmButton;
	private Button queryForAdaptedGermplasmButton2;

	private Button breedingManagerListManager;

	public MabcWorkflowDiagram(boolean workflowPreview, Project project, Role role) {
		this.workflowPreview = workflowPreview;

		if (!workflowPreview) {
			this.project = project;
		}

		this.role = role;
	}

	@Override
	public void afterPropertiesSet() {
		this.assemble();
	}

	protected void initializeComponents() {
		// dashboard title
		this.dashboardTitle = new Label();
		this.dashboardTitle.setDebugId("dashboardTitle");
		this.dashboardTitle.setStyleName("gcp-content-title");

		this.projectPlanningTitle = new Label("Program Planning");
		this.projectPlanningTitle.setDebugId("projectPlanningTitle");
		this.projectPlanningTitle.setStyleName(MabcWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.backcrossingTitle = new Label("Backcrossing");
		this.backcrossingTitle.setDebugId("backcrossingTitle");
		this.backcrossingTitle.setStyleName(MabcWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.fieldTrialManagementTitle = new Label("Field Trial Management");
		this.fieldTrialManagementTitle.setDebugId("fieldTrialManagementTitle");
		this.fieldTrialManagementTitle.setStyleName(MabcWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.genotypingTitle = new Label("Genotyping");
		this.genotypingTitle.setDebugId("genotypingTitle");
		this.genotypingTitle.setStyleName(MabcWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.statisticalAnalysisTitle = new Label("Statistical Analysis");
		this.statisticalAnalysisTitle.setDebugId("statisticalAnalysisTitle");
		this.statisticalAnalysisTitle.setStyleName(MabcWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.breedingDecisionTitle = new Label("Breeding Decision");
		this.breedingDecisionTitle.setDebugId("breedingDecisionTitle");
		this.breedingDecisionTitle.setStyleName(MabcWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.breedingPlannerButton = new Button(this.messageSource.getMessage(Message.BREEDING_PLANNER_MABC));
		this.breedingPlannerButton.setDebugId("breedingPlannerButton");
		this.breedingPlannerButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingPlannerButton.setSizeUndefined();
		this.breedingPlannerButton.setDescription("Click to launch the freestanding Breeding Planner application.");

		this.germplasmImportButton = new Button("IBFB Import Germplasm Lists");
		this.germplasmImportButton.setDebugId("germplasmImportButton");
		this.germplasmImportButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.germplasmImportButton.setSizeUndefined();
		this.germplasmImportButton.setDescription("Click to launch Fieldbook on Nursery Manager View.");

		this.germplasmImportButton2 = new Button("Import Germplasm Lists");
		this.germplasmImportButton2.setDebugId("germplasmImportButton2");
		this.germplasmImportButton2.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.germplasmImportButton2.setSizeUndefined();
		this.germplasmImportButton2.setDescription("Click to launch the Germplasm Import View.");

		this.mainHeadToHeadButton = new Button(this.messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
		this.mainHeadToHeadButton.setDebugId("mainHeadToHeadButton");
		this.mainHeadToHeadButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.mainHeadToHeadButton.setSizeUndefined();
		this.mainHeadToHeadButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

		this.mainHeadToHeadButton2 = new Button(this.messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
		this.mainHeadToHeadButton2.setDebugId("mainHeadToHeadButton2");
		this.mainHeadToHeadButton2.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.mainHeadToHeadButton2.setSizeUndefined();
		this.mainHeadToHeadButton2.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

		// GCP-6394
		this.ontologyBrowserButton = new Button(this.messageSource.getMessage(Message.MANAGE_ONTOLOGIES));
		this.ontologyBrowserButton.setDebugId("ontologyBrowserButton");
		this.ontologyBrowserButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.ontologyBrowserButton.setSizeUndefined();
		this.ontologyBrowserButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_ONTOLOGY_BROWSER));

		this.metaAnalysisButton = new Button(this.messageSource.getMessage(Message.META_ANALYSIS));
		this.metaAnalysisButton.setDebugId("metaAnalysisButton");
		this.metaAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.metaAnalysisButton.setSizeUndefined();
		this.metaAnalysisButton.setDescription(this.messageSource.getMessage(Message.META_ANALYSIS));

		this.manageGermplasmListsButton = new Button("Manage Germplasm Lists");
		this.manageGermplasmListsButton.setDebugId("manageGermplasmListsButton");
		this.manageGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.manageGermplasmListsButton.setSizeUndefined();
		this.manageGermplasmListsButton.setDescription("Click to launch Germplasm List Manager");

		this.browseGenotypingDataButton = new Button("Browse Genotyping Data");
		this.browseGenotypingDataButton.setDebugId("browseGenotypingDataButton");
		this.browseGenotypingDataButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.browseGenotypingDataButton.setSizeUndefined();
		this.browseGenotypingDataButton.setDescription("Click to launch genotyping data");

		this.breedingManagerButton = new Button("Manage Nurseries");
		this.breedingManagerButton.setDebugId("breedingManagerButton");
		this.breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingManagerButton.setSizeUndefined();
		this.breedingManagerButton.setDescription("Click to launch Manage Nurseries");

		this.browseStudiesButton = new Button("Browse Studies and Datasets");
		this.browseStudiesButton.setDebugId("browseStudiesButton");
		this.browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.browseStudiesButton.setSizeUndefined();
		this.browseStudiesButton.setDescription("Click to launch Study Browser");

		this.breedingViewButton = new Button("Breeding View Standalone (all analyses)");
		this.breedingViewButton.setDebugId("breedingViewButton");
		this.breedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewButton.setSizeUndefined();
		this.breedingViewButton.setDescription("Click to launch Breeding View");

		this.breedingViewSingleSiteAnalysisCentralButton = new Button("Single-Site Analysis for Central Datasets");
		this.breedingViewSingleSiteAnalysisCentralButton.setDebugId("breedingViewSingleSiteAnalysisCentralButton");
		this.breedingViewSingleSiteAnalysisCentralButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewSingleSiteAnalysisCentralButton.setSizeUndefined();
		this.breedingViewSingleSiteAnalysisCentralButton
				.setDescription("Click to launch Single-Site Analysis on Study Datasets from Central IBDB");

		this.breedingViewSingleSiteAnalysisLocalButton = new Button("Single-Site Analysis");
		this.breedingViewSingleSiteAnalysisLocalButton.setDebugId("breedingViewSingleSiteAnalysisLocalButton");
		this.breedingViewSingleSiteAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewSingleSiteAnalysisLocalButton.setSizeUndefined();
		this.breedingViewSingleSiteAnalysisLocalButton.setDescription("Click to launch Single-Site Analysis on Study Datasets");

		this.breedingViewMultiSiteAnalysisButton = new Button("Multi-site Analysis");
		this.breedingViewMultiSiteAnalysisButton.setDebugId("breedingViewMultiSiteAnalysisButton");
		this.breedingViewMultiSiteAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewMultiSiteAnalysisButton.setSizeUndefined();
		this.breedingViewMultiSiteAnalysisButton.setDescription("Click to launch Multi-Site Analysis on Study Datasets");

		this.gdmsButton = new Button("Manage Genotyping Data");
		this.gdmsButton.setDebugId("gdmsButton");
		this.gdmsButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.gdmsButton.setSizeUndefined();
		this.gdmsButton.setDescription("Click to launch GDMS");

		this.mbdtButton = new Button(this.messageSource.getMessage(Message.MBDT_MABC));
		this.mbdtButton.setDebugId("mbdtButton");
		this.mbdtButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.mbdtButton.setSizeUndefined();
		this.mbdtButton.setDescription("Click to launch MBDT");

		this.fieldbookButton = new Button(this.messageSource.getMessage(Message.MANAGE_TRIALS));
		this.fieldbookButton.setDebugId("fieldbookButton");
		this.fieldbookButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.fieldbookButton.setSizeUndefined();
		this.fieldbookButton.setDescription("Click to launch Fieldbook");

		this.queryForAdaptedGermplasmButton = new Button(this.messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
		this.queryForAdaptedGermplasmButton.setDebugId("queryForAdaptedGermplasmButton");
		this.queryForAdaptedGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.queryForAdaptedGermplasmButton.setSizeUndefined();
		this.queryForAdaptedGermplasmButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));

		this.queryForAdaptedGermplasmButton2 = new Button(this.messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
		this.queryForAdaptedGermplasmButton2.setDebugId("queryForAdaptedGermplasmButton2");
		this.queryForAdaptedGermplasmButton2.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.queryForAdaptedGermplasmButton2.setSizeUndefined();
		this.queryForAdaptedGermplasmButton2.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));

		this.breedingManagerListManager =
				new Button(this.messageSource.getMessage(Message.BREEDING_MANAGER_BROWSE_FOR_GERMPLASMS_AND_LISTS));
		this.breedingManagerListManager.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingManagerListManager.setSizeUndefined();
		this.breedingManagerListManager.setDescription(this.messageSource.getMessage(Message.CLICK_TO_BROWSE_FOR_GERMPLASMS_AND_LISTS));

		this.optimasButton = new Button("OptiMAS");
		this.optimasButton.setDebugId("optimasButton");
		this.optimasButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.optimasButton.setSizeUndefined();
		this.optimasButton.setDescription("Click to launch OptiMAS");

		this.makeCrossesButton = new Button("Make Crosses");
		this.makeCrossesButton.setDebugId("makeCrossesButton");
		this.makeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.makeCrossesButton.setSizeUndefined();
		this.makeCrossesButton.setDescription("Click to launch Crossing Manager");

		this.downArrowImage1 = new Embedded("", new ThemeResource(MabcWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrowImage1.setDebugId("downArrowImage1");
		this.downArrowImage2 = new Embedded("", new ThemeResource(MabcWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrowImage2.setDebugId("downArrowImage2");
		this.downArrowImage3 = new Embedded("", new ThemeResource(MabcWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrowImage3.setDebugId("downArrowImage3");
		this.downArrowImage4 = new Embedded("", new ThemeResource(MabcWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrowImage4.setDebugId("downArrowImage4");
		this.twoHeadedArrowImage = new Embedded("", new ThemeResource(MabcWorkflowDiagram.TWO_HEADED_ARROW_THEME_RESOURCE));
		this.twoHeadedArrowImage.setDebugId("twoHeadedArrowImage");
	}

	protected void initializeLayout() {
		this.setSizeFull();
		this.setScrollable(true);
		this.setContent(this.layoutWorkflowArea());
	}

	protected ComponentContainer layoutWorkflowArea() {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setDebugId("layout");
		layout.setWidth("620px");
		layout.setHeight("1000px");

		String extraSpace = MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + "px";
		int top = 10;
		String topInPixels = "";

		// the steps on the first column
		Component projectPlanningArea = this.layoutProjectPlanning();
		layout.addComponent(projectPlanningArea, "top:" + extraSpace + "; left:" + extraSpace);

		top = top + MabcWorkflowDiagram.PROJECT_PLANNING_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		layout.addComponent(this.downArrowImage1, "top:" + topInPixels + "; left:" + MabcWorkflowDiagram.FIRST_COLUMN_LEFT_FOR_ARROWS);

		top = top + MabcWorkflowDiagram.ARROW_IMAGE_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component backcrossingArea = this.layoutBackcrossing();
		layout.addComponent(backcrossingArea, "top:" + topInPixels + "; left:" + extraSpace);

		top = top + MabcWorkflowDiagram.WORKFLOW_STEP_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		layout.addComponent(this.downArrowImage2, "top:" + topInPixels + "; left:" + MabcWorkflowDiagram.FIRST_COLUMN_LEFT_FOR_ARROWS);

		top = top + MabcWorkflowDiagram.ARROW_IMAGE_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component fieldTrialArea = this.layoutFieldTrialManagement();
		layout.addComponent(fieldTrialArea, "top:" + topInPixels + "; left:" + extraSpace);

		top = top + MabcWorkflowDiagram.FIELD_TRIAL_MANAGEMENT_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		layout.addComponent(this.downArrowImage3, "top:" + topInPixels + "; left:" + MabcWorkflowDiagram.FIRST_COLUMN_LEFT_FOR_ARROWS);

		top = top + MabcWorkflowDiagram.ARROW_IMAGE_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component statisticalAnalysisArea = this.layoutStatisticalAnalysis();
		layout.addComponent(statisticalAnalysisArea, "top:" + topInPixels + "; left:" + extraSpace);

		top = top + MabcWorkflowDiagram.STATISTICAL_ANALYSIS_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		layout.addComponent(this.downArrowImage4, "top:" + topInPixels + "; left:" + MabcWorkflowDiagram.FIRST_COLUMN_LEFT_FOR_ARROWS);

		top = top + MabcWorkflowDiagram.ARROW_IMAGE_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component breedingDecisionArea = this.layoutBreedingDecision();
		layout.addComponent(breedingDecisionArea, "top:" + topInPixels + "; left:" + extraSpace);

		// the steps on the second column
		top =
				10 + MabcWorkflowDiagram.PROJECT_PLANNING_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS
						+ MabcWorkflowDiagram.ARROW_IMAGE_HEIGHT + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		int left =
				MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MabcWorkflowDiagram.WORKFLOW_STEP_WIDTH
						+ MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MabcWorkflowDiagram.ARROW_IMAGE_WIDTH
						+ MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		String leftInPixels = left + "px";

		Component genotypingArea = this.layoutGenotypingStep();
		layout.addComponent(genotypingArea, "top:" + topInPixels + "; left:" + leftInPixels);

		top =
				MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MabcWorkflowDiagram.WORKFLOW_STEP_EXTRA_HEIGHT
						+ MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MabcWorkflowDiagram.ARROW_IMAGE_HEIGHT
						+ MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + 40;
		topInPixels = top + "px";
		left =
				MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MabcWorkflowDiagram.WORKFLOW_STEP_WIDTH
						+ MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MabcWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		leftInPixels = left + "px";
		layout.addComponent(this.twoHeadedArrowImage, "top:" + topInPixels + "; left:" + leftInPixels);

		final VerticalLayout rootContainer = new VerticalLayout();
		rootContainer.setDebugId("rootContainer");
		rootContainer.setMargin(new Layout.MarginInfo(false, true, true, true));
		rootContainer.setSpacing(false);

		if (!this.workflowPreview) {
			Label header = new Label();
			header.setDebugId("header");
			header.setStyleName(Bootstrap.Typography.H1.styleName());
			header.setValue(this.role.getLabel());
			rootContainer.addComponent(header);

		}
		rootContainer.addComponent(layout);
		rootContainer.setSizeUndefined();

		return rootContainer;
	}

	protected Component layoutProjectPlanning() {
		VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		this.configureWorkflowStepLayout(layout);
		layout.setHeight(MabcWorkflowDiagram.PROJECT_PLANNING_HEIGHT + "px");

		layout.addComponent(this.projectPlanningTitle);
		layout.setComponentAlignment(this.projectPlanningTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.projectPlanningTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setDebugId("emptyLabel");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.breedingPlannerButton);
		this.breedingPlannerButton.setHeight("20px");
		layout.setComponentAlignment(this.breedingPlannerButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingPlannerButton, 0);

		// GCP-6394
		layout.addComponent(this.browseStudiesButton);
		this.browseStudiesButton.setHeight("20px");
		layout.setComponentAlignment(this.browseStudiesButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.browseStudiesButton, 0);

		layout.addComponent(this.germplasmImportButton2);
		this.germplasmImportButton2.setHeight("20px");
		layout.setComponentAlignment(this.germplasmImportButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.germplasmImportButton2, 0);

		layout.addComponent(this.browseGenotypingDataButton);
		this.browseGenotypingDataButton.setHeight("20px");
		layout.setComponentAlignment(this.browseGenotypingDataButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.browseGenotypingDataButton, 0);

		layout.addComponent(this.mainHeadToHeadButton2);
		this.mainHeadToHeadButton2.setHeight("20px");
		layout.setComponentAlignment(this.mainHeadToHeadButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.mainHeadToHeadButton2, 0);

		layout.addComponent(this.ontologyBrowserButton);
		this.ontologyBrowserButton.setHeight("20px");
		layout.setComponentAlignment(this.ontologyBrowserButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.ontologyBrowserButton, 0);

		layout.addComponent(this.queryForAdaptedGermplasmButton2);
		this.queryForAdaptedGermplasmButton2.setHeight("20px");
		layout.setComponentAlignment(this.queryForAdaptedGermplasmButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.queryForAdaptedGermplasmButton2, 0);

		layout.addComponent(this.breedingManagerListManager);
		this.breedingManagerListManager.setHeight("20px");
		layout.setComponentAlignment(this.breedingManagerListManager, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingManagerListManager, 0);

		return layout;
	}

	protected Component layoutBackcrossing() {
		VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.backcrossingTitle);
		layout.setComponentAlignment(this.backcrossingTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.backcrossingTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setDebugId("emptyLabel");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.mbdtButton);
		layout.setComponentAlignment(this.mbdtButton, Alignment.TOP_CENTER);
		this.mbdtButton.setHeight("20px");
		layout.setExpandRatio(this.mbdtButton, 0);

		layout.addComponent(this.makeCrossesButton);
		layout.setComponentAlignment(this.makeCrossesButton, Alignment.TOP_CENTER);
		this.makeCrossesButton.setHeight("20px");
		layout.setExpandRatio(this.makeCrossesButton, 0);

		layout.addComponent(this.breedingManagerButton);
		layout.setComponentAlignment(this.breedingManagerButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingManagerButton, 0);

		return layout;
	}

	protected Component layoutFieldTrialManagement() {
		VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		this.configureWorkflowStepLayout(layout);

		layout.setHeight(MabcWorkflowDiagram.FIELD_TRIAL_MANAGEMENT_HEIGHT + "px");

		layout.addComponent(this.fieldTrialManagementTitle);
		layout.setComponentAlignment(this.fieldTrialManagementTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.fieldTrialManagementTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setDebugId("emptyLabel");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.fieldbookButton);
		layout.setComponentAlignment(this.fieldbookButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.fieldbookButton, 0);

		return layout;
	}

	protected Component layoutGenotypingStep() {
		VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.genotypingTitle);
		layout.setComponentAlignment(this.genotypingTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.genotypingTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setDebugId("emptyLabel");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.gdmsButton);
		layout.setComponentAlignment(this.gdmsButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.gdmsButton, 0);

		return layout;
	}

	protected Component layoutStatisticalAnalysis() {
		VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		this.configureWorkflowStepLayout(layout);

		layout.setHeight(MabcWorkflowDiagram.STATISTICAL_ANALYSIS_HEIGHT + "px");

		layout.addComponent(this.statisticalAnalysisTitle);
		layout.setComponentAlignment(this.statisticalAnalysisTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.statisticalAnalysisTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setDebugId("emptyLabel");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.breedingViewSingleSiteAnalysisLocalButton);
		this.breedingViewSingleSiteAnalysisLocalButton.setHeight("20px");
		layout.setComponentAlignment(this.breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewSingleSiteAnalysisLocalButton, 0);

		layout.addComponent(this.metaAnalysisButton);
		this.metaAnalysisButton.setHeight("20px");
		layout.setComponentAlignment(this.metaAnalysisButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.metaAnalysisButton, 0);

		layout.addComponent(this.breedingViewMultiSiteAnalysisButton);
		this.breedingViewMultiSiteAnalysisButton.setHeight("20px");
		layout.setComponentAlignment(this.breedingViewMultiSiteAnalysisButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewMultiSiteAnalysisButton, 0);

		layout.addComponent(this.breedingViewButton);
		layout.setComponentAlignment(this.breedingViewButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewButton, 0);

		return layout;
	}

	protected Component layoutBreedingDecision() {
		VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.breedingDecisionTitle);
		layout.setComponentAlignment(this.breedingDecisionTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingDecisionTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setDebugId("emptyLabel");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.mainHeadToHeadButton);
		layout.setComponentAlignment(this.mainHeadToHeadButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.mainHeadToHeadButton, 0);

		layout.addComponent(this.queryForAdaptedGermplasmButton);
		layout.setComponentAlignment(this.queryForAdaptedGermplasmButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.queryForAdaptedGermplasmButton, 0);

		return layout;
	}

	protected Component createPanel(String caption, String... buttonCaptions) {
		VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		this.configureWorkflowStepLayout(layout);

		Label titleLabel = new Label(caption);
		titleLabel.setDebugId("titleLabel");
		titleLabel.setStyleName("gcp-section-title");
		titleLabel.setSizeUndefined();

		layout.addComponent(titleLabel);
		layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER);
		layout.setExpandRatio(titleLabel, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setDebugId("emptyLabel");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		for (String buttonCaption : buttonCaptions) {
			Button button = new Button(buttonCaption);
			button.setDebugId("button");
			button.setStyleName(BaseTheme.BUTTON_LINK + MabcWorkflowDiagram.GCP_WORKFLOW_LINK);

			layout.addComponent(button);
			layout.setComponentAlignment(button, Alignment.TOP_CENTER);
			layout.setExpandRatio(button, 0);
		}

		return layout;
	}

	protected void configureWorkflowStepLayout(VerticalLayout layout) {
		layout.setWidth(MabcWorkflowDiagram.WORKFLOW_STEP_WIDTH + "px");
		layout.setHeight(MabcWorkflowDiagram.WORKFLOW_STEP_HEIGHT + "px");
		layout.setStyleName("gcp-workflow-step");
		layout.setMargin(false, false, true, false);
	}

	protected void initializeActions() {
		if (!this.workflowPreview) {

			this.germplasmImportButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.IBFB_GERMPLASM_IMPORT));
			this.germplasmImportButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_IMPORT));

			this.breedingPlannerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_PLANNER));
			this.mainHeadToHeadButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));
			this.mainHeadToHeadButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));

			this.mbdtButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MBDT));
			this.ontologyBrowserButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.ONTOLOGY_BROWSER_FIELDBOOK_WEB));
			this.metaAnalysisButton.addListener(new ChangeWindowAction(WindowEnums.BV_META_ANALYSIS, this.project, null));
			this.browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
			this.gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
			this.breedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));

			this.breedingViewSingleSiteAnalysisCentralButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW, this.project,
					WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));
			this.breedingViewSingleSiteAnalysisLocalButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW, this.project,
					WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));

			this.fieldbookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB));
			this.optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));
			this.browseGenotypingDataButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
			this.breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));
			this.makeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));

			this.manageGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));

			this.breedingViewMultiSiteAnalysisButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_GXE, this.project, null));

			this.queryForAdaptedGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
			this.queryForAdaptedGermplasmButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
			this.breedingManagerListManager.addListener(new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN));
		}
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();

		if (this.workflowPreview) {
			this.setStyleName("gcp-removelink");
		}
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		if (this.workflowPreview) {
			this.messageSource.setValue(this.dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "MABC");
		} else {
			this.messageSource.setValue(this.dashboardTitle, Message.PROJECT_TITLE, this.project.getProjectName());
		}

		this.messageSource.setCaption(this.manageGermplasmListsButton, Message.LIST_MANAGER);
		this.messageSource.setCaption(this.breedingViewMultiSiteAnalysisButton, Message.MULTI_SITE_ANALYSIS_LINK);
		this.messageSource.setCaption(this.breedingViewButton, Message.BREEDING_VIEW);
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messagesource) {
		this.messageSource = messagesource;
	}
}
