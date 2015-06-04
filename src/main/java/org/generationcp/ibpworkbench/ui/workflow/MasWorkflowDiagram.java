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
public class MasWorkflowDiagram extends Panel implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	// this is in pixels and used for layouting
	private static final int WORKFLOW_STEP_HEIGHT = 125;
	private static final int WORKFLOW_STEP_EXTRA_HEIGHT = 210;
	private static final int PROJECT_PLANNING_HEIGHT = 240;
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
	private Label populationDevelopmentTitle;
	private Label fieldTrialManagementTitle;

	private Label genotypingTitle;
	private Label statisticalAnalysisTitle;
	private Label breedingDecisionTitle;

	// links for tools
	private Button browseStudiesButton;
	private Button browseGermplasmListsButton;
	private Button gdmsButton;
	private Button breedingManagerButton;
	private Button breedingViewButton;
	private Button breedingViewSingleSiteAnalysisCentralButton;
	private Button breedingViewSingleSiteAnalysisLocalButton;
	private Button fieldbookButton;
	private Button optimasButton;
	private Button browseGenotypingDataButton;
	private Button makeCrossesButton;

	private Button mainHeadToHeadButton;
	private Button mainHeadToHeadButton2;
	private Button breedingPlannerButton;
	private Button queryForAdaptedGermplasmButton;
	private Button queryForAdaptedGermplasmButton2;

	private Embedded downArrowImage1;
	private Embedded downArrowImage2;
	private Embedded downArrowImage3;
	private Embedded downArrowImage4;
	private Embedded twoHeadedArrowImage;

	private final Role role;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Button manageGermplasmListsButton;

	private Button breedingViewMultiSiteAnalysisButton;

	private Button planMasTimelineButton;

	private Button germplasmImportButton;

	private Button germplasmImportButton2;
	private Button ontologyBrowserFBBtn;
	private Button metaAnalysisBtn;

	public MasWorkflowDiagram(boolean workflowPreview, Project project, Role role) {
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
		this.dashboardTitle.setStyleName("gcp-content-title");

		this.projectPlanningTitle = new Label(this.messageSource.getMessage(Message.PROJECT_PLANNING));
		this.projectPlanningTitle.setStyleName(MasWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.populationDevelopmentTitle = new Label(this.messageSource.getMessage(Message.POPULATION_DEVELOPMENT));
		this.populationDevelopmentTitle.setStyleName(MasWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.fieldTrialManagementTitle = new Label(this.messageSource.getMessage(Message.FIELD_TRIAL_MANAGEMENT));
		this.fieldTrialManagementTitle.setStyleName(MasWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.genotypingTitle = new Label(this.messageSource.getMessage(Message.GENOTYPING));
		this.genotypingTitle.setStyleName(MasWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.statisticalAnalysisTitle = new Label(this.messageSource.getMessage(Message.STATISTICAL_ANALYSIS));
		this.statisticalAnalysisTitle.setStyleName(MasWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.breedingDecisionTitle = new Label(this.messageSource.getMessage(Message.BREEDING_DECISION));
		this.breedingDecisionTitle.setStyleName(MasWorkflowDiagram.GCP_SECTION_TITLE_LARGE);

		this.breedingPlannerButton = new Button("Breeding Planner");
		this.breedingPlannerButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingPlannerButton.setSizeUndefined();
		this.breedingPlannerButton.setDescription("Click to launch the freestanding Breeding Planner application.");

		this.planMasTimelineButton = new Button(this.messageSource.getMessage(Message.BREEDING_PLANNER_MAS));
		this.planMasTimelineButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.planMasTimelineButton.setSizeUndefined();
		this.planMasTimelineButton.setDescription("Click to launch the freestanding Breeding Planner application.");

		this.germplasmImportButton = new Button("IBFB Import Germplasm Lists");
		this.germplasmImportButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.germplasmImportButton.setSizeUndefined();
		this.germplasmImportButton.setDescription("Click to launch Fieldbook on Nursery Manager View.");

		this.germplasmImportButton2 = new Button("Import Germplasm Lists");
		this.germplasmImportButton2.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.germplasmImportButton2.setSizeUndefined();
		this.germplasmImportButton2.setDescription("Click to launch the Germplasm Import View.");

		this.browseStudiesButton = new Button(this.messageSource.getMessage(Message.BROWSE_STUDIES_AND_DATASETS));
		this.browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.browseStudiesButton.setSizeUndefined();
		this.browseStudiesButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_STUDY_BROWSER));

		this.browseGermplasmListsButton = new Button("Browse Germplasms and Lists");
		this.browseGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.browseGermplasmListsButton.setSizeUndefined();
		this.browseGermplasmListsButton.setDescription("Click to launch the Germplasm List Manager.");

		this.breedingManagerButton = new Button(this.messageSource.getMessage(Message.BREEDING_MANAGER));
		this.breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingManagerButton.setSizeUndefined();
		this.breedingManagerButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_MANAGER));

		this.manageGermplasmListsButton = new Button(this.messageSource.getMessage(Message.LIST_MANAGER));
		this.manageGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.manageGermplasmListsButton.setSizeUndefined();
		this.manageGermplasmListsButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_LIST_MANAGER));

		this.browseGenotypingDataButton = new Button(this.messageSource.getMessage(Message.GENOTYPIC_DATA_BROWSER_LINK));
		this.browseGenotypingDataButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.browseGenotypingDataButton.setSizeUndefined();
		this.browseGenotypingDataButton.setDescription(this.messageSource.getMessage(Message.GENOTYPIC_DATA_BROWSER_DESC));

		this.breedingViewButton = new Button(this.messageSource.getMessage(Message.BREEDING_VIEW));
		this.breedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewButton.setSizeUndefined();
		this.breedingViewButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW));

		this.breedingViewSingleSiteAnalysisCentralButton =
				new Button(this.messageSource.getMessage(Message.SINGLE_SITE_ANALYSIS_CENTRAL_LINK));
		this.breedingViewSingleSiteAnalysisCentralButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewSingleSiteAnalysisCentralButton.setSizeUndefined();
		this.breedingViewSingleSiteAnalysisCentralButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));

		this.breedingViewSingleSiteAnalysisLocalButton = new Button(this.messageSource.getMessage(Message.SINGLE_SITE_ANALYSIS_LOCAL_LINK));
		this.breedingViewSingleSiteAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewSingleSiteAnalysisLocalButton.setSizeUndefined();
		this.breedingViewSingleSiteAnalysisLocalButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));

		this.breedingViewMultiSiteAnalysisButton = new Button(this.messageSource.getMessage(Message.MULTI_SITE_ANALYSIS_LINK));
		this.breedingViewMultiSiteAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewMultiSiteAnalysisButton.setSizeUndefined();
		this.breedingViewMultiSiteAnalysisButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_MULTI_SITE_ANALYSIS));

		this.gdmsButton = new Button(this.messageSource.getMessage(Message.MANAGE_GENOTYPING_DATA));
		this.gdmsButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.gdmsButton.setSizeUndefined();
		this.gdmsButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_GDMS));

		this.fieldbookButton = new Button("Manage Trials");
		this.fieldbookButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.fieldbookButton.setSizeUndefined();
		this.fieldbookButton.setDescription("Click to launch Fieldbook on Trial Manager View.");

		this.optimasButton = new Button(this.messageSource.getMessage(Message.OPTIMAS));
		this.optimasButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.optimasButton.setSizeUndefined();
		this.optimasButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_OPTIMAS));

		this.makeCrossesButton = new Button(this.messageSource.getMessage(Message.MAKE_CROSSES));
		this.makeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.makeCrossesButton.setSizeUndefined();
		this.makeCrossesButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_CROSSING_MANAGER));

		this.downArrowImage1 = new Embedded("", new ThemeResource(MasWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrowImage2 = new Embedded("", new ThemeResource(MasWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrowImage3 = new Embedded("", new ThemeResource(MasWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrowImage4 = new Embedded("", new ThemeResource(MasWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.twoHeadedArrowImage = new Embedded("", new ThemeResource(MasWorkflowDiagram.TWO_HEADED_ARROW_THEME_RESOURCE));

		this.mainHeadToHeadButton = new Button(this.messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
		this.mainHeadToHeadButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.mainHeadToHeadButton.setSizeUndefined();
		this.mainHeadToHeadButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

		this.mainHeadToHeadButton2 = new Button(this.messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
		this.mainHeadToHeadButton2.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.mainHeadToHeadButton2.setSizeUndefined();
		this.mainHeadToHeadButton2.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

		this.queryForAdaptedGermplasmButton = new Button(this.messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
		this.queryForAdaptedGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.queryForAdaptedGermplasmButton.setSizeUndefined();
		this.queryForAdaptedGermplasmButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));

		this.queryForAdaptedGermplasmButton2 = new Button(this.messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
		this.queryForAdaptedGermplasmButton2.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.queryForAdaptedGermplasmButton2.setSizeUndefined();
		this.queryForAdaptedGermplasmButton2.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));

		this.ontologyBrowserFBBtn = new Button("Manage Ontologies");
		this.ontologyBrowserFBBtn.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.ontologyBrowserFBBtn.setSizeUndefined();
		this.ontologyBrowserFBBtn.setDescription("Click to launch Fieldbook on Ontology Browser view");

		this.metaAnalysisBtn = new Button("Meta Analysis of Field Trials");
		this.metaAnalysisBtn.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);
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
		layout.setWidth("620px");
		layout.setHeight("1400px");

		String extraSpace = MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + "px";
		int top = 10;
		String topInPixels = "";

		// the steps on the first column
		Component projectPlanningArea = this.layoutProjectPlanning();
		layout.addComponent(projectPlanningArea, "top:" + extraSpace + "; left:" + extraSpace);

		top = top + MasWorkflowDiagram.PROJECT_PLANNING_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		layout.addComponent(this.downArrowImage1, "top:" + topInPixels + "; left:" + MasWorkflowDiagram.FIRST_COLUMN_LEFT_FOR_ARROWS);

		top = top + MasWorkflowDiagram.ARROW_IMAGE_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component populationDevelopmentArea = this.layoutPopulationDevelopment();
		layout.addComponent(populationDevelopmentArea, "top:" + topInPixels + "; left:" + extraSpace);

		top = top + MasWorkflowDiagram.WORKFLOW_STEP_EXTRA_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		layout.addComponent(this.downArrowImage2, "top:" + topInPixels + "; left:" + MasWorkflowDiagram.FIRST_COLUMN_LEFT_FOR_ARROWS);

		top = top + MasWorkflowDiagram.ARROW_IMAGE_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component fieldTrialArea = this.layoutFieldTrialManagement();
		layout.addComponent(fieldTrialArea, "top:" + topInPixels + "; left:" + extraSpace);

		top = top + MasWorkflowDiagram.WORKFLOW_STEP_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		layout.addComponent(this.downArrowImage3, "top:" + topInPixels + "; left:" + MasWorkflowDiagram.FIRST_COLUMN_LEFT_FOR_ARROWS);

		top = top + MasWorkflowDiagram.ARROW_IMAGE_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component statisticalAnalysisArea = this.layoutStatisticalAnalysis();
		layout.addComponent(statisticalAnalysisArea, "top:" + topInPixels + "; left:" + extraSpace);

		top = top + MasWorkflowDiagram.WORKFLOW_STEP_EXTRA_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		layout.addComponent(this.downArrowImage4, "top:" + topInPixels + "; left:" + MasWorkflowDiagram.FIRST_COLUMN_LEFT_FOR_ARROWS);

		top = top + MasWorkflowDiagram.ARROW_IMAGE_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		Component breedingDecisionArea = this.layoutBreedingDecision();
		layout.addComponent(breedingDecisionArea, "top:" + topInPixels + "; left:" + extraSpace);

		// the steps on the second column
		top =
				10 + MasWorkflowDiagram.PROJECT_PLANNING_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS
						+ MasWorkflowDiagram.ARROW_IMAGE_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		topInPixels = top + "px";
		int left =
				MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MasWorkflowDiagram.WORKFLOW_STEP_WIDTH
						+ MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MasWorkflowDiagram.ARROW_IMAGE_WIDTH
						+ MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		String leftInPixels = left + "px";

		Component genotypingArea = this.layoutGenotypingStep();
		layout.addComponent(genotypingArea, "top:" + topInPixels + "; left:" + leftInPixels);

		top =
				10 + MasWorkflowDiagram.PROJECT_PLANNING_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS
						+ MasWorkflowDiagram.ARROW_IMAGE_HEIGHT + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + 50;
		topInPixels = top + "px";
		left =
				MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MasWorkflowDiagram.WORKFLOW_STEP_WIDTH
						+ MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS + MasWorkflowDiagram.EXTRA_SPACE_BETWEEN_COMPONENTS;
		leftInPixels = left + "px";
		layout.addComponent(this.twoHeadedArrowImage, "top:" + topInPixels + "; left:" + leftInPixels);

		final VerticalLayout rootContainer = new VerticalLayout();
		rootContainer.setMargin(new Layout.MarginInfo(false, true, true, true));
		rootContainer.setSpacing(false);

		if (!this.workflowPreview) {
			Label header = new Label();
			header.setStyleName(Bootstrap.Typography.H1.styleName());
			header.setValue(this.role.getLabel());
			rootContainer.addComponent(header);

		}
		rootContainer.addComponent(layout);
		rootContainer.setSizeUndefined();
		rootContainer.setWidth("750px");

		return rootContainer;
	}

	protected Component layoutProjectPlanning() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);
		layout.setHeight(MasWorkflowDiagram.PROJECT_PLANNING_HEIGHT + "px");

		layout.addComponent(this.projectPlanningTitle);
		layout.setComponentAlignment(this.projectPlanningTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.projectPlanningTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("5px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.ontologyBrowserFBBtn);
		this.ontologyBrowserFBBtn.setHeight("20px");
		layout.setComponentAlignment(this.ontologyBrowserFBBtn, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.ontologyBrowserFBBtn, 0);

		layout.addComponent(this.planMasTimelineButton);
		this.planMasTimelineButton.setHeight("20px");
		layout.setComponentAlignment(this.planMasTimelineButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.planMasTimelineButton, 0);

		layout.addComponent(this.browseStudiesButton);
		this.browseStudiesButton.setHeight("20px");
		layout.setComponentAlignment(this.browseStudiesButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.browseStudiesButton, 0);

		layout.addComponent(this.browseGermplasmListsButton);
		this.browseGermplasmListsButton.setHeight("20px");
		layout.setComponentAlignment(this.browseGermplasmListsButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.browseGermplasmListsButton, 0);

		layout.addComponent(this.browseGenotypingDataButton);
		this.browseGenotypingDataButton.setHeight("20px");
		layout.setComponentAlignment(this.browseGenotypingDataButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.browseGenotypingDataButton, 0);

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

	protected Component layoutPopulationDevelopment() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);
		layout.setHeight(MasWorkflowDiagram.WORKFLOW_STEP_EXTRA_HEIGHT + "px");

		layout.addComponent(this.populationDevelopmentTitle);
		layout.setComponentAlignment(this.populationDevelopmentTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.populationDevelopmentTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.germplasmImportButton2);
		this.germplasmImportButton2.setHeight("20px");
		layout.setComponentAlignment(this.germplasmImportButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.germplasmImportButton2, 0);

		/**
		 * layout.addComponent(manageGermplasmListsButton); manageGermplasmListsButton.setHeight("20px");
		 * layout.setComponentAlignment(manageGermplasmListsButton, Alignment.TOP_CENTER); layout.setExpandRatio(manageGermplasmListsButton,
		 * 0);
		 **/

		layout.addComponent(this.makeCrossesButton);
		this.makeCrossesButton.setHeight("20px");
		layout.setComponentAlignment(this.makeCrossesButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.makeCrossesButton, 0);

		layout.addComponent(this.breedingManagerButton);
		layout.setComponentAlignment(this.breedingManagerButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingManagerButton, 0);

		return layout;
	}

	protected Component layoutFieldTrialManagement() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.fieldTrialManagementTitle);
		layout.setComponentAlignment(this.fieldTrialManagementTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.fieldTrialManagementTitle, 0);

		Label emptyLabel = new Label(" ");
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
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.genotypingTitle);
		layout.setComponentAlignment(this.genotypingTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.genotypingTitle, 0);

		Label emptyLabel = new Label(" ");
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
		this.configureWorkflowStepLayout(layout);
		layout.setHeight(MasWorkflowDiagram.WORKFLOW_STEP_EXTRA_HEIGHT + "px");

		layout.addComponent(this.statisticalAnalysisTitle);
		layout.setComponentAlignment(this.statisticalAnalysisTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.statisticalAnalysisTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("5px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.breedingViewSingleSiteAnalysisLocalButton);
		this.breedingViewSingleSiteAnalysisLocalButton.setHeight("20px");
		layout.setComponentAlignment(this.breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewSingleSiteAnalysisLocalButton, 0);

		layout.addComponent(this.metaAnalysisBtn);
		this.metaAnalysisBtn.setHeight("20px");
		layout.setComponentAlignment(this.metaAnalysisBtn, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.metaAnalysisBtn, 0);

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
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.breedingDecisionTitle);
		layout.setComponentAlignment(this.breedingDecisionTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingDecisionTitle, 0);

		Label emptyLabel = new Label(" ");
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
			button.setStyleName(BaseTheme.BUTTON_LINK + MasWorkflowDiagram.GCP_WORKFLOW_LINK);

			layout.addComponent(button);
			layout.setComponentAlignment(button, Alignment.TOP_CENTER);
			layout.setExpandRatio(button, 0);
		}

		return layout;
	}

	protected void configureWorkflowStepLayout(VerticalLayout layout) {
		layout.setWidth(MasWorkflowDiagram.WORKFLOW_STEP_WIDTH + "px");
		layout.setHeight(MasWorkflowDiagram.WORKFLOW_STEP_HEIGHT + "px");
		layout.setStyleName("gcp-workflow-step");
		layout.setMargin(false, false, true, false);
	}

	protected void initializeActions() {
		if (!this.workflowPreview) {

			this.germplasmImportButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.IBFB_GERMPLASM_IMPORT));
			this.germplasmImportButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_IMPORT));

			this.planMasTimelineButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_PLANNER));
			this.breedingPlannerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_PLANNER));
			this.mainHeadToHeadButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));
			this.mainHeadToHeadButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));

			this.browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
			this.browseGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN));
			this.gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
			this.browseGenotypingDataButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
			this.manageGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
			this.breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));
			this.breedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));

			this.breedingViewSingleSiteAnalysisCentralButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW, this.project,
					WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));
			this.breedingViewSingleSiteAnalysisLocalButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW, this.project,
					WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));

			this.breedingViewMultiSiteAnalysisButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_GXE, this.project, null));

			this.fieldbookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB));
			this.optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));

			this.makeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));

			this.queryForAdaptedGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
			this.queryForAdaptedGermplasmButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));

			this.metaAnalysisBtn.addListener(new ChangeWindowAction(WindowEnums.BV_META_ANALYSIS, this.project, null));
			this.ontologyBrowserFBBtn.addListener(new LaunchWorkbenchToolAction(ToolEnum.ONTOLOGY_BROWSER_FIELDBOOK_WEB));
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
			this.messageSource.setValue(this.dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "MAS");
		} else {
			this.messageSource.setValue(this.dashboardTitle, Message.PROJECT_TITLE, this.project.getProjectName());
		}
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
