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

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Component;
import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Efficio.Daniel
 *
 */
public class DashboardMainTreeListener implements ItemClickEvent.ItemClickListener{

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

        if(source instanceof GermplasmListPreview){
            GermplasmListPreview preview = ((GermplasmListPreview)source);
            
            Object propertyValue = event.getItemId();
            
            // expand the node
            preview.expandTree(event.getItemId());
            
            preview.processToolbarButtons(propertyValue);
        }
        else if (source instanceof NurseryListPreview) {
            NurseryListPreview preview = ((NurseryListPreview)source);
            
            Object propertyValue = event.getItemId();
            
            // expand the node
            preview.expandTree(event.getItemId());
            
            preview.processToolbarButtons(propertyValue);
        }
    }
}
