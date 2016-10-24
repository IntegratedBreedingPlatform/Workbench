
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

		if (value == null) {

			GridLayout gLayout = new GridLayout(2, 2);
			gLayout.setDebugId("gLayout");
			gLayout.setColumnExpandRatio(0, 0);
			gLayout.setColumnExpandRatio(1, 1);
			gLayout.setWidth("100%");
			gLayout.setSpacing(true);
			gLayout.addStyleName("marginTop10");

			this.source.getDesignDetailsContainer().removeAllComponents();
			gLayout.addComponent(this.source.getLblSpecifyGenotypesHeader(), 0, 0, 1, 0);
			gLayout.addComponent(this.source.getLblGenotypes(), 0, 1);
			gLayout.addComponent(this.source.getSelGenotypes(), 1, 1);
			this.source.getDesignDetailsContainer().addComponent(gLayout);
			return;
		}

		if (value.equals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName())) {

			this.source.displayRowColumnDesignElements();

		} else if (value.equals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName())) {

			this.source.displayIncompleteBlockDesignElements();

		} else if (value.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName())) {

			this.source.displayRandomizedBlockDesignElements();

		} else if (value.equals(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName())) {
			this.source.getSelColumnFactor().setEnabled(false);
			this.source.getSelRowFactor().setEnabled(false);
			this.source.getSelBlocks().setEnabled(true);
		} else if (value.equals(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName())) {
			this.source.getSelColumnFactor().setEnabled(true);
			this.source.getSelRowFactor().setEnabled(true);
			this.source.getSelBlocks().setEnabled(false);
		} else if (value.equals(DesignType.P_REP_DESIGN.getName())){

			this.source.displayPRepDesignElements();
		}
	}

}
