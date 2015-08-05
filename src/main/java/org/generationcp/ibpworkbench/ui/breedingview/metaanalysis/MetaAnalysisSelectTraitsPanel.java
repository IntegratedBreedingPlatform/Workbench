
package org.generationcp.ibpworkbench.ui.breedingview.metaanalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.MetaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class MetaAnalysisSelectTraitsPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;
	private final List<MetaEnvironmentModel> metaEnvironments;
	private Map<Integer, DataSet> dataSets;
	private Map<Integer, TrialEnvironments> trialEnvironmentsList;
	private Map<String, Boolean> factorsCheckBoxState;
	private Map<String, Boolean> variatesCheckBoxState;
	private Table environmentsTable;
	private Table variatesSelectionTable;
	private Table factorsAnalysisTable;
	private Table factorsSelectionTable;

	private CheckBox chkSelectAllVariates;
	private CheckBox chkSelectAllFactors;
	private CheckBox chkSelectAllEnvironments;
	private Property.ValueChangeListener selectAllEnvironmentsListener;
	private Property.ValueChangeListener selectAllFactorsListener;
	private Property.ValueChangeListener selectAllTraitsListener;

	private Button btnBack;
	private Button btnReset;
	private Button btnNext;
	private Component buttonArea;

	private Label lblPageTitle;
	private Label lblSelectEnvVarForAnalysis;
	private Label lblSelectEnvVarForAnalysisDesc;
	private Label lblSelectVariates;
	private Label lblSelectFactorsForAnalysis;
	private Label lblSelectFactorsForAnalysisDesc;
	private Label lblSelectFactors;

	private final Project currentProject;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;


	private final MetaAnalysisPanel selectDatasetsForMetaAnalysisPanel;

	public MetaAnalysisSelectTraitsPanel(Project project, List<MetaEnvironmentModel> metaEnvironments,
			MetaAnalysisPanel selectDatasetsForMetaAnalysisPanel) {
		this.metaEnvironments = metaEnvironments;
		this.currentProject = project;
		this.selectDatasetsForMetaAnalysisPanel = selectDatasetsForMetaAnalysisPanel;
	}

	private void initializeComponents() {

		this.lblPageTitle = new Label();
		this.lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		this.factorsCheckBoxState = new HashMap<String, Boolean>();
		this.variatesCheckBoxState = new HashMap<String, Boolean>();

		this.environmentsTable = new Table();
		this.environmentsTable.setColumnCollapsingAllowed(true);

		this.selectAllEnvironmentsListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				for (Iterator<?> itr =
						MetaAnalysisSelectTraitsPanel.this.environmentsTable.getContainerDataSource().getItemIds().iterator(); itr
						.hasNext();) {
					MetaEnvironmentModel m = (MetaEnvironmentModel) itr.next();
					m.setActive((Boolean) event.getProperty().getValue());
				}

				MetaAnalysisSelectTraitsPanel.this.environmentsTable.refreshRowCache();

			}
		};

		this.selectAllFactorsListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				for (Iterator<?> itr = MetaAnalysisSelectTraitsPanel.this.factorsSelectionTable.getContainerPropertyIds().iterator(); itr
						.hasNext();) {
					Object propertyId = itr.next();
					CheckBox chk =
							(CheckBox) MetaAnalysisSelectTraitsPanel.this.factorsSelectionTable.getItem(1).getItemProperty(propertyId)
									.getValue();
					if (chk.isEnabled()) {
						chk.setValue(event.getProperty().getValue());
						MetaAnalysisSelectTraitsPanel.this.factorsCheckBoxState.put(propertyId.toString(), (Boolean) event.getProperty()
								.getValue());
					}

				}

			}
		};

		this.selectAllTraitsListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				for (Iterator<?> itr = MetaAnalysisSelectTraitsPanel.this.variatesSelectionTable.getContainerPropertyIds().iterator(); itr
						.hasNext();) {
					Object propertyId = itr.next();
					CheckBox chk =
							(CheckBox) MetaAnalysisSelectTraitsPanel.this.variatesSelectionTable.getItem(1).getItemProperty(propertyId)
									.getValue();
					chk.setValue(event.getProperty().getValue());
					MetaAnalysisSelectTraitsPanel.this.variatesCheckBoxState.put(propertyId.toString(), (Boolean) event.getProperty()
							.getValue());
				}

			}
		};

		this.chkSelectAllVariates = new CheckBox();
		this.chkSelectAllVariates.setImmediate(true);
		this.chkSelectAllVariates.setCaption("Select All Traits");
		this.chkSelectAllVariates.addListener(this.selectAllTraitsListener);
		this.chkSelectAllFactors = new CheckBox();
		this.chkSelectAllFactors.setImmediate(true);
		this.chkSelectAllFactors.setCaption("Select All Factors");
		this.chkSelectAllFactors.addListener(this.selectAllFactorsListener);
		this.chkSelectAllEnvironments = new CheckBox();
		this.chkSelectAllEnvironments.setImmediate(true);
		this.chkSelectAllEnvironments.setCaption("Select All Environments");
		this.chkSelectAllEnvironments.addListener(this.selectAllEnvironmentsListener);
		this.chkSelectAllEnvironments.setValue(true);

		this.lblSelectEnvVarForAnalysis = new Label();
		this.lblSelectEnvVarForAnalysis.setStyleName(Bootstrap.Typography.H4.styleName());
		this.lblSelectEnvVarForAnalysisDesc = new Label();
		this.lblSelectVariates = new Label();
		this.lblSelectFactorsForAnalysis = new Label();
		this.lblSelectFactorsForAnalysis.setStyleName(Bootstrap.Typography.H4.styleName());
		this.lblSelectFactorsForAnalysisDesc = new Label();
		this.lblSelectFactors = new Label();

		this.environmentsTable.setWidth("100%");
		this.factorsAnalysisTable = new Table();
		this.factorsAnalysisTable.setWidth("100%");
		this.factorsAnalysisTable.setColumnCollapsingAllowed(true);

		this.dataSets = new HashMap<Integer, DataSet>();
		this.trialEnvironmentsList = new HashMap<Integer, TrialEnvironments>();

		ColumnGenerator generatedVariateColumn = new ColumnGenerator() {

			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				String countData = "0";
				MetaEnvironmentModel item = (MetaEnvironmentModel) itemId;
				VariableType varType =
						MetaAnalysisSelectTraitsPanel.this.dataSets.get(item.getDataSetId()).findVariableTypeByLocalName(
								columnId.toString());

				if (varType == null) {
					return "0";
				}

				try {

					if (item.getDataSetTypeId() == DataSetType.MEANS_DATA.getId()) {
						countData =
								String.valueOf(studyDataManager.countStocks(
										item.getDataSetId(),
										MetaAnalysisSelectTraitsPanel.this.trialEnvironmentsList.get(item.getDataSetId())
												.findOnlyOneByLocalName(item.getTrialFactorName(), item.getTrial()).getId(),
										varType.getId()));
					} else {
						countData =
								String.valueOf(studyDataManager.countStocks(
										item.getDataSetId(),
										MetaAnalysisSelectTraitsPanel.this.trialEnvironmentsList.get(item.getDataSetId())
												.findOnlyOneByLocalName(item.getTrialFactorName(), item.getTrial()).getId(),
										varType.getId()));
					}

				} catch (MiddlewareQueryException e) {

					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return countData;
			}

		};

		ColumnGenerator generatedFactorColumn = new ColumnGenerator() {

			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {

				MetaEnvironmentModel item = (MetaEnvironmentModel) itemId;
				VariableType varType =
						MetaAnalysisSelectTraitsPanel.this.dataSets.get(item.getDataSetId()).findVariableTypeByLocalName(
								columnId.toString());
				if (varType == null) {
					return "";
				} else {
					return "X";
				}

			}

		};

		final Property.ValueChangeListener envCheckBoxListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 6946721935764963485L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean val = (Boolean) event.getProperty().getValue();
				CheckBox chk = (CheckBox) event.getProperty();
				((MetaEnvironmentModel) chk.getData()).setActive(val);
				if (val == false) {
					MetaAnalysisSelectTraitsPanel.this.chkSelectAllEnvironments
							.removeListener(MetaAnalysisSelectTraitsPanel.this.selectAllEnvironmentsListener);
					MetaAnalysisSelectTraitsPanel.this.chkSelectAllEnvironments.setValue(false);
					MetaAnalysisSelectTraitsPanel.this.chkSelectAllEnvironments
							.addListener(MetaAnalysisSelectTraitsPanel.this.selectAllEnvironmentsListener);
				}

			}

		};

		this.environmentsTable.addGeneratedColumn("", new ColumnGenerator() {

			private static final long serialVersionUID = -850728728803335183L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				MetaEnvironmentModel item = (MetaEnvironmentModel) itemId;

				CheckBox chk = new CheckBox();
				chk.setValue(item.getActive());
				chk.setData(itemId);
				chk.setImmediate(true);
				chk.addListener(envCheckBoxListener);

				return chk;
			}
		});

		HashSet<String> variatesColumnList = new HashSet<String>();
		HashMap<String, Boolean> factorsColumnList = new HashMap<String, Boolean>();
		for (MetaEnvironmentModel metaEnvironment : this.metaEnvironments) {
			if (this.dataSets.get(metaEnvironment.getDataSetId()) == null) {
				try {
					DataSet ds;
					TrialEnvironments envs;
					ds = studyDataManager.getDataSet(metaEnvironment.getDataSetId());
					envs = studyDataManager.getTrialEnvironmentsInDataset(ds.getId());
					this.dataSets.put(metaEnvironment.getDataSetId(), ds);
					this.trialEnvironmentsList.put(metaEnvironment.getDataSetId(), envs);

					for (VariableType v : ds.getVariableTypes().getVariates().getVariableTypes()) {
						try {
							this.environmentsTable.addGeneratedColumn(v.getLocalName(), generatedVariateColumn);
							variatesColumnList.add(v.getLocalName());
						} catch (Exception e) {
						}
					}

					for (VariableType f : ds.getVariableTypes().getFactors().getVariableTypes()) {
						if (f.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET) {
							continue;
						}

						Boolean isGidOrDesig = false;

						if (f.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_DESIGNATION_STORAGE.getId()
								|| f.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_GID_STORAGE.getId()) {
							isGidOrDesig = true;
						}

						try {
							this.factorsAnalysisTable.addGeneratedColumn(f.getLocalName(), generatedFactorColumn);
							factorsColumnList.put(f.getLocalName(), isGidOrDesig);
						} catch (Exception e) {
						}
					}

				} catch (MiddlewareQueryException e) {

					e.printStackTrace();
				}

			} else {
				continue;
			}
		}

		BeanItemContainer<MetaEnvironmentModel> environmentsTableContainer =
				new BeanItemContainer<MetaEnvironmentModel>(MetaEnvironmentModel.class);

		for (MetaEnvironmentModel metaEnvironment : this.metaEnvironments) {
			metaEnvironment.setActive(true);
			environmentsTableContainer.addBean(metaEnvironment);

		}
		this.environmentsTable.setContainerDataSource(environmentsTableContainer);
		List<String> visibleCols = new ArrayList<String>();
		visibleCols.add("");
		visibleCols.add("dataSetName");
		visibleCols.add("trial");
		visibleCols.add("environment");
		visibleCols.addAll(variatesColumnList);
		this.environmentsTable.setVisibleColumns(visibleCols.toArray());
		visibleCols.clear();
		visibleCols.add("SELECT");
		visibleCols.add("Dataset Name");
		visibleCols.add("Trial");
		visibleCols.add("Environment");
		visibleCols.addAll(variatesColumnList);
		this.environmentsTable.setColumnHeaders(visibleCols.toArray(new String[0]));

		BeanItemContainer<MetaEnvironmentModel> factorsAnalysisTableContainer =
				new BeanItemContainer<MetaEnvironmentModel>(MetaEnvironmentModel.class);
		for (MetaEnvironmentModel metaEnvironment : this.metaEnvironments) {
			factorsAnalysisTableContainer.addBean(metaEnvironment);

		}
		this.factorsAnalysisTable.setContainerDataSource(factorsAnalysisTableContainer);
		visibleCols.clear();
		visibleCols.add("dataSetName");
		visibleCols.add("trial");
		visibleCols.add("environment");
		for (Entry<String, Boolean> s : factorsColumnList.entrySet()) {
			visibleCols.add(s.getKey());
		}
		this.factorsAnalysisTable.setVisibleColumns(visibleCols.toArray());
		visibleCols.clear();
		visibleCols.add("Dataset Name");
		visibleCols.add("Trial");
		visibleCols.add("Environment");
		for (Entry<String, Boolean> s : factorsColumnList.entrySet()) {
			visibleCols.add(s.getKey());
		}
		this.factorsAnalysisTable.setColumnHeaders(visibleCols.toArray(new String[0]));

		Property.ValueChangeListener traitCheckBoxListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1572419094504976594L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean val = (Boolean) event.getProperty().getValue();
				CheckBox chk = (CheckBox) event.getProperty();
				MetaAnalysisSelectTraitsPanel.this.variatesCheckBoxState.put(chk.getData().toString(), val);
				if (val == false) {
					MetaAnalysisSelectTraitsPanel.this.chkSelectAllVariates
							.removeListener(MetaAnalysisSelectTraitsPanel.this.selectAllTraitsListener);
					MetaAnalysisSelectTraitsPanel.this.chkSelectAllVariates.setValue(false);
					MetaAnalysisSelectTraitsPanel.this.chkSelectAllVariates
							.addListener(MetaAnalysisSelectTraitsPanel.this.selectAllTraitsListener);
				}

			}

		};

		this.variatesSelectionTable = new Table();
		this.variatesSelectionTable.setWidth("100%");
		this.variatesSelectionTable.setHeight("80px");
		this.variatesSelectionTable.setColumnCollapsingAllowed(true);
		List<CheckBox> vCheckBoxes = new ArrayList<CheckBox>();
		IndexedContainer variatesSelectionTableContainer = new IndexedContainer();
		for (Object s : variatesColumnList.toArray()) {
			variatesSelectionTableContainer.addContainerProperty(s.toString(), CheckBox.class, null);
			CheckBox variateCheckBox = new CheckBox();
			variateCheckBox.setImmediate(true);
			variateCheckBox.addListener(traitCheckBoxListener);
			variateCheckBox.setData(s);
			vCheckBoxes.add(variateCheckBox);
			this.variatesCheckBoxState.put(s.toString(), false);
		}

		this.variatesSelectionTable.setContainerDataSource(variatesSelectionTableContainer);
		this.variatesSelectionTable.addItem(vCheckBoxes.toArray(), 1);

		Property.ValueChangeListener factorCheckBoxListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 456441415676960629L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean val = (Boolean) event.getProperty().getValue();
				CheckBox chk = (CheckBox) event.getProperty();
				MetaAnalysisSelectTraitsPanel.this.factorsCheckBoxState.put(chk.getData().toString(), val);
				if (val == false) {
					MetaAnalysisSelectTraitsPanel.this.chkSelectAllFactors
							.removeListener(MetaAnalysisSelectTraitsPanel.this.selectAllFactorsListener);
					MetaAnalysisSelectTraitsPanel.this.chkSelectAllFactors.setValue(false);
					MetaAnalysisSelectTraitsPanel.this.chkSelectAllFactors
							.addListener(MetaAnalysisSelectTraitsPanel.this.selectAllFactorsListener);
				}

			}

		};

		this.factorsSelectionTable = new Table();
		this.factorsSelectionTable.setWidth("100%");
		this.factorsSelectionTable.setHeight("80px");
		this.factorsSelectionTable.setColumnCollapsingAllowed(true);
		List<CheckBox> fCheckBoxes = new ArrayList<CheckBox>();
		IndexedContainer factorsSelectionTableContainer = new IndexedContainer();
		for (Entry<String, Boolean> s : factorsColumnList.entrySet()) {
			factorsSelectionTableContainer.addContainerProperty(s.getKey(), CheckBox.class, null);
			CheckBox factorCheckBox = new CheckBox();
			factorCheckBox.setImmediate(true);
			factorCheckBox.addListener(factorCheckBoxListener);
			factorCheckBox.setData(s.getKey());
			fCheckBoxes.add(factorCheckBox);
			this.factorsCheckBoxState.put(s.getKey(), false);

			if (s.getValue()) {// GID and DESIG factors are required
				factorCheckBox.setValue(true);
				factorCheckBox.setCaption("Required");
				factorCheckBox.setStyleName("gcp-required-caption");
				factorCheckBox.setEnabled(false);
				this.factorsCheckBoxState.put(s.getKey(), true);
			}

		}
		this.factorsSelectionTable.setContainerDataSource(factorsSelectionTableContainer);
		this.factorsSelectionTable.addItem(fCheckBoxes.toArray(), 1);

	}

	private void initializeLayout() {

		this.setSizeUndefined();
		this.setSpacing(true);
		this.setWidth("95%");

		VerticalLayout layout1 = new VerticalLayout();
		layout1.setMargin(new MarginInfo(false, true, false, true));
		layout1.setSpacing(true);
		layout1.addComponent(this.lblPageTitle);
		layout1.addComponent(this.lblSelectEnvVarForAnalysis);
		layout1.addComponent(this.lblSelectEnvVarForAnalysisDesc);
		layout1.addComponent(this.environmentsTable);
		layout1.addComponent(this.chkSelectAllEnvironments);
		this.addComponent(layout1);

		VerticalLayout layout2 = new VerticalLayout();
		layout2.setMargin(new MarginInfo(false, true, false, true));
		layout2.setSpacing(true);
		layout2.addComponent(this.lblSelectVariates);
		layout2.addComponent(this.variatesSelectionTable);
		layout2.addComponent(this.chkSelectAllVariates);
		this.addComponent(layout2);

		VerticalLayout layout3 = new VerticalLayout();
		layout3.setMargin(new MarginInfo(true, true, false, true));
		layout3.setSpacing(true);
		layout3.addComponent(this.lblSelectFactorsForAnalysis);
		layout3.addComponent(this.lblSelectFactorsForAnalysisDesc);
		layout3.addComponent(this.factorsAnalysisTable);
		this.addComponent(layout3);

		VerticalLayout layout4 = new VerticalLayout();
		layout4.setMargin(new MarginInfo(false, true, false, true));
		layout4.setSpacing(true);
		layout4.addComponent(this.lblSelectFactors);
		layout4.addComponent(this.factorsSelectionTable);
		layout4.addComponent(this.chkSelectAllFactors);
		this.addComponent(layout4);

		this.buttonArea = this.layoutButtonArea();
		this.addComponent(this.buttonArea);
		this.setComponentAlignment(this.buttonArea, Alignment.TOP_CENTER);

	}

	protected Component layoutButtonArea() {

		HorizontalLayout buttonLayout = new HorizontalLayout();

		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);

		this.btnBack = new Button();
		this.btnReset = new Button();
		this.btnNext = new Button();
		this.btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		buttonLayout.addComponent(this.btnBack);
		buttonLayout.addComponent(this.btnReset);
		buttonLayout.addComponent(this.btnNext);
		buttonLayout.setComponentAlignment(this.btnBack, Alignment.TOP_CENTER);
		buttonLayout.setComponentAlignment(this.btnNext, Alignment.TOP_CENTER);
		return buttonLayout;
	}

	protected void initializeActions() {
		this.btnBack.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				IContentWindow window = (IContentWindow) event.getComponent().getWindow();
				MetaAnalysisSelectTraitsPanel.this.selectDatasetsForMetaAnalysisPanel.setParent(null);
				window.showContent(MetaAnalysisSelectTraitsPanel.this.selectDatasetsForMetaAnalysisPanel);
			}
		});

		this.btnReset.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				MetaAnalysisSelectTraitsPanel.this.chkSelectAllVariates.setValue(true);
				MetaAnalysisSelectTraitsPanel.this.chkSelectAllVariates.setValue(false);

				MetaAnalysisSelectTraitsPanel.this.chkSelectAllFactors.setValue(true);
				MetaAnalysisSelectTraitsPanel.this.chkSelectAllFactors.setValue(false);

				MetaAnalysisSelectTraitsPanel.this.chkSelectAllEnvironments.setValue(true);
			}
		});

		this.btnNext.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -4809085840378185820L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (MetaAnalysisSelectTraitsPanel.this.variatesCheckBoxState.size() == 0
						|| MetaAnalysisSelectTraitsPanel.this.factorsCheckBoxState.size() == 0

				) {
					return;
				}

				final File file = MetaAnalysisSelectTraitsPanel.this.exportData();


				if (file == null) {
					return;
				}

				FileResource fr = new FileResource(file, event.getComponent().getWindow().getApplication()) {

					private static final long serialVersionUID = 765143030552676513L;

					@Override
					public DownloadStream getStream() {
						DownloadStream ds;
						try {
							ds = new DownloadStream(new FileInputStream(this.getSourceFile()), this.getMIMEType(), this.getFilename());

							ds.setParameter("Content-Disposition", "attachment; filename=" + file.getName());
							ds.setCacheTime(this.getCacheTime());
							return ds;

						} catch (FileNotFoundException e) {
							// No logging for non-existing files at this level.
							return null;
						}
					}
				};

				event.getComponent().getWindow().getApplication().getMainWindow().open(fr);
			}
		});

	}

	private File exportData() {

		Workbook workbook = new HSSFWorkbook();
		Sheet defaultSheet = workbook.createSheet("Merged DataSets");

		// Create Header Row
		int cellCounter = 0;
		int rowCounter = 0;
		Boolean headerRowCreated = false;
		List<String> supressColumnList = new ArrayList<String>();

		Row headerRow = defaultSheet.createRow(rowCounter++);

		Iterator<?> envIterator = this.environmentsTable.getItemIds().iterator();
		while (envIterator.hasNext()) {
			MetaEnvironmentModel envModel = (MetaEnvironmentModel) envIterator.next();

			if (envModel.getActive()) {

				try {

					String desigFactorName = "";
					String gidFactorName = "";
					String entrynoFactorName = "";

					List<Experiment> exps = studyDataManager.getExperiments(envModel.getDataSetId(), 0, Integer.MAX_VALUE);
					Experiment e = exps.get(0);
					if (e != null) {
						for (VariableType var : e.getFactors().getVariableTypes().getVariableTypes()) {
							if (var.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_DESIGNATION_STORAGE.getId()) {
								desigFactorName = var.getLocalName();
							} else if (var.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_GID_STORAGE.getId()) {
								gidFactorName = var.getLocalName();
							} else if (var.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_NUMBER_STORAGE.getId()) {
								entrynoFactorName = var.getLocalName();
							}
						}
					}

					for (Experiment exp : exps) {

						if (!headerRowCreated) {

							headerRow.createCell(cellCounter++).setCellValue("STUDYNAME");
							headerRow.createCell(cellCounter++).setCellValue("TRIALID");
							headerRow.createCell(cellCounter++).setCellValue("ENTRYID");
							if (desigFactorName != "") {
								headerRow.createCell(cellCounter++).setCellValue(desigFactorName);
							} else {
								headerRow.createCell(cellCounter++).setCellValue("DESIG");
							}
							if (gidFactorName != "") {
								headerRow.createCell(cellCounter++).setCellValue(gidFactorName);
							} else {
								headerRow.createCell(cellCounter++).setCellValue("GID");
							}
							supressColumnList.add(desigFactorName);
							supressColumnList.add(gidFactorName);
							for (Entry<String, Boolean> entry : this.factorsCheckBoxState.entrySet()) {
								// suppress the desig and gid columns
								if (supressColumnList.contains(entry.getKey())) {
									continue;
								}

								if (entry.getValue()) {
									headerRow.createCell(cellCounter++).setCellValue(entry.getKey());
								}
							}
							for (Entry<String, Boolean> entry : this.variatesCheckBoxState.entrySet()) {
								if (entry.getValue()) {
									headerRow.createCell(cellCounter++).setCellValue(entry.getKey());
								}
							}

							headerRowCreated = true;
						}// if header Row Created

						Variable trialVariable = exp.getFactors().findByLocalName(envModel.getTrialFactorName());
						if (trialVariable == null) {
							continue;
						}
						if (!trialVariable.getValue().equalsIgnoreCase(envModel.getTrial())) {
							continue;
						}

						cellCounter = 0;
						Row row = defaultSheet.createRow(rowCounter++);

						row.createCell(cellCounter++).setCellValue(envModel.getStudyName()); // STUDYNAME
						row.createCell(cellCounter++).setCellValue(String.format("%s-%s", envModel.getStudyId(), envModel.getTrial())); // TRIALID
						Variable varEntryNo = exp.getFactors().findByLocalName(entrynoFactorName); // //ENTRYID
						if (varEntryNo != null) {
							row.createCell(cellCounter++)
									.setCellValue(String.format("%s-%s", envModel.getStudyId(), varEntryNo.getValue()));
						} else {
							row.createCell(cellCounter++).setCellValue("");
						}
						Variable varDesig = exp.getFactors().findByLocalName(desigFactorName); // DESIG
						if (varDesig != null) {
							row.createCell(cellCounter++).setCellValue(varDesig.getValue());
						} else {
							row.createCell(cellCounter++).setCellValue("");
						}
						Variable varGid = exp.getFactors().findByLocalName(gidFactorName); // GID
						if (varGid != null) {
							row.createCell(cellCounter++).setCellValue(varGid.getValue());
						} else {
							row.createCell(cellCounter++).setCellValue("");
						}

						for (Entry<String, Boolean> entry : this.factorsCheckBoxState.entrySet()) {

							// suppress the desig and gid columns
							if (supressColumnList.contains(entry.getKey())) {
								continue;
							}

							if (entry.getValue()) {
								Variable var = exp.getFactors().findByLocalName(entry.getKey());
								String cellValue = "";
								if (var != null) {
									cellValue = var.getValue();
								}
								row.createCell(cellCounter++).setCellValue(cellValue);
							}

						}
						for (Entry<String, Boolean> entry : this.variatesCheckBoxState.entrySet()) {
							if (entry.getValue()) {
								Variable var = exp.getVariates().findByLocalName(entry.getKey());
								String cellValue = "";
								if (var != null) {
									cellValue = var.getValue();
								}
								row.createCell(cellCounter++).setCellValue(cellValue);
							}
						}

					}
				} catch (MiddlewareQueryException e) {

					e.printStackTrace();
				}

			}
		}// while

		try {
			// NOTE: Directory location is hardcoded to workspace/<projectId/breeding_view/input>
			String dir =
					"workspace" + File.separator + this.currentProject.getProjectName().toString() + File.separator + "breeding_view"
							+ File.separator + "input";
			new File(dir).mkdirs();
			File xlsFile = new File(dir + File.separator + "mergedDataSets.xls");
			FileOutputStream fos = new FileOutputStream(xlsFile);
			workbook.write(fos);
			fos.close();
			return xlsFile.getAbsoluteFile();

		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

	}

	@Override
	public void updateLabels() {

		this.messageSource.setCaption(this.btnBack, Message.BACK);
		this.messageSource.setCaption(this.btnReset, Message.RESET);
		this.messageSource.setCaption(this.btnNext, Message.EXPORT_DATA);
		this.messageSource.setValue(this.lblPageTitle, Message.TITLE_METAANALYSIS);
		this.messageSource.setValue(this.lblSelectEnvVarForAnalysis, Message.META_SELECT_ENV_VAR_FOR_ANALYSIS);
		this.messageSource.setValue(this.lblSelectEnvVarForAnalysisDesc, Message.META_SELECT_ENV_VAR_FOR_ANALYSIS_DESC);
		this.messageSource.setValue(this.lblSelectVariates, Message.META_SELECT_VARIATES);
		this.messageSource.setValue(this.lblSelectFactorsForAnalysis, Message.META_SELECT_FACTORS_FOR_ANALYSIS);
		this.messageSource.setValue(this.lblSelectFactorsForAnalysisDesc, Message.META_SELECT_FACTORS_FOR_ANALYSIS_DESC);
		this.messageSource.setValue(this.lblSelectFactors, Message.META_SELECT_FACTORS);

	}

	private void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	@Override
	public void attach() {
		super.attach();

		this.updateLabels();
	}

}
