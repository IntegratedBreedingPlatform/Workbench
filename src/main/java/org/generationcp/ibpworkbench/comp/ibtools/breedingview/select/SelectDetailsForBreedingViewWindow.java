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

package org.generationcp.ibpworkbench.comp.ibtools.breedingview.select;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CancelDetailsAsInputForBreedingViewAction;
import org.generationcp.ibpworkbench.actions.RunBreedingViewAction;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.mysql.jdbc.StringUtils;
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
    
    private Label lblVersion;
    private Label lblEnvironment;
    private Label lblBlocks;
    private Label lblReplicates;
    private Label lblDesignType;
    private Label valVersion;
    private Label valEnvironment;
    private Label valBlocks;
    private Label valReplicates;
    private Label valDesignType;
    private Button btnRun;
    private Button btnCancel;
    private TextField txtVersion;
    private TextField txtEnvironment;
    private TextField txtBlocks;
    private TextField txtReplicates;
    private TextField txtDesignType;
    private Select selDesignType;
    private Component buttonArea;
    
    private BreedingViewInput breedingViewInput;
    private Tool tool;
    
    private VerticalLayout generalLayout;
    private GridLayout entryLayout;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public SelectDetailsForBreedingViewWindow(Tool tool, BreedingViewInput breedingViewInput) {

        this.tool = tool;
        this.breedingViewInput = breedingViewInput;
        
        setModal(true);

       /* Make the sub window 50% the size of the browser window */
        setWidth("40%");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();
        

        setCaption("Enter Breeding View Project Details: ");
        
    }
    

    public Tool getTool() {
        return tool;
    }

    public TextField getTxtVersion() {
        return txtVersion;
    }

    public TextField getTxtEnvironment() {
        return txtEnvironment;
    }
    
    public TextField getTxtBlocks() {
        return txtBlocks;
    }

    public TextField getTxtReplicates() {
        return txtReplicates;
    }

    public TextField getTxtDesignType() {
        return txtDesignType;
    }

    public Select getSelDesignType() {
        return selDesignType;
    }
    
    public BreedingViewInput getBreedingViewInput() {
        return breedingViewInput;
    }

    protected void initialize() {
    }

    protected void initializeComponents() {
        
        generalLayout = new VerticalLayout();
        
        lblVersion = new Label();
        lblEnvironment = new Label();
        lblBlocks = new Label("Blocks");
        lblReplicates = new Label("Replicates");
        lblDesignType = new Label();
        
        txtVersion = new TextField();
        txtVersion.setNullRepresentation("");
        
        if (!StringUtils.isNullOrEmpty(breedingViewInput.getVersion())) {
            
            txtVersion.setValue(breedingViewInput.getVersion());
            txtVersion.setReadOnly(true);
            txtVersion.setRequired(false);
            
        } else {
            
            txtVersion.setNullSettingAllowed(false);
            txtVersion.setRequired(true);
            
        }
        
        txtEnvironment = new TextField();
        txtEnvironment.setNullRepresentation("");
        
        if (!StringUtils.isNullOrEmpty(breedingViewInput.getEnvironmentName())) {
            
            txtEnvironment.setValue(breedingViewInput.getEnvironmentName());
            txtEnvironment.setReadOnly(true);
            txtEnvironment.setRequired(false);
            
        } else {
            
            txtEnvironment.setNullSettingAllowed(false);
            txtEnvironment.setRequired(true);
            
        }
        
        txtBlocks = new TextField();
        txtBlocks.setNullRepresentation("");
        
        String blockName = null;
        
        if (breedingViewInput.getBlocks() != null) {
        
            blockName = breedingViewInput.getBlocks().getName();
        
        }
        
        if (!StringUtils.isNullOrEmpty(blockName)) {
            
            txtBlocks.setValue(blockName);
            txtBlocks.setReadOnly(true);
            txtBlocks.setRequired(false);
            
        } else {
            
            txtBlocks.setNullSettingAllowed(false);
            txtBlocks.setRequired(true);
            
        }
        
        txtReplicates = new TextField();
        txtReplicates.setNullRepresentation("");
        
        String replicateName = null;
        
        if (breedingViewInput.getReplicates() != null) {
        
            replicateName = breedingViewInput.getReplicates().getName();
        
        }
        
        if (!StringUtils.isNullOrEmpty(replicateName)) {
            
            txtReplicates.setValue(replicateName);
            txtReplicates.setReadOnly(true);
            txtReplicates.setRequired(false);
            
        } else {
            
            txtReplicates.setNullSettingAllowed(false);
            txtReplicates.setRequired(true);
            
        }
        
        entryLayout = new GridLayout(4, 6);
        
        if (!StringUtils.isNullOrEmpty(breedingViewInput.getDesignType())) {
            
            txtDesignType = new TextField();
            txtDesignType.setValue(breedingViewInput.getDesignType());
            txtDesignType.setReadOnly(true);
            txtDesignType.setRequired(false);
            entryLayout.addComponent(txtDesignType, 0, 5, 1, 5);
            
        } else {
            
            selDesignType = new Select();
            selDesignType.setImmediate(true); 
            selDesignType.addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
            selDesignType.addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
            selDesignType.addItem(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
            selDesignType.setNullSelectionAllowed(false);
            selDesignType.setNewItemsAllowed(false);
            entryLayout.addComponent(selDesignType, 0, 5, 1, 5);
            
        }
        
        btnRun = new Button();
        btnCancel = new Button();

        entryLayout.addComponent(lblEnvironment, 0, 0, 1, 0);
        entryLayout.addComponent(txtEnvironment, 0, 1, 1, 1);
        entryLayout.addComponent(lblVersion, 2, 0, 3, 0);
        entryLayout.addComponent(txtVersion, 2, 1, 3, 1);
        entryLayout.addComponent(lblBlocks, 0, 2, 1, 2);
        entryLayout.addComponent(txtBlocks, 0, 3, 1, 3);
        entryLayout.addComponent(lblReplicates, 2, 2, 3, 2);
        entryLayout.addComponent(txtReplicates, 2, 3, 3, 3);
        entryLayout.addComponent(lblDesignType, 0, 4, 1, 4);
        
        buttonArea = layoutButtonArea();
        
        generalLayout.addComponent(entryLayout);
        generalLayout.addComponent(buttonArea);
        
        setContent(generalLayout);
    }

    protected void initializeLayout() {
        
        generalLayout.setMargin(true);
        generalLayout.setWidth("100%");
        
        entryLayout.setMargin(true, false, true, false);
        entryLayout.setWidth("100%");
        entryLayout.setSpacing(true);
        //entryLayout.setWidth("250px");

        selDesignType.setWidth("100%");
    
        
    }

    protected void initializeActions() {
       btnCancel.addListener(new CancelDetailsAsInputForBreedingViewAction(this));
       btnRun.addListener(new RunBreedingViewAction(this));
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
        btnRun = new Button();

        buttonLayout.addComponent(btnCancel);
        buttonLayout.addComponent(btnRun);

        return buttonLayout;
    }
    
    @Override
    public void afterPropertiesSet() {
        assemble();
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    @Override
    public void updateLabels() {
        messageSource.setValue(lblVersion, Message.VERSION);
        messageSource.setValue(lblEnvironment, Message.ENVIRONMENT);
        messageSource.setValue(lblDesignType, Message.DESIGN_TYPE);
        messageSource.setCaption(btnRun, Message.RUN_BREEDING_VIEW);
        messageSource.setCaption(btnCancel, Message.CANCEL);
    }

}
