
package org.generationcp.ibpworkbench.ui.breedingview.metaanalysis;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.breedingview.BreedingViewTreeTable;
import org.generationcp.middleware.domain.dms.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

public class StudyTreeExpandAction implements Tree.ExpandListener {

	private static final Logger LOG = LoggerFactory.getLogger(StudyTreeExpandAction.class);
	private static final long serialVersionUID = -5091664285613837786L;

	private final SelectDatasetDialog source;
	private final BreedingViewTreeTable tr;

	public StudyTreeExpandAction(SelectDatasetDialog source, BreedingViewTreeTable tr) {
		this.source = source;
		this.tr = tr;
	}

	@Override
	public void nodeExpand(ExpandEvent event) {

		try {
			this.source.queryChildrenStudies((Reference) event.getItemId(), this.tr);

		} catch (InternationalizableException e) {
			StudyTreeExpandAction.LOG.error(e.toString() + "\n" + e.getStackTrace());
			MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
			LOG.error(e.getMessage(),e);
		}

		try {
			this.source.queryChildrenDatasets((Reference) event.getItemId(), this.tr);
		} catch (Exception e) {
			StudyTreeExpandAction.LOG.error(e.toString() + "\n" + e.getStackTrace());
			LOG.error(e.getMessage(),e);
		}
	}

}
