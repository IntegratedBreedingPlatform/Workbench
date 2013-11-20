package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.ui.window.WorkflowPreviewWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class OpenWorkflowPreviewAction implements ClickListener {
    
    private static final long serialVersionUID = -512415075117700453L;

    private static final Logger LOG = LoggerFactory.getLogger(OpenWorkflowPreviewAction.class);
    
    public OpenWorkflowPreviewAction() {
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        Button showButton = (Button) event.getComponent();
        
        WorkflowPreviewWindow popupWindow = new WorkflowPreviewWindow((Integer) showButton.getData());
        showButton.getWindow().addWindow(popupWindow);
    }
}
