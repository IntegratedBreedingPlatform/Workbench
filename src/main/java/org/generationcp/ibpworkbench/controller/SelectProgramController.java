package org.generationcp.ibpworkbench.controller;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(SelectProgramController.URL)
public class SelectProgramController {
    public static final String URL = "/selectProgram/";

    @Resource
    private ContextUtil contextUtil;

    @Resource
    private WorkbenchDataManager workbenchDataManager;

    @Resource
    private HttpServletRequest request;

    @RequestMapping(value = "/setCurrentProgram", method = RequestMethod.POST)
    @ResponseBody
    public String setProgramSelected(@RequestBody final String programUUID) {
        final Project project = this.workbenchDataManager.getProjectByUuid(programUUID);
        if (project != null) {
            org.generationcp.commons.util.ContextUtil.setContextInfo(this.request,
                    this.contextUtil.getCurrentWorkbenchUserId(), project.getProjectId(), null);
        }
        return "success";
    }
}

