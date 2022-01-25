package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VariableTableComponentTest {

	@Test
	public void testTableWithNoCheckboxAndNoSelectAllOption() throws Exception {

		final VariableTableComponent variableTableComponent =
				new VariableTableComponent(new String[] {VariableTableComponent.NAME_COLUMN, VariableTableComponent.DESCRIPTION_COLUMN},
						false);
		variableTableComponent.afterPropertiesSet();

		final List<Object> visibleColumns = Arrays.asList(variableTableComponent.getTable().getVisibleColumns());

		assertTrue(variableTableComponent.getCheckboxValuesMap().isEmpty());
		assertTrue(variableTableComponent.getVariableTableItems().isEmpty());
		assertEquals(2, visibleColumns.size());
		assertTrue(visibleColumns.contains(VariableTableComponent.NAME_COLUMN));
		assertTrue(visibleColumns.contains(VariableTableComponent.DESCRIPTION_COLUMN));
		assertEquals(1, variableTableComponent.getComponentCount());

	}

	@Test
	public void testTableWithCheckboxAndSelectAllOption() throws Exception {

		final VariableTableComponent variableTableComponent = new VariableTableComponent(
				new String[] {VariableTableComponent.CHECKBOX_COLUMN, VariableTableComponent.NAME_COLUMN,
						VariableTableComponent.DESCRIPTION_COLUMN, VariableTableComponent.SCALE_NAME_COLUMN}, true);
		variableTableComponent.afterPropertiesSet();

		final List<Object> visibleColumns = Arrays.asList(variableTableComponent.getTable().getVisibleColumns());

		assertTrue(variableTableComponent.getCheckboxValuesMap().isEmpty());
		assertTrue(variableTableComponent.getVariableTableItems().isEmpty());
		assertEquals(4, visibleColumns.size());
		assertTrue(visibleColumns.contains(VariableTableComponent.CHECKBOX_COLUMN));
		assertTrue(visibleColumns.contains(VariableTableComponent.DESCRIPTION_COLUMN));
		assertTrue(visibleColumns.contains(VariableTableComponent.NAME_COLUMN));
		assertTrue(visibleColumns.contains(VariableTableComponent.SCALE_NAME_COLUMN));
		assertEquals(2, variableTableComponent.getComponentCount());

	}

	@Test
	public void testLoadData() throws Exception {

		final VariableTableComponent variableTableComponent =
				new VariableTableComponent(new String[] {VariableTableComponent.NAME_COLUMN, VariableTableComponent.DESCRIPTION_COLUMN},
						false);
		variableTableComponent.afterPropertiesSet();

		final String entryNo = "ENTRY_NO";
		final String plotNo = "PLOT_NO";

		final List<DMSVariableType> variableTypeList = new ArrayList<>();
		final DMSVariableType entryNoVariable =
				DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(TermId.ENTRY_NO);
		entryNoVariable.setLocalName(entryNo);
		final DMSVariableType plotNoVariable = DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(TermId.PLOT_NO);
		plotNoVariable.setLocalName(plotNo);

		variableTypeList.add(entryNoVariable);
		variableTypeList.add(plotNoVariable);

		variableTableComponent.loadData(variableTypeList);

		assertEquals(2, variableTableComponent.getCheckboxValuesMap().size());
		assertEquals(2, variableTableComponent.getVariableTableItems().size());
		assertFalse(variableTableComponent.getCheckboxValuesMap().get(entryNo));
		assertFalse(variableTableComponent.getCheckboxValuesMap().get(plotNo));

	}

	@Test
	public void testToggleCheckbox() throws Exception {

		final String variable = "Variable";
		final int variableId = 1;
		final Table table = Mockito.mock(Table.class);

		final VariableTableComponent variableTableComponent = new VariableTableComponent(
				new String[] {VariableTableComponent.CHECKBOX_COLUMN, VariableTableComponent.NAME_COLUMN,
						VariableTableComponent.DESCRIPTION_COLUMN, VariableTableComponent.SCALE_NAME_COLUMN}, true);
		variableTableComponent.afterPropertiesSet();
		variableTableComponent.setTable(table);

		final VariableTableItem item = new VariableTableItem();
		item.setName(variable);
		item.setId(variableId);
		final BeanItem<VariableTableItem> beanItem = new BeanItem<VariableTableItem>(item);
		when(table.getItem(Mockito.anyInt())).thenReturn(beanItem);

		variableTableComponent.toggleCheckbox(variableId, false,true);

		assertFalse(item.getActive());
		assertTrue(item.isDisabled());
		assertFalse(variableTableComponent.getCheckboxValuesMap().get(variable));

		verify(table).refreshRowCache();

	}

	@Test
	public void testToggleCheckboxVariableItemIsNonNumeric() throws Exception {

		final String variable = "Variable";
		final int variableId = 1;
		final Table table = Mockito.mock(Table.class);

		final VariableTableComponent variableTableComponent = new VariableTableComponent(
				new String[] {VariableTableComponent.CHECKBOX_COLUMN, VariableTableComponent.NAME_COLUMN,
						VariableTableComponent.DESCRIPTION_COLUMN, VariableTableComponent.SCALE_NAME_COLUMN}, true);
		variableTableComponent.afterPropertiesSet();
		variableTableComponent.setTable(table);

		final VariableTableItem item = new VariableTableItem();
		item.setName(variable);
		item.setId(variableId);
		item.setNonNumeric(true);
		final BeanItem<VariableTableItem> beanItem = new BeanItem<VariableTableItem>(item);
		when(table.getItem(Mockito.anyInt())).thenReturn(beanItem);

		variableTableComponent.toggleCheckbox(variableId, true,true);

		assertFalse(item.getActive());
		assertTrue(item.isDisabled());
		assertFalse(variableTableComponent.getCheckboxValuesMap().get(variable));

		verify(table).refreshRowCache();

	}

	@Test
	public void testResetAllCheckbox() throws Exception {

		final String variable1 = "Variable1";
		final int variableId1 = 1;
		final String variable2 = "Variable2";
		final int variableId2 = 2;

		final Table table = Mockito.mock(Table.class);

		final VariableTableComponent variableTableComponent = new VariableTableComponent(
				new String[] {VariableTableComponent.CHECKBOX_COLUMN, VariableTableComponent.NAME_COLUMN,
						VariableTableComponent.DESCRIPTION_COLUMN, VariableTableComponent.SCALE_NAME_COLUMN}, true);
		variableTableComponent.afterPropertiesSet();
		variableTableComponent.setTable(table);

		final VariableTableItem item1 = new VariableTableItem();
		item1.setName(variable1);
		item1.setId(variableId1);
		final VariableTableItem item2 = new VariableTableItem();
		item2.setName(variable2);
		item2.setId(variableId2);
		final BeanItem<VariableTableItem> beanItem1 = new BeanItem<VariableTableItem>(item1);
		final BeanItem<VariableTableItem> beanItem2 = new BeanItem<VariableTableItem>(item2);

		final List itemIds = new ArrayList();
		itemIds.add(variableId1);
		itemIds.add(variableId2);
		when(table.getItemIds()).thenReturn(itemIds);
		when(table.getItem(variableId1)).thenReturn(beanItem1);
		when(table.getItem(variableId2)).thenReturn(beanItem2);

		variableTableComponent.resetAllCheckbox();

		assertFalse(item1.getActive());
		assertFalse(item1.isDisabled());
		assertFalse(variableTableComponent.getCheckboxValuesMap().get(variable1));
		assertFalse(item2.getActive());
		assertFalse(item2.isDisabled());
		assertFalse(variableTableComponent.getCheckboxValuesMap().get(variable2));

		verify(table).refreshRowCache();

	}

	@Test
	public void testTransformVariableTypeToVariableTableItem() {

		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);
		final DMSVariableType dmsVariableType = createDMSVariableType();
		final VariableTableItem variableTableItem = variableTableComponent.transformVariableTypeToVariableTableItem(dmsVariableType);

		assertEquals(dmsVariableType.getId(), variableTableItem.getId().intValue());
		assertEquals(dmsVariableType.getLocalName(), variableTableItem.getName());
		assertEquals(dmsVariableType.getLocalDescription(), variableTableItem.getDescription());
		assertEquals(dmsVariableType.getStandardVariable().getScale().getName(), variableTableItem.getScale());
		assertEquals(dmsVariableType.getStandardVariable().getMethod().getName(), variableTableItem.getMethod());
		assertEquals(dmsVariableType.getStandardVariable().getProperty().getName(), variableTableItem.getProperty());
		assertEquals(dmsVariableType.getStandardVariable().getDataType().getName(), variableTableItem.getDatatype());
		assertTrue(variableTableItem.getActive());
		assertFalse(variableTableItem.isNonNumeric());

	}

	@Test
	public void testTransformVariableTypeToVariableTableItemNonNumericVariable() {

		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);
		final DMSVariableType dmsVariableType = createDMSVariableType();
		dmsVariableType.getStandardVariable().setDataType(new Term(TermId.CATEGORICAL_VARIABLE.getId(), "Categorical Variable", ""));
		final VariableTableItem variableTableItem = variableTableComponent.transformVariableTypeToVariableTableItem(dmsVariableType);

		assertEquals(dmsVariableType.getId(), variableTableItem.getId().intValue());
		assertEquals(dmsVariableType.getLocalName(), variableTableItem.getName());
		assertEquals(dmsVariableType.getLocalDescription(), variableTableItem.getDescription());
		assertEquals(dmsVariableType.getStandardVariable().getScale().getName(), variableTableItem.getScale());
		assertEquals(dmsVariableType.getStandardVariable().getMethod().getName(), variableTableItem.getMethod());
		assertEquals(dmsVariableType.getStandardVariable().getProperty().getName(), variableTableItem.getProperty());
		assertEquals(dmsVariableType.getStandardVariable().getDataType().getName(), variableTableItem.getDatatype());
		assertFalse(variableTableItem.getActive());
		assertTrue(variableTableItem.isNonNumeric());

	}

	@Test
	public void testTableItemDescriptionGenerator() {
		final Table table = Mockito.mock(Table.class);
		final VariableTableItem item = new VariableTableItem();
		item.setName("Name");
		item.setProperty("Property");
		item.setScale("Scale");
		item.setMethod("Method");
		item.setDatatype("Data type");

		final BeanContainer<Integer, VariableTableItem> container = Mockito.mock(BeanContainer.class);
		final BeanItem<VariableTableItem> beanItem = new BeanItem<VariableTableItem>(item);
		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);
		variableTableComponent.setTable(table);
		when(table.getContainerDataSource()).thenReturn(container);
		when(container.getItem(Mockito.any(Object.class))).thenReturn(beanItem);

		final VariableTableComponent.TableItemDescriptionGenerator tableItemDescriptionGenerator =
				variableTableComponent.new TableItemDescriptionGenerator();

		final String result = tableItemDescriptionGenerator.generateDescription(null, 1, null);

		Assert.assertEquals(
				"<span class=\"gcp-table-header-bold\">Name</span><br><span>Property:</span> Property<br><span>Scale:</span> Scale<br><span>Method:</span> Method<br><span>Data Type:</span> Data type",
				result);

	}

	@Test
	public void testTableColumnGeneratorItemIsActive() {

		final Table table = Mockito.mock(Table.class);
		final VariableTableItem item = new VariableTableItem();
		item.setName("Name");
		item.setProperty("Property");
		item.setScale("Scale");
		item.setMethod("Method");
		item.setDatatype("Data type");
		item.setDisabled(false);
		item.setActive(true);

		final BeanContainer<Integer, VariableTableItem> container = Mockito.mock(BeanContainer.class);
		final BeanItem<VariableTableItem> beanItem = new BeanItem<VariableTableItem>(item);
		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);
		variableTableComponent.setTable(table);
		when(table.getContainerDataSource()).thenReturn(container);
		when(container.getItem(Mockito.any(Object.class))).thenReturn(beanItem);

		final VariableTableComponent.TableColumnGenerator tableColumnGenerator = variableTableComponent.new TableColumnGenerator();

		final Object generatedObject = tableColumnGenerator.generateCell(null, 1, null);
		final CheckBox checkBox = (CheckBox) generatedObject;

		assertTrue(checkBox.isImmediate());
		assertTrue(checkBox.isVisible());
		assertFalse(checkBox.getListeners(Property.ValueChangeEvent.class).isEmpty());
		assertTrue(checkBox.isEnabled());
		assertTrue((Boolean) checkBox.getValue());

	}

	@Test
	public void testTableColumnGeneratorItemIsInactive() {

		final Table table = Mockito.mock(Table.class);
		final VariableTableItem item = new VariableTableItem();
		item.setName("Name");
		item.setProperty("Property");
		item.setScale("Scale");
		item.setMethod("Method");
		item.setDatatype("Data type");
		item.setDisabled(true);
		item.setActive(false);

		final BeanContainer<Integer, VariableTableItem> container = Mockito.mock(BeanContainer.class);
		final BeanItem<VariableTableItem> beanItem = new BeanItem<VariableTableItem>(item);
		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);
		variableTableComponent.setTable(table);
		when(table.getContainerDataSource()).thenReturn(container);
		when(container.getItem(Mockito.any(Object.class))).thenReturn(beanItem);

		final VariableTableComponent.TableColumnGenerator tableColumnGenerator = variableTableComponent.new TableColumnGenerator();

		final Object generatedObject = tableColumnGenerator.generateCell(null, 1, null);
		final CheckBox checkBox = (CheckBox) generatedObject;

		assertTrue(checkBox.isImmediate());
		assertTrue(checkBox.isVisible());
		assertFalse(checkBox.getListeners(Property.ValueChangeEvent.class).isEmpty());
		assertFalse(checkBox.isEnabled());
		assertFalse((Boolean) checkBox.getValue());

	}

	@Test
	public void testCheckBoxListenerChecked() {

		final String variable = "Variable";
		final VariableTableItem item = new VariableTableItem();
		item.setName(variable);
		final VariableTableComponent.SelectionChangedListener selectionChangedListener =
				Mockito.mock(VariableTableComponent.SelectionChangedListener.class);
		final CheckBox selectAll = Mockito.mock(CheckBox.class);
		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);
		variableTableComponent.addSelectionChangedListener(selectionChangedListener);
		variableTableComponent.setSelectAll(selectAll);
		final VariableTableComponent.CheckBoxListener checkBoxListener = variableTableComponent.new CheckBoxListener(item);
		final Property.ValueChangeEvent event = Mockito.mock(Property.ValueChangeEvent.class);
		final Property property = Mockito.mock(Property.class);
		when(event.getProperty()).thenReturn(property);
		when(property.getValue()).thenReturn(true);

		checkBoxListener.valueChange(event);

		assertTrue(item.getActive());
		verify(selectionChangedListener).onSelectionChanged(item);
		verifyZeroInteractions(selectAll);

	}

	@Test
	public void testCheckBoxListenerUnchecked() {

		final String variable = "Variable";
		final VariableTableItem item = new VariableTableItem();
		item.setName(variable);
		final VariableTableComponent.SelectionChangedListener selectionChangedListener =
				Mockito.mock(VariableTableComponent.SelectionChangedListener.class);
		final CheckBox selectAll = Mockito.mock(CheckBox.class);
		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);
		variableTableComponent.addSelectionChangedListener(selectionChangedListener);
		variableTableComponent.setSelectAll(selectAll);
		final VariableTableComponent.CheckBoxListener checkBoxListener = variableTableComponent.new CheckBoxListener(item);
		final Property.ValueChangeEvent event = Mockito.mock(Property.ValueChangeEvent.class);
		final Property property = Mockito.mock(Property.class);
		when(event.getProperty()).thenReturn(property);
		when(property.getValue()).thenReturn(false);

		checkBoxListener.valueChange(event);

		assertFalse(item.getActive());
		verify(selectionChangedListener).onSelectionChanged(item);
		verify(selectAll).removeListener(ArgumentMatchers.<Property.ValueChangeListener>isNull());
		verify(selectAll).setValue(false);
		verify(selectAll).addListener(ArgumentMatchers.<Property.ValueChangeListener>isNull());

	}

	@Test
	public void testSelectAllListenerChecked() {

		final Table table = Mockito.mock(Table.class);
		final VariableTableComponent.SelectAllChangedListener selectAllChangedListener =
				Mockito.mock(VariableTableComponent.SelectAllChangedListener.class);

		final BeanContainer<Integer, VariableTableItem> container = Mockito.mock(BeanContainer.class);

		final VariableTableItem item1 = this.createVariableTableItem(1);
		final VariableTableItem item2 = this.createVariableTableItem(2);
		final BeanItem<VariableTableItem> beanItem1 = new BeanItem<VariableTableItem>(item1);
		final BeanItem<VariableTableItem> beanItem2 = new BeanItem<VariableTableItem>(item2);
		final Map<String, Boolean> checkboxValuesMap = new HashMap<>();
		checkboxValuesMap.put(item1.getName(), false);
		checkboxValuesMap.put(item2.getName(), false);

		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);

		variableTableComponent.setTable(table);
		variableTableComponent.getCheckboxValuesMap().putAll(checkboxValuesMap);
		variableTableComponent.addSelectAllChangedListener(selectAllChangedListener);

		when(table.getContainerDataSource()).thenReturn(container);
		when(container.getItemIds()).thenReturn(Arrays.asList(1, 2));
		when(container.getItem(1)).thenReturn(beanItem1);
		when(container.getItem(2)).thenReturn(beanItem2);

		final Property.ValueChangeEvent event = Mockito.mock(Property.ValueChangeEvent.class);
		final Property property = Mockito.mock(Property.class);
		when(event.getProperty()).thenReturn(property);
		when(property.getValue()).thenReturn(true);

		final VariableTableComponent.SelectAllListener selectAllListener = variableTableComponent.new SelectAllListener();

		selectAllListener.valueChange(event);

		assertTrue(item1.getActive());
		assertTrue(item2.getActive());
		assertTrue(variableTableComponent.getCheckboxValuesMap().get(item1.getName()));
		assertTrue(variableTableComponent.getCheckboxValuesMap().get(item2.getName()));
		verify(table).refreshRowCache();
		verify(selectAllChangedListener).onSelectionChanged(Mockito.anyBoolean());

	}

	@Test
	public void testSelectAllListenerCheckedButItemIsDisabled() {

		final Table table = Mockito.mock(Table.class);
		final VariableTableComponent.SelectAllChangedListener selectAllChangedListener =
				Mockito.mock(VariableTableComponent.SelectAllChangedListener.class);

		final BeanContainer<Integer, VariableTableItem> container = Mockito.mock(BeanContainer.class);

		final VariableTableItem item1 = this.createVariableTableItem(1);
		item1.setDisabled(true);
		final VariableTableItem item2 = this.createVariableTableItem(2);
		item2.setDisabled(true);
		final BeanItem<VariableTableItem> beanItem1 = new BeanItem<VariableTableItem>(item1);
		final BeanItem<VariableTableItem> beanItem2 = new BeanItem<VariableTableItem>(item2);
		final Map<String, Boolean> checkboxValuesMap = new HashMap<>();
		checkboxValuesMap.put(item1.getName(), false);
		checkboxValuesMap.put(item2.getName(), false);

		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);

		variableTableComponent.setTable(table);
		variableTableComponent.getCheckboxValuesMap().putAll(checkboxValuesMap);
		variableTableComponent.addSelectAllChangedListener(selectAllChangedListener);

		when(table.getContainerDataSource()).thenReturn(container);
		when(container.getItemIds()).thenReturn(Arrays.asList(1, 2));
		when(container.getItem(1)).thenReturn(beanItem1);
		when(container.getItem(2)).thenReturn(beanItem2);

		final Property.ValueChangeEvent event = Mockito.mock(Property.ValueChangeEvent.class);
		final Property property = Mockito.mock(Property.class);
		when(event.getProperty()).thenReturn(property);
		when(property.getValue()).thenReturn(true);

		final VariableTableComponent.SelectAllListener selectAllListener = variableTableComponent.new SelectAllListener();

		selectAllListener.valueChange(event);

		assertFalse(item1.getActive());
		assertFalse(item2.getActive());
		assertTrue(variableTableComponent.getCheckboxValuesMap().get(item1.getName()));
		assertTrue(variableTableComponent.getCheckboxValuesMap().get(item2.getName()));
		verify(table).refreshRowCache();
		verify(selectAllChangedListener).onSelectionChanged(Mockito.anyBoolean());

	}

	@Test
	public void testSelectAllListenerUnchecked() {

		final Table table = Mockito.mock(Table.class);
		final VariableTableComponent.SelectAllChangedListener selectAllChangedListener =
				Mockito.mock(VariableTableComponent.SelectAllChangedListener.class);

		final BeanContainer<Integer, VariableTableItem> container = Mockito.mock(BeanContainer.class);

		final VariableTableItem item1 = this.createVariableTableItem(1);
		final VariableTableItem item2 = this.createVariableTableItem(2);
		final BeanItem<VariableTableItem> beanItem1 = new BeanItem<VariableTableItem>(item1);
		final BeanItem<VariableTableItem> beanItem2 = new BeanItem<VariableTableItem>(item2);
		final Map<String, Boolean> checkboxValuesMap = new HashMap<>();
		checkboxValuesMap.put(item1.getName(), true);
		checkboxValuesMap.put(item2.getName(), true);

		final VariableTableComponent variableTableComponent = new VariableTableComponent(null);

		variableTableComponent.setTable(table);
		variableTableComponent.getCheckboxValuesMap().putAll(checkboxValuesMap);
		variableTableComponent.addSelectAllChangedListener(selectAllChangedListener);

		when(table.getContainerDataSource()).thenReturn(container);
		when(container.getItemIds()).thenReturn(Arrays.asList(1, 2));
		when(container.getItem(1)).thenReturn(beanItem1);
		when(container.getItem(2)).thenReturn(beanItem2);

		final Property.ValueChangeEvent event = Mockito.mock(Property.ValueChangeEvent.class);
		final Property property = Mockito.mock(Property.class);
		when(event.getProperty()).thenReturn(property);
		when(property.getValue()).thenReturn(false);

		final VariableTableComponent.SelectAllListener selectAllListener = variableTableComponent.new SelectAllListener();

		selectAllListener.valueChange(event);

		assertFalse(item1.getActive());
		assertFalse(item2.getActive());
		assertFalse(variableTableComponent.getCheckboxValuesMap().get(item1.getName()));
		assertFalse(variableTableComponent.getCheckboxValuesMap().get(item2.getName()));
		verify(table).refreshRowCache();
		verify(selectAllChangedListener).onSelectionChanged(Mockito.anyBoolean());

	}

	private DMSVariableType createDMSVariableType() {

		final DMSVariableType dmsVariableType = new DMSVariableType();
		final Integer rank = 1;
		final String variableName = "Variable";
		final String variableDescription = "Variable Description";
		final String scaleName = "Scale";
		final String methodName = "Method";
		final String propertyName = "Property";
		final String datatypeName = "Numeric Variable";

		dmsVariableType.setRank(rank);
		dmsVariableType.setLocalName(variableName);
		dmsVariableType.setLocalDescription(variableDescription);
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setScale(new Term(2, scaleName, ""));
		standardVariable.setMethod(new Term(3, methodName, ""));
		standardVariable.setProperty(new Term(4, propertyName, ""));
		standardVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), datatypeName, ""));
		dmsVariableType.setStandardVariable(standardVariable);
		return dmsVariableType;

	}

	private VariableTableItem createVariableTableItem(final int id) {

		final VariableTableItem item = new VariableTableItem();
		item.setId(id);
		item.setName("Name");
		item.setProperty("Property");
		item.setScale("Scale");
		item.setMethod("Method");
		item.setDatatype("Data type");
		item.setDisabled(false);
		item.setActive(false);
		return item;
	}

}
