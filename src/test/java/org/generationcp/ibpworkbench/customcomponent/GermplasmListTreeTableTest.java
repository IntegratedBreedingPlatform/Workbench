
package org.generationcp.ibpworkbench.customcomponent;

import org.generationcp.ibpworkbench.crossingmanager.CrossingManagerListTreeComponent;
import org.generationcp.ibpworkbench.crossingmanager.CrossingManagerMakeCrossesComponent;
import org.generationcp.ibpworkbench.crossingmanager.listeners.CrossingManagerTreeActionsListener;
import org.generationcp.ibpworkbench.listmanager.ListManagerTreeComponent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.vaadin.ui.AbstractSelect;

public class GermplasmListTreeTableTest {

	private GermplasmListTreeTable listTreeTable;
	private ListManagerTreeComponent listManagerTreeComponent;
	private CrossingManagerListTreeComponent crossManagerTreeComponent;

	@Before
	public void setUp() {
		this.listTreeTable = Mockito.mock(GermplasmListTreeTable.class);
	}

	@Test
	public void testListManagerItemDescription() {
		this.listManagerTreeComponent = new ListManagerTreeComponent();
		this.listManagerTreeComponent.setGermplasmListSource(this.listTreeTable);

		this.listManagerTreeComponent.addListTreeItemDescription();

		Mockito.verify(this.listTreeTable, Mockito.never()).setItemDescriptionGenerator(
				Matchers.any(AbstractSelect.ItemDescriptionGenerator.class));
	}

	@Test
	public void testCrossingManagerItemDescription() {
		CrossingManagerTreeActionsListener listener = Mockito.mock(CrossingManagerTreeActionsListener.class);
		CrossingManagerMakeCrossesComponent crossesComponent = Mockito.mock(CrossingManagerMakeCrossesComponent.class);

		this.crossManagerTreeComponent = new CrossingManagerListTreeComponent(listener, crossesComponent);
		this.crossManagerTreeComponent.setGermplasmListSource(this.listTreeTable);

		this.crossManagerTreeComponent.addListTreeItemDescription();

		Mockito.verify(this.listTreeTable, Mockito.never()).setItemDescriptionGenerator(
				Matchers.any(AbstractSelect.ItemDescriptionGenerator.class));
	}

}
