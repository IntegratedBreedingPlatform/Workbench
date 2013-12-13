package org.generationcp.browser.study;

import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.browser.study.StudyAccordionMenu;
import org.generationcp.browser.study.StudyDetailComponent;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@Configurable
public class StudyInfoDialog extends Window implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = -7651767452229107837L;
    
    private final static Logger LOG = LoggerFactory.getLogger(StudyInfoDialog.class);
    
    public static final String CLOSE_SCREEN_BUTTON_ID = "StudyInfoDialog Close Button ID";

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    

    private Window parentWindow;
    private Button cancelButton;
    private Integer studyId;
   
    private StudyDataManagerImpl studyDataManager;
    
    private ManagerFactory managerFactory;

	private boolean h2hCall;
    
    public StudyInfoDialog(Window parentWindow, Integer studyId,boolean h2hCall, StudyDataManagerImpl studyDataManager){
        
        this.parentWindow = parentWindow;
        this.studyId = studyId;
        this.h2hCall= h2hCall;
        this.studyDataManager = studyDataManager;
        	
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //set as modal window, other components are disabled while window is open
        this.setModal(true);
        // define window size, set as not resizable
        this.setWidth("1100px");
        this.setHeight("650px");
        this.setResizable(false);
        this.setClosable(true);
        this.setStyleName(Reindeer.WINDOW_LIGHT);
        //setCaption("Study Information");
        // center window within the browser
        center();

         
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        
        
        try {
            Study study = this.studyDataManager.getStudy(studyId);
            setCaption("Study Information: "+study.getName());
            //don't show study details if study record is a Folder ("F")
            String studyType = study.getType();
            //if (!hasChildStudy(studyId) && !isFolderType(studyType)){
               // createStudyInfoTab(studyId);
            //}
            Accordion accordion = new StudyAccordionMenu(studyId, new StudyDetailComponent(this.studyDataManager, studyId),
                    studyDataManager, false,h2hCall);
            accordion.setWidth("100%");
            accordion.setHeight("100%");
            mainLayout.addComponent(accordion);
        } catch (NumberFormatException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            e.printStackTrace();
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_INVALID_FORMAT),
                    messageSource.getMessage(Message.ERROR_IN_NUMBER_FORMAT));
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            e.printStackTrace();
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_STUDY_DETAIL_BY_ID),
                    messageSource.getMessage(Message.ERROR_IN_GETTING_STUDY_DETAIL_BY_ID));
        }
        
        
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        
        cancelButton = new Button("Close");
        cancelButton.setData(CLOSE_SCREEN_BUTTON_ID);
        cancelButton.addListener(new Button.ClickListener() {
		
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				parentWindow.removeWindow(StudyInfoDialog.this);
				
			}
		});
        
        
        
        
        buttonLayout.addComponent(cancelButton);
        mainLayout.addComponent(buttonLayout);
        mainLayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
        
        
        addComponent(mainLayout);
    }

    
    @Override
    public void updateLabels() {
        // TODO Auto-generated method stub
        
    }
}
