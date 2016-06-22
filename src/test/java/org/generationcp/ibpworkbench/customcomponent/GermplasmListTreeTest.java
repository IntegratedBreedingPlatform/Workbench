
package org.generationcp.ibpworkbench.customcomponent;

import org.generationcp.ibpworkbench.customfields.ListTreeComponent;
import org.generationcp.ibpworkbench.customfields.LocalListFoldersTreeComponent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.vaadin.ui.AbstractSelect;

public class GermplasmListTreeTest {

	private GermplasmListTree listTree;
	private ListTreeComponent listTreeComponent;

	@Before
	public void setUp() {
		this.listTree = Mockito.mock(GermplasmListTree.class);

		this.listTreeComponent = new LocalListFoldersTreeComponent(1);
		this.listTreeComponent.setGermplasmListSource(this.listTree);
	}

	@Test
	public void testLocalTreeItemDescription() {
		this.listTreeComponent.addListTreeItemDescription();

		Mockito.verify(this.listTree).setItemDescriptionGenerator(Matchers.any(AbstractSelect.ItemDescriptionGenerator.class));
	}

}
