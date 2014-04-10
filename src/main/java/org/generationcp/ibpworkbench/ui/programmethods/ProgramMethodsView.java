package org.generationcp.ibpworkbench.ui.programmethods;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
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
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

 import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
  * Created with IntelliJ IDEA.
  * User: cyrus
  * Date: 11/11/13
  * Time: 9:48 AM
  * To change this template use File | Settings | File Templates.
  */

 @Configurable
 public class ProgramMethodsView extends CustomComponent implements InitializingBean, IContainerFittable {
     private ProgramMethodsPresenter presenter;
     @Autowired
     private SimpleResourceBundleMessageSource messageSource;


     public final static String[][] methodTypes = {{"GEN","Generative"},{"DER","Derivative"},{"MAN","Maintenance"}};
     public final static String[][] methodGroups = {{"S","Self Fertilizing"},{"O","Cross Pollinating"},{"C","Clonally Propagating"},{"G","All System"}};

     public final static Map<String,String> tableColumns;
     public final static Map<String,Integer> tableColumnSizes;

         /*
               *
               * columnWidthsMap.put("mname",210);
                  columnWidthsMap.put("mgrp",45);
                  columnWidthsMap.put("mcode",40);
                  columnWidthsMap.put("mtype",40);
                  columnWidthsMap.put("fmdate",70);
               * */

     static {
         tableColumns = new LinkedHashMap<String,String>();
         tableColumns.put("select","<span class='glyphicon glyphicon-ok'></span>");
         tableColumns.put("mname","Method Name");
         tableColumns.put("desc","Description");
         tableColumns.put("mgrp","Group");
         tableColumns.put("mcode","Code");
         tableColumns.put("mtype","Type");
         tableColumns.put("date","Date");

         tableColumnSizes = new HashMap<String, Integer>();
         tableColumnSizes.put("select",20);
         tableColumnSizes.put("mname",210);
         tableColumnSizes.put("mgrp",45);
         tableColumnSizes.put("mcode",40);
         tableColumnSizes.put("mtype",40);
         tableColumnSizes.put("date",70);

     }


     private Button addNewMethodsBtn;
     private VerticalLayout root;
     private Button saveBtn;
     private Table availableTable;
     private Table favoritesTable;
     private CheckBox availableSelectAll;
     private CheckBox favoriteSelectAll;
     private Select groupFilter;
     private Select typeFilter;
     private TextField searchField;
     private Label resultCountLbl;
     private BeanItemContainer<MethodView> availableTableContainer;
     private BeanItemContainer<MethodView> favoritesTableContainer;
     private Button addToFavoriteBtn;
     private Button removeToFavoriteBtn;


     public ProgramMethodsView(Project project) {
         presenter = new ProgramMethodsPresenter(this,project);
     }


     @Override
     public void fitToContainer(Window parentWindow) {
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

     private void initializeComponents() {
         resultCountLbl = new Label();

         addNewMethodsBtn = new Button("Add New Method");
         addNewMethodsBtn.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");

         saveBtn = new Button("Save Favorites");
         saveBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());

         availableSelectAll = new CheckBox("Select All");
         availableSelectAll.setImmediate(true);
         favoriteSelectAll = new CheckBox("Select All");
         favoriteSelectAll.setImmediate(true);

         // TABLES!
         availableTable = buildCustomTable(availableSelectAll);

         favoritesTable = buildCustomTable(favoriteSelectAll);

         addToFavoriteBtn = new Button("Add to Favorite Methods");
         addToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

         removeToFavoriteBtn = new Button("Remove to Favorite Methods");
         removeToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

         // filter form
         this.initializeFilterForm();
     }

     private void initializeFilterForm() {
         typeFilter = new Select();
         typeFilter.setImmediate(true);
         typeFilter.setNullSelectionItemId(false);

         groupFilter = new Select();
         groupFilter.setImmediate(true);
         groupFilter.setNullSelectionAllowed(false);

         searchField = new TextField();
         searchField.setImmediate(true);

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
                 final MethodView beanItem = ((BeanItemContainer<MethodView>) source.getContainerDataSource()).getItem(itemId).getBean();

                 final CheckBox select = new CheckBox();
                 select.setImmediate(true);
                 select.addListener(new Button.ClickListener() {
                     @Override
                     public void buttonClick(Button.ClickEvent clickEvent) {
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

         table.addGeneratedColumn("date",new Table.ColumnGenerator() {
             @Override
             public Object generateCell(final Table source, final Object itemId, Object colId) {
                 final MethodView beanItem = ((BeanItemContainer<MethodView>) source.getContainerDataSource()).getItem(itemId).getBean();

                 DateFormat df = new SimpleDateFormat("yyyyMMdd");
                 DateFormat newDf = new SimpleDateFormat("MM/dd/yyyy");

                 if (beanItem.getMdate().toString().length() > 1) {
                     try {
                         return newDf.format(df.parse(beanItem.getMdate().toString()));
                     } catch (ParseException e) {
                         return "N/A";
                         //e.printStackTrace();
                     }
                 } else
                     return "N/A";
             }
         });

         table.addGeneratedColumn("desc", new Table.ColumnGenerator() {
             @Override
             public Object generateCell(final Table source, final Object itemId, Object colI) {
                 final MethodView beanItem = ((BeanItemContainer<MethodView>) source.getContainerDataSource()).getItem(itemId).getBean();

                 Label l = new Label();
                 l.setDescription(beanItem.getMdesc());
                 l.setValue(beanItem.getMdesc());

                 if (beanItem.getMdesc().length() > 90) {
                     l.setValue(beanItem.getMdesc().substring(0,90-3).trim().concat("..."));
                 }

                 return l;
             }
         });

         // Add behavior to table when selected/has new Value (must be immediate)
         final Property.ValueChangeListener vcl = new Property.ValueChangeListener() {
             @Override
             public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                 Table source = ((Table) valueChangeEvent.getProperty());
                 BeanItemContainer<MethodView> container = (BeanItemContainer<MethodView>) source.getContainerDataSource();

                 // disable previously selected items
                 for (MethodView beanItem : container.getItemIds()) {
                     beanItem.setActive(false);
                 }

                 // set current selection to true
                 for (MethodView selectedItem : (Collection <MethodView>) source.getValue() ) {
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

                 moveSelectedItems(((Table)t.getSourceComponent()),((Table)dragAndDropEvent.getTargetDetails().getTarget()));

                 ((Table)dragAndDropEvent.getTargetDetails().getTarget()).addListener(vcl);
             }

             @Override
             public AcceptCriterion getAcceptCriterion() {
                 return AbstractSelect.AcceptItem.ALL;
             }
         });

         return table;
     }

     private void moveSelectedItems(Table source, Table target) {
         LinkedList<Object> sourceItems = new LinkedList<Object>(((Collection<Object>) source.getValue()));
         ListIterator<Object> sourceItemsIterator = sourceItems.listIterator(sourceItems.size());

         while (sourceItemsIterator.hasPrevious()) {
             MethodView itemId = (MethodView) sourceItemsIterator.previous();
             itemId.setActive(false);
             ((BeanItemContainer<MethodView>) target.getContainerDataSource()).addItemAt(0, itemId);
             source.removeItem(itemId);
         }
     }

     private void initializeValues() {

             /* INITIALIZE FILTER CONTROLS DATA */
         typeFilter.addItem("");
         typeFilter.setItemCaption("","Generation Advancement Types");

         for (String[] methodType : methodTypes) {
             typeFilter.addItem(methodType[0]);
             typeFilter.setItemCaption(methodType[0], methodType[1]);
         }

         typeFilter.select(methodTypes[0][0]);

         groupFilter.addItem("");
         groupFilter.setItemCaption("", "Crop Reproductive Systems");
         for(String[] methodGroup : methodGroups) {
             groupFilter.addItem(methodGroup[0]);
             groupFilter.setItemCaption(methodGroup[0],methodGroup[1]);
         }
         groupFilter.select("");


             /* INITIALIZE TABLE DATA */
         favoritesTableContainer = new BeanItemContainer<MethodView>(MethodView.class,presenter.getSavedProgramMethods());
         availableTableContainer = new BeanItemContainer<MethodView>(MethodView.class,presenter.getFilteredResults(groupFilter.getValue().toString(),typeFilter.getValue().toString(),""));

         resultCountLbl.setValue("Result: " + availableTableContainer.size());

         availableTable.setContainerDataSource(availableTableContainer);
         favoritesTable.setContainerDataSource(favoritesTableContainer);

             /* SETUP TABLE FIELDS */
         this.setupTableFields(availableTable);
         this.setupTableFields(favoritesTable);
     }

     private void setupTableFields(Table table) {
         table.setVisibleColumns(tableColumns.keySet().toArray());
         table.setColumnHeaders(tableColumns.values().toArray(new String[]{}));

         //Field[] fields = LocationViewModel.class.getDeclaredFields();
         //table.setColumnWidth("select",20);
         //table.setColumnExpandRatio(tableColumns.keySet().toArray()[1],1.0F);

         for (String col : tableColumnSizes.keySet())
         {
             table.setColumnWidth(col,tableColumnSizes.get(col));
         }

         table.setColumnExpandRatio(tableColumns.keySet().toArray()[2],1.0F);
     }

     private void initializeLayout() {
         root = new VerticalLayout();
         root.setSpacing(false);
         root.setMargin(new Layout.MarginInfo(false,true,true,true));

         final Label availableMethodsTitle = new Label("Available Methods");
         availableMethodsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

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
         root.addComponent(availableMethodsTitle);
         root.addComponent(buildFilterForm());
         root.addComponent(availableTable);
         root.addComponent(availableTableBar);
         root.addComponent(buildFavoriteTableTitle());
         root.addComponent(favoritesTable);
         root.addComponent(favoritesTableBar);

         this.setCompositionRoot(root);
     }

     private Component buildFavoriteTableTitle() {
         final HorizontalLayout root = new HorizontalLayout();
         root.setWidth("100%");
         root.setMargin(true, false, false, false);

         final Label favoriteMethodsTitle = new Label(messageSource.getMessage(Message.FAVORITE_PROGRAM_METHODS));
         favoriteMethodsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

         root.addComponent(favoriteMethodsTitle);
         root.addComponent(saveBtn);
         root.setExpandRatio(favoriteMethodsTitle,1.0F);

         return root;
     }

     private Component buildFilterForm() {
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
         field2.addComponent(typeFilter);


         final HorizontalLayout field3 = new HorizontalLayout();
         field3.addStyleName("field");
         field3.setSpacing(true);
         field3.setSizeUndefined();
         field3.addComponent(groupFilter);

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

     private Component buildPageTitle() {
         final VerticalLayout root = new VerticalLayout();
         root.setMargin(new Layout.MarginInfo(false,false,true,false));
         root.setWidth("100%");

         final HorizontalLayout titleContainer = new HorizontalLayout();
         titleContainer.setSizeUndefined();
         titleContainer.setWidth("100%");
         titleContainer.setMargin(true, false, false, false);	// move this to css

         final Label heading = new Label("<span class='bms-methods' style='color: #B8D432; font-size: 23px'></span>&nbsp;Breeding Methods",Label.CONTENT_XHTML);
         heading.setStyleName(Bootstrap.Typography.H4.styleName());

         titleContainer.addComponent(heading);
         titleContainer.addComponent(addNewMethodsBtn);
         titleContainer.setComponentAlignment(addNewMethodsBtn, Alignment.MIDDLE_RIGHT);

         final Label headingDesc = new Label("To choose Favorite Breeding Methods for your program, select entries from the Available Breeding Methods table at the top and drag them onto the lower table. You can also add any new methods that you need for managing your program.");

         root.addComponent(titleContainer);
         root.addComponent(headingDesc);

         return root;
     }

     public void addRow(MethodView item,boolean atAvailableTable,Integer index) {
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

     public void addRow(Method item,boolean atAvailableTable,Integer index) {
         if (index != null) {
             if (atAvailableTable) {
                 availableTableContainer.addItemAt(index,presenter.convertMethod(item));

             } else {
                 favoritesTableContainer.addItemAt(index,presenter.convertMethod(item));
             }
         } else {
             if (atAvailableTable) {
                 availableTableContainer.addItem(presenter.convertMethod(item));

             } else {
                 favoritesTableContainer.addItem(presenter.convertMethod(item));
             }
         }
     }

     private void initializeActions() {
         addNewMethodsBtn.addListener(new Button.ClickListener() {
             @Override
             public void buttonClick(Button.ClickEvent event) {
                 //ProgramMethodsView.this.presenter.doAddMethodAction();
                 event.getComponent().getWindow().addWindow(new AddBreedingMethodsWindow(ProgramMethodsView.this));

             }
         });

         Property.ValueChangeListener filterAction = new Property.ValueChangeListener() {
             @Override
             public void valueChange(Property.ValueChangeEvent event) {
                 availableTableContainer.removeAllItems();
                 availableTableContainer.addAll(presenter.getFilteredResults(groupFilter.getValue().toString(), typeFilter.getValue().toString(), searchField.getValue().toString(),favoritesTableContainer.getItemIds()));

                 resultCountLbl.setValue("Results: " + availableTable.getContainerDataSource().getItemIds().size() + " items");
             }
         };

         searchField.addListener(filterAction);
         groupFilter.addListener(filterAction);
         typeFilter.addListener(filterAction);

         availableSelectAll.addListener(new Button.ClickListener() {
             @Override
             public void buttonClick(Button.ClickEvent clickEvent) {
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
             public void buttonClick(Button.ClickEvent clickEvent) {
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
             public void buttonClick(Button.ClickEvent event) {
                 if (ProgramMethodsView.this.presenter.saveProgramMethod(favoritesTableContainer.getItemIds())) {
                     MessageNotifier.showMessage(event.getComponent().getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.METHODS_SUCCESSFULLY_CONFIGURED));
                 } else {    // should never happen

                 }
             }
         });
     }

 }