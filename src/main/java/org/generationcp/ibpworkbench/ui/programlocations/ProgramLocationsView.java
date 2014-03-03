package org.generationcp.ibpworkbench.ui.programlocations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IWorkbenchSession;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenProgramLocationsAction;
import org.generationcp.ibpworkbench.ui.common.IContainerFittable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ConversionException;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class ProgramLocationsView extends CustomComponent implements InitializingBean, IContainerFittable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 694660099102399989L;
	private Button addNewLocationsBtn;
	private Table selectedLocationsTable;
	private Table availableLocationsTable;
	private Select countryFilter;
	private Select locationTypeFilter;
	private TextField searchField;
	//private Button doFilterBtn;

	private ClickListener onAvailableLocationSelect;
	private ClickListener onRemoveSaveLocation;
	private Button saveBtn;
	private Button cancelBtn;
	
	private ProgramLocationsPresenter programLocationsPresenter;
	private static final Logger LOG = LoggerFactory.getLogger(ProgramLocationsView.class);
	private Field[] fields;
	private ArrayList<Integer> selectedProjectLocationIds = new ArrayList<Integer>();
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private Label resultCountLbl;
    private  VerticalLayout root;
    private Table availTable;
    private Table selTable;

    public ProgramLocationsView(Project project) {
        this.programLocationsPresenter = new ProgramLocationsPresenter(project);
	}

    @Override
    public void attach() {
        super.attach();

        this.programLocationsPresenter.onAttachInitialize((IWorkbenchSession) this.getApplication());
    }
	
	protected void initializeComponents() {
        root = new VerticalLayout();
		root.setSpacing(false);
        root.setMargin(new Layout.MarginInfo(false,true,true,true));

        final HorizontalLayout availableLocationsTitleContainer = new HorizontalLayout();
        final Label heading = new Label("Manage Program Locations");
        heading.setStyleName(Bootstrap.Typography.H1.styleName());

        final Label availableLocationsTitle = new Label("Available Locations");
		availableLocationsTitle.setStyleName(Bootstrap.Typography.H2.styleName());

        addNewLocationsBtn = new Button("Add New Location");
		addNewLocationsBtn.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");

        availableLocationsTitleContainer.addComponent(availableLocationsTitle);
		availableLocationsTitleContainer.addComponent(addNewLocationsBtn);

        availableLocationsTitleContainer.setComponentAlignment(addNewLocationsBtn, Alignment.MIDDLE_RIGHT);
		availableLocationsTitleContainer.setSizeUndefined();
        availableLocationsTitleContainer.setWidth("100%");
		availableLocationsTitleContainer.setMargin(false,true,true,false);	// move this to css

        final HorizontalLayout selectedLocationsTitleContainer = new HorizontalLayout();
        selectedLocationsTitleContainer.setMargin(true,true,false,false);

        final Label selectedLocationsTitle = new Label(messageSource.getMessage(Message.FAVORITE_PROGRAM_LOCATIONS));
        selectedLocationsTitle.setStyleName(Bootstrap.Typography.H2.styleName());

        selectedLocationsTitleContainer.addComponent(selectedLocationsTitle);

        availTable = this.buildAvailableLocationsTable();
        availTable.setHeight("250px");
        selTable = this.buildSelectedLocationsTable();
        selTable.setHeight("250px");

        root.addComponent(heading);
        root.addComponent(availableLocationsTitleContainer);
        root.addComponent(this.buildLocationFilterForm());
        root.addComponent(availTable);
        root.addComponent(selectedLocationsTitleContainer);
		root.addComponent(selTable);
		root.addComponent(this.buildActionButtons());

        this.setCompositionRoot(root);
    }

    @Override
    public void fitToContainer(final Window parentWindow) {
        availTable.setHeight("100%");
        selTable.setHeight("100%");

        root.setExpandRatio(availTable,1.0f);
        root.setExpandRatio(selTable,1.0f);
        root.setSizeFull();

        this.setSizeFull();

        // special actions added to save and cancel btns
        final Button.ClickListener execCloseFrameJS =  new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                parentWindow.executeJavaScript("window.parent.closeLocationFrame();");
            }
        };

        saveBtn.addListener(execCloseFrameJS);
        cancelBtn.addListener(execCloseFrameJS);

    }
	
	protected void initializeValues() throws MiddlewareQueryException {
		/* INITIALIZE FILTER CONTROLS DATA */
		BeanItemContainer<Country> countryContainer = new BeanItemContainer<Country>(Country.class);
		countryContainer.addAll(programLocationsPresenter.getCountryList());
		countryFilter.setContainerDataSource(countryContainer);
		countryFilter.setItemCaptionPropertyId("isoabbr");
		
		BeanItemContainer<UserDefinedField> locationTypeContainer = new BeanItemContainer<UserDefinedField>(UserDefinedField.class);
		locationTypeContainer.addAll(programLocationsPresenter.getLocationTypeList());
		locationTypeFilter.setContainerDataSource(locationTypeContainer);
		locationTypeFilter.setItemCaptionPropertyId("fname");
		locationTypeFilter.select(locationTypeFilter.getItemIds().iterator().next());	// select first item
		
		/* INITIALIZE TABLE DATA */
		IndexedContainer itemContainer = new IndexedContainer();
		IndexedContainer itemContainer2 = new IndexedContainer();
		
		fields = LocationTableViewModel.class.getDeclaredFields();
		
		HashMap<String,String> visibleFieldsMap = new LinkedHashMap<String, String>();
		visibleFieldsMap.put("locationName","Location Name");
		visibleFieldsMap.put("cntryFullName","Country Full Name");
		visibleFieldsMap.put("locationAbbreviation","Location Abbreviation");
		visibleFieldsMap.put("ltype","Location Type");
		//visibleFieldsMap.put("latitude","Latitude");
		//visibleFieldsMap.put("longtitude","Longtitude");
		//visibleFieldsMap.put("altitude","Altitude");
		
		HashMap<String,String> visibleFieldsMap2 = new LinkedHashMap<String, String>(visibleFieldsMap);
		visibleFieldsMap2.put("removeBtn","Remove");
		visibleFieldsMap.put("selectBtn","Select");
		
		for (Field field : fields) {
			field.setAccessible(true);				
			itemContainer.addContainerProperty(field.getName(),field.getType(),null);
			itemContainer2.addContainerProperty(field.getName(),field.getType(),null);
			
		}
		// add select field
		itemContainer.addContainerProperty("selectBtn",Button.class,null);
		itemContainer2.addContainerProperty("removeBtn",Button.class,null);		
		try {
			// add all items to selected table first
			this.generateRows(programLocationsPresenter.getSavedProgramLocations(), itemContainer2, false);
			
			// add all items for available locations table
			this.generateRows(programLocationsPresenter.getFilteredResults(null,this.getSelectedLocationTypeIdFromFilter() ,""), itemContainer, true);
			
			resultCountLbl.setValue("Result: " + itemContainer.getItemIds().size() + " items");
			
			
		} catch (ReadOnlyException e) {
			e.printStackTrace();
		} catch (ConversionException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		availableLocationsTable.setContainerDataSource(itemContainer);
		availableLocationsTable.setVisibleColumns(visibleFieldsMap.keySet().toArray(new String[visibleFieldsMap.size()]));
		availableLocationsTable.setColumnHeaders(visibleFieldsMap.values().toArray(new String[visibleFieldsMap.size()]));
		
		selectedLocationsTable.setContainerDataSource(itemContainer2);
		selectedLocationsTable.setVisibleColumns(visibleFieldsMap2.keySet().toArray(new String[visibleFieldsMap2.size()]));
		selectedLocationsTable.setColumnHeaders(visibleFieldsMap2.values().toArray(new String[visibleFieldsMap2.size()]));
	}

	protected void initializeActions() {
		addNewLocationsBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -3097404625783712141L;

			@Override
			public void buttonClick(ClickEvent event) {
				ProgramLocationsView.this.onAddLocationWindowAction(event);
			}
		});

        searchField.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                ProgramLocationsView.this.onUpdateAvailableTableOnFilter(event);
            }
        });

        countryFilter.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                ProgramLocationsView.this.onUpdateAvailableTableOnFilter(event);
            }
        });

        locationTypeFilter.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                ProgramLocationsView.this.onUpdateAvailableTableOnFilter(event);
            }
        });


        onAvailableLocationSelect = new Button.ClickListener() {
			private static final long serialVersionUID = 7925088387225345287L;

			@Override
			public void buttonClick(ClickEvent event) {
				ProgramLocationsView.this.onSelectAvailableLocation(event);
			}
		};
		
		onRemoveSaveLocation = new Button.ClickListener() {
			private static final long serialVersionUID = 8914547929303039639L;

			@Override
			public void buttonClick(ClickEvent event) {
				ProgramLocationsView.this.onRemoveSavedLocation(event);
			}
		};
		
		saveBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 9113834880192462010L;

			@Override
			public void buttonClick(ClickEvent event) {
				ProgramLocationsView.this.onSaveProgramLocations(event);
			}
		});
		
		cancelBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 5980254872602301350L;

			@Override
			public void buttonClick(ClickEvent event) {

                IWorkbenchSession appSession = (IWorkbenchSession) event.getComponent().getApplication();

                (new OpenProgramLocationsAction(appSession.getSessionData().getLastOpenedProject(),appSession.getSessionData().getUserData())).buttonClick(event);
			}
		});
	}

	
	public Component buildLocationFilterForm() {

		countryFilter = new Select();
        countryFilter.setImmediate(true);
		locationTypeFilter = new Select();
        locationTypeFilter.setImmediate(true);
		searchField = new TextField();
        searchField.setImmediate(true);
		//doFilterBtn = new Button("Filter");
		
		countryFilter.setNullSelectionAllowed(true);
		locationTypeFilter.select(String.valueOf(410));
		locationTypeFilter.setWidth("220px");
        locationTypeFilter.setNullSelectionAllowed(true);

		final Label countryLbl = new Label("Country");
		final Label ltypeLbl = new Label("Location Type");
		final Label searchLbl = new Label("Search Locations");

		countryLbl.setSizeUndefined();
		ltypeLbl.setSizeUndefined();
		searchLbl.setSizeUndefined();

		countryLbl.setStyleName("loc-filterlbl");
		ltypeLbl.setStyleName("loc-filterlbl");
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
        field2.addComponent(countryLbl);
        field2.addComponent(countryFilter);

        container.addComponent(field2);

        final HorizontalLayout field3 = new HorizontalLayout();
        field3.addStyleName("field");
        field3.setSpacing(true);
        field3.setSizeUndefined();
        field3.addComponent(ltypeLbl);
        field3.addComponent(locationTypeFilter);

        container.addComponent(field3);

		//container.addComponent(doFilterBtn);
		
		resultCountLbl = new Label("");
		resultCountLbl.setStyleName("loc-resultcnt");
		//root.addComponent(resultCountLbl);
		//root.setExpandRatio(resultCountLbl,1.0f);

		return container;
	}
	
	private Table buildCustomTable() {
		Table table = new Table() {
			private static final long serialVersionUID = -14085524627550290L;

			@Override
			public String getColumnAlignment(Object propertyId) {
				Class<?> t = getContainerDataSource().getType(propertyId);
				
				if (t == Integer.class || t == Long.class || t == Double.class || t == Float.class) {
					return Table.ALIGN_RIGHT;
				} else if (t == String.class) {
					return Table.ALIGN_LEFT;
				} else return Table.ALIGN_CENTER;
			}
			
		};
		
		
		table.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			 
			private static final long serialVersionUID = -2457435122226402123L;

			public String getStyle(Object itemId, Object propertyId) {
				
			    if (propertyId != null && propertyId.toString().equals("locationName"))
			    	return propertyId.toString();
			    
			    return null;
			}
        	    });
	    
	   
		
		table.setColumnWidth("locationName",300);
        table.setColumnWidth("locationAbbreviation",180);
        table.setColumnWidth("cntryFullName",225);
        table.setColumnWidth("ltype",180);
		table.setColumnWidth("removeBtn",60);
		table.setColumnWidth("selectBtn",60);
		table.setStyleName("loc-table");
		table.setSelectable(true);
		table.setMultiSelect(false);
		
		return table;
	}
	
	public Table buildAvailableLocationsTable() {
		availableLocationsTable = buildCustomTable();
		availableLocationsTable.setWidth("100%");
		
		return availableLocationsTable;
	}
	
	public Table buildSelectedLocationsTable() {
		selectedLocationsTable = buildCustomTable();
		selectedLocationsTable.setWidth("100%");
		
		return selectedLocationsTable;
	}
	
	public Component buildActionButtons() {
		final HorizontalLayout root = new HorizontalLayout();
		saveBtn = new Button("Save");
		saveBtn.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());

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
	
	public void onAddLocationWindowAction(Button.ClickEvent event) {
		 event.getComponent().getWindow().addWindow(new AddLocationsWindow(this, programLocationsPresenter));
		
		LOG.debug("onAddLocationWindowAction:");
	}
	
	public void onUpdateAvailableTableOnFilter(Property.ValueChangeEvent event) {
		LOG.debug("onUpdateAvailableTableOnFilter:");
		
		Country selectedCountry = (Country) countryFilter.getValue();
		UserDefinedField selectedLocationType =  (UserDefinedField) locationTypeFilter.getValue();
		String locationName = (String) searchField.getValue();
		
		Integer cntryId = (selectedCountry != null) ? selectedCountry.getCntryid() : null;
		Integer locationTypeId = (selectedLocationType != null) ? selectedLocationType.getFldno() : null;

		try {
			List<LocationTableViewModel> results = programLocationsPresenter.getFilteredResults(cntryId, locationTypeId, locationName);
			Container container = availableLocationsTable.getContainerDataSource();
			container.removeAllItems();
			
			try {
				this.generateRows(results,container,true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			resultCountLbl.setValue("Results: " + container.getItemIds().size() + " items");
			
		} catch (MiddlewareQueryException e) {
			// show error message
			e.printStackTrace();
		}
		
		
	}
	
	public void onSelectAvailableLocation(Button.ClickEvent event) {
		LOG.debug("onAvailableLocationSelect: " + event.getButton().getData());
		
		Item selectedItem = availableLocationsTable.getItem(event.getButton().getData());
		
		LocationTableViewModel model = new LocationTableViewModel();
		
		model.setLocationName((String) selectedItem.getItemProperty("locationName").getValue());
		model.setCntryFullName((String) selectedItem.getItemProperty("cntryFullName").getValue());
		model.setLocationAbbreviation((String) selectedItem.getItemProperty("locationAbbreviation").getValue());
		model.setLtype((String) selectedItem.getItemProperty("ltype").getValue());
		model.setLocationId( (Integer) selectedItem.getItemProperty("locationId").getValue());
		
		try {
			addRow(model,(IndexedContainer) selectedLocationsTable.getContainerDataSource(),false);
			availableLocationsTable.removeItem(event.getButton().getData());
			//IndexedContainer container = (IndexedContainer) selectedLocationsTable.getContainerDataSource();
			//selectedLocationsTable.select(container.getIdByIndex(container.size()-1));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onRemoveSavedLocation(Button.ClickEvent event) {
		LOG.debug("onRemoveSavedLocation: " + event.getButton().getData());
		
		Item item = (Item)selectedLocationsTable.getItem(event.getButton().getData());
		Integer locId = (Integer) item.getItemProperty("locationId").getValue();
		
		LocationTableViewModel model = new LocationTableViewModel();
		
		model.setLocationName((String) item.getItemProperty("locationName").getValue());
		model.setCntryFullName((String) item.getItemProperty("cntryFullName").getValue());
		model.setLocationAbbreviation((String) item.getItemProperty("locationAbbreviation").getValue());
		model.setLtype((String) item.getItemProperty("ltype").getValue());
		model.setLocationId( (Integer) item.getItemProperty("locationId").getValue());
		
		try {
			addRow(model, (IndexedContainer) availableLocationsTable.getContainerDataSource(),true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		selectedLocationsTable.removeItem(event.getButton().getData());
		selectedProjectLocationIds.remove(locId);
	}
	
	@SuppressWarnings("unchecked")
	public void onSaveProgramLocations(Button.ClickEvent event) {
		try {
			//programLocationsPresenter.saveProgramLocation(selectedLocationsTable.getContainerDataSource(), event);
			
			if (programLocationsPresenter.saveProgramLocation(selectedProjectLocationIds) )
				MessageNotifier.showMessage(event.getComponent().getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.LOCATION_SUCCESSFULLY_CONFIGURED));
	        
			
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.debug("onSaveProgramLocations:");
	}

	private int getSelectedLocationTypeIdFromFilter() {
		UserDefinedField udf = (UserDefinedField) locationTypeFilter.getValue();
		
		return (udf != null) ? udf.getFldno() : null;
	}
	
	/**
	 * 
	 * @param results - True for Available locations, False for the Selected locations table
	 * @param dataContainer
	 * @param isAvailableTable
	 * @throws ReadOnlyException
	 * @throws ConversionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void generateRows(List<LocationTableViewModel> results,Container dataContainer,boolean isAvailableTable) throws ReadOnlyException, ConversionException, IllegalArgumentException, IllegalAccessException {
		if (fields == null) return;

        for (LocationTableViewModel location : results) {
			if (selectedProjectLocationIds.contains(location.getLocationId()))
				continue;
			
			Object itemId = dataContainer.addItem();
			Item newItem = dataContainer.getItem(itemId);
			
			for (Field field : fields) {
                    newItem.getItemProperty(field.getName()).setValue(field.get(location));
			}
			
			Button btn = new Button();
			btn.setWidth("16px");
			btn.setHeight("16px");
			
			if (isAvailableTable) {
				btn.setStyleName(Reindeer.BUTTON_LINK + " loc-select-btn");
				btn.addListener(onAvailableLocationSelect);
				newItem.getItemProperty("selectBtn").setValue(btn);

				
				
			} else {				
				btn.setStyleName(Reindeer.BUTTON_LINK + " loc-remove-btn");
				btn.addListener(onRemoveSaveLocation);
				newItem.getItemProperty("removeBtn").setValue(btn);
				
				if (!selectedProjectLocationIds.contains(location.getLocationId()))
					selectedProjectLocationIds.add(location.getLocationId());
			}
			

			btn.setData(itemId);
		}
	}
	
	private void addRow(LocationTableViewModel location,IndexedContainer dataContainer,boolean isAvailableTable) throws ReadOnlyException, ConversionException, IllegalArgumentException, IllegalAccessException {
			
			Object itemId = dataContainer.addItemAt(0);
			Item newItem = dataContainer.getItem(itemId);
			
			for (Field field : fields) {
				newItem.getItemProperty(field.getName()).setValue(field.get(location));	
			}
			
			Button btn = new Button();
			btn.setWidth("16px");
			btn.setHeight("16px");
			
			if (isAvailableTable) {
				btn.setStyleName(Reindeer.BUTTON_LINK + " loc-select-btn");
				btn.addListener(onAvailableLocationSelect);
				newItem.getItemProperty("selectBtn").setValue(btn);

			} else {
				btn.setStyleName(Reindeer.BUTTON_LINK + " loc-remove-btn");
				btn.addListener(onRemoveSaveLocation);
				newItem.getItemProperty("removeBtn").setValue(btn);
				
				if (!selectedProjectLocationIds.contains(location.getLocationId()))
					selectedProjectLocationIds.add(location.getLocationId());
			}
		
			btn.setData(itemId);
			
	}
	
	public void addToAvailableLocation(Location loc){
		
		try {
			LocationTableViewModel location = programLocationsPresenter.getLocationDetailsByLocId(loc.getLocid());
			addRow(location, (IndexedContainer) selectedLocationsTable.getContainerDataSource(), false);
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadOnlyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			initializeComponents();
			initializeActions();
			initializeValues();					
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}	
	}

}
