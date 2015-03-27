package org.generationcp.ibpworkbench.service;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.tomcat.util.TomcatUtil;
import org.generationcp.commons.util.Util;
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.exception.AppLaunchException;
import org.generationcp.ibpworkbench.exception.ConfigurationChangeException;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by cyrus on 3/4/15.
 */
public class AppLauncherService {
	public static final String WEB_SERVICE_URL_PROPERTY = "bv.web.url";
	private final static Logger LOG = LoggerFactory.getLogger(AppLauncherService.class);

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private ToolUtil toolUtil;

	@Resource
	private TomcatUtil tomcatUtil;

	@Resource
	private Properties workbenchProperties;

	@Resource
	private SessionData sessionData;



	public String launchTool(String toolName, Integer idParam) throws AppLaunchException {
		try {
			String url = "";
			Tool tool = workbenchDataManager.getToolWithName(toolName);

			if (tool == null) {
				throw new AppLaunchException(Message.LAUNCH_TOOL_ERROR.name());
			}

			// update the tool configuration if needed
			tomcatUtil.deployWebAppIfNecessary(tool);
			updateGermplasmStudyBrowserConfigurationIfNecessary(tool);

			switch (tool.getToolType()) {
			case NATIVE:
				this.launchNativeapp(tool);
				break;
			case WEB_WITH_LOGIN:
				url = this.launchWebappWithLogin(tool);
				break;
			case WEB:
				url = this.launchWebapp(tool, idParam);
			}

			// log proj act
			sessionData.logProgramActivity(tool.getTitle(), "Launched " + tool.getTitle());

			return url;

		} catch (MiddlewareQueryException e) {
			throw new AppLaunchException(Message.DATABASE_ERROR.name(), new String[] { toolName },
					e);
		}
	}

	protected void updateGermplasmStudyBrowserConfigurationIfNecessary(Tool tool)
			throws AppLaunchException, MiddlewareQueryException {
		// if user is trying to launch the FieldBook webapp,
		// and if the user is trying to launch the BreedingManager webapp
		// we need to reconfigure and deploy the GermplasmBrowser webapp
		if (Util.isOneOf(tool.getToolName()
				, ToolName.fieldbook_web.name()
				, ToolName.nursery_manager_fieldbook_web.name()
				, ToolName.trial_manager_fieldbook_web.name()
				, ToolName.ontology_browser_fieldbook_web.name()
				, ToolName.bm_list_manager.name()
				, ToolName.bm_list_manager_main.name()
				, ToolName.crossing_manager.name()
				, ToolName.germplasm_import.name()
				, ToolName.list_manager.name()
				, ToolName.nursery_template_wizard.name()
		)) {
			Tool germplasmBrowserTool = workbenchDataManager
					.getToolWithName(ToolName.germplasm_browser.name());
			tomcatUtil.deployWebAppIfNecessary(germplasmBrowserTool);
		}
	}

	protected void launchNativeapp(Tool tool) throws AppLaunchException {
		try {
			// close the native tool
			toolUtil.closeNativeTool(tool);
		} catch (IOException e) {
			LOG.info(e.getMessage(), e);
		}

		try {
			if (tool.getToolName().equals(ToolEnum.BREEDING_VIEW.getToolName())) {
				// when launching BreedingView, update the web service tool first
				Tool webServiceTool = new Tool();
				webServiceTool.setToolName("ibpwebservice");
				webServiceTool.setPath(workbenchProperties.getProperty(WEB_SERVICE_URL_PROPERTY));
				webServiceTool.setToolType(ToolType.WEB);

				tomcatUtil.deployWebAppIfNecessary(webServiceTool);
			}

			// this should only affect MBDT
			toolUtil.updateToolConfigurationForProject(tool, sessionData.getLastOpenedProject());

			toolUtil.launchNativeTool(tool);

		} catch (IOException e) {
			File absoluteToolFile = new File(tool.getPath()).getAbsoluteFile();
			throw new AppLaunchException(Message.LAUNCH_TOOL_ERROR_DESC.name(),
					new String[] { absoluteToolFile.getAbsolutePath() }, e);
		} catch (ConfigurationChangeException e) {
			LOG.debug(e.getMessage(),e);
		}
	}

	protected String launchWebapp(Tool tool, Integer idParam) {
		return WorkbenchAppPathResolver.getWorkbenchAppPath(tool, String.valueOf(idParam),
				"?restartApplication" + sessionData.getWorkbenchContextParameters());
	}

	protected String launchWebappWithLogin(Tool tool) {
		final String loginUrl = tool.getPath();
		final String params = "restartApplication&selectedProjectId=%s&loggedInUserId=%s";

		return WorkbenchAppPathResolver.getFullWebAddress(loginUrl,
				String.format(params, sessionData.getLastOpenedProject().getProjectId(), sessionData.getUserData().getUserid()));

	}
}
