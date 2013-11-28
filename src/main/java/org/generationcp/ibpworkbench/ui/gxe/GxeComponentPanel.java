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
package org.generationcp.ibpworkbench.ui.gxe;

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
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenWorkflowForRoleAction;
import org.generationcp.ibpworkbench.ui.StudiesTabCloseListener;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.GxeUtility;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
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
import com.vaadin.terminal.ThemeResource;
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
public class GxeComponentPanel extends VerticalLayout implements
		InitializingBean {

	private static final Logger LOG = LoggerFactory
			.getLogger(GxeComponentPanel.class);
	private static final long serialVersionUID = 1L;

	// private TwinColSelect select;

	private Map<Integer, Table> studyTables = new HashMap<Integer, Table>();
	private Tree studiesTree;
	private TabSheet studiesTabsheet;
	private ThemeResource folderResource;
    private ThemeResource leafResource;
	
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

	public GxeComponentPanel(Project project,Role role) {
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
				if (childStudy instanceof StudyReference){
					Object itemId = addCaptionedItem(childStudy.getName(), childStudy.getId(),
							folderParentItem);
					studiesTree.setItemIcon(itemId, leafResource);
				
				}else if (childStudy instanceof FolderReference) {
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
			studiesTree.setItemIcon(folderParentItem, folderResource);
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

	public void generateTabContent(Study study, String selectedEnvFactorName, String selectedEnvGroupFactorName, Map<String, Boolean> variatesCheckboxState, GxeSelectEnvironmentPanel gxeSelectEnvironmentPanel) {
	
		if (selectedEnvFactorName == null || selectedEnvFactorName == "") return;

		GxeEnvironmentAnalysisPanel tabContainer = new GxeEnvironmentAnalysisPanel(studyDataManager, project, study, gxeSelectEnvironmentPanel, selectedEnvFactorName, selectedEnvGroupFactorName, variatesCheckboxState);
		tabContainer.setSelectedEnvFactorName(selectedEnvFactorName);

		getStudiesTabsheet().replaceComponent(getStudiesTabsheet().getSelectedTab(), tabContainer);
		getStudiesTabsheet().getTab(tabContainer).setClosable(true);
		getStudiesTabsheet().setSelectedTab(tabContainer);
	}
	

	protected void initializeComponents() {
		
		folderResource =  new ThemeResource("images/folder.png");
        leafResource =  new ThemeResource("images/leaf_16.png");
		
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
		selectDatabase.select(Database.LOCAL);
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

		setStudiesTabsheet(generateTabSheet());

		horizontal.addComponent(getStudiesTabsheet());

		horizontal.setWidth("100%");
		//horizontal.setHeight("530px");
		horizontal.setExpandRatio(getStudiesTabsheet(), 1.0F);

		this.addComponent(horizontal);

	}


	protected void initializeLayout() {
		this.setSpacing(true);
		this.setMargin(true);

        this.setSizeUndefined();
        this.setWidth("100%");
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
		private GxeComponentPanel gxeAnalysisComponentPanel;
		
		private static final long serialVersionUID = 1L;

		public StudiesTreeAction(
				GxeComponentPanel gxeAnalysisComponentPanel) {
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
			
				for ( Iterator<Component> tabs = getStudiesTabsheet().getComponentIterator(); tabs.hasNext();){
					Component tab = tabs.next();
					Study tabStudyData = (Study)((VerticalLayout) tab).getData();
					if (p2.getValue() != null && tabStudyData != null){
						if (tabStudyData.getId() == Integer.parseInt(p2.getValue().toString())){
							
							GxeSelectEnvironmentPanel selectEnvironmentPanel = new GxeSelectEnvironmentPanel(studyDataManager ,project, tabStudyData, gxeAnalysisComponentPanel);
							selectEnvironmentPanel.setCaption(tabStudyData.getName());
							
							((VerticalLayout) tab).addComponent(selectEnvironmentPanel);
							
							studyTables.remove(tabStudyData.getId());
							//studiesTabsheet.removeComponent(tab);
							
							return;
						}
					}
					
				}
			
			
			try {
				Study study = studyDataManager.getStudy(Integer.parseInt(p2.toString()));
				
				if (study==null) return;
				System.out.println("selected from folder tree:" + study.toString());
			
				if (study.getName() != null && studyDataManager.getDataSetsByType(study.getId(), DataSetType.MEANS_DATA).size() > 0){
					
					GxeSelectEnvironmentPanel selectEnvironmentPanel = new GxeSelectEnvironmentPanel(studyDataManager ,project, study, gxeAnalysisComponentPanel);
					
					selectEnvironmentPanel.setCaption(study.getName());
					getStudiesTabsheet().addTab(selectEnvironmentPanel);
					getStudiesTabsheet().getTab(selectEnvironmentPanel).setClosable(true);
					
					//gxeAnalysisComponentPanel.getWindow().addWindow(win);
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

	public TabSheet getStudiesTabsheet() {
		return studiesTabsheet;
	}

	public void setStudiesTabsheet(TabSheet studiesTabsheet) {
		this.studiesTabsheet = studiesTabsheet;
	}

}
