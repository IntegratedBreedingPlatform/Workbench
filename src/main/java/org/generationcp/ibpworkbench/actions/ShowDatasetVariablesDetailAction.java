
package org.generationcp.ibpworkbench.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

/**
 *
 * @author Jeffrey Morales
 *
 */
@Configurable
public class ShowDatasetVariablesDetailAction implements ItemClickListener {

	private static final long serialVersionUID = 1L;
	private static final String NUMERIC_VARIABLE = "Numeric variable";

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	private final Table tblVariates;

	private final Table tblFactors;

	private final SingleSiteAnalysisPanel singleSiteAnalysisPanel;

	private final static Logger LOG = LoggerFactory.getLogger(ShowDatasetVariablesDetailAction.class);

	public ShowDatasetVariablesDetailAction(Table tblFactors, Table tblVariates, SingleSiteAnalysisPanel singleSiteAnalysisPanel) {
		this.tblFactors = tblFactors;
		this.tblVariates = tblVariates;
		this.singleSiteAnalysisPanel = singleSiteAnalysisPanel;
	}

	@Override
	public void itemClick(ItemClickEvent event) {

		if (!(event.getItemId() instanceof DatasetReference)) {
			return;
		}

		DatasetReference datasetRef = (DatasetReference) event.getItemId();
		Integer dataSetId = datasetRef.getId();

		if (dataSetId == null) {
			return;
		}

		try {

			DataSet ds = this.studyDataManager.getDataSet(dataSetId);

			Study currentStudy = this.singleSiteAnalysisPanel.getCurrentStudy();

			if (currentStudy == null) {
				Study study = this.studyDataManager.getStudy(ds.getStudyId());
				this.singleSiteAnalysisPanel.setCurrentStudy(study);
			} else if (this.singleSiteAnalysisPanel.getCurrentStudy().getId() != ds.getStudyId()) {
				Study study = this.studyDataManager.getStudy(ds.getStudyId());
				this.singleSiteAnalysisPanel.setCurrentStudy(study);
			}

			List<FactorModel> factorList = new ArrayList<FactorModel>();
			List<VariateModel> variateList = new ArrayList<VariateModel>();

			for (VariableType factor : ds.getVariableTypes().getFactors().getVariableTypes()) {

				if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET) {
					continue;
				}

				FactorModel fm = new FactorModel();
				fm.setId(factor.getRank());
				fm.setName(factor.getLocalName());
				fm.setDescription(factor.getLocalDescription());
				fm.setScname(factor.getStandardVariable().getScale().getName());
				fm.setScaleid(factor.getStandardVariable().getScale().getId());
				fm.setTmname(factor.getStandardVariable().getMethod().getName());
				fm.setTmethid(factor.getStandardVariable().getMethod().getId());
				fm.setTrname(factor.getStandardVariable().getName());
				fm.setTraitid(factor.getStandardVariable().getProperty().getId());

				factorList.add(fm);
			}

			for (VariableType variate : ds.getVariableTypes().getVariates().getVariableTypes()) {

				VariateModel vm = new VariateModel();
				vm.setId(variate.getRank());
				vm.setName(variate.getLocalName());
				vm.setDescription(variate.getLocalDescription());
				vm.setScname(variate.getStandardVariable().getScale().getName());
				vm.setScaleid(variate.getStandardVariable().getScale().getId());
				vm.setTmname(variate.getStandardVariable().getMethod().getName());
				vm.setTmethid(variate.getStandardVariable().getMethod().getId());
				vm.setTrname(variate.getStandardVariable().getName());
				vm.setTraitid(variate.getStandardVariable().getProperty().getId());
				vm.setDatatype(variate.getStandardVariable().getDataType().getName());

				if (vm.getDatatype().equals(ShowDatasetVariablesDetailAction.NUMERIC_VARIABLE)) {
					vm.setActive(true);
				}

				ShowDatasetVariablesDetailAction.LOG.debug(variate.toString());
				variateList.add(vm);

			}

			this.singleSiteAnalysisPanel.setCurrentDatasetName(ds.getName());
			this.singleSiteAnalysisPanel.setCurrentDataSetId(ds.getId());

			this.updateFactorsTable(factorList);
			this.updateVariatesTable(variateList);

		} catch (MiddlewareQueryException e) {
			this.showDatabaseError(event.getComponent().getWindow());
		}

	}

	private void updateFactorsTable(List<FactorModel> factorList) {
		Object[] oldColumns = this.tblFactors.getVisibleColumns();
		String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);

		BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
		container.setBeanIdProperty("id");
		this.tblFactors.setContainerDataSource(container);

		for (FactorModel f : factorList) {
			container.addBean(f);
		}

		this.tblFactors.setContainerDataSource(container);

		this.tblFactors.setVisibleColumns(columns);
	}

	private void updateVariatesTable(List<VariateModel> variateList) {

		// reset
		this.singleSiteAnalysisPanel.getVariatesCheckboxState().clear();
		this.singleSiteAnalysisPanel.setNumOfSelectedVariates(0);
		this.singleSiteAnalysisPanel.toggleNextButton(false);

		// load data
		BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
		container.setBeanIdProperty("id");

		for (VariateModel v : variateList) {
			container.addBean(v);
			this.singleSiteAnalysisPanel.getVariatesCheckboxState().put(v.getName(), v.getActive());
		}
		this.tblVariates.setContainerDataSource(container);
		this.tblVariates.setVisibleColumns(new String[] {"", "name", "description", "scname"});
		this.tblVariates.setColumnHeaders(new String[] {"", "Name", "Description", "Scale"});
	}

	private void showDatabaseError(Window window) {
		MessageNotifier.showError(window, this.messageSource.getMessage(Message.DATABASE_ERROR),
				"<br />" + this.messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
	}
}
