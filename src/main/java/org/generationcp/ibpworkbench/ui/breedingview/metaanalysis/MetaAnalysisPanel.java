/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.breedingview.metaanalysis;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import org.generationcp.browser.study.listeners.ViewStudyDetailsButtonClickListener;
import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.MetaEnvironmentModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.*;

/**
 * 
 * @author Aldrin Batac
 * 
 */
@Configurable
public class MetaAnalysisPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent, IBPWorkbenchLayout {

	private static final long serialVersionUID = 1L;

	private TabSheet tabSheet;
	private HorizontalLayout titleLayout;
	private Label toolTitle;
	private HeaderLabelLayout heading;
	private Label lblBuildNewAnalysisDescription;
	private Label lblReviewEnvironments;
	private Label lblSelectDatasetsForAnalysis;
	private Label lblSelectDatasetsForAnalysisDescription;
	private Button linkCloseAllTab;
	private Table selectedEnvironmenTable;

	private Button browseLink;

	private VerticalLayout selectedDataSetEnvironmentLayout;
	private GridLayout studyDetailsLayout;

	private Project currentProject;
	private Study currentStudy;
	private Integer currentRepresentationId;
	private Integer currentDataSetId;

	private String currentDatasetName;

	private Button btnCancel;
	private Button btnNext;
	private Component buttonArea;

	private Map<String, Boolean> variatesCheckboxState;

	private static final Logger LOG = LoggerFactory.getLogger(MetaAnalysisPanel.class);

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	public MetaAnalysisPanel(Project currentProject, Database database) {
		this.currentProject = currentProject;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setCaption(this.btnCancel, Message.RESET);
		this.messageSource.setCaption(this.btnNext, Message.NEXT);
		this.messageSource.setValue(this.toolTitle, Message.TITLE_METAANALYSIS);
		this.messageSource.setValue(this.lblBuildNewAnalysisDescription, Message.META_BUILD_NEW_ANALYSIS_DESCRIPTION);
		this.messageSource.setValue(this.lblSelectDatasetsForAnalysisDescription, Message.META_SELECT_DATASETS_FOR_ANALYSIS_DESCRIPTION);
	}

	@Override
	public void instantiateComponents() {

		this.setTitleContent();

		ThemeResource resource = new ThemeResource("../vaadin-retro/images/search-nurseries.png");
		Label headingLabel = new Label("Select Data for Analysis");
		headingLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		headingLabel.addStyleName("label-bold");
		this.heading = new HeaderLabelLayout(resource, headingLabel);

		this.browseLink = new Button();
		this.browseLink.setImmediate(true);
		this.browseLink.setStyleName("link");
		this.browseLink.setCaption("Browse");
		this.browseLink.setWidth("48px");

		this.tabSheet = new TabSheet();
		this.tabSheet.setWidth("100%");

		this.setSelectedEnvironmenTable(new Table());
		BeanItemContainer<MetaEnvironmentModel> container = new BeanItemContainer<MetaEnvironmentModel>(MetaEnvironmentModel.class);
		this.getSelectedEnvironmenTable().setWidth("100%");
		this.getSelectedEnvironmenTable().setHeight("450px");
		this.getSelectedEnvironmenTable().setContainerDataSource(container);
		this.getSelectedEnvironmenTable().setVisibleColumns(new Object[] {"studyName", "dataSetName", "trial", "environment"});
		this.getSelectedEnvironmenTable().setColumnHeaders(new String[] {"Study Name", "Dataset Name", "Trial", "Environment"});

		this.lblReviewEnvironments =
				new Label("<span class='bms-environments' style='position:relative; top: -2px; color: #0076A9; "
						+ "font-size: 25px; font-weight: bold;'></span><b>&nbsp;" + "<span style='position:relative; top: -3px;'>"
						+ this.messageSource.getMessage(Message.META_REVIEW_ENVIRONMENTS) + "</span></b>", Label.CONTENT_XHTML);
		this.lblReviewEnvironments.setStyleName(Bootstrap.Typography.H3.styleName());

		this.lblBuildNewAnalysisDescription = new Label();

		this.linkCloseAllTab = new Button();
		this.linkCloseAllTab.setStyleName("link");
		this.linkCloseAllTab.setImmediate(true);
		this.linkCloseAllTab.setCaption("Close All Tabs");
		this.linkCloseAllTab.setVisible(false);

		this.lblSelectDatasetsForAnalysis =
				new Label("<span class='bms-dataset' style='position:relative; top: -1px; color: #FF4612; "
						+ "font-size: 20px; font-weight: bold;'></span><b>&nbsp;"
						+ this.messageSource.getMessage(Message.META_SELECT_DATASETS_FOR_ANALYSIS) + "</b>", Label.CONTENT_XHTML);
		this.lblSelectDatasetsForAnalysis.setStyleName(Bootstrap.Typography.H3.styleName());

		this.lblSelectDatasetsForAnalysisDescription = new Label();

		// initialize buttons
		this.btnCancel = new Button();
		this.btnNext = new Button();
		this.btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());

	}

	private void setTitleContent() {
		this.titleLayout = new HorizontalLayout();
		this.titleLayout.setSpacing(true);

		this.toolTitle = new Label();
		this.toolTitle.setContentMode(Label.CONTENT_XHTML);
		this.toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		this.toolTitle.setHeight("26px");
		this.toolTitle.setWidth("430px");

		this.titleLayout.addComponent(this.toolTitle);
		this.titleLayout.addComponent(new HelpButton(HelpModule.MULTI_YEAR_MULTI_SITE_ANALYSIS,
				"Go to Multi-Year Multi-Site Analysis Tutorial"));
	}

	@Override
	public void initializeValues() {
		// method inherited from interface, does nothing
	}

	@Override
	public void addListeners() {

		this.tabSheet.addListener(new TabSheet.SelectedTabChangeListener() {

			private static final long serialVersionUID = -7822326039221887888L;

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if (MetaAnalysisPanel.this.tabSheet.getComponentCount() <= 1) {
					MetaAnalysisPanel.this.linkCloseAllTab.setVisible(false);
				} else {
					MetaAnalysisPanel.this.linkCloseAllTab.setVisible(true);
				}
			}
		});

		this.browseLink.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(ClickEvent event) {

				SelectDatasetDialog dialog =
						new SelectDatasetDialog(event.getComponent().getWindow(), MetaAnalysisPanel.this,
								MetaAnalysisPanel.this.currentProject);
				event.getComponent().getWindow().addWindow(dialog);
			}

		});

		this.linkCloseAllTab.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				MetaAnalysisPanel.this.tabSheet.removeAllComponents();
				MetaAnalysisPanel.this.linkCloseAllTab.setVisible(false);
			}
		});

		this.btnCancel.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				MetaAnalysisPanel.this.tabSheet.removeAllComponents();
				MetaAnalysisPanel.this.selectedEnvironmenTable.removeAllItems();
			}
		});

		this.btnNext.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 3367191648910396919L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				List<MetaEnvironmentModel> metaEnvironments = new ArrayList<MetaEnvironmentModel>();
				Iterator<MetaEnvironmentModel> itr =
						(Iterator<MetaEnvironmentModel>) MetaAnalysisPanel.this.getSelectedEnvironmenTable().getContainerDataSource()
								.getItemIds().iterator();
				while (itr.hasNext()) {
					metaEnvironments.add(itr.next());
				}

				if (!metaEnvironments.isEmpty()) {
					IContentWindow w = (IContentWindow) event.getComponent().getWindow();
					w.showContent(new MetaAnalysisSelectTraitsPanel(MetaAnalysisPanel.this.getCurrentProject(), metaEnvironments,
							MetaAnalysisPanel.this));
				}

			}
		});
	}

	@Override
	public void layoutComponents() {
		this.setMargin(false, true, false, true);
		this.setSpacing(true);
		this.setWidth("100%");

		HorizontalLayout browseLabelLayout = new HorizontalLayout();
		browseLabelLayout.addComponent(this.browseLink);
		browseLabelLayout.addComponent(new Label("for a dataset to work with."));
		browseLabelLayout.setSizeUndefined();
		browseLabelLayout.setMargin(false);

		VerticalLayout selectDataForAnalysisLayout = new VerticalLayout();
		selectDataForAnalysisLayout.addComponent(this.heading);
		selectDataForAnalysisLayout.addComponent(browseLabelLayout);

		this.studyDetailsLayout = new GridLayout(10, 3);
		this.studyDetailsLayout.setMargin(false);
		this.studyDetailsLayout.setSpacing(true);
		this.studyDetailsLayout.setWidth("100%");
		this.studyDetailsLayout.addComponent(this.lblReviewEnvironments, 0, 0, 4, 0);
		this.studyDetailsLayout.addComponent(this.linkCloseAllTab, 8, 0, 9, 0);
		this.studyDetailsLayout.setComponentAlignment(this.linkCloseAllTab, Alignment.TOP_RIGHT);
		this.studyDetailsLayout.addComponent(this.lblBuildNewAnalysisDescription, 0, 1, 9, 1);

		this.selectedDataSetEnvironmentLayout = new VerticalLayout();
		this.selectedDataSetEnvironmentLayout.setMargin(false);
		this.selectedDataSetEnvironmentLayout.setSpacing(true);
		this.selectedDataSetEnvironmentLayout.addComponent(this.lblSelectDatasetsForAnalysis);
		this.selectedDataSetEnvironmentLayout.addComponent(this.lblSelectDatasetsForAnalysisDescription);
		this.selectedDataSetEnvironmentLayout.addComponent(this.getSelectedEnvironmenTable());

		this.buttonArea = this.layoutButtonArea();

		this.addComponent(this.titleLayout);
		this.addComponent(selectDataForAnalysisLayout);
		this.addComponent(this.studyDetailsLayout);
		this.addComponent(this.selectedDataSetEnvironmentLayout);
		this.addComponent(this.buttonArea);
		this.setComponentAlignment(this.buttonArea, Alignment.TOP_CENTER);
	}

	protected Component layoutButtonArea() {

		HorizontalLayout buttonLayout = new HorizontalLayout();

		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);

		buttonLayout.addComponent(this.btnCancel);
		buttonLayout.addComponent(this.btnNext);
		buttonLayout.setComponentAlignment(this.btnCancel, Alignment.TOP_CENTER);
		buttonLayout.setComponentAlignment(this.btnNext, Alignment.TOP_CENTER);
		return buttonLayout;
	}

	public void generateTab(int dataSetId) {
		try {
			if (this.studyDetailsLayout.getComponent(0, 2) == null) {
				this.studyDetailsLayout.addComponent(this.tabSheet, 0, 2, 9, 2);
			}

			TabSheet tabSheet = MetaAnalysisPanel.this.getTabsheet();
			DataSet ds = MetaAnalysisPanel.this.studyDataManager.getDataSet(dataSetId);

			Iterator<Component> itr = tabSheet.getComponentIterator();
			while (itr.hasNext()) {
				EnvironmentTabComponent tab = (EnvironmentTabComponent) itr.next();
				if (tab.getDataSetId() == ds.getId()) {
					tabSheet.setSelectedTab(tab);
					return;
				}
			}

			EnvironmentTabComponent component = new EnvironmentTabComponent(ds);
			tabSheet.addTab(component);
			tabSheet.getTab(component).setClosable(true);
			tabSheet.getTab(component).setCaption(ds.getName());
			tabSheet.setSelectedTab(component);

			if (tabSheet.getComponentCount() > 1) {
				this.linkCloseAllTab.setVisible(true);
			}

		} catch (Exception e) {
			MetaAnalysisPanel.LOG.error("Error generating dataset tab for id = " + dataSetId, e);
		}
	}// end of generateTab

	// SETTERS AND GETTERS
	public Project getCurrentProject() {
		return this.currentProject;
	}

	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}

	public Study getCurrentStudy() {
		return this.currentStudy;
	}

	public void setCurrentStudy(Study currentStudy) {
		this.currentStudy = currentStudy;
	}

	public Integer getCurrentRepresentationId() {
		return this.currentRepresentationId;
	}

	public void setCurrentRepresentationId(Integer currentRepresentationId) {
		this.currentRepresentationId = currentRepresentationId;
	}

	public Integer getCurrentDataSetId() {
		return this.currentDataSetId;
	}

	public void setCurrentDataSetId(Integer currentDataSetId) {
		this.currentDataSetId = currentDataSetId;
	}

	public String getCurrentDatasetName() {
		return this.currentDatasetName;
	}

	public void setCurrentDatasetName(String currentDatasetName) {
		this.currentDatasetName = currentDatasetName;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return this.variatesCheckboxState;
	}

	public void setVariatesCheckboxState(HashMap<String, Boolean> variatesCheckboxState) {
		this.variatesCheckboxState = variatesCheckboxState;
	}

	public TabSheet getTabsheet() {
		return this.tabSheet;
	}

	public void setTabsheet(TabSheet tabsheet) {
		this.tabSheet = tabsheet;
	}

	public Table getSelectedEnvironmenTable() {
		return this.selectedEnvironmenTable;
	}

	public void setSelectedEnvironmenTable(Table selectedEnvironmenTable) {
		this.selectedEnvironmenTable = selectedEnvironmenTable;
	}

	class EnvironmentTabComponent extends VerticalLayout {

		private static final long serialVersionUID = 1L;

		DataSet dataSet;
		String studyName;

		Label lblFactors;
		Label lblFactorDescription;
		Label lblTraits;
		Label lblTraitDescription;

		public EnvironmentTabComponent(DataSet dataSet) {

			this.dataSet = dataSet;

			this.setSpacing(true);
			this.setMargin(true);
			this.setWidth("100%");
			this.setHeight("100%");

			this.initializeComponents();

		}

		public int getDataSetId() {
			return this.dataSet.getId();
		}

		private void initializeComponents() {

			try {
				this.studyName = MetaAnalysisPanel.this.studyDataManager.getStudy(this.dataSet.getStudyId()).getName();
			} catch (MiddlewareException e) {
				MetaAnalysisPanel.LOG.error("Error getting study name", e);
			}

			this.lblFactors =
					new Label(
							"<span class='bms-factors' style='color: #39B54A; font-size: 20px; font-weight: bold;'></span><b>&nbsp;FACTORS</b>",
							Label.CONTENT_XHTML);
			this.lblFactors.setStyleName(Bootstrap.Typography.H3.styleName());
			this.lblFactorDescription = new Label("The factors of the dataset you have selected are shown below for your review.");
			this.lblTraits =
					new Label(
							"<span class='bms-variates' style='color: #B8D433; font-size: 20px; font-weight: bold;'></span><b>&nbsp;TRAITS</b>",
							Label.CONTENT_XHTML);
			this.lblTraits.setStyleName(Bootstrap.Typography.H3.styleName());
			this.lblTraitDescription = new Label("The traits of the dataset you have selected are shown below for your review.");

			Label lblStudyName = new Label("<b>Study Name:</b> " + this.studyName);
			lblStudyName.setContentMode(Label.CONTENT_XHTML);
			Button linkFullStudyDetails = new Button("<span class='glyphicon glyphicon-open' style='right: 6px'></span>Full Study Details");
			Button linkSaveToList = new Button("<span class='glyphicon glyphicon-plus' style='right: 6px'></span>Save To List");
			linkFullStudyDetails.setHtmlContentAllowed(true);
			linkSaveToList.setHtmlContentAllowed(true);

			linkSaveToList.addListener(new Button.ClickListener() {

				private static final long serialVersionUID = -91508239632267095L;

				@Override
				public void buttonClick(ClickEvent event) {
					EnvironmentTabComponent.this.addDataSetToTable();

				}
			});

			linkFullStudyDetails.addListener(new ViewStudyDetailsButtonClickListener(this.dataSet.getStudyId(), this.studyName));

			final HorizontalLayout buttonContainer = new HorizontalLayout();
			buttonContainer.setSpacing(true);
			buttonContainer.addComponent(linkFullStudyDetails);
			buttonContainer.addComponent(linkSaveToList);

			final HorizontalLayout tableContainer = new HorizontalLayout();
			tableContainer.setSpacing(true);
			tableContainer.setSizeFull();

			final VerticalLayout factorsContainer = new VerticalLayout();
			factorsContainer.setSpacing(true);

			final VerticalLayout descContainer1 = new VerticalLayout();
			descContainer1.setSpacing(false);
			descContainer1.setHeight("90px");
			descContainer1.setWidth("100%");
			descContainer1.addComponent(this.lblFactors);
			descContainer1.addComponent(this.lblFactorDescription);
			descContainer1.setExpandRatio(this.lblFactorDescription, 1.0f);

			factorsContainer.addComponent(descContainer1);
			factorsContainer.addComponent(this.initializeFactorsTable());

			final VerticalLayout variatesContainer = new VerticalLayout();
			variatesContainer.setSpacing(true);

			final VerticalLayout descContainer2 = new VerticalLayout();
			descContainer2.setSpacing(false);
			descContainer2.setHeight("90px");
			descContainer2.setWidth("100%");
			descContainer2.addComponent(this.lblTraits);
			descContainer2.addComponent(this.lblTraitDescription);
			descContainer2.setExpandRatio(this.lblTraitDescription, 1.0f);

			variatesContainer.addComponent(descContainer2);
			variatesContainer.addComponent(this.initializeVariatesTable());

			tableContainer.addComponent(factorsContainer);
			tableContainer.addComponent(variatesContainer);
			tableContainer.setExpandRatio(factorsContainer, 1.0F);
			tableContainer.setExpandRatio(variatesContainer, 1.0F);

			this.addComponent(lblStudyName);
			this.addComponent(buttonContainer);
			this.addComponent(tableContainer);

			this.setComponentAlignment(buttonContainer, Alignment.MIDDLE_CENTER);
			this.setExpandRatio(tableContainer, 1.0f);
		}

		protected Table initializeVariatesTable() {

			final Table tblVariates = new Table();
			tblVariates.setImmediate(true);
			tblVariates.setWidth("100%");
			tblVariates.setHeight("270px");
			tblVariates.setColumnExpandRatio("name", 1);
			tblVariates.setColumnExpandRatio("description", 4);
			tblVariates.setColumnExpandRatio("scname", 1);

			tblVariates.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

				private static final long serialVersionUID = 1L;

				@Override
				@SuppressWarnings("unchecked")
				public String generateDescription(Component source, Object itemId, Object propertyId) {
					BeanContainer<Integer, VariateModel> container =
							(BeanContainer<Integer, VariateModel>) tblVariates.getContainerDataSource();
					VariateModel vm = container.getItem(itemId).getBean();

					StringBuilder sb = new StringBuilder();
					sb.append(String.format("<span class=\"gcp-table-header-bold\">%s</span><br>", vm.getName()));
					sb.append(String.format("<span>Property:</span> %s<br>", vm.getTrname()));
					sb.append(String.format("<span>Scale:</span> %s<br>", vm.getScname()));
					sb.append(String.format("<span>Method:</span> %s<br>", vm.getTmname()));
					sb.append(String.format("<span>Data Type:</span> %s", vm.getDatatype()));

					return sb.toString();
				}
			});

			BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
			container.setBeanIdProperty("id");

			for (DMSVariableType variate : this.dataSet.getVariableTypes().getVariates().getVariableTypes()) {
				VariateModel vm = new VariateModel();
				vm.setId(variate.getRank());
				vm.setName(variate.getLocalName());
				vm.setDescription(variate.getLocalDescription());
				vm.setScname(variate.getStandardVariable().getScale().getName());
				vm.setScaleid(variate.getStandardVariable().getScale().getId());
				vm.setTmname(variate.getStandardVariable().getMethod().getName());
				vm.setTmethid(variate.getStandardVariable().getMethod().getId());
				vm.setTrname(variate.getStandardVariable().getProperty().getName());
				vm.setTraitid(variate.getStandardVariable().getProperty().getId());
				vm.setDatatype(variate.getStandardVariable().getDataType().getName());
				container.addBean(vm);
			}

			tblVariates.setContainerDataSource(container);

			String[] columns = new String[] {"name", "description", "scname"};
			String[] columnHeaders = new String[] {"Name", "Description", "Scale"};
			tblVariates.setVisibleColumns(columns);
			tblVariates.setColumnHeaders(columnHeaders);

			return tblVariates;
		}

		protected Table initializeFactorsTable() {

			final Table tblFactors = new Table();
			tblFactors.setImmediate(true);
			tblFactors.setWidth("100%");
			tblFactors.setHeight("270px");
			BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
			container.setBeanIdProperty("id");

			for (DMSVariableType factor : this.dataSet.getVariableTypes().getFactors().getVariableTypes()) {

				if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET || factor.getStandardVariable().getPhenotypicType() == PhenotypicType.STUDY) {
					continue;
				}

				FactorModel fm = new FactorModel();
				fm.setId(factor.getRank());
				fm.setName(factor.getLocalName());
				fm.setDescription(factor.getLocalDescription());
				fm.setScname(factor.getStandardVariable().getScale().getName());
				fm.setScaleid(factor.getStandardVariable().getScale().getId());
				fm.setTmname(factor.getStandardVariable().getMethod().getName());
				fm.setTmethid(factor.getStandardVariable().getMethod().getId());
				fm.setTrname(factor.getStandardVariable().getProperty().getName());
				fm.setTraitid(factor.getStandardVariable().getProperty().getId());
				container.addBean(fm);
			}

			tblFactors.setContainerDataSource(container);

			String[] columns = new String[] {"name", "description"};
			String[] columnHeaders = new String[] {"Name", "Description"};
			tblFactors.setVisibleColumns(columns);
			tblFactors.setColumnHeaders(columnHeaders);

			tblFactors.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

				private static final long serialVersionUID = 1L;

				@Override
				@SuppressWarnings("unchecked")
				public String generateDescription(Component source, Object itemId, Object propertyId) {
					BeanContainer<Integer, FactorModel> container =
							(BeanContainer<Integer, FactorModel>) tblFactors.getContainerDataSource();
					FactorModel fm = container.getItem(itemId).getBean();

					StringBuilder sb = new StringBuilder();
					sb.append(String.format("<span class=\"gcp-table-header-bold\">%s</span><br>", fm.getName()));
					sb.append(String.format("<span>Property:</span> %s<br>", fm.getTrname()));
					sb.append(String.format("<span>Scale:</span> %s<br>", fm.getScname()));
					sb.append(String.format("<span>Method:</span> %s<br>", fm.getTmname()));
					sb.append(String.format("<span>Data Type:</span> %s", fm.getDataType()));

					return sb.toString();
				}
			});

			return tblFactors;
		}

		@SuppressWarnings("unchecked")
		private void addDataSetToTable() {

			BeanItemContainer<MetaEnvironmentModel> container =
					(BeanItemContainer<MetaEnvironmentModel>) MetaAnalysisPanel.this.getSelectedEnvironmenTable().getContainerDataSource();

			String trialInstanceFactorName = null;
			String environmentFactorName = null;

			for (DMSVariableType f : this.dataSet.getVariableTypes().getFactors().getVariableTypes()) {
				if (f.getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					trialInstanceFactorName = f.getLocalName();
				}

				if (f.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
						&& "abbreviation".equalsIgnoreCase(f.getStandardVariable().getScale().getName())) {
					environmentFactorName = f.getLocalName();
				}

				if (isGeolocationProperty(f.getStandardVariable()) && environmentFactorName == null) {
					environmentFactorName = f.getLocalName();
				}

			}

			if (environmentFactorName == null) {
				environmentFactorName = trialInstanceFactorName;
			}

			TrialEnvironments envs = studyDataManager.getTrialEnvironmentsInDataset(this.dataSet.getId());

			List<Variable> variables;
			variables = envs.getVariablesByLocalName(environmentFactorName);

			for (Variable var : variables) {
				TrialEnvironment env = envs.findOnlyOneByLocalName(environmentFactorName, var.getValue());
				if (env == null && environmentFactorName != trialInstanceFactorName) {
					environmentFactorName = trialInstanceFactorName;
				}
				break;
			}

			if (environmentFactorName == trialInstanceFactorName) {
				variables = envs.getVariablesByLocalName(environmentFactorName);
			}

			for (Variable var : variables) {
				if (var != null && !"".equals(var.getValue())) {
					TrialEnvironment env = envs.findOnlyOneByLocalName(environmentFactorName, var.getValue());

					if (env != null) {

						String trialNo = env.getVariables().findByLocalName(trialInstanceFactorName).getValue();
						String envName = env.getVariables().findByLocalName(environmentFactorName).getValue();

						MetaEnvironmentModel bean = new MetaEnvironmentModel();
						bean.setTrial(trialNo);
						bean.setEnvironment(envName);
						bean.setDataSetId(this.dataSet.getId());
						bean.setDataSetName(this.dataSet.getName());
						bean.setStudyId(this.dataSet.getStudyId());
						bean.setStudyName(this.studyName);
						bean.setTrialFactorName(trialInstanceFactorName);
						if (this.dataSet.getDataSetType() == null) {
							bean.setDataSetTypeId(DataSetType.PLOT_DATA.getId());
						} else {
							bean.setDataSetTypeId(this.dataSet.getDataSetType().getId());
						}

						container.addBean(bean);
					}
				}
			}
		}

		private boolean isGeolocationProperty(StandardVariable standardVariable) {
			return standardVariable.getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT && (
					standardVariable.getId() != TermId.TRIAL_INSTANCE_FACTOR.getId()
							|| standardVariable.getId() != TermId.LATITUDE.getId() &&
							standardVariable.getId() != TermId.LONGITUDE.getId() &&
							standardVariable.getId() != TermId.GEODETIC_DATUM.getId() &&
							standardVariable.getId() != TermId.ALTITUDE.getId());
		}

	}// end of EnvironmentTabComponent inner class

}// end of MetaAnalysisPanel

