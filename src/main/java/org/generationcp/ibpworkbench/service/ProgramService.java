
package org.generationcp.ibpworkbench.service;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserService;
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

@Service
@Transactional
public class ProgramService {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramService.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private UserService userService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LocationDataManager locationDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private org.generationcp.middleware.api.program.ProgramService programServiceMw;

	@Autowired
	private org.generationcp.commons.spring.util.ContextUtil contextUtil;

	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	/**
	 * Create new project in workbench. Finally, create a new folder under <install directory>/workspace/<program name>
	 *
	 * @param program : program to save
	 */
	public void createNewProgram(final Project program) {
		// Need to save first to workbench_project so project id can be saved in session
		this.saveWorkbenchProject(program);
		this.setContextInfoAndCurrentCrop(program);

		this.addUnspecifiedLocationToFavorite(program);

		// After saving, we create folder for program under <install directory>/workspace
		this.installationDirectoryUtil.createWorkspaceDirectoriesForProject(program);

		ProgramService.LOG.info(
				"Program created. ID:" + program.getProjectId() + " Name:" + program.getProjectName() + " Start date:" + program
						.getStartDate());
	}

	private void setContextInfoAndCurrentCrop(final Project program) {
		final Cookie authToken = WebUtils.getCookie(this.request, ContextConstants.PARAM_AUTH_TOKEN);
		ContextUtil.setContextInfo(this.request, this.contextUtil.getCurrentWorkbenchUserId(), program.getProjectId(),
				authToken != null ? authToken.getValue() : null);

		ContextHolder.setCurrentCrop(program.getCropType().getCropName());
		ContextHolder.setCurrentProgram(program.getUniqueID());
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

	void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setInstallationDirectoryUtil(final InstallationDirectoryUtil installationDirectoryUtil) {
		this.installationDirectoryUtil = installationDirectoryUtil;
	}

	public void updateMembersProjectUserInfo(final Collection<WorkbenchUser> userList, final Project project) {
		//Addition of new members
		for (final WorkbenchUser u : userList) {
			if (u.isEnabled()) {
				u.getRoles().stream().filter(ur -> ur.getId() == null && ur.getCreatedBy() == null)
					.forEach(ur -> ur.setCreatedBy(this.contextUtil.getCurrentWorkbenchUser()));

				final UserDto userDto = new UserDto(u);
				this.userService.updateUser(userDto);
			}
		}

		// Get the users with no association to the specified program.
		final List<Integer> userIdsToBeRemoved =
			this.getUsersNotAssociatedToSpecificProgram(project.getProjectId(), userList);
		if (!userIdsToBeRemoved.isEmpty()) {
			this.programServiceMw.removeProgramMembers(userIdsToBeRemoved, project.getUniqueID());
		}

	}

	protected List<Integer> getUsersNotAssociatedToSpecificProgram(final long projectId, final Collection<WorkbenchUser> workbenchUsers) {
		final List<Integer> activeUserIds = this.userService.getActiveUserIDsWithAccessToTheProgram(projectId);
		final List<Integer> userIdsOfUsersAssociatedToAProgram = new ArrayList<>();
		for (final WorkbenchUser user : workbenchUsers) {
			userIdsOfUsersAssociatedToAProgram.add(user.getUserid());
		}
		activeUserIds.removeAll(userIdsOfUsersAssociatedToAProgram);
		return activeUserIds;
	}

	public void addUnspecifiedLocationToFavorite(final Project program) {
		final String unspecifiedLocationID = this.locationDataManager.retrieveLocIdOfUnspecifiedLocation();
		if (!StringUtils.isEmpty(unspecifiedLocationID)) {
			final ProgramFavorite favorite = new ProgramFavorite();
			favorite.setEntityId(Integer.parseInt(unspecifiedLocationID));
			favorite.setEntityType(ProgramFavorite.FavoriteType.LOCATION.getName());
			favorite.setUniqueID(program.getUniqueID());
			this.germplasmDataManager.saveProgramFavorite(favorite);
		}
	}

}
