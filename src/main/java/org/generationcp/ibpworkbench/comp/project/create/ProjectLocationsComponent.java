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

package org.generationcp.ibpworkbench.comp.project.create;

import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenAddLocationWindowAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;

/**
 * The fifth tab (Locations) in Create Project Accordion Component.
 * 
 * @author Joyce Avestro
 */
//TODO Modify AddLocation action to save locations to CreateProjectPanel
@SuppressWarnings("unchecked")
@Configurable
public class ProjectLocationsComponent extends VerticalLayout implements InitializingBean{

    private static final Logger LOG = LoggerFactory.getLogger(ProjectLocationsComponent.class);
    private static final long serialVersionUID = 1L;

    private CreateProjectPanel createProjectPanel;

    private Button previousButton;
    private Button showLocationWindowButton;
    private Component buttonArea;
    BeanItemContainer<Location> beanItemContainer;
    TwinColSelect select;

	@Autowired
    private ManagerFactoryProvider managerFactoryProvider;
	private Button testButton;

	
    public ProjectLocationsComponent(CreateProjectPanel createProjectPanel) {
        this.createProjectPanel = createProjectPanel;
    }

    public Button getShowLocationWindowButton() {
        return showLocationWindowButton;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }

    protected void assemble() {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();
    }

    protected void initializeComponents() {
        setSpacing(true);
        setMargin(true);

        select = new TwinColSelect();
        select.setLeftColumnCaption("Available Locations");
        select.setRightColumnCaption("Selected Locations");
        select.setRows(10);
        select.setWidth("690px");
        select.setMultiSelect(true);
        select.setNullSelectionAllowed(true);

        CropType cropType = createProjectPanel.getSelectedCropType();
        if (cropType != null) {
            try {
                Container container = createLocationsContainer(cropType);
                select.setContainerDataSource(container);

                for (Object itemId : container.getItemIds()) {
                    Location location = (Location) itemId;
                    select.setItemCaption(itemId, location.getLname());
                }
            } catch (MiddlewareQueryException e) {
                LOG.error("Error encountered while getting central locations", e);
                throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
            }
        }

        addComponent(select);

        buttonArea = layoutButtonArea();
        addComponent(buttonArea);
    }

    protected void initializeValues() {
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);
    }

    protected void initializeActions() {
        previousButton.addListener(new PreviousButtonClickListener());
        showLocationWindowButton.addListener(new OpenAddLocationWindowAction(this));
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        showLocationWindowButton = new Button("Add Location");
        previousButton = new Button("Previous");

        buttonLayout.addComponent(showLocationWindowButton);
        buttonLayout.addComponent(previousButton);
        return buttonLayout;
    }

    private Container createLocationsContainer(CropType cropType) throws MiddlewareQueryException {
        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForCropType(cropType);

        beanItemContainer = new BeanItemContainer<Location>(Location.class);
        if (managerFactory == null) {
            return beanItemContainer;
        }

        long locCount = managerFactory.getGermplasmDataManager().countAllLocations();
        List<Location> locationList = managerFactory.getGermplasmDataManager().getAllLocations(0, (int) locCount);

        for (Location location : locationList) {
            beanItemContainer.addBean(location);
        }

        return beanItemContainer;
    }


    public boolean validate(){
        boolean success = true;
        if (select != null) {
            Set<Location> locations = (Set<Location>) select.getValue();
            if (locations.size() == 0){
                MessageNotifier.showWarning(getWindow(), "Warning", "No location selected.");  
                success = false;
            }
        } else {
            MessageNotifier.showWarning(getWindow(), "Warning", "No location selected.");
        }  
        return success;
    }
    

    public boolean validateAndSave(){
        if (validate()){ // save if valid
            Set<Location> locations = (Set<Location>) select.getValue();
            Project project = createProjectPanel.getProject();
            project.setLocations(locations);
            createProjectPanel.setProject(project);
        } 
        return true;    // locations not required, so even if there are no values, this returns true
    }
    
    private class PreviousButtonClickListener implements ClickListener{
        private static final long serialVersionUID = 1L;

        @Override
        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
          createProjectPanel.getCreateProjectAccordion().setFocusToTab(CreateProjectAccordion.FOURTH_TAB_BREEDING_METHODS);
        }
    }
    
    public BeanItemContainer<Location> getBeanItemContainer() {
		return beanItemContainer;
	}

	public TwinColSelect getSelect() {
		return select;
	}


}
