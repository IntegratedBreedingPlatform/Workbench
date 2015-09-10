
package org.generationcp.ibpworkbench.util;

import org.generationcp.middleware.pojos.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class WorkbenchUtil {

	private WorkbenchUtil() {
		// default private constructor so we cant instantiate this
	}

	public static boolean isPasswordEqualToUsername(User user) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.matches(user.getName(), user.getPassword());
	}
}
