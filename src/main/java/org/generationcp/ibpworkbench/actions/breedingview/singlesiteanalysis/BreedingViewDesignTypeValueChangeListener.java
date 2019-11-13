
package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDesignDetails;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;

public class BreedingViewDesignTypeValueChangeListener implements ValueChangeListener {

	private static final long serialVersionUID = -6425208753343322313L;

	private SingleSiteAnalysisDesignDetails source;

	public BreedingViewDesignTypeValueChangeListener(final SingleSiteAnalysisDesignDetails source) {
		this.source = source;
	}

	@Override
	public void valueChange(final ValueChangeEvent event) {
		final String value = (String) event.getProperty().getValue();

		if (value.equals(ExperimentDesignType.ROW_COL.getBvName())) {

			this.source.displayRowColumnDesignElements();

		} else if (value.equals(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName())) {

			this.source.displayIncompleteBlockDesignElements();

		} else if (value.equals(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName())) {

			this.source.displayRandomizedBlockDesignElements();

		} else if (value.equals(ExperimentDesignType.P_REP.getBvName())) {

			this.source.displayPRepDesignElements();

		} else if (value.equals(ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getBvName())) {

			this.source.displayAugmentedDesignElements();
		}
	}

}
