package org.generationcp.ibpworkbench.germplasm;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.ibpworkbench.germplasm.containers.GermplasmIndexContainer;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class GermplasmPedigreeTreeComponentTest {
	
	private static final Integer GID = new Random().nextInt();
	
	@Mock
	private GermplasmQueries germplasmQueries;
	
	@Mock
	private VerticalLayout layout;
	
	@Mock
	private TabSheet tabSheet;
	
	@Mock
	private GermplasmPedigreeTree tree;
	
	private GermplasmPedigreeTreeComponent pedigreeTreeComponent;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final GermplasmIndexContainer indexContainer = new GermplasmIndexContainer(germplasmQueries);
		Mockito.doReturn(this.tree).when(this.germplasmQueries).generatePedigreeTree(GID, 1, false);
		Mockito.doReturn(this.getTreeNode(GID)).when(this.tree).getRoot();
		this.pedigreeTreeComponent = new GermplasmPedigreeTreeComponent(GID, germplasmQueries, indexContainer, layout, tabSheet);
	}
	
	@Test
	public void testGetNodeLabel() {
		final int id = new Random().nextInt();
		final GermplasmPedigreeTreeNode node = getTreeNode(id);
		Assert.assertEquals(node.getGermplasm().getPreferredName().getNval() + "(" + id + ")", this.pedigreeTreeComponent.getNodeLabel(node));
	}
	
	@Test
	public void testGetNodeLabelWhenGidEqualsZero() {
		final int id = 0;
		final GermplasmPedigreeTreeNode node = getTreeNode(id);
		Assert.assertEquals(Name.UNKNOWN, this.pedigreeTreeComponent.getNodeLabel(node));
	}
	
	private GermplasmPedigreeTreeNode getTreeNode(int gid) {
		final GermplasmPedigreeTreeNode node = new GermplasmPedigreeTreeNode();
		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(gid);
		final Name preferredName = new Name();
		preferredName.setNval(gid == 0? Name.UNKNOWN : RandomStringUtils.randomAlphabetic(20));
		germplasm.setPreferredName(preferredName);
		node.setGermplasm(germplasm);
		return node;
	}

}
