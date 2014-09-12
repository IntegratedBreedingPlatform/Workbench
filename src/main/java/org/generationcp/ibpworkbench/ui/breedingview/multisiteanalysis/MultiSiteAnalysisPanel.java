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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.SelectStudyDialog;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Multisite analysis component
 * 
 * @author Aldrich Abrogena
 */
@Configurable
public class MultiSiteAnalysisPanel extends VerticalLayout implements InitializingBean, 
								InternationalizableComponent, IBPWorkbenchLayout {

	private static final long serialVersionUID = 1L;

	// private TwinColSelect select;

	private Map<Integer, Table> studyTables = new HashMap<Integer, Table>();
	private TabSheet studiesTabsheet;
	private VerticalLayout tabSheetContainer;

	private Button browseLink;

	private Label lblPageTitle;
	private HeaderLabelLayout heading;

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

	private final static Logger LOG = LoggerFactory.getLogger(MultiSiteAnalysisPanel.class);

	public MultiSiteAnalysisPanel(Project project) {
		LOG.debug("Project is " + project.getProjectName());
		this.project = project;
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
	public void updateLabels() {
		messageSource.setValue(lblPageTitle, Message.TITLE_GXE);	
	}
	
	@Override
	public void instantiateComponents() {
		
		ManagerFactory managerFactory = managerFactoryProvider
				.getManagerFactoryForProject(project);
		setStudyDataManager(managerFactory.getNewStudyDataManager());
		
		lblPageTitle = new Label();
    	lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());
    	lblPageTitle.setHeight("26px");

		ThemeResource resource = new ThemeResource("../vaadin-retro/images/search-nurseries.png");
        Label headingLabel =  new Label("Select Data for Analysis");
        headingLabel.setStyleName(Bootstrap.Typography.H4.styleName());
        headingLabel.addStyleName("label-bold");
        heading = new HeaderLabelLayout(resource,headingLabel);

		browseLink = new Button();
		browseLink.setImmediate(true);
		browseLink.setStyleName("link");
		browseLink.setCaption("Browse");
		browseLink.setWidth("48px");

		setStudiesTabsheet(generateTabSheet());
		
		tabSheetContainer = new VerticalLayout();
		tabSheetContainer.addComponent(getStudiesTabsheet());
		tabSheetContainer.setMargin(true, false,false,false);
	}

	@Override
	public void initializeValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListeners() {
		browseLink.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(ClickEvent event) {

				SelectStudyDialog dialog = new SelectStudyDialog(event.getComponent().getWindow(), MultiSiteAnalysisPanel.this ,(StudyDataManagerImpl) getStudyDataManager());
				event.getComponent().getWindow().addWindow(dialog);
			}

		});
	}

	@Override
	public void layoutComponents() {
		this.setSpacing(true);
		this.setMargin(new MarginInfo(false,true,true,true));
		this.setSizeUndefined();
		this.setWidth("100%");
		
		HorizontalLayout browseLabelLayout = new HorizontalLayout();
		browseLabelLayout.addComponent(browseLink);
		browseLabelLayout.addComponent(new Label("for a study to work with."));
		browseLabelLayout.setSizeUndefined();
		
		VerticalLayout selectDataForAnalysisLayout = new VerticalLayout();
    	selectDataForAnalysisLayout.addComponent(heading);
    	selectDataForAnalysisLayout.addComponent(browseLabelLayout);
		
		addComponent(lblPageTitle);
		addComponent(selectDataForAnalysisLayout);
	}

	protected void repaintTab(Component comp, Study study) {

		if (comp != null) {
			VerticalLayout container = (VerticalLayout) comp;
			container.setSpacing(true);
			container.setMargin(true, false, false, false);
			container.removeAllComponents();

			Label tabTitle = new Label("&nbsp;&nbsp;"
					+ "Adjusted means dataset", Label.CONTENT_XHTML);
			tabTitle.setStyleName(Bootstrap.Typography.H1.styleName());

			container.addComponent(tabTitle);

			container.addComponent(studyTables.get(study.getId()));
			container.setExpandRatio(studyTables.get(study.getId()), 1.0F);

			container.setSizeFull();
		}
	}

	public void generateTabContent(Study study, String selectedEnvFactorName,String selectedGenotypeFactorName, String selectedEnvGroupFactorName, Map<String, Boolean> variatesCheckboxState, MultiSiteAnalysisSelectPanel gxeSelectEnvironmentPanel) {

		if (selectedEnvFactorName == null || selectedEnvFactorName == "") return;

		MultiSiteAnalysisGxePanel tabContainer = new MultiSiteAnalysisGxePanel(getStudyDataManager(), project, study, gxeSelectEnvironmentPanel, selectedEnvFactorName, selectedGenotypeFactorName ,selectedEnvGroupFactorName, variatesCheckboxState);
		tabContainer.setSelectedEnvFactorName(selectedEnvFactorName);
		tabContainer.setVisible(true);

		getStudiesTabsheet().setVisible(true);
		getStudiesTabsheet().replaceComponent(getStudiesTabsheet().getSelectedTab(), tabContainer);
		getStudiesTabsheet().getTab(tabContainer).setClosable(true);
		getStudiesTabsheet().setSelectedTab(tabContainer);
	}

	public void openStudyMeansDataset(Study study) throws MiddlewareQueryException {

		if (getComponentIndex(tabSheetContainer) == -1){
			addComponent(tabSheetContainer);
		}

		for ( Iterator<Component> tabs = getStudiesTabsheet().getComponentIterator(); tabs.hasNext();){
			Component tab = tabs.next();
			Study tabStudyData = (Study)((VerticalLayout) tab).getData();
			if (tabStudyData.getId() == study.getId()){
				studiesTabsheet.setSelectedTab(tab);
				return;
			}

		}


		try {

			
			if (study==null) return;
			LOG.debug("selected from folder tree:" + study.toString());
			
			List<DataSet> dataSets = null;
			try{
				dataSets = getStudyDataManager().getDataSetsByType(study.getId(), DataSetType.MEANS_DATA);
			}catch(MiddlewareQueryException e){
				e.printStackTrace();
			}

			if (dataSets != null && study.getName() != null && dataSets.size() > 0){

				MultiSiteAnalysisSelectPanel selectEnvironmentPanel = new MultiSiteAnalysisSelectPanel(getStudyDataManager() ,project, study, this);

				selectEnvironmentPanel.setCaption(study.getName());
				getStudiesTabsheet().addTab(selectEnvironmentPanel);
				getStudiesTabsheet().getTab(selectEnvironmentPanel).setClosable(true);

			}else{
				throw new MiddlewareQueryException("This study doesnt have an existing MEANS dataset.");
			}


		} catch (NumberFormatException e) {

			e.printStackTrace();

		} 

		requestRepaintAll();


	}



	protected TabSheet generateTabSheet() {
		TabSheet tab = new TabSheet();

		tab.setImmediate(true);
		tab.setStyleName(Reindeer.TABSHEET_MINIMAL);
		tab.setStyleName("panel-border");
		//tab.setSizeFull();
		tab.setHeight("100%");

		return tab;
	}

	public TabSheet getStudiesTabsheet() {
		return studiesTabsheet;
	}

	public void setStudiesTabsheet(TabSheet studiesTabsheet) {
		this.studiesTabsheet = studiesTabsheet;
	}

	public StudyDataManager getStudyDataManager() {
		return studyDataManager;
	}

	public void setStudyDataManager(StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}
}
