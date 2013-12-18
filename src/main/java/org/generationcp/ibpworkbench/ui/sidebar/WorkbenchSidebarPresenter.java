package org.generationcp.ibpworkbench.ui.sidebar;

import com.vaadin.ui.Window;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
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

    public Map<WorkbenchSidebarCategory,List<WorkbenchSidebarCategoryLink>> getCategoryLinkItems() {
        // get all categories first
        Map<WorkbenchSidebarCategory,List<WorkbenchSidebarCategoryLink>> sidebarLinks = new LinkedHashMap<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>>();

        try {
            List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<WorkbenchSidebarCategoryLink>();

            List<WorkbenchSidebarCategory> workbenchSidebarCategoryList = manager.getAllWorkbenchSidebarCategory();

            for (WorkbenchSidebarCategory category : workbenchSidebarCategoryList) {
                if (category.getSidebarCategoryName().equals("workflows")) {

                    if (IBPWorkbenchApplication.get().getSessionData().getSelectedProject() != null) {
                        List<Role> roles = manager.getRolesByProjectAndUser(IBPWorkbenchApplication.get().getSessionData().getSelectedProject(),IBPWorkbenchApplication.get().getSessionData().getUserData());

                        for (Role role : roles) {
                            //we dont include the tools anymore
                            if(!role.getName().equalsIgnoreCase("Manager"))
                                categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,role.getWorkflowTemplate().getName(),role.getLabel()));
                        }
                    }
                }
                if (category.getSidebarCategoryName().equals("admin")) {
                    
                    
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"member","Program Members"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"project_location","Program Locations"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"project_method","Program Methods"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"backup_ibdb","Backup Program"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"restore_ibdb","Restore from Backup File"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"update_project","Update Program"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"delete_project","Delete Program"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"user_tools","Manage User-Added Tools"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(manager.getToolWithName(LaunchWorkbenchToolAction.ToolEnum.DATASET_IMPORTER.getToolName()),category,LaunchWorkbenchToolAction.ToolEnum.DATASET_IMPORTER.getToolName(),"Data Import Tool"));
                    categoryLinks.add(new WorkbenchSidebarCategoryLink(manager.getToolWithName(LaunchWorkbenchToolAction.ToolEnum.NURSERY_TEMPLATE_WIZARD.getToolName()),category,LaunchWorkbenchToolAction.ToolEnum.NURSERY_TEMPLATE_WIZARD.getToolName(),messageSource.getMessage(Message.NURSERY_TEMPLATE)));

                    //add the softare_license in the tools
                    //categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"software_license","Software License"));
                } else {
                    categoryLinks.addAll(manager.getAllWorkbenchSidebarLinksByCategoryId(category));
                }
            }

            for (WorkbenchSidebarCategoryLink link : categoryLinks) {
                if (sidebarLinks.get(link.getWorkbenchSidebarCategory()) == null)
                    sidebarLinks.put(link.getWorkbenchSidebarCategory(),new ArrayList<WorkbenchSidebarCategoryLink>());

                if (link.getTool() == null)
                    link.setTool(new Tool(link.getSidebarLinkName(),link.getSidebarLinkTitle(),""));


                sidebarLinks.get(link.getWorkbenchSidebarCategory()).add(link);
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


    public void updateProjectLastOpenedDate(Project project) {
        try {

            // set the last opened project in the session
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();


            ProjectUserInfoDAO projectUserInfoDao = manager.getProjectUserInfoDao();
            ProjectUserInfo	projectUserInfo = projectUserInfoDao.getByProjectIdAndUserId(project.getProjectId().intValue(), app.getSessionData().getUserData().getUserid());
            if (projectUserInfo != null) {
                projectUserInfo.setLastOpenDate(new Date());
                manager.saveOrUpdateProjectUserInfo(projectUserInfo);
            }

            project.setLastOpenDate(new Date());
            manager.mergeProject(project);

            app.getSessionData().setLastOpenedProject(project);

        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString(), e);

            /*
            MessageNotifier.showError(window,
                messageSource.getMessage(Message.DATABASE_ERROR),
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
             */
        }
    }
}