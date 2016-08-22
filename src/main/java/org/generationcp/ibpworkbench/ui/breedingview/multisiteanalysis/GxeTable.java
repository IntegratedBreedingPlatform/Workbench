
package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.commons.sea.xml.Environment;
import org.generationcp.ibpworkbench.util.TableItems;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.util.DatasetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class GxeTable extends Table {

	private static final String LS_BLUPS = "ls blups";
	private static final String ERROR_ESTIMATE = "error estimate";
	private static final Logger LOG = LoggerFactory.getLogger(GxeTable.class);
	private static final long serialVersionUID = 1274131837702381485L;

	public static final int CELL_CHECKBOX = 1;
	public static final int CELL_CHECKBOX_HEADER = 2;
	public static final int CELL_CHECKBOX_COLUMN = 3;
	public static final int CELL_CHECKBOX_ROW = 4;
	public static final int CELL_CHECKBOX_ALL = 5;
	public static final int CELL_LABEL = 6;

	private final StudyDataManager studyDataManager;
	private final List<String> columnNames = new ArrayList<String>();
	private final Map<Integer, String> factorLocalNames = new TreeMap<Integer, String>();
	private final Map<Integer, String> variateLocalNames = new TreeMap<Integer, String>();
	private Map<String, Boolean> variatesCheckBoxState = new HashMap<String, Boolean>();

	private Map<String, Map<String, String>> heritabilityValues = new HashMap<String, Map<String, String>>();

	private String trialInstanceFactorName = "";

	private String selectedEnvFactorName = "";

	private String selectedEnvGroupFactorName = "";

	private int meansDataSetId;

	private DataSet meansDataSet;

	private List<Experiment> exps;

	private final VariableTypeList germplasmFactors = new VariableTypeList();

	private final Property.ValueChangeListener gxeCheckBoxColumnListener;

	public GxeTable(StudyDataManager studyDataManager, Integer studyId, String selectedEnvFactorName, String selectedEnvGroupFactorName,
			Map<String, Boolean> variatesCheckBoxState, Property.ValueChangeListener gxeCheckBoxColumnListener) {
		this.selectedEnvFactorName = selectedEnvFactorName;
		this.selectedEnvGroupFactorName = selectedEnvGroupFactorName;
		this.studyDataManager = studyDataManager;
		this.variatesCheckBoxState = variatesCheckBoxState;
		this.gxeCheckBoxColumnListener = gxeCheckBoxColumnListener;
		this.initializeTable();
		this.fillTableWithDataset(studyId);
	}

	private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				return e1.getValue().compareTo(e2.getValue());
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	private void initializeTable() {

		this.setImmediate(true);
		this.setWidth("100%");
		this.setHeight("453px");
		this.setContainerDataSource(new IndexedContainer());
		this.setEditable(true);
		this.setColumnReorderingAllowed(true);
		this.setSortDisabled(true);
		this.setColumnCollapsingAllowed(true);

	}

	private void initializeHeader(Map<Integer, String> factors, Map<Integer, String> variates, Container container) {

		List<String> factorsList = new ArrayList<String>(factors.values());
		List<String> variatesList = new ArrayList<String>(variates.values());

		Collections.sort(variatesList);

		this.columnNames.add(" ");
		this.columnNames.addAll(factorsList);
		this.columnNames.addAll(variatesList);

		List<String> columnHeaders = new ArrayList<String>();
		for (String s : this.columnNames) {
			columnHeaders.add(s.replace("_Means", ""));
		}

		this.setVisibleColumns(this.columnNames.toArray(new Object[0]));
		this.setColumnHeaders(columnHeaders.toArray(new String[0]));

	}

	private void createRow(int rowIndex, TableItems[] tableItems, Container container) {

		Object[] obj = new Object[tableItems.length];

		for (int i = 0; i < tableItems.length; i++) {

			if (tableItems[i].getType() == GxeTable.CELL_CHECKBOX) {

				CheckBox cb = new CheckBox();
				cb.setDebugId("cb");
				cb.setCaption(tableItems[i].getLabel());
				cb.setValue(tableItems[i].getValue());
				cb.setImmediate(true);
				cb.addListener(this.getGxeCheckBoxColumnListener());

				obj[i] = cb;
			} else if (tableItems[i].getType() == GxeTable.CELL_LABEL) {
				obj[i] = tableItems[i].getLabel();
			}

		}

		this.addItem(obj, rowIndex);

	}

	protected void fillTableWithDataset(Integer studyId) {

		Container container = this.getContainerDataSource();
		container.removeAllItems();

		container.addContainerProperty(" ", CheckBox.class, null);

		this.setHeritabilityValues(this.getHeribilityValuesFromPlotDataSet(studyId));

		Set<String> envNames = new HashSet<String>();

		try {

			List<DataSet> meansDataSets = this.studyDataManager.getDataSetsByType(studyId, DataSetType.MEANS_DATA);
			if (meansDataSets != null && !meansDataSets.isEmpty()) {

				this.meansDataSet = meansDataSets.get(0);
				this.meansDataSetId = this.meansDataSet.getId();

				TrialEnvironments envs = this.studyDataManager.getTrialEnvironmentsInDataset(this.meansDataSetId);
				// get the SITE NAME and SITE NO

				DataSet trialDataSet = DatasetUtil.getTrialDataSet(this.studyDataManager, studyId);
				VariableTypeList trialEnvFactors = trialDataSet.getVariableTypes().getFactors();

				for (DMSVariableType factor : trialEnvFactors.getVariableTypes()) {

					this.addNecessaryFactorsToContainer(factor, container);

				}

				this.germplasmFactors.addAll(this.meansDataSet.getFactorsByPhenotypicType(PhenotypicType.GERMPLASM));
				// get the Variates
				VariableTypeList variates = this.meansDataSet.getVariableTypes().getVariates();
				for (DMSVariableType v : variates.getVariableTypes()) {
					container.addContainerProperty(v.getLocalName(), Label.class, null);
					if (!v.getStandardVariable().getMethod().getName().equalsIgnoreCase(GxeTable.ERROR_ESTIMATE)
							&& !v.getStandardVariable().getMethod().getName()
									.equalsIgnoreCase("error estimate (" + v.getLocalName().replace("_UnitErrors", "") + ")")
							&& !v.getStandardVariable().getMethod().getName().equalsIgnoreCase(GxeTable.LS_BLUPS)
							&& this.getVariatesCheckBoxState().get(v.getLocalName())) {

						this.variateLocalNames.put(v.getRank(), v.getLocalName());

					}
				}

				this.initializeHeader(this.factorLocalNames, this.variateLocalNames, container);

				// generate the rows
				this.exps =
						this.studyDataManager.getExperimentsWithTrialEnvironment(trialDataSet.getId(), this.meansDataSetId, 0,
								Integer.MAX_VALUE);

				int rowCounter = 3;

				for (Experiment exp : this.exps) {

					String locationValTrial = exp.getFactors().findByLocalName(this.trialInstanceFactorName).getValue();
					String locationVal = exp.getFactors().findByLocalName(this.selectedEnvFactorName).getValue();
					if (envNames.contains(locationVal)) {
						continue;
					}

					TableItems[] row = new TableItems[this.factorLocalNames.size() + this.variateLocalNames.size() + 1];

					row[0] = new TableItems();
					row[0].setType(GxeTable.CELL_CHECKBOX);
					row[0].setLabel(" ");
					row[0].setValue(true);

					int cellCounter = 1;
					int colIndexEnvFactorName = 0;

					for (Map.Entry<Integer, String> f : this.factorLocalNames.entrySet()) {
						String fValue = exp.getFactors().findByLocalName(f.getValue()).getValue();
						if (f.getValue().equalsIgnoreCase(this.selectedEnvFactorName)) {
							envNames.add(fValue);
							colIndexEnvFactorName = cellCounter;
						}
						row[cellCounter] = new TableItems();
						row[cellCounter].setLabel(fValue);
						row[cellCounter].setType(GxeTable.CELL_LABEL);
						cellCounter++;
					}

					for (Iterator<Entry<Integer, String>> v = GxeTable.entriesSortedByValues(this.variateLocalNames).iterator(); v
							.hasNext();) {

						Entry<Integer, String> x = v.next();

						row[cellCounter] = new TableItems();
						Variable var = exp.getVariates().findByLocalName(x.getValue());
						int varKey = 0;
						if (var != null) {
							varKey = var.getVariableType().getId();
						}
						String meansData = "";
						meansData =
								this.getMeansData(this.meansDataSetId, envs, this.selectedEnvFactorName,
										row[colIndexEnvFactorName].getLabel(), varKey);

						String heritabilityVal = this.getHeritabilityValues().get(locationValTrial).get(x.getValue());
						if (heritabilityVal != null) {
							meansData = String.format("%s (%s)", meansData, heritabilityVal);
						}

						row[cellCounter].setLabel(meansData);
						row[cellCounter].setValue(true);
						row[cellCounter].setType(GxeTable.CELL_LABEL);
						cellCounter++;
					}

					rowCounter++;
					this.createRow(rowCounter, row, container);
				}

			}

		} catch (MiddlewareException e) {
			GxeTable.LOG.error(e.getMessage(), e);
		}

	}

	protected Map<String, Map<String, String>> getHeribilityValuesFromPlotDataSet(int studyId) {
		List<DataSet> plotDatasets = new ArrayList<DataSet>();
		Map<String, Map<String, String>> heritabilityValues = new HashMap<>();

		try {

			List<DatasetReference> datasetRefs = this.studyDataManager.getDatasetReferences(studyId);
			for (DatasetReference dsRef : datasetRefs) {
				DataSet ds = this.studyDataManager.getDataSet(dsRef.getId());

				if (ds.getDataSetType() != DataSetType.MEANS_DATA) {

					Iterator<DMSVariableType> itrFactor = ds.getVariableTypes().getFactors().getVariableTypes().iterator();
					while (itrFactor.hasNext()) {
						DMSVariableType f = itrFactor.next();
						if (f.getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
							this.trialInstanceFactorName = f.getLocalName();
						}
					}

					Iterator<DMSVariableType> itrVariates = ds.getVariableTypes().getVariates().getVariableTypes().iterator();
					while (itrVariates.hasNext()) {
						if (itrVariates.next().getLocalName().contains("_Heritability")) {
							plotDatasets.add(ds);
						}
					}

				}

			}

			if (!plotDatasets.isEmpty()) {
				this.exps = this.studyDataManager.getExperiments(plotDatasets.get(0).getId(), 0, Integer.MAX_VALUE);
				for (Experiment exp : this.exps) {

					String envName = exp.getFactors().findByLocalName(this.trialInstanceFactorName).getValue();

					Map<String, String> vals = new HashMap<String, String>();

					for (Entry<String, Boolean> entry : this.getVariatesCheckBoxState().entrySet()) {
						String name = entry.getKey().replace("_Means", "_Heritability");
						Variable var = exp.getVariates().findByLocalName(name);
						if (var != null) {
							// heritability value
							vals.put(entry.getKey(), var.getValue().toString());
						}

					}

					heritabilityValues.put(envName, vals);

				}
			}

		} catch (MiddlewareException e1) {
			GxeTable.LOG.error(e1.getMessage(), e1);
		}

		return heritabilityValues;
	}

	protected String getMeansData(int meansDataSetId, TrialEnvironments envs, String envFactorName, String envName, int varKey) {
		try {
			return String.valueOf(this.studyDataManager.countStocks(meansDataSetId, envs.findOnlyOneByLocalName(envFactorName, envName)
					.getId(), varKey));
		} catch (MiddlewareQueryException e) {
			GxeTable.LOG.error(e.getMessage(), e);
			return "";
		}

	}

	protected void addNecessaryFactorsToContainer(DMSVariableType factor, Container container) {
		// Always Show the TRIAL INSTANCE Factor
		if (factor.getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
			container.addContainerProperty(factor.getLocalName(), Label.class, "");
			this.factorLocalNames.put(factor.getRank(), factor.getLocalName());
		}

		// Selected Environment Name
		if (factor.getLocalName().equalsIgnoreCase(this.selectedEnvFactorName)) {
			container.addContainerProperty(factor.getLocalName(), Label.class, "");
			this.factorLocalNames.put(factor.getRank(), factor.getLocalName());
		}

		// Selected Environment Group Name
		if (factor.getLocalName().equalsIgnoreCase(this.selectedEnvGroupFactorName)) {
			container.addContainerProperty(factor.getLocalName(), Label.class, "");
			this.factorLocalNames.put(factor.getRank(), factor.getLocalName());
		}
	}

	public GxeEnvironment getGxeEnvironment() {
		GxeEnvironment gxeEnvironment = new GxeEnvironment();
		List<GxeEnvironmentLabel> environmentLabels = new ArrayList<GxeEnvironmentLabel>();

		Object[] obj = this.getContainerDataSource().getItemIds().toArray();

		for (Integer i = 0; i < obj.length; i++) {
			Property cbColumn = this.getContainerProperty(obj[i], " ");
			Property locationColumn = this.getContainerProperty(obj[i], this.selectedEnvFactorName);
			if ((Boolean) ((CheckBox) cbColumn.getValue()).getValue()) {
				GxeEnvironmentLabel environmentLabel = new GxeEnvironmentLabel();
				environmentLabel.setName(((Label) locationColumn.getValue()).getValue().toString());
				environmentLabel.setActive(true);
				environmentLabels.add(environmentLabel);
			}
		}

		gxeEnvironment.setLabel(environmentLabels);

		return gxeEnvironment;

	}

	public List<Environment> getSelectedEnvironments() {
		List<Environment> selectedEnvironments = new ArrayList<Environment>();

		Object[] obj = this.getContainerDataSource().getItemIds().toArray();

		for (Integer i = 0; i < obj.length; i++) {
			Property cbColumn = this.getContainerProperty(obj[i], " ");
			Property locationColumn = this.getContainerProperty(obj[i], this.selectedEnvFactorName);
			Property trialNoColumn = this.getContainerProperty(obj[i], this.trialInstanceFactorName);
			if ((Boolean) ((CheckBox) cbColumn.getValue()).getValue()) {
				Environment environment = new Environment();
				environment.setName(((Label) locationColumn.getValue()).getValue().toString());
				environment.setActive(true);
				environment.setTrialno(((Label) trialNoColumn.getValue()).getValue().toString());
				selectedEnvironments.add(environment);
			}
		}

		return selectedEnvironments;

	}

	public int getMeansDataSetId() {
		return this.meansDataSetId;
	}

	public DataSet getMeansDataSet() {
		return this.meansDataSet;
	}

	public List<Experiment> getExperiments() {
		return this.exps;
	}

	public String getEnvironmentName() {
		return this.selectedEnvFactorName;
	}

	public List<DMSVariableType> getGermplasmFactors() {
		return this.germplasmFactors.getVariableTypes();
	}

	public VariableTypeList getEntryCodeFactor() {
		return this.meansDataSet.getFactorsByProperty(TermId.ENTRY_NUMBER_STORAGE.getId());
	}

	public Map<String, Boolean> getVariatesCheckBoxState() {
		return this.variatesCheckBoxState;
	}

	public void setVariatesCheckBoxState(Map<String, Boolean> variatesCheckBoxState) {
		this.variatesCheckBoxState = variatesCheckBoxState;
	}

	public Map<String, Map<String, String>> getHeritabilityValues() {
		return this.heritabilityValues;
	}

	public void setHeritabilityValues(Map<String, Map<String, String>> heritabilityValues) {
		this.heritabilityValues = heritabilityValues;
	}

	protected void setSelectedEnvFactorName(String selectedEnvFactorName) {
		this.selectedEnvFactorName = selectedEnvFactorName;
	}

	protected void setSelectedEnvGroupFactorName(String selectedEnvGroupFactorName) {
		this.selectedEnvGroupFactorName = selectedEnvGroupFactorName;
	}

	protected Property.ValueChangeListener getGxeCheckBoxColumnListener() {
		return this.gxeCheckBoxColumnListener;
	}
}
