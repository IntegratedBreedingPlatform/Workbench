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
import org.generationcp.ibpworkbench.ui.dashboard.listener.GermplasmListTreeExpandListener;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.middleware.pojos.GermplasmList;
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
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class GermplasmListPreview extends VerticalLayout {

	private static final long serialVersionUID = 1941905235449423109L;

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListPreview.class);
	
    @Autowired
    private SessionData sessionData;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    private GermplasmListPreviewPresenter presenter;
    
    private Tree treeView;

    private Project project;


    private ThemeResource folderResource;
    private ThemeResource leafResource;

    private Panel panel;
    private HorizontalLayout toolbar;



    public static String MY_LIST = "";
    public static String SHARED_LIST = "";

    private Button openListManagerBtn;
    private Button addFolderBtn;
    private Button deleteFolderBtn;
    private Button renameFolderBtn;

    private Object lastItemId;

    public GermplasmListPreview(Project project) {
        this.project = project;

        presenter = new GermplasmListPreviewPresenter(this, project);

        try {
            if (project != null) {
                assemble();
            }
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
        }

        folderResource = new ThemeResource("../vaadin-retro/svg/folder-icon.svg");
        leafResource = new ThemeResource("images/leaf_16.png");


    }

    public void setProject(Project project) {
        this.removeAllComponents();
        this.setSizeFull();

        // add toolbar here
        panel = new Panel();
        panel.removeAllComponents();

        this.addComponent(buildToolbar());

        this.project = project;
        presenter = new GermplasmListPreviewPresenter(this, this.project);
        presenter.generateInitialTreeNode();

        CssLayout treeContainer = new CssLayout();
        treeContainer.setSizeUndefined();
        treeContainer.addComponent(treeView);

        panel.setContent(treeContainer);
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        panel.setSizeFull();

        this.addComponent(panel);
        this.setExpandRatio(panel, 1.0F);


    }

    private Component buildToolbar() {
        this.toolbar = new HorizontalLayout();
        this.toolbar.setSpacing(true);
        this.toolbar.setMargin(true);

        openListManagerBtn = new Button("<span class='glyphicon glyphicon-open' style='right: 4px'></span>" + messageSource.getMessage(Message.LAUNCH));
        openListManagerBtn.setHtmlContentAllowed(true);
        openListManagerBtn.setDescription(messageSource.getMessage(Message.OPEN_IN_LIST_MANAGER));
        openListManagerBtn.setEnabled(false);

        renameFolderBtn = new Button("<span class='bms-edit' style='color:#0082CB'><span>");
        renameFolderBtn.setHtmlContentAllowed(true);
        renameFolderBtn.setDescription(messageSource.getMessage(Message.RENAME_ITEM));

        addFolderBtn = new Button("<span class='bms-add' style='color:#00AF40'></span>");
        addFolderBtn.setHtmlContentAllowed(true);
        addFolderBtn.setDescription(messageSource.getMessage(Message.ADD_FOLDER));

        deleteFolderBtn = new Button("<span class='bms-delete' style='color:#F4A41C'></span>");
        deleteFolderBtn.setHtmlContentAllowed(true);
        deleteFolderBtn.setDescription(messageSource.getMessage(Message.DELETE_ITEM));

        openListManagerBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        renameFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " action");
        addFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " action");
        deleteFolderBtn.setStyleName(Bootstrap.Buttons.LINK.styleName() + " action");

        openListManagerBtn.setWidth("100px");
        renameFolderBtn.setWidth("26px");
        addFolderBtn.setWidth("26px");
        deleteFolderBtn.setWidth("26px");

        this.toolbar.addComponent(openListManagerBtn);

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
        openListManagerBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (lastItemId == null || lastItemId instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_NO_SELECTION));
                    return;
                }

                if (presenter.isFolder((Integer) lastItemId)) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_ITEM_IS_FOLDER, treeView.getItemCaption(lastItemId)));
                    return;
                }
                presenter.updateProjectLastOpenedDate();

                //update sidebar selection
                LOG.trace("selecting sidebar");
                WorkbenchMainView mainWindow = (WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow();

                if (null != WorkbenchSidebar.sidebarTreeMap.get("manage_list")){
                    mainWindow.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("manage_list"));
                }
                // page change to list manager, with parameter passed
                (new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.BM_LIST_MANAGER, sessionData.getSelectedProject(), (Integer) lastItemId)).buttonClick(event);

            }
        });

        renameFolderBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final Button.ClickEvent event) {
                if (lastItemId == null) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_ITEM_NO_RENAME_SELECT));
                    return;
                }

                if (lastItemId instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_CANNOT_RENAME_ITEM, (String) lastItemId));
                    return;
                }

                if (!presenter.isFolder((Integer) lastItemId)) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_ITEM_NO_RENAME_SELECT));
                    return;
                }

                final InputPopup w = new InputPopup(messageSource.getMessage(Message.RENAME_ITEM),messageSource.getMessage(Message.ITEM_NAME),treeView.getItemCaption(lastItemId));
                w.setOkListener(new Button.ClickListener(){
					private static final long serialVersionUID = -242570054807727077L;

					@Override
                    public void buttonClick(Button.ClickEvent event1) {
                        try {
                            presenter.renameGermplasmListFolder(w.getFieldVal(), (Integer) lastItemId);
                        } catch (Exception e) {
                        	LOG.error(e.getMessage(), e);
                            MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getMessage());
                            return;
                        }

                        // update UI
                        treeView.setItemCaption(lastItemId, w.getFieldVal());

                        // close popup
                        event.getComponent().getWindow().removeWindow(w);
                    }
                });

                // show window
                event.getComponent().getWindow().addWindow(w);

            }
        });

        final InputPopup addFolderPopup = new InputPopup(messageSource.getMessage(Message.ADD_FOLDER),messageSource.getMessage(Message.ITEM_NAME),"");
        addFolderPopup.setOkListener(new Button.ClickListener() {
			private static final long serialVersionUID = 2842797806931785183L;

			@Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Integer newItem = null;
                try {

                    if (treeView.getValue() instanceof String){
                        newItem = presenter.addGermplasmListFolder(addFolderPopup.getFieldVal(), null);
                    } else {
                        newItem = presenter.addGermplasmListFolder(addFolderPopup.getFieldVal(), (Integer) treeView.getValue());
                    }

                    //update UI
                    if (newItem != null) {
                        treeView.addItem(newItem);
                        treeView.setItemCaption(newItem, addFolderPopup.getFieldVal());
                        treeView.setChildrenAllowed(newItem, true);
                        treeView.setItemIcon(newItem, folderResource);

                        GermplasmList parent = presenter.getGermplasmListParent(newItem);
                        if (parent != null) {
                            treeView.setParent(newItem, parent.getId());
                        } else {
                            treeView.setParent(newItem, messageSource.getMessage(Message.PROGRAM_LIST));
                        }

                        if (parent != null && !treeView.isExpanded(parent.getId())){
                        	expandTree(parent.getId());
                        } else {
                            treeView.expandItem(MY_LIST);
                        }

                        treeView.select(newItem);
                        lastItemId = newItem;
                        treeView.setImmediate(true);
                        processToolbarButtons(newItem);
                    }


                } catch (Exception e) {
                	LOG.error(e.getMessage(), e);
                    MessageNotifier.showError(clickEvent.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
                    return;
                }



                // close popup
                IBPWorkbenchApplication.get().getMainWindow().removeWindow(clickEvent.getComponent().getWindow());

            }
        });

        addFolderBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addFolderPopup.clearFieldVal();
                event.getComponent().getWindow().addWindow(addFolderPopup);
            }
        });

        deleteFolderBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final Button.ClickEvent event) {

                if (lastItemId instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_CANNOT_DELETE_ITEM));
                    return;
                }

                GermplasmList gpList = null;

                try {
                    gpList = presenter.validateForDeleteGermplasmList((Integer) lastItemId);
                } catch (Exception e) {
                	LOG.error(e.getMessage(), e);
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.ERROR), e.getMessage());
                    return;
                }

                final GermplasmList finalGpList = gpList;
                ConfirmDialog.show(event.getComponent().getWindow(),
                        messageSource.getMessage(Message.DELETE_ITEM),
                        messageSource.getMessage(Message.DELETE_ITEM_CONFIRM),
                        messageSource.getMessage(Message.YES), messageSource.getMessage(Message.NO), new ConfirmDialog.Listener() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void onClose(ConfirmDialog dialog) {
                                if (dialog.isConfirmed()) {
                                    try {
                                        GermplasmList parent = presenter.getGermplasmListParent(finalGpList.getId());
                                        presenter.deleteGermplasmListFolder(finalGpList);
                                        treeView.removeItem(lastItemId);
                                        treeView.select(null);
                                        if (parent == null) {
                                            treeView.select(MY_LIST);
                                            lastItemId = MY_LIST;
                                            processToolbarButtons(MY_LIST);
                                        } else {
                                            treeView.select(parent.getId());
                                            lastItemId = parent.getId();
                                            processToolbarButtons(parent.getId());
                                        }
                                        treeView.setImmediate(true);
                                    } catch (Exception e) {
                                    	LOG.error(e.getMessage(), e);
                                        MessageNotifier.showError(event.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
                                    }
                                }
                            }
                });
            }
        });
    }

    public void expandTree(Object itemId) {

        if (treeView.isExpanded(itemId)) {
            treeView.collapseItem(itemId);
        } else {
            treeView.expandItem(itemId);
        }
        lastItemId = itemId;

        treeView.select(itemId);

        treeView.setImmediate(true);
    }

    protected void initializeComponents() {
    	// do nothing
    }

    public void generateTree(List<GermplasmList> germplasmListParentLocal, List<GermplasmList> germplasmListParentCentral) {
        MY_LIST = messageSource.getMessage(Message.PROGRAM_LIST);
        SHARED_LIST = messageSource.getMessage(Message.SHARED_LIST);

        lastItemId = null;
        treeView = new Tree();
        treeView.setContainerDataSource(new HierarchicalContainer());
        treeView.setDropHandler(new GermplasmListTreeDropHandler(treeView, presenter));
        treeView.setDragMode(TreeDragMode.NODE);

        treeView.addItem(MY_LIST);
        treeView.setItemCaption(MY_LIST, MY_LIST);
        treeView.setItemIcon(MY_LIST, folderResource);

        treeView.addItem(SHARED_LIST);
        treeView.setItemCaption(SHARED_LIST, SHARED_LIST);
        treeView.setItemIcon(SHARED_LIST, folderResource);

        treeView.setNullSelectionAllowed(false);

        for (GermplasmList parentList : germplasmListParentLocal) {
            treeView.addItem(parentList.getId());
            treeView.setItemCaption(parentList.getId(), parentList.getName());
            treeView.setParent(parentList.getId(), MY_LIST);
            boolean hasChildList = getPresenter().hasChildList(parentList.getId());

            treeView.setChildrenAllowed(parentList.getId(), hasChildList);
            
            if (parentList.isFolder()) {
            	treeView.setItemIcon(parentList.getId(), folderResource);
            } else {
                treeView.setItemIcon(parentList.getId(), leafResource);
            }

            treeView.setSelectable(true);
        }
        for (GermplasmList parentList : germplasmListParentCentral) {
            treeView.addItem(parentList.getId());
            treeView.setItemCaption(parentList.getId(), parentList.getName());
            treeView.setParent(parentList.getId(), SHARED_LIST);
            boolean hasChildList = getPresenter().hasChildList(parentList.getId());

            treeView.setChildrenAllowed(parentList.getId(), hasChildList);
            
            if (parentList.isFolder()) {
            	treeView.setItemIcon(parentList.getId(), folderResource);
            } else {
                treeView.setItemIcon(parentList.getId(), leafResource);
            }

            treeView.setSelectable(true);
        }

        treeView.addListener(new GermplasmListTreeExpandListener(this));
        treeView.addListener(new DashboardMainTreeListener(this, project));
        treeView.setImmediate(true);
    }

    /**
     * Set the toolbar button's enabled state.
     *
     * @param enabled
     */
    public void setToolbarButtonsEnabled(boolean enabled) {
        addFolderBtn.setEnabled(enabled);
        renameFolderBtn.setEnabled(enabled);
        deleteFolderBtn.setEnabled(enabled);
    }

    /**
     * Set the Add button's enabled state.
     *
     * @param enabled
     */
    public void setToolbarAddButtonEnabled(boolean enabled) {
        addFolderBtn.setEnabled(enabled);
    }
    
    /**
     * Set the Delete button's enabled state.
     *
     * @param enabled
     */
    public void setToolbarDeleteButtonEnabled(boolean enabled) {
    	deleteFolderBtn.setEnabled(enabled);
    }

    /**
     * Set the Launch button's enabled state.
     *
     * @param enabled
     */
    public void setToolbarLaunchButtonEnabled(boolean enabled) {
        openListManagerBtn.setEnabled(enabled);
    }

    public void addGermplasmListNode(int parentGermplasmListId, List<GermplasmList> germplasmListChildren, Object itemId) {

        for (GermplasmList listChild : germplasmListChildren) {

            boolean hasChildList = getPresenter().hasChildList(listChild.getId());

            treeView.addItem(listChild.getId());
            treeView.setItemCaption(listChild.getId(), listChild.getName());
            treeView.setParent(listChild.getId(), parentGermplasmListId);
            // allow children if list has sub-lists

            treeView.setChildrenAllowed(listChild.getId(), hasChildList);
            
            ThemeResource resource = leafResource;
            if (listChild.isFolder()) {
                resource = folderResource;
            }
            treeView.setItemIcon(listChild.getId(), resource);

            treeView.setSelectable(true);

        }
        LOG.trace("Add node {0}", itemId);
        treeView.select(itemId);
        lastItemId = itemId;
        treeView.setImmediate(true);
    }


    public GermplasmListPreviewPresenter getPresenter() {
        return presenter;
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
        boolean isSharedListNode = treeItem instanceof String && treeItem.equals(GermplasmListPreview.SHARED_LIST);
        boolean isCentralGermplasmList = treeItem instanceof Integer && ((Integer) treeItem).intValue() > 0;
        boolean isMyListNode = treeItem instanceof String && treeItem.equals(GermplasmListPreview.MY_LIST);
        boolean isFolder = treeItem instanceof String || getPresenter().isFolder((Integer) treeItem);

        // set the toolbar button state
        if (isSharedListNode || isCentralGermplasmList) {
            setToolbarButtonsEnabled(false);
        } else if (isMyListNode) {
            setToolbarButtonsEnabled(false);
            setToolbarAddButtonEnabled(true);
        } else if (!isFolder) {
            setToolbarButtonsEnabled(false);
            setToolbarAddButtonEnabled(true);
            setToolbarDeleteButtonEnabled(true);
        } else {
            setToolbarButtonsEnabled(true);
        }

        // set the launch button state
        setToolbarLaunchButtonEnabled(!isSharedListNode && !isMyListNode && !isFolder);
    }


    protected void assemble() throws Exception {

        initializeComponents();
        initializeLayout();
        initializeActions();

    }


    public ManagerFactoryProvider getManagerFactoryProvider() {
        return managerFactoryProvider;
    }


    public void setManagerFactoryProvider(
            ManagerFactoryProvider managerFactoryProvider) {
        this.managerFactoryProvider = managerFactoryProvider;
    }

}
