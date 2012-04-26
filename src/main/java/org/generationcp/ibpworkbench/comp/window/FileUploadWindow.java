package org.generationcp.ibpworkbench.comp.window;

import org.generationcp.ibpworkbench.comp.vaadin.Upload;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FileUploadWindow extends Window {
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
