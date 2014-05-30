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

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.domain.dms.*;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Multisite analysis component
 * 
 * @author Aldrich Abrogena
 */
@Configurable
public class MultiSiteAnalysisPanel extends VerticalLayout implements
InitializingBean {

	private static final long serialVersionUID = 1L;

	// private TwinColSelect select;

	private Map<Integer, Table> studyTables = new HashMap<Integer, Table>();
	private TabSheet studiesTabsheet;
	private VerticalLayout tabSheetContainer;

	private Button browseLink;

	private Label lblPageTitle;

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
		assemble();
		updateLabels();
	}

	private void updateLabels(){
		messageSource.setValue(lblPageTitle, Message.TITLE_GXE);
		
	}

	protected void assemble() {

		initializeComponents();
		initializeLayout();
		initializeActions();

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


	protected void initializeComponents() {

		browseLink = new Button();
		browseLink.setImmediate(true);
		browseLink.setStyleName("link");
		browseLink.setCaption("Browse");
		browseLink.setWidth("48px");
		browseLink.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(ClickEvent event) {

				SelectStudyDialog dialog = new SelectStudyDialog(event.getComponent().getWindow(), MultiSiteAnalysisPanel.this ,(StudyDataManagerImpl) getStudyDataManager());
				event.getComponent().getWindow().addWindow(dialog);
			}

		});

		HorizontalLayout browseLabelLayout = new HorizontalLayout();
		browseLabelLayout.addComponent(browseLink);
		browseLabelLayout.addComponent(new Label("for a study to work with."));
		browseLabelLayout.setSizeUndefined();

		lblPageTitle = new Label();
		lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		ThemeResource resource = new ThemeResource("../vaadin-retro/images/search-nurseries.png");
        Label headingLabel =  new Label("Select Data for Analysis");
        headingLabel.setStyleName(Bootstrap.Typography.H4.styleName());
        HeaderLabelLayout heading = new HeaderLabelLayout(resource,headingLabel);
        heading.setMargin(true, false, false, false);
		
		
		ManagerFactory managerFactory = managerFactoryProvider
				.getManagerFactoryForProject(project);
		setStudyDataManager(managerFactory.getNewStudyDataManager());

		addComponent(lblPageTitle);
		addComponent(heading);
		addComponent(browseLabelLayout);

		setStudiesTabsheet(generateTabSheet());
		
		tabSheetContainer = new VerticalLayout();
		tabSheetContainer.addComponent(getStudiesTabsheet());
		tabSheetContainer.setMargin(true, false,false,false);
		
		//addComponent(tabSheetContainer);

	}


	protected void initializeLayout() {
		//this.setSpacing(true);
		this.setMargin(new MarginInfo(false,true,true,true));
		this.setSizeUndefined();
		this.setWidth("100%");
	}

	protected void initializeActions() {



	}


	public void openStudyMeansDataset(Study study) {

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

			if (study.getName() != null && getStudyDataManager().getDataSetsByType(study.getId(), DataSetType.MEANS_DATA).size() > 0){

				MultiSiteAnalysisSelectPanel selectEnvironmentPanel = new MultiSiteAnalysisSelectPanel(getStudyDataManager() ,project, study, this);

				selectEnvironmentPanel.setCaption(study.getName());
				getStudiesTabsheet().addTab(selectEnvironmentPanel);
				getStudiesTabsheet().getTab(selectEnvironmentPanel).setClosable(true);

			}


		} catch (NumberFormatException e) {

			e.printStackTrace();

		} catch (MiddlewareQueryException e) {

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
