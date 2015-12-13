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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.BreedingViewDesignTypeValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewEnvFactorValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewReplicatesValueChangeListener;
import org.generationcp.ibpworkbench.actions.RunSingleSiteAction;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.mysql.jdbc.StringUtils;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Jeffrey Morales
 *
 */
@Configurable
public class SingleSiteAnalysisDetailsPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final String MARGIN_TOP10 = "marginTop10";
	private static final String REPLICATES = "REPLICATES";

	private final class RunBreedingViewButtonListener implements Button.ClickListener {

		private static final long serialVersionUID = -6682011023617457906L;

		@Override
		public void buttonClick(final ClickEvent event) {

			if (Boolean.parseBoolean(SingleSiteAnalysisDetailsPanel.this.isServerApp)) {
				new RunSingleSiteAction(SingleSiteAnalysisDetailsPanel.this, SingleSiteAnalysisDetailsPanel.this.project)
						.buttonClick(event);
				return;
			}

			List<DataSet> dataSets;
			try {

				dataSets =
						SingleSiteAnalysisDetailsPanel.this.studyDataManager.getDataSetsByType(
								SingleSiteAnalysisDetailsPanel.this.breedingViewInput.getStudyId(), DataSetType.MEANS_DATA);
				if (!dataSets.isEmpty()) {

					final DataSet meansDataSet = dataSets.get(0);
					final TrialEnvironments envs =
							SingleSiteAnalysisDetailsPanel.this.studyDataManager.getTrialEnvironmentsInDataset(meansDataSet.getId());

					Boolean environmentExists = false;
					for (final SeaEnvironmentModel model : SingleSiteAnalysisDetailsPanel.this.getSelectedEnvironments()) {

						final TrialEnvironment env =
								envs.findOnlyOneByLocalName(SingleSiteAnalysisDetailsPanel.this.breedingViewInput.getTrialInstanceName(),
										model.getTrialno());
						if (env != null) {
							environmentExists = true;
							break;
						}

					}

					if (environmentExists) {
						ConfirmDialog
								.show(event.getComponent().getWindow(),
										"",
										"One or more of the selected traits has existing means data. If you save the results of this analysis, the existing values will be overwritten.",
										"OK", "Cancel", new Runnable() {

											@Override
											public void run() {

												new RunSingleSiteAction(SingleSiteAnalysisDetailsPanel.this,
														SingleSiteAnalysisDetailsPanel.this.project).buttonClick(event);
											}

										});
					} else {
						new RunSingleSiteAction(SingleSiteAnalysisDetailsPanel.this, SingleSiteAnalysisDetailsPanel.this.project)
								.buttonClick(event);
					}

				} else {
					new RunSingleSiteAction(SingleSiteAnalysisDetailsPanel.this, SingleSiteAnalysisDetailsPanel.this.project)
							.buttonClick(event);
				}

			} catch (final MiddlewareQueryException e) {
				new RunSingleSiteAction(SingleSiteAnalysisDetailsPanel.this, SingleSiteAnalysisDetailsPanel.this.project)
						.buttonClick(event);
				SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);
			} catch (final Exception e) {
				new RunSingleSiteAction(SingleSiteAnalysisDetailsPanel.this, SingleSiteAnalysisDetailsPanel.this.project)
						.buttonClick(event);
				SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);
			}

		}
	}

	private final class FooterCheckBoxListener implements Property.ValueChangeListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(final ValueChangeEvent event) {

			final boolean selected = (Boolean) event.getProperty().getValue();
			if (!selected) {
				SingleSiteAnalysisDetailsPanel.this.disableEnvironmentEntries();
				SingleSiteAnalysisDetailsPanel.this.tblEnvironmentSelection.refreshRowCache();
				return;
			}

			try {

				final List<String> invalidEnvironments = SingleSiteAnalysisDetailsPanel.this.getInvalidEnvironments();
				if (!invalidEnvironments.isEmpty()) {

					MessageNotifier
							.showError(
									SingleSiteAnalysisDetailsPanel.this.getWindow(),
									SingleSiteAnalysisDetailsPanel.INVALID_SELECTION_STRING,
									SingleSiteAnalysisDetailsPanel.this.getSelEnvFactor().getValue().toString()
											+ " "
											+ StringUtil.joinIgnoreEmpty(",", invalidEnvironments)
											+ " cannot be used for analysis because the plot data is not complete. The data must contain at least 2 common entries with values.");
				}

			} catch (final Exception e) {
				SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);
			}

		}
	}

	private final class EnvironmentCheckBoxListener implements Property.ValueChangeListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(final ValueChangeEvent event) {

			final CheckBox chk = (CheckBox) event.getProperty();
			final Boolean val = (Boolean) event.getProperty().getValue();

			if (!val) {
				SingleSiteAnalysisDetailsPanel.this.footerCheckBox
						.removeListener(SingleSiteAnalysisDetailsPanel.this.footerCheckBoxListener);
				SingleSiteAnalysisDetailsPanel.this.footerCheckBox.setValue(false);
				SingleSiteAnalysisDetailsPanel.this.footerCheckBox.addListener(SingleSiteAnalysisDetailsPanel.this.footerCheckBoxListener);
				return;
			}

			final SeaEnvironmentModel model = (SeaEnvironmentModel) chk.getData();

			TrialEnvironments trialEnvironments;
			try {
				trialEnvironments =
						SingleSiteAnalysisDetailsPanel.this.studyDataManager
								.getTrialEnvironmentsInDataset(SingleSiteAnalysisDetailsPanel.this.getBreedingViewInput().getDatasetId());
				final TrialEnvironment trialEnv =
						trialEnvironments.findOnlyOneByLocalName(SingleSiteAnalysisDetailsPanel.this.getSelEnvFactor().getValue()
								.toString(), model.getEnvironmentName());

				if (trialEnv == null) {

					MessageNotifier.showError(SingleSiteAnalysisDetailsPanel.this.getWindow(),
							SingleSiteAnalysisDetailsPanel.INVALID_SELECTION_STRING, "\"" + model.getEnvironmentName()
									+ "\" value is not a valid selection for breeding view.");
					chk.setValue(false);
					model.setActive(false);

				} else {

					final int germplasmTermId =
							SingleSiteAnalysisDetailsPanel.this.getTermId(SingleSiteAnalysisDetailsPanel.this.selGenotypes.getValue()
									.toString(), SingleSiteAnalysisDetailsPanel.this.factorsInDataset);

					final Boolean valid =
							SingleSiteAnalysisDetailsPanel.this.studyDataManager.containsAtLeast2CommonEntriesWithValues(
									SingleSiteAnalysisDetailsPanel.this.getBreedingViewInput().getDatasetId(), model.getLocationId(),
									germplasmTermId);

					if (!valid) {
						MessageNotifier
								.showError(
										SingleSiteAnalysisDetailsPanel.this.getWindow(),
										SingleSiteAnalysisDetailsPanel.INVALID_SELECTION_STRING,
										SingleSiteAnalysisDetailsPanel.this.getSelEnvFactor().getValue().toString()
												+ " \""
												+ model.getEnvironmentName()
												+ "\" cannot be used for analysis because the plot data is not complete. The data must contain at least 2 common entries with values.");
						chk.setValue(false);
						model.setActive(false);
					} else {
						model.setActive(val);
					}

				}
			} catch (final ConfigException e) {
				SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);
			} catch (final MiddlewareException e) {
				SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);
			}

		}
	}

	private final class GenotypeValueChangeListener implements Property.ValueChangeListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(final ValueChangeEvent event) {

			try {

				final List<String> invalidEnvironments = SingleSiteAnalysisDetailsPanel.this.getInvalidEnvironments();
				if (!invalidEnvironments.isEmpty()) {

					MessageNotifier
							.showError(
									SingleSiteAnalysisDetailsPanel.this.getWindow(),
									SingleSiteAnalysisDetailsPanel.INVALID_SELECTION_STRING,
									SingleSiteAnalysisDetailsPanel.this.getSelEnvFactor().getValue().toString()
											+ " "
											+ StringUtil.joinIgnoreEmpty(",", invalidEnvironments)
											+ " cannot be used for analysis because the plot data is not complete. The data must contain at least 2 common entries with values.");
				}

			} catch (final Exception e) {
				SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);
			}

		}

	}

	private final class UploadListener implements Button.ClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(final ClickEvent event) {
			final Map<String, Boolean> visibleTraitsMap = new HashMap<>();
			for (final DMSVariableType factor : SingleSiteAnalysisDetailsPanel.this.factorsInDataset) {
				visibleTraitsMap.put(factor.getLocalName(), true);
			}
			visibleTraitsMap.putAll(SingleSiteAnalysisDetailsPanel.this.breedingViewInput.getVariatesActiveState());

			final FileUploadBreedingViewOutputWindow window =
					new FileUploadBreedingViewOutputWindow(event.getComponent().getWindow(),
							SingleSiteAnalysisDetailsPanel.this.breedingViewInput.getStudyId(),
							SingleSiteAnalysisDetailsPanel.this.project, visibleTraitsMap);

			event.getComponent().getWindow().addWindow(window);

		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(SingleSiteAnalysisDetailsPanel.class);
	private static final long serialVersionUID = 1L;

	public static final String REPLICATION_FACTOR = "replication factor";
	public static final String BLOCKING_FACTOR = "blocking factor";
	public static final String ROW_FACTOR = "row in layout";
	public static final String COLUMN_FACTOR = "column in layout";
	private static final String INVALID_SELECTION_STRING = "Invalid Selection";
	private static final String LABEL_BOLD_STYLING = "label-bold";
	private static final String LABEL_WIDTH = "160px";
	private static final String SELECT_BOX_WIDTH = "191px";
	private static final String SELECT_COLUMN = "select";
	private static final String TRIAL_NO_COLUMN = "trialno";

	@Value("${workbench.is.server.app}")
	private String isServerApp;

	private SingleSiteAnalysisPanel selectDatasetForBreedingViewPanel;

	private Label lblPageTitle;
	private Label lblTitle;
	private Label lblDatasetName;
	private Label lblDatasourceName;

	private Label lblVersion;
	private Label lblProjectType;
	private Label lblAnalysisName;
	private Label lblSiteEnvironment;
	private Label lblSpecifyEnvFactor;
	private Label lblSelectEnvironmentForAnalysis;
	private Label lblSpecifyNameForAnalysisEnv;
	private Label lblDesign;
	private Label lblDesignType;
	private Label lblReplicates;
	private Label lblBlocks;
	private Label lblSpecifyRowFactor;
	private Label lblSpecifyColumnFactor;
	private Label lblGenotypes;
	private Label lblDataSelectedForAnalysisHeader;
	private Label lblChooseEnvironmentHeader;
	private Label lblChooseEnvironmentDescription;
	private Label lblChooseEnvironmentForAnalysisDescription;
	private Label lblSpecifyDesignDetailsHeader;
	private Label lblSpecifyGenotypesHeader;
	private Button btnRun;
	private Button btnUpload;
	private Button btnReset;
	private Button btnBack;
	private TextField txtVersion;
	private Label valueProjectType;
	private TextField txtAnalysisName;
	private TextField txtNameForAnalysisEnv;
	private Label valueDatasetName;
	private Label valueDatasourceName;
	private Select selDesignType;
	private Select selEnvFactor;
	private Select selReplicates;
	private Select selBlocks;
	private Select selRowFactor;
	private Select selColumnFactor;
	private Select selGenotypes;

	private VerticalLayout blockRowColumnContainer;

	private CheckBox footerCheckBox;

	private Map<String, Boolean> environmentsCheckboxState;

	private VerticalLayout tblEnvironmentLayout;
	private Table tblEnvironmentSelection;

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

	private Property.ValueChangeListener envCheckBoxListener;
	private Property.ValueChangeListener footerCheckBoxListener;

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

	public Tool getTool() {
		return this.tool;
	}

	public TextField getTxtVersion() {
		return this.txtVersion;
	}

	public Select getSelDesignType() {
		return this.selDesignType;
	}

	public BreedingViewInput getBreedingViewInput() {
		return this.breedingViewInput;
	}

	public Label getValueProjectType() {
		return this.valueProjectType;
	}

	public TextField getTxtAnalysisName() {
		return this.txtAnalysisName;
	}

	public TextField getTxtNameForAnalysisEnv() {
		return this.txtNameForAnalysisEnv;
	}

	public Select getSelEnvFactor() {
		return this.selEnvFactor;
	}

	public void setSelEnvFactor(Select selEnvFactor) {
		this.selEnvFactor = selEnvFactor;
	}

	public Select getSelReplicates() {
		return this.selReplicates;
	}

	public Select getSelBlocks() {
		return this.selBlocks;
	}

	public Select getSelRowFactor() {
		return this.selRowFactor;
	}

	public Select getSelColumnFactor() {
		return this.selColumnFactor;
	}

	public Select getSelGenotypes() {
		return this.selGenotypes;
	}

	public void setStudyDataManager(StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	protected void initialize() {
		// not yet implemented
	}

	protected void initializeComponents() {

		this.lblPageTitle = new Label();
		this.lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		this.environmentsCheckboxState = new HashMap<String, Boolean>();

		this.tblEnvironmentLayout = new VerticalLayout();
		this.tblEnvironmentLayout.setSizeUndefined();
		this.tblEnvironmentLayout.setSpacing(true);
		this.tblEnvironmentLayout.setWidth("100%");

		this.tblEnvironmentSelection = new Table();
		this.tblEnvironmentSelection.setHeight("200px");
		this.tblEnvironmentSelection.setWidth("100%");

		this.setBlockRowColumnContainer(new VerticalLayout());

		this.envCheckBoxListener = new EnvironmentCheckBoxListener();

		this.tblEnvironmentSelection.addGeneratedColumn(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, new ColumnGenerator() {

			private static final long serialVersionUID = 8164025367842219781L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final SeaEnvironmentModel item = (SeaEnvironmentModel) itemId;

				final CheckBox chk = new CheckBox();
				chk.setData(item);
				chk.setValue(item.getActive());
				chk.setImmediate(true);
				chk.addListener(SingleSiteAnalysisDetailsPanel.this.envCheckBoxListener);
				return chk;
			}

		});

		this.footerCheckBoxListener = new FooterCheckBoxListener();

		this.footerCheckBox = new CheckBox("Select All", false);
		this.footerCheckBox.addListener(this.footerCheckBoxListener);
		this.footerCheckBox.setImmediate(true);

		this.tblEnvironmentLayout.addComponent(this.tblEnvironmentSelection);
		this.tblEnvironmentLayout.addComponent(this.footerCheckBox);

		this.mainLayout = new VerticalLayout();

		this.lblTitle = new Label();
		this.lblTitle.setStyleName(Bootstrap.Typography.H4.styleName());
		this.lblTitle.addStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblTitle.setHeight("25px");

		this.lblDatasetName = new Label();
		this.lblDatasetName.setContentMode(Label.CONTENT_XHTML);
		this.lblDatasetName.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblDatasourceName = new Label();
		this.lblDatasourceName.setContentMode(Label.CONTENT_XHTML);
		this.lblDatasourceName.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);

		this.lblVersion = new Label();
		this.lblVersion.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblProjectType = new Label();
		this.lblProjectType.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblProjectType.setWidth("100px");
		this.lblAnalysisName = new Label();
		this.lblAnalysisName.setContentMode(Label.CONTENT_XHTML);
		this.lblAnalysisName.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblSiteEnvironment = new Label();
		this.lblSpecifyEnvFactor = new Label();
		this.lblSpecifyEnvFactor.setContentMode(Label.CONTENT_XHTML);
		this.lblSpecifyEnvFactor.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblSelectEnvironmentForAnalysis = new Label();
		this.lblSelectEnvironmentForAnalysis.setContentMode(Label.CONTENT_XHTML);
		this.lblSelectEnvironmentForAnalysis.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblSpecifyNameForAnalysisEnv = new Label();
		this.lblSpecifyNameForAnalysisEnv.setContentMode(Label.CONTENT_XHTML);
		this.lblSpecifyNameForAnalysisEnv.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblDesign = new Label();
		this.lblDesignType = new Label();
		this.lblDesignType.setContentMode(Label.CONTENT_XHTML);
		this.lblDesignType.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblDesignType.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblReplicates = new Label();
		this.lblReplicates.setContentMode(Label.CONTENT_XHTML);
		this.lblReplicates.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblReplicates.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblBlocks = new Label();
		this.lblBlocks.setContentMode(Label.CONTENT_XHTML);
		this.lblBlocks.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblBlocks.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblSpecifyRowFactor = new Label();
		this.lblSpecifyRowFactor.setContentMode(Label.CONTENT_XHTML);
		this.lblSpecifyRowFactor.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblSpecifyRowFactor.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblSpecifyColumnFactor = new Label();
		this.lblSpecifyColumnFactor.setContentMode(Label.CONTENT_XHTML);
		this.lblSpecifyColumnFactor.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblSpecifyColumnFactor.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);
		this.lblGenotypes = new Label();
		this.lblGenotypes.setWidth(SingleSiteAnalysisDetailsPanel.LABEL_WIDTH);
		this.lblGenotypes.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);

		this.lblDataSelectedForAnalysisHeader =
				new Label("<span class='bms-dataset' style='position:relative; top: -1px; color: #FF4612; "
						+ "font-size: 20px; font-weight: bold;'></span><b>&nbsp;"
						+ this.messageSource.getMessage(Message.BV_DATA_SELECTED_FOR_ANALYSIS_HEADER) + "</b>", Label.CONTENT_XHTML);
		this.lblDataSelectedForAnalysisHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.lblChooseEnvironmentHeader =
				new Label("<span class='bms-environments' style='position:relative; top: -2px; color: #0076A9; "
						+ "font-size: 25px; font-weight: bold;'></span><b>&nbsp;" + "<span style='position:relative; top: -3px;'>"
						+ this.messageSource.getMessage(Message.BV_CHOOSE_ENVIRONMENT_HEADER) + "</span></b>", Label.CONTENT_XHTML);
		this.lblChooseEnvironmentHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.lblChooseEnvironmentDescription = new Label();
		this.lblChooseEnvironmentForAnalysisDescription = new Label();
		this.lblChooseEnvironmentForAnalysisDescription.setContentMode(Label.CONTENT_XHTML);
		this.lblChooseEnvironmentForAnalysisDescription.setStyleName(SingleSiteAnalysisDetailsPanel.LABEL_BOLD_STYLING);

		this.lblSpecifyDesignDetailsHeader =
				new Label("<span class='bms-exp-design' style='color: #9A8478; " + "font-size: 22px; font-weight: bold;'></span><b>&nbsp;"
						+ this.messageSource.getMessage(Message.BV_SPECIFY_DESIGN_DETAILS_HEADER) + "</b>", Label.CONTENT_XHTML);
		this.lblSpecifyDesignDetailsHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.lblSpecifyGenotypesHeader =
				new Label("<span class='bms-factors' style='color: #39B54A; " + "font-size: 20px; font-weight: bold;'></span><b>&nbsp;"
						+ this.messageSource.getMessage(Message.BV_SPECIFY_GENOTYPES_HEADER) + "</b>", Label.CONTENT_XHTML);
		this.lblSpecifyGenotypesHeader.setStyleName(Bootstrap.Typography.H3.styleName());

		this.txtVersion = new TextField();
		this.txtVersion.setNullRepresentation("");

		if (!StringUtils.isNullOrEmpty(this.getBreedingViewInput().getVersion())) {

			this.txtVersion.setValue(this.getBreedingViewInput().getVersion());
			this.txtVersion.setReadOnly(true);
			this.txtVersion.setRequired(false);

		} else {

			this.txtVersion.setNullSettingAllowed(false);
			this.txtVersion.setRequired(false);

		}

		this.valueProjectType = new Label();
		this.valueProjectType.setValue("Field Trial");

		this.valueDatasetName = new Label();
		this.valueDatasetName.setWidth("100%");
		this.valueDatasetName.setValue(this.getBreedingViewInput().getDatasetName());

		this.valueDatasourceName = new Label();
		this.valueDatasourceName.setWidth("100%");
		this.valueDatasourceName.setValue(this.getBreedingViewInput().getDatasetSource());

		this.txtAnalysisName = new TextField();
		this.txtAnalysisName.setNullRepresentation("");
		if (!StringUtils.isNullOrEmpty(this.getBreedingViewInput().getBreedingViewAnalysisName())) {
			this.txtAnalysisName.setValue(this.getBreedingViewInput().getBreedingViewAnalysisName());
		}
		this.txtAnalysisName.setRequired(false);
		this.txtAnalysisName.setWidth("450");

		this.selEnvFactor = new Select();
		this.selEnvFactor.setImmediate(true);
		this.populateChoicesForEnvironmentFactor();
		this.selEnvFactor.setNullSelectionAllowed(false);
		this.selEnvFactor.setNewItemsAllowed(false);

		this.populateChoicesForEnvForAnalysis();

		this.txtNameForAnalysisEnv = new TextField();
		this.txtNameForAnalysisEnv.setNullRepresentation("");
		this.txtNameForAnalysisEnv.setRequired(false);

		this.selDesignType = new Select();
		this.selDesignType.setImmediate(true);
		this.selDesignType.setNullSelectionAllowed(true);
		this.selDesignType.setNewItemsAllowed(false);
		this.selDesignType.addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		this.selDesignType.addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		this.selDesignType.addItem(DesignType.ROW_COLUMN_DESIGN.getName());
		this.selDesignType.addItem(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
		this.selDesignType.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.selReplicates = new Select();
		this.selReplicates.setImmediate(true);
		this.populateChoicesForReplicates();
		this.selReplicates.setNullSelectionAllowed(true);
		this.selReplicates.setNewItemsAllowed(false);
		this.selReplicates.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.selBlocks = new Select();
		this.selBlocks.setImmediate(true);
		this.selBlocks.setEnabled(false);
		this.populateChoicesForBlocks();
		this.selBlocks.setNullSelectionAllowed(false);
		this.selBlocks.setNewItemsAllowed(false);
		this.selBlocks.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.selRowFactor = new Select();
		this.selRowFactor.setImmediate(true);
		this.populateChoicesForRowFactor();
		this.selRowFactor.setNullSelectionAllowed(false);
		this.selRowFactor.setNewItemsAllowed(false);
		this.selRowFactor.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.selColumnFactor = new Select();
		this.selColumnFactor.setImmediate(true);
		this.populateChoicesForColumnFactor();
		this.selColumnFactor.setNullSelectionAllowed(false);
		this.selColumnFactor.setNewItemsAllowed(false);
		this.selColumnFactor.setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.refineChoicesForBlocksReplicationRowAndColumnFactos();

		this.setSelGenotypes(new Select());
		this.getSelGenotypes().setImmediate(true);
		this.populateChoicesForGenotypes();
		this.getSelGenotypes().setNullSelectionAllowed(true);
		this.getSelGenotypes().setNewItemsAllowed(false);
		this.getSelGenotypes().setWidth(SingleSiteAnalysisDetailsPanel.SELECT_BOX_WIDTH);

		this.btnRun = new Button();
		this.btnRun.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.btnUpload = new Button();
		this.btnUpload.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.btnReset = new Button();
		this.btnBack = new Button();

		this.checkDesignFactor();

	}

	public void populateChoicesForEnvironmentFactor() {

		if (this.trialVariablesInDataset == null) {
			return;
		}

		final String pleaseChoose = this.messageSource.getMessage(Message.PLEASE_CHOOSE);
		this.getSelEnvFactor().addItem(pleaseChoose);

		for (final DMSVariableType factor : this.trialVariablesInDataset) {
			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT) {
				this.getSelEnvFactor().addItem(factor.getLocalName());
				if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(factor.getLocalName())) {
					this.getSelEnvFactor().setValue(factor.getLocalName());
				}
			}
		}
		this.getSelEnvFactor().setValue(pleaseChoose);
		this.getSelEnvFactor().select(this.getSelEnvFactor().getItemIds().iterator().next());

		if (this.getSelEnvFactor().getItemIds().isEmpty()) {
			this.getSelEnvFactor().setEnabled(false);
		} else {
			this.getSelEnvFactor().setEnabled(true);
		}

	}

	public DMSVariableType getVariableByLocalName(final List<DMSVariableType> variables, final String name) {
		for (final DMSVariableType factor : variables) {
			if (factor.getLocalName().equals(name)) {
				return factor;
			}
		}
		return null;
	}

	public void populateChoicesForEnvForAnalysis() {

		this.footerCheckBox.setValue(false);
		String trialInstanceFactor = "";

		try {
			this.environmentsCheckboxState.clear();
			this.tblEnvironmentSelection.removeAllItems();
		} catch (final Exception e) {
			SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);
		}

		final String envFactorName = (String) this.selEnvFactor.getValue();

		final DMSVariableType factor = this.getVariableByLocalName(this.trialVariablesInDataset, envFactorName);

		if (factor == null) {
			return;
		}

		try {

			final BeanItemContainer<SeaEnvironmentModel> container = new BeanItemContainer<SeaEnvironmentModel>(SeaEnvironmentModel.class);
			this.tblEnvironmentSelection.setContainerDataSource(container);

			final VariableTypeList trialEnvFactors =
					this.studyDataManager.getDataSet(this.getBreedingViewInput().getDatasetId()).getVariableTypes().getFactors();

			for (final DMSVariableType f : trialEnvFactors.getVariableTypes()) {

				// Always Show the TRIAL INSTANCE Factor
				if (f.getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
					trialInstanceFactor = f.getLocalName();
				}

			}

			TrialEnvironments trialEnvironments;
			trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(this.getBreedingViewInput().getDatasetId());

			for (final TrialEnvironment env : trialEnvironments.getTrialEnvironments()) {

				final Variable trialVar = env.getVariables().findByLocalName(trialInstanceFactor);
				final Variable selectedEnvVar = env.getVariables().findByLocalName(envFactorName);

				if (trialVar != null && selectedEnvVar != null) {

					final TrialEnvironment temp = trialEnvironments.findOnlyOneByLocalName(envFactorName, selectedEnvVar.getValue());

					if (temp != null) {
						final SeaEnvironmentModel bean = new SeaEnvironmentModel();
						bean.setActive(false);
						bean.setEnvironmentName(selectedEnvVar.getValue());
						bean.setTrialno(trialVar.getValue());
						bean.setLocationId(temp.getId());
						container.addBean(bean);
					}

				}

			}

			if (trialInstanceFactor.equalsIgnoreCase(envFactorName)) {
				this.tblEnvironmentSelection.setVisibleColumns(new Object[] {SingleSiteAnalysisDetailsPanel.SELECT_COLUMN,
						SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN});
				this.tblEnvironmentSelection.setColumnHeaders(new String[] {"SELECT", trialInstanceFactor});
				this.tblEnvironmentSelection.setColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, 45);
				this.tblEnvironmentSelection.setColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, -1);
				this.tblEnvironmentSelection.setWidth("45%");
				this.getBreedingViewInput().setTrialInstanceName(trialInstanceFactor);
			} else {
				this.tblEnvironmentSelection.setVisibleColumns(new Object[] {SingleSiteAnalysisDetailsPanel.SELECT_COLUMN,
						SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, "environmentName"});
				this.tblEnvironmentSelection.setColumnHeaders(new String[] {"SELECT", trialInstanceFactor, envFactorName});
				this.tblEnvironmentSelection.setColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, 45);
				this.tblEnvironmentSelection.setColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, 60);
				this.tblEnvironmentSelection.setColumnWidth("environmentName", 500);
				this.tblEnvironmentSelection.setWidth("90%");

				this.getBreedingViewInput().setTrialInstanceName(trialInstanceFactor);
			}

		} catch (final ConfigException e) {
			SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);
		} catch (final MiddlewareException e) {
			SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);

		}

	}

	protected void populateChoicesForGenotypes() {

		for (final DMSVariableType factor : this.factorsInDataset) {
			if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM && factor.getId() != TermId.ENTRY_TYPE.getId()) {
				this.getSelGenotypes().addItem(factor.getLocalName());
				this.getSelGenotypes().setValue(factor.getLocalName());
			}
		}

		this.getSelGenotypes().select(this.getSelGenotypes().getItemIds().iterator().next());

	}

	protected void populateChoicesForReplicates() {
		for (final DMSVariableType factor : this.factorsInDataset) {
			if (factor.getStandardVariable().getProperty().getName().toString().trim()
					.equalsIgnoreCase(SingleSiteAnalysisDetailsPanel.REPLICATION_FACTOR)) {
				this.getSelReplicates().addItem(factor.getLocalName());
				this.getSelReplicates().setValue(factor.getLocalName());
			}
		}

		if (this.getSelReplicates().getItemIds().isEmpty()) {
			this.getSelReplicates().setEnabled(false);
		} else {
			this.getSelReplicates().setEnabled(true);
		}
	}

	protected void populateChoicesForBlocks() {

		for (final DMSVariableType factor : this.factorsInDataset) {
			if (factor.getStandardVariable().getProperty().getName().toString().trim()
					.equalsIgnoreCase(SingleSiteAnalysisDetailsPanel.BLOCKING_FACTOR)) {
				this.getSelBlocks().addItem(factor.getLocalName());
				this.getSelBlocks().setValue(factor.getLocalName());
				this.getSelBlocks().setEnabled(true);
			}
		}

	}

	protected void populateChoicesForRowFactor() {

		for (final DMSVariableType factor : this.factorsInDataset) {
			if (factor.getStandardVariable().getProperty().getName().toString().trim()
					.equalsIgnoreCase(SingleSiteAnalysisDetailsPanel.ROW_FACTOR)) {
				this.getSelRowFactor().addItem(factor.getLocalName());
				this.getSelRowFactor().setValue(factor.getLocalName());
			}
		}

	}

	protected void populateChoicesForColumnFactor() {

		for (final DMSVariableType factor : this.factorsInDataset) {
			if (factor.getStandardVariable().getProperty().getName().toString().trim()
					.equalsIgnoreCase(SingleSiteAnalysisDetailsPanel.COLUMN_FACTOR)) {
				this.getSelColumnFactor().addItem(factor.getLocalName());
				this.getSelColumnFactor().setValue(factor.getLocalName());
			}
		}

	}

	public void refineChoicesForBlocksReplicationRowAndColumnFactos() {
		if (this.selReplicates.getValue() != null) {
			this.selBlocks.removeItem(this.selReplicates.getValue());
			this.selRowFactor.removeItem(this.selReplicates.getValue());
			this.selColumnFactor.removeItem(this.selReplicates.getValue());
		}

		if (this.selBlocks.getValue() != null) {
			this.selReplicates.removeItem(this.selBlocks.getValue());
			this.selRowFactor.removeItem(this.selBlocks.getValue());
			this.selColumnFactor.removeItem(this.selBlocks.getValue());
		}

		if (this.selRowFactor.getValue() != null) {
			this.selReplicates.removeItem(this.selRowFactor.getValue());
			this.selBlocks.removeItem(this.selRowFactor.getValue());
			this.selColumnFactor.removeItem(this.selRowFactor.getValue());
		}

		if (this.selColumnFactor.getValue() != null) {
			this.selReplicates.removeItem(this.selColumnFactor.getValue());
			this.selBlocks.removeItem(this.selColumnFactor.getValue());
			this.selRowFactor.removeItem(this.selColumnFactor.getValue());
		}
	}

	public void checkDesignFactor() {
		String designFactor = null;
		final int designType = this.retrieveExperimentalDesignTypeID();
		if (designType != 0) {

			if (designType == TermId.RANDOMIZED_COMPLETE_BLOCK.getId()) {
				designFactor = DesignType.RANDOMIZED_BLOCK_DESIGN.getName();
				this.displayRandomizedBlockDesignElements();
			} else if (designType == TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()
					|| designType == TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()) {
				designFactor = DesignType.INCOMPLETE_BLOCK_DESIGN.getName();
				this.displayIncompleteBlockDesignElements();
			} else if (designType == TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()
					|| designType == TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()) {
				designFactor = DesignType.ROW_COLUMN_DESIGN.getName();
				this.displayRowColumnDesignElements();
			} else if (designType == TermId.ALPHA_LATTICE_E30_REP2.getId() || designType == TermId.ALPHA_LATTICE_E30_REP3.getId()
					|| designType == TermId.ALPHA_LATTICE_E50_REP2.getId()) {
				designFactor = DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName();
				this.displayAlphaLatticeDesignElements();
			}

			this.selDesignType.setValue(designFactor);
		} else {
			this.selDesignType.select(null);
		}

	}

	public void displayAlphaLatticeDesignElements() {
		final GridLayout gLayout = new GridLayout(2, 3);
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(SingleSiteAnalysisDetailsPanel.MARGIN_TOP10);

		this.getBlockRowColumnContainer().removeAllComponents();
		gLayout.addComponent(this.getLblBlocks(), 0, 0);
		gLayout.addComponent(this.getSelBlocks(), 1, 0);
		gLayout.addComponent(this.getLblSpecifyGenotypesHeader(), 0, 1, 1, 1);
		gLayout.addComponent(this.getLblGenotypes(), 0, 2);
		gLayout.addComponent(this.getSelGenotypes(), 1, 2);

		this.getBlockRowColumnContainer().addComponent(gLayout);
	}

	protected int retrieveExperimentalDesignTypeID() {
		try {
			final String expDesign =
					this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(),
							this.breedingViewInput.getStudyId());
			if (expDesign != null && !"".equals(expDesign.trim()) && NumberUtils.isNumber(expDesign)) {
				return Integer.parseInt(expDesign);
			}
		} catch (final MiddlewareQueryException e) {
			SingleSiteAnalysisDetailsPanel.LOG.error(e.getMessage(), e);
		}

		return 0;
	}

	protected void initializeLayout() {

		this.mainLayout.setSizeUndefined();
		this.mainLayout.setWidth("100%");
		this.mainLayout.setSpacing(true);

		final VerticalLayout selectedInfoLayout = new VerticalLayout();
		selectedInfoLayout.setSizeUndefined();
		selectedInfoLayout.setWidth("100%");
		selectedInfoLayout.setSpacing(true);

		final HorizontalLayout row1 = new HorizontalLayout();
		row1.setSpacing(true);
		row1.addComponent(this.lblDataSelectedForAnalysisHeader);

		final HorizontalLayout row2a = new HorizontalLayout();
		row2a.setSpacing(true);
		row2a.addComponent(this.lblDatasetName);
		row2a.addComponent(this.valueDatasetName);
		final HorizontalLayout row2b = new HorizontalLayout();
		row2b.setSpacing(true);
		row2b.addComponent(this.lblProjectType);
		row2b.addComponent(this.valueProjectType);

		final GridLayout row2 = new GridLayout(2, 1);
		row2.setSizeUndefined();
		row2.setWidth("100%");
		row2.setColumnExpandRatio(0, 0.45f);
		row2.setColumnExpandRatio(1, 0.55f);
		row2.addComponent(row2a);
		row2.addComponent(row2b);

		final HorizontalLayout row3 = new HorizontalLayout();
		row3.setSpacing(true);
		row3.addComponent(this.lblDatasourceName);
		row3.addComponent(this.valueDatasourceName);

		final VerticalLayout row4 = new VerticalLayout();
		row4.setSpacing(true);
		row4.addComponent(this.lblAnalysisName);
		row4.addComponent(this.txtAnalysisName);

		selectedInfoLayout.addComponent(row1);
		selectedInfoLayout.addComponent(row2);
		selectedInfoLayout.addComponent(row3);
		selectedInfoLayout.addComponent(row4);

		final GridLayout chooseEnvironmentLayout = new GridLayout(2, 9);
		chooseEnvironmentLayout.setColumnExpandRatio(0, 4);
		chooseEnvironmentLayout.setColumnExpandRatio(1, 2);
		chooseEnvironmentLayout.setWidth("100%");
		chooseEnvironmentLayout.setSpacing(true);
		chooseEnvironmentLayout.setMargin(false, true, true, false);
		chooseEnvironmentLayout.addComponent(this.lblChooseEnvironmentHeader, 0, 0, 1, 0);
		chooseEnvironmentLayout.addComponent(this.lblChooseEnvironmentDescription, 0, 1, 1, 1);
		chooseEnvironmentLayout.addComponent(this.lblSpecifyEnvFactor, 0, 2);
		chooseEnvironmentLayout.addComponent(this.selEnvFactor, 1, 2);
		chooseEnvironmentLayout.addComponent(this.lblChooseEnvironmentForAnalysisDescription, 0, 3, 1, 3);
		chooseEnvironmentLayout.addComponent(this.tblEnvironmentLayout, 0, 4, 1, 4);
		chooseEnvironmentLayout.addComponent(this.lblVersion, 0, 5);
		chooseEnvironmentLayout.addComponent(this.txtVersion, 1, 5);

		final VerticalLayout designDetailsWrapper = new VerticalLayout();

		final GridLayout designDetailsLayout = new GridLayout(2, 3);
		designDetailsLayout.setColumnExpandRatio(0, 0);
		designDetailsLayout.setColumnExpandRatio(1, 1);
		designDetailsLayout.setWidth("100%");
		designDetailsLayout.setSpacing(true);
		designDetailsLayout.setMargin(false, false, false, false);
		designDetailsLayout.addComponent(this.lblSpecifyDesignDetailsHeader, 0, 0, 1, 0);
		designDetailsLayout.addComponent(this.lblDesignType, 0, 1);
		designDetailsLayout.addComponent(this.selDesignType, 1, 1);
		designDetailsLayout.addComponent(this.lblReplicates, 0, 2);
		designDetailsLayout.addComponent(this.selReplicates, 1, 2);

		designDetailsWrapper.addComponent(designDetailsLayout);

		final GridLayout gLayout = new GridLayout(2, 2);
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(SingleSiteAnalysisDetailsPanel.MARGIN_TOP10);
		gLayout.addComponent(this.getLblSpecifyGenotypesHeader(), 0, 0, 1, 0);
		gLayout.addComponent(this.getLblGenotypes(), 0, 1);
		gLayout.addComponent(this.getSelGenotypes(), 1, 1);
		this.getBlockRowColumnContainer().addComponent(gLayout);

		designDetailsWrapper.addComponent(this.getBlockRowColumnContainer());

		this.mainLayout.addComponent(this.lblPageTitle);
		this.mainLayout.addComponent(new Label(""));

		final VerticalLayout subMainLayout = new VerticalLayout();
		subMainLayout.addComponent(this.lblTitle);
		subMainLayout.addComponent(selectedInfoLayout);
		this.mainLayout.addComponent(subMainLayout);

		final HorizontalLayout combineLayout = new HorizontalLayout();
		combineLayout.setSizeUndefined();
		combineLayout.setWidth("100%");
		combineLayout.addComponent(chooseEnvironmentLayout);
		combineLayout.addComponent(designDetailsWrapper);
		this.mainLayout.addComponent(combineLayout);

		final HorizontalLayout combineLayout2 = new HorizontalLayout();
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

	private void reset() {

		this.selEnvFactor.select(this.selEnvFactor.getItemIds().iterator().next());
		this.checkDesignFactor();
		this.selReplicates.select(this.selReplicates.getItemIds().iterator().next());
		this.getSelGenotypes().select(this.getSelGenotypes().getItemIds().iterator().next());
		this.footerCheckBox.setValue(false);
		this.txtAnalysisName.setValue(this.getBreedingViewInput().getBreedingViewAnalysisName());

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

		final Button.ClickListener runBreedingView = new RunBreedingViewButtonListener();

		this.btnRun.addListener(runBreedingView);
		this.btnRun.setClickShortcut(KeyCode.ENTER);
		this.btnRun.addStyleName("primary");

		this.btnUpload.addListener(new UploadListener());
		this.btnUpload.addStyleName("primary");

		this.selDesignType.addListener(new BreedingViewDesignTypeValueChangeListener(this));
		this.selReplicates.addListener(new BreedingViewReplicatesValueChangeListener(this));
		this.selEnvFactor.addListener(new BreedingViewEnvFactorValueChangeListener(this));
		this.selGenotypes.addListener(new GenotypeValueChangeListener());

	}

	protected void assemble() {
		this.initialize();
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
		this.messageSource.setValue(this.lblVersion, Message.BV_VERSION);
		this.messageSource.setValue(this.lblProjectType, Message.BV_PROJECT_TYPE);
		this.messageSource.setValue(this.lblAnalysisName, Message.BV_ANALYSIS_NAME);
		this.messageSource.setValue(this.lblSiteEnvironment, Message.BV_SITE_ENVIRONMENT);
		this.messageSource.setValue(this.lblSpecifyEnvFactor, Message.BV_SPECIFY_ENV_FACTOR);
		this.messageSource.setValue(this.lblSelectEnvironmentForAnalysis, Message.BV_SELECT_ENV_FOR_ANALYSIS);
		this.messageSource.setValue(this.lblSpecifyNameForAnalysisEnv, Message.BV_SPECIFY_NAME_FOR_ANALYSIS_ENV);
		this.messageSource.setValue(this.lblDesign, Message.BV_DESIGN);
		this.messageSource.setValue(this.lblDesignType, Message.DESIGN_TYPE);
		this.messageSource.setValue(this.getLblReplicates(), Message.BV_SPECIFY_REPLICATES);
		this.messageSource.setValue(this.getLblBlocks(), Message.BV_SPECIFY_BLOCKS);
		this.messageSource.setValue(this.getLblSpecifyRowFactor(), Message.BV_SPECIFY_ROW_FACTOR);
		this.messageSource.setValue(this.getLblSpecifyColumnFactor(), Message.BV_SPECIFY_COLUMN_FACTOR);
		this.messageSource.setValue(this.getLblGenotypes(), Message.BV_GENOTYPES);

		if (Boolean.parseBoolean(this.isServerApp)) {
			this.messageSource.setCaption(this.btnRun, Message.DOWNLOAD_INPUT_FILES);
			this.btnUpload.setVisible(true);
			this.btnUpload.setCaption("Upload Output Files to BMS");
		} else {
			this.messageSource.setCaption(this.btnRun, Message.RUN_BREEDING_VIEW);
			this.btnUpload.setVisible(false);
		}
		this.messageSource.setCaption(this.btnReset, Message.RESET);
		this.messageSource.setCaption(this.btnBack, Message.BACK);

		this.messageSource.setValue(this.lblTitle, Message.BV_TITLE);
		this.messageSource.setValue(this.lblPageTitle, Message.TITLE_SSA);
		this.messageSource.setValue(this.lblDatasetName, Message.BV_DATASET_NAME);
		this.messageSource.setValue(this.lblDatasourceName, Message.BV_DATASOURCE_NAME);
		this.messageSource.setValue(this.lblChooseEnvironmentDescription, Message.BV_CHOOSE_ENVIRONMENT_DESCRIPTION);
		this.messageSource.setValue(this.lblChooseEnvironmentForAnalysisDescription, Message.BV_CHOOSE_ENVIRONMENT_FOR_ANALYSIS_DESC);
	}

	public void setBreedingViewInput(final BreedingViewInput breedingViewInput) {
		this.breedingViewInput = breedingViewInput;
	}

	public Label getLblBlocks() {
		return this.lblBlocks;
	}

	public void setLblBlocks(final Label lblBlocks) {
		this.lblBlocks = lblBlocks;
	}

	public Label getLblSpecifyRowFactor() {
		return this.lblSpecifyRowFactor;
	}

	public void setLblSpecifyRowFactor(final Label lblSpecifyRowFactor) {
		this.lblSpecifyRowFactor = lblSpecifyRowFactor;
	}

	public Label getLblSpecifyColumnFactor() {
		return this.lblSpecifyColumnFactor;
	}

	public void setLblSpecifyColumnFactor(final Label lblSpecifyColumnFactor) {
		this.lblSpecifyColumnFactor = lblSpecifyColumnFactor;
	}

	public Label getLblReplicates() {
		return this.lblReplicates;
	}

	public void setLblReplicates(final Label lblReplicates) {
		this.lblReplicates = lblReplicates;
	}

	public Map<String, Boolean> getEnvironmentsCheckboxState() {
		return this.environmentsCheckboxState;
	}

	public void setEnvironmentsCheckboxState(final Map<String, Boolean> environmentsCheckboxState) {
		this.environmentsCheckboxState = environmentsCheckboxState;
	}

	public List<SeaEnvironmentModel> getSelectedEnvironments() {

		final List<SeaEnvironmentModel> envs = new ArrayList<SeaEnvironmentModel>();
		for (final Iterator<?> itr = this.tblEnvironmentSelection.getContainerDataSource().getItemIds().iterator(); itr.hasNext();) {
			final SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();
			if (m.getActive()) {
				envs.add(m);
			}
		}
		return envs;
	}

	public VerticalLayout getBlockRowColumnContainer() {
		return this.blockRowColumnContainer;
	}

	public void setBlockRowColumnContainer(final VerticalLayout blockRowColumnContainer) {
		this.blockRowColumnContainer = blockRowColumnContainer;
	}

	public void setSelGenotypes(final Select selGenotypes) {
		this.selGenotypes = selGenotypes;
	}

	public Label getLblGenotypes() {
		return this.lblGenotypes;
	}

	public void setLblGenotypes(final Label lblGenotypes) {
		this.lblGenotypes = lblGenotypes;
	}

	public Label getLblSpecifyGenotypesHeader() {
		return this.lblSpecifyGenotypesHeader;
	}

	public void setLblSpecifyGenotypesHeader(final Label lblSpecifyGenotypesHeader) {
		this.lblSpecifyGenotypesHeader = lblSpecifyGenotypesHeader;
	}

	public SimpleResourceBundleMessageSource getMessageSource() {
		return this.messageSource;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void displayRowColumnDesignElements() {
		final GridLayout gLayout = new GridLayout(2, 4);
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(SingleSiteAnalysisDetailsPanel.MARGIN_TOP10);

		this.getBlockRowColumnContainer().removeAllComponents();
		gLayout.addComponent(this.getLblSpecifyColumnFactor(), 0, 0);
		gLayout.addComponent(this.getSelColumnFactor(), 1, 0);
		gLayout.addComponent(this.getLblSpecifyRowFactor(), 0, 1);
		gLayout.addComponent(this.getSelRowFactor(), 1, 1);
		gLayout.addComponent(this.getLblSpecifyGenotypesHeader(), 0, 2, 1, 2);
		gLayout.addComponent(this.getLblGenotypes(), 0, 3);
		gLayout.addComponent(this.getSelGenotypes(), 1, 3);
		this.getBlockRowColumnContainer().addComponent(gLayout);

		if (!this.getSelReplicates().isEnabled() || this.getSelReplicates().getItemIds().isEmpty()) {

			for (final Object itemId : this.getSelBlocks().getItemIds()) {
				this.getSelReplicates().addItem(itemId);
				this.getSelReplicates().setItemCaption(itemId, SingleSiteAnalysisDetailsPanel.REPLICATES);
				this.getSelReplicates().select(itemId);
				this.getSelReplicates().setEnabled(true);
			}
		}
	}

	public void displayRandomizedBlockDesignElements() {
		final GridLayout gLayout = new GridLayout(2, 2);
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(SingleSiteAnalysisDetailsPanel.MARGIN_TOP10);

		this.getBlockRowColumnContainer().removeAllComponents();
		gLayout.addComponent(this.getLblSpecifyGenotypesHeader(), 0, 0, 1, 0);
		gLayout.addComponent(this.getLblGenotypes(), 0, 1);
		gLayout.addComponent(this.getSelGenotypes(), 1, 1);
		this.getBlockRowColumnContainer().addComponent(gLayout);

		if (!this.getSelReplicates().isEnabled() || this.getSelReplicates().getItemIds().isEmpty()) {

			for (final Object itemId : this.getSelBlocks().getItemIds()) {
				this.getSelReplicates().addItem(itemId);
				this.getSelReplicates().setItemCaption(itemId, SingleSiteAnalysisDetailsPanel.REPLICATES);
				this.getSelReplicates().select(itemId);
				this.getSelReplicates().setEnabled(true);
			}
		}
	}

	public void displayIncompleteBlockDesignElements() {
		final GridLayout gLayout = new GridLayout(2, 3);
		gLayout.setColumnExpandRatio(0, 0);
		gLayout.setColumnExpandRatio(1, 1);
		gLayout.setWidth("100%");
		gLayout.setSpacing(true);
		gLayout.addStyleName(SingleSiteAnalysisDetailsPanel.MARGIN_TOP10);

		this.getBlockRowColumnContainer().removeAllComponents();
		gLayout.addComponent(this.getLblBlocks(), 0, 0);
		gLayout.addComponent(this.getSelBlocks(), 1, 0);
		gLayout.addComponent(this.getLblSpecifyGenotypesHeader(), 0, 1, 1, 1);
		gLayout.addComponent(this.getLblGenotypes(), 0, 2);
		gLayout.addComponent(this.getSelGenotypes(), 1, 2);

		this.getBlockRowColumnContainer().addComponent(gLayout);

		if (!this.getSelReplicates().isEnabled() || this.getSelReplicates().getItemIds().isEmpty()) {

			for (final Object itemId : this.getSelBlocks().getItemIds()) {
				this.getSelReplicates().addItem(itemId);
				this.getSelReplicates().setItemCaption(itemId, SingleSiteAnalysisDetailsPanel.REPLICATES);
				this.getSelReplicates().select(itemId);
				this.getSelReplicates().setEnabled(true);
			}
		}
	}

	protected void disableEnvironmentEntries() {

		this.setEnvironmentEntryValues(false);
		this.tblEnvironmentSelection.refreshRowCache();
	}

	protected void setEnvironmentEntryValues(final boolean value) {
		for (final Iterator<?> itr = this.tblEnvironmentSelection.getContainerDataSource().getItemIds().iterator(); itr.hasNext();) {
			final SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();
			m.setActive(value);

		}
	}

	protected int getTermId(final String localName, final List<DMSVariableType> list) {
		for (final DMSVariableType variable : list) {
			if (variable.getLocalName().equals(localName)) {
				return variable.getId();
			}
		}

		return 0;
	}

	protected List<String> getInvalidEnvironments() {

		final List<String> invalidEnvs = new ArrayList<>();

		for (final Iterator<?> itr = this.tblEnvironmentSelection.getContainerDataSource().getItemIds().iterator(); itr.hasNext();) {
			final SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();

			final int germplasmTermId = this.getTermId(this.selGenotypes.getValue().toString(), this.factorsInDataset);

			final Boolean valid =
					this.studyDataManager.containsAtLeast2CommonEntriesWithValues(this.getBreedingViewInput().getDatasetId(),
							m.getLocationId(), germplasmTermId);

			if (!valid) {
				invalidEnvs.add(m.getEnvironmentName());
				m.setActive(false);
			} else {
				m.setActive(true);
			}
		}

		this.tblEnvironmentSelection.refreshRowCache();

		return invalidEnvs;

	}
}
