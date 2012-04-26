package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

public class CloseWindowAction implements ClickListener {
    private static final long serialVersionUID = 1L;

    @Override
    public void buttonClick(ClickEvent event) {
        Window window = event.getButton().getWindow();
        window.getParent().removeWindow(window);
    }
}
