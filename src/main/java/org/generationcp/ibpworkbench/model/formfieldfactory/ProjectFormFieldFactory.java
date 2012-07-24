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

package org.generationcp.ibpworkbench.model.formfieldfactory;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

@Configurable
public class ProjectFormFieldFactory extends DefaultFieldFactory{
    
    private static final Logger LOG = LoggerFactory.getLogger(ProjectFormFieldFactory.class);
    private static final long serialVersionUID = 1L;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
        Field field = super.createField(item, propertyId, uiContext);

        if ("projectName".equals(propertyId)) {
            TextField tf = (TextField) field;
            tf.setRequired(true);
            tf.setRequiredError("Please enter a Project Name.");
            tf.addValidator(new StringLengthValidator("Project Name must be 3-255 characters", 3, 255, false));
        } else if ("targetDueDate".equals(propertyId)) {
            field.setRequired(true);
            field.setRequiredError("Please enter a Target Due Date.");
        } else if ("template".equals(propertyId)) {
            BeanContainer<Long, WorkflowTemplate> templateContainer = new BeanContainer<Long, WorkflowTemplate>(WorkflowTemplate.class);
            templateContainer.setBeanIdProperty("templateId");

            //TODO: Verify the try-catch flow
            try {
                List<WorkflowTemplate> templateList = workbenchDataManager.getWorkflowTemplates();

                for (WorkflowTemplate template : templateList) {
                    templateContainer.addBean(template);
                }

                ComboBox comboBox = new ComboBox("Workflow Template");
                comboBox.setContainerDataSource(templateContainer);
                comboBox.setItemCaptionPropertyId("name");
                comboBox.setRequired(true);
                comboBox.setRequiredError("Please enter a Workflow Template.");

                return comboBox;
            } catch (QueryException e) {
                LOG.error("Error encountered while getting workflow templates", e);
            }
        }
        else if ("cropType".equals(propertyId)) {
            BeanItemContainer<CropType> beanItemContainer = new BeanItemContainer<CropType>(CropType.class);
            for (CropType cropType : CropType.values()) {
                beanItemContainer.addBean(cropType);
            }
            
            ComboBox comboBox = new ComboBox("Crop");
            comboBox.setContainerDataSource(beanItemContainer);
            comboBox.setItemCaptionPropertyId("cropName");
            comboBox.setRequired(true);
            comboBox.setRequiredError("Please select a Crop.");
            
            return comboBox;
        }

        return field;
    }
}
