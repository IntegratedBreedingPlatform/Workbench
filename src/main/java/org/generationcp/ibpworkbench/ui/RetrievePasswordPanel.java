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

package org.generationcp.ibpworkbench.ui;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Mark Agarrado
 *
 */
@Configurable
public class RetrievePasswordPanel extends VerticalLayout implements InitializingBean {

	private static final long serialVersionUID = -7706262821699961159L;

	private final SecurityQuestion securityQuestion;
	private final String userPassword;

	private TextField securityAnswerField;
	private Button retrieveBtn;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public RetrievePasswordPanel(SecurityQuestion securityQuestion, String userPassword) {
		this.securityQuestion = securityQuestion;
		this.userPassword = userPassword;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	protected void initializeComponents() {
		Label securityQuestionLabel = new Label(this.securityQuestion.getSecurityQuestion());
		securityQuestionLabel.setDebugId("securityQuestionLabel");
		this.addComponent(securityQuestionLabel);
		this.securityAnswerField = new TextField();
		this.securityAnswerField.setDebugId("securityAnswerField");
		this.securityAnswerField.setDebugId("vaadin_answer_txt");
		this.addComponent(this.securityAnswerField);

		this.retrieveBtn = new Button(this.messageSource.getMessage(Message.BTN_RETRIEVE));
		this.retrieveBtn.setDebugId("retrieveBtn");
		this.addComponent(this.retrieveBtn);
	}

	protected void initializeLayout() {
		this.setSpacing(true);
		this.setMargin(true);
		this.setComponentAlignment(this.retrieveBtn, Alignment.MIDDLE_RIGHT);
	}

	protected void initializeActions() {
		this.retrieveBtn.addListener(new RetrieveButtonClickListener());
	}

	private class RetrieveButtonClickListener implements ClickListener {

		private static final long serialVersionUID = 6073105923144151761L;

		@Override
		public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
			String userAnswer = (String) RetrievePasswordPanel.this.securityAnswerField.getValue();
			if (userAnswer.equals(RetrievePasswordPanel.this.securityQuestion.getSecurityAnswer())) {
				MessageNotifier
						.showWarning(event.getButton().getWindow(),
								RetrievePasswordPanel.this.messageSource.getMessage(Message.YOUR_PASSWORD),
								RetrievePasswordPanel.this.userPassword);
			} else {
				MessageNotifier.showError(event.getButton().getWindow(),
						RetrievePasswordPanel.this.messageSource.getMessage(Message.ERROR_IN_PASSWORD_RETRIEVAL),
						RetrievePasswordPanel.this.messageSource.getMessage(Message.ERROR_SECURITY_ANSWER_INVALID));
			}
		}
	}

}
