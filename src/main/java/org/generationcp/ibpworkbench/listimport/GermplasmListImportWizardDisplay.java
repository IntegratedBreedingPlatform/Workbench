
package org.generationcp.ibpworkbench.listimport;

import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.constants.AppConstants.CssStyles;
import org.generationcp.ibpworkbench.customcomponent.BreedingManagerWizardDisplay;

import com.vaadin.ui.Label;

public class GermplasmListImportWizardDisplay extends BreedingManagerWizardDisplay {

	private static final long serialVersionUID = 1251783649172220389L;

	public GermplasmListImportWizardDisplay(String... steps) {
		super(steps);
	}

	@Override
	protected String getLabelWidth() {
		return "250px";
	}

	@Override
	protected String getLabelStyleName() {
		return Bootstrap.Typography.H4.styleName() + " " + CssStyles.BOLD;
	}

	@Override
	protected boolean showAllSteps() {
		return false;
	}

	@Override
	protected boolean displayStepNumber() {
		return false;
	}

	@Override
	protected void updateSelectedStep() {
		for (int i = 0; i < this.wizardLabels.size(); i++) {
			Label label = this.wizardLabels.get(i);
			label.setVisible(i == this.currentIndex);
		}
	}
}
