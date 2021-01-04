
package org.generationcp.ibpworkbench.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/accessNotAvailable")
public class AccessNoAvailableController {

	@RequestMapping(method = RequestMethod.GET)
	public String getAccessNotAvailablePage() {
		return "accessNotAvailable";
	}
}
