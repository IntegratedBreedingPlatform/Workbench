package org.generationcp.ibpworkbench.ui.programmethods;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.actions.CancelBreedingMethodAction;
import org.generationcp.ibpworkbench.actions.SaveNewBreedingMethodAction;
import org.generationcp.ibpworkbench.ui.ProjectBreedingMethodsPanel;
import org.generationcp.ibpworkbench.ui.form.AddBreedingMethodForm;
import org.generationcp.ibpworkbench.model.BreedingMethodModel;

public class AddBreedingMethodsWindow extends Window {
    
    /**
     * 
     */
    private static final long serialVersionUID = 3983198771242295731L;

    private Label newBreedingMethodTitle;
    
    private AddBreedingMethodForm addBreedingMethodForm;
    
    private Button cancelButton;
    
    private Button addBreedingMethodButton;

    private Component buttonArea;
    
    private VerticalLayout layout;
    

	private Component projectBreedingMethodsPanel;

    
    private final static String[] VISIBLE_ITEM_PROPERTIES = new String[] { "methodName", "methodDescription", "methodType", "methodCode" };

    public AddBreedingMethodsWindow(ProgramMethodsView projectBreedingMethodsPanel) {
        /*
         * Make the window modal, which will disable all other components while
         * it is visible
         */

        this.projectBreedingMethodsPanel=projectBreedingMethodsPanel;
        setModal(true);

       /* Make the sub window 50% the size of the browser window */
        setWidth("900px");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();

        assemble();

        setCaption("Add New Breeding Method");

    }

    
    public AddBreedingMethodsWindow(ProjectBreedingMethodsPanel projectBreedingMethodsPanel) {
        /*
         * Make the window modal, which will disable all other components while
         * it is visible
         */

        this.addStyleName(Reindeer.WINDOW_LIGHT);

        this.projectBreedingMethodsPanel=projectBreedingMethodsPanel;
        setModal(true);

       /* Make the sub window 50% the size of the browser window */
        setWidth("850px");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();
        
        assemble();
        
        setCaption("Add Breeding Method");

    }
    
	protected void initializeComponents() {
        
        layout = new VerticalLayout();
        setContent(layout);
        
        //newBreedingMethodTitle = new Label("Add Breeding Method");
        //newBreedingMethodTitle.setStyleName("gcp-content-title");

        //layout.addComponent(newBreedingMethodTitle);

        addBreedingMethodForm = new AddBreedingMethodForm(new BreedingMethodModel());
        layout.addComponent(addBreedingMethodForm);

        cancelButton = new Button("Cancel");
        addBreedingMethodButton = new Button("Save");
        addBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonArea = layoutButtonArea();
        layout.addComponent(buttonArea);
        layout.setComponentAlignment(buttonArea, Alignment.MIDDLE_RIGHT);

    }

    protected void initializeLayout() {
        this.addStyleName(Reindeer.WINDOW_LIGHT);
        layout.setSpacing(true);
        layout.setMargin(true);
    }
    
    protected void initializeActions() {
        
        addBreedingMethodButton.addListener(new SaveNewBreedingMethodAction(addBreedingMethodForm, this, this.projectBreedingMethodsPanel));  
        
        cancelButton.addListener(new CancelBreedingMethodAction(this));
    }
    

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Cancel");
        addBreedingMethodButton = new Button("Add");
        addBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(addBreedingMethodButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    public void refreshVisibleItems(){
        addBreedingMethodForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
    }
}

