package org.generationcp.ibpworkbench.ui.programlocations;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
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

import java.util.*;

@Configurable
 public class ProgramLocationsView extends CustomComponent implements InitializingBean, IContainerFittable {
	private static final long serialVersionUID = 2596164971437339822L;
	
	private ProgramLocationsPresenter presenter;
     @Autowired
     private SimpleResourceBundleMessageSource messageSource;

     private static final Logger LOG = LoggerFactory.getLogger(ProgramLocationsView.class);

     public static final Map<String,String> TABLE_COLUMNS;
     public static final Map<String,Integer> TABLE_COLUMN_SIZES;
     
     private static final String AVAILABLE = "available";
 	 private static final String FAVORITES = "favorites";
 	 private static final String FIELD = "field";
 	 private static final String SELECT = "select";
 	 private static final String LOCATION_NAME = "locationName";
 	 private static final String LOCATION_ABBREVIATION = "locationAbbreviation";
 	 private static final String LATITUDE = "latitude";
 	 private static final String LONGITUDE = "longitude";
 	 private static final String ALTITUDE = "altitude";
 	 private static final String LTYPE_STR = "ltypeStr";

     static {
         TABLE_COLUMNS = new LinkedHashMap<String,String>();
         TABLE_COLUMNS.put(SELECT,"<span class='glyphicon glyphicon-ok'></span>");
         TABLE_COLUMNS.put(LOCATION_NAME,"Name");
         TABLE_COLUMNS.put(LOCATION_ABBREVIATION,"abbr.");
         TABLE_COLUMNS.put(LATITUDE,"Lat");
         TABLE_COLUMNS.put(LONGITUDE,"Long");
         TABLE_COLUMNS.put(ALTITUDE,"Alt");
         TABLE_COLUMNS.put(LTYPE_STR,"Type");

         TABLE_COLUMN_SIZES = new HashMap<String, Integer>();
         TABLE_COLUMN_SIZES.put(SELECT,20);
         TABLE_COLUMN_SIZES.put(LOCATION_ABBREVIATION,80);
         TABLE_COLUMN_SIZES.put(LTYPE_STR,240);
     }

     private Button addNewLocationsBtn;
     private VerticalLayout root;
     private Button saveBtn;
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

     public ProgramLocationsView(Project project) {
         presenter = new ProgramLocationsPresenter(this,project);
     }

    public ProgramLocationsView(CropType cropType) {
        presenter = new ProgramLocationsPresenter(this,cropType);
        cropOnly = true;

    }

     private void initializeComponents() {
         resultCountLbl = new Label();

         addNewLocationsBtn = new Button("Add New Location");
         addNewLocationsBtn.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");

         saveBtn = new Button("Save Favorites");
         saveBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());

         availableSelectAll = new CheckBox("Select All");
         availableSelectAll.setImmediate(true);
         favoriteSelectAll = new CheckBox("Select All");
         favoriteSelectAll.setImmediate(true);
         
         availTotalEntriesLabel = new Label(messageSource.getMessage(Message.TOTAL_ENTRIES) + ":  <b>0</b>", Label.CONTENT_XHTML);
         favTotalEntriesLabel = new Label(messageSource.getMessage(Message.TOTAL_ENTRIES) + ":  <b>0</b>", Label.CONTENT_XHTML);
         availSelectedEntriesLabel = new Label("<i>" + messageSource.getMessage(Message.SELECTED) + ":   <b>0</b></i>", Label.CONTENT_XHTML);
         favSelectedEntriesLabel = new Label("<i>" + messageSource.getMessage(Message.SELECTED) + ":   <b>0</b></i>", Label.CONTENT_XHTML);
         
         // TABLES!
         availableTable = buildCustomTable(availableSelectAll, availTotalEntriesLabel, availSelectedEntriesLabel);
         availableTable.setData(AVAILABLE);

         favoritesTable = buildCustomTable(favoriteSelectAll, favTotalEntriesLabel, favSelectedEntriesLabel);
         favoritesTable.setData(FAVORITES);

         addToFavoriteBtn = new Button("Add to Favorite Locations");
         addToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

         removeToFavoriteBtn = new Button("Remove from Favorite Locations");
         removeToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

         // filter form
         this.initializeFilterForm();
     }
     private void initializeActions() {
         addNewLocationsBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -7171034021312549121L;

			@Override
             public void buttonClick(ClickEvent clickEvent) {
                 clickEvent.getComponent().getWindow().addWindow(new AddLocationsWindow(ProgramLocationsView.this,presenter));
             }
         });

         Property.ValueChangeListener filterAction = new Property.ValueChangeListener() {
			private static final long serialVersionUID = -913467981172163048L;

			@Override
             public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                 Country selectedCountry = (Country) countryFilter.getValue();
                 UserDefinedField selectedLocationType =  (UserDefinedField) locationTypeFilter.getValue();
                 String locationName = (String) searchField.getValue();

                 Integer cntryId = (selectedCountry != null) ? selectedCountry.getCntryid() : null;
                 Integer locationTypeId = (selectedLocationType != null) ? selectedLocationType.getFldno() : null;


                 try {
                     availableTableContainer.removeAllItems();
                     availableTableContainer.addAll(presenter.getFilteredResults(cntryId, locationTypeId, locationName,favoritesTableContainer.getItemIds()));

                     resultCountLbl.setValue("Results: " + availableTableContainer.getItemIds().size() + " items");
                     updateNoOfEntries(availTotalEntriesLabel, availableTable);
                     updateSelectedNoOfEntries(availSelectedEntriesLabel, availableTable);
                 } catch (MiddlewareQueryException e) {
                     LOG.error(e.getMessage(),e);
                 }
             }
         };

         searchField.addListener(filterAction);
         countryFilter.addListener(filterAction);
         locationTypeFilter.addListener(filterAction);

         availableSelectAll.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -8196548064100650289L;

			@Override
             public void buttonClick(ClickEvent clickEvent) {

                 if ((Boolean) ((CheckBox)clickEvent.getComponent()).getValue()){
                     availableTable.setValue(availableTable.getItemIds());
                 }else{
                     availableTable.setValue(null);
                 }
                 
                 updateSelectedNoOfEntries(availSelectedEntriesLabel, availableTable);
             }
         });


         favoriteSelectAll.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -3779881074831495245L;

			@Override
             public void buttonClick(ClickEvent clickEvent) {
                 if ((Boolean) ((CheckBox)clickEvent.getComponent()).getValue()){
                     favoritesTable.setValue(favoritesTable.getItemIds());
                 }else{
                     favoritesTable.setValue(null);
                 }
                 
                 updateSelectedNoOfEntries(favSelectedEntriesLabel, favoritesTable);

             }
         });

         addToFavoriteBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 131276363615646691L;

			@Override
             public void buttonClick(Button.ClickEvent clickEvent) {
                 moveSelectedItems(availableTable,favoritesTable);
             }
         });

         removeToFavoriteBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -2208257555061319115L;

			@Override
             public void buttonClick(Button.ClickEvent clickEvent) {
                 moveSelectedItems(favoritesTable,availableTable);
             }
         });

         saveBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -1949478106602489651L;

			@Override
             public void buttonClick(ClickEvent clickEvent) {
                 try {
                     if (presenter.saveProgramLocation(favoritesTableContainer.getItemIds())) {
                         MessageNotifier.showMessage(clickEvent.getComponent().getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.LOCATION_SUCCESSFULLY_CONFIGURED));
                     }

                 } catch (MiddlewareQueryException e) {
                	 LOG.error(e.getMessage(),e);
                 }
                 LOG.debug("onSaveProgramLocations:");
             }
         });

     }
     
     protected void updateNoOfEntries(Label totalEntries, Table table){
    	 int count = 0;
    	 count = table.getItemIds().size();
    	 
    	 totalEntries.setValue(messageSource.getMessage(Message.TOTAL_ENTRIES) + ": " 
        		 + "  <b>" + count + "</b>");
     }
     
     private void updateSelectedNoOfEntries(Label selectedEntries, Table table){
    	 int count = 0;
    	 
    	 Collection<?> selectedItems = (Collection<?>)table.getValue();
		 count = selectedItems.size();
    	 
		 
		 selectedEntries.setValue("<i>" + messageSource.getMessage(Message.SELECTED) + ": " 
        		 + "  <b>" + count + "</b></i>");
     }

    /**
     * Use this to retrieve the favorite locations from the view, you might have to convert LocationViewModel to Middleware's
     * Location bean
     * @return
     */
     public Collection<Location> getFavoriteLocations() {
         return presenter.convertTo(favoritesTableContainer.getItemIds()) ;
     }

     @SuppressWarnings("unchecked")
	private void moveSelectedItems(Table source,Table target) {
         List<Object> sourceItems = new LinkedList<Object>((Collection<Object>) source.getValue());
         ListIterator<Object> sourceItemsIterator = sourceItems.listIterator(sourceItems.size());

         BeanItemContainer<LocationViewModel> targetDataContainer = (BeanItemContainer<LocationViewModel>) target.getContainerDataSource();
         Container sourceDataContainer = source.getContainerDataSource();

         int counter = 0;
         while (sourceItemsIterator.hasPrevious()) {
             LocationViewModel itemId = (LocationViewModel) sourceItemsIterator.previous();
             itemId.setActive(false);
             
             if (source.getData().toString().equals(AVAILABLE)){
            	 targetDataContainer.addItemAt(0, itemId);
            	 if (counter < 100) {
                     target.unselect(itemId);
                 }
             }else{
            	 sourceDataContainer.removeItem(itemId);
             }
             counter++;
         }
         
         
         if (counter >= 100 & target.getData().toString().equals(FAVORITES)){
        	 target.setValue(null);
         }
         
         if (source.getData().toString().equals(AVAILABLE)){
        	 source.setValue(null);
         }
         
         source.refreshRowCache();
         target.refreshRowCache();
         
         source.setValue(null);
         
         //refresh the fav location table
         updateNoOfEntries(favTotalEntriesLabel, source);
         updateSelectedNoOfEntries(favSelectedEntriesLabel, source);
     }

     private void initializeLayout() {
         root = new VerticalLayout();
         root.setSpacing(false);
         root.setMargin(new Layout.MarginInfo(false,true,true,true));

         final Label availableLocationsTitle = new Label(messageSource.getMessage(Message.ALL_LOCATIONS));
         availableLocationsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

         availableTable.setWidth("100%");
         favoritesTable.setWidth("100%");
         availableTable.setHeight("250px");
         favoritesTable.setHeight("250px");

         final HorizontalLayout availableTableBar = new HorizontalLayout();
         final HorizontalLayout favoritesTableBar = new HorizontalLayout();

         availableSelectAll.setWidth("100px");
         favoriteSelectAll.setWidth("100px");

         availableTableBar.setStyleName("select-all-bar");
         favoritesTableBar.setStyleName("select-all-bar");

         availableTableBar.setSizeUndefined();
         favoritesTableBar.setSizeUndefined();
         availableTableBar.setSpacing(true);
         favoritesTableBar.setSpacing(true);

         availableTableBar.addComponent(availableSelectAll);
         availableTableBar.addComponent(addToFavoriteBtn);
         favoritesTableBar.addComponent(favoriteSelectAll);
         favoritesTableBar.addComponent(removeToFavoriteBtn);


         root.addComponent(buildPageTitle());
         root.addComponent(availableLocationsTitle);
         root.addComponent(buildFilterForm());
         root.addComponent(buildLocationTableLabels(availTotalEntriesLabel, availSelectedEntriesLabel));
         root.addComponent(availableTable);
         root.addComponent(availableTableBar);
         root.addComponent(buildSelectedLocationsTitle());
         root.addComponent(buildLocationTableLabels(favTotalEntriesLabel, favSelectedEntriesLabel));
         root.addComponent(favoritesTable);
         root.addComponent(favoritesTableBar);

         this.setCompositionRoot(root);
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
         layout.setMargin(new Layout.MarginInfo(false,false,true,false));
         layout.setWidth("100%");

         final HorizontalLayout titleContainer = new HorizontalLayout();
         titleContainer.setSizeUndefined();
         titleContainer.setWidth("100%");
         titleContainer.setMargin(true, false, false, false);

         final Label heading = new Label("<span class='bms-locations' style='color: #D1B02A; font-size: 23px'></span>&nbsp;Locations",Label.CONTENT_XHTML);
         heading.setStyleName(Bootstrap.Typography.H4.styleName());

         titleContainer.addComponent(heading);

         if (!cropOnly) {
             titleContainer.addComponent(addNewLocationsBtn);
             titleContainer.setComponentAlignment(addNewLocationsBtn, Alignment.MIDDLE_RIGHT);
         }

         String content = "To choose Favorite Locations for your program, " +
                 "select entries from the Available Locations table at the top and drag them " +
                 "into the lower table.";

         if (!cropOnly) {
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

         final Label selectedLocationsTitle = new Label(messageSource.getMessage(Message.FAVORITE_PROGRAM_LOCATIONS));
         selectedLocationsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

         layout.addComponent(selectedLocationsTitle);

         if (!cropOnly) {
        	 layout.addComponent(saveBtn);
         }

         layout.setExpandRatio(selectedLocationsTitle,1.0F);

         return layout;
     }


     private void initializeValues() throws MiddlewareQueryException {
        BeanItemContainer<Country> countryContainer = new BeanItemContainer<Country>(Country.class);
        Country nullItem = new Country();
        nullItem.setCntryid(0);
        nullItem.setIsoabbr("All Countries");
        countryContainer.addItem(nullItem);
        countryContainer.addAll(presenter.getCountryList());


              /* INITIALIZE FILTER CONTROLS DATA */
         countryFilter.setContainerDataSource(countryContainer);
         countryFilter.setItemCaptionPropertyId("isoabbr");
         countryFilter.setNullSelectionItemId(nullItem);
         countryFilter.setNullSelectionAllowed(true);

         List<UserDefinedField> locationTypes = new ArrayList<UserDefinedField>();
         UserDefinedField nullUdf = new UserDefinedField();
         nullUdf.setFname("All Location Types");
         nullUdf.setFldno(0);
         locationTypes.add(nullUdf);
         locationTypes.addAll(presenter.getLocationTypeList());

         BeanItemContainer<UserDefinedField> udfContainer = new BeanItemContainer<UserDefinedField>(UserDefinedField.class,locationTypes);
         udfContainer.addAll(locationTypes);

         locationTypeFilter.setContainerDataSource(udfContainer);
         locationTypeFilter.setItemCaptionPropertyId("fname");
         locationTypeFilter.select(locationTypes.get(1));
         locationTypeFilter.setNullSelectionItemId(nullUdf);
         locationTypeFilter.setNullSelectionAllowed(true);

              /* INITIALIZE TABLE DATA */
         favoritesTableContainer = new BeanItemContainer<LocationViewModel>(LocationViewModel.class,presenter.getSavedProgramLocations());
         availableTableContainer= new BeanItemContainer<LocationViewModel>(LocationViewModel.class, presenter.getFilteredResults(null, this.getSelectedLocationTypeIdFromFilter(), ""));

         resultCountLbl.setValue("Result: " + availableTableContainer.size());

         availableTable.setContainerDataSource(availableTableContainer);
         updateNoOfEntries(availTotalEntriesLabel, availableTable);
         
         favoritesTable.setContainerDataSource(favoritesTableContainer);
         updateNoOfEntries(favTotalEntriesLabel, favoritesTable);

              /* SETUP TABLE FIELDS */
         this.setupTableFields(availableTable);
         this.setupTableFields(favoritesTable);
     }

     private void initializeFilterForm() {
         countryFilter = new Select();
         countryFilter.setImmediate(true);

         locationTypeFilter = new Select();
         locationTypeFilter.setImmediate(true);

         searchField = new TextField();
         searchField.setImmediate(true);
     }

     private Component buildFilterForm() {
         locationTypeFilter.setWidth("240px");

         final Label filterLbl = new Label("<b>Filter By:</b>&nbsp;",Label.CONTENT_XHTML);
         final Label searchLbl = new Label("<b>Search For:</b>&nbsp;",Label.CONTENT_XHTML);

         filterLbl.setSizeUndefined();
         searchLbl.setSizeUndefined();

         filterLbl.setStyleName("loc-filterlbl");
         searchLbl.setStyleName("loc-filterlbl");

         final CssLayout container = new CssLayout();
         container.addStyleName("loc-filter-bar");
         container.setSizeUndefined();
         container.setWidth("100%");


         final HorizontalLayout field1 = new HorizontalLayout();
         field1.addStyleName(FIELD);
         field1.setSpacing(true);
         field1.setSizeUndefined();
         field1.addComponent(searchLbl);
         field1.addComponent(searchField);

         container.addComponent(field1);

         final HorizontalLayout field2 = new HorizontalLayout();
         field2.addStyleName(FIELD);
         field2.setSpacing(true);
         field2.setSizeUndefined();
         field2.addComponent(filterLbl);
         field2.addComponent(countryFilter);


         final HorizontalLayout field3 = new HorizontalLayout();
         field3.addStyleName(FIELD);
         field3.setSpacing(true);
         field3.setSizeUndefined();
         field3.addComponent(locationTypeFilter);

         HorizontalLayout filterContainer = new HorizontalLayout();
         filterContainer.setSpacing(true);
         filterContainer.setStyleName("pull-right");
         filterContainer.setSizeUndefined();

         filterContainer.addComponent(field2);
         filterContainer.addComponent(field3);

         container.addComponent(filterContainer);


         resultCountLbl = new Label("");
         resultCountLbl.setStyleName("loc-resultcnt");

         return container;
     }

     private void setupTableFields(Table table) {
         table.setVisibleColumns(TABLE_COLUMNS.keySet().toArray());
         table.setColumnHeaders(TABLE_COLUMNS.values().toArray(new String[]{}));

         table.setColumnWidth(SELECT,20);
         table.setColumnExpandRatio(TABLE_COLUMNS.keySet().toArray()[1],0.7F);
         table.setColumnExpandRatio(TABLE_COLUMNS.keySet().toArray()[6],0.3F);

     }

     private Table buildCustomTable(final CheckBox assocSelectAll, final Label totalEntries, final Label selectedEntries) {
         final Table table = new Table();

         table.setImmediate(true);
         table.setSelectable(true);
         table.setMultiSelect(true);
         table.setDragMode(Table.TableDragMode.MULTIROW);

         table.addGeneratedColumn(SELECT, new Table.ColumnGenerator() {
			private static final long serialVersionUID = 346170573915290251L;

			@Override
             public Object generateCell(final Table source, final Object itemId, Object colId) {
                 final CheckBox select = new CheckBox();
                 select.setImmediate(true);
                 select.addListener(new Button.ClickListener() {
					private static final long serialVersionUID = 4839268740583678422L;

					@Override
                     public void buttonClick(ClickEvent clickEvent) {
                         Boolean val = (Boolean) ((CheckBox) clickEvent.getComponent())
                                 .getValue();

                         ((LocationViewModel)itemId).setActive(val);
                         if (val) {
                             source.select(itemId);
                         } else {
                             source.unselect(itemId);
                             assocSelectAll.setValue(val);
                         }
                     }
                 });

                 if (((LocationViewModel)itemId).isActive()) {
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
                 for (LocationViewModel selectedItem : (Collection <LocationViewModel>) source.getValue() ) {
                     selectedItem.setActive(true);
                 }
                 
                 //update the no of selected items
                 updateSelectedNoOfEntries(selectedEntries, table);

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

                 ((Table)dragAndDropEvent.getTargetDetails().getTarget()).removeListener(vcl);
                 
                 
                 Object itemIdOver = t.getItemId();
                 Set<Object> sourceItemIds = (Set<Object>)((Table) t.getSourceComponent()).getValue();
                 
                 if (itemIdOver!=null && sourceItemIds.isEmpty()) {
                 	if (((LocationViewModel)itemIdOver).isEnabled()){
                 		if (((Table) t.getSourceComponent()).getData().toString().equals(FAVORITES)){
                 			((Table) t.getSourceComponent()).getContainerDataSource().removeItem(itemIdOver);
                 			updateNoOfEntries(favTotalEntriesLabel, (Table) t.getSourceComponent());
                 		}
                 		((Table) dragAndDropEvent.getTargetDetails().getTarget()).getContainerDataSource().addItem(itemIdOver);
                 	}
                 }else{
                	 moveSelectedItems((Table) t.getSourceComponent(), (Table) dragAndDropEvent.getTargetDetails().getTarget());
                 }


                 ((Table)dragAndDropEvent.getTargetDetails().getTarget()).addListener(vcl);
                 
                 //update no of items
                 updateNoOfEntries(totalEntries, table);
             }

             @Override
             public AcceptCriterion getAcceptCriterion() {
                 return AbstractSelect.AcceptItem.ALL;
             }
         });

         return table;
     }


     public void addRow(LocationViewModel item,boolean atAvailableTable,Integer index) {
         if (index != null) {
             
                 availableTableContainer.addItemAt(index,item);
                 favoritesTableContainer.addItemAt(index,item);
             
         } else {
             
                 availableTableContainer.addItem(item);
                 favoritesTableContainer.addItem(item);
             
         }

     }

     @Override
     public void fitToContainer(final Window parentWindow) {
         availableTable.setHeight("100%");
         favoritesTable.setHeight("100%");

         root.setExpandRatio(availableTable,1.0f);
         root.setExpandRatio(favoritesTable,1.0f);
         root.setSizeFull();

         this.setSizeFull();

     }

     @Override
     public void afterPropertiesSet() throws Exception {
         initializeComponents();
         initializeValues();
         initializeLayout();
         initializeActions();

     }

     private Integer getSelectedLocationTypeIdFromFilter() {
         UserDefinedField udf = (UserDefinedField) locationTypeFilter.getValue();

         return (udf != null) ? udf.getFldno() : null;
     }

	public SimpleResourceBundleMessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
     
     

 }
