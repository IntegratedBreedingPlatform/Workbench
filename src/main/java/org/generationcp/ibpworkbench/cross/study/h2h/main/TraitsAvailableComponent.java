
package org.generationcp.ibpworkbench.cross.study.h2h.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.cross.study.commons.EnvironmentFilter;
import org.generationcp.ibpworkbench.cross.study.h2h.main.listeners.HeadToHeadCrossStudyMainButtonClickListener;
import org.generationcp.ibpworkbench.cross.study.h2h.main.listeners.HeadToHeadCrossStudyMainValueChangeListener;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.TraitForComparison;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.h2h.TraitType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnResizeEvent;

@Configurable
public class TraitsAvailableComponent extends AbsoluteLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 991899235025710803L;

	public static final String BACK_BUTTON_ID = "TraitsAvailableComponent Back Button ID";
	public static final String NEXT_BUTTON_ID = "TraitsAvailableComponent Next Button ID";
	public static final String CHECKBOX_ID = "TraitsAvailableComponent Checkbox ID";

	private static final String TRAIT_COLUMN_ID = "TraitsAvailableComponent Trait Column Id";
	private static final String TRAIT_PROPERTY_COLUMN_ID = "TraitsAvailableComponent Trait Property Column Id";
	private static final String TRAIT_DESCRIPTION_COLUMN_ID = "TraitsAvailableComponent Trait Description Column Id";
	private static final String NUMBER_OF_ENV_COLUMN_ID = "TraitsAvailableComponent Number of Environments Column Id";
	private static final String TAG_COLUMN_ID = "TraitsAvailableComponent Tag Column Id";
	private static final String DIRECTION_COLUMN_ID = "TraitsAvailableComponent Direction Column Id";
	private static final String TAG_ALL = "TraitsAvailableComponent TAG_ALL Column Id";

	private Table traitsTable;

	private Button nextButton;

	private final HeadToHeadCrossStudyMain mainScreen;
	private final EnvironmentFilter nextScreen;

	private HeaderLabelLayout selectTraitReminderLayout;

	public static final Integer INCREASING = 1;
	public static final Integer DECREASING = 0;

	@Autowired
	private CrossStudyDataManager crossStudyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private ContextUtil contextUtil;

	// will contain all the tagged row
	private List<ComboBox> traitForComparisons;
	// will contain the mapping from comboBox to the specific row
	private Map<ComboBox, TraitInfo> traitMaps;
	// will contain the map of trait and trial environment
	private Map<String, Map<String, TrialEnvironment>> traitEnvironmentMap;
	// will contain the map of trial environment
	private Map<String, TrialEnvironment> trialEnvironmentMap;
	private Map<String, String> germplasmIdNameMap;
	private Map<String, String> germplasmIdMGIDMap;

	private Set<Integer> germplasmIds;
	private List<GermplasmPair> finalGermplasmPair;
	private List<GermplasmPair> prevfinalGermplasmPair;
	private List<GermplasmPair> environmentPairList;

	private CheckBox tagUnTagAll;

	private AbsoluteLayout tableLayout;

	private OptionGroup variableFilterOptionGroup;

	public TraitsAvailableComponent(final HeadToHeadCrossStudyMain mainScreen, final EnvironmentFilter nextScreen) {
		this.mainScreen = mainScreen;
		this.nextScreen = nextScreen;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setHeight("500px");
		this.setWidth("1000px");

		final Label selectTraitLabel = new Label(this.messageSource.getMessage(Message.HEAD_TO_HEAD_SELECT_TRAITS));
		selectTraitLabel.setDebugId("selectTraitLabel");
		selectTraitLabel.setImmediate(true);
		selectTraitLabel.setWidth("400px");

		this.initializeVariableFilterOptionGroup();

		final HorizontalLayout horizontalH2HFilters = new HorizontalLayout();
		horizontalH2HFilters.setDebugId("horizontalH2HFilters");
		horizontalH2HFilters.setSpacing(true);
		horizontalH2HFilters.setWidth("800px");
		horizontalH2HFilters.addComponent(selectTraitLabel);
		horizontalH2HFilters.addComponent(this.variableFilterOptionGroup);

		this.addComponent(horizontalH2HFilters, "top:10px;left:35px");
		final Label selectTraitReminderLabel = new Label(this.messageSource.getMessage(Message.HEAD_TO_HEAD_SELECT_TRAITS_REMINDER));
		selectTraitReminderLabel.setDebugId("selectTraitReminderLabel");
		selectTraitReminderLabel.setImmediate(true);
		selectTraitReminderLabel.setStyleName("gcp-bold-italic");
		this.selectTraitReminderLayout = new HeaderLabelLayout(new ThemeResource("images/warning.png"), selectTraitReminderLabel);
		this.addComponent(this.selectTraitReminderLayout, "top:35px;left:35px");

		final Panel tablePanel = new Panel();
		tablePanel.setDebugId("tablePanel");
		tablePanel.setWidth("950px");
		tablePanel.setHeight("400px");

		this.tableLayout = new AbsoluteLayout();
		this.tableLayout.setDebugId("tableLayout");
		this.tableLayout.setWidth("950px");

		this.traitsTable = new Table();
		this.traitsTable.setDebugId("traitsTable");
		this.traitsTable.setWidth("948px");
		this.traitsTable.setHeight("380px");
		this.traitsTable.setImmediate(true);

		this.traitsTable.addContainerProperty(TraitsAvailableComponent.TAG_COLUMN_ID, CheckBox.class, null);
		this.traitsTable.addContainerProperty(TraitsAvailableComponent.TRAIT_COLUMN_ID, String.class, null);
		this.traitsTable.addContainerProperty(TraitsAvailableComponent.TRAIT_PROPERTY_COLUMN_ID, String.class, null);
		this.traitsTable.addContainerProperty(TraitsAvailableComponent.TRAIT_DESCRIPTION_COLUMN_ID, String.class, null);
		this.traitsTable.addContainerProperty(TraitsAvailableComponent.NUMBER_OF_ENV_COLUMN_ID, Integer.class, null);
		this.traitsTable.addContainerProperty(TraitsAvailableComponent.DIRECTION_COLUMN_ID, ComboBox.class, null);

		this.traitsTable.setColumnHeader(TraitsAvailableComponent.TAG_COLUMN_ID, this.messageSource.getMessage(Message.HEAD_TO_HEAD_TAG));
		this.traitsTable.setColumnHeader(TraitsAvailableComponent.TRAIT_COLUMN_ID,
				this.messageSource.getMessage(Message.HEAD_TO_HEAD_VARIABLE_NAME));
		this.traitsTable.setColumnHeader(TraitsAvailableComponent.TRAIT_PROPERTY_COLUMN_ID,
				this.messageSource.getMessage(Message.HEAD_TO_HEAD_PROPERTY));
		this.traitsTable.setColumnHeader(TraitsAvailableComponent.TRAIT_DESCRIPTION_COLUMN_ID,
				this.messageSource.getMessage(Message.HEAD_TO_HEAD_DESCRIPTION));
		this.traitsTable.setColumnHeader(TraitsAvailableComponent.NUMBER_OF_ENV_COLUMN_ID,
				this.messageSource.getMessage(Message.HEAD_TO_HEAD_NO_OF_ENVS));
		this.traitsTable.setColumnHeader(TraitsAvailableComponent.DIRECTION_COLUMN_ID,
				this.messageSource.getMessage(Message.HEAD_TO_HEAD_DIRECTION));

		this.traitsTable.setColumnWidth(TraitsAvailableComponent.TAG_COLUMN_ID, 40);
		this.traitsTable.setColumnWidth(TraitsAvailableComponent.TRAIT_COLUMN_ID, 150);
		this.traitsTable.setColumnWidth(TraitsAvailableComponent.TRAIT_PROPERTY_COLUMN_ID, 80);
		this.traitsTable.setColumnWidth(TraitsAvailableComponent.TRAIT_DESCRIPTION_COLUMN_ID, 295);
		this.traitsTable.setColumnWidth(TraitsAvailableComponent.NUMBER_OF_ENV_COLUMN_ID, 155);
		this.traitsTable.setColumnWidth(TraitsAvailableComponent.DIRECTION_COLUMN_ID, 130);

		this.tableLayout.addComponent(this.traitsTable, "top:0px;left:0px");

		this.traitsTable.addListener(new Table.ColumnResizeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void columnResize(final ColumnResizeEvent event) {
				final int diff = event.getCurrentWidth() - event.getPreviousWidth();
				final float newWidth = diff + TraitsAvailableComponent.this.traitsTable.getWidth();

				final String widthPx = String.valueOf(newWidth) + "px";
				TraitsAvailableComponent.this.traitsTable.setWidth(widthPx);
				TraitsAvailableComponent.this.tableLayout.setWidth(widthPx);
			}
		});

		this.tagUnTagAll = new CheckBox();
		this.tagUnTagAll.setDebugId("tagUnTagAll");
		this.tagUnTagAll.setValue(false);
		this.tagUnTagAll.setImmediate(true);
		this.tagUnTagAll.setData(TraitsAvailableComponent.TAG_ALL);
		this.tagUnTagAll.addListener(new HeadToHeadCrossStudyMainValueChangeListener(this, true));

		this.tableLayout.addComponent(this.tagUnTagAll, "top:4px;left:30px");

		tablePanel.setContent(this.tableLayout);
		this.addComponent(tablePanel, "top:60px;left:30px");

		this.nextButton = new Button("Next");
		this.nextButton.setDebugId("nextButton");
		this.nextButton.setData(TraitsAvailableComponent.NEXT_BUTTON_ID);
		this.nextButton.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));
		this.nextButton.setEnabled(false);
		this.nextButton.setWidth("80px");
		this.nextButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.addComponent(this.nextButton, "top:470px;left:500px");

		final Button backButton = new Button("Back");
		backButton.setDebugId("backButton");
		backButton.setData(TraitsAvailableComponent.BACK_BUTTON_ID);
		backButton.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));
		backButton.setWidth("80px");
		this.addComponent(backButton, "top:470px;left:410px");

	}

	void initializeVariableFilterOptionGroup() {
		this.variableFilterOptionGroup = new OptionGroup(null);
		this.variableFilterOptionGroup.setImmediate(true);
		this.variableFilterOptionGroup.addItem(this.messageSource.getMessage(Message.HEAD_TO_HEAD_CHECK_ALL));
		this.variableFilterOptionGroup.select(this.messageSource.getMessage(Message.HEAD_TO_HEAD_CHECK_ALL));
		this.variableFilterOptionGroup.addItem(this.messageSource.getMessage(Message.HEAD_TO_HEAD_CHECK_TRAITS));
		this.variableFilterOptionGroup.addItem(this.messageSource.getMessage(Message.HEAD_TO_HEAD_CHECK_ANALYSIS));
		this.variableFilterOptionGroup.addStyleName("horizontal");
		this.variableFilterOptionGroup.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(final ValueChangeEvent event) {
				TraitsAvailableComponent.this.updatePopulateTraitsAndAnalysisAvailableTable();
			}
		});
	}

	private ComboBox getDirectionComboBox() {
		final ComboBox combo = new ComboBox();
		combo.setDebugId("combo");
		combo.setNullSelectionAllowed(false);
		combo.setTextInputAllowed(false);
		combo.setImmediate(true);

		combo.addItem(TraitsAvailableComponent.INCREASING);
		combo.setItemCaption(TraitsAvailableComponent.INCREASING, this.messageSource.getMessage(Message.HEAD_TO_HEAD_INCREASING));

		combo.addItem(TraitsAvailableComponent.DECREASING);
		combo.setItemCaption(TraitsAvailableComponent.DECREASING, this.messageSource.getMessage(Message.HEAD_TO_HEAD_DECREASING));

		combo.setValue(TraitsAvailableComponent.INCREASING);

		combo.setEnabled(false);
		return combo;

	}

	private void updatePopulateTraitsAndAnalysisAvailableTable() {
		this.prevfinalGermplasmPair = null;
		this.tagUnTagAll.setValue(false);
		this.populateTraitsAvailableTable(this.finalGermplasmPair, this.germplasmIdNameMap, this.germplasmIdMGIDMap);
	}

	public void populateTraitsAvailableTable(final List<GermplasmPair> germplasmPairList, final Map<String, String> germplasmIdNameMap,
			final Map<String, String> germplasmIdMGIDMap) {

		this.initializeVariables();
		final Map<String, List<TraitInfo>> traitMap = new HashMap<>();
		final Map<String, Set<String>> traitEnvMap = new HashMap<>();

		this.germplasmIdNameMap = germplasmIdNameMap;
		this.germplasmIdMGIDMap = germplasmIdMGIDMap;
		this.finalGermplasmPair = germplasmPairList;
		final boolean doRefresh = this.validateDoRefresh();

		if (doRefresh) {
			this.prevfinalGermplasmPair = germplasmPairList;
			this.refreshEnviromentPairList(germplasmPairList);
		}
		this.createEnviromentMap(traitMap, traitEnvMap);
		this.initializeTable(traitMap, traitEnvMap);

	}

	void refreshEnviromentPairList(final List<GermplasmPair> germplasmPairList) {
		// By default both Traits and Analysis variables will be shown
		List<Integer> experimentTypes = Arrays.asList(TermId.PLOT_EXPERIMENT.getId(), TermId.AVERAGE_EXPERIMENT.getId());
		final String variableFilterSelected = this.variableFilterOptionGroup.getValue().toString();

		// Remove the experiment type we will not include based on selected variable filter
		if (this.messageSource.getMessage(Message.HEAD_TO_HEAD_CHECK_TRAITS).equals(variableFilterSelected)) {
			experimentTypes = Arrays.asList(TermId.PLOT_EXPERIMENT.getId());
		} else if (this.messageSource.getMessage(Message.HEAD_TO_HEAD_CHECK_ANALYSIS).equals(variableFilterSelected)) {
			experimentTypes = Arrays.asList(TermId.AVERAGE_EXPERIMENT.getId());
		}

		// only call when need to refresh
		this.prevfinalGermplasmPair = germplasmPairList;
		this.environmentPairList = this.crossStudyDataManager.getEnvironmentsForGermplasmPairs(germplasmPairList, experimentTypes, contextUtil.getCurrentProgramUUID());
	}

	private void createEnviromentMap(final Map<String, List<TraitInfo>> traitMap, final Map<String, Set<String>> traitEnvMap) {
		for (final GermplasmPair pair : this.environmentPairList) {
			final TrialEnvironments env = pair.getTrialEnvironments();

			this.germplasmIds.add(Integer.valueOf(pair.getGid1()));
			this.germplasmIds.add(Integer.valueOf(pair.getGid2()));

			for (final TrialEnvironment trialEnv : env.getTrialEnvironments()) {
				this.trialEnvironmentMap.put(Integer.toString(trialEnv.getId()), trialEnv);
				for (final TraitInfo info : trialEnv.getTraits()) {

					// add here the checking if the trait is non numeric
					if (info.getType() != TraitType.NUMERIC) {
						continue;
					}

					final String id = Integer.toString(info.getId());
					List<TraitInfo> tempList = new ArrayList<>();
					if (traitMap.containsKey(id)) {
						tempList = traitMap.get(id);
					}
					tempList.add(info);
					traitMap.put(id, tempList);
					Set<String> envIds = traitEnvMap.get(id);
					if (envIds == null) {
						envIds = new HashSet<>();
					}
					envIds.add(Integer.toString(trialEnv.getId()));
					traitEnvMap.put(id, envIds);

					// we need to keep track on the environments
					Map<String, TrialEnvironment> tempEnvMap = new HashMap<>();
					if (this.traitEnvironmentMap.containsKey(id)) {
						tempEnvMap = this.traitEnvironmentMap.get(id);
					}
					tempEnvMap.put(Integer.toString(trialEnv.getId()), trialEnv);
					this.traitEnvironmentMap.put(id, tempEnvMap);

				}
			}
		}
	}

	private boolean validateDoRefresh() {
		boolean doRefresh = true;
		// we checked if its the same
		if (this.prevfinalGermplasmPair != null && this.prevfinalGermplasmPair.size() == this.finalGermplasmPair.size()
				&& this.prevfinalGermplasmPair.containsAll(this.finalGermplasmPair)
				&& this.finalGermplasmPair.containsAll(this.prevfinalGermplasmPair)) {
			doRefresh = false;

		}
		return doRefresh;
	}

	private void initializeTable(final Map<String, List<TraitInfo>> traitMap, final Map<String, Set<String>> traitEnvMap) {

		for (final Map.Entry<String, List<TraitInfo>> entry : traitMap.entrySet()) {
			final String id = entry.getKey();
			final List<TraitInfo> traitInfoList = entry.getValue();
			// we get the 1st one since its all the same for this specific list
			final TraitInfo info = traitInfoList.get(0);
			final CheckBox box = new CheckBox();
			box.setDebugId("box");
			final ComboBox comboBox = this.getDirectionComboBox();
			box.setImmediate(true);
			final Integer tableId = Integer.valueOf(id);

			final Integer numOfEnv = traitEnvMap.get(id).size();
			this.traitsTable.addItem(new Object[] {box, info.getName(), info.getProperty(), info.getDescription(), numOfEnv, comboBox},
					tableId);
			box.addListener(new HeadToHeadCrossStudyMainValueChangeListener(this, comboBox));
			this.traitMaps.put(comboBox, info);

		}

		if (this.traitsTable.getItemIds().isEmpty()) {
			MessageNotifier.showWarning(this.getWindow(), "Warning!",
					"No environments and traits were found for the pairs of germplasm entries you have specified.");
		}
	}

	private void initializeVariables() {
		this.tagUnTagAll.setValue(false);
		this.traitsTable.removeAllItems();
		this.nextButton.setEnabled(false);
		this.visibleReminderFilterSelect(true);
		this.traitForComparisons = new ArrayList<>();
		this.traitMaps = new HashMap<>();
		this.traitEnvironmentMap = new HashMap<>();
		this.trialEnvironmentMap = new HashMap<>();
		this.germplasmIds = new HashSet<>();
	}

	private void visibleReminderFilterSelect(final boolean visibleReminderFilter) {

		if (visibleReminderFilter) {
			this.selectTraitReminderLayout.setVisible(true);
			this.selectTraitReminderLayout.setICON(new ThemeResource("images/warning.png"));
		} else {
			this.selectTraitReminderLayout.setVisible(false);
			this.selectTraitReminderLayout.setICON(null);
		}
	}

	public void clickCheckBox(final Component combo, final boolean boolVal) {
		if (combo != null) {
			final ComboBox comboBox = (ComboBox) combo;
			comboBox.setEnabled(boolVal);
			final TraitInfo info = this.traitMaps.get(comboBox);

			if (info != null) {
				if (boolVal) {
					this.traitForComparisons.add(comboBox);
				} else {
					this.traitForComparisons.remove(comboBox);
				}
			}

			if (this.traitForComparisons.isEmpty()) {
				this.nextButton.setEnabled(false);
				this.visibleReminderFilterSelect(true);
			} else {
				this.nextButton.setEnabled(true);
				this.visibleReminderFilterSelect(false);
			}
		}
	}

	public void clickTagAllCheckbox(final boolean boxChecked) {
		final Object[] tableItemIds = this.traitsTable.getItemIds().toArray();
		for (int i = 0; i < tableItemIds.length; i++) {
			final Item row = this.traitsTable.getItem(tableItemIds[i]);
			final CheckBox box = (CheckBox) row.getItemProperty(TraitsAvailableComponent.TAG_COLUMN_ID).getValue();
			box.setValue(boxChecked);
		}
	}

	public void nextButtonClickAction() {
		final List<TraitForComparison> traitForComparisonsList = new ArrayList<>();
		for (final ComboBox combo : this.traitForComparisons) {
			final TraitInfo info = this.traitMaps.get(combo);
			final TraitForComparison traitForComparison = new TraitForComparison(info, (Integer) combo.getValue());
			traitForComparisonsList.add(traitForComparison);
		}
		if (this.nextScreen != null) {
			this.nextScreen.populateEnvironmentsTable(traitForComparisonsList, this.traitEnvironmentMap, this.trialEnvironmentMap,
					this.germplasmIds, this.finalGermplasmPair, this.germplasmIdNameMap, this.germplasmIdMGIDMap);
		}
		this.mainScreen.selectThirdTab();
	}

	public void backButtonClickAction() {
		this.mainScreen.selectFirstTab();
	}

	@Override
	public void updateLabels() {
		// do nothing
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setCrossStudyDataManager(final CrossStudyDataManager crossStudyDataManager) {
		this.crossStudyDataManager = crossStudyDataManager;
	}

	public OptionGroup getVariableFilterOptionGroup() {
		return this.variableFilterOptionGroup;
	}

}
