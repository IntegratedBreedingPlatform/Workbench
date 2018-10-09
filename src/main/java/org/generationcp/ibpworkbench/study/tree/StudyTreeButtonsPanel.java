
package org.generationcp.ibpworkbench.study.tree;

import javax.annotation.Resource;

import org.generationcp.commons.util.StudyPermissionValidator;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.StudyTabSheet;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class StudyTreeButtonsPanel extends HorizontalLayout implements InitializingBean, GermplasmStudyBrowserLayout {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;
	
	@Resource
	private StudyPermissionValidator studyPermissionValidator;

	private StudyTree studyTree;
	private final StudyTabSheet studyTabSheet;
	private final BrowseStudyTreeComponent browseTreeComponent;

	private HorizontalLayout controlButtonsSubLayout;
	private Button addFolderBtn;
	private Button deleteFolderBtn;
	private Button renameFolderBtn;

	public StudyTreeButtonsPanel(final BrowseStudyTreeComponent browseTreeComponent) {
		this.browseTreeComponent = browseTreeComponent;
		this.studyTree = browseTreeComponent.getStudyTree();
		this.studyTabSheet = browseTreeComponent.getTabSheetStudy();
	}

	@Override
	public void instantiateComponents() {
		this.renameFolderBtn =
				new Button("<span class='bms-edit' style='left: 2px; color: #0083c0;font-size: 18px; font-weight: bold;'></span>");
		this.renameFolderBtn.setHtmlContentAllowed(true);
		this.renameFolderBtn.setDescription(this.messageSource.getMessage(Message.RENAME_ITEM));
		this.renameFolderBtn.setStyleName(BaseTheme.BUTTON_LINK);
		this.renameFolderBtn.setWidth("25px");
		this.renameFolderBtn.setHeight("30px");
		this.renameFolderBtn.setEnabled(false);

		this.addFolderBtn =
				new Button("<span class='bms-add' style='left: 2px; color: #00a950;font-size: 18px; font-weight: bold;'></span>");
		this.addFolderBtn.setHtmlContentAllowed(true);
		this.addFolderBtn.setDescription(this.messageSource.getMessage(Message.ADD_NEW_FOLDER));
		this.addFolderBtn.setStyleName(BaseTheme.BUTTON_LINK);
		this.addFolderBtn.setWidth("25px");
		this.addFolderBtn.setHeight("30px");
		this.addFolderBtn.setEnabled(false);

		this.deleteFolderBtn =
				new Button("<span class='bms-delete' style='left: 2px; color: #f4a41c;font-size: 18px; font-weight: bold;'></span>");
		this.deleteFolderBtn.setHtmlContentAllowed(true);
		this.deleteFolderBtn.setDescription(this.messageSource.getMessage(Message.DELETE_ITEM));
		this.deleteFolderBtn.setStyleName(BaseTheme.BUTTON_LINK);
		this.deleteFolderBtn.setWidth("25px");
		this.deleteFolderBtn.setHeight("30px");
		this.deleteFolderBtn.setEnabled(false);
	}

	@Override
	public void initializeValues() {
		// Nothing to initialize
	}

	@Override
	public void addListeners() {
		this.renameFolderBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				final Object selectedStudyTreeNodeId = StudyTreeButtonsPanel.this.studyTree.getValue();
				final int itemId = Integer.valueOf(selectedStudyTreeNodeId.toString());
				final Boolean isFolder = StudyTreeButtonsPanel.this.studyTree.isFolder(itemId);
				
				if (isFolder || StudyTreeButtonsPanel.this.treeNodeCanBeRenamed(itemId)) {
					final String name = StudyTreeButtonsPanel.this.studyTree.getItemCaption(selectedStudyTreeNodeId);
					StudyTreeButtonsPanel.this.browseTreeComponent.getParentComponent().getWindow().addWindow(new StudyTreeRenameItemWindow(
							itemId, name, StudyTreeButtonsPanel.this.studyTree, StudyTreeButtonsPanel.this.studyTabSheet));
				}
			}
		});

		this.addFolderBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				final Object parentId = StudyTreeButtonsPanel.this.studyTree.getValue();
				StudyTreeButtonsPanel.this.browseTreeComponent.getParentComponent().getWindow()
						.addWindow(new StudyTreeAddFolderWindow(parentId, StudyTreeButtonsPanel.this.studyTree));
			}
		});

		this.deleteFolderBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				final int studyId = Integer.valueOf(StudyTreeButtonsPanel.this.studyTree.getValue().toString());
				final StudyTreeDeleteItemHandler deleteHandler = new StudyTreeDeleteItemHandler(StudyTreeButtonsPanel.this.studyTree,
						StudyTreeButtonsPanel.this, StudyTreeButtonsPanel.this.browseTreeComponent.getParentComponent().getWindow());
				deleteHandler.showConfirmDeletionDialog(studyId);
			}
		});

	}

	@Override
	public void layoutComponents() {
		this.controlButtonsSubLayout = new HorizontalLayout();
		this.controlButtonsSubLayout.addComponent(this.addFolderBtn);
		this.controlButtonsSubLayout.addComponent(this.renameFolderBtn);
		this.controlButtonsSubLayout.addComponent(this.deleteFolderBtn);
		this.controlButtonsSubLayout.setComponentAlignment(this.addFolderBtn, Alignment.BOTTOM_RIGHT);
		this.controlButtonsSubLayout.setComponentAlignment(this.renameFolderBtn, Alignment.BOTTOM_RIGHT);
		this.controlButtonsSubLayout.setComponentAlignment(this.deleteFolderBtn, Alignment.BOTTOM_RIGHT);

		this.setWidth("100%");
		this.setHeight("30px");
		this.setSpacing(true);

		final StudyTypeFilterComponent studyTypeComponent = this.browseTreeComponent.getStudyTypeFilterComponent();
		this.addComponent(studyTypeComponent);
		this.addComponent(this.controlButtonsSubLayout);
		this.setComponentAlignment(studyTypeComponent, Alignment.BOTTOM_LEFT);
		this.setComponentAlignment(this.controlButtonsSubLayout, Alignment.BOTTOM_RIGHT);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();

	}

	public void updateButtons(final Object itemId) {
		if (itemId instanceof String) {
			// this means its the ROOT Folder
			this.addFolderBtn.setEnabled(true);
			this.renameFolderBtn.setEnabled(false);
			this.deleteFolderBtn.setEnabled(false);

		} else if (this.studyTree.isFolder((Integer) itemId)) {
			this.addFolderBtn.setEnabled(true);
			this.renameFolderBtn.setEnabled(true);
			this.deleteFolderBtn.setEnabled(true);
			// The rest of the local lists
		} else {
			this.addFolderBtn.setEnabled(true);
			this.renameFolderBtn.setEnabled(true);
			this.deleteFolderBtn.setEnabled(false);
		}

	}
	
	Boolean treeNodeCanBeRenamed(final Integer studyId) {		
		final Window window = this.browseTreeComponent.getParentComponent().getWindow();
		final StudyReference studyReference = this.studyDataManager.getStudyReference(studyId);
		if (null == studyReference.getProgramUUID()) {
			MessageNotifier.showError(window, this.messageSource.getMessage(Message.ERROR), "Program templates cannot be renamed.");
			return false;
		} else if (this.studyPermissionValidator.userLacksPermissionForStudy(studyReference)) {
			MessageNotifier.showError(window, this.messageSource.getMessage(Message.ERROR),
					this.messageSource.getMessage(Message.LOCKED_STUDY_CANT_BE_MODIFIED, studyReference.getOwnerName()));
			return false;
		}
		return true;
	}

	protected Button getAddFolderBtn() {
		return this.addFolderBtn;
	}

	protected void setAddFolderBtn(final Button addFolderBtn) {
		this.addFolderBtn = addFolderBtn;
	}

	protected Button getDeleteFolderBtn() {
		return this.deleteFolderBtn;
	}

	protected void setDeleteFolderBtn(final Button deleteFolderBtn) {
		this.deleteFolderBtn = deleteFolderBtn;
	}

	protected Button getRenameFolderBtn() {
		return this.renameFolderBtn;
	}

	protected void setRenameFolderBtn(final Button renameFolderBtn) {
		this.renameFolderBtn = renameFolderBtn;
	}

	protected void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected HorizontalLayout getControlButtonsSubLayout() {
		return this.controlButtonsSubLayout;
	}

	protected void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	protected void setStudyTree(final StudyTree studyTree) {
		this.studyTree = studyTree;
	}

}
