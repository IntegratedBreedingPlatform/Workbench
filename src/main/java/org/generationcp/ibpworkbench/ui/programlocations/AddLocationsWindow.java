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
package org.generationcp.ibpworkbench.ui.programlocations;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.actions.SaveNewLocationAction;
import org.generationcp.ibpworkbench.ui.form.AddLocationForm;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;

/**
 *  @author Jeffrey Morales, Joyce Avestro
 *  
 */
public class AddLocationsWindow extends Window{

    private static final long serialVersionUID = 3983198771242295731L;

    private Label newLocationTitle;

    private AddLocationForm addLocationForm;

    private Button cancelButton;

    private Button addLocationButton;

    private Component buttonArea;

    private VerticalLayout layout;

    private ProgramLocationsPresenter programLocationsPresenter;
    private ProgramLocationsView programLocationsView;

    private LocationDataManager ldm;

    public AddLocationsWindow(ProgramLocationsView programLocationsView, ProgramLocationsPresenter programLocationsPresenter) {
        this.programLocationsView = programLocationsView;
        this.programLocationsPresenter = programLocationsPresenter;

        this.addStyleName(Reindeer.WINDOW_LIGHT);


        initialize();
    }

    private void initialize() {
        /*
         * Make the window modal, which will disable all other components while
         * it is visible
         */
        setModal(true);

        /* Make the sub window 50% the size of the browser window */
        setWidth("500px");

        // dmv : change the height to accomodate vertical layout
        setHeight("500px");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();

        assemble();

        setCaption("Add New Location");

    }

    protected void initializeComponents() {

        layout = new VerticalLayout();
        setContent(layout);

        //newLocationTitle = new Label("Add Location");
        //newLocationTitle.setStyleName("gcp-content-title");

        //layout.addComponent(newLocationTitle);

        addLocationForm = new AddLocationForm(new LocationViewModel(),programLocationsPresenter);
        layout.addComponent(addLocationForm);

        cancelButton = new Button("Cancel");
        addLocationButton = new Button("Save");
        addLocationButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());

        buttonArea = layoutButtonArea();
        layout.addComponent(buttonArea);
        layout.setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);
    }

    protected void initializeLayout() {
        layout.setSpacing(true);
        layout.setMargin(true);
    }

    protected void initializeActions() {

       
        addLocationButton.addListener(new SaveNewLocationAction(addLocationForm, this, programLocationsPresenter));
        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                AddLocationsWindow.this.getParent().removeWindow(AddLocationsWindow.this);
            }
        });
        
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Cancel");
        addLocationButton = new Button("Add");
        addLocationButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(addLocationButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

}
