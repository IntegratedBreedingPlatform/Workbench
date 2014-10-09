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

package org.generationcp.ibpworkbench.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


/**
 * @author Mark Agarrado
 *
 */
@Configurable
public class RetrievePasswordPanel extends VerticalLayout implements InitializingBean {

    private static final long serialVersionUID = -7706262821699961159L;
    
    private SecurityQuestion securityQuestion;
    private String userPassword;
    
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
        assemble();
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    protected void initializeComponents() {
        Label securityQuestionLabel = new Label(securityQuestion.getSecurityQuestion());
        addComponent(securityQuestionLabel);
        securityAnswerField = new TextField();
        securityAnswerField.setDebugId("vaadin-answer-forgotpassword");
        addComponent(securityAnswerField);
        
        retrieveBtn = new Button(messageSource.getMessage(Message.BTN_RETRIEVE));
        addComponent(retrieveBtn);
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        setComponentAlignment(retrieveBtn, Alignment.MIDDLE_RIGHT);
    }
    
    protected void initializeActions() {
        retrieveBtn.addListener(new RetrieveButtonClickListener());
    }
    
    private class RetrieveButtonClickListener implements ClickListener {
        private static final long serialVersionUID = 6073105923144151761L;
        
        @Override
        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
            String userAnswer = (String) securityAnswerField.getValue();
            if (userAnswer.equals(securityQuestion.getSecurityAnswer())) {
                MessageNotifier.showWarning(event.getButton().getWindow(),
                        messageSource.getMessage(Message.YOUR_PASSWORD),
                        userPassword);
            } else {
                MessageNotifier.showError(event.getButton().getWindow(),
                        messageSource.getMessage(Message.ERROR_IN_PASSWORD_RETRIEVAL),
                        messageSource.getMessage(Message.ERROR_SECURITY_ANSWER_INVALID));
            }
        }
    }

}
