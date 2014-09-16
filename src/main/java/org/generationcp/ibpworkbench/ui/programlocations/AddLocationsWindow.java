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
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.actions.SaveNewLocationAction;
import org.generationcp.ibpworkbench.ui.form.AddLocationForm;
import org.generationcp.middleware.manager.api.LocationDataManager;

/**
 *  @author Jeffrey Morales, Joyce Avestro
 *  
 */
public class AddLocationsWindow extends BaseSubWindow {

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

        assemble();
    }

    protected void initializeComponents() {

        //newLocationTitle = new Label("Add Location");
        //newLocationTitle.setStyleName("gcp-content-title");

        //layout.addComponent(newLocationTitle);

        addLocationForm = new AddLocationForm(programLocationsPresenter);

        cancelButton = new Button("Cancel");
        addLocationButton = new Button("Save");
        addLocationButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonArea = layoutButtonArea();
    }

    protected void initializeLayout() {
        this.addStyleName(Reindeer.WINDOW_LIGHT);
        this.setModal(true);
        this.setWidth("600px");
        this.setResizable(false);
        this.center();
        this.setCaption("Add New Location");


        layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("420px");

        final Panel p = new Panel();
        p.setStyleName("form-panel");
        p.setSizeFull();

        final VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.addComponent(new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>", Label.CONTENT_XHTML));
        vl.addComponent(addLocationForm);
        vl.setExpandRatio(addLocationForm,1.0F);

        p.addComponent(vl);
        layout.addComponent(p);
        layout.addComponent(buttonArea);

        layout.setExpandRatio(p,1.0F);
        layout.setComponentAlignment(buttonArea, Alignment.MIDDLE_CENTER);

        layout.setSpacing(true);
        layout.setMargin(true);

        setContent(layout);
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
        addLocationButton = new Button("Save");
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
