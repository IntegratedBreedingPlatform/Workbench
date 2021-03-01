/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.List;

import javax.annotation.Resource;

import org.generationcp.commons.util.StudyPermissionValidator;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.RunBreedingViewButtonClickListener;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.UploadBVFilesButtonClickListener;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Jeffrey Morales
 */
@Configurable
public class SingleSiteAnalysisDetailsPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	public static final List<Integer> GENOTYPES_TO_HIDE = Lists.newArrayList(TermId.ENTRY_TYPE.getId(), TermId.OBS_UNIT_ID.getId());
	public static final String INCOMPLETE_PLOT_DATA_ERROR =
			"cannot be used for analysis because the plot data is not complete. The data must contain at least 2 common entries with values.";
	public static final String MARGIN_TOP10 = "marginTop10";
	protected static final String REPLICATES = "REPLICATES";

	public static final String INVALID_SELECTION_STRING = "Invalid Selection";
	public static final String LABEL_BOLD_STYLING = "label-bold";
	public static final String LABEL_WIDTH = "185px";
	public static final String SELECT_BOX_WIDTH = "191px";
	public static final String SELECT_COLUMN = "select";
	public static final String TRIAL_NO_COLUMN = "trialno";
	public static final String ENVIRONMENT_NAME = "environmentName";
	protected static final String REQUIRED_FIELD_INDICATOR = " <span style='color: red'>*</span>";

	private SingleSiteAnalysisPanel selectDatasetForBreedingViewPanel;

	private Label lblPageTitle;
	private Label lblTitle;

	private SingleSiteAnalysisStudyDetailsComponent studyDetailsComponent;
	private SingleSiteAnalysisEnvironmentsComponent environmentsComponent;
	private SingleSiteAnalysisDesignDetails designDetailsComponent;
	private SingleSiteAnalysisGenotypesComponent genotypesComponent;

	private Button btnRun;
	private Button btnUpload;
	private Button btnReset;
	private Button btnBack;

	private BreedingViewInput breedingViewInput;
	private Tool tool;
	private List<DMSVariableType> factorsInDataset;
	private List<DMSVariableType> trialVariablesInDataset;

	private Project project;

	private VerticalLayout mainLayout;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Resource
	private StudyPermissionValidator studyPermissionValidator;
	
	public SingleSiteAnalysisDetailsPanel() {
		this.setWidth("100%");
	}

	public SingleSiteAnalysisDetailsPanel(final Tool tool, final BreedingViewInput breedingViewInput,
			final List<DMSVariableType> factorsInDataset, final List<DMSVariableType> trialVariablesInDataset, final Project project,
			final SingleSiteAnalysisPanel selectDatasetForBreedingViewPanel) {

		this.tool = tool;
		this.setBreedingViewInput(breedingViewInput);
		this.factorsInDataset = factorsInDataset;
		this.trialVariablesInDataset = trialVariablesInDataset;
		this.project = project;
		this.selectDatasetForBreedingViewPanel = selectDatasetForBreedingViewPanel;

		this.setWidth("100%");

	}

	protected void initializeComponents() {

		this.lblPageTitle = new Label();
		this.lblPageTitle.setDebugId("lblPageTitle");
		this.lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		final String datasetName = this.breedingViewInput.getDatasetName();
		final String description = this.breedingViewInput.getDescription();
		final String studyName = this.breedingViewInput.getDatasetSource();
		final String objective = this.breedingViewInput.getObjective();
		final String analysisName = this.breedingViewInput.getBreedingViewAnalysisName();

		this.studyDetailsComponent = new SingleSiteAnalysisStudyDetailsComponent(datasetName, description, objective, studyName, analysisName, true);
		this.environmentsComponent = new SingleSiteAnalysisEnvironmentsComponent(this);
		this.designDetailsComponent = new SingleSiteAnalysisDesignDetails(this);
		this.genotypesComponent = new SingleSiteAnalysisGenotypesComponent(this);

		this.mainLayout = new VerticalLayout();
		this.mainLayout.setDebugId("mainLayout");

		this.lblTitle = new Label();
		this.lblTitle.setDebugId("lblTitle");
		this.lblTitle.setStyleName(Bootstrap.Typography.H4.styleName());
		this.lblTitle.addStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblTitle.setHeight("25px");

		this.instantiateActionButtons();
	}

	protected void instantiateActionButtons() {
		this.btnRun = new Button();
		this.btnRun.setDebugId("btnRun");
		this.btnRun.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.btnUpload = new Button();
		this.btnUpload.setDebugId("btnUpload");
		this.btnUpload.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.btnReset = new Button();
		this.btnReset.setDebugId("btnReset");
		this.btnBack = new Button();
		this.btnBack.setDebugId("btnBack");
	}

	protected void initializeLayout() {

		this.mainLayout.setSizeUndefined();
		this.mainLayout.setWidth("100%");
		this.mainLayout.setSpacing(true);
		this.mainLayout.addComponent(this.lblPageTitle);
		this.mainLayout.addComponent(new Label(""));
		this.mainLayout.addComponent(this.lblTitle);

		final HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setDebugId("topLayout");
		topLayout.setSizeUndefined();
		topLayout.setWidth("100%");
		topLayout.addComponent(this.studyDetailsComponent);
		topLayout.addComponent(this.environmentsComponent);
		this.mainLayout.addComponent(topLayout);

		final HorizontalLayout bottomLayout = new HorizontalLayout();
		bottomLayout.setDebugId("combineLayout");
		bottomLayout.setSizeUndefined();
		bottomLayout.setSpacing(true);
		bottomLayout.setWidth("100%");
		bottomLayout.addComponent(this.designDetailsComponent);
		bottomLayout.addComponent(this.genotypesComponent);
		this.mainLayout.addComponent(bottomLayout);

		final HorizontalLayout combineLayout2 = new HorizontalLayout();
		combineLayout2.setDebugId("combineLayout2");
		combineLayout2.setSpacing(true);
		combineLayout2.addComponent(this.btnBack);
		combineLayout2.addComponent(this.btnReset);
		combineLayout2.addComponent(this.btnRun);
		combineLayout2.addComponent(this.btnUpload);
		combineLayout2.setComponentAlignment(this.btnReset, Alignment.TOP_CENTER);
		combineLayout2.setComponentAlignment(this.btnRun, Alignment.TOP_CENTER);
		combineLayout2.setComponentAlignment(this.btnUpload, Alignment.TOP_CENTER);
		this.mainLayout.addComponent(combineLayout2);
		this.mainLayout.setComponentAlignment(combineLayout2, Alignment.TOP_CENTER);

		this.mainLayout.setMargin(new MarginInfo(false, true, true, true));

		this.addComponent(this.mainLayout);
	}

	void reset() {
		this.environmentsComponent.reset();
		this.designDetailsComponent.reset();
		this.genotypesComponent.selectFirstItem();
		this.studyDetailsComponent.setAnalysisName();
	}

	protected void initializeActions() {

		this.btnBack.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 3878612968330447329L;

			@Override
			public void buttonClick(final ClickEvent event) {

				final IContentWindow w = (IContentWindow) event.getComponent().getWindow();
				SingleSiteAnalysisDetailsPanel.this.selectDatasetForBreedingViewPanel.setParent(null);
				w.showContent(SingleSiteAnalysisDetailsPanel.this.selectDatasetForBreedingViewPanel);

			}
		});

		this.btnReset.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 3878612968330447329L;

			@Override
			public void buttonClick(final ClickEvent event) {

				SingleSiteAnalysisDetailsPanel.this.reset();

			}
		});

		this.btnRun.addListener(new RunBreedingViewButtonClickListener(this));
		this.btnRun.setClickShortcut(KeyCode.ENTER);
		this.btnRun.addStyleName("primary");

		this.btnUpload.addListener(new UploadBVFilesButtonClickListener(this));
		this.btnUpload.addStyleName("primary");
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	@Override
	public void afterPropertiesSet() {
		this.assemble();
	}

	@Override
	public void attach() {
		super.attach();

		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setCaption(this.btnRun, Message.DOWNLOAD_INPUT_FILES);
		this.btnUpload.setVisible(true);
		this.btnUpload.setCaption("Upload Output Files to BMS");
		final StudyReference study = this.studyDataManager.getStudyReference(this.breedingViewInput.getStudyId());
		if (this.studyPermissionValidator.userLacksPermissionForStudy(study)) {
			this.btnUpload.setEnabled(false);
			this.btnUpload.setDescription(this.messageSource.getMessage(Message.LOCKED_STUDY_CANT_BE_MODIFIED, study.getOwnerName()));
		}

		this.messageSource.setCaption(this.btnReset, Message.CANCEL);
		this.messageSource.setCaption(this.btnBack, Message.BACK);

		this.messageSource.setValue(this.lblTitle, Message.BV_TITLE);
		this.messageSource.setValue(this.lblPageTitle, Message.TITLE_SSA);
	}

	public void setBreedingViewInput(final BreedingViewInput breedingViewInput) {
		this.breedingViewInput = breedingViewInput;
	}

	public List<SeaEnvironmentModel> getSelectedEnvironments() {
		return this.environmentsComponent.getSelectedEnvironments();
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	int getTermId(final String localName, final List<DMSVariableType> list) {
		for (final DMSVariableType variable : list) {
			if (variable.getLocalName().equals(localName)) {
				return variable.getId();
			}
		}

		return 0;
	}

	public Boolean environmentContainsValidDataForAnalysis(final SeaEnvironmentModel m) {
		final int germplasmTermId = this.getTermId(this.getSelGenotypesValue(), this.factorsInDataset);

		return this.studyDataManager.containsAtLeast2CommonEntriesWithValues(this.getBreedingViewInput().getDatasetId(), m.getLocationId(),
				germplasmTermId);
	}

	public Boolean replicateFactorEnabled() {
		return this.designDetailsComponent.replicateFactorEnabled();
	}

	public Tool getTool() {
		return this.tool;
	}

	public String getSelDesignTypeValue() {
		return this.designDetailsComponent.getSelDesignTypeValue();
	}

	public BreedingViewInput getBreedingViewInput() {
		return this.breedingViewInput;
	}

	public String getTxtAnalysisNameValue() {
		return this.studyDetailsComponent.getTxtAnalysisName();
	}

	public String getSelEnvFactorValue() {
		return this.environmentsComponent.getSelEnvFactorValue();
	}

	public void setTrialVariablesInDataset(final List<DMSVariableType> trialVariablesInDataset) {
		this.trialVariablesInDataset = trialVariablesInDataset;
	}

	public String getSelReplicatesValue() {
		return this.designDetailsComponent.getSelReplicatesValue();
	}

	public String getSelBlocksValue() {
		return this.designDetailsComponent.getSelBlocksValue();
	}

	public String getSelRowFactorValue() {
		return this.designDetailsComponent.getSelRowFactorValue();
	}

	public String getSelColumnFactorValue() {
		return this.designDetailsComponent.getSelColumnFactorValue();
	}

	public String getSelGenotypesValue() {
		return this.genotypesComponent.getSelGenotypesValue();
	}

	public Table getTblEnvironmentSelection() {
		return this.environmentsComponent.getTblEnvironmentSelection();
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	public SingleSiteAnalysisPanel getSelectDatasetForBreedingViewPanel() {
		return this.selectDatasetForBreedingViewPanel;
	}

	public List<DMSVariableType> getFactorsInDataset() {
		return this.factorsInDataset;
	}

	public List<DMSVariableType> getTrialVariablesInDataset() {
		return this.trialVariablesInDataset;
	}

	public Project getProject() {
		return this.project;
	}

	public SingleSiteAnalysisEnvironmentsComponent getEnvironmentsComponent() {
		return this.environmentsComponent;
	}

	protected void setStudyDetailsComponent(final SingleSiteAnalysisStudyDetailsComponent studyDetailsComponent) {
		this.studyDetailsComponent = studyDetailsComponent;
	}

	protected void setEnvironmentsComponent(final SingleSiteAnalysisEnvironmentsComponent environmentsComponent) {
		this.environmentsComponent = environmentsComponent;
	}

	protected void setDesignDetailsComponent(final SingleSiteAnalysisDesignDetails designDetailsComponent) {
		this.designDetailsComponent = designDetailsComponent;
	}

	protected void setGenotypesComponent(final SingleSiteAnalysisGenotypesComponent genotypesComponent) {
		this.genotypesComponent = genotypesComponent;
	}

	protected Button getBtnRun() {
		return this.btnRun;
	}

	protected Button getBtnUpload() {
		return this.btnUpload;
	}

	protected Button getBtnReset() {
		return this.btnReset;
	}

	protected Button getBtnBack() {
		return this.btnBack;
	}

	
	public void setStudyPermissionValidator(StudyPermissionValidator studyPermissionValidator) {
		this.studyPermissionValidator = studyPermissionValidator;
	}

}
