package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.workbench.Project;
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

    // TODO, move to message source
    public final static String NOT_FOLDER = "Selected item is not a folder, please choose another item on the list.";
    public final static String NO_PARENT = "Selected item is a root item, please choose another item on the list.";
    public final static String HAS_CHILDREN = "Selected item contains other items, plase choose another item on the list.";

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
        }
    }

    public boolean hasChildren(Integer id) throws MiddlewareQueryException {
        return !this.getManagerFactory().getGermplasmListManager().getGermplasmListByParentFolderId(id,1,Integer.MAX_VALUE).isEmpty();
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
            GermplasmList gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

            if (!gpList.isFolder())
                throw new Error(NOT_FOLDER);

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
     * @param parentId
     * @return ID of the newly added germplasmList, null if not successful
     */
    public Integer addGermplasmListFolder(String folderName,Integer parentId) throws Error {
        GermplasmList parent = null;
        try {
            parent = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(parentId);

            if (!parent.isFolder())
                throw new Error(NOT_FOLDER);
            else {
                Calendar cal = Calendar.getInstance();
                GermplasmList gpList = new GermplasmList(null,folderName,cal.getTime().getTime(),"FOLDER",IBPWorkbenchApplication.get().getSessionData().getUserData().getUserid(),folderName,parent,1);

                return this.getManagerFactory().getGermplasmListManager().addGermplasmList(gpList);
            }

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }
    }

    public Integer deleteGermplasmListFolder(Integer id) throws Error {
        try {
            GermplasmList gplist = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

            if (hasChildren(gplist.getId())) {
                throw new Error(HAS_CHILDREN);
            } else if (!gplist.isFolder()) {
                throw new Error(NOT_FOLDER);
            }

            return this.getManagerFactory().getGermplasmListManager().deleteGermplasmList(gplist);

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }
    }

    public Integer dropGermplasmListFolderToParent(Integer id,Integer parentId) throws Error {
        try {
            GermplasmList gpList = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);

            if (!gpList.isFolder())
                 throw new Error(NOT_FOLDER);

            GermplasmList parent = this.getManagerFactory().getGermplasmListManager().getGermplasmListById(id);
            gpList.setParent(parent);

            return this.getManagerFactory().getGermplasmListManager().updateGermplasmList(gpList);

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new Error(messageSource.getMessage(Message.ERROR_DATABASE));
        }
    }

}
