package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.ibpworkbench.ui.programmembers.NewProjectAddUserPanel;
import org.generationcp.middleware.pojos.User;

public class NewProjectAddUserWindow extends BaseSubWindow {
    
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
        setWidth("925px");
        setHeight("540px");
        setResizable(false);
        
        // center window within the browser
        center();
        
        assemble();
        
        setCaption("Add a New Workbench User");
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

