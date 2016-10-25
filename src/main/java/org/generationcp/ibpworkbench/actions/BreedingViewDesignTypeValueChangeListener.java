
package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.GridLayout;

public class BreedingViewDesignTypeValueChangeListener implements ValueChangeListener {

	private static final long serialVersionUID = -6425208753343322313L;

	SingleSiteAnalysisDetailsPanel source;

	public BreedingViewDesignTypeValueChangeListener(SingleSiteAnalysisDetailsPanel source) {
		this.source = source;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		String value = (String) event.getProperty().getValue();

		if (value.equals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName())) {

			this.source.displayRowColumnDesignElements();

		} else if (value.equals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName())) {

			this.source.displayIncompleteBlockDesignElements();

		} else if (value.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName())) {

			this.source.displayRandomizedBlockDesignElements();

		} else if (value.equals(DesignType.P_REP_DESIGN.getName())){

			this.source.displayPRepDesignElements();

		} else if (value.equals(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName())){

			this.source.displayAugmentedDesignElements();
		}
	}

}
