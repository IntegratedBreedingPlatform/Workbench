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

package org.generationcp.ibpworkbench.ui.vaadin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Upload.Receiver;

@Configurable
public class Upload extends com.vaadin.ui.Upload implements Receiver{

    private static final Logger LOG = LoggerFactory.getLogger(Upload.class);
    private static final long serialVersionUID = 1L;

    private String uploadPath = "./";

    private List<String> allowedMimeTypes;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public Upload() {
        super();
    }

    public Upload(String caption) {
        super();
        setCaption(caption);
        setReceiver(this);
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        String newUploadPath = uploadPath;
        if (!uploadPath.endsWith(File.separator)) {
            newUploadPath += File.separator;
        }
        this.uploadPath = newUploadPath;
    }

    public List<String> getAllowedMimeTypes() {
        return allowedMimeTypes;
    }

    public void setAllowedMimeTypes(List<String> allowedContentTypes) {
        this.allowedMimeTypes = new ArrayList<String>(allowedContentTypes);
    }

    public void addAllowedMimeType(String contentType) {
        if (allowedMimeTypes == null) {
            allowedMimeTypes = new ArrayList<String>();
        }
        allowedMimeTypes.add(contentType);
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        // check mime type
        boolean allowed = false;
        for (String allowedMime : allowedMimeTypes) {
            if (allowedMime.equals(mimeType)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            return null;
        }

        // TODO: Check if we are going to encounter a race condition here,
        // when two users try to upload a file with the same filename.
        File file = new File(uploadPath + filename);
        try {
            // Open the file for writing.
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            // Error while opening the file. Not reported here.
            LOG.error("FileNotFoundException", e);
            MessageNotifier.showError(getWindow(), 
                    messageSource.getMessage(Message.FILE_NOT_FOUND_ERROR), 
                    "<br />" + messageSource.getMessage(Message.FILE_NOT_FOUND_ERROR_DESC, 
                            uploadPath + filename));
            return null;
        }
    }
}
