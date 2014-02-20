package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.List;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainTreeListener;
import org.generationcp.ibpworkbench.ui.dashboard.listener.NurseryListTreeExpandListener;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
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

    private ThemeResource folderResource = new ThemeResource("images/folder.png");
    private ThemeResource leafResource = new ThemeResource("images/leaf_16.png");

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

    public static String SHARED_STUDIES;
    public static String MY_STUDIES;

    public static final int ROOT_FOLDER = 1;

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

        MY_STUDIES = messageSource.getMessage(Message.MY_STUDIES);
        SHARED_STUDIES = messageSource.getMessage(Message.SHARED_STUDIES);

        presenter = new NurseryListPreviewPresenter(this, project);
        //presenter.generateTreeNodes();
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

    protected void initializeComponents() {
        //this.setHeight("400px");
    }

    public void generateTopListOfTree(List<FolderReference> centralFolders, List<FolderReference> localFolders) {

        treeView = new Tree();
        treeView.setContainerDataSource(new HierarchicalContainer());
        treeView.setDropHandler(new NurseryTreeDropHandler(treeView, presenter));
        treeView.setDragMode(TreeDragMode.NODE);

        addInstanceTree(treeView, localFolders, false);
        addInstanceTree(treeView, centralFolders, true);

        treeView.addListener(new NurseryListTreeExpandListener(this));
        treeView.addListener(new DashboardMainTreeListener(this, project));
        treeView.setImmediate(true);
        treeView.setNullSelectionAllowed(false);

    }


    private void addInstanceTree(Tree treeView, List<FolderReference> folders, boolean isCentral) {


        String folderName = null;
        if (isCentral) {
            folderName = SHARED_STUDIES;
        } else {
            folderName = MY_STUDIES;
        }

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
                treeView.setItemIcon(folderReference.getId(), leafResource);
            }

            treeView.setSelectable(true);
        }
    }


//	public void generateTree(List<TreeNode> treeNodes){
//        
//        treeView = new Tree();
//        treeView.setDragMode(TreeDragMode.NODE);
//
//        doCreateTree(treeNodes, treeView, null, folderResource, leafResource);
//        
//        treeView.addListener(new DashboardMainTreeListener(this, project));
//        treeView.setImmediate(true);
//        
//    }

//    private void doCreateTree(List<TreeNode> treeNodes, Tree treeView, Object parent, ThemeResource folder, ThemeResource leaf){
//        for(TreeNode treeNode : treeNodes){
//        	
//            treeView.addItem(treeNode.getId());
//            treeView.setItemCaption(treeNode.getId(), treeNode.getName());
//
//            // Set resource icon
//            ThemeResource resource = folder;
//            if(treeNode.isLeaf()){
//                resource = leaf;
//                treeView.setChildrenAllowed(treeNode.getId(), false);
//                //we add listener if its the leaf
//                Item item = treeView.getItem(treeNode.getId());
//                
//                if (treeNode.getName().equals(messageSource.getMessage(Message.MY_STUDIES)) 
//                		|| treeNode.getName().equals(messageSource.getMessage(Message.SHARED_STUDIES))){
//                	resource = folder;
//                }
//            }
//            treeView.setItemIcon(treeNode.getId(), resource);
//
//            // Disable arrow of folders with no children
//            if (treeNode.getTreeNodeList().size() == 0){
//                treeView.setChildrenAllowed(treeNode.getId(), false);
//            }
//            
//            // Set parent
//            if(parent != null){
//                treeView.setParent(treeNode.getId(), parent);
//            }
//            
//            // Create children nodes
//            doCreateTree(treeNode.getTreeNodeList(), treeView, treeNode.getId(), folder, leaf);
//        }
//    }

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

    }

    protected void assemble() throws Exception {
        initializeComponents();
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

        renameFolderBtn = new Button("<span class='glyphicon glyphicon-pencil' style='right: 2px'></span>");
        renameFolderBtn.setHtmlContentAllowed(true);
        renameFolderBtn.setDescription(messageSource.getMessage(Message.RENAME_FOLDER));

        addFolderBtn = new Button("<span class='glyphicon glyphicon-plus' style='right: 2px'></span>");
        addFolderBtn.setHtmlContentAllowed(true);
        addFolderBtn.setDescription(messageSource.getMessage(Message.ADD_FOLDER));

        deleteFolderBtn = new Button("<span class='glyphicon glyphicon-trash' style='right: 2px'></span>");
        deleteFolderBtn.setHtmlContentAllowed(true);
        deleteFolderBtn.setDescription(messageSource.getMessage(Message.DELETE_FOLDER));

        openStudyManagerBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        renameFolderBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());
        addFolderBtn.setStyleName(Bootstrap.Buttons.INFO.styleName());
        deleteFolderBtn.setStyleName(Bootstrap.Buttons.DANGER.styleName());

        openStudyManagerBtn.setWidth("100px");
        renameFolderBtn.setWidth("40px");
        addFolderBtn.setWidth("40px");
        deleteFolderBtn.setWidth("40px");

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

        //this.toolbar.setSizeFull();
        this.toolbar.setWidth("100%");

        initializeToolbarActions();

        return this.toolbar;
    }

    private void initializeToolbarActions() {
        openStudyManagerBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (treeView.getValue() == null || treeView.getValue() instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_NO_SELECTION));
                    return;
                }
                /*
                if (presenter.isFolder((Integer)lastItemId)) {
                    MessageNotifier.showError(event.getComponent().getWindow(),"Selected Item is a folder","");
                    return;
                }*/

                presenter.updateProjectLastOpenedDate();

                // page change to list manager, with parameter passed
                Project project = sessionData.getSelectedProject();
                Object value = treeView.getValue();


                //update sidebar selection
                LOG.trace("selecting sidebar");
                WorkbenchMainView mainWindow = (WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow();

                if (null != WorkbenchSidebar.sidebarTreeMap.get("study_browser"))
                    mainWindow.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("study_browser"));

                // launch tool
                new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.STUDY_BROWSER_WITH_ID, project, ((Integer) value).intValue()).buttonClick(event);

            }
        });

        renameFolderBtn.addListener(new Button.ClickListener() {

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

                final Window w = new Window(messageSource.getMessage(Message.RENAME_LIST_FOLDER, treeView.getItemCaption(treeView.getValue())));
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

                Label l = new Label(messageSource.getMessage(Message.FOLDER_LIST_NAME));
                final TextField name = new TextField();
                name.setValue(treeView.getItemCaption(treeView.getValue()));

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
                            presenter.renameNurseryListFolder(name.getValue().toString(), (Integer) treeView.getValue());
                        } catch (Error e) {
                            MessageNotifier.showError(event.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
                            return;
                        }

                        // update UI
                        treeView.setItemCaption(treeView.getValue(), name.getValue().toString());

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

                Label l = new Label(messageSource.getMessage(Message.FOLDER_LIST_NAME));
                final TextField name = new TextField();

                formContainer.addComponent(l);
                formContainer.addComponent(name);

                HorizontalLayout btnContainer = new HorizontalLayout();
                btnContainer.setSpacing(true);
                btnContainer.setWidth("100%");

                Label spacer = new Label("");
                btnContainer.addComponent(spacer);
                btnContainer.setExpandRatio(spacer, 1.0F);

                Button ok = new Button("Ok");
                ok.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
                ok.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        Integer newItem = null;
                        try {
                            if (treeView.getValue() instanceof String)//top folder
                                newItem = presenter.addNurseryListFolder(name.getValue().toString(), ROOT_FOLDER);
                            else
                                newItem = presenter.addNurseryListFolder(name.getValue().toString(), (Integer) treeView.getValue());
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

                            DmsProject parent = (DmsProject) presenter.getStudyNodeParent(newItem);
                            boolean isRoot = parent == null || parent.getProjectId().intValue() == ROOT_FOLDER;
                            if (!isRoot) {
                                treeView.setParent(newItem, parent.getProjectId());
                            } else {
                                treeView.setParent(newItem, MY_STUDIES);
                            }

                            if (!isRoot) {
                                if (!treeView.isExpanded(parent.getProjectId()))
                                    expandTree(parent.getProjectId());
                            } else
                                treeView.expandItem(MY_STUDIES);

                            treeView.select(newItem);
                            treeView.setImmediate(true);
                            processToolbarButtons(newItem);
                        }

                        // close popup
                        IBPWorkbenchApplication.get().getMainWindow().removeWindow(event.getComponent().getWindow());
                    }
                });

                Button cancel = new Button("Cancel");
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

                LOG.info(treeView.getValue() != null ? treeView.getValue().toString() : null);

                if (treeView.getValue() instanceof String) {
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), messageSource.getMessage(Message.INVALID_CANNOT_DELETE_ITEM, treeView.getValue().toString()));
                    return;
                }

                Integer id;

                try {
                    id = presenter.validateForDeleteNurseryList((Integer) treeView.getValue());
                } catch (Error e) {
                    MessageNotifier.showError(event.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
                    return;
                }

                final Integer finalId = id;
                ConfirmDialog.show(event.getComponent().getWindow(),
                        messageSource.getMessage(Message.DELETE_LIST_FOLDER, treeView.getItemCaption(treeView.getValue())),
                        messageSource.getMessage(Message.DELETE_LIST_FOLDER_CONFIRM, treeView.getItemCaption(treeView.getValue())),
                        messageSource.getMessage(Message.YES), messageSource.getMessage(Message.NO), new ConfirmDialog.Listener() {
                    @Override
                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                DmsProject parent = (DmsProject) presenter.getStudyNodeParent(finalId);
                                presenter.deleteNurseryListFolder(finalId);
                                treeView.removeItem(treeView.getValue());
                                if (parent.getProjectId().intValue() == ROOT_FOLDER) {
                                    treeView.select(MY_STUDIES);
                                    processToolbarButtons(MY_STUDIES);
                                } else {
                                    treeView.select(parent.getProjectId());
                                    processToolbarButtons(parent.getProjectId());
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
                treeView.setItemIcon(sc.getId(), leafResource);
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

    public void setToolbarLaunchButtonEnabled(boolean enabled) {
        openStudyManagerBtn.setEnabled(enabled);
    }

    public void processToolbarButtons(Object treeItem) {

        boolean isSharedStudy = treeItem instanceof String && treeItem.equals(NurseryListPreview.SHARED_STUDIES);
        boolean isCentralStudy = treeItem instanceof Integer && ((Integer) treeItem).intValue() > 0;
        boolean isMyStudy = treeItem instanceof String && treeItem.equals(NurseryListPreview.MY_STUDIES);
        boolean isFolder = treeItem instanceof String || getPresenter().isFolder((Integer) treeItem);

        // set the toolbar button state
        if (isSharedStudy || isCentralStudy) {
            setToolbarButtonsEnabled(false);
        } else if (isMyStudy) {
            setToolbarButtonsEnabled(false);
            setToolbarAddButtonEnabled(true);
        } else if (!isFolder) {
            setToolbarButtonsEnabled(false);
            setToolbarAddButtonEnabled(true);
        } else {
            setToolbarButtonsEnabled(true);
        }

        // set the launch button state
        setToolbarLaunchButtonEnabled(!isSharedStudy && !isMyStudy && !isFolder);
    }

}
