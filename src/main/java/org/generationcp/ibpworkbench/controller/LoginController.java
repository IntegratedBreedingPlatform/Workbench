package org.generationcp.ibpworkbench.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@RequestMapping("/form")
	public String getLoginForm() {
		return "login";
	}

}
