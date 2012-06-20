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

import org.generationcp.ibpworkbench.comp.FieldBookObservationPanel;
import org.generationcp.ibpworkbench.comp.vaadin.Upload;
import org.generationcp.ibpworkbench.comp.window.FileUploadWindow;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;

import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class FieldBookUploadSucceededListener implements SucceededListener{

    private static final long serialVersionUID = 1L;
    private FileUploadWindow window;

    public FieldBookUploadSucceededListener(FileUploadWindow window) {
        this.window = window;
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        Window parentWindow = this.window.getParent();

        // hide the upload window
        parentWindow.removeWindow(this.window);

        // show notification
        String description = "<br/>" + event.getFilename() + " uploaded.";
        parentWindow.showNotification("Upload Success", description, Notification.TYPE_TRAY_NOTIFICATION);

        IContentWindow contentWindow = (IContentWindow) parentWindow;

        Upload upload = (Upload) event.getSource();

        // display the table
        FieldBookObservationPanel panel = new FieldBookObservationPanel(upload.getUploadPath() + event.getFilename());
        contentWindow.showContent(panel);
    }
}
