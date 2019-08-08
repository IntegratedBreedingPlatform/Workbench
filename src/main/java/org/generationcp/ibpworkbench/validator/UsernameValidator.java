/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.validator;

import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.validator.AbstractValidator;

/**
 * <b>Description</b>: Tests if a Users record with the same Username already
 * exists.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jul 22, 2012
 */
@Configurable
public class UsernameValidator extends AbstractValidator {

	private static final long serialVersionUID = -1537885028422014862L;

	@Autowired
	private UserService userService;

	@Autowired
	private ValidatorCounter validatorCounter;

	public UsernameValidator() {
		super("User with Username \"{0}\" already exists.");
	}

	@Override
	public boolean isValid(final Object value) {

		int usernameCounter;
		usernameCounter = this.validatorCounter.getUsernameCounter();
		usernameCounter++;
		this.validatorCounter.setUsernameCounter(usernameCounter);

		if (usernameCounter > 2) {
			this.validatorCounter.setUsernameCounter(0);
			return true;
		}

		if (this.userService.isUsernameExists(value.toString().trim())) {
			return false;
		}

		return true;
	}

	void setValidatorCounter(final ValidatorCounter validatorCounter) {
		this.validatorCounter = validatorCounter;
	}

	void setUserService(final UserService userService) {
		this.userService = userService;
	}

}
