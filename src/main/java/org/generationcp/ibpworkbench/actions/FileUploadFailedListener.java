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
import com.vaadin.ui.Window.Notification;

public class FileUploadFailedListener implements FailedListener{

    private static final long serialVersionUID = 1L;

    @Override
    public void uploadFailed(FailedEvent event) {
        Window window = event.getComponent().getWindow();

        String description = "<br/>" + event.getFilename() + " cannot be uploaded.<br/>" + "Perhaps the file is in a wrong format?<br/>"
                + "Try uploading again.";
        window.showNotification("Upload Failed", description, Notification.TYPE_ERROR_MESSAGE);
    }
}
