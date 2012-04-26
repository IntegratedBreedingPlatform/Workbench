package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class FileUploadFailedListener implements FailedListener {
    private static final long serialVersionUID = 1L;

    @Override
    public void uploadFailed(FailedEvent event) {
        Window window = event.getComponent().getWindow();
        
        String description = "<br/>" + event.getFilename() + " cannot be uploaded.<br/>"
                           + "Perhaps the file is in a wrong format?<br/>"
                           + "Try uploading again.";
        window.showNotification("Upload Failed", description, Notification.TYPE_ERROR_MESSAGE);
    }
}
