package org.generationcp.ibpworkbench.ui.systemlabel;

import com.vaadin.data.Container;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class SystemLabelFieldFactory extends DefaultFieldFactory {

	@Override
	public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {

		if (SystemLabelView.NAME.equals(propertyId.toString())) {
			TextField textField = new TextField();
			textField.setMaxLength(200);
			textField.setRequired(true);
			textField.setInvalidCommitted(true);
			textField.setSizeFull();
			return textField;
		}

		return super.createField(container, itemId, propertyId, uiContext);
	}

}
