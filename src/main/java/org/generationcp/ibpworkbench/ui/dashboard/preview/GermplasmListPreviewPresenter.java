package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
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


/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class GermplasmListPreviewPresenter implements InitializingBean {
    private final GermplasmListPreview view;
    private static final Logger LOG = LoggerFactory.getLogger(GermplasmListPreviewPresenter.class);

    private Project project;

    @Autowired
    private WorkbenchDataManager manager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    // TODO, move to message source
    public final static String NOT_FOLDER = "Selected item is not a folder.";
    public final static String NO_PARENT = "Selected item is a root item, please choose another item on the list.";
    public final static String HAS_CHILDREN = "Folder has child items.";
    private static final String NO_SELECTION = "Please select a folder item";
    private static final String BLANK_NAME = "Folder name cannot be blank";
    private static final String INVALID_NAME = "Please choose a different name";
    private static final String NAME_NOT_UNIQUE = "Name is not unique";


    private ManagerFactory managerFactory;
    
    private final static int BATCH_SIZE = 50;

    public GermplasmListPreviewPresenter(GermplasmListPreview view,Project project) {
        this.view = view;

        this.project = project;
        
        if(this.project != null){
            setManagerFactory(view.getManagerFactoryProvider().getManagerFactoryForProject(this.project));
         }
    }
    
    public void generateInitialTreeNode(){
        List<GermplasmList> germplasmListParentLocal = new ArrayList<GermplasmList>();
        List<GermplasmList> germplasmListParentCentral = new ArrayList<GermplasmList>();

        try {
            germplasmListParentLocal = this.getManagerFactory().getGermplasmListManager().getAllTopLevelListsBatched(BATCH_SIZE, Database.LOCAL);
            germplasmListParentCentral = this.getManagerFactory().getGermplasmListManager().getAllTopLevelListsBatched(BATCH_SIZE, Database.CENTRAL);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            e.printStackTrace();
          
            germplasmListParentLocal = new ArrayList<GermplasmList>();
            germplasmListParentCentral = new ArrayList<GermplasmList>();
        }
        
        view.generateTree(germplasmListParentLocal, germplasmListParentCentral);

    }
    
    public void addGermplasmListNode(int parentGermplasmListId, Object itemId) throws InternationalizableException{
        List<GermplasmList> germplasmListChildren = new ArrayList<GermplasmList>();

        try {
            if(getManagerFactory() == null){
                setManagerFactory(view.getManagerFactoryProvider().getManagerFactoryForProject(this.project));
            }
            
            germplasmListChildren = this.getManagerFactory().getGermplasmListManager().getGermplasmListByParentFolderIdBatched(parentGermplasmListId, BATCH_SIZE);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            e.printStackTrace();/*
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_GERMPLASM_LISTS_BY_PARENT_FOLDER_ID));
                    */
            germplasmListChildren = new ArrayList<GermplasmList>();
        }
        
        view.addGermplasmListNode(parentGermplasmListId, germplasmListChildren, itemId);

    }
    
    public boolean hasChildList(int listId) {

        List<GermplasmList> listChildren = new ArrayList<GermplasmList>();

        try {
            if(getManagerFactory() == null){
                setManagerFactory(view.getManagerFactoryProvider().getManagerFactoryForProject(this.project));
            }
            listChildren = getManagerFactory().getGermplasmListManager().getGermplasmListByParentFolderId(listId, 0, 1);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            /*
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_GERMPLASM_LISTS_BY_PARENT_FOLDER_ID));
                    */
            listChildren = new ArrayList<GermplasmList>();
        }
        
        return !listChildren.isEmpty();
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

    public boolean isFolder(Integer id) {
        try {
            return this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id).isFolder();
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean hasChildren(Integer id) throws MiddlewareQueryException {
        return !this.getManagerFactory().getGermplasmListManager().getGermplasmListByParentFolderId(id,0,Integer.MAX_VALUE).isEmpty();
    }

    public GermplasmList getGermplasmListParent(Integer id) throws Error {
        //GermplasmListManagerImpl.getGermplasmListById(id) then from the resulting GermplasmList, check getParent()!=null?getParent().getId():null

        try {
            GermplasmList gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id).getParent();

            return gpList;
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            throw new Error(messageSource.getMessage(Message.DATABASE_ERROR));
        } catch (NullPointerException e) {
            throw new Error(NO_PARENT);
        }

    }

    public boolean renameGermplasmListFolder(String newName,Integer id) throws Error {
        try {

            if (newName == null || newName.isEmpty()) {
                throw new Error(messageSource.getMessage(Message.INVALID_CANNOT_RENAME_EMPTY_STRING));
            }

            GermplasmList gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

            if (!gpList.isFolder())
                throw new Error(NOT_FOLDER);

            checkIfUnique(newName);
            gpList.setName(newName);

            this.getManagerFactory().getGermplasmListManager().updateGermplasmList(gpList);

            return true;
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }
    }

    /**
     *
     * @param folderName
     * @param id
     * @return ID of the newly added germplasmList, null if not successful
     */
    public Integer addGermplasmListFolder(String folderName,Integer id) throws Error {
    	if (folderName == null || folderName.trim().equals("")) {
            throw new Error(BLANK_NAME);
        }
        if (folderName.equals(view.MY_LIST) || folderName.equals(view.SHARED_LIST)) {
            throw new Error(INVALID_NAME);
        }
        
    	GermplasmList gpList = null;
        GermplasmList newList = null;
        try {
        	
        	checkIfUnique(folderName);
            

            if (id == null) {
                newList = new GermplasmList(null,folderName,Long.valueOf((new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime())),"FOLDER",sessionData.getUserData().getUserid(),folderName,null,1);
            }
            else {
                gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

                if (!gpList.isFolder()) {
                    GermplasmList parent = null;

                    parent = gpList.getParent();

                    if (parent == null) {
                        newList = new GermplasmList(null,folderName,Long.valueOf((new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime())),"FOLDER",sessionData.getUserData().getUserid(),folderName,null,1);
                    } else {
                        newList = new GermplasmList(null,folderName,Long.valueOf((new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime())),"FOLDER",sessionData.getUserData().getUserid(),folderName,parent,1);
                    }
                } else {
                    newList = new GermplasmList(null,folderName,Long.valueOf((new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime())),"FOLDER",sessionData.getUserData().getUserid(),folderName,gpList,1);
                }

            }

            newList.setDescription("(NEW FOLDER) " + folderName);
            return this.getManagerFactory().getGermplasmListManager().addGermplasmList(newList);

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }
    }

    private void checkIfUnique(String folderName) throws MiddlewareQueryException, Error {
    	List<GermplasmList> centralDuplicate = this.getManagerFactory().getGermplasmListManager().
            	getGermplasmListByName(folderName, 0, 1, null, Database.CENTRAL);
        if(centralDuplicate!=null && !centralDuplicate.isEmpty()) {
        	throw new Error(NAME_NOT_UNIQUE);
        }
        List<GermplasmList> localDuplicate = this.getManagerFactory().getGermplasmListManager().
            	getGermplasmListByName(folderName, 0, 1, null, Database.LOCAL);
        if(localDuplicate!=null && !localDuplicate.isEmpty()) {
        	throw new Error(NAME_NOT_UNIQUE);
        }
	}

	public GermplasmList validateForDeleteGermplasmList(Integer id) throws Error {
        if (id == null) {
            throw new Error(NO_SELECTION);
        }
        GermplasmList gpList = null;

        try {
            gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

        } catch (MiddlewareQueryException e) {
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }

        if (gpList == null) {
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }

        /**
        if (!gpList.isFolder()) {
            throw new Error(NOT_FOLDER);
        }**/

        try {
            if (hasChildren(gpList.getId())) {
                throw new Error(HAS_CHILDREN);
            }
        } catch (MiddlewareQueryException e) {
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }

        return gpList;
    }

    /**
     * Assumes that the deletion has been validated
     * @param gpList
     * @return
     * @throws Error
     */
    public Integer deleteGermplasmListFolder(GermplasmList gpList) throws Error {
        try {
            return this.getManagerFactory().getGermplasmListManager().deleteGermplasmList(gpList);
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }
    }

    public Integer dropGermplasmListToParent(Integer id, Integer parentId) throws Error {
        try {
            GermplasmList gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

            /*if (!gpList.isFolder())
                 throw new Error(NOT_FOLDER);*/

            if (parentId != null) {
                GermplasmList parent = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(parentId);
                            gpList.setParent(parent);
            } else {
                gpList.setParent(null);
            }


            return this.getManagerFactory().getGermplasmListManager().updateGermplasmList(gpList);

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
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

}
