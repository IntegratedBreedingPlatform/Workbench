package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.*;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenWorkflowForRoleAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 10/28/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class UpdateProjectPanel extends CreateProjectPanel {
    

	@Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SessionData sessionData;

    private String oldProjectName;
    
    private ProjectBasicDetailsComponent projectBasicDetails;
    
    private Label heading;

    public UpdateProjectPanel() {
        super();
    }

    @Override
    protected void initializeActions() {
        super.saveProjectButton.addListener(new UpdateProjectAction(this));
        super.saveProjectButton.setCaption("Save");
        cancelButton.addListener(new HomeAction());
    }

    
    @Override
    protected  void initializeComponents() {
    	
    	 
        heading = new Label("<span class='bms-members' style='color: #D1B02A; font-size: 23px'></span>&nbsp;Basic Details",Label.CONTENT_XHTML);
        heading.setStyleName(Bootstrap.Typography.H4.styleName()); 
    	
        newProjectTitleArea = new HorizontalLayout();
        newProjectTitleArea.setSpacing(true);

        UpdateProjectAccordion projectAccordion = new UpdateProjectAccordion(this);
        createProjectAccordion = projectAccordion;
        
        projectBasicDetails = new ProjectBasicDetailsComponent(this, true);
        projectBasicDetails.updateProjectDetailsFormField(this.getProject());
  
        buttonArea = layoutButtonArea();
    }
    
    @Override
    protected  void initializeLayout() {
    	
    	VerticalLayout root = new VerticalLayout();
        root.setMargin(new Layout.MarginInfo(true,true,true,true));
        root.setSpacing(true);
        root.setWidth("800px");
        
        root.addComponent(heading);
        root.addComponent(projectBasicDetails);
        root.addComponent(buttonArea);
        root.setComponentAlignment(buttonArea, Alignment.TOP_CENTER);
        
    
        setScrollable(true);
        setContent(root);
        
        

    }

    @Override
    protected void initializeValues() {
        // initialize component values

    }

    @Override
    public void afterPropertiesSet() {
        try {
            // initialize state
            currentUser = workbenchDataManager.getUserById(sessionData.getUserData().getUserid());   // get hibernate managed version of user
            project = sessionData.getSelectedProject();
            oldProjectName = new String(project.getProjectName());


            this.setSelectedCropType(project.getCropType());

            this.initializeComponents();
            this.initializeLayout();
            this.initializeActions();
            this.initializeValues();


        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }



    }

    public String getOldProjectName() {
        return oldProjectName;
    }
    
    @Override
	public boolean validate() {
    	 if (projectBasicDetails.validateAndSave()) {
             return true;
         }

         return false;
	}
    
    
}
