package org.generationcp.ibpworkbench.manager;

import java.util.List;

import org.generationcp.middleware.pojos.workbench.Contact;

public interface IContactManager {
	public List<Contact> getContacts();
    public void addContact(Contact contact);
    public void removeContact(Contact contact);
}
