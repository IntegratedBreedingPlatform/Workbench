package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.commons.sea.xml.Environment;
import org.generationcp.ibpworkbench.util.TableItems;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

	@Autowired
	private OntologyVariableService ontologyVariableService;

	private final List<String> columnNames = new ArrayList<>();
	private final Map<Integer, String> factorLocalNames = new TreeMap<>();
	private final Map<Integer, String> variateLocalNames = new TreeMap<>();
	private Map<String, Boolean> variatesCheckBoxState = new HashMap<>();

	private final Integer studyId;

	private String trialInstanceFactorName = "";

	private String selectedEnvFactorName = "";

	private String selectedEnvGroupFactorName = "";

	private int meansDataSetId;

	private DataSet meansDataSet;

	private List<Experiment> exps;

	private final VariableTypeList germplasmFactors = new VariableTypeList();

	private final Property.ValueChangeListener gxeCheckBoxColumnListener;

	public GxeTable(
		final Integer studyId, final String selectedEnvFactorName, final String selectedEnvGroupFactorName,
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

		final Map<String, Map<String, String>> heritabilityValuesMap = this.getHeribilityValuesFromSummaryStatisticsDataset(studyId);

		final Set<String> envNames = new HashSet<>();

		try {

			final List<DataSet> meansDataSets = this.studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.MEANS_DATA.getId());
			if (meansDataSets != null && !meansDataSets.isEmpty()) {

				this.meansDataSet = meansDataSets.get(0);
				this.meansDataSetId = this.meansDataSet.getId();

				final TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(this.meansDataSetId);
				final boolean isSelectedEnvironmentFactorALocation =
					this.studyDataManager.isLocationIdVariable(studyId, this.selectedEnvFactorName);
				final Map<String, String> locationNameMap = this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId);

				// get the SITE NAME and SITE NO

				final DataSet trialDataSet = this.studyDataManager.findOneDataSetByType(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());
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

						final String heritabilityVal = heritabilityValuesMap.get(trialInstanceFactorValue).get(x.getValue());
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

	protected Map<String, Map<String, String>> getHeribilityValuesFromSummaryStatisticsDataset(final int studyId) {
		final Map<String, Map<String, String>> heritabilityValuesMap = new HashMap<>();

		try {

			final DataSet plotDataset =
				this.studyDataManager.findOneDataSetByType(studyId, DatasetTypeEnum.PLOT_DATA.getId());
			final DataSet summaryStatisticsDataset =
				this.studyDataManager.findOneDataSetByType(studyId, DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId());
			this.trialInstanceFactorName =
				summaryStatisticsDataset.getVariableTypes().findById(TermId.TRIAL_INSTANCE_FACTOR.getId()).getLocalName();
			final List<Experiment> summaryStasticsExperiments =
				this.studyDataManager.getExperiments(summaryStatisticsDataset.getId(), 0, Integer.MAX_VALUE);

			if (CollectionUtils.isNotEmpty(summaryStasticsExperiments)) {
				final Map<String, Integer> methodsIdsMap = new CaseInsensitiveMap();
				for (final DMSVariableType variable : summaryStasticsExperiments.get(0).getVariates().getVariableTypes()
					.getVariableTypes()) {
					methodsIdsMap.putIfAbsent(variable.getStandardVariable().getMethod().getName(),
						variable.getStandardVariable().getMethod().getId());
				}
				final Map<String, Integer> traitVariableIdsMap =
					new CaseInsensitiveMap(plotDataset.getVariableTypes().getVariates().getVariableTypes().stream().collect(
						Collectors.toMap(DMSVariableType::getLocalName, DMSVariableType::getId)));
				final MultiKeyMap analysisMethodsOfTraits =
					this.ontologyVariableService.getAnalysisMethodsOfTraits(new ArrayList<>(traitVariableIdsMap.values()),
						new ArrayList<>(methodsIdsMap.values()));

				for (final Experiment experiment : summaryStasticsExperiments) {

					final String environmentName = experiment.getFactors().findByLocalName(this.trialInstanceFactorName).getValue();
					final Map<String, String> heritabilityValues = new HashMap<>();

					for (final Entry<String, Boolean> entry : this.getVariatesCheckBoxState().entrySet()) {
						final String traitName = entry.getKey().replace("_Means", "");
						final Integer variableId = traitVariableIdsMap.get(traitName);
						final Integer methodId = methodsIdsMap.get("Heritability");
						final Integer analsisVariableId = (Integer) analysisMethodsOfTraits.get(variableId, methodId);
						if (analsisVariableId != null) {
							final Variable variable = experiment.getVariates().findById(analsisVariableId);
							if (variable != null) {
								// heritability value
								heritabilityValues.put(entry.getKey(), variable.getValue());
							}
						}
					}
					heritabilityValuesMap.put(environmentName, heritabilityValues);
				}

			}

		} catch (final MiddlewareException e1) {
			GxeTable.LOG.error(e1.getMessage(), e1);
		}

		return heritabilityValuesMap;
	}

	protected String getMeansData(
		final int meansDataSetId, final TrialEnvironments envs, final String envFactorName, final String envName,
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

	protected void setOntologyVariableService(final OntologyVariableService ontologyVariableService) {
		this.ontologyVariableService = ontologyVariableService;
	}
}
