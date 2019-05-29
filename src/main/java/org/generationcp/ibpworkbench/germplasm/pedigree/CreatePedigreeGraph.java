
package org.generationcp.ibpworkbench.germplasm.pedigree;

import com.vaadin.ui.Window;
import org.generationcp.ibpworkbench.germplasm.GermplasmQueries;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;

import java.io.File;

public class CreatePedigreeGraph {

	private final GermplasmQueries qQuery;
	private GraphVizUtility graphVizUtility;
	private final int gid;
	private final int level;
	private final Window window;
	private boolean includeDerivativeLines;

	public CreatePedigreeGraph(
		final int gid, final int level, final Boolean includeDerivativeLines, final Window window,
		final GermplasmQueries qQuery) {
		this.qQuery = qQuery;
		this.gid = gid;
		this.level = level;
		this.window = window;
		this.includeDerivativeLines = includeDerivativeLines;
	}

	public CreatePedigreeGraph(final int gid, final int level, final Window window, final GermplasmQueries qQuery) {
		this.qQuery = qQuery;
		this.gid = gid;
		this.level = level;
		this.window = window;
	}

	/**
	 * Construct a DOT graph in memory, convert it to image and store the image in the file system.
	 *
	 * @param graphName
	 */
	public void create(final String graphName) {
		this.graphVizUtility = new GraphVizUtility();
		this.create(graphName, this.graphVizUtility);
	}

	/**
	 * Construct a DOT graph in memory, convert it to image and store the image in the file system.
	 *
	 * @param graphName
	 * @param gv
	 */
	public void create(final String graphName, final GraphVizUtility gv) {
		this.graphVizUtility = gv;
		this.graphVizUtility.initialize();
		this.graphVizUtility.setImageOutputPath(GraphVizUtility.createImageOutputPathForWindow(this.window));
		this.graphVizUtility.addln(GraphVizUtility.START_GRAPH);

		this.createDiGraphNode();
		this.graphVizUtility.addln(GraphVizUtility.END_GRAPH);

		final String type = "png";

		// Load the directory as a resource
		final File out = new File(this.graphVizUtility.graphVizOutputPath(graphName + "." + type));
		// create graph
		this.graphVizUtility.writeGraphToFile(this.graphVizUtility.getGraph(this.graphVizUtility.getDotSource(), type), out);

	}

	private void createDiGraphNode() {
		final GermplasmPedigreeTree germplasmPedigreeTree =
			this.qQuery.generatePedigreeTree(Integer.valueOf(this.gid), this.level, this.includeDerivativeLines);

		if (this.level == 1) {
			final String leafNodeGIDRoot = this.createNodeTextWithFormatting(germplasmPedigreeTree.getRoot());
			this.graphVizUtility.addln(leafNodeGIDRoot + ";");
		} else {
			this.addNode(germplasmPedigreeTree.getRoot(), 1);
		}
	}

	String createNodeTextWithFormatting(final GermplasmPedigreeTreeNode node) {
		final Integer nodeGid = node.getGermplasm().getGid();
		final String leafNodeGIDRoot = nodeGid.toString();
		final String preferredName = node.getGermplasm().getPreferredName().getNval();
		final StringBuilder sb = new StringBuilder(preferredName);
		// The breakline " \n" is needed otherwise the "UNKNOWN" text for GID = 0 becomes truncated at the bottom
		sb.append(" \n ");
		if (nodeGid != 0) {
			sb.append("GID: " + leafNodeGIDRoot);
		}
		this.graphVizUtility.addln(leafNodeGIDRoot + " [shape=box];");
		this.graphVizUtility.addln(leafNodeGIDRoot + " [label=\"" + sb.toString() + "\", fontname=\"Helvetica\", fontsize=12.0, ordering=\"in\"];");
		return leafNodeGIDRoot;
	}

	private void addNode(final GermplasmPedigreeTreeNode node, final int level) {

		if (node.getLinkedNodes().isEmpty()) {
			final String leafNodeGIDRoot = this.createNodeTextWithFormatting(node);
			this.graphVizUtility.addln(leafNodeGIDRoot + ";");
		}

		for (final GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
			final String leafNodeGID = this.createNodeTextWithFormatting(parent);
			final String parentNodeGID = this.createNodeTextWithFormatting(node);

			if (level == 1) {
				final String leafNodeGIDRoot = this.createNodeTextWithFormatting(node);
				this.graphVizUtility.addln(leafNodeGID + "->" + leafNodeGIDRoot + ";");
			} else {
				this.graphVizUtility.addln(leafNodeGID + "->" + parentNodeGID + ";");
			}

			this.addNode(parent, level + 1);
		}
	}

	
	protected void setGraphVizUtility(GraphVizUtility graphVizUtility) {
		this.graphVizUtility = graphVizUtility;
	}

}
