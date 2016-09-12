package org.generationcp.ibpworkbench.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(LogoutController.URL)
public class LogoutController {

	public static final String URL = "/logout";

	@RequestMapping(method = RequestMethod.GET)
	public String logout() {
		return "logout";
	}
}
