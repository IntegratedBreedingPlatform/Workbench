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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CancelLocationAction;
import org.generationcp.ibpworkbench.actions.OpenAddLocationWindowAction;
import org.generationcp.ibpworkbench.actions.SaveProjectLocationAction;
import org.generationcp.ibpworkbench.comp.common.TwoColumnSelect;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *  @author Jeffrey Morales, Joyce Avestro
 *  
 */
@Configurable
public class ProjectLocationPanel extends VerticalLayout implements InitializingBean {

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
    
    private Table dataTable;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    private VerticalLayout locationLayout;
    private GridLayout gridLocationLayout;
    private Select selectLocationCountry;
    private Select selectLocationType;
    private Button btnFilterLocation;
    private TwoColumnSelect selectLocation;
    private CropType cropType;
    
    private Window blPopupWindow;
    
    private ProjectLocationPanel thisInstance;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private ManagerFactory managerFactory;

    public ProjectLocationPanel(Project project, Role role) {
        this.project = project;
        this.role = role;
        this.thisInstance = this;
    }

    public List<Location> getNewLocations() {
        return newLocations;
    }

    public void setNewLocations(List<Location> newLocations) {
        this.newLocations = newLocations;
    }
    
    public void addNewLocations(Location newLocation) {
    	newLocations.add(newLocation);
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
        managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
        
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
        
        dataTable = new Table();
        dataTable.setWidth("690px");
        dataTable.setHeight("150px");
    	dataTable.setImmediate(true);
        
        addComponent(layoutLocationArea());
        buttonArea = layoutButtonArea();
        addComponent(buttonArea);

    }
    
    @Override
    public void detach() {
        super.detach();
        
        final Window parentWindow = thisInstance.getApplication().getMainWindow();
        parentWindow.removeWindow(blPopupWindow);
    }
    
    protected void repaintTable()
    {
    	//better change this
         
    	List<String> columnHeaders = new ArrayList<String>();
        columnHeaders.add("Location Type");
        columnHeaders.add("Location Name");
        columnHeaders.add("Location Abbreviation");
        
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("Location Type", String.class, null);
        container.addContainerProperty("Location Name", String.class, null);
        container.addContainerProperty("Location Abbreviation", String.class, null);
        dataTable.setContainerDataSource(container);
        
        dataTable.setVisibleColumns(columnHeaders.toArray(new String[0]));
        dataTable.setColumnHeaders(columnHeaders.toArray(new String[0]));
    }

    protected void initializeValues() throws MiddlewareQueryException {
    	newLocations = new ArrayList<Location>();
  
    	GermplasmDataManager gdm = this.getGermplasmDataManager();
    	
    	//Get all Local locations
    	List<Location> allLocalLocations = gdm.getAllLocalLocations(0,Integer.MAX_VALUE);
    	
    	// Initialize IBPWorkbench.app session
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        for (Location loc : allLocalLocations) {
        	if (loc.getLocid()<0 && !app.getSessionData().getUniqueLocations().contains(loc.getLname())) {
        		LocationModel locModel = new LocationModel();
            	locModel.setCntryid(loc.getCntryid());
            	locModel.setLocationAbbreviation(loc.getLabbr());
            	locModel.setLocationId(loc.getLocid());
            	locModel.setLocationName(loc.getLname());
            	locModel.setLtype(loc.getLtype());
            	
                app.getSessionData().getUniqueLocations().add(locModel.getLocationName());

                //Integer nextKey = app.getSessionData().getProjectLocationData().keySet().size() + 1;
                //nextKey = nextKey * -1;
                //app.getSessionData().getProjectLocationData().put(nextKey, locModel);
                app.getSessionData().getProjectLocationData().put(locModel.getLocationId(), locModel);
            }
        }
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
        selectLocation.getLeftSelect().setImmediate(true);
        selectLocation.getLeftSelect().addListener(new ChangeValueAction());
    }
    
    
    private class ChangeValueAction implements ValueChangeListener {
    	
    	//ProjectLocationPanel.LOG.debug("ValueChangeEvent triggered");
    	
        private static final long serialVersionUID = 1L;
        
        /*
        @Override
        public void valueChange(ValueChangeEvent event) {
        	
            Container indexContainer = dataTable.getContainerDataSource();

            indexContainer.removeAllItems();

            @SuppressWarnings("unchecked")
            Set<Location> locationSet = (Set<Location>) event.getProperty().getValue();
            
            for (Location location : locationSet) {
                Item item = indexContainer.addItem(location);
                item.getItemProperty("Location Type").setValue(location.getLtype());
                item.getItemProperty("Location Name").setValue(location.getLname());
                item.getItemProperty("Location Abbreviation").setValue(location.getLabbr());
            }
        }
        */
        
        @Override
        public void valueChange(ValueChangeEvent event) {
        	final Window parentWindow = thisInstance.getWindow();
        	
        	Object selectedItem = selectLocation.getLeftSelect().getValue();
			if (selectedItem instanceof Set) {
				//ProjectBreedingMethodsPanel.LOG.debug("Set returned, either items moved to right or left column is multi selected");
				
				
			        
				@SuppressWarnings("unchecked")
                Set<Location> locationSet = (Set<Location>) selectedItem;
				
				if (locationSet.size() == 0) {
					
					if (blPopupWindow != null) {
						parentWindow.removeWindow(blPopupWindow);
					}
				} else if (locationSet.size() > 0) {
					List<Location> selectedLocations = new ArrayList<Location>(locationSet);
					openWindow(parentWindow,selectedLocations);
				}
				
			}
        	
        }
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
        
        Iterator<?> iterator = selectLocationCountry.getItemIds().iterator();
        if (iterator.hasNext()){
        	iterator.next();
        	selectLocationCountry.select(iterator.next());
        }else{
        	selectLocationCountry.select("");
        }
        
        
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

        selectLocation = new TwoColumnSelect();
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
        
        locationLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
        locationLayout.addComponent(selectLocation);
 
        repaintTable();
        locationLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
        //locationLayout.addComponent(dataTable);
        return locationLayout;

    }

    private void populateCountryList() throws MiddlewareQueryException {
        cropType = project.getCropType();
        
        List<Country> countryList = managerFactory.getGermplasmDataManager().getAllCountry();

        for (Country c : countryList) {
            selectLocationCountry.addItem(String.valueOf(c.getCntryid()));
            selectLocationCountry.setItemCaption(String.valueOf(c.getCntryid()), c.getIsoabbr());
        }

    }

    private void populateLocationTypeList() throws MiddlewareQueryException {

        List<UserDefinedField> userDefineField = managerFactory.getGermplasmDataManager().getUserDefinedFieldByFieldTableNameAndType(
                "LOCATION", "LTYPE");
        
       
        
        for (UserDefinedField u : userDefineField) {
            selectLocationType.addItem(String.valueOf(u.getFldno()));
            selectLocationType.setItemCaption(String.valueOf(u.getFldno()), u.getFname());
        }
    }

    private void populateExistingProjectLocations() throws MiddlewareQueryException {
        Long projectId = project.getProjectId();
        List<Long> projectLocationIds = workbenchDataManager.getLocationIdsByProjectId(projectId, 0,Integer.MAX_VALUE);

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
                selectLocation.setItemCaption(location,location.getLname());
                selectLocation.setValue(location);
            }
        }
    }

    private Container createLocationsContainer(CropType cropType, Set<Location> selectedLocation) throws MiddlewareQueryException {

        //ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForCropType(cropType);
        //ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
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

        List<Location> locationList = null;
        if (!locationCountryID.equals("") && locationType.equals("")) {
            country = managerFactory.getGermplasmDataManager().getCountryById(Integer.valueOf(locationCountryID));
            locationList = managerFactory.getGermplasmDataManager().getLocationsByCountry(country, 0,Integer.MAX_VALUE);
        } else if (locationCountryID.equals("") && !locationType.equals("")) {
            locationList = managerFactory.getGermplasmDataManager().getLocationsByType(Integer.valueOf(locationType));
        } else if (!locationCountryID.equals("") && !locationType.equals("")) {
            country = managerFactory.getGermplasmDataManager().getCountryById(Integer.valueOf(locationCountryID));
            locationList = managerFactory.getGermplasmDataManager().getLocationsByCountryAndType(country, Integer.valueOf(locationType));
        } else {
            locationList = managerFactory.getGermplasmDataManager().getAllLocations(0,Integer.MAX_VALUE);
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
            Set<Location> selectedLocations = (Set<Location>) selectLocation.getValue();
            ListSelect leftSelect = selectLocation.getLeftSelect();
            Collection<Location> availableLocations = (Collection<Location>)leftSelect.getItemIds();

            try {
                saveProjectLocation(availableLocations, selectedLocations, project);
            } catch (MiddlewareQueryException e) {
                LOG.error("Error encountered while saving project locations", e);
                throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
            }
        }
        return true; // locations not required, so even if there are no values, this returns true
    }

    private void saveProjectLocation(Collection<Location> availableLocations, Set<Location> selectedLocations, Project projectSaved) throws MiddlewareQueryException {
    	//DEBUG LOGS
    	// check newLocations property
    	LOG.debug("newLocations count: " + newLocations.size());
    	
    	GermplasmDataManager germplasmDataManager = this.getGermplasmDataManager();
  
        // Delete existing project locations in the database
        List<ProjectLocationMap> projectLocationMapList = workbenchDataManager.getProjectLocationMapByProjectId(
                project.getProjectId(), 0,Integer.MAX_VALUE);
        
        for (ProjectLocationMap projectLocationMap : projectLocationMapList){
            workbenchDataManager.deleteProjectLocationMap(projectLocationMap);
        }
        
        
        //add available location to local db location table if it does not yet exist
        for (Location l : this.newLocations) {
            Location location = initiliazeLocation(l);
            germplasmDataManager.addLocation(location);
        }
        
        projectLocationMapList = new ArrayList<ProjectLocationMap>();
        
        /*
         * add selected location to local db location table if it does not yet exist
         * add location in workbench_project_loc_map in workbench db
         */
        for (Location l : selectedLocations) {
            ProjectLocationMap projectLocationMap = new ProjectLocationMap();
        
            projectLocationMap.setLocationId(l.getLocid().longValue());
            projectLocationMap.setProject(projectSaved);
            projectLocationMapList.add(projectLocationMap);
        }


        // Add the new set of project locations
        workbenchDataManager.addProjectLocationMap(projectLocationMapList);
        
        MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.LOCATION_SUCCESSFULLY_CONFIGURED));
                    //"Success", "Project location(s) successfully configured");

    }
    
    private void _saveProjectLocation(Collection<Location> availableLocations, Set<Location> selectedLocations, Project projectSaved) throws MiddlewareQueryException {
        GermplasmDataManager germplasmDataManager = managerFactory.getGermplasmDataManager();

        // Delete existing project locations in the database
        List<ProjectLocationMap> projectLocations = workbenchDataManager.getProjectLocationMapByProjectId(
                                                                                                          project.getProjectId(), 0, (int) workbenchDataManager.countLocationIdsByProjectId(project.getProjectId()));
        for (ProjectLocationMap projectLocationMap : projectLocations){
            workbenchDataManager.deleteProjectLocationMap(projectLocationMap);
        }
        List<Location> allLocations = germplasmDataManager.getAllLocations(0, (int)germplasmDataManager.countAllLocations());
        for (Location l : allLocations) {
            germplasmDataManager.deleteLocation(l);
        }
        //add available location to local db location table if it does not yet exist
        for (Location l : availableLocations) {
            if (l.getLocid() < 1) {
                Location location = initiliazeLocation(l);
                germplasmDataManager.addLocation(location);
            }
        }
        List<ProjectLocationMap> projectLocationMapList = new ArrayList<ProjectLocationMap>();
        long locID = 0;
        /*
         * add selected location to local db location table if it does not yet exist
         * add location in workbench_project_loc_map in workbench db
         */
        for (Location l : selectedLocations) {
            ProjectLocationMap projectLocationMap = new ProjectLocationMap();
            if (l.getLocid() < 1) {
                //save the added new location to the local database created
                Location location = initiliazeLocation(l);
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


    public TwoColumnSelect getSelect() {
        return selectLocation;
    }

    @Override
    public void attach() {
        super.attach();
    }
    
    public GermplasmDataManager getGermplasmDataManager() {
    	if (managerFactory == null)    	
    		managerFactory=managerFactoryProvider.getManagerFactoryForProject(project);
    	
    	return managerFactory.getGermplasmDataManager();
    }

    private Location initiliazeLocation(Location l) {
        Location location = new Location();
        location.setLocid(l.getLocid());
        location.setLabbr(l.getLabbr());
        location.setLname(l.getLname());
        location.setLrplce(0);

        Integer ltype = (l.getLtype() != null) ? l.getLtype() : 0;
        Integer cntryid = (l.getCntryid() != null) ? l.getCntryid() : 0;

        location.setLtype(ltype);
        location.setCntryid(cntryid);

        location.setNllp(0);
        location.setSnl1id(0);
        location.setSnl2id(0);
        location.setSnl3id(0);
        return location;
    }

    
    private void openWindow(Window parentWindow,List<Location> selectedLocation) {
        if (blPopupWindow != null) {
            parentWindow.removeWindow(blPopupWindow);
        }

        blPopupWindow = new ProjectBreedingLocationsPopup(selectedLocation);
        blPopupWindow.setPositionX(97);
        blPopupWindow.setPositionY(408);

        parentWindow.addWindow(blPopupWindow);
    }
    
    class ProjectBreedingLocationsPopup extends Window {
        private static final long serialVersionUID = 1L;
        
        private VerticalLayout main = new VerticalLayout();
    	
    	private ProjectBreedingLocationsPopup() {
    		main.setSpacing(true);
    		main.setMargin(false);
    		
    		this.setCaption("Breeding Location Details");
    		
    		this.setResizable(false);
    		this.setScrollable(true);
    		this.setDraggable(true);
    		this.setWidth("400px");
    		this.setHeight("180px");


    		
    		setContent(main);
    	}
    	
    	
    	public ProjectBreedingLocationsPopup(List<Location> selectLocation) {
    		this();
    		
    		
    		Collections.sort(selectLocation,new Comparator<Location>() {
				@Override
				public int compare(Location o1, Location o2) {
					return o1.getLname().compareTo(o2.getLname());
				}
			});
    		
    		for (int i = 0; i < selectLocation.size(); i++) {
    			if (i % 2 == 0)
    				init(selectLocation.get(i),false);
    			else {
    				init(selectLocation.get(i),true);
    			}
    		}
    		
    		
    	}
    	
    	public ProjectBreedingLocationsPopup(Location m) {
    		this();
    		
    		init(m,false);
    	}
    
    	private void init(Location l,boolean isOdd) {
    		
    		
    		try {
    			LOG.debug(l.toString());
    			
				List<LocationDetails> locdet = ProjectLocationPanel.this.getGermplasmDataManager().getLocationDetailsByLocId(l.getLocid(), 0, 1);
				LocationDetails details = locdet.get(0);
				//public void setBreedingMethodDetailsValues(String mtitle, String ldesc,String lname, String lcntry,String labbrv, String ltype,boolean isOdd) {
				   
				setBreedingMethodDetailsValues(l.getLname(),details.getLocation_description().trim().equals("-") ? "" : details.getLocation_description(),details.getLocation_name(),details.getCountry_full_name(),details.getLocation_abbreviation(),details.getLocation_type(),isOdd);
			} catch (MiddlewareQueryException e) {
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				// TODO: Note, I dont know why but I'm getting IndexOutOfBoundsException for newly added locations. Abro, can you confirm if this is a bug or something expected
				//Do nothing for the moment
			}
				//setBreedingMethodDetailsValues(l.getLname(),l.getLname(),l.getLabbr(),l.getLabbr(),l.getLabbr(),formattedDate,isOdd);
    	}
    	
    	public void setBreedingMethodDetailsValues(String mtitle, String ldesc,String lname, String lcntry,String labbrv, String ltype,boolean isOdd) {
	   		 Label mtitleLbl = new Label(mtitle,Label.CONTENT_TEXT);
	   		 Label ldescLbl = new Label(ldesc,Label.CONTENT_TEXT);
	   		 Label lnameLbl = new Label(lname,Label.CONTENT_TEXT);
	   		 Label lcntryLbl = new Label(lcntry,Label.CONTENT_TEXT);
	   		 Label labbrvLbl = new Label(labbrv,Label.CONTENT_TEXT);
	   		 Label ltypeLbl = new Label(ltype,Label.CONTENT_TEXT);
	   		
			CustomLayout c = new CustomLayout("breedingLocationsPopupLayout");
   			c.addStyleName("bmPopupLayout");
   			
   			if (isOdd)
   				c.addStyleName("odd");
   			
			c.addComponent(mtitleLbl,"mtitle");
	   		c.addComponent(ldescLbl,"ldesc");
	   		c.addComponent(lnameLbl,"lname");
	   		c.addComponent(lcntryLbl,"lcountry");
	   		c.addComponent(labbrvLbl,"labbrv");
	   		c.addComponent(ltypeLbl,"ltype");
	   		
	   	
	   		main.addComponent(c);
    	}    	
    }
}
