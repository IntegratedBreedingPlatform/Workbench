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
        setWidth("700px");
        setResizable(false);
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
        setWidth("500px");
        setResizable(false);
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();

        assemble();

        setCaption("Add Breeding Method");

    }

    protected void initializeComponents() {
        //newBreedingMethodTitle = new Label("Add Breeding Method");
        //newBreedingMethodTitle.setStyleName("gcp-content-title");

        //layout.addComponent(newBreedingMethodTitle);

        addBreedingMethodForm = new AddBreedingMethodForm(new BreedingMethodModel());

        cancelButton = new Button("Cancel");
        addBreedingMethodButton = new Button("Save");
        addBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonArea = layoutButtonArea();


    }

    protected void initializeLayout() {
        this.addStyleName(Reindeer.WINDOW_LIGHT);

        layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("410px");

        final Panel p = new Panel();
        p.setStyleName("form-panel");
        p.setSizeFull();

        final VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.addComponent(new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>", Label.CONTENT_XHTML));
        vl.addComponent(addBreedingMethodForm);
        vl.setExpandRatio(addBreedingMethodForm,1.0F);

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

