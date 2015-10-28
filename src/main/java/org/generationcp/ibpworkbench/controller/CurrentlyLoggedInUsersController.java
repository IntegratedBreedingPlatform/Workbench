
package org.generationcp.ibpworkbench.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/users")
@Controller
public class CurrentlyLoggedInUsersController {

	@Autowired
	@Qualifier("sessionRegistry")
	private SessionRegistry sessionRegistry;

	@RequestMapping("/current/list")
	@ResponseBody
	public List<String> getCurrentlyLoggedInUsers() {
		List<Object> principals = this.sessionRegistry.getAllPrincipals();

		List<String> usersNamesList = new ArrayList<String>();

		for (Object principal : principals) {
			if (principal instanceof User) {
				usersNamesList.add(((User) principal).getUsername());
			}
		}

		return usersNamesList;
	}
}
