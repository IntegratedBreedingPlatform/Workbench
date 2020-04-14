package org.generationcp.ibpworkbench.controller;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/brapi-fieldmap")
public class BrAPIFieldmapController {

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model) throws MiddlewareQueryException {
		return "brapi-fieldmap/index";
	}
}
