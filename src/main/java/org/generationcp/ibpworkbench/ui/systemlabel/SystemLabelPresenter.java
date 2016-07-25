package org.generationcp.ibpworkbench.ui.systemlabel;

import com.mysql.jdbc.StringUtils;
import com.vaadin.data.Validator;
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
	protected void saveTerms(final List<Term> terms) throws Validator.InvalidValueException {

		// Then save the changes.
		ontologyDataManager.updateTerms(terms);

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

	/**
	 * Gets the terms from the table.
	 * @return
	 */
	protected List<Term> retrieveTermsFromTable() {

		Table systemLableTable = view.getTblSystemLabels();

		systemLableTable.commit();

		// Get the Terms from the table's container
		BeanItemContainer<Term> container = (BeanItemContainer<Term>) systemLableTable.getContainerDataSource();
		return new ArrayList<Term>((Collection<Term>) container.getItemIds());

	}

	/**
	 * Checks if all terms are valid. Returns false if there is one or more term with empty name.
	 * @param terms
	 * @return
	 */
	protected boolean isAllTermsValid(final List<Term> terms) {
		for (Term term : terms) {
			if (StringUtils.isNullOrEmpty(term.getName())) {
				return false;
			}
		}

		return true;

	}

	public void setSystemLabelIds(final String systemLabelIds) {
		this.systemLabelIds = systemLabelIds;
	}



}
