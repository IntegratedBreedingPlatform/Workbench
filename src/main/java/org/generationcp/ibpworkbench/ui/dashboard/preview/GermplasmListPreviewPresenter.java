package org.generationcp.ibpworkbench.ui.dashboard.preview;

import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class GermplasmListPreviewPresenter implements InitializingBean {
    public static final String FOLDER = "FOLDER";
    private static final Logger LOG = LoggerFactory.getLogger(GermplasmListPreviewPresenter.class);
    private static final int BATCH_SIZE = 50;
    private GermplasmListPreview view;
    private Project project;
    @Autowired
    private WorkbenchDataManager manager;
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;
    private ManagerFactory managerFactory;

    public GermplasmListPreviewPresenter(GermplasmListPreview view, Project project) {
        this.view = view;

        this.project = project;

        if (this.project != null) {
            setManagerFactory(view.getManagerFactoryProvider().getManagerFactoryForProject(this.project));
        }
    }

    public GermplasmListPreviewPresenter() {
    }


    public void generateInitialTreeNode() {
        List<GermplasmList> germplasmListParentLocal;
        List<GermplasmList> germplasmListParentCentral;

        try {
            germplasmListParentLocal = this.getManagerFactory().getGermplasmListManager().getAllTopLevelListsBatched(BATCH_SIZE, Database.LOCAL);
            germplasmListParentCentral = this.getManagerFactory().getGermplasmListManager().getAllTopLevelListsBatched(BATCH_SIZE, Database.CENTRAL);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getLocalizedMessage(), e);

            germplasmListParentLocal = new ArrayList<GermplasmList>();
            germplasmListParentCentral = new ArrayList<GermplasmList>();
        }

        view.generateTree(germplasmListParentLocal, germplasmListParentCentral);

    }

    public void addGermplasmListNode(int parentGermplasmListId, Object itemId) {
        List<GermplasmList> germplasmListChildren;

        try {
            if (getManagerFactory() == null) {
                setManagerFactory(view.getManagerFactoryProvider().getManagerFactoryForProject(this.project));
            }

            germplasmListChildren = this.getManagerFactory().getGermplasmListManager().getGermplasmListByParentFolderIdBatched(parentGermplasmListId, BATCH_SIZE);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getLocalizedMessage(), e);
            germplasmListChildren = new ArrayList<GermplasmList>();
        }

        view.addGermplasmListNode(parentGermplasmListId, germplasmListChildren, itemId);

    }

    public boolean hasChildList(int listId) {

        List<GermplasmList> listChildren;

        try {
            if (getManagerFactory() == null) {
                setManagerFactory(view.getManagerFactoryProvider().getManagerFactoryForProject(this.project));
            }
            listChildren = getManagerFactory().getGermplasmListManager().getGermplasmListByParentFolderId(listId, 0, 1);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getLocalizedMessage(), e);
            listChildren = new ArrayList<GermplasmList>();
        }

        return !listChildren.isEmpty();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // does nothing
    }

    public ManagerFactory getManagerFactory() {

        return managerFactory;
    }

    public void setManagerFactory(ManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

    public boolean isFolder(Integer id) {
        try {
            return this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id).isFolder();
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return false;
        } catch (NullPointerException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return false;
        }
    }

    public boolean hasChildren(Integer id) throws MiddlewareQueryException {
        return !this.getManagerFactory().getGermplasmListManager().getGermplasmListByParentFolderId(id, 0, Integer.MAX_VALUE).isEmpty();
    }

    public GermplasmList getGermplasmListParent(Integer id) throws GermplasmListPreviewException {
        try {
            return this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id).getParent();

        } catch (MiddlewareQueryException e) {
            throw new GermplasmListPreviewException(messageSource.getMessage(Message.DATABASE_ERROR),e);
        } catch (NullPointerException e) {
            throw new GermplasmListPreviewException(GermplasmListPreviewException.NO_PARENT,e);
        }

    }

    public boolean renameGermplasmListFolder(String newName, Integer id) throws GermplasmListPreviewException {
        try {

            if (newName == null || newName.isEmpty()) {
                throw new GermplasmListPreviewException(messageSource.getMessage(Message.INVALID_ITEM_NAME));
            }

            GermplasmList gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

            if (!gpList.isFolder()) {
                throw new GermplasmListPreviewException(GermplasmListPreviewException.NOT_FOLDER);
            }

            checkIfUnique(newName);
            gpList.setName(newName);

            this.getManagerFactory().getGermplasmListManager().updateGermplasmList(gpList);

            return true;
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new GermplasmListPreviewException(messageSource.getMessage(Message.ERROR_DATABASE));
        }
    }

    /**
     * @param folderName of the germplasm
     * @param id of the parent
     * @return ID of the newly added germplasmList, null if not successful
     */
    public Integer addGermplasmListFolder(String folderName, Integer id) throws GermplasmListPreviewException {
        if (folderName == null || "".equals(folderName.trim())) {
            throw new GermplasmListPreviewException(GermplasmListPreviewException.BLANK_NAME);
        } else if (folderName.equals(GermplasmListPreview.MY_LIST) || folderName.equals(GermplasmListPreview.SHARED_LIST)) {
            throw new GermplasmListPreviewException(GermplasmListPreviewException.INVALID_NAME);
        }

        GermplasmList gpList, newList;
        try {

            checkIfUnique(folderName);

            Integer userId = manager.getLocalIbdbUserId(sessionData.getUserData().getUserid(),
                    sessionData.getSelectedProject().getProjectId());

            if (id != null) {
                gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

                if (null != gpList && !gpList.isFolder()) {
                    newList = new GermplasmList(null, folderName, Long.valueOf((new SimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT)).format(Calendar.getInstance().getTime())), FOLDER, userId, folderName, gpList.getParent(), 0);
                } else {
                    newList = new GermplasmList(null, folderName, Long.valueOf((new SimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT)).format(Calendar.getInstance().getTime())), FOLDER, userId, folderName, gpList, 0);
                }
            } else {
                newList = new GermplasmList(null, folderName, Long.valueOf((new SimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT)).format(Calendar.getInstance().getTime())), FOLDER, userId, folderName, null, 0);
            }

            newList.setDescription("(NEW FOLDER) " + folderName);
            return this.getManagerFactory().getGermplasmListManager().addGermplasmList(newList);

        } catch (MiddlewareQueryException e) {
            throw new GermplasmListPreviewException(messageSource.getMessage(Message.ERROR_DATABASE),e);
        }
    }

    private void checkIfUnique(String folderName) throws MiddlewareQueryException, GermplasmListPreviewException {
        List<GermplasmList> centralDuplicate = this.getManagerFactory().getGermplasmListManager().
                getGermplasmListByName(folderName, 0, 1, null, Database.CENTRAL);
        if (centralDuplicate != null && !centralDuplicate.isEmpty()) {
            throw new GermplasmListPreviewException(GermplasmListPreviewException.NAME_NOT_UNIQUE);
        }
        List<GermplasmList> localDuplicate = this.getManagerFactory().getGermplasmListManager().
                getGermplasmListByName(folderName, 0, 1, null, Database.LOCAL);
        if (localDuplicate != null && !localDuplicate.isEmpty()) {
            throw new GermplasmListPreviewException(GermplasmListPreviewException.NAME_NOT_UNIQUE);
        }
    }

    public GermplasmList validateForDeleteGermplasmList(Integer id) throws GermplasmListPreviewException {
        GermplasmList gpList;

        try {
            if (id == null) {
                throw new GermplasmListPreviewException(GermplasmListPreviewException.NO_SELECTION);
            }

            gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

            if (gpList == null) {
                throw new GermplasmListPreviewException(messageSource.getMessage(Message.ERROR_DATABASE));
            } else if (hasChildren(gpList.getId())) {
                throw new GermplasmListPreviewException(GermplasmListPreviewException.HAS_CHILDREN);
            }
        } catch (MiddlewareQueryException e) {
            throw new GermplasmListPreviewException(messageSource.getMessage(Message.ERROR_DATABASE),e);
        }

        return gpList;
    }

    /**
     * Assumes that the deletion has been validated
     *
     * @param gpList list to be deleted
     * @return deleted integer id
     * @throws Error
     */
    public Integer deleteGermplasmListFolder(GermplasmList gpList) throws GermplasmListPreviewException {
        try {
            return this.getManagerFactory().getGermplasmListManager().deleteGermplasmList(gpList);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new GermplasmListPreviewException(messageSource.getMessage(Message.ERROR_DATABASE));
        }
    }

    public Integer dropGermplasmListToParent(Integer id, Integer parentId) throws GermplasmListPreviewException {
        try {
            GermplasmList gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

            if (parentId != null) {
                GermplasmList parent = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(parentId);
                gpList.setParent(parent);
            } else {
                gpList.setParent(null);
            }

            return this.getManagerFactory().getGermplasmListManager().updateGermplasmList(gpList);

        } catch (MiddlewareQueryException e) {
            throw new GermplasmListPreviewException(messageSource.getMessage(Message.ERROR_DATABASE),e);
        }
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

    public void setView(GermplasmListPreview view) {
        this.view = view;
    }
}
