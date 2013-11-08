package org.generationcp.ibpworkbench.projectlocations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Property;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenWorkflowForRoleAction;
import org.generationcp.ibpworkbench.comp.window.AddLocationsWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class ProjectLocationsView extends CustomComponent implements InitializingBean {

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
	
	private ProjectLocationsController projectLocationsController;
	private static final Logger LOG = LoggerFactory.getLogger(ProjectLocationsView.class);
	private Field[] fields;
	private ArrayList<Integer> selectedProjectLocationIds = new ArrayList<Integer>();
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
	private Label resultCountLbl;
	
	public ProjectLocationsView(Project project,Role role) {
		this.projectLocationsController = new ProjectLocationsController(project,role);
		
	}
	
	protected void initializeComponents() {
		final VerticalLayout root = new VerticalLayout();
		root.setSpacing(true);
		root.setMargin(true);
		
		final HorizontalLayout availableLocationsTitleContainer = new HorizontalLayout();
		final Label availableLocationsTitle = new Label("<span style='font-size: 18px; line-height: 35px;'>Available Locations</span>",Label.CONTENT_XHTML);
		availableLocationsTitle.setSizeFull();
		availableLocationsTitle.setStyleName("gcp-LocationsTableTitle");
		addNewLocationsBtn = new Button("Add New Location");
		addNewLocationsBtn.setStyleName(Reindeer.BUTTON_LINK + " loc-add-btn");
		final Label spacer = new Label();
				
		availableLocationsTitleContainer.addComponent(availableLocationsTitle);
		availableLocationsTitleContainer.addComponent(spacer);
		availableLocationsTitleContainer.addComponent(addNewLocationsBtn);
		availableLocationsTitleContainer.setExpandRatio(availableLocationsTitle, 1.0f);
		availableLocationsTitleContainer.setExpandRatio(spacer,1.0f);
		availableLocationsTitleContainer.setWidth("100%");
		availableLocationsTitleContainer.setMargin(false,false,true,false);	// move this to css
		
		root.addComponent(availableLocationsTitleContainer);
		root.addComponent(this.buildLocationFilterForm());
		root.addComponent(this.buildAvailableLocationsTable());

		final Label selectedLocationsTitle = new Label("<span style='font-size: 18px; display: inline-block; margin-top: 15px'>Project Locations</span>",Label.CONTENT_XHTML);
		selectedLocationsTitle.setStyleName("gcp-LocationsTableTitle");

		root.addComponent(selectedLocationsTitle);

		root.addComponent(this.buildSelectedLocationsTable());
		root.addComponent(this.buildActionButtons());

		this.setCompositionRoot(root);
	}
	
	protected void initializeValues() throws MiddlewareQueryException {
		/* INITIALIZE FILTER CONTROLS DATA */
		BeanItemContainer<Country> countryContainer = new BeanItemContainer<Country>(Country.class);
		countryContainer.addAll(projectLocationsController.getCountryList());
		countryFilter.setContainerDataSource(countryContainer);
		countryFilter.setItemCaptionPropertyId("isoabbr");
		
		BeanItemContainer<UserDefinedField> locationTypeContainer = new BeanItemContainer<UserDefinedField>(UserDefinedField.class);
		locationTypeContainer.addAll(projectLocationsController.getLocationTypeList());
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
			this.generateRows(projectLocationsController.getSavedProjectLocations(), itemContainer2, false);
			
			// add all items for available locations table
			this.generateRows(projectLocationsController.getFilteredResults(null,this.getSelectedLocationTypeIdFromFilter() ,""), itemContainer, true);
			
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
				ProjectLocationsView.this.onAddLocationWindowAction(event);
			}
		});

        searchField.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                ProjectLocationsView.this.onUpdateAvailableTableOnFilter(event);
            }
        });

        countryFilter.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                ProjectLocationsView.this.onUpdateAvailableTableOnFilter(event);
            }
        });

        locationTypeFilter.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                ProjectLocationsView.this.onUpdateAvailableTableOnFilter(event);
            }
        });


        onAvailableLocationSelect = new Button.ClickListener() {
			private static final long serialVersionUID = 7925088387225345287L;

			@Override
			public void buttonClick(ClickEvent event) {
				ProjectLocationsView.this.onSelectAvailableLocation(event);
			}
		};
		
		onRemoveSaveLocation = new Button.ClickListener() {
			private static final long serialVersionUID = 8914547929303039639L;

			@Override
			public void buttonClick(ClickEvent event) {
				ProjectLocationsView.this.onRemoveSavedLocation(event);
			}
		};
		
		saveBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 9113834880192462010L;

			@Override
			public void buttonClick(ClickEvent event) {
				ProjectLocationsView.this.onSaveProjectLocations(event);
			}
		});
		
		cancelBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 5980254872602301350L;

			@Override
			public void buttonClick(ClickEvent event) {
				ProjectLocationsView.this.onCancel(event);
			}
		});
	}

	
	public Component buildLocationFilterForm() {

		final HorizontalLayout root = new HorizontalLayout();
		final HorizontalLayout container = new HorizontalLayout();		

		countryFilter = new Select();
        countryFilter.setImmediate(true);
		locationTypeFilter = new Select();
        locationTypeFilter.setImmediate(true);
		searchField = new TextField();
        searchField.setImmediate(true);
		//doFilterBtn = new Button("Filter");
		
		countryFilter.setNullSelectionAllowed(true);
		locationTypeFilter.select(String.valueOf(410));
		locationTypeFilter.setNullSelectionAllowed(true);

		
		final Label spacer = new Label();
		spacer.setWidth("100%");
		
		container.setSpacing(true);
		
		
		final Label countryLbl = new Label("Country");
		final Label ltypeLbl = new Label("Location Type");
		final Label searchLbl = new Label("Search Locations");
		
		countryLbl.setSizeFull();
		ltypeLbl.setSizeFull();
		searchLbl.setSizeFull();
		
		countryLbl.setStyleName("loc-filterlbl");
		ltypeLbl.setStyleName("loc-filterlbl");
		searchLbl.setStyleName("loc-filterlbl");


        container.addComponent(searchLbl);
        container.addComponent(searchField);
		container.addComponent(countryLbl);
		container.addComponent(countryFilter);
		container.addComponent(ltypeLbl);
		container.addComponent(locationTypeFilter);
		//container.addComponent(doFilterBtn);
		
		root.addComponent(container);
		
		resultCountLbl = new Label("");
		resultCountLbl.setStyleName("loc-resultcnt");
		//root.addComponent(resultCountLbl);
		//root.setExpandRatio(resultCountLbl,1.0f);
		
		root.setSizeFull();
		return root;
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
	    
	   
		
		table.setColumnWidth("locationName",310);
		table.setColumnWidth("locationAbbreviation",130);
		table.setColumnWidth("removeBtn",60);
		table.setColumnWidth("selectBtn",60);
		table.setStyleName("loc-table");
		table.setSelectable(true);
		table.setMultiSelect(false);
		
		table.setHeight("250px");
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
		cancelBtn = new Button("Cancel");
		
		final Label spacer = new Label("");
		spacer.setWidth("100%");
		
		root.addComponent(spacer);
		root.addComponent(saveBtn);
		root.addComponent(cancelBtn);
		
		root.setExpandRatio(spacer, 1.0f);
		root.setSpacing(true);
		root.setMargin(true);
		root.setWidth("100%");
		return root;
	}
	
	public void onAddLocationWindowAction(Button.ClickEvent event) {
		 event.getComponent().getWindow().addWindow(new AddLocationsWindow(this, projectLocationsController));
		
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
			List<LocationTableViewModel> results = projectLocationsController.getFilteredResults(cntryId, locationTypeId, locationName);
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
	public void onSaveProjectLocations(Button.ClickEvent event) {
		try {
			//projectLocationsController.saveProjectLocation(selectedLocationsTable.getContainerDataSource(), event);
			
			if (projectLocationsController.saveProjectLocation(selectedProjectLocationIds) )
				MessageNotifier.showMessage(event.getComponent().getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.LOCATION_SUCCESSFULLY_CONFIGURED));
	        
			
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.debug("onSaveProjectLocations:");
	}
	
	public void onCancel(Button.ClickEvent event) {
		LOG.debug("onCancel:");
		
		try {
                (new OpenWorkflowForRoleAction(projectLocationsController.getProject())).doAction(event.getButton().getWindow(),
                		String.format("/OpenProjectWorkflowForRole?projectId=%d&roleId=%d",
                				projectLocationsController.getProject().getProjectId(),
                				projectLocationsController.getRole().getRoleId())
                			, true);
        } catch (Exception e) {
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(event.getComponent().getWindow(), i.getCaption(), i.getDescription());
            }
            return;
        }

		
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
			LocationTableViewModel location = projectLocationsController.getLocationDetailsByLocId(loc.getLocid());
			addRow(location, (IndexedContainer) availableLocationsTable.getContainerDataSource(), true);
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
