package org.generationcp.ibpworkbench.ui.sidebar;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

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

    @Autowired
    private WorkbenchDataManager manager;

    @Autowired
    private SessionData sessionData;
    
    @Value("${workbench.is.backup.and.restore.enabled}")
    private String isBackupAndRestoreEnabled;

    public WorkbenchSidebarPresenter(WorkbenchSidebar view) {
        this.view = view;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
    	//do nothing
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
                if ("workflows".equals(category.getSidebarCategoryName()) && 
                		sessionData.getSelectedProject() != null) {
                	addCategoryLinkBasedOnRole(categoryLinks,category);
                }
                if ("admin".equals(category.getSidebarCategoryName())) {
                	addAdminCategoryLinks(categoryLinks,category);
                } else {
                    categoryLinks.addAll(manager.getAllWorkbenchSidebarLinksByCategoryId(category));
                }
            }

            for (WorkbenchSidebarCategoryLink link : categoryLinks) {
                if (sidebarLinks.get(link.getWorkbenchSidebarCategory()) == null) {
                    sidebarLinks.put(link.getWorkbenchSidebarCategory(), new ArrayList<WorkbenchSidebarCategoryLink>());
                }
                if (link.getTool() == null) {
                    link.setTool(new Tool(link.getSidebarLinkName(), link.getSidebarLinkTitle(), ""));
                }
                sidebarLinks.get(link.getWorkbenchSidebarCategory()).add(link);
            }
        } catch (MiddlewareQueryException e) {
        	LOG.error(e.getMessage(),e);
        }
        return sidebarLinks;
    }

    protected void addAdminCategoryLinks(
			List<WorkbenchSidebarCategoryLink> categoryLinks, 
    		WorkbenchSidebarCategory category) throws MiddlewareQueryException {
    	categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"manage_program","Manage Program Settings"));
    	if(isBackupAndRestoreEnabled!=null && Boolean.valueOf(isBackupAndRestoreEnabled)) {
    		categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"recovery","Backup and Restore Program Data"));
    	}
        categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"user_tools","Manage User-Added Tools"));
        categoryLinks.add(new WorkbenchSidebarCategoryLink(manager.getToolWithName(ToolEnum.DATASET_IMPORTER.getToolName()),category,
				ToolEnum.DATASET_IMPORTER.getToolName(),"Data Import Tool"));
        categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"tool_versions","Tools and Crops Versions"));
        categoryLinks.add(new WorkbenchSidebarCategoryLink(null,category,"software_license","Software License"));
	}

	private void addCategoryLinkBasedOnRole(
    		List<WorkbenchSidebarCategoryLink> categoryLinks, 
    		WorkbenchSidebarCategory category) throws MiddlewareQueryException {
    	List<Role> roles = manager.getRolesByProjectAndUser(
    			sessionData.getSelectedProject(),sessionData.getUserData());
    	for (Role role : roles) {
            //we dont include the tools anymore
            if(!"Manager".equalsIgnoreCase(role.getName())) {
                categoryLinks.add(new WorkbenchSidebarCategoryLink(null, category, role.getWorkflowTemplate().getName(), role.getLabel()));
            }
        }
	}

	public List<Role> getRoleByTemplateName(String templateName) {
        try {
            return manager.getRolesByWorkflowTemplate(manager.getWorkflowTemplateByName(templateName).get(0));

        } catch (MiddlewareQueryException e) {
        	LOG.error(e.getMessage(),e);
        }
        return Collections.emptyList();
    }

    public void updateProjectLastOpenedDate() {
        try {

            // set the last opened project in the session
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
            Project project = app.getSessionData().getSelectedProject();

            ProjectUserInfoDAO projectUserInfoDao = manager.getProjectUserInfoDao();
            ProjectUserInfo projectUserInfo = projectUserInfoDao.getByProjectIdAndUserId(project.getProjectId().intValue(), app.getSessionData().getUserData().getUserid());
            if (projectUserInfo != null) {
                projectUserInfo.setLastOpenDate(new Date());
                manager.saveOrUpdateProjectUserInfo(projectUserInfo);
            }

            project.setLastOpenDate(new Date());
            manager.mergeProject(project);

            app.getSessionData().setLastOpenedProject(project);

        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString(), e);
        }
    }

	public void setIsBackupAndRestoreEnabled(String isBackupAndRestoreEnabled) {
		this.isBackupAndRestoreEnabled = isBackupAndRestoreEnabled;
	}

	public void setManager(WorkbenchDataManager manager) {
		this.manager = manager;
	}
	
	
    
    
}