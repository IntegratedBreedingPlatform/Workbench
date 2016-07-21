package org.generationcp.ibpworkbench.ui.systemlabel;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;
import junit.framework.Assert;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SystemLabelPresenterTest {

	private static final int TEST_TERMS_SIZE = 3;

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

		Mockito.when(ontologyDataManager.getTermsByIds(Mockito.anyList())).thenReturn(this.createTerms());
		// Simulate the system label termid values.
		systemLabelPresenter.setSystemLabelIds(
				String.format("%s,%s,%s", TermId.AVAILABLE_INVENTORY.getId(), TermId.LOT_ID_INVENTORY.getId(),
						TermId.TOTAL_INVENTORY.getId()));

		BeanItemContainer<Term> container = new BeanItemContainer<Term>(Term.class);
		Mockito.when(view.getTblSystemLabels()).thenReturn(systemLabelTable);
		Mockito.when(systemLabelTable.getContainerDataSource()).thenReturn(container);

	}

	@Test
	public void testRetrieveSystemLabelTerms() {

		systemLabelPresenter.retrieveSystemLabelTerms();

		ArgumentCaptor<List> termIdListCaptor = new ArgumentCaptor<>();

		// GetTermsByIds should be invoked
		Mockito.verify(ontologyDataManager).getTermsByIds(termIdListCaptor.capture());

		List<Integer> termIds = termIdListCaptor.getValue();

		Assert.assertEquals(TEST_TERMS_SIZE, termIds.size());
		Assert.assertEquals(TermId.AVAILABLE_INVENTORY.getId(), termIds.get(0).intValue());
		Assert.assertEquals(TermId.LOT_ID_INVENTORY.getId(), termIds.get(1).intValue());
		Assert.assertEquals(TermId.TOTAL_INVENTORY.getId(), termIds.get(2).intValue());

	}

	@Test
	public void testLoadDataSystemLabelTable() {

		systemLabelPresenter.loadDataSystemLabelTable();

		Mockito.verify(systemLabelTable).removeAllItems();
		Mockito.verify(systemLabelTable).setPageLength(3);

		Assert.assertEquals(TEST_TERMS_SIZE, systemLabelTable.getContainerDataSource().size());

	}

	@Test
	public void testSaveTerms() {

		Container container = systemLabelTable.getContainerDataSource();

		systemLabelPresenter.saveTerms();

		Mockito.verify(systemLabelTable).commit();

		Mockito.verify(ontologyDataManager).updateTerms(new ArrayList<Term>((Collection<Term>) container.getItemIds()));

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
