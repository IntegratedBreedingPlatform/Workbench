package org.generationcp.ibpworkbench.ui.sidebar;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class WorkbenchSidebarPresenter implements InitializingBean {
    private final WorkbenchSidebar view;
    private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebarPresenter.class);

    private Project project;

    @Autowired
    private WorkbenchDataManager manager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public WorkbenchSidebarPresenter(WorkbenchSidebar view,Project project) {
        this.view = view;

        this.project = project;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public WorkbenchDataManager getManager() {
        return manager;
    }

    public Map<WorkbenchSidebarCategory,List<Tool>> getCategoryLinkItems() {
        // get all categories first
        Map<WorkbenchSidebarCategory,List<Tool>> sidebarLinks = new LinkedHashMap<WorkbenchSidebarCategory, List<Tool>>();

        try {
            List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<WorkbenchSidebarCategoryLink>();   // TODO: THESE SHOULD BE A MIDDLEWARE CALL

            List<WorkbenchSidebarCategory> workbenchSidebarCategoryList = manager.getAllWorkbenchSidebarCategory();

            for (WorkbenchSidebarCategory category : workbenchSidebarCategoryList) {
                if (category.getSidebarCategoryName().equals("workflows")) {

                    if (IBPWorkbenchApplication.get().getSessionData().getSelectedProject() != null) {
                        List<Role> roles = manager.getRolesByProjectAndUser(IBPWorkbenchApplication.get().getSessionData().getSelectedProject(),IBPWorkbenchApplication.get().getSessionData().getUserData());

                        for (Role role : roles) {
                            categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,role.getWorkflowTemplate().getName(),role.getLabel()));
                        }
                    }
                }
                if (category.getSidebarCategoryName().equals("admin")) {
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"member","Project Member"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"project_location","Project Location"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"project_method","Project Method"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"backup_ibdb","Backup Program"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"restore_ibdb","Restore Program"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"delete_project","Delete Program"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"user_tools","User Tools"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(manager.getToolWithName("dataset_importer"),category,"data_import_tool","Marker Assisted Recurrent Selection (MARS)"));
                }
            }

            for (WorkbenchSidebarCategoryLink link : categoryLinks) {
                if (sidebarLinks.get(link.getWorkbenchSidebarCategory()) == null)
                    sidebarLinks.put(link.getWorkbenchSidebarCategory(),new ArrayList<Tool>());

                if (link.getTool() == null) {
                    sidebarLinks.get(link.getWorkbenchSidebarCategory()).add(new Tool(link.getSidebarLinkName(),link.getSidebarLinkTitle(),""));
                } else {
                    sidebarLinks.get(link.getWorkbenchSidebarCategory()).add(link.getTool());
                }

            }


        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return sidebarLinks;
    }

    public List<Role> getRoleByTemplateName(String templateName) {
        try {
            return manager.getRolesByWorkflowTemplate(manager.getWorkflowTemplateByName(templateName).get(0));

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Collections.EMPTY_LIST;
    }



}
