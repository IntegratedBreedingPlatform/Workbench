
package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class RunBreedingViewButtonClickListener implements ClickListener {

	private static final long serialVersionUID = -6682011023617457906L;

	private RunSingleSiteAction runSingleSiteAction;

	public RunBreedingViewButtonClickListener(final SingleSiteAnalysisDetailsPanel ssaDetailsPanel) {
		super();
		this.runSingleSiteAction = new RunSingleSiteAction(ssaDetailsPanel);
	}

	@Override
	public void buttonClick(final ClickEvent event) {

		this.runSingleSiteAction.buttonClick(event);
	}

	protected void setRunSingleSiteAction(final RunSingleSiteAction runSingleSiteAction) {
		this.runSingleSiteAction = runSingleSiteAction;
	}

}
