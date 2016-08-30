
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
import org.generationcp.ibpworkbench.ui.dashboard.listener.DeleteConfirmDialogListener;
import org.generationcp.ibpworkbench.ui.dashboard.listener.NurseryListTreeExpandListener;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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
 * Created with IntelliJ IDEA. User: cyrus Date: 11/19/13 Time: 7:20 PM
 * <p/>
 * Revision done by mae 1. Display hierarchy of studies from root to children per database instance (instead of categories like year, season
 * and study type)
 */
@Configurable
public class NurseryListPreview extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private NurseryListPreviewPresenter presenter;

	private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreview.class);

	private Tree treeView;

	private Project project;

	private Panel panel;

	private final ThemeResource folderResource = new ThemeResource("../vaadin-retro/svg/folder-icon.svg");
	private final ThemeResource studyResource = new ThemeResource("../vaadin-retro/svg/study-icon.svg");

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private HorizontalLayout toolbar;
	private Button openStudyManagerBtn;
	private Button renameFolderBtn;
	private Button addFolderBtn;
	private Button deleteFolderBtn;

	public static String NURSERIES_AND_TRIALS;
	public static final int ROOT_FOLDER = 1;

	private static final String ACTION_STYLE_CLASS = "action";

	public NurseryListPreview(Project project) {

		this.project = project;
		this.presenter = new NurseryListPreviewPresenter(this, project);

		try {
			if (project != null) {
				this.assemble();
			}
		} catch (Exception e) {
			NurseryListPreview.LOG.error(e.getMessage(), e);
		}
	}

	public void setProject(Project project) {
		this.removeAllComponents();
		this.setSizeFull();

		this.panel = new Panel();
		this.panel.setDebugId("panel");
		this.panel.removeAllComponents();

		this.addComponent(this.buildToolbar());

		this.project = project;

		NurseryListPreview.NURSERIES_AND_TRIALS = this.messageSource.getMessage(Message.NURSERIES_AND_TRIALS);
		this.presenter.setProject(project);
		this.presenter.generateInitialTreeNodes();

		CssLayout treeContainer = new CssLayout();
		treeContainer.setDebugId("treeContainer");
		treeContainer.setSizeUndefined();
		treeContainer.addComponent(this.treeView);

		this.panel.setContent(treeContainer);
		this.panel.setStyleName(Reindeer.PANEL_LIGHT);
		this.panel.setSizeFull();

		this.addComponent(this.panel);
		this.setExpandRatio(this.panel, 1.0F);
	}

	public void generateTopListOfTree(List<Reference> root) {

		this.treeView = new Tree();
		this.treeView.setDebugId("treeView");
		this.treeView.setContainerDataSource(new HierarchicalContainer());
		this.treeView.setDropHandler(new NurseryTreeDropHandler(this.treeView, this.presenter));
		this.treeView.setDragMode(TreeDragMode.NODE);

		this.addInstanceTree(this.treeView, root);

		this.treeView.addListener(new NurseryListTreeExpandListener(this));
		this.treeView.addListener(new DashboardMainTreeListener(this, this.project));
		this.treeView.setImmediate(true);
		this.treeView.setNullSelectionAllowed(false);

	}

	private void addInstanceTree(Tree treeView, List<Reference> folders) {
		String folderName = NurseryListPreview.NURSERIES_AND_TRIALS;
		treeView.addItem(folderName);
		treeView.setItemCaption(folderName, folderName);
		treeView.setItemIcon(folderName, this.folderResource);

		for (Reference folderReference : folders) {
			treeView.addItem(folderReference.getId());
			treeView.setItemCaption(folderReference.getId(), folderReference.getName());
			treeView.setParent(folderReference.getId(), folderName);
			boolean isFolder = folderReference.isFolder();

			if (isFolder) {
				treeView.setChildrenAllowed(folderReference.getId(), true);
				treeView.setItemIcon(folderReference.getId(), this.folderResource);
			} else {
				treeView.setChildrenAllowed(folderReference.getId(), false);
				treeView.setItemIcon(folderReference.getId(), this.studyResource);
			}

			treeView.setSelectable(true);
		}
	}

	public void expandTree(Object itemId) {

		if (itemId == null) {
			return;
		}

		if (this.treeView.isExpanded(itemId)) {
			this.treeView.collapseItem(itemId);
			this.treeView.select(itemId);
		} else {
			this.treeView.expandItem(itemId);
			this.treeView.select(itemId);
		}

		this.treeView.setImmediate(true);
	}

	protected void initializeLayout() {
		this.setSizeFull();
	}

	protected void initializeActions() {
		// empty block of code
	}

	protected void assemble() {
		this.initializeLayout();
		this.initializeActions();
	}

	public NurseryListPreviewPresenter getPresenter() {
		return this.presenter;
	}

	public void setPresenter(NurseryListPreviewPresenter presenter) {
		this.presenter = presenter;
	}

	private Component buildToolbar() {
		this.toolbar = new HorizontalLayout();
		this.toolbar.setDebugId("toolbar");
		this.toolbar.setSpacing(true);
		this.toolbar.setMargin(true);

		this.openStudyManagerBtn =
				new Button("<span class='glyphicon glyphicon-open' style='right: 4px'></span>"
						+ this.messageSource.getMessage(Message.LAUNCH));
		this.openStudyManagerBtn.setHtmlContentAllowed(true);
		this.openStudyManagerBtn.setDescription(this.messageSource.getMessage(Message.OPEN_IN_STUDY_BROWSER));
		this.openStudyManagerBtn.setEnabled(false);

		this.renameFolderBtn = new Button("<span class='bms-edit' style='color:#0082CB'><span>");
		this.renameFolderBtn.setDebugId("renameFolderBtn");
		this.renameFolderBtn.setHtmlContentAllowed(true);
		this.renameFolderBtn.setDescription(this.messageSource.getMessage(Message.RENAME_ITEM));

		this.addFolderBtn = new Button("<span class='bms-add' style='color:#00AF40'></span>");
		this.addFolderBtn.setDebugId("addFolderBtn");
		this.addFolderBtn.setHtmlContentAllowed(true);
		this.addFolderBtn.setDescription(this.messageSource.getMessage(Message.ADD_FOLDER));

		this.deleteFolderBtn = new Button("<span class='bms-delete' style='color:#F4A41C'></span>");
		this.deleteFolderBtn.setDebugId("deleteFolderBtn");
		this.deleteFolderBtn.setHtmlContentAllowed(true);
		this.deleteFolderBtn.setDescription(this.messageSource.getMessage(Message.DELETE_ITEM));

		this.openStudyManagerBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.renameFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " " + NurseryListPreview.ACTION_STYLE_CLASS);
		this.addFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " " + NurseryListPreview.ACTION_STYLE_CLASS);
		this.deleteFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " " + NurseryListPreview.ACTION_STYLE_CLASS);

		this.openStudyManagerBtn.setWidth("100px");
		this.renameFolderBtn.setWidth("26px");
		this.addFolderBtn.setWidth("26px");
		this.deleteFolderBtn.setWidth("26px");

		this.toolbar.addComponent(this.openStudyManagerBtn);

		Label spacer = new Label("");
		spacer.setDebugId("spacer");
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
		this.openStudyManagerBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (NurseryListPreview.this.treeView.getValue() == null || NurseryListPreview.this.treeView.getValue() instanceof String) {
					MessageNotifier.showError(event.getComponent().getWindow(),
							NurseryListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION),
							NurseryListPreview.this.messageSource.getMessage(Message.INVALID_NO_SELECTION));
					return;
				}

				NurseryListPreview.this.presenter.updateProjectLastOpenedDate();

				// page change to list manager, with parameter passed
				Object value = NurseryListPreview.this.treeView.getValue();

				// update sidebar selection
				NurseryListPreview.LOG.trace("selecting sidebar");
				WorkbenchMainView mainWindow = (WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow();

				if (null != WorkbenchSidebar.sidebarTreeMap.get("study_browser")) {
					mainWindow.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("study_browser"));
				}

				// launch tool
				int studyId = ((Integer) value).intValue();
				StudyType studyType = NurseryListPreview.this.presenter.getStudyType(studyId);
				if (studyType != null && studyType.getId() == StudyType.T.getId()) {
					new LaunchWorkbenchToolAction(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB, studyId).buttonClick(event);
				} else if (studyType != null && studyType.getId() == StudyType.N.getId()) {
					new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB, studyId).buttonClick(event);
				} else {
					new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER_WITH_ID, studyId).buttonClick(event);
				}
			}
		});

		this.renameFolderBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (NurseryListPreview.this.treeView.getValue() == null) {
					MessageNotifier.showError(event.getComponent().getWindow(),
							NurseryListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION),
							NurseryListPreview.this.messageSource.getMessage(Message.INVALID_ITEM_NO_RENAME_SELECT));
					return;
				}

				if (NurseryListPreview.this.treeView.getValue() instanceof String) {
					MessageNotifier.showError(event.getComponent().getWindow(), NurseryListPreview.this.messageSource
							.getMessage(Message.INVALID_NO_SELECTION), NurseryListPreview.this.messageSource.getMessage(
							Message.INVALID_CANNOT_RENAME_ITEM, (String) NurseryListPreview.this.treeView.getValue()));
					return;
				}

				if (!NurseryListPreview.this.presenter.isFolder((Integer) NurseryListPreview.this.treeView.getValue())) {
					MessageNotifier.showError(event.getComponent().getWindow(),
							NurseryListPreview.this.messageSource.getMessage(Message.INVALID_ITEM_NO_RENAME_SELECT), "");
					return;
				}

				final InputPopup w =
						new InputPopup(NurseryListPreview.this.messageSource.getMessage(Message.RENAME_ITEM),
								NurseryListPreview.this.messageSource.getMessage(Message.ITEM_NAME), NurseryListPreview.this.treeView
										.getItemCaption(NurseryListPreview.this.treeView.getValue()));

				w.setOkListener(new Button.ClickListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(Button.ClickEvent event) {
						try {
							NurseryListPreview.this.presenter.renameNurseryListFolder(w.getFieldVal(),
									(Integer) NurseryListPreview.this.treeView.getValue());
						} catch (Exception e) {
							NurseryListPreview.LOG.error(e.getMessage(), e);
							MessageNotifier.showError(event.getComponent().getWindow(),
									NurseryListPreview.this.messageSource.getMessage(Message.INVALID_INPUT), e.getMessage());
							return;
						}

						// update UI
						NurseryListPreview.this.treeView.setItemCaption(NurseryListPreview.this.treeView.getValue(), w.getFieldVal());

						// close popup
						IBPWorkbenchApplication.get().getMainWindow().removeWindow(w);
					}
				});

				// show window
				event.getComponent().getParent().getWindow().addWindow(w);

			}
		});

		this.addFolderBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				final InputPopup w =
						new InputPopup(NurseryListPreview.this.messageSource.getMessage(Message.ADD_FOLDER),
								NurseryListPreview.this.messageSource.getMessage(Message.FOLDER_NAME), "");

				w.setOkListener(new Button.ClickListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(Button.ClickEvent event) {
						Integer newItem = null;
						try {
							if (NurseryListPreview.this.treeView.getValue() instanceof String) {
								// top folder
								newItem =
										NurseryListPreview.this.presenter.addNurseryListFolder(w.getFieldVal(),
												NurseryListPreview.ROOT_FOLDER);
							} else {
								newItem =
										NurseryListPreview.this.presenter.addNurseryListFolder(w.getFieldVal(),
												(Integer) NurseryListPreview.this.treeView.getValue());
							}
						} catch (Exception e) {
							NurseryListPreview.LOG.error(e.getMessage(), e);
							MessageNotifier.showError(event.getComponent().getWindow(),
									NurseryListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
							return;
						}

						// update UI
						if (newItem != null) {
							NurseryListPreview.this.treeView.addItem(newItem);
							NurseryListPreview.this.treeView.setItemCaption(newItem, w.getFieldVal());
							NurseryListPreview.this.treeView.setChildrenAllowed(newItem, true);
							NurseryListPreview.this.treeView.setItemIcon(newItem, NurseryListPreview.this.folderResource);

							DmsProject parent = (DmsProject) NurseryListPreview.this.presenter.getStudyNodeParent(newItem);
							boolean isRoot = parent == null || parent.getProjectId().intValue() == NurseryListPreview.ROOT_FOLDER;
							if (!isRoot) {
								NurseryListPreview.this.treeView.setParent(newItem, parent.getProjectId());
							} else {
								NurseryListPreview.this.treeView.setParent(newItem, NurseryListPreview.NURSERIES_AND_TRIALS);
							}

							if (!isRoot) {
								if (!NurseryListPreview.this.treeView.isExpanded(parent.getProjectId())) {
									NurseryListPreview.this.expandTree(parent.getProjectId());
								}
							} else {
								NurseryListPreview.this.treeView.expandItem(NurseryListPreview.NURSERIES_AND_TRIALS);
							}

							NurseryListPreview.this.treeView.select(newItem);
							NurseryListPreview.this.treeView.setImmediate(true);
							NurseryListPreview.this.processToolbarButtons(newItem);
						}

						// close popup
						IBPWorkbenchApplication.get().getMainWindow().removeWindow(event.getComponent().getWindow());
					}
				});

				// show window
				event.getComponent().getWindow().addWindow(w);
			}
		});

		this.deleteFolderBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {

				NurseryListPreview.LOG.trace(NurseryListPreview.this.treeView.getValue() != null ? NurseryListPreview.this.treeView
						.getValue().toString() : null);

				if (NurseryListPreview.this.treeView.getValue() instanceof String) {
					MessageNotifier.showError(event.getComponent().getWindow(), NurseryListPreview.this.messageSource
							.getMessage(Message.INVALID_OPERATION), NurseryListPreview.this.messageSource.getMessage(
							Message.INVALID_CANNOT_DELETE_ITEM, NurseryListPreview.this.treeView.getValue().toString()));
					return;
				}

				Integer id;

				try {
					id =
							NurseryListPreview.this.presenter.validateForDeleteNurseryList((Integer) NurseryListPreview.this.treeView
									.getValue());
				} catch (Exception e) {
					NurseryListPreview.LOG.error(e.getMessage(), e);
					MessageNotifier.showError(event.getComponent().getWindow(),
							NurseryListPreview.this.messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
					return;
				}

				final Integer finalId = id;
				ConfirmDialog.show(event.getComponent().getWindow(), NurseryListPreview.this.messageSource.getMessage(Message.DELETE_ITEM),
						NurseryListPreview.this.messageSource.getMessage(Message.DELETE_ITEM_CONFIRM),
						NurseryListPreview.this.messageSource.getMessage(Message.YES), NurseryListPreview.this.messageSource
								.getMessage(Message.NO), new DeleteConfirmDialogListener(NurseryListPreview.this.presenter,
								NurseryListPreview.this.treeView, finalId, event));
			}
		});
	}

	public void addChildrenNode(int parentId, List<Reference> studyChildren) {
		for (Reference sc : studyChildren) {
			this.treeView.addItem(sc.getId());
			this.treeView.setItemCaption(sc.getId(), sc.getName());
			this.treeView.setParent(sc.getId(), parentId);
			// check if the study has sub study
			if (sc.isFolder()) {
				this.treeView.setChildrenAllowed(sc.getId(), true);
				this.treeView.setItemIcon(sc.getId(), this.folderResource);
			} else {
				this.treeView.setChildrenAllowed(sc.getId(), false);
				this.treeView.setItemIcon(sc.getId(), this.studyResource);
			}
			this.treeView.setSelectable(true);
		}
		this.treeView.select(parentId);
		this.treeView.setImmediate(true);
	}

	public void setToolbarButtonsEnabled(boolean enabled) {
		this.addFolderBtn.setEnabled(enabled);
		this.renameFolderBtn.setEnabled(enabled);
		this.deleteFolderBtn.setEnabled(enabled);
	}

	public void setToolbarAddButtonEnabled(boolean enabled) {
		this.addFolderBtn.setEnabled(enabled);
	}

	public void setToolbarDeleteButtonEnabled(boolean enabled) {
		this.deleteFolderBtn.setEnabled(enabled);
	}

	public void setToolbarLaunchButtonEnabled(boolean enabled) {
		this.openStudyManagerBtn.setEnabled(enabled);
	}

	public void processToolbarButtons(Object treeItem) {

		boolean isMyStudy = treeItem instanceof String && treeItem.equals(NurseryListPreview.NURSERIES_AND_TRIALS);
		boolean isFolder = treeItem instanceof String || this.getPresenter().isFolder((Integer) treeItem);

		// set the toolbar button state
		if (isMyStudy) {
			this.setToolbarButtonsEnabled(false);
			this.setToolbarAddButtonEnabled(true);
		} else {
			this.setToolbarButtonsEnabled(true);
		}

		// set the launch button state
		this.setToolbarLaunchButtonEnabled(!isMyStudy && !isFolder);
	}

	public Tree getTreeView() {
		return this.treeView;
	}

	public Button getRenameFolderBtn() {
		return this.renameFolderBtn;
	}

	public Button getAddFolderBtn() {
		return this.addFolderBtn;
	}

	public Button getDeleteFolderBtn() {
		return this.deleteFolderBtn;
	}

	public Button getOpenStudyManagerBtn() {
		return this.openStudyManagerBtn;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
