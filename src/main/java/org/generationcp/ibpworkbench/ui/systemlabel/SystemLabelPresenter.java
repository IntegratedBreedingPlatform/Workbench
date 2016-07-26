package org.generationcp.ibpworkbench.ui.systemlabel;

import com.mysql.jdbc.StringUtils;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configurable
public class SystemLabelPresenter {

	private SystemLabelView view;

	@Resource
	private OntologyDataManager ontologyDataManager;

	public SystemLabelPresenter(final SystemLabelView view) {
		this.view = view;
	}

	/**
	 * Gets the list of System Label terms.
	 *
	 * @return
	 */
	protected List<Term> retrieveSystemLabelTerms() {

		// NOTE: We know that the system label terms are within the id range of 1700 to 1799 so we will get all
		// the terms within this range.
		final List<Integer> termIdSystemLabels = new ArrayList<>();

		for (int i = 1700; i < 1800; i++) {
			termIdSystemLabels.add(Integer.valueOf(i));
		}

		return ontologyDataManager.getTermsByIds(termIdSystemLabels);

	}

	/**
	 * Saves the Term changes from the System Label table.
	 */
	protected void saveTerms() {

		final List<Term> terms = this.retrieveTermsFromTable();

		if (this.isAllTermsValid(terms)) {

			ontologyDataManager.updateTerms(terms);

			view.showSaveSuccessMessage();

		} else {

			view.showValidationErrorMessage();
		}

	}


	/**
	 * Loads the list of Terms to the System Label table.
	 */
	protected void loadDataSystemLabelTable() {

		final Table systemLableTable = view.getTblSystemLabels();
		systemLableTable.removeAllItems();

		final List<Term> terms = this.retrieveSystemLabelTerms();
		((BeanItemContainer<Term>) systemLableTable.getContainerDataSource()).addAll(terms);
		systemLableTable.setPageLength(terms.size());

	}

	/**
	 * Gets the terms from the table.
	 * @return
	 */
	protected List<Term> retrieveTermsFromTable() {

		final Table systemLableTable = view.getTblSystemLabels();

		systemLableTable.commit();

		// Get the Terms from the table's container
		final BeanItemContainer<Term> container = (BeanItemContainer<Term>) systemLableTable.getContainerDataSource();
		return new ArrayList<Term>((Collection<Term>) container.getItemIds());

	}

	/**
	 * Checks if all terms are valid. Returns false if there is one or more term with empty name.
	 * @param terms
	 * @return
	 */
	protected boolean isAllTermsValid(final List<Term> terms) {
		for (final Term term : terms) {
			if (StringUtils.isNullOrEmpty(term.getName())) {
				return false;
			}
		}

		return true;

	}



}
