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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class MarsWorkflowDiagram extends Panel implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	private static final String DOWN_ARROW_THEME_RESOURCE = "images/blc-arrow-d.png";

	private static final String GCP_WORKFLOW_LINK = " gcp-workflow-link";

	private static final String COMPONENT_HEIGHT = "750px";

	private static final String GCP_SECTION_TITLE = "gcp-section-title";

	private final boolean workflowPreview;

	private Project project;

	private Label dashboardTitle;

	// titles
	private Label projectPlanningTitle;
	private Label populationDevelopmentTitle;
	private Label fieldTrialManagementTitle;
	private Label genotypingTitle;
	private Button mainHeadToHeadButton;
	private Button mainHeadToHeadButton2;
	private Label phenotypicAnalysisTitle;
	private Label qtlAnalysisTitle;
	private Label singleSiteAnalysisTitle;

	private Label qtlSelectionTitle;

	private Label recombinationCycleTitle;

	private Label finalBreedingDecisionTitle;

	// buttons
	private Button browseGermplasmButton;
	private Button browseStudiesButton;
	private Button browseGermplasmListsButton;
	private Button browseGenotypingDataButton;

	private Button breedingManagerButton;

	private Button fieldBookButton;

	private Button gdmsButton;

	private Button phenotypicBreedingViewButton;
	private Button breedingViewSingleSiteAnalysisCentralButton;
	private Button breedingViewSingleSiteAnalysisLocalButton;

	private Button qtlBreedingViewButton;

	private Button optimasButton;

	private Button manageGermplasmListsButton;

	private Button breedingViewMultiSiteAnalysisButton;

	private Button makeCrossesButton;
	private Button recomMakeCrossesButton;

	private Embedded downArrow11;
	private Embedded downArrow12;
	private Embedded downArrow13;

	private Embedded downArrow21;

	private Embedded downArrow31;
	private Embedded downArrow32;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Button recomBreedingManagerButton;

	private final Role role;

	private Button breedingPlannerButton;

	private Button germplasmImportButton;

	private Button germplasmImportButton2;

	private Button queryForAdaptedGermplasmButton;

	private Button queryForAdaptedGermplasmButton2;

	private Button breedingManagerListManager;
	private Button ontologyBrowserFBBtn;
	private Button metaAnalysisBtn;
	private Button metaAnalysisBtn2;

	public MarsWorkflowDiagram(boolean workflowPreview, Project project, Role role) {
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

		// project planning
		this.projectPlanningTitle = new Label();
		this.projectPlanningTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		this.populationDevelopmentTitle = new Label();
		this.populationDevelopmentTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		this.fieldTrialManagementTitle = new Label();
		this.fieldTrialManagementTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		this.genotypingTitle = new Label();
		this.genotypingTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		this.phenotypicAnalysisTitle = new Label();
		this.phenotypicAnalysisTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		this.qtlAnalysisTitle = new Label();
		this.qtlAnalysisTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		this.singleSiteAnalysisTitle = new Label();
		this.singleSiteAnalysisTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		this.qtlSelectionTitle = new Label();
		this.qtlSelectionTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		this.recombinationCycleTitle = new Label();
		this.recombinationCycleTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		this.finalBreedingDecisionTitle = new Label();
		this.finalBreedingDecisionTitle.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);

		// project planning buttons
		this.browseGermplasmButton = new Button();
		this.browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.browseGermplasmButton.setSizeUndefined();

		this.browseStudiesButton = new Button();
		this.browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.browseStudiesButton.setSizeUndefined();

		this.browseGermplasmListsButton = new Button();
		this.browseGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.browseGermplasmListsButton.setSizeUndefined();

		this.browseGenotypingDataButton = new Button("Browse Genotyping Data");
		this.browseGenotypingDataButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.browseGenotypingDataButton.setSizeUndefined();
		this.browseGenotypingDataButton.setDescription("Click to launch genotyping data");

		// population development buttons
		this.breedingManagerButton = new Button();
		this.breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingManagerButton.setSizeUndefined();

		this.breedingPlannerButton = new Button(this.messageSource.getMessage(Message.BREEDING_PLANNER_MARS));
		this.breedingPlannerButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingPlannerButton.setSizeUndefined();
		this.breedingPlannerButton.setDescription("Click to launch the freestanding Breeding Planner application.");

		this.germplasmImportButton = new Button("IBFB Import Germplasm Lists");
		this.germplasmImportButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.germplasmImportButton.setSizeUndefined();
		this.germplasmImportButton.setDescription("Click to launch Fieldbook on Nursery Manager View.");

		this.germplasmImportButton2 = new Button("Import Germplasm Lists");
		this.germplasmImportButton2.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.germplasmImportButton2.setSizeUndefined();
		this.germplasmImportButton2.setDescription("Click to launch the Germplasm Import View.");

		// field trial management buttons
		this.fieldBookButton = new Button("Manage Trials");
		this.fieldBookButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.fieldBookButton.setDescription("Click to launch Fieldbook on Trial Manager View");
		this.fieldBookButton.setSizeUndefined();

		// genotyping buttons
		this.gdmsButton = new Button();
		this.gdmsButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.gdmsButton.setSizeUndefined();

		// phenotypic analysis buttons
		this.phenotypicBreedingViewButton = new Button();
		this.phenotypicBreedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.phenotypicBreedingViewButton.setSizeUndefined();

		// qtl analysis buttons
		this.qtlBreedingViewButton = new Button();
		this.qtlBreedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.qtlBreedingViewButton.setSizeUndefined();

		this.breedingViewSingleSiteAnalysisCentralButton = new Button("Single-Site Analysis for Central Datasets");
		this.breedingViewSingleSiteAnalysisCentralButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewSingleSiteAnalysisCentralButton.setSizeUndefined();
		this.breedingViewSingleSiteAnalysisCentralButton
				.setDescription("Click to launch Single-Site Analysis on Study Datasets from Central IBDB");

		this.breedingViewSingleSiteAnalysisLocalButton = new Button("Single-Site Analysis");
		this.breedingViewSingleSiteAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewSingleSiteAnalysisLocalButton.setSizeUndefined();
		this.breedingViewSingleSiteAnalysisLocalButton.setDescription("Click to launch Single-Site Analysis on Study Datasets");

		// recombination cycle selection buttons
		this.optimasButton = new Button();
		this.optimasButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.optimasButton.setSizeUndefined();

		this.recomBreedingManagerButton = new Button();
		this.recomBreedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.recomBreedingManagerButton.setSizeUndefined();

		this.manageGermplasmListsButton = new Button();
		this.manageGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.manageGermplasmListsButton.setSizeUndefined();

		this.breedingViewMultiSiteAnalysisButton = new Button();
		this.breedingViewMultiSiteAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingViewMultiSiteAnalysisButton.setSizeUndefined();

		this.makeCrossesButton = new Button();
		this.makeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.makeCrossesButton.setSizeUndefined();

		this.recomMakeCrossesButton = new Button();
		this.recomMakeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.recomMakeCrossesButton.setSizeUndefined();

		// arrows
		this.downArrow11 = new Embedded(null, new ThemeResource(MarsWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrow12 = new Embedded(null, new ThemeResource(MarsWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrow13 = new Embedded(null, new ThemeResource(MarsWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));

		this.downArrow21 = new Embedded(null, new ThemeResource(MarsWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));

		this.downArrow31 = new Embedded(null, new ThemeResource(MarsWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));
		this.downArrow32 = new Embedded(null, new ThemeResource(MarsWorkflowDiagram.DOWN_ARROW_THEME_RESOURCE));

		this.mainHeadToHeadButton = new Button(this.messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
		this.mainHeadToHeadButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.mainHeadToHeadButton.setSizeUndefined();
		this.mainHeadToHeadButton.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

		this.mainHeadToHeadButton2 = new Button(this.messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
		this.mainHeadToHeadButton2.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.mainHeadToHeadButton2.setSizeUndefined();
		this.mainHeadToHeadButton2.setDescription(this.messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

		this.queryForAdaptedGermplasmButton = new Button(this.messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
		this.queryForAdaptedGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.queryForAdaptedGermplasmButton.setSizeUndefined();
		this.queryForAdaptedGermplasmButton.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));

		this.queryForAdaptedGermplasmButton2 = new Button(this.messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
		this.queryForAdaptedGermplasmButton2.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.queryForAdaptedGermplasmButton2.setSizeUndefined();
		this.queryForAdaptedGermplasmButton2.setDescription(this.messageSource
				.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));

		this.breedingManagerListManager =
				new Button(this.messageSource.getMessage(Message.BREEDING_MANAGER_BROWSE_FOR_GERMPLASMS_AND_LISTS));
		this.breedingManagerListManager.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.breedingManagerListManager.setSizeUndefined();
		this.breedingManagerListManager.setDescription(this.messageSource.getMessage(Message.CLICK_TO_BROWSE_FOR_GERMPLASMS_AND_LISTS));

		this.ontologyBrowserFBBtn = new Button("Manage Ontologies");
		this.ontologyBrowserFBBtn.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.ontologyBrowserFBBtn.setSizeUndefined();
		this.ontologyBrowserFBBtn.setDescription("Click to launch Fieldbook on Ontology Browser view");

		this.metaAnalysisBtn = new Button("Meta Analysis of Field Trials");
		this.metaAnalysisBtn.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.metaAnalysisBtn.setSizeUndefined();
		this.metaAnalysisBtn.setDescription("Click to launch Meta Analysis of Field Trial Tool");

		this.metaAnalysisBtn2 = new Button("Meta Analysis of Field Trials");
		this.metaAnalysisBtn2.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);
		this.metaAnalysisBtn2.setSizeUndefined();
		this.metaAnalysisBtn2.setDescription("Click to launch Meta Analysis of Field Trial Tool");
	}

	protected void initializeLayout() {
		this.setSizeFull();
		this.setScrollable(true);
		this.setContent(this.layoutWorkflowArea());
	}

	protected ComponentContainer layoutWorkflowArea() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setHeight("1500px");

		Component breedingManagementArea = this.layoutBreedingManagementArea();
		breedingManagementArea.setHeight("100%");
		layout.addComponent(breedingManagementArea);

		Component markerTraitAnalysisArea = this.layoutMarkerTraitAnalysisArea();
		markerTraitAnalysisArea.setHeight("100%");
		layout.addComponent(markerTraitAnalysisArea);

		Component markerImplementationArea = this.layoutMarkerImplementationArea();
		markerImplementationArea.setHeight("100%");
		layout.addComponent(markerImplementationArea);

		final VerticalLayout rootContainer = new VerticalLayout();
		rootContainer.setSizeUndefined();
		rootContainer.setMargin(new Layout.MarginInfo(false, true, true, true));
		rootContainer.setSpacing(false);

		if (!this.workflowPreview) {
			Label header = new Label();
			header.setStyleName(Bootstrap.Typography.H1.styleName());
			header.setValue(this.role.getLabel());
			rootContainer.addComponent(header);

		}
		rootContainer.addComponent(layout);

		return rootContainer;
	}

	protected ComponentContainer layoutBreedingManagementArea() {
		VerticalLayout layout = new VerticalLayout();
		layout.setHeight(MarsWorkflowDiagram.COMPONENT_HEIGHT);
		layout.setMargin(new Layout.MarginInfo(true, false, false, false));
		layout.setSpacing(true);

		Component projectPlanningArea = this.layoutProjectPlanning();
		layout.addComponent(projectPlanningArea);

		layout.addComponent(this.downArrow11);
		layout.setComponentAlignment(this.downArrow11, Alignment.MIDDLE_CENTER);

		Component populationManagementArea = this.layoutPopulationDevelopment();
		layout.addComponent(populationManagementArea);

		layout.addComponent(this.downArrow12);
		layout.setComponentAlignment(this.downArrow12, Alignment.MIDDLE_CENTER);

		Component fieldTrialArea = this.layoutFieldTrialManagement();
		layout.addComponent(fieldTrialArea);

		layout.addComponent(this.downArrow13);
		layout.setComponentAlignment(this.downArrow13, Alignment.MIDDLE_CENTER);

		Component genotypingArea = this.layoutGenotyping();
		layout.addComponent(genotypingArea);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		return layout;
	}

	protected ComponentContainer layoutProjectPlanning() {
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

		layout.addComponent(this.breedingPlannerButton);
		layout.setComponentAlignment(this.breedingPlannerButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingPlannerButton, 0);

		layout.addComponent(this.browseStudiesButton);
		layout.setComponentAlignment(this.browseStudiesButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.browseStudiesButton, 0);

		layout.addComponent(this.browseGermplasmListsButton);
		this.browseGermplasmListsButton.setHeight("20px");
		layout.setComponentAlignment(this.browseGermplasmListsButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.browseGermplasmListsButton, 0);

		layout.addComponent(this.ontologyBrowserFBBtn);
		this.ontologyBrowserFBBtn.setHeight("20px");
		layout.setComponentAlignment(this.ontologyBrowserFBBtn, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.ontologyBrowserFBBtn, 0);

		layout.addComponent(this.browseGenotypingDataButton);
		layout.setComponentAlignment(this.browseGenotypingDataButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.browseGenotypingDataButton, 0);

		layout.addComponent(this.mainHeadToHeadButton2);
		layout.setComponentAlignment(this.mainHeadToHeadButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.mainHeadToHeadButton2, 0);

		layout.addComponent(this.queryForAdaptedGermplasmButton2);
		layout.setComponentAlignment(this.queryForAdaptedGermplasmButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.queryForAdaptedGermplasmButton2, 0);

		return layout;
	}

	protected ComponentContainer layoutPopulationDevelopment() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.populationDevelopmentTitle);
		layout.setComponentAlignment(this.populationDevelopmentTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.populationDevelopmentTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.germplasmImportButton2);
		layout.setComponentAlignment(this.germplasmImportButton2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.germplasmImportButton2, 0);

		layout.addComponent(this.makeCrossesButton);
		layout.setComponentAlignment(this.makeCrossesButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.makeCrossesButton, 0);

		layout.addComponent(this.breedingManagerButton);
		layout.setComponentAlignment(this.breedingManagerButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingManagerButton, 0);

		return layout;
	}

	protected ComponentContainer layoutFieldTrialManagement() {
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

		layout.addComponent(this.fieldBookButton);
		layout.setComponentAlignment(this.fieldBookButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.fieldBookButton, 0);

		return layout;
	}

	protected ComponentContainer layoutGenotyping() {
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

	protected ComponentContainer layoutMarkerTraitAnalysisArea() {
		VerticalLayout layout = new VerticalLayout();
		layout.setHeight(MarsWorkflowDiagram.COMPONENT_HEIGHT);
		layout.setMargin(new Layout.MarginInfo(true, false, false, false));
		layout.setSpacing(true);

		Component markerTraitAnalysisArea = this.layoutPhenotypicAnalysis();
		layout.addComponent(markerTraitAnalysisArea);

		layout.addComponent(this.downArrow21);
		layout.setComponentAlignment(this.downArrow21, Alignment.MIDDLE_CENTER);

		Component qtlAnalysisArea = this.layoutQtlAnalysis();
		layout.addComponent(qtlAnalysisArea);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		return layout;
	}

	protected ComponentContainer layoutPhenotypicAnalysis() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.phenotypicAnalysisTitle);
		layout.setComponentAlignment(this.phenotypicAnalysisTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.phenotypicAnalysisTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.breedingViewSingleSiteAnalysisLocalButton);
		layout.setComponentAlignment(this.breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewSingleSiteAnalysisLocalButton, 0);

		layout.addComponent(this.breedingViewMultiSiteAnalysisButton);
		layout.setComponentAlignment(this.breedingViewMultiSiteAnalysisButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewMultiSiteAnalysisButton, 0);

		layout.addComponent(this.metaAnalysisBtn2);
		layout.setComponentAlignment(this.metaAnalysisBtn2, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.metaAnalysisBtn2, 0);

		layout.addComponent(this.phenotypicBreedingViewButton);
		layout.setComponentAlignment(this.phenotypicBreedingViewButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.phenotypicBreedingViewButton, 0);

		return layout;
	}

	protected ComponentContainer layoutQtlAnalysis() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.qtlAnalysisTitle);
		layout.setComponentAlignment(this.qtlAnalysisTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.qtlAnalysisTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.qtlBreedingViewButton);
		layout.setComponentAlignment(this.qtlBreedingViewButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.qtlBreedingViewButton, 0);

		return layout;
	}

	protected ComponentContainer layoutSingleSiteAnalysis() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.singleSiteAnalysisTitle);
		layout.setComponentAlignment(this.singleSiteAnalysisTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.singleSiteAnalysisTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.breedingViewSingleSiteAnalysisLocalButton);
		layout.setComponentAlignment(this.breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewSingleSiteAnalysisLocalButton, 0);

		layout.addComponent(this.breedingViewSingleSiteAnalysisCentralButton);
		layout.setComponentAlignment(this.breedingViewSingleSiteAnalysisCentralButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.breedingViewSingleSiteAnalysisCentralButton, 0);

		layout.addComponent(this.metaAnalysisBtn);
		layout.setComponentAlignment(this.metaAnalysisBtn, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.metaAnalysisBtn, 0);

		return layout;
	}

	protected Component layoutMarkerImplementationArea() {
		VerticalLayout layout = new VerticalLayout();
		layout.setHeight(MarsWorkflowDiagram.COMPONENT_HEIGHT);
		layout.setMargin(new Layout.MarginInfo(true, false, false, false));
		layout.setSpacing(true);

		Component qtlSelectionArea = this.layoutQtlSelection();
		layout.addComponent(qtlSelectionArea);

		layout.addComponent(this.downArrow31);
		layout.setComponentAlignment(this.downArrow31, Alignment.MIDDLE_CENTER);

		Component recombinationCycleArea = this.layoutRecombinationCycle();
		layout.addComponent(recombinationCycleArea);

		layout.addComponent(this.downArrow32);
		layout.setComponentAlignment(this.downArrow32, Alignment.MIDDLE_CENTER);

		Component finalBreedingDecisionArea = this.layoutFinalBreedingDecision();
		layout.addComponent(finalBreedingDecisionArea);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		return layout;
	}

	protected ComponentContainer layoutQtlSelection() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.qtlSelectionTitle);
		layout.setComponentAlignment(this.qtlSelectionTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.qtlSelectionTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		return layout;
	}

	protected ComponentContainer layoutRecombinationCycle() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.recombinationCycleTitle);
		layout.setComponentAlignment(this.recombinationCycleTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.recombinationCycleTitle, 0);

		Label emptyLabel = new Label(" ");
		emptyLabel.setWidth("100%");
		emptyLabel.setHeight("20px");
		layout.addComponent(emptyLabel);
		layout.setExpandRatio(emptyLabel, 100);

		layout.addComponent(this.optimasButton);
		layout.setComponentAlignment(this.optimasButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.optimasButton, 0);

		layout.addComponent(this.recomMakeCrossesButton);
		layout.setComponentAlignment(this.recomMakeCrossesButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.recomMakeCrossesButton, 0);

		layout.addComponent(this.recomBreedingManagerButton);
		layout.setComponentAlignment(this.recomBreedingManagerButton, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.recomBreedingManagerButton, 0);

		return layout;
	}

	protected ComponentContainer layoutFinalBreedingDecision() {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		layout.addComponent(this.finalBreedingDecisionTitle);
		layout.setComponentAlignment(this.finalBreedingDecisionTitle, Alignment.TOP_CENTER);
		layout.setExpandRatio(this.finalBreedingDecisionTitle, 0);

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

	protected ComponentContainer createPanel(String caption, String... buttonCaptions) {
		VerticalLayout layout = new VerticalLayout();
		this.configureWorkflowStepLayout(layout);

		Label titleLabel = new Label(caption);
		titleLabel.setStyleName(MarsWorkflowDiagram.GCP_SECTION_TITLE);
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
			button.setStyleName(BaseTheme.BUTTON_LINK + MarsWorkflowDiagram.GCP_WORKFLOW_LINK);

			layout.addComponent(button);
			layout.setComponentAlignment(button, Alignment.TOP_CENTER);
			layout.setExpandRatio(button, 0);
		}

		return layout;
	}

	protected void configureWorkflowStepLayout(VerticalLayout layout) {
		layout.setWidth("280px");
		layout.setHeight("215px");
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

			this.browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_BROWSER));
			this.browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
			this.browseGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN));

			this.browseGenotypingDataButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));

			this.gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));

			this.fieldBookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB));

			this.optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));
			this.breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));

			this.recomBreedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));

			this.phenotypicBreedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));
			this.qtlBreedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));

			this.breedingViewSingleSiteAnalysisCentralButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW, this.project,
					WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));
			this.breedingViewSingleSiteAnalysisLocalButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_VIEW, this.project,
					WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));

			this.manageGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));

			this.breedingViewMultiSiteAnalysisButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_GXE, this.project, null));

			this.makeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));
			this.recomMakeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));

			this.queryForAdaptedGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
			this.queryForAdaptedGermplasmButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
			this.breedingManagerListManager.addListener(new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN));

			this.ontologyBrowserFBBtn.addListener(new LaunchWorkbenchToolAction(ToolEnum.ONTOLOGY_BROWSER_FIELDBOOK_WEB));
			this.metaAnalysisBtn.addListener(new ChangeWindowAction(WindowEnums.BV_META_ANALYSIS, this.project, null));
			this.metaAnalysisBtn2.addListener(new ChangeWindowAction(WindowEnums.BV_META_ANALYSIS, this.project, null));
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
			this.messageSource.setValue(this.dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "MARS");
		} else {
			this.messageSource.setValue(this.dashboardTitle, Message.PROJECT_TITLE, this.project.getProjectName());
		}

		// titles
		this.messageSource.setValue(this.projectPlanningTitle, Message.PROJECT_PLANNING);
		this.messageSource.setValue(this.populationDevelopmentTitle, Message.POPULATION_DEVELOPMENT);
		this.messageSource.setValue(this.fieldTrialManagementTitle, Message.FIELD_TRIAL_MANAGEMENT);
		this.messageSource.setValue(this.genotypingTitle, Message.GENOTYPING);

		this.messageSource.setValue(this.phenotypicAnalysisTitle, Message.PHENOTYPIC_ANALYSIS);
		this.messageSource.setValue(this.qtlAnalysisTitle, Message.QTL_ANALYSIS);

		this.messageSource.setValue(this.qtlSelectionTitle, Message.QTL_SELECTION);

		this.messageSource.setValue(this.recombinationCycleTitle, Message.RECOMBINATION_CYCLE);

		this.messageSource.setValue(this.finalBreedingDecisionTitle, Message.FINAL_BREEDING_DECISION);

		// buttons
		this.messageSource.setCaption(this.browseGermplasmButton, Message.BROWSE_GERMPLASM_INFORMATION);
		this.messageSource.setDescription(this.browseGermplasmButton, Message.CLICK_TO_LAUNCH_GERMPLASM_BROWSER);

		this.messageSource.setCaption(this.browseStudiesButton, Message.BROWSE_STUDIES_AND_DATASETS);
		this.messageSource.setDescription(this.browseStudiesButton, Message.CLICK_TO_LAUNCH_STUDY_BROWSER);

		this.messageSource.setCaption(this.browseGermplasmListsButton, Message.BROWSE_GERMPLAM_LISTS);
		this.messageSource.setDescription(this.browseGermplasmListsButton, Message.CLICK_TO_LAUNCH_GERMPLASM_LIST_BROWSER);

		this.messageSource.setCaption(this.breedingManagerButton, Message.BREEDING_MANAGER);
		this.messageSource.setDescription(this.breedingManagerButton, Message.CLICK_TO_LAUNCH_BREEDING_MANAGER);

		this.messageSource.setCaption(this.recomBreedingManagerButton, Message.BREEDING_MANAGER);
		this.messageSource.setDescription(this.recomBreedingManagerButton, Message.CLICK_TO_LAUNCH_BREEDING_MANAGER);

		this.messageSource.setCaption(this.gdmsButton, Message.MANAGE_GENOTYPING_DATA);
		this.messageSource.setDescription(this.gdmsButton, Message.CLICK_TO_LAUNCH_GDMS);

		this.messageSource.setCaption(this.phenotypicBreedingViewButton, Message.BREEDING_VIEW);
		this.messageSource.setDescription(this.phenotypicBreedingViewButton, Message.CLICK_TO_LAUNCH_BREEDING_VIEW);

		this.messageSource.setCaption(this.qtlBreedingViewButton, Message.BREEDING_VIEW);
		this.messageSource.setDescription(this.qtlBreedingViewButton, Message.CLICK_TO_LAUNCH_BREEDING_VIEW);

		this.messageSource.setCaption(this.optimasButton, Message.OPTIMAS_MARS);
		this.messageSource.setDescription(this.optimasButton, Message.CLICK_TO_LAUNCH_OPTIMAS);

		this.messageSource.setCaption(this.breedingViewMultiSiteAnalysisButton, Message.MULTI_SITE_ANALYSIS_LINK);

		this.messageSource.setCaption(this.manageGermplasmListsButton, Message.LIST_MANAGER);
		this.messageSource.setDescription(this.manageGermplasmListsButton, Message.CLICK_TO_LAUNCH_LIST_MANAGER);

		this.messageSource.setCaption(this.makeCrossesButton, Message.MAKE_CROSSES);
		this.messageSource.setDescription(this.makeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);

		this.messageSource.setCaption(this.recomMakeCrossesButton, Message.MAKE_CROSSES);
		this.messageSource.setDescription(this.recomMakeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);

		this.messageSource.setCaption(this.makeCrossesButton, Message.MAKE_CROSSES);
		this.messageSource.setDescription(this.makeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);

		this.messageSource.setCaption(this.recomMakeCrossesButton, Message.MAKE_CROSSES);
		this.messageSource.setDescription(this.recomMakeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);

		this.messageSource.setCaption(this.queryForAdaptedGermplasmButton, Message.QUERY_FOR_ADAPTED_GERMPLASM);
		this.messageSource.setDescription(this.queryForAdaptedGermplasmButton, Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM);

		this.messageSource.setCaption(this.queryForAdaptedGermplasmButton2, Message.QUERY_FOR_ADAPTED_GERMPLASM);
		this.messageSource.setDescription(this.queryForAdaptedGermplasmButton2, Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM);
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
