package org.generationcp.ibpworkbench.controller;

import com.google.common.base.Optional;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import java.util.function.Function;

@Controller
public class JHipsterController {

	@Resource
	protected ContextUtil contextUtil;

	@ModelAttribute("cropName")
	public String getCropName() {
		return this.getProjectPropertyFromContext((project) -> project.getCropType().getCropName());
	}

	@ModelAttribute("currentProgramId")
	public String getCurrentProgramId() {
		return this.getProjectPropertyFromContext(Project::getUniqueID);
	}

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

	private String getProjectPropertyFromContext(Function<Project, String> propertyMapper) {
		final Optional<Project> project = this.contextUtil.getProject();
		if (project.isPresent()) {
			return propertyMapper.apply(project.get());
		}
		return "";
	}
}
