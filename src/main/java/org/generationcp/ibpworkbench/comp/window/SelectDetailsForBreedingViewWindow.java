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

package org.generationcp.ibpworkbench.comp.window;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CancelDetailsAsInputForBreedingViewAction;
import org.generationcp.ibpworkbench.actions.OpenAndSaveFromBreedingViewAction;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class SelectDetailsForBreedingViewWindow extends Window implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private Label lblName;
    private Label lblVersion;
    private Label lblEnvironment;
    private Label lblProjectType;
    private Label lblDesignType;
    private Button btnAccept;
    private Button btnCancel;
    private TextField txtName;
    private TextField txtVersion;
    private TextField txtEnvironment;
    private Select selProjectType;
    private Select selDesignType;
    private Component buttonArea;
    private Project project;
    
    private VerticalLayout generalLayout;
    private GridLayout entryLayout;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public SelectDetailsForBreedingViewWindow(Project project) {

        this.project = project;
        
        setModal(true);

       /* Make the sub window 50% the size of the browser window */
        setWidth("50%");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();
        

        setCaption("Breeding View Entry Form: ");
        
    }
    
    public TextField getTxtName() {
        return txtName;
    }

    public TextField getTxtVersion() {
        return txtVersion;
    }

    public TextField getTxtEnvironment() {
        return txtEnvironment;
    }

    public Select getSelProjectType() {
        return selProjectType;
    }

    public Select getSelDesignType() {
        return selDesignType;
    }
    
    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    protected void initialize() {
    }

    protected void initializeComponents() {
        
        generalLayout = new VerticalLayout();
        
        lblName = new Label();
        //lblName.setStyleName("gcp-content-title");
        lblVersion = new Label();
        //lblVersion.setStyleName("gcp-content-title");
        lblEnvironment = new Label();
        //lblEnvironment.setStyleName("gcp-content-title");
        lblProjectType = new Label();
        //lblProjectType.setStyleName("gcp-content-title");
        lblDesignType = new Label();
        //lblDesignType.setStyleName("gcp-content-title");
        
        txtName = new TextField();
        txtVersion = new TextField();
        txtEnvironment = new TextField();
        
        selProjectType = new Select();
        selProjectType.setImmediate(true); 
        selProjectType.addItem(ProjectType.FIELD_TRIAL.getName());
        
        selDesignType = new Select();
        selDesignType.setImmediate(true); 
        selDesignType.addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
        selDesignType.addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
        selDesignType.addItem(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
        
        btnAccept = new Button();
        btnCancel = new Button();
        
        entryLayout = new GridLayout(8, 7);
        entryLayout.setMargin(true, false, false, false);
        entryLayout.setWidth("250px");
        entryLayout.addComponent(lblName, 2, 0, 3, 0);
        entryLayout.addComponent(txtName, 2, 1, 3, 1);
        entryLayout.addComponent(lblVersion, 4, 0, 5, 0);
        entryLayout.addComponent(txtVersion, 4, 1, 5, 1);
        entryLayout.addComponent(lblEnvironment, 2, 2, 3, 2);
        entryLayout.addComponent(txtEnvironment, 2, 3, 3, 3);
        entryLayout.addComponent(lblProjectType, 2, 4, 3, 4);
        entryLayout.addComponent(selProjectType, 2, 5, 3, 5);
        entryLayout.addComponent(lblDesignType, 4, 4, 5, 4);
        entryLayout.addComponent(selDesignType, 4, 5, 5, 5);
        
        buttonArea = layoutButtonArea();
        
        generalLayout.addComponent(entryLayout);
        generalLayout.addComponent(buttonArea);
        
        setContent(generalLayout);
    }

    protected void initializeLayout() {
        
        generalLayout.setMargin(true);
        
        entryLayout.setWidth("100%");
        
    }

    protected void initializeActions() {
       btnCancel.addListener(new CancelDetailsAsInputForBreedingViewAction(this));
       btnAccept.addListener(new OpenAndSaveFromBreedingViewAction(project, this));
    }

    protected void assemble() {
        initialize();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        btnCancel = new Button();
        btnAccept = new Button();

        buttonLayout.addComponent(btnCancel);
        buttonLayout.addComponent(btnAccept);

        return buttonLayout;
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    @Override
    public void updateLabels() {
        messageSource.setValue(lblName, Message.NAME);
        messageSource.setValue(lblVersion, Message.VERSION);
        messageSource.setValue(lblEnvironment, Message.ENVIRONMENT);
        messageSource.setValue(lblProjectType, Message.PROJECT_TYPE);
        messageSource.setValue(lblDesignType, Message.DESIGN_TYPE);
        messageSource.setCaption(btnAccept, Message.NEXT);
        messageSource.setCaption(btnCancel, Message.CANCEL);
    }
}
