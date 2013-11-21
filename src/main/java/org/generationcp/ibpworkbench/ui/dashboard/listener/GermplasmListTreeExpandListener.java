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

package  org.generationcp.ibpworkbench.ui.dashboard.listener;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

public class GermplasmListTreeExpandListener implements Tree.ExpandListener{
    
    private static final Logger LOG = LoggerFactory.getLogger(GermplasmListTreeExpandListener.class);
    private static final long serialVersionUID = -5145904396164706110L;

    private Component source;

    public GermplasmListTreeExpandListener(Component source) {
        this.source = source;
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
        if (source instanceof GermplasmListPreview) {
            try {
               ((GermplasmListPreview) source).addGermplasmListNode(Integer.valueOf(event.getItemId().toString()));
            } catch (InternationalizableException e) {
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        }
    }

}
