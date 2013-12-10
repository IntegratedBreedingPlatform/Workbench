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

import com.vaadin.data.Property;
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
public class DashboardMainTreeListener implements Property.ValueChangeListener{

    private Project project;
    private Component source;
    
    private static final Logger LOG = LoggerFactory.getLogger(DashboardMainClickListener.class);
    
    public DashboardMainTreeListener(Component source, Project project){
        this.project = project;
        this.source = source;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if(event.getProperty()==null || event.getProperty().getValue() ==null ) {
            return;
        }
        
        if(source instanceof GermplasmListPreview){
            GermplasmListPreview preview = ((GermplasmListPreview)source);
            
            Object propertyValue = event.getProperty().getValue();
            
            boolean isSharedListNode = (propertyValue instanceof  String && propertyValue.equals(GermplasmListPreview.SHARED_LIST));
            boolean isCentralGermplasmList = (propertyValue instanceof Integer && ((Integer)propertyValue).intValue() > 0);
            boolean isMyListNode = propertyValue instanceof  String && propertyValue.equals(GermplasmListPreview.MY_LIST);
            boolean isFolder = propertyValue instanceof String || preview.getPresenter().isFolder((Integer) propertyValue);
            
            // expand the node
            preview.expandTree(event.getProperty().getValue());
            
            // set the toolbar button state
            if (isSharedListNode || isCentralGermplasmList) {
                preview.setToolbarButtonsEnabled(false);
            } else if (isMyListNode) {
                preview.setToolbarButtonsEnabled(false);
                preview.setToolbarAddButtonEnabled(true);
            } else if (!isFolder) {
                preview.setToolbarButtonsEnabled(false);
                preview.setToolbarAddButtonEnabled(true);
            } else {
                preview.setToolbarButtonsEnabled(true);
            }
            
            // set the launch button state
            preview.setToolbarLaunchButtonEnabled(!isSharedListNode && !isMyListNode && !isFolder);
        }
        else if (source instanceof NurseryListPreview) {
            NurseryListPreview preview = ((NurseryListPreview)source);
            
            Object propertyValue = event.getProperty().getValue();
            
            boolean isSharedStudy = propertyValue instanceof  String && propertyValue.equals(NurseryListPreview.SHARED_STUDIES);
            boolean isCentralStudy = propertyValue instanceof Integer && ((Integer)propertyValue).intValue() > 0;
            boolean isMyStudy = propertyValue instanceof  String && propertyValue.equals(NurseryListPreview.MY_STUDIES);
            boolean isFolder = propertyValue instanceof String || preview.getPresenter().isFolder((Integer) propertyValue);
            // expand the node
            preview.expandTree(event.getProperty().getValue());
            
            // set the toolbar button state
            if (isSharedStudy || isCentralStudy) {
                preview.setToolbarButtonsEnabled(false);
            } else if (isMyStudy) {
                preview.setToolbarButtonsEnabled(false);
                preview.setToolbarAddButtonEnabled(true);
            } else if (!isFolder) {
                preview.setToolbarButtonsEnabled(false);
                preview.setToolbarAddButtonEnabled(true);
            } else {
                preview.setToolbarButtonsEnabled(true);
            } 

            // set the launch button state
            preview.setToolbarLaunchButtonEnabled(!isSharedStudy && !isMyStudy && !isFolder);
        }
    }
}
