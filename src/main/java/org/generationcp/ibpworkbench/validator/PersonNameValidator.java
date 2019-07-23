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
import com.vaadin.ui.Field;

/**
 * <b>Description</b>: Tests if a Persons record with the same First Name and
 * Last Name already exists.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jul 22, 2012
 */
@Configurable
public class PersonNameValidator extends AbstractValidator {

	private static final long serialVersionUID = 4065915808146235650L;

	@Autowired
	private UserService userService;

	@Autowired
	private ValidatorCounter validatorCounter;

	private Field firstName;
	private Field lastName;

	public PersonNameValidator(final Field firstName, final Field lastName) {
		super("Person with First Name \"{0}\" and Last Name \"{1}\" already exists.");
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public void validate(final Object value) {
		if (!this.isValid(value)) {
			final String message = this.getErrorMessage().replace("{0}", this.firstName.getValue().toString())
					.replace("{1}", this.lastName.getValue().toString());
			throw new InvalidValueException(message);
		}
	}

	@Override
	public boolean isValid(final Object value) {
		int personCounter;

		personCounter = this.validatorCounter.getNameValidationCounter();
		personCounter++;
		this.validatorCounter.setNameValidationCounter(personCounter);

		if (personCounter > 2) {
			this.validatorCounter.setNameValidationCounter(0);
			return true;
		}

		return !this.userService.isPersonExists(this.firstName.getValue().toString(),
				this.lastName.getValue().toString());

	}

	void setFirstName(final Field firstName) {
		this.firstName = firstName;
	}

	void setLastName(final Field lastName) {
		this.lastName = lastName;
	}

	void setValidatorCounter(final ValidatorCounter validatorCounter) {
		this.validatorCounter = validatorCounter;
	}

	void setUserService(final UserService userService) {
		this.userService = userService;
	}
}
