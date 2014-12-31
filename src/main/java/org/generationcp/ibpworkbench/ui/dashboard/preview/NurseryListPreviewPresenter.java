package org.generationcp.ibpworkbench.ui.dashboard.preview;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class NurseryListPreviewPresenter implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreviewPresenter.class);
    public static final int MAX_STUDY_FOLDER_NAME_LENGTH = 255;

    private NurseryListPreview view;
    private Project project;

    @Autowired
    private WorkbenchDataManager manager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private ManagerFactory managerFactory;

    public NurseryListPreviewPresenter(NurseryListPreview view, Project project) {
        this.view = view;
        this.project = project;

        if (this.project != null && view.getManagerFactoryProvider() != null) {
            setManagerFactory(view.getManagerFactoryProvider().getManagerFactoryForProject(this.project));
        }
    }

    public NurseryListPreviewPresenter() {

    }

    public void generateInitialTreeNodes() {

        List<FolderReference> root;
        try {
        	root = this.getManagerFactory().getStudyDataManager().getRootFolders(project.getUniqueID());
            view.generateTopListOfTree(root);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // do nothing
    }


    public ManagerFactory getManagerFactory() {
        return managerFactory;
    }


    public void setManagerFactory(ManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

    public boolean isFolder(Integer value) {
        try {
            boolean isStudy = this.getManagerFactory().getStudyDataManager().isStudy(value);
            LOG.info("isFolder = " + !isStudy);
            return !isStudy;
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
        }

        return false;
    }

    public void renameNurseryListFolder(String newFolderName, Integer folderId) throws NurseryListPreviewException {
        try {

            validateStudyFolderName(newFolderName);

            this.getManagerFactory().getStudyDataManager().renameSubFolder(newFolderName, folderId);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
        }
    }

    public void deleteNurseryListFolder(Integer id) throws MiddlewareQueryException {
        this.getManagerFactory().getFieldbookMiddlewareService().deleteStudy(id);
    }

    public Object getStudyNodeParent(Integer newItem) {
        try {
            return this.getManagerFactory().getStudyDataManager().getParentFolder(newItem);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
            return null;
        }
    }

    public boolean moveNurseryListFolder(Integer sourceId, Integer targetId, boolean isAStudy) throws NurseryListPreviewException {
        try {
            return getManagerFactory().getStudyDataManager().moveDmsProject(sourceId, targetId, isAStudy);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
            throw new NurseryListPreviewException(e.getMessage());
        }
    }

    public Integer addNurseryListFolder(String name, Integer id) throws NurseryListPreviewException {
        try {

            validateStudyFolderName(name);

            Integer parentFolderId = id;
            if (!isFolder(id)) {
                //get parent
                DmsProject dmsProject = this.getManagerFactory().getStudyDataManager().getParentFolder(id);
                if (dmsProject == null) {
                    throw new NurseryListPreviewException(NurseryListPreviewException.NO_PARENT);
                }
                parentFolderId = dmsProject.getProjectId();
            }
            return this.getManagerFactory().getStudyDataManager().addSubFolder(parentFolderId, name, name, project.getUniqueID());
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
            throw new NurseryListPreviewException(e.getMessage());
        }
    }

    protected void validateStudyFolderName(String name) throws NurseryListPreviewException {
        if (name == null || "".equals(name.trim())) {
            throw new NurseryListPreviewException(NurseryListPreviewException.BLANK_NAME);
        }
        if (name.equals(NurseryListPreview.NURSERIES_AND_TRIALS)) {
            throw new NurseryListPreviewException(NurseryListPreviewException.INVALID_NAME);
        }

        if (name.length() > MAX_STUDY_FOLDER_NAME_LENGTH) {
            throw new NurseryListPreviewException(NurseryListPreviewException.TOO_LONG);
        }
    }

    public Integer validateForDeleteNurseryList(Integer id) throws NurseryListPreviewException {
        LOG.info("id = " + id);
        if (id == null) {
            throw new NurseryListPreviewException(NurseryListPreviewException.NO_SELECTION);
        }
        DmsProject dmsProject;

        try {
            dmsProject = this.getManagerFactory().getStudyDataManager().getProject(id);

        } catch (MiddlewareQueryException e) {
            throw new NurseryListPreviewException(messageSource.getMessage(Message.ERROR_DATABASE),e);
        }

        if (dmsProject == null) {
            throw new NurseryListPreviewException(messageSource.getMessage(Message.ERROR_DATABASE));
        }

        if (hasChildren(id)) {
            throw new NurseryListPreviewException(NurseryListPreviewException.HAS_CHILDREN);
        }

        return id;
    }

    private boolean hasChildren(Integer id) throws NurseryListPreviewException {
        List<Reference> studyChildren;

        try {
            studyChildren = this.getManagerFactory().getStudyDataManager().getChildrenOfFolder(id, project.getUniqueID());
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
            throw new NurseryListPreviewException(messageSource.getMessage(Message.ERROR_DATABASE),e);
        }
        if (studyChildren != null && !studyChildren.isEmpty()) {
            LOG.info("hasChildren = true");
            return true;
        }
        LOG.info("hasChildren = false");
        return false;

    }

    public void addChildrenNode(int parentId) {
        List<Reference> studyChildren;

        try {
            studyChildren = this.getManagerFactory().getStudyDataManager().getChildrenOfFolder(parentId, project.getUniqueID());
        } catch (MiddlewareQueryException e) {
            LOG.error(messageSource.getMessage(Message.ERROR_DATABASE),e);
            studyChildren = new ArrayList<Reference>();
        }

        view.addChildrenNode(parentId, studyChildren);

    }

    public void updateProjectLastOpenedDate() {
        try {

            // set the last opened project in the session
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();


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
            LOG.error(messageSource.getMessage(Message.ERROR_DATABASE), e);
        }
    }

    public StudyType getStudyType(int studyId) {
        try {
            Study study = this.getManagerFactory().getStudyDataManager().getStudy(studyId);
            if (study != null && study.getType() != null) {
                return StudyType.getStudyType(study.getType());
            }
            return null;
        } catch (MiddlewareQueryException e) {
            LOG.error(messageSource.getMessage(Message.ERROR_DATABASE),e);
            return null;
        }
    }

    public void setView(NurseryListPreview view) {
        this.view = view;
    }

	public void setProject(Project project) {
		this.project = project;
	}

	public void processToolbarButtons(Object treeItem) {
    	//to be overridden
    }
}
