package org.generationcp.ibpworkbench.controller;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;

@Controller
public class JHipsterController {

	@Resource
	protected ContextUtil contextUtil;

	@ModelAttribute("showReleaseNotes")
	public boolean showReleaseNotes() {
		return this.contextUtil.shouldShowReleaseNotes();
	}

	@RequestMapping(value = {"/" ,"/jhipster"}, method = RequestMethod.GET)
	public String index(Model model) throws MiddlewareQueryException {
		return "jhipster/index";
	}

	// Workaround for base-href page reload
	@RequestMapping(value = {"/app", "/main"}, method = RequestMethod.GET)
	public RedirectView onRefreshPage() throws MiddlewareQueryException {
		final RedirectView redirectView = new RedirectView("/main/");
		redirectView.setContextRelative(true);
		redirectView.setExposeModelAttributes(false);
		return redirectView;
	}

}
