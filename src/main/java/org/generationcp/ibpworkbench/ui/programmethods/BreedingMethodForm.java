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
package org.generationcp.ibpworkbench.ui.programmethods;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.formfieldfactory.BreedingMethodFormFieldFactory;
import org.generationcp.ibpworkbench.ui.programmethods.MethodView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Arrays;
import java.util.Map;


/**
 * <b>Description</b>: Custom form for adding Locations.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Jeffrey Morales
 * <br>
 * <b>File Created</b>: August 20, 2012
 */
@Configurable
public class BreedingMethodForm extends Form {

    /**
     *
     */
    private static final long serialVersionUID = -3649453194910730855L;
    private final MethodView modelBean;
    private GridLayout grid;
    private Map<Integer,String> classMap;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;


    public BreedingMethodForm(Map<Integer,String> classMap) {
    	this.classMap = classMap;
    	modelBean = new MethodView();
        assemble();
        
    }

    public BreedingMethodForm(Map<Integer,String> classMap, MethodView methodView)
    {
    	this.classMap = classMap;
    	modelBean = methodView;
        assemble();
    }


    protected void assemble() {
        initializeComponents();
        initializeLayout();
    }

    protected void initializeLayout() {

    }

    protected void initializeComponents() {
        grid = new GridLayout(2,6);
        grid.setSpacing(true);
        grid.setMargin(new Layout.MarginInfo(true,false,false,false));
        this.setLayout(grid);

        setItemDataSource(new BeanItem<MethodView>(modelBean));

        setComponentError(null);
        setFormFieldFactory(new BreedingMethodFormFieldFactory(classMap));
        this.setVisibleItemProperties(Arrays.asList(
                new String[] { "mname", "mcode", "mdesc", "mtype", "mgrp", "geneq" }));


        setWriteThrough(false);
        setInvalidCommitted(false);
        setValidationVisibleOnCommit(false);

        //grid.setWidth("100%");


    }


    @Override
    protected void attachField(Object propertyId, Field field) {
        field.setStyleName("hide-caption");
        field.setCaption(null);
        if("mname".equals(propertyId)) {
            grid.addComponent(field, 1, 0);
        } else if ("mcode".equals(propertyId)) {
            grid.addComponent(field, 1, 1);
        } else if ("mdesc".equals(propertyId)) {
            grid.addComponent(field, 1, 2);
        } else if ("mtype".equals(propertyId)) {
            grid.addComponent(field, 1, 3);
        } else if ("mgrp".equals(propertyId)) {
            grid.addComponent(field, 1, 4);
        } else if ("geneq".equals(propertyId)) {
            grid.addComponent(field,1,5);
        }
    }

    @Override
    public  void attach() {

        if (grid.getComponent(0, 0) == null)
        grid.addComponent(createLabel(messageSource.getMessage(Message.BREED_METH_NAME),true),0,0);

        if (grid.getComponent(0, 1) == null)
        grid.addComponent(createLabel(messageSource.getMessage(Message.BREED_METH_CODE),true),0,1);

        if (grid.getComponent(0, 2) == null)
        grid.addComponent(createLabel(messageSource.getMessage(Message.BREED_METH_DESC),true),0,2);

        if (grid.getComponent(0, 3) == null)
        grid.addComponent(createLabel(messageSource.getMessage(Message.BREED_METH_TYPE),true),0,3);

        if (grid.getComponent(0, 4) == null)
        grid.addComponent(createLabel(messageSource.getMessage(Message.BREED_METH_GRP)),0,4);

        if (grid.getComponent(0, 5) == null)
        grid.addComponent(createLabel(messageSource.getMessage(Message.BREED_METH_CLASS),true),0,5);

        super.attach();

    }

    private Label createLabel(String caption) {
        return this.createLabel(caption,false);
    }

        private Label createLabel(String caption,boolean required){

        Label label = new Label();
        label.setContentMode(Label.CONTENT_XHTML);
        label.setWidth("220px");

        if (!required)
            label.setValue(String.format("<b>%s</b>",caption));
        else
            label.setValue(String.format("<b>%s</b> <span style='color: red'>*</span>",caption));

        return label;
    }
}
