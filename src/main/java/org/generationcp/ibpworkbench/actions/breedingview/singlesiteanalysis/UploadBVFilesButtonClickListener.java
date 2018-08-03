package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import java.util.HashMap;
import java.util.Map;

import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.middleware.domain.dms.DMSVariableType;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;


public class UploadBVFilesButtonClickListener implements ClickListener {

	private static final long serialVersionUID = 1L;
	
	private SingleSiteAnalysisDetailsPanel ssaDetailsPanel;

	public UploadBVFilesButtonClickListener(final SingleSiteAnalysisDetailsPanel ssaDetailsPanel) {
		super();
		this.ssaDetailsPanel = ssaDetailsPanel;
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		final Map<String, Boolean> visibleTraitsMap = new HashMap<>();
		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			visibleTraitsMap.put(factor.getLocalName(), true);
		}
		visibleTraitsMap.putAll(this.ssaDetailsPanel.getBreedingViewInput().getVariatesActiveState());

		final FileUploadBreedingViewOutputWindow window = new FileUploadBreedingViewOutputWindow(event.getComponent().getWindow(),
				this.ssaDetailsPanel.getBreedingViewInput().getStudyId(), this.ssaDetailsPanel.getProject(),
				visibleTraitsMap);

		event.getComponent().getWindow().addWindow(window);

	}

}
