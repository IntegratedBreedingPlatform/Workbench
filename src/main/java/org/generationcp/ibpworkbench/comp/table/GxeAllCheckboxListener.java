package org.generationcp.ibpworkbench.comp.table;

import java.util.Collection;
import java.util.Iterator;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;

public class GxeAllCheckboxListener implements ValueChangeListener {

	private static final long serialVersionUID = 1L;

	private String[] stringList;
	private Container container;
	public GxeAllCheckboxListener(String[] stringList, Container container) {
			this.stringList = stringList;
			this.container = container;
	}

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		// TODO Auto-generated method stub
		try {

			Collection<?> items = container.getItemIds();

			for (Iterator<?> myitems = items.iterator(); myitems.hasNext();) {

				Integer key = (Integer) myitems.next();
				Item item = container.getItem(key);

				for (String column : stringList) {
					if (column.equalsIgnoreCase(" ") && key.intValue() == 0)
						continue;
					if (column.equalsIgnoreCase("environment")
							|| column.equalsIgnoreCase("genotype"))
						continue;
					/** added this since we dont have any checkboxes inside
						anymore**/
					if (!column.equalsIgnoreCase(" ") && key.intValue() > 0)
						continue;
					
					CheckBox ba = (CheckBox) item.getItemProperty(column)
							.getValue();
					ba.setValue(event.getProperty().getValue());

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
