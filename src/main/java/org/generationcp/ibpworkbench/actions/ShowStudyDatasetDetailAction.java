package org.generationcp.ibpworkbench.actions;

import java.util.Arrays;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class ShowStudyDatasetDetailAction implements ItemClickListener {
    private static final long serialVersionUID = 1L;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Label studyDetailLabel;
    
    private Table tblDataset;
    
    private Table tblVariables;
    
    private Project currentProject;

    public ShowStudyDatasetDetailAction(Label studyDetailLabel, Table tblDataset, Table tblVariables, Project currentProject) {
        this.studyDetailLabel = studyDetailLabel;
        this.tblDataset = tblDataset;
        this.tblVariables = tblVariables;
        this.currentProject = currentProject;
    }
    
    @Override
    public void itemClick(ItemClickEvent event) {

        Study study = (Study) event.getItemId();

        if (study == null) {
            return;
        }
        
        System.out.println(study);
        
    
/*        if (openSelectDatasetForBreedingViewAction != null) {
            selectDatasetForBreedingViewButton.removeListener(openSelectDatasetForBreedingViewAction);
        }
        openSelectDatasetForBreedingViewAction = new OpenSelectDatasetForBreedingViewAction(project);
        selectDatasetForBreedingViewButton.addListener(openSelectDatasetForBreedingViewAction);*/
        
        //SessionData sessionData = IBPWorkbenchApplication.get().getSessionData();
        
        try {
            StudyDataManager studyDataManager = managerFactoryProvider.getManagerFactoryForProject(currentProject).getStudyDataManager();
            List<Representation> datasetList = studyDataManager.getRepresentationByStudyID(study.getId());
            
            updateDatasetTable(datasetList);

        }
        catch (MiddlewareQueryException e) {
            showDatabaseError(event.getComponent().getWindow());
        }
    }
    
    private void updateDatasetTable(List<Representation> datasetList) {
        Object[] oldColumns = tblDataset.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, Representation> container = new BeanContainer<Integer, Representation>(Representation.class);
        container.setBeanIdProperty("id");
        tblDataset.setContainerDataSource(container);
        
        for (Representation representation : datasetList) {
            container.addBean(representation);
        }
        
        tblDataset.setContainerDataSource(container);
        
        tblDataset.setVisibleColumns(columns);
    }
    
    
    private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }
}
