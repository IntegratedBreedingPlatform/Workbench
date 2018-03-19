package org.generationcp.ibpworkbench.controller;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

@Controller
@RequestMapping("/jhipster")
public class JHipsterController {

	@Resource
	protected ContextUtil contextUtil;

	@ModelAttribute("cropName")
	public String getCropName() {
		return this.contextUtil.getProjectInContext().getCropType().getCropName();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model) throws MiddlewareQueryException {
		return "jhipster/index";
	}
}
