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
package org.generationcp.ibpworkbench.ui.window;

import java.io.File;
import java.util.Map;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.UploadBreedingViewOutputAction;
import org.generationcp.ibpworkbench.ui.common.UploadField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.easyuploads.FileFactory;

import com.vaadin.data.Validator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 *  @author Aldrin Batac
 *  
 */
@Configurable
public class FileUploadBreedingViewOutputWindow extends BaseSubWindow implements InitializingBean {

	private static final String BMS_UPLOAD_CONTAINER = "bms-upload-container";

	private static final Logger LOG = LoggerFactory.getLogger(FileUploadBreedingViewOutputWindow.class);
	
    private static final long serialVersionUID = 3983198771242295731L;
    
    private Label description;

    private Button cancelButton;

    private Button uploadButton;

    private Component buttonArea;

    private VerticalLayout layout;
    
    private Window window;
    
    private CustomUploadField uploadZip;

	private Label uploadZipLabel;
    
    private int studyId;
    
	private Project project;

	private Map<String, Boolean> variatesStateMap;
    
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public FileUploadBreedingViewOutputWindow(Window window, int studyId, Project project, Map<String, Boolean> variatesStateMap) {
        this.window = window;
        this.studyId = studyId;
        this.project = project;
        this.variatesStateMap = variatesStateMap;
    }
    
    public void show(){
    	window.getParent().addWindow(this);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
		initialize();
	}

    private void initialize() {
        /*
         * Make the window modal, which will disable all other components while
         * it is visible
         */
    	addStyleName(Reindeer.WINDOW_LIGHT);
    	
        setModal(true);

        /* Make the sub window 50% the size of the browser window */
        setWidth("700px");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();

        assemble();

        setCaption(messageSource.getMessage(Message.BV_UPLOAD_HEADER));

    }

    protected void initializeComponents() {
        layout = new VerticalLayout();
        layout.setWidth("100%");
        setContent(layout);
        this.setParentWindow(window);
      
        uploadZip = new CustomUploadField();
        uploadZip.setFieldType(UploadField.FieldType.FILE);
        uploadZip.setNoFileSelectedText("");
        uploadZip.setSelectedFileText("");
        uploadZip.setDeleteCaption(messageSource.getMessage(Message.CLEAR));
        uploadZip.setFileFactory(new CustomFileFactory());
        uploadZip.getRootLayout().setStyleName(BMS_UPLOAD_CONTAINER);
        uploadZip.getRootLayout().setWidth("100%");
        uploadZip.setButtonCaption(messageSource.getMessage(Message.BROWSE));
        uploadZip.setDeleteButtonListener(new DeleteButtonListener());
        
        uploadZipLabel = new Label(messageSource.getMessage(Message.BV_UPLOAD_ZIP), Label.CONTENT_XHTML);
        
        description = new Label(messageSource.getMessage(Message.BV_UPLOAD_DESCRIPTION), Label.CONTENT_XHTML);
        description.setStyleName(Bootstrap.Typography.TEXT_LEFT.styleName());
        
        layout.addComponent(description);
        layout.addComponent(uploadZipLabel);
        layout.addComponent(uploadZip);

        cancelButton = new Button("Cancel");
        uploadButton = new Button("Upload");
        uploadButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonArea = layoutButtonArea();
        
        layout.addComponent(buttonArea);
        layout.setComponentAlignment(buttonArea, Alignment.BOTTOM_CENTER);
        
    }

    protected void initializeLayout() {
        layout.setSpacing(true);
        layout.setMargin(true);
    }

    protected void initializeActions() {

        uploadButton.addListener(new UploadBreedingViewOutputAction(this));
        
        cancelButton.addListener(new CancelButtonListener());
        
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(uploadButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
      
    private final class DeleteButtonListener implements Button.ClickListener {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
			// do nothing
		}
	}

	private final class CancelButtonListener implements ClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(ClickEvent event) {
		    focus();
		    getParent().removeWindow(FileUploadBreedingViewOutputWindow.this);
		}
	}


	public class CustomUploadField extends UploadField {
    	
    	private static final long serialVersionUID = 1L;
    	
    	@Override
        public void uploadFinished(Upload.FinishedEvent event) {
             super.uploadFinished(event);
             LOG.debug("Upload is finished");
        }

		@Override
        public void validate() {
            if (!this.isValid()) {
                throw new Validator.InvalidValueException("NOT_VALID");
            }
        }

        @Override
        public boolean isValid() {
            return hasFileSelected() && getExtension(this.getLastFileName()).toLowerCase().contains("zip");
        }
        
        public boolean hasFileSelected() {
        	return this.getLastFileName() != null;
        }

        private String getExtension(String f){
            String ext = null;
            int i = f.lastIndexOf('.');

            if (i > 0 && i < f.length() - 1) {
                ext = f.substring(i + 1).toLowerCase();
            }

            if(ext == null) {
                return "";
            }
            return ext;
        }
    }
    
    public class CustomFileFactory implements FileFactory{

    	private static final String UPLOAD_DIR = "temp";
    	
    	private File file;
    	
		@Override
		public File createFile(String fileName, String mimeType) {
			File saveDir = new File(new File(UPLOAD_DIR).getAbsolutePath());
	        if (!saveDir.exists() || !saveDir.isDirectory()) {
	            saveDir.mkdirs();
	        }

	        StringBuilder sb = new StringBuilder();
	        if (new File(saveDir.getAbsolutePath() + "/" + fileName).exists()) {
	            for (int x = 1; x < 10000; x++) {
	                String temp = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + x + ".zip";
	                if (!new File(saveDir.getAbsolutePath() + "/" + temp).exists()) {
	                    sb.append(fileName.substring(0, fileName.lastIndexOf(".")));
	                    sb.append("_" + x + ".zip");
	                    break;
	                }
	            }
	        } else {
	            sb.append(fileName);
	        }

	        this.file = new File(saveDir, sb.toString());
	        return this.file;
		}
		
		public File getFile(){
			return this.file;
		}
    	
    }
    
    public CustomUploadField getUploadZip() {
		return uploadZip;
	}
    
    public Map<String, Boolean> getVariatesStateMap() {
		return variatesStateMap;
	}
	
	public int getStudyId() {
		return studyId;
	}
	
    public Project getProject() {
		return project;
	}

}
