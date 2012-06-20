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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginForm extends CustomComponent{

    private static final long serialVersionUID = 1L;
    private Button btnLogin;
    private TextField txtEmailAddress;
    private PasswordField pfPassword;

    public LoginForm() {
        assemble();
    }

    public Button getBtnLogin() {
        return btnLogin;
    }

    public TextField getTxtEmailAddress() {
        return txtEmailAddress;
    }

    public PasswordField getPfPassword() {
        return pfPassword;
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
        loginPanel.setHeight("120px");

        // login panel layout
        VerticalLayout loginPanelLayout = new VerticalLayout();
        loginPanelLayout.setImmediate(false);
        loginPanelLayout.setWidth("100.0%");
        loginPanelLayout.setHeight("100.0%");
        loginPanelLayout.setMargin(false);
        loginPanel.setContent(loginPanelLayout);

        // title label
        Label titleLabel = new Label("GCP Workbench - Sign-in");
        titleLabel.setWidth(null);
        titleLabel.setStyleName("gcp-form-title");
        loginPanelLayout.addComponent(titleLabel);
        loginPanelLayout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER);

        GridLayout usernamePasswordArea = buildUsernamePasswordArea();
        loginPanelLayout.addComponent(usernamePasswordArea);
        loginPanelLayout.setComponentAlignment(usernamePasswordArea, Alignment.TOP_CENTER);

        btnLogin = new Button("Login");
        loginPanelLayout.addComponent(btnLogin);
        loginPanelLayout.setComponentAlignment(btnLogin, Alignment.TOP_CENTER);

        return loginPanel;
    }

    private GridLayout buildUsernamePasswordArea() {
        // email address
        Label lblEmailAddress = new Label("Email Address");
        lblEmailAddress.setWidth(null);

        txtEmailAddress = new TextField();

        // password
        Label lblPassword = new Label("Password");
        lblPassword.setWidth(null);

        pfPassword = new PasswordField();

        GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setWidth("250px");
        gridLayout.addComponent(lblEmailAddress);
        gridLayout.addComponent(txtEmailAddress);
        gridLayout.addComponent(lblPassword);
        gridLayout.addComponent(pfPassword);

        return gridLayout;
    }

}
