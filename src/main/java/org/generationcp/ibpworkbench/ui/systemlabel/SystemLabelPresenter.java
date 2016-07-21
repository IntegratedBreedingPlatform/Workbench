package org.generationcp.ibpworkbench.ui.systemlabel;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configurable
public class SystemLabelPresenter {

	private SystemLabelView view;

	@Resource
	private OntologyDataManager ontologyDataManager;

	@Value("${system.label.ids}")
	private String systemLabelIds;

	public SystemLabelPresenter(SystemLabelView view) {
		this.view = view;
	}

	/**
	 * Gets the list of Term based on the termIds specified in the property settings {system.label.ids}
	 *
	 * @return
	 */
	protected List<Term> retrieveSystemLabelTerms() {

		List<Integer> termIdSystemLabels = new ArrayList<>();
		for (String value : systemLabelIds.split(",")) {
			termIdSystemLabels.add(Integer.valueOf(value));
		}
		return ontologyDataManager.getTermsByIds(termIdSystemLabels);

	}

	/**
	 * Saves the Term changes from the System Label table.
	 */
	protected void saveTerms() {

		Table systemLableTable = view.getTblSystemLabels();

		// Make sure that fields are validated
		systemLableTable.commit();

		// Get the Terms from the table's container
		BeanItemContainer<Term> container = (BeanItemContainer<Term>) systemLableTable.getContainerDataSource();

		// Then save the changes.
		ontologyDataManager.updateTerms(new ArrayList<Term>((Collection<Term>) container.getItemIds()));

	}

	/**
	 * Loads the list of Terms to the System Label table.
	 */
	protected void loadDataSystemLabelTable() {

		Table systemLableTable = view.getTblSystemLabels();
		systemLableTable.removeAllItems();

		List<Term> terms = this.retrieveSystemLabelTerms();
		((BeanItemContainer<Term>) systemLableTable.getContainerDataSource()).addAll(terms);
		systemLableTable.setPageLength(terms.size());

	}

	public void setSystemLabelIds(final String systemLabelIds) {
		this.systemLabelIds = systemLabelIds;
	}

}
