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

package org.generationcp.ibpworkbench.model.formfieldfactory;

import org.generationcp.ibpworkbench.validator.PersonNameValidator;
import org.generationcp.ibpworkbench.validator.UsernameValidator;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

/**
 * <b>Description</b>: Field factory for generating Users and Persons fields for UserAccountForm class.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jul 16, 2012
 */
@Configurable
public class UserAccountFormFieldFactory extends DefaultFieldFactory {

	private static final long serialVersionUID = 3560059243526106791L;

	private Field firstName;
	private Field lastName;
	private ComboBox role;
	private Field email;
	private Field username;

	public UserAccountFormFieldFactory() {
		this.initFields();
	}

	private void initFields() {
		this.firstName = new TextField() {

			private static final long serialVersionUID = -2217839652903057419L;

			@Override
			public Object getValue() {
				return super.getValue().toString().trim();
			}

		};
		this.lastName = new TextField() {

			/**
			 *
			 */
			private static final long serialVersionUID = 7065033448781385928L;

			@Override
			public Object getValue() {
				return super.getValue().toString().trim();
			}

		};
		this.firstName.addValidator(new PersonNameValidator(this.firstName, this.lastName));

		this.role = new ComboBox();

		this.email = new TextField() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1374065891578773582L;

			@Override
			public Object getValue() {
				return super.getValue().toString().trim();
			}

		};
		this.username = new TextField() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1488280476705768186L;

			@Override
			public Object getValue() {
				return super.getValue().toString().trim();
			}

		};

		int tabIndex = 100;
		this.firstName.setTabIndex(tabIndex++);
		this.lastName.setTabIndex(tabIndex++);
		this.role.setTabIndex(tabIndex++);
		this.email.setTabIndex(tabIndex++);
		this.username.setTabIndex(tabIndex++);

		this.firstName.setStyleName("hide-caption");
		this.lastName.setStyleName("hide-caption");
		this.role.setStyleName("hide-caption");
		this.email.setStyleName("hide-caption");
		this.username.setStyleName("hide-caption");

		this.firstName.setDebugId("vaadin-firstName-txt");
		this.lastName.setDebugId("vaadin-lastName-txt");
		this.email.setDebugId("vaadin-email-txt");
		this.username.setDebugId("vaadin-username-txt");

	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {

		Field field = super.createField(item, propertyId, uiContext);

		if ("firstName".equals(propertyId)) {
			this.firstName.setRequired(true);
			this.firstName.setRequiredError("Please enter a First Name.");
			this.firstName.addValidator(new StringLengthValidator("First Name must be 1-20 characters.", 1, 20, false));
			return this.firstName;
		} else if ("lastName".equals(propertyId)) {
			this.lastName.setRequired(true);
			this.lastName.setRequiredError("Please enter a Last Name.");
			this.lastName.addValidator(new StringLengthValidator("Last Name must be 1-50 characters.", 1, 50, false));
			return this.lastName;
		} else if ("role".equals(propertyId)) {
			this.role.setNullSelectionAllowed(true);
			this.role.setRequired(true);
			this.role.setRequiredError("Please select a role");
			return this.role;
		} else if ("email".equals(propertyId)) {
			this.email.setRequired(true);
			this.email.setRequiredError("Please enter an Email Address.");
			this.email.setWidth("80%");
			this.email.addValidator(new StringLengthValidator("Email Address must be 5-40 characters.", 5, 40, false));
			this.email.addValidator(new EmailValidator("Please enter a valid Email Address."));
			return this.email;
		} else if ("username".equals(propertyId)) {
			this.username.setRequired(true);
			this.username.setRequiredError("Please enter a Username.");
			this.username.setWidth("80%");
			this.username.addValidator(new StringLengthValidator("Username must be 1-30 characters.", 1, 30, false));
			this.username.addValidator(new UsernameValidator());
			return this.username;
		}
		return field;
	}

}
