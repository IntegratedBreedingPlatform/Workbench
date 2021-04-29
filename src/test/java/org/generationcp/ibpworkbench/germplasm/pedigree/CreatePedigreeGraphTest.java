
package org.generationcp.ibpworkbench.germplasm.pedigree;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.ui.Window;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.ibpworkbench.germplasm.GermplasmQueries;
import org.generationcp.middleware.data.initializer.GermplasmPedigreeTreeTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

public class CreatePedigreeGraphTest {

	private static final String PATH = "C:\\SamplePath";
	private static final String FILE_TYPE = "png";
	private static final String SAMPLE_GRAPH = "SampleGraph";
	private static final int LEVEL = 3;
	private static final int GID = 1000;
	@Mock
	private GermplasmQueries qQuery;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private GraphVizUtility graphVizUtility;

	@Mock
	private Window window;

	@Mock
	private Application application;

	@Mock
	private ApplicationContext applicationContext;

	private CreatePedigreeGraph createPedigreeGraph;

	private final File sampleFile = new File("Sample File");
	private GermplasmPedigreeTree germplasmPedigreeTree;
	private GermplasmPedigreeTreeTestDataInitializer germplasmPedigreeTreeTDI;

	@Before
	public void setUp() throws URISyntaxException {
		MockitoAnnotations.initMocks(this);
		this.createPedigreeGraph = new CreatePedigreeGraph(GID, LEVEL, this.window, this.qQuery, this.germplasmDataManager);
		this.createPedigreeGraph.setGraphVizUtility(this.graphVizUtility);

		Mockito.doReturn(this.window).when(this.window).getWindow();
		Mockito.doReturn(this.application).when(this.window).getApplication();
		Mockito.doReturn(this.applicationContext).when(this.application).getContext();
		Mockito.doReturn(this.sampleFile).when(this.applicationContext).getBaseDirectory();

		this.germplasmPedigreeTreeTDI = new GermplasmPedigreeTreeTestDataInitializer();
		this.germplasmPedigreeTree = this.germplasmPedigreeTreeTDI.createGermplasmPedigreeTree(GID, LEVEL);
		Mockito.doReturn(this.germplasmPedigreeTree).when(this.qQuery).generatePedigreeTree(Integer.valueOf(GID), LEVEL, false);

		Mockito.doReturn(PATH).when(this.graphVizUtility).graphVizOutputPath(SAMPLE_GRAPH + "." + FILE_TYPE);
	}

	@Test
	public void testCreate() {
		this.createPedigreeGraph.create(SAMPLE_GRAPH, this.graphVizUtility);
		this.verifyIfEveryLinkedNodeHasBeenAdded(this.germplasmPedigreeTree.getRoot());
	}

	@Test
	public void testCreateNodeTextWithFormatting() {
		final Integer id = new Random().nextInt();
		final GermplasmPedigreeTreeNode node = this.getTreeNode(id);
		final String result = this.createPedigreeGraph.createNodeTextWithFormatting(node);

		final String idString = id.toString();
		Assert.assertEquals(idString, result);
		final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.graphVizUtility, Mockito.times(2)).addln(stringCaptor.capture());
		final List<String> allValues = stringCaptor.getAllValues();
		Assert.assertEquals(idString + " [shape=box];", allValues.get(0));
		final String display = node.getGermplasm().getPreferredName().getNval() + " \n GID: " + idString;
		Assert.assertEquals(idString + " [label=\"" + display + "\", fontname=\"Helvetica\", fontsize=12.0, ordering=\"in\"];", allValues.get(1));
	}

	@Test
	public void testCreateNodeTextWithFormattingWhenGidIsZero() {
		final Integer id = 0;
		final GermplasmPedigreeTreeNode node = this.getTreeNode(id);
		final String result = this.createPedigreeGraph.createNodeTextWithFormatting(node);

		final String idString = id.toString();
		Assert.assertEquals(idString, result);
		final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		Mockito.verify(this.graphVizUtility, Mockito.times(2)).addln(stringCaptor.capture());
		final List<String> allValues = stringCaptor.getAllValues();
		Assert.assertEquals(idString + " [shape=box, style=dashed];", allValues.get(0));
		final String display = node.getGermplasm().getPreferredName().getNval() + " \n ";
		Assert.assertEquals(idString + " [label=\"" + display + "\", fontname=\"Helvetica\", fontsize=12.0, ordering=\"in\"];", allValues.get(1));
	}

	@Test
	public void testCreateUnkownMaleParentDerivative() {
		this.createPedigreeGraph = new CreatePedigreeGraph(GID, LEVEL, true, false, this.window, this.qQuery, this.germplasmDataManager);
		this.createPedigreeGraph.setGraphVizUtility(this.graphVizUtility);
		this.germplasmPedigreeTree = this.germplasmPedigreeTreeTDI.createGermplasmPedigreeTreeDerivativeUnkownMale(GID, LEVEL);
		Mockito.doReturn(this.germplasmPedigreeTree).when(this.qQuery).generatePedigreeTree(GID, LEVEL, true);

		this.createPedigreeGraph.create(SAMPLE_GRAPH, this.graphVizUtility);
		this.verifyIfEveryLinkedNodeHasBeenAdded(this.germplasmPedigreeTree.getRoot());

	}

	private void verifyIfEveryLinkedNodeHasBeenAdded(final GermplasmPedigreeTreeNode node) {
		if (node.getLinkedNodes().isEmpty()) {
			Mockito.verify(this.graphVizUtility, Mockito.atLeast(1)).addln(node.getGermplasm().getGid() + ";");
		} else {
			for (final GermplasmPedigreeTreeNode parentNode : node.getLinkedNodes()) {
				if(node.getGermplasm().getGpid1().equals(node.getGermplasm().getGpid2())) {
					Mockito.verify(this.graphVizUtility, Mockito.atLeast(1)).addln(
						parentNode.getGermplasm().getGid() + "->" + node.getGermplasm().getGid() + ";");
				} else if(node.getGermplasm().getGpid1().equals(parentNode.getGermplasm().getGid())) {
					Mockito.verify(this.graphVizUtility, Mockito.atLeast(1)).addln(
						parentNode.getGermplasm().getGid() + "->" + node.getGermplasm().getGid() + " [color=\"RED\", arrowhead=\"odottee\"];");
				} else if(node.getGermplasm().getGpid2().equals(parentNode.getGermplasm().getGid())) {
					Mockito.verify(this.graphVizUtility, Mockito.atLeast(1)).addln(
						parentNode.getGermplasm().getGid() + "->" + node.getGermplasm().getGid() + " [color=\"BLUE\", arrowhead=\"veeodot\"];");
				} else if (node.getGermplasm().getGnpgs() == null){
					Mockito.verify(this.graphVizUtility, Mockito.atLeast(1)).addln(
							parentNode.getGermplasm().getGid() + "->" + node.getGermplasm().getGid() + ";");
				}

				this.verifyIfEveryLinkedNodeHasBeenAdded(parentNode);
			}
		}
	}

	private GermplasmPedigreeTreeNode getTreeNode(final int gid) {
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
