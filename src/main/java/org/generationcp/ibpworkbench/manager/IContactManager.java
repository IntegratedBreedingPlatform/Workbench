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

package org.generationcp.ibpworkbench.manager;

import java.util.List;

import org.generationcp.middleware.pojos.workbench.Contact;

public interface IContactManager{

    public List<Contact> getContacts();

    public void addContact(Contact contact);

    public void removeContact(Contact contact);
}
