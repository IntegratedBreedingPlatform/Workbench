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

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.ui.breedingview.SelectStudyDialog;
import org.generationcp.ibpworkbench.ui.breedingview.SelectStudyDialogForBreedingViewUpload;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Aldrin Batac
 */
@Configurable
public class SingleSiteAnalysisPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent, IBPWorkbenchLayout {

	private static final long serialVersionUID = 1L;

	private Button browseLink;
	private Button uploadLink;

	private HorizontalLayout titleLayout;
	private Label toolTitle;
	private HeaderLabelLayout heading;
	private Label lblGermplasmDescriptors;
	private Label lblVariates;
	private Label lblCovariates;

	private VariableTableComponent germplasmDescriptorsComponent;
	private VariableTableComponent variatesTableComponent;
	private VariableTableComponent covariatesTableComponent;

	private VerticalLayout studyDetailsContainer;
	private VerticalLayout germplasmDescriptorHeaderLayout;
	private VerticalLayout germplasmDescriptorTableLayout;
	private VerticalLayout traitHeaderLayout;
	private VerticalLayout traitTableLayout;
	private VerticalLayout covariateHeaderLayout;
	private VerticalLayout covariateTableLayout;

	private VerticalLayout rootLayout;

	private GridLayout studyDetailsLayout;

	private Project currentProject;

	private Study currentStudy;

	private Integer currentDataSetId;

	private String currentDatasetName;

	private Button btnCancel;
	private Button btnNext;
	private Component buttonArea;

	private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Value("${workbench.is.server.app}")
	private String isServerApp;

	@Autowired
	private StudyDataManager studyDataManager;

	public SingleSiteAnalysisPanel(final Project currentProject) {
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
		this.messageSource.setCaption(this.btnCancel, Message.CANCEL);
		this.messageSource.setCaption(this.btnNext, Message.NEXT);
		this.messageSource.setValue(this.toolTitle, Message.TITLE_SSA);
	}

	@Override
	public void instantiateComponents() {
		this.setTitleContent();

		final ThemeResource resource = new ThemeResource("../vaadin-retro/images/search-nurseries.png");
		final Label headingLabel = new Label("Select Data for Analysis");
		headingLabel.setDebugId("headingLabel");
		headingLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		headingLabel.addStyleName("label-bold");
		this.heading = new HeaderLabelLayout(resource, headingLabel);
		this.heading.setDebugId("heading");

		this.browseLink = new Button();
		this.browseLink.setDebugId("browseLink");
		this.browseLink.setImmediate(true);
		this.browseLink.setStyleName("link");
		this.browseLink.setCaption("Browse");
		this.browseLink.setWidth("48px");

		this.uploadLink = new Button();
		this.uploadLink.setDebugId("uploadLink");
		this.uploadLink.setImmediate(true);
		this.uploadLink.setStyleName("link");
		this.uploadLink.setCaption("Upload");
		this.uploadLink.setWidth("48px");

		this.initalizeTableComponents();

		this.buttonArea = this.layoutButtonArea();

		this.lblGermplasmDescriptors = new Label(
				"<span class='bms-factors' style='color: #39B54A; font-size: 22px; font-weight: bold;'></span><b>&nbsp;GERMPLASM DESCRIPTORS</b>",
				Label.CONTENT_XHTML);
		this.lblGermplasmDescriptors.setStyleName(Bootstrap.Typography.H4.styleName());
		this.lblGermplasmDescriptors.setWidth("100%");

		this.lblVariates = new Label(
				"<span class='bms-variates' style='color: #B8D433; font-size: 22px; font-weight: bold;'></span><b>&nbsp;TRAITS</b>",
				Label.CONTENT_XHTML);
		this.lblVariates.setWidth("100%");
		this.lblVariates.setStyleName(Bootstrap.Typography.H4.styleName());

		this.lblCovariates = new Label(
				"<span class='bms-variates' style='color: #B8D433; font-size: 22px; font-weight: bold;'></span><b>&nbsp;COVARIATES</b>",
				Label.CONTENT_XHTML);
		this.lblCovariates.setWidth("100%");
		this.lblCovariates.setStyleName(Bootstrap.Typography.H4.styleName());

	}

	@Override
	public void initializeValues() {
		// no values to initialize
	}

	@Override
	public void addListeners() {

		this.browseLink.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(final ClickEvent event) {
				final SelectStudyDialog dialog = new SelectStudyDialog(event.getComponent().getWindow(), SingleSiteAnalysisPanel.this,
						SingleSiteAnalysisPanel.this.currentProject);
				event.getComponent().getWindow().addWindow(dialog);

			}

		});

		this.uploadLink.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(final ClickEvent event) {
				final SelectStudyDialogForBreedingViewUpload dialog =
						new SelectStudyDialogForBreedingViewUpload(event.getComponent().getWindow(), SingleSiteAnalysisPanel.this,
								SingleSiteAnalysisPanel.this.currentProject);
				event.getComponent().getWindow().addWindow(dialog);
			}

		});

		this.btnCancel.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final ClickEvent event) {
				SingleSiteAnalysisPanel.this.reset();
				SingleSiteAnalysisPanel.this.toggleNextButton(false);
			}
		});
		this.openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(this);

		this.btnNext.addListener(this.openSelectDatasetForExportAction);
	}

	@Override
	public void layoutComponents() {
		this.setWidth("100%");

		final HorizontalLayout browseLabelLayout = new HorizontalLayout();
		browseLabelLayout.setDebugId("browseLabelLayout");
		browseLabelLayout.addComponent(this.browseLink);
		Label workWith = null;

		if (Boolean.parseBoolean(this.isServerApp)) {
			workWith = new Label("for a study to work with ");
		} else {
			workWith = new Label("for a study to work with.");
		}

		workWith.setWidth("150px");
		browseLabelLayout.addComponent(workWith);
		final Label orLabel = new Label("or");
		orLabel.setDebugId("orLabel");
		orLabel.setWidth("20px");

		if (Boolean.parseBoolean(this.isServerApp)) {
			browseLabelLayout.addComponent(orLabel);
			browseLabelLayout.addComponent(this.uploadLink);
			browseLabelLayout.addComponent(new Label(" Breeding View output files to BMS."));
		}

		browseLabelLayout.setSizeUndefined();

		final VerticalLayout selectDataForAnalysisLayout = new VerticalLayout();
		selectDataForAnalysisLayout.setDebugId("selectDataForAnalysisLayout");
		selectDataForAnalysisLayout.addComponent(this.heading);
		selectDataForAnalysisLayout.addComponent(browseLabelLayout);

		this.studyDetailsContainer = new VerticalLayout();
		this.germplasmDescriptorHeaderLayout = new VerticalLayout();
		this.germplasmDescriptorHeaderLayout.setDebugId("germplasmDescriptorHeaderLayout");
		this.traitHeaderLayout = new VerticalLayout();
		this.traitHeaderLayout.setDebugId("traitHeaderLayout");
		this.covariateHeaderLayout = new VerticalLayout();
		this.covariateHeaderLayout.setDebugId("traitHeaderLayout");
		this.germplasmDescriptorTableLayout = new VerticalLayout();
		this.germplasmDescriptorTableLayout.setDebugId("germplasmDescriptorTableLayout");
		this.traitTableLayout = new VerticalLayout();
		this.traitTableLayout.setDebugId("traitTableLayout");
		this.covariateTableLayout = new VerticalLayout();
		this.covariateTableLayout.setDebugId("covariateTableLayout");

		this.traitTableLayout.setSpacing(true);

		final SingleSiteAnalysisStudyDetailsComponent studyDetailsComponent = new SingleSiteAnalysisStudyDetailsComponent();
		this.studyDetailsContainer.addComponent(studyDetailsComponent);
		this.germplasmDescriptorHeaderLayout.addComponent(this.lblGermplasmDescriptors);
		this.traitHeaderLayout.addComponent(this.lblVariates);
		this.traitHeaderLayout.addComponent(new Label(
				"The traits in the dataset you have selected are shown below. Select the traits you wish to submit for analysis."));
		this.germplasmDescriptorTableLayout.addComponent(this.germplasmDescriptorsComponent);
		this.traitTableLayout.addComponent(this.variatesTableComponent);
		this.covariateHeaderLayout.addComponent(this.lblCovariates);
		this.covariateHeaderLayout
				.addComponent(new Label("Indicate any covariates you would like to include in the analysis below (optional)."));
		this.covariateTableLayout.addComponent(this.covariatesTableComponent);

		this.studyDetailsContainer.setMargin(true, false, false, false);
		this.germplasmDescriptorHeaderLayout.setMargin(true, true, false, false);
		this.traitHeaderLayout.setMargin(true, true, true, false);
		this.covariateHeaderLayout.setMargin(true, true, true, false);
		this.germplasmDescriptorTableLayout.setMargin(false, true, false, false);
		this.traitTableLayout.setMargin(false, true, false, false);
		this.covariateTableLayout.setMargin(false, true, false, false);

		this.studyDetailsLayout = new GridLayout(2, 5);
		this.studyDetailsLayout.setDebugId("studyDetailsLayout");
		this.studyDetailsLayout.setWidth("100%");

		this.studyDetailsLayout.addComponent(studyDetailsContainer, 0, 0, 0, 1);
		this.studyDetailsLayout.addComponent(this.germplasmDescriptorHeaderLayout, 1, 0, 1, 0);
		this.studyDetailsLayout.addComponent(this.germplasmDescriptorTableLayout, 1, 1, 1, 1);
		this.studyDetailsLayout.addComponent(this.traitHeaderLayout, 0, 2, 0, 2);
		this.studyDetailsLayout.addComponent(this.traitTableLayout, 0, 3, 0, 3);
		this.studyDetailsLayout.addComponent(this.covariateHeaderLayout, 1, 2, 1, 2);
		this.studyDetailsLayout.addComponent(this.covariateTableLayout, 1, 3, 1, 3);

		this.rootLayout = new VerticalLayout();
		this.rootLayout.setDebugId("rootLayout");
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
		this.titleLayout.setDebugId("titleLayout");
		this.titleLayout.setSpacing(true);

		this.toolTitle = new Label();
		this.toolTitle.setDebugId("toolTitle");
		this.toolTitle.setContentMode(Label.CONTENT_XHTML);
		this.toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		this.toolTitle.setHeight("26px");
		this.toolTitle.setWidth("278px");

		this.titleLayout.addComponent(this.toolTitle);
		this.titleLayout.addComponent(new HelpButton(HelpModule.SINGLE_SITE_ANALYSIS, "Go to Single Site Analysis Tutorial"));
	}

	protected Component layoutButtonArea() {

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");

		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);

		this.btnCancel = new Button();
		this.btnCancel.setDebugId("btnCancel");
		this.btnNext = new Button();
		this.btnNext.setDebugId("btnNext");
		this.btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.btnNext.setEnabled(false);// default

		buttonLayout.addComponent(this.btnCancel);
		buttonLayout.addComponent(this.btnNext);
		buttonLayout.setComponentAlignment(this.btnCancel, Alignment.TOP_CENTER);
		buttonLayout.setComponentAlignment(this.btnNext, Alignment.TOP_CENTER);
		return buttonLayout;
	}

	protected void reset() {
		this.studyDetailsContainer.removeAllComponents();
		this.germplasmDescriptorTableLayout.removeAllComponents();
		this.traitTableLayout.removeAllComponents();
		this.covariateTableLayout.removeAllComponents();

		this.initalizeTableComponents();

		this.germplasmDescriptorTableLayout.addComponent(this.germplasmDescriptorsComponent);
		this.traitTableLayout.addComponent(this.variatesTableComponent);
		this.covariateTableLayout.addComponent(this.covariatesTableComponent);
		this.studyDetailsContainer.addComponent(new SingleSiteAnalysisStudyDetailsComponent());
	}

	public Map<String, Boolean> getVariatesSelectionMap() {
		return this.variatesTableComponent.getCheckboxValuesMap();
	}

	public Map<String, Boolean> getCovariatesSelectionMap() {
		return this.covariatesTableComponent.getCheckboxValuesMap();
	}

	public void toggleNextButton(final boolean enabled) {
		this.btnNext.setEnabled(enabled);
	}

	public void showStudyDetails(final int dataSetId) {

		final DataSet ds = this.studyDataManager.getDataSet(dataSetId);

		if (this.getCurrentStudy() == null || this.getCurrentStudy().getId() != ds.getStudyId()) {
			final Study study = this.studyDataManager.getStudy(ds.getStudyId());
			this.setCurrentStudy(study);
		}

		final List<DMSVariableType> variates = ds.getVariableTypes().getVariates().getVariableTypes();
		final List<DMSVariableType> factors = ds.getVariableTypes().getFactors().getVariableTypes();

		this.setCurrentDatasetName(ds.getName());
		this.setCurrentDataSetId(ds.getId());

		this.germplasmDescriptorsComponent.loadData(this.filterDatasetAndStudyAndTreatmentFactorVariables(factors));
		this.covariatesTableComponent.loadData(variates);
		this.variatesTableComponent.loadData(variates);

		this.studyDetailsContainer.removeAllComponents();
		final SingleSiteAnalysisStudyDetailsComponent studyDetailsComponent =
				new SingleSiteAnalysisStudyDetailsComponent(ds.getName(), currentStudy.getDescription(), getCurrentStudy().getObjective(),
						currentStudy.getName(), "", false);
		this.studyDetailsContainer.addComponent(studyDetailsComponent);
	}

	protected List<DMSVariableType> filterDatasetAndStudyAndTreatmentFactorVariables(final List<DMSVariableType> factors) {
		final List<DMSVariableType> filteredVariables = new ArrayList<>();
		final List<Integer> treatmentFactorIds = this.getTreatmentFactors(factors);
		for (final DMSVariableType factor : factors) {
			if (factor.getStandardVariable().getPhenotypicType() != PhenotypicType.DATASET
					&& factor.getStandardVariable().getPhenotypicType() != PhenotypicType.STUDY && !treatmentFactorIds.contains(factor.getId())) {
				filteredVariables.add(factor);
			}
		}
		return filteredVariables;
	}

	protected List<Integer> getTreatmentFactors(final List<DMSVariableType> factors) {
		final List<Integer> treatmentFactorIds = new ArrayList<>();
		for(final DMSVariableType factor: factors) {
			if(!StringUtil.isEmpty(factor.getTreatmentLabel())) {
				treatmentFactorIds.add(factor.getId());
			}
		}
		return treatmentFactorIds;
	}

	protected void initalizeTableComponents() {

		this.germplasmDescriptorsComponent =
				new VariableTableComponent(new String[] {VariableTableComponent.NAME_COLUMN, VariableTableComponent.DESCRIPTION_COLUMN},
						false);
		this.variatesTableComponent = new VariableTableComponent(
				new String[] {VariableTableComponent.CHECKBOX_COLUMN, VariableTableComponent.NAME_COLUMN,
						VariableTableComponent.DESCRIPTION_COLUMN, VariableTableComponent.SCALE_NAME_COLUMN});
		this.variatesTableComponent.addSelectionChangedListener(new VariateTableSelectionChangedListener());
		this.variatesTableComponent.addSelectAllChangedListener(new VariateTableSelectAllChangedListener());
		this.covariatesTableComponent = new VariableTableComponent(
				new String[] {VariableTableComponent.CHECKBOX_COLUMN, VariableTableComponent.NAME_COLUMN,
						VariableTableComponent.DESCRIPTION_COLUMN, VariableTableComponent.SCALE_NAME_COLUMN});
		this.covariatesTableComponent.addSelectionChangedListener(new CovariateTableSelectionChangedListener());
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

	public List<VariableTableItem> getFactorList() {
		return this.germplasmDescriptorsComponent.getVariableTableItems();
	}

	public List<VariableTableItem> getVariateList() {
		return this.variatesTableComponent.getVariableTableItems();
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	protected void setGermplasmDescriptorsComponent(final VariableTableComponent germplasmDescriptorsComponent) {
		this.germplasmDescriptorsComponent = germplasmDescriptorsComponent;
	}

	protected void setVariatesTableComponent(final VariableTableComponent variatesTableComponent) {
		this.variatesTableComponent = variatesTableComponent;
	}

	protected void setCovariatesTableComponent(final VariableTableComponent covariatesTableComponent) {
		this.covariatesTableComponent = covariatesTableComponent;
	}

	protected void setStudyDetailsContainer(final VerticalLayout studyDetailsContainer) {
		this.studyDetailsContainer = studyDetailsContainer;
	}

	protected void setGermplasmDescriptorTableLayout(final VerticalLayout germplasmDescriptorTableLayout) {
		this.germplasmDescriptorTableLayout = germplasmDescriptorTableLayout;
	}

	protected void setTraitTableLayout(final VerticalLayout traitTableLayout) {
		this.traitTableLayout = traitTableLayout;
	}

	protected void setCovariateTableLayout(final VerticalLayout covariateTableLayout) {
		this.covariateTableLayout = covariateTableLayout;
	}

	protected VariableTableComponent getGermplasmDescriptorsComponent() {
		return germplasmDescriptorsComponent;
	}

	protected VariableTableComponent getVariatesTableComponent() {
		return variatesTableComponent;
	}

	protected VariableTableComponent getCovariatesTableComponent() {
		return covariatesTableComponent;
	}

	protected void setBtnNext(final Button btnNext) {
		this.btnNext = btnNext;
	}

	protected void toggleNextButton() {
		if (SingleSiteAnalysisPanel.this.variatesTableComponent.someItemsAreSelected()) {
			SingleSiteAnalysisPanel.this.btnNext.setEnabled(true);
		} else {
			SingleSiteAnalysisPanel.this.btnNext.setEnabled(false);
		}
	}

	class VariateTableSelectionChangedListener implements VariableTableComponent.SelectionChangedListener {

		@Override
		public void onSelectionChanged(final VariableTableItem variableTableItem) {

			SingleSiteAnalysisPanel.this.covariatesTableComponent.toggleCheckbox(variableTableItem.getId(), false, variableTableItem.getActive());
			SingleSiteAnalysisPanel.this.toggleNextButton();
		}
	}


	class VariateTableSelectAllChangedListener implements VariableTableComponent.SelectAllChangedListener {

		@Override
		public void onSelectionChanged(boolean isChecked) {

			if (!isChecked) {
				SingleSiteAnalysisPanel.this.covariatesTableComponent.resetAllCheckbox();
			}
			SingleSiteAnalysisPanel.this.toggleNextButton();
		}
	}

	class CovariateTableSelectionChangedListener implements VariableTableComponent.SelectionChangedListener {

		@Override
		public void onSelectionChanged(final VariableTableItem variableTableItem) {

			if (!variableTableItem.getActive()) {
				SingleSiteAnalysisPanel.this.variatesTableComponent.toggleCheckbox(variableTableItem.getId(), true, false);
			}
			SingleSiteAnalysisPanel.this.toggleNextButton();

		}
	}

}
