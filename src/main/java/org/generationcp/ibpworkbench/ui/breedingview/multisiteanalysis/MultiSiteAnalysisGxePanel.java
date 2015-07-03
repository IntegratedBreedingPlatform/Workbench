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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.RunMultiSiteAction;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Aldrin Batac
 *
 */
@Configurable
public class MultiSiteAnalysisGxePanel extends VerticalLayout implements InitializingBean, InternationalizableComponent, IBPWorkbenchLayout {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(MultiSiteAnalysisGxePanel.class);

	private GxeTable gxeTable;

	private Table selectTraitsTable;

	private Integer currentRepresentationId;

	private Integer currentDataSetId;

	private String currentDatasetName;

	private Button btnBack;
	private Button btnReset;
	private Button btnRunMultiSite;
	private Map<String, Boolean> variatesCheckboxState;
	private final MultiSiteAnalysisSelectPanel gxeSelectEnvironmentPanel;

	private List<DataSet> ds;

	@Value("${workbench.is.server.app}")
	private String isServerApp;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ToolUtil toolUtil;

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private StudyDataManager studyDataManager;

	private ManagerFactory managerFactory;

	private final MultiSiteParameters multiSiteParameters;

	private Label lblDataSelectedForAnalysisHeader;
	private Label lblDatasetName;
	private Label txtDatasetName;
	private Label lblDatasourceName;
	private Label txtDatasourceName;
	private Label lblSelectedEnvironmentFactor;
	private Label txtSelectedEnvironmentFactor;
	private Label lblSelectedEnvironmentGroupFactor;
	private Label txtSelectedEnvironmentGroupFactor;
	private Label lblAdjustedMeansHeader;
	private Label lblAdjustedMeansDescription;
	private Label lblSelectTraitsForAnalysis;
	private CheckBox chkSelectAllEnvironments;
	private CheckBox chkSelectAllTraits;
	private Property.ValueChangeListener selectAllEnvironmentsListener;
	private Property.ValueChangeListener selectAllTraitsListener;

	public MultiSiteAnalysisGxePanel(StudyDataManager studyDataManager, MultiSiteAnalysisSelectPanel gxeSelectEnvironmentPanel,
			Map<String, Boolean> variatesCheckboxState, MultiSiteParameters multiSiteParameters) {
		this.studyDataManager = studyDataManager;
		this.gxeSelectEnvironmentPanel = gxeSelectEnvironmentPanel;
		this.variatesCheckboxState = variatesCheckboxState;
		this.multiSiteParameters = multiSiteParameters;
		this.setCaption(multiSiteParameters.getStudy().getName());
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
		this.messageSource.setCaption(this.btnBack, Message.BACK);
		this.messageSource.setCaption(this.btnReset, Message.RESET);
		if (Boolean.parseBoolean(this.isServerApp)) {
			this.messageSource.setCaption(this.btnRunMultiSite, Message.DOWNLOAD_INPUT_FILES);
		} else {
			this.messageSource.setCaption(this.btnRunMultiSite, Message.LAUNCH_BREEDING_VIEW);
		}

		this.messageSource.setValue(this.lblDataSelectedForAnalysisHeader, Message.GXE_SELECTED_INFO);
		this.messageSource.setValue(this.lblDatasetName, Message.BV_DATASET_NAME);
		this.messageSource.setValue(this.lblDatasourceName, Message.BV_DATASOURCE_NAME);
		this.messageSource.setValue(this.lblSelectedEnvironmentFactor, Message.GXE_SELECTED_ENVIRONMENT_FACTOR);
		this.messageSource.setValue(this.lblSelectedEnvironmentGroupFactor, Message.GXE_SELECTED_ENVIRONMENT_GROUP_FACTOR);
		this.messageSource.setValue(this.lblAdjustedMeansDescription, Message.GXE_ADJUSTED_MEANS_DESCRIPTION);
		this.messageSource.setValue(this.lblSelectTraitsForAnalysis, Message.GXE_SELECT_TRAITS_FOR_ANALYSIS);
	}

	@Override
	public void instantiateComponents() {

		this.managerFactory = this.managerFactoryProvider.getManagerFactoryForProject(this.multiSiteParameters.getProject());

		this.lblDataSelectedForAnalysisHeader = new Label();
		this.lblDataSelectedForAnalysisHeader.setStyleName(Bootstrap.Typography.H2.styleName());

		this.lblDatasetName = new Label();
		this.lblDatasetName.setStyleName("label-bold");

		this.txtDatasetName = new Label();

		this.lblDatasourceName = new Label();
		this.lblDatasourceName.setStyleName("label-bold");

		this.txtDatasourceName = new Label();

		this.lblSelectedEnvironmentFactor = new Label();
		this.lblSelectedEnvironmentFactor.setStyleName("label-bold");

		this.txtSelectedEnvironmentFactor = new Label();

		this.lblSelectedEnvironmentGroupFactor = new Label();
		this.lblSelectedEnvironmentGroupFactor.setStyleName("label-bold");

		this.txtSelectedEnvironmentGroupFactor = new Label();

		this.chkSelectAllEnvironments = new CheckBox("Select all environments", true);
		this.chkSelectAllEnvironments.setImmediate(true);

		this.chkSelectAllTraits = new CheckBox("Select all traits", true);
		this.chkSelectAllTraits.setImmediate(true);

		this.lblAdjustedMeansHeader =
				new Label("<span class='bms-dataset' style='position:relative; top: -1px; color: #FF4612; "
						+ "font-size: 22px; font-weight: bold;'></span><b>&nbsp;"
						+ this.messageSource.getMessage(Message.GXE_ADJUSTED_MEANS_HEADER) + "</b>", Label.CONTENT_XHTML);
		this.lblAdjustedMeansHeader.setStyleName(Bootstrap.Typography.H2.styleName());

		this.lblAdjustedMeansDescription = new Label();

		this.lblSelectTraitsForAnalysis = new Label();

		this.btnRunMultiSite = new Button();
		this.btnBack = new Button();
		this.btnReset = new Button();

	}

	@Override
	public void initializeValues() {
		this.ds = null;
		try {
			this.ds = this.studyDataManager.getDataSetsByType(this.multiSiteParameters.getStudy().getId(), DataSetType.MEANS_DATA);
		} catch (MiddlewareException e) {
			MultiSiteAnalysisGxePanel.LOG.error("Error getting means dataset", e);
		}

		if (this.ds != null && !this.ds.isEmpty()) {
			this.setCaption(this.ds.get(0).getName());
			this.txtDatasetName.setValue(this.ds.get(0).getName());
			this.txtDatasourceName.setValue(this.multiSiteParameters.getStudy().getName());
			this.txtSelectedEnvironmentFactor.setValue(this.multiSiteParameters.getSelectedEnvironmentFactorName());
			this.txtSelectedEnvironmentGroupFactor.setValue(this.multiSiteParameters.getSelectedEnvGroupFactorName());

			Property.ValueChangeListener envCheckBoxListener = new Property.ValueChangeListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					Boolean val = (Boolean) event.getProperty().getValue();
					if (!val) {
						MultiSiteAnalysisGxePanel.this.chkSelectAllEnvironments
								.removeListener(MultiSiteAnalysisGxePanel.this.selectAllEnvironmentsListener);
						MultiSiteAnalysisGxePanel.this.chkSelectAllEnvironments.setValue(false);
						MultiSiteAnalysisGxePanel.this.chkSelectAllEnvironments
								.addListener(MultiSiteAnalysisGxePanel.this.selectAllEnvironmentsListener);
					}

				}

			};

			this.setGxeTable(new GxeTable(this.studyDataManager, this.multiSiteParameters.getStudy().getId(), this.multiSiteParameters
					.getSelectedEnvironmentFactorName(), this.multiSiteParameters.getSelectedEnvGroupFactorName(),
					this.variatesCheckboxState, envCheckBoxListener));
			this.getGxeTable().setHeight("300px");
		}

		this.selectTraitsTable = new Table();
		IndexedContainer container = new IndexedContainer();

		Property.ValueChangeListener traitCheckBoxListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = -1109780465477901066L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean val = (Boolean) event.getProperty().getValue();

				if (val == false) {
					MultiSiteAnalysisGxePanel.this.chkSelectAllTraits
							.removeListener(MultiSiteAnalysisGxePanel.this.selectAllTraitsListener);
					MultiSiteAnalysisGxePanel.this.chkSelectAllTraits.setValue(false);
					MultiSiteAnalysisGxePanel.this.chkSelectAllTraits.addListener(MultiSiteAnalysisGxePanel.this.selectAllTraitsListener);
				}

			}

		};

		List<CheckBox> cells = new ArrayList<CheckBox>();
		List<String> columnNames = new ArrayList<String>();

		SortedSet<String> keys = new TreeSet<String>(this.getVariatesCheckboxState().keySet());
		for (String key : keys) {
			if (this.getVariatesCheckboxState().get(key)) {
				container.addContainerProperty(key, CheckBox.class, null);
				columnNames.add(key.replace("_Means", ""));
				CheckBox chk = new CheckBox("", true);
				chk.setImmediate(true);
				chk.addListener(traitCheckBoxListener);
				cells.add(chk);
			}

		}

		this.selectTraitsTable.setContainerDataSource(container);
		this.selectTraitsTable.addItem(cells.toArray(new Object[0]), 1);
		this.selectTraitsTable.setHeight("80px");
		this.selectTraitsTable.setWidth("100%");
		this.selectTraitsTable.setColumnHeaders(columnNames.toArray(new String[0]));
		this.selectTraitsTable.setColumnCollapsingAllowed(true);
		for (Entry<String, Boolean> trait : this.getVariatesCheckboxState().entrySet()) {
			if (trait.getValue()) {
				this.selectTraitsTable.setColumnWidth(trait.getKey(), 100);
			}
		}
	}

	@Override
	public void addListeners() {
		this.selectAllEnvironmentsListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				for (Iterator<?> itr = MultiSiteAnalysisGxePanel.this.gxeTable.getItemIds().iterator(); itr.hasNext();) {
					CheckBox chk = (CheckBox) MultiSiteAnalysisGxePanel.this.gxeTable.getItem(itr.next()).getItemProperty(" ").getValue();
					chk.setValue(event.getProperty().getValue());
				}

			}
		};

		this.selectAllTraitsListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				for (Iterator<?> itr = MultiSiteAnalysisGxePanel.this.selectTraitsTable.getContainerPropertyIds().iterator(); itr.hasNext();) {
					CheckBox chk =
							(CheckBox) MultiSiteAnalysisGxePanel.this.selectTraitsTable.getItem(1).getItemProperty(itr.next()).getValue();
					chk.setValue(event.getProperty().getValue());
				}

			}
		};

		// Generate Buttons
		this.btnRunMultiSite.addListener(new RunMultiSiteAction(this.managerFactory, this.studyDataManager, this.gxeTable,
				this.selectTraitsTable, this.multiSiteParameters));

		this.btnBack.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				TabSheet tabSheet =
						MultiSiteAnalysisGxePanel.this.gxeSelectEnvironmentPanel.getGxeAnalysisComponentPanel().getStudiesTabsheet();
				tabSheet.replaceComponent(tabSheet.getSelectedTab(), MultiSiteAnalysisGxePanel.this.gxeSelectEnvironmentPanel);

			}
		});

		this.btnReset.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				MultiSiteAnalysisGxePanel.this.chkSelectAllTraits.setValue(true);
				MultiSiteAnalysisGxePanel.this.chkSelectAllEnvironments.setValue(true);
			}
		});

		this.chkSelectAllEnvironments.addListener(this.selectAllEnvironmentsListener);

		this.chkSelectAllTraits.addListener(this.selectAllTraitsListener);
	}

	@Override
	public void layoutComponents() {
		this.setWidth("100%");
		this.setSpacing(true);
		this.setMargin(true);

		GridLayout selectedInfoLayout = new GridLayout(4, 3);
		selectedInfoLayout.setSizeUndefined();
		selectedInfoLayout.setSpacing(true);
		selectedInfoLayout.setMargin(false, false, true, false);
		selectedInfoLayout.setColumnExpandRatio(0, 1);
		selectedInfoLayout.setColumnExpandRatio(1, 3);
		selectedInfoLayout.setColumnExpandRatio(2, 2);
		selectedInfoLayout.setColumnExpandRatio(3, 1);
		selectedInfoLayout.addComponent(this.lblDatasetName, 0, 1);
		selectedInfoLayout.addComponent(this.txtDatasetName, 1, 1);
		selectedInfoLayout.addComponent(this.lblDatasourceName, 0, 2);
		selectedInfoLayout.addComponent(this.txtDatasourceName, 1, 2);
		selectedInfoLayout.addComponent(this.lblSelectedEnvironmentFactor, 2, 1);
		selectedInfoLayout.addComponent(this.txtSelectedEnvironmentFactor, 3, 1);
		selectedInfoLayout.addComponent(this.lblSelectedEnvironmentGroupFactor, 2, 2);
		selectedInfoLayout.addComponent(this.txtSelectedEnvironmentGroupFactor, 3, 2);

		this.addComponent(this.lblDataSelectedForAnalysisHeader);
		this.addComponent(selectedInfoLayout);

		this.addComponent(this.lblAdjustedMeansHeader);
		this.addComponent(this.lblAdjustedMeansDescription);
		this.addComponent(this.getGxeTable());
		this.addComponent(this.chkSelectAllEnvironments);
		this.setExpandRatio(this.getGxeTable(), 1.0F);

		// hack, just wanna add space here
		this.addComponent(new Label("<br/>", Label.CONTENT_XHTML));

		this.addComponent(this.lblSelectTraitsForAnalysis);
		this.addComponent(this.selectTraitsTable);
		this.setExpandRatio(this.selectTraitsTable, 1.0F);
		this.addComponent(this.chkSelectAllTraits);

		this.addComponent(this.layoutButtonArea());
	}

	protected Component layoutButtonArea() {
		HorizontalLayout buttonLayout = new HorizontalLayout();

		buttonLayout.setSizeFull();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);

		this.btnRunMultiSite.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		Label spacer = new Label("&nbsp;", Label.CONTENT_XHTML);
		spacer.setSizeFull();

		buttonLayout.addComponent(spacer);
		buttonLayout.setExpandRatio(spacer, 1.0F);
		buttonLayout.addComponent(this.btnBack);
		buttonLayout.addComponent(this.btnReset);
		buttonLayout.addComponent(this.btnRunMultiSite);

		return buttonLayout;
	}

	@Override
	public Object getData() {
		return this.multiSiteParameters.getStudy();

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

	public StudyDataManager getStudyDataManager() {
		if (this.studyDataManager == null) {
			this.studyDataManager = this.managerFactory.getNewStudyDataManager();
		}
		return this.studyDataManager;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return this.variatesCheckboxState;
	}

	public void setVariatesCheckboxState(Map<String, Boolean> hashMap) {
		this.variatesCheckboxState = hashMap;
	}

	public GxeTable getGxeTable() {
		return this.gxeTable;
	}

	public void setGxeTable(GxeTable gxeTable) {
		this.gxeTable = gxeTable;
	}

}
