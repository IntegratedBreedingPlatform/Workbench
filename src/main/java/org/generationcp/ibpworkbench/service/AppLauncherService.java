package org.generationcp.ibpworkbench.service;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.exception.AppLaunchException;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * Created by cyrus on 3/4/15.
 */
public class AppLauncherService {

	private static final Logger LOG = LoggerFactory.getLogger(AppLauncherService.class);
	public static final String LAUNCHED = "Launched ";

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private ToolUtil toolUtil;

	@Resource
	private ContextUtil contextUtil;

	public String launchTool(String toolName, Integer idParam) throws AppLaunchException {

		boolean logProgramActivity = true;
		String url = "";
		Tool tool = this.workbenchDataManager.getToolWithName(toolName);

		if (tool == null) {
			throw new AppLaunchException(Message.LAUNCH_TOOL_ERROR.name());
		}

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
			this.contextUtil.logProgramActivity(tool.getTitle(), LAUNCHED + tool.getTitle());
		}


		return url;

	}

	protected void launchNativeapp(Tool tool) throws AppLaunchException {
		try {
			// close the native tool
			this.toolUtil.closeNativeTool(tool);
		} catch (IOException e) {
			AppLauncherService.LOG.warn(e.getMessage(), e);
		}

		try {
			this.toolUtil.launchNativeTool(tool);
		} catch (IOException e) {
			File absoluteToolFile = new File(tool.getPath()).getAbsoluteFile();
			throw new AppLaunchException(Message.LAUNCH_TOOL_ERROR_DESC.name(), new String[] {absoluteToolFile.getAbsolutePath()}, e);
		}
	}

	protected String launchWebapp(Tool tool, Integer idParam) {

		String contextParameterString =
				org.generationcp.commons.util.ContextUtil.getContextParameterString(this.contextUtil.getContextInfoFromSession());

		String authenticationTokenString = org.generationcp.commons.util.ContextUtil
				.addQueryParameter(ContextConstants.PARAM_AUTH_TOKEN, SecurityUtil.getEncodedToken());

		Project project = workbenchDataManager.getProjectById(this.contextUtil.getContextInfoFromSession().getSelectedProjectId());

		String CurrentProgramUUID = org.generationcp.commons.util.ContextUtil
			.addQueryParameter("programUUID", project.getUniqueID());

		String cropName = org.generationcp.commons.util.ContextUtil
			.addQueryParameter("cropName", project.getCropType().getCropName());

		return WorkbenchAppPathResolver.getWorkbenchAppPath(tool, String.valueOf(idParam),
			"?restartApplication" + contextParameterString + authenticationTokenString + cropName + CurrentProgramUUID);

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
		final String params = "restartApplication%s";

		return WorkbenchAppPathResolver.getFullWebAddress(loginUrl, String.format(params,
				org.generationcp.commons.util.ContextUtil.getContextParameterString(this.contextUtil.getContextInfoFromSession())));

	}

}
