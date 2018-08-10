
package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import java.util.List;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisEnvironmentsComponent;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

public class GenotypeFactorValueChangeListener implements ValueChangeListener {

	private static final long serialVersionUID = 1L;

	private final SingleSiteAnalysisEnvironmentsComponent environmentsComponent;

	public GenotypeFactorValueChangeListener(final SingleSiteAnalysisEnvironmentsComponent environmentsComponent) {
		super();
		this.environmentsComponent = environmentsComponent;
	}

	@Override
	public void valueChange(final ValueChangeEvent event) {
		final List<String> invalidEnvironments = this.environmentsComponent.getInvalidEnvironments();
		if (!invalidEnvironments.isEmpty()) {

			MessageNotifier.showError(this.environmentsComponent.getWindow(), SingleSiteAnalysisDetailsPanel.INVALID_SELECTION_STRING,
					this.environmentsComponent.getSelEnvFactorValue() + " " + StringUtil.joinIgnoreEmpty(",", invalidEnvironments) + " "
							+ SingleSiteAnalysisDetailsPanel.INCOMPLETE_PLOT_DATA_ERROR);
		}

	}

}
