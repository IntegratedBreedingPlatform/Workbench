
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.GenotypeFactorValueChangeListener;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.oms.TermId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class SingleSiteAnalysisGenotypesComponent extends VerticalLayout
		implements InitializingBean, InternationalizableComponent, GermplasmStudyBrowserLayout {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Label lblGenotypes;
	private Label lblSpecifyGenotypesHeader;
	private Select selGenotypes;

	private final SingleSiteAnalysisDetailsPanel ssaDetailsPanel;

	public SingleSiteAnalysisGenotypesComponent(final SingleSiteAnalysisDetailsPanel ssaDetailsPanel) {
		super();
		this.ssaDetailsPanel = ssaDetailsPanel;
	}

	@Override
	public void instantiateComponents() {
		this.lblGenotypes = new Label();
		this.lblGenotypes.setDebugId("lblGenotypes");
		this.lblGenotypes.setContentMode(Label.CONTENT_XHTML);
		this.lblGenotypes.setWidth("150px");
		this.lblGenotypes.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);

		this.lblSpecifyGenotypesHeader =
				new Label("<span class='bms-factors' style='color: #39B54A; " + "font-size: 20px; font-weight: bold;'></span><b>&nbsp;"
						+ this.messageSource.getMessage(Message.BV_SPECIFY_GENOTYPES_HEADER) + "</b>", Label.CONTENT_XHTML);
		this.lblSpecifyGenotypesHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.selGenotypes = new Select();
		this.selGenotypes.setImmediate(true);
		this.selGenotypes.setNullSelectionAllowed(true);
		this.selGenotypes.setNewItemsAllowed(false);
		this.selGenotypes.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

	}

	@Override
	public void initializeValues() {
		this.populateChoicesForGenotypes();

	}

	@Override
	public void addListeners() {
		this.selGenotypes.addListener(new GenotypeFactorValueChangeListener(this.ssaDetailsPanel.getEnvironmentsComponent()));

	}

	@Override
	public void layoutComponents() {
		final GridLayout gLayout = new GridLayout(2, 2);
		gLayout.setDebugId("gLayout");
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(SingleSiteAnalysisDetailsPanel.MARGIN_TOP10);
		gLayout.addComponent(this.lblSpecifyGenotypesHeader, 0, 0, 1, 0);
		gLayout.addComponent(this.lblGenotypes, 0, 1);
		gLayout.addComponent(this.selGenotypes, 1, 1);
		this.addComponent(gLayout);
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setValue(this.lblGenotypes, Message.BV_GENOTYPES);

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	public void selectFirstItem() {
		this.selGenotypes.select(this.selGenotypes.getItemIds().iterator().next());
	}

	protected void populateChoicesForGenotypes() {
		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			if ((PhenotypicType.GERMPLASM.equals(factor.getStandardVariable().getPhenotypicType()) ||
				 factor.getStandardVariable().getId() == TermId.ENTRY_NO.getId()) &&
				!SingleSiteAnalysisDetailsPanel.GENOTYPES_TO_HIDE.contains(factor.getId())) {
				this.selGenotypes.addItem(factor.getLocalName());
				this.selGenotypes.setValue(factor.getLocalName());
			}
		}
		this.selectFirstItem();
	}

	public String getSelGenotypesValue() {
		return (String) this.selGenotypes.getValue();
	}

	protected Select getSelGenotypes() {
		return this.selGenotypes;
	}

	protected void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
