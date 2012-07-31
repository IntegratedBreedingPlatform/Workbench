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

package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.FieldBookObservationPanel;
import org.generationcp.ibpworkbench.comp.vaadin.Upload;
import org.generationcp.ibpworkbench.comp.window.FileUploadWindow;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;

@Configurable
public class FieldBookUploadSucceededListener implements SucceededListener{

    private static final long serialVersionUID = 1L;
    private FileUploadWindow window;
    
    private static final Logger LOG = LoggerFactory.getLogger(FieldBookUploadSucceededListener.class);
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public FieldBookUploadSucceededListener(FileUploadWindow window) {
        this.window = window;
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        Window parentWindow = this.window.getParent();

        // hide the upload window
        parentWindow.removeWindow(this.window);

        // show notification
        MessageNotifier.showTrayNotification(parentWindow, messageSource.getMessage(Message.UPLOAD_SUCCESS),
                messageSource.getMessage(Message.UPLOAD_SUCCESS_DESC, event.getFilename()));

        IContentWindow contentWindow = (IContentWindow) parentWindow;

        Upload upload = (Upload) event.getSource();

        // display the table
        FieldBookObservationPanel panel = null;
        try {
            panel = new FieldBookObservationPanel(upload.getUploadPath() + event.getFilename());
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(parentWindow, i.getCaption(), i.getDescription());
            }
            return;
        }
        
        contentWindow.showContent(panel);
    }
}
