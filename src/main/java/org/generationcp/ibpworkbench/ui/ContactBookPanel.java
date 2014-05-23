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

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.manager.IContactManager;
import org.generationcp.ibpworkbench.manager.MockContactManager;
import org.generationcp.middleware.pojos.workbench.Contact;

import java.util.List;

public class ContactBookPanel extends VerticalLayout{

    private static final long serialVersionUID = 1L;

    private Label lblContactBook;
    private TextField txtSearch;
    private Table tblContact;

    public ContactBookPanel() {
        assemble();
    }

    protected void assemble() {
        initializeComponent();
        initializeLayout();

    }

    protected void initializeLayout() {
        setMargin(true);
        setSpacing(true);

        addComponent(lblContactBook);
        addComponent(txtSearch);
        addComponent(tblContact);
    }

    protected void initializeComponent() {
        lblContactBook = new Label("Contact Book");
        lblContactBook.setStyleName(Bootstrap.Typography.H1.styleName());

        txtSearch = new TextField();
        txtSearch.setInputPrompt("Search Contacts");

        tblContact = new Table();

        tblContact.setWidth("90%");
        tblContact.setHeight("100%");

        IContactManager projectManager = MockContactManager.getInstance();
        List<Contact> contacts = projectManager.getContacts();

        BeanContainer<String, Contact> contactContainer = new BeanContainer<String, Contact>(Contact.class);
        contactContainer.setBeanIdProperty("contactId");
        for (Contact contact : contacts) {
            contactContainer.addBean(contact);
        }
        tblContact.setContainerDataSource(contactContainer);

        tblContact.setColumnHeader("firstName", "Name");
        tblContact.setColumnHeader("email", "Email");
        tblContact.setColumnHeader("address", "Address");
        tblContact.setColumnHeader("phoneNumber", "Phone Number");

        String[] columns = new String[] { "firstName", "email", "address1", "phoneNumber" };
        tblContact.setVisibleColumns(columns);
    }

}
