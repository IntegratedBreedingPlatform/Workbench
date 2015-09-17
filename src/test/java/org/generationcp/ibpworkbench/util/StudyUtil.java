
package org.generationcp.ibpworkbench.util;

import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;

import java.util.ArrayList;
import java.util.List;

public class StudyUtil {

	public static final int STUDY_ID = -1;

	public static final int DATASET_ID = -3;
	public static final String DATASET_NAME = "Test Dataset";
	public static final String DATASET_DESCRIPTION = "Test Dataset Description";
	public static final DataSetType DATASET_TYPE = DataSetType.PLOT_DATA;

	public static final String PROPERTY_POSTFIX = "_PROPERTY";
	public static final String SCALE_POSTFIX = "_SCALE";
	public static final String METHOD_POSTFIX = "_METHOD";
	public static final String DATA_TYPE_POSTFIX = "_DATA_TYPE";
	public static final String TRAIT_CLASS_POSTFIX = "_TRAIT_CLASS";
	public static final String STORED_IN_POSTFIX = "_STORED_IN";

	public static final String DATASET_VAR_NAME = "DATASET_NAME";
	public static final String DATASET_VAR_NAME_DESC = "Dataset name";
	public static final String DATASET_VAR_TITLE = "DATASET_TITLE";
	public static final String DATASET_VAR_TITLE_DESC = "Dataset title";
	public static final String DATASET_VAR_TYPE = "DATASET_TYPE";
	public static final String DATASET_VAR_TYPE_DESC = "Dataset type";

	public static final String TRIAL_INSTANCE_VAR_NAME = "TRIAL_INSTANCE";
	public static final String TRIAL_INSTANCE_VAR_DESC = "Trial instance - enumerated (number)";

	public static final String GERMPLASM_ENTRY_VAR_PREFIX = "GERMP_ENTRY_VAR_";
	public static final String TRIAL_DESIGN_VAR_PREFIX = "TRIAL_DESIGN_VAR_";
	public static final String VARIATE_VAR_PREFIX = "VARIATE_VAR_";
	public static final String VAR_NAME = "NAME";
	public static final String VAR_DESCRIPTION = "DESC";

	public static final String NUMERIC_PREFIX = "NUMERIC_";
	public static final String CATEGORICAL_NUMERIC_PREFIX = "CATEGORICAL_NUMERIC_";
	public static final String CATEGORICAL_NON_NUMERIC_PREFIX = "CATEGORICAL_NON_NUMERIC_";
	public static final String CATEGORICAL_VAR_NUMERIC_ENUM_NAME = "0";
	public static final String CATEGORICAL_VAR_NUMERIC_ENUM_DEF = "No disease";

	public int rank;

	public static StudyUtil studyUtil;

	private StudyUtil() {
		this.rank = 1;
	}

	public static StudyUtil getInstance() {
		if (StudyUtil.studyUtil == null) {
			StudyUtil.studyUtil = new StudyUtil();
		}
		return StudyUtil.studyUtil;
	}

	public Study createStudyTestData() {
		Study study = new Study();
		study.setId(StudyUtil.STUDY_ID);
		return study;
	}

	public DataSet createDatasetTestData() {
		DataSet dataset = new DataSet();
		dataset.setId(StudyUtil.DATASET_ID);
		dataset.setName(StudyUtil.DATASET_NAME);
		dataset.setDescription(StudyUtil.DATASET_DESCRIPTION);
		dataset.setDataSetType(StudyUtil.DATASET_TYPE);
		dataset.setStudyId(StudyUtil.STUDY_ID);
		dataset.setVariableTypes(this.createDatasetVariableTypesTestData());
		return dataset;
	}

	public VariableTypeList createDatasetVariableTypesTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		this.rank = 1;
		variableTypes.addAll(this.createDatasetVarsTestData());
		variableTypes.addAll(this.createTrialEnvironmentVarsTestData());
		variableTypes.addAll(this.createGermplasmEntryVarsTestData());
		variableTypes.addAll(this.createTrialDesignVarsTestData());
		variableTypes.addAll(this.createVariateVarsTestData());
		return variableTypes.sort();
	}

	public VariableTypeList createVariateVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		variableTypes.add(this.createNumericVariateTestData());
		variableTypes.add(this.createCategoricalNumericVariateTestData());
		variableTypes.add(this.createCategoricalNonNumericVariateTestData());
		return variableTypes;
	}

	public List<VariateModel> transformVariableTypeListToVariateModels(VariableTypeList variableTypeList) {
		List<VariateModel> variateList = new ArrayList<VariateModel>();
		for (DMSVariableType variate : variableTypeList.getVariates().getVariableTypes()) {
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
			variateList.add(vm);
		}
		return variateList;
	}

	public DMSVariableType createCategoricalNonNumericVariateTestData() {
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(StudyUtil.VARIATE_VAR_PREFIX + StudyUtil.CATEGORICAL_NON_NUMERIC_PREFIX + StudyUtil.VAR_NAME);
		variableType.setLocalDescription(
				StudyUtil.VARIATE_VAR_PREFIX + StudyUtil.CATEGORICAL_NON_NUMERIC_PREFIX + StudyUtil.VAR_DESCRIPTION);
		variableType.setRank(this.rank++);
		variableType.setStandardVariable(
				this.createStandardVariableTestData(variableType.getRank(), variableType.getLocalName(), variableType.getLocalDescription(),
						TermId.CATEGORICAL_VARIABLE.getId(), TermId.CATEGORICAL_VARIATE.getId(), PhenotypicType.VARIATE));
		variableType.setRole(PhenotypicType.VARIATE);

		return variableType;
	}

	public DMSVariableType createCategoricalNumericVariateTestData() {
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(StudyUtil.VARIATE_VAR_PREFIX + StudyUtil.CATEGORICAL_NUMERIC_PREFIX + StudyUtil.VAR_NAME);
		variableType.setLocalDescription(StudyUtil.VARIATE_VAR_PREFIX + StudyUtil.CATEGORICAL_NUMERIC_PREFIX + StudyUtil.VAR_DESCRIPTION);
		variableType.setRank(this.rank++);
		variableType.setStandardVariable(this.createStandardVariableTestData(variableType.getRank(), variableType.getLocalName(),
				variableType.getLocalDescription(), TermId.CATEGORICAL_VARIABLE.getId(), TermId.CATEGORICAL_VARIATE.getId(),
				PhenotypicType.VARIATE));
		List<Enumeration> validValues = new ArrayList<Enumeration>();
		validValues.add(new Enumeration(1, StudyUtil.CATEGORICAL_VAR_NUMERIC_ENUM_NAME, StudyUtil.CATEGORICAL_VAR_NUMERIC_ENUM_DEF, 1));
		variableType.getStandardVariable().setEnumerations(validValues);
		variableType.setRole(PhenotypicType.VARIATE);

		return variableType;
	}

	public DMSVariableType createNumericVariateTestData() {
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(StudyUtil.VARIATE_VAR_PREFIX + StudyUtil.NUMERIC_PREFIX + StudyUtil.VAR_NAME);
		variableType.setLocalDescription(StudyUtil.VARIATE_VAR_PREFIX + StudyUtil.NUMERIC_PREFIX + StudyUtil.VAR_DESCRIPTION);
		variableType.setRank(this.rank++);
		variableType.setStandardVariable(
				this.createStandardVariableTestData(variableType.getRank(), variableType.getLocalName(), variableType.getLocalDescription(),
						TermId.NUMERIC_VARIABLE.getId(), TermId.OBSERVATION_VARIATE.getId(), PhenotypicType.VARIATE));
		variableType.setRole(PhenotypicType.VARIATE);

		return variableType;
	}

	public VariableTypeList createTrialDesignVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(StudyUtil.TRIAL_DESIGN_VAR_PREFIX + StudyUtil.VAR_NAME);
		variableType.setLocalDescription(StudyUtil.TRIAL_DESIGN_VAR_PREFIX + StudyUtil.VAR_DESCRIPTION);
		variableType.setRank(this.rank++);
		variableType.setStandardVariable(
				this.createStandardVariableTestData(TermId.PLOT_NO.getId(), variableType.getLocalName(), variableType.getLocalDescription(),
						TermId.NUMERIC_VARIABLE.getId(), TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), PhenotypicType.TRIAL_DESIGN));
		variableTypes.add(variableType);
		variableType.setRole(PhenotypicType.TRIAL_DESIGN);

		return variableTypes;
	}

	public VariableTypeList createGermplasmEntryVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(StudyUtil.GERMPLASM_ENTRY_VAR_PREFIX + StudyUtil.VAR_NAME);
		variableType.setLocalDescription(StudyUtil.GERMPLASM_ENTRY_VAR_PREFIX + StudyUtil.VAR_DESCRIPTION);
		variableType.setRank(this.rank++);
		variableType.setStandardVariable(this.createStandardVariableTestData(TermId.ENTRY_NO.getId(), variableType.getLocalName(),
				variableType.getLocalDescription(), TermId.NUMERIC_VARIABLE.getId(), TermId.ENTRY_NUMBER_STORAGE.getId(),
				PhenotypicType.GERMPLASM));
		variableTypes.add(variableType);
		variableType.setRole(PhenotypicType.GERMPLASM);

		return variableTypes;
	}

	public VariableTypeList createTrialEnvironmentVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(StudyUtil.TRIAL_INSTANCE_VAR_NAME);
		variableType.setLocalDescription(StudyUtil.TRIAL_INSTANCE_VAR_DESC);
		variableType.setRank(this.rank++);
		variableType.setStandardVariable(
				this.createStandardVariableTestData(TermId.TRIAL_INSTANCE_FACTOR.getId(), variableType.getLocalName(),
						variableType.getLocalDescription(), TermId.NUMERIC_VARIABLE.getId(), TermId.TRIAL_INSTANCE_STORAGE.getId(),
						PhenotypicType.TRIAL_ENVIRONMENT));
		variableTypes.add(variableType);
		variableType.setRole(PhenotypicType.TRIAL_ENVIRONMENT);

		return variableTypes;
	}

	public VariableTypeList createDatasetVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		variableTypes.add(this.createDatasetNameVariableTypeTestData());
		variableTypes.add(this.createDatasetTitleVariableTypeTestData());
		variableTypes.add(this.createDatasetTypeVariableTypeTestData());
		return variableTypes;
	}

	public DMSVariableType createDatasetTypeVariableTypeTestData() {
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(StudyUtil.DATASET_VAR_TYPE);
		variableType.setLocalDescription(StudyUtil.DATASET_VAR_TYPE_DESC);
		variableType.setRank(this.rank++);
		variableType.setStandardVariable(this.createStandardVariableTestData(TermId.DATASET_TYPE.getId(), variableType.getLocalName(),
				variableType.getLocalDescription(), TermId.CATEGORICAL_VARIABLE.getId(), TermId.DATASET_INFO_STORAGE.getId(),
				PhenotypicType.DATASET));
		variableType.setRole(PhenotypicType.DATASET);

		return variableType;
	}

	public DMSVariableType createDatasetTitleVariableTypeTestData() {
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(StudyUtil.DATASET_VAR_TITLE);
		variableType.setLocalDescription(StudyUtil.DATASET_VAR_TITLE_DESC);
		variableType.setRank(this.rank++);
		variableType.setStandardVariable(this.createStandardVariableTestData(TermId.DATASET_TITLE.getId(), variableType.getLocalName(),
				variableType.getLocalDescription(), TermId.CHARACTER_VARIABLE.getId(), TermId.DATASET_TITLE_STORAGE.getId(),
				PhenotypicType.DATASET));
		variableType.setRole(PhenotypicType.DATASET);

		return variableType;
	}

	public DMSVariableType createDatasetNameVariableTypeTestData() {
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(StudyUtil.DATASET_VAR_NAME);
		variableType.setLocalDescription(StudyUtil.DATASET_VAR_NAME_DESC);
		variableType.setRank(this.rank++);
		variableType.setStandardVariable(this.createStandardVariableTestData(TermId.DATASET_NAME.getId(), variableType.getLocalName(),
				variableType.getLocalDescription(), TermId.CHARACTER_VARIABLE.getId(), TermId.DATASET_NAME_STORAGE.getId(),
				PhenotypicType.DATASET));
		variableType.setRole(PhenotypicType.DATASET);

		return variableType;
	}

	public StandardVariable createStandardVariableTestData(int id, String name, String description, Integer dataTypeId, Integer storedIn,
			PhenotypicType phenotypicType) {
		StandardVariable var = new StandardVariable();
		var.setId(id);
		var.setName(name);
		var.setDescription(description);
		var.setCropOntologyId(null);
		var.setProperty(new Term(id++, name + StudyUtil.PROPERTY_POSTFIX, description + StudyUtil.PROPERTY_POSTFIX));
		var.setScale(new Term(id++, name + StudyUtil.SCALE_POSTFIX, description + StudyUtil.SCALE_POSTFIX));
		var.setMethod(new Term(id++, name + StudyUtil.METHOD_POSTFIX, description + StudyUtil.METHOD_POSTFIX));
		var.setDataType(new Term(dataTypeId, name + StudyUtil.DATA_TYPE_POSTFIX, description + StudyUtil.DATA_TYPE_POSTFIX));
		var.setIsA(new Term(id++, name + StudyUtil.TRAIT_CLASS_POSTFIX, description + StudyUtil.TRAIT_CLASS_POSTFIX));
		var.setPhenotypicType(phenotypicType);
		var.setConstraints(null);
		var.setEnumerations(null);
		return var;
	}

}
