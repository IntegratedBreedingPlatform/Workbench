
package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import java.util.Collection;
import java.util.Iterator;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;

public class GxeCheckBoxGroupListener implements ValueChangeListener {

	private static final long serialVersionUID = 1L;

	private final String[] stringList;
	private final GxeTable table;
	private final CheckBox source;

	public GxeCheckBoxGroupListener(String[] stringList, GxeTable table, CheckBox source) {
		this.stringList = stringList;
		this.table = table;
		this.source = source;
	}

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		// TODO Auto-generated method stub

		if ("All rows".equalsIgnoreCase(this.source.getCaption())) {
			Collection<?> items = this.table.getContainerDataSource().getItemIds();
			for (Iterator<?> myitems = items.iterator(); myitems.hasNext();) {

				Integer key = (Integer) myitems.next();
				Item item = this.table.getContainerDataSource().getItem(key);

				if (key > 1) {
					GxeCheckBoxGroup ba = (GxeCheckBoxGroup) item.getItemProperty(" ").getValue();
					ba.setValue((Boolean) event.getProperty().getValue());
				}

			}

		} else if ("All columns".equalsIgnoreCase(this.source.getCaption())) {

			Collection<?> cols = this.table.getContainerDataSource().getItem(1).getItemPropertyIds();

			for (Iterator<?> myItems = cols.iterator(); myItems.hasNext();) {

				Object obj = this.table.getContainerDataSource().getItem(1).getItemProperty(myItems.next()).getValue();
				if (obj instanceof CheckBox) {
					((CheckBox) obj).setValue(event.getProperty().getValue());
				}

			}
		} else if (" ".equals(this.source.getCaption())) {

			((GxeCheckBoxGroup) this.source.getParent()).setValue((Boolean) event.getProperty().getValue());

		}

		this.table.requestRepaintAll();

	}

}
