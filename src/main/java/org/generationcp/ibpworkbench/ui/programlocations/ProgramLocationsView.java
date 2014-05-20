package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.*;

 import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.*;

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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button.ClickEvent;

@Configurable
 public class ProgramLocationsView extends CustomComponent implements InitializingBean, IContainerFittable {
     private ProgramLocationsPresenter presenter;
     @Autowired
     private SimpleResourceBundleMessageSource messageSource;

     private static final Logger LOG = LoggerFactory.getLogger(ProgramLocationsView.class);

     public final static Map<String,String> tableColumns;
     public final static Map<String,Integer> tableColumnSizes;

     static {
         tableColumns = new LinkedHashMap<String,String>();
         tableColumns.put("select","<span class='glyphicon glyphicon-ok'></span>");
         tableColumns.put("locationId","id");
         tableColumns.put("locationName","Name");
         tableColumns.put("locationAbbreviation","abbr.");
         tableColumns.put("ltypeStr","Type");

         tableColumnSizes = new HashMap<String, Integer>();
         tableColumnSizes.put("select",20);
         tableColumnSizes.put("locationAbbreviation",80);
         tableColumnSizes.put("ltypeStr",240);

     }

     private Button addNewLocationsBtn;
     private VerticalLayout root;
     private Button saveBtn;
     private Table availableTable;
     private Table favoritesTable;
     private CheckBox availableSelectAll;
     private CheckBox favoriteSelectAll;
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

         // TABLES!
         availableTable = buildCustomTable(availableSelectAll);

         favoritesTable = buildCustomTable(favoriteSelectAll);

         addToFavoriteBtn = new Button("Add to Favorite Locations");
         addToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

         removeToFavoriteBtn = new Button("Remove from Favorite Locations");
         removeToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

         // filter form
         this.initializeFilterForm();
     }
     private void initializeActions() {
         addNewLocationsBtn.addListener(new Button.ClickListener() {
             @Override
             public void buttonClick(ClickEvent clickEvent) {
                 clickEvent.getComponent().getWindow().addWindow(new AddLocationsWindow(ProgramLocationsView.this,presenter));
             }
         });

         Property.ValueChangeListener filterAction = new Property.ValueChangeListener() {
             @Override
             public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                 //ProgramLocationsView.this.LOG.debug("onUpdateAvailableTableOnFilter:");

                 Country selectedCountry = (Country) countryFilter.getValue();
                 UserDefinedField selectedLocationType =  (UserDefinedField) locationTypeFilter.getValue();
                 String locationName = (String) searchField.getValue();

                 Integer cntryId = (selectedCountry != null) ? selectedCountry.getCntryid() : null;
                 Integer locationTypeId = (selectedLocationType != null) ? selectedLocationType.getFldno() : null;


                 try {
                     availableTableContainer.removeAllItems();
                     availableTableContainer.addAll(presenter.getFilteredResults(cntryId, locationTypeId, locationName,favoritesTableContainer.getItemIds()));

                     resultCountLbl.setValue("Results: " + availableTableContainer.getItemIds().size() + " items");

                 } catch (MiddlewareQueryException e) {
                     // show error message
                     e.printStackTrace();
                 }
             }
         };

         searchField.addListener(filterAction);
         countryFilter.addListener(filterAction);
         locationTypeFilter.addListener(filterAction);

         availableSelectAll.addListener(new Button.ClickListener() {
             @Override
             public void buttonClick(ClickEvent clickEvent) {
                 for (Object itemId :availableTable.getItemIds()) {
                     if (true == (Boolean) ((CheckBox)clickEvent.getComponent()).getValue())
                         availableTable.select(itemId);
                     else
                         availableTable.unselect(itemId);
                 }
             }
         });


         favoriteSelectAll.addListener(new Button.ClickListener() {
             @Override
             public void buttonClick(ClickEvent clickEvent) {
                 for (Object itemId : favoritesTable.getItemIds()) {
                     if (true == (Boolean) ((CheckBox)clickEvent.getComponent()).getValue())
                         favoritesTable.select(itemId);
                     else
                         favoritesTable.unselect(itemId);
                 }
             }
         });

         addToFavoriteBtn.addListener(new Button.ClickListener() {
             @Override
             public void buttonClick(Button.ClickEvent clickEvent) {
                 moveSelectedItems(availableTable,favoritesTable);
             }
         });

         removeToFavoriteBtn.addListener(new Button.ClickListener() {
             @Override
             public void buttonClick(Button.ClickEvent clickEvent) {
                 moveSelectedItems(favoritesTable,availableTable);
             }
         });

         saveBtn.addListener(new Button.ClickListener() {
             @Override
             public void buttonClick(ClickEvent clickEvent) {
                 try {
                     if (presenter.saveProgramLocation(favoritesTableContainer.getItemIds())) {
                         MessageNotifier.showMessage(clickEvent.getComponent().getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.LOCATION_SUCCESSFULLY_CONFIGURED));
                     }

                 } catch (MiddlewareQueryException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
                 LOG.debug("onSaveProgramLocations:");
             }
         });

     }

    /**
     * Use this to retrieve the favorite locations from the view, you might have to convert LocationViewModel to Middleware's
     * Location bean
     * @return
     */
     public Collection<Location> getFavoriteLocations() {
         return presenter.convertTo(favoritesTableContainer.getItemIds()) ;
     }

     private void moveSelectedItems(Table source,Table target) {
         LinkedList<Object> sourceItems = new LinkedList<Object>(((Collection<Object>) source.getValue()));
         ListIterator<Object> sourceItemsIterator = sourceItems.listIterator(sourceItems.size());

         while (sourceItemsIterator.hasPrevious()) {
             LocationViewModel itemId = (LocationViewModel) sourceItemsIterator.previous();
             itemId.setActive(false);
             ((BeanItemContainer<LocationViewModel>) target.getContainerDataSource()).addItemAt(0, itemId);
             source.removeItem(itemId);
         }
     }

     private void initializeLayout() {
         root = new VerticalLayout();
         root.setSpacing(false);
         root.setMargin(new Layout.MarginInfo(false,true,true,true));

         final Label availableLocationsTitle = new Label("Available Locations");
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
         root.addComponent(availableTable);
         root.addComponent(availableTableBar);
         root.addComponent(buildSelectedLocationsTitle());
         root.addComponent(favoritesTable);
         root.addComponent(favoritesTableBar);

         this.setCompositionRoot(root);
     }

     private Component buildPageTitle() {
         final VerticalLayout root = new VerticalLayout();
         root.setMargin(new Layout.MarginInfo(false,false,true,false));
         root.setWidth("100%");

         final HorizontalLayout titleContainer = new HorizontalLayout();
         titleContainer.setSizeUndefined();
         titleContainer.setWidth("100%");
         titleContainer.setMargin(true, false, false, false);	// move this to css

         final Label heading = new Label("<span class='bms-locations' style='color: #D1B02A; font-size: 23px'></span>&nbsp;Locations",Label.CONTENT_XHTML);
         heading.setStyleName(Bootstrap.Typography.H4.styleName());

         titleContainer.addComponent(heading);

         if (!cropOnly) {
             titleContainer.addComponent(addNewLocationsBtn);
             titleContainer.setComponentAlignment(addNewLocationsBtn, Alignment.MIDDLE_RIGHT);
         }

         final Label headingDesc = new Label("To choose Favorite Locations for your program, select entries from the Available Locations table at the top and drag them into the lower table. You can also add any new locations that you need for managing your program.");

         root.addComponent(titleContainer);
         root.addComponent(headingDesc);

         return root;
     }

     private Component buildSelectedLocationsTitle() {
         final HorizontalLayout root = new HorizontalLayout();
         root.setWidth("100%");
         root.setMargin(true, false, false, false);

         final Label selectedLocationsTitle = new Label(messageSource.getMessage(Message.FAVORITE_PROGRAM_LOCATIONS));
         selectedLocationsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

         root.addComponent(selectedLocationsTitle);

         if (!cropOnly)
            root.addComponent(saveBtn);

         root.setExpandRatio(selectedLocationsTitle,1.0F);

         return root;
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
         favoritesTable.setContainerDataSource(favoritesTableContainer);

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
         field1.addStyleName("field");
         field1.setSpacing(true);
         field1.setSizeUndefined();
         field1.addComponent(searchLbl);
         field1.addComponent(searchField);

         container.addComponent(field1);

         final HorizontalLayout field2 = new HorizontalLayout();
         field2.addStyleName("field");
         field2.setSpacing(true);
         field2.setSizeUndefined();
         field2.addComponent(filterLbl);
         field2.addComponent(countryFilter);


         final HorizontalLayout field3 = new HorizontalLayout();
         field3.addStyleName("field");
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
         //root.addComponent(resultCountLbl);
         //root.setExpandRatio(resultCountLbl,1.0f);

         return container;
     }

     private void setupTableFields(Table table) {
         table.setVisibleColumns(tableColumns.keySet().toArray());
         table.setColumnHeaders(tableColumns.values().toArray(new String[]{}));

         //Field[] fields = LocationViewModel.class.getDeclaredFields();
         table.setColumnWidth("select",20);
         table.setColumnExpandRatio(tableColumns.keySet().toArray()[2],0.7F);
         table.setColumnExpandRatio(tableColumns.keySet().toArray()[4],0.3F);

             /*
             for (String col : tableColumnSizes.keySet())
             {
                 table.setColumnWidth(col,tableColumnSizes.get(col));
             }*/
     }

     private Table buildCustomTable(final CheckBox assocSelectAll) {
         Table table = new Table();

         table.setImmediate(true);
         table.setSelectable(true);
         table.setMultiSelect(true);
         table.setDragMode(Table.TableDragMode.MULTIROW);

         table.addGeneratedColumn("select", new Table.ColumnGenerator() {
             @Override
             public Object generateCell(final Table source, final Object itemId, Object colId) {
                 final LocationViewModel beanItem = ((BeanItemContainer<LocationViewModel>) source.getContainerDataSource()).getItem(itemId).getBean();

                 final CheckBox select = new CheckBox();
                 select.setImmediate(true);
                 select.addListener(new Button.ClickListener() {
                     @Override
                     public void buttonClick(ClickEvent clickEvent) {
                         Boolean val = (Boolean) ((CheckBox) clickEvent.getComponent())
                                 .getValue();

                         beanItem.setActive(val);
                         if (val)
                             source.select(itemId);
                         else {
                             source.unselect(itemId);
                             assocSelectAll.setValue(val);
                         }
                     }
                 });

                 if (beanItem.isActive())
                     select.setValue(true);
                 else
                     select.setValue(false);


                 return select;
             }
         });

         // Add behavior to table when selected/has new Value (must be immediate)
         final Table.ValueChangeListener vcl = new Property.ValueChangeListener() {
             @Override
             public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                 Table source = ((Table) valueChangeEvent.getProperty());
                 BeanItemContainer<LocationViewModel> container = (BeanItemContainer<LocationViewModel>) source.getContainerDataSource();

                 // disable previously selected items
                 for (LocationViewModel beanItem : container.getItemIds()) {
                     beanItem.setActive(false);
                 }

                 // set current selection to true
                 for (LocationViewModel selectedItem : (Collection <LocationViewModel>) source.getValue() ) {
                     selectedItem.setActive(true);
                 }

                 // do table repaint
                 source.requestRepaint();
                 source.refreshRowCache();
             }
         };

         table.addListener(vcl);

         // Add Drag+Drop behavior
         table.setDropHandler(new DropHandler() {
             @Override
             public void drop(DragAndDropEvent dragAndDropEvent) {
                 DataBoundTransferable t = (DataBoundTransferable) dragAndDropEvent.getTransferable();

                 if (t.getSourceComponent() == dragAndDropEvent.getTargetDetails().getTarget())
                     return;

                 ((Table)dragAndDropEvent.getTargetDetails().getTarget()).removeListener(vcl);
                 
                 
                 Object itemIdOver = t.getItemId();
                 Set<Object> sourceItemIds = (Set<Object>)((Table) t.getSourceComponent()).getValue();
                 
                 if (itemIdOver!=null && (sourceItemIds.size() <= 0)) {
                 	if (((LocationViewModel)itemIdOver).isEnabled()){
                 		((Table) t.getSourceComponent()).removeItem(itemIdOver);
                 		((Table) dragAndDropEvent.getTargetDetails().getTarget()).addItem(itemIdOver); 
                 	}
                 }else{
                	 moveSelectedItems(((Table) t.getSourceComponent()), ((Table) dragAndDropEvent.getTargetDetails().getTarget()));
                 }


                 ((Table)dragAndDropEvent.getTargetDetails().getTarget()).addListener(vcl);

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
             if (atAvailableTable) {
                 availableTableContainer.addItemAt(index,item);

             } else {
                 favoritesTableContainer.addItemAt(index,item);
             }
         } else {
             if (atAvailableTable) {
                 availableTableContainer.addItem(item);

             } else {
                 favoritesTableContainer.addItem(item);
             }
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

         // special actions added to save and cancel btns
              /*final Button.ClickListener execCloseFrameJS =  new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent clickEvent) {
                                parentWindow.executeJavaScript("window.parent.closeLocationFrame();");
                            }
                        };
              */
         //saveBtn.addListener(execCloseFrameJS);
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

 }
