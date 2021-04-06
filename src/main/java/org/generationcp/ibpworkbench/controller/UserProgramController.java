package org.generationcp.ibpworkbench.controller;

import org.generationcp.commons.security.AuthorizationService;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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

    @RequestMapping(value = "/userProgramInfo", method = RequestMethod.POST)
    public ResponseEntity<String> setUserProgramInfo(@RequestBody final String programUUID) {
        final Project project = this.workbenchDataManager.getProjectByUuid(programUUID);
        if (project != null) {
            org.generationcp.commons.util.ContextUtil.setContextInfo(this.request,
                    this.contextUtil.getCurrentWorkbenchUserId(), project.getProjectId(), null);

            this.authorizationService.reloadAuthorities(project);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}