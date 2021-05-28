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
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.breeding.manager.listmanager.GermplasmDetailsUrlService;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.germplasm.containers.GermplasmIndexContainer;
import org.generationcp.ibpworkbench.germplasm.listeners.GermplasmButtonClickListener;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class GermplasmMaintenanceNeighborhoodComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;
	private GermplasmPedigreeTree germplasmMaintenanceNeighborhood;
	private final GermplasmQueries qQuery;
	private final VerticalLayout mainLayout;
	private final TabSheet tabSheet;
	private final GermplasmIndexContainer dataIndexContainer;
	private Tree maintenanceNeighborhoodTree;
	private final int gid;
	private Label labelNumberOfStepsBackward;
	private Label labelNumberOfStepsForward;
	private Button btnDisplay;
	private HorizontalLayout hLayout1;
	private HorizontalLayout hLayout2;
	private HorizontalLayout hLayout3;
	private Select selectNumberOfStepBackward;
	private Select selectNumberOfStepForward;
	public static final String DISPLAY_BUTTON_ID = "Display Derivative Neighborhood";

	@SuppressWarnings("unused")
	private final static Logger LOG = LoggerFactory.getLogger(GermplasmMaintenanceNeighborhoodComponent.class);

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private GermplasmDetailsUrlService germplasmDetailsUrlService;

	public GermplasmMaintenanceNeighborhoodComponent(final int gid, final GermplasmQueries qQuery,
		final GermplasmIndexContainer dataResultIndexContainer,
		final VerticalLayout mainLayout, final TabSheet tabSheet) throws InternationalizableException {

		super();

		this.mainLayout = mainLayout;
		this.tabSheet = tabSheet;
		this.qQuery = qQuery;
		this.dataIndexContainer = dataResultIndexContainer;
		this.gid = gid;
	}

	private void addNode(final GermplasmPedigreeTreeNode node, final int level) {

		if (level == 1) {
			final String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
			final String rootNodeLabel = name + "(" + node.getGermplasm().getGid() + ")";
			final int rootNodeId = node.getGermplasm().getGid();
			this.maintenanceNeighborhoodTree.addItem(rootNodeId);
			this.maintenanceNeighborhoodTree.setItemCaption(rootNodeId, rootNodeLabel);
			this.maintenanceNeighborhoodTree.setParent(rootNodeId, rootNodeId);
			this.maintenanceNeighborhoodTree.setChildrenAllowed(rootNodeId, true);
			this.maintenanceNeighborhoodTree.expandItemsRecursively(rootNodeId);
		}
		for (final GermplasmPedigreeTreeNode child : node.getLinkedNodes()) {
			final String name = child.getGermplasm().getPreferredName() != null ? child.getGermplasm().getPreferredName().getNval() : null;
			final int parentNodeId = node.getGermplasm().getGid();
			final String childNodeLabel = name + "(" + child.getGermplasm().getGid() + ")";
			final int childNodeId = child.getGermplasm().getGid();
			this.maintenanceNeighborhoodTree.addItem(childNodeId);
			this.maintenanceNeighborhoodTree.setItemCaption(childNodeId, childNodeLabel);
			this.maintenanceNeighborhoodTree.setParent(childNodeId, parentNodeId);
			this.maintenanceNeighborhoodTree.setChildrenAllowed(childNodeId, true);
			this.maintenanceNeighborhoodTree.expandItemsRecursively(childNodeId);

			if (child.getGermplasm().getGid() == this.gid) {
				this.maintenanceNeighborhoodTree.setValue(childNodeId);
				this.maintenanceNeighborhoodTree.setImmediate(true);
			}

			this.addNode(child, level + 1);
		}
	}

	@Override
	public void afterPropertiesSet() {
		this.setSpacing(true);
		this.setMargin(true);

		this.setStyleName("gsb-component-wrap");

		this.hLayout1 = new HorizontalLayout();
		this.hLayout1.setSpacing(true);
		this.hLayout1.setMargin(false, true, false, false);
		this.hLayout1.addStyleName("gsb-component-wrap");

		this.hLayout2 = new HorizontalLayout();
		this.hLayout2.setSpacing(true);
		this.hLayout2.setMargin(false, true, false, false);
		this.hLayout2.addStyleName("gsb-component-wrap");

		this.hLayout3 = new HorizontalLayout();
		this.hLayout3.setSpacing(true);
		this.hLayout3.addStyleName("gsb-component-wrap");

		this.labelNumberOfStepsBackward = new Label();
		this.labelNumberOfStepsBackward.setWidth("170px");
		this.selectNumberOfStepBackward = new Select();
		this.selectNumberOfStepBackward.setWidth("50px");
		this.populateSelectSteps(this.selectNumberOfStepBackward);
		this.selectNumberOfStepBackward.setNullSelectionAllowed(false);
		this.selectNumberOfStepBackward.select("2");

		this.labelNumberOfStepsForward = new Label();
		this.labelNumberOfStepsForward.setWidth("170px");
		this.selectNumberOfStepForward = new Select();
		this.selectNumberOfStepForward.setWidth("50px");
		this.populateSelectSteps(this.selectNumberOfStepForward);
		this.selectNumberOfStepForward.setNullSelectionAllowed(false);
		this.selectNumberOfStepForward.select("3");

		this.btnDisplay = new Button();
		this.btnDisplay.setData(GermplasmMaintenanceNeighborhoodComponent.DISPLAY_BUTTON_ID);
		this.btnDisplay.setDescription("Display Germplasm Maintenance Neighborhood ");
		this.btnDisplay.addListener(new GermplasmButtonClickListener(this));
		this.btnDisplay.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		this.hLayout1.addComponent(this.labelNumberOfStepsBackward);
		this.hLayout1.addComponent(this.selectNumberOfStepBackward);
		this.hLayout2.addComponent(this.labelNumberOfStepsForward);
		this.hLayout2.addComponent(this.selectNumberOfStepForward);
		this.hLayout3.addComponent(this.btnDisplay);

		final CssLayout cssLayout = new CssLayout();
		cssLayout.setWidth("100%");
		cssLayout.addComponent(this.hLayout1);
		cssLayout.addComponent(this.hLayout2);
		cssLayout.addComponent(this.hLayout3);

		this.addComponent(cssLayout);

		this.maintenanceNeighborhoodTree = new Tree();
		this.addComponent(this.maintenanceNeighborhoodTree);

		this.maintenanceNeighborhoodTree.setSelectable(false);

		this.maintenanceNeighborhoodTree.setItemStyleGenerator(new Tree.ItemStyleGenerator() {

			@Override
			public String getStyle(final Object itemId) {
				return "link";
			}
		});

		this.maintenanceNeighborhoodTree.addListener(new ItemClickEvent.ItemClickListener() {

			@Override
			public void itemClick(final ItemClickEvent event) {
				final String gid = event.getItemId().toString();
				GermplasmMaintenanceNeighborhoodComponent.this
					.getWindow().open(GermplasmMaintenanceNeighborhoodComponent.this.germplasmDetailsUrlService
						.getExternalResource(Integer.parseInt(gid), false),
					"_blank", false);
			}
		});

		this.maintenanceNeighborhoodTree.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {

			private static final long serialVersionUID = 3442425534732855473L;

			@Override
			public String generateDescription(final Component source, final Object itemId, final Object propertyId) {
				return GermplasmMaintenanceNeighborhoodComponent.this.messageSource.getMessage(Message.CLICK_TO_VIEW_GERMPLASM_DETAILS);
			}
		});

		this.displayButtonClickAction();
	}

	private void populateSelectSteps(final Select select) {

		for (int i = 1; i <= 10; i++) {
			select.addItem(String.valueOf(i));
		}
	}

	@Override
	public void attach() {

		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {

		this.messageSource.setValue(this.labelNumberOfStepsBackward, Message.NUMBER_OF_STEPS_BACKWARD_LABEL);
		this.messageSource.setValue(this.labelNumberOfStepsForward, Message.NUMBER_OF_STEPS_FORWARD_LABEL);
		this.messageSource.setCaption(this.btnDisplay, Message.DISPLAY_BUTTON_LABEL);

	}

	public void displayButtonClickAction() {

		this.removeComponent(this.maintenanceNeighborhoodTree);
		this.maintenanceNeighborhoodTree.removeAllItems();
		final int numberOfStepsBackward = Integer.valueOf(this.selectNumberOfStepBackward.getValue().toString());
		final int numberOfStepsForward = Integer.valueOf(this.selectNumberOfStepForward.getValue().toString());

		this.germplasmMaintenanceNeighborhood =
			this.qQuery.getMaintenanceNeighborhood(Integer.valueOf(this.gid), numberOfStepsBackward, numberOfStepsForward); // throws
		// QueryException
		if (this.germplasmMaintenanceNeighborhood != null) {
			this.addNode(this.germplasmMaintenanceNeighborhood.getRoot(), 1);
		}

		// Prevent any items in the tree to be selected.
		for (final Object itemId : this.maintenanceNeighborhoodTree.getItemIds()) {
			this.maintenanceNeighborhoodTree.unselect(itemId);
		}

		this.addComponent(this.maintenanceNeighborhoodTree);

	}
}
