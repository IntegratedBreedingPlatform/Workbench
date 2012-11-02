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

package org.generationcp.ibpworkbench.comp.form;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenRegisterUserAccountAction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class LoginForm extends CustomComponent implements InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;
    
    private Label lblTitle;
    private Label lblUsername;
    private Label lblPassword;
    private Button btnLogin;
    private TextField txtUsername;
    private PasswordField pfPassword;
    private Component messageArea;
    private Label lblMessage;
    private int userId;
    private Button registerUserAccountButton;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public LoginForm() {
        super();
    }
    
    @Override
    public void afterPropertiesSet() {
        assemble();
    }
    
    public int getUserId() {
    	return userId;
    }

    public Button getBtnLogin() {
        return btnLogin;
    }

    public TextField getTxtUsername() {
        return txtUsername;
    }

    public PasswordField getPfPassword() {
        return pfPassword;
    }
    
    public Component getMessageArea() {
        return messageArea;
    }
    
    public Label getMessageLabel() {
        return lblMessage;
    }

    protected void initialize() {
    }

    protected void initializeComponents() {
        // top-level component properties
        setWidth("100.0%");
        setHeight("100.0%");

        Panel panel = buildLoginPanel();
        setCompositionRoot(panel);
    }

    protected void initializeLayout() {
    }

    protected void initializeActions() {
        registerUserAccountButton.addListener(new OpenRegisterUserAccountAction());
    }

    protected void assemble() {
        initialize();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    private Panel buildLoginPanel() {
        // common part: create layout
        Panel loginPanel = new Panel();
        loginPanel.setImmediate(false);
        loginPanel.setWidth("360px");

        // login panel layout
        VerticalLayout loginPanelLayout = new VerticalLayout();
        loginPanelLayout.setImmediate(false);
        loginPanelLayout.setWidth("100.0%");
        loginPanelLayout.setHeight("100.0%");
        loginPanelLayout.setMargin(true, false, true, false);
        loginPanel.setContent(loginPanelLayout);

        // title label
        lblTitle = new Label();
        lblTitle.setWidth(null);
        lblTitle.setStyleName("gcp-section-title-large");
        loginPanelLayout.addComponent(lblTitle);
        loginPanelLayout.setComponentAlignment(lblTitle, Alignment.TOP_CENTER);
        loginPanelLayout.setExpandRatio(lblTitle, 0);

        GridLayout usernamePasswordArea = buildUsernamePasswordArea();
        loginPanelLayout.addComponent(usernamePasswordArea);
        loginPanelLayout.setComponentAlignment(usernamePasswordArea, Alignment.TOP_CENTER);
        loginPanelLayout.setExpandRatio(usernamePasswordArea, 0);

        btnLogin = new Button();
        loginPanelLayout.addComponent(btnLogin);
        loginPanelLayout.setComponentAlignment(btnLogin, Alignment.TOP_CENTER);
        
        registerUserAccountButton = new Button();
        registerUserAccountButton.setStyleName(BaseTheme.BUTTON_LINK);
        loginPanelLayout.addComponent(registerUserAccountButton);
        loginPanelLayout.setComponentAlignment(registerUserAccountButton, Alignment.BOTTOM_CENTER);

        return loginPanel;
    }

    private GridLayout buildUsernamePasswordArea() {
        // email address
        lblUsername = new Label();
        lblUsername.setWidth(null);

        txtUsername = new TextField();

        // password
        lblPassword = new Label();
        lblPassword.setWidth(null);

        pfPassword = new PasswordField();
        
        messageArea = buildMessageArea();
        
        GridLayout gridLayout = new GridLayout(2, 3);
        gridLayout.setMargin(true, false, false, false);
        gridLayout.setWidth("250px");
        gridLayout.addComponent(lblUsername);
        gridLayout.addComponent(txtUsername);
        gridLayout.addComponent(lblPassword);
        gridLayout.addComponent(pfPassword);
        gridLayout.addComponent(messageArea, 0, 2, 1, 2);

        lblMessage.setVisible(false);
        
        return gridLayout;
    }
    
    private Component buildMessageArea() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setMargin(true, false, true, false);
        
        lblMessage = new Label();
        lblMessage.setSizeUndefined();
        
        layout.addComponent(lblMessage);
        
        return layout;
    }
    
    public void setErrorMessage(String message) {
        lblMessage.setCaption(message);
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    @Override
    public void updateLabels() {
        messageSource.setValue(lblTitle, Message.login_title);
        messageSource.setCaption(btnLogin, Message.login);
        messageSource.setValue(lblUsername, Message.USERNAME);
        messageSource.setValue(lblPassword, Message.password);
        messageSource.setValue(lblMessage, Message.error_login_invalid);
        messageSource.setCaption(registerUserAccountButton, Message.REGISTER_USER_ACCOUNT);
    }
}
