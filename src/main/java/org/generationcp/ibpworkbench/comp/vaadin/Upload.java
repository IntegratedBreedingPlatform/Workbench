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

package org.generationcp.ibpworkbench.comp.vaadin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Upload.Receiver;

public class Upload extends com.vaadin.ui.Upload implements Receiver{

    private static final long serialVersionUID = 1L;

    private String uploadPath = "./";

    private List<String> allowedMimeTypes;

    public Upload() {
    }

    public Upload(String caption) {
        setCaption(caption);
        setReceiver(this);
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        if (!uploadPath.endsWith(File.separator)) {
            uploadPath += File.separator;
        }

        this.uploadPath = uploadPath;
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
            e.printStackTrace();
            return null;
        }
    }
}
