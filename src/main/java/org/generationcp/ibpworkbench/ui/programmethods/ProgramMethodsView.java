package org.generationcp.ibpworkbench.ui.programmethods;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.*;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IWorkbenchSession;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenProgramMethodsAction;
import org.generationcp.ibpworkbench.ui.common.IContainerFittable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.lang.reflect.Field;
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
    public final static String[][] methodTypes = {{"GEN","Generative"},{"DER","Derivative"},{"MAN","Maintenance"}};
    public final static String[][] methodGroups = {{"S","Self Fertilizing"},{"O","Cross Pollinating"},{"C","Clonally Propagating"},{"G","All System"}};

    public final static Map<String,Integer> columnWidthsMap;

    static {
        columnWidthsMap = new HashMap<String, Integer>();
        columnWidthsMap.put("select",20);
        columnWidthsMap.put("mname",210);
        columnWidthsMap.put("mgrp",45);
        columnWidthsMap.put("mcode",40);
        columnWidthsMap.put("mtype",40);
        columnWidthsMap.put("fmdate",70);
    }

    private ProgramMethodsPresenter presenter;
    private Button addNewMethodBtn;
    private Select groupFilter;
    private Select typeFilter;
    private TextField searchField;
    private Label resultCountLbl;
    private Table availableMethodsTable;
    private Table selectedMethodsTable;
    private Button saveBtn;
    private Button cancelBtn;
    private java.lang.reflect.Field[] fields;

    private ArrayList<Integer> selectedMethodIds = new ArrayList<Integer>();

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    private VerticalLayout root;
    private Table availTbl;
    private Table selTbl;
    private CheckBox selectAllAvailable;
    private Button addToFavoriteBtn;
    private CheckBox selectAllFav;
    private Button removeToFavoriteBtn;

    private Map<Integer,CheckBox> allCheckBoxMap = new HashMap<Integer, CheckBox>();
    private Map<Integer,Integer> prevSelectedItems = new HashMap<Integer,Integer>();

    public ProgramMethodsView(Project project) {
        presenter = new ProgramMethodsPresenter(this,project);
    }

    @Override
    public void attach() {
        super.attach();

        presenter.onAttachInitialize((IWorkbenchSession) this.getApplication());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
         try {
            initializeComponents();

             // Preinit components
             typeFilter.addItem("");
             typeFilter.setItemCaption("","All Method Types");
             for (String[] methodType : methodTypes) {
                 typeFilter.addItem(methodType[0]);
                 typeFilter.setItemCaption(methodType[0], methodType[1]);
             }

             typeFilter.setNullSelectionItemId("Method Type");
             typeFilter.setNullSelectionAllowed(false);
             typeFilter.select(methodTypes[0][0]);

             groupFilter.addItem("");
             groupFilter.setItemCaption("", "All Method Groups");
             for(String[] methodGroup : methodGroups) {
                 groupFilter.addItem(methodGroup[0]);
                 groupFilter.setItemCaption(methodGroup[0],methodGroup[1]);
             }
             groupFilter.setNullSelectionItemId("Method Group");
             groupFilter.select("");
             groupFilter.setNullSelectionAllowed(false);


             initializeActions();
            initializeValues();
         } catch (MiddlewareQueryException e) {
             e.printStackTrace();
         }
    }

    private void initializeComponents() {
        root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(new Layout.MarginInfo(false,true,true,true));

        final HorizontalLayout titleContainer = new HorizontalLayout();
        final Label heading = new Label("<span class='glyphicon glyphicon-th-large'></span>&nbsp;Breeding Methods",Label.CONTENT_XHTML);
        final Label headingDesc = new Label("To choose Favorite Breeding Methods for your program, select entries from the Available Breeding Methods table at the top and drag them onto the lower table. You can also add any new methods that you need for managing your program.");

        heading.setStyleName(Bootstrap.Typography.H4.styleName());

        final Label availableMethodsTitle = new Label("Available Breeding Methods");
        availableMethodsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

        addNewMethodBtn = new Button("Add new Method");
        addNewMethodBtn.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");

        titleContainer.addComponent(heading);
        titleContainer.addComponent(addNewMethodBtn);

        titleContainer.setComponentAlignment(addNewMethodBtn, Alignment.MIDDLE_RIGHT);
        titleContainer.setSizeUndefined();
        titleContainer.setWidth("100%");
        titleContainer.setMargin(true, true, true, false);	// move this to css

        final HorizontalLayout selectedMethodTitleContainer = new HorizontalLayout();
        selectedMethodTitleContainer.setMargin(false,true,false,false);

        final Label selectedMethodsTitle = new Label(messageSource.getMessage(Message.FAVORITE_PROGRAM_METHODS));
        selectedMethodsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

        selectedMethodTitleContainer.addComponent(selectedMethodsTitle);

        availTbl = this.buildAvailableMethodsTable();
        selTbl = this.buildSelectedMethodsTable();

        availTbl.setHeight("250px");
        selTbl.setHeight("250px");

        /* AVAILABLE SELECT ALL BAR */
        final CssLayout availableSelectAllContainer = new CssLayout();
        availableSelectAllContainer.addStyleName("loc-filter-bar");
        availableSelectAllContainer.setMargin(new Layout.MarginInfo(false,false,true,false));
        availableSelectAllContainer.setSizeUndefined();
        availableSelectAllContainer.setWidth("100%");

        selectAllAvailable = new CheckBox();
        selectAllAvailable.setImmediate(true);
        final Label selectAllLbl = new Label("&nbsp;&nbsp;Select All&nbsp;&nbsp;&nbsp;&nbsp;",Label.CONTENT_XHTML);
        addToFavoriteBtn = new Button("Add selected to favorite breeding methods");
        addToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

        final HorizontalLayout selectAllContainer1 = new HorizontalLayout();
        selectAllContainer1.addStyleName("align-select-all");
        selectAllContainer1.addComponent(selectAllAvailable);
        selectAllContainer1.addComponent(selectAllLbl);

        availableSelectAllContainer.addComponent(selectAllContainer1);
        availableSelectAllContainer.addComponent(addToFavoriteBtn);

        /* FAVORITES SELECT ALL BAR */
        final CssLayout favoriteSelectAllContainer = new CssLayout();
        favoriteSelectAllContainer.addStyleName("loc-filter-bar");
        favoriteSelectAllContainer.setMargin(new Layout.MarginInfo(false,false,true,false));
        favoriteSelectAllContainer.setSizeUndefined();
        favoriteSelectAllContainer.setWidth("100%");


        selectAllFav = new CheckBox();
        selectAllFav.setImmediate(true);
        final Label selectAllFavLbl = new Label("&nbsp;&nbsp;Select All&nbsp;&nbsp;&nbsp;&nbsp;",Label.CONTENT_XHTML);
        removeToFavoriteBtn = new Button("Remove selected favorite breeding methods");
        removeToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

        final HorizontalLayout selectAllContainer2 = new HorizontalLayout();
        selectAllContainer2.addStyleName("align-select-all");
        selectAllContainer2.addComponent(selectAllFav);
        selectAllContainer2.addComponent(selectAllFavLbl);

        favoriteSelectAllContainer.addComponent(selectAllContainer2);
        favoriteSelectAllContainer.addComponent(removeToFavoriteBtn);

        /* BUILD ALL COMPONENTS TO THE ROOT/PAGE */
        root.addComponent(titleContainer);
        root.addComponent(headingDesc);
        root.addComponent(availableMethodsTitle);
        root.addComponent(this.buildMethodFilterForm());
        root.addComponent(availTbl);
        root.addComponent(availableSelectAllContainer);
        root.addComponent(selectedMethodTitleContainer);
        root.addComponent(selTbl);
        root.addComponent(favoriteSelectAllContainer);
        root.addComponent(this.buildActionButtons());

        this.setCompositionRoot(root);
    }

    @Override
    public void fitToContainer(final Window parentWindow) {
        availTbl.setHeight("100%");
        selTbl.setHeight("100%");

        root.setExpandRatio(availTbl,1.0f);
        root.setExpandRatio(selTbl,1.0f);
        root.setSizeFull();

        this.setSizeFull();

        // special actions added to save and cancel btns
        final Button.ClickListener execCloseFrameJS =  new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                parentWindow.executeJavaScript("window.parent.closeMethodFrame();");
            }
        };

        saveBtn.addListener(execCloseFrameJS);
        cancelBtn.setCaption("Reset");
        cancelBtn.addListener(execCloseFrameJS);

    }

    private Component buildActionButtons() {
        final HorizontalLayout root = new HorizontalLayout();
        saveBtn = new Button("Save");
        saveBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

        cancelBtn = new Button("Cancel");

        final HorizontalLayout innerContainer = new HorizontalLayout();
        innerContainer.setSpacing(true);
        innerContainer.setSizeUndefined();

        innerContainer.addComponent(cancelBtn);
        innerContainer.addComponent(saveBtn);

        root.addComponent(innerContainer);
        root.setComponentAlignment(innerContainer,Alignment.MIDDLE_CENTER);

        root.setSpacing(true);
        root.setMargin(new Layout.MarginInfo(false,true,true,true));
        root.setWidth("100%");
        return root;
    }

    private Component buildMethodFilterForm() {
        final CssLayout container = new CssLayout();
        container.addStyleName("loc-filter-bar");
        container.setSizeUndefined();
        container.setWidth("100%");

        container.setMargin(new Layout.MarginInfo(true,false,true,false));

        groupFilter = new Select();
        groupFilter.setImmediate(true);

        typeFilter = new Select();
        typeFilter.setImmediate(true);

        searchField = new TextField();
        searchField.setImmediate(true);
        searchField.setNullRepresentation("");


        final Label spacer = new Label();
        spacer.setWidth("100%");

        final Label searchLbl = new Label("<b>Search for:&nbsp;&nbsp;&nbsp;</b>",Label.CONTENT_XHTML);

        searchLbl.setSizeUndefined();

        searchLbl.setStyleName("loc-filterlbl");

        final HorizontalLayout field1 = new HorizontalLayout();
        field1.addStyleName("field");
        field1.setSizeUndefined();
        field1.setSpacing(true);
        field1.addComponent(searchLbl);
        field1.addComponent(searchField);

        HorizontalLayout filterContainer = new HorizontalLayout();
        filterContainer.setSpacing(true);
        filterContainer.setStyleName("pull-right");
        filterContainer.setSizeUndefined();

        final HorizontalLayout field2 = new HorizontalLayout();
        field2.addStyleName("field");
        field2.setSpacing(true);
        field2.setSizeUndefined();
        field2.addComponent(groupFilter);


        final HorizontalLayout field3 = new HorizontalLayout();
        field3.addStyleName("field");
        field3.setSpacing(true);
        field3.setSizeUndefined();
        field3.addComponent(typeFilter);

        filterContainer.addComponent(new Label("<b>Filter By:&nbsp;&nbsp;&nbsp;</b>",Label.CONTENT_XHTML));
        filterContainer.addComponent(field2);
        filterContainer.addComponent(field3);

        container.addComponent(field1);
        container.addComponent(filterContainer);

        resultCountLbl = new Label("");
        resultCountLbl.setStyleName("loc-resultcnt");

        return container;
    }

    private Table buildCustomTable(Map<String,Integer> columnWidthMap) {
        final Table table = new Table() {
            private static final long serialVersionUID = -14085524627550290L;

            @Override
            public String getColumnAlignment(Object propertyId) {
                Class<?> t = getContainerDataSource().getType(propertyId);

                if (t == Integer.class || t == Long.class || t == Double.class || t == Float.class) {
                    return Table.ALIGN_RIGHT;
                } if (t == Label.class || t == String.class)
                    return Table.ALIGN_LEFT;
                else
                    return Table.ALIGN_CENTER;
            }
        };

        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        table.setDragMode(Table.TableDragMode.MULTIROW);
        table.setStyleName("loc-table");
        table.setHeight("250px");

        table.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {
                for (Integer mId : prevSelectedItems.keySet()) {
                    if (itemClickEvent.getComponent() != availableMethodsTable) {
                        availableMethodsTable.unselect(prevSelectedItems.get(mId));
                    } else {
                        selectedMethodsTable.unselect(prevSelectedItems.get(mId));
                    }

                    allCheckBoxMap.get(mId).setValue(false);
                }
                prevSelectedItems.clear();
            }
        });

        table.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                Set<Integer> difference = new HashSet<Integer>(prevSelectedItems.keySet());


                for (Object itemId : ((Collection)table.getValue())) {
                    if (table.getItem(itemId) == null)
                        continue;

                    Integer mId = (Integer) table.getItem(itemId).getItemProperty("mid").getValue();

                    if (!prevSelectedItems.containsKey(mId))
                        prevSelectedItems.put(mId, (Integer) itemId);

                    allCheckBoxMap.get(mId).setValue(true);

                    difference.remove(mId);
                }

                for (Integer mId : difference) {
                    allCheckBoxMap.get(mId).setValue(false);
                }
            }
        });


        table.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent dragAndDropEvent) {
                // criteria verify that this is safe
                final DataBoundTransferable t = (DataBoundTransferable) dragAndDropEvent
                        .getTransferable();

                if (t.getSourceComponent() == dragAndDropEvent.getTargetDetails().getTarget())
                    return;

                LinkedList<Integer> sourceItems = new LinkedList<Integer>(((Collection) ((Table)t.getSourceComponent()).getValue()));

                ListIterator<Integer> sourceItemsIterator = sourceItems.listIterator(sourceItems.size());
                Table destinationTbl = (Table) dragAndDropEvent.getTargetDetails().getTarget();

                while (sourceItemsIterator.hasPrevious()) {
                    Integer itemId =  sourceItemsIterator.previous();
                    Item item = t.getSourceContainer().getItem(itemId);

                    if (item == null)
                        continue;

                    try {
                        addRow(presenter.getMethodByID((Integer)item.getItemProperty("mid").getValue()),destinationTbl,0);
                        t.getSourceContainer().removeItem(itemId);

                    } catch (IllegalAccessException e) {
                        // cannot move
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AbstractSelect.AcceptItem.ALL;
            }
        });


        table.setCellStyleGenerator(new Table.CellStyleGenerator() {

            private static final long serialVersionUID = -2457435122226402123L;

            public String getStyle(Object itemId, Object propertyId) {

                if (propertyId != null && propertyId.toString().equals("mname"))
                    return propertyId.toString();

                return null;
            }
        });


        //table.setColumnWidth("locationName",310);
        //table.setColumnWidth("locationAbbreviation",130);
        //table.setColumnWidth("removeBtn",60);
        //table.setColumnWidth("selectBtn",60);

        for (String columnName : columnWidthMap.keySet()) {
            table.setColumnWidth(columnName,columnWidthMap.get(columnName));
        }

        return table;
    }

    private Table buildAvailableMethodsTable() {
        availableMethodsTable = buildCustomTable(columnWidthsMap);
        availableMethodsTable.setWidth("100%");

        return availableMethodsTable;
    }

    private Table buildSelectedMethodsTable() {
        selectedMethodsTable = buildCustomTable(columnWidthsMap);
        selectedMethodsTable.setWidth("100%");

        return selectedMethodsTable;
    }

    private void initializeActions() {

        addNewMethodBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                   //ProgramMethodsView.this.presenter.doAddMethodAction();
                   event.getComponent().getWindow().addWindow(new AddBreedingMethodsWindow(ProgramMethodsView.this));

            }
        });

        Property.ValueChangeListener filterAction = new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                List<Method> results = presenter.getFilteredResults(groupFilter.getValue().toString(),typeFilter.getValue().toString(),searchField.getValue().toString());
                availableMethodsTable.getContainerDataSource().removeAllItems();

                try {
                    ProgramMethodsView.this.generateRows(results,true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                resultCountLbl.setValue("Results: " + availableMethodsTable.getContainerDataSource().getItemIds().size() + " items");

            }
        };

        searchField.addListener(filterAction);
        groupFilter.addListener(filterAction);
        typeFilter.addListener(filterAction);

        /*
        * private CheckBox selectAllAvailable;
            private Button addToFavoriteBtn;
            private CheckBox selectAllFav;
            private Button removeToFavoriteBtn;

        * */
        selectAllAvailable.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {

                if (true ==valueChangeEvent.getProperty().getValue())
                    availableMethodsTable.setValue(availableMethodsTable.getItemIds());
                else {
                    availableMethodsTable.setValue(null);
                    prevSelectedItems.clear();
                }

                //selectAllFav.setValue(false);

            }
        });

        selectAllFav.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {

                if (true ==valueChangeEvent.getProperty().getValue())
                    selectedMethodsTable.setValue(selectedMethodsTable.getItemIds());
                else {
                    selectedMethodsTable.setValue(null);
                    prevSelectedItems.clear();

                }

                //selectAllAvailable.setValue(false);
            }
        });

        addToFavoriteBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                moveSelectedItems(availableMethodsTable,selectedMethodsTable);
            }
        });

        removeToFavoriteBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                moveSelectedItems(selectedMethodsTable,availableMethodsTable);
            }
        });

        saveBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (ProgramMethodsView.this.presenter.saveProgramMethod(selectedMethodIds)) {
                    MessageNotifier.showMessage(event.getComponent().getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.METHODS_SUCCESSFULLY_CONFIGURED));
                } else {    // should never happen

                }


            }
        });

        cancelBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = -4479216826096826464L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                IWorkbenchSession appSession = (IWorkbenchSession) event.getComponent().getApplication();

                (new OpenProgramMethodsAction(appSession.getSessionData().getLastOpenedProject(), appSession.getSessionData().getUserData())).buttonClick(event);
            }
        });

    }

    private void initializeValues() throws MiddlewareQueryException {
        /* INITIALIZE TABLE DATA */

        selectedMethodsTable.setContainerDataSource(new IndexedContainer());
        availableMethodsTable.setContainerDataSource(new IndexedContainer());


        IndexedContainer itemContainer = (IndexedContainer) selectedMethodsTable.getContainerDataSource();
        IndexedContainer itemContainer2 = (IndexedContainer) availableMethodsTable.getContainerDataSource();

        fields = Method.class.getDeclaredFields();

        HashMap<String,String> visibleFieldsMap = new LinkedHashMap<String, String>();
        visibleFieldsMap.put("select","<span class='glyphicon glyphicon-ok'></span>");
        visibleFieldsMap.put("mname","Method Name");
        visibleFieldsMap.put("fmdesc","Description");
        visibleFieldsMap.put("mgrp","Group");
        visibleFieldsMap.put("mcode","Code");
        visibleFieldsMap.put("mtype","Type");
        visibleFieldsMap.put("fmdate","Date");

       
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getName().equals("serialVersionUID"))
                continue;

            itemContainer.addContainerProperty(field.getName(),field.getType(),null);
            itemContainer2.addContainerProperty(field.getName(),field.getType(),null);

        }

        availableMethodsTable.getContainerDataSource().addContainerProperty("select", CheckBox.class, false);
        selectedMethodsTable.getContainerDataSource().addContainerProperty("select", CheckBox.class, false);

        availableMethodsTable.getContainerDataSource().addContainerProperty("fmdesc", Label.class, null);
        selectedMethodsTable.getContainerDataSource().addContainerProperty("fmdesc", Label.class, null);

        availableMethodsTable.getContainerDataSource().addContainerProperty("fmdate", String.class, null);
        selectedMethodsTable.getContainerDataSource().addContainerProperty("fmdate", String.class, null);

        try {
            // add all items to selected table first
            this.generateRows(presenter.getSavedProgramMethods(), false);

            // add all items for available locations table
            this.generateRows(presenter.getFilteredResults(groupFilter.getValue().toString(), typeFilter.getValue().toString(), ""), true);

            resultCountLbl.setValue("Result: " + itemContainer.getItemIds().size() + " items");

        } catch (Exception e) {
            e.printStackTrace();
        }

        availableMethodsTable.setVisibleColumns(visibleFieldsMap.keySet().toArray(new String[visibleFieldsMap.size()]));
        availableMethodsTable.setColumnHeaders(visibleFieldsMap.values().toArray(new String[visibleFieldsMap.size()]));

        selectedMethodsTable.setVisibleColumns(visibleFieldsMap.keySet().toArray(new String[visibleFieldsMap.size()]));
        selectedMethodsTable.setColumnHeaders(visibleFieldsMap.values().toArray(new String[visibleFieldsMap.size()]));

    }

    private void generateRows(List<Method> methodList, boolean isAvailableTable) throws IllegalAccessException {
           for (Method method : methodList) {
               if (selectedMethodIds.contains(method.getMid()))
                   continue;

               addRow(method,isAvailableTable,null);
           }
    }

    public void addRow(Method method, final Table isAvailableTable,Integer index) throws IllegalAccessException {
        if (isAvailableTable == availableMethodsTable)
            addRow(method,true,index);
        else
            addRow(method,false,index);

    }

    public void addRow(Method method, final boolean isAvailableTable,Integer index) throws IllegalAccessException {
        Object itemId = null;
        IndexedContainer itemContainer = null;

        if (isAvailableTable)
            itemContainer = (IndexedContainer) availableMethodsTable.getContainerDataSource();
        else
            itemContainer = (IndexedContainer) selectedMethodsTable.getContainerDataSource();

        if (index != null)
            itemId = itemContainer.addItemAt(0);
        else
            itemId = itemContainer.addItem();

        Item newItem = itemContainer.getItem(itemId);

        for (Field field : fields) {
            if (field.getName().equals("serialVersionUID"))
                continue;

            if (field.getName().equals("mdesc")) {
                String val = field.get(method).toString();
                Label l = new Label();
                l.setDescription(val);
                if (val.length() > 90) {
                    val = val.substring(0,90 -3).trim().concat("...");
                }
                l.setValue(val);

                newItem.getItemProperty("fmdesc").setValue(l);
            }

            if (field.getName().equals("mdate")) {
                String date = field.get(method).toString();

                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                DateFormat newDf = new SimpleDateFormat("MM/dd/yyyy");

                if (date.length() > 1) {
                    try {
                        date = newDf.format(df.parse(date));
                    } catch (ParseException e) {
                        date = "N/A";
                        //e.printStackTrace();
                    }
                } else
                    date = "N/A";

                newItem.getItemProperty("fmdate").setValue(date);
            }

            newItem.getItemProperty(field.getName()).setValue(field.get(method));
        }

        Button btn = new Button();
        btn.setWidth("16px");
        btn.setHeight("16px");


        if (!isAvailableTable) {
            if (!selectedMethodIds.contains(method.getMid()))
                selectedMethodIds.add(method.getMid());
        }

        final CheckBox select = new CheckBox();
        select.setImmediate(true);
        final Object finalItemId = itemId;

        select.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                for (Integer mId : prevSelectedItems.keySet()) {
                    if (select.getParent() != availableMethodsTable) {
                        availableMethodsTable.unselect(prevSelectedItems.get(mId));
                    } else {
                        selectedMethodsTable.unselect(prevSelectedItems.get(mId));
                    }

                    allCheckBoxMap.get(mId).setValue(false);
                }
                prevSelectedItems.clear();

                if (true == ((CheckBox)clickEvent.getComponent()).getValue()) {
                    if (isAvailableTable)  {
                        availableMethodsTable.select(finalItemId);
                    }
                    else {
                        selectedMethodsTable.select(finalItemId);

                    }
                } else {
                    if (isAvailableTable)
                        availableMethodsTable.unselect(finalItemId);
                    else
                        selectedMethodsTable.unselect(finalItemId);

                }


            }
        });

        newItem.getItemProperty("select").setValue(select);

        Integer mId = (Integer) newItem.getItemProperty("mid").getValue();

        if (allCheckBoxMap.containsKey(mId))
            allCheckBoxMap.remove(mId);

        allCheckBoxMap.put(mId,select);


        btn.setData(itemId);
    }

    /** TODO: The Following methods are only for compatibility to old Classes depentent on old Project Breeding Methods Panel UI **/
    public ManagerFactory getManagerFactory() {
        return this.presenter.getManagerFactory();
    }

    public void moveSelectedItems(Table source,Table destination) {
        LinkedList<Integer> sourceItems = new LinkedList<Integer>(((Collection) source.getValue()));
        ListIterator<Integer> sourceItemsIterator = sourceItems.listIterator(sourceItems.size());


        while (sourceItemsIterator.hasPrevious()) {
            Integer itemId =  sourceItemsIterator.previous();
            Item item = source.getItem(itemId);

            try {
                addRow(presenter.getMethodByID((Integer)item.getItemProperty("mid").getValue()),destination,0);
                source.removeItem(itemId);

            } catch (IllegalAccessException e) {
                // cannot move
                e.printStackTrace();
            }

        }
    }
}