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

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.programmethods.AddBreedingMethodsWindow;
import org.generationcp.ibpworkbench.ui.programmethods.BreedingMethodForm;
import org.generationcp.ibpworkbench.ui.programmethods.MethodView;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jeffrey Morales
 */

@Configurable
@Deprecated
public class SaveNewBreedingMethodAction implements ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(SaveNewBreedingMethodAction.class);
    private static final long serialVersionUID = 1L;

    private BreedingMethodForm newBreedingMethodForm;

    private AddBreedingMethodsWindow window;

    private Component projectBreedingMethodsPanel;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    public SaveNewBreedingMethodAction(BreedingMethodForm newBreedingMethodForm, AddBreedingMethodsWindow window, Component projectBreedingMethodsPanel) {
        this.newBreedingMethodForm = newBreedingMethodForm;
        this.window = window;
        this.projectBreedingMethodsPanel = projectBreedingMethodsPanel;

    }

    @Override
    public void buttonClick(ClickEvent event) {

        try {
            newBreedingMethodForm.commit();
        } catch (Validator.EmptyValueException e) {
            MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getLocalizedMessage());
            return;
        }

        @SuppressWarnings("unchecked")
        BeanItem<MethodView> breedingMethodBean = (BeanItem<MethodView>) newBreedingMethodForm.getItemDataSource();
        MethodView breedingMethod = breedingMethodBean.getBean();

        if (StringUtils.isEmpty(breedingMethod.getMtype())) {
            MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), "Please select a Generation Advancement Type");
            return;
        }

        if (!sessionData.getUniqueBreedingMethods().contains(breedingMethod.getMname())) {

            sessionData.getUniqueBreedingMethods().add(breedingMethod.getMname());

            Integer nextKey = sessionData.getProjectBreedingMethodData().keySet().size() + 1;

            nextKey = nextKey * -1;

            MethodView newBreedingMethod = new MethodView();

            newBreedingMethod.setMname(breedingMethod.getMname());
            newBreedingMethod.setMdesc(breedingMethod.getMdesc());
            newBreedingMethod.setMcode(breedingMethod.getMcode());
            newBreedingMethod.setMgrp(breedingMethod.getMgrp());
            newBreedingMethod.setMtype(breedingMethod.getMtype());
            newBreedingMethod.setGeneq(breedingMethod.getGeneq());

            newBreedingMethod.setMid(nextKey);

            sessionData.getProjectBreedingMethodData().put(nextKey, newBreedingMethod);

            LOG.info(sessionData.getProjectBreedingMethodData().toString());

            if (sessionData.getUserData() != null) {
                newBreedingMethod.setUser(sessionData.getUserData().getUserid());
            }

            newBreedingMethod.setMattr(0);
            newBreedingMethod.setMprgn(0);
            newBreedingMethod.setReference(0);


            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            newBreedingMethod.setMdate(Integer.parseInt(sdf.format(new Date())));
            newBreedingMethod.setMfprg(0);

            //TODO: MOVE THIS CODE TO PRESENTER, GET PRESENTER REFERENCE FROM HERE
            GermplasmDataManager gdm = managerFactoryProvider.getManagerFactoryForProject(sessionData.getLastOpenedProject()).getGermplasmDataManager();

            try {
                newBreedingMethod.setMid(gdm.addMethod(newBreedingMethod.copy()));

            } catch (Exception e) { // we might have null exception, better be prepared
                e.printStackTrace();
            }

            newBreedingMethod.setIsnew(true);

            if (projectBreedingMethodsPanel instanceof ProgramMethodsView) {
                ProgramMethodsView pv = (ProgramMethodsView) projectBreedingMethodsPanel;

                pv.addRow(newBreedingMethod, false, 0);
            }

            window.getParent().removeWindow(window);

        }

    }
}
