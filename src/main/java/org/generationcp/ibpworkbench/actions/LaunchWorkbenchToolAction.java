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
package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;
import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.exception.AppLaunchException;
import org.generationcp.ibpworkbench.navigation.UriUtils;
import org.generationcp.ibpworkbench.service.AppLauncherService;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configurable
public class LaunchWorkbenchToolAction implements WorkflowConstants, ClickListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private final static Logger LOG = LoggerFactory.getLogger(LaunchWorkbenchToolAction.class);
	private ToolEnum toolEnum;

	/**
	 * An id passed to the tool.
	 * We assume that the tool recognizes:
	 * <tool url>-idParam
	 * format and gets the id parameter from there.
	 * This is used for Germplasm Browser and Study Browser tools at the moment.
	 * This is a very dirty implementation.
	 * I wish we could do better but that is what we have for now.
	 */
	private Integer idParam;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private Properties workbenchProperties;

	@Resource
	private AppLauncherService appLauncherService;

	public LaunchWorkbenchToolAction() {
	}

	public LaunchWorkbenchToolAction(ToolEnum toolEnum) {
		this.toolEnum = toolEnum;
	}

	public LaunchWorkbenchToolAction(ToolEnum toolEnum, int idParam) {
		this(toolEnum);
		this.idParam = idParam;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		onAppLaunch(event.getComponent().getWindow());
	}

	@Override
	public void doAction(Event event) {
		// does nothing
	}

	@Override
	public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {

		String a = uriFragment.split("/")[1];

		String toolName = (a).split("\\?")[0];

		Map<String, List<String>> params = UriUtils.getUriParameters(uriFragment);

		this.toolEnum = ToolEnum.equivalentToolEnum(toolName);

		if (uriFragment.equals("/breeding_view")) {
			new ChangeWindowAction().doAction(window, "breeding_view", isLinkAccessed);
			return;
		}
		if (uriFragment.equals("/BreedingGxE")) {
			new ChangeWindowAction().doAction(window, "BreedingGxE", isLinkAccessed);
			return;
		}
		if (uriFragment.equals("/ProjectMembers")) {
			new ChangeWindowAction().doAction(window, "ProjectMembers", isLinkAccessed);
			return;
		}

		if (this.toolEnum != null) {
			this.onAppLaunch(window);
		} else {
			LOG.debug("Cannot launch tool due to invalid tool: {}", uriFragment.split("/")[1]);
			MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR),
					messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC,
							Arrays.asList(uriFragment.split("/")[1]).toArray()));
		}

	}

	protected void onAppLaunch(Window window) {
		try {
			final IContentWindow contentFrame = ((IContentWindow) window);
			String url = appLauncherService.launchTool(toolEnum.getToolName(), idParam);
			if (!"".equals(url)) {
				contentFrame.showContent(url);
			}
		} catch (AppLaunchException e) {

			MessageNotifier.showError(window,
					messageSource.getMessage(Message.LAUNCH_TOOL_ERROR),
					"<br />" + messageSource
							.getMessage(e.getMessage(), (Object[])e.getParams()));
		}
	}

	public Properties getWorkbenchProperties() {
		return workbenchProperties;
	}

	public void setWorkbenchProperties(Properties workbenchProperties) {
		this.workbenchProperties = workbenchProperties;
	}
}
