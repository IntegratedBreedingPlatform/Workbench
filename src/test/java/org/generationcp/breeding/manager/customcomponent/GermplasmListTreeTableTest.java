
package org.generationcp.breeding.manager.customcomponent;

import com.vaadin.ui.AbstractSelect;
import org.generationcp.breeding.manager.crossingmanager.CrossingManagerListTreeComponent;
import org.generationcp.breeding.manager.crossingmanager.listeners.CrossingManagerTreeActionsListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class GermplasmListTreeTableTest {

	private GermplasmListTreeTable listTreeTable;
	private CrossingManagerListTreeComponent crossManagerTreeComponent;

	@Before
	public void setUp() {
		this.listTreeTable = Mockito.mock(GermplasmListTreeTable.class);
	}

	@Test
	public void testCrossingManagerItemDescription() {
		CrossingManagerTreeActionsListener listener = Mockito.mock(CrossingManagerTreeActionsListener.class);

		this.crossManagerTreeComponent = new CrossingManagerListTreeComponent(listener);
		this.crossManagerTreeComponent.setGermplasmListSource(this.listTreeTable);

		Mockito.verify(this.listTreeTable, Mockito.never()).setItemDescriptionGenerator(
				Matchers.any(AbstractSelect.ItemDescriptionGenerator.class));
	}

}
