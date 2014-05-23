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

import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NurseryListTreeExpandListener implements Tree.ExpandListener{
    
	private static final Logger LOG = LoggerFactory.getLogger(NurseryListTreeExpandListener.class);
	private static final long serialVersionUID = 4340373177977306882L;


    private Component source;

    public NurseryListTreeExpandListener(Component source) {
        this.source = source;
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
        if (source instanceof NurseryListPreview) {
            try {
                String id = event.getItemId().toString();
                int studyId = Integer.valueOf(id);
                
               ((NurseryListPreview) source).getPresenter().addChildrenNode(studyId);
            }catch (NumberFormatException e) {
                LOG.error("Click on the root");
            } 
            catch (InternationalizableException e) {
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        }
    }

}
