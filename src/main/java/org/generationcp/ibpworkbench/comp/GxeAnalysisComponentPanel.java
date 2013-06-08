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
package org.generationcp.ibpworkbench.comp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenWorkflowForRoleAction;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectEnvironmentForGxeWindow;
import org.generationcp.ibpworkbench.comp.table.GxeTable;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.GxeUtility;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DataSetType;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Multisite analysis component
 * 
 * @author Aldrich Abrogena
 */
@Configurable
public class GxeAnalysisComponentPanel extends VerticalLayout implements
		InitializingBean {

	private static final Logger LOG = LoggerFactory
			.getLogger(GxeAnalysisComponentPanel.class);
	private static final long serialVersionUID = 1L;

	// private TwinColSelect select;

	private Map<Integer, Table> studyTables = new HashMap<Integer, Table>();
	private Tree studiesTree;
	private TabSheet studiesTabsheet;
	
	protected Boolean setAll = true;
	protected Boolean fromOthers = true;

	private StudyDataManager studyDataManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private ToolUtil toolUtil;

	private Project project;
	private Role role;
	
	private Select selectDatabase = new Select();

	public GxeAnalysisComponentPanel(Project project,Role role) {
		LOG.debug("Project is " + project.getProjectName());
		this.project = project;
		this.role = role;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assemble();
	}

	protected void assemble() {

		initializeComponents();
		initializeLayout();
		initializeActions();

	}

	/**
	 * Helper to add an item with specified caption and (optional) parent.
	 * 
	 * @param caption
	 *            The item caption
	 * @param parent
	 *            The (optional) parent item id
	 * @return the created item's id
	 */
	private Object addCaptionedItem(String caption, Integer objid, Object parent) {
		// add item, let tree decide id
		final Object id = studiesTree.addItem();
		// get the created item
		final Item item = studiesTree.getItem(id);
		// set our "caption" property
		final Property p = item.getItemProperty("caption");
		p.setValue(caption);

		final Property idp = item.getItemProperty("id");
		idp.setValue(objid);

		if (parent != null) {
			studiesTree.setChildrenAllowed(parent, true);
			studiesTree.setParent(id, parent);
			studiesTree.setChildrenAllowed(id, false);
		}
		return id;
	}

	protected void generateChildren(FolderReference folderParent,
			Object folderParentItem) throws MiddlewareQueryException {

		List<Reference> children = studyDataManager
				.getChildrenOfFolder(folderParent.getId());

		if (children.size() == 0) {
			// The planet has no moons so make it a leaf.
			// studiesTree.setChildrenAllowed(folderParent.getName(), false);
		} else {
			// Add children (moons) under the planets.
			for (Reference childStudy : children) {
				if (childStudy instanceof StudyReference)
					addCaptionedItem(childStudy.getName(), childStudy.getId(),
							folderParentItem);
				else if (childStudy instanceof FolderReference) {
					Object myfolderParentItem = addCaptionedItem(
							childStudy.getName(), childStudy.getId(),
							folderParentItem);
					generateChildren((FolderReference) childStudy,
							myfolderParentItem);
				}

			}

			// Expand the subtree.
			// studiesTree.expandItemsRecursively(studies);
		}
	}

	protected void refreshStudies() throws MiddlewareQueryException {

		List<FolderReference> listFolder = studyDataManager
				.getRootFolders((Database)selectDatabase.getValue());

		studiesTree.removeAllItems();
		
		studiesTree.addContainerProperty("caption", String.class, "");
		studiesTree.addContainerProperty("id", String.class, "");

		studiesTree
				.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		studiesTree.setItemCaptionPropertyId("caption");

		/* Add planets as root items in the tree. */
		for (FolderReference folderParent : listFolder) {
			Object folderParentItem = addCaptionedItem(folderParent.getName(),
					folderParent.getId(), null);
			generateChildren(folderParent, folderParentItem);

		}

	}

	protected void repaintTab(Component comp, Study study) {

		if (comp != null) {
			VerticalLayout container = (VerticalLayout) comp;
			container.setSpacing(true);
			container.setMargin(true, false, false, false);
			container.removeAllComponents();

			Label tabTitle = new Label("&nbsp;&nbsp;"
					+ "Adjusted means dataset", Label.CONTENT_XHTML);
			tabTitle.setStyleName("gcp-content-title");

			container.addComponent(tabTitle);
			
			container.addComponent(studyTables.get(study.getId()));
			container.setExpandRatio(studyTables.get(study.getId()), 1.0F);

			container.setSizeFull();
		}
	}

	public void generateTabContent(Study study, String selectedEnvFactorName) {
	
		if (selectedEnvFactorName == null || selectedEnvFactorName == "") return;

		VerticalLayout tabContainer = new VerticalLayout();
		tabContainer.setStyleName("gcp-light-grey");
		tabContainer.setSpacing(true);
		tabContainer.setMargin(true, false, false, false);

		Label tabTitle = new Label("&nbsp;&nbsp;" + "Adjusted means datasets",
				Label.CONTENT_XHTML);
		tabTitle.setStyleName("gcp-content-title");
		tabContainer.addComponent(tabTitle);
		
		List<DataSet> ds = null;
		
		DataSet meansDataSet = null;
		try {
			ds = studyDataManager.getDataSetsByType(study.getId(), DataSetType.MEANS_DATA);
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (ds != null && ds.size() > 0){
			studyTables.put(study.getId(), new GxeTable(studyDataManager, study.getId(), selectedEnvFactorName));
			tabContainer.addComponent(studyTables.get(study.getId()));
			tabContainer.setExpandRatio(studyTables.get(study.getId()), 1.0F);
			
			meansDataSet = ds.get(0);
		}else{
			Label temp = new Label("&nbsp;&nbsp;No means dataset available for this study (" + study.getName().toString() + ")" );
			temp.setContentMode(Label.CONTENT_XHTML);
			tabContainer.addComponent(temp);
			tabContainer.setExpandRatio(temp, 1.0F);
		}
		
	
		tabContainer.setSizeFull();
		tabContainer.setCaption(study.getName());
		tabContainer.setData(study);
		
		studiesTabsheet.addComponent(tabContainer);
		
		if (meansDataSet != null)
			studiesTabsheet.getTab(tabContainer).setCaption(meansDataSet.getName());
		
		studiesTabsheet.getTab(tabContainer).setClosable(true);
		studiesTabsheet.setCloseHandler(new StudiesTabCloseListener(studyTables));
		studiesTabsheet.setSelectedTab(tabContainer);
	}

	protected void initializeComponents() {

		setSpacing(true);
		setMargin(true);
	
		HorizontalLayout horizontal = new HorizontalLayout();

		ManagerFactory managerFactory = managerFactoryProvider
				.getManagerFactoryForProject(project);
		studyDataManager = managerFactory.getNewStudyDataManager();

		studiesTree = new Tree("Studies");
		studiesTree.setSizeFull();
		studiesTree.setImmediate(true);

		Panel studiesPanel = new Panel();
		studiesPanel.setWidth("200px");
		studiesPanel.setHeight("100%");
		
		selectDatabase.setImmediate(true);
		selectDatabase.addItem(Database.CENTRAL);
		selectDatabase.addItem(Database.LOCAL);
		selectDatabase.setCaption("Select Database");
		selectDatabase.select(Database.CENTRAL);
		selectDatabase.addListener(new Property.ValueChangeListener(){

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					refreshStudies();
				} catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		//studiesPanel.addComponent(selectDatabase);
		studiesPanel.addComponent(studiesTree);
		
		try {
			refreshStudies();
			requestRepaintAll();
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}

		horizontal.addComponent(studiesPanel);

		studiesTabsheet = generateTabSheet();

		horizontal.addComponent(studiesTabsheet);

		horizontal.setWidth("100%");
		horizontal.setHeight("530px");
		horizontal.setExpandRatio(studiesTabsheet, 1.0F);

		addComponent(horizontal);

		Button button = new Button(messageSource.getMessage(Message.LAUNCH_BREEDING_VIEW));
		//Button gxebutton = new Button("Launch the Breeding View's GxE Analysis");

		button.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -7090745965019240566L;

			@Override
			public void buttonClick(ClickEvent event) {
				final ClickEvent buttonClickEvent = event;

				/*
				ConfirmDialog.show(event.getComponent().getWindow(),"Select Dataset Export Type",
						"Please select an export type for the dataset",
						"MS Excel File","CSV File",new ConfirmDialog.Listener() {
							
							@Override
							public void onClose(ConfirmDialog dialog) {
								launchBV(dialog.isConfirmed(),buttonClickEvent.getComponent().getWindow());
							}
						});
				*/
				
				launchBV(false,buttonClickEvent.getComponent().getWindow());
						
			}
			
			private void launchBV(boolean isXLS,final Window windowSource) {
				String inputDir = "";
				Tool breedingViewTool = null;
				try{
					breedingViewTool = workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
					inputDir = toolUtil.getInputDirectoryForTool(project, breedingViewTool);
				}catch(MiddlewareQueryException ex){
					
				}
				//TODO NOTE: change the filename of xml/xls using unique identifiers
				String inputFileName = "";
				
				
				Study study = null;
				try {
					study = (Study) ((VerticalLayout)studiesTabsheet.getSelectedTab()).getData();
						
				} catch (NullPointerException e) {
					MessageNotifier
					.showError(windowSource,
							"Cannot export dataset",
							"No dataset is selected. Please open a study that has a dataset.");
					
					return;
				}
				
				if (studyTables.get(study.getId()) != null && studyTables.get(study.getId()) instanceof GxeTable) {
					GxeTable table = (GxeTable) studyTables.get(study.getId());
					
					inputFileName = String.format("%s_%s_%s", project.getProjectName().trim(), table.getMeansDataSetId(), table.getMeansDataSet().getName());
					GxeEnvironment gxeEnv = table.getGxeEnvironment();
					
					List<Trait> selectedTraits = table.getSelectedTraits();
					
					//TODO: switch xls file or csv file depending on user input
					
					File datasetExportFile = null;
					
					if (isXLS)
						datasetExportFile = GxeUtility.exportGxEDatasetToBreadingViewXls(table.getMeansDataSet(), table.getExperiments(),table.getEnvironmentName(),gxeEnv,selectedTraits, project);
					else
						datasetExportFile = GxeUtility.exportGxEDatasetToBreadingViewCsv(table.getMeansDataSet(), table.getExperiments(),table.getEnvironmentName(),gxeEnv,selectedTraits, project);
					
					
					LOG.debug(datasetExportFile.getAbsolutePath());
					
					GxeInput gxeInput =  new GxeInput(project, "", 0, 0, "", "", "", "");
					
					if (isXLS)
						gxeInput.setSourceXLSFilePath(datasetExportFile.getAbsolutePath());
					else
						gxeInput.setSourceCSVFilePath(datasetExportFile.getAbsolutePath());
				
					gxeInput.setDestXMLFilePath(String.format("%s\\%s.xml", inputDir, inputFileName));
					gxeInput.setTraits(selectedTraits);
					gxeInput.setEnvironment(gxeEnv);
					Genotypes genotypes = new Genotypes();
					
					try {
						String strGenoType;
						strGenoType = studyDataManager.getLocalNameByStandardVariableId(table.getMeansDataSetId(), 8230);
						if (strGenoType != null && strGenoType != "") genotypes.setName(strGenoType);
					} catch (MiddlewareQueryException e1) {
						genotypes.setName("G!");
					}
	
					gxeInput.setGenotypes(genotypes);
					gxeInput.setEnvironmentName(table.getEnvironmentName());
					gxeInput.setBreedingViewProjectName(project.getProjectName());
					
					GxeUtility.generateXmlFieldBook(gxeInput);
					
					File absoluteToolFile = new File(breedingViewTool.getPath()).getAbsoluteFile();
		            Runtime runtime = Runtime.getRuntime();
		            LOG.info(gxeInput.toString());
		            LOG.info(absoluteToolFile.getAbsolutePath() + " -project=\"" +  gxeInput.getDestXMLFilePath() + "\"");
		            try {
						runtime.exec(absoluteToolFile.getAbsolutePath() + " -project=\"" +  gxeInput.getDestXMLFilePath() + "\"");
					
						MessageNotifier
						.showMessage(windowSource,
								"GxE files saved",
								"Successfully generated the means dataset and xml input files for breeding view.");
		            } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						MessageNotifier
						.showMessage(windowSource,
								"Cannot launch " + absoluteToolFile.getName(),
								"But it successfully created GxE Excel and XML input file for the breeding_view!");
					}
				}
			}
		});
		
		Button testGenerateTable = new Button("Test Generate Table, studyId=10080");
		testGenerateTable.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Study study;
				try {
					study = studyDataManager.getStudy(10080);
				
					SelectEnvironmentForGxeWindow win = new SelectEnvironmentForGxeWindow(studyDataManager ,project, study, GxeAnalysisComponentPanel.this);
					GxeAnalysisComponentPanel.this.getWindow().addWindow(win);
					
				} catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		Button cancelBtn = new Button("Cancel");
		cancelBtn.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
			        
				Project project = GxeAnalysisComponentPanel.this.project;
				Role role = GxeAnalysisComponentPanel.this.role;
				
	            String url = String.format("/OpenProjectWorkflowForRole?projectId=%d&roleId=%d", project.getProjectId(), role.getRoleId());
	            (new OpenWorkflowForRoleAction(project)).doAction(event.getComponent().getWindow(), url, true);
				} catch (Exception e) {
					LOG.error("Exception", e);
		            if(e.getCause() instanceof InternationalizableException) {
		                InternationalizableException i = (InternationalizableException) e.getCause();
		                MessageNotifier.showError(event.getComponent().getWindow(), i.getCaption(), i.getDescription());
		            }
		            return;
				}
			}
		});
		
		
		this.setExpandRatio(horizontal, 1.0F);
 
		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSizeFull();
		btnLayout.setSpacing(true);
		btnLayout.setMargin(true);
		
		Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
		btnLayout.addComponent(spacer);
		btnLayout.setExpandRatio(spacer,1.0F);
		
		btnLayout.addComponent(button);
		btnLayout.addComponent(cancelBtn);
		//btnLayout.addComponent(gxebutton);
		//btnLayout.addComponent(testGenerateTable);
		
		this.addComponent(btnLayout);

	}


	protected void initializeLayout() {
		setSpacing(true);
		setMargin(true);
		setSizeFull();
	}

	protected void initializeActions() {
		// newMemberButton.addListener(new
		// OpenNewProjectAddUserWindowAction(select));
		studiesTree.addListener(new StudiesTreeAction(this));
		// studiesTabsheet.addListener(new StudiesTabFocusListener());

	}


	private class StudiesTreeAction implements ValueChangeListener {

		/**
		 * 
		 */
		private GxeAnalysisComponentPanel gxeAnalysisComponentPanel;
		
		private static final long serialVersionUID = 1L;

		public StudiesTreeAction(
				GxeAnalysisComponentPanel gxeAnalysisComponentPanel) {
			// TODO Auto-generated constructor stub
			this.gxeAnalysisComponentPanel = gxeAnalysisComponentPanel;
		}

		@Override
		public void valueChange(ValueChangeEvent event) {
			
			System.out.println(event);
			System.out.println(event.getProperty().getValue());
			
			Property p = event.getProperty();
			if (p.getValue() == null) return;
			Container container = studiesTree.getContainerDataSource();
			Property p2 = container.getContainerProperty(new Integer(p.toString()), "id");
			
				for ( Iterator<Component> tabs = studiesTabsheet.getComponentIterator(); tabs.hasNext();){
					Component tab = tabs.next();
					Study tabStudyData = (Study)((VerticalLayout) tab).getData();
					if (p2.getValue() != null && tabStudyData != null){
						if (tabStudyData.getId() == Integer.parseInt(p2.getValue().toString())){
							
							SelectEnvironmentForGxeWindow win = new SelectEnvironmentForGxeWindow(studyDataManager ,project, tabStudyData, gxeAnalysisComponentPanel);
							gxeAnalysisComponentPanel.getWindow().addWindow(win);
							//studiesTabsheet.setSelectedTab(tab);
							studyTables.remove(tabStudyData.getId());
							studiesTabsheet.removeComponent(tab);
							
							return;
						}
					}
					
				}
			
			
			try {
				Study study = studyDataManager.getStudy(Integer.parseInt(p2.toString()));
				
				if (study==null) return;
				System.out.println("selected from folder tree:" + study.toString());
			
				if (study.getName() != null && studyDataManager.getDataSetsByType(study.getId(), DataSetType.MEANS_DATA).size() > 0){
					
					SelectEnvironmentForGxeWindow win = new SelectEnvironmentForGxeWindow(studyDataManager ,project, study, gxeAnalysisComponentPanel);
					gxeAnalysisComponentPanel.getWindow().addWindow(win);
					//studiesTabsheet.setImmediate(true);	
				}
				
				
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
				
			} catch (MiddlewareQueryException e) {
				
				e.printStackTrace();
			}
			
			requestRepaintAll();
			

		}

	}

	protected TabSheet generateTabSheet() {
		TabSheet tab = new TabSheet();

		tab.setImmediate(true);

		//repaintTab(tab.getSelectedTab());

		tab.setSizeFull();

		return tab;
	}

}
