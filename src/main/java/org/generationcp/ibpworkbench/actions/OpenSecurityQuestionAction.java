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

package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.comp.window.SecurityQuestionWindow;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class OpenSecurityQuestionAction implements ClickListener{
    
    private static final long serialVersionUID = 1L;

    public OpenSecurityQuestionAction() {
        
    }

    @Override
    public void buttonClick(ClickEvent event) {
        SecurityQuestionWindow window = new SecurityQuestionWindow();
        
        event.getComponent().getWindow().addWindow(window);
    }

}
