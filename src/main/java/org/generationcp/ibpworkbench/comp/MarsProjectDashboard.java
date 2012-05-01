package org.generationcp.ibpworkbench.comp;

import org.generationcp.ibpworkbench.actions.FieldBookUploadSucceededListener;
import org.generationcp.ibpworkbench.actions.FileUploadFailedListener;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction.ToolId;
import org.generationcp.ibpworkbench.comp.window.FileUploadWindow;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.comp.window.QtlAnalysisWindow;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class MarsProjectDashboard extends VerticalLayout {
    private static final long serialVersionUID = 1L;
    
    private Project project;

    private Label dashboardTitle;

    // Breeding Management controls
    private Label breedingManagementTitle;
    
    private Label fieldTrialManagementTitle;
    //private Link fieldBookLink;
    private Button fieldBookButton;
    private Button uploadFieldBookDataButton;
    
    private Label populationManagementTitle;
    private Button browseGermplasmButton;
    private Button retrieveGermplasmPhenotypicButton;
    private Button gdmsButton;
    
    // Marker Trait Analysis controls
    private Label markerTraitAnalysisTitle;
    
    private Button qtlAnalysisButton;

    // Marker Implementation controls
    private Label markerImplementationTitle;

    public MarsProjectDashboard(Project project) {
        this.project = project;
        
        assemble();
    }
    
    protected void initializeComponents() {
        // dashboard title
        dashboardTitle = new Label("Project: " + project.getProjectName());
        dashboardTitle.setStyleName("gcp-content-title");
        
        // breeding management
        breedingManagementTitle = new Label("Breeding Management");
        breedingManagementTitle.setStyleName("gcp-section-title");
        
        fieldTrialManagementTitle = new Label("Field Trial Management");
        fieldTrialManagementTitle.setStyleName("gcp-section-title");
        fieldTrialManagementTitle.setSizeUndefined();
        
//        fieldBookLink = new Link("Field Book", new ExternalResource("http://localhost:10080/ibfb/master.jnlp"));
//        fieldBookLink.setSizeUndefined();
        
        fieldBookButton = new Button("Field Book");
        fieldBookButton.setStyleName(BaseTheme.BUTTON_LINK);
        fieldBookButton.setSizeUndefined();
        
        uploadFieldBookDataButton = new Button("Upload Field Book Data");
        uploadFieldBookDataButton.setStyleName(BaseTheme.BUTTON_LINK);
        uploadFieldBookDataButton.setSizeUndefined();
        
        populationManagementTitle = new Label("Population Management");
        populationManagementTitle.setStyleName("gcp-section-title");
        populationManagementTitle.setSizeUndefined();
        
        browseGermplasmButton = new Button("Browse Germplasm Information");
        browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseGermplasmButton.setSizeUndefined();
        
        retrieveGermplasmPhenotypicButton = new Button("Retrieve Germplasm by Phenotypic Data");
        retrieveGermplasmPhenotypicButton.setStyleName(BaseTheme.BUTTON_LINK);
        retrieveGermplasmPhenotypicButton.setSizeUndefined();
        
        gdmsButton = new Button("GDMS");
        gdmsButton.setStyleName(BaseTheme.BUTTON_LINK);
        gdmsButton.setSizeUndefined();
        
        // marker trait analysis
        markerTraitAnalysisTitle = new Label("Marker Trait Analysis");
        markerTraitAnalysisTitle.setStyleName("gcp-section-title");
        
        // marker implementation
        markerImplementationTitle = new Label("Marker Implementation");
        markerImplementationTitle.setStyleName("gcp-section-title");
    }
    
    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        
        dashboardTitle.setSizeUndefined();
        addComponent(dashboardTitle);
        
        Component workFlowArea = layoutWorkflowArea();
        workFlowArea.setSizeUndefined();
        addComponent(workFlowArea);
    }
    
    protected Component layoutWorkflowArea() {
        Panel panel = new Panel();
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        
        Component breedingManagementArea = layoutBreedingManagementArea();
        breedingManagementArea.setHeight("100%");
        layout.addComponent(breedingManagementArea);
        
        Component markerTraitAnalysisArea = layoutMarkerTraitAnalysis();
        markerTraitAnalysisArea.setHeight("100%");
        layout.addComponent(markerTraitAnalysisArea);
        
        Component markerImplementationArea = layoutMarkerImplementation();
        markerImplementationArea.setHeight("100%");
        layout.addComponent(markerImplementationArea);
        
        panel.setContent(layout);
        return panel;
    }
    
    protected Component layoutBreedingManagementArea() {
        Panel panel = new Panel();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("400px");
        layout.setMargin(true);
        layout.setSpacing(true);
        
        breedingManagementTitle.setSizeUndefined();
        layout.addComponent(breedingManagementTitle);
        layout.setComponentAlignment(breedingManagementTitle, Alignment.TOP_CENTER);
        
        Component genotypingArea = createPanel("Genotyping", "Lab Book");
        layoutPanel(genotypingArea);
        layout.addComponent(genotypingArea);
        
        Component fieldTrialArea = layoutFieldTrialManagement();
        layoutPanel(fieldTrialArea);
        layout.addComponent(fieldTrialArea);
        
        Component populationManagementArea = layoutPopulationManagement();
        layoutPanel(populationManagementArea);
        layout.addComponent(populationManagementArea);
        
        Component projectPlanningArea = createPanel("Project Planning");
        layoutPanel(projectPlanningArea);
        layout.addComponent(projectPlanningArea);
        
        panel.setContent(layout);
        return panel;
    }
    
    protected Component layoutPopulationManagement() {
        Panel panel = new Panel();

        VerticalLayout layout = new VerticalLayout();

        layout.addComponent(populationManagementTitle);
        layout.setComponentAlignment(populationManagementTitle, Alignment.TOP_CENTER);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);

        layout.addComponent(browseGermplasmButton);
        layout.setComponentAlignment(browseGermplasmButton, Alignment.MIDDLE_CENTER);

        layout.addComponent(retrieveGermplasmPhenotypicButton);
        layout.setComponentAlignment(retrieveGermplasmPhenotypicButton, Alignment.MIDDLE_CENTER);

        layout.addComponent(gdmsButton);
        layout.setComponentAlignment(gdmsButton, Alignment.MIDDLE_CENTER);

        panel.setContent(layout);
        return panel;
    }
    
    protected Component layoutFieldTrialManagement() {
        Panel panel = new Panel();

        VerticalLayout layout = new VerticalLayout();

        layout.addComponent(fieldTrialManagementTitle);
        layout.setComponentAlignment(fieldTrialManagementTitle, Alignment.TOP_CENTER);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);

        layout.addComponent(fieldBookButton);
        layout.setComponentAlignment(fieldBookButton, Alignment.MIDDLE_CENTER);

        layout.addComponent(uploadFieldBookDataButton);
        layout.setComponentAlignment(uploadFieldBookDataButton, Alignment.MIDDLE_CENTER);
        
        panel.setContent(layout);
        return panel;
    }
    
    protected Component layoutMarkerTraitAnalysis() {
        Panel panel = new Panel();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("400px");
        layout.setMargin(true);
        layout.setSpacing(true);
        
        markerTraitAnalysisTitle.setSizeUndefined();
        layout.addComponent(markerTraitAnalysisTitle);
        layout.setComponentAlignment(markerTraitAnalysisTitle, Alignment.TOP_CENTER);
        
//        Component loadDataSetsArea = createPanel("Load Datasets", "Analytical Pipeline");
//        layoutPanel(loadDataSetsArea);
//        layout.addComponent(loadDataSetsArea);
        
//        Component phenotypicAnalysisArea = createPanel("Phenotypic Analysis");
//        layoutPanel(phenotypicAnalysisArea);
//        layout.addComponent(phenotypicAnalysisArea);
//        
//        Component genotypicAnalysisArea = createPanel("Genotypic Analysis");
//        layoutPanel(genotypicAnalysisArea);
//        layout.addComponent(genotypicAnalysisArea);
        
        Component qtlAnalysisArea = layoutQtlAnalysisArea();
        layoutPanel(qtlAnalysisArea);
        layout.addComponent(qtlAnalysisArea);
        layout.setExpandRatio(qtlAnalysisArea, 1.0f);
        
        panel.setContent(layout);
        return panel;
    }
    
    protected Component layoutQtlAnalysisArea() {
        Panel panel = new Panel();

        VerticalLayout layout = new VerticalLayout();

        Label titleLabel = new Label("QTL Analysis");
        titleLabel.setStyleName("gcp-section-title");
        titleLabel.setSizeUndefined();

        layout.addComponent(titleLabel);
        layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);

        
        qtlAnalysisButton = new Button("Analytical Pipeline");
        qtlAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK);

        layout.addComponent(qtlAnalysisButton);
        layout.setComponentAlignment(qtlAnalysisButton, Alignment.MIDDLE_CENTER);

        panel.setContent(layout);
        return panel;
    }
    
    protected Component layoutMarkerImplementation() {
        Panel panel = new Panel();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("400px");
        layout.setMargin(true);
        layout.setSpacing(true);
        
        markerImplementationTitle.setSizeUndefined();
        layout.addComponent(markerImplementationTitle);
        layout.setComponentAlignment(markerImplementationTitle, Alignment.TOP_CENTER);
        
        Component ideotypeDesignArea = createPanel("Ideotype Design", "Selection Index");
        layoutPanel(ideotypeDesignArea);
        layout.addComponent(ideotypeDesignArea);
        
        Component plantSelectionArea = createPanel("Plant Selection", "OptiMAS");
        layoutPanel(plantSelectionArea);
        layout.addComponent(plantSelectionArea);
        
        Component populationArea = createPanel("Population", "Breeding Manager");
        layoutPanel(populationArea);
        layout.addComponent(populationArea);
        
        Component genotypingArea = createPanel("Genotyping", "Lab Book");
        layoutPanel(genotypingArea);
        layout.addComponent(genotypingArea);
        
        Component projectCompletionArea = createPanel("Project Completion");
        layoutPanel(projectCompletionArea);
        layout.addComponent(projectCompletionArea);
        
        panel.setContent(layout);
        return panel;
    }
    
    protected Panel createPanel(String caption, String... buttonCaptions) {
        Panel panel = new Panel();

        VerticalLayout layout = new VerticalLayout();

        Label titleLabel = new Label(caption);
        titleLabel.setStyleName("gcp-section-title");
        titleLabel.setSizeUndefined();

        layout.addComponent(titleLabel);
        layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);

        for (String buttonCaption : buttonCaptions) {
            Button button = new Button(buttonCaption);
            button.setStyleName(BaseTheme.BUTTON_LINK);

            layout.addComponent(button);
            layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
        }

        panel.setContent(layout);
        return panel;
    }
    
    protected void layoutButton(Button button) {
        button.setWidth("180px");
    }
    
    protected void layoutPanel(Component component) {
        component.setWidth("250px");
        //component.setHeight("80px");
    }
    
    protected void initializeActions() {
        uploadFieldBookDataButton.addListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                FileUploadWindow fileUploadWindow = new FileUploadWindow();
                fileUploadWindow.setWidth("380px");
                fileUploadWindow.setHeight("240px");
                fileUploadWindow.setModal(true);
                
                // set allowed mime types
                fileUploadWindow.getUpload().addAllowedMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                fileUploadWindow.getUpload().addAllowedMimeType("application/vnd.ms-excel");
                
                // set the upload listeners
                fileUploadWindow.getUpload().addListener(new FieldBookUploadSucceededListener(fileUploadWindow));
                fileUploadWindow.getUpload().addListener(new FileUploadFailedListener());
                
                getWindow().addWindow(fileUploadWindow);
                fileUploadWindow.center();
            }
        });
        
        browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolId.GERMPLASM_BROWSER));
        retrieveGermplasmPhenotypicButton.addListener(new LaunchWorkbenchToolAction(ToolId.GERMPLASM_PHENOTYPIC));
        
        gdmsButton.addListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                Embedded browser = new Embedded("", new ExternalResource("http://localhost:8080/ibpworkbench/VAADIN/themes/gcp-default/layouts/load_gdms.html"));
                browser.setType(Embedded.TYPE_BROWSER);
                browser.setSizeFull();
                
                IContentWindow contentWindow = (IContentWindow) getWindow();
                contentWindow.showContent(browser);
            }
        });
        
        fieldBookButton.addListener(new LaunchWorkbenchToolAction(ToolId.FIELDBOOK));
        
        qtlAnalysisButton.addListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                QtlAnalysisWindow window = new QtlAnalysisWindow();
                window.setModal(true);
                
                getWindow().addWindow(window);
                
                window.center();
            }
        });
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}
