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
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenAddLocationWindowAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * The fifth tab (Locations) in Create Project Accordion Component.
 * 
 * @author Joyce Avestro
 * @author Jeffrey Morales
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

	@Autowired
    private ManagerFactoryProvider managerFactoryProvider;
	private VerticalLayout locationLayout;
	private GridLayout gridLocationLayout;
	private Select selectLocationCountry;
	private Select selectLocationType;
	private Button btnFilterLocation;
	private TwinColSelect selectLocation;
	private ManagerFactory managerFactory;
	private CropType cropType;
	
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

    protected void assemble() throws MiddlewareQueryException {
        initializeComponents();
    	initializeValues();
        initializeLayout();
        initializeActions();
    }

    protected void initializeComponents() throws MiddlewareQueryException {
        setSpacing(true);
        setMargin(true);

        addComponent(layoutLocationArea());
        buttonArea = layoutButtonArea();
        addComponent(buttonArea);
        
    }

    protected void initializeValues() throws MiddlewareQueryException {
      	  
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

        showLocationWindowButton = new Button("Add New Location");
        previousButton = new Button("Previous");

        buttonLayout.addComponent(showLocationWindowButton);
        buttonLayout.addComponent(previousButton);
        return buttonLayout;
    }
    
    private Component layoutLocationArea() throws MiddlewareQueryException {

        locationLayout = new VerticalLayout();
        gridLocationLayout = new GridLayout();
        gridLocationLayout.setRows(3);
        gridLocationLayout.setColumns(4);
        gridLocationLayout.setSpacing(true);

        selectLocationCountry = new Select();
    	selectLocationCountry.addItem("");
        populateCountryList();
        selectLocationCountry.select("1");
        selectLocationCountry.setNullSelectionAllowed(false);

        selectLocationType = new Select();
        selectLocationType.addItem("");
        populateLocationTypeList();
        selectLocationType.select("");
        selectLocationType.setNullSelectionAllowed(false);

        btnFilterLocation = new Button("Filter");
        btnFilterLocation.addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
            	selectLocation.removeAllItems();
                cropType = createProjectPanel.getSelectedCropType();
                if (cropType != null) {
                    try {
                        Container container = createLocationsContainer(cropType);
                        selectLocation.setContainerDataSource(container);

                        for (Object itemId : container.getItemIds()) {
                            Location loc = (Location) itemId;
                            selectLocation.setItemCaption(itemId, loc.getLname());
                        }
                    } catch (MiddlewareQueryException e) {
                        LOG.error("Error encountered while getting central methods", e);
                        throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                    }
                }
            }
        });

        gridLocationLayout.addComponent(new Label("Select Country To Filter"), 1, 1);
        gridLocationLayout.addComponent(selectLocationCountry, 2, 1);
        gridLocationLayout.addComponent(new Label("Select Location Type To Filter"), 1, 2);
        gridLocationLayout.addComponent(selectLocationType, 2, 2);
        gridLocationLayout.addComponent(btnFilterLocation, 3, 1);

        selectLocation = new TwinColSelect();
        selectLocation.setLeftColumnCaption("Available Locations");
        selectLocation.setRightColumnCaption("Selected Locations");
        selectLocation.setRows(10);
        selectLocation.setWidth("690px");
        selectLocation.setMultiSelect(true);
        selectLocation.setNullSelectionAllowed(true);

        if (cropType != null) {
        	selectLocation.removeAllItems();
            try {
                Container container = createLocationsContainer(cropType);
                selectLocation.setContainerDataSource(container);

                for (Object itemId : container.getItemIds()) {
                    Location location = (Location) itemId;
                    selectLocation.setItemCaption(itemId, location.getLname());
                }
            } catch (MiddlewareQueryException e) {
                LOG.error("Error encountered while getting central locations", e);
                throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
            }
        }

        locationLayout.addComponent(gridLocationLayout);
        locationLayout.addComponent(selectLocation);

        return locationLayout;

    }

	private void populateCountryList() throws MiddlewareQueryException {
    	cropType = createProjectPanel.getSelectedCropType();
    	ManagerFactory managerFactory=managerFactoryProvider.getManagerFactoryForCropType(cropType);
    	List<Country> countryList=managerFactory.getGermplasmDataManager().getAllCountry();
    	 
    	 for(Country c: countryList){
    	       selectLocationCountry.addItem(String.valueOf(c.getCntryid()));
    	       selectLocationCountry.setItemCaption(String.valueOf(c.getCntryid()), c.getIsoabbr());
    	}
		
	}
	
    private void populateLocationTypeList() throws MiddlewareQueryException {
    	
    	ManagerFactory managerFactory=managerFactoryProvider.getManagerFactoryForCropType(cropType);
    	List<UserDefinedField> userDefineField=managerFactory.getGermplasmDataManager().getUserDefinedFieldByFieldTableNameAndType("LOCATION","LTYPE");
    	 
    	 for(UserDefinedField u: userDefineField){
    		 selectLocationType.addItem(String.valueOf(u.getFldno()));
    		 selectLocationType.setItemCaption(String.valueOf(u.getFldno()), u.getFname());
    	}
	}

	private Container createLocationsContainer(CropType cropType) throws MiddlewareQueryException {
		
		ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForCropType(cropType);
		beanItemContainer = new BeanItemContainer<Location>(Location.class);
        if (managerFactory == null) {
            return beanItemContainer;
        }
        String locationCountryID =selectLocationCountry.getValue().toString();
        String locationType = selectLocationType.getValue().toString();
        Country country;

        long locCount = managerFactory.getGermplasmDataManager().countAllLocations();
        List<Location> locationList = null;
        if (selectLocationCountry.getValue().toString() != "" && selectLocationType.getValue().toString() == "") {
        	country=managerFactory.getGermplasmDataManager().getCountryById(Integer.valueOf(locationCountryID));
        	locCount=(int) managerFactory.getGermplasmDataManager().countLocationsByCountry(country);
        	locationList = managerFactory.getGermplasmDataManager().getLocationsByCountry(country, 0, (int)locCount);
        } else if (selectLocationCountry.getValue().toString() == "" && selectLocationType.getValue().toString() != "") {
        	locationList = managerFactory.getGermplasmDataManager().getLocationsByType(Integer.valueOf(locationType));
        } else if (selectLocationCountry.getValue().toString() != "" && selectLocationType.getValue().toString() != "") {
        	country=managerFactory.getGermplasmDataManager().getCountryById(Integer.valueOf(locationCountryID));
        	locationList = managerFactory.getGermplasmDataManager().getLocationsByCountryAndType(country, Integer.valueOf(locationType));
        } else {
//        	locationList = managerFactory.getGermplasmDataManager().getAllLocations(0, (int) locCount);
        }

        for (Location loc : locationList) {
            beanItemContainer.addBean(loc);
        }
        
        return beanItemContainer;
    }


    public boolean validate(){
        return true;
    }

    public boolean validateAndSave(){
        if (validate()){ // save if valid
            Set<Location> locations = (Set<Location>) selectLocation.getValue();
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


	public TwinColSelect getSelect() {
		return selectLocation;
	}


}
