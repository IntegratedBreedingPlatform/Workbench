package org.generationcp.ibpworkbench.ui.dashboard.preview;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class GermplasmListPreviewPresenter implements InitializingBean {
    private final GermplasmListPreview view;
    private static final Logger LOG = LoggerFactory.getLogger(GermplasmListPreviewPresenter.class);

    private Project project;

    @Autowired
    private WorkbenchDataManager manager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public GermplasmListPreviewPresenter(GermplasmListPreview view,Project project) {
        this.view = view;

        this.project = project;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }



}
