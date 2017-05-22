package org.generationcp.ibpworkbench.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.annotation.Resource;

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
			boolean logProgramActivity = true;
			String url = "";
			Tool tool = this.workbenchDataManager.getToolWithName(toolName);

			if (tool == null) {
				throw new AppLaunchException(Message.LAUNCH_TOOL_ERROR.name());
			}

			// update the tool configuration if needed
			this.tomcatUtil.deployWebAppIfNecessary(tool);
			this.updateGermplasmStudyBrowserConfigurationIfNecessary(tool);

			switch (tool.getToolType()) {
				case NATIVE:
					this.launchNativeapp(tool);
					break;
				case WEB_WITH_LOGIN:
					url = this.launchWebappWithLogin(tool);
					break;
				case WEB:
					if ("migrator".equals(tool.getToolName())) {
						logProgramActivity = false;
						url = this.launchMigratorWebapp(tool, idParam);
					} else {
						url = this.launchWebapp(tool, idParam);
					}
					break;
				default:
					// empty default case
			}
			if (logProgramActivity) {
				// log project activity
				this.sessionData.logProgramActivity(tool.getTitle(), "Launched " + tool.getTitle());
			}
			return url;

		} catch (MiddlewareQueryException e) {
			throw new AppLaunchException(Message.DATABASE_ERROR.name(), new String[] {toolName}, e);
		}
	}

	protected void updateGermplasmStudyBrowserConfigurationIfNecessary(Tool tool) throws AppLaunchException, MiddlewareQueryException {
		// if user is trying to launch the FieldBook webapp,
		// and if the user is trying to launch the BreedingManager webapp
		// we need to reconfigure and deploy the GermplasmBrowser webapp
		if (Util.isOneOf(tool.getToolName(), ToolName.FIELDBOOK_WEB.name(), ToolName.NURSERY_MANAGER_FIELDBOOK_WEB.name(),
				ToolName.TRIAL_MANAGER_FIELDBOOK_WEB.name(), ToolName.ONTOLOGY_BROWSER_FIELDBOOK_WEB.name(),
				ToolName.BM_LIST_MANAGER.name(), ToolName.BM_LIST_MANAGER_MAIN.name(), ToolName.CROSSING_MANAGER.name(),
				ToolName.GERMPLASM_IMPORT.name(), ToolName.LIST_MANAGER.name(), ToolName.NURSERY_TEMPLATE_WIZARD.name())) {
			Tool germplasmBrowserTool = this.workbenchDataManager.getToolWithName(ToolName.GERMPLASM_BROWSER.name());
			this.tomcatUtil.deployWebAppIfNecessary(germplasmBrowserTool);
		}
	}

	protected void launchNativeapp(Tool tool) throws AppLaunchException {
		try {
			// close the native tool
			this.toolUtil.closeNativeTool(tool);
		} catch (IOException e) {
			AppLauncherService.LOG.warn(e.getMessage(), e);
		}

		try {
			if (tool.getToolName().equals(ToolEnum.BREEDING_VIEW.getToolName())) {
				// when launching BreedingView, update the web service tool first
				Tool webServiceTool = new Tool();
				webServiceTool.setToolName("ibpwebservice");
				webServiceTool.setPath(this.workbenchProperties.getProperty(AppLauncherService.WEB_SERVICE_URL_PROPERTY));
				webServiceTool.setToolType(ToolType.WEB);

				this.tomcatUtil.deployWebAppIfNecessary(webServiceTool);
			}

			this.toolUtil.launchNativeTool(tool);

		} catch (IOException e) {
			File absoluteToolFile = new File(tool.getPath()).getAbsoluteFile();
			throw new AppLaunchException(Message.LAUNCH_TOOL_ERROR_DESC.name(), new String[] {absoluteToolFile.getAbsolutePath()}, e);
		} 
	}

	protected String launchWebapp(Tool tool, Integer idParam) {
		return WorkbenchAppPathResolver.getWorkbenchAppPath(tool, String.valueOf(idParam),
				"?restartApplication" + this.sessionData.getWorkbenchContextParameters());
	}

	/**
	 * This method has been added to handle special case when there is no program inside workbench.
	 * So it will not check for session data and current selected project.
	 *
	 * @param tool    tool to be launched.
	 * @param idParam id
	 * @return path of the tool to be launched.
	 */
	protected String launchMigratorWebapp(Tool tool, Integer idParam) {
		return WorkbenchAppPathResolver.getWorkbenchAppPath(tool, String.valueOf(idParam));
	}

	protected String launchWebappWithLogin(Tool tool) {
		final String loginUrl = tool.getPath();
		final String params = "restartApplication&selectedProjectId=%s&loggedInUserId=%s";

		return WorkbenchAppPathResolver.getFullWebAddress(loginUrl,
				String.format(params, this.sessionData.getLastOpenedProject().getProjectId(), this.sessionData.getUserData().getUserid()));

	}

	public boolean isBMS3Installed() throws IOException {
		if (!System.getProperty("os.name").startsWith("Windows")) {
			return true;
		}

		Process p = Runtime.getRuntime().exec("sc query MysqlIBWS");
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		boolean isBMS3Installed = false;
		String line = reader.readLine();
		while (line != null) {
			if (line.trim().startsWith("STATE")) {
				isBMS3Installed = true;
			}
			line = reader.readLine();
		}

		return isBMS3Installed;
	}
}
