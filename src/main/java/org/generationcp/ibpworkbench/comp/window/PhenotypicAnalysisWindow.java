/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.ibpworkbench.comp.window;

import org.generationcp.ibpworkbench.actions.CloseWindowAction;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class PhenotypicAnalysisWindow extends Window {
    private static final long serialVersionUID = 1L;
    
    private Button runButton;
    private Button cancelButton;

    public PhenotypicAnalysisWindow() {
        super("Phenotypic Analysis");
        
        assemble();
    }
    
    protected void initializeComponents() {
        runButton = new Button("Run");
        
        cancelButton = new Button("Cancel");
        cancelButton.setStyleName(BaseTheme.BUTTON_LINK);
    }
    
    protected void initializeLayout() {
        setWidth("320px");
        setHeight("240px");
        
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);
        
        Component contentArea = layoutContentArea();
        contentArea.setSizeFull();
        layout.addComponent(contentArea);
        layout.setExpandRatio(contentArea, 1.0f);
        
        Component buttonArea = layoutButtonArea();
        layout.addComponent(buttonArea);
        layout.setComponentAlignment(buttonArea, Alignment.BOTTOM_CENTER);
        
        setContent(layout);
    }
    
    protected Component layoutContentArea() {
        Panel panel = new Panel();
        
        return panel;
    }
    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
//        buttonLayout.setMargin(false, false, true, false);
        
        buttonLayout.addComponent(runButton);
        
        buttonLayout.addComponent(cancelButton);
        buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        
        return buttonLayout;
    }
    
    protected void initializeActions() {
        cancelButton.addListener(new CloseWindowAction());
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}
