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

package org.generationcp.ibpworkbench.ui.programmembers;

import java.util.Collection;

import org.generationcp.commons.security.Role;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CloseWindowAction;
import org.generationcp.ibpworkbench.actions.SaveNewProjectAddUserAction;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.ibpworkbench.ui.form.UserAccountForm;
import org.generationcp.middleware.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * <b>Description</b>: Panel for displaying UserAccountForm in the AddUser pop-up window.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Mark Agarrado <br>
 * <b>File Created</b>: October 15, 2012
 */
@Configurable
public class NewProjectAddUserPanel extends Panel {

	private static final long serialVersionUID = 2187912990347713234L;

	private VerticalLayout vl;
	private VerticalLayout rootLayout;

	private UserAccountForm userForm;

	private Button saveButton;
	private Button cancelButton;

	private HorizontalLayout buttonLayout;

	private final TwinTableSelect<User> membersSelect;

	private final static Object[] VISIBLE_ITEM_PROPERTIES = new Object[] {"firstName", "lastName", "role", "email", "username"};

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public NewProjectAddUserPanel(TwinTableSelect<User> membersSelect) {
		this.membersSelect = membersSelect;

		this.assemble();
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeValues();
		this.initializeLayout();
		this.initializeActions();
	}

	protected void initializeComponents() {
		this.rootLayout = new VerticalLayout();
		this.vl = new VerticalLayout();

		final Panel p = new Panel();
		p.setStyleName("form-panel");
		p.setSizeFull();

		this.userForm = new UserAccountForm(new UserAccountModel(), NewProjectAddUserPanel.VISIBLE_ITEM_PROPERTIES);

		this.vl.setSizeFull();
		this.vl.addComponent(new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>",
				Label.CONTENT_XHTML));
		this.vl.addComponent(this.userForm);
		this.vl.setSpacing(true);

		p.addComponent(this.vl);

		this.saveButton = new Button();
		this.cancelButton = new Button();
		this.buttonLayout = new HorizontalLayout();
		this.buttonLayout.addComponent(this.cancelButton);
		this.buttonLayout.addComponent(this.saveButton);
		this.buttonLayout.setWidth("140px");
		this.buttonLayout.setMargin(true, false, false, false);

		this.rootLayout.setMargin(new Layout.MarginInfo(false, true, true, true));
		this.rootLayout.setSpacing(true);
		Label lblTitle = new Label("Register a New User Account");
		lblTitle.setStyleName(Bootstrap.Typography.H4.styleName());
		this.rootLayout.addComponent(lblTitle);
		this.rootLayout.addComponent(p);
		this.rootLayout.addComponent(this.buttonLayout);
		this.rootLayout.setComponentAlignment(p, Alignment.MIDDLE_CENTER);
		this.rootLayout.setComponentAlignment(this.buttonLayout, Alignment.MIDDLE_CENTER);

		this.setContent(this.rootLayout);

	}

	protected void initializeValues() {

		ComboBox roleField = (ComboBox) this.userForm.getField("role");
		roleField.addItem(Role.ADMIN.name());
		roleField.addItem(Role.BREEDER.name());
		roleField.addItem(Role.TECHNICIAN.name());
		roleField.select(null);

	}

	protected void initializeLayout() {
		this.setImmediate(false);
		this.setStyleName(Reindeer.PANEL_LIGHT);
		this.setWidth("925px");

	}

	protected void initializeActions() {
		this.saveButton.addListener(new SaveNewProjectAddUserAction(this.userForm, this.membersSelect));
		this.saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		this.cancelButton.addListener(new CloseWindowAction());
	}

	@Override
	public void attach() {
		super.attach();

		this.updateLabels();
	}

	public void updateLabels() {
		this.messageSource.setCaption(this.saveButton, Message.SAVE);
		this.messageSource.setCaption(this.cancelButton, Message.CANCEL);
	}

	public UserAccountForm getForm() {
		return this.userForm;
	}

	public void refreshVisibleItems() {
		this.userForm.setVisibleItemProperties(NewProjectAddUserPanel.VISIBLE_ITEM_PROPERTIES);
	}
}
