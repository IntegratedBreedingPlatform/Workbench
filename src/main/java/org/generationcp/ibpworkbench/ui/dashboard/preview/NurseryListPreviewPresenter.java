package org.generationcp.ibpworkbench.ui.dashboard.preview;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
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
import org.springframework.stereotype.Component;

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
@Component
@Configurable
public class NurseryListPreviewPresenter implements InitializingBean {

    private final NurseryListPreview view;
    private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreviewPresenter.class);

    private Project project;
    // TODO, move to message source    
    private static final String NO_SELECTION = "Please select a folder item";
    public final static String NOT_FOLDER = "Selected item is not a folder.";
    public final static String HAS_CHILDREN = "Folder has child items.";
    private static final String BLANK_NAME = "Folder name cannot be blank";
    private static final String INVALID_NAME = "Please choose a different name";

    @Autowired
    private WorkbenchDataManager manager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private ManagerFactory managerFactory;


    public NurseryListPreviewPresenter(NurseryListPreview view, Project project) {
        this.view = view;
        this.project = project;

        if (this.project != null) {
            if (view.getManagerFactoryProvider() != null) {
                setManagerFactory(view.getManagerFactoryProvider().getManagerFactoryForProject(this.project));
            }
        }

    }

    public void generateInitialTreeNodes() {

        List<FolderReference> centralRootFolders = new ArrayList<FolderReference>();
        List<FolderReference> localRootFolders = new ArrayList<FolderReference>();
        try {
            centralRootFolders = this.getManagerFactory().getStudyDataManager().getRootFolders(Database.CENTRAL);
            localRootFolders = this.getManagerFactory().getStudyDataManager().getRootFolders(Database.LOCAL);

            view.generateTopListOfTree(centralRootFolders, localRootFolders);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
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
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }

        return false;
    }

    public void renameNurseryListFolder(String newFolderName, Integer folderId) throws Error {
        try {

            if (newFolderName == null || newFolderName.isEmpty()) {
                throw new Error(messageSource.getMessage(Message.INVALID_ITEM_NAME));
            }

            this.getManagerFactory().getStudyDataManager().renameSubFolder(newFolderName, folderId);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
    }

    public void deleteNurseryListFolder(Integer id) {
        try {
            this.getManagerFactory().getStudyDataManager().deleteEmptyFolder(id);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
        }
    }

    public Object getStudyNodeParent(Integer newItem) {
        try {
            return this.getManagerFactory().getStudyDataManager().getParentFolder(newItem);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            return null;
        }
    }

    public boolean moveNurseryListFolder(Integer sourceId, Integer targetId, boolean isAStudy) throws Error {


        try {
            return getManagerFactory().getStudyDataManager().moveDmsProject(sourceId, targetId, isAStudy);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            throw new Error(e.getMessage());
        }
    }

    public Integer addNurseryListFolder(String name, Integer id) throws Error {
        try {
            if (name == null || name.trim().equals("")) {
                throw new Error(BLANK_NAME);
            }
            if (name.equals(view.MY_STUDIES) || name.equals(view.SHARED_STUDIES)) {
                throw new Error(INVALID_NAME);
            }
            Integer parentFolderId = id;
            if (!isFolder(id)) {
                //get parent
                DmsProject project = this.getManagerFactory().getStudyDataManager().getParentFolder(id);
                if (project == null) {
                    throw new Error("Parent folder cannot be null");
                }
                parentFolderId = project.getProjectId();
            }
            return this.getManagerFactory().getStudyDataManager().addSubFolder(parentFolderId, name, name);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            throw new Error(e.getMessage());
        }
    }

    public Integer validateForDeleteNurseryList(Integer id) throws Error {
        LOG.info("id = " + id);
        if (id == null) {
            throw new Error(NO_SELECTION);
        }
        DmsProject project = null;

        try {
            project = this.getManagerFactory().getStudyDataManager().getProject(id);

        } catch (MiddlewareQueryException e) {
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }

        if (project == null) {
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }

        try {
            if (hasChildren(id)) {
                throw new Error(HAS_CHILDREN);
            }
        } catch (MiddlewareQueryException e) {
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }

        return id;
    }

    private boolean hasChildren(Integer id) throws MiddlewareQueryException {
        List<Reference> studyChildren = null;

        try {
            studyChildren = this.getManagerFactory().getStudyDataManager().getChildrenOfFolder(new Integer(id));
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            throw e;
        }
        if (studyChildren != null && !studyChildren.isEmpty()) {
            LOG.info("hasChildren = true");
            return true;
        }
        LOG.info("hasChildren = false");
        return false;

    }

    public void addChildrenNode(int parentId) throws InternationalizableException {
        List<Reference> studyChildren = new ArrayList<Reference>();

        try {
            studyChildren = this.getManagerFactory().getStudyDataManager().getChildrenOfFolder(new Integer(parentId));
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
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
            LOG.error(e.toString(), e);
        }
    }
    
    public StudyType getStudyType(int studyId) {
        try {
        	Study study = this.getManagerFactory().getStudyDataManager().getStudy(studyId);
        	if(study!=null && study.getType()!=null) {
        		return StudyType.getStudyType(study.getType());
        	}
            return null;
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            return null;
        }
    }
}
