/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.dashboard.listener;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.security.AuthorizationService;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.service.api.permission.PermissionService;
import org.generationcp.middleware.service.api.permission.PermissionServiceImpl;
import org.generationcp.middleware.service.api.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Configurable
public class LaunchProgramAction implements ItemClickListener, ClickListener {

	private static final long serialVersionUID = 5742093045098439073L;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ContextUtil contextUtil;

	@Autowired
	private AuthorizationService authorizationService;

	private static final Logger LOG = LoggerFactory.getLogger(LaunchProgramAction.class);

	private Project selectedProgram;

	private WorkbenchMainView workbenchMainView;

	public LaunchProgramAction() {
		super();
	}

	public LaunchProgramAction(final Project selectedProgram) {
		this.selectedProgram = selectedProgram;
	}

	void openSelectedProgram(final Project project, final Window window) {
		try {
			final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);

			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(final TransactionStatus status) {

					// Sets selected program/project to context
					org.generationcp.commons.util.ContextUtil
							.setContextInfo(LaunchProgramAction.this.request, LaunchProgramAction.this.contextUtil.getCurrentWorkbenchUserId(), project.getProjectId(), null);

					LaunchProgramAction.this.updateProjectLastOpenedDate(project);

					// Set project name to header
					LaunchProgramAction.this.workbenchMainView = (WorkbenchMainView) window;
					if (LaunchProgramAction.this.workbenchMainView.getSidebar() == null) {
						LaunchProgramAction.this.workbenchMainView.setSidebar(new WorkbenchSidebar());
					}

					final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> sidebarMenu =
						LaunchProgramAction.this.getSidebarMenu( //
							LaunchProgramAction.this.contextUtil.getCurrentWorkbenchUserId(), //
							project.getCropType().getCropName(), //
							Integer.valueOf(project.getProjectId().toString()));

					LaunchProgramAction.this.workbenchMainView.getSidebar().populateLinks(sidebarMenu);
					LaunchProgramAction.this.workbenchMainView.addTitle(project.getProjectName());

					// Set the first tool the user has access to
					final ToolName firstAvailableTool = getFirstAvailableTool(sidebarMenu);

					if (firstAvailableTool == null) {
						MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR),
							messageSource.getMessage(Message.LAUNCH_TOOL_ERROR_NO_TOOL));
						return;
					}

					// update sidebar selection
					LaunchProgramAction.LOG.trace("selecting sidebar");
					if (WorkbenchSidebar.sidebarTreeMap.get(firstAvailableTool.getName()) != null) {
						LaunchProgramAction.this.workbenchMainView.getSidebar()
							.selectItem(WorkbenchSidebar.sidebarTreeMap.get(firstAvailableTool.getName()));
					}

					final ActionListener listener =
						LaunchProgramAction.this.workbenchMainView.getSidebar().getLinkActions(firstAvailableTool.getName(),
							project);

					listener.doAction(workbenchMainView, "/" + firstAvailableTool.getName(), true);

					authorizationService.reloadAuthorities(project);

				}
			});
		} catch (final InternationalizableException e) {
			LaunchProgramAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(window, e.getCaption(), e.getDescription());
		} catch (final Exception e) {
			LaunchProgramAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(window, "", e.getLocalizedMessage());

		}
	}

	private ToolName getFirstAvailableTool(final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> sidebarMenu) {

		if (sidebarMenu != null) {
			for (final List<WorkbenchSidebarCategoryLink> links : sidebarMenu.values()) {
				for (final WorkbenchSidebarCategoryLink link : links) {
					if (link.getTool() != null) {
						return ToolName.equivalentToolEnum(link.getTool().getToolName());
					}
				}
			}
		}
		return null;
	}

	final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> getSidebarMenu(
		final Integer userId, final String cropName, final Integer programId) {
		final List<PermissionDto> permissions =
			this.permissionService.getPermissionLinks(userId, cropName, programId);
		// get all categories first
		final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> categoryMap = new LinkedHashMap<>();
		final Set<WorkbenchSidebarCategoryLink> links = new HashSet<>();

		for (final PermissionDto permission : permissions) {
			final WorkbenchSidebarCategoryLink link =
				this.workbenchDataManager.getWorkbenchSidebarLinksByCategoryId(permission.getWorkbenchCategoryLinkId());
			if (link != null && !links.contains(link)) {
				if (categoryMap.get(link.getWorkbenchSidebarCategory()) == null) {
					categoryMap.put(link.getWorkbenchSidebarCategory(), new ArrayList<>());
				}
				if (link.getTool() == null) {
					link.setTool(new Tool(link.getSidebarLinkName(), link.getSidebarLinkTitle(), ""));
				}
				categoryMap.get(link.getWorkbenchSidebarCategory()).add(link);
				links.add(link);
			}
		}

		//Convert HashMap to TreeMap.It will be sorted in natural order.
		final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> treeMap = new TreeMap<>( categoryMap );

		//sorting the list with a comparator
		for (final WorkbenchSidebarCategory category : treeMap.keySet()) {
			Collections.sort(categoryMap.get(category));
		}

		return treeMap;
	}

	/**
	 * Updates last opened project for user in DB
	 *
	 * @param project : the program selected in dashboard
	 */
	void updateProjectLastOpenedDate(final Project project) {

		final ProjectUserInfo projectUserInfo =
				this.userService.getProjectUserInfoByProjectIdAndUserId(project.getProjectId(), this.contextUtil.getCurrentWorkbenchUserId());
		if (projectUserInfo != null) {
			projectUserInfo.setLastOpenDate(new Date());
			this.userService.saveOrUpdateProjectUserInfo(projectUserInfo);
		}
		else {
				final ProjectUserInfo pUserInfo = new ProjectUserInfo(project, this.contextUtil.getCurrentWorkbenchUser());
				pUserInfo.setLastOpenDate(new Date());
				this.userService.saveOrUpdateProjectUserInfo(pUserInfo);
		}

		project.setLastOpenDate(new Date());
		this.workbenchDataManager.mergeProject(project);

	}

	@Override
	public void itemClick(final ItemClickEvent event) {
		final Project project = (Project) event.getItemId();
		this.openSelectedProgram(project, event.getComponent().getWindow());
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		if (this.selectedProgram != null) {
			this.openSelectedProgram(this.selectedProgram, event.getComponent().getWindow());
		}

	}

	public void setTransactionManager(final PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}

	public void setPermissionService(final PermissionServiceImpl permissionService) {
		this.permissionService = permissionService;
	}

	public void setWindow(final WorkbenchMainView window) {
		this.workbenchMainView = window;
	}
}
