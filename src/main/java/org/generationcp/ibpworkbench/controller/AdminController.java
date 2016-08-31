
package org.generationcp.ibpworkbench.controller;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@RequestMapping(method = RequestMethod.GET)
	public String angular2poc(Model model) throws MiddlewareQueryException {
		return "angular2/admin/index";
	}
}
