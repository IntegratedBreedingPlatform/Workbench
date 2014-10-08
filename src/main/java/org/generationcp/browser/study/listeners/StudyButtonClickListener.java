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

package org.generationcp.browser.study.listeners;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Layout;
import org.generationcp.browser.study.TableViewerComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyButtonClickListener implements Button.ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(StudyButtonClickListener.class);
    private static final long serialVersionUID = 7921109465618354206L;

    private Object source;

    public StudyButtonClickListener(Layout source) {
        this.source = source;
    }

    public StudyButtonClickListener(TableViewerComponent source) {
    	this.source = source;
	}

	@Override
    public void buttonClick(ClickEvent event) {
        
        
        
    }

}
