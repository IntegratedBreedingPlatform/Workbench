package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewPanel;
import org.generationcp.middleware.manager.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

public class DatabaseOptionAction implements ValueChangeListener{
    
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseOptionAction.class);
    private static final long serialVersionUID = -5091664285613837786L;

    private SelectDatasetForBreedingViewPanel selectDatasetForBreedingViewWindow;

    public DatabaseOptionAction(SelectDatasetForBreedingViewPanel selectDatasetForBreedingViewWindow) {
        
        this.selectDatasetForBreedingViewWindow = selectDatasetForBreedingViewWindow;

    }

    @Override
    public void valueChange(ValueChangeEvent event) {

            if (event.getProperty().getValue().equals(Database.CENTRAL)) {
                LOG.info("Swtiched Database to CENTRAL");
                selectDatasetForBreedingViewWindow.refreshStudyTreeTable(Database.CENTRAL);   
                
            } else {
                LOG.info("Swtiched Database to LOCAL");
                selectDatasetForBreedingViewWindow.refreshStudyTreeTable(Database.LOCAL);
            }
            
            selectDatasetForBreedingViewWindow.setCurrentStudy(null);
            selectDatasetForBreedingViewWindow.setCurrentRepresentationId(null);
            selectDatasetForBreedingViewWindow.setCurrentDatasetName(null);
            
    }
}
