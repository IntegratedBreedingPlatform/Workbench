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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.ComboBox;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.commons.vaadin.validator.ValidationUtil;
import org.generationcp.ibpworkbench.ui.project.create.ProjectBasicDetailsComponent;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Iterator;

@Configurable
public class CropTypeComboAction implements ValueChangeListener, NewItemHandler{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(CropTypeComboAction.class);

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private ProjectBasicDetailsComponent sourceComponent;
    
    private ComboBox cropTypeComboBox;

    // Used to keep track if the selected item is the last item added to crop type combo box
    private boolean cropTypeComboBoxLastAdded;

    // Used to keep track of the last value selected
    private String lastValue;

    public CropTypeComboAction() {
        cropTypeComboBoxLastAdded = false;
        lastValue = null;
    }

    public CropTypeComboAction(ComboBox cropTypeComboBox) {
        this();
        setCropTypeComboBox(cropTypeComboBox);
    }
    
    public CropTypeComboAction(ProjectBasicDetailsComponent sourceComponent, ComboBox cropTypeComboBox) {
        this(cropTypeComboBox);
        setSourceComponent(sourceComponent);
    }

    public ProjectBasicDetailsComponent getSourceComponent() {
        return sourceComponent;
    }

    public void setSourceComponent(ProjectBasicDetailsComponent sourceComponent) {
        this.sourceComponent = sourceComponent;
    }

    public ComboBox getCropTypeComboBox() {
        return cropTypeComboBox;
    }

    public void setCropTypeComboBox(ComboBox cropTypeComboBox) {
        this.cropTypeComboBox = cropTypeComboBox;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        
        String value = null;        
        if ((CropType) event.getProperty().getValue() != null) {
           value = ((CropType) event.getProperty().getValue()).getCropName();
        }
        
        boolean sameAsLastValue = lastValue == null ? value == null : lastValue.equals(value);

        if (sameAsLastValue || cropTypeComboBoxLastAdded) {
            return;
        } else {
            lastValue = value;
            cropTypeComboBoxLastAdded = false;
        }

        // set the visible properties again, so that all fields gets renewed
        if (sourceComponent != null){
            sourceComponent.refreshVisibleItems();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addNewItem(String newItemCaption) {
        // if not yet in the database
        if (!cropTypeComboBox.containsId(newItemCaption)) {
            Iterator<Validator> validatorIterator = cropTypeComboBox.getValidators().iterator();
            while (validatorIterator.hasNext()) {
                Validator validator = validatorIterator.next();
                try {
                    validator.validate(newItemCaption);
                } catch (InvalidValueException e) {
                    LOG.error("Invalid value for Crop: " + newItemCaption , e);

                    MessageNotifier.showRequiredFieldError(cropTypeComboBox.getWindow(), ValidationUtil.getMessageFor(e));
                    cropTypeComboBox.focus();
                    return;
                }
            }
    
            // add crop to database
            CropType cropType = new CropType(newItemCaption);
            cropType.setDbName("ibdbv2_" + newItemCaption.toLowerCase().replaceAll("\\s+", "_") + "_merged");
            cropTypeComboBoxLastAdded = true;
            ((BeanItemContainer<CropType>) cropTypeComboBox.getContainerDataSource()).addBean(cropType);

            // set the combo box value to the newly added crop
            cropTypeComboBox.setValue(cropType);

            if (sourceComponent != null){
                sourceComponent.refreshVisibleItems();
            }
        }
    }
}
