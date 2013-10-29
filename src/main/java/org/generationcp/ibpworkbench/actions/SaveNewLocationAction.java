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

import java.util.Date;
import java.util.List;

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.comp.common.ConfirmDialog;
import org.generationcp.ibpworkbench.comp.form.AddLocationForm;
import org.generationcp.ibpworkbench.comp.window.AddLocationsWindow;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.ibpworkbench.projectlocations.ProjectLocationsController;
import org.generationcp.ibpworkbench.projectlocations.ProjectLocationsView;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * 
 * @author Jeffrey Morales, Joyce Avestro
 * 
 */

@Configurable
public class SaveNewLocationAction implements ClickListener{

    private static final Logger LOG = LoggerFactory.getLogger(SaveNewLocationAction.class);
    private static final long serialVersionUID = 1L;

    private AddLocationForm newLocationForm;

    private AddLocationsWindow window;

    private final ProjectLocationsView projectLocationsView;
    private final ProjectLocationsController projectLocationsController;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    public SaveNewLocationAction(AddLocationForm newLocationForm, AddLocationsWindow window,
            ProjectLocationsView projectLocationsView, ProjectLocationsController projectLocationsController) {
        this.newLocationForm = newLocationForm;
        this.window = window;
        this.projectLocationsView = projectLocationsView;
        this.projectLocationsController = projectLocationsController;
        
    }

    @Override
    public void buttonClick(ClickEvent event) {
    	
    	newLocationForm.commit();
    	@SuppressWarnings("unchecked")
		BeanItem<LocationModel> locationBean = (BeanItem<LocationModel>) newLocationForm.getItemDataSource();
        LocationModel location = locationBean.getBean();
    	
    	StringBuilder sb = new StringBuilder();
    	try {
    		List<Location> existingLocations = projectLocationsController.getGermplasmDataManager().getLocationsByName(location.getLocationName(), Operation.EQUAL);
		
    		if (existingLocations.size() > 0){
    			if (existingLocations.size() == 1){
    				sb.append("There is already 1 location of the name you've specified:\n");
    			}else{
    				sb.append("There are already " + existingLocations.size() + " locations of the name you've specified:\n");
    			}
    			sb.append(location.getLocationName() + "\n");
    			sb.append("Continue to save anyway?");

    			ConfirmDialog.show(window.getParent(), "Confirmation", sb.toString(), "Yes", "Cancel", new ConfirmDialog.Listener() {

    				private static final long serialVersionUID = 1L;

    				@Override
    				public void onClose(ConfirmDialog dialog) {
    					if (dialog.isConfirmed()){
    						saveLocation();
    					}
    					
    				}
    			});
    			
    		}else{
    			saveLocation();
    		}
    		
    	} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
    	
		

    }
    
    private void saveLocation(){
    	
         @SuppressWarnings("unchecked")
         BeanItem<LocationModel> locationBean = (BeanItem<LocationModel>) newLocationForm.getItemDataSource();
         LocationModel location = locationBean.getBean();

         IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

         // TODO: (BUG) there's a problem getting the nextKey of the locations when there's already existing locations
         // in the local database. Always starts at -1 for new sessions
         //if (!app.getSessionData().getUniqueLocations().contains(location.getLocationName())) {

             app.getSessionData().getUniqueLocations().add(location.getLocationName());

             Integer nextKey = app.getSessionData().getProjectLocationData().keySet().size() + 1;

             nextKey = nextKey * -1;

             LocationModel newLocation = new LocationModel();

             newLocation.setLocationName(location.getLocationName());
             newLocation.setLocationAbbreviation(location.getLocationAbbreviation());
             newLocation.setLocationId(nextKey);
             
             newLocation.setLtype(location.getLtype() != null ? location.getLtype() : 0);
             newLocation.setCntryid(location.getCntryid() != null ? location.getCntryid() : 0);
             
             app.getSessionData().getProjectLocationData().put(nextKey, newLocation);

             LOG.info(app.getSessionData().getProjectLocationData().toString());

             newLocationForm.commit();

             Location newLoc = this.initiliazeLocation(new Location());
             newLoc.setLocid(newLocation.getLocationId());
             newLoc.setLname(newLocation.getLocationName());
             newLoc.setLabbr(newLocation.getLocationAbbreviation());
             newLoc.setLtype(newLocation.getLtype() != null ? newLocation.getLtype() : 0);
             newLoc.setCntryid(newLocation.getCntryid() != null ? newLocation.getCntryid() : 0);
             
             
             try {
 				projectLocationsController.getGermplasmDataManager().addLocation(newLoc);
 				projectLocationsView.addToAvailableLocation(newLoc);
 			} catch (MiddlewareQueryException e1) {
 				e1.printStackTrace();
 			}

             User user = app.getSessionData().getUserData();
             Project currentProject = app.getSessionData().getLastOpenedProject();
             ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, "Project Locations", "Added new Location ("+ newLocation.getLocationName() + ")", user, new Date());
             try {
 				workbenchDataManager.addProjectActivity(projAct);
 			} catch (MiddlewareQueryException e) {
 				e.printStackTrace();
 			}

             newLocation = null;
             window.getParent().removeWindow(window);

         //}
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
}
