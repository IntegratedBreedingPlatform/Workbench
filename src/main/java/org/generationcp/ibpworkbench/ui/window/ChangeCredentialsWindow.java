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
import com.vaadin.ui.Layout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configurable
public class ChangeCredentialsWindow extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

	private final boolean isAdminUser = false;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private Label fullNameLabel;
	private Label firstNameLabel;
	private Label lastNameLabel;
	private Label emailAddressLabel;
	private Label passwordLabel;
	private Label confirmLabel;

	private Button cancelButton;
	private Button saveButton;

	private TextField firstName;
	private TextField lastName;
	private TextField emailAddress;
	private PasswordField password;
	private PasswordField confirm_password;

	public ChangeCredentialsWindow() {
	}

	/**
	 * Assemble the UI after all dependencies has been set.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void initializeComponents() throws Exception {

		this.setOverrideFocus(true);
		this.addStyleName(Reindeer.WINDOW_LIGHT);

		this.setCaption(messageSource.getMessage(Message.CHANGE_CREDENTIALS));

		this.fullNameLabel = new Label(messageSource.getMessage(Message.USER_ACC_NAME), Label.CONTENT_XHTML);
		this.fullNameLabel.setDebugId("fullNameLabel");
		this.fullNameLabel.setStyleName("v-label");

		this.firstNameLabel = new Label(messageSource.getMessage(Message.USER_ACC_FNAME), Label.CONTENT_XHTML);
		this.firstNameLabel.setDebugId("firstNameLabel");
		this.firstNameLabel.setStyleName("v-label");

		this.lastNameLabel = new Label(messageSource.getMessage(Message.USER_ACC_LNAME), Label.CONTENT_XHTML);
		this.lastNameLabel.setDebugId("lastNameLabel");
		this.lastNameLabel.setStyleName("v-label");

		this.emailAddressLabel = new Label(messageSource.getMessage(Message.USER_ACC_EMAIL), Label.CONTENT_XHTML);
		this.emailAddressLabel.setDebugId("emailAddressLabel");
		this.emailAddressLabel.setStyleName("v-label");

		this.passwordLabel = new Label(messageSource.getMessage(Message.USER_ACC_PASSWORD), Label.CONTENT_XHTML);
		this.passwordLabel.setDebugId("passwordLabel");
		this.passwordLabel.setStyleName("v-label");

		this.confirmLabel = new Label(messageSource.getMessage(Message.USER_ACC_PASSWORD_CONFIRM), Label.CONTENT_XHTML);
		this.confirmLabel.setDebugId("confirmLabel");
		this.confirmLabel.setStyleName("v-label");

		this.firstName = new TextField();
		this.firstName.setMaxLength(20);
		this.firstName.setDebugId("firstNameText");

		this.lastName = new TextField();
		this.lastName.setMaxLength(50);
		this.lastName.setDebugId("lastNameText");

		this.emailAddress = new TextField();
		this.emailAddress.setMaxLength(40);
		this.emailAddress.setDebugId("emailAddressText");

		this.password = new PasswordField();
		this.password.setDebugId("password");
		this.password.focus();

		this.confirm_password = new PasswordField();
		this.confirm_password.setDebugId("confirm_password");

		this.saveButton = new Button("Save");
		this.saveButton.setDebugId("saveButton");
		this.saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setDebugId("cancelButton");
	}

	protected void initializeLayout() {

		this.setWidth("600px");
		this.setHeight("300px");
		this.setModal(true);
		this.setResizable(false);
		this.setClosable(false);

		final VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);

		this.passwordLabel.setWidth("140px");
		this.password.setWidth("140px");
		this.confirmLabel.setWidth("140px");
		this.confirm_password.setWidth("140px");

		final HorizontalLayout buttonlayout = new HorizontalLayout();
		buttonlayout.setDebugId("buttonlayout");

		buttonlayout.addComponent(this.cancelButton);
		buttonlayout.addComponent(this.saveButton);
		buttonlayout.setSpacing(true);

		final Layout editableFields;

		if (isAdminUser) {
			editableFields = createEditableFieldsLayoutForAdmin();
		} else {
			editableFields = createEditableFieldsLayoutForUser();
		}

		layout.addComponent(editableFields);
		layout.addComponent(buttonlayout);
		layout.setComponentAlignment(buttonlayout, Alignment.MIDDLE_CENTER);
		layout.setExpandRatio(editableFields, 1.0F);

		this.setContent(layout);
	}

	protected Layout createEditableFieldsLayoutForAdmin() {

		final GridLayout fieldsLayout = new GridLayout(3, 5);
		fieldsLayout.setDebugId("passwordGridLayout");
		fieldsLayout.setMargin(false, false, false, false);

		// first row
		fieldsLayout.addComponent(this.fullNameLabel);
		fieldsLayout.addComponent(this.firstNameLabel);
		fieldsLayout.addComponent(this.lastNameLabel);

		// second
		fieldsLayout.addComponent(new Label());
		fieldsLayout.addComponent(this.firstName);
		fieldsLayout.addComponent(this.lastName);

		//third
		fieldsLayout.addComponent(this.emailAddressLabel);
		fieldsLayout.addComponent(this.emailAddress);
		fieldsLayout.addComponent(new Label());

		//foourth
		fieldsLayout.addComponent(this.passwordLabel);
		fieldsLayout.addComponent(this.password);
		fieldsLayout.addComponent(new Label());

		// fifth
		fieldsLayout.addComponent(this.confirmLabel);
		fieldsLayout.addComponent(this.confirm_password);
		fieldsLayout.addComponent(new Label());
		fieldsLayout.setSizeFull();

		return fieldsLayout;

	}

	protected Layout createEditableFieldsLayoutForUser() {

		final GridLayout fieldsLayout = new GridLayout(2, 2);
		fieldsLayout.setDebugId("passwordGridLayout");
		fieldsLayout.setMargin(false, false, false, false);

		fieldsLayout.addComponent(this.passwordLabel);
		fieldsLayout.addComponent(this.password);
		fieldsLayout.addComponent(this.confirmLabel);
		fieldsLayout.addComponent(this.confirm_password);
		fieldsLayout.setSizeFull();

		return fieldsLayout;

	}

	protected void initializeActions() {
		this.saveButton.addListener(new ClickListener() {

			@Override
			public void buttonClick(final ClickEvent clickEvent) {

				ChangeCredentialsWindow.this.saveCredentials();

			}
		});

	}

	protected void assemble() throws Exception {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	@Override
	public void updateLabels() {
		// currently does nothing
	}

	protected void saveCredentials() {

		try {

			final String firstNameValue = firstName.getValue().toString();
			final String lastNameValue = lastName.getValue().toString();
			final String emailAddressValue = emailAddress.getValue().toString();
			final String passwordValue = password.getValue().toString();
			final String passwordConfirmValue = password.getValue().toString();

			validate(firstNameValue, lastNameValue, emailAddressValue, passwordValue, passwordConfirmValue);

			updateUser(firstNameValue, lastNameValue, emailAddressValue, passwordValue);

			MessageNotifier.showMessage(this.getWindow(), messageSource.getMessage(Message.SUCCESS),
					messageSource.getMessage(Message.CREDENTIALS_UPDATED_SUCCESS_MESSAGE), 3000);

			closeWindow();

		} catch (final ValidationException e) {

			MessageNotifier.showRequiredFieldError(this.getWindow(), e.getMessage());

		}

	}

	protected void updateUser(final String firstName, final String lastName, final String emailAddress, final String password) {

		final User user = this.workbenchDataManager.getUserByUsername(this.contextUtil.getCurrentWorkbenchUser().getName());
		user.setPassword(passwordEncoder.encode(password));

		final Person person = user.getPerson();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setEmail(emailAddress);

		this.workbenchDataManager.updateUser(user);

	}

	protected void validate(final String firstName, final String lastName, final String emailAddress, final String password, final String confirmPassword)
			throws ValidationException {

		if (isAdminUser) {
			validateName(firstName, lastName);
			validateEmailAdresss(emailAddress);
		}

		validatePassword(password, confirmPassword);

	}

	protected void validatePassword(final String password, final String confirmPassword) throws ValidationException {

		if ("".equals(password) || " ".equals(password)) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_PASSWORD_IS_BLANK));
		}

		if (!password.equals(confirmPassword)) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_CONFIRM_PASSWORD));
		}

	}

	protected void validateEmailAdresss(final String emailAddress) throws ValidationException {

		if ("".equals(emailAddress)) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_EMAIL_IS_BLANK));
		}

		if (this.workbenchDataManager.isPersonWithEmailExists(emailAddress)) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_EMAIL_ALREADY_EXISTS));
		}

	}

	protected void validateName(final String firstName, final String lastName) throws ValidationException {

		if ("".equals(firstName) || "".equals(lastName)) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_NAME_IS_BLANK));
		}

	}

	protected class ValidationException extends Exception {

		ValidationException(final String message) {
			super(message);
		}

	}

}
