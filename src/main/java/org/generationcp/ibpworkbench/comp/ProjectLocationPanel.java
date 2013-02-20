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

package org.generationcp.ibpworkbench.comp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CancelLocationAction;
import org.generationcp.ibpworkbench.actions.OpenAddLocationWindowAction;
import org.generationcp.ibpworkbench.actions.SaveProjectLocationAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;

/**
 *  @author Jeffrey Morales, Joyce Avestro
 *  
 */
@Configurable
public class ProjectLocationPanel extends VerticalLayout implements InitializingBean{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ProjectLocationPanel.class);

    private Project project;
    private Role role;
    private List<Location> newLocations; 

    private Component buttonArea;
    private Button addLocationWindowButton;
    private Button saveLocationButton;
    private Button cancelButton;
    BeanItemContainer<Location> beanItemContainer;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    private VerticalLayout locationLayout;
    private GridLayout gridLocationLayout;
    private Select selectLocationCountry;
    private Select selectLocationType;
    private Button btnFilterLocation;
    private TwinColSelect selectLocation;
    private CropType cropType;
    private ManagerFactory managerFactory;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public ProjectLocationPanel(Project project, Role role) {
        this.project = project;
        this.role = role;
    }

    public List<Location> getNewLocations() {
        return newLocations;
    }

    public void setNewLocations(List<Location> newLocations) {
        this.newLocations = newLocations;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        addLocationWindowButton.addListener(new OpenAddLocationWindowAction(this));
        saveLocationButton.addListener(new SaveProjectLocationAction(this));
        cancelButton.addListener(new CancelLocationAction(this));
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        addLocationWindowButton = new Button(messageSource.getMessage(Message.LOCATION_ADD_NEW)); //"Add New Location"
        cancelButton = new Button(messageSource.getMessage(Message.CANCEL)); //"Cancel"
        saveLocationButton = new Button(messageSource.getMessage(Message.SAVE_CHANGES)); //"Save Changes"

        buttonLayout.addComponent(addLocationWindowButton);
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveLocationButton);
        return buttonLayout;
    }

    @SuppressWarnings("unchecked")
    private Component layoutLocationArea() throws MiddlewareQueryException {

        locationLayout = new VerticalLayout();
        gridLocationLayout = new GridLayout();
        gridLocationLayout.setRows(3);
        gridLocationLayout.setColumns(4);
        gridLocationLayout.setSpacing(true);

        selectLocationCountry = new Select();
        selectLocationCountry.addItem("");
        populateCountryList();
        selectLocationCountry.select("");
        selectLocationCountry.setNullSelectionAllowed(false);

        selectLocationType = new Select();
        selectLocationType.addItem("");
        populateLocationTypeList();
        selectLocationType.select("");
        selectLocationType.setNullSelectionAllowed(false);

        btnFilterLocation = new Button(messageSource.getMessage(Message.FILTER)); //"Filter"
        btnFilterLocation.addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                Set<Location> selectedLocation = (Set<Location>) selectLocation.getValue();
                selectLocation.removeAllItems();
                cropType = project.getCropType();
                if (cropType != null) {
                    try {
                        Container container = createLocationsContainer(cropType, selectedLocation);
                        selectLocation.setContainerDataSource(container);

                        for (Object itemId : container.getItemIds()) {
                            Location loc = (Location) itemId;
                            selectLocation.setItemCaption(itemId, loc.getLname());
                        }

                        if (selectedLocation.size() > 0) {
                            for (Location location : selectedLocation) {
                                selectLocation.select(location);
                                selectLocation.setValue(location);
                            }

                        }
                    } catch (MiddlewareQueryException e) {
                        LOG.error("Error encountered while getting central methods", e);
                        throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                    }
                }
            }
        });

        gridLocationLayout.addComponent(new Label(messageSource.getMessage(Message.LOCATION_COUNTRY_FILTER)), 1, 1); //"Select Country To Filter"
        gridLocationLayout.addComponent(selectLocationCountry, 2, 1);
        gridLocationLayout.addComponent(new Label("Select Location Type To Filter"), 1, 2);
        gridLocationLayout.addComponent(selectLocationType, 2, 2);
        gridLocationLayout.addComponent(btnFilterLocation, 3, 1);

        selectLocation = new TwinColSelect();
        selectLocation.setLeftColumnCaption(messageSource.getMessage(Message.LOCATION_AVAILABLE_LOCATIONS)); //"Available Locations"
        selectLocation.setRightColumnCaption(messageSource.getMessage(Message.LOCATION_SELECTED_LOCATIONS)); //"Selected Locations"
        selectLocation.setRows(10);
        selectLocation.setWidth("690px");
        selectLocation.setMultiSelect(true);
        selectLocation.setNullSelectionAllowed(true);

        if (cropType != null) {
            try {
                Set<Location> selectedLocation = (Set<Location>) selectLocation.getValue();
                Container container = createLocationsContainer(cropType, selectedLocation);
                selectLocation.setContainerDataSource(container);

                for (Object itemId : container.getItemIds()) {
                    Location location = (Location) itemId;
                    selectLocation.setItemCaption(itemId, location.getLname());
                }
                populateExistingProjectLocations();

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
        cropType = project.getCropType();
        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForCropType(cropType);
        List<Country> countryList = managerFactory.getGermplasmDataManager().getAllCountry();

        for (Country c : countryList) {
            selectLocationCountry.addItem(String.valueOf(c.getCntryid()));
            selectLocationCountry.setItemCaption(String.valueOf(c.getCntryid()), c.getIsoabbr());
        }

    }

    private void populateLocationTypeList() throws MiddlewareQueryException {

        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForCropType(cropType);
        List<UserDefinedField> userDefineField = managerFactory.getGermplasmDataManager().getUserDefinedFieldByFieldTableNameAndType(
                "LOCATION", "LTYPE");

        for (UserDefinedField u : userDefineField) {
            selectLocationType.addItem(String.valueOf(u.getFldno()));
            selectLocationType.setItemCaption(String.valueOf(u.getFldno()), u.getFname());
        }
    }

    private void populateExistingProjectLocations() throws MiddlewareQueryException {

        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
        Long projectId = project.getProjectId();
        List<Long> projectLocationIds = workbenchDataManager.getLocationIdsByProjectId(projectId, 0, (int) workbenchDataManager.countLocationIdsByProjectId(projectId));

        Set<Location> existingProjectLocations = new HashSet<Location>(); 
        for (Long locationId : projectLocationIds){
            Location location = managerFactory.getGermplasmDataManager().getLocationByID(locationId.intValue());
            if (location != null){
                existingProjectLocations.add(location);
            }
        }
        
        // Add existing project locations to selection
        if (existingProjectLocations.size() > 0) {
            for (Location location : existingProjectLocations) {
                selectLocation.select(location);
                selectLocation.setValue(location);
            }
        }

    }

    private Container createLocationsContainer(CropType cropType, Set<Location> selectedLocation) throws MiddlewareQueryException {

        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForCropType(cropType);
        beanItemContainer = new BeanItemContainer<Location>(Location.class);
        if (managerFactory == null) {
            return beanItemContainer;
        }

        String locationCountryID = "";
        if (selectLocationCountry.getValue() != null) {
            locationCountryID = selectLocationCountry.getValue().toString();
        }
        String locationType = "";
        if (selectLocationType.getValue() != null) {
            locationType = selectLocationType.getValue().toString();
        }
        Country country;

        long locCount = managerFactory.getGermplasmDataManager().countAllLocations();
        List<Location> locationList = null;
        if (!locationCountryID.equals("") && locationType.equals("")) {
            country = managerFactory.getGermplasmDataManager().getCountryById(Integer.valueOf(locationCountryID));
            locCount = (int) managerFactory.getGermplasmDataManager().countLocationsByCountry(country);
            locationList = managerFactory.getGermplasmDataManager().getLocationsByCountry(country, 0, (int) locCount);
        } else if (locationCountryID.equals("") && !locationType.equals("")) {
            locationList = managerFactory.getGermplasmDataManager().getLocationsByType(Integer.valueOf(locationType));
        } else if (!locationCountryID.equals("") && !locationType.equals("")) {
            country = managerFactory.getGermplasmDataManager().getCountryById(Integer.valueOf(locationCountryID));
            locationList = managerFactory.getGermplasmDataManager().getLocationsByCountryAndType(country, Integer.valueOf(locationType));
        } else {
            locationList = managerFactory.getGermplasmDataManager().getAllLocations(0, (int) locCount);
        }
        

        //Sort locations
        Collections.sort(locationList, Location.LocationNameComparator);

        for (Location loc : locationList) {
            beanItemContainer.addBean(loc);
        }

        if (selectedLocation.size() > 0) {
            for (Location location : selectedLocation) {
                beanItemContainer.addBean(location);
            }
        }
        return beanItemContainer;
    }
    
    public boolean validate() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean validateAndSave() {

        if (validate()) { // save if valid
            Set<Location> locations = (Set<Location>) selectLocation.getValue();
            //Save project
            if ((locations != null) && (!locations.isEmpty())) {
                try {
                    saveProjectLocation(locations, project);
                } catch (MiddlewareQueryException e) {
                    LOG.error("Error encountered while saving project locations", e);
                    throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                }
            }

        }
        return true; // locations not required, so even if there are no values, this returns true
    }

    private void saveProjectLocation(Set<Location> locations, Project projectSaved) throws MiddlewareQueryException {

        GermplasmDataManager germplasmDataManager = managerFactoryProvider.getManagerFactoryForProject(project).getGermplasmDataManager();

        // Delete existing project locations in the database
        List<ProjectLocationMap> projectLocations = workbenchDataManager.getProjectLocationMapByProjectId(
                                                project.getProjectId(), 0, (int) workbenchDataManager.countLocationIdsByProjectId(project.getProjectId()));
        for (ProjectLocationMap projectLocationMap : projectLocations){
            workbenchDataManager.deleteProjectLocationMap(projectLocationMap);
        }

        List<ProjectLocationMap> projectLocationMapList = new ArrayList<ProjectLocationMap>();
        long locID = 0;
        for (Location l : locations) {
            ProjectLocationMap projectLocationMap = new ProjectLocationMap();
            if (l.getLocid() < 1) {
                //save the added new location to the local database created
                Location location = new Location();
                location.setLocid(l.getLocid());
                location.setCntryid(0);
                location.setLabbr(l.getLabbr());
                location.setLname(l.getLname());
                location.setLrplce(0);
                location.setLtype(0);
                location.setNllp(0);
                location.setSnl1id(0);
                location.setSnl2id(0);
                location.setSnl3id(0);

                locID = germplasmDataManager.addLocation(location);
            } else {
                locID = l.getLocid();
            }
            projectLocationMap.setLocationId(locID);
            projectLocationMap.setProject(projectSaved);
            projectLocationMapList.add(projectLocationMap);
        }

        // Add the new set of project locations
        workbenchDataManager.addProjectLocationMap(projectLocationMapList);
        
        MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.LOCATION_SUCCESSFULLY_CONFIGURED));
                    //"Success", "Project location(s) successfully configured");

    }

    public TwinColSelect getSelect() {
        return selectLocation;
    }

    @Override
    public void attach() {
        super.attach();
    }

}
