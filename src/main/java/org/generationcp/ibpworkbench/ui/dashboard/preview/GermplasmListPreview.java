
package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.List;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.common.InputPopup;
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainTreeListener;
import org.generationcp.ibpworkbench.ui.dashboard.listener.GermplasmListTreeExpandListener;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/19/13 Time: 7:20 PM To change this template use File | Settings | File Templates.
 */
@Configurable
public class GermplasmListPreview extends VerticalLayout {

	private static final long serialVersionUID = 1941905235449423109L;

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListPreview.class);

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private PlatformTransactionManager transactionManager;


	private GermplasmListPreviewPresenter presenter;

	private Tree treeView;

	private Project project;

	private final ThemeResource folderResource;
	private final ThemeResource leafResource;

	private Panel panel;
	private HorizontalLayout toolbar;

	private String listLabel = "";

	private Button openListManagerBtn;
	private Button addFolderBtn;
	private Button deleteFolderBtn;
	private Button renameFolderBtn;

	private Object lastItemId;

	public GermplasmListPreview(Project project) {
		this.project = project;

		this.presenter = new GermplasmListPreviewPresenter(this, project);

		try {
			if (project != null) {
				this.assemble();
			}
		} catch (Exception e) {
			GermplasmListPreview.LOG.error(e.getMessage(), e);
		}

		this.folderResource = new ThemeResource("../vaadin-retro/svg/folder-icon.svg");
		this.leafResource = new ThemeResource("images/leaf_16.png");
	}

	public void setProject(Project project) {
		this.removeAllComponents();
		this.setSizeFull();

		// add toolbar here
		this.panel = new Panel();
		this.panel.removeAllComponents();

		this.addComponent(this.buildToolbar());

		this.project = project;
		this.presenter = new GermplasmListPreviewPresenter(this, this.project);
		this.presenter.generateInitialTreeNode();

		CssLayout treeContainer = new CssLayout();
		treeContainer.setSizeUndefined();
		treeContainer.addComponent(this.treeView);

		this.panel.setContent(treeContainer);
		this.panel.setStyleName(Reindeer.PANEL_LIGHT);
		this.panel.setSizeFull();

		this.addComponent(this.panel);
		this.setExpandRatio(this.panel, 1.0F);
	}

	private Component buildToolbar() {
		this.toolbar = new HorizontalLayout();
		this.toolbar.setSpacing(true);
		this.toolbar.setMargin(true);

		this.openListManagerBtn =
				new Button("<span class='glyphicon glyphicon-open' style='right: 4px'></span>"
						+ this.messageSource.getMessage(Message.LAUNCH));
		this.openListManagerBtn.setHtmlContentAllowed(true);
		this.openListManagerBtn.setDescription(this.messageSource.getMessage(Message.OPEN_IN_LIST_MANAGER));
		this.openListManagerBtn.setEnabled(false);

		this.renameFolderBtn = new Button("<span class='bms-edit' style='color:#0082CB'><span>");
		this.renameFolderBtn.setHtmlContentAllowed(true);
		this.renameFolderBtn.setDescription(this.messageSource.getMessage(Message.RENAME_ITEM));

		this.addFolderBtn = new Button("<span class='bms-add' style='color:#00AF40'></span>");
		this.addFolderBtn.setHtmlContentAllowed(true);
		this.addFolderBtn.setDescription(this.messageSource.getMessage(Message.ADD_FOLDER));

		this.deleteFolderBtn = new Button("<span class='bms-delete' style='color:#F4A41C'></span>");
		this.deleteFolderBtn.setHtmlContentAllowed(true);
		this.deleteFolderBtn.setDescription(this.messageSource.getMessage(Message.DELETE_ITEM));

		this.openListManagerBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.renameFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " action");
		this.addFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " action");
		this.deleteFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " action");

		this.openListManagerBtn.setWidth("100px");
		this.renameFolderBtn.setWidth("26px");
		this.addFolderBtn.setWidth("26px");
		this.deleteFolderBtn.setWidth("26px");

		this.toolbar.addComponent(this.openListManagerBtn);

		Label spacer = new Label("");
		this.toolbar.addComponent(spacer);
		this.toolbar.setExpandRatio(spacer, 1.0F);

		this.renameFolderBtn.setEnabled(false);
		this.addFolderBtn.setEnabled(false);
		this.deleteFolderBtn.setEnabled(false);

		this.toolbar.addComponent(this.addFolderBtn);
		this.toolbar.addComponent(this.renameFolderBtn);
		this.toolbar.addComponent(this.deleteFolderBtn);

		this.toolbar.setWidth("100%");

		this.initializeToolbarActions();

		return this.toolbar;
	}

	private void initializeToolbarActions() {
		this.openListManagerBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				TransactionTemplate transactionTemplate = new TransactionTemplate(GermplasmListPreview.this.transactionManager);
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						if (GermplasmListPreview.this.lastItemId == null || GermplasmListPreview.this.lastItemId instanceof String) {
							MessageNotifier.showError(event.getComponent().getWindow(),
									GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION),
									GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_NO_SELECTION));
							return;
						}

						if (GermplasmListPreview.this.presenter.isFolder((Integer) GermplasmListPreview.this.lastItemId)) {
							MessageNotifier.showError(event.getComponent().getWindow(), GermplasmListPreview.this.messageSource
									.getMessage(Message.INVALID_OPERATION), GermplasmListPreview.this.messageSource.getMessage(
											Message.INVALID_ITEM_IS_FOLDER,
											GermplasmListPreview.this.treeView.getItemCaption(GermplasmListPreview.this.lastItemId)));
							return;
						}
						GermplasmListPreview.this.presenter.updateProjectLastOpenedDate();

						// update sidebar selection
						GermplasmListPreview.LOG.trace("selecting sidebar");
						WorkbenchMainView mainWindow = (WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow();

						if (null != WorkbenchSidebar.sidebarTreeMap.get("manage_list")) {
							mainWindow.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("manage_list"));
						}
						// page change to list manager, with parameter passed
						new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER, (Integer) GermplasmListPreview.this.lastItemId).buttonClick(event);
					}
				});
			}
		});

		this.renameFolderBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				if (GermplasmListPreview.this.lastItemId == null) {
					MessageNotifier.showError(event.getComponent().getWindow(),
							GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION),
							GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_ITEM_NO_RENAME_SELECT));
					return;
				}

				if (GermplasmListPreview.this.lastItemId instanceof String) {
					MessageNotifier.showError(event.getComponent().getWindow(), GermplasmListPreview.this.messageSource
							.getMessage(Message.INVALID_OPERATION), GermplasmListPreview.this.messageSource.getMessage(
									Message.INVALID_CANNOT_RENAME_ITEM, (String) GermplasmListPreview.this.lastItemId));
					return;
				}

				if (!GermplasmListPreview.this.presenter.isFolder((Integer) GermplasmListPreview.this.lastItemId)) {
					MessageNotifier.showError(event.getComponent().getWindow(),
							GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION),
							GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_ITEM_NO_RENAME_SELECT));
					return;
				}

				final InputPopup w =
						new InputPopup(GermplasmListPreview.this.messageSource.getMessage(Message.RENAME_ITEM),
								GermplasmListPreview.this.messageSource.getMessage(Message.ITEM_NAME), GermplasmListPreview.this.treeView
								.getItemCaption(GermplasmListPreview.this.lastItemId));
				w.setOkListener(new Button.ClickListener() {

					private static final long serialVersionUID = -242570054807727077L;

					@Override
					public void buttonClick(Button.ClickEvent event1) {
						try {
							GermplasmListPreview.this.presenter.renameGermplasmListFolder(w.getFieldVal(),
									(Integer) GermplasmListPreview.this.lastItemId);
						} catch (Exception e) {
							GermplasmListPreview.LOG.error(e.getMessage(), e);
							MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getMessage());
							return;
						}

						// update UI
						GermplasmListPreview.this.treeView.setItemCaption(GermplasmListPreview.this.lastItemId, w.getFieldVal());

						// close popup
						event.getComponent().getWindow().removeWindow(w);
					}
				});

				// show window
				event.getComponent().getWindow().addWindow(w);

			}
		});

		final InputPopup addFolderPopup =
				new InputPopup(this.messageSource.getMessage(Message.ADD_FOLDER), this.messageSource.getMessage(Message.ITEM_NAME), "");
		addFolderPopup.setOkListener(new Button.ClickListener() {

			private static final long serialVersionUID = 2842797806931785183L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				Integer newItem = null;
				try {

					if (GermplasmListPreview.this.treeView.getValue() instanceof String) {
						newItem = GermplasmListPreview.this.presenter.addGermplasmListFolder(addFolderPopup.getFieldVal(), null);
					} else {
						newItem =
								GermplasmListPreview.this.presenter.addGermplasmListFolder(addFolderPopup.getFieldVal(),
										(Integer) GermplasmListPreview.this.treeView.getValue());
					}

					// update UI
					if (newItem != null) {
						GermplasmListPreview.this.treeView.addItem(newItem);
						GermplasmListPreview.this.treeView.setItemCaption(newItem, addFolderPopup.getFieldVal());
						GermplasmListPreview.this.treeView.setChildrenAllowed(newItem, true);
						GermplasmListPreview.this.treeView.setItemIcon(newItem, GermplasmListPreview.this.folderResource);

						GermplasmList parent = GermplasmListPreview.this.presenter.getGermplasmListParent(newItem);
						if (parent != null) {
							GermplasmListPreview.this.treeView.setParent(newItem, parent.getId());
						} else {
							GermplasmListPreview.this.treeView.setParent(newItem,
									GermplasmListPreview.this.messageSource.getMessage(Message.LISTS));
						}

						if (parent != null && !GermplasmListPreview.this.treeView.isExpanded(parent.getId())) {
							GermplasmListPreview.this.expandTree(parent.getId());
						} else {
							GermplasmListPreview.this.treeView.expandItem(GermplasmListPreview.this.listLabel);
						}

						GermplasmListPreview.this.treeView.select(newItem);
						GermplasmListPreview.this.lastItemId = newItem;
						GermplasmListPreview.this.treeView.setImmediate(true);
						GermplasmListPreview.this.processToolbarButtons(newItem);
					}

				} catch (Exception e) {
					GermplasmListPreview.LOG.error(e.getMessage(), e);
					MessageNotifier.showError(clickEvent.getComponent().getWindow(),
							GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
					return;
				}

				// close popup
				IBPWorkbenchApplication.get().getMainWindow().removeWindow(clickEvent.getComponent().getWindow());

			}
		});

		this.addFolderBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				addFolderPopup.clearFieldVal();
				event.getComponent().getWindow().addWindow(addFolderPopup);
			}
		});

		this.deleteFolderBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {

				if (GermplasmListPreview.this.lastItemId instanceof String) {
					MessageNotifier.showError(event.getComponent().getWindow(),
							GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION),
							GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_CANNOT_DELETE_ITEM));
					return;
				}

				GermplasmList gpList = null;

				try {
					gpList =
							GermplasmListPreview.this.presenter
							.validateForDeleteGermplasmList((Integer) GermplasmListPreview.this.lastItemId);
				} catch (Exception e) {
					GermplasmListPreview.LOG.error(e.getMessage(), e);
					MessageNotifier.showError(event.getComponent().getWindow(),
							GermplasmListPreview.this.messageSource.getMessage(Message.ERROR), e.getMessage());
					return;
				}

				final GermplasmList finalGpList = gpList;
				ConfirmDialog.show(event.getComponent().getWindow(),
						GermplasmListPreview.this.messageSource.getMessage(Message.DELETE_ITEM),
						GermplasmListPreview.this.messageSource.getMessage(Message.DELETE_ITEM_CONFIRM),
						GermplasmListPreview.this.messageSource.getMessage(Message.YES),
						GermplasmListPreview.this.messageSource.getMessage(Message.NO), new ConfirmDialog.Listener() {

							private static final long serialVersionUID = 1L;

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {
										GermplasmList parent =
												GermplasmListPreview.this.presenter.getGermplasmListParent(finalGpList.getId());
										GermplasmListPreview.this.presenter.deleteGermplasmListFolder(finalGpList);
										GermplasmListPreview.this.treeView.removeItem(GermplasmListPreview.this.lastItemId);
										GermplasmListPreview.this.treeView.select(null);
										if (parent == null) {
											GermplasmListPreview.this.treeView.select(GermplasmListPreview.this.listLabel);
											GermplasmListPreview.this.lastItemId = GermplasmListPreview.this.listLabel;
											GermplasmListPreview.this.processToolbarButtons(GermplasmListPreview.this.listLabel);
										} else {
											GermplasmListPreview.this.treeView.select(parent.getId());
											GermplasmListPreview.this.lastItemId = parent.getId();
											GermplasmListPreview.this.processToolbarButtons(parent.getId());
										}
										GermplasmListPreview.this.treeView.setImmediate(true);
									} catch (Exception e) {
										GermplasmListPreview.LOG.error(e.getMessage(), e);
										MessageNotifier.showError(event.getComponent().getWindow(),
												GermplasmListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION),
												e.getMessage());
									}
								}
							}
						});
			}
		});
	}

	public void expandTree(Object itemId) {

		if (this.treeView.isExpanded(itemId)) {
			this.treeView.collapseItem(itemId);
		} else {
			this.treeView.expandItem(itemId);
		}
		this.lastItemId = itemId;

		this.treeView.select(itemId);

		this.treeView.setImmediate(true);
	}

	protected void initializeComponents() {
		// do nothing
	}

	public void generateTree(List<GermplasmList> germplasmListParent) {
		this.listLabel = this.messageSource.getMessage(Message.LISTS);

		this.lastItemId = null;
		this.treeView = new Tree();
		this.treeView.setContainerDataSource(new HierarchicalContainer());
		this.treeView.setDropHandler(new GermplasmListTreeDropHandler(this.treeView, this.presenter));
		this.treeView.setDragMode(TreeDragMode.NODE);

		this.treeView.addItem(this.listLabel);
		this.treeView.setItemCaption(this.listLabel, this.listLabel);
		this.treeView.setItemIcon(this.listLabel, this.folderResource);

		this.treeView.setNullSelectionAllowed(false);

		for (GermplasmList parentList : germplasmListParent) {
			this.treeView.addItem(parentList.getId());
			this.treeView.setItemCaption(parentList.getId(), parentList.getName());
			this.treeView.setParent(parentList.getId(), this.listLabel);
			boolean hasChildList = this.getPresenter().hasChildList(parentList.getId());

			this.treeView.setChildrenAllowed(parentList.getId(), hasChildList);

			if (parentList.isFolder()) {
				this.treeView.setItemIcon(parentList.getId(), this.folderResource);
			} else {
				this.treeView.setItemIcon(parentList.getId(), this.leafResource);
			}

			this.treeView.setSelectable(true);
		}

		this.treeView.addListener(new GermplasmListTreeExpandListener(this));
		this.treeView.addListener(new DashboardMainTreeListener(this, this.project));
		this.treeView.setImmediate(true);
	}

	/**
	 * Set the toolbar button's enabled state.
	 *
	 * @param enabled
	 */
	public void setToolbarButtonsEnabled(boolean enabled) {
		this.addFolderBtn.setEnabled(enabled);
		this.renameFolderBtn.setEnabled(enabled);
		this.deleteFolderBtn.setEnabled(enabled);
	}

	/**
	 * Set the Add button's enabled state.
	 *
	 * @param enabled
	 */
	public void setToolbarAddButtonEnabled(boolean enabled) {
		this.addFolderBtn.setEnabled(enabled);
	}

	/**
	 * Set the Delete button's enabled state.
	 *
	 * @param enabled
	 */
	public void setToolbarDeleteButtonEnabled(boolean enabled) {
		this.deleteFolderBtn.setEnabled(enabled);
	}

	/**
	 * Set the Launch button's enabled state.
	 *
	 * @param enabled
	 */
	public void setToolbarLaunchButtonEnabled(boolean enabled) {
		this.openListManagerBtn.setEnabled(enabled);
	}

	public void addGermplasmListNode(int parentGermplasmListId, List<GermplasmList> germplasmListChildren, Object itemId) {

		for (GermplasmList listChild : germplasmListChildren) {

			boolean hasChildList = this.getPresenter().hasChildList(listChild.getId());

			this.treeView.addItem(listChild.getId());
			this.treeView.setItemCaption(listChild.getId(), listChild.getName());
			this.treeView.setParent(listChild.getId(), parentGermplasmListId);
			// allow children if list has sub-lists

			this.treeView.setChildrenAllowed(listChild.getId(), hasChildList);

			ThemeResource resource = this.leafResource;
			if (listChild.isFolder()) {
				resource = this.folderResource;
			}
			this.treeView.setItemIcon(listChild.getId(), resource);

			this.treeView.setSelectable(true);

		}
		GermplasmListPreview.LOG.trace("Add node {0}", itemId);
		this.treeView.select(itemId);
		this.lastItemId = itemId;
		this.treeView.setImmediate(true);
	}

	public GermplasmListPreviewPresenter getPresenter() {
		return this.presenter;
	}

	public void setPresenter(GermplasmListPreviewPresenter presenter) {
		this.presenter = presenter;
	}

	protected void initializeLayout() {
		this.setSizeFull();
	}

	protected void initializeActions() {
		// do nothing
	}

	public void processToolbarButtons(Object treeItem) {
		boolean isMyListNode = treeItem instanceof String && treeItem.equals(this.listLabel);
		boolean isFolder = treeItem instanceof String || this.getPresenter().isFolder((Integer) treeItem);

		// set the toolbar button state
		if (isMyListNode) {
			this.setToolbarButtonsEnabled(false);
			this.setToolbarAddButtonEnabled(true);
		} else if (!isFolder) {
			this.setToolbarButtonsEnabled(false);
			this.setToolbarAddButtonEnabled(true);
			this.setToolbarDeleteButtonEnabled(true);
		} else {
			this.setToolbarButtonsEnabled(true);
		}

		// set the launch button state
		this.setToolbarLaunchButtonEnabled(!isMyListNode && !isFolder);
	}

	protected void assemble() throws Exception {

		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();

	}

	public String getListLabel() {
		return listLabel;
	}

	public void setListLabel(String listLabel) {
		this.listLabel = listLabel;
	}


}
