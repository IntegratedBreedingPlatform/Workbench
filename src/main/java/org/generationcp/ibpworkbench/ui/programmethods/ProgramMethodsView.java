
package org.generationcp.ibpworkbench.ui.programmethods;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.security.AuthorizationUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.common.IContainerFittable;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/11/13 Time: 9:48 AM To change this template use File | Settings | File Templates.
 */

@Configurable
public class ProgramMethodsView extends CustomComponent implements InitializingBean, IContainerFittable {

	private static final long serialVersionUID = 3300444018220674997L;

	private boolean cropOnly = false;
	protected ProgramMethodsPresenter presenter;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private AuthorizationUtil authorizationUtil;

	public static final String[][] METHOD_TYPES = { {"GEN", "Generative"}, {"DER", "Derivative"}, {"MAN", "Maintenance"}};
	public static final String[][] METHOD_GROUPS = { {"S", "Self Fertilizing"}, {"O", "Cross Pollinating"}, {"C", "Clonally Propagating"},
			{"G", "All System"}};
	private static Action copyBreedingMethodAction = new Action("Copy Breeding Method");

	public static final Map<String, String> TABLE_COLUMNS;
	public static final Map<String, Integer> TABLE_COLUMN_SIZES;

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
		TABLE_COLUMNS = new LinkedHashMap<>();
		ProgramMethodsView.TABLE_COLUMNS.put(ProgramMethodsView.SELECT, "<span class='glyphicon glyphicon-ok'></span>");
		ProgramMethodsView.TABLE_COLUMNS.put(ProgramMethodsView.GMNAME, "Method Name");
		ProgramMethodsView.TABLE_COLUMNS.put(ProgramMethodsView.DESC, "Description");
		ProgramMethodsView.TABLE_COLUMNS.put(ProgramMethodsView.MGRP, "Group");
		ProgramMethodsView.TABLE_COLUMNS.put(ProgramMethodsView.MCODE, "Code");
		ProgramMethodsView.TABLE_COLUMNS.put(ProgramMethodsView.MTYPE, "Type");
		ProgramMethodsView.TABLE_COLUMNS.put(ProgramMethodsView.DATE, "Date");
		ProgramMethodsView.TABLE_COLUMNS.put(ProgramMethodsView.CLASS, "Class");

		TABLE_COLUMN_SIZES = new HashMap<>();
		ProgramMethodsView.TABLE_COLUMN_SIZES.put(ProgramMethodsView.SELECT, 20);
		ProgramMethodsView.TABLE_COLUMN_SIZES.put(ProgramMethodsView.GMNAME, 210);
		ProgramMethodsView.TABLE_COLUMN_SIZES.put(ProgramMethodsView.MGRP, 45);
		ProgramMethodsView.TABLE_COLUMN_SIZES.put(ProgramMethodsView.MCODE, 40);
		ProgramMethodsView.TABLE_COLUMN_SIZES.put(ProgramMethodsView.MTYPE, 40);
		ProgramMethodsView.TABLE_COLUMN_SIZES.put(ProgramMethodsView.DATE, 70);
		ProgramMethodsView.TABLE_COLUMN_SIZES.put(ProgramMethodsView.CLASS, 45);
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

	private Map<Integer, String> classMap;
	private Button searchGoBtn;

	public ProgramMethodsView(Project project) {
		this.presenter = new ProgramMethodsPresenter(this, project);
	}

	public ProgramMethodsView(CropType cropType) {
		this.cropOnly = true;
		this.presenter = new ProgramMethodsPresenter(this, cropType);
	}

	@Override
	public void fitToContainer(Window parentWindow) {
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

	private void initializeComponents() {
		this.resultCountLbl = new Label();
		this.resultCountLbl.setDebugId("resultCountLbl");

		this.addNewMethodsBtn = new Button("Add New Method");
		this.addNewMethodsBtn.setDebugId("addNewMethodsBtn");
		this.addNewMethodsBtn.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");
		this.addNewMethodsBtn.setVisible(false);

		this.saveBtn = new Button("Save Favorites");
		this.saveBtn.setDebugId("saveBtn");
		this.saveBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());
		try {
			initializeRestrictedComponents();
		}catch (AccessDeniedException e){
			/**
			 * Do nothing: the screen needs to be displayed, only some of the components needs to be hidden.
			 * If a user with unauthorize access is trying to access this method an ${@link AccessDeniedException} will be thrown.
			 */
		}
		this.searchGoBtn = new Button("Go");
		this.searchGoBtn.setDebugId("searchGoBtn");
		this.searchGoBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());

		this.availableSelectAll = new CheckBox("Select All");
		this.availableSelectAll.setDebugId("availableSelectAll");
		this.availableSelectAll.setImmediate(true);
		this.favoriteSelectAll = new CheckBox("Select All");
		this.favoriteSelectAll.setDebugId("favoriteSelectAll");
		this.favoriteSelectAll.setImmediate(true);

		this.availTotalEntriesLabel = new Label(this.messageSource.getMessage(Message.TOTAL_ENTRIES) + ":  <b>0</b>", Label.CONTENT_XHTML);
		this.availTotalEntriesLabel.setDebugId("availTotalEntriesLabel");
		this.favTotalEntriesLabel = new Label(this.messageSource.getMessage(Message.TOTAL_ENTRIES) + ":  <b>0</b>", Label.CONTENT_XHTML);
		this.favTotalEntriesLabel.setDebugId("favTotalEntriesLabel");
		this.availSelectedEntriesLabel =
				new Label("<i>" + this.messageSource.getMessage(Message.SELECTED) + ":   <b>0</b></i>", Label.CONTENT_XHTML);
		this.favSelectedEntriesLabel =
				new Label("<i>" + this.messageSource.getMessage(Message.SELECTED) + ":   <b>0</b></i>", Label.CONTENT_XHTML);

		// TABLES!
		this.availableTable = this.buildCustomTable(this.availableSelectAll, this.availTotalEntriesLabel, this.availSelectedEntriesLabel);
		this.availableTable.setData(ProgramMethodsView.AVAILABLE);
		this.favoritesTable = this.buildCustomTable(this.favoriteSelectAll, this.favTotalEntriesLabel, this.favSelectedEntriesLabel);
		this.favoritesTable.setData(ProgramMethodsView.FAVORITES);

		this.addToFavoriteBtn = new Button("Add to Favorite Methods");
		this.addToFavoriteBtn.setDebugId("addToFavoriteBtn");
		this.addToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());

		this.removeToFavoriteBtn = new Button("Remove from Favorite Methods");
		this.removeToFavoriteBtn.setDebugId("removeToFavoriteBtn");
		this.removeToFavoriteBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());
		// filter form
		this.initializeFilterForm();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERADMIN')")
	private void initializeRestrictedComponents() {
		this.addNewMethodsBtn.setVisible(true);
	}

	private void initializeFilterForm() {
		this.typeFilter = new Select();
		this.typeFilter.setDebugId("typeFilter");
		this.typeFilter.setImmediate(true);
		this.typeFilter.setNullSelectionItemId(false);

		this.groupFilter = new Select();
		this.groupFilter.setDebugId("groupFilter");
		this.groupFilter.setImmediate(true);
		this.groupFilter.setNullSelectionAllowed(false);

		this.searchField = new TextField();
		this.searchField.setDebugId("searchField");
		this.searchField.setImmediate(true);

	}

	private Table buildCustomTable(final CheckBox assocSelectAll, final Label totalEntries, final Label selectedEntries) {
		final Table table = new Table();
		table.setDebugId("table");

		table.setImmediate(true);
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setDragMode(Table.TableDragMode.MULTIROW);

		table.addGeneratedColumn(ProgramMethodsView.SELECT, new Table.ColumnGenerator() {

			private static final long serialVersionUID = -2712621177075270647L;

			@Override
			public Object generateCell(final Table source, final Object itemId, Object colId) {
				final CheckBox select = new CheckBox();
				select.setDebugId("select");
				select.setImmediate(true);
				select.addListener(new Button.ClickListener() {

					private static final long serialVersionUID = -5401459415390953417L;

					@Override
					public void buttonClick(Button.ClickEvent clickEvent) {
						Boolean val = (Boolean) ((CheckBox) clickEvent.getComponent()).getValue();

						((MethodView) itemId).setActive(val);
						if (val) {
							source.select(itemId);
						} else {
							source.unselect(itemId);
							assocSelectAll.setValue(val);
						}
					}
				});

				if (((MethodView) itemId).isActive()) {
					select.setValue(true);
				} else {
					select.setValue(false);
				}

				return select;
			}
		});

		table.addGeneratedColumn(ProgramMethodsView.GMNAME, new Table.ColumnGenerator() {

			private static final long serialVersionUID = -9087436773196724575L;

			@Override
			public Object generateCell(final Table source, final Object itemId, Object colId) {
				//TODO Verify with Mariano if this functionality will be restricted to superadmin user
				if (authorizationUtil.isSuperAdminUser()) {
					final Button mNameBtn = new Button(((MethodView) itemId).getMname());
					mNameBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());
					mNameBtn.setData(itemId);
					mNameBtn.addListener(ProgramMethodsView.this.editMethodListener);
					return mNameBtn;
				} else {
					// If logged in user does not have admin authority, do not render as link
					final Label mNameLabel = new Label();
					mNameLabel.setDebugId("mNameLabel");
					mNameLabel.setDescription(((MethodView) itemId).getMname());
					mNameLabel.setValue(((MethodView) itemId).getMname());

					return mNameLabel;
				}

			}
		});

		table.addGeneratedColumn(ProgramMethodsView.CLASS, new Table.ColumnGenerator() {

			private static final long serialVersionUID = -9208828919595982878L;

			@Override
			public Object generateCell(final Table source, final Object itemId, Object colId) {
				Label classLbl = new Label("");
				classLbl.setDebugId("classLbl");
				classLbl.setContentMode(Label.CONTENT_XHTML);
				String methodClass = ProgramMethodsView.this.classMap.get(((MethodView) itemId).getGeneq());
				methodClass = methodClass == null ? "" : methodClass;
				classLbl.setValue(methodClass);
				classLbl.setDescription(methodClass);
				return classLbl;
			}
		});

		table.addGeneratedColumn(ProgramMethodsView.DATE, new Table.ColumnGenerator() {

			private static final long serialVersionUID = -8704716382416470975L;

			@Override
			public Object generateCell(final Table source, final Object itemId, Object colId) {
				DateFormat df = DateUtil.getSimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);
				DateFormat newDf = DateUtil.getSimpleDateFormat(DateUtil.FRONTEND_DATE_FORMAT_2);

				if (((MethodView) itemId).getMdate().toString().length() > 1) {
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

		table.addGeneratedColumn(ProgramMethodsView.DESC, new Table.ColumnGenerator() {

			private static final long serialVersionUID = 6278117387128053730L;

			@Override
			public Object generateCell(final Table source, final Object itemId, Object colI) {
				Label l = new Label();
				l.setDebugId("l");
				l.setDescription(((MethodView) itemId).getMdesc());
				l.setValue(((MethodView) itemId).getMdesc());

				if (((MethodView) itemId).getMdesc().length() > 90) {
					l.setValue(((MethodView) itemId).getMdesc().substring(0, 90 - 3).trim().concat("..."));
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
				for (MethodView selectedItem : (Collection<MethodView>) source.getValue()) {
					selectedItem.setActive(true);
				}

				// update the no of selected items
				ProgramMethodsView.this.updateSelectedNoOfEntries(selectedEntries, table);

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

				((Table) dragAndDropEvent.getTargetDetails().getTarget()).removeListener(vcl);

				Object itemIdOver = t.getItemId();
				Set<Object> sourceItemIds = (Set<Object>) ((Table) t.getSourceComponent()).getValue();

				if (itemIdOver != null && sourceItemIds.isEmpty()) {
					if (((MethodView) itemIdOver).isEnabled()) {

						if (((Table) t.getSourceComponent()).getData().toString().equals(ProgramMethodsView.FAVORITES)) {
							((Table) t.getSourceComponent()).getContainerDataSource().removeItem(itemIdOver);
							ProgramMethodsView.this.updateNoOfEntries(ProgramMethodsView.this.favTotalEntriesLabel,
									(Table) t.getSourceComponent());
						}
						((Table) dragAndDropEvent.getTargetDetails().getTarget()).getContainerDataSource().addItem(itemIdOver);

					}
				} else {
					ProgramMethodsView.this.moveSelectedItems((Table) t.getSourceComponent(), (Table) dragAndDropEvent.getTargetDetails()
							.getTarget());
				}

				((Table) dragAndDropEvent.getTargetDetails().getTarget()).addListener(vcl);

				// update no of items
				ProgramMethodsView.this.updateNoOfEntries(totalEntries, table);
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
			if (source.getData().toString().equals(ProgramMethodsView.AVAILABLE)) {
				targetDataContainer.addItemAt(0, itemId);
				if (counter < 100) {
					target.unselect(itemId);
				}

				target.setValue(null);

				// refresh the fav location table
				this.updateNoOfEntries(this.favTotalEntriesLabel, target);
				this.updateSelectedNoOfEntries(this.favSelectedEntriesLabel, target);
			} else {
				sourceDataContainer.removeItem(itemId);
				source.setValue(null);

				// refresh the fav location table
				this.updateNoOfEntries(this.favTotalEntriesLabel, source);
				this.updateSelectedNoOfEntries(this.favSelectedEntriesLabel, source);
			}
			counter++;
		}

		if (counter >= 100 && target.getData().toString().equals(ProgramMethodsView.FAVORITES)) {
			target.setValue(null);
		}

		if (source.getData().toString().equals(ProgramMethodsView.AVAILABLE)) {
			source.setValue(null);
		}

		source.refreshRowCache();
		target.refreshRowCache();
	}

	/**
	 * Use this method to return the list of methods in Favorite Methods table, you might have to manually convert the MethodView bean to
	 * Middleware's Method bean.
	 * 
	 * @return
	 */
	public Collection<Method> getFavoriteMethods() {
		return this.presenter.convertTo(this.favoritesTableContainer.getItemIds());

	}

	private void initializeValues() {

		/* INITIALIZE FILTER CONTROLS DATA */
		this.typeFilter.addItem("");
		this.typeFilter.setItemCaption("", "All Generation Advancement Types");

		for (String[] methodType : ProgramMethodsView.METHOD_TYPES) {
			this.typeFilter.addItem(methodType[0]);
			this.typeFilter.setItemCaption(methodType[0], methodType[1]);
		}

		this.typeFilter.select("");

		this.groupFilter.addItem("");
		this.groupFilter.setItemCaption("", "All Crop Reproductive Systems");
		for (String[] methodGroup : ProgramMethodsView.METHOD_GROUPS) {
			this.groupFilter.addItem(methodGroup[0]);
			this.groupFilter.setItemCaption(methodGroup[0], methodGroup[1]);
		}
		this.groupFilter.select("");

		/* INITIALIZE TABLE DATA */
		this.favoritesTableContainer = new BeanItemContainer<MethodView>(MethodView.class, this.presenter.getSavedProgramMethods());
		this.setAvailableTableContainer(new BeanItemContainer<MethodView>(MethodView.class, this.presenter.getFilteredResults(
				this.groupFilter.getValue().toString(), this.typeFilter.getValue().toString(), "")));

		this.resultCountLbl.setValue("Result: " + this.getAvailableTableContainer().size());

		this.availableTable.setContainerDataSource(this.getAvailableTableContainer());
		this.updateNoOfEntries(this.availTotalEntriesLabel, this.availableTable);

		this.favoritesTable.setContainerDataSource(this.favoritesTableContainer);
		this.updateNoOfEntries(this.favTotalEntriesLabel, this.favoritesTable);

		/* SETUP TABLE FIELDS */
		this.setupTableFields(this.availableTable);
		this.setupTableFields(this.favoritesTable);

		this.retrieveMethodClasses();
	}

	public Map<Integer, String> retrieveMethodClasses() {
		// set lookup map for classes
		if (this.classMap == null || this.classMap.isEmpty()) {
			this.classMap = this.presenter.getMethodClasses();
		}
		return this.classMap;
	}

	private void setupTableFields(Table table) {
		table.setVisibleColumns(ProgramMethodsView.TABLE_COLUMNS.keySet().toArray());
		table.setColumnHeaders(ProgramMethodsView.TABLE_COLUMNS.values().toArray(new String[] {}));

		for (String col : ProgramMethodsView.TABLE_COLUMN_SIZES.keySet()) {
			table.setColumnWidth(col, ProgramMethodsView.TABLE_COLUMN_SIZES.get(col));

			if (ProgramMethodsView.TABLE_COLUMN_SIZES.get(col) < 75) {
				table.setColumnAlignment(col, Table.ALIGN_CENTER);
			}

		}

		table.setColumnExpandRatio(ProgramMethodsView.TABLE_COLUMNS.keySet().toArray()[2], 1.0F);
	}

	private void initializeLayout() {
		this.root = new VerticalLayout();
		this.root.setDebugId("root");
		this.root.setSpacing(false);
		this.root.setMargin(new Layout.MarginInfo(false, true, true, true));

		final Label availableMethodsTitle = new Label(this.messageSource.getMessage(Message.AVAILABLE_METHODS));
		availableMethodsTitle.setDebugId("availableMethodsTitle");
		availableMethodsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

		this.availableTable.setWidth("100%");
		this.favoritesTable.setWidth("100%");
		this.availableTable.setHeight("250px");
		this.favoritesTable.setHeight("250px");

		final HorizontalLayout availableTableBar = new HorizontalLayout();
		availableTableBar.setDebugId("availableTableBar");
		final HorizontalLayout favoritesTableBar = new HorizontalLayout();
		favoritesTableBar.setDebugId("favoritesTableBar");

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
		this.root.addComponent(availableMethodsTitle);
		this.root.addComponent(this.buildFilterForm());
		this.root.addComponent(this.buildLocationTableLabels(this.availTotalEntriesLabel, this.availSelectedEntriesLabel));
		this.root.addComponent(this.availableTable);
		this.root.addComponent(availableTableBar);
		this.root.addComponent(this.buildFavoriteTableTitle());
		this.root.addComponent(this.buildLocationTableLabels(this.favTotalEntriesLabel, this.favSelectedEntriesLabel));
		this.root.addComponent(this.favoritesTable);
		this.root.addComponent(favoritesTableBar);

		this.setCompositionRoot(this.root);
	}

	private HorizontalLayout buildLocationTableLabels(Label totalEntries, Label selectedEntries) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setDebugId("layout");
		layout.setSpacing(true);
		layout.setWidth("300px");

		layout.addComponent(totalEntries);
		layout.addComponent(selectedEntries);
		return layout;
	}

	private Component buildFavoriteTableTitle() {
		final HorizontalLayout layout = new HorizontalLayout();
		layout.setDebugId("layout");
		layout.setWidth("100%");
		layout.setMargin(true, false, false, false);

		final Label favoriteMethodsTitle = new Label(this.messageSource.getMessage(Message.FAVORITE_PROGRAM_METHODS));
		favoriteMethodsTitle.setDebugId("favoriteMethodsTitle");
		favoriteMethodsTitle.setStyleName(Bootstrap.Typography.H3.styleName());

		layout.addComponent(favoriteMethodsTitle);

		if (!this.cropOnly) {
			layout.addComponent(this.saveBtn);
		}

		layout.setExpandRatio(favoriteMethodsTitle, 1.0F);

		return layout;
	}

	private Component buildFilterForm() {
		this.groupFilter.setWidth("220px");
		this.typeFilter.setWidth("245px");

		final Label filterLbl = new Label("<b>Filter By:</b>&nbsp;", Label.CONTENT_XHTML);
		filterLbl.setDebugId("filterLbl");
		final Label searchLbl = new Label("<b>Search For:</b>&nbsp;", Label.CONTENT_XHTML);
		searchLbl.setDebugId("searchLbl");

		filterLbl.setSizeUndefined();
		searchLbl.setSizeUndefined();

		filterLbl.setStyleName("loc-filterlbl");
		searchLbl.setStyleName("loc-filterlbl");

		final CssLayout container = new CssLayout();
		container.setDebugId("container");
		container.addStyleName("loc-filter-bar");
		container.setSizeUndefined();
		container.setWidth("100%");

		final HorizontalLayout field1 = new HorizontalLayout();
		field1.setDebugId("field1");
		field1.addStyleName(ProgramMethodsView.FIELD);
		field1.setSpacing(true);
		field1.setSizeUndefined();
		field1.addComponent(searchLbl);
		field1.addComponent(this.searchField);
		field1.addComponent(this.searchGoBtn);

		this.searchGoBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4839268740583678422L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				ProgramMethodsView.this.doMethodSearch();
			}
		});

		container.addComponent(field1);

		final HorizontalLayout field2 = new HorizontalLayout();
		field2.setDebugId("field2");
		field2.addStyleName(ProgramMethodsView.FIELD);
		field2.setSpacing(true);
		field2.setSizeUndefined();
		field2.addComponent(filterLbl);
		field2.addComponent(this.typeFilter);

		final HorizontalLayout field3 = new HorizontalLayout();
		field3.setDebugId("field3");
		field3.addStyleName(ProgramMethodsView.FIELD);
		field3.setSpacing(true);
		field3.setSizeUndefined();
		field3.addComponent(this.groupFilter);

		HorizontalLayout filterContainer = new HorizontalLayout();
		filterContainer.setDebugId("filterContainer");
		filterContainer.setSpacing(true);
		filterContainer.setStyleName("pull-right");
		filterContainer.setSizeUndefined();

		filterContainer.addComponent(field2);
		filterContainer.addComponent(field3);

		container.addComponent(filterContainer);

		this.resultCountLbl = new Label("");
		this.resultCountLbl.setDebugId("resultCountLbl");
		this.resultCountLbl.setStyleName("loc-resultcnt");

		return container;
	}

	private Component buildPageTitle() {
		final VerticalLayout layout = new VerticalLayout();
		layout.setDebugId("layout");
		layout.setMargin(new Layout.MarginInfo(false, false, true, false));
		layout.setWidth("100%");

		final HorizontalLayout titleContainer = new HorizontalLayout();
		titleContainer.setDebugId("titleContainer");
		titleContainer.setSizeUndefined();
		titleContainer.setWidth("100%");
		titleContainer.setMargin(true, false, false, false);

		final Label heading =
				new Label("<span class='bms-methods' style='color: #B8D432; font-size: 23px'></span>&nbsp;Breeding Methods",
						Label.CONTENT_XHTML);
		heading.setStyleName(Bootstrap.Typography.H4.styleName());

		titleContainer.addComponent(heading);

		if (!this.cropOnly) {
			titleContainer.addComponent(this.addNewMethodsBtn);
			titleContainer.setComponentAlignment(this.addNewMethodsBtn, Alignment.MIDDLE_RIGHT);
		}

		String content =
				"To choose Favorite Breeding Methods for your program, select entries from the Available Breeding Methods table at the top and drag them onto the lower table.";

		if (!this.cropOnly) {
			content += " You can also add any new methods that you need for managing your program.";
		}

		final Label headingDesc = new Label(content);
		headingDesc.setDebugId("headingDesc");

		layout.addComponent(titleContainer);
		layout.addComponent(headingDesc);

		return layout;
	}

	public void addRow(MethodView item, boolean atAvailableTable, Integer index) {
		if (index != null) {
			if (atAvailableTable) {
				this.getAvailableTableContainer().addItemAt(index, item);

			} else {
				this.getAvailableTableContainer().addItemAt(index, item);
				this.favoritesTableContainer.addItemAt(index, item);
			}
		} else {
			if (atAvailableTable) {
				this.getAvailableTableContainer().addItem(item);

			} else {
				this.getAvailableTableContainer().addItem(item);
				this.favoritesTableContainer.addItem(item);
			}
		}
		this.updateNoOfEntries();
	}

	private void updateNoOfEntries() {
		this.updateNoOfEntries(this.favTotalEntriesLabel, this.favoritesTable);
		this.updateNoOfEntries(this.availTotalEntriesLabel, this.availableTable);
	}

	private void doMethodSearch() {
		this.getAvailableTableContainer().removeAllItems();
		this.getAvailableTableContainer().addAll(
				this.presenter.getFilteredResults(this.groupFilter.getValue().toString(), this.typeFilter.getValue().toString(),
						this.searchField.getValue().toString()));

		this.resultCountLbl.setValue("Results: " + this.availableTable.getContainerDataSource().getItemIds().size() + " items");
		this.updateNoOfEntries(this.availTotalEntriesLabel, this.availableTable);
		this.updateSelectedNoOfEntries(this.availSelectedEntriesLabel, this.availableTable);
	}

	private void initializeActions() {

		this.editMethodListener = new Button.ClickListener() {

			private static final long serialVersionUID = -6938448455072630697L;

			@Override
			@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERADMIN')")
			public void buttonClick(Button.ClickEvent event) {
				event.getComponent()
						.getWindow()
						.addWindow(
								new EditBreedingMethodsWindow(ProgramMethodsView.this.presenter, (MethodView) event.getButton().getData()));
			}
		};

		this.addNewMethodsBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6467414813762353127L;

			@Override
			@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERADMIN')")
			public void buttonClick(Button.ClickEvent event) {
				event.getComponent().getWindow().addWindow(new AddBreedingMethodsWindow(ProgramMethodsView.this));
			}
		});

		Property.ValueChangeListener filterAction = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 8914267618640094463L;

			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				ProgramMethodsView.this.doMethodSearch();
			}
		};

		this.searchField.addListener(filterAction);
		this.groupFilter.addListener(filterAction);
		this.typeFilter.addListener(filterAction);

		this.availableSelectAll.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -2842000142630845841L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {

				if ((Boolean) ((CheckBox) clickEvent.getComponent()).getValue()) {
					ProgramMethodsView.this.availableTable.setValue(ProgramMethodsView.this.availableTable.getItemIds());
				} else {
					ProgramMethodsView.this.availableTable.setValue(null);
				}

			}
		});

		this.favoriteSelectAll.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 2545400532783269974L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				if ((Boolean) ((CheckBox) clickEvent.getComponent()).getValue()) {
					ProgramMethodsView.this.favoritesTable.setValue(ProgramMethodsView.this.favoritesTable.getItemIds());
				} else {
					ProgramMethodsView.this.favoritesTable.setValue(null);
				}

			}
		});

		this.addToFavoriteBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8741702112016745513L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				ProgramMethodsView.this.moveSelectedItems(ProgramMethodsView.this.availableTable, ProgramMethodsView.this.favoritesTable);
				ProgramMethodsView.this.availableSelectAll.setValue(false);
			}
		});

		this.removeToFavoriteBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -7252226977128582313L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				ProgramMethodsView.this.moveSelectedItems(ProgramMethodsView.this.favoritesTable, ProgramMethodsView.this.availableTable);
				ProgramMethodsView.this.favoriteSelectAll.setValue(false);
			}
		});

		this.saveBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1484296798437173855L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (ProgramMethodsView.this.presenter.saveFavoriteBreedingMethod(ProgramMethodsView.this.favoritesTableContainer
						.getItemIds())) {
					MessageNotifier.showMessage(event.getComponent().getWindow(),
							ProgramMethodsView.this.messageSource.getMessage(Message.SUCCESS),
							ProgramMethodsView.this.messageSource.getMessage(Message.METHODS_SUCCESSFULLY_CONFIGURED));
				}
			}
		});

		this.availableTable.addActionHandler(new Handler() {

			private static final long serialVersionUID = 4185416256388693137L;

			@Override
			public Action[] getActions(Object target, Object sender) {
				return new Action[] {ProgramMethodsView.copyBreedingMethodAction};
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action == ProgramMethodsView.copyBreedingMethodAction) {
					ProgramMethodsView.this.availableTable.getParent().getWindow()
							.addWindow(new AddBreedingMethodsWindow(ProgramMethodsView.this, ((MethodView) target).copyMethodView()));
				}

			}

		});

		this.favoritesTable.addActionHandler(new Handler() {

			private static final long serialVersionUID = 6635300830598766541L;

			@Override
			public Action[] getActions(Object target, Object sender) {
				return new Action[] {ProgramMethodsView.copyBreedingMethodAction};
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action == ProgramMethodsView.copyBreedingMethodAction) {
					ProgramMethodsView.this.favoritesTable.getParent().getWindow()
							.addWindow(new AddBreedingMethodsWindow(ProgramMethodsView.this, ((MethodView) target).copyMethodView()));
				}

			}

		});
	}

	protected void updateNoOfEntries(Label totalEntries, Table table) {
		int count = table.getItemIds().size();

		totalEntries.setValue(this.messageSource.getMessage(Message.TOTAL_ENTRIES) + ": " + "  <b>" + count + "</b>");
	}

	private void updateSelectedNoOfEntries(Label selectedEntries, Table table) {
		Collection<?> selectedItems = (Collection<?>) table.getValue();
		int count = selectedItems.size();

		selectedEntries.setValue("<i>" + this.messageSource.getMessage(Message.SELECTED) + ": " + "  <b>" + count + "</b></i>");
	}

	protected void refreshTable() {
		// do table repaint
		this.availableTable.requestRepaint();
		this.availableTable.refreshRowCache();

		this.favoritesTable.requestRepaint();
		this.favoritesTable.refreshRowCache();

	}

	public BeanItemContainer<MethodView> getAvailableTableContainer() {
		return this.availableTableContainer;
	}

	public void setAvailableTableContainer(BeanItemContainer<MethodView> availableTableContainer) {
		this.availableTableContainer = availableTableContainer;
	}

	public SimpleResourceBundleMessageSource getMessageSource() {
		return this.messageSource;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
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

	public BeanItemContainer<MethodView> getFavoritesTableContainer() {
		return this.favoritesTableContainer;
	}

	public void setFavoritesTableContainer(BeanItemContainer<MethodView> favoritesTableContainer) {
		this.favoritesTableContainer = favoritesTableContainer;
	}

	public Label getAvailTotalEntriesLabel() {
		return this.availTotalEntriesLabel;
	}

	public void setAvailTotalEntriesLabel(Label availTotalEntriesLabel) {
		this.availTotalEntriesLabel = availTotalEntriesLabel;
	}

	public Label getFavTotalEntriesLabel() {
		return this.favTotalEntriesLabel;
	}

	public void setFavTotalEntriesLabel(Label favTotalEntriesLabel) {
		this.favTotalEntriesLabel = favTotalEntriesLabel;
	}

}
