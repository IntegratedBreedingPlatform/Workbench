/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.ui.dashboard.listener;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;


/**
 * @author Efficio.Daniel
 *
 */
public class DashboardMainTreeListener implements ItemClickListener{

    private Project project;
    private Component source;
    
    private static final Logger LOG = LoggerFactory.getLogger(DashboardMainClickListener.class);
    
    public DashboardMainTreeListener(Component source, Project project){
        this.project = project;
        this.source = source;
    }

    /* (non-Javadoc)
     * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
     */
    @Override
    public void itemClick(ItemClickEvent event) {
        // TODO Auto-generated method stub
        //event.getItemId()
        if(source instanceof GermplasmListPreview){
            System.out.println(event.getItemId());
        }else if(source instanceof NurseryListPreview){
            System.out.println(event.getItemId());
        }
    }
    
    

}
