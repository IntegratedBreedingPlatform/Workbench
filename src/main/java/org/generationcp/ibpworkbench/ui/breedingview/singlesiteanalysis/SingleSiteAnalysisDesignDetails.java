package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.BreedingViewDesignTypeValueChangeListener;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class SingleSiteAnalysisDesignDetails extends VerticalLayout implements InitializingBean, InternationalizableComponent, GermplasmStudyBrowserLayout {

	private static final long serialVersionUID = 1L;
	
	public static final String REPLICATION_FACTOR = "replication factor";
	public static final String BLOCKING_FACTOR = "blocking factor";
	public static final String ROW_FACTOR = "row in layout";
	public static final String COLUMN_FACTOR = "column in layout";
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private StudyDataManager studyDataManager;
	
	private Label lblDesign;
	private Label lblDesignType;
	private Label lblReplicates;
	private Label lblBlocks;
	private Label lblSpecifyRowFactor;
	private Label lblSpecifyColumnFactor;
	private Label lblSpecifyDesignDetailsHeader;
	
	private Select selDesignType;
	private Select selReplicates;
	private Select selBlocks;
	private Select selRowFactor;
	private Select selColumnFactor;
	
	private VerticalLayout designDetailsContainer;
	
	private SingleSiteAnalysisDetailsPanel ssaDetailsPanel;

	public SingleSiteAnalysisDesignDetails(final SingleSiteAnalysisDetailsPanel ssaDetailsPanel) {
		super();
		this.ssaDetailsPanel = ssaDetailsPanel;
	}
	
	@Override
	public void instantiateComponents() {
		this.lblDesign = new Label();
		this.lblDesign.setDebugId("lblDesign");
		
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
		
		this.lblSpecifyDesignDetailsHeader = new Label(
				"<span class='bms-exp-design' style='color: #9A8478; " + "font-size: 22px; font-weight: bold;'></span><b>&nbsp;"
						+ this.messageSource.getMessage(Message.BV_SPECIFY_DESIGN_DETAILS_HEADER) + "</b>", Label.CONTENT_XHTML);
		this.lblSpecifyDesignDetailsHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.selDesignType = new Select();
		this.selDesignType.setDebugId("selDesignType");
		this.selDesignType.setImmediate(true);
		this.selDesignType.setNullSelectionAllowed(true);
		this.selDesignType.setNewItemsAllowed(false);
		this.selDesignType.addItem(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
		this.selDesignType.setItemCaption(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName(), "Incomplete block design");
		this.selDesignType.addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		this.selDesignType.setItemCaption(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), "Randomized block design");
		this.selDesignType.addItem(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());
		this.selDesignType.setItemCaption(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName(), "Row-column design");
		this.selDesignType.addItem(DesignType.P_REP_DESIGN.getName());
		this.selDesignType.setItemCaption(DesignType.P_REP_DESIGN.getName(), "P-rep design");
		this.selDesignType.addItem(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName());
		this.selDesignType.setItemCaption(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName(), "Augmented design");
		this.selDesignType.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.selReplicates = new Select();
		this.selReplicates.setDebugId("selReplicates");
		this.selReplicates.setImmediate(true);
		this.selReplicates.setNullSelectionAllowed(true);
		this.selReplicates.setNewItemsAllowed(false);
		this.selReplicates.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.selBlocks = new Select();
		this.selBlocks.setDebugId("selBlocks");
		this.selBlocks.setImmediate(true);
		this.selBlocks.setEnabled(false);
		this.selBlocks.setNullSelectionAllowed(false);
		this.selBlocks.setNewItemsAllowed(false);
		this.selBlocks.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.selRowFactor = new Select();
		this.selRowFactor.setDebugId("selRowFactor");
		this.selRowFactor.setImmediate(true);
		this.selRowFactor.setNullSelectionAllowed(false);
		this.selRowFactor.setNewItemsAllowed(false);
		this.selRowFactor.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.selColumnFactor = new Select();
		this.selColumnFactor.setDebugId("selColumnFactor");
		this.selColumnFactor.setImmediate(true);
		this.selColumnFactor.setNullSelectionAllowed(false);
		this.selColumnFactor.setNewItemsAllowed(false);
		this.selColumnFactor.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		designDetailsContainer = new VerticalLayout();
		
	}

	@Override
	public void initializeValues() {
		this.populateChoicesForReplicates();
		this.populateChoicesForBlocks();
		this.populateChoicesForRowFactor();
		this.populateChoicesForColumnFactor();
		this.refineChoicesForBlocksReplicationRowAndColumnFactors();
		this.displayDesignElementsBasedOnDesignTypeOfTheStudy();
	}

	@Override
	public void addListeners() {
		this.selDesignType.addListener(new BreedingViewDesignTypeValueChangeListener(this));
	}

	@Override
	public void layoutComponents() {
		final GridLayout designDetailsLayout = new GridLayout(2, 3);
		designDetailsLayout.setDebugId("designDetailsLayout");
		designDetailsLayout.setColumnExpandRatio(0, 0);
		designDetailsLayout.setColumnExpandRatio(1, 1);
		designDetailsLayout.setWidth("100%");
		designDetailsLayout.setSpacing(true);
		designDetailsLayout.setMargin(false, false, false, false);
		designDetailsLayout.addComponent(this.lblSpecifyDesignDetailsHeader, 0, 0, 1, 0);
		designDetailsLayout.addComponent(this.lblDesignType, 0, 1);
		designDetailsLayout.addComponent(this.selDesignType, 1, 1);

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
		this.messageSource.setValue(this.lblDesign, Message.BV_DESIGN);
		this.messageSource.setValue(this.lblDesignType, Message.DESIGN_TYPE);
		this.messageSource.setValue(this.lblReplicates, Message.BV_SPECIFY_REPLICATES);
		this.messageSource.setValue(this.lblBlocks, Message.BV_SPECIFY_BLOCKS);
		this.messageSource.setValue(this.lblSpecifyRowFactor, Message.BV_SPECIFY_ROW_FACTOR);
		this.messageSource.setValue(this.lblSpecifyColumnFactor, Message.BV_SPECIFY_COLUMN_FACTOR);
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
	}
	
	protected int retrieveExperimentalDesignTypeID() {
		final String expDesign = this.studyDataManager
				.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.ssaDetailsPanel.getBreedingViewInput().getStudyId());
		if (expDesign != null && !"".equals(expDesign.trim()) && NumberUtils.isNumber(expDesign)) {
			return Integer.parseInt(expDesign);
		}

		return 0;
	}
	
	protected void displayDesignElementsBasedOnDesignTypeOfTheStudy() {
		String designFactor = null;
		final int designType = this.retrieveExperimentalDesignTypeID();
		if (designType != 0) {

			if (designType == TermId.RANDOMIZED_COMPLETE_BLOCK.getId()) {
				designFactor = DesignType.RANDOMIZED_BLOCK_DESIGN.getName();
				this.displayRandomizedBlockDesignElements();
			} else if (designType == TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId() || designType == TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN
					.getId()) {
				designFactor = DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName();
				this.displayIncompleteBlockDesignElements();
			} else if (designType == TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()
					|| designType == TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()) {
				designFactor = DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName();
				this.displayRowColumnDesignElements();
			} else if (designType == TermId.AUGMENTED_RANDOMIZED_BLOCK.getId()) {
				designFactor = DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName();
				this.displayAugmentedDesignElements();
			}

			this.selDesignType.setValue(designFactor);
		} else {
			this.selDesignType.select(null);
		}

	}
	
	public void displayRandomizedBlockDesignElements() {

		this.designDetailsContainer.removeAllComponents();

		// Add visible components for Randomized Block Design
		final GridLayout gLayout = this.createGridLayout(2, 1);
		gLayout.addComponent(this.lblReplicates, 0, 0);
		gLayout.addComponent(this.selReplicates, 1, 0);

		this.designDetailsContainer.addComponent(gLayout);

		this.substituteMissingReplicatesWithBlocks();
	}

	public void displayIncompleteBlockDesignElements() {

		this.designDetailsContainer.removeAllComponents();

		final GridLayout gLayout = this.createGridLayout(2, 2);

		// Add visible components for Incomplete Block Design
		gLayout.addComponent(this.lblReplicates, 0, 0);
		gLayout.addComponent(this.selReplicates, 1, 0);
		gLayout.addComponent(this.lblBlocks, 0, 1);
		gLayout.addComponent(this.selBlocks, 1, 1);

		this.designDetailsContainer.addComponent(gLayout);

		this.substituteMissingReplicatesWithBlocks();
	}

	public void displayAugmentedDesignElements() {

		this.designDetailsContainer.removeAllComponents();

		final GridLayout gLayout = this.createGridLayout(2, 2);

		// Add visible components for Augmented Design
		gLayout.addComponent(this.lblBlocks, 0, 1);
		gLayout.addComponent(this.selBlocks, 1, 1);

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
		gLayout.addComponent(this.lblSpecifyColumnFactor, 0, 1);
		gLayout.addComponent(this.selColumnFactor, 1, 1);
		gLayout.addComponent(this.lblSpecifyRowFactor, 0, 2);
		gLayout.addComponent(this.selRowFactor, 1, 2);

		this.changeRowAndColumnLabelsBasedOnDesignType(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN);

		this.designDetailsContainer.addComponent(gLayout);

		this.substituteMissingReplicatesWithBlocks();
	}

	public void displayPRepDesignElements() {

		this.designDetailsContainer.removeAllComponents();

		final GridLayout gLayout = this.createGridLayout(2, 3);

		// Add visible components for P-rep Design
		gLayout.addComponent(this.lblBlocks, 0, 0);
		gLayout.addComponent(this.selBlocks, 1, 0);
		gLayout.addComponent(this.lblSpecifyColumnFactor, 0, 1);
		gLayout.addComponent(this.selColumnFactor, 1, 1);
		gLayout.addComponent(this.lblSpecifyRowFactor, 0, 2);
		gLayout.addComponent(this.selRowFactor, 1, 2);

		this.changeRowAndColumnLabelsBasedOnDesignType(DesignType.P_REP_DESIGN);

		// P-rep design do not need a replicates factor, make the select box
		// unselected
		this.selReplicates.select(null);

		this.designDetailsContainer.addComponent(gLayout);

	}

	void changeRowAndColumnLabelsBasedOnDesignType(final DesignType designType) {

		if (designType == DesignType.P_REP_DESIGN) {
			// When the design type is P-rep, the row and column factors are
			// optional. So we
			// need to change the text label to NOT have red asterisk (*)
			// appended to the text label.
			this.lblSpecifyRowFactor.setValue(this.messageSource.getMessage(Message.BV_SPECIFY_ROW_FACTOR));
			this.lblSpecifyColumnFactor.setValue(this.messageSource.getMessage(Message.BV_SPECIFY_COLUMN_FACTOR));

		} else if (designType == DesignType.RESOLVABLE_ROW_COLUMN_DESIGN) {

			// For Row and Column Design, row and column factors are all
			// required so their text labels should have a
			// red asterisk (*)
			this.lblSpecifyRowFactor.setValue(
					this.messageSource.getMessage(Message.BV_SPECIFY_ROW_FACTOR) + SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR);
			this.lblSpecifyColumnFactor.setValue(this.messageSource.getMessage(Message.BV_SPECIFY_COLUMN_FACTOR)
					+ SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR);

		} else {
			// Default labels
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
		 * If a trial doesn't have a replicates factor, this will use the block
		 * factor as a substitute for replicates factor.
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

	
	protected void populateChoicesForReplicates() {
		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			if (factor.getStandardVariable().getProperty().getName().trim()
					.equalsIgnoreCase(REPLICATION_FACTOR)) {
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

	protected void populateChoicesForBlocks() {

		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			if (factor.getStandardVariable().getProperty().getName().trim()
					.equalsIgnoreCase(BLOCKING_FACTOR)) {
				this.selBlocks.addItem(factor.getLocalName());
				this.selBlocks.setValue(factor.getLocalName());
				this.selBlocks.setEnabled(true);
			}
		}

	}

	protected void populateChoicesForRowFactor() {

		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			if (factor.getStandardVariable().getProperty().getName().trim()
					.equalsIgnoreCase(ROW_FACTOR)) {
				this.selRowFactor.addItem(factor.getLocalName());
				this.selRowFactor.setValue(factor.getLocalName());
			}
		}

	}

	protected void populateChoicesForColumnFactor() {

		for (final DMSVariableType factor : this.ssaDetailsPanel.getFactorsInDataset()) {
			if (factor.getStandardVariable().getProperty().getName().trim()
					.equalsIgnoreCase(COLUMN_FACTOR)) {
				this.selColumnFactor.addItem(factor.getLocalName());
				this.selColumnFactor.setValue(factor.getLocalName());
			}
		}

	}

	public void refineChoicesForBlocksReplicationRowAndColumnFactors() {
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
	
	public String getSelReplicatesValue() {
		return (String) this.selReplicates.getValue();
	}
	
	public String getSelBlocksValue() {
		return (String) this.selBlocks.getValue();
	}
	
	public String getSelRowFactorValue() {
		return (String) this.selRowFactor.getValue();
	}
	
	public String getSelColumnFactorValue() {
		return (String) this.selColumnFactor.getValue();
	}
	
	public Boolean replicateFactorEnabled() {
		return this.selReplicates.isEnabled();
	}
}
