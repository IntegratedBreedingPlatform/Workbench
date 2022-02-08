
package org.generationcp.ibpworkbench.controller;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/ontology")
public class OntologyController {

	@Autowired
	private ContextUtil context;

	@RequestMapping(method = RequestMethod.GET)
	public String ontology(Model model) throws MiddlewareQueryException {
		model.addAttribute("currentCrop", this.context.getProjectInContext().getCropType().getCropName());
		model.addAttribute("currentProgramId", this.context.getProjectInContext().getUniqueID());
		model.addAttribute("selectedProjectId", this.context.getProjectInContext().getProjectId());
		model.addAttribute("loggedInUserId", this.context.getContextInfoFromSession().getLoggedInUserId());
		return "ontology";
	}
}
