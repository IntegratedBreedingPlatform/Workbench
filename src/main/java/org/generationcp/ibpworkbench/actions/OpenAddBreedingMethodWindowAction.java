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

import org.generationcp.ibpworkbench.comp.window.AddBreedingMethodsWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class OpenAddBreedingMethodWindowAction implements ClickListener{
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenAddBreedingMethodWindowAction.class);

    private static final long serialVersionUID = 1L;

    public OpenAddBreedingMethodWindowAction() {

    }

    @Override
    public void buttonClick(ClickEvent event) {
        event.getComponent().getWindow().addWindow(new AddBreedingMethodsWindow());
    }

}
