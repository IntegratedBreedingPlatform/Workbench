
package org.generationcp.ibpworkbench.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Value("${security.2fa.enabled}")
	private boolean enableTwoFactorAuthentication;

	@RequestMapping(method = RequestMethod.GET)
	public String index(final Model model) {
		model.addAttribute("enableTwoFactorAuthentication", this.enableTwoFactorAuthentication);
		return "angular2/admin/index";
	}
}
