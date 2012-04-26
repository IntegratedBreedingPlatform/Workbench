package org.generationcp.ibpworkbench.comp.window;

import com.vaadin.ui.Component;

/**
 * An {@link IContentWindow} is a window that provides a "content area".
 * 
 * @author Glenn Marintes
 */
public interface IContentWindow {
    
    public void showContent(Component content);
}
