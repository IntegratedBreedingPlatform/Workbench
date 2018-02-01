/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.actions.ChangePasswordAction;
import org.generationcp.middleware.pojos.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ChangePasswordWindow extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	@Autowired
	private ContextUtil contextUtil;

	private static final long serialVersionUID = 1L;

	private Label passwordLabel;
	private Label confirmLabel;

	private Button cancelButton;
	private Button saveButton;

	private PasswordField password;
	private PasswordField confirmPassword;

	public ChangePasswordWindow() {
		// does nothing
	}

	/**
	 * Assemble the UI after all dependencies has been set.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void initializeComponents() {
		this.setOverrideFocus(true);
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setCaption("Change Password");

		this.passwordLabel = new Label("&nbsp;&nbsp;&nbsp;Password: &nbsp;&nbsp;", Label.CONTENT_XHTML);
		this.passwordLabel.setDebugId("passwordLabel");
		this.confirmLabel = new Label("&nbsp;&nbsp;&nbsp;Confirm Password :&nbsp;&nbsp;", Label.CONTENT_XHTML);
		this.confirmLabel.setDebugId("confirmLabel");
		this.passwordLabel.setStyleName("v-label");
		this.confirmLabel.setStyleName("v-label");

		this.password = new PasswordField();
		this.password.setDebugId("password");
		this.password.focus();

		this.confirmPassword = new PasswordField();
		this.confirmPassword.setDebugId("confirmPassword");

		this.saveButton = new Button("Save");
		this.saveButton.setDebugId("saveButton");
		this.saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setDebugId("cancelButton");
	}

	protected void initializeLayout() {
		this.setWidth("335px");
		this.setHeight("200px");
		this.setModal(true);

		final VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);

		this.passwordLabel.setWidth("140px");
		this.password.setWidth("140px");
		this.confirmLabel.setWidth("140px");
		this.confirmPassword.setWidth("140px");

		final GridLayout passwordGridLayout = new GridLayout(2, 2);
		passwordGridLayout.setDebugId("passwordGridLayout");
		passwordGridLayout.setMargin(false, false, false, false);
		passwordGridLayout.addComponent(this.passwordLabel);
		passwordGridLayout.addComponent(this.password);
		passwordGridLayout.addComponent(this.confirmLabel);
		passwordGridLayout.addComponent(this.confirmPassword);
		passwordGridLayout.setSizeFull();

		final HorizontalLayout buttonlayout = new HorizontalLayout();
		buttonlayout.setDebugId("buttonlayout");

		buttonlayout.addComponent(this.cancelButton);
		buttonlayout.addComponent(this.saveButton);
		buttonlayout.setSpacing(true);

		layout.addComponent(passwordGridLayout);
		layout.addComponent(buttonlayout);
		layout.setComponentAlignment(buttonlayout, Alignment.MIDDLE_CENTER);
		layout.setExpandRatio(passwordGridLayout, 1.0F);

		this.setContent(layout);
	}


	protected void initializeActions() {
		final User user = contextUtil.getCurrentWorkbenchUser();

		this.saveButton.addListener(new ChangePasswordAction(user.getName(), this.password, this.confirmPassword));
		this.cancelButton.addListener(new RemoveWindowListener());
	}

	public class RemoveWindowListener implements ClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(final ClickEvent event) {
			event.getComponent().getWindow().getParent().removeWindow(ChangePasswordWindow.this.getWindow());
		}

	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	@Override
	public void updateLabels() {
		// currently does nothing
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

}
