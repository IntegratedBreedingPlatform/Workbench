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
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.validator.UserAccountValidator;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

@Configurable
public class ChangeCredentialsWindow extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ChangeCredentialsWindow.class);
	public static final String V_LABEL = "v-label";

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

	private CredentialsChangedEvent credentialsChangedEvent;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private Label fullNameLabel;
	private Label firstNameLabel;
	private Label lastNameLabel;
	private Label emailAddressLabel;
	private Label passwordLabel;
	private Label confirmLabel;

	private Button saveButton;

	private TextField firstName;
	private TextField lastName;
	private TextField emailAddress;
	private PasswordField password;
	private PasswordField confirmPassword;

	public ChangeCredentialsWindow(final CredentialsChangedEvent credentialsChangedEvent) {

		this.credentialsChangedEvent = credentialsChangedEvent;

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

		this.setCaption(messageSource.getMessage(Message.CHANGE_CREDENTIALS));

		this.fullNameLabel = new Label(messageSource.getMessage(Message.USER_ACC_NAME), Label.CONTENT_XHTML);
		this.fullNameLabel.setDebugId("fullNameLabel");
		this.fullNameLabel.setStyleName(V_LABEL);

		this.firstNameLabel = new Label(messageSource.getMessage(Message.USER_ACC_FNAME), Label.CONTENT_XHTML);
		this.firstNameLabel.setDebugId("firstNameLabel");
		this.firstNameLabel.setStyleName(V_LABEL);

		this.lastNameLabel = new Label(messageSource.getMessage(Message.USER_ACC_LNAME), Label.CONTENT_XHTML);
		this.lastNameLabel.setDebugId("lastNameLabel");
		this.lastNameLabel.setStyleName(V_LABEL);

		this.emailAddressLabel = new Label(messageSource.getMessage(Message.USER_ACC_EMAIL), Label.CONTENT_XHTML);
		this.emailAddressLabel.setDebugId("emailAddressLabel");
		this.emailAddressLabel.setStyleName(V_LABEL);

		this.passwordLabel = new Label(messageSource.getMessage(Message.USER_ACC_PASSWORD_NOT_REQUIRED), Label.CONTENT_XHTML);
		this.passwordLabel.setDebugId("passwordLabel");
		this.passwordLabel.setStyleName(V_LABEL);

		this.confirmLabel = new Label(messageSource.getMessage(Message.USER_ACC_PASSWORD_CONFIRM_NOT_REQUIRED), Label.CONTENT_XHTML);
		this.confirmLabel.setDebugId("confirmLabel");
		this.confirmLabel.setStyleName(V_LABEL);

		this.setFirstName(new TextField());
		this.getFirstName().setMaxLength(20);
		this.getFirstName().setDebugId("firstNameText");
		this.getFirstName().focus();

		this.setLastName(new TextField());
		this.getLastName().setMaxLength(50);
		this.getLastName().setDebugId("lastNameText");

		this.setEmailAddress(new TextField());
		this.getEmailAddress().setMaxLength(40);
		this.getEmailAddress().setDebugId("emailAddressText");

		this.password = new PasswordField();
		this.password.setDebugId("password");

		this.confirmPassword = new PasswordField();
		this.confirmPassword.setDebugId("confirmPassword");

		this.saveButton = new Button("Save");
		this.saveButton.setDebugId("saveButton");
		this.saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

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

		final String width = "140px";
		this.passwordLabel.setWidth(width);
		this.password.setWidth(width);
		this.confirmLabel.setWidth(width);
		this.confirmPassword.setWidth(width);

		final HorizontalLayout buttonlayout = new HorizontalLayout();
		buttonlayout.setDebugId("buttonlayout");

		buttonlayout.addComponent(this.saveButton);
		buttonlayout.setSpacing(true);

		final GridLayout fieldsLayout = new GridLayout(3, 5);
		fieldsLayout.setDebugId("passwordGridLayout");
		fieldsLayout.setMargin(false, false, false, false);

		// first row
		fieldsLayout.addComponent(new Label());
		fieldsLayout.addComponent(this.firstNameLabel);
		fieldsLayout.addComponent(this.lastNameLabel);

		// second
		fieldsLayout.addComponent(this.fullNameLabel);
		fieldsLayout.addComponent(this.getFirstName());
		fieldsLayout.addComponent(this.getLastName());

		//third
		fieldsLayout.addComponent(this.emailAddressLabel);
		fieldsLayout.addComponent(this.getEmailAddress());
		fieldsLayout.addComponent(new Label());

		//foourth
		fieldsLayout.addComponent(this.passwordLabel);
		fieldsLayout.addComponent(this.password);
		fieldsLayout.addComponent(new Label());

		// fifth
		fieldsLayout.addComponent(this.confirmLabel);
		fieldsLayout.addComponent(this.confirmPassword);
		fieldsLayout.addComponent(new Label());
		fieldsLayout.setSizeFull();

		layout.addComponent(fieldsLayout);
		layout.addComponent(buttonlayout);
		layout.setComponentAlignment(buttonlayout, Alignment.MIDDLE_CENTER);
		layout.setExpandRatio(fieldsLayout, 1.0F);

		this.setContent(layout);
	}

	protected void initializeActions() {
		this.saveButton.addListener(new ClickListener() {

			@Override
			public void buttonClick(final ClickEvent clickEvent) {

				ChangeCredentialsWindow.this.saveCredentials();

			}
		});

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

	protected void saveCredentials() {

		try {

			final String firstNameValue = getFirstName().getValue().toString();
			final String lastNameValue = getLastName().getValue().toString();
			final String emailAddressValue = getEmailAddress().getValue().toString();
			final String passwordValue = password.getValue().toString();
			final String passwordConfirmValue = confirmPassword.getValue().toString();

			validateName(firstNameValue, lastNameValue);
			validateEmailAdresss(emailAddressValue);
			validateEmailFormat(emailAddressValue);
			validatePassword(passwordValue, passwordConfirmValue);

			updateUser(firstNameValue, lastNameValue, emailAddressValue, passwordValue);

			MessageNotifier.showMessage(this.getParent(), messageSource.getMessage(Message.SUCCESS),
					messageSource.getMessage(Message.CREDENTIALS_UPDATED_SUCCESS_MESSAGE), 3000);

			credentialsChangedEvent.onChanged(firstNameValue, lastNameValue, emailAddressValue);

			closeWindow();

		} catch (final ValidationException e) {

			LOG.debug(e.getMessage(), e);
			MessageNotifier.showRequiredFieldError(this.getParent(), e.getMessage());

		}

	}

	protected void updateUser(final String firstName, final String lastName, final String emailAddress, final String password) {

		final WorkbenchUser user = this.contextUtil.getCurrentWorkbenchUser();
		if (!StringUtils.isEmpty(password)) {
			user.setPassword(passwordEncoder.encode(password));
		}

		final Person person = user.getPerson();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setEmail(emailAddress);

		this.workbenchDataManager.updateUser(user);

	}

	protected void validatePassword(final String password, final String confirmPassword) throws ValidationException {

		if (!StringUtils.isEmpty(password) && !password.equals(confirmPassword)) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_CONFIRM_PASSWORD));
		}

	}

	protected void validateEmailAdresss(final String emailAddress) throws ValidationException {

		if (StringUtils.isEmpty(emailAddress)) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_EMAIL_IS_BLANK));
		}

		if (this.workbenchDataManager.isPersonWithEmailExists(emailAddress)) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_EMAIL_ALREADY_EXISTS));
		}

	}

	protected void validateEmailFormat(final String emailAddress) throws ValidationException {
		if (!Pattern.compile(UserAccountValidator.EMAIL_PATTERN).matcher(emailAddress).matches()) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_EMAIL_IS_INVALID_FORMAT));
		}
	}

	protected void validateName(final String firstName, final String lastName) throws ValidationException {

		if ("".equals(firstName) || "".equals(lastName)) {
			throw new ValidationException(messageSource.getMessage(Message.ERROR_NAME_IS_BLANK));
		}

	}

	protected TextField getFirstName() {
		return firstName;
	}

	protected void setFirstName(TextField firstName) {
		this.firstName = firstName;
	}

	protected TextField getLastName() {
		return lastName;
	}

	protected void setLastName(TextField lastName) {
		this.lastName = lastName;
	}

	protected TextField getEmailAddress() {
		return emailAddress;
	}

	protected void setEmailAddress(TextField emailAddress) {
		this.emailAddress = emailAddress;
	}

	protected class ValidationException extends Exception {

		ValidationException(final String message) {
			super(message);
		}

	}

	public interface CredentialsChangedEvent {

		void onChanged(final String firstname, final String lastName, String emailAddress);

	}

}
