package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;

public class GxeRowCheckboxListener implements ValueChangeListener {

	private static final long serialVersionUID = 1L;
	private String[] stringList;
	private Container container;

	public GxeRowCheckboxListener(String[] stringList, Container container) {
		this.stringList = stringList;
		this.container = container;
	}

	@Override
	public void valueChange(
			com.vaadin.data.Property.ValueChangeEvent event) {
		// TODO Auto-generated method stub
				try {
					
					container.getItemIds();
					container.getItem(0);

				} catch (Exception e) {
					e.printStackTrace();
				}

	}

}
