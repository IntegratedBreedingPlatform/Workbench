package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
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
    
    public void addGermplasmListNode(int parentGermplasmListId) throws InternationalizableException{
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
        
        view.addGermplasmListNode(parentGermplasmListId, germplasmListChildren);

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


    public boolean validateOpenList(Object value) {
        return true;
    }

    public boolean isFolder(Object r) {
        return true;  //To change body of created methods use File | Settings | File Templates.
    }
}
