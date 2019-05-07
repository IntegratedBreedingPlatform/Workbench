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
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.util.DatasetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.*;
import java.util.Map.Entry;

@Configurable
public class GxeTable extends Table implements InitializingBean {

	private static final String LS_BLUPS = "ls blups";
	private static final String ERROR_ESTIMATE = "error estimate";
	private static final Logger LOG = LoggerFactory.getLogger(GxeTable.class);
	private static final long serialVersionUID = 1274131837702381485L;

	public static final int CELL_CHECKBOX = 1;
	public static final int CELL_LABEL = 6;

	@Autowired
	private StudyDataManager studyDataManager;

	private final List<String> columnNames = new ArrayList<>();
	private final Map<Integer, String> factorLocalNames = new TreeMap<>();
	private final Map<Integer, String> variateLocalNames = new TreeMap<>();
	private Map<String, Boolean> variatesCheckBoxState = new HashMap<>();

	private Map<String, Map<String, String>> heritabilityValues = new HashMap<>();

	private final Integer studyId;

	private String trialInstanceFactorName = "";

	private String selectedEnvFactorName = "";

	private String selectedEnvGroupFactorName = "";

	private int meansDataSetId;

	private DataSet meansDataSet;

	private List<Experiment> exps;

	private final VariableTypeList germplasmFactors = new VariableTypeList();

	private final Property.ValueChangeListener gxeCheckBoxColumnListener;

	public GxeTable(final Integer studyId, final String selectedEnvFactorName, final String selectedEnvGroupFactorName,
			final Map<String, Boolean> variatesCheckBoxState, final Property.ValueChangeListener gxeCheckBoxColumnListener) {
		this.selectedEnvFactorName = selectedEnvFactorName;
		this.selectedEnvGroupFactorName = selectedEnvGroupFactorName;
		this.variatesCheckBoxState = variatesCheckBoxState;
		this.gxeCheckBoxColumnListener = gxeCheckBoxColumnListener;
		this.studyId = studyId;
	}

	private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(final Map<K, V> map) {
		final SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(final Map.Entry<K, V> e1, final Map.Entry<K, V> e2) {
				return e1.getValue().compareTo(e2.getValue());
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public void initializeTable() {

		this.setImmediate(true);
		this.setWidth("100%");
		this.setHeight("453px");
		this.setContainerDataSource(new IndexedContainer());
		this.setEditable(true);
		this.setColumnReorderingAllowed(true);
		this.setSortDisabled(true);
		this.setColumnCollapsingAllowed(true);

		this.fillTableWithDataset(this.studyId);

	}

	private void initializeHeader(final Map<Integer, String> factors, final Map<Integer, String> variates) {

		final List<String> factorsList = new ArrayList<>(factors.values());
		final List<String> variatesList = new ArrayList<>(variates.values());

		Collections.sort(variatesList);

		this.columnNames.add(" ");
		this.columnNames.addAll(factorsList);
		this.columnNames.addAll(variatesList);

		final List<String> columnHeaders = new ArrayList<>();
		for (final String s : this.columnNames) {
			columnHeaders.add(s.replace("_Means", ""));
		}

		this.setVisibleColumns(this.columnNames.toArray(new Object[0]));
		this.setColumnHeaders(columnHeaders.toArray(new String[0]));

	}

	private void createRow(final int rowIndex, final TableItems[] tableItems) {

		final Object[] obj = new Object[tableItems.length];

		for (int i = 0; i < tableItems.length; i++) {

			if (tableItems[i].getType() == GxeTable.CELL_CHECKBOX) {

				final CheckBox cb = new CheckBox();
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

	protected void fillTableWithDataset(final Integer studyId) {

		final Container container = this.getContainerDataSource();
		container.removeAllItems();

		container.addContainerProperty(" ", CheckBox.class, null);

		this.setHeritabilityValues(this.getHeribilityValuesFromPlotDataSet(studyId));

		final Set<String> envNames = new HashSet<>();

		try {

			final List<DataSet> meansDataSets = this.studyDataManager.getDataSetsByType(studyId, DatasetType.MEANS_DATA);
			if (meansDataSets != null && !meansDataSets.isEmpty()) {

				this.meansDataSet = meansDataSets.get(0);
				this.meansDataSetId = this.meansDataSet.getId();

				final TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(this.meansDataSetId);
				final boolean isSelectedEnvironmentFactorALocation =
						this.studyDataManager.isLocationIdVariable(studyId, selectedEnvFactorName);
				final Map<String, String> locationNameMap = this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId);

				// get the SITE NAME and SITE NO

				final DataSet trialDataSet = DatasetUtil.getTrialDataSet(this.studyDataManager, studyId);
				final VariableTypeList trialEnvFactors = trialDataSet.getVariableTypes().getFactors();

				for (final DMSVariableType factor : trialEnvFactors.getVariableTypes()) {

					this.addNecessaryFactorsToContainer(factor, container);

				}

				this.germplasmFactors.addAll(this.meansDataSet.getFactorsByPhenotypicType(PhenotypicType.GERMPLASM));
				// get the Variates
				final VariableTypeList variates = this.meansDataSet.getVariableTypes().getVariates();
				for (final DMSVariableType v : variates.getVariableTypes()) {
					container.addContainerProperty(v.getLocalName(), Label.class, null);
					if (!v.getStandardVariable().getMethod().getName().equalsIgnoreCase(GxeTable.ERROR_ESTIMATE) && !v.getStandardVariable()
							.getMethod().getName().equalsIgnoreCase("error estimate (" + v.getLocalName().replace("_UnitErrors", "") + ")")
							&& !v.getStandardVariable().getMethod().getName().equalsIgnoreCase(GxeTable.LS_BLUPS) && this
							.getVariatesCheckBoxState().get(v.getLocalName())) {

						this.variateLocalNames.put(v.getRank(), v.getLocalName());

					}
				}

				this.initializeHeader(this.factorLocalNames, this.variateLocalNames);

				// generate the rows
				this.exps = this.studyDataManager
						.getExperimentsWithTrialEnvironment(trialDataSet.getId(), this.meansDataSetId, 0, Integer.MAX_VALUE);

				int rowCounter = 3;

				for (final Experiment exp : this.exps) {

					final String trialInstanceFactorValue = exp.getFactors().findByLocalName(this.trialInstanceFactorName).getValue();
					if (envNames.contains(trialInstanceFactorValue)) {
						continue;
					}

					final TableItems[] row = new TableItems[this.factorLocalNames.size() + this.variateLocalNames.size() + 1];

					row[0] = new TableItems();
					row[0].setType(GxeTable.CELL_CHECKBOX);
					row[0].setLabel(" ");
					row[0].setValue(true);

					int cellCounter = 1;

					for (final Map.Entry<Integer, String> f : this.factorLocalNames.entrySet()) {

						String fValue = exp.getFactors().findByLocalName(f.getValue()).getValue();

						if (f.getValue().equalsIgnoreCase(this.selectedEnvFactorName)) {
							envNames.add(trialInstanceFactorValue);
							if (isSelectedEnvironmentFactorALocation) {
								fValue = locationNameMap.get(fValue);
							}
						}
						row[cellCounter] = new TableItems();
						row[cellCounter].setLabel(fValue);
						row[cellCounter].setType(GxeTable.CELL_LABEL);
						cellCounter++;
					}

					for (final Iterator<Entry<Integer, String>> v = GxeTable.entriesSortedByValues(this.variateLocalNames).iterator(); v
							.hasNext(); ) {

						final Entry<Integer, String> x = v.next();

						row[cellCounter] = new TableItems();
						final Variable var = exp.getVariates().findByLocalName(x.getValue());
						int varKey = 0;
						if (var != null) {
							varKey = var.getVariableType().getId();
						}
						String meansData = "";
						meansData = this.getMeansData(this.meansDataSetId, trialEnvironments, this.trialInstanceFactorName,
								trialInstanceFactorValue, varKey);

						final String heritabilityVal = this.getHeritabilityValues().get(trialInstanceFactorValue).get(x.getValue());
						if (heritabilityVal != null) {
							meansData = String.format("%s (%s)", meansData, heritabilityVal);
						}

						row[cellCounter].setLabel(meansData);
						row[cellCounter].setValue(true);
						row[cellCounter].setType(GxeTable.CELL_LABEL);
						cellCounter++;
					}

					rowCounter++;
					this.createRow(rowCounter, row);
				}

			}

		} catch (final MiddlewareException e) {
			GxeTable.LOG.error(e.getMessage(), e);
		}

	}

	protected Map<String, Map<String, String>> getHeribilityValuesFromPlotDataSet(final int studyId) {
		final List<DataSet> plotDatasets = new ArrayList<>();
		final Map<String, Map<String, String>> heritabilityValues = new HashMap<>();

		try {

			final List<DatasetReference> datasetRefs = this.studyDataManager.getDatasetReferences(studyId);
			for (final DatasetReference dsRef : datasetRefs) {
				final DataSet ds = this.studyDataManager.getDataSet(dsRef.getId());

				if (ds.getDatasetType().getDatasetTypeId() != DatasetType.MEANS_DATA) {

					final Iterator<DMSVariableType> itrFactor = ds.getVariableTypes().getFactors().getVariableTypes().iterator();
					while (itrFactor.hasNext()) {
						final DMSVariableType f = itrFactor.next();
						if (f.getStandardVariable().getId() == TermId.TRIAL_INSTANCE_FACTOR.getId()) {
							this.trialInstanceFactorName = f.getLocalName();
						}
					}

					final Iterator<DMSVariableType> itrVariates = ds.getVariableTypes().getVariates().getVariableTypes().iterator();
					while (itrVariates.hasNext()) {
						if (itrVariates.next().getLocalName().contains("_Heritability")) {
							plotDatasets.add(ds);
						}
					}

				}

			}

			if (!plotDatasets.isEmpty()) {
				this.exps = this.studyDataManager.getExperiments(plotDatasets.get(0).getId(), 0, Integer.MAX_VALUE);
				for (final Experiment exp : this.exps) {

					final String envName = exp.getFactors().findByLocalName(this.trialInstanceFactorName).getValue();

					final Map<String, String> vals = new HashMap<>();

					for (final Entry<String, Boolean> entry : this.getVariatesCheckBoxState().entrySet()) {
						final String name = entry.getKey().replace("_Means", "_Heritability");
						final Variable var = exp.getVariates().findByLocalName(name);
						if (var != null) {
							// heritability value
							vals.put(entry.getKey(), var.getValue());
						}

					}

					heritabilityValues.put(envName, vals);

				}
			}

		} catch (final MiddlewareException e1) {
			GxeTable.LOG.error(e1.getMessage(), e1);
		}

		return heritabilityValues;
	}

	protected String getMeansData(final int meansDataSetId, final TrialEnvironments envs, final String envFactorName, final String envName,
			final int varKey) {
		try {
			return String.valueOf(
					this.studyDataManager.countStocks(meansDataSetId, envs.findOnlyOneByLocalName(envFactorName, envName).getId(), varKey));
		} catch (final MiddlewareQueryException e) {
			GxeTable.LOG.error(e.getMessage(), e);
			return "";
		}

	}

	protected void addNecessaryFactorsToContainer(final DMSVariableType factor, final Container container) {
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
		final GxeEnvironment gxeEnvironment = new GxeEnvironment();
		final List<GxeEnvironmentLabel> environmentLabels = new ArrayList<>();

		final Object[] environments = this.getContainerDataSource().getItemIds().toArray();

		for (Integer i = 0; i < environments.length; i++) {
			final Property cbColumn = this.getContainerProperty(environments[i], " ");
			final Property locationColumn = this.getContainerProperty(environments[i], this.selectedEnvFactorName);
			if ((Boolean) ((CheckBox) cbColumn.getValue()).getValue()) {
				final GxeEnvironmentLabel environmentLabel = new GxeEnvironmentLabel();
				environmentLabel.setName(((Label) locationColumn.getValue()).getValue().toString());
				environmentLabel.setActive(true);
				environmentLabels.add(environmentLabel);
			}
		}

		gxeEnvironment.setLabel(environmentLabels);

		return gxeEnvironment;

	}

	public List<Environment> getSelectedEnvironments() {
		final List<Environment> selectedEnvironments = new ArrayList<>();

		final Object[] obj = this.getContainerDataSource().getItemIds().toArray();

		for (Integer i = 0; i < obj.length; i++) {
			final Property cbColumn = this.getContainerProperty(obj[i], " ");
			final Property locationColumn = this.getContainerProperty(obj[i], this.selectedEnvFactorName);
			final Property trialNoColumn = this.getContainerProperty(obj[i], this.trialInstanceFactorName);
			if ((Boolean) ((CheckBox) cbColumn.getValue()).getValue()) {
				final Environment environment = new Environment();
				environment.setName(((Label) locationColumn.getValue()).getValue().toString());
				environment.setActive(true);
				environment.setTrialno(((Label) trialNoColumn.getValue()).getValue().toString());
				environment.setTrial(((Label) trialNoColumn.getValue()).getValue().toString());
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

	public Map<String, Boolean> getVariatesCheckBoxState() {
		return this.variatesCheckBoxState;
	}

	public Map<String, Map<String, String>> getHeritabilityValues() {
		return this.heritabilityValues;
	}

	public void setHeritabilityValues(final Map<String, Map<String, String>> heritabilityValues) {
		this.heritabilityValues = heritabilityValues;
	}

	protected Property.ValueChangeListener getGxeCheckBoxColumnListener() {
		return this.gxeCheckBoxColumnListener;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeTable();
	}

	protected void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}
}
