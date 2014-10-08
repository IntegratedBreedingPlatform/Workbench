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
package org.generationcp.ibpworkbench.actions;

import com.vaadin.Application;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


/**
 * <b>Description</b>: Displays the workbench login window after successfully saving a new User Account.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado
 * <br>
 * <b>File Created</b>: Nov 8, 2012
 */
@Configurable
public class OpenLoginWindowFromRegistrationAction implements ClickListener{
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private static final long serialVersionUID = 5784289264247702925L;

    @Override
    public void buttonClick(ClickEvent event) {
        final Application app = event.getComponent().getApplication();

        ConfirmDialog.show(app.getMainWindow(),messageSource.getMessage(Message.REGISTER_SUCCESS),messageSource.getMessage(Message.REGISTER_SUCCESS_DESCRIPTION),messageSource.getMessage(Message.OK),null,new ConfirmDialog.Listener() {
			@Override
			public void onClose(ConfirmDialog dialog) {
				app.close();				
			}
		});
    }

}
