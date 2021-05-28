/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.germplasm;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.breeding.manager.listmanager.GermplasmDetailsUrlService;
import org.generationcp.commons.util.Util;
import org.generationcp.ibpworkbench.germplasm.containers.GermplasmIndexContainer;
import org.generationcp.ibpworkbench.germplasm.listeners.GermplasmTreeExpandListener;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class GermplasmPedigreeTreeComponent extends Tree {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(GermplasmPedigreeTreeComponent.class);
	private GermplasmPedigreeTree germplasmPedigreeTree;
	private GermplasmQueries qQuery;
	private VerticalLayout mainLayout;
	private TabSheet tabSheet;
	private GermplasmIndexContainer dataIndexContainer;
	private Boolean includeDerivativeLines;

	@Autowired
	private GermplasmDetailsUrlService germplasmDetailsUrlService;

	public GermplasmPedigreeTreeComponent(final int gid, final GermplasmQueries qQuery,
		final GermplasmIndexContainer dataResultIndexContainer,
		final VerticalLayout mainLayout, final TabSheet tabSheet) {

		super();

		this.initializeTree(gid, qQuery, dataResultIndexContainer, mainLayout, tabSheet, false);

	}

	public GermplasmPedigreeTreeComponent(final int gid, final GermplasmQueries qQuery,
		final GermplasmIndexContainer dataResultIndexContainer,
		final VerticalLayout mainLayout, final TabSheet tabSheet, final Boolean includeDerivativeLines) {

		super();

		this.initializeTree(gid, qQuery, dataResultIndexContainer, mainLayout, tabSheet, includeDerivativeLines);
	}

	private void initializeTree(final int gid, final GermplasmQueries qQuery, final GermplasmIndexContainer dataResultIndexContainer,
		final VerticalLayout mainLayout, final TabSheet tabSheet, final Boolean includeDerivativeLines) {
		this.mainLayout = mainLayout;
		this.tabSheet = tabSheet;
		this.qQuery = qQuery;
		this.dataIndexContainer = dataResultIndexContainer;

		this.includeDerivativeLines = includeDerivativeLines;

		this.setSizeFull();
		this.germplasmPedigreeTree = qQuery.generatePedigreeTree(Integer.valueOf(gid), 1, includeDerivativeLines);
		this.addNode(this.germplasmPedigreeTree.getRoot(), 1);
		this.setImmediate(false);

		this.setSelectable(false);
		this.setItemStyleGenerator(new ItemStyleGenerator() {

			@Override
			public String getStyle(final Object itemId) {
				return "link";
			}
		});

		this.addListener(new ItemClickEvent.ItemClickListener() {

			private static final long serialVersionUID = -6626097251439208783L;

			@Override
			public void itemClick(final ItemClickEvent event) {
				final String[] itemGids = event.getItemId().toString().split("@");

				// TODO: check if we can avoid to concatenate the parent gid and child gid for itemId
				// If the itemId contains both the parent gid and child gid (i.e. [parentGid]@[childGid]), then only get the child Id
				final String gid = itemGids.length == 1 ? itemGids[0] : itemGids[1];
				GermplasmPedigreeTreeComponent.this
					.getWindow().open(GermplasmPedigreeTreeComponent.this.germplasmDetailsUrlService
					.getExternalResource(Integer.parseInt(gid), false), "_blank", false);
			}
		});

		this.addListener(new GermplasmTreeExpandListener(this));

	}

	private void addNode(final GermplasmPedigreeTreeNode node, final int level) {
		if (level == 1) {
			final String leafNodeId = node.getGermplasm().getGid().toString();
			this.addItem(leafNodeId);
			this.setItemCaption(leafNodeId, this.getNodeLabel(node));
			this.setParent(leafNodeId, leafNodeId);
			this.setChildrenAllowed(leafNodeId, true);

		}

		for (final GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
			final String leafNodeId = node.getGermplasm().getGid().toString();
			final Integer gid = parent.getGermplasm().getGid();

			if (gid.equals(0) && !Util.isEmpty(parent.getLinkedNodes())) {
				// If unknown with children
				this.addNode(parent, leafNodeId);
			} else {
				final String parentNodeId = node.getGermplasm().getGid() + "@" + gid;
				this.addItem(parentNodeId);
				this.setItemCaption(parentNodeId, this.getNodeLabel(parent));
				this.setParent(parentNodeId, leafNodeId);
				this.setChildrenAllowed(parentNodeId, true);

				this.addNode(parent, level + 1);
			}

		}
	}

	String getNodeLabel(final GermplasmPedigreeTreeNode node) {
		String preferredName = "";
		final Integer gid = node.getGermplasm().getGid();
		try {
			preferredName = node.getGermplasm().getPreferredName().getNval();
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			preferredName = String.valueOf(gid);
		}
		final StringBuilder sb = new StringBuilder(preferredName);
		if (gid != 0) {
			sb.append("(" + gid + ")");

		}
		return sb.toString();
	}

	private void addNode(final GermplasmPedigreeTreeNode node, final String itemIdOfParent) {
		for (final GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
			final String parentNodeId = node.getGermplasm().getGid() + "@" + parent.getGermplasm().getGid();
			this.addItem(parentNodeId);
			this.setItemCaption(parentNodeId, this.getNodeLabel(parent));
			this.setParent(parentNodeId, itemIdOfParent);
			this.setChildrenAllowed(parentNodeId, true);
		}
	}

	public void pedigreeTreeExpandAction(final String itemId) {
		if (itemId.contains("@")) {
			final String gidString = itemId.substring(itemId.indexOf("@") + 1, itemId.length());
			this.germplasmPedigreeTree = this.qQuery.generatePedigreeTree(Integer.valueOf(gidString), 2, this.includeDerivativeLines);
			this.addNode(this.germplasmPedigreeTree.getRoot(), itemId);
		} else {
			this.germplasmPedigreeTree = this.qQuery.generatePedigreeTree(Integer.valueOf(itemId), 2, this.includeDerivativeLines);
			this.addNode(this.germplasmPedigreeTree.getRoot(), 2);
		}

	}

}
