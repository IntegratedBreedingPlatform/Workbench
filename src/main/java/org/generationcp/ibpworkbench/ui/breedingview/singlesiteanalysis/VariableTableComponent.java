package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class VariableTableComponent extends VerticalLayout implements InitializingBean, IBPWorkbenchLayout {

	private static final long serialVersionUID = 1L;
	public static final String CHECKBOX_COLUMN = "checkbox";
	public static final String NAME_COLUMN = "name";
	public static final String DESCRIPTION_COLUMN = "description";
	public static final String SCALE_NAME_COLUMN = "scale";
	public static final String CHECKBOX_COLUMN_HEADER = "<span class='glyphicon glyphicon-ok'></span>";
	public static final String NAME_COLUMN_HEADER = "Name";
	public static final String DESCRIPTION_COLUMN_HEADER = "Description";
	public static final String SCALE_COLUMN_HEADER = "Scale";


	public interface SelectionChangedListener {

		public void onSelectionChanged(final VariableTableItem variableTableItem);
	}


	public interface SelectAllChangedListener {

		public void onSelectionChanged(boolean isChecked);
	}


	private final Map<String, Boolean> checkboxValuesMap = new HashMap<>();
	private final List<VariableTableItem> variableTableItems = new ArrayList<>();
	private final String[] visibleColumns;
	private boolean enableSelectAll = true;

	private Property.ValueChangeListener selectAllListener;

	private SelectionChangedListener selectionChangedListener = new SelectionChangedListener() {

		@Override
		public void onSelectionChanged(final VariableTableItem variableTableItem) {
			// do nothing
		}
	};

	private SelectAllChangedListener selectAllChangedListener = new SelectAllChangedListener() {

		@Override
		public void onSelectionChanged(boolean isChecked) {
			// do nothing
		}
	};

	private CheckBox selectAll;
	private Table table;

	public VariableTableComponent(final String[] visibleColumns) {
		super();
		this.visibleColumns = visibleColumns;
	}

	public VariableTableComponent(final String[] visibleColumns, final boolean enableSelectAll) {
		super();
		this.visibleColumns = visibleColumns;
		this.enableSelectAll = enableSelectAll;
	}

	@Override
	public void instantiateComponents() {

		this.table = new Table();
		this.table.setDebugId("table");
		this.table.setImmediate(true);
		this.table.setWidth("100%");
		this.table.setHeight("400px");
		this.table.setColumnExpandRatio(CHECKBOX_COLUMN, 0.5f);
		this.table.setColumnExpandRatio(NAME_COLUMN, 1);
		this.table.setColumnExpandRatio(DESCRIPTION_COLUMN, 4);
		this.table.setColumnExpandRatio(SCALE_NAME_COLUMN, 1);
		this.table.setColumnHeader(CHECKBOX_COLUMN, CHECKBOX_COLUMN_HEADER);
		this.table.setColumnHeader(NAME_COLUMN, NAME_COLUMN_HEADER);
		this.table.setColumnHeader(DESCRIPTION_COLUMN, DESCRIPTION_COLUMN_HEADER);
		this.table.setColumnHeader(SCALE_NAME_COLUMN, SCALE_COLUMN_HEADER);
		this.table.addGeneratedColumn(CHECKBOX_COLUMN, new TableColumnGenerator());
		this.table.setColumnWidth(CHECKBOX_COLUMN, 18);
		this.initializeTableContainer(new ArrayList<VariableTableItem>());

		this.selectAllListener = new SelectAllListener();

		this.selectAll = new CheckBox();
		this.selectAll.setDebugId("chkSelectAll");
		this.selectAll.setImmediate(true);
		this.selectAll.setCaption("Select All");

	}

	@Override
	public void initializeValues() {
		// do nothing
	}

	@Override
	public void addListeners() {
		this.table.setItemDescriptionGenerator(new TableItemDescriptionGenerator());
		this.selectAll.addListener(this.selectAllListener);
	}

	@Override
	public void layoutComponents() {
		addComponent(this.table);
		if (enableSelectAll) {
			addComponent(this.selectAll);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.addListeners();
		this.layoutComponents();
	}

	public void loadData(final List<DMSVariableType> variateList) {

		this.checkboxValuesMap.clear();
		this.variableTableItems.clear();

		for (final DMSVariableType variableType : variateList) {
			final VariableTableItem variableTableItem = this.transformVariableTypeToVariableTableItem(variableType);
			this.variableTableItems.add(variableTableItem);
			this.checkboxValuesMap.put(variableTableItem.getName(), variableTableItem.getActive());
		}
		this.initializeTableContainer(variableTableItems);
	}

	public void addSelectionChangedListener(final SelectionChangedListener listener) {
		this.selectionChangedListener = listener;
	}

	public void addSelectAllChangedListener(final SelectAllChangedListener listener) {
		this.selectAllChangedListener = listener;
	}

	public boolean someItemsAreSelected() {
		return checkboxValuesMap.containsValue(true);
	}

	public Map<String, Boolean> getCheckboxValuesMap() {
		return checkboxValuesMap;
	}

	public void toggleCheckbox(final Integer id, final boolean isActive, final boolean isDisabled) {
		final BeanItem<VariableTableItem> item = (BeanItem<VariableTableItem>) this.table.getItem(id);
		if (item != null) {
			final VariableTableItem variableTableItem = item.getBean();
			variableTableItem.setActive(variableTableItem.isNonNumeric() ? false : isActive);
			variableTableItem.setDisabled(isDisabled);
			checkboxValuesMap.put(variableTableItem.getName(), variableTableItem.getActive());
			this.table.refreshRowCache();
		}
	}

	public void resetAllCheckbox() {
		for (Object itemId : this.table.getItemIds()) {
			final BeanItem<VariableTableItem> item = (BeanItem<VariableTableItem>) this.table.getItem(itemId);
			final VariableTableItem variableTableItem = item.getBean();
			variableTableItem.setActive(false);
			variableTableItem.setDisabled(false);
			checkboxValuesMap.put(variableTableItem.getName(), variableTableItem.getActive());
		}
		this.table.refreshRowCache();
	}

	protected void initializeTableContainer(final List<VariableTableItem> variableTableItems) {

		final BeanContainer<Integer, VariableTableItem> container = new BeanContainer<Integer, VariableTableItem>(VariableTableItem.class);
		container.setBeanIdProperty("id");
		for (final VariableTableItem variableTableItem : variableTableItems) {
			container.addBean(variableTableItem);
		}
		this.table.setContainerDataSource(container);
		this.table.setVisibleColumns(visibleColumns);

	}

	protected VariableTableItem transformVariableTypeToVariableTableItem(final DMSVariableType variableType) {
		final VariableTableItem variableTableItem = new VariableTableItem();
		variableTableItem.setId(variableType.getId());
		variableTableItem.setName(variableType.getLocalName());
		variableTableItem.setDescription(variableType.getLocalDescription());
		variableTableItem.setScale(variableType.getStandardVariable().getScale().getName());
		variableTableItem.setMethod(variableType.getStandardVariable().getMethod().getName());
		variableTableItem.setProperty(variableType.getStandardVariable().getProperty().getName());
		variableTableItem.setDatatype(variableType.getStandardVariable().getDataType().getName());

		if (variableType.getStandardVariable().isNumeric()) {
			variableTableItem.setActive(true);
			if (variableType.getStandardVariable().isNumericCategoricalVariate()) {
				variableTableItem.setNumericCategoricalVariate(true);
			}
		} else {
			variableTableItem.setNonNumeric(true);
		}
		return variableTableItem;
	}

	protected final class TableItemDescriptionGenerator implements Table.ItemDescriptionGenerator {

		private static final String DESCRIPTION =
				"<span class=\"gcp-table-header-bold\">%s</span><br>" + "<span>Property:</span> %s<br><span>Scale:</span> %s<br>"
						+ "<span>Method:</span> %s<br><span>Data Type:</span> %s";

		@Override
		@SuppressWarnings("unchecked")
		public String generateDescription(final Component source, final Object itemId, final Object propertyId) {
			final BeanContainer<Integer, VariableTableItem> container =
					(BeanContainer<Integer, VariableTableItem>) VariableTableComponent.this.table.getContainerDataSource();
			final VariableTableItem vm = container.getItem(itemId).getBean();
			return String.format(this.DESCRIPTION, vm.getName(), vm.getProperty(), vm.getScale(), vm.getMethod(), vm.getDatatype());
		}
	}


	protected final class TableColumnGenerator implements Table.ColumnGenerator {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		@Override
		public Object generateCell(final Table source, final Object itemId, final Object columnId) {

			final BeanContainer<Integer, VariableTableItem> container =
					(BeanContainer<Integer, VariableTableItem>) VariableTableComponent.this.table.getContainerDataSource();
			final VariableTableItem variableTableItem = container.getItem(itemId).getBean();

			final CheckBox checkBox = new CheckBox();
			checkBox.setDebugId("checkBox");
			checkBox.setImmediate(true);
			checkBox.setVisible(true);
			checkBox.addListener(new CheckBoxListener(variableTableItem));
			checkBox.setEnabled(!variableTableItem.isDisabled());

			if (variableTableItem.getActive()) {
				checkBox.setValue(true);
			} else {
				checkBox.setValue(false);
			}
			return checkBox;
		}
	}


	protected final class CheckBoxListener implements Property.ValueChangeListener {

		private final VariableTableItem variableTableItem;
		private static final long serialVersionUID = 1L;

		protected CheckBoxListener(final VariableTableItem variableTableItem) {
			this.variableTableItem = variableTableItem;
		}

		@Override
		public void valueChange(final Property.ValueChangeEvent event) {
			final Boolean val = (Boolean) event.getProperty().getValue();
			checkboxValuesMap.put(this.variableTableItem.getName(), val);
			this.variableTableItem.setActive(val);
			if (!val) {
				VariableTableComponent.this.selectAll.removeListener(VariableTableComponent.this.selectAllListener);
				VariableTableComponent.this.selectAll.setValue(val);
				VariableTableComponent.this.selectAll.addListener(VariableTableComponent.this.selectAllListener);
			}
			VariableTableComponent.this.selectionChangedListener.onSelectionChanged(variableTableItem);
		}
	}


	protected final class SelectAllListener implements Property.ValueChangeListener {

		private static final long serialVersionUID = 344514045768824046L;

		@SuppressWarnings("unchecked")
		@Override
		public void valueChange(final Property.ValueChangeEvent event) {
			final Boolean val = (Boolean) event.getProperty().getValue();
			final BeanContainer<Integer, VariableTableItem> container =
					(BeanContainer<Integer, VariableTableItem>) VariableTableComponent.this.table.getContainerDataSource();
			for (final Object itemId : container.getItemIds()) {
				final VariableTableItem variateModel = container.getItem(itemId).getBean();
				variateModel.setActive(variateModel.isDisabled() ? false : val);
			}
			table.refreshRowCache();
			for (final Map.Entry<String, Boolean> entry : VariableTableComponent.this.checkboxValuesMap.entrySet()) {
				VariableTableComponent.this.checkboxValuesMap.put(entry.getKey(), val);
			}
			selectAllChangedListener.onSelectionChanged(val);
		}
	}

	public List<VariableTableItem> getVariableTableItems() {
		return variableTableItems;
	}

	public SelectionChangedListener getSelectionChangedListener() {
		return selectionChangedListener;
	}

	public SelectAllChangedListener getSelectAllChangedListener() {
		return selectAllChangedListener;
	}

	protected Table getTable() {
		return table;
	}

	protected void setTable(final Table table) {
		this.table = table;
	}

	protected void setSelectAll(final CheckBox selectAll) {
		this.selectAll = selectAll;
	}

}
