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

package org.generationcp.ibpworkbench.comp;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ForgotPasswordUsernamePanel extends VerticalLayout implements InitializingBean {

    private static final long serialVersionUID = -716281326638272855L;
    private static final Logger LOG = LoggerFactory.getLogger(ForgotPasswordUsernamePanel.class);
    
    private Label unameLabel;
    private TextField unameField;
    private Button nextBtn;
    
    private User user;
    private SecurityQuestion securityQuestion;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public ForgotPasswordUsernamePanel() {
        
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
        unameLabel = new Label(messageSource.getMessage(Message.ENTER_USERNAME_LABEL));
        addComponent(unameLabel);
        unameField = new TextField();
        addComponent(unameField);
        nextBtn = new Button(messageSource.getMessage(Message.BTN_NEXT));
        addComponent(nextBtn);
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        setComponentAlignment(nextBtn, Alignment.MIDDLE_RIGHT);
    }
    
    protected void initializeActions() {
        nextBtn.addListener(new NextButtonClickListener());
    }
    
    private class NextButtonClickListener implements ClickListener {
        private static final long serialVersionUID = -1465067055175075712L;

        @Override
        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
            String username = (String) unameField.getValue();
            List<User> userList = new ArrayList<User>();
            try {
                userList = workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL);
            } catch (MiddlewareQueryException e) {
                LOG.error(messageSource.getMessage(Message.ERROR_IN_USERNAME_RETRIEVAL), e);
                MessageNotifier.showError(event.getButton().getWindow(),
                        messageSource.getMessage(Message.DATABASE_ERROR),
                        messageSource.getMessage(Message.ERROR_IN_USERNAME_RETRIEVAL));
            }
            user = new User();
            if (userList.isEmpty()) {
                MessageNotifier.showError(event.getButton().getWindow(),
                        messageSource.getMessage(Message.DATABASE_ERROR),
                        messageSource.getMessage(Message.ERROR_USERNAME_INVALID));
                return;
            } else {
                user = userList.get(0);
            }
            
            List<SecurityQuestion> questions = new ArrayList<SecurityQuestion>();
            try {
                questions = workbenchDataManager.getQuestionsByUserId(user.getUserid());
            } catch (MiddlewareQueryException e) {
                LOG.error(messageSource.getMessage(Message.ERROR_IN_QUESTION_RETRIEVAL), e);
                MessageNotifier.showError(event.getButton().getWindow(),
                        messageSource.getMessage(Message.DATABASE_ERROR),
                        messageSource.getMessage(Message.ERROR_IN_QUESTION_RETRIEVAL));
            }
            if (questions.isEmpty()) {
                MessageNotifier.showError(event.getButton().getWindow(),
                        messageSource.getMessage(Message.DATABASE_ERROR),
                        messageSource.getMessage(Message.ERROR_NO_SECURITY_QUESTIONS, username));
            } else {
                securityQuestion = questions.get(0);
                RetrievePasswordPanel retrievePanel = new RetrievePasswordPanel(securityQuestion, user.getPassword());
                event.getButton().getWindow().setContent(retrievePanel);
            }
        }
    }
}
