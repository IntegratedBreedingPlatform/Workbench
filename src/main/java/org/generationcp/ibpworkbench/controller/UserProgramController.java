package org.generationcp.ibpworkbench.controller;

import org.generationcp.commons.security.AuthorizationService;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(UserProgramController.URL)
public class UserProgramController {

	public static final String URL = "/userProgramController/";

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private AuthorizationService authorizationService;

	@Resource
	private HttpServletRequest request;

	/* This controller was implemented in IBP-4421 fixing an authentication issue.
	   As a part of the IBP-4397 was implemented the APIs to return the last project user selected and save the project selected by the user.
	   Also was implement ParamContext in AccountService and RouteAccessService
	   to set the crop and programUUID and recover the user authorities to validate the access.
	   Remove after verifying that no other module depends on these values being in context info.

	   IBP-4829 I reuse the controller to set the projectId to the context and reload spring authorities after the user select a program.
	   It is to avoid the case when a user admin changes the role of a user with a session logged.
	   */
	@RequestMapping(value = "/context/program", method = RequestMethod.POST)
	public ResponseEntity<String> setContextProgram(@RequestBody final String programUUID) {
		final Project project = this.workbenchDataManager.getProjectByUuid(programUUID);
		if (project != null) {
			org.generationcp.commons.util.ContextUtil.setContextInfo(this.request,
				this.contextUtil.getCurrentWorkbenchUserId(), project.getProjectId());

			this.authorizationService.reloadAuthorities(project);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
