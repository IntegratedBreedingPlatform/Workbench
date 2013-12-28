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

package org.generationcp.ibpworkbench.ui.form;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenRegisterUserAccountAction;
import org.generationcp.ibpworkbench.actions.OpenSecurityQuestionAction;
import org.generationcp.ibpworkbench.util.CookieUtils;
import org.generationcp.ibpworkbench.util.CookieUtils.LoginCookieProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

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
    private Button forgotPasswordButton;
    private Panel loginPanel;
        
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private Label lblSubTitle;

	private CheckBox rememberChk;

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

    public Panel getLoginPanel() {
    	return loginPanel;
    }
    
    protected void initialize() {
    }

    protected void initializeComponents() {
        // top-level component properties
        setWidth("100.0%");
        setHeight("100.0%");
        
        this.loginPanel = buildLoginPanel();
        setCompositionRoot(loginPanel);
        
        // initialize values based on remember me option
        if (CookieUtils.getCookieValue(LoginCookieProperties.REMEMBER_OPT).equalsIgnoreCase("true")) {
        	this.txtUsername.setValue(CookieUtils.getCookieValue(LoginCookieProperties.USERNAME));
        	this.pfPassword.setValue(CookieUtils.getCookieValue(LoginCookieProperties.PASSWORD));
        	this.rememberChk.setValue(Boolean.TRUE);
        }
    }

    protected void initializeLayout() {
    }

    protected void initializeActions() {
        registerUserAccountButton.addListener(new OpenRegisterUserAccountAction());
        
        forgotPasswordButton.addListener(new OpenSecurityQuestionAction());
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
        loginPanel.setWidth("640px");
        loginPanel.setHeight("480px");
        loginPanel.setStyleName(Reindeer.PANEL_LIGHT + " gcp-login");

        // login panel layout
        VerticalLayout loginPanelLayout = new VerticalLayout();
        loginPanelLayout.setImmediate(false);
        loginPanelLayout.setStyleName("gcp-login");
        loginPanelLayout.setMargin(true, false, true, false);
        loginPanel.setContent(loginPanelLayout);

        // title label
        lblTitle = new Label();
        lblTitle.setWidth("100%");
        lblTitle.setStyleName("gcp-login-title");
        
        lblSubTitle = new Label();
        lblSubTitle.setWidth("100%");
        lblSubTitle.setStyleName("gcp-login-subtitle");
        
        loginPanelLayout.addComponent(lblTitle);
        loginPanelLayout.addComponent(lblSubTitle);
        
        rememberChk = new CheckBox(messageSource.getMessage(Message.REMEMBER_ME));
        
        GridLayout usernamePasswordArea = buildUsernamePasswordArea();
        usernamePasswordArea.setSpacing(true);
        
        loginPanelLayout.addComponent(usernamePasswordArea);
        loginPanelLayout.setComponentAlignment(usernamePasswordArea, Alignment.TOP_CENTER);
        loginPanelLayout.setExpandRatio(usernamePasswordArea, 0);
        

        btnLogin = new Button();
        btnLogin.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        btnLogin.setClickShortcut(KeyCode.ENTER);
        
        
        VerticalLayout dummy = new VerticalLayout();
        dummy.setMargin(true);
        dummy.setSizeUndefined();
        
        dummy.addComponent(btnLogin);
        
        //btnLogin.addStyleName("primary");
        loginPanelLayout.addComponent(dummy);
        loginPanelLayout.setComponentAlignment(dummy, Alignment.TOP_CENTER);
        
        
        
        HorizontalLayout subLinksLayout = new HorizontalLayout();
        subLinksLayout.setWidth("290px");
        subLinksLayout.setHeight("30px");
        //subLinksLayout.setMargin(true,false,true,false);
        subLinksLayout.setSpacing(true);
        
        
        forgotPasswordButton = new Button();
        forgotPasswordButton.setStyleName(BaseTheme.BUTTON_LINK  + " gcp-workflow-link");
        subLinksLayout.addComponent(forgotPasswordButton);
        
        registerUserAccountButton = new Button();
        registerUserAccountButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        subLinksLayout.addComponent(registerUserAccountButton);

        
        
        loginPanelLayout.addComponent(subLinksLayout);
        loginPanelLayout.setComponentAlignment(subLinksLayout, Alignment.TOP_CENTER);

        loginPanelLayout.requestRepaint();
        loginPanel.requestRepaint();
        
        return loginPanel;
    }

    private GridLayout buildUsernamePasswordArea() {
        // email address
        lblUsername = new Label();
        lblUsername.setSizeUndefined();
        lblUsername.setStyleName("gcp-loginform-lbl");
        txtUsername = new TextField();
        txtUsername.focus();

        // password
        lblPassword = new Label();
        lblPassword.setSizeUndefined();
        lblPassword.setStyleName("gcp-loginform-lbl");
        pfPassword = new PasswordField();
        
        messageArea = buildMessageArea();
        
        txtUsername.setWidth("150px"); pfPassword.setWidth("150px");


        GridLayout gridLayout = new GridLayout(2, 3);
        gridLayout.setMargin(true, false, false, false);
        gridLayout.setWidth("250px");
        gridLayout.addComponent(lblUsername);
        gridLayout.addComponent(txtUsername);
        gridLayout.addComponent(lblPassword);
        gridLayout.addComponent(pfPassword);
        gridLayout.addComponent(new Label());
        gridLayout.addComponent(rememberChk);
        //gridLayout.addComponent(messageArea, 0, 2, 1, 2);

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
        messageSource.setValue(lblTitle, Message.LOGIN_TITLE);
        messageSource.setValue(lblSubTitle, Message.LOGIN_SUBTITLE);
        
        messageSource.setCaption(btnLogin, Message.LOGIN);
        messageSource.setValue(lblUsername, Message.USERNAME);
        messageSource.setValue(lblPassword, Message.PASSWORD);
        messageSource.setValue(lblMessage, Message.ERROR_LOGIN_INVALID);
        messageSource.setCaption(forgotPasswordButton, Message.FORGOT_PASSWORD);
        messageSource.setCaption(registerUserAccountButton, Message.REGISTER_USER_ACCOUNT);
    }
    
    public boolean isRememberEnabled() {
    	return rememberChk.booleanValue();
    }
}
