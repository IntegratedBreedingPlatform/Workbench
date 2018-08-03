package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisEnvironmentsComponent;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;

public class SSAEnvironmentsCheckboxValueChangeListener implements ValueChangeListener {

	private static final long serialVersionUID = 1L;
	
	private SingleSiteAnalysisEnvironmentsComponent ssaEnvironmentsComponent;

	public SSAEnvironmentsCheckboxValueChangeListener(final SingleSiteAnalysisEnvironmentsComponent ssaEnvironmentsComponent) {
		super();
		this.ssaEnvironmentsComponent = ssaEnvironmentsComponent;
	}

	@Override
	public void valueChange(final ValueChangeEvent event) {

		final CheckBox checkbox = (CheckBox) event.getProperty();
		final Boolean isChecked = (Boolean) event.getProperty().getValue();

		final SeaEnvironmentModel checkboxEnvironmentModel = (SeaEnvironmentModel) checkbox.getData();
		checkboxEnvironmentModel.setActive(isChecked);

		if (!isChecked) {

			// If any of the checkbox options is unchecked, make sure the "Check All" checkbox is also unchecked.
			// Remove the footer checkbox listener temporarily to avoid firing of value change event
			this.ssaEnvironmentsComponent.getFooterCheckBox().removeListener(this.ssaEnvironmentsComponent.getFooterCheckBoxListener());
			this.ssaEnvironmentsComponent.getFooterCheckBox().setValue(false);

			// Then add again the footer checkbox listener
			this.ssaEnvironmentsComponent.getFooterCheckBox().addListener(this.ssaEnvironmentsComponent.getFooterCheckBoxListener());

		} else {
			
			final Boolean studyContainsMinimumData = 
					this.ssaEnvironmentsComponent.environmentContainsValidDataForAnalysis(checkboxEnvironmentModel);

			if (!studyContainsMinimumData) {
				MessageNotifier.showError(this.ssaEnvironmentsComponent.getWindow(),
						SingleSiteAnalysisDetailsPanel.INVALID_SELECTION_STRING,
						this.ssaEnvironmentsComponent.getSelEnvFactorValue() + " \"" + checkboxEnvironmentModel
								.getEnvironmentName()
								+ "\"" + SingleSiteAnalysisDetailsPanel.INCOMPLETE_PLOT_DATA_ERROR);
				checkbox.setValue(false);
				checkboxEnvironmentModel.setActive(false);
			}

		}

	}
}
