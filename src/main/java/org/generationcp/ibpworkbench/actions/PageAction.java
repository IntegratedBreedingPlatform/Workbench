package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import org.apache.commons.lang.NotImplementedException;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;

/**
 * Created by cyrus on 7/29/15.
 */
public class PageAction implements ActionListener {

	private final String pageUrl;

	public PageAction(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	@Override
	public void doAction(Component.Event event) {
		throw new NotImplementedException();
	}

	@Override
	public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
		final IContentWindow contentFrame = (IContentWindow) window;
		if (!"".equals(uriFragment)) {
			contentFrame.showContent(pageUrl);
		}
	}
}
