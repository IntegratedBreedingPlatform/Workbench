
package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

public class BreedingViewEnvFactorValueChangeListener implements ValueChangeListener {

	private static final long serialVersionUID = -6425208753343322313L;

	SingleSiteAnalysisDetailsPanel source;

	public BreedingViewEnvFactorValueChangeListener(SingleSiteAnalysisDetailsPanel source) {
		this.source = source;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		this.source.populateChoicesForEnvForAnalysis();
	}

}
