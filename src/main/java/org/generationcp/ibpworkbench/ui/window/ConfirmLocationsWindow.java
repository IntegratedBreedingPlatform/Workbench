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
package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Location;

import java.util.List;

/**
 *  @author Jeffrey Morales, Joyce Avestro
 *  
 */
public class ConfirmLocationsWindow extends BaseSubWindow {

    private static final long serialVersionUID = 3983198771242295731L;

    private Label confirmMessage;

    private Button cancelButton;

    private Button okButton;
    
    private ClickListener okButtonListener;

    private Component buttonArea;

    private VerticalLayout layout;
    
    private Window window;
    
    private List<Location> existingLocations;
    
    private Table locationsTable;

    private ProgramLocationsPresenter programLocationsPresenter;
    
    public ConfirmLocationsWindow(Window window, List<Location> existingLocations ,ProgramLocationsPresenter programLocationsPresenter, ClickListener okButtonListener) {
        this.window = window;
        this.existingLocations = existingLocations;
    	this.programLocationsPresenter = programLocationsPresenter;
    	this.okButtonListener = okButtonListener;

        this.addStyleName(Reindeer.WINDOW_LIGHT);


        initialize();
    }
    
    public void show(){
    	window.getParent().addWindow(this);
    }

    private void initialize() {
        /*
         * Make the window modal, which will disable all other components while
         * it is visible
         */
        setModal(true);

        /* Make the sub window 50% the size of the browser window */
        setWidth("800px");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();

        assemble();

        setCaption("Confirm Location");

    }

    protected void initializeComponents() {
        layout = new VerticalLayout();
        setContent(layout);
        this.setParentWindow(window);

        confirmMessage = new Label();
        if (existingLocations.size() == 1){
        	confirmMessage.setCaption("There is already 1 location of the name you've specified:\n");
		}else{
			confirmMessage.setCaption("There are already " + existingLocations.size() + " locations of the name you've specified:\n");
		}
        
        confirmMessage.setStyleName(Bootstrap.Typography.H3.styleName());

        layout.addComponent(confirmMessage);
        
        locationsTable = new Table();
        locationsTable.setMultiSelect(false);
        locationsTable.setSelectable(false);
        locationsTable.setImmediate(true);
        locationsTable.setWidth("100%");
        locationsTable.setColumnReorderingAllowed(true);
        BeanItemContainer<LocationViewModel> container = new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
        try {
			for (Location loc : existingLocations){
				LocationViewModel l = programLocationsPresenter.getLocationDetailsByLocId(loc.getLocid());
				container.addItem(l);
			}
        } catch (MiddlewareQueryException e) {
			
			e.printStackTrace();
		}
		
        locationsTable.setContainerDataSource(container);
        layout.addComponent(locationsTable);
        locationsTable.setVisibleColumns(new String[]{
        		"locationName","cntryFullName","locationAbbreviation", "ltypeStr"
        });
        locationsTable.setColumnHeaders(new String[]{
        		"LOCATION NAME","COUNTRY FULL NAME", "LOCATION ABBREVIATION","LOCATION TYPE"
        });
        
        Label confirmMessage2 = new Label("Continue to save anyway?");
        layout.addComponent(confirmMessage2);

        cancelButton = new Button("Cancel");
        okButton = new Button("Save");
        okButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonArea = layoutButtonArea();
        layout.addComponent(buttonArea);

    }

    protected void initializeLayout() {
        layout.setSpacing(true);
        layout.setMargin(true);
    }

    protected void initializeActions() {

        okButton.addListener(new ClickListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				window.getParent().removeWindow(ConfirmLocationsWindow.this);
				okButtonListener.buttonClick(event);
			}
        	
        });
        
        cancelButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
		        window.focus();
                window.getParent().removeWindow(ConfirmLocationsWindow.this);
			}
		});
        
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(okButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

}
