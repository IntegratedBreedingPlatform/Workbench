/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.ibpworkbench.model;

import org.generationcp.commons.security.Role;

import java.io.Serializable;

/**
 * <b>Description</b>: A combined Users and Persons POJO.
 * <p/>
 * <br>
 * <br>
 * <p/>
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 16, 2012
 */
public class UserAccountModel implements Serializable {

	private static final long serialVersionUID = 7669967119863861617L;

	private String firstName;
	private String lastName;
	private String middlename;
	private String email;

	private String username;
	private String password;
	private String passwordConfirmation;

	private String role;

	private String securityQuestion;
	private String securityAnswer;

	/**
	 * Initialize fields so that the "null" String value does not appear.
	 */
	public UserAccountModel() {
		firstName = "";
		lastName = "";
		middlename = "";
		email = "";

		username = "";
		password = "";
		passwordConfirmation = "";
		securityQuestion = "";
		securityAnswer = "";

		// default
		role = Role.TECHNICIAN.name();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middlename;
	}

	public void setMiddleName(String ioname) {
		this.middlename = ioname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String pEmail) {
		this.email = pEmail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public String getSecurityAnswer() {
		return securityAnswer;
	}

	public void setSecurityAnswer(String securityAnswer) {
		this.securityAnswer = securityAnswer;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void trimAll() {
		firstName = firstName.trim();
		lastName = lastName.trim();
		middlename = middlename.trim();
		email = email.trim();

		username = username.trim();
		password = password.trim();
		passwordConfirmation = passwordConfirmation.trim();
		securityQuestion = securityQuestion.trim();
		securityAnswer = securityAnswer.trim();
	}
}
