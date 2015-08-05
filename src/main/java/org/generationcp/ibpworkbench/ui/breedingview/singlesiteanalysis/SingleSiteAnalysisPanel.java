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

package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.SelectStudyDialog;
import org.generationcp.ibpworkbench.ui.breedingview.SelectStudyDialogForBreedingViewUpload;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableType;
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
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Aldrin Batac
 * 
 */
@Configurable
public class SingleSiteAnalysisPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent, IBPWorkbenchLayout {

	private final class TableColumnGenerator implements Table.ColumnGenerator {

		private final Table table;
		private static final long serialVersionUID = 1L;

		private TableColumnGenerator(Table table) {
			this.table = table;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object generateCell(Table source, Object itemId, Object columnId) {

			BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) this.table.getContainerDataSource();
			final VariateModel vm = container.getItem(itemId).getBean();

			final CheckBox checkBox = new CheckBox();
			checkBox.setImmediate(true);
			checkBox.setVisible(true);
			checkBox.addListener(new CheckBoxListener(vm));

			if (vm.getActive()) {
				checkBox.setValue(true);
			} else {
				checkBox.setValue(false);
			}

			return checkBox;

		}
	}

	private final class SelectAllListener implements Property.ValueChangeListener {

		private static final long serialVersionUID = 344514045768824046L;

		@SuppressWarnings("unchecked")
		@Override
		public void valueChange(ValueChangeEvent event) {

			Boolean val = (Boolean) event.getProperty().getValue();
			BeanContainer<Integer, VariateModel> container =
					(BeanContainer<Integer, VariateModel>) SingleSiteAnalysisPanel.this.tblVariates.getContainerDataSource();
			for (Object itemId : container.getItemIds()) {
				container.getItem(itemId).getBean().setActive(val);
			}
			SingleSiteAnalysisPanel.this.tblVariates.refreshRowCache();
			for (Entry<String, Boolean> entry : SingleSiteAnalysisPanel.this.variatesCheckboxState.entrySet()) {
				SingleSiteAnalysisPanel.this.variatesCheckboxState.put(entry.getKey(), val);
			}
			if (val) {
				SingleSiteAnalysisPanel.this.numOfSelectedVariates = SingleSiteAnalysisPanel.this.variatesCheckboxState.size();
			} else {
				SingleSiteAnalysisPanel.this.numOfSelectedVariates = 0;
			}

			if (SingleSiteAnalysisPanel.this.numOfSelectedVariates == 0) {
				SingleSiteAnalysisPanel.this.toggleNextButton(false);
			} else {
				SingleSiteAnalysisPanel.this.toggleNextButton(val);
			}

		}
	}

	private final class CheckBoxListener implements Property.ValueChangeListener {

		private final VariateModel vm;
		private static final long serialVersionUID = 1L;

		private CheckBoxListener(VariateModel vm) {
			this.vm = vm;
		}

		@Override
		public void valueChange(final ValueChangeEvent event) {
			Boolean val = (Boolean) event.getProperty().getValue();
			SingleSiteAnalysisPanel.this.variatesCheckboxState.put(this.vm.getName(), val);
			this.vm.setActive(val);

			if (!val) {
				SingleSiteAnalysisPanel.this.chkVariatesSelectAll.removeListener(SingleSiteAnalysisPanel.this.selectAllListener);
				SingleSiteAnalysisPanel.this.chkVariatesSelectAll.setValue(val);
				SingleSiteAnalysisPanel.this.chkVariatesSelectAll.addListener(SingleSiteAnalysisPanel.this.selectAllListener);
				SingleSiteAnalysisPanel.this.numOfSelectedVariates--;
				if (SingleSiteAnalysisPanel.this.numOfSelectedVariates == 0) {
					SingleSiteAnalysisPanel.this.toggleNextButton(false);
				}
			} else {
				if (SingleSiteAnalysisPanel.this.numOfSelectedVariates < SingleSiteAnalysisPanel.this.variatesCheckboxState.size()) {// add
																																		// this
																																		// check
																																		// to
																																		// ensure
																																		// that
																																		// the
																																		// number
																																		// of
																																		// selected
																																		// does
																																		// not
																																		// exceed
																																		// the
																																		// total
																																		// number
																																		// of
																																		// variates
					SingleSiteAnalysisPanel.this.numOfSelectedVariates++;
				}
				SingleSiteAnalysisPanel.this.toggleNextButton(true);
			}

		}
	}

	private static final long serialVersionUID = 1L;
	private Button browseLink;
	private Button uploadLink;

	private HorizontalLayout titleLayout;
	private Label toolTitle;
	private HeaderLabelLayout heading;
	private Label lblFactors;
	private Label lblVariates;

	private Table tblFactors;
	private Table tblVariates;
	private Property.ValueChangeListener selectAllListener;
	private CheckBox chkVariatesSelectAll;

	private VerticalLayout lblFactorContainer;
	private VerticalLayout lblVariateContainer;
	private VerticalLayout tblFactorContainer;
	private VerticalLayout tblVariateContainer;

	private VerticalLayout rootLayout;

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
	private int numOfSelectedVariates = 0;
	private List<FactorModel> factorList;
	private List<VariateModel> variateList;

	private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;

	private static final Logger LOG = LoggerFactory.getLogger(SingleSiteAnalysisPanel.class);

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Value("${workbench.is.server.app}")
	private String isServerApp;

	private StudyDataManager studyDataManager;

	private ManagerFactory managerFactory;

	private static final String NAMED_COLUMN_1 = "name";
	private static final String NAMED_COLUMN_2 = "description";
	private static final String NAMED_COLUMN_3 = "scname";

	private static final String CAMEL_CASE_NAMED_COLUMN_1 = "Name";
	private static final String CAMEL_CASE_NAMED_COLUMN_2 = "Description";
	private static final String CAMEL_CASE_NAMED_COLUMN_3 = "Scale";

	public SingleSiteAnalysisPanel(Project currentProject, Database database) {
		this.currentProject = currentProject;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();

		this.updateLabels();
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
		this.messageSource.setValue(this.toolTitle, Message.TITLE_SSA);
	}

	@Override
	public void instantiateComponents() {
		this.managerFactory = this.managerFactoryProvider.getManagerFactoryForProject(this.currentProject);

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

		this.uploadLink = new Button();
		this.uploadLink.setImmediate(true);
		this.uploadLink.setStyleName("link");
		this.uploadLink.setCaption("Upload");
		this.uploadLink.setWidth("48px");

		this.setVariatesCheckboxState(new HashMap<String, Boolean>());

		this.tblFactors = this.initializeFactorsTable();
		this.tblVariates = this.initializeVariatesTable();
		this.buttonArea = this.layoutButtonArea();

		this.lblFactors =
				new Label(
						"<span class='bms-factors' style='color: #39B54A; font-size: 22px; font-weight: bold;'></span><b>&nbsp;FACTORS</b>",
						Label.CONTENT_XHTML);
		this.lblFactors.setStyleName(Bootstrap.Typography.H4.styleName());
		this.lblFactors.setWidth("100%");

		this.lblVariates =
				new Label(
						"<span class='bms-variates' style='color: #B8D433; font-size: 22px; font-weight: bold;'></span><b>&nbsp;TRAITS</b>",
						Label.CONTENT_XHTML);
		this.lblVariates.setWidth("100%");
		this.lblVariates.setStyleName(Bootstrap.Typography.H4.styleName());

		this.chkVariatesSelectAll = new CheckBox();
		this.chkVariatesSelectAll.setImmediate(true);
		this.chkVariatesSelectAll.setCaption("Select All");

	}

	@Override
	public void initializeValues() {
		// no values to initialize
	}

	@Override
	public void addListeners() {
		this.selectAllListener = new SelectAllListener();

		this.chkVariatesSelectAll.addListener(this.selectAllListener);

		this.browseLink.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (event.getComponent() != null && event.getComponent().getWindow() != null) {
					SelectStudyDialog dialog =
							new SelectStudyDialog(event.getComponent().getWindow(), SingleSiteAnalysisPanel.this,
									(StudyDataManagerImpl) SingleSiteAnalysisPanel.this.getStudyDataManager(),
									SingleSiteAnalysisPanel.this.currentProject);
					event.getComponent().getWindow().addWindow(dialog);
				} else if (event.getComponent() == null) {
					SingleSiteAnalysisPanel.LOG.error("event component is null");
				} else if (event.getComponent().getWindow() == null) {
					SingleSiteAnalysisPanel.LOG.error("event component window is null");
				}
			}

		});

		this.uploadLink.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (event.getComponent() != null && event.getComponent().getWindow() != null) {
					SelectStudyDialogForBreedingViewUpload dialog =
							new SelectStudyDialogForBreedingViewUpload(event.getComponent().getWindow(), SingleSiteAnalysisPanel.this,
									(StudyDataManagerImpl) SingleSiteAnalysisPanel.this.getStudyDataManager(),
									SingleSiteAnalysisPanel.this.currentProject);
					event.getComponent().getWindow().addWindow(dialog);
				} else if (event.getComponent() == null) {
					SingleSiteAnalysisPanel.LOG.error("event component is null");
				} else if (event.getComponent().getWindow() == null) {
					SingleSiteAnalysisPanel.LOG.error("event component window is null");
				}
			}

		});

		this.btnCancel.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				SingleSiteAnalysisPanel.this.refreshFactorsAndVariatesTable();
				SingleSiteAnalysisPanel.this.toggleNextButton(false);
			}
		});
		this.openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(this);
		this.btnNext.addListener(this.openSelectDatasetForExportAction);
	}

	@Override
	public void layoutComponents() {
		this.setWidth("100%");

		HorizontalLayout browseLabelLayout = new HorizontalLayout();
		browseLabelLayout.addComponent(this.browseLink);
		Label workWith = null;

		if (Boolean.parseBoolean(this.isServerApp)) {
			workWith = new Label("for a study to work with ");
		} else {
			workWith = new Label("for a study to work with.");
		}

		workWith.setWidth("150px");
		browseLabelLayout.addComponent(workWith);
		Label orLabel = new Label("or");
		orLabel.setWidth("20px");

		if (Boolean.parseBoolean(this.isServerApp)) {
			browseLabelLayout.addComponent(orLabel);
			browseLabelLayout.addComponent(this.uploadLink);
			browseLabelLayout.addComponent(new Label(" Breeding View output files to BMS."));
		}

		browseLabelLayout.setSizeUndefined();

		VerticalLayout selectDataForAnalysisLayout = new VerticalLayout();
		selectDataForAnalysisLayout.addComponent(this.heading);
		selectDataForAnalysisLayout.addComponent(browseLabelLayout);

		this.lblFactorContainer = new VerticalLayout();
		this.lblVariateContainer = new VerticalLayout();
		this.tblFactorContainer = new VerticalLayout();
		this.tblVariateContainer = new VerticalLayout();
		this.tblVariateContainer.setSpacing(true);

		this.lblFactorContainer.addComponent(this.lblFactors);
		this.lblVariateContainer.addComponent(this.lblVariates);
		this.tblFactorContainer.addComponent(this.tblFactors);
		this.tblVariateContainer.addComponent(this.tblVariates);
		this.tblVariateContainer.addComponent(this.chkVariatesSelectAll);

		this.lblFactorContainer.setMargin(true, false, false, false);
		this.lblVariateContainer.setMargin(true, true, false, true);
		this.tblFactorContainer.setMargin(false, false, false, false);
		this.tblVariateContainer.setMargin(false, true, false, true);

		this.studyDetailsLayout = new GridLayout(10, 3);
		this.studyDetailsLayout.setWidth("100%");
		this.studyDetailsLayout.addComponent(this.lblFactorContainer, 0, 0, 4, 0);
		this.studyDetailsLayout.addComponent(this.lblVariateContainer, 5, 0, 9, 0);
		this.studyDetailsLayout.addComponent(this.tblFactorContainer, 0, 1, 4, 1);
		this.studyDetailsLayout.addComponent(this.tblVariateContainer, 5, 1, 9, 1);

		this.rootLayout = new VerticalLayout();
		this.rootLayout.setWidth("100%");
		this.rootLayout.setSpacing(true);
		this.rootLayout.setMargin(false, false, false, true);
		this.rootLayout.addComponent(this.titleLayout);
		this.rootLayout.addComponent(selectDataForAnalysisLayout);
		this.rootLayout.addComponent(this.studyDetailsLayout);
		this.rootLayout.addComponent(this.buttonArea);
		this.rootLayout.setComponentAlignment(this.buttonArea, Alignment.TOP_CENTER);

		this.addComponent(this.rootLayout);
	}

	private void setTitleContent() {
		this.titleLayout = new HorizontalLayout();
		this.titleLayout.setSpacing(true);

		this.toolTitle = new Label();
		this.toolTitle.setContentMode(Label.CONTENT_XHTML);
		this.toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		this.toolTitle.setHeight("26px");
		this.toolTitle.setWidth("278px");

		this.titleLayout.addComponent(this.toolTitle);
		this.titleLayout.addComponent(new HelpButton(HelpModule.SINGLE_SITE_ANALYSIS, "Go to Single Site Analysis Tutorial"));
	}

	protected Component layoutButtonArea() {

		HorizontalLayout buttonLayout = new HorizontalLayout();

		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);

		this.btnCancel = new Button();
		this.btnNext = new Button();
		this.btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.btnNext.setEnabled(false);// default

		buttonLayout.addComponent(this.btnCancel);
		buttonLayout.addComponent(this.btnNext);
		buttonLayout.setComponentAlignment(this.btnCancel, Alignment.TOP_CENTER);
		buttonLayout.setComponentAlignment(this.btnNext, Alignment.TOP_CENTER);
		return buttonLayout;
	}

	protected Table initializeFactorsTable() {

		final Table table = new Table();
		table.setImmediate(true);
		table.setWidth("100%");
		table.setHeight("400px");

		BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
		container.setBeanIdProperty("id");
		table.setContainerDataSource(container);

		String[] columns = new String[] {SingleSiteAnalysisPanel.NAMED_COLUMN_1, SingleSiteAnalysisPanel.NAMED_COLUMN_2};
		String[] columnHeaders =
				new String[] {SingleSiteAnalysisPanel.CAMEL_CASE_NAMED_COLUMN_1, SingleSiteAnalysisPanel.CAMEL_CASE_NAMED_COLUMN_2};
		table.setVisibleColumns(columns);
		table.setColumnHeaders(columnHeaders);

		table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;
			private final String description = "<span class=\"gcp-table-header-bold\">%s</span><br>"
					+ "<span>Property:</span> %s<br><span>Scale:</span> %s<br>" + "<span>Method:</span> %s<br><span>Data Type:</span> %s";

			@Override
			@SuppressWarnings("unchecked")
			public String generateDescription(Component source, Object itemId, Object propertyId) {
				BeanContainer<Integer, FactorModel> container = (BeanContainer<Integer, FactorModel>) table.getContainerDataSource();
				FactorModel fm = container.getItem(itemId).getBean();
				return String.format(this.description, fm.getName(), fm.getTrname(), fm.getScname(), fm.getTmname(), fm.getDataType());
			}
		});

		return table;
	}

	protected Table initializeVariatesTable() {

		this.variatesCheckboxState.clear();

		final Table table = new Table();
		table.setImmediate(true);
		table.setWidth("100%");
		table.setHeight("400px");
		table.setColumnExpandRatio("", 0.5f);
		table.setColumnExpandRatio(SingleSiteAnalysisPanel.NAMED_COLUMN_1, 1);
		table.setColumnExpandRatio(SingleSiteAnalysisPanel.NAMED_COLUMN_2, 4);
		table.setColumnExpandRatio(SingleSiteAnalysisPanel.NAMED_COLUMN_3, 1);
		table.addGeneratedColumn("", new TableColumnGenerator(table));

		table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;
			private final String description = "<span class=\"gcp-table-header-bold\">%s</span><br>"
					+ "<span>Property:</span> %s<br><span>Scale:</span> %s<br>" + "<span>Method:</span> %s<br><span>Data Type:</span> %s";

			@Override
			@SuppressWarnings("unchecked")
			public String generateDescription(Component source, Object itemId, Object propertyId) {
				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) table.getContainerDataSource();
				VariateModel vm = container.getItem(itemId).getBean();
				return String.format(this.description, vm.getName(), vm.getTrname(), vm.getScname(), vm.getTmname(), vm.getDatatype());
			}
		});

		BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
		container.setBeanIdProperty("id");
		table.setContainerDataSource(container);

		String[] columns =
				new String[] {"", SingleSiteAnalysisPanel.NAMED_COLUMN_1, SingleSiteAnalysisPanel.NAMED_COLUMN_2,
						SingleSiteAnalysisPanel.NAMED_COLUMN_3};
		String[] columnHeaders =
				new String[] {"<span class='glyphicon glyphicon-ok'></span>", SingleSiteAnalysisPanel.CAMEL_CASE_NAMED_COLUMN_1,
						SingleSiteAnalysisPanel.CAMEL_CASE_NAMED_COLUMN_2, SingleSiteAnalysisPanel.CAMEL_CASE_NAMED_COLUMN_3};
		table.setVisibleColumns(columns);
		table.setColumnHeaders(columnHeaders);
		table.setColumnWidth("", 18);
		return table;
	}

	private Table[] refreshFactorsAndVariatesTable() {
		Table[] toreturn = new Table[2];

		this.tblFactorContainer.removeAllComponents();
		this.tblVariateContainer.removeAllComponents();
		this.tblFactors = this.initializeFactorsTable();
		this.tblVariates = this.initializeVariatesTable();
		this.tblFactorContainer.addComponent(this.tblFactors);
		this.tblVariateContainer.addComponent(this.tblVariates);
		this.tblVariateContainer.addComponent(this.chkVariatesSelectAll);
		toreturn[0] = this.tblFactors;
		toreturn[1] = this.tblVariates;
		return toreturn;
	}

	public StudyDataManager getStudyDataManager() {
		if (this.studyDataManager == null) {
			this.studyDataManager = this.getManagerFactory().getNewStudyDataManager();
		}
		return this.studyDataManager;
	}

	public ManagerFactory getManagerFactory() {
		return this.managerFactory;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return this.variatesCheckboxState;
	}

	public void setVariatesCheckboxState(Map<String, Boolean> variatesCheckboxState) {
		this.variatesCheckboxState = variatesCheckboxState;
	}

	public void toggleNextButton(boolean enabled) {
		this.btnNext.setEnabled(enabled);
	}

	public int getNumOfSelectedVariates() {
		return this.numOfSelectedVariates;
	}

	public void setNumOfSelectedVariates(int numOfSelectedVariates) {
		this.numOfSelectedVariates = numOfSelectedVariates;
	}

	public void showDatasetVariatesDetails(int dataSetId) {

		try {

			DataSet ds = this.studyDataManager.getDataSet(dataSetId);

			if (this.getCurrentStudy() == null) {
				Study study = this.studyDataManager.getStudy(ds.getStudyId());
				this.setCurrentStudy(study);
			} else if (this.getCurrentStudy().getId() != ds.getStudyId()) {
				Study study = this.studyDataManager.getStudy(ds.getStudyId());
				this.setCurrentStudy(study);
			}

			this.factorList = new ArrayList<FactorModel>();
			this.variateList = new ArrayList<VariateModel>();

			for (VariableType factor : ds.getVariableTypes().getFactors().getVariableTypes()) {

				if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET) {
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

				this.factorList.add(fm);
			}

			for (VariableType variate : ds.getVariableTypes().getVariates().getVariableTypes()) {
				VariateModel vm = this.transformVariableTypeToVariateModel(variate);
				this.variateList.add(vm);
			}

			this.setCurrentDatasetName(ds.getName());
			this.setCurrentDataSetId(ds.getId());

			this.updateFactorsTable(this.factorList);
			this.updateVariatesTable(this.variateList);

		} catch (MiddlewareQueryException e) {
			SingleSiteAnalysisPanel.LOG.error(e.getMessage(), e);
			this.showDatabaseError(this.getWindow());
		}

		this.getManagerFactory().close();
	}

	public VariateModel transformVariableTypeToVariateModel(VariableType variate) {
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

		if (variate.getStandardVariable().isNumeric()) {
			vm.setActive(true);
			if (variate.getStandardVariable().isNumericCategoricalVariate()) {
				vm.setNumericCategoricalVariate(true);
			}
		} else {
			vm.setNonNumeric(true);
		}
		return vm;
	}

	private void updateFactorsTable(List<FactorModel> factorList) {
		Object[] oldColumns = this.tblFactors.getVisibleColumns();
		String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);

		BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
		container.setBeanIdProperty("id");
		this.tblFactors.setContainerDataSource(container);

		for (FactorModel f : factorList) {
			container.addBean(f);
		}

		this.tblFactors.setContainerDataSource(container);

		this.tblFactors.setVisibleColumns(columns);
	}

	private void updateVariatesTable(List<VariateModel> variateList) {
		// reset
		this.getVariatesCheckboxState().clear();
		this.setNumOfSelectedVariates(0);
		this.toggleNextButton(false);

		// load data
		BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
		container.setBeanIdProperty("id");

		for (VariateModel v : variateList) {
			container.addBean(v);
			this.getVariatesCheckboxState().put(v.getName(), v.getActive());
		}
		this.tblVariates.setContainerDataSource(container);
		this.tblVariates.setVisibleColumns(new String[] {"", SingleSiteAnalysisPanel.NAMED_COLUMN_1,
				SingleSiteAnalysisPanel.NAMED_COLUMN_2, SingleSiteAnalysisPanel.NAMED_COLUMN_3});
		this.tblVariates.setColumnHeaders(new String[] {"<span class='glyphicon glyphicon-ok'></span>",
				SingleSiteAnalysisPanel.CAMEL_CASE_NAMED_COLUMN_1, SingleSiteAnalysisPanel.CAMEL_CASE_NAMED_COLUMN_2,
				SingleSiteAnalysisPanel.CAMEL_CASE_NAMED_COLUMN_3});
	}

	private void showDatabaseError(Window window) {
		MessageNotifier.showError(window, this.messageSource.getMessage(Message.DATABASE_ERROR),
				"<br />" + this.messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
	}

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

	public List<FactorModel> getFactorList() {
		return this.factorList;
	}

	public List<VariateModel> getVariateList() {
		return this.variateList;
	}

	public void setFactorList(List<FactorModel> factorList) {
		this.factorList = factorList;
	}

	public void setVariateList(List<VariateModel> variateList) {
		this.variateList = variateList;
	}

	public ManagerFactoryProvider getManagerFactoryProvider() {
		return this.managerFactoryProvider;
	}

	public void setManagerFactoryProvider(ManagerFactoryProvider managerFactoryProvider) {
		this.managerFactoryProvider = managerFactoryProvider;
	}

	public void setStudyDataManager(StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	public void setManagerFactory(ManagerFactory managerFactory) {
		this.managerFactory = managerFactory;
	}

}
