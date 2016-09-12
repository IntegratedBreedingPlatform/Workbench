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

package org.generationcp.ibpworkbench.ui.form;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.model.formfieldfactory.UserAccountFormFieldFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

/**
 * <b>Description</b>: Custom form for registering a user account.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jul 11, 2012
 */
@Configurable
public class UserAccountForm extends Form {

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private static final long serialVersionUID = -7726164779128415602L;

	private BeanItem<UserAccountModel> userBean;

	private final UserAccountModel userAccount;

	private final Object[] visibleProperties;

	private Label lblName;
	private Label lblFirstName;
	private Label lblLastName;
	private Label lblRole;
	private Label lblEmail;
	private Label lblUserName;

	private GridLayout grid;

	public UserAccountForm(UserAccountModel userAccount) {
		this.userAccount = userAccount;

		this.visibleProperties = new Object[] {"firstName", "lastName", "role", "email", "username"};
		super.setVisibleItemProperties(this.visibleProperties);

		this.assemble();
	}

	public UserAccountForm(UserAccountModel userAccount, Object[] visibleProperties) {
		this.userAccount = userAccount;
		this.visibleProperties = visibleProperties;
		super.setVisibleItemProperties(visibleProperties);

		this.assemble();
	}

	protected void assemble() {

		this.initializeComponents();

	}

	protected void initializeComponents() {

		this.setImmediate(false);
		this.grid = new GridLayout(5, 8);
		this.grid.setDebugId("grid");
		this.grid.setSpacing(true);
		this.grid.setMargin(new Layout.MarginInfo(true, true, true, true));
		this.grid.setWidth("100%");

		this.setLayout(this.grid);

		this.userBean = new BeanItem<UserAccountModel>(this.userAccount);

		this.setItemDataSource(this.userBean);
		this.setComponentError(null);
		this.setFormFieldFactory(new UserAccountFormFieldFactory());
		this.setVisibleItemProperties(this.visibleProperties);

		this.setWriteThrough(false);
		this.setInvalidCommitted(false);
		this.setValidationVisibleOnCommit(false);

	}

	@Override
	protected void attachField(Object propertyId, Field field) {

		if ("firstName".equals(propertyId)) {
			this.grid.addComponent(field, 1, 0);
		} else if ("lastName".equals(propertyId)) {
			this.grid.addComponent(field, 3, 0);
		} else if ("role".equals(propertyId)) {
			this.grid.addComponent(field, 1, 2);
		} else if ("email".equals(propertyId)) {
			this.grid.addComponent(field, 1, 3, 2, 3);
		} else if ("username".equals(propertyId)) {
			this.grid.addComponent(field, 1, 4, 2, 4);
		}
	}

	@Override
	public void attach() {

		this.lblName = this.createLabel();
		this.lblName.setValue(this.messageSource.getMessage(Message.USER_ACC_NAME));

		this.lblFirstName = this.createLabel();
		this.lblFirstName.setValue(this.messageSource.getMessage(Message.USER_ACC_FNAME));
		this.lblLastName = this.createLabel();
		this.lblLastName.setValue(this.messageSource.getMessage(Message.USER_ACC_LNAME));
		this.lblRole = this.createLabel();
		this.lblRole.setValue(this.messageSource.getMessage(Message.USER_ACC_ROLE));
		this.lblEmail = this.createLabel();
		this.lblEmail.setValue(this.messageSource.getMessage(Message.USER_ACC_EMAIL));
		this.lblUserName = this.createLabel();
		this.lblUserName.setValue(this.messageSource.getMessage(Message.USER_ACC_USERNAME));

		if (this.grid.getComponent(0, 0) == null) {
			this.grid.addComponent(this.lblName, 0, 0);
		}
		if (this.grid.getComponent(1, 1) == null) {
			this.grid.addComponent(this.lblFirstName, 1, 1);
		}
		if (this.grid.getComponent(3, 1) == null) {
			this.grid.addComponent(this.lblLastName, 3, 1);
		}

		this.grid.setComponentAlignment(this.lblLastName, Alignment.TOP_LEFT);

		if (this.grid.getComponent(0, 2) == null) {
			this.grid.addComponent(this.lblRole, 0, 2);
		}

		if (this.grid.getComponent(0, 3) == null) {
			this.grid.addComponent(this.lblEmail, 0, 3);
		}
		if (this.grid.getComponent(0, 4) == null) {
			this.grid.addComponent(this.lblUserName, 0, 4);
		}

		super.attach();

	}

	private Label createLabel() {

		Label label = new Label();
		label.setDebugId("label");
		label.setContentMode(Label.CONTENT_XHTML);
		label.setWidth("150px");
		return label;
	}

}
