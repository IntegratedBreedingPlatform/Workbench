package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewWindow;
import org.generationcp.middleware.pojos.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.Window;

public class StudyTreeExpandAction implements Tree.ExpandListener{
    
    private static final Logger LOG = LoggerFactory.getLogger(StudyTreeExpandAction.class);
    private static final long serialVersionUID = -5091664285613837786L;

    private Window source;
    private TreeTable tr;

    public StudyTreeExpandAction(Window source, TreeTable tr) {
        this.source = source;
        this.tr = tr;
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
        if (source instanceof SelectDatasetForBreedingViewWindow) {
            try {
                ((SelectDatasetForBreedingViewWindow) source).queryChildrenStudies((Study)event.getItemId(), tr);
            } catch (InternationalizableException e) {
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        }
    }

}
