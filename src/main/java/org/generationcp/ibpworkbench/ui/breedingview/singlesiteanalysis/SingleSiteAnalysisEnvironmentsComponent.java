
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.BreedingViewEnvFactorValueChangeListener;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.SSAEnvironmentsCheckboxValueChangeListener;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.SSAEnvironmentsFooterCheckboxListener;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Configurable
public class SingleSiteAnalysisEnvironmentsComponent extends VerticalLayout
		implements InitializingBean, InternationalizableComponent, GermplasmStudyBrowserLayout {

	private static final long serialVersionUID = 1L;

	private static final String LABEL_BOLD_STYLING = "label-bold";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	private Label lblSiteEnvironment;
	private Label lblSpecifyEnvFactor;
	private Label lblSelectEnvironmentForAnalysis;

	private Label lblChooseEnvironmentHeader;
	private Label lblChooseEnvironmentDescription;
	private Label lblChooseEnvironmentForAnalysisDescription;

	private Map<String, Boolean> environmentsCheckboxState;
	private Table tblEnvironmentSelection;
	private Select selEnvFactor;
	private CheckBox footerCheckBox;
	private Property.ValueChangeListener envCheckBoxListener;
	private Property.ValueChangeListener footerCheckBoxListener;
	private VerticalLayout tblEnvironmentLayout;

	private final SingleSiteAnalysisDetailsPanel ssaDetailsPanel;

	public SingleSiteAnalysisEnvironmentsComponent(final SingleSiteAnalysisDetailsPanel ssaDetailsPanel) {
		super();
		this.ssaDetailsPanel = ssaDetailsPanel;
	}

	@Override
	public void instantiateComponents() {
		this.environmentsCheckboxState = new HashMap<>();

		this.footerCheckBox = new CheckBox("Select All", false);
		this.footerCheckBox.setDebugId("footerCheckBox");
		this.footerCheckBox.setImmediate(true);

		this.lblSiteEnvironment = new Label();
		this.lblSiteEnvironment.setDebugId("lblSiteEnvironment");
		this.lblSpecifyEnvFactor = new Label();
		this.lblSpecifyEnvFactor.setDebugId("lblSpecifyEnvFactor");
		this.lblSpecifyEnvFactor.setContentMode(Label.CONTENT_XHTML);
		this.lblSpecifyEnvFactor.setStyleName(SingleSiteAnalysisEnvironmentsComponent.LABEL_BOLD_STYLING);
		this.lblSelectEnvironmentForAnalysis = new Label();
		this.lblSelectEnvironmentForAnalysis.setDebugId("lblSelectEnvironmentForAnalysis");
		this.lblSelectEnvironmentForAnalysis.setContentMode(Label.CONTENT_XHTML);
		this.lblSelectEnvironmentForAnalysis.setStyleName(SingleSiteAnalysisEnvironmentsComponent.LABEL_BOLD_STYLING);

		this.lblChooseEnvironmentHeader = new Label("<span class='bms-environments' style='position:relative; top: -2px; color: #0076A9; "
				+ "font-size: 25px; font-weight: bold;'></span><b>&nbsp;" + "<span style='position:relative; top: -3px;'>"
				+ this.messageSource.getMessage(Message.BV_CHOOSE_ENVIRONMENT_HEADER) + "</span></b>", Label.CONTENT_XHTML);
		this.lblChooseEnvironmentHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.lblChooseEnvironmentDescription = new Label();
		this.lblChooseEnvironmentDescription.setDebugId("lblChooseEnvironmentDescription");
		this.lblChooseEnvironmentForAnalysisDescription = new Label();
		this.lblChooseEnvironmentForAnalysisDescription.setDebugId("lblChooseEnvironmentForAnalysisDescription");
		this.lblChooseEnvironmentForAnalysisDescription.setContentMode(Label.CONTENT_XHTML);
		this.lblChooseEnvironmentForAnalysisDescription.setStyleName(SingleSiteAnalysisEnvironmentsComponent.LABEL_BOLD_STYLING);

		this.selEnvFactor = new Select();
		this.selEnvFactor.setDebugId("selEnvFactor");
		this.selEnvFactor.setImmediate(true);
		this.selEnvFactor.setNullSelectionAllowed(false);
		this.selEnvFactor.setNewItemsAllowed(false);

		this.tblEnvironmentSelection = new Table();
		this.tblEnvironmentSelection.setDebugId("tblEnvironmentSelection");
		this.tblEnvironmentSelection.setHeight("200px");
		this.tblEnvironmentSelection.setWidth("100%");

		this.tblEnvironmentLayout = new VerticalLayout();
		this.tblEnvironmentLayout.setDebugId("tblEnvironmentLayout");
		this.tblEnvironmentLayout.setSizeUndefined();
		this.tblEnvironmentLayout.setSpacing(true);
		this.tblEnvironmentLayout.setWidth("100%");
	}

	@Override
	public void initializeValues() {
		this.populateChoicesForEnvironmentFactor();
		this.populateChoicesForEnvForAnalysis();

	}

	@Override
	public void addListeners() {
		this.selEnvFactor.addListener(new BreedingViewEnvFactorValueChangeListener(this));

		this.footerCheckBoxListener = new SSAEnvironmentsFooterCheckboxListener(this);
		this.footerCheckBox.addListener(this.footerCheckBoxListener);

		this.envCheckBoxListener = new SSAEnvironmentsCheckboxValueChangeListener(this);
		this.tblEnvironmentSelection.addGeneratedColumn(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, new ColumnGenerator() {

			private static final long serialVersionUID = 8164025367842219781L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final SeaEnvironmentModel item = (SeaEnvironmentModel) itemId;

				final CheckBox chk = new CheckBox();
				chk.setDebugId("chk");
				chk.setData(item);
				chk.setValue(item.getActive());
				chk.setImmediate(true);
				chk.addListener(SingleSiteAnalysisEnvironmentsComponent.this.envCheckBoxListener);
				return chk;
			}

		});

	}

	@Override
	public void layoutComponents() {
		this.tblEnvironmentLayout.addComponent(this.tblEnvironmentSelection);
		this.tblEnvironmentLayout.addComponent(this.footerCheckBox);

		final GridLayout chooseEnvironmentLayout = new GridLayout(2, 9);
		chooseEnvironmentLayout.setDebugId("chooseEnvironmentLayout");
		chooseEnvironmentLayout.setColumnExpandRatio(0, 4);
		chooseEnvironmentLayout.setColumnExpandRatio(1, 2);
		chooseEnvironmentLayout.setWidth("100%");
		chooseEnvironmentLayout.setSpacing(true);
		chooseEnvironmentLayout.setMargin(false, true, true, false);
		chooseEnvironmentLayout.addComponent(this.lblChooseEnvironmentHeader, 0, 0, 1, 0);
		chooseEnvironmentLayout.addComponent(this.lblChooseEnvironmentDescription, 0, 1, 1, 1);
		chooseEnvironmentLayout.addComponent(this.lblSpecifyEnvFactor, 0, 2);
		chooseEnvironmentLayout.addComponent(this.selEnvFactor, 1, 2);
		chooseEnvironmentLayout.addComponent(this.lblChooseEnvironmentForAnalysisDescription, 0, 3, 1, 3);
		chooseEnvironmentLayout.addComponent(this.tblEnvironmentLayout, 0, 4, 1, 4);

		this.addComponent(chooseEnvironmentLayout);
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setValue(this.lblSiteEnvironment, Message.BV_SITE_ENVIRONMENT);
		this.messageSource.setValue(this.lblSpecifyEnvFactor, Message.BV_SPECIFY_ENV_FACTOR);
		this.messageSource.setValue(this.lblSelectEnvironmentForAnalysis, Message.BV_SELECT_ENV_FOR_ANALYSIS);
		this.messageSource.setValue(this.lblChooseEnvironmentDescription, Message.BV_CHOOSE_ENVIRONMENT_DESCRIPTION);
		this.messageSource.setValue(this.lblChooseEnvironmentForAnalysisDescription, Message.BV_CHOOSE_ENVIRONMENT_FOR_ANALYSIS_DESC);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	public void reset() {
		this.selEnvFactor.select(this.selEnvFactor.getItemIds().iterator().next());
		this.footerCheckBox.setValue(false);
	}

	void populateChoicesForEnvironmentFactor() {

		if (this.ssaDetailsPanel.getTrialVariablesInDataset() == null) {
			return;
		}

		final String pleaseChoose = this.messageSource.getMessage(Message.PLEASE_CHOOSE);
		this.selEnvFactor.addItem(pleaseChoose);

		for (final DMSVariableType factor : this.ssaDetailsPanel.getTrialVariablesInDataset()) {
			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT) {
				this.selEnvFactor.addItem(factor.getLocalName());
				if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(factor.getLocalName())) {
					this.selEnvFactor.setValue(factor.getLocalName());
				}
			}
		}
		this.selEnvFactor.setValue(pleaseChoose);
		this.selEnvFactor.select(this.selEnvFactor.getItemIds().iterator().next());

		if (this.selEnvFactor.getItemIds().isEmpty()) {
			this.selEnvFactor.setEnabled(false);
		} else {
			this.selEnvFactor.setEnabled(true);
		}

	}

	public DMSVariableType getVariableByLocalName(final List<DMSVariableType> variables, final String name) {
		for (final DMSVariableType factor : variables) {
			if (factor.getLocalName().equals(name)) {
				return factor;
			}
		}
		return null;
	}

	public void populateChoicesForEnvForAnalysis() {

		final String selectedEnvironmentFactorName = (String) this.selEnvFactor.getValue();
		final DMSVariableType factor =
				this.getVariableByLocalName(this.ssaDetailsPanel.getTrialVariablesInDataset(), selectedEnvironmentFactorName);

		if (factor == null) {
			return;
		}

		this.footerCheckBox.setValue(false);
		this.environmentsCheckboxState.clear();
		this.tblEnvironmentSelection.removeAllItems();
		final String trialInstanceFactorName = this.studyDataManager.getLocalNameByStandardVariableId(
				this.ssaDetailsPanel.getBreedingViewInput().getDatasetId(), TermId.TRIAL_INSTANCE_FACTOR.getId());

		this.populateEnvironmentSelectionTableWithTrialEnvironments(this.tblEnvironmentSelection, trialInstanceFactorName,
				selectedEnvironmentFactorName);
		this.adjustEnvironmentSelectionTable(this.tblEnvironmentSelection, trialInstanceFactorName, selectedEnvironmentFactorName);

		this.ssaDetailsPanel.getBreedingViewInput().setTrialInstanceName(trialInstanceFactorName);
	}

	protected void adjustEnvironmentSelectionTable(final Table table, final String trialInstanceFactorName,
			final String selectedEnvironmentFactorName) {
		if (trialInstanceFactorName.equalsIgnoreCase(selectedEnvironmentFactorName)) {
			table.setVisibleColumns(
					new Object[] {SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN});
			table.setColumnHeaders(new String[] {"SELECT", trialInstanceFactorName});
			table.setColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, 45);
			table.setColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, -1);
			table.setWidth("45%");
		} else {
			table.setVisibleColumns(new Object[] {SingleSiteAnalysisDetailsPanel.SELECT_COLUMN,
					SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, SingleSiteAnalysisDetailsPanel.ENVIRONMENT_NAME});
			table.setColumnHeaders(new String[] {"SELECT", trialInstanceFactorName, selectedEnvironmentFactorName});
			table.setColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, 45);
			table.setColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, 60);
			table.setColumnWidth(SingleSiteAnalysisDetailsPanel.ENVIRONMENT_NAME, 500);
			table.setWidth("90%");
		}

	}

	protected void populateEnvironmentSelectionTableWithTrialEnvironments(final Table table, final String trialInstanceFactorName,
			final String selectedEnvironmentFactorName) {
		final BeanItemContainer<SeaEnvironmentModel> container = new BeanItemContainer<>(SeaEnvironmentModel.class);
		final int datasetId = this.ssaDetailsPanel.getBreedingViewInput().getDatasetId();
		final TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(datasetId);

		final boolean isSelectedEnvironmentFactorALocation = this.studyDataManager
				.isLocationIdVariable(this.ssaDetailsPanel.getBreedingViewInput().getStudyId(), selectedEnvironmentFactorName);
		final Map<String, String> locationNameMap =
				this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(this.ssaDetailsPanel.getBreedingViewInput().getStudyId());

		for (final TrialEnvironment trialEnvironment : trialEnvironments.getTrialEnvironments()) {

			final Variable trialVar = trialEnvironment.getVariables().findByLocalName(trialInstanceFactorName);
			final Variable selectedEnvVar = trialEnvironment.getVariables().findByLocalName(selectedEnvironmentFactorName);

			if (trialVar != null && selectedEnvVar != null) {

				final SeaEnvironmentModel bean = new SeaEnvironmentModel();
				bean.setActive(false);
				if (isSelectedEnvironmentFactorALocation) {
					// Get the location name of the location id
					bean.setEnvironmentName(locationNameMap.get(selectedEnvVar.getValue()));
				} else {
					bean.setEnvironmentName(selectedEnvVar.getValue());
				}

				bean.setTrialno(trialVar.getValue());
				bean.setLocationId(trialEnvironment.getId());
				container.addBean(bean);

			}

		}

		table.setContainerDataSource(container);

	}

	public List<SeaEnvironmentModel> getSelectedEnvironments() {

		final List<SeaEnvironmentModel> envs = new ArrayList<>();
		for (final Iterator<?> itr = this.tblEnvironmentSelection.getContainerDataSource().getItemIds().iterator(); itr.hasNext();) {
			final SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();
			if (m.getActive()) {
				envs.add(m);
			}
		}
		return envs;
	}

	public List<String> getInvalidEnvironments() {

		final List<String> invalidEnvs = new ArrayList<>();

		for (final Iterator<?> itr = this.tblEnvironmentSelection.getContainerDataSource().getItemIds().iterator(); itr.hasNext();) {
			final SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();

			if (m.getActive()) {
				final Boolean valid = this.ssaDetailsPanel.environmentContainsValidDataForAnalysis(m);

				if (!valid) {
					invalidEnvs.add(m.getEnvironmentName());
					m.setActive(false);
				}
			}
		}

		this.tblEnvironmentSelection.refreshRowCache();

		return invalidEnvs;

	}

	public boolean environmentContainsValidDataForAnalysis(final SeaEnvironmentModel model) {
		return this.ssaDetailsPanel.environmentContainsValidDataForAnalysis(model);
	}

	public String getSelEnvFactorValue() {
		return (String) this.selEnvFactor.getValue();
	}

	public Table getTblEnvironmentSelection() {
		return this.tblEnvironmentSelection;
	}

	public CheckBox getFooterCheckBox() {
		return this.footerCheckBox;
	}

	public void setFooterCheckBox(final CheckBox footerCheckBox) {
		this.footerCheckBox = footerCheckBox;
	}

	public Property.ValueChangeListener getFooterCheckBoxListener() {
		return this.footerCheckBoxListener;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	public void setSelEnvFactor(final Select selEnvFactor) {
		this.selEnvFactor = selEnvFactor;
	}

	public Map<String, Boolean> getEnvironmentsCheckboxState() {
		return this.environmentsCheckboxState;
	}

	public void setEnvironmentsCheckboxState(final Map<String, Boolean> environmentsCheckboxState) {
		this.environmentsCheckboxState = environmentsCheckboxState;
	}

	protected Select getSelEnvFactor() {
		return this.selEnvFactor;
	}
}
