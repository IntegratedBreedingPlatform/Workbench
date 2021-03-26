
package org.generationcp.ibpworkbench.germplasm;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.ui.ComponentTree;
import org.generationcp.commons.vaadin.ui.ComponentTreeItem;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.germplasm.containers.GermplasmIndexContainer;
import org.generationcp.ibpworkbench.germplasm.pedigree.GermplasmPedigreeGraphComponent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class GermplasmPedigreeDetailsComponent extends VerticalLayout implements InternationalizableComponent, InitializingBean {

	private static final long serialVersionUID = -424140540302043647L;

	public static final int TOGGABLE_Y_COORDINATE = 30;

	private ComponentTree componentTree;

	private ComponentTreeItem pedigreeTreeItem;
	private ComponentTreeItem generationHistoryTreeItem;
	private ComponentTreeItem managementNeighborsTreeItem;
	private ComponentTreeItem derivativeNeighborhoodTreeItem;
	private ComponentTreeItem maintenanceNeighborhoodTreeItem;
	private ComponentTreeItem groupRelativesTreeItem;

	private ComponentTreeItem tempPedigreeTreeItemChild;
	private ComponentTreeItem tempGenerationHistoryTreeItemChild;
	private ComponentTreeItem tempManagementNeighborsTreeItemChild;
	private ComponentTreeItem tempDerivativeNeighborhoodTreeItemChild;
	private ComponentTreeItem tempMaintenanceNeighborhoodTreeItemChild;
	private ComponentTreeItem tempGroupRelativesTreeItemChild;

	private GermplasmPedigreeTreeContainer pedigreeTreeComponent;
	private GermplasmGenerationHistoryComponent generationHistoryComponent;
	private GermplasmManagementNeighborsComponent managementNeighborsComponent;
	private GermplasmDerivativeNeighborhoodComponent derivativeNeighborhoodComponent;
	private GermplasmMaintenanceNeighborhoodComponent maintenanceNeighborhoodComponent;
	private GermplasmGroupRelativesComponent groupRelativesComponent;

	private final Integer gid;
	private final GermplasmQueries germplasmQueries;
	private GermplasmDetailModel germplasmDetailModel;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public GermplasmPedigreeDetailsComponent(final Integer gid, final GermplasmQueries germplasmQueries) {
		this.gid = gid;
		this.germplasmQueries = germplasmQueries;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	private void initializeComponents() {

		this.componentTree = new ComponentTree();
		this.componentTree.setSizeFull();
		this.componentTree.setMargin(false);
		this.componentTree.addStyleName("pedigree-details-component-tree");

		this.germplasmDetailModel = this.germplasmQueries.getGermplasmDetails(this.gid);

		this.pedigreeTreeItem =
			this.componentTree.addChild(ComponentTreeItem.createHeaderComponent(this.messageSource
				.getMessage(Message.PEDIGREE_TREE_LABEL)));
		this.tempPedigreeTreeItemChild = this.pedigreeTreeItem.addChild(new Label());
		this.generationHistoryTreeItem =
			this.componentTree.addChild(ComponentTreeItem.createHeaderComponent(this.messageSource
				.getMessage(Message.GENERATION_HISTORY_LABEL)));
		this.tempGenerationHistoryTreeItemChild = this.generationHistoryTreeItem.addChild(new Label());
		this.managementNeighborsTreeItem =
			this.componentTree.addChild(ComponentTreeItem.createHeaderComponent(this.messageSource
				.getMessage(Message.MANAGEMENT_NEIGHBORS_LABEL)));
		this.tempManagementNeighborsTreeItemChild = this.managementNeighborsTreeItem.addChild(new Label());
		this.derivativeNeighborhoodTreeItem =
			this.componentTree.addChild(ComponentTreeItem.createHeaderComponent(this.messageSource
				.getMessage(Message.DERIVATIVE_NEIGHBORHOOD_LABEL)));
		this.tempDerivativeNeighborhoodTreeItemChild = this.derivativeNeighborhoodTreeItem.addChild(new Label());
		this.maintenanceNeighborhoodTreeItem =
			this.componentTree.addChild(ComponentTreeItem.createHeaderComponent(this.messageSource
				.getMessage(Message.MAINTENANCE_NEIGHBORHOOD_LABEL)));
		this.tempMaintenanceNeighborhoodTreeItemChild = this.maintenanceNeighborhoodTreeItem.addChild(new Label());
		this.groupRelativesTreeItem =
			this.componentTree.addChild(ComponentTreeItem.createHeaderComponent(this.messageSource
				.getMessage(Message.GROUP_RELATIVES_LABEL)));
		this.tempGroupRelativesTreeItemChild = this.groupRelativesTreeItem.addChild(new Label());
	}

	private void initializeValues() {
		this.pedigreeTreeComponent = null;
		this.generationHistoryComponent = null;
		this.managementNeighborsComponent = null;
		this.derivativeNeighborhoodComponent = null;
		this.maintenanceNeighborhoodComponent = null;
		this.groupRelativesComponent = null;
	}

	private void addListeners() {

		this.pedigreeTreeItem.addListener(new LayoutClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(final LayoutClickEvent event) {
				if (event.getRelativeY() < GermplasmPedigreeDetailsComponent.TOGGABLE_Y_COORDINATE) {
					GermplasmPedigreeDetailsComponent.this.showPedigreeTree();
					GermplasmPedigreeDetailsComponent.this.pedigreeTreeItem.toggleChild();
				}
			}
		});

		this.pedigreeTreeItem.addExpanderClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6108554806619975288L;

			@Override
			public void buttonClick(final ClickEvent event) {
				GermplasmPedigreeDetailsComponent.this.showPedigreeTree();
			}
		});

		this.generationHistoryTreeItem.addListener(new LayoutClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(final LayoutClickEvent event) {
				if (event.getRelativeY() < GermplasmPedigreeDetailsComponent.TOGGABLE_Y_COORDINATE) {
					GermplasmPedigreeDetailsComponent.this.showGenerationHistory();
					GermplasmPedigreeDetailsComponent.this.generationHistoryTreeItem.toggleChild();
				}
			}
		});

		this.generationHistoryTreeItem.addExpanderClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6108554806619975288L;

			@Override
			public void buttonClick(final ClickEvent event) {
				GermplasmPedigreeDetailsComponent.this.showGenerationHistory();
			}
		});

		this.managementNeighborsTreeItem.addListener(new LayoutClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(final LayoutClickEvent event) {
				if (event.getRelativeY() < GermplasmPedigreeDetailsComponent.TOGGABLE_Y_COORDINATE) {
					GermplasmPedigreeDetailsComponent.this.showManagementNeighbors();
					GermplasmPedigreeDetailsComponent.this.managementNeighborsTreeItem.toggleChild();
				}
			}
		});

		this.managementNeighborsTreeItem.addExpanderClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6108554806619975288L;

			@Override
			public void buttonClick(final ClickEvent event) {
				GermplasmPedigreeDetailsComponent.this.showManagementNeighbors();
			}
		});

		this.derivativeNeighborhoodTreeItem.addListener(new LayoutClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(final LayoutClickEvent event) {
				if (event.getRelativeY() < GermplasmPedigreeDetailsComponent.TOGGABLE_Y_COORDINATE) {
					GermplasmPedigreeDetailsComponent.this.showDerivativeNeighborhood();
					GermplasmPedigreeDetailsComponent.this.derivativeNeighborhoodTreeItem.toggleChild();
				}
			}
		});

		this.derivativeNeighborhoodTreeItem.addExpanderClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6108554806619975288L;

			@Override
			public void buttonClick(final ClickEvent event) {
				GermplasmPedigreeDetailsComponent.this.showDerivativeNeighborhood();
			}
		});

		this.maintenanceNeighborhoodTreeItem.addListener(new LayoutClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(final LayoutClickEvent event) {
				if (event.getRelativeY() < GermplasmPedigreeDetailsComponent.TOGGABLE_Y_COORDINATE) {
					GermplasmPedigreeDetailsComponent.this.showMaintenanceNeighborhood();
					GermplasmPedigreeDetailsComponent.this.maintenanceNeighborhoodTreeItem.toggleChild();
				}
			}
		});

		this.maintenanceNeighborhoodTreeItem.addExpanderClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6108554806619975288L;

			@Override
			public void buttonClick(final ClickEvent event) {
				GermplasmPedigreeDetailsComponent.this.showMaintenanceNeighborhood();
			}
		});

		this.groupRelativesTreeItem.addListener(new LayoutClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(final LayoutClickEvent event) {
				if (event.getRelativeY() < GermplasmPedigreeDetailsComponent.TOGGABLE_Y_COORDINATE) {
					GermplasmPedigreeDetailsComponent.this.showGroupRelatives();
					GermplasmPedigreeDetailsComponent.this.groupRelativesTreeItem.toggleChild();
				}
			}
		});

		this.groupRelativesTreeItem.addExpanderClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6108554806619975288L;

			@Override
			public void buttonClick(final ClickEvent event) {
				GermplasmPedigreeDetailsComponent.this.showGroupRelatives();
			}
		});
	}

	private void showPedigreeTree() {
		if (this.pedigreeTreeComponent == null) {
			this.pedigreeTreeComponent = new GermplasmPedigreeTreeContainer(this.gid, this.germplasmQueries,
				new GermplasmPedigreeTreeContainer.GermplasmPedigreeTreeActions() {

					@Override
					public void showPedigreeGraphWindow() {
						GermplasmPedigreeDetailsComponent.this.showPedigreeGraphWindow();
					}
				});
			this.pedigreeTreeItem.removeChild(this.tempPedigreeTreeItemChild);
			this.pedigreeTreeItem.addChild(this.pedigreeTreeComponent);
		}
	}

	private void showGenerationHistory() {
		if (this.generationHistoryComponent == null) {
			this.generationHistoryComponent =
				new GermplasmGenerationHistoryComponent(new GermplasmIndexContainer(this.germplasmQueries), this.germplasmDetailModel);
			this.generationHistoryTreeItem.removeChild(this.tempGenerationHistoryTreeItemChild);
			this.generationHistoryTreeItem.addChild(this.generationHistoryComponent);
		}
	}

	private void showManagementNeighbors() {
		if (this.managementNeighborsComponent == null) {
			this.managementNeighborsComponent = new GermplasmManagementNeighborsComponent(this.gid);
			this.managementNeighborsTreeItem.removeChild(this.tempManagementNeighborsTreeItemChild);
			this.managementNeighborsTreeItem.addChild(this.managementNeighborsComponent);
		}
	}

	private void showDerivativeNeighborhood() {
		if (this.derivativeNeighborhoodComponent == null) {
			this.derivativeNeighborhoodComponent =
				new GermplasmDerivativeNeighborhoodComponent(this.gid, this.germplasmQueries, new GermplasmIndexContainer(
					this.germplasmQueries), null, null);
			this.derivativeNeighborhoodTreeItem.removeChild(this.tempDerivativeNeighborhoodTreeItemChild);
			this.derivativeNeighborhoodTreeItem.addChild(this.derivativeNeighborhoodComponent);
		}
	}

	private void showMaintenanceNeighborhood() {
		if (this.maintenanceNeighborhoodComponent == null) {
			this.maintenanceNeighborhoodComponent =
				new GermplasmMaintenanceNeighborhoodComponent(this.gid, this.germplasmQueries, new GermplasmIndexContainer(
					this.germplasmQueries), null, null);
			this.maintenanceNeighborhoodTreeItem.removeChild(this.tempMaintenanceNeighborhoodTreeItemChild);
			this.maintenanceNeighborhoodTreeItem.addChild(this.maintenanceNeighborhoodComponent);
		}
	}

	private void showGroupRelatives() {
		if (this.groupRelativesComponent == null) {
			this.groupRelativesComponent = new GermplasmGroupRelativesComponent(this.gid);
			this.groupRelativesTreeItem.removeChild(this.tempGroupRelativesTreeItemChild);
			this.groupRelativesTreeItem.addChild(this.groupRelativesComponent);
		}
	}

	private void layoutComponents() {
		this.setSizeUndefined();
		this.setWidth("100%");
		this.setMargin(false);
		this.addComponent(this.componentTree);
	}

	@Override
	public void updateLabels() {
		// do nothing
	}

	public void showPedigreeGraphWindow() {
		final Window pedigreeGraphWindow = new BaseSubWindow("Pedigree Graph");
		pedigreeGraphWindow.setModal(true);
		pedigreeGraphWindow.setWidth("100%");
		pedigreeGraphWindow.setHeight("620px");
		pedigreeGraphWindow.setName("Pedigree Graph");
		pedigreeGraphWindow.addStyleName(Reindeer.WINDOW_LIGHT);
		pedigreeGraphWindow.addComponent(new GermplasmPedigreeGraphComponent(this.gid, this.germplasmQueries));
		this.getWindow().addWindow(pedigreeGraphWindow);
	}
}
