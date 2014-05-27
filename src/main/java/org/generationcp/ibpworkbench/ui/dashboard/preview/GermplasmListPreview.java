package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
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
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainTreeListener;
import org.generationcp.ibpworkbench.ui.dashboard.listener.GermplasmListTreeExpandListener;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.middleware.pojos.GermplasmList;
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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/19/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class GermplasmListPreview extends VerticalLayout {
    @Autowired
    private SessionData sessionData;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    private GermplasmListPreviewPresenter presenter;
    private static final Logger LOG = LoggerFactory.getLogger(GermplasmListPreview.class);
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        folderResource = new ThemeResource("images/folder.png");
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

        //this.toolbar.setSizeFull();
        this.toolbar.setWidth("100%");

        initializeToolbarActions();

        return this.toolbar;
    }

    private void initializeToolbarActions() {
        openListManagerBtn.addListener(new Button.ClickListener() {

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

                if (null != WorkbenchSidebar.sidebarTreeMap.get("manage_list"))
                    mainWindow.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("manage_list"));

                // page change to list manager, with parameter passed
                (new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.BM_LIST_MANAGER, sessionData.getSelectedProject(), (Integer) lastItemId)).buttonClick(event);

            }
        });

        renameFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
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

                final Window w = new Window(messageSource.getMessage(Message.RENAME_ITEM));
                w.setWidth("300px");
                w.setHeight("150px");
                w.setModal(true);
                w.setResizable(false);
                w.setStyleName(Reindeer.WINDOW_LIGHT);

                VerticalLayout container = new VerticalLayout();
                container.setSpacing(true);
                container.setMargin(true);

                HorizontalLayout formContainer = new HorizontalLayout();
                formContainer.setSpacing(true);

                Label l = new Label(messageSource.getMessage(Message.ITEM_NAME));
                final TextField name = new TextField();
                name.setValue(treeView.getItemCaption(lastItemId));

                formContainer.addComponent(l);
                formContainer.addComponent(name);

                HorizontalLayout btnContainer = new HorizontalLayout();
                btnContainer.setSpacing(true);
                btnContainer.setWidth("100%");

                Label spacer = new Label("");
                btnContainer.addComponent(spacer);
                btnContainer.setExpandRatio(spacer, 1.0F);

                Button ok = new Button(messageSource.getMessage(Message.OK));
                ok.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
                ok.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        try {
                            presenter.renameGermplasmListFolder(name.getValue().toString(), (Integer) lastItemId);
                        } catch (Error e) {
                            MessageNotifier.showError(event.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_INPUT), e.getMessage());
                            return;
                        }

                        // update UI
                        treeView.setItemCaption(lastItemId, name.getValue().toString());

                        // close popup
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(event.getComponent().getWindow());
                    }
                });

                Button cancel = new Button(messageSource.getMessage(Message.CANCEL));
                cancel.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(w);
                    }
                });

                btnContainer.addComponent(ok);
                btnContainer.addComponent(cancel);

                container.addComponent(formContainer);
                container.addComponent(btnContainer);

                w.setContent(container);

                // show window
                IBPWorkbenchApplication.get().getMainWindow().addWindow(w);

            }
        });

        addFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                final Window w = new Window(messageSource.getMessage(Message.ADD_FOLDER));
                w.setWidth("300px");
                w.setHeight("150px");
                w.setModal(true);
                w.setResizable(false);
                w.setStyleName(Reindeer.WINDOW_LIGHT);

                VerticalLayout container = new VerticalLayout();
                container.setSpacing(true);
                container.setMargin(true);

                HorizontalLayout formContainer = new HorizontalLayout();
                formContainer.setSpacing(true);

                Label l = new Label(messageSource.getMessage(Message.FOLDER_NAME));
                final TextField name = new TextField();

                formContainer.addComponent(l);
                formContainer.addComponent(name);

                HorizontalLayout btnContainer = new HorizontalLayout();
                btnContainer.setSpacing(true);
                btnContainer.setWidth("100%");

                Label spacer = new Label("");
                btnContainer.addComponent(spacer);
                btnContainer.setExpandRatio(spacer, 1.0F);

                Button ok = new Button(messageSource.getMessage(Message.OK));
                ok.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
                ok.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        Integer newItem = null;
                        try {

                            if (treeView.getValue() instanceof String)
                                newItem = presenter.addGermplasmListFolder(name.getValue().toString(), null);
                            else
                                newItem = presenter.addGermplasmListFolder(name.getValue().toString(), (Integer) treeView.getValue());
                        } catch (Error e) {
                            MessageNotifier.showError(event.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
                            return;
                        }

                        //update UI
                        if (newItem != null) {
                            treeView.addItem(newItem);
                            treeView.setItemCaption(newItem, name.getValue().toString());
                            treeView.setChildrenAllowed(newItem, true);
                            treeView.setItemIcon(newItem, folderResource);

                            GermplasmList parent = presenter.getGermplasmListParent(newItem);
                            if (parent != null) {
                                treeView.setParent(newItem, parent.getId());
                            } else {
                                treeView.setParent(newItem, messageSource.getMessage(Message.PROGRAM_LIST));
                            }

                            if (parent != null) {
                                if (!treeView.isExpanded(parent.getId()))
                                    expandTree(parent.getId());
                            } else
                                treeView.expandItem(MY_LIST);

                            treeView.select(newItem);
                            lastItemId = newItem;
                            treeView.setImmediate(true);
                            processToolbarButtons(newItem);
                        }

                        // close popup
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(event.getComponent().getWindow());
                    }
                });

                Button cancel = new Button(messageSource.getMessage(Message.CANCEL));
                cancel.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(w);
                    }
                });

                btnContainer.addComponent(ok);
                btnContainer.addComponent(cancel);

                container.addComponent(formContainer);
                container.addComponent(btnContainer);

                w.setContent(container);

                // show window
                IBPWorkbenchApplication.get().getMainWindow().addWindow(w);
            }
        });

        deleteFolderBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(final Button.ClickEvent event) {

                if (lastItemId instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_CANNOT_DELETE_ITEM));
                    return;
                }

                GermplasmList gpList = null;

                try {
                    gpList = presenter.validateForDeleteGermplasmList((Integer) lastItemId);
                } catch (Error e) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.ERROR), e.getMessage());
                    return;
                }

                final GermplasmList finalGpList = gpList;
                ConfirmDialog.show(event.getComponent().getWindow(),
                        messageSource.getMessage(Message.DELETE_ITEM),
                        messageSource.getMessage(Message.DELETE_ITEM_CONFIRM),
                        messageSource.getMessage(Message.YES), messageSource.getMessage(Message.NO), new ConfirmDialog.Listener() {
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
                            } catch (Error e) {
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
        //treeView = new Tree("");
        //this.setHeight("400px");
        //this.setHeight("100%");
        //this.addComponent(treeView);
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
        //return germplasmListTree;
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

    public void addGermplasmListNode(int parentGermplasmListId, List<GermplasmList> germplasmListChildren, Object itemId) throws InternationalizableException {

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
        //System.out.println("add node " + itemId);
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

    }

    public void processToolbarButtons(Object treeItem) {
        boolean isSharedListNode = (treeItem instanceof String && treeItem.equals(GermplasmListPreview.SHARED_LIST));
        boolean isCentralGermplasmList = (treeItem instanceof Integer && ((Integer) treeItem).intValue() > 0);
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
