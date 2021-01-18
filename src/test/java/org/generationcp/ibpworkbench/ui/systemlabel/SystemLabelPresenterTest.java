package org.generationcp.ibpworkbench.ui.systemlabel;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;
import org.junit.Assert;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SystemLabelPresenterTest {

	private static final int SYSTEM_LABELS_TERM_RANGE_SIZE = 100;
	private static final int SYSTEM_LABELS_TEST_RECORDS_SIZE = 3;

	@Mock
	private SystemLabelView view;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@Mock
	private Table systemLabelTable;

	@InjectMocks
	private SystemLabelPresenter systemLabelPresenter = new SystemLabelPresenter(view);

	@Before
	public void init() {

		Mockito.when(ontologyDataManager.getTermsByIds(ArgumentMatchers.<List<Integer>>any())).thenReturn(this.createTerms());

		BeanItemContainer<Term> container = new BeanItemContainer<Term>(Term.class);
		container.addAll(this.createTerms());

		Mockito.when(view.getTblSystemLabels()).thenReturn(systemLabelTable);
		Mockito.when(systemLabelTable.getContainerDataSource()).thenReturn(container);

	}

	@Test
	public void testRetrieveSystemLabelTerms() {

		systemLabelPresenter.retrieveSystemLabelTerms();

		ArgumentCaptor<List> termIdListCaptor = ArgumentCaptor.forClass(List.class);

		// GetTermsByIds should be invoked
		Mockito.verify(ontologyDataManager).getTermsByIds(termIdListCaptor.capture());

		List<Integer> termIds = termIdListCaptor.getValue();

		Assert.assertEquals(SYSTEM_LABELS_TERM_RANGE_SIZE, termIds.size());


	}

	@Test
	public void testLoadDataSystemLabelTable() {

		systemLabelPresenter.loadDataSystemLabelTable();

		Mockito.verify(systemLabelTable).removeAllItems();
		Mockito.verify(systemLabelTable).setPageLength(3);

		Assert.assertEquals(SYSTEM_LABELS_TEST_RECORDS_SIZE, systemLabelTable.getContainerDataSource().size());

	}

	@Test
	public void testSaveTermsSuccessful() {

		systemLabelPresenter.saveTerms();

		Mockito.verify(ontologyDataManager).updateTerms(ArgumentMatchers.<List<Term>>any());
		Mockito.verify(view).showSaveSuccessMessage();

	}

	@Test
	public void testSaveTermsValidationError() {

		// Modify the name of the first Term item to simulate validation error
		Term term = (Term) systemLabelTable.getContainerDataSource().getItemIds().iterator().next();
		term.setName("");

		systemLabelPresenter.saveTerms();

		Mockito.verify(ontologyDataManager, Mockito.times(0)).updateTerms(ArgumentMatchers.<List<Term>>any());
		Mockito.verify(view).showValidationErrorMessage();

	}

	@Test
	public void testRetrieveTermsFromTable() {

		List<Term> terms = systemLabelPresenter.retrieveTermsFromTable();

		Assert.assertEquals(SYSTEM_LABELS_TEST_RECORDS_SIZE, terms.size());

		// Make sure that the table.commit is called so that data is updated with the user input.
		Mockito.verify(systemLabelTable).commit();

	}

	@Test
	public void testIsAllTermsValidTrue() {

		List<Term> terms = this.createTerms();
		Assert.assertTrue(systemLabelPresenter.isAllTermsValid(terms));

	}

	@Test
	public void testIsAllTermsValidFalse() {

		List<Term> terms = this.createTerms();
		// Set the first term's name to empty to simulate invalidity.
		terms.get(0).setName("");

		Assert.assertFalse(systemLabelPresenter.isAllTermsValid(terms));

	}

	private List<Term> createTerms() {

		List<Term> terms = new ArrayList<>();

		terms.add(this.creatTerm(TermId.AVAILABLE_INVENTORY.getId()));
		terms.add(this.creatTerm(TermId.LOT_ID_INVENTORY.getId()));
		terms.add(this.creatTerm(TermId.TOTAL_INVENTORY.getId()));

		return terms;

	}

	private Term creatTerm(final int i) {

		Term term = new Term();
		term.setId(i);
		term.setDefinition("DEFINITION " + i);
		term.setName("NAME" + i);
		return term;

	}

}
