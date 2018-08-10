
package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisEnvironmentsComponent;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

public class BreedingViewEnvFactorValueChangeListener implements ValueChangeListener {

	private static final long serialVersionUID = -6425208753343322313L;

	private final SingleSiteAnalysisEnvironmentsComponent source;

	public BreedingViewEnvFactorValueChangeListener(final SingleSiteAnalysisEnvironmentsComponent source) {
		this.source = source;
	}

	@Override
	public void valueChange(final ValueChangeEvent event) {
		this.source.populateChoicesForEnvForAnalysis();
	}

}
