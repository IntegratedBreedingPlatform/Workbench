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

import org.generationcp.ibpworkbench.comp.UserAccountPanel;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


/**
 * <b>Description</b>: 
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 11, 2012
 */
public class OpenRegisterUserAccountAction implements ClickListener, ActionListener {

    private static final long serialVersionUID = 8120323541231442435L;

    @Override
    public void buttonClick(ClickEvent event) {
        
        UserAccountPanel p = new UserAccountPanel();
        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(p);
        vl.setComponentAlignment(p, Alignment.MIDDLE_CENTER);
        vl.setMargin(true);
        vl.setSpacing(true);
        
        event.getComponent().getWindow().setContent(vl);
    }

    @Override
    public void doAction(Event event) {
        
    }

    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        
    }

}
