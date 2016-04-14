package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.common.IContainerFittable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Configurable
public class ProgramLocationsView extends CustomComponent implements InitializingBean, IContainerFittable {

	private static final long serialVersionUID = 2596164971437339822L;

	private final ProgramLocationsPresenter presenter;
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private static final Logger LOG = LoggerFactory.getLogger(ProgramLocationsView.class);

	public static final Map<String, String> TABLE_COLUMNS;
	public static final Map<String, Integer> TABLE_COLUMN_SIZES;

	protected static final String AVAILABLE = "available";
	protected static final String FAVORITES = "favorites";
	private static final String FIELD = "field";
	private static final String SELECT = "select";
	private static final String LOCATION_NAME = "locationName";
	private static final String LOCATION_ABBREVIATION = "locationAbbreviation";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String ALTITUDE = "altitude";
	private static final String LTYPE_STR = "ltypeStr";


	static {
		TABLE_COLUMNS = new LinkedHashMap<String, String>();
		ProgramLocationsView.TABLE_COLUMNS.put(ProgramLocationsView.SELECT, "<span class='glyphicon glyphicon-ok'></span>");
		ProgramLocationsView.TABLE_COLUMNS.put(ProgramLocationsView.LOCATION_NAME, "Name");
		ProgramLocationsView.TABLE_COLUMNS.put(ProgramLocationsView.LOCATION_ABBREVIATION, "abbr.");
		ProgramLocationsView.TABLE_COLUMNS.put(ProgramLocationsView.LATITUDE, "Lat");
		ProgramLocationsView.TABLE_COLUMNS.put(ProgramLocationsView.LONGITUDE, "Long");
		ProgramLocationsView.TABLE_COLUMNS.put(ProgramLocationsView.ALTITUDE, "Alt");
		ProgramLocationsView.TABLE_COLUMNS.put(ProgramLocationsView.LTYPE_STR, "Type");

		TABLE_COLUMN_SIZES = new HashMap<String, Integer>();
		ProgramLocationsView.TABLE_COLUMN_SIZES.put(ProgramLocationsView.SELECT, 20);
		ProgramLocationsView.TABLE_COLUMN_SIZES.put(ProgramLocationsView.LOCATION_ABBREVIATION, 80);
		ProgramLocationsView.TABLE_COLUMN_SIZES.put(ProgramLocationsView.LTYPE_STR, 240);
	}

	private Button addNewLocationsBtn;
	private VerticalLayout root;
	private Button saveFavouritesBtn;
	private Table availableTable;
	private Table favoritesTable;
	private CheckBox availableSelectAll;
	private CheckBox favoriteSelectAll;
	private Label availTotalEntriesLabel;
	private Label favTotalEntriesLabel;
	private Label availSelectedEntriesLabel;
	private Label favSelectedEntriesLabel;
	private Select countryFilter;
	private Select locationTypeFilter;
	private TextField searchField;
	private Label resultCountLbl;
	private BeanItemContainer<LocationViewModel> availableTableContainer;
	private BeanItemContainer<LocationViewModel> favoritesTableContainer;
	private Button addToFavoriteBtn;
	private Button removeToFavoriteBtn;

	private Boolean cropOnly = false;
	private Button searchGoBtn;

	public ProgramLocationsView(Project project) {
		this.presenter = new ProgramLocationsPresenter(this, project);
	}

	public ProgramLocationsView(CropType cropType) {
		this.presenter = new ProgramLocationsPresenter(this, cropType);
		this.cropOnly = true;

	}

	private void initializeComponents() {
		this.resultCountLbl = new Label();
		this.addNewLocationsBtn = new Button("Add New Location");
		this.addNewLocationsBtn.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");
		this.addNewLocationsBtn.setVisible(false);
		this.saveFavouritesBtn = new Button("Save Favorites");
		this.saveFavouritesBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());

		this.searchGoBtn = new Button("Go");
		this.searchGoBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());

		this.availableSelectAll = new CheckBox("Select All");
		this.availableSelectAll.setImmediate(true);
		this.favoriteSelectAll = new CheckBox("Select All");
		this.favoriteSelectAll.setImmediate(true);
		try {
			AddRestrictredComponents();
		}catch (AccessDeniedException e){
			// Do no do anything as the screen needs to be displayed just the buttons don't
		}
		this.availTotalEntriesLabel = new Label(this.messageSource.getMessage(Message.TOTAL_ENTRIES) + ":  <b>0</b>", Label.CONTENT_XHTML);
		this.favTotalEntriesLabel = new Label(this.messageSource.getMessage(Message.TOTAL_ENTRIES) + ":  <b>0</b>", Label.CONTENT_XHTML);
		this.availSelectedEntriesLabel =
				new Label("<i>" + this.messageSource.getMessage(Message.SELECTED) + ":   <b>0</b></i>", Label.CONTENT_XHTML);
		this.favSelectedEntriesLabel =
				new Label("<i>" + this.messageSource.getMessage(Message.SELECTED) + ":   <b>0</b></i>", Label.CONTENT_XHTML);

		// TABLES!
		this.availableTable = this.buildCustomTable(this.availableSelectAll, this.availTotalEntriesLabel, this.availSelectedEntriesLabel);
		this.availableTable.setData(ProgramLocationsView.AVAILABLE);

		this.favoritesTable = this.buildCustomTable(this.favoriteSelectAll, this.favTotalEntriesLabel, this.favSelectedEntriesLabel);
		this.favoritesTable.setData(ProgramLocationsView.FAVORITES);

		this.addToFavoriteBtn = new Button("Add to Favorite Locations");
		this.addToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());


		this.removeToFavoriteBtn = new Button("Remove from Favorite Locations");
		this.removeToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

		// filter form
		this.initializeFilterForm();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	private void AddRestrictredComponents() {

		this.addNewLocationsBtn.setVisible(true);
	}

	private void doLocationSearch() {
		Country selectedCountry = (Country) this.countryFilter.getValue();
		UserDefinedField selectedLocationType = (UserDefinedField) this.locationTypeFilter.getValue();
		String locationName = (String) this.searchField.getValue();

		Integer cntryId = selectedCountry != null ? selectedCountry.getCntryid() : null;
		Integer locationTypeId = selectedLocationType != null ? selectedLocationType.getFldno() : null;

		try {
			this.availableTableContainer.removeAllItems();
			this.availableTableContainer.addAll(this.presenter.getFilteredResults(cntryId, locationTypeId, locationName,
					this.favoritesTableContainer.getItemIds()));

			this.resultCountLbl.setValue("Results: " + this.availableTableContainer.getItemIds().size() + " items");
			this.updateNoOfEntries(this.availTotalEntriesLabel, this.availableTable);
			this.updateSelectedNoOfEntries(this.availSelectedEntriesLabel, this.availableTable);
		} catch (MiddlewareQueryException e) {
			ProgramLocationsView.LOG.error(e.getMessage(), e);
		}
	}

	private void initializeActions() {
		this.addNewLocationsBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -7171034021312549121L;

			@Override
			public void buttonClick(ClickEvent clickEvent) {
				clickEvent.getComponent().getWindow()
						.addWindow(new AddLocationsWindow(ProgramLocationsView.this, ProgramLocationsView.this.presenter));
			}
		});

		Property.ValueChangeListener filterAction = new Property.ValueChangeListener() {

			private static final long serialVersionUID = -913467981172163048L;

			@Override
			public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
				ProgramLocationsView.this.doLocationSearch();
			}
		};

		this.searchField.addListener(filterAction);
		this.countryFilter.addListener(filterAction);
		this.locationTypeFilter.addListener(filterAction);

		this.availableSelectAll.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -8196548064100650289L;

			@Override
			public void buttonClick(ClickEvent clickEvent) {

				if ((Boolean) ((CheckBox) clickEvent.getComponent()).getValue()) {
					ProgramLocationsView.this.availableTable.setValue(ProgramLocationsView.this.availableTable.getItemIds());
				} else {
					ProgramLocationsView.this.availableTable.setValue(null);
				}

				ProgramLocationsView.this.updateSelectedNoOfEntries(ProgramLocationsView.this.availSelectedEntriesLabel,
						ProgramLocationsView.this.availableTable);
			}
		});

		this.favoriteSelectAll.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -3779881074831495245L;

			@Override
			public void buttonClick(ClickEvent clickEvent) {
				if ((Boolean) ((CheckBox) clickEvent.getComponent()).getValue()) {
					ProgramLocationsView.this.favoritesTable.setValue(ProgramLocationsView.this.favoritesTable.getItemIds());
				} else {
					ProgramLocationsView.this.favoritesTable.setValue(null);
				}

				ProgramLocationsView.this.updateSelectedNoOfEntries(ProgramLocationsView.this.favSelectedEntriesLabel,
						ProgramLocationsView.this.favoritesTable);

			}
		});

		this.addToFavoriteBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 131276363615646691L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				ProgramLocationsView.this.moveSelectedItems(ProgramLocationsView.this.availableTable,
						ProgramLocationsView.this.favoritesTable);
			}
		});

		this.removeToFavoriteBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -2208257555061319115L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				ProgramLocationsView.this.moveSelectedItems(ProgramLocationsView.this.favoritesTable,
						ProgramLocationsView.this.availableTable);
			}
		});

		this.saveFavouritesBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -1949478106602489651L;

			@Override
			public void buttonClick(ClickEvent clickEvent) {
				if (ProgramLocationsView.this.presenter.saveFavouriteLocations(ProgramLocationsView.this.favoritesTableContainer.getItemIds())) {
					MessageNotifier.showMessage(clickEvent.getComponent().getWindow(),
							ProgramLocationsView.this.messageSource.getMessage(Message.SUCCESS),
							ProgramLocationsView.this.messageSource.getMessage(Message.LOCATION_SUCCESSFULLY_CONFIGURED));
				}
			}
		});

	}

	protected void updateNoOfEntries(Label totalEntries, Table table) {
		int count = 0;
		count = table.getItemIds().size();

		totalEntries.setValue(this.messageSource.getMessage(Message.TOTAL_ENTRIES) + ": " + "  <b>" + count + "</b>");
	}

	private void updateSelectedNoOfEntries(Label selectedEntries, Table table) {
		int count = 0;

		Collection<?> selectedItems = (Collection<?>) table.getValue();
		count = selectedItems.size();

		selectedEntries.setValue("<i>" + this.messageSource.getMessage(Message.SELECTED) + ": " + "  <b>" + count + "</b></i>");
	}

	/**
	 * Use this to retrieve the favorite locations from the view, you might have to convert LocationViewModel to Middleware's Location bean
	 *
	 * @return
	 */
	public Collection<Location> getFavoriteLocations() {
		return this.presenter.convertTo(this.favoritesTableContainer.getItemIds());
	}

	@SuppressWarnings("unchecked")
	protected void moveSelectedItems(Table source, Table target) {
		List<Object> sourceItems = new LinkedList<Object>((Collection<Object>) source.getValue());
		ListIterator<Object> sourceItemsIterator = sourceItems.listIterator(sourceItems.size());

		BeanItemContainer<LocationViewModel> targetDataContainer = (BeanItemContainer<LocationViewModel>) target.getContainerDataSource();
		Container sourceDataContainer = source.getContainerDataSource();

		int counter = 0;
		while (sourceItemsIterator.hasPrevious()) {
			LocationViewModel itemId = (LocationViewModel) sourceItemsIterator.previous();
			itemId.setActive(false);

			if (source.getData().toString().equals(ProgramLocationsView.AVAILABLE)) {
				targetDataContainer.addItemAt(0, itemId);
				if (counter < 100) {
					target.unselect(itemId);
				}
			} else {
				sourceDataContainer.removeItem(itemId);
			}
			counter++;
		}

		if (counter >= 100 & target.getData().toString().equals(ProgramLocationsView.FAVORITES)) {
			target.setValue(null);
		}

		Table favoritesTableRef = source;
		if (source.getData().toString().equals(ProgramLocationsView.AVAILABLE)) {
			source.setValue(null);
			favoritesTableRef = target;
		}

		source.refreshRowCache();
		target.refreshRowCache();

		source.setValue(null);

		// refresh the fav location table
		this.updateNoOfEntries(this.favTotalEntriesLabel, favoritesTableRef);
		this.updateSelectedNoOfEntries(this.favSelectedEntriesLabel, favoritesTableRef);
	}

	private void initializeLayout() {
		this.root = new VerticalLayout();
		this.root.setSpacing(false);
		this.root.setMargin(new Layout.MarginInfo(false, true, true, true));

		final Label availableLocationsTitle = new Label(this.messageSource.getMessage(Message.ALL_LOCATIONS));
		availableLocationsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

		this.availableTable.setWidth("100%");
		this.favoritesTable.setWidth("100%");
		this.availableTable.setHeight("250px");
		this.favoritesTable.setHeight("250px");

		final HorizontalLayout availableTableBar = new HorizontalLayout();
		final HorizontalLayout favoritesTableBar = new HorizontalLayout();

		this.availableSelectAll.setWidth("100px");
		this.favoriteSelectAll.setWidth("100px");

		availableTableBar.setStyleName("select-all-bar");
		favoritesTableBar.setStyleName("select-all-bar");

		availableTableBar.setSizeUndefined();
		favoritesTableBar.setSizeUndefined();
		availableTableBar.setSpacing(true);
		favoritesTableBar.setSpacing(true);

		availableTableBar.addComponent(this.availableSelectAll);
		availableTableBar.addComponent(this.addToFavoriteBtn);
		favoritesTableBar.addComponent(this.favoriteSelectAll);
		favoritesTableBar.addComponent(this.removeToFavoriteBtn);

		this.root.addComponent(this.buildPageTitle());
		this.root.addComponent(availableLocationsTitle);
		this.root.addComponent(this.buildFilterForm());
		this.root.addComponent(this.buildLocationTableLabels(this.availTotalEntriesLabel, this.availSelectedEntriesLabel));
		this.root.addComponent(this.availableTable);
		this.root.addComponent(availableTableBar);
		this.root.addComponent(this.buildSelectedLocationsTitle());
		this.root.addComponent(this.buildLocationTableLabels(this.favTotalEntriesLabel, this.favSelectedEntriesLabel));
		this.root.addComponent(this.favoritesTable);
		this.root.addComponent(favoritesTableBar);

		this.setCompositionRoot(this.root);
	}

	private HorizontalLayout buildLocationTableLabels(Label totalEntries, Label selectedEntries) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setWidth("300px");

		layout.addComponent(totalEntries);
		layout.addComponent(selectedEntries);
		return layout;
	}

	private Component buildPageTitle() {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(new Layout.MarginInfo(false, false, true, false));
		layout.setWidth("100%");

		final HorizontalLayout titleContainer = new HorizontalLayout();
		titleContainer.setSizeUndefined();
		titleContainer.setWidth("100%");
		titleContainer.setMargin(true, false, false, false);

		final Label heading =
				new Label("<span class='bms-locations' style='color: #D1B02A; font-size: 23px'></span>&nbsp;Locations", Label.CONTENT_XHTML);
		heading.setStyleName(Bootstrap.Typography.H4.styleName());

		titleContainer.addComponent(heading);

		if (!this.cropOnly) {
			titleContainer.addComponent(this.addNewLocationsBtn);
			titleContainer.setComponentAlignment(this.addNewLocationsBtn, Alignment.MIDDLE_RIGHT);
		}

		String content =
				"To choose Favorite Locations for your program, "
						+ "select entries from the Available Locations table at the top and drag them " + "into the lower table.";

		if (!this.cropOnly) {
			content += " You can also add any new locations that you need for managing your program.";
		}

		final Label headingDesc = new Label(content);

		layout.addComponent(titleContainer);
		layout.addComponent(headingDesc);

		return layout;
	}

	private Component buildSelectedLocationsTitle() {
		final HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");
		layout.setMargin(true, false, false, false);

		final Label selectedLocationsTitle = new Label(this.messageSource.getMessage(Message.FAVORITE_PROGRAM_LOCATIONS));
		selectedLocationsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

		layout.addComponent(selectedLocationsTitle);

		if (!this.cropOnly) {
			layout.addComponent(this.saveFavouritesBtn);
		}

		layout.setExpandRatio(selectedLocationsTitle, 1.0F);

		return layout;
	}

	private void initializeValues() throws MiddlewareQueryException {
		BeanItemContainer<Country> countryContainer = new BeanItemContainer<Country>(Country.class);
		Country nullItem = new Country();
		nullItem.setCntryid(0);
		nullItem.setIsoabbr("All Countries");
		countryContainer.addItem(nullItem);
		countryContainer.addAll(this.presenter.getCountryList());

		/* INITIALIZE FILTER CONTROLS DATA */
		this.countryFilter.setContainerDataSource(countryContainer);
		this.countryFilter.setItemCaptionPropertyId("isoabbr");
		this.countryFilter.setNullSelectionItemId(nullItem);
		this.countryFilter.setNullSelectionAllowed(true);


		List<UserDefinedField> locationTypes = new ArrayList<UserDefinedField>();
		UserDefinedField nullUdf = new UserDefinedField();
		nullUdf.setFname("All Location Types");
		nullUdf.setFldno(0);
		locationTypes.add(nullUdf);
		locationTypes.addAll(this.presenter.getLocationTypeList());

		BeanItemContainer<UserDefinedField> udfContainer = new BeanItemContainer<UserDefinedField>(UserDefinedField.class, locationTypes);
		udfContainer.addAll(locationTypes);

		this.locationTypeFilter.setContainerDataSource(udfContainer);
		this.locationTypeFilter.setItemCaptionPropertyId("fname");
		if (locationTypes.size() > 1) {
			this.locationTypeFilter.select(locationTypes.get(1));
		} else {
			this.locationTypeFilter.select(locationTypes.get(0));
		}
		this.locationTypeFilter.setNullSelectionItemId(nullUdf);
		this.locationTypeFilter.setNullSelectionAllowed(true);

		/* INITIALIZE TABLE DATA */
		this.favoritesTableContainer =
				new BeanItemContainer<LocationViewModel>(LocationViewModel.class, this.presenter.getSavedProgramLocations());
		this.availableTableContainer =
				new BeanItemContainer<LocationViewModel>(LocationViewModel.class, this.presenter.getFilteredResults(null,
						this.getSelectedLocationTypeIdFromFilter(), ""));

		this.resultCountLbl.setValue("Result: " + this.availableTableContainer.size());

		this.availableTable.setContainerDataSource(this.availableTableContainer);
		this.updateNoOfEntries(this.availTotalEntriesLabel, this.availableTable);

		this.favoritesTable.setContainerDataSource(this.favoritesTableContainer);
		this.updateNoOfEntries(this.favTotalEntriesLabel, this.favoritesTable);

		/* SETUP TABLE FIELDS */
		this.setupTableFields(this.availableTable);
		this.setupTableFields(this.favoritesTable);
	}

	private void initializeFilterForm() {
		this.countryFilter = new Select();
		this.countryFilter.setImmediate(true);

		this.locationTypeFilter = new Select();
		this.locationTypeFilter.setImmediate(true);

		this.searchField = new TextField();
		this.searchField.setImmediate(true);
	}

	private Component buildFilterForm() {
		this.locationTypeFilter.setWidth("240px");

		final Label filterLbl = new Label("<b>Filter By:</b>&nbsp;", Label.CONTENT_XHTML);
		final Label searchLbl = new Label("<b>Search For:</b>&nbsp;", Label.CONTENT_XHTML);

		filterLbl.setSizeUndefined();
		searchLbl.setSizeUndefined();

		filterLbl.setStyleName("loc-filterlbl");
		searchLbl.setStyleName("loc-filterlbl");

		final CssLayout container = new CssLayout();
		container.addStyleName("loc-filter-bar");
		container.setSizeUndefined();
		container.setWidth("100%");

		final HorizontalLayout field1 = new HorizontalLayout();
		field1.addStyleName(ProgramLocationsView.FIELD);
		field1.setSpacing(true);
		field1.setSizeUndefined();
		field1.addComponent(searchLbl);
		field1.addComponent(this.searchField);
		field1.addComponent(this.searchGoBtn);

		this.searchGoBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4839268740583678422L;

			@Override
			public void buttonClick(ClickEvent clickEvent) {
				ProgramLocationsView.this.doLocationSearch();
			}
		});

		container.addComponent(field1);

		final HorizontalLayout field2 = new HorizontalLayout();
		field2.addStyleName(ProgramLocationsView.FIELD);
		field2.setSpacing(true);
		field2.setSizeUndefined();
		field2.addComponent(filterLbl);
		field2.addComponent(this.countryFilter);

		final HorizontalLayout field3 = new HorizontalLayout();
		field3.addStyleName(ProgramLocationsView.FIELD);
		field3.setSpacing(true);
		field3.setSizeUndefined();
		field3.addComponent(this.locationTypeFilter);

		HorizontalLayout filterContainer = new HorizontalLayout();
		filterContainer.setSpacing(true);
		filterContainer.setStyleName("pull-right");
		filterContainer.setSizeUndefined();

		filterContainer.addComponent(field2);
		filterContainer.addComponent(field3);

		container.addComponent(filterContainer);

		this.resultCountLbl = new Label("");
		this.resultCountLbl.setStyleName("loc-resultcnt");

		return container;
	}

	private void setupTableFields(Table table) {
		table.setVisibleColumns(ProgramLocationsView.TABLE_COLUMNS.keySet().toArray());
		table.setColumnHeaders(ProgramLocationsView.TABLE_COLUMNS.values().toArray(new String[] {}));

		table.setColumnWidth(ProgramLocationsView.SELECT, 20);
		table.setColumnExpandRatio(ProgramLocationsView.TABLE_COLUMNS.keySet().toArray()[1], 0.7F);
		table.setColumnExpandRatio(ProgramLocationsView.TABLE_COLUMNS.keySet().toArray()[6], 0.3F);

	}

	private Table buildCustomTable(final CheckBox assocSelectAll, final Label totalEntries, final Label selectedEntries) {
		final Table table = new Table();

		table.setImmediate(true);
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setDragMode(Table.TableDragMode.MULTIROW);

		table.addGeneratedColumn(ProgramLocationsView.SELECT, new Table.ColumnGenerator() {

			private static final long serialVersionUID = 346170573915290251L;

			@Override
			public Object generateCell(final Table source, final Object itemId, Object colId) {
				final CheckBox select = new CheckBox();
				select.setImmediate(true);
				select.addListener(new Button.ClickListener() {

					private static final long serialVersionUID = 4839268740583678422L;

					@Override
					public void buttonClick(ClickEvent clickEvent) {
						Boolean val = (Boolean) ((CheckBox) clickEvent.getComponent()).getValue();

						((LocationViewModel) itemId).setActive(val);
						if (val) {
							source.select(itemId);
						} else {
							source.unselect(itemId);
							assocSelectAll.setValue(val);
						}
					}
				});

				if (((LocationViewModel) itemId).isActive()) {
					select.setValue(true);
				} else {
					select.setValue(false);
				}

				return select;
			}
		});

		// Add behavior to table when selected/has new Value (must be immediate)
		final Table.ValueChangeListener vcl = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 650604866887197865L;

			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
				Table source = (Table) valueChangeEvent.getProperty();
				BeanItemContainer<LocationViewModel> container = (BeanItemContainer<LocationViewModel>) source.getContainerDataSource();

				// disable previously selected items
				for (LocationViewModel beanItem : container.getItemIds()) {
					beanItem.setActive(false);
				}

				// set current selection to true
				for (LocationViewModel selectedItem : (Collection<LocationViewModel>) source.getValue()) {
					selectedItem.setActive(true);
				}

				// update the no of selected items
				ProgramLocationsView.this.updateSelectedNoOfEntries(selectedEntries, table);

				// do table repaint
				source.requestRepaint();
				source.refreshRowCache();
			}
		};

		table.addListener(vcl);

		// Add Drag+Drop behavior
		table.setDropHandler(new DropHandler() {

			private static final long serialVersionUID = -1306941998752864672L;

			@SuppressWarnings("unchecked")
			@Override
			public void drop(DragAndDropEvent dragAndDropEvent) {
				DataBoundTransferable t = (DataBoundTransferable) dragAndDropEvent.getTransferable();

				if (t.getSourceComponent() == dragAndDropEvent.getTargetDetails().getTarget()) {
					return;
				}

				((Table) dragAndDropEvent.getTargetDetails().getTarget()).removeListener(vcl);

				Object itemIdOver = t.getItemId();
				Set<Object> sourceItemIds = (Set<Object>) ((Table) t.getSourceComponent()).getValue();

				if (itemIdOver != null && sourceItemIds.isEmpty()) {
					if (((LocationViewModel) itemIdOver).isEnabled()) {
						if (((Table) t.getSourceComponent()).getData().toString().equals(ProgramLocationsView.FAVORITES)) {
							((Table) t.getSourceComponent()).getContainerDataSource().removeItem(itemIdOver);
							ProgramLocationsView.this.updateNoOfEntries(ProgramLocationsView.this.favTotalEntriesLabel,
									(Table) t.getSourceComponent());
						}
						((Table) dragAndDropEvent.getTargetDetails().getTarget()).getContainerDataSource().addItem(itemIdOver);
					}
				} else {
					ProgramLocationsView.this.moveSelectedItems((Table) t.getSourceComponent(), (Table) dragAndDropEvent.getTargetDetails()
							.getTarget());
				}

				((Table) dragAndDropEvent.getTargetDetails().getTarget()).addListener(vcl);

				// update no of items
				ProgramLocationsView.this.updateNoOfEntries(totalEntries, table);
			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AbstractSelect.AcceptItem.ALL;
			}
		});

		return table;
	}

	public void addRow(LocationViewModel item, boolean atAvailableTable, Integer index) {
		Country selectedCountry = (Country) this.countryFilter.getValue();
		UserDefinedField selectedLocationType = (UserDefinedField) this.locationTypeFilter.getValue();
		String locationName = (String) this.searchField.getValue();
		if (index != null) {
			if (this.isToBeDisplayedInAvailableLocations(item, locationName, selectedCountry, selectedLocationType)) {
				this.availableTableContainer.addItemAt(index, item);
			}
			this.favoritesTableContainer.addItemAt(index, item);
		} else {
			if (this.isToBeDisplayedInAvailableLocations(item, locationName, selectedCountry, selectedLocationType)) {
				this.availableTableContainer.addItem(item);
			}
			this.favoritesTableContainer.addItem(item);
		}
		this.updateNoOfEntries();
	}

	protected boolean isToBeDisplayedInAvailableLocations(LocationViewModel item, String locationName, Country selectedCountry,
			UserDefinedField selectedLocationType) {

		Integer cntryId = selectedCountry != null ? selectedCountry.getCntryid() : null;
		Integer locationTypeId = selectedLocationType != null ? selectedLocationType.getFldno() : null;

		if (cntryId != null && !cntryId.equals(item.getCntryid())) {
			return false;
		}

		if (locationTypeId != null && 0 != locationTypeId.intValue() && !locationTypeId.equals(item.getLtype())) {
			return false;
		}

		return !(locationName != null && !locationName.isEmpty() && !item.getLocationName().toLowerCase()
				.contains(locationName.toLowerCase()));

	}

	private void updateNoOfEntries() {
		this.updateNoOfEntries(this.favTotalEntriesLabel, this.favoritesTable);
		this.updateNoOfEntries(this.availTotalEntriesLabel, this.availableTable);
	}

	@Override
	public void fitToContainer(final Window parentWindow) {
		this.availableTable.setHeight("100%");
		this.favoritesTable.setHeight("100%");

		this.root.setExpandRatio(this.availableTable, 1.0f);
		this.root.setExpandRatio(this.favoritesTable, 1.0f);
		this.root.setSizeFull();

		this.setSizeFull();

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeComponents();
		this.initializeValues();
		this.initializeLayout();
		this.initializeActions();

	}

	private Integer getSelectedLocationTypeIdFromFilter() {
		UserDefinedField udf = (UserDefinedField) this.locationTypeFilter.getValue();

		return udf != null ? udf.getFldno() : null;
	}

	public SimpleResourceBundleMessageSource getMessageSource() {
		return this.messageSource;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public Label getFavTotalEntriesLabel() {
		return this.favTotalEntriesLabel;
	}

	public void setFavTotalEntriesLabel(Label favTotalEntriesLabel) {
		this.favTotalEntriesLabel = favTotalEntriesLabel;
	}

	public Label getFavSelectedEntriesLabel() {
		return this.favSelectedEntriesLabel;
	}

	public void setFavSelectedEntriesLabel(Label favSelectedEntriesLabel) {
		this.favSelectedEntriesLabel = favSelectedEntriesLabel;
	}

	public Label getAvailSelectedEntriesLabel() {
		return this.availSelectedEntriesLabel;
	}

	public void setAvailSelectedEntriesLabel(Label availSelectedEntriesLabel) {
		this.availSelectedEntriesLabel = availSelectedEntriesLabel;
	}

	public Table getAvailableTable() {
		return this.availableTable;
	}

	public void setAvailableTable(Table availableTable) {
		this.availableTable = availableTable;
	}

	public Table getFavoritesTable() {
		return this.favoritesTable;
	}

	public void setFavoritesTable(Table favoritesTable) {
		this.favoritesTable = favoritesTable;
	}

	public Label getAvailTotalEntriesLabel() {
		return this.availTotalEntriesLabel;
	}

	public void setAvailTotalEntriesLabel(Label availTotalEntriesLabel) {
		this.availTotalEntriesLabel = availTotalEntriesLabel;
	}

	public BeanItemContainer<LocationViewModel> getAvailableTableContainer() {
		return this.availableTableContainer;
	}

	public void setAvailableTableContainer(BeanItemContainer<LocationViewModel> availableTableContainer) {
		this.availableTableContainer = availableTableContainer;
	}

	public BeanItemContainer<LocationViewModel> getFavoritesTableContainer() {
		return this.favoritesTableContainer;
	}

	public void setFavoritesTableContainer(BeanItemContainer<LocationViewModel> favoritesTableContainer) {
		this.favoritesTableContainer = favoritesTableContainer;
	}

	public void setCountryFilter(Select countryFilter) {
		this.countryFilter = countryFilter;
	}

	public void setLocationTypeFilter(Select locationTypeFilter) {
		this.locationTypeFilter = locationTypeFilter;
	}

	public void setSearchField(TextField searchField) {
		this.searchField = searchField;
	}

}
