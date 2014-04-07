package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.ui.themes.Reindeer;

import org.generationcp.ibpworkbench.ui.NewProjectAddUserPanel;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.pojos.User;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class NewProjectAddUserWindow extends Window {
    
    /**
     * 
     */
    private static final long serialVersionUID = 3983198771242295731L;

    private NewProjectAddUserPanel newUserPanel;
    
    private TwinTableSelect<User> membersSelect;
    
    private VerticalLayout layout;
    
    public NewProjectAddUserWindow(TwinTableSelect<User> membersSelect) {
        this.addStyleName(Reindeer.WINDOW_LIGHT);


        this.membersSelect = membersSelect;
        
        // set as modal window, other components are disabled while window is open
        setModal(true);

        // define window size, set as not resizable
        setWidth("680px");
        setHeight("520px");
        setResizable(false);
        
        // center window within the browser
        center();
        
        assemble();
        
        setCaption("Add New Workbench User");
    }
    
    protected void initializeComponents() {
        
        layout = new VerticalLayout();
        setContent(layout);
        
        // reuse "Register New Account" window from login screen 
        newUserPanel = new NewProjectAddUserPanel(membersSelect);
        newUserPanel.setSizeFull();
        layout.addComponent(newUserPanel);
    }

    protected void initializeLayout() {
        layout.setSpacing(true);
        layout.setMargin(true);
    }
    
    protected void initializeActions() {
        
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}

