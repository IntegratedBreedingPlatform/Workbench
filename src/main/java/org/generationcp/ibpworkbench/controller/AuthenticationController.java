package org.generationcp.ibpworkbench.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {
	
	@RequestMapping("/login")
	public String getLoginPage() {
		return "login";
	}

}
