
package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;
import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisEnvironmentsComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class SSAEnvironmentsFooterCheckboxListener implements ValueChangeListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(SSAEnvironmentsFooterCheckboxListener.class);

	private final SingleSiteAnalysisEnvironmentsComponent ssaEnvironmentsComponent;

	public SSAEnvironmentsFooterCheckboxListener(final SingleSiteAnalysisEnvironmentsComponent ssaEnvironmentsComponent) {
		super();
		this.ssaEnvironmentsComponent = ssaEnvironmentsComponent;
	}

	@Override
	public void valueChange(final ValueChangeEvent event) {

		final boolean selected = (Boolean) event.getProperty().getValue();
		if (!selected) {
			this.disableEnvironmentEntries();
			return;
		}

		try {

			final List<String> invalidEnvironments = this.ssaEnvironmentsComponent.getInvalidEnvironments(true);
			if (!invalidEnvironments.isEmpty()) {
				MessageNotifier
						.showError(this.ssaEnvironmentsComponent.getWindow(), SingleSiteAnalysisDetailsPanel.INVALID_SELECTION_STRING,
								this.ssaEnvironmentsComponent.getSelEnvFactorValue() + " "
										+ StringUtil.joinIgnoreEmpty(",", invalidEnvironments) + " "
										+ SingleSiteAnalysisDetailsPanel.INCOMPLETE_PLOT_DATA_ERROR);
			}

		} catch (final Exception e) {
			SSAEnvironmentsFooterCheckboxListener.LOG.error(e.getMessage(), e);
		}

	}

	private void disableEnvironmentEntries() {
		final Table environmentsTable = this.ssaEnvironmentsComponent.getTblEnvironmentSelection();
		for (final Iterator<?> itr = environmentsTable.getContainerDataSource().getItemIds().iterator(); itr.hasNext();) {
			final SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();
			m.setActive(false);
		}
		environmentsTable.refreshRowCache();
	}

}
