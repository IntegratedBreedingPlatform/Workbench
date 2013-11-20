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

package org.generationcp.ibpworkbench.ui.project.create;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

/**
 * Listener for accordion tab select action.
 * 
 * @author Joyce Avestro
 *
 */
public class WorkbenchSelectedTabChangeListener implements TabSheet.SelectedTabChangeListener{

    private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSelectedTabChangeListener.class);
    private static final long serialVersionUID = 1L;
    private Object source;

    public WorkbenchSelectedTabChangeListener(Object source) {
        this.source = source;
    }

    @Override
    public void selectedTabChange(SelectedTabChangeEvent event) {

        if (source instanceof CreateProjectAccordion) {

            try {
                ((CreateProjectAccordion) source).selectedTabChangeAction();
            } catch (InternationalizableException e) {
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(((CreateProjectAccordion) source).getWindow(), e.getCaption(), e.getDescription());
            }
        }
    }

}
