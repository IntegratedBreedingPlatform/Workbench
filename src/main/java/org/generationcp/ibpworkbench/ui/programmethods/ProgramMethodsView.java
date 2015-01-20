package org.generationcp.ibpworkbench.ui.programmethods;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
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
import org.generationcp.middleware.pojos.workbench.CropType;
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
	private static final long serialVersionUID = 3300444018220674997L;
	
	private boolean cropOnly = false;
    protected ProgramMethodsPresenter presenter;

    @Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public static final String[][] METHOD_TYPES = {{"GEN","Generative"},{"DER","Derivative"},{"MAN","Maintenance"}};
	public static final String[][] METHOD_GROUPS = {{"S","Self Fertilizing"},{"O","Cross Pollinating"},{"C","Clonally Propagating"},{"G","All System"}};
	private static Action copyBreedingMethodAction = new Action("Copy Breeding Method");
     
	public static final Map<String,String> TABLE_COLUMNS;
	public static final Map<String,Integer> TABLE_COLUMN_SIZES;

	public static final String AVAILABLE = "available";
	public static final String FAVORITES = "favorites";
	private static final String SELECT = "select";
	private static final String GMNAME = "gMname";
	private static final String DESC = "desc";
	private static final String MGRP = "mgrp";
	private static final String MCODE = "mcode";
	private static final String MTYPE = "mtype";
	private static final String DATE = "date";
	private static final String CLASS = "class";	
	private static final String FIELD = "field";
	
    private Button.ClickListener editMethodListener;

    static {
    	 TABLE_COLUMNS = new LinkedHashMap<String,String>();
         TABLE_COLUMNS.put(SELECT,"<span class='glyphicon glyphicon-ok'></span>");
         TABLE_COLUMNS.put(GMNAME,"Method Name");
         TABLE_COLUMNS.put(DESC,"Description");
         TABLE_COLUMNS.put(MGRP,"Group");
         TABLE_COLUMNS.put(MCODE,"Code");
         TABLE_COLUMNS.put(MTYPE,"Type");
         TABLE_COLUMNS.put(DATE,"Date");
         TABLE_COLUMNS.put(CLASS,"Class");

         TABLE_COLUMN_SIZES = new HashMap<String, Integer>();
         TABLE_COLUMN_SIZES.put(SELECT,20);
         TABLE_COLUMN_SIZES.put(GMNAME,210);
         TABLE_COLUMN_SIZES.put(MGRP,45);
         TABLE_COLUMN_SIZES.put(MCODE,40);
         TABLE_COLUMN_SIZES.put(MTYPE,40);
         TABLE_COLUMN_SIZES.put(DATE,70);
         TABLE_COLUMN_SIZES.put(CLASS,45);
     }

     private Button addNewMethodsBtn;
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
     private Select groupFilter;
     private Select typeFilter;
     private TextField searchField;
     private Label resultCountLbl;
     private BeanItemContainer<MethodView> availableTableContainer;
     private BeanItemContainer<MethodView> favoritesTableContainer;
     private Button addToFavoriteBtn;
     private Button removeToFavoriteBtn;
     
     private Map<Integer,String> classMap;


     public ProgramMethodsView(Project project) {
         presenter = new ProgramMethodsPresenter(this,project);
     }

    public ProgramMethodsView(CropType cropType) {
        this.cropOnly = true;
        presenter = new ProgramMethodsPresenter(this,cropType);
    }


     @Override
     public void fitToContainer(Window parentWindow) {
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
         
         availTotalEntriesLabel = new Label(messageSource.getMessage(Message.TOTAL_ENTRIES) + ":  <b>0</b>", Label.CONTENT_XHTML);
         favTotalEntriesLabel = new Label(messageSource.getMessage(Message.TOTAL_ENTRIES) + ":  <b>0</b>", Label.CONTENT_XHTML);
         availSelectedEntriesLabel = new Label("<i>" + messageSource.getMessage(Message.SELECTED) + ":   <b>0</b></i>", Label.CONTENT_XHTML);
         favSelectedEntriesLabel = new Label("<i>" + messageSource.getMessage(Message.SELECTED) + ":   <b>0</b></i>", Label.CONTENT_XHTML);
         
         // TABLES!
         availableTable = buildCustomTable(availableSelectAll, availTotalEntriesLabel, availSelectedEntriesLabel);
         availableTable.setData(AVAILABLE);
         favoritesTable = buildCustomTable(favoriteSelectAll, favTotalEntriesLabel, favSelectedEntriesLabel);
         favoritesTable.setData(FAVORITES);

         addToFavoriteBtn = new Button("Add to Favorite Methods");
         addToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

         removeToFavoriteBtn = new Button("Remove from Favorite Methods");
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

     private Table buildCustomTable(final CheckBox assocSelectAll, final Label totalEntries, final Label selectedEntries) {
         final Table table = new Table();

         table.setImmediate(true);
         table.setSelectable(true);
         table.setMultiSelect(true);
         table.setDragMode(Table.TableDragMode.MULTIROW);

         table.addGeneratedColumn(SELECT, new Table.ColumnGenerator() {
			private static final long serialVersionUID = -2712621177075270647L;

			@Override
             public Object generateCell(final Table source, final Object itemId, Object colId) {
                 final CheckBox select = new CheckBox();
                 select.setImmediate(true);
                 select.addListener(new Button.ClickListener() {
					private static final long serialVersionUID = -5401459415390953417L;

					@Override
                     public void buttonClick(Button.ClickEvent clickEvent) {
                         Boolean val = (Boolean) ((CheckBox) clickEvent.getComponent())
                                 .getValue();

                         ((MethodView)itemId).setActive(val);
                         if (val) {
                             source.select(itemId);
                         } else {
                             source.unselect(itemId);
                             assocSelectAll.setValue(val);
                         }
                     }
                 });

                 if (((MethodView)itemId).isActive()) {
                     select.setValue(true);
                 } else {
                     select.setValue(false);
                 }


                 return select;
             }
         });

         table.addGeneratedColumn(GMNAME, new Table.ColumnGenerator() {
			private static final long serialVersionUID = -9087436773196724575L;

			@Override
             public Object generateCell(final Table source, final Object itemId, Object colId) {
                 final Button mNameBtn = new Button(((MethodView)itemId).getMname());
                 mNameBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());
                 mNameBtn.setData((MethodView)itemId);
                 mNameBtn.addListener(editMethodListener);

                 return mNameBtn;
             }
         });



         table.addGeneratedColumn(CLASS, new Table.ColumnGenerator() {
			private static final long serialVersionUID = -9208828919595982878L;

			@Override
             public Object generateCell(final Table source, final Object itemId, Object colId) {
                 Label classLbl = new Label("");
                 classLbl.setContentMode(Label.CONTENT_XHTML);
                 String methodClass = classMap.get(((MethodView)itemId).getGeneq());
                 methodClass = methodClass==null?"":methodClass;
                 classLbl.setValue(methodClass);
                 classLbl.setDescription(methodClass);
                 return classLbl;
             }
         });


         table.addGeneratedColumn(DATE,new Table.ColumnGenerator() {
			private static final long serialVersionUID = -8704716382416470975L;

			@Override
             public Object generateCell(final Table source, final Object itemId, Object colId) {
                 DateFormat df = new SimpleDateFormat("yyyyMMdd");
                 DateFormat newDf = new SimpleDateFormat("MM/dd/yyyy");

                 if (((MethodView)itemId).getMdate().toString().length() > 1) {
                     try {
                         return newDf.format(df.parse(((MethodView) itemId).getMdate().toString()));
                     } catch (ParseException e) {
                         return "N/A";
                     }
                 } else {
                     return "N/A";
                 }
             }
         });

         table.addGeneratedColumn(DESC, new Table.ColumnGenerator() {
			private static final long serialVersionUID = 6278117387128053730L;

			@Override
             public Object generateCell(final Table source, final Object itemId, Object colI) {
                 Label l = new Label();
                 l.setDescription(((MethodView)itemId).getMdesc());
                 l.setValue(((MethodView) itemId).getMdesc());

                 if (((MethodView)itemId).getMdesc().length() > 90) {
                     l.setValue(((MethodView)itemId).getMdesc().substring(0,90-3).trim().concat("..."));
                 }

                 return l;
             }
         });

         // Add behavior to table when selected/has new Value (must be immediate)
         final Property.ValueChangeListener vcl = new Property.ValueChangeListener() {
			private static final long serialVersionUID = -3156210329504164970L;

			@SuppressWarnings("unchecked")
			@Override
             public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                 Table source = (Table) valueChangeEvent.getProperty();
                 BeanItemContainer<MethodView> container = (BeanItemContainer<MethodView>) source.getContainerDataSource();

                 // disable previously selected items
                 for (MethodView beanItem : container.getItemIds()) {
                     beanItem.setActive(false);
                 }

                 // set current selection to true
                 for (MethodView selectedItem : (Collection <MethodView>) source.getValue() ) {
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
			private static final long serialVersionUID = -8853235163238131008L;

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
                  	if (((MethodView)itemIdOver).isEnabled()){
                  		
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

     @SuppressWarnings("unchecked")
	private void moveSelectedItems(Table source, Table target) {
         List<Object> sourceItems = new LinkedList<Object>((Collection<Object>) source.getValue());
         ListIterator<Object> sourceItemsIterator = sourceItems.listIterator(sourceItems.size());

         BeanItemContainer<MethodView> targetDataContainer = (BeanItemContainer<MethodView>) target.getContainerDataSource();
         Container sourceDataContainer = source.getContainerDataSource();

         
         int counter = 0;
         while (sourceItemsIterator.hasPrevious()) {
             MethodView itemId = (MethodView) sourceItemsIterator.previous();
             itemId.setActive(false);
             if (source.getData().toString().equals(AVAILABLE)){
            	 targetDataContainer.addItemAt(0, itemId);
            	 if (counter < 100) {
                     target.unselect(itemId);
                 }
            	 
            	 target.setValue(null);
            	 
                 //refresh the fav location table
                 updateNoOfEntries(favTotalEntriesLabel, target);
                 updateSelectedNoOfEntries(favSelectedEntriesLabel, target);
             }else{
            	 sourceDataContainer.removeItem(itemId);
            	 source.setValue(null);
            	 
                 //refresh the fav location table
                 updateNoOfEntries(favTotalEntriesLabel, source);
                 updateSelectedNoOfEntries(favSelectedEntriesLabel, source);
             }
             counter++;
         }
         
         if (counter >= 100 && target.getData().toString().equals(FAVORITES)){
        	 target.setValue(null);
         }
         
         if (source.getData().toString().equals(AVAILABLE)){
        	 source.setValue(null);
         }
         
         source.refreshRowCache();
         target.refreshRowCache();
     }

    /**
     * Use this method to return the list of methods in Favorite Methods table, you might have to manually
     * convert the MethodView bean to Middleware's Method bean.
     * @return
     */
    public Collection<Method> getFavoriteMethods() {
        return presenter.convertTo(favoritesTableContainer.getItemIds());

    }

     private void initializeValues() {

             /* INITIALIZE FILTER CONTROLS DATA */
         typeFilter.addItem("");
         typeFilter.setItemCaption("","All Generation Advancement Types");

         for (String[] methodType : METHOD_TYPES) {
             typeFilter.addItem(methodType[0]);
             typeFilter.setItemCaption(methodType[0], methodType[1]);
         }

         typeFilter.select("");

         groupFilter.addItem("");
         groupFilter.setItemCaption("", "All Crop Reproductive Systems");
         for(String[] methodGroup : METHOD_GROUPS) {
             groupFilter.addItem(methodGroup[0]);
             groupFilter.setItemCaption(methodGroup[0],methodGroup[1]);
         }
         groupFilter.select("");


             /* INITIALIZE TABLE DATA */
         favoritesTableContainer = new BeanItemContainer<MethodView>(MethodView.class,presenter.getSavedProgramMethods());
         setAvailableTableContainer(new BeanItemContainer<MethodView>(MethodView.class,presenter.getFilteredResults(groupFilter.getValue().toString(),typeFilter.getValue().toString(),"")));

         resultCountLbl.setValue("Result: " + getAvailableTableContainer().size());

         availableTable.setContainerDataSource(getAvailableTableContainer());
         updateNoOfEntries(availTotalEntriesLabel, availableTable);
         
         favoritesTable.setContainerDataSource(favoritesTableContainer);
         updateNoOfEntries(favTotalEntriesLabel, favoritesTable);
         
         /* SETUP TABLE FIELDS */
         this.setupTableFields(availableTable);
         this.setupTableFields(favoritesTable);
         
         retrieveMethodClasses();
     }

    public Map<Integer,String> retrieveMethodClasses() {
    	 //set lookup map for classes
    	 if(classMap==null || classMap.isEmpty()) {
    		 classMap = presenter.getMethodClasses();
    	 }
    	 return classMap;
	}

	private void setupTableFields(Table table) {
         table.setVisibleColumns(TABLE_COLUMNS.keySet().toArray());
         table.setColumnHeaders(TABLE_COLUMNS.values().toArray(new String[]{}));

         for (String col : TABLE_COLUMN_SIZES.keySet()) {
             table.setColumnWidth(col,TABLE_COLUMN_SIZES.get(col));

             if (TABLE_COLUMN_SIZES.get(col) < 75) {
                 table.setColumnAlignment(col, Table.ALIGN_CENTER);
             }

         }

         table.setColumnExpandRatio(TABLE_COLUMNS.keySet().toArray()[2],1.0F);
     }

     private void initializeLayout() {
         root = new VerticalLayout();
         root.setSpacing(false);
         root.setMargin(new Layout.MarginInfo(false,true,true,true));

         final Label availableMethodsTitle = new Label(messageSource.getMessage(Message.AVAILABLE_METHODS));
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
         root.addComponent(buildLocationTableLabels(availTotalEntriesLabel, availSelectedEntriesLabel));
         root.addComponent(availableTable);
         root.addComponent(availableTableBar);
         root.addComponent(buildFavoriteTableTitle());
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

     private Component buildFavoriteTableTitle() {
         final HorizontalLayout layout = new HorizontalLayout();
         layout.setWidth("100%");
         layout.setMargin(true, false, false, false);

         final Label favoriteMethodsTitle = new Label(messageSource.getMessage(Message.FAVORITE_PROGRAM_METHODS));
         favoriteMethodsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

         layout.addComponent(favoriteMethodsTitle);

         if (!cropOnly) {
        	 layout.addComponent(saveBtn);
         }

         layout.setExpandRatio(favoriteMethodsTitle,1.0F);

         return layout;
     }

     private Component buildFilterForm() {
         groupFilter.setWidth("220px");
         typeFilter.setWidth("245px");

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
         field2.addComponent(typeFilter);


         final HorizontalLayout field3 = new HorizontalLayout();
         field3.addStyleName(FIELD);
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

         return container;
     }

     private Component buildPageTitle() {
         final VerticalLayout layout = new VerticalLayout();
         layout.setMargin(new Layout.MarginInfo(false,false,true,false));
         layout.setWidth("100%");

         final HorizontalLayout titleContainer = new HorizontalLayout();
         titleContainer.setSizeUndefined();
         titleContainer.setWidth("100%");
         titleContainer.setMargin(true, false, false, false);

         final Label heading = new Label("<span class='bms-methods' style='color: #B8D432; font-size: 23px'></span>&nbsp;Breeding Methods",Label.CONTENT_XHTML);
         heading.setStyleName(Bootstrap.Typography.H4.styleName());

         titleContainer.addComponent(heading);

         if (!cropOnly) {
             titleContainer.addComponent(addNewMethodsBtn);
             titleContainer.setComponentAlignment(addNewMethodsBtn, Alignment.MIDDLE_RIGHT);
         }

         String content = "To choose Favorite Breeding Methods for your program, select entries from the Available Breeding Methods table at the top and drag them onto the lower table.";

         if (!cropOnly) {
             content += " You can also add any new methods that you need for managing your program.";
         }

         final Label headingDesc = new Label(content);

         layout.addComponent(titleContainer);
         layout.addComponent(headingDesc);

         return layout;
     }

     public void addRow(MethodView item,boolean atAvailableTable,Integer index) {
         if (index != null) {
             if (atAvailableTable) {
                 getAvailableTableContainer().addItemAt(index,item);

             } else {
            	 getAvailableTableContainer().addItemAt(index,item);
                 favoritesTableContainer.addItemAt(index,item);
             }
         } else {
             if (atAvailableTable) {
                 getAvailableTableContainer().addItem(item);

             } else {
            	 getAvailableTableContainer().addItem(item);
                 favoritesTableContainer.addItem(item);
             }
         }
         updateNoOfEntries();
     }

    private void updateNoOfEntries() {
    	 updateNoOfEntries(favTotalEntriesLabel,favoritesTable);
         updateNoOfEntries(availTotalEntriesLabel,availableTable);
	}

    private void initializeActions() {

         editMethodListener = new Button.ClickListener() {
			private static final long serialVersionUID = -6938448455072630697L;

			@Override
             public void buttonClick(Button.ClickEvent event) {
                 event.getComponent().getWindow().addWindow(new EditBreedingMethodsWindow(ProgramMethodsView.this.presenter,(MethodView)event.getButton().getData()));
             }
         };

         addNewMethodsBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 6467414813762353127L;

			@Override
             public void buttonClick(Button.ClickEvent event) {
                 event.getComponent().getWindow().addWindow(new AddBreedingMethodsWindow(ProgramMethodsView.this));
             }
         });

         Property.ValueChangeListener filterAction = new Property.ValueChangeListener() {
			private static final long serialVersionUID = 8914267618640094463L;

			@Override
             public void valueChange(Property.ValueChangeEvent event) {
                 getAvailableTableContainer().removeAllItems();
                 getAvailableTableContainer().addAll(presenter.getFilteredResults(groupFilter.getValue().toString(), typeFilter.getValue().toString(), searchField.getValue().toString()));

                 resultCountLbl.setValue("Results: " + availableTable.getContainerDataSource().getItemIds().size() + " items");
                 updateNoOfEntries(availTotalEntriesLabel, availableTable);
                 updateSelectedNoOfEntries(availSelectedEntriesLabel, availableTable);
             }
         };

         searchField.addListener(filterAction);
         groupFilter.addListener(filterAction);
         typeFilter.addListener(filterAction);

         availableSelectAll.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -2842000142630845841L;

			@Override
             public void buttonClick(Button.ClickEvent clickEvent) {

                 if ((Boolean) ((CheckBox)clickEvent.getComponent()).getValue()) {
                     availableTable.setValue(availableTable.getItemIds());
                 } else {
                     availableTable.setValue(null);
                 }


             }
         });

         favoriteSelectAll.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 2545400532783269974L;

			@Override
             public void buttonClick(Button.ClickEvent clickEvent) {
                 if ((Boolean) ((CheckBox)clickEvent.getComponent()).getValue()) {
                     favoritesTable.setValue(favoritesTable.getItemIds());
                 } else {
                     favoritesTable.setValue(null);
                 }

             }
         });

         addToFavoriteBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 8741702112016745513L;

			@Override
             public void buttonClick(Button.ClickEvent clickEvent) {
                 moveSelectedItems(availableTable,favoritesTable);
                 availableSelectAll.setValue(false);
             }
         });

         removeToFavoriteBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -7252226977128582313L;

			@Override
             public void buttonClick(Button.ClickEvent clickEvent) {
                 moveSelectedItems(favoritesTable,availableTable);
                 favoriteSelectAll.setValue(false);
             }
         });

         saveBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1484296798437173855L;

			@Override
             public void buttonClick(Button.ClickEvent event) {
                 if (ProgramMethodsView.this.presenter.saveFavoriteBreedingMethod(favoritesTableContainer.getItemIds())) {
                     MessageNotifier.showMessage(event.getComponent().getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.METHODS_SUCCESSFULLY_CONFIGURED));
                 }
             }
         });
         
         
         availableTable.addActionHandler(new Handler(){
			private static final long serialVersionUID = 4185416256388693137L;

			@Override
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { copyBreedingMethodAction };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action == copyBreedingMethodAction){
					availableTable.getParent().getWindow().addWindow(new AddBreedingMethodsWindow(ProgramMethodsView.this, ((MethodView) target).copyMethodView()));
				}
				
			}
        	 
         });
         
         favoritesTable.addActionHandler(new Handler(){
			private static final long serialVersionUID = 6635300830598766541L;

			@Override
 			public Action[] getActions(Object target, Object sender) {
 				return new Action[] { copyBreedingMethodAction };
 			}

 			@Override
 			public void handleAction(Action action, Object sender, Object target) {
 				if (action == copyBreedingMethodAction){
 					favoritesTable.getParent().getWindow().addWindow(new AddBreedingMethodsWindow(ProgramMethodsView.this, ((MethodView) target).copyMethodView()));
 				}
 				
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
     
    protected void refreshTable() {
        // do table repaint
        availableTable.requestRepaint();
        availableTable.refreshRowCache();

        favoritesTable.requestRepaint();
        favoritesTable.refreshRowCache();

    }

	public BeanItemContainer<MethodView> getAvailableTableContainer() {
		return availableTableContainer;
	}

	public void setAvailableTableContainer(BeanItemContainer<MethodView> availableTableContainer) {
		this.availableTableContainer = availableTableContainer;
	}

	public SimpleResourceBundleMessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public Table getAvailableTable() {
		return availableTable;
	}

	public void setAvailableTable(Table availableTable) {
		this.availableTable = availableTable;
	}

	public Table getFavoritesTable() {
		return favoritesTable;
	}

	public void setFavoritesTable(Table favoritesTable) {
		this.favoritesTable = favoritesTable;
	}

	public BeanItemContainer<MethodView> getFavoritesTableContainer() {
		return favoritesTableContainer;
	}

	public void setFavoritesTableContainer(
			BeanItemContainer<MethodView> favoritesTableContainer) {
		this.favoritesTableContainer = favoritesTableContainer;
	}

	public Label getAvailTotalEntriesLabel() {
		return availTotalEntriesLabel;
	}

	public void setAvailTotalEntriesLabel(Label availTotalEntriesLabel) {
		this.availTotalEntriesLabel = availTotalEntriesLabel;
	}

	public Label getFavTotalEntriesLabel() {
		return favTotalEntriesLabel;
	}

	public void setFavTotalEntriesLabel(Label favTotalEntriesLabel) {
		this.favTotalEntriesLabel = favTotalEntriesLabel;
	}
	
	
	
	

 }