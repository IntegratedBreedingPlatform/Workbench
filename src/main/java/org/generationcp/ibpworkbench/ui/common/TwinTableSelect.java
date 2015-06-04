
package org.generationcp.ibpworkbench.ui.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.middleware.pojos.BeanFormState;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;

public class TwinTableSelect<T extends BeanFormState> extends GridLayout {

	private static final long serialVersionUID = 1L;

	private Object[] visibleColumns;
	private String[] columnHeaders;

	private Table tableLeft;
	private Table tableRight;

	private Label lblLeftColumnCaption;
	private Label lblRightColumnCaption;

	private Button btnLinkLeft;
	private Button btnLinkRight;

	private CheckBox chkSelectAllLeft;
	private CheckBox chkSelectAllRight;

	private final Class<? super T> type;

	public TwinTableSelect(Class<? super T> class1) {

		super(2, 3);

		this.type = class1;

		this.initializeComponents();
		this.initializeActions();
		this.initializeLayout();

	}

	private void initializeComponents() {

		this.setImmediate(true);
		this.setSizeFull();
		this.setSpacing(true);

		this.lblLeftColumnCaption = new Label();
		this.lblLeftColumnCaption.setSizeFull();
		this.lblRightColumnCaption = new Label();
		this.lblRightColumnCaption.setSizeFull();

		this.btnLinkLeft = new Button();
		this.btnLinkLeft.setStyleName("link");
		this.btnLinkLeft.setImmediate(true);
		this.btnLinkRight = new Button();
		this.btnLinkRight.setStyleName("link");
		this.btnLinkRight.setImmediate(true);

		this.chkSelectAllLeft = new CheckBox("Select All");
		this.chkSelectAllRight = new CheckBox("Select All");
		this.chkSelectAllLeft.setImmediate(true);
		this.chkSelectAllRight.setImmediate(true);

		this.tableLeft = this.buildCustomTable();
		this.tableLeft.setData("left");
		this.tableRight = this.buildCustomTable();
		this.tableRight.setData("right");

		this.setLeftContainerDataSource(new BeanItemContainer<T>(this.type));
		this.setRightContainerDataSource(new BeanItemContainer<T>(this.type));

	}

	private void initializeActions() {

		this.chkSelectAllLeft.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Table table = TwinTableSelect.this.getTableLeft();

				Boolean val = (Boolean) ((CheckBox) event.getComponent()).getValue();
				for (Object itemId : TwinTableSelect.this.getTableLeft().getItemIds()) {
					((T) itemId).setActive(val);

					if (val && ((T) itemId).isEnabled()) {
						table.select(itemId);
					} else {
						table.unselect(itemId);
					}
				}

				table.requestRepaint();
				table.refreshRowCache();

			}
		});

		this.chkSelectAllRight.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Table table = TwinTableSelect.this.getTableRight();
				Boolean val = (Boolean) ((CheckBox) event.getComponent()).getValue();
				for (Object itemId : TwinTableSelect.this.getTableRight().getItemIds()) {
					((T) itemId).setActive(val);

					if (val && ((T) itemId).isEnabled()) {
						table.select(itemId);
					} else {
						table.unselect(itemId);
					}
				}

				table.requestRepaint();
				table.refreshRowCache();

			}
		});

	}

	private void initializeLayout() {

		this.lblLeftColumnCaption.setStyleName(Bootstrap.Typography.H3.styleName());
		this.lblRightColumnCaption.setStyleName(Bootstrap.Typography.H3.styleName());

		this.addComponent(this.lblLeftColumnCaption, 0, 0);
		this.addComponent(this.lblRightColumnCaption, 1, 0);

		this.addComponent(this.getTableLeft(), 0, 1);
		this.addComponent(this.getTableRight(), 1, 1);

		HorizontalLayout hLayout1 = new HorizontalLayout();
		hLayout1.addComponent(this.chkSelectAllLeft);
		hLayout1.addComponent(this.btnLinkLeft);
		hLayout1.setComponentAlignment(this.btnLinkLeft, Alignment.TOP_RIGHT);
		hLayout1.setSizeFull();
		this.addComponent(hLayout1, 0, 2);

		HorizontalLayout hLayout2 = new HorizontalLayout();
		hLayout2.addComponent(this.chkSelectAllRight);
		hLayout2.addComponent(this.btnLinkRight);
		hLayout2.setComponentAlignment(this.btnLinkRight, Alignment.TOP_RIGHT);
		hLayout2.setSizeFull();
		this.addComponent(hLayout2, 1, 2);

		this.setColumnExpandRatio(0, 1.0f);
		this.setColumnExpandRatio(1, 1.0f);
		this.setRowExpandRatio(0, 1f);
		this.setRowExpandRatio(1, 10f);
		this.setRowExpandRatio(2, 1f);

		this.setStyleName("cell-style");

	}

	private void setLeftContainerDataSource(Container container) {
		this.getTableLeft().setContainerDataSource(container);
		if (this.visibleColumns != null) {
			this.setVisibleColumns(this.visibleColumns);
		}
		if (this.columnHeaders != null) {
			this.setColumnHeaders(this.columnHeaders);
		}
	}

	private void setRightContainerDataSource(Container container) {
		this.getTableRight().setContainerDataSource(container);
		if (this.visibleColumns != null) {
			this.setVisibleColumns(this.visibleColumns);
		}
		if (this.columnHeaders != null) {
			this.setColumnHeaders(this.columnHeaders);
		}
	}

	private Table buildCustomTable() {
		final Table table = new Table();
		final Property.ValueChangeListener tableValueChangeListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				Table source = (Table) event.getProperty();

				for (Object itemId : source.getItemIds()) {
					T bean = (T) itemId;
					bean.setActive(false);
				}

				Collection<T> sourceItemIds = (Collection<T>) source.getValue();
				for (T itemId : sourceItemIds) {
					if (itemId.isEnabled()) {
						itemId.setActive(true);
					}
				}

				((Table) event.getProperty()).requestRepaint();
				((Table) event.getProperty()).refreshRowCache();

			}

		};

		table.addListener(tableValueChangeListener);

		table.addGeneratedColumn("select", new Table.ColumnGenerator() {

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(final Table source, final Object itemId, Object columnId) {

				BeanItemContainer<T> container = (BeanItemContainer<T>) source.getContainerDataSource();
				final T bean = container.getItem(itemId).getBean();

				final CheckBox checkBox = new CheckBox();
				checkBox.setImmediate(true);
				checkBox.setVisible(true);
				checkBox.addListener(new Button.ClickListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						Boolean val = (Boolean) ((CheckBox) event.getComponent()).getValue();

						bean.setActive(val);
						if (val && bean.isEnabled()) {
							source.select(itemId);
						} else {
							source.unselect(itemId);
							if (source.getData().equals("left")) {
								TwinTableSelect.this.chkSelectAllLeft.setValue(val);
							} else {
								TwinTableSelect.this.chkSelectAllRight.setValue(val);
							}
						}

					}
				});

				checkBox.setEnabled(bean.isEnabled());

				if (bean.isActive() && bean.isEnabled()) {
					checkBox.setValue(true);
				} else {
					checkBox.setValue(false);

				}

				return checkBox;

			}

		});

		table.setDragMode(TableDragMode.MULTIROW);
		table.setDropHandler(new DropHandler() {

			private static final long serialVersionUID = 1L;

			@Override
			public void drop(DragAndDropEvent dropEvent) {

				DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();

				if (t.getSourceComponent() == dropEvent.getTargetDetails().getTarget()) {
					return;
				}

				Table source = (Table) t.getSourceComponent();
				Table target = (Table) dropEvent.getTargetDetails().getTarget();

				Object itemIdOver = t.getItemId();

				// temporarily disable the value change listener to avoid conflict
				target.removeListener(tableValueChangeListener);

				Set<Object> sourceItemIds = (Set<Object>) source.getValue();
				for (Object itemId : sourceItemIds) {
					if (((T) itemId).isEnabled()) {
						source.removeItem(itemId);
						target.addItem(itemId);
					}
				}

				boolean selectedItemIsDisabled = false;
				if (sourceItemIds.size() == 1) {
					if (!((T) sourceItemIds.iterator().next()).isEnabled()) {
						selectedItemIsDisabled = true;
					}
				}

				if (itemIdOver != null && (sourceItemIds.size() <= 0 || selectedItemIsDisabled)) {
					if (((T) itemIdOver).isEnabled()) {
						source.removeItem(itemIdOver);
						target.addItem(itemIdOver);
					}
				}

				target.addListener(tableValueChangeListener);

			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptItem.ALL;
			}
		});

		final Action actionAddToProgramMembers = new Action("Add Selected Items");
		final Action actionRemoveFromProgramMembers = new Action("Remove Selected Items");
		final Action actionSelectAll = new Action("Select All");
		final Action actionDeSelectAll = new Action("De-select All");

		table.addActionHandler(new Action.Handler() {

			@Override
			public Action[] getActions(final Object target, final Object sender) {

				if (table.getData().toString().equals("left")) {
					return new Action[] {actionAddToProgramMembers, actionSelectAll, actionDeSelectAll};
				} else {
					return new Action[] {actionRemoveFromProgramMembers, actionSelectAll, actionDeSelectAll};
				}

			}

			@Override
			public void handleAction(final Action action, final Object sender, final Object target) {
				if (actionSelectAll == action) {
					if (table.getData().toString().equals("left")) {
						TwinTableSelect.this.chkSelectAllLeft.setValue(true);
						TwinTableSelect.this.chkSelectAllLeft.click();
					} else {
						TwinTableSelect.this.chkSelectAllRight.setValue(true);
						TwinTableSelect.this.chkSelectAllRight.click();
					}

				} else if (actionDeSelectAll == action) {
					if (table.getData().toString().equals("left")) {
						TwinTableSelect.this.chkSelectAllLeft.setValue(false);
						TwinTableSelect.this.chkSelectAllLeft.click();
					} else {
						TwinTableSelect.this.chkSelectAllRight.setValue(false);
						TwinTableSelect.this.chkSelectAllRight.click();
					}
				} else if (actionAddToProgramMembers == action) {
					TwinTableSelect.this.addCheckedSelectedItems();
				} else if (actionRemoveFromProgramMembers == action) {
					TwinTableSelect.this.removeCheckedSelectedItems();
				}

			}

		});

		table.setColumnWidth("select", 20);
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setSizeFull();
		table.setNullSelectionAllowed(false);
		table.setImmediate(true);
		table.setReadOnly(false);

		return table;
	}

	public void setContainerDataSource(Container container) {
		this.getTableLeft().setContainerDataSource(container);
		this.getTableRight().removeAllItems();
		if (this.visibleColumns != null) {
			this.setVisibleColumns(this.visibleColumns);
		}
		if (this.columnHeaders != null) {
			this.setColumnHeaders(this.columnHeaders);
		}
		this.chkSelectAllLeft.setValue(false);
		this.chkSelectAllRight.setValue(false);
	}

	public void setVisibleColumns(Object[] visibleColumns) {
		this.visibleColumns = visibleColumns;
		this.getTableLeft().setVisibleColumns(visibleColumns);
		this.getTableRight().setVisibleColumns(visibleColumns);
	}

	public void setColumnHeaders(String[] columnHeaders) {
		this.columnHeaders = columnHeaders;
		this.getTableLeft().setColumnHeaders(columnHeaders);
		this.getTableRight().setColumnHeaders(columnHeaders);
	}

	public void select(Object itemId) {
		this.getTableLeft().removeItem(itemId);
		this.getTableRight().addItem(itemId);
	}

	public void unselect(Object itemId) {
		this.getTableRight().removeItem(itemId);
		this.getTableLeft().addItem(itemId);
	}

	public void addItem(Object itemId) {
		this.getTableLeft().addItem(itemId);
		this.select(itemId);
	}

	public Set<T> getValue() {
		Set ret = new HashSet<T>();
		for (Object itemId : this.getTableRight().getItemIds()) {
			ret.add(itemId);
		}
		return ret;
	}

	public void setValue(Set<T> values) {
		this.getTableRight().removeAllItems();
		for (T itemId : values) {
			this.getTableRight().addItem(itemId);
		}

	}

	public Label getLeftColumnCaption() {
		return this.lblLeftColumnCaption;
	}

	public void setLeftColumnCaption(String leftColumnCaption) {
		this.lblLeftColumnCaption.setValue(leftColumnCaption);
	}

	public void setLeftLinkCaption(String caption) {
		this.btnLinkLeft.setCaption(caption);
	}

	public void addLeftLinkListener(Button.ClickListener listener) {
		this.btnLinkLeft.addListener(listener);
	}

	public Label getRightColumnCaption() {
		return this.lblRightColumnCaption;
	}

	public void setRightColumnCaption(String rightColumnCaption) {
		this.lblRightColumnCaption.setValue(rightColumnCaption);
	}

	public void setRightLinkCaption(String caption) {
		this.btnLinkRight.setCaption(caption);
	}

	public void addRightLinkListener(Button.ClickListener listener) {
		this.btnLinkRight.addListener(listener);
	}

	public Table getTableLeft() {
		return this.tableLeft;
	}

	public Table getTableRight() {
		return this.tableRight;
	}

	public void removeAllSelectedItems() {

		for (Object itemId : this.getTableRight().getItemIds()) {
			this.getTableLeft().addItem(itemId);
		}
		this.getTableRight().removeAllItems();

	}

	public void removeCheckedSelectedItems() {

		for (Object itemId : (Set<Object>) this.getTableRight().getValue()) {
			if (((T) itemId).isActive() && ((T) itemId).isEnabled()) {
				((T) itemId).setActive(false);
				this.getTableLeft().addItem(itemId);
				this.getTableRight().removeItem(itemId);

			}
		}

	}

	public void addCheckedSelectedItems() {

		for (Object itemId : (Set<Object>) this.getTableLeft().getValue()) {
			if (((T) itemId).isActive() && ((T) itemId).isEnabled()) {
				((T) itemId).setActive(false);
				this.getTableRight().addItem(itemId);
				this.getTableLeft().removeItem(itemId);

			}
		}

	}

	public void addAllToSelectedItems() {

		for (Object itemId : this.getTableLeft().getItemIds()) {
			this.select(itemId);
		}

	}

}
