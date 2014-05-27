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

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.ibpworkbench.ui.vaadin.Upload;

public class FileUploadWindow extends Window{

    private static final long serialVersionUID = 1L;

    private Upload upload;

    public FileUploadWindow() {
        super("Upload File");

        assemble();
    }

    public FileUploadWindow(String caption) {
        super(caption);

        assemble();
    }

    public Upload getUpload() {
        return upload;
    }

    protected void initializeComponents() {
        upload = new Upload("Upload File");
        upload.setUploadPath("c:/tmp/uploads/");
    }

    protected void initializeLayout() {
        this.addStyleName(Reindeer.WINDOW_LIGHT);


        setWidth("320px");
        setHeight("240px");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);

        Component contentArea = layoutContentArea();
        contentArea.setSizeFull();
        layout.addComponent(contentArea);
        layout.setExpandRatio(contentArea, 1.0f);

        setContent(layout);
    }

    protected Component layoutContentArea() {
        VerticalLayout layout = new VerticalLayout();

        layout.addComponent(upload);

        return layout;
    }

    protected void initializeActions() {
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}
