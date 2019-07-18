
package org.generationcp.ibpworkbench.service;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProgramService {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramService.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LocationDataManager locationDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private org.generationcp.commons.spring.util.ContextUtil contextUtil;

	// http://cropwiki.irri.org/icis/index.php/TDM_Users_and_Access
	public static final int PROJECT_USER_ACCESS_NUMBER = 100;
	public static final int PROJECT_USER_TYPE = 422;
	public static final int PROJECT_USER_STATUS = 1;

	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	/**
	 * Create new project in workbench and add specified users as project members. Also creates copy of workbench person and user to currect
	 * crop DB, if not yet existing. Finally, create a new folder under <install directory>/workspace/<program name>
	 *
	 * @param program : program to save
	 * @param programUsers : users to add as members of new program
	 */
	public void createNewProgram(final Project program, final Set<WorkbenchUser> programUsers) {
		// Need to save first to workbench_project so project id can be saved in session
		this.saveWorkbenchProject(program);
		this.setContextInfoAndCurrentCrop(program);

		this.saveProgramMembers(program, programUsers);
		this.addUnspecifiedLocationToFavorite(program);

		// After saving, we create folder for program under <install directory>/workspace
		this.installationDirectoryUtil.createWorkspaceDirectoriesForProject(program);

		ProgramService.LOG.info("Program created. ID:" + program.getProjectId() + " Name:" + program.getProjectName() + " Start date:"
				+ program.getStartDate());
	}

	/**
	 * Save user(s) with SUPERADMIN role and other selected users as members of given program by saving in the ff tables:
	 * workbench_project_user_role, workbench_project_user_info and in crop.persons (if applicable)
	 *
	 * @param program : program to add members to
	 * @param users : users to add as members of given program
	 */
	public void saveProgramMembers(final Project program, final Set<WorkbenchUser> users) {
		// Add user(s) with SUPERADMIN role to selected users of program to give access to new program
		final List<WorkbenchUser> superAdminUsers = this.workbenchDataManager.getSuperAdminUsers();
		users.addAll(superAdminUsers);

		// Save workbench project metadata and to crop users, persons (if necessary)
		if (!users.isEmpty()) {

			this.saveProjectUserInfo(program, users);
		}
	}

	private void setContextInfoAndCurrentCrop(final Project program) {
		final Cookie authToken = WebUtils.getCookie(this.request, ContextConstants.PARAM_AUTH_TOKEN);
		ContextUtil.setContextInfo(this.request, this.contextUtil.getCurrentWorkbenchUserId(), program.getProjectId(),
				authToken != null ? authToken.getValue() : null);

		ContextHolder.setCurrentCrop(program.getCropType().getCropName());
		ContextHolder.setCurrentProgram(program.getUniqueID());
	}

	/*
	 * Create records for workbench_project_user_info table if combination of project id, user id is not yet existing in workbench DB
	 */
	private void saveProjectUserInfo(final Project program,  final Set<WorkbenchUser> users) {
		for (final WorkbenchUser user : users) {
			final Integer userID = user.getUserid();
			final Long projectID = program.getProjectId();

			if (this.workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(projectID, userID) == null) {
				final ProjectUserInfo pUserInfo = new ProjectUserInfo(program, userID);
				this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
			}
		}
	}

	/*
	 * Create new record in workbench_project table in workbench DB for current crop.
	 *
	 * @param program
	 */
	private void saveWorkbenchProject(final Project program) {
		// sets current user as program owner
		program.setUserId(this.contextUtil.getCurrentWorkbenchUserId());

		final CropType cropType = this.workbenchDataManager.getCropTypeByName(program.getCropType().getCropName());
		if (cropType == null) {
			this.workbenchDataManager.addCropType(program.getCropType());
		}
		program.setLastOpenDate(null);

		this.workbenchDataManager.addProject(program);
	}

	private Integer getCurrentDate() {
		return DateUtil.getCurrentDateAsIntegerValue();
	}

	void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setInstallationDirectoryUtil(final InstallationDirectoryUtil installationDirectoryUtil) {
		this.installationDirectoryUtil = installationDirectoryUtil;
	}
	
	public void updateMembersUserInfo(final Collection<WorkbenchUser> userList, final Project project) {
		//Addition of new members
		for (WorkbenchUser u : userList) {
			if (this.workbenchDataManager.getProjectUserInfoByProjectIdAndUserId(project.getProjectId(),
					u.getUserid()) == null) {
				ProjectUserInfo pUserInfo = new ProjectUserInfo(project, u.getUserid());
				this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
			}
		}

		//Removal of users
		final List<Integer> removedUserIds = this.getRemovedUserIds(project.getProjectId(), userList);
		if(!removedUserIds.isEmpty()) {
			this.workbenchDataManager.removeUsersFromProgram(removedUserIds, project.getProjectId());
		}
	}
	
	public List<Integer> getRemovedUserIds(final long projectId, final Collection<WorkbenchUser> userList) {
		final List<Integer> activeUserIds = this.workbenchDataManager.getActiveUserIDsByProjectId(projectId);
		List<Integer> programMemberIds = new ArrayList<>();
		for(WorkbenchUser user: userList) {
			programMemberIds.add(user.getUserid());
		}
		activeUserIds.removeAll(programMemberIds);
		return activeUserIds;
	}

	public void addUnspecifiedLocationToFavorite(final Project program) {
		final String unspecifiedLocationID = this.locationDataManager.retrieveLocIdOfUnspecifiedLocation();
		if(!StringUtils.isEmpty(unspecifiedLocationID)){
			final ProgramFavorite favorite = new ProgramFavorite();
			favorite.setEntityId(Integer.parseInt(unspecifiedLocationID));
			favorite.setEntityType(ProgramFavorite.FavoriteType.LOCATION.getName());
			favorite.setUniqueID(program.getUniqueID());
			this.germplasmDataManager.saveProgramFavorite(favorite);
		}
	}
}
