
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

		if (value.equals(ExperimentDesignType.ROW_COL.getBvDesignName())) {

			this.source.displayRowColumnDesignElements();

		} else if (value.equals(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvDesignName())) {

			this.source.displayIncompleteBlockDesignElements();

		} else if (value.equals(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvDesignName())) {

			this.source.displayRandomizedBlockDesignElements();

		} else if (value.equals(ExperimentDesignType.P_REP.getBvDesignName())) {

			this.source.displayPRepDesignElements();

		} else if (value.equals(ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getBvDesignName())) {

			this.source.displayAugmentedDesignElements();
		}
	}

}
