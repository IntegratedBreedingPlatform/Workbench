package org.generationcp.ibpworkbench.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewWindow;
import org.generationcp.ibpworkbench.model.RepresentationModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
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
    
    @Autowired
    private StudyDataManager studyDataManager;
    
    private Table tblDataset;
    
    private Table tblFactors;
    
    private Table tblVariates;
    
    private SelectDatasetForBreedingViewWindow selectDatasetForBreedingViewWindow;
    

    public ShowStudyDatasetDetailAction(Table tblDataset, Table tblFactors, Table tblVariates, SelectDatasetForBreedingViewWindow selectDatasetForBreedingViewWindow) {
 
        this.tblDataset = tblDataset;
        this.tblFactors = tblFactors;
        this.tblVariates = tblVariates;
        this.selectDatasetForBreedingViewWindow = selectDatasetForBreedingViewWindow;
        this.studyDataManager = selectDatasetForBreedingViewWindow.getStudyDataManager();
    }

    @Override
    public void itemClick(ItemClickEvent event) {

        Study study = null;
		try {
			if (event.getItemId() instanceof StudyReference){
				System.out.println("Item is Study");
				study = studyDataManager.getStudy(((StudyReference)event.getItemId()).getId());
			}else if (event.getItemId() instanceof FolderReference){
				System.out.println("Item is FolderReference");
				study = studyDataManager.getStudy(((FolderReference)event.getItemId()).getId());
			}
			
		} catch (MiddlewareQueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        if (study == null) {
            return;
        }
        
        if (!study.equals(selectDatasetForBreedingViewWindow.getCurrentStudy())) {
            
            selectDatasetForBreedingViewWindow.setCurrentStudy(study);
            selectDatasetForBreedingViewWindow.setCurrentRepresentationId(null);
            selectDatasetForBreedingViewWindow.setCurrentDatasetName(null);
        }
        
        try {
			List<DatasetReference> datasetRefs = studyDataManager.getDatasetReferences(study.getId());
			  updateDatasetTable(datasetRefs, tblFactors, tblVariates);
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }
    
    private void updateDatasetTable(List<DatasetReference> datasetList, Table tblFactors, Table tblVariates) {
        Object[] oldColumns = tblDataset.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, DatasetReference> container = new BeanContainer<Integer, DatasetReference>(DatasetReference.class);
        container.setBeanIdProperty("id");
        tblDataset.setContainerDataSource(container);
        
        for (DatasetReference datasetRef : datasetList) {
				container.addBean(datasetRef);
        }
        
        tblDataset.setContainerDataSource(container);
        
        tblDataset.setVisibleColumns(columns);
        
        tblDataset.addListener(new ShowDatasetVariablesDetailAction(tblDataset, tblFactors, tblVariates, selectDatasetForBreedingViewWindow));
    }
    
    
    private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }

}
