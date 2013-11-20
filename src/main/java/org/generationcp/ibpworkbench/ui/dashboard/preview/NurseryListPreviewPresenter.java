package org.generationcp.ibpworkbench.ui.dashboard.preview;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class NurseryListPreviewPresenter implements InitializingBean {
    private final NurseryListPreview view;
    private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreviewPresenter.class);

    private Project project;

    @Autowired
    private WorkbenchDataManager manager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public NurseryListPreviewPresenter(NurseryListPreview view,Project project) {
        this.view = view;

        this.project = project;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }


}
