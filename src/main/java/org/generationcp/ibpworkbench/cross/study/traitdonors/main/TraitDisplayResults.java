
package org.generationcp.ibpworkbench.cross.study.traitdonors.main;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.cross.study.adapted.dialogs.SaveToListDialog;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.CategoricalTraitEvaluator;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.CategoricalTraitFilter;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.CharacterTraitEvaluator;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.CharacterTraitFilter;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.NumericTraitEvaluator;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.NumericTraitFilter;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.ObservationList;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.TableResultRow;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.TraitObservationScore;
import org.generationcp.ibpworkbench.cross.study.constants.EnvironmentWeight;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.EnvironmentForComparison;
import org.generationcp.ibpworkbench.study.listeners.GidLinkButtonClickListener;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.ObservationKey;
import org.generationcp.middleware.domain.h2h.TraitType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Results are displayed here. Main goal is to award points for traits that fall within a desired, specified range. Points are removed if
 * over many locations, the traits do not meet the desired range.
 *
 * -- If the selected range covers all measurements for all locations, then the score will be 1.0 -- If there are more observations outside
 * the range than inside, the score will be negative -- Positive scores of any kind reflect more than 50% compliance in the range
 *
 * FIXME : the numbers displayed here are not correct and must be reviewed FIXME : this is a long class and further guidance through the
 * code is required
 *
 * @author rebecca
 *
 */
@Configurable
public class TraitDisplayResults extends AbsoluteLayout implements InitializingBean, InternationalizableComponent {

	private static final String GERMPLASM_COL_TABLE_WIDTH = "340px";

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(TraitDisplayResults.class);

	private static final String TAG_COLUMN_ID = "DisplayResults Tag Column Id";
	private static final String LINE_NO = "DisplayResults Line No";
	private static final String LINE_GID = "DisplayResults Line GID";
	private static final String LINE_DESIGNATION = "DisplayResults Line Designation";
	private static final String COMBINED_SCORE_COLUMN_ID = "DisplayResults Combined Score";

	public static final String SAVE_BUTTON_ID = "DisplayResults Save Button ID";
	public static final String BACK_BUTTON_ID = "DisplayResults Back Button ID";
	public static final String NEXT_ENTRY_BUTTON_ID = "DisplayResults Next Entry Button ID";
	public static final String PREV_ENTRY_BUTTON_ID = "DisplayResults Prev Entry Button ID";

	private final TraitDonorsQueryMain mainScreen;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private CrossStudyDataManager crossStudyDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private AbsoluteLayout resultsTable;
	private PagedTable germplasmColTable;
	private PagedTable traitsColTable;
	private PagedTable combinedScoreTagColTable;

	private Integer noOfTraitColumns;
	private List<String> columnHeaders;

	List<TableResultRow> tableRows;
	List<TableResultRow> tableRowsSelected;
	Integer currentLineIndex;

	private Button backButton;
	private Button saveButton;
	private Button nextEntryBtn;
	private Button prevEntryBtn;

	private List<Integer> environmentIds;
	List<EnvironmentForComparison> environments;

	private List<Integer> traitIds;
	private Map<Integer, String> germplasmIdNameMap;

	private List<NumericTraitFilter> numericTraitFilter;
	private List<CharacterTraitFilter> characterTraitFilter;
	private List<CategoricalTraitFilter> categoricalTraitFilter;

	private List<Observation> observations;
	private Map<ObservationKey, ObservationList> observationsMap;

	private SaveToListDialog saveGermplasmListDialog;
	private Map<Integer, String> selectedGermplasmMap;

	private CheckBox tagAllCheckBoxOnCombinedScoreTagColTable;

	public TraitDisplayResults(final TraitDonorsQueryMain mainScreen) {
		this.mainScreen = mainScreen;
	}

	@Override
	public void updateLabels() {
		// do not implement
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setHeight("550px");
		this.setWidth("1000px");

		this.resultsTable = new AbsoluteLayout();
		this.resultsTable.setDebugId("resultTable");
		this.resultsTable.setWidth("1000px");

		this.createGermplasmColTable();
		this.createTraitsColTable();
		this.createCombinedScoreTagColTable();

		this.addTablesToResultsTable();

		this.addComponent(new Label("<style> .v-table-column-selector { width:0; height:0; overflow:hidden; }"
				+ ".v-table-row, .v-table-row-odd { height: 25px; } " + ".v-table-header { height: auto; background-color: #dcdee0;} "
				+ ".v-table-header-wrap { height: auto; background-color: #dcdee0; } "
				+ ".v-table-caption-container { height: auto; background-color: #dcdee0; } " + ".v-table { border-radius: 0px; } "
				+ " </style>", Label.CONTENT_XHTML));
		this.addComponent(this.resultsTable, "top:0px;left:0px");

		this.addTagAllCheckBoxToCombinedScoreTagColTable();

		this.prevEntryBtn = new Button(this.messageSource.getMessage(Message.PREV_ARROW));
		this.prevEntryBtn.setDebugId("prevEntryBtn");
		this.prevEntryBtn.setData(TraitDisplayResults.NEXT_ENTRY_BUTTON_ID);

		this.prevEntryBtn.addListener(new ClickListener() {

			private static final long serialVersionUID = 7083618946346280184L;

			@Override
			public void buttonClick(final ClickEvent event) {
				TraitDisplayResults.this.prevEntryButtonClickAction();
			}
		});

		this.prevEntryBtn.setWidth("50px");
		this.prevEntryBtn.setEnabled(true);
		this.prevEntryBtn.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.addComponent(this.prevEntryBtn, "top:470px;left:455px");

		this.nextEntryBtn = new Button(this.messageSource.getMessage(Message.NEXT_ARROW));
		this.nextEntryBtn.setDebugId("nextEntryBtn");
		this.nextEntryBtn.setData(TraitDisplayResults.NEXT_ENTRY_BUTTON_ID);

		this.nextEntryBtn.addListener(new ClickListener() {

			private static final long serialVersionUID = -4837144379158727020L;

			@Override
			public void buttonClick(final ClickEvent event) {
				TraitDisplayResults.this.nextEntryButtonClickAction();
			}
		});

		this.nextEntryBtn.setWidth("50px");
		this.nextEntryBtn.setEnabled(true);
		this.nextEntryBtn.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.addComponent(this.nextEntryBtn, "top:470px;left:515x");

		this.backButton = new Button(this.messageSource.getMessage(Message.BACK));
		this.backButton.setDebugId("backButton");
		this.backButton.setData(TraitDisplayResults.BACK_BUTTON_ID);
		this.backButton.addListener(new ClickListener() {

			private static final long serialVersionUID = -8767137627847480579L;

			@Override
			public void buttonClick(final ClickEvent event) {
				TraitDisplayResults.this.backButtonClickAction();
			}
		});

		this.backButton.setWidth("100px");
		this.backButton.setEnabled(true);
		this.addComponent(this.backButton, "top:500px;left:405px");

		this.saveButton = new Button(this.messageSource.getMessage(Message.SAVE_GERMPLASMS_TO_NEW_LIST_LABEL));
		this.saveButton.setDebugId("saveButton");
		this.saveButton.setData(TraitDisplayResults.SAVE_BUTTON_ID);
		this.saveButton.addListener(new ClickListener() {

			private static final long serialVersionUID = -4170202465915624787L;

			@Override
			public void buttonClick(final ClickEvent event) {
				TraitDisplayResults.this.saveButtonClickAction();
			}
		});

		this.saveButton.setWidth("100px");
		this.saveButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.saveButton.setEnabled(false);
		this.addComponent(this.saveButton, "top:500px;left:515px");
	}

	protected void addTablesToResultsTable() {
		this.resultsTable.addComponent(this.germplasmColTable, "top:20px;left:20px");
		this.resultsTable.addComponent(this.traitsColTable, "top:20px;left:345px");
		this.resultsTable.addComponent(this.combinedScoreTagColTable, "top:20px;left:819px");
	}

	protected void createCombinedScoreTagColTable() {
		this.combinedScoreTagColTable = new PagedTable();
		this.combinedScoreTagColTable.setDebugId("combinedScoreTagColTable");
		this.combinedScoreTagColTable.setWidth("160px");
		this.combinedScoreTagColTable.setImmediate(true);
		this.combinedScoreTagColTable.setPageLength(15);
		this.combinedScoreTagColTable.setColumnCollapsingAllowed(true);
		this.combinedScoreTagColTable.setColumnReorderingAllowed(false);
	}

	protected void createTraitsColTable() {
		this.traitsColTable = new PagedTable();
		this.traitsColTable.setDebugId("traitsColTable");
		this.traitsColTable.setWidth("490px");
		this.traitsColTable.setImmediate(true);
		this.traitsColTable.setPageLength(15);
		this.traitsColTable.setColumnCollapsingAllowed(true);
		this.traitsColTable.setColumnReorderingAllowed(false);
	}

	protected void createGermplasmColTable() {
		this.germplasmColTable = new PagedTable();
		this.germplasmColTable.setDebugId("germplasmColTable");
		this.germplasmColTable.setWidth(GERMPLASM_COL_TABLE_WIDTH);
		this.germplasmColTable.setImmediate(true);
		this.germplasmColTable.setPageLength(15);
		this.germplasmColTable.setColumnCollapsingAllowed(true);
		this.germplasmColTable.setColumnReorderingAllowed(false);
	}

	public void populateResultsTable(final List<EnvironmentForComparison> environments, final List<NumericTraitFilter> numericTraitFilter,
			final List<CharacterTraitFilter> characterTraitFilter, final List<CategoricalTraitFilter> categoricalTraitFilter) {
		this.environments = environments;
		this.environmentIds = this.getEnvironmentIds(environments);
		this.numericTraitFilter = numericTraitFilter;
		this.characterTraitFilter = characterTraitFilter;
		this.categoricalTraitFilter = categoricalTraitFilter;

		this.traitIds = this.getTraitIds(numericTraitFilter, characterTraitFilter, categoricalTraitFilter);

		this.germplasmIdNameMap = this.getGermplasm(this.traitIds, this.environmentIds);
		this.selectedGermplasmMap = new HashMap<Integer, String>();

		this.resultsTable.removeAllComponents();
		this.createGermplasmColTable();
		this.germplasmColTable = this.createResultsTable(this.germplasmColTable);
		this.createTraitsColTable();
		this.traitsColTable = this.createResultsTable(this.traitsColTable);
		this.createCombinedScoreTagColTable();
		this.combinedScoreTagColTable = this.createResultsTable(this.combinedScoreTagColTable);
		this.addTablesToResultsTable();
		for (final Object propertyId : this.germplasmColTable.getContainerPropertyIds()) {
			if (propertyId.toString().equals(TraitDisplayResults.LINE_NO)|| propertyId.toString().equals(TraitDisplayResults.LINE_GID) || propertyId.toString().equals(TraitDisplayResults.LINE_DESIGNATION)) {
				this.germplasmColTable.setColumnCollapsed(propertyId, false);
			} else {
				this.germplasmColTable.setColumnCollapsed(propertyId, true);
			}
		}

		for (final Object propertyId : this.traitsColTable.getContainerPropertyIds()) {
			if (propertyId.toString().equals(TraitDisplayResults.LINE_NO) || propertyId.toString().equals(TraitDisplayResults.LINE_GID) || propertyId.toString().equals(TraitDisplayResults.LINE_DESIGNATION) || propertyId.toString().equals(TraitDisplayResults.TAG_COLUMN_ID) || propertyId.toString().equals(TraitDisplayResults.COMBINED_SCORE_COLUMN_ID)) {
				this.traitsColTable.setColumnCollapsed(propertyId, true);
			} else {
				this.traitsColTable.setColumnCollapsed(propertyId, false);
			}
		}

		for (final Object propertyId : this.combinedScoreTagColTable.getContainerPropertyIds()) {
			if (propertyId.toString().equals(TraitDisplayResults.TAG_COLUMN_ID) || propertyId.toString().equals(TraitDisplayResults.COMBINED_SCORE_COLUMN_ID)) {
				this.combinedScoreTagColTable.setColumnCollapsed(propertyId, false);
			} else {
				this.combinedScoreTagColTable.setColumnCollapsed(propertyId, true);
			}
		}

		// header column listener
		this.germplasmColTable.addListener(new Table.HeaderClickListener() {

			private static final long serialVersionUID = -9165077040691158639L;

			@Override
			public void headerClick(final HeaderClickEvent event) {
				final Object property = event.getPropertyId();
				final Object[] properties = new Object[] {property};

				final boolean order = TraitDisplayResults.this.germplasmColTable.isSortAscending();
				final boolean[] ordering = new boolean[] {order};

				TraitDisplayResults.this.traitsColTable.sort(properties, ordering);
				TraitDisplayResults.this.combinedScoreTagColTable.sort(properties, ordering);
			}
		});

		this.traitsColTable.addListener(new Table.HeaderClickListener() {

			private static final long serialVersionUID = -6923284105485115775L;

			@Override
			public void headerClick(final HeaderClickEvent event) {
				final Object property = event.getPropertyId();
				final Object[] properties = new Object[] {property};

				final boolean order = TraitDisplayResults.this.traitsColTable.isSortAscending();
				final boolean[] ordering = new boolean[] {order};

				TraitDisplayResults.this.germplasmColTable.sort(properties, ordering);
				TraitDisplayResults.this.combinedScoreTagColTable.sort(properties, ordering);
			}
		});

		this.combinedScoreTagColTable.addListener(new Table.HeaderClickListener() {

			private static final long serialVersionUID = 9161532217269536655L;

			@Override
			public void headerClick(final HeaderClickEvent event) {
				final Object property = event.getPropertyId();
				final Object[] properties = new Object[] {property};

				final boolean order = TraitDisplayResults.this.combinedScoreTagColTable.isSortAscending();
				final boolean[] ordering = new boolean[] {order};

				TraitDisplayResults.this.traitsColTable.sort(properties, ordering);
				TraitDisplayResults.this.germplasmColTable.sort(properties, ordering);
			}
		});

	}

	public PagedTable createResultsTable(final PagedTable resultTable) {
		resultTable.addContainerProperty(TraitDisplayResults.LINE_NO, Integer.class, null);
		resultTable.addContainerProperty(TraitDisplayResults.LINE_GID, Button.class, null);
		resultTable.addContainerProperty(TraitDisplayResults.LINE_DESIGNATION, String.class, null);

		resultTable.setColumnHeader(TraitDisplayResults.LINE_NO, "Line<br/> No");
		resultTable.setColumnHeader(TraitDisplayResults.LINE_GID, "Line<br/> GID");
		resultTable.setColumnHeader(TraitDisplayResults.LINE_DESIGNATION, "Line<br/> Designation");

		int noOfColumns = 3;
		this.noOfTraitColumns = 0;
		for (final NumericTraitFilter trait : this.numericTraitFilter) {
			final String name = this.getNameLabel(trait.getTraitInfo().getName().trim());
			final String weight = this.getWeightLabel(trait.getPriority().getWeight());
			final Integer traitId = trait.getTraitInfo().getId();

			resultTable.addContainerProperty(this.getContainerPropertyName(name, traitId, TraitType.NUMERIC), Integer.class, null);
			resultTable.addContainerProperty(this.getContainerPropertyName(weight, traitId, TraitType.NUMERIC), Double.class, null);

			resultTable.setColumnHeader(this.getContainerPropertyName(name, traitId, TraitType.NUMERIC), name);
			resultTable.setColumnHeader(this.getContainerPropertyName(weight, traitId, TraitType.NUMERIC), weight);

			noOfColumns += 2;
			this.noOfTraitColumns += 2;
		}

		for (final CharacterTraitFilter trait : this.characterTraitFilter) {
			final String name = this.getNameLabel(trait.getTraitInfo().getName().trim());
			final String weight = this.getWeightLabel(trait.getPriority().getWeight());
			final Integer traitId = trait.getTraitInfo().getId();

			resultTable.addContainerProperty(this.getContainerPropertyName(name, traitId, TraitType.CHARACTER), Integer.class, null);
			resultTable.addContainerProperty(this.getContainerPropertyName(weight, traitId, TraitType.CHARACTER), Double.class, null);

			resultTable.setColumnHeader(this.getContainerPropertyName(name, traitId, TraitType.CHARACTER), name);
			resultTable.setColumnHeader(this.getContainerPropertyName(weight, traitId, TraitType.CHARACTER), weight);

			noOfColumns += 2;
			this.noOfTraitColumns += 2;
		}

		for (final CategoricalTraitFilter trait : this.categoricalTraitFilter) {
			final String name = this.getNameLabel(trait.getTraitInfo().getName().trim());
			final String weight = this.getWeightLabel(trait.getPriority().getWeight());
			final Integer traitId = trait.getTraitInfo().getId();

			resultTable.addContainerProperty(this.getContainerPropertyName(name, traitId, TraitType.CATEGORICAL), Integer.class, null);
			resultTable.addContainerProperty(this.getContainerPropertyName(weight, traitId, TraitType.CATEGORICAL), Double.class, null);

			resultTable.setColumnHeader(this.getContainerPropertyName(name, traitId, TraitType.CATEGORICAL), name);
			resultTable.setColumnHeader(this.getContainerPropertyName(weight, traitId, TraitType.CATEGORICAL), weight);

			noOfColumns += 2;
			this.noOfTraitColumns += 2;
		}

		resultTable.addContainerProperty(TraitDisplayResults.COMBINED_SCORE_COLUMN_ID, Double.class, null);
		resultTable.setColumnHeader(TraitDisplayResults.COMBINED_SCORE_COLUMN_ID, "Combined<br/> Score");
		noOfColumns++;

		resultTable.addContainerProperty(TraitDisplayResults.TAG_COLUMN_ID, CheckBox.class, null);
		resultTable.setColumnHeader(TraitDisplayResults.TAG_COLUMN_ID, "Tag<br/>\n");
		noOfColumns++;

		this.tableRows = this.getTableRowsResults();
		this.currentLineIndex = 0;
		this.populateRowsResultsTable(resultTable, noOfColumns);

		return resultTable;
	}

	private String getNameLabel(final String name) {
		return name + "<br/> No of Obs";
	}

	private String getWeightLabel(final int weight) {
		return "Success Ratio";
	}

	private String getContainerPropertyName(final String name, final Integer traitId, final TraitType traitType) {
		return "DisplayResults " + name + traitId + " " + traitType.toString().toLowerCase();
	}

	public void populateRowsResultsTable(final PagedTable resultTable, final Integer noOfColumns) {
		int lineNo = this.currentLineIndex + 1;

		for (final TableResultRow row : this.tableRows) {
			final int gid = row.getGermplasmId();
			final String germplasmName = this.germplasmIdNameMap.get(gid);

			final Object[] itemObj = new Object[noOfColumns];

			itemObj[0] = lineNo;

			// make GID as link
			final String gidString = String.valueOf(gid);
			final Button gidButton = new Button(gidString, new GidLinkButtonClickListener(gidString));
			gidButton.setDebugId("gidButton");
			gidButton.setStyleName(BaseTheme.BUTTON_LINK);
			gidButton.setDescription("Click to view Germplasm information");
			itemObj[1] = gidButton;
			itemObj[2] = germplasmName == null ? "" : germplasmName;

			this.columnHeaders = this.getColumnProperties(resultTable.getContainerPropertyIds());

			final Map<NumericTraitFilter, TraitObservationScore> numericTOSMap = row.getNumericTOSMap();
			for (final Map.Entry<NumericTraitFilter, TraitObservationScore> numericTOS : numericTOSMap.entrySet()) {
				final String traitName = numericTOS.getKey().getTraitInfo().getName().trim();
				final Integer traitId = numericTOS.getKey().getTraitInfo().getId();

				final String name = this.getNameLabel(traitName);

				final int index = this.columnHeaders.indexOf(this.getContainerPropertyName(name, traitId, TraitType.NUMERIC));

				itemObj[index] = numericTOS.getValue().getNoOfObservation();
				itemObj[index + 1] = numericTOS.getValue().getWtScore();

			}

			final Map<CharacterTraitFilter, TraitObservationScore> characterTOSMap = row.getCharacterTOSMap();
			for (final Map.Entry<CharacterTraitFilter, TraitObservationScore> characterTOS : characterTOSMap.entrySet()) {
				final String traitName = characterTOS.getKey().getTraitInfo().getName().trim();
				final Integer traitId = characterTOS.getKey().getTraitInfo().getId();

				final String name = this.getNameLabel(traitName);

				final int index = this.columnHeaders.indexOf(this.getContainerPropertyName(name, traitId, TraitType.CHARACTER));

				itemObj[index] = characterTOS.getValue().getNoOfObservation();
				itemObj[index + 1] = characterTOS.getValue().getWtScore();

			}

			final Map<CategoricalTraitFilter, TraitObservationScore> categoricalTOSMap = row.getCategoricalTOSMap();
			for (final Map.Entry<CategoricalTraitFilter, TraitObservationScore> categoricalTOS : categoricalTOSMap.entrySet()) {
				final String traitName = categoricalTOS.getKey().getTraitInfo().getName().trim();
				final Integer traitId = categoricalTOS.getKey().getTraitInfo().getId();

				final String name = this.getNameLabel(traitName);

				final int index = this.columnHeaders.indexOf(this.getContainerPropertyName(name, traitId, TraitType.CATEGORICAL));

				itemObj[index] = categoricalTOS.getValue().getNoOfObservation();
				itemObj[index + 1] = categoricalTOS.getValue().getWtScore();

			}

			itemObj[noOfColumns - 2] = row.getCombinedScore();

			final CheckBox box = new CheckBox();
			box.setDebugId("box");
			box.setImmediate(true);
			box.setData(row);
			if (this.selectedGermplasmMap.containsKey(gid)) {
				box.setValue(true);
			}

			box.addListener(new ClickListener() {

				private static final long serialVersionUID = -3482228761993860979L;

				@Override
				public void buttonClick(final ClickEvent event) {
					final CheckBox box = (CheckBox) event.getSource();
					final TableResultRow row = (TableResultRow) box.getData();

					if (box.booleanValue()) {
						box.setValue(true);
					} else {
						box.setValue(false);
					}

					TraitDisplayResults.this.addItemForSelectedGermplasm(row);
				}
			});

			itemObj[noOfColumns - 1] = box;

			resultTable.addItem(itemObj, row);

			lineNo++;
		}
	}

	public List<String> getColumnHeaders(final String[] headers) {
		final List<String> columns = new ArrayList<>();

		for (int i = 0; i < headers.length; i++) {
			columns.add(headers[i].trim());
		}

		return columns;
	}

	@SuppressWarnings("rawtypes")
	public List<String> getColumnProperties(final Collection properties) {
		final List<String> columns = new ArrayList<>();

		for (final Object prop : properties) {
			columns.add(prop.toString());
		}

		return columns;
	}

	private Double getTotalEnvWeightForTrait(final Integer traitId, final Integer gid) {
		double totalEnvWeight = 0.0;
		for (final EnvironmentForComparison env : this.environments) {
			final ObservationKey key = new ObservationKey(traitId, gid, env.getEnvironmentNumber());
			final ObservationList obsList = this.observationsMap.get(key);

			if (obsList != null) {
				final ComboBox weightComboBox = env.getWeightComboBox();
				final EnvironmentWeight weight = (EnvironmentWeight) weightComboBox.getValue();
				totalEnvWeight = totalEnvWeight + Double.valueOf(weight.getWeight());
			}
		}
		return totalEnvWeight;
	}

	private Double roundOffDoubleToTwoDecimalPlaces(final Double toRoundOff) {
		final double roundedOff = Math.round(toRoundOff.doubleValue() * 100.0) / 100.0;
		return Double.valueOf(roundedOff);
	}

	public List<TableResultRow> getTableRowsResults() {
		final List<TableResultRow> rows = new ArrayList<TableResultRow>();

		try {
			// TODO must reuse the observations class Object and not have multiple calls of getObservationForTraits
			this.observations = this.crossStudyDataManager.getObservationsForTraits(this.traitIds, this.environmentIds);
			this.observationsMap = this.getObservationsMap(this.observations);

			final List<Integer> germplasmIds = new ArrayList<Integer>();
			germplasmIds.addAll(this.germplasmIdNameMap.keySet());

			for (final Map.Entry<Integer, String> germplasm : this.germplasmIdNameMap.entrySet()) {
				final int germplasmId = germplasm.getKey();

				final Map<NumericTraitFilter, TraitObservationScore> numericTOSMap = new HashMap<NumericTraitFilter, TraitObservationScore>();
				final Map<CharacterTraitFilter, TraitObservationScore> characterTOSMap =
						new HashMap<>();
				final Map<CategoricalTraitFilter, TraitObservationScore> categoricalTOSMap =
						new HashMap<>();

				// NUMERIC TRAIT
				for (final NumericTraitFilter trait : this.numericTraitFilter) {
					double envWt;
					int noOfObservation = 0;
					int noObsForAllEnvs = 0;
					double scorePerTrait = 0.0;

					final double totalEnvWeight = this.getTotalEnvWeightForTrait(trait.getTraitInfo().getId(), germplasmId);

					for (final EnvironmentForComparison env : this.environments) {
						final ObservationKey key = new ObservationKey(trait.getTraitInfo().getId(), germplasmId, env.getEnvironmentNumber());
						final ObservationList obsList = this.observationsMap.get(key);

						// if the observation exist
						if (obsList != null) {
							final ComboBox weightComboBox = env.getWeightComboBox();
							final EnvironmentWeight weight = (EnvironmentWeight) weightComboBox.getValue();
							envWt = Double.valueOf(weight.getWeight()) / totalEnvWeight;

							noOfObservation = obsList.getObservationList().size();
							noObsForAllEnvs += noOfObservation;

							double scorePerEnv = 0.0;
							for (final Observation obs : obsList.getObservationList()) {
								if (this.testNumericTraitVal(trait, obs)) {
									scorePerEnv = scorePerEnv + Double.valueOf(1);
								} else {
									scorePerEnv = scorePerEnv + Double.valueOf(-1);
								}
							}

							scorePerEnv = envWt * (scorePerEnv / Double.valueOf(noOfObservation));

							scorePerTrait += scorePerEnv;
						}
					}

					// No Of Observation and Wt Score Per Trait
					scorePerTrait = this.roundOffDoubleToTwoDecimalPlaces(scorePerTrait);
					final TraitObservationScore tos = new TraitObservationScore(germplasmId, noObsForAllEnvs, scorePerTrait);
					numericTOSMap.put(trait, tos);
				}

				// CHARACTER TRAIT
				for (final CharacterTraitFilter trait : this.characterTraitFilter) {
					double envWt;
					int noOfObservation = 0;
					int noObsForAllEnvs = 0;
					double scorePerTrait = 0.0;

					final Double totalEnvWeight = this.getTotalEnvWeightForTrait(trait.getTraitInfo().getId(), germplasmId);

					for (final EnvironmentForComparison env : this.environments) {
						final ObservationKey key = new ObservationKey(trait.getTraitInfo().getId(), germplasmId, env.getEnvironmentNumber());
						final ObservationList obsList = this.observationsMap.get(key);

						// if the observation exist
						if (obsList != null) {
							final ComboBox weightComboBox = env.getWeightComboBox();
							final EnvironmentWeight weight = (EnvironmentWeight) weightComboBox.getValue();
							envWt = Double.valueOf(weight.getWeight()) / totalEnvWeight;

							noOfObservation = obsList.getObservationList().size();
							noObsForAllEnvs += noOfObservation;

							double scorePerEnv = 0.0;
							for (final Observation obs : obsList.getObservationList()) {
								if (this.testCharacterTraitVal(trait, obs)) {
									scorePerEnv = scorePerEnv + Double.valueOf(1);
								} else {
									scorePerEnv = scorePerEnv + Double.valueOf(-1);
								}
							}

							scorePerEnv = envWt * (scorePerEnv / Double.valueOf(noOfObservation));

							scorePerTrait += scorePerEnv;
						}
					}

					// No Of Observation and Wt Score Per Trait
					scorePerTrait = this.roundOffDoubleToTwoDecimalPlaces(scorePerTrait);
					final TraitObservationScore tos = new TraitObservationScore(germplasmId, noObsForAllEnvs, scorePerTrait);
					characterTOSMap.put(trait, tos);
				}

				// CATEGORICAL TRAIT
				for (final CategoricalTraitFilter trait : this.categoricalTraitFilter) {
					double envWt;
					int noOfObservation = 0;
					int noObsForAllEnvs = 0;
					double scorePerTrait = 0.0;

					final Double totalEnvWeight = this.getTotalEnvWeightForTrait(trait.getTraitInfo().getId(), germplasmId);

					for (final EnvironmentForComparison env : this.environments) {
						final ObservationKey key = new ObservationKey(trait.getTraitInfo().getId(), germplasmId, env.getEnvironmentNumber());
						final ObservationList obsList = this.observationsMap.get(key);

						// if the observation exist
						if (obsList != null) {
							final ComboBox weightComboBox = env.getWeightComboBox();
							final EnvironmentWeight weight = (EnvironmentWeight) weightComboBox.getValue();
							envWt = Double.valueOf(weight.getWeight()) / totalEnvWeight;

							noOfObservation = obsList.getObservationList().size();
							noObsForAllEnvs += noOfObservation;

							double scorePerEnv = 0.0;
							for (final Observation obs : obsList.getObservationList()) {
								if (this.testCategoricalTraitVal(trait, obs)) {
									scorePerEnv = scorePerEnv + Double.valueOf(1);
								} else {
									scorePerEnv = scorePerEnv + Double.valueOf(-1);
								}
							}

							scorePerEnv = envWt * (scorePerEnv / Double.valueOf(noOfObservation));

							scorePerTrait += scorePerEnv;
						}
					}

					// No Of Observation and Wt Score Per Trait
					scorePerTrait = this.roundOffDoubleToTwoDecimalPlaces(scorePerTrait);
					final TraitObservationScore tos = new TraitObservationScore(germplasmId, noObsForAllEnvs, scorePerTrait);
					categoricalTOSMap.put(trait, tos);
				}

				rows.add(new TableResultRow(germplasmId, numericTOSMap, characterTOSMap, categoricalTOSMap));
			}

		} catch (final MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}

		return rows;
	}

	public boolean testNumericTraitVal(final NumericTraitFilter trait, final Observation observation) {
		// skip testing traits invalid value
		if (!NumberUtils.isNumber(observation.getValue())) {
			return true;
		} else {
			final NumericTraitEvaluator eval =
					new NumericTraitEvaluator(trait.getCondition(), trait.getLimits(), Double.valueOf(observation.getValue()));
			return eval.evaluate();
		}
	}

	public boolean testCharacterTraitVal(final CharacterTraitFilter trait, final Observation observation) {
		// skip testing traits with "missing" value
		if ("missing".equalsIgnoreCase(observation.getValue())) {
			return true;
		} else {
			final CharacterTraitEvaluator eval = new CharacterTraitEvaluator(trait.getCondition(), trait.getLimits(), observation.getValue());
			return eval.evaluate();
		}
	}

	public boolean testCategoricalTraitVal(final CategoricalTraitFilter trait, final Observation observation) {
		final CategoricalTraitEvaluator eval = new CategoricalTraitEvaluator(trait.getCondition(), trait.getLimits(), observation.getValue());

		return eval.evaluate();
	}

	public Map<ObservationKey, ObservationList> getObservationsMap(final List<Observation> observations) {
		final Map<ObservationKey, ObservationList> keyObservationsMap = new HashMap<>();

		for (final Observation obs : observations) {
			final ObservationKey key = obs.getId();

			if (!keyObservationsMap.containsKey(key)) {
				final ObservationList list = new ObservationList(key);
				list.add(obs);
				keyObservationsMap.put(key, list);
			} else {
				final ObservationList obslist = keyObservationsMap.get(key);
				final List<Observation> list = obslist.getObservationList();
				list.add(obs);
				obslist.setObservationList(list);

				keyObservationsMap.put(key, obslist);
			}
		}

		return keyObservationsMap;
	}

	public List<Integer> getTraitIds(final List<NumericTraitFilter> numericTraitFilter, final List<CharacterTraitFilter> characterTraitFilter,
			final List<CategoricalTraitFilter> categoricalTraitFilter) {
		final List<Integer> ids = new ArrayList<>();

		for (final NumericTraitFilter trait : numericTraitFilter) {
			ids.add(trait.getTraitInfo().getId());
		}

		for (final CharacterTraitFilter trait : characterTraitFilter) {
			ids.add(trait.getTraitInfo().getId());
		}

		for (final CategoricalTraitFilter trait : categoricalTraitFilter) {
			ids.add(trait.getTraitInfo().getId());
		}

		return ids;
	}

	public List<Integer> getEnvironmentIds(final List<EnvironmentForComparison> environments) {
		final List<Integer> envIds = new ArrayList<>();

		for (final EnvironmentForComparison env : environments) {
			envIds.add(env.getEnvironmentNumber());
		}

		return envIds;
	}

	public Map<Integer, String> getGermplasm(final List<Integer> traitIds, final List<Integer> environmentIds) {
		Map<Integer, String> gidNameMap = new HashMap<>();

		final List<Integer> germplasmIds = new ArrayList<>();
		final List<Integer> traitIdList = new ArrayList<>();
		traitIdList.addAll(traitIds);

		try {
			// TODO must reuse this observations Object and not have multiple calls of getObservationForTraits
			this.observations = this.crossStudyDataManager.getObservationsForTraits(traitIdList, environmentIds);

			final Iterator<Observation> obsIter = this.observations.iterator();
			while (obsIter.hasNext()) {
				final Observation observation = obsIter.next();
				final int id = observation.getId().getGermplasmId();
				if (!germplasmIds.contains(id)) {
					germplasmIds.add(id);
				}
			}

			gidNameMap = this.germplasmDataManager.getPreferredNamesByGids(germplasmIds);
		} catch (final MiddlewareQueryException ex) {
			TraitDisplayResults.LOG.error("Database error!", ex);
			MessageNotifier.showError(this.getWindow(), "Database Error!", this.messageSource.getMessage(Message.ERROR_REPORT_TO));
		}

		return gidNameMap;
	}

	public void nextEntryButtonClickAction() {
		if (this.currentLineIndex + 15 <= this.tableRows.size()) {
			this.germplasmColTable.nextPage();
			this.traitsColTable.nextPage();
			this.combinedScoreTagColTable.nextPage();
			this.currentLineIndex += 15;
		} else {
			MessageNotifier.showWarning(this.getWindow(), "Notification", "No More Rows to display.");
		}
	}

	public void prevEntryButtonClickAction() {
		if (this.currentLineIndex - 15  >= 0) {
			this.currentLineIndex -= 15;
			this.germplasmColTable.previousPage();
			this.traitsColTable.previousPage();
			this.combinedScoreTagColTable.previousPage();
		} else {
			this.currentLineIndex = 0;
			MessageNotifier.showWarning(this.getWindow(), "Notification", "No More Rows to preview.");
		}
	}

	public void backButtonClickAction() {
		this.mainScreen.selectSecondTab();
	}

	public void saveButtonClickAction() {
		this.openDialogSaveList();
	}

	public void addItemForSelectedGermplasm(final TableResultRow row) {
		final Integer gid = row.getGermplasmId();
		final String preferredName = this.germplasmIdNameMap.get(gid);

		if (this.selectedGermplasmMap.isEmpty()) {
			this.selectedGermplasmMap.put(gid, preferredName);
		} else {
			if (this.selectedGermplasmMap.containsKey(gid)) {
				this.selectedGermplasmMap.remove(gid);
			} else {
				this.selectedGermplasmMap.put(gid, preferredName);
			}
		}

		this.toggleSaveButton();

	}

	public void toggleSaveButton() {
		if (!this.selectedGermplasmMap.isEmpty()) {
			this.saveButton.setEnabled(true);
		} else if (this.selectedGermplasmMap.isEmpty()) {
			this.saveButton.setEnabled(false);
		}
	}

	private void openDialogSaveList() {
		final Window parentWindow = this.getWindow();

		this.saveGermplasmListDialog = new SaveToListDialog(this.mainScreen, this, parentWindow, this.selectedGermplasmMap);
		this.saveGermplasmListDialog.setDebugId("saveGermplasmListDialog");
		this.saveGermplasmListDialog.addStyleName(Reindeer.WINDOW_LIGHT);

		parentWindow.addWindow(this.saveGermplasmListDialog);
	}

	private void addTagAllCheckBoxToCombinedScoreTagColTable() {

		this.tagAllCheckBoxOnCombinedScoreTagColTable = new CheckBox();
		this.tagAllCheckBoxOnCombinedScoreTagColTable.setDebugId("tagAllCheckBoxOnCombinedScoreTagColTable");
		this.tagAllCheckBoxOnCombinedScoreTagColTable.setImmediate(true);

		this.addComponent(this.tagAllCheckBoxOnCombinedScoreTagColTable,
				"top:30px; left:" + (817 + this.combinedScoreTagColTable.getWidth() - 27) + "px;");

		this.tagAllCheckBoxOnCombinedScoreTagColTable.addListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(final ValueChangeEvent event) {
				if ((Boolean) TraitDisplayResults.this.tagAllCheckBoxOnCombinedScoreTagColTable.getValue()) {
					TraitDisplayResults.this.tagAllEnvironmentsOnCombinedScoreTagColTable();
				} else {
					TraitDisplayResults.this.untagAllEnvironmentsOnCombinedScoreTagColTable();
				}
			}
		});

	}

	private void tagAllEnvironmentsOnCombinedScoreTagColTable() {
		final Object[] tableItemIds = this.combinedScoreTagColTable.getItemIds().toArray();
		for (int i = 0; i < tableItemIds.length; i++) {
			if (this.combinedScoreTagColTable.getItem(tableItemIds[i]).getItemProperty(TraitDisplayResults.TAG_COLUMN_ID).getValue() instanceof CheckBox) {
				((CheckBox) this.combinedScoreTagColTable.getItem(tableItemIds[i]).getItemProperty(TraitDisplayResults.TAG_COLUMN_ID)
						.getValue()).setValue(true);
			}
		}
		this.selectedGermplasmMap.clear();
		for (int i = 0; i < this.tableRows.size(); i++) {
			final String preferredName = this.germplasmIdNameMap.get(this.tableRows.get(i).getGermplasmId());
			this.selectedGermplasmMap.put(this.tableRows.get(i).getGermplasmId(), preferredName);
		}
		this.toggleSaveButton();
	}

	private void untagAllEnvironmentsOnCombinedScoreTagColTable() {
		final Object[] tableItemIds = this.combinedScoreTagColTable.getItemIds().toArray();
		for (int i = 0; i < tableItemIds.length; i++) {
			if (this.combinedScoreTagColTable.getItem(tableItemIds[i]).getItemProperty(TraitDisplayResults.TAG_COLUMN_ID).getValue() instanceof CheckBox) {
				((CheckBox) this.combinedScoreTagColTable.getItem(tableItemIds[i]).getItemProperty(TraitDisplayResults.TAG_COLUMN_ID)
						.getValue()).setValue(false);
			}
		}
		this.selectedGermplasmMap.clear();
		this.toggleSaveButton();
	}

	public void setTraitIds(final List<Integer> traitIds) {
		this.traitIds = traitIds;
	}

	public void setEnvironmentIds(final List<Integer> environmentIds) {
		this.environmentIds = environmentIds;
	}

	public void setCrossStudyDataManager(final CrossStudyDataManager crossStudyDataManager) {
		this.crossStudyDataManager = crossStudyDataManager;
	}

	public void setGermplasmDataManager(final GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public void setResultsTable(final AbsoluteLayout resultsTable) {
		this.resultsTable = resultsTable;
	}

	public AbsoluteLayout getResultsTable() {
		return this.resultsTable;
	}

	public PagedTable getCreateCombinedScoreTagColTable() {
		return this.combinedScoreTagColTable;
	}

	public PagedTable getGermplasmColTable() {
		return this.germplasmColTable;
	}

	public PagedTable getTraitsColTable() {
		return this.traitsColTable;
	}

	public void setTableRows(final List<TableResultRow> tableRows) {
		this.tableRows = tableRows;
	}

	public void setCurrentLineIndex(final Integer currentLineIndex) {
		this.currentLineIndex = currentLineIndex;
	}

	public void setGermplasmIdNameMap(final Map<Integer, String> germplasmIdNameMap) {
		this.germplasmIdNameMap = germplasmIdNameMap;
	}

	public void setSelectedGermplasmMap(final Map<Integer, String> selectedGermplasmMap) {
		this.selectedGermplasmMap = selectedGermplasmMap;
	}

	public Integer getCurrentLineIndex() {
		return this.currentLineIndex;
	}
}
