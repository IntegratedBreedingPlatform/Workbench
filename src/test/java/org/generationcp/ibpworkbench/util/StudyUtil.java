package org.generationcp.ibpworkbench.util;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;

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
		rank = 1;
	}
	
	public static StudyUtil getInstance() {
		if(studyUtil==null) {
			studyUtil = new StudyUtil();
		}
		return studyUtil;
	}
	
	
	
	public Study createStudyTestData() {
		Study study = new Study();
		study.setId(STUDY_ID);
		return study;
	}
	
	public DataSet createDatasetTestData() {
		DataSet dataset = new DataSet();
		dataset.setId(DATASET_ID);
		dataset.setName(DATASET_NAME);
		dataset.setDescription(DATASET_DESCRIPTION);
		dataset.setDataSetType(DATASET_TYPE);
		dataset.setStudyId(STUDY_ID);
		dataset.setVariableTypes(createDatasetVariableTypesTestData());
		return dataset;
	}
	
	public VariableTypeList createDatasetVariableTypesTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		rank = 1;
		variableTypes.addAll(createDatasetVarsTestData());
		variableTypes.addAll(createTrialEnvironmentVarsTestData());
		variableTypes.addAll(createGermplasmEntryVarsTestData());
		variableTypes.addAll(createTrialDesignVarsTestData());
		variableTypes.addAll(createVariateVarsTestData());
		return variableTypes.sort();
	}

	public VariableTypeList createVariateVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		variableTypes.add(createNumericVariateTestData());
		variableTypes.add(createCategoricalNumericVariateTestData());
		variableTypes.add(createCategoricalNonNumericVariateTestData());
		return variableTypes;
	}
	
	public List<VariateModel> transformVariableTypeListToVariateModels(
			VariableTypeList variableTypeList) {
		List<VariateModel> variateList = new ArrayList<VariateModel>();
		for (VariableType variate : variableTypeList.getVariates().getVariableTypes()) {
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
        	if (variate.getStandardVariable().isNumeric()){
        		vm.setActive(true);
        		if(variate.getStandardVariable().isNumericCategoricalVariate()) {
        			vm.setNumericCategoricalVariate(true);
        		}
        	} else {
        		vm.setNonNumeric(true);
        	}
        	variateList.add(vm);
		}
		return variateList;
	}
	
	public VariableType createCategoricalNonNumericVariateTestData() {
		VariableType variableType = new VariableType();
		variableType.setLocalName(VARIATE_VAR_PREFIX+CATEGORICAL_NON_NUMERIC_PREFIX+VAR_NAME);
		variableType.setLocalDescription(VARIATE_VAR_PREFIX+CATEGORICAL_NON_NUMERIC_PREFIX+VAR_DESCRIPTION);
		variableType.setRank(rank++);
		variableType.setStandardVariable(createStandardVariableTestData(
				variableType.getRank(),
				variableType.getLocalName(),
				variableType.getLocalDescription(),
				TermId.CATEGORICAL_VARIABLE.getId(),
				TermId.CATEGORICAL_VARIATE.getId(),
				PhenotypicType.VARIATE));
		return variableType;
	}

	public VariableType createCategoricalNumericVariateTestData() {
		VariableType variableType = new VariableType();
		variableType.setLocalName(VARIATE_VAR_PREFIX+CATEGORICAL_NUMERIC_PREFIX+VAR_NAME);
		variableType.setLocalDescription(VARIATE_VAR_PREFIX+CATEGORICAL_NUMERIC_PREFIX+VAR_DESCRIPTION);
		variableType.setRank(rank++);
		variableType.setStandardVariable(createStandardVariableTestData(
				variableType.getRank(),
				variableType.getLocalName(),
				variableType.getLocalDescription(),
				TermId.CATEGORICAL_VARIABLE.getId(),
				TermId.CATEGORICAL_VARIATE.getId(),
				PhenotypicType.VARIATE));
		List<Enumeration> validValues = new ArrayList<Enumeration>();
		validValues.add(new Enumeration(1, 
				CATEGORICAL_VAR_NUMERIC_ENUM_NAME, CATEGORICAL_VAR_NUMERIC_ENUM_DEF, 1));
		variableType.getStandardVariable().setEnumerations(validValues);
		return variableType;
	}

	public VariableType createNumericVariateTestData() {
		VariableType variableType = new VariableType();
		variableType.setLocalName(VARIATE_VAR_PREFIX+NUMERIC_PREFIX+VAR_NAME);
		variableType.setLocalDescription(VARIATE_VAR_PREFIX+NUMERIC_PREFIX+VAR_DESCRIPTION);
		variableType.setRank(rank++);
		variableType.setStandardVariable(createStandardVariableTestData(
				variableType.getRank(),
				variableType.getLocalName(),
				variableType.getLocalDescription(),
				TermId.NUMERIC_VARIABLE.getId(),
				TermId.OBSERVATION_VARIATE.getId(),
				PhenotypicType.VARIATE));
		return variableType;
	}

	public VariableTypeList createTrialDesignVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		VariableType variableType = new VariableType();
		variableType.setLocalName(TRIAL_DESIGN_VAR_PREFIX+VAR_NAME);
		variableType.setLocalDescription(TRIAL_DESIGN_VAR_PREFIX+VAR_DESCRIPTION);
		variableType.setRank(rank++);
		variableType.setStandardVariable(createStandardVariableTestData(
				TermId.PLOT_NO.getId(),
				variableType.getLocalName(),
				variableType.getLocalDescription(),
				TermId.NUMERIC_VARIABLE.getId(),
				TermId.TRIAL_DESIGN_INFO_STORAGE.getId(),
				PhenotypicType.TRIAL_DESIGN));
		variableTypes.add(variableType);
		return variableTypes;
	}

	public VariableTypeList createGermplasmEntryVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		VariableType variableType = new VariableType();
		variableType.setLocalName(GERMPLASM_ENTRY_VAR_PREFIX+VAR_NAME);
		variableType.setLocalDescription(GERMPLASM_ENTRY_VAR_PREFIX+VAR_DESCRIPTION);
		variableType.setRank(rank++);
		variableType.setStandardVariable(createStandardVariableTestData(
				TermId.ENTRY_NO.getId(),
				variableType.getLocalName(),
				variableType.getLocalDescription(),
				TermId.NUMERIC_VARIABLE.getId(),
				TermId.ENTRY_NUMBER_STORAGE.getId(),
				PhenotypicType.GERMPLASM));
		variableTypes.add(variableType);
		return variableTypes;
	}

	public VariableTypeList createTrialEnvironmentVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		VariableType variableType = new VariableType();
		variableType.setLocalName(TRIAL_INSTANCE_VAR_NAME);
		variableType.setLocalDescription(TRIAL_INSTANCE_VAR_DESC);
		variableType.setRank(rank++);
		variableType.setStandardVariable(
				createStandardVariableTestData(
					TermId.TRIAL_INSTANCE_FACTOR.getId(),
					variableType.getLocalName(),
					variableType.getLocalDescription(),
					TermId.NUMERIC_VARIABLE.getId(),
					TermId.TRIAL_INSTANCE_STORAGE.getId(),
					PhenotypicType.TRIAL_ENVIRONMENT));
		variableTypes.add(variableType);
		
		return variableTypes;
	}

	public VariableTypeList createDatasetVarsTestData() {
		VariableTypeList variableTypes = new VariableTypeList();
		variableTypes.add(createDatasetNameVariableTypeTestData());
		variableTypes.add(createDatasetTitleVariableTypeTestData());
		variableTypes.add(createDatasetTypeVariableTypeTestData());
		return variableTypes;
	}

	public VariableType createDatasetTypeVariableTypeTestData() {
		VariableType variableType = new VariableType();
		variableType.setLocalName(DATASET_VAR_TYPE);
		variableType.setLocalDescription(DATASET_VAR_TYPE_DESC);
		variableType.setRank(rank++);
		variableType.setStandardVariable(createStandardVariableTestData(
				TermId.DATASET_TYPE.getId(), variableType.getLocalName(), 
				variableType.getLocalDescription(), 
				TermId.CATEGORICAL_VARIABLE.getId(), 
				TermId.DATASET_INFO_STORAGE.getId(), 
				PhenotypicType.DATASET));
		return variableType;
	}

	public VariableType createDatasetTitleVariableTypeTestData() {
		VariableType variableType = new VariableType();
		variableType.setLocalName(DATASET_VAR_TITLE);
		variableType.setLocalDescription(DATASET_VAR_TITLE_DESC);
		variableType.setRank(rank++);
		variableType.setStandardVariable(createStandardVariableTestData(
				TermId.DATASET_TITLE.getId(), variableType.getLocalName(), 
				variableType.getLocalDescription(), 
				TermId.CHARACTER_VARIABLE.getId(), 
				TermId.DATASET_TITLE_STORAGE.getId(), 
				PhenotypicType.DATASET));
		return variableType;
	}

	public VariableType createDatasetNameVariableTypeTestData() {
		VariableType variableType = new VariableType();
		variableType.setLocalName(DATASET_VAR_NAME);
		variableType.setLocalDescription(DATASET_VAR_NAME_DESC);
		variableType.setRank(rank++);
		variableType.setStandardVariable(createStandardVariableTestData(
				TermId.DATASET_NAME.getId(), variableType.getLocalName(), 
				variableType.getLocalDescription(), 
				TermId.CHARACTER_VARIABLE.getId(), 
				TermId.DATASET_NAME_STORAGE.getId(), 
				PhenotypicType.DATASET));
		return variableType;
	}

	public StandardVariable createStandardVariableTestData(
			int id,
			String name, 
			String description, 
			Integer dataTypeId, 
			Integer storedIn, 
			PhenotypicType phenotypicType) {
		StandardVariable var = new StandardVariable();
		var.setId(id);
		var.setName(name);
		var.setDescription(description);
		var.setCropOntologyId(null);
	    var.setProperty(new Term(id++,name+PROPERTY_POSTFIX,description+PROPERTY_POSTFIX));
		var.setScale(new Term(id++,name+SCALE_POSTFIX,description+SCALE_POSTFIX));
		var.setMethod(new Term(id++,name+METHOD_POSTFIX,description+METHOD_POSTFIX));
	    var.setDataType(new Term(dataTypeId,name+DATA_TYPE_POSTFIX,description+DATA_TYPE_POSTFIX));
	    var.setStoredIn(new Term(storedIn,name+STORED_IN_POSTFIX,description+STORED_IN_POSTFIX));
	    var.setIsA(new Term(id++,name+TRAIT_CLASS_POSTFIX,description+TRAIT_CLASS_POSTFIX));
	    var.setPhenotypicType(phenotypicType);
	    var.setConstraints(null);
	    var.setEnumerations(null);
	    return var;
	}

}
