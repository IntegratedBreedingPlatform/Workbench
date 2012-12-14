package org.generationcp.ibpworkbench.actions;

import java.util.Arrays;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewWindow;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.Variate;
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
public class ShowDatasetVariablesDetailAction implements ItemClickListener {
    private static final long serialVersionUID = 1L;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Table tblDataset;
    
    private Table tblVariates;
    
    private Table tblFactors;
    
    private SelectDatasetForBreedingViewWindow selectDatasetForBreedingViewWindow;

    public ShowDatasetVariablesDetailAction(Table tblDataset, Table tblFactors, Table tblVariates, SelectDatasetForBreedingViewWindow selectDatasetForBreedingViewWindow) {
        this.tblDataset = tblDataset;
        this.tblFactors = tblFactors;
        this.tblVariates = tblVariates;
        this.selectDatasetForBreedingViewWindow = selectDatasetForBreedingViewWindow;
    }
    
    @Override
    public void itemClick(ItemClickEvent event) {

        Integer represno = (Integer) event.getItemId();

        if (represno == null) {
            return;
        }

        try {
            
            ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(selectDatasetForBreedingViewWindow.getCurrentProject());
            
            TraitDataManager traitDataManager = managerFactory.getTraitDataManager();
            StudyDataManager studyDataManager = managerFactory.getStudyDataManager();
            List<Factor> factorList = studyDataManager.getFactorsByRepresentationId(represno);
            List<Variate> variateList = studyDataManager.getVariatesByRepresentationId(represno);
            String datasetName = (String)tblDataset.getItem(represno).getItemProperty("name").getValue();
            
            selectDatasetForBreedingViewWindow.setCurrentRepresentationId(represno);
            selectDatasetForBreedingViewWindow.setCurrentDatasetName(datasetName);
            
            updateFactorsTable(factorList, traitDataManager);
            updateVariatesTable(variateList, traitDataManager);

        }
        catch (MiddlewareQueryException e) {
            showDatabaseError(event.getComponent().getWindow());
        }
    }
    
    private void updateFactorsTable(List<Factor> factorList, TraitDataManager traitDataManager) {
        Object[] oldColumns = tblFactors.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
        container.setBeanIdProperty("id");
        tblFactors.setContainerDataSource(container);
        
        try {
            
            //TODO
            // Curation errors must be handled here correctly
        
            for (Factor factor : factorList) {
                
                FactorModel factorModel = new FactorModel();
                
                factorModel.setId(factor.getId());
                factorModel.setName(factor.getName());
                factorModel.setTraitid(factor.getTraitId());
                
                if (traitDataManager.getTraitById(factor.getTraitId()) != null) {
                
                    if (traitDataManager.getTraitById(factor.getTraitId()).getName() != null) {
        
                        factorModel.setTrname(traitDataManager.getTraitById(factor.getTraitId()).getName());
                    
                    } else {
                        
                        factorModel.setTrname(factor.getTraitId().toString());
                        
                    }
                
                } else {
                    
                    factorModel.setTrname(factor.getTraitId().toString());
                }
                
                factorModel.setScaleid(factor.getScaleId());
                
                if (traitDataManager.getScaleByID(factor.getScaleId()) != null) {
                
                    if (traitDataManager.getScaleByID(factor.getScaleId()).getName() != null) {
                    
                        factorModel.setScname(traitDataManager.getScaleByID(factor.getScaleId()).getName());
                    
                    } else {
                        
                        factorModel.setScname(factor.getScaleId().toString());
                        
                    }
                    
                } else {
                    
                    factorModel.setScname(factor.getScaleId().toString());
                }
                
                factorModel.setTmethid(factor.getMethodId());
                
                if (traitDataManager.getTraitMethodById(factor.getMethodId()) != null) {
                
                    if (traitDataManager.getTraitMethodById(factor.getMethodId()).getName() != null) {
                    
                        factorModel.setTmname(traitDataManager.getTraitMethodById(factor.getMethodId()).getName());
                    
                    } else {
                        
                        factorModel.setTmname(factor.getMethodId().toString());
                        
                    }
                
                } else {
                    
                    factorModel.setTmname(factor.getMethodId().toString());
                }
                
                container.addBean(factorModel);
                
            }
        
        } catch (MiddlewareQueryException e) {
            throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_IN_GETTING_DATASET_FACTOR);
        }
        
        tblFactors.setContainerDataSource(container);
        
        tblFactors.setVisibleColumns(columns);

    }
    
    private void updateVariatesTable(List<Variate> variateList, TraitDataManager traitDataManager) {
        Object[] oldColumns = tblVariates.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
        container.setBeanIdProperty("id");
        tblVariates.setContainerDataSource(container);
        
        try {
        
            //TODO
            // Curation errors must be handled here correctly
            
            for (Variate variate : variateList) {
                
                VariateModel variateModel = new VariateModel();
                
                variateModel.setId(variate.getId());
                variateModel.setName(variate.getName());
                variateModel.setTraitid(variate.getTraitId());
                
                if (traitDataManager.getTraitById(variate.getTraitId()) != null) {

                    if (traitDataManager.getTraitById(variate.getTraitId()).getName() != null) {
                    
                        variateModel.setTrname(traitDataManager.getTraitById(variate.getTraitId()).getName());
                        
                    } else {
                        
                        variateModel.setTrname(variate.getTraitId().toString());
                        
                    }
                
                } else {
                    
                    variateModel.setTrname(variate.getTraitId().toString());
                }
                
                variateModel.setScaleid(variate.getScaleId());
                
                if (traitDataManager.getScaleByID(variate.getScaleId()) != null) {
                
                    if (traitDataManager.getScaleByID(variate.getScaleId()).getName() != null) {
                    
                        variateModel.setScname(traitDataManager.getScaleByID(variate.getScaleId()).getName());
                    
                    } else {
                        
                        variateModel.setScname(variate.getScaleId().toString());
                        
                    }
                
                } else {
                    
                    variateModel.setScname(variate.getScaleId().toString());
                }
                    
                variateModel.setTmethid(variate.getMethodId());
                
                if (traitDataManager.getTraitMethodById(variate.getMethodId()) != null) {
                
                    if (traitDataManager.getTraitMethodById(variate.getMethodId()).getName() != null) {
                    
                        variateModel.setTmname(traitDataManager.getTraitMethodById(variate.getMethodId()).getName());
                        
                    } else {
                        
                        variateModel.setTmname(variate.getMethodId().toString());
                        
                    }
                
                } else {
                    
                    variateModel.setTmname(variate.getMethodId().toString());
                }
                
                container.addBean(variateModel);
                
            }
        
        } catch (MiddlewareQueryException e) {
            throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_IN_GETTING_DATASET_VARIATE);
        }
        
        tblVariates.setContainerDataSource(container);
        
        tblVariates.setVisibleColumns(columns);

    }
    
    
    private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }
}
