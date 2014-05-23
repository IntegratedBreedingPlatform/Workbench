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

import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Window;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class FileUploadFailedListener implements FailedListener{

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private static final long serialVersionUID = 1L;

    @Override
    public void uploadFailed(FailedEvent event) {
        Window window = event.getComponent().getWindow();

        MessageNotifier.showError(window, messageSource.getMessage(Message.UPLOAD_ERROR), 
                messageSource.getMessage(Message.UPLOAD_ERROR_DESC, event.getFilename()));
    }
}
