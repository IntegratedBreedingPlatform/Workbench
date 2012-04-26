package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.comp.CreateNewProjectPanel;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;

public class OpenNewProjectAction implements ClickListener {
    private static final long serialVersionUID = 1L;

    @Override
    public void buttonClick(ClickEvent event) {
        Component component = event.getComponent();
        IContentWindow window = (IContentWindow) component.getWindow();
        
        CreateNewProjectPanel newProjectPanel = new CreateNewProjectPanel();
        newProjectPanel.setWidth("480px");
        window.showContent(newProjectPanel);
        
        newProjectPanel.getSaveButton().addListener(new SaveNewProjectAction(newProjectPanel.getForm()));
    }

}
