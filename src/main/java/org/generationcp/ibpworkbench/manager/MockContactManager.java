/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.ibpworkbench.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.workbench.Contact;

public class MockContactManager implements IContactManager {
	public List<Contact> contacts;
	
	protected MockContactManager(){
		contacts = new ArrayList<Contact>();
		
		Contact contact1 = new Contact();
		contact1.setContactId(1L);
		contact1.setFirstName("Stelle Andrews");
		contact1.setEmail("Stelle.Andrews@emailaddress.com");
		contact1.setPhoneNumber("213-345-0864");
		contact1.setAddress1("Newark");
		
		Contact contact2 = new Contact();
		contact2.setContactId(2L);
		contact2.setFirstName("Carrol Burnel");
		contact2.setEmail("Carrol.Burnel@emailaddress.com");
		contact2.setPhoneNumber("415-960-2790");
		contact2.setAddress1("San Jose");
		
		Contact contact3 = new Contact();
		contact3.setContactId(3L);
		contact3.setFirstName("Ellen Calcote");
		contact3.setEmail("Ellen.Calcote@emailaddress.com");
		contact3.setPhoneNumber("213-345-9863");
		contact3.setAddress1("Newark");
		
		contacts.add(contact1);
		contacts.add(contact2);
		contacts.add(contact3);
	}
	public List<Contact> getContacts(){
		return contacts;
	}
	public void addContact(Contact contact) {
		contacts.add(contact);
	}
	public void removeContact(Contact contact) {
		contacts.remove(contact);
	}
	public static IContactManager getInstance() {
		// TODO Auto-generated method stub
		return SingletonHolder.instance;
	}
	private static class SingletonHolder {
        public static final IContactManager instance = new MockContactManager();
    }
}
