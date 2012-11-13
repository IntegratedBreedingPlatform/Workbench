/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.comp.window.AddBreedingMethodsWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.ComboBox;

@Configurable
public class MethodTypeComboAction implements ValueChangeListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MethodTypeComboAction.class);

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private AddBreedingMethodsWindow sourcePanel;
    private ComboBox methodTypeComboBox;

    // Used to keep track if the selected item is the last item added to crop type combo box
    private boolean methodTypeComboBoxLastAdded;

    // Used to keep track of the last value selected
    private String lastValue;

    public MethodTypeComboAction() {
        methodTypeComboBoxLastAdded = false;
        lastValue = null;
    }

    public MethodTypeComboAction(ComboBox methodTypeComboBox) {
        this();
        setMethodTypeComboBox(methodTypeComboBox);
    }

    public MethodTypeComboAction(AddBreedingMethodsWindow sourcePanel, ComboBox methodTypeComboBox) {
        this(methodTypeComboBox);
        setSourcePanel(sourcePanel);
    }

    public AddBreedingMethodsWindow getSourcePanel() {
        return sourcePanel;
    }

    public void setSourcePanel(AddBreedingMethodsWindow sourcePanel) {
        this.sourcePanel = sourcePanel;
    }

    public ComboBox getMethodTypeComboBox() {
        return methodTypeComboBox;
    }

    public void setMethodTypeComboBox(ComboBox methodTypeComboBox) {
        this.methodTypeComboBox = methodTypeComboBox;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        String value = (String) event.getProperty().getValue();
        boolean sameAsLastValue = lastValue == null ? value == null : lastValue.equals(value);

        if (sameAsLastValue || methodTypeComboBoxLastAdded) {
            return;
        } else {
            lastValue = value;
            methodTypeComboBoxLastAdded = false;
        }

        // set the visible properties again, so that all fields gets renewed
        sourcePanel.refreshVisibleItems();
    }

}
