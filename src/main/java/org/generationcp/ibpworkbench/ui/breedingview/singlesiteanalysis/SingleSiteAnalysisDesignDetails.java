
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.BreedingViewDesignTypeValueChangeListener;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Optional;

@Configurable
public class SingleSiteAnalysisDesignDetails extends VerticalLayout
	implements InitializingBean, InternationalizableComponent, GermplasmStudyBrowserLayout {

	private static final long serialVersionUID = 1L;

	public static final String REPLICATION_FACTOR = "replication factor";
	public static final String BLOCKING_FACTOR = "blocking factor";
	public static final String ROW_FACTOR = "row in layout";
	public static final String COLUMN_FACTOR = "column in layout";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ExperimentDesignService experimentDesignService;

	private Label lblSpecifyDesignDescription;
	private Label lblDesignType;
	private Label lblReplicates;
	private Label lblBlocks;
	private Label lblSpecifyRowFactor;
	private Label lblSpecifyColumnFactor;
	private Label lblSpecifyDesignDetailsHeader;

	private ComboBox selDesignType;
	private ComboBox selReplicates;
	private ComboBox selBlocks;
	private ComboBox selRowFactor;
	private ComboBox selColumnFactor;

	private VerticalLayout designDetailsContainer;

	private final SingleSiteAnalysisDetailsPanel ssaDetailsPanel;

	public SingleSiteAnalysisDesignDetails(final SingleSiteAnalysisDetailsPanel ssaDetailsPanel) {
		super();
		this.ssaDetailsPanel = ssaDetailsPanel;
	}

	@Override
	public void instantiateComponents() {
		this.lblSpecifyDesignDescription = new Label();
		this.lblSpecifyDesignDescription.setDebugId("lblDesignDescription");

		this.lblDesignType = new Label();
		this.lblDesignType.setDebugId("lblDesignType");
		this.lblDesignType.setContentMode(Label.CONTENT_XHTML);
		this.lblDesignType.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblDesignType.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);

		this.lblReplicates = new Label();
		this.lblReplicates.setDebugId("lblReplicates");
		this.lblReplicates.setContentMode(Label.CONTENT_XHTML);
		this.lblReplicates.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblReplicates.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);

		this.lblBlocks = new Label();
		this.lblBlocks.setDebugId("lblBlocks");
		this.lblBlocks.setContentMode(Label.CONTENT_XHTML);
		this.lblBlocks.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblBlocks.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);

		this.lblSpecifyRowFactor = new Label();
		this.lblSpecifyRowFactor.setDebugId("lblSpecifyRowFactor");
		this.lblSpecifyRowFactor.setContentMode(Label.CONTENT_XHTML);
		this.lblSpecifyRowFactor.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblSpecifyRowFactor.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);

		this.lblSpecifyColumnFactor = new Label();
		this.lblSpecifyColumnFactor.setDebugId("lblSpecifyColumnFactor");
		this.lblSpecifyColumnFactor.setContentMode(Label.CONTENT_XHTML);
		this.lblSpecifyColumnFactor.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblSpecifyColumnFactor.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);

		this.lblSpecifyDesignDetailsHeader =
			new Label("<span class='bms-exp-design' style='color: #9A8478; " + "font-size: 22px; font-weight: bold;'></span><b>&nbsp;"
				+ this.messageSource.getMessage(Message.BV_SPECIFY_DESIGN_DETAILS_HEADER) + "</b>", Label.CONTENT_XHTML);
		this.lblSpecifyDesignDetailsHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		final String pleaseChoose = this.messageSource.getMessage(Message.PLEASE_CHOOSE);
		this.selDesignType = new ComboBox();
		this.selDesignType.setDebugId("selDesignType");
		this.selDesignType.setImmediate(true);
		this.selDesignType.setNullSelectionAllowed(true);
		this.selDesignType.setNewItemsAllowed(false);
		this.selDesignType.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);
		this.selDesignType.setInputPrompt(pleaseChoose);

		this.selReplicates = new ComboBox();
		this.selReplicates.setDebugId("selReplicates");
		this.selReplicates.setImmediate(true);
		this.selReplicates.setNullSelectionAllowed(true);
		this.selReplicates.setNewItemsAllowed(false);
		this.selReplicates.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);
		this.selReplicates.setInputPrompt(pleaseChoose);

		this.selBlocks = new ComboBox();
		this.selBlocks.setDebugId("selBlocks");
		this.selBlocks.setImmediate(true);
		this.selBlocks.setEnabled(false);
		this.selBlocks.setNullSelectionAllowed(false);
		this.selBlocks.setNewItemsAllowed(false);
		this.selBlocks.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);
		this.selBlocks.setInputPrompt(pleaseChoose);

		this.selRowFactor = new ComboBox();
		this.selRowFactor.setDebugId("selRowFactor");
		this.selRowFactor.setImmediate(true);
		this.selRowFactor.setNullSelectionAllowed(true);
		this.selRowFactor.setNewItemsAllowed(false);
		this.selRowFactor.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);
		this.selRowFactor.setInputPrompt(pleaseChoose);

		this.selColumnFactor = new ComboBox();
		this.selColumnFactor.setDebugId("selColumnFactor");
		this.selColumnFactor.setImmediate(true);
		this.selColumnFactor.setNullSelectionAllowed(true);
		this.selColumnFactor.setNewItemsAllowed(false);
		this.selColumnFactor.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);
		this.selColumnFactor.setInputPrompt(pleaseChoose);

		this.designDetailsContainer = new VerticalLayout();

	}

	@Override
	public void initializeValues() {
		this.populateDesignTypeOptions();
		this.populateChoicesForReplicates();
		this.populateChoicesForBlocks();
		this.populateChoicesForRowFactor();
		this.populateChoicesForColumnFactor();
		this.setDefaultValueForRowAndColumnFactors();

		this.refineChoicesForBlocksReplicationRowAndColumnFactors();
		this.displayDesignElementsBasedOnDesignTypeOfTheStudy();
	}

	private void populateDesignTypeOptions() {
		this.selDesignType.addItem(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName());
		this.selDesignType.setItemCaption(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName(), "Incomplete block design");
		this.selDesignType.addItem(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName());
		this.selDesignType.setItemCaption(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName(), "Randomized block design");
		this.selDesignType.addItem(ExperimentDesignType.ROW_COL.getBvName());
		this.selDesignType.setItemCaption(ExperimentDesignType.ROW_COL.getBvName(), "Row-column design");
		this.selDesignType.addItem(ExperimentDesignType.P_REP.getBvName());
		this.selDesignType.setItemCaption(ExperimentDesignType.P_REP.getBvName(), "P-rep design");
		this.selDesignType.addItem(ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getBvName());
		this.selDesignType.setItemCaption(ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getBvName(), "Augmented design");
	}

	@Override
	public void addListeners() {
		this.selDesignType.addListener(new BreedingViewDesignTypeValueChangeListener(this));
	}

	@Override
	public void layoutComponents() {
		final GridLayout designDetailsLayout = new GridLayout(2, 4);
		designDetailsLayout.setDebugId("designDetailsLayout");
		designDetailsLayout.setColumnExpandRatio(0, 0);
		designDetailsLayout.setColumnExpandRatio(1, 1);
		designDetailsLayout.setWidth("100%");
		designDetailsLayout.setSpacing(true);
		designDetailsLayout.setMargin(false, false, false, false);
		designDetailsLayout.addComponent(this.lblSpecifyDesignDetailsHeader, 0, 0, 1, 0);
		designDetailsLayout.addComponent(this.lblSpecifyDesignDescription, 0, 1, 1, 1);
		designDetailsLayout.addComponent(this.lblDesignType, 0, 2);
		designDetailsLayout.addComponent(this.selDesignType, 1, 2);

		this.addComponent(designDetailsLayout);
		this.addComponent(this.designDetailsContainer);
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setValue(this.lblSpecifyDesignDescription, Message.BV_DESIGN_DETAILS_DESCRIPTION);
		this.messageSource.setValue(this.lblDesignType, Message.DESIGN_TYPE);
		this.messageSource.setValue(this.lblReplicates, Message.BV_SPECIFY_REPLICATES);
		this.messageSource.setValue(this.lblBlocks, Message.BV_SPECIFY_BLOCKS);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	public void reset() {
		this.displayDesignElementsBasedOnDesignTypeOfTheStudy();
		this.selReplicates.select(this.selReplicates.getItemIds().iterator().next());
		this.setDefaultValueForRowAndColumnFactors();
	}

	private int retrieveExperimentalDesignTypeID() {
		final Integer studyId = this.ssaDetailsPanel.getBreedingViewInput().getStudyId();
		final Optional<Integer> experimentDesignTypeTermId = this.experimentDesignService.getStudyExperimentDesignTypeTermId(studyId);
		if (experimentDesignTypeTermId.isPresent()) {
			return experimentDesignTypeTermId.get();
		}

		return 0;
	}

	void displayDesignElementsBasedOnDesignTypeOfTheStudy() {
		final int designType = this.retrieveExperimentalDesignTypeID();
		if (designType != 0) {

			final ExperimentDesignType experimentDesignType = ExperimentDesignType.getDesignTypeItemByTermId(designType);
			this.selDesignType.setValue(experimentDesignType.getBvName());

			if (ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getId().equals(experimentDesignType.getId())) {
				this.displayRandomizedBlockDesignElements();
			} else if (ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getId().equals(experimentDesignType.getId())) {
				this.displayIncompleteBlockDesignElements();
			} else if (ExperimentDesignType.ROW_COL.getId().equals(experimentDesignType.getId())) {
				this.displayRowColumnDesignElements();
			} else if (ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getId().equals(experimentDesignType.getId())) {
				this.displayAugmentedDesignElements();
			}

		} else {
			this.selDesignType.select(null);
		}

	}

	private void addSpatialVariablesToLayout(final GridLayout gLayout, final int startRowIndex, final Boolean isMandatory) {
		gLayout.addComponent(this.lblSpecifyRowFactor, 0, startRowIndex);
		gLayout.addComponent(this.selRowFactor, 1, startRowIndex);
		gLayout.addComponent(this.lblSpecifyColumnFactor, 0, startRowIndex + 1);
		gLayout.addComponent(this.selColumnFactor, 1, startRowIndex + 1);
		this.markRowAndColumnFactorsAsMandatory(isMandatory);
	}

	public void displayRandomizedBlockDesignElements() {

		this.designDetailsContainer.removeAllComponents();

		// Add visible components for Randomized Block Design
		final GridLayout gLayout = this.createGridLayout(2, 3);
		gLayout.addComponent(this.lblReplicates, 0, 0);
		gLayout.addComponent(this.selReplicates, 1, 0);
		this.addSpatialVariablesToLayout(gLayout, 1, false);

		this.designDetailsContainer.addComponent(gLayout);

		this.substituteMissingReplicatesWithBlocks();
	}

	public void displayIncompleteBlockDesignElements() {

		this.designDetailsContainer.removeAllComponents();

		final GridLayout gLayout = this.createGridLayout(2, 4);

		// Add visible components for Incomplete Block Design
		gLayout.addComponent(this.lblReplicates, 0, 0);
		gLayout.addComponent(this.selReplicates, 1, 0);
		gLayout.addComponent(this.lblBlocks, 0, 1);
		gLayout.addComponent(this.selBlocks, 1, 1);
		this.addSpatialVariablesToLayout(gLayout, 2, false);

		this.designDetailsContainer.addComponent(gLayout);

		this.substituteMissingReplicatesWithBlocks();
	}

	public void displayAugmentedDesignElements() {

		this.designDetailsContainer.removeAllComponents();

		final GridLayout gLayout = this.createGridLayout(2, 4);

		// Add visible components for Augmented Design
		gLayout.addComponent(this.lblBlocks, 0, 1);
		gLayout.addComponent(this.selBlocks, 1, 1);
		this.addSpatialVariablesToLayout(gLayout, 2, false);

		// Augmented design does not need a replicates factor, make the
		// replicates factor select box unselected
		this.selReplicates.select(null);

		this.designDetailsContainer.addComponent(gLayout);

	}

	public void displayRowColumnDesignElements() {

		this.designDetailsContainer.removeAllComponents();

		final GridLayout gLayout = this.createGridLayout(2, 3);

		// Add visible components for Row-and-column Design
		gLayout.addComponent(this.lblReplicates, 0, 0);
		gLayout.addComponent(this.selReplicates, 1, 0);
		this.addSpatialVariablesToLayout(gLayout, 1, true);

		this.designDetailsContainer.addComponent(gLayout);

		this.substituteMissingReplicatesWithBlocks();
	}

	public void displayPRepDesignElements() {

		this.designDetailsContainer.removeAllComponents();

		final GridLayout gLayout = this.createGridLayout(2, 3);

		// Add visible components for P-rep Design
		gLayout.addComponent(this.lblBlocks, 0, 0);
		gLayout.addComponent(this.selBlocks, 1, 0);
		this.addSpatialVariablesToLayout(gLayout, 1, false);

		// P-rep design do not need a replicates factor, make the select box
		// unselected
		this.selReplicates.select(null);

		this.designDetailsContainer.addComponent(gLayout);

	}

	private void markRowAndColumnFactorsAsMandatory(final Boolean isMandatory) {
		if (isMandatory) {
			this.lblSpecifyRowFactor.setValue(
				this.messageSource.getMessage(Message.BV_SPECIFY_ROW_FACTOR) + SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR);
			this.lblSpecifyColumnFactor.setValue(this.messageSource.getMessage(Message.BV_SPECIFY_COLUMN_FACTOR)
				+ SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR);
		} else {
			this.lblSpecifyRowFactor.setValue(this.messageSource.getMessage(Message.BV_SPECIFY_ROW_FACTOR));
			this.lblSpecifyColumnFactor.setValue(this.messageSource.getMessage(Message.BV_SPECIFY_COLUMN_FACTOR));
		}

	}

	private GridLayout createGridLayout(final int col, final int row) {

		final GridLayout gLayout = new GridLayout(col, row);
		gLayout.setDebugId("gLayout");
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(SingleSiteAnalysisDetailsPanel.MARGIN_TOP10);

		return gLayout;

	}

	void substituteMissingReplicatesWithBlocks() {

		/**
		 * If a trial doesn't have a replicates factor, this will use the block factor as a substitute for replicates factor.
		 */

		if (!this.selReplicates.isEnabled() || this.selReplicates.getItemIds().isEmpty()) {

			for (final Object itemId : this.selBlocks.getItemIds()) {
				this.selReplicates.addItem(itemId);
				this.selReplicates.setItemCaption(itemId, SingleSiteAnalysisDetailsPanel.REPLICATES);
				this.selReplicates.select(itemId);
				this.selReplicates.setEnabled(true);
			}
		}

	}

	void populateChoicesForReplicates() {
		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			if (factor.getStandardVariable().getProperty().getName().trim()
				.equalsIgnoreCase(SingleSiteAnalysisDesignDetails.REPLICATION_FACTOR)) {
				this.selReplicates.addItem(factor.getLocalName());
				this.selReplicates.setValue(factor.getLocalName());
			}
		}

		if (this.selReplicates.getItemIds().isEmpty()) {
			this.selReplicates.setEnabled(false);
		} else {
			this.selReplicates.setEnabled(true);
		}
	}

	void populateChoicesForBlocks() {

		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			if (factor.getStandardVariable().getProperty().getName().trim()
				.equalsIgnoreCase(SingleSiteAnalysisDesignDetails.BLOCKING_FACTOR)) {
				this.selBlocks.addItem(factor.getLocalName());
				this.selBlocks.setValue(factor.getLocalName());
				this.selBlocks.setEnabled(true);
			}
		}

	}

	void populateChoicesForRowFactor() {
		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			if (factor.getStandardVariable().getProperty().getName().trim().equalsIgnoreCase(SingleSiteAnalysisDesignDetails.ROW_FACTOR)) {
				this.selRowFactor.addItem(factor.getLocalName());
			}
		}
	}

	void populateChoicesForColumnFactor() {
		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			if (factor.getStandardVariable().getProperty().getName().trim()
				.equalsIgnoreCase(SingleSiteAnalysisDesignDetails.COLUMN_FACTOR)) {
				this.selColumnFactor.addItem(factor.getLocalName());
			}
		}
	}

	private void setDefaultValueForRowAndColumnFactors() {
		// If there is exactly one row factor, remove the "Please choose" option and automatically select only factor
		if (this.selRowFactor.getItemIds().size() == 1) {
			this.selRowFactor.select(this.selRowFactor.getItemIds().iterator().next());
		}
		// If there is exactly one column factor, remove the "Please choose" option and automatically select only factor
		if (this.selColumnFactor.getItemIds().size() == 1) {
			this.selColumnFactor.select(this.selColumnFactor.getItemIds().iterator().next());
		}
	}

	private void refineChoicesForBlocksReplicationRowAndColumnFactors() {
		if (this.selReplicates.getValue() != null) {
			this.selBlocks.removeItem(this.selReplicates.getValue());
			this.selRowFactor.removeItem(this.selReplicates.getValue());
			this.selColumnFactor.removeItem(this.selReplicates.getValue());
		}

		if (this.selBlocks.getValue() != null) {
			this.selReplicates.removeItem(this.selBlocks.getValue());
			this.selRowFactor.removeItem(this.selBlocks.getValue());
			this.selColumnFactor.removeItem(this.selBlocks.getValue());
		}

		if (this.selRowFactor.getValue() != null) {
			this.selReplicates.removeItem(this.selRowFactor.getValue());
			this.selBlocks.removeItem(this.selRowFactor.getValue());
			this.selColumnFactor.removeItem(this.selRowFactor.getValue());
		}

		if (this.selColumnFactor.getValue() != null) {
			this.selReplicates.removeItem(this.selColumnFactor.getValue());
			this.selBlocks.removeItem(this.selColumnFactor.getValue());
			this.selRowFactor.removeItem(this.selColumnFactor.getValue());
		}
	}

	public String getSelDesignTypeValue() {
		return (String) this.selDesignType.getValue();
	}

	String getSelReplicatesValue() {
		return (String) this.selReplicates.getValue();
	}

	String getSelBlocksValue() {
		return (String) this.selBlocks.getValue();
	}

	String getSelRowFactorValue() {
		return (String) this.selRowFactor.getValue();
	}

	String getSelColumnFactorValue() {
		return (String) this.selColumnFactor.getValue();
	}

	Boolean replicateFactorEnabled() {
		return this.selReplicates.isEnabled();
	}

	VerticalLayout getDesignDetailsContainer() {
		return this.designDetailsContainer;
	}

	Label getLblReplicates() {
		return this.lblReplicates;
	}

	Label getLblBlocks() {
		return this.lblBlocks;
	}

	Label getLblSpecifyRowFactor() {
		return this.lblSpecifyRowFactor;
	}

	Label getLblSpecifyColumnFactor() {
		return this.lblSpecifyColumnFactor;
	}

	ComboBox getSelReplicates() {
		return this.selReplicates;
	}

	ComboBox getSelBlocks() {
		return this.selBlocks;
	}

	ComboBox getSelRowFactor() {
		return this.selRowFactor;
	}

	ComboBox getSelColumnFactor() {
		return this.selColumnFactor;
	}

	ComboBox getSelDesignType() {
		return this.selDesignType;
	}

	protected void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	void setExperimentDesignService(final ExperimentDesignService studyDataManager) {
		this.experimentDesignService = studyDataManager;
	}
}
