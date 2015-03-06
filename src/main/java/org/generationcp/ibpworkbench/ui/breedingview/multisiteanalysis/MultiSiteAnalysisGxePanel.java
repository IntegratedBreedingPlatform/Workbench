/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.RunMultiSiteAction;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Aldrin Batac
 *
 */
@Configurable
public class MultiSiteAnalysisGxePanel extends VerticalLayout implements InitializingBean, 
										InternationalizableComponent, IBPWorkbenchLayout {

	private static final long serialVersionUID = 1L;
    
    private GxeTable gxeTable;
    
    private Table selectTraitsTable;
    
    private Integer currentRepresentationId;
    
    private Integer currentDataSetId;
    
    private String currentDatasetName;
    
    private Button btnBack;
    private Button btnReset;
    private Button btnRunMultiSite;
    private Map<String, Boolean> variatesCheckboxState;
    private MultiSiteAnalysisSelectPanel gxeSelectEnvironmentPanel;
    
    private List<DataSet> ds;
    
    @Value("${workbench.is.server.app}")
	private String isServerApp;
    
    @Autowired
	private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
	private ToolUtil toolUtil;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;
    
    private MultiSiteParameters multiSiteParameters;

	private Label lblDataSelectedForAnalysisHeader;
	private Label lblDatasetName;
	private Label txtDatasetName;
	private Label lblDatasourceName;
	private Label txtDatasourceName;
	private Label lblSelectedEnvironmentFactor;
	private Label txtSelectedEnvironmentFactor;
	private Label lblSelectedEnvironmentGroupFactor;
	private Label txtSelectedEnvironmentGroupFactor;
	private Label lblAdjustedMeansHeader;
	private Label lblAdjustedMeansDescription;
	private Label lblSelectTraitsForAnalysis;
	private CheckBox chkSelectAllEnvironments;
	private CheckBox chkSelectAllTraits;
	private Property.ValueChangeListener selectAllEnvironmentsListener;
	private Property.ValueChangeListener selectAllTraitsListener;
	
	
    public MultiSiteAnalysisGxePanel(StudyDataManager studyDataManager,
    		MultiSiteAnalysisSelectPanel gxeSelectEnvironmentPanel, 
    		Map<String, Boolean> variatesCheckboxState, MultiSiteParameters multiSiteParameters) {
    	this.studyDataManager = studyDataManager;
        this.gxeSelectEnvironmentPanel = gxeSelectEnvironmentPanel;
        this.variatesCheckboxState = variatesCheckboxState;
        this.multiSiteParameters = multiSiteParameters;
        setCaption(multiSiteParameters.getStudy().getName());
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
		
		updateLabels();
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        messageSource.setCaption(btnBack, Message.BACK);
        messageSource.setCaption(btnReset, Message.RESET);
        if (Boolean.parseBoolean(isServerApp)){
			messageSource.setCaption(btnRunMultiSite, Message.DOWNLOAD_INPUT_FILES);
        }else{
        	messageSource.setCaption(btnRunMultiSite, Message.LAUNCH_BREEDING_VIEW);
        }
        
        messageSource.setValue(lblDataSelectedForAnalysisHeader, Message.GXE_SELECTED_INFO);
        messageSource.setValue(lblDatasetName , Message.BV_DATASET_NAME);
        messageSource.setValue(lblDatasourceName, Message.BV_DATASOURCE_NAME);
        messageSource.setValue(lblSelectedEnvironmentFactor, Message.GXE_SELECTED_ENVIRONMENT_FACTOR);
        messageSource.setValue(lblSelectedEnvironmentGroupFactor, Message.GXE_SELECTED_ENVIRONMENT_GROUP_FACTOR);
        messageSource.setValue(lblAdjustedMeansDescription  , Message.GXE_ADJUSTED_MEANS_DESCRIPTION);
        messageSource.setValue(lblSelectTraitsForAnalysis, Message.GXE_SELECT_TRAITS_FOR_ANALYSIS);
    }
    
	@Override
	public void instantiateComponents() {
		
		managerFactory = managerFactoryProvider.getManagerFactoryForProject(multiSiteParameters.getProject());
		
	   	lblDataSelectedForAnalysisHeader = new Label();
    	lblDataSelectedForAnalysisHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	
    	lblDatasetName = new Label();
    	lblDatasetName.setStyleName("label-bold");
    	
    	txtDatasetName = new Label();
    	
    	lblDatasourceName = new Label();
    	lblDatasourceName.setStyleName("label-bold");
    	
    	txtDatasourceName = new Label();
    	
    	lblSelectedEnvironmentFactor = new Label();
    	lblSelectedEnvironmentFactor.setStyleName("label-bold");
    	
    	txtSelectedEnvironmentFactor = new Label();
    	
    	lblSelectedEnvironmentGroupFactor = new Label();
    	lblSelectedEnvironmentGroupFactor.setStyleName("label-bold");
    	
    	txtSelectedEnvironmentGroupFactor = new Label();
    	
    	chkSelectAllEnvironments = new CheckBox("Select all environments", true);
    	chkSelectAllEnvironments.setImmediate(true);
    	
    	chkSelectAllTraits = new CheckBox("Select all traits", true);
    	chkSelectAllTraits.setImmediate(true);
    	
    	lblAdjustedMeansHeader = new Label("<span class='bms-dataset' style='position:relative; top: -1px; color: #FF4612; "
        		+ "font-size: 22px; font-weight: bold;'></span><b>&nbsp;"+
        		messageSource.getMessage(Message.GXE_ADJUSTED_MEANS_HEADER)+"</b>",Label.CONTENT_XHTML);
    	lblAdjustedMeansHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	
    	lblAdjustedMeansDescription  = new Label();
    	
    	lblSelectTraitsForAnalysis = new Label();
    	    	
    	btnRunMultiSite = new Button();
		btnBack = new Button();  
		btnReset = new Button(); 
        
	}

	@Override
	public void initializeValues() {
		ds = null;
		try {
			ds = studyDataManager.getDataSetsByType(multiSiteParameters.getStudy().getId(), DataSetType.MEANS_DATA);
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
		
		if (ds != null && ds.size() > 0){
			setCaption(ds.get(0).getName());
			txtDatasetName.setValue(ds.get(0).getName());
			txtDatasourceName.setValue(multiSiteParameters.getStudy().getName());
			txtSelectedEnvironmentFactor.setValue(multiSiteParameters.getSelectedEnvironmentFactorName());
			txtSelectedEnvironmentGroupFactor.setValue(multiSiteParameters.getSelectedEnvGroupFactorName());
			
			Property.ValueChangeListener envCheckBoxListener = new Property.ValueChangeListener(){
				
				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					Boolean val = (Boolean) event.getProperty().getValue();
					if (val == false){
						chkSelectAllEnvironments.removeListener(selectAllEnvironmentsListener);
						chkSelectAllEnvironments.setValue(false);
						chkSelectAllEnvironments.addListener(selectAllEnvironmentsListener);
					}
					
				}
				
			};
			
			setGxeTable(new GxeTable(studyDataManager, multiSiteParameters.getStudy().getId(), multiSiteParameters.getSelectedEnvironmentFactorName(), multiSiteParameters.getSelectedEnvGroupFactorName(), variatesCheckboxState, envCheckBoxListener));
			getGxeTable().setHeight("300px");
		}
		
		selectTraitsTable = new Table();
		IndexedContainer container = new IndexedContainer();
		
		Property.ValueChangeListener traitCheckBoxListener = new Property.ValueChangeListener(){
		
			private static final long serialVersionUID = -1109780465477901066L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean val = (Boolean) event.getProperty().getValue();
				
				if (val == false){
					chkSelectAllTraits.removeListener(selectAllTraitsListener);
					chkSelectAllTraits.setValue(false);
					chkSelectAllTraits.addListener(selectAllTraitsListener);
				}
				
			}
			
		};
				
		List<CheckBox> cells = new ArrayList<CheckBox>();
		List<String> columnNames = new ArrayList<String>();

		SortedSet<String> keys = new TreeSet<String>(getVariatesCheckboxState().keySet());
		for (String key : keys) { 
		   if(getVariatesCheckboxState().get(key)){
			   container.addContainerProperty(key, CheckBox.class, null);
				columnNames.add(key.replace("_Means", ""));
				CheckBox chk = new CheckBox("", true);
				chk.setImmediate(true);
				chk.addListener(traitCheckBoxListener);
				cells.add(chk);
		   }
		 
		}
		
		selectTraitsTable.setContainerDataSource(container);
		selectTraitsTable.addItem(cells.toArray(new Object[0]), 1);
		selectTraitsTable.setHeight("80px");
		selectTraitsTable.setWidth("100%");
		selectTraitsTable.setColumnHeaders(columnNames.toArray(new String[0]));
		selectTraitsTable.setColumnCollapsingAllowed(true);
		for (Entry<String, Boolean> trait : getVariatesCheckboxState().entrySet()){
			if (trait.getValue()){
				selectTraitsTable.setColumnWidth(trait.getKey(), 100);
			}
		}
	}

	@Override
	public void addListeners() {
		selectAllEnvironmentsListener = new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				for (Iterator<?> itr = gxeTable.getItemIds().iterator(); itr.hasNext();){
					CheckBox chk = (CheckBox) gxeTable.getItem(itr.next()).getItemProperty((Object) " ").getValue();
					chk.setValue((Boolean) event.getProperty().getValue());
				}
				
			}
		};
    	
    	selectAllTraitsListener = new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				for (Iterator<?> itr = selectTraitsTable.getContainerPropertyIds().iterator(); itr.hasNext();){
					CheckBox chk = (CheckBox) selectTraitsTable.getItem(1).getItemProperty(itr.next()).getValue();
					chk.setValue(event.getProperty().getValue());
				}
				
				
			}
		};

		//Generate Buttons
		btnRunMultiSite.addListener(new RunMultiSiteAction(managerFactory, studyDataManager, gxeTable, selectTraitsTable, multiSiteParameters));
	
		btnBack.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;
	
			@Override
			public void buttonClick(ClickEvent event) {
				
				TabSheet tabSheet = gxeSelectEnvironmentPanel.getGxeAnalysisComponentPanel()
					.getStudiesTabsheet();
				tabSheet.replaceComponent(tabSheet.getSelectedTab(), gxeSelectEnvironmentPanel);
					
			}
		});
	
		btnReset.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;
	
			@Override
			public void buttonClick(ClickEvent event) {
				
				chkSelectAllTraits.setValue(true);
				chkSelectAllEnvironments.setValue(true);
			}
		});
		
		chkSelectAllEnvironments.addListener(selectAllEnvironmentsListener);
		
		chkSelectAllTraits.addListener(selectAllTraitsListener);
	}

	@Override
	public void layoutComponents() {
        setWidth("100%");
        setSpacing(true);
		setMargin(true);
		
		GridLayout selectedInfoLayout = new GridLayout(4, 3);
        selectedInfoLayout.setSizeUndefined();
        selectedInfoLayout.setSpacing(true);
        selectedInfoLayout.setMargin(false, false, true, false);
        selectedInfoLayout.setColumnExpandRatio(0, 1);
        selectedInfoLayout.setColumnExpandRatio(1, 3);
        selectedInfoLayout.setColumnExpandRatio(2, 2);
        selectedInfoLayout.setColumnExpandRatio(3, 1);
        selectedInfoLayout.addComponent(lblDatasetName, 0, 1);
        selectedInfoLayout.addComponent(txtDatasetName, 1, 1);
        selectedInfoLayout.addComponent(lblDatasourceName, 0, 2);
        selectedInfoLayout.addComponent(txtDatasourceName, 1, 2);
        selectedInfoLayout.addComponent(lblSelectedEnvironmentFactor, 2, 1);
        selectedInfoLayout.addComponent(txtSelectedEnvironmentFactor, 3, 1);
        selectedInfoLayout.addComponent(lblSelectedEnvironmentGroupFactor , 2, 2);
        selectedInfoLayout.addComponent(txtSelectedEnvironmentGroupFactor, 3, 2);
		
        addComponent(lblDataSelectedForAnalysisHeader);
		addComponent(selectedInfoLayout);
		
		addComponent(lblAdjustedMeansHeader);
		addComponent(lblAdjustedMeansDescription);
		addComponent(getGxeTable());
		addComponent(chkSelectAllEnvironments);
		setExpandRatio(getGxeTable(), 1.0F);
		
		// hack, just wanna add space here
		addComponent(new Label("<br/>", Label.CONTENT_XHTML));
		
		addComponent(lblSelectTraitsForAnalysis);		
		addComponent(selectTraitsTable);
		setExpandRatio(selectTraitsTable, 1.0F);
		addComponent(chkSelectAllTraits);
		
        addComponent(layoutButtonArea());
	}
    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        buttonLayout.setSizeFull();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);

        btnRunMultiSite.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
        spacer.setSizeFull();
        
        buttonLayout.addComponent(spacer);
        buttonLayout.setExpandRatio(spacer,1.0F);
        buttonLayout.addComponent(btnBack);
        buttonLayout.addComponent(btnReset);
        buttonLayout.addComponent(btnRunMultiSite);

        return buttonLayout;
    }

    @Override
	public Object getData(){
		return multiSiteParameters.getStudy();
		
	}

    public Integer getCurrentRepresentationId() {
        return currentRepresentationId;
    }

    public void setCurrentRepresentationId(Integer currentRepresentationId) {
        this.currentRepresentationId = currentRepresentationId;
    }
    
    public Integer getCurrentDataSetId() {
        return currentDataSetId;
    }

    public void setCurrentDataSetId(Integer currentDataSetId) {
        this.currentDataSetId = currentDataSetId;
    }

    public String getCurrentDatasetName() {
        return currentDatasetName;
    }

    public void setCurrentDatasetName(String currentDatasetName) {
        this.currentDatasetName = currentDatasetName;
    }
    
    public StudyDataManager getStudyDataManager() {
    	if (this.studyDataManager == null) {
            this.studyDataManager = managerFactory.getNewStudyDataManager();
        }
		return this.studyDataManager;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return variatesCheckboxState;
	}
	
	public void setVariatesCheckboxState(HashMap<String, Boolean> hashMap) {
			this.variatesCheckboxState = hashMap;
	}

	public GxeTable getGxeTable() {
		return gxeTable;
	}

	public void setGxeTable(GxeTable gxeTable) {
		this.gxeTable = gxeTable;
	}

}// End of MultiSiteAnalysisGxePanel
