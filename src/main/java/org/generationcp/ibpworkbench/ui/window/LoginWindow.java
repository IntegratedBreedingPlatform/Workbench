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

package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.LoginPresenter;
import org.generationcp.ibpworkbench.actions.OpenSecurityQuestionAction;
import org.generationcp.ibpworkbench.ui.programmembers.OpenRegisterUserAccountAction;
import org.generationcp.ibpworkbench.util.CookieUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class LoginWindow extends Window implements InitializingBean {
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    private TextField usernameFld;
    private PasswordField passwordFld;
    private CheckBox rememberChk;
    private Button forgotPasswordBtn;
    private Button registerUserBtn;
    private Button loginBtn;

    private LoginPresenter presenter;


    public LoginWindow() {
        super("Breeding Management System | Workbench");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();

        presenter = new LoginPresenter(this);
    }

    private void initializeActions() {
        loginBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                presenter.doLogin(usernameFld.getValue().toString(),passwordFld.getValue().toString(),clickEvent);
            }
        });

        registerUserBtn.addListener(new OpenRegisterUserAccountAction());
        forgotPasswordBtn.addListener(new OpenSecurityQuestionAction());

    }

    private void initializeLayout() {
        this.setSizeFull();

        final Panel loginPanel = new Panel();

        final VerticalLayout loginPanelContainer = new VerticalLayout();
        loginPanelContainer.setSizeFull();

        loginPanel.setContent(loginPanelContainer);

        loginPanel.setWidth("640px");
        loginPanel.setHeight("417px");
        loginPanel.setStyleName(Reindeer.PANEL_LIGHT + " gcp-login");

        final Label loginTitle = new Label(messageSource.getMessage(Message.LOGIN_TITLE),Label.CONTENT_XHTML);
        loginTitle.setStyleName("gcp-login-title");

        loginPanel.addComponent(loginTitle);

        final ComponentContainer loginFrm = layoutLoginForm();
        loginPanelContainer.addComponent(loginFrm);
        loginPanelContainer.setComponentAlignment(loginFrm, Alignment.MIDDLE_CENTER);
        loginPanelContainer.setExpandRatio(loginFrm,1.0F);

        final VerticalLayout root = new VerticalLayout();
        root.addComponent(loginPanel);
        root.setComponentAlignment(loginPanel,Alignment.MIDDLE_CENTER);
        root.setSizeFull();

        this.setContent(root);
    }

    private void initializeValues() {
        // initialize values based on remember me option
        if (CookieUtils.getCookieValue(CookieUtils.LoginCookieProperties.REMEMBER_OPT).equalsIgnoreCase("true")) {
            this.usernameFld.setValue(CookieUtils.getCookieValue(CookieUtils.LoginCookieProperties.USERNAME));
            this.passwordFld.setValue(CookieUtils.getCookieValue(CookieUtils.LoginCookieProperties.PASSWORD));
            this.rememberChk.setValue(Boolean.TRUE);
        }
    }

    private void initializeComponents() {
        usernameFld = new TextField();
        usernameFld.setDebugId("vaadin-username-txt");
        usernameFld.setNullRepresentation("");
        usernameFld.setNullSettingAllowed(false);


        passwordFld = new PasswordField();
        passwordFld.setDebugId("vaadin-password-txt");
        passwordFld.setNullRepresentation("");
        passwordFld.setNullSettingAllowed(false);

        rememberChk = new CheckBox();

        forgotPasswordBtn = new Button(messageSource.getMessage(Message.FORGOT_PASSWORD));
        registerUserBtn = new Button("<span style='padding-right:10px; position: relative; top: 2px'>" + messageSource.getMessage(Message.REGISTER_USER_ACCOUNT) + "</span>");
        registerUserBtn.setHtmlContentAllowed(true);
        loginBtn = new Button(messageSource.getMessage(Message.SIGNIN));
        loginBtn.setDebugId("vaadin-login-btn");
    }

    private ComponentContainer layoutLoginForm() {
        forgotPasswordBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());
        registerUserBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());
        loginBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        loginBtn.setWidth("146px");
        passwordFld.setWidth("146px");
        usernameFld.setWidth("146px");

        usernameFld.setTabIndex(1);
        passwordFld.setTabIndex(2);
        rememberChk.setTabIndex(3);
        loginBtn.setTabIndex(4);
        registerUserBtn.setTabIndex(5);
        forgotPasswordBtn.setTabIndex(6);

        usernameFld.focus();

        final GridLayout loginFrmLayout = new GridLayout(2,5);
        loginFrmLayout.setSpacing(true);

        final Label dummyLabel = new Label(" ",Label.CONTENT_XHTML);

        final Label usernameLbl = new Label("<b>" + (messageSource.getMessage(Message.USERNAME) + ":").toUpperCase(),Label.CONTENT_XHTML);
        final Label passwordLbl = new Label("<b>" + (messageSource.getMessage(Message.PASSWORD)+ ":").toUpperCase() + "</b>",Label.CONTENT_XHTML);
        final Label rememberMeLbl = new Label(messageSource.getMessage(Message.REMEMBER_ME) +"&nbsp;&nbsp;&nbsp;",Label.CONTENT_XHTML);


        final Label desc = new Label("Please note: If this is your first time " +
                "using BMS, you will need to register a new account using the link beside. " +
                "The BMS app does not share login information with the IBP portal.");
        desc.setWidth("300px");
        final PopupView infoPopup = new PopupView("",desc);
        infoPopup.setStyleName("gcp-login-info bms-fa-question-circle");

        // USERNAME GROUP
        final HorizontalLayout usernameGrp = new HorizontalLayout();
        usernameGrp.setSpacing(true);
        usernameGrp.addComponent(usernameFld);
        usernameGrp.addComponent(infoPopup);
        usernameGrp.addComponent(registerUserBtn);

        // PASSWORD GROUP
        final HorizontalLayout passwordGrp = new HorizontalLayout();
        passwordGrp.setSpacing(true);
        passwordGrp.addComponent(passwordFld);
        passwordGrp.addComponent(rememberChk);
        passwordGrp.addComponent(rememberMeLbl);

        // ADD TO GRID
        loginFrmLayout.addComponent(dummyLabel,0,0,1,0);
        loginFrmLayout.addComponent(usernameLbl,0,1);
        loginFrmLayout.addComponent(usernameGrp,1,1);
        loginFrmLayout.setComponentAlignment(usernameLbl,Alignment.MIDDLE_LEFT);
        loginFrmLayout.setComponentAlignment(usernameGrp,Alignment.MIDDLE_LEFT);

        loginFrmLayout.addComponent(passwordLbl,0,2);
        loginFrmLayout.addComponent(passwordGrp,1,2);
        loginFrmLayout.setComponentAlignment(passwordLbl,Alignment.MIDDLE_LEFT);
        loginFrmLayout.setComponentAlignment(passwordGrp,Alignment.MIDDLE_LEFT);


        final Label preloadFont = new Label("<span class='glyphicon glyphicon-stop' style=\"color:transparent; font-size: 1px\"></span>",Label.CONTENT_XHTML);

        final HorizontalLayout buttonContainer = new HorizontalLayout();
        buttonContainer.setSizeUndefined();
        buttonContainer.addComponent(loginBtn);
        buttonContainer.addComponent(preloadFont);

        loginFrmLayout.addComponent(buttonContainer,1,3);
        loginFrmLayout.addComponent(forgotPasswordBtn,1,4);
        
        loginFrmLayout.setComponentAlignment(forgotPasswordBtn,Alignment.TOP_LEFT);


        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.addComponent(loginFrmLayout);
        root.setComponentAlignment(loginFrmLayout,Alignment.MIDDLE_CENTER);



        return root;
    }

    public boolean isRememberEnabled() {
        return rememberChk.booleanValue();
    }
}
