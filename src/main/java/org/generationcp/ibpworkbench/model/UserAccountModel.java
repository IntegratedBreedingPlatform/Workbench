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

package org.generationcp.ibpworkbench.model;

import java.io.Serializable;

import org.generationcp.middleware.pojos.workbench.Role;


/**
 * <b>Description</b>: A combined Users and Persons POJO.
 * <p/>
 * <br>
 * <br>
 * <p/>
 * <b>Author</b>: Michael Blancaflor <br>
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
	
	private Integer roleId;
	
	private Role role;

	private Integer otpCode;

	/**
	 * Initialize fields so that the "null" String value does not appear.
	 */
	public UserAccountModel() {
		this.firstName = "";
		this.lastName = "";
		this.middlename = "";
		this.email = "";

		this.username = "";
		this.password = "";
		this.passwordConfirmation = "";
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return this.middlename;
	}

	public void setMiddleName(String ioname) {
		this.middlename = ioname;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String pEmail) {
		this.email = pEmail;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return this.passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public Integer getRoleId() {
		return this.roleId;
	}

	public void setRoleId(final Integer roleId) {
		this.roleId = roleId;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(final Role role) {
		this.role = role;
	}

	public Integer getOtpCode() {
		return this.otpCode;
	}

	public void setOtpCode(final Integer otpCode) {
		this.otpCode = otpCode;
	}


	public void trimAll() {
		this.firstName = this.firstName.trim();
		this.lastName = this.lastName.trim();
		this.middlename = this.middlename.trim();
		this.email = this.email.trim();

		this.username = this.username.trim();
		this.password = this.password.trim();
		this.passwordConfirmation = this.passwordConfirmation.trim();
	}
}
