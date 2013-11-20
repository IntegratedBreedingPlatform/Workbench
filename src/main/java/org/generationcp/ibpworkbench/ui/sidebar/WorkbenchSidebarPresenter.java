package org.generationcp.ibpworkbench.ui.sidebar;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WorkbenchSidebarPresenter implements InitializingBean {
    private final WorkbenchSidebar view;
    private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebarPresenter.class);

    private Project project;
    private Role role;

    @Autowired
    private WorkbenchDataManager manager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public WorkbenchSidebarPresenter(WorkbenchSidebar view,Project project,Role role) {
        this.view = view;

        this.project = project;
        this.role = role;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

}
