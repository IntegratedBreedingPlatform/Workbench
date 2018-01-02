
package org.generationcp.browser.study.listeners;

import javax.annotation.Resource;

import org.generationcp.commons.constant.DefaultGermplasmStudyBrowserPath;
import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.ShortcutAction;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

@Configurable
public class ViewStudyDetailsButtonClickListener implements ClickListener {

	private static final long serialVersionUID = -2009510049166285893L;
	private static final Logger LOG = LoggerFactory.getLogger(ViewStudyDetailsButtonClickListener.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private org.generationcp.commons.spring.util.ContextUtil contextUtil;

	private final int studyId;
	private final String studyName;

	public ViewStudyDetailsButtonClickListener(int studyId, String studyName) {
		super();
		this.studyId = studyId;
		this.studyName = studyName;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Tool tool = null;
		try {
			tool = this.workbenchDataManager.getToolWithName(ToolName.STUDY_BROWSER_WITH_ID.toString());
		} catch (MiddlewareQueryException qe) {
			ViewStudyDetailsButtonClickListener.LOG.error("QueryException", qe);
		}

		String contextParameterString =
				ContextUtil.getContextParameterString(contextUtil.getCurrentWorkbenchUserId(), contextUtil.getProjectInContext().getProjectId());

		String authenticationTokenString = ContextUtil.addQueryParameter(ContextConstants.PARAM_AUTH_TOKEN, SecurityUtil.getEncodedToken());

		String addtlParams = contextParameterString + authenticationTokenString;
		ExternalResource studyLink;
		if (tool == null) {
			studyLink =
					new ExternalResource(WorkbenchAppPathResolver.getFullWebAddress(DefaultGermplasmStudyBrowserPath.STUDY_BROWSER_LINK
							+ this.studyId, "?restartApplication" + addtlParams));
		} else {
			studyLink =
					new ExternalResource(WorkbenchAppPathResolver.getWorkbenchAppPath(tool, String.valueOf(this.studyId),
							"?restartApplication" + addtlParams));
		}
		ViewStudyDetailsButtonClickListener.LOG.debug(studyLink.getURL());
		this.renderStudyDetailsWindow(studyLink, event.getComponent().getWindow());

	}

	protected void renderStudyDetailsWindow(ExternalResource studyLink, Window window) {
		String windowTitle = "Study Information: " + this.studyName;
		final Window studyWindow = new BaseSubWindow(windowTitle);
		final Embedded studyInfo = new Embedded(null, studyLink);
		studyInfo.setDebugId("studyInfo");
		studyInfo.setType(Embedded.TYPE_BROWSER);
		studyInfo.setSizeFull();

		AbsoluteLayout layoutForStudy = new AbsoluteLayout();
		layoutForStudy.setDebugId("layoutForStudy");
		layoutForStudy.setMargin(false);
		layoutForStudy.setWidth("100%");
		layoutForStudy.setHeight("100%");
		layoutForStudy.addStyleName("no-caption");
		layoutForStudy.addComponent(studyInfo, "top:0; left:0;");

		studyWindow.setContent(layoutForStudy);
		studyWindow.setWidth("90%");
		studyWindow.setHeight("90%");
		studyWindow.center();
		studyWindow.setResizable(false);
		studyWindow.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);

		studyWindow.setModal(true);
		studyWindow.addStyleName("graybg");

		window.addWindow(studyWindow);
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
}
