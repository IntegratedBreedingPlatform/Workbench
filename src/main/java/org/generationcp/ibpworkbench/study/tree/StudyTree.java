package org.generationcp.ibpworkbench.study.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.listeners.StudyItemClickListener;
import org.generationcp.ibpworkbench.study.listeners.StudyTreeCollapseListener;
import org.generationcp.ibpworkbench.study.listeners.StudyTreeExpandListener;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;

@Configurable
public class StudyTree extends Tree implements InitializingBean, GermplasmStudyBrowserLayout {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StudyTree.class);
	public static final String STUDY_ROOT_NODE = "STUDY_ROOT_NODE";

	@Autowired
	private StudyDataManager studyDataManager;
	
	@Autowired
	private ContextUtil contextUtil;
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private UserProgramStateDataManager programStateManager;
	
	private final ThemeResource folderResource = new ThemeResource("../vaadin-retro/svg/folder-icon.svg");
	private final ThemeResource studyResource = new ThemeResource("../vaadin-retro/svg/study-icon.svg");
	private final ThemeResource dataSetResource = new ThemeResource("../vaadin-retro/svg/dataset-icon.svg");

	private StudyTreeDragAndDropHandler dropHandler;
	private BrowseStudyTreeComponent browseStudyTreeComponent;
	private Object selectedStudyNodeId;
	
	public StudyTree(final BrowseStudyTreeComponent browseStudyTreeComponent) {
		this.browseStudyTreeComponent = browseStudyTreeComponent;
	}
	
	@Override
	public void instantiateComponents() {
		this.setDragMode(TreeDragMode.NODE);

		this.addItem(STUDY_ROOT_NODE);
		this.setItemCaption(STUDY_ROOT_NODE, this.messageSource.getMessage(Message.STUDIES));
		this.setItemIcon(STUDY_ROOT_NODE, this.getThemeResourceByReference(new FolderReference(null, null)));

		this.addStyleName("studyBrowserTree");
		this.setImmediate(true);
		this.setWidth("98%");
		
		// add tooltip
		this.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {

			private static final long serialVersionUID = -2669417630841097077L;

			@Override
			public String generateDescription(final Component source, final Object itemId, final Object propertyId) {
				return StudyTree.this.messageSource.getMessage(Message.STUDY_DETAILS_LABEL);
			}
		});
		
		this.dropHandler = new StudyTreeDragAndDropHandler(this);
	}

	@Override
	public void initializeValues() {
		this.populateRootNode();
	}

	@Override
	public void addListeners() {
		this.addListener(new StudyTreeExpandListener(this));
		this.addListener(new StudyItemClickListener(this));
		this.addListener(new StudyTreeCollapseListener(this));
		this.dropHandler.setupTreeDragAndDropHandler();
	}

	@Override
	public void layoutComponents() {
		// No layouting needed
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}
	
	public void populateRootNode() {
		List<Reference> rootFolders = new ArrayList<Reference>();
		try {
			final String programUUID = this.contextUtil.getProjectInContext().getUniqueID();
			rootFolders = this.studyDataManager.getRootFolders(programUUID);
		} catch (final MiddlewareQueryException e) {
			StudyTree.LOG.error(e.getMessage(), e);
			if (this.getWindow() != null) {
				MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
						this.messageSource.getMessage(Message.ERROR_IN_GETTING_TOP_LEVEL_STUDIES));
			}
		}

		for (final Reference ps : rootFolders) {
			this.addItem(ps.getId());
			this.setItemCaption(ps.getId(), ps.getName());

			if (!ps.isFolder()) {
				this.setItemIcon(ps.getId(), this.studyResource);
			} else {
				this.setItemIcon(ps.getId(), this.getThemeResourceByReference(ps));
			}

			this.setParent(ps.getId(), STUDY_ROOT_NODE);
			if (!this.hasChildStudy(ps.getId())) {
				this.setChildrenAllowed(ps.getId(), false);
			}
		}
	}
	
	private ThemeResource getThemeResourceByReference(final Reference r) {

		if (r instanceof FolderReference) {
			StudyTree.LOG.debug("r is FolderReference");
			return this.folderResource;
		} else if (r instanceof StudyReference) {
			StudyTree.LOG.debug("r is StudyReference");
			return this.studyResource;
		} else if (r instanceof DatasetReference) {
			StudyTree.LOG.debug("r is DatasetReference");
			return this.dataSetResource;
		} else {
			return this.folderResource;
		}

	}
	
	public boolean hasChildStudy(final int studyId) {

		List<Reference> studyChildren = new ArrayList<Reference>();

		try {
			final String programUUID = this.contextUtil.getProjectInContext().getUniqueID();
			studyChildren = this.studyDataManager.getChildrenOfFolder(new Integer(studyId), programUUID);
		} catch (final MiddlewareQueryException e) {
			StudyTree.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
			studyChildren = new ArrayList<>();
		}
		if (!studyChildren.isEmpty()) {
			return true;
		}
		return false;
	}
	
	// Called by StudyItemClickListener
	public void studyTreeItemClickAction(final Object itemId) {

		try {
			this.expandOrCollapseStudyTreeNode(itemId);
			final int studyId = Integer.valueOf(itemId.toString());

			if (!this.hasChildStudy(studyId) && !this.isFolder(studyId)) {
				this.browseStudyTreeComponent.createStudyInfoTab(studyId);
			}
			this.selectStudy(itemId);
		} catch (final NumberFormatException e) {
			StudyTree.LOG.error(e.getMessage(), e);
		} 
	}

	public void selectStudy(final Object itemId) {
		this.browseStudyTreeComponent.updateButtons(itemId);
		this.setSelectedNodeId(itemId);
		this.setNullSelectionAllowed(false);
		this.select(itemId);
		this.setValue(itemId);
	}
	
	public void expandOrCollapseStudyTreeNode(final Object itemId) {
		if (!this.isExpanded(itemId)) {
			this.expandItem(itemId);
		} else {
			this.collapseItem(itemId);
		}
	}
	
	// FIXME - Performance problem if such checking is done per tree node. The query that retrieves tree metadata should have all the information already.
	// Can not get rid of it until Vaadin tree object is constructed with appropriate information already available from Middleware service.
	public Boolean isFolder(final Integer studyId) {
		try {
			final boolean isStudy = this.studyDataManager.isStudy(studyId);
			return !isStudy;
		} catch (final MiddlewareQueryException e) {
			LOG.error(e.getMessage());
			return false;
		}
	}
	
	public void addStudyNode(final int parentStudyId) {

		List<Reference> studyChildren = new ArrayList<Reference>();
		try {
			final String programUUID = this.contextUtil.getProjectInContext().getUniqueID();
			studyChildren =
					this.studyDataManager.getChildrenOfFolder(Integer.valueOf(parentStudyId), programUUID);
		} catch (final MiddlewareQueryException e) {
			StudyTree.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
			studyChildren = new ArrayList<Reference>();
		}

		for (final Reference sc : studyChildren) {
			this.addItem(sc.getId());
			this.setItemCaption(sc.getId(), sc.getName());
			this.setParent(sc.getId(), parentStudyId);

			// check if the study has sub study
			if (this.hasChildStudy(sc.getId())) {
				this.setChildrenAllowed(sc.getId(), true);
				this.setItemIcon(sc.getId(), this.getThemeResourceByReference(sc));
			} else {
				this.setChildrenAllowed(sc.getId(), false);
				this.setItemIcon(sc.getId(), this.getThemeResourceByReference(sc));
			}

		}
	}
	
	public void reinitializeTree() {
		try {
			final List<String> parsedState =
					programStateManager.getUserProgramTreeStateByUserIdProgramUuidAndType(contextUtil.getCurrentWorkbenchUserId(),
							contextUtil.getCurrentProgramUUID(), ListTreeState.STUDY_LIST.name());

			if (parsedState.isEmpty() || (parsedState.size() == 1 && StringUtils.isEmpty(parsedState.get(0)))) {
				this.collapseItem(STUDY_ROOT_NODE);
				return;
			}

			this.expandItem(STUDY_ROOT_NODE);
			for (final String s : parsedState) {
				final String trimmed = s.trim();
				if (!StringUtils.isNumeric(trimmed)) {
					continue;
				}

				final int itemId = Integer.parseInt(trimmed);
				this.expandItem(itemId);
			}

			this.select(null);
		} catch (final MiddlewareQueryException e) {
			LOG.error("Error creating study tree");
		}
	}
	
	public void setSelectedNodeId(final Object id) {
		this.selectedStudyNodeId = id;
	}

	
	protected Object getSelectedNodeId() {
		return selectedStudyNodeId;
	}

}
