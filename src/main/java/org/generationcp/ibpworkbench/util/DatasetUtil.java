package org.generationcp.ibpworkbench.util;

import java.util.List;

import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;

public class DatasetUtil {

	public static DataSet getTrialDataSet(StudyDataManager studyDataManager, int studyId) throws MiddlewareQueryException {
    	List<DataSet> summaryDatasets = studyDataManager.getDataSetsByType(studyId, DataSetType.SUMMARY_DATA);
        if(summaryDatasets==null || summaryDatasets.isEmpty()) {
        	//check for old logic where trial dataset is still a plot dataset type
        	List<DataSet> plotDatasets = studyDataManager.getDataSetsByType(studyId, DataSetType.PLOT_DATA);
        	for (DataSet dataSet : plotDatasets) {
                String name = dataSet.getName();
                if (name != null && (name.startsWith("MEASUREMENT EFEC_") || name.endsWith("-PLOTDATA"))) {//old or new name for measurements/plot
                	continue;
                } else if (name != null && (name.startsWith("TRIAL_") || name.endsWith("-ENVIRONMENT"))) {//old or new name for trial/summary
                    return dataSet;
                } else {
                    if (dataSet != null && dataSet.getVariableTypes().getVariableTypes() != null) {
                        boolean aTrialDataset = true;
                        for (VariableType variableType : dataSet.getVariableTypes().getVariableTypes()) {
                            if (variableType.getStandardVariable().getPhenotypicType()
                                    == PhenotypicType.GERMPLASM) {
                            	aTrialDataset = false;
                                break;
                            }
                        }
                        if (aTrialDataset) {
                            return dataSet;
                        }
                    }
                }
            }
        } else {
        	return summaryDatasets.get(0);
        }
        return null;
	}
	
	public static DataSet getMeansDataSet(StudyDataManager studyDataManager, int studyId) throws MiddlewareQueryException {
		return studyDataManager.getDataSetsByType(studyId, DataSetType.MEANS_DATA).get(0);
	}

}
