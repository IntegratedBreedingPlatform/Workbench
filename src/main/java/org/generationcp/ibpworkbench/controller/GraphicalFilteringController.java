package org.generationcp.ibpworkbench.controller;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/graphical-filtering")
public class GraphicalFilteringController {

	@RequestMapping(method = RequestMethod.GET)
	public String index(final Model model) throws MiddlewareQueryException {
		return "BrAPI-Graphical-Filtering/index";
	}

	@Value("${pagedresult.default.page.size}")
	public String defaultPageSize;

	@ModelAttribute("defaultPageSize")
	public String getDefaultPageSize() {
		return this.defaultPageSize;
	}
}
