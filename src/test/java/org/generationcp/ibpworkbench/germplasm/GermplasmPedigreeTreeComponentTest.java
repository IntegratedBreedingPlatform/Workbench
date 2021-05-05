package org.generationcp.ibpworkbench.germplasm;

import com.vaadin.data.Item;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

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

	private GermplasmIndexContainer indexContainer;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.indexContainer = new GermplasmIndexContainer(germplasmQueries);
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

	@Test
	public void testDerivativeUnknownMaleParent() {

		final GermplasmPedigreeTreeNode nodeWithUnknownParent = this.getTreeNodeDerivativeUnknownMaleKnownFemale();
		Mockito.doReturn(this.tree).when(this.germplasmQueries).generatePedigreeTree(nodeWithUnknownParent.getGermplasm().getGid(), 1, false);
		Mockito.doReturn(nodeWithUnknownParent).when(this.tree).getRoot();
		final GermplasmPedigreeTreeComponent pedigreeTreeComponentUnknownMaleParent = new GermplasmPedigreeTreeComponent(nodeWithUnknownParent.getGermplasm().getGid(),
				this.germplasmQueries, this.indexContainer, this.layout, this.tabSheet);

		final String caption = pedigreeTreeComponentUnknownMaleParent.getItemCaption(nodeWithUnknownParent.getGermplasm().getGid().toString());
		Assert.assertEquals(pedigreeTreeComponentUnknownMaleParent.getNodeLabel(nodeWithUnknownParent),caption);
		Assert.assertEquals(1, nodeWithUnknownParent.getLinkedNodes().size());

		final GermplasmPedigreeTreeNode nodeMaleParent = nodeWithUnknownParent.getLinkedNodes().get(0);
		final Germplasm germplasmMaleParent = nodeMaleParent.getGermplasm();
		final String keyMaleParent = nodeWithUnknownParent.getGermplasm().getGid().toString() + "@" +
				germplasmMaleParent.getGid();
		final Item maleParentItem = pedigreeTreeComponentUnknownMaleParent.getItem(keyMaleParent);
		Assert.assertNull(maleParentItem);

		final GermplasmPedigreeTreeNode nodeFemaleParent = nodeMaleParent.getLinkedNodes().get(0);
		final Germplasm germplasmFemaleParent = nodeFemaleParent.getGermplasm();
		final String keyFemaleParent = germplasmMaleParent.getGid().toString() + "@" +
				germplasmFemaleParent.getGid().toString();
		final String femaleParentCaption = pedigreeTreeComponentUnknownMaleParent.getItemCaption(keyFemaleParent);
		Assert.assertEquals(pedigreeTreeComponentUnknownMaleParent.getNodeLabel(nodeFemaleParent), femaleParentCaption);

		// Level 3
		Mockito.doReturn(this.tree).when(this.germplasmQueries).generatePedigreeTree(nodeFemaleParent.getGermplasm().getGid(), 2, false);
		Mockito.doReturn(nodeFemaleParent).when(this.tree).getRoot();
		pedigreeTreeComponentUnknownMaleParent.pedigreeTreeExpandAction(keyFemaleParent);
		//Grand Parent
		final String keyFGrandParent = germplasmFemaleParent.getGid().toString() + "@" +
				nodeFemaleParent.getLinkedNodes().get(0).getGermplasm().getGid().toString();
		final Item itemGrandParent = pedigreeTreeComponentUnknownMaleParent.getItem(keyFGrandParent);
		Assert.assertNotNull(itemGrandParent);
	}

	@Test
	public void testGenerativeUnknownFemaleParent() {
		final GermplasmPedigreeTreeNode nodeWithUnknownParent = this.getUnknownParent(false);
		Mockito.doReturn(this.tree).when(this.germplasmQueries).generatePedigreeTree(nodeWithUnknownParent.getGermplasm().getGid(), 1, false);
		Mockito.doReturn(nodeWithUnknownParent).when(this.tree).getRoot();
		final GermplasmPedigreeTreeComponent pedigreeTreeComponentUnknownFemaleParent = new GermplasmPedigreeTreeComponent(nodeWithUnknownParent.getGermplasm().getGid(),
				this.germplasmQueries, this.indexContainer, this.layout, this.tabSheet);

		final String caption = pedigreeTreeComponentUnknownFemaleParent.getItemCaption(nodeWithUnknownParent.getGermplasm().getGid().toString());
		Assert.assertEquals(pedigreeTreeComponentUnknownFemaleParent.getNodeLabel(nodeWithUnknownParent),caption);
		Assert.assertEquals(2, nodeWithUnknownParent.getLinkedNodes().size());

		// Unknown Female Parent
		final GermplasmPedigreeTreeNode nodeFemaleParent = nodeWithUnknownParent.getLinkedNodes().get(0);
		final Germplasm germplasmFemaleParent = nodeFemaleParent.getGermplasm();
		final String keyFemaleParent = nodeWithUnknownParent.getGermplasm().getGid().toString() + "@" +
				germplasmFemaleParent.getGid();
		final Item maleParentItem = pedigreeTreeComponentUnknownFemaleParent.getItem(keyFemaleParent);
		final String femaleParentCaption = pedigreeTreeComponentUnknownFemaleParent.getItemCaption(keyFemaleParent);

		Assert.assertNotNull(maleParentItem);
		Assert.assertEquals(pedigreeTreeComponentUnknownFemaleParent.getNodeLabel(nodeFemaleParent), femaleParentCaption);


		// Known Male Parent
		final GermplasmPedigreeTreeNode nodeMaleParent = nodeWithUnknownParent.getLinkedNodes().get(1);
		final Germplasm germplasmMaleParent = nodeMaleParent.getGermplasm();
		final String keyMaleParent =  nodeWithUnknownParent.getGermplasm().getGid().toString() + "@" +
				germplasmMaleParent.getGid();
		final String maleParentCaption = pedigreeTreeComponentUnknownFemaleParent.getItemCaption(keyMaleParent);
		Assert.assertEquals(pedigreeTreeComponentUnknownFemaleParent.getNodeLabel(nodeMaleParent), maleParentCaption);

		// Known Male Parent with Grand Parent Level 3
		Mockito.doReturn(this.tree).when(this.germplasmQueries).generatePedigreeTree(nodeMaleParent.getGermplasm().getGid(), 2, false);
		Mockito.doReturn(nodeMaleParent).when(this.tree).getRoot();
		pedigreeTreeComponentUnknownFemaleParent.pedigreeTreeExpandAction(keyMaleParent);
		//Grand Parent
		final String keyMGrandParent = germplasmMaleParent.getGid().toString() + "@" +
				nodeMaleParent.getLinkedNodes().get(0).getGermplasm().getGid().toString();
		final Item itemGrandParent = pedigreeTreeComponentUnknownFemaleParent.getItem(keyMGrandParent);
		Assert.assertNotNull(itemGrandParent);

	}

	@Test
	public void testGenerativeUnknownMaleParent() {
		final GermplasmPedigreeTreeNode nodeWithUnknownParent = this.getUnknownParent(true);
		Mockito.doReturn(this.tree).when(this.germplasmQueries).generatePedigreeTree(nodeWithUnknownParent.getGermplasm().getGid(), 1, false);
		Mockito.doReturn(nodeWithUnknownParent).when(this.tree).getRoot();
		final GermplasmPedigreeTreeComponent pedigreeTreeComponentUnknownFemaleParent = new GermplasmPedigreeTreeComponent(nodeWithUnknownParent.getGermplasm().getGid(),
				this.germplasmQueries, this.indexContainer, this.layout, this.tabSheet);

		final String caption = pedigreeTreeComponentUnknownFemaleParent.getItemCaption(nodeWithUnknownParent.getGermplasm().getGid().toString());
		Assert.assertEquals(pedigreeTreeComponentUnknownFemaleParent.getNodeLabel(nodeWithUnknownParent),caption);
		Assert.assertEquals(2, nodeWithUnknownParent.getLinkedNodes().size());

		// Known Female
		final GermplasmPedigreeTreeNode nodeFemaleParent = nodeWithUnknownParent.getLinkedNodes().get(0);
		final Germplasm germplasmFemaleParent = nodeFemaleParent.getGermplasm();
		final String keyFemaleParent = nodeWithUnknownParent.getGermplasm().getGid().toString() + "@" +
				germplasmFemaleParent.getGid().toString();
		final String femaleParentCaption = pedigreeTreeComponentUnknownFemaleParent.getItemCaption(keyFemaleParent);
		Assert.assertEquals(pedigreeTreeComponentUnknownFemaleParent.getNodeLabel(nodeFemaleParent), femaleParentCaption);

		// Known Female with Grand Parent Level 3
		Mockito.doReturn(this.tree).when(this.germplasmQueries).generatePedigreeTree(nodeFemaleParent.getGermplasm().getGid(), 2, false);
		Mockito.doReturn(nodeFemaleParent).when(this.tree).getRoot();
		pedigreeTreeComponentUnknownFemaleParent.pedigreeTreeExpandAction(keyFemaleParent);
		//Grand Parent
		final String keyFGrandParent = germplasmFemaleParent.getGid().toString() + "@" +
				nodeFemaleParent.getLinkedNodes().get(0).getGermplasm().getGid().toString();
		final Item itemGrandParent = pedigreeTreeComponentUnknownFemaleParent.getItem(keyFGrandParent);
		Assert.assertNotNull(itemGrandParent);

		// Uknown Male Parent
		final GermplasmPedigreeTreeNode nodeMaleParent = nodeWithUnknownParent.getLinkedNodes().get(1);
		final Germplasm germplasmMaleParent = nodeMaleParent.getGermplasm();
		final String keyMaleParent = nodeWithUnknownParent.getGermplasm().getGid().toString() + "@" +
				germplasmMaleParent.getGid();
		final Item maleParentItem = pedigreeTreeComponentUnknownFemaleParent.getItem(keyMaleParent);
		final String maleParentItemCaption = pedigreeTreeComponentUnknownFemaleParent.getItemCaption(keyMaleParent);
		Assert.assertNotNull(maleParentItem);
		Assert.assertEquals(pedigreeTreeComponentUnknownFemaleParent.getNodeLabel(nodeMaleParent), maleParentItemCaption);


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

	private GermplasmPedigreeTreeNode getTreeNodeDerivativeUnknownMaleKnownFemale() {
		final GermplasmPedigreeTreeNode node = this.getTreeNode(new Random().nextInt());

		// Known Female with grand Parent
		final GermplasmPedigreeTreeNode femaleParent = this.getTreeNode(1234567);
		femaleParent.setLinkedNodes(Collections.singletonList(this.getTreeNode(7896)));

		// Unknown Male
		final GermplasmPedigreeTreeNode maleParent = this.getTreeNode(0);
		maleParent.setLinkedNodes(Collections.singletonList(femaleParent));

		node.setLinkedNodes(Collections.singletonList(maleParent));

		return node;
	}

	private GermplasmPedigreeTreeNode getUnknownParent(final boolean isUnknownMale) {

		GermplasmPedigreeTreeNode maleParent;
		GermplasmPedigreeTreeNode femaleParent;
		if (isUnknownMale) {
			maleParent = this.getTreeNode(0);
			femaleParent = this.getTreeNode(1234567);
			femaleParent.setLinkedNodes(Collections.singletonList(this.getTreeNode(7654)));
		} else {
			maleParent = this.getTreeNode(1234567);
			maleParent.setLinkedNodes(Collections.singletonList(this.getTreeNode(7654)));
			femaleParent = this.getTreeNode(0);
		}

		final GermplasmPedigreeTreeNode node = this.getTreeNode(new Random().nextInt());
		node.setLinkedNodes(Arrays.asList(femaleParent, maleParent));
		return node;
	}
}
