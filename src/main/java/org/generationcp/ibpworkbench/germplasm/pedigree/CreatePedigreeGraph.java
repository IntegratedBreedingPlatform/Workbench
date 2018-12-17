
package org.generationcp.ibpworkbench.germplasm.pedigree;

import com.vaadin.ui.Window;
import org.generationcp.ibpworkbench.germplasm.GermplasmQueries;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;

import java.io.File;

public class CreatePedigreeGraph {

	private final GermplasmQueries qQuery;
	private GraphVizUtility gv;
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
		this.gv = new GraphVizUtility();
		this.create(graphName, this.gv);
	}

	/**
	 * Construct a DOT graph in memory, convert it to image and store the image in the file system.
	 *
	 * @param graphName
	 * @param gv
	 */
	public void create(final String graphName, final GraphVizUtility gv) {
		this.gv = gv;
		this.gv.initialize();
		this.gv.setImageOutputPath(GraphVizUtility.createImageOutputPathForWindow(this.window));
		this.gv.addln(GraphVizUtility.START_GRAPH);

		this.createDiGraphNode();
		this.gv.addln(GraphVizUtility.END_GRAPH);

		final String type = "png";

		// Load the directory as a resource
		final File out = new File(this.gv.graphVizOutputPath(graphName + "." + type));
		// create graph
		this.gv.writeGraphToFile(this.gv.getGraph(this.gv.getDotSource(), type), out);

	}

	private void createDiGraphNode() {
		final GermplasmPedigreeTree germplasmPedigreeTree =
			this.qQuery.generatePedigreeTree(Integer.valueOf(this.gid), this.level, this.includeDerivativeLines);

		if (this.level == 1) {
			final String leafNodeGIDRoot = this.createNodeTextWithFormatting(germplasmPedigreeTree.getRoot());
			this.gv.addln(leafNodeGIDRoot + ";");
		} else {
			this.addNode(germplasmPedigreeTree.getRoot(), 1);
		}
	}

	private String createNodeTextWithFormatting(final GermplasmPedigreeTreeNode node) {
		final String leafNodeGIDRoot = node.getGermplasm().getGid().toString();
		final String leafNodeLabelRoot =
			node.getGermplasm().getPreferredName().getNval() + "\n" + "GID: " + node.getGermplasm().getGid().toString();
		this.gv.addln(leafNodeGIDRoot + " [shape=box];");
		this.gv.addln(leafNodeGIDRoot + " [label=\"" + leafNodeLabelRoot + "\", fontname=\"Helvetica\", fontsize=12.0, ordering=\"in\"];");
		return leafNodeGIDRoot;
	}

	private void addNode(final GermplasmPedigreeTreeNode node, final int level) {

		if (node.getLinkedNodes().isEmpty()) {
			final String leafNodeGIDRoot = this.createNodeTextWithFormatting(node);
			this.gv.addln(leafNodeGIDRoot + ";");
		}

		for (final GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {

			if (!"0".equalsIgnoreCase(parent.getGermplasm().getGid().toString())) {

				final String leafNodeGID = this.createNodeTextWithFormatting(parent);
				final String parentNodeGID = this.createNodeTextWithFormatting(node);

				if (level == 1) {
					final String leafNodeGIDRoot = this.createNodeTextWithFormatting(node);
					this.gv.addln(leafNodeGID + "->" + leafNodeGIDRoot + ";");
				} else {
					this.gv.addln(leafNodeGID + "->" + parentNodeGID + ";");
				}
			}

			this.addNode(parent, level + 1);
		}
	}

}
