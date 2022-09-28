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

package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Aldrin Batac
 *
 */
@Configurable
public class MultiSiteAnalysisSelectPanel extends VerticalLayout
		implements InitializingBean, InternationalizableComponent, IBPWorkbenchLayout {

	private static final String TESTEDIN = "testedin";
	private static final String DESCRIPTION_COLUMN = "description";
	private static final String DESCRIPTION = "Description";
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(MultiSiteAnalysisSelectPanel.class);

	private Table factors;
	private Table variates;
	private Property.ValueChangeListener selectAllListener;
	private CheckBox chkVariatesSelectAll;

	private Boolean refreshing = false;

	private Label lblEnvironmentFactorHeader;
	private Label lblEnvironmentFactorDescription;
	private Label lblGenotypesFactorDescription;
	private Label lblEnvironmentGroupsHeader;
	private Label lblEnvironmentGroupsDescription;
	private Label lblEnvironmentGroupsSpecify;
	private Label lblReviewSelectedDataset;
	private Label lblFactorTableHeader;
	private Label lblFactorTableDescription;
	private Label lblVariateTableHeader;
	private Label lblVariateTableDescription;

	private Label lblStudyTreeDetailTitle;

	private VerticalLayout generalLayout;

	private HorizontalLayout specifyEnvironmentFactorLayout;
	private HorizontalLayout specifyGenotypesFactorLayout;
	private HorizontalLayout specifyEnvironmentGroupsLayout;

	private VerticalLayout datasetVariablesDetailLayout;

	private Project currentProject;

	private Study currentStudy;

	private Integer currentRepresentationId;

	private Integer currentDataSetId;

	private String currentDatasetName;

	private Button btnCancel;
	private Button btnNext;
	private Component buttonArea;
	private Select selectSpecifyEnvironment;
	private Select selectSpecifyGenotypes;
	private Select selectSpecifyEnvironmentGroups;

	private Map<String, Boolean> variatesCheckboxState;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private MultiSiteAnalysisPanel gxeAnalysisComponentPanel;

	@Autowired
	private final StudyDataManager studyDataManager;

	private final List<String> environmentNames = new ArrayList<String>();
	private TrialEnvironments trialEnvironments = null;

	public MultiSiteAnalysisSelectPanel(final StudyDataManager studyDataManager, final Project currentProject, final Study study,
			final MultiSiteAnalysisPanel gxeAnalysisComponentPanel) {
		this.studyDataManager = studyDataManager;
		this.currentProject = currentProject;
		this.currentStudy = study;
		this.setGxeAnalysisComponentPanel(gxeAnalysisComponentPanel);

		this.setWidth("100%");

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
		this.messageSource.setValue(this.lblEnvironmentFactorHeader, Message.GXE_ENVIRONMENT_FACTOR_HEADER);
		this.messageSource.setValue(this.lblEnvironmentFactorDescription, Message.GXE_ENVIRONMENT_FACTOR_DESCRIPTION);
		this.messageSource.setValue(this.lblGenotypesFactorDescription, Message.GXE_GENOTYPES_FACTOR_DESCRIPTION);
		this.messageSource.setValue(this.lblEnvironmentGroupsHeader, Message.GXE_ENVIRONMENT_GROUPS_HEADER);
		this.messageSource.setValue(this.lblEnvironmentGroupsDescription, Message.GXE_ENVIRONMENT_GROUPS_DESCRIPTION);
		this.messageSource.setValue(this.lblEnvironmentGroupsSpecify, Message.GXE_ENVIRONMENT_GROUPS_SPECIFY);
		this.messageSource.setValue(this.lblReviewSelectedDataset, Message.GXE_REVIEW_SELECTED_DATASET);
		this.messageSource.setValue(this.lblFactorTableDescription, Message.GXE_FACTOR_TABLE_DESCRIPTION);
		this.messageSource.setValue(this.lblVariateTableDescription, Message.GXE_TRAIT_TABLE_DESCRIPTION);
	}

	@Override
	public void instantiateComponents() {

		this.setVariatesCheckboxState(new HashMap<String, Boolean>());

		this.lblEnvironmentFactorHeader = new Label();
		this.lblEnvironmentFactorHeader.setDebugId("lblEnvironmentFactorHeader");
		this.lblEnvironmentFactorHeader.setStyleName(Bootstrap.Typography.H2.styleName());

		this.lblEnvironmentFactorDescription = new Label();
		this.lblEnvironmentFactorDescription.setDebugId("lblEnvironmentFactorDescription");
		this.lblGenotypesFactorDescription = new Label();
		this.lblGenotypesFactorDescription.setDebugId("lblGenotypesFactorDescription");

		this.lblEnvironmentGroupsHeader = new Label();
		this.lblEnvironmentGroupsHeader.setDebugId("lblEnvironmentGroupsHeader");
		this.lblEnvironmentGroupsHeader.setStyleName(Bootstrap.Typography.H2.styleName());

		this.lblEnvironmentGroupsDescription = new Label();
		this.lblEnvironmentGroupsDescription.setDebugId("lblEnvironmentGroupsDescription");

		this.lblEnvironmentGroupsSpecify = new Label();
		this.lblEnvironmentGroupsSpecify.setDebugId("lblEnvironmentGroupsSpecify");

		this.lblReviewSelectedDataset = new Label();
		this.lblReviewSelectedDataset.setDebugId("lblReviewSelectedDataset");
		this.lblReviewSelectedDataset.setStyleName(Bootstrap.Typography.H2.styleName());

		this.lblFactorTableHeader = new Label("<span class='bms-factors' style='position: relative; top:-2px; color: #39B54A; "
				+ "font-size: 22px; font-weight: bold;'></span><b>&nbsp;FACTORS</b>", Label.CONTENT_XHTML);
		this.lblFactorTableHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.lblFactorTableDescription = new Label();
		this.lblFactorTableDescription.setDebugId("lblFactorTableDescription");

		this.lblVariateTableHeader = new Label(
				"<span class='bms-variates' style='position: relative; top:-2px; color: #B8D433; font-size: 22px; font-weight: bold;'></span><b>&nbsp;TRAITS</b>",
				Label.CONTENT_XHTML);
		this.lblVariateTableHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.lblVariateTableDescription = new Label();
		this.lblVariateTableDescription.setDebugId("lblVariateTableDescription");

		this.lblStudyTreeDetailTitle = new Label();
		this.lblStudyTreeDetailTitle.setDebugId("lblStudyTreeDetailTitle");
		this.lblStudyTreeDetailTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		this.factors = this.initializeFactorsTable();
		this.factors.setImmediate(true);
		this.initializeVariatesTable();
		this.variates.setImmediate(true);

		this.chkVariatesSelectAll = new CheckBox();
		this.chkVariatesSelectAll.setDebugId("chkVariatesSelectAll");
		this.chkVariatesSelectAll.setImmediate(true);
		this.chkVariatesSelectAll.setCaption("Select All");

		this.selectSpecifyEnvironment = new Select();
		this.selectSpecifyEnvironment.setDebugId("selectSpecifyEnvironment");
		this.selectSpecifyEnvironment.setSizeFull();
		this.selectSpecifyEnvironment.setImmediate(true);

		this.selectSpecifyGenotypes = new Select();
		this.selectSpecifyGenotypes.setDebugId("selectSpecifyGenotypes");
		this.selectSpecifyGenotypes.setSizeFull();

		this.selectSpecifyEnvironmentGroups = new Select();
		this.selectSpecifyEnvironmentGroups.setDebugId("selectSpecifyEnvironmentGroups");
		this.selectSpecifyEnvironmentGroups.setSizeFull();

		this.populateFactorsVariatesByDataSetId(this.currentStudy, this.factors, this.variates);

		// initialize buttons
		this.btnCancel = new Button();
		this.btnCancel.setDebugId("btnCancel");
		this.btnNext = new Button();
		this.btnNext.setDebugId("btnNext");
		this.btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
	}

	@Override
	public void initializeValues() {

		for (final Iterator<?> i = this.selectSpecifyEnvironment.getItemIds().iterator(); i.hasNext();) {
			this.selectSpecifyEnvironment.select(i.next());
			break;
		}

		for (final Iterator<?> i = this.selectSpecifyGenotypes.getItemIds().iterator(); i.hasNext();) {
			this.selectSpecifyGenotypes.select(i.next());
			break;
		}

		final Object item = "None";
		this.selectSpecifyEnvironmentGroups.addItem(item);
		this.selectSpecifyEnvironmentGroups.select(item);

		this.environmentNames.clear();
		try {
			this.trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(this.getCurrentDataSetId());
			for (final Variable var : this.trialEnvironments.getVariablesByLocalName(this.selectSpecifyEnvironment.getValue().toString())) {
				if (var.getValue() != null && !"".equals(var.getValue())) {
					this.environmentNames.add(var.getValue());
				}
			}
		} catch (final MiddlewareException e) {
			MultiSiteAnalysisSelectPanel.LOG.error("Error getting trial environments" + e);
		}
	}

	@Override
	public void addListeners() {
		this.selectSpecifyEnvironment.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(final ValueChangeEvent event) {

				try {
					MultiSiteAnalysisSelectPanel.this.factors.removeAllItems();
					MultiSiteAnalysisSelectPanel.this.variates.removeAllItems();
					MultiSiteAnalysisSelectPanel.this.environmentNames.clear();

					if (MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironment.getValue() == null) {
						return;
					}

				} catch (final Exception e) {
					MultiSiteAnalysisSelectPanel.LOG.error("Error changing values for environment factor", e);
				}

				try {
					MultiSiteAnalysisSelectPanel.this.trialEnvironments = MultiSiteAnalysisSelectPanel.this.studyDataManager
							.getTrialEnvironmentsInDataset(MultiSiteAnalysisSelectPanel.this.getCurrentDataSetId());
					for (final Variable var : MultiSiteAnalysisSelectPanel.this.trialEnvironments
							.getVariablesByLocalName(MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironment.getValue().toString())) {
						if (var.getValue() != null && !"".equals(var.getValue())) {
							MultiSiteAnalysisSelectPanel.this.environmentNames.add(var.getValue());
						}
					}
				} catch (final MiddlewareException e) {
					MultiSiteAnalysisSelectPanel.LOG.error("Error getting trial environments", e);
				}

				MultiSiteAnalysisSelectPanel.this.populateFactorsVariatesByDataSetId(MultiSiteAnalysisSelectPanel.this.currentStudy,
						MultiSiteAnalysisSelectPanel.this.factors, MultiSiteAnalysisSelectPanel.this.variates);

			}
		});

		this.selectAllListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = -6750267436054378894L;

			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(final ValueChangeEvent event) {
				final Boolean val = (Boolean) event.getProperty().getValue();
				final BeanContainer<Integer, VariateModel> container =
						(BeanContainer<Integer, VariateModel>) MultiSiteAnalysisSelectPanel.this.variates.getContainerDataSource();
				for (final Object itemId : container.getItemIds()) {
					container.getItem(itemId).getBean().setActive(val);
				}
				for (final Entry<String, Boolean> entry : MultiSiteAnalysisSelectPanel.this.variatesCheckboxState.entrySet()) {
					MultiSiteAnalysisSelectPanel.this.variatesCheckboxState.put(entry.getKey(), val);
				}

				MultiSiteAnalysisSelectPanel.this.refreshing = true;
				MultiSiteAnalysisSelectPanel.this.variates.refreshRowCache();
				MultiSiteAnalysisSelectPanel.this.refreshing = false;
			}

		};
		this.chkVariatesSelectAll.addListener(this.selectAllListener);

		this.btnCancel.setImmediate(true);
		this.btnCancel.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4719456133687409089L;

			@Override
			public void buttonClick(final ClickEvent event) {
				MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironment.select(null);
				MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironment
						.select(MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironment.getItemIds().iterator().next());
				MultiSiteAnalysisSelectPanel.this.selectSpecifyGenotypes
						.select(MultiSiteAnalysisSelectPanel.this.selectSpecifyGenotypes.getItemIds().iterator().next());
				MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironmentGroups.select("Analyze All");
				MultiSiteAnalysisSelectPanel.this.chkVariatesSelectAll.setValue(false);

			}
		});
		this.btnNext.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8377610125826448065L;

			@Override
			public void buttonClick(final ClickEvent event) {
				if (MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironment.getValue() != null
						&& MultiSiteAnalysisSelectPanel.this.selectSpecifyGenotypes.getValue() != null) {
					MultiSiteAnalysisSelectPanel.this.getGxeAnalysisComponentPanel().generateTabContent(
							MultiSiteAnalysisSelectPanel.this.currentStudy,
							MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironment.getValue().toString(),
							MultiSiteAnalysisSelectPanel.this.selectSpecifyGenotypes.getValue().toString(),
							MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironmentGroups.getValue().toString(),
							MultiSiteAnalysisSelectPanel.this.variatesCheckboxState, MultiSiteAnalysisSelectPanel.this);
				}

			}
		});
	}

	@Override
	public void layoutComponents() {

		// Sub-Layouts
		this.specifyEnvironmentFactorLayout = new HorizontalLayout();
		this.specifyEnvironmentFactorLayout.setDebugId("specifyEnvironmentFactorLayout");
		this.specifyEnvironmentFactorLayout.setSpacing(true);
		this.specifyGenotypesFactorLayout = new HorizontalLayout();
		this.specifyGenotypesFactorLayout.setDebugId("specifyGenotypesFactorLayout");
		this.specifyGenotypesFactorLayout.setSpacing(true);
		this.specifyEnvironmentGroupsLayout = new HorizontalLayout();
		this.specifyEnvironmentGroupsLayout.setDebugId("specifyEnvironmentGroupsLayout");
		this.specifyEnvironmentGroupsLayout.setSpacing(true);
		this.datasetVariablesDetailLayout = new VerticalLayout();
		this.datasetVariablesDetailLayout.setDebugId("datasetVariablesDetailLayout");

		this.specifyEnvironmentFactorLayout.addComponent(this.lblEnvironmentFactorDescription);
		this.specifyEnvironmentFactorLayout.addComponent(this.selectSpecifyEnvironment);

		this.specifyGenotypesFactorLayout.addComponent(this.lblGenotypesFactorDescription);
		this.specifyGenotypesFactorLayout.addComponent(this.selectSpecifyGenotypes);

		this.specifyEnvironmentGroupsLayout.addComponent(this.lblEnvironmentGroupsSpecify);
		this.specifyEnvironmentGroupsLayout.addComponent(this.selectSpecifyEnvironmentGroups);

		this.buttonArea = this.layoutButtonArea();

		// Main Layout
		this.generalLayout = new VerticalLayout();
		this.generalLayout.setDebugId("generalLayout");
		this.generalLayout.setSpacing(true);
		this.generalLayout.setMargin(true);

		this.generalLayout.addComponent(this.lblEnvironmentFactorHeader);
		this.generalLayout.addComponent(this.specifyEnvironmentFactorLayout);
		this.generalLayout.addComponent(this.specifyGenotypesFactorLayout);

		this.generalLayout.addComponent(this.lblEnvironmentGroupsDescription);
		this.generalLayout.addComponent(this.specifyEnvironmentGroupsLayout);

		this.generalLayout.addComponent(this.lblReviewSelectedDataset);
		this.generalLayout.addComponent(this.lblFactorTableHeader);
		this.generalLayout.addComponent(this.lblFactorTableDescription);
		this.generalLayout.addComponent(this.factors);
		this.generalLayout.addComponent(this.lblVariateTableHeader);
		this.generalLayout.addComponent(this.lblVariateTableDescription);
		this.generalLayout.addComponent(this.variates);
		this.generalLayout.addComponent(this.chkVariatesSelectAll);

		this.generalLayout.addComponent(this.datasetVariablesDetailLayout);
		this.generalLayout.addComponent(this.buttonArea);

		this.generalLayout.setComponentAlignment(this.buttonArea, Alignment.TOP_LEFT);

		this.addComponent(this.generalLayout);
	}

	protected Table initializeFactorsTable() {

		final Table tblFactors = new Table();
		tblFactors.setDebugId("tblFactors");
		tblFactors.setImmediate(true);
		tblFactors.setWidth("100%");
		tblFactors.setHeight("170px");

		final BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
		container.setBeanIdProperty("id");
		tblFactors.setContainerDataSource(container);

		final String[] columns = new String[] {"name", MultiSiteAnalysisSelectPanel.DESCRIPTION_COLUMN};
		final String[] columnHeaders = new String[] {"Name", MultiSiteAnalysisSelectPanel.DESCRIPTION};
		tblFactors.setVisibleColumns(columns);
		tblFactors.setColumnHeaders(columnHeaders);

		tblFactors.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("unchecked")
			public String generateDescription(final Component source, final Object itemId, final Object propertyId) {
				final BeanContainer<Integer, FactorModel> container =
						(BeanContainer<Integer, FactorModel>) tblFactors.getContainerDataSource();
				final FactorModel fm = container.getItem(itemId).getBean();

				final StringBuilder sb = new StringBuilder();
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

	protected void initializeVariatesTable() {

		this.variates = new Table();
		this.variates.setDebugId("variates");
		this.variates.setImmediate(true);
		this.variates.setWidth("100%");
		this.variates.setHeight("100%");
		this.variates.setColumnExpandRatio("", 0.5f);
		this.variates.setColumnExpandRatio("name", 1);
		this.variates.setColumnExpandRatio(MultiSiteAnalysisSelectPanel.DESCRIPTION_COLUMN, 4);
		this.variates.setColumnExpandRatio(MultiSiteAnalysisSelectPanel.TESTEDIN, 1);

		this.variates.addGeneratedColumn(MultiSiteAnalysisSelectPanel.TESTEDIN, new Table.ColumnGenerator() {

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {

				final BeanContainer<Integer, VariateModel> container =
						(BeanContainer<Integer, VariateModel>) source.getContainerDataSource();
				final VariateModel vm = container.getItem(itemId).getBean();

				final int testedIn = MultiSiteAnalysisSelectPanel.this.getTestedIn(
						MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironment.getValue().toString(),
						MultiSiteAnalysisSelectPanel.this.environmentNames, vm.getVariableId(),
						MultiSiteAnalysisSelectPanel.this.getCurrentDataSetId(), MultiSiteAnalysisSelectPanel.this.trialEnvironments);
				if (testedIn > 3) {
					vm.setActive(true);
				}

				return String.format("%s of %s", testedIn, MultiSiteAnalysisSelectPanel.this.environmentNames.size());

			}
		});

		this.variates.addGeneratedColumn("", new Table.ColumnGenerator() {

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {

				final BeanContainer<Integer, VariateModel> container =
						(BeanContainer<Integer, VariateModel>) MultiSiteAnalysisSelectPanel.this.variates.getContainerDataSource();
				final VariateModel vm = container.getItem(itemId).getBean();

				final CheckBox checkBox = new CheckBox();
				checkBox.setDebugId("checkBox");
				checkBox.setImmediate(true);
				checkBox.setVisible(true);
				checkBox.addListener(new Property.ValueChangeListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(final ValueChangeEvent event) {
						final Boolean val = (Boolean) event.getProperty().getValue();
						MultiSiteAnalysisSelectPanel.this.getVariatesCheckboxState().put(vm.getName(), val);
						vm.setActive(val);

						if (!val) {
							MultiSiteAnalysisSelectPanel.this.chkVariatesSelectAll
									.removeListener(MultiSiteAnalysisSelectPanel.this.selectAllListener);
							MultiSiteAnalysisSelectPanel.this.chkVariatesSelectAll.setValue(val);
							MultiSiteAnalysisSelectPanel.this.chkVariatesSelectAll
									.addListener(MultiSiteAnalysisSelectPanel.this.selectAllListener);
						}

					}
				});

				if (!MultiSiteAnalysisSelectPanel.this.refreshing) {
					final int testedIn = MultiSiteAnalysisSelectPanel.this.getTestedIn(
							MultiSiteAnalysisSelectPanel.this.selectSpecifyEnvironment.getValue().toString(),
							MultiSiteAnalysisSelectPanel.this.environmentNames, vm.getVariableId(),
							MultiSiteAnalysisSelectPanel.this.getCurrentDataSetId(), MultiSiteAnalysisSelectPanel.this.trialEnvironments);
					if (testedIn > 3) {
						vm.setActive(true);
					} else {
						vm.setActive(false);
					}
				}

				if (vm.getActive()) {
					checkBox.setValue(true);
					MultiSiteAnalysisSelectPanel.this.getVariatesCheckboxState().put(vm.getName(), true);
				} else {
					checkBox.setValue(false);
					MultiSiteAnalysisSelectPanel.this.getVariatesCheckboxState().put(vm.getName(), false);
				}

				return checkBox;

			}

		});

		this.variates.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("unchecked")
			public String generateDescription(final Component source, final Object itemId, final Object propertyId) {
				final BeanContainer<Integer, VariateModel> container =
						(BeanContainer<Integer, VariateModel>) MultiSiteAnalysisSelectPanel.this.variates.getContainerDataSource();
				final VariateModel vm = container.getItem(itemId).getBean();

				final StringBuilder sb = new StringBuilder();
				sb.append(String.format("<span class=\"gcp-table-header-bold\">%s</span><br>", vm.getDisplayName()));
				sb.append(String.format("<span>Property:</span> %s<br>", vm.getTrname()));
				sb.append(String.format("<span>Scale:</span> %s<br>", vm.getScname()));
				sb.append(String.format("<span>Method:</span> %s<br>", vm.getTmname()));
				sb.append(String.format("<span>Data Type:</span> %s", vm.getDatatype()));

				return sb.toString();
			}
		});

		final BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
		container.setBeanIdProperty("id");
		this.variates.setContainerDataSource(container);

		final String[] columns =
				new String[] {"", "displayName", MultiSiteAnalysisSelectPanel.DESCRIPTION_COLUMN, MultiSiteAnalysisSelectPanel.TESTEDIN};
		final String[] columnHeaders = new String[] {"<span class='glyphicon glyphicon-ok'></span>", "Name",
				MultiSiteAnalysisSelectPanel.DESCRIPTION, "Tested In"};
		this.variates.setVisibleColumns(columns);
		this.variates.setColumnHeaders(columnHeaders);
		this.variates.setColumnWidth("", 18);

	}

	protected Component layoutButtonArea() {
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSizeFull();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);

		final Label spacer = new Label("&nbsp;", Label.CONTENT_XHTML);
		spacer.setDebugId("spacer");
		spacer.setSizeFull();

		buttonLayout.addComponent(spacer);
		buttonLayout.setExpandRatio(spacer, 1.0F);
		buttonLayout.addComponent(this.btnCancel);
		buttonLayout.addComponent(this.btnNext);

		return buttonLayout;
	}

	public void populateFactorsVariatesByDataSetId(final Study study, final Table factors, final Table variates) {

		try {
			final int studyId = study.getId();
			final DataSet ds = this.studyDataManager.findOneDataSetByType(studyId, DatasetTypeEnum.MEANS_DATA.getId());
			final DataSet trialDs = this.studyDataManager.findOneDataSetByType(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());

			if (ds == null || trialDs == null) {
				return;
			}

			final List<FactorModel> factorList = new ArrayList<FactorModel>();
			final List<VariateModel> variateList = new ArrayList<VariateModel>();

			this.populateEnvironmentDropdown(trialDs);

			this.populateGenotypeDropdown(ds, factorList);

			this.populateTraitGroup(ds, variateList);

			this.setCurrentDatasetName(ds.getName());
			this.setCurrentDataSetId(ds.getId());

			this.updateFactorsTable(factorList, factors);
			this.updateVariatesTable(variateList, factors, variates);

		} catch (final MiddlewareException e) {
			MultiSiteAnalysisSelectPanel.LOG.error("Error getting dataset(s) for MSA screen", e);
		}
	}

	protected void populateTraitGroup(final DataSet ds, final List<VariateModel> variateList) {
		for (final DMSVariableType variate : ds.getVariableTypes().getVariates().getVariableTypes()) {

			final VariateModel vm = new VariateModel();
			vm.setId(variate.getRank());
			vm.setVariableId(variate.getId());
			vm.setName(variate.getLocalName());
			vm.setDisplayName(variate.getLocalName().replace("_Means", ""));
			vm.setScname(variate.getStandardVariable().getScale().getName());
			vm.setScaleid(variate.getStandardVariable().getScale().getId());
			vm.setTmname(variate.getStandardVariable().getMethod().getName());
			vm.setTmethid(variate.getStandardVariable().getMethod().getId());
			vm.setTrname(variate.getStandardVariable().getProperty().getName());
			vm.setTraitid(variate.getStandardVariable().getProperty().getId());
			vm.setDescription(variate.getLocalDescription());
			vm.setDatatype(variate.getStandardVariable().getDataType().getName());
			if (!"error estimate".equalsIgnoreCase(variate.getStandardVariable().getMethod().getName())
					&& !variate.getStandardVariable().getMethod().getName()
							.equalsIgnoreCase("error estimate (" + variate.getLocalName().replace("_UnitErrors", "") + ")")
					&& !"ls blups".equalsIgnoreCase(variate.getStandardVariable().getMethod().getName())) {
				vm.setActive(false);
				variateList.add(vm);
			}

		}
	}

	protected void populateGenotypeDropdown(final DataSet ds, final List<FactorModel> factorList) {
		for (final DMSVariableType factor : ds.getVariableTypes().getFactors().getVariableTypes()) {

			final FactorModel fm = new FactorModel();
			fm.setId(factor.getRank());
			fm.setName(factor.getLocalName());
			fm.setScname(factor.getStandardVariable().getScale().getName());
			fm.setScaleid(factor.getStandardVariable().getScale().getId());
			fm.setTmname(factor.getStandardVariable().getMethod().getName());
			fm.setTmethid(factor.getStandardVariable().getMethod().getId());
			fm.setTrname(factor.getStandardVariable().getProperty().getName());
			fm.setDescription(factor.getLocalDescription());
			fm.setTraitid(factor.getStandardVariable().getProperty().getId());
			fm.setDataType(factor.getStandardVariable().getDataType().getName());

			if ((PhenotypicType.GERMPLASM.equals(factor.getStandardVariable().getPhenotypicType()) ||
				 factor.getStandardVariable().getId() == TermId.ENTRY_NO.getId()) &&
				!SingleSiteAnalysisDetailsPanel.GENOTYPES_TO_HIDE.contains(factor.getId())) {
				factorList.add(fm);
				this.getSelectSpecifyGenotypes().addItem(fm.getName());
			}
		}
	}

	protected void populateEnvironmentDropdown(final DataSet trialDs) {
		for (final DMSVariableType factor : trialDs.getVariableTypes().getFactors().getVariableTypes()) {

			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
					&& factor.getStandardVariable().getId() != TermId.TRIAL_INSTANCE_FACTOR.getId()) {
				this.getSelectSpecifyEnvironmentGroups().addItem(factor.getLocalName());
			}

			if (factor.getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()
					|| this.isGeolocationProperty(factor.getStandardVariable())) {
				this.getSelectSpecifyEnvironment().addItem(factor.getLocalName());
			}
		}
	}

	private boolean isGeolocationProperty(final StandardVariable standardVariable) {
		return standardVariable.getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
				&& (standardVariable.getId() != TermId.TRIAL_INSTANCE_FACTOR.getId() || standardVariable.getId() != TermId.LATITUDE.getId()
						&& standardVariable.getId() != TermId.LONGITUDE.getId() && standardVariable.getId() != TermId.GEODETIC_DATUM.getId()
						&& standardVariable.getId() != TermId.ALTITUDE.getId());
	}

	private void updateFactorsTable(final List<FactorModel> factorList, final Table factors) {
		final Object[] oldColumns = factors.getVisibleColumns();
		final String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);

		final BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
		container.setBeanIdProperty("id");
		factors.setContainerDataSource(container);

		for (final FactorModel f : factorList) {
			container.addBean(f);
		}

		factors.setContainerDataSource(container);

		factors.setVisibleColumns(columns);
	}

	private void updateVariatesTable(final List<VariateModel> variateList, final Table factors, final Table variates) {
		final BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
		container.setBeanIdProperty("id");
		this.variates.setContainerDataSource(container);

		for (final VariateModel v : variateList) {
			container.addBean(v);
		}

		this.variates.setContainerDataSource(container);

		this.variates.setVisibleColumns(
				new String[] {"", "displayName", MultiSiteAnalysisSelectPanel.DESCRIPTION_COLUMN, MultiSiteAnalysisSelectPanel.TESTEDIN});
		this.variates.setColumnHeaders(new String[] {"<span class='glyphicon glyphicon-ok'></span>", "Name",
				MultiSiteAnalysisSelectPanel.DESCRIPTION, "Tested In"});
	}

	private int getTestedIn(final String envFactorName, final List<String> environmentNames, final Integer variableId,
			final Integer meansDataSetId, final TrialEnvironments trialEnvironments) {
		int counter = 0;

		for (final String environmentName : environmentNames) {
			try {
				final TrialEnvironment te = trialEnvironments.findOnlyOneByLocalName(envFactorName, environmentName);
				if (te != null) {
					final long count = this.studyDataManager.countStocks(meansDataSetId, te.getId(), variableId);
					if (count > 0) {
						counter++;
					}
				}
			} catch (final Exception e) {
				MultiSiteAnalysisSelectPanel.LOG.error("Error counting stocks", e);
			}

		}

		return counter;
	}

	// SETTERS AND GETTERS
	public Project getCurrentProject() {
		return this.currentProject;
	}

	public void setCurrentProject(final Project currentProject) {
		this.currentProject = currentProject;
	}

	public Study getCurrentStudy() {
		return this.currentStudy;
	}

	public void setCurrentStudy(final Study currentStudy) {
		this.currentStudy = currentStudy;
	}

	public Integer getCurrentRepresentationId() {
		return this.currentRepresentationId;
	}

	public void setCurrentRepresentationId(final Integer currentRepresentationId) {
		this.currentRepresentationId = currentRepresentationId;
	}

	public Integer getCurrentDataSetId() {
		return this.currentDataSetId;
	}

	public void setCurrentDataSetId(final Integer currentDataSetId) {
		this.currentDataSetId = currentDataSetId;
	}

	public String getCurrentDatasetName() {
		return this.currentDatasetName;
	}

	public void setCurrentDatasetName(final String currentDatasetName) {
		this.currentDatasetName = currentDatasetName;
	}

	public MultiSiteAnalysisPanel getGxeAnalysisComponentPanel() {
		return this.gxeAnalysisComponentPanel;
	}

	public void setGxeAnalysisComponentPanel(final MultiSiteAnalysisPanel gxeAnalysisComponentPanel) {
		this.gxeAnalysisComponentPanel = gxeAnalysisComponentPanel;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return this.variatesCheckboxState;
	}

	public void setVariatesCheckboxState(final Map<String, Boolean> variatesCheckboxState) {
		this.variatesCheckboxState = variatesCheckboxState;
	}

	@Override
	public Object getData() {
		return this.getCurrentStudy();

	}

	public Select getSelectSpecifyEnvironment() {
		return this.selectSpecifyEnvironment;
	}

	public Select getSelectSpecifyGenotypes() {
		return this.selectSpecifyGenotypes;
	}

	public Select getSelectSpecifyEnvironmentGroups() {
		return this.selectSpecifyEnvironmentGroups;
	}

}
