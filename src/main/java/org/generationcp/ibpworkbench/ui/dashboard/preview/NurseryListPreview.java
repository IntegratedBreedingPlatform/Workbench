package org.generationcp.ibpworkbench.ui.dashboard.preview;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
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

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:20 PM
 * <p/>
 * Revision done by mae
 * 1. Display hierarchy of studies from root to children per database instance (instead of categories like year, season and study type)
 */
@Configurable
public class NurseryListPreview extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private NurseryListPreviewPresenter presenter;

    private static final Logger LOG = LoggerFactory.getLogger(NurseryListPreview.class);

    private Tree treeView;

    private Project project;

    private Panel panel;

    private ThemeResource folderResource = new ThemeResource("../vaadin-retro/svg/folder-icon.svg");
    private ThemeResource studyResource = new ThemeResource("../vaadin-retro/svg/study-icon.svg");

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

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
        presenter = new NurseryListPreviewPresenter(this, project);

        try {
            if (project != null) {
                assemble();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }


    public void setProject(Project project) {
        this.removeAllComponents();
        this.setSizeFull();

        panel = new Panel();
        panel.removeAllComponents();

        this.addComponent(buildToolbar());

        this.project = project;

        NURSERIES_AND_TRIALS = messageSource.getMessage(Message.NURSERIES_AND_TRIALS);
        presenter = new NurseryListPreviewPresenter(this, project);

        presenter.generateInitialTreeNodes();

        CssLayout treeContainer = new CssLayout();
        treeContainer.setSizeUndefined();
        treeContainer.addComponent(treeView);

        panel.setContent(treeContainer);
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        panel.setSizeFull();

        this.addComponent(panel);
        this.setExpandRatio(panel, 1.0F);
    }

    public void generateTopListOfTree(List<FolderReference> root) {

        treeView = new Tree();
        treeView.setContainerDataSource(new HierarchicalContainer());
        treeView.setDropHandler(new NurseryTreeDropHandler(treeView, presenter));
        treeView.setDragMode(TreeDragMode.NODE);

        addInstanceTree(treeView, root);

        treeView.addListener(new NurseryListTreeExpandListener(this));
        treeView.addListener(new DashboardMainTreeListener(this, project));
        treeView.setImmediate(true);
        treeView.setNullSelectionAllowed(false);

    }


    private void addInstanceTree(Tree treeView, List<FolderReference> folders) {
    	String folderName = NURSERIES_AND_TRIALS;
        treeView.addItem(folderName);
        treeView.setItemCaption(folderName, folderName);
        treeView.setItemIcon(folderName, folderResource);


        for (FolderReference folderReference : folders) {
            treeView.addItem(folderReference.getId());
            treeView.setItemCaption(folderReference.getId(), folderReference.getName());
            treeView.setParent(folderReference.getId(), folderName);
            boolean isFolder = getPresenter().isFolder(folderReference.getId());

            if (isFolder) {
                treeView.setChildrenAllowed(folderReference.getId(), true);
                treeView.setItemIcon(folderReference.getId(), folderResource);
            } else {
                treeView.setChildrenAllowed(folderReference.getId(), false);
                treeView.setItemIcon(folderReference.getId(), studyResource);
            }

            treeView.setSelectable(true);
        }
    }

    public void expandTree(Object itemId) {

        if (itemId == null) {
            return;
        }

        if (treeView.isExpanded(itemId)) {
            treeView.collapseItem(itemId);
            treeView.select(itemId);
        } else {
            treeView.expandItem(itemId);
            treeView.select(itemId);
        }

        treeView.setImmediate(true);
    }

    protected void initializeLayout() {
        this.setSizeFull();
    }

    protected void initializeActions() {
    	//empty block of code
    }

    protected void assemble() {
        initializeLayout();
        initializeActions();
    }


    public NurseryListPreviewPresenter getPresenter() {
        return presenter;
    }


    public void setPresenter(NurseryListPreviewPresenter presenter) {
        this.presenter = presenter;
    }


    public ManagerFactoryProvider getManagerFactoryProvider() {
        return managerFactoryProvider;
    }


    public void setManagerFactoryProvider(
            ManagerFactoryProvider managerFactoryProvider) {
        this.managerFactoryProvider = managerFactoryProvider;
    }


    private Component buildToolbar() {
        this.toolbar = new HorizontalLayout();
        this.toolbar.setSpacing(true);
        this.toolbar.setMargin(true);

        openStudyManagerBtn = new Button("<span class='glyphicon glyphicon-open' style='right: 4px'></span>" + messageSource.getMessage(Message.LAUNCH));
        openStudyManagerBtn.setHtmlContentAllowed(true);
        openStudyManagerBtn.setDescription(messageSource.getMessage(Message.OPEN_IN_STUDY_BROWSER));
        openStudyManagerBtn.setEnabled(false);

        renameFolderBtn = new Button("<span class='bms-edit' style='color:#0082CB'><span>");
        renameFolderBtn.setHtmlContentAllowed(true);
        renameFolderBtn.setDescription(messageSource.getMessage(Message.RENAME_ITEM));

        addFolderBtn = new Button("<span class='bms-add' style='color:#00AF40'></span>");
        addFolderBtn.setHtmlContentAllowed(true);
        addFolderBtn.setDescription(messageSource.getMessage(Message.ADD_FOLDER));

        deleteFolderBtn = new Button("<span class='bms-delete' style='color:#F4A41C'></span>");
        deleteFolderBtn.setHtmlContentAllowed(true);
        deleteFolderBtn.setDescription(messageSource.getMessage(Message.DELETE_ITEM));

        openStudyManagerBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        renameFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " " + ACTION_STYLE_CLASS);
        addFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " " + ACTION_STYLE_CLASS);
        deleteFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " " + ACTION_STYLE_CLASS);

        openStudyManagerBtn.setWidth("100px");
        renameFolderBtn.setWidth("26px");
        addFolderBtn.setWidth("26px");
        deleteFolderBtn.setWidth("26px");

        this.toolbar.addComponent(openStudyManagerBtn);

        Label spacer = new Label("");
        this.toolbar.addComponent(spacer);
        this.toolbar.setExpandRatio(spacer, 1.0F);


        renameFolderBtn.setEnabled(false);
        addFolderBtn.setEnabled(false);
        deleteFolderBtn.setEnabled(false);

        this.toolbar.addComponent(addFolderBtn);
        this.toolbar.addComponent(renameFolderBtn);
        this.toolbar.addComponent(deleteFolderBtn);

        this.toolbar.setWidth("100%");

        initializeToolbarActions();

        return this.toolbar;
    }

    private void initializeToolbarActions() {
        openStudyManagerBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (treeView.getValue() == null || treeView.getValue() instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_NO_SELECTION));
                    return;
                }
                
                presenter.updateProjectLastOpenedDate();

                // page change to list manager, with parameter passed
                Project selectedProject = sessionData.getSelectedProject();
                Object value = treeView.getValue();


                //update sidebar selection
                LOG.trace("selecting sidebar");
                WorkbenchMainView mainWindow = (WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow();

                if (null != WorkbenchSidebar.sidebarTreeMap.get("study_browser")) {
                    mainWindow.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("study_browser"));
                }

                // launch tool
                int studyId = ((Integer) value).intValue();
                StudyType studyType = presenter.getStudyType(studyId);
                if(studyType!=null && studyType.getId()==StudyType.T.getId()) {
                	new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB, selectedProject, studyId).buttonClick(event);
                } else if(studyType!=null && studyType.getId()==StudyType.N.getId()) {
                	new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB, selectedProject, studyId).buttonClick(event);
                } else {
                	new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.STUDY_BROWSER_WITH_ID, selectedProject, studyId).buttonClick(event);
                }
                

            }
        });

        renameFolderBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (treeView.getValue() == null) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_ITEM_NO_RENAME_SELECT));
                    return;
                }

                if (treeView.getValue() instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_NO_SELECTION), messageSource.getMessage(Message.INVALID_CANNOT_RENAME_ITEM, (String) treeView.getValue()));
                    return;
                }

                if (!presenter.isFolder((Integer) treeView.getValue())) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_ITEM_NO_RENAME_SELECT), "");
                    return;
                }

                final InputPopup w = new InputPopup(messageSource.getMessage(Message.RENAME_ITEM),messageSource.getMessage(Message.ITEM_NAME),treeView.getItemCaption(treeView.getValue()));

                w.setOkListener(new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        try {
                            presenter.renameNurseryListFolder(w.getFieldVal(), (Integer) treeView.getValue());
                        } catch (Exception e) {
                        	LOG.error(e.getMessage(), e);
                            MessageNotifier.showError(event.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_INPUT), e.getMessage());
                            return;
                        }

                        // update UI
                        treeView.setItemCaption(treeView.getValue(), w.getFieldVal());

                        // close popup
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(w);
                    }
                });

                // show window
                event.getComponent().getParent().getWindow().addWindow(w);

            }
        });

        addFolderBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                final InputPopup w = new InputPopup(messageSource.getMessage(Message.ADD_FOLDER),messageSource.getMessage(Message.FOLDER_NAME),"");

                w.setOkListener(new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        Integer newItem = null;
                        try {
                            if (treeView.getValue() instanceof String) {
                            	//top folder
                                newItem = presenter.addNurseryListFolder(w.getFieldVal(), ROOT_FOLDER);
                            } else {
                                newItem = presenter.addNurseryListFolder(w.getFieldVal(), (Integer) treeView.getValue());
                            }
                        } catch (Exception e) {
                        	LOG.error(e.getMessage(), e);
                            MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
                            return;
                        }

                        //update UI
                        if (newItem != null) {
                            treeView.addItem(newItem);
                            treeView.setItemCaption(newItem, w.getFieldVal());
                            treeView.setChildrenAllowed(newItem, true);
                            treeView.setItemIcon(newItem, folderResource);

                            DmsProject parent = (DmsProject) presenter.getStudyNodeParent(newItem);
                            boolean isRoot = parent == null || parent.getProjectId().intValue() == ROOT_FOLDER;
                            if (!isRoot) {
                                treeView.setParent(newItem, parent.getProjectId());
                            } else {
                                treeView.setParent(newItem, NURSERIES_AND_TRIALS);
                            }

                            if (!isRoot) {
                                if (!treeView.isExpanded(parent.getProjectId())) {
                                    expandTree(parent.getProjectId());
                                }
                            } else {
                                treeView.expandItem(NURSERIES_AND_TRIALS);
                            }

                            treeView.select(newItem);
                            treeView.setImmediate(true);
                            processToolbarButtons(newItem);
                        }

                        // close popup
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(event.getComponent().getWindow());
                    }
                });

                // show window
                event.getComponent().getWindow().addWindow(w);
            }
        });

        deleteFolderBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final Button.ClickEvent event) {

                LOG.info(treeView.getValue() != null ? treeView.getValue().toString() : null);

                if (treeView.getValue() instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_CANNOT_DELETE_ITEM, treeView.getValue().toString()));
                    return;
                }

                Integer id;

                try {
                    id = presenter.validateForDeleteNurseryList((Integer) treeView.getValue());
                } catch (Exception e) {
                	LOG.error(e.getMessage(), e);
                    MessageNotifier.showError(event.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
                    return;
                }

                final Integer finalId = id;
                ConfirmDialog.show(event.getComponent().getWindow(),
                        messageSource.getMessage(Message.DELETE_ITEM),
                        messageSource.getMessage(Message.DELETE_ITEM_CONFIRM),
                        messageSource.getMessage(Message.YES), messageSource.getMessage(Message.NO), new DeleteConfirmDialogListener(presenter, treeView, finalId, event));
            }
        });
    }

    public void addChildrenNode(int parentId, List<Reference> studyChildren) {
        for (Reference sc : studyChildren) {
            treeView.addItem(sc.getId());
            treeView.setItemCaption(sc.getId(), sc.getName());
            treeView.setParent(sc.getId(), parentId);
            // check if the study has sub study
            if (presenter.isFolder(sc.getId())) {
                treeView.setChildrenAllowed(sc.getId(), true);
                treeView.setItemIcon(sc.getId(), folderResource);
            } else {
                treeView.setChildrenAllowed(sc.getId(), false);
                treeView.setItemIcon(sc.getId(), studyResource);
            }
            treeView.setSelectable(true);
        }
        treeView.select(parentId);
        treeView.setImmediate(true);
    }


    public void setToolbarButtonsEnabled(boolean enabled) {
        addFolderBtn.setEnabled(enabled);
        renameFolderBtn.setEnabled(enabled);
        deleteFolderBtn.setEnabled(enabled);
    }

    public void setToolbarAddButtonEnabled(boolean enabled) {
        addFolderBtn.setEnabled(enabled);
    }

    public void setToolbarDeleteButtonEnabled(boolean enabled) {
        deleteFolderBtn.setEnabled(enabled);
    }    
    
    public void setToolbarLaunchButtonEnabled(boolean enabled) {
        openStudyManagerBtn.setEnabled(enabled);
    }

    public void processToolbarButtons(Object treeItem) {

        boolean isMyStudy = treeItem instanceof String && treeItem.equals(NurseryListPreview.NURSERIES_AND_TRIALS);
        boolean isFolder = treeItem instanceof String || getPresenter().isFolder((Integer) treeItem);

        // set the toolbar button state
        if (isMyStudy) {
            setToolbarButtonsEnabled(false);
            setToolbarAddButtonEnabled(true);
        } else {
            setToolbarButtonsEnabled(true);
        }

        // set the launch button state
        setToolbarLaunchButtonEnabled(!isMyStudy && !isFolder);
    }


	public Tree getTreeView() {
		return treeView;
	}


	public Button getRenameFolderBtn() {
		return renameFolderBtn;
	}


	public Button getAddFolderBtn() {
		return addFolderBtn;
	}


	public Button getDeleteFolderBtn() {
		return deleteFolderBtn;
	}


	public Button getOpenStudyManagerBtn() {
		return openStudyManagerBtn;
	}


	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	
}
