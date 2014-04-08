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
package org.generationcp.ibpworkbench.ui.form;

import java.util.Arrays;

import com.vaadin.ui.*;
import org.generationcp.ibpworkbench.model.BreedingMethodModel;
import org.generationcp.ibpworkbench.model.formfieldfactory.BreedingMethodFormFieldFactory;

import com.vaadin.data.util.BeanItem;


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
public class AddBreedingMethodForm extends Form{

    /**
     *
     */
    private static final long serialVersionUID = -3649453194910730855L;

    private BeanItem<BreedingMethodModel> breedingMethodBean;

    private BreedingMethodModel method;

    public AddBreedingMethodForm(BreedingMethodModel method) {
        this.method = method;

        assemble();
    }

    protected void assemble() {

        initializeComponents();
        initializeLayout();
    }

    protected void initializeLayout() {

    }

    protected void initializeComponents() {
        final FormLayout fl = new FormLayout();

        fl.setSpacing(true);

        breedingMethodBean = new BeanItem<BreedingMethodModel>(method);
        setItemDataSource(breedingMethodBean);

        setComponentError(null);
        setFormFieldFactory(new BreedingMethodFormFieldFactory());

        setVisibleItemProperties(Arrays.asList(
                new String[] { "methodName", "methodCode", "methodDescription", "methodType", "methodGroup" }));

        setWriteThrough(false);
        setInvalidCommitted(false);
        setValidationVisibleOnCommit(false);


        this.setLayout(fl);


        this.getLayout();
    }

    /*
    @Override
    protected void attachField(Object propertyId, Field field) {
        if("methodName".equals(propertyId)) {
            grid.addComponent(field, 0, 1);
        } else if ("methodDescription".equals(propertyId)) {
            grid.addComponent(field, 0, 2);
        } else if ("methodType".equals(propertyId)) {
            grid.addComponent(field, 0, 3);
        } else if ("methodGroup".equals(propertyId)) {
            grid.addComponent(field, 0, 4);
        } else if ("methodCode".equals(propertyId)) {
            grid.addComponent(field, 0, 5);
        }
    }
    */

}
