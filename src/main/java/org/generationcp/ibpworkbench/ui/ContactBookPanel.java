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

import java.util.List;

import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.manager.IContactManager;
import org.generationcp.ibpworkbench.manager.MockContactManager;
import org.generationcp.middleware.pojos.workbench.Contact;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ContactBookPanel extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private Label lblContactBook;
	private TextField txtSearch;
	private Table tblContact;

	public ContactBookPanel() {
		this.assemble();
	}

	protected void assemble() {
		this.initializeComponent();
		this.initializeLayout();

	}

	protected void initializeLayout() {
		this.setMargin(true);
		this.setSpacing(true);

		this.addComponent(this.lblContactBook);
		this.addComponent(this.txtSearch);
		this.addComponent(this.tblContact);
	}

	protected void initializeComponent() {
		this.lblContactBook = new Label("Contact Book");
		this.lblContactBook.setDebugId("lblContactBook");
		this.lblContactBook.setStyleName(Bootstrap.Typography.H1.styleName());

		this.txtSearch = new TextField();
		this.txtSearch.setDebugId("txtSearch");
		this.txtSearch.setInputPrompt("Search Contacts");

		this.tblContact = new Table();
		this.tblContact.setDebugId("tblContact");

		this.tblContact.setWidth("90%");
		this.tblContact.setHeight("100%");

		IContactManager projectManager = MockContactManager.getInstance();
		List<Contact> contacts = projectManager.getContacts();

		BeanContainer<String, Contact> contactContainer = new BeanContainer<String, Contact>(Contact.class);
		contactContainer.setBeanIdProperty("contactId");
		for (Contact contact : contacts) {
			contactContainer.addBean(contact);
		}
		this.tblContact.setContainerDataSource(contactContainer);

		this.tblContact.setColumnHeader("firstName", "Name");
		this.tblContact.setColumnHeader("email", "Email");
		this.tblContact.setColumnHeader("address", "Address");
		this.tblContact.setColumnHeader("phoneNumber", "Phone Number");

		String[] columns = new String[] {"firstName", "email", "address1", "phoneNumber"};
		this.tblContact.setVisibleColumns(columns);
	}

}
