
package org.generationcp.ibpworkbench.cross.study.h2h.main;

import com.vaadin.data.Item;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnResizeEvent;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.cross.study.commons.EnvironmentFilter;
import org.generationcp.ibpworkbench.cross.study.h2h.main.listeners.HeadToHeadCrossStudyMainButtonClickListener;
import org.generationcp.ibpworkbench.cross.study.h2h.main.listeners.HeadToHeadCrossStudyMainValueChangeListener;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.TraitForComparison;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.h2h.TraitType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configurable
public class TraitsAvailableComponent extends AbsoluteLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 991899235025710803L;

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(org.generationcp.ibpworkbench.cross.study.h2h.main.TraitsAvailableComponent.class);

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

	private CheckBox traitFilterCheckBox;
	private CheckBox analysisFilterCheckBox;

	public TraitsAvailableComponent(HeadToHeadCrossStudyMain mainScreen, EnvironmentFilter nextScreen) {
		this.mainScreen = mainScreen;
		this.nextScreen = nextScreen;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setHeight("500px");
		this.setWidth("1000px");

		Label selectTraitLabel = new Label(this.messageSource.getMessage(Message.HEAD_TO_HEAD_SELECT_TRAITS));
		selectTraitLabel.setDebugId("selectTraitLabel");
		selectTraitLabel.setImmediate(true);
		selectTraitLabel.setWidth("400px");

		this.traitFilterCheckBox = new CheckBox(this.messageSource.getMessage(Message.HEAD_TO_HEAD_CHECK_TRAITS),true);
		this.traitFilterCheckBox.setImmediate(true);
		this.traitFilterCheckBox.setDebugId("traitFilterCheckBox");
		this.traitFilterCheckBox.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final com.vaadin.ui.Button.ClickEvent event) {
				updatePopulateTraitsAndAnalysisAvailableTable((Boolean) traitFilterCheckBox.getValue(),(Boolean) analysisFilterCheckBox.getValue());
			}

		});

		this.analysisFilterCheckBox = new CheckBox(this.messageSource.getMessage(Message.HEAD_TO_HEAD_CHECK_ANALYSIS),true);
		this.analysisFilterCheckBox.setDebugId("analysisFilterCheckBox");
		this.analysisFilterCheckBox.setImmediate(true);
		this.analysisFilterCheckBox.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final com.vaadin.ui.Button.ClickEvent event) {
				updatePopulateTraitsAndAnalysisAvailableTable((Boolean) traitFilterCheckBox.getValue(),(Boolean) analysisFilterCheckBox.getValue());
			}

		});

		HorizontalLayout horizontalH2HFilters = new HorizontalLayout();
		horizontalH2HFilters.setDebugId("horizontalH2HFilters");
		horizontalH2HFilters.setSpacing(true);
		horizontalH2HFilters.setWidth("800px");
		horizontalH2HFilters.addComponent(selectTraitLabel);
		horizontalH2HFilters.addComponent(this.traitFilterCheckBox);
		horizontalH2HFilters.addComponent(this.analysisFilterCheckBox);

		this.addComponent(horizontalH2HFilters,"top:10px;left:35px");
		Label selectTraitReminderLabel = new Label(this.messageSource.getMessage(Message.HEAD_TO_HEAD_SELECT_TRAITS_REMINDER));
		selectTraitReminderLabel.setDebugId("selectTraitReminderLabel");
		selectTraitReminderLabel.setImmediate(true);
		selectTraitReminderLabel.setStyleName("gcp-bold-italic");
		selectTraitReminderLayout = new HeaderLabelLayout(new ThemeResource("images/warning.png"), selectTraitReminderLabel);
		this.addComponent(selectTraitReminderLayout,"top:35px;left:35px");

		Panel tablePanel = new Panel();
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
		this.traitsTable.setColumnHeader(TraitsAvailableComponent.TRAIT_PROPERTY_COLUMN_ID, this.messageSource.getMessage(Message.HEAD_TO_HEAD_PROPERTY));
		this.traitsTable.setColumnHeader(TraitsAvailableComponent.TRAIT_DESCRIPTION_COLUMN_ID, this.messageSource.getMessage(Message.HEAD_TO_HEAD_DESCRIPTION));
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
			public void columnResize(ColumnResizeEvent event) {
				int diff = event.getCurrentWidth() - event.getPreviousWidth();
				float newWidth = diff + TraitsAvailableComponent.this.traitsTable.getWidth();

				String widthPx = String.valueOf(newWidth) + "px";
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

		Button backButton = new Button("Back");
		backButton.setDebugId("backButton");
		backButton.setData(TraitsAvailableComponent.BACK_BUTTON_ID);
		backButton.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));
		backButton.setWidth("80px");
		addComponent(backButton, "top:470px;left:410px");

	}

	private ComboBox getDirectionComboBox() {
		ComboBox combo = new ComboBox();
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

	private void updatePopulateTraitsAndAnalysisAvailableTable(final boolean traitCheckBox, final boolean analysisCheckBox) {
		this.prevfinalGermplasmPair = null;
		this.tagUnTagAll.setValue(false);
		this.populateTraitsAvailableTable(this.finalGermplasmPair, this.germplasmIdNameMap, this.germplasmIdMGIDMap,traitCheckBox, analysisCheckBox);
	}

	public void populateTraitsAvailableTable(final List<GermplasmPair> germplasmPairList, final Map<String, String> germplasmIdNameMap, final Map<String, String> germplasmIdMGIDMap,final boolean traitCheckBox, final boolean analysisCheckBox) {

		this.initializeVariables();
		final Map<String, List<TraitInfo>> traitMap = new HashMap<>();
		final Map<String, Set<String>> traitEnvMap = new HashMap<>();

		this.germplasmIdNameMap = germplasmIdNameMap;
		this.germplasmIdMGIDMap = germplasmIdMGIDMap;
		this.finalGermplasmPair = germplasmPairList;
		final boolean doRefresh = this.validateDoRefresh();

		if (doRefresh) {
			this.prevfinalGermplasmPair = germplasmPairList;
			this.refreshEnviromentPairList(germplasmPairList, traitCheckBox, analysisCheckBox);
		}
		this.createEnviromentMap(traitMap, traitEnvMap);
		this.initializeTable(traitMap, traitEnvMap);

	}

	private void refreshEnviromentPairList(List<GermplasmPair> germplasmPairList, boolean traitCheckBox, boolean analysisCheckBox) {
		boolean filterByTraits = false;
		boolean filterByAnalysis = false;

		// Checking both checkbox will be equivalent to not checking anything
		if ((!traitCheckBox && analysisCheckBox) || (traitCheckBox && !analysisCheckBox)) {
			filterByTraits = traitCheckBox;
			filterByAnalysis = analysisCheckBox;
		}

		// only call when need to refresh
		this.prevfinalGermplasmPair = germplasmPairList;
		try {
			this.environmentPairList =
				this.crossStudyDataManager.getEnvironmentsForGermplasmPairs(germplasmPairList, filterByTraits, filterByAnalysis);
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void createEnviromentMap(final Map<String, List<TraitInfo>> traitMap, final Map<String, Set<String>> traitEnvMap) {
		for (GermplasmPair pair : this.environmentPairList) {
			TrialEnvironments env = pair.getTrialEnvironments();

			this.germplasmIds.add(Integer.valueOf(pair.getGid1()));
			this.germplasmIds.add(Integer.valueOf(pair.getGid2()));

			for(TrialEnvironment trialEnv: env.getTrialEnvironments()){
				this.trialEnvironmentMap.put(Integer.toString(trialEnv.getId()), trialEnv);
				for (TraitInfo info : trialEnv.getTraits()) {

					// add here the checking if the trait is non numeric
					if (info.getType() != TraitType.NUMERIC) {
						continue;
					}

					String id = Integer.toString(info.getId());
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
		if (this.prevfinalGermplasmPair != null && (this.prevfinalGermplasmPair.size() == this.finalGermplasmPair.size())) {
			if (this.prevfinalGermplasmPair.containsAll(finalGermplasmPair) && this.finalGermplasmPair
				.containsAll(prevfinalGermplasmPair)) {
				doRefresh = false;
			}
		}
		return doRefresh;
	}

	private void initializeTable(final Map<String, List<TraitInfo>> traitMap, final Map<String, Set<String>> traitEnvMap) {

		for (Map.Entry<String, List<TraitInfo>> entry : traitMap.entrySet()){
			String id = entry.getKey();
			List<TraitInfo> traitInfoList = entry.getValue();
			// we get the 1st one since its all the same for this specific list
			TraitInfo info = traitInfoList.get(0);
			CheckBox box = new CheckBox();
			box.setDebugId("box");
			ComboBox comboBox = this.getDirectionComboBox();
			box.setImmediate(true);
			Integer tableId = Integer.valueOf(id);

			Integer numOfEnv = traitEnvMap.get(id).size();
			this.traitsTable.addItem(new Object[] {box, info.getName(),info.getProperty() ,info.getDescription(), numOfEnv, comboBox}, tableId);
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

	private void visibleReminderFilterSelect(boolean visibleReminderFilter) {

		if (visibleReminderFilter) {
			this.selectTraitReminderLayout.setVisible(true);
			this.selectTraitReminderLayout.setICON(new ThemeResource("images/warning.png"));
		} else {
			this.selectTraitReminderLayout.setVisible(false);
			this.selectTraitReminderLayout.setICON(null);
		}
	}

	public void clickCheckBox(Component combo, boolean boolVal) {
		if (combo != null) {
			ComboBox comboBox = (ComboBox) combo;
			comboBox.setEnabled(boolVal);
			TraitInfo info = this.traitMaps.get(comboBox);

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

	public void clickTagAllCheckbox(boolean boxChecked) {
		Object[] tableItemIds = this.traitsTable.getItemIds().toArray();
		for (int i = 0; i < tableItemIds.length; i++) {
			Item row = this.traitsTable.getItem(tableItemIds[i]);
			CheckBox box = (CheckBox) row.getItemProperty(TraitsAvailableComponent.TAG_COLUMN_ID).getValue();
			box.setValue(boxChecked);
		}
	}

	public void nextButtonClickAction() {
		List<TraitForComparison> traitForComparisonsList = new ArrayList<>();
		for (ComboBox combo : this.traitForComparisons) {
			TraitInfo info = this.traitMaps.get(combo);
			TraitForComparison traitForComparison = new TraitForComparison(info, (Integer) combo.getValue());
			traitForComparisonsList.add(traitForComparison);
		}
		if (this.nextScreen != null) {
			this.nextScreen.populateEnvironmentsTable(traitForComparisonsList, this.traitEnvironmentMap, this.trialEnvironmentMap,
					this.germplasmIds, this.finalGermplasmPair, this.germplasmIdNameMap,this.germplasmIdMGIDMap);
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

	public CheckBox getTraitFilterCheckBox() {
		return traitFilterCheckBox;
	}

	public boolean getTraitFilterValue() {
		return (Boolean) getTraitFilterCheckBox().getValue();
	}

	public CheckBox getAnalysisFilterCheckBox() {
		return analysisFilterCheckBox;
	}

	public boolean getAnalysisFilterValue() {
		return (Boolean) getAnalysisFilterCheckBox().getValue();
	}
}
