package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.breedingview.metaanalysis.MetaAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.middleware.domain.dms.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyTreeExpandAction implements Tree.ExpandListener{
    
    private static final Logger LOG = LoggerFactory.getLogger(StudyTreeExpandAction.class);
    private static final long serialVersionUID = -5091664285613837786L;

    private SelectStudyDialog source;
    private TreeTable tr;
    

    public StudyTreeExpandAction(SelectStudyDialog source, TreeTable tr) {
        this.source = source;
        this.tr = tr;
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
       
            try {
            	((SelectStudyDialog) source).queryChildrenStudies((Reference)event.getItemId(), tr);
            	
            } catch (InternationalizableException e) {
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
            
    }

}
