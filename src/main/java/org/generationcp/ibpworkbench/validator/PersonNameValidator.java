/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.validator;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.Field;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * <b>Description</b>: Tests if a Persons record with the same First Name and Last Name already exists.
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
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ValidatorCounter validatorCounter;

	private final Field firstName;
	private final Field lastName;

	public PersonNameValidator(Field firstName, Field lastName) {
		super("Person with First Name \"{0}\" and Last Name \"{1}\" already exists.");
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public void validate(Object value) {
		if (!this.isValid(value)) {
			String message =
					this.getErrorMessage().replace("{0}", this.firstName.getValue().toString())
							.replace("{1}", this.lastName.getValue().toString());
			throw new InvalidValueException(message);
		}
	}

	@Override
	public boolean isValid(Object value) {
		int personCounter;
		personCounter = validatorCounter.getNameValidationCounter();
		personCounter++;
		validatorCounter.setNameValidationCounter(personCounter);

		if (personCounter > 2) {
			validatorCounter.setNameValidationCounter(0);
			return true;
		}

		return !this.workbenchDataManager.isPersonExists(this.firstName.getValue().toString(), this.lastName.getValue().toString());

	}

}
