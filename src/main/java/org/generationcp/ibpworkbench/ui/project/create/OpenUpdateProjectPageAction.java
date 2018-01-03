
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Date;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 10/28/13 Time: 12:40 PM To change this template use File | Settings | File Templates.
 */
@Configurable
public class OpenUpdateProjectPageAction implements Button.ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ContextUtil contextUtil;

	@Override
	public void buttonClick(Button.ClickEvent event) {
		this.doAction(event.getComponent().getWindow(), null, true);
	}

	@Override
	public void doAction(Component.Event event) {
		// currently does nothing
	}

	@Override
	public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {

		final IContentWindow w = (IContentWindow) window;
		UpdateProjectPanel projectPanel = new UpdateProjectPanel();
		projectPanel.setDebugId("projectPanel");
		w.showContent(projectPanel);

		contextUtil.logProgramActivity("Update Program", "Launched Update Program");


	}

}
