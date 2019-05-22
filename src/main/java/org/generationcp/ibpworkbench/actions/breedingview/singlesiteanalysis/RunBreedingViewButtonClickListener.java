
package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

@Configurable
public class RunBreedingViewButtonClickListener implements ClickListener {

	private static final long serialVersionUID = -6682011023617457906L;
	private static final Logger LOG = LoggerFactory.getLogger(RunBreedingViewButtonClickListener.class);

	@Autowired
	private StudyDataManager studyDataManager;

	private final SingleSiteAnalysisDetailsPanel ssaDetailsPanel;
	private RunSingleSiteAction runSingleSiteAction;

	public RunBreedingViewButtonClickListener(final SingleSiteAnalysisDetailsPanel ssaDetailsPanel) {
		super();
		this.ssaDetailsPanel = ssaDetailsPanel;
		this.runSingleSiteAction = new RunSingleSiteAction(ssaDetailsPanel);
	}

	@Override
	public void buttonClick(final ClickEvent event) {

		if (Boolean.parseBoolean(this.ssaDetailsPanel.getIsServerApp())) {
			this.runSingleSiteAction.buttonClick(event);
			return;
		}

		final List<DataSet> dataSets;
		try {

			dataSets = this.studyDataManager.getDataSetsByType(
				this.ssaDetailsPanel.getBreedingViewInput().getStudyId(),
				DatasetTypeEnum.MEANS_DATA.getId());
			if (!dataSets.isEmpty()) {

				final DataSet meansDataSet = dataSets.get(0);
				final TrialEnvironments envs = this.studyDataManager.getTrialEnvironmentsInDataset(meansDataSet.getId());

				Boolean environmentExists = false;
				for (final SeaEnvironmentModel model : this.ssaDetailsPanel.getSelectedEnvironments()) {

					final TrialEnvironment env = envs
						.findOnlyOneByLocalName(this.ssaDetailsPanel.getBreedingViewInput().getTrialInstanceName(), model.getTrialno());
					if (env != null) {
						environmentExists = true;
						break;
					}

				}

				if (environmentExists) {
					ConfirmDialog.show(event.getComponent().getWindow(), "",
						"One or more of the selected traits has existing means data. If you save the results of this analysis, the existing values will be overwritten.",
						"OK", "Cancel", new Runnable() {

							@Override
							public void run() {

								new RunSingleSiteAction(RunBreedingViewButtonClickListener.this.ssaDetailsPanel).buttonClick(event);
							}

						});
				} else {
					this.runSingleSiteAction.buttonClick(event);
				}

			} else {
				this.runSingleSiteAction.buttonClick(event);
			}

		} catch (final Exception e) {
			this.runSingleSiteAction.buttonClick(event);
			RunBreedingViewButtonClickListener.LOG.error(e.getMessage(), e);
		}

	}

	protected void setRunSingleSiteAction(final RunSingleSiteAction runSingleSiteAction) {
		this.runSingleSiteAction = runSingleSiteAction;
	}

}
