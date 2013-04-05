package org.generationcp.ibpworkbench.comp.window;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.*;

@Configurable
public class BackupIBDBWindow extends Window implements InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;
    
    private static final String VERSION = "1.1.3.10";
    

    private Project project;
        
    // Components
	private ComponentContainer rootLayout;
	private FormLayout formLayout;
	private Select select;
	private Button cancelBtn;
	private Button saveBtn;
	
	private static final String WINDOW_WIDTH = "400px";
	private static final String WINDOW_HEIGHT = "190px";
    
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public BackupIBDBWindow(Project project) {
    	this.project = project;
    }

    /**
     * Assemble the UI after all dependencies has been set.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }

    protected void initializeComponents() throws Exception {
		select = new Select("Choose a project to backup");
		
		saveBtn = new Button("Save");
		saveBtn.setSizeUndefined();
		cancelBtn = new Button("Cancel");
		cancelBtn.setSizeUndefined();
    }

    protected void initializeLayout() {
		this.setCaption("Backup IB Database");
		this.setWidth(WINDOW_WIDTH);
		this.setHeight(WINDOW_HEIGHT);
		this.setResizable(false);
		this.setModal(true);
				
		rootLayout = this.getContent();
		formLayout = new FormLayout();
		formLayout.setMargin(true);
		formLayout.setSizeFull();
		
		rootLayout.addComponent(formLayout);
		// bind components to layout
		formLayout.addComponent(select);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);
		hl.setMargin(true);
		
		
		Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
		spacer.setWidth("100%");
		
		
		hl.addComponent(spacer);
		hl.addComponent(saveBtn);
		hl.addComponent(cancelBtn);
		
		hl.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
		hl.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
		hl.setExpandRatio(spacer,1.0f);
		
		rootLayout.addComponent(hl);
    }

    protected void initializeActions() {
    	// DO button listeners + actions here
    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

	@Override
	public void updateLabels() {
		//messageSource.setCaption(this, Message.BACKUP_IBDBWINDOW);
		//messageSource.setCaption(select, Message.BACKUP_IBDBROJ_DROPDOWN);
		messageSource.setCaption(saveBtn,Message.SAVE);
		messageSource.setCaption(cancelBtn,Message.CANCEL);
	}
}
