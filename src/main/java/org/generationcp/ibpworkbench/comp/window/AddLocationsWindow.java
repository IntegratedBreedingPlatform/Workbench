package org.generationcp.ibpworkbench.comp.window;

import org.generationcp.ibpworkbench.actions.CancelLocationAction;
import org.generationcp.ibpworkbench.actions.SaveNewLocationAction;
import org.generationcp.ibpworkbench.comp.form.AddLocationForm;
import org.generationcp.ibpworkbench.comp.project.create.ProjectLocationsComponent;
import org.generationcp.ibpworkbench.model.LocationModel;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AddLocationsWindow extends Window {
    
    /**
     *  @author Jeffrey Morales
     */
    private static final long serialVersionUID = 3983198771242295731L;

    private Label newLocationTitle;
    
    private AddLocationForm addLocationForm;
    
    private Button cancelButton;
    
    private Button addLocationButton;

    private Component buttonArea;
    
    private VerticalLayout layout;
    
    private ProjectLocationsComponent projectLocationComponent;
    
	public AddLocationsWindow(ProjectLocationsComponent projectLocationComponent) {
		 this.projectLocationComponent=projectLocationComponent;
        /*
         * Make the window modal, which will disable all other components while
         * it is visible
         */
        setModal(true);

       /* Make the sub window 50% the size of the browser window */
        setWidth("50%");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();
        
        assemble();
        
        setCaption("Add Location");
       

    }
    
    protected void initializeComponents() {
        
        layout = new VerticalLayout();
        setContent(layout);
        
        newLocationTitle = new Label("Add Location");
        newLocationTitle.setStyleName("gcp-content-title");

        layout.addComponent(newLocationTitle);

        addLocationForm = new AddLocationForm(new LocationModel());
        layout.addComponent(addLocationForm);

        cancelButton = new Button("Cancel");
        addLocationButton = new Button("Save");
        
        buttonArea = layoutButtonArea();
        layout.addComponent(buttonArea);
        
    }

    protected void initializeLayout() {
        layout.setSpacing(true);
        layout.setMargin(true);
    }
    
    protected void initializeActions() {
        
        addLocationButton.addListener(new SaveNewLocationAction(addLocationForm, this, this.projectLocationComponent));  
        
        cancelButton.addListener(new CancelLocationAction(this));
    }
    

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Cancel");
        addLocationButton = new Button("Add");
        
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

