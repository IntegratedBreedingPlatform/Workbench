package org.generationcp.ibpworkbench.ui.projectmethods;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
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
public class ProjectMethodsView extends CustomComponent implements InitializingBean {
    public final static String[][] methodTypes = {{"GEN","Generative"},{"DER","Derivative"},{"MAN","Maintenance"}};
    public final static String[][] methodGroups = {{"S","Self Fertilizing"},{"O","Cross Pollinating"},{"C","Clonally Propagating"},{"G","All System"}};

    public final static Map<String,Integer> columnWidthsMap;

    static {
        columnWidthsMap = new HashMap<String, Integer>();
        columnWidthsMap.put("mname",210);
        columnWidthsMap.put("mgrp",128);
        columnWidthsMap.put("mcode",120);
        columnWidthsMap.put("mtype",115);
        columnWidthsMap.put("fmdate",115);
        columnWidthsMap.put("selectBtn",60);
        columnWidthsMap.put("removeBtn",60);
    }

    private ProjectMethodsPresenter presenter;
    private Project project;
    private Button addNewMethodBtn;
    private Select groupFilter;
    private Select typeFilter;
    private TextField searchField;
    private Label resultCountLbl;
    private Table availableMethodsTable;
    private Table selectedMethodsTable;
    private Button saveBtn;
    private Button cancelBtn;
    private Button.ClickListener onAddToSelectedMethodAction;
    private Button.ClickListener onRemoveSelectedMethodAction;
    private java.lang.reflect.Field[] fields;

    private ArrayList<Integer> selectedMethodIds = new ArrayList<Integer>();

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;


    public ProjectMethodsView(Project project) {
        presenter = new ProjectMethodsPresenter(this,project);

        this.project = project;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
         try {
            initalizeComponents();

             // Preinit components
             typeFilter.addItem("");
             for (String[] methodType : methodTypes) {
                 typeFilter.addItem(methodType[0]);
                 typeFilter.setItemCaption(methodType[0], methodType[1]);
             }

             typeFilter.setNullSelectionAllowed(false);
             typeFilter.select(methodTypes[0][0]);

             groupFilter.addItem("");
             for(String[] methodGroup : methodGroups) {
                 groupFilter.addItem(methodGroup[0]);
                 groupFilter.setItemCaption(methodGroup[0],methodGroup[1]);
             }
             groupFilter.select("");
             groupFilter.setNullSelectionAllowed(false);


             initializeActions();
            initializeValues();
         } catch (MiddlewareQueryException e) {
             e.printStackTrace();
         }
    }

    private void initalizeComponents() {
        final VerticalLayout root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(new Layout.MarginInfo(false,true,true,true));

        final Label heading = new Label("Manage Program Methods");
        heading.setStyleName(Bootstrap.Typography.H1.styleName());
        root.addComponent(heading);

        final HorizontalLayout availableMethodsTitleContainer = new HorizontalLayout();
        final Label availableMethodsTitle = new Label("Available Methods");
        availableMethodsTitle.setStyleName(Bootstrap.Typography.H2.styleName());

        addNewMethodBtn = new Button("Add new Method");
        addNewMethodBtn.setStyleName(Bootstrap.Buttons.INFO.styleName() +  " loc-add-btn");

        availableMethodsTitleContainer.addComponent(availableMethodsTitle);
        availableMethodsTitleContainer.addComponent(addNewMethodBtn);

        availableMethodsTitleContainer.setComponentAlignment(addNewMethodBtn,Alignment.MIDDLE_RIGHT);
        availableMethodsTitleContainer.setSizeUndefined();
        availableMethodsTitleContainer.setWidth("100%");
        availableMethodsTitleContainer.setMargin(false,true,true,false);	// move this to css

        root.addComponent(availableMethodsTitleContainer);
        root.addComponent(this.buildMethodFilterForm());
        root.addComponent(this.buildAvailableMethodsTable());

        final HorizontalLayout selectedMethodTitleContainer = new HorizontalLayout();
        selectedMethodTitleContainer.setMargin(true,true,false,false);

        final Label selectedMethodsTitle = new Label("Program Methods");
        selectedMethodsTitle.setStyleName(Bootstrap.Typography.H2.styleName());

        selectedMethodTitleContainer.addComponent(selectedMethodsTitle);

        root.addComponent(selectedMethodTitleContainer);
        root.addComponent(this.buildSelectedMethodsTable());
        root.addComponent(this.buildActionButtons());

        this.setCompositionRoot(root);
    }

    private Component buildActionButtons() {
        final HorizontalLayout root = new HorizontalLayout();
        saveBtn = new Button("Save");
        saveBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

        cancelBtn = new Button("Cancel");

        final Label spacer = new Label("");
        spacer.setWidth("100%");

        root.addComponent(spacer);
        root.addComponent(cancelBtn);
        root.addComponent(saveBtn);

        root.setExpandRatio(spacer, 1.0f);
        root.setSpacing(true);
        root.setMargin(true);
        root.setWidth("100%");
        return root;
    }

    private Component buildMethodFilterForm() {
        final HorizontalLayout root = new HorizontalLayout();
        final HorizontalLayout container = new HorizontalLayout();

        container.setSpacing(true);
        container.setMargin(new Layout.MarginInfo(false,false,true,false));

        groupFilter = new Select();
        groupFilter.setImmediate(true);
        groupFilter.setNullSelectionAllowed(false);

        typeFilter = new Select();
        typeFilter.setImmediate(true);
        typeFilter.setNullSelectionAllowed(false);

        searchField = new TextField();
        searchField.setImmediate(true);
        searchField.setNullRepresentation("");


        final Label spacer = new Label();
        spacer.setWidth("100%");

        final Label groupLbl = new Label("Method Group");
        final Label typeLbl = new Label("Method Type");
        final Label searchLbl = new Label("Search Methods");

        groupLbl.setSizeFull();
        typeLbl.setSizeFull();
        searchLbl.setSizeFull();

        groupLbl.setStyleName("loc-filterlbl");
        typeLbl.setStyleName("loc-filterlbl");
        searchLbl.setStyleName("loc-filterlbl");

        container.addComponent(searchLbl);
        container.addComponent(searchField);
        container.addComponent(groupLbl);
        container.addComponent(groupFilter);
        container.addComponent(typeLbl);
        container.addComponent(typeFilter);

        root.addComponent(container);

        resultCountLbl = new Label("");
        resultCountLbl.setStyleName("loc-resultcnt");
        //root.addComponent(resultCountLbl);
        //root.setExpandRatio(resultCountLbl,1.0f);

        root.setSizeFull();


        return root;
    }

    private Table buildCustomTable(Map<String,Integer> columnWidthMap) {
        Table table = new Table() {
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

        table.setStyleName("loc-table");
        table.setSelectable(true);
        table.setMultiSelect(false);

        table.setHeight("250px");
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
                   //ProjectMethodsView.this.presenter.doAddMethodAction();
                   event.getComponent().getWindow().addWindow(new AddBreedingMethodsWindow(ProjectMethodsView.this));

            }
        });

        Property.ValueChangeListener filterAction = new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                List<Method> results = presenter.getFilteredResults(groupFilter.getValue().toString(),typeFilter.getValue().toString(),searchField.getValue().toString());
                availableMethodsTable.getContainerDataSource().removeAllItems();

                try {
                    ProjectMethodsView.this.generateRows(results,true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                resultCountLbl.setValue("Results: " + availableMethodsTable.getContainerDataSource().getItemIds().size() + " items");

            }
        };

        searchField.addListener(filterAction);
        groupFilter.addListener(filterAction);
        typeFilter.addListener(filterAction);

        onAddToSelectedMethodAction = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                Item selectedItem = availableMethodsTable.getItem(event.getButton().getData());

                ProjectMethodsView.this.presenter.doMoveToSelectedMethod(Integer.parseInt(selectedItem.getItemProperty("mid").getValue().toString()));

                availableMethodsTable.removeItem(selectedItem);

                availableMethodsTable.removeItem(event.getButton().getData());


            }
        };

        onRemoveSelectedMethodAction = new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Item item = (Item)selectedMethodsTable.getItem(event.getButton().getData());
                Integer locId = (Integer) item.getItemProperty("mid").getValue();

                ProjectMethodsView.this.presenter.doRemoveSelectedMethod(locId);

                selectedMethodsTable.removeItem(event.getButton().getData());
                selectedMethodIds.remove(locId);
            }
        };

        saveBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (ProjectMethodsView.this.presenter.saveProjectLocation(selectedMethodIds)) {
                    MessageNotifier.showMessage(event.getComponent().getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.METHODS_SUCCESSFULLY_CONFIGURED));
                } else {    // should never happen

                }
            }
        });

        cancelBtn.addListener(new Button.ClickListener() {
           private static final long serialVersionUID = -4479216826096826464L;

			@Override
            public void buttonClick(Button.ClickEvent event) {
                ProjectMethodsView.this.reset();
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
        visibleFieldsMap.put("mname","Method Name");
        visibleFieldsMap.put("fmdesc","Method Description");
        visibleFieldsMap.put("mgrp","Method Group");
        visibleFieldsMap.put("mcode","Method Code");
        visibleFieldsMap.put("mtype","Method Type");
        visibleFieldsMap.put("fmdate","Method Date");

        HashMap<String,String> visibleFieldsMap2 = new LinkedHashMap<String, String>(visibleFieldsMap);
        visibleFieldsMap2.put("removeBtn","Remove");
        visibleFieldsMap.put("selectBtn","Select");

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getName().equals("serialVersionUID"))
                continue;

            itemContainer.addContainerProperty(field.getName(),field.getType(),null);
            itemContainer2.addContainerProperty(field.getName(),field.getType(),null);

        }

        availableMethodsTable.getContainerDataSource().addContainerProperty("selectBtn", Button.class, null);
        selectedMethodsTable.getContainerDataSource().addContainerProperty("removeBtn", Button.class, null);

        availableMethodsTable.getContainerDataSource().addContainerProperty("fmdesc", Label.class, null);
        selectedMethodsTable.getContainerDataSource().addContainerProperty("fmdesc", Label.class, null);

        availableMethodsTable.getContainerDataSource().addContainerProperty("fmdate", String.class, null);
        selectedMethodsTable.getContainerDataSource().addContainerProperty("fmdate", String.class, null);

        try {
            // add all items to selected table first
            this.generateRows(presenter.getSavedProjectMethods(), false);

            // add all items for available locations table
            this.generateRows(presenter.getFilteredResults(groupFilter.getValue().toString(), typeFilter.getValue().toString(), ""), true);

            resultCountLbl.setValue("Result: " + itemContainer.getItemIds().size() + " items");

        } catch (Exception e) {
            e.printStackTrace();
        }

        availableMethodsTable.setVisibleColumns(visibleFieldsMap.keySet().toArray(new String[visibleFieldsMap.size()]));
        availableMethodsTable.setColumnHeaders(visibleFieldsMap.values().toArray(new String[visibleFieldsMap.size()]));

        selectedMethodsTable.setVisibleColumns(visibleFieldsMap2.keySet().toArray(new String[visibleFieldsMap2.size()]));
        selectedMethodsTable.setColumnHeaders(visibleFieldsMap2.values().toArray(new String[visibleFieldsMap2.size()]));

    }

    private void generateRows(List<Method> methodList, boolean isAvailableTable) throws IllegalAccessException {
           for (Method method : methodList) {
               if (selectedMethodIds.contains(method.getMid()))
                   continue;

               addRow(method,isAvailableTable,null);
           }
    }

    public void addRow(Method method, boolean isAvailableTable,Integer index) throws IllegalAccessException {
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

        if (isAvailableTable) {
            btn.setStyleName(Reindeer.BUTTON_LINK + " loc-select-btn");
            btn.addListener(onAddToSelectedMethodAction);
            newItem.getItemProperty("selectBtn").setValue(btn);
        } else {
            btn.setStyleName(Reindeer.BUTTON_LINK + " loc-remove-btn");
            btn.addListener(onRemoveSelectedMethodAction);
            newItem.getItemProperty("removeBtn").setValue(btn);

            if (!selectedMethodIds.contains(method.getMid()))
                selectedMethodIds.add(method.getMid());
        }

        btn.setData(itemId);
    }

    public void addToAvailableMethods(Method method) {
        try {
            addRow(method,true,0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    /** TODO: The Following methods are only for compatibility to old Classes depentent on old Project Breeding Methods Panel UI **/
    public ManagerFactory getManagerFactory() {
        return this.presenter.getManagerFactory();
    }
    
    private void reset(){
    	if(methodTypes!=null) {
    		typeFilter.select(methodTypes[0][0]);
    	} else {
    		typeFilter.select((Object) null);
    	}
    	groupFilter.select("");
		searchField.setValue("");
		
		try {
			initializeValues();
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
	}
}