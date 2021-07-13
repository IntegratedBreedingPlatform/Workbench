
package org.generationcp.ibpworkbench.study.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.commons.vaadin.util.SaveTreeStateListener;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.tree.listeners.StudyTreeCollapseListener;
import org.generationcp.ibpworkbench.study.tree.listeners.StudyTreeExpandListener;
import org.generationcp.ibpworkbench.study.tree.listeners.StudyTreeItemClickListener;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.study.StudyTypeDto;
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

	protected static final ThemeResource FOLDER_ICON = new ThemeResource("../vaadin-retro/svg/folder-icon.svg");
	protected static final ThemeResource STUDY_ICON = new ThemeResource("../vaadin-retro/svg/study-icon.svg");

	private StudyTreeDragAndDropHandler dropHandler;
	private final BrowseStudyTreeComponent browseStudyTreeComponent;
	private StudyTypeDto studyTypeFilter;
	private SaveTreeStateListener saveTreeStateListener;
	private StudyTreeExpandListener expandListener;
	private StudyTreeItemClickListener clickListener;

	public StudyTree(final BrowseStudyTreeComponent browseStudyTreeComponent, final StudyTypeDto filter) {
		this.browseStudyTreeComponent = browseStudyTreeComponent;
		this.studyTypeFilter = filter;
	}

	@Override
	public void instantiateComponents() {
		this.setDragMode(TreeDragMode.NODE);
		this.dropHandler = new StudyTreeDragAndDropHandler(this);
		this.saveTreeStateListener = new SaveTreeStateListener(this, ListTreeState.STUDY_LIST.name(), StudyTree.STUDY_ROOT_NODE);

		this.addItem(StudyTree.STUDY_ROOT_NODE);
		this.setItemCaption(StudyTree.STUDY_ROOT_NODE, this.messageSource.getMessage(Message.STUDIES));
		this.setItemIcon(StudyTree.STUDY_ROOT_NODE, this.getThemeResourceByReference(new FolderReference(null, null)));

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

	}

	@Override
	public void initializeValues() {
		this.populateRootNode();
	}

	@Override
	public void addListeners() {
		this.expandListener = new StudyTreeExpandListener(this);
		this.addListener(this.expandListener);
		this.clickListener = new StudyTreeItemClickListener(this, this.browseStudyTreeComponent);
		this.addListener(this.clickListener);
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
			final StudyTypeDto studyTypeDto = this.studyDataManager.getStudyTypeByLabel(this.studyTypeFilter.getLabel());
			rootFolders = this.studyDataManager.getRootFoldersByStudyType(programUUID, studyTypeDto == null ? null : studyTypeDto.getId());
		} catch (final MiddlewareQueryException e) {
			StudyTree.LOG.error(e.getMessage(), e);
			if (this.getWindow() != null) {
				MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
						this.messageSource.getMessage(Message.ERROR_IN_GETTING_TOP_LEVEL_STUDIES));
			}
		}

		for (final Reference item : rootFolders) {
			this.addItem(item.getId());
			this.setItemCaption(item.getId(), item.getName());

			if (!item.isFolder()) {
				this.setItemIcon(item.getId(), StudyTree.STUDY_ICON);
			} else {
				this.setItemIcon(item.getId(), this.getThemeResourceByReference(item));
			}

			this.setParent(item.getId(), StudyTree.STUDY_ROOT_NODE);
			if (!this.hasChildStudy(item.getId())) {
				this.setChildrenAllowed(item.getId(), false);
			}
		}
	}

	public ThemeResource getThemeResourceByReference(final Reference r) {

		if (r instanceof FolderReference) {
			StudyTree.LOG.debug("r is FolderReference");
			return StudyTree.FOLDER_ICON;
		} else if (r instanceof StudyReference) {
			StudyTree.LOG.debug("r is StudyReference");
			return StudyTree.STUDY_ICON;
		} else {
			return StudyTree.FOLDER_ICON;
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

	public void studyTreeItemClickAction(final Object itemId) {
		this.clickListener.studyTreeItemClickAction(itemId);
	}

	public void selectItem(final Object itemId) {
		this.browseStudyTreeComponent.updateButtons(itemId);
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

	// FIXME - Performance problem if such checking is done per tree node. The query that retrieves tree metadata should have all the
	// information already.
	// Can not get rid of it until Vaadin tree object is constructed with appropriate information already available from Middleware service.
	public Boolean isFolder(final Integer studyId) {
		try {
			final boolean isStudy = this.studyDataManager.isStudy(studyId);
			return !isStudy;
		} catch (final MiddlewareQueryException e) {
			StudyTree.LOG.error(e.getMessage(), e);
			return false;
		}
	}

	public void addChildren(final int parentStudyId) {
		this.expandListener.addChildren(parentStudyId, this.getWindow());
	}

	public void expandSavedTreeState() {
		try {
			final List<String> parsedState =
					this.programStateManager.getUserProgramTreeState(this.contextUtil.getCurrentWorkbenchUserId(),
							this.contextUtil.getCurrentProgramUUID(), ListTreeState.STUDY_LIST.name());

			this.expandNodes(parsedState);
		} catch (final MiddlewareQueryException e) {
			StudyTree.LOG.error("Error creating study tree", e);
		}
	}

	protected void expandNodes(final List<String> nodesToExpand) {
		if (nodesToExpand.isEmpty() || nodesToExpand.size() == 1 && StringUtils.isEmpty(nodesToExpand.get(0))) {
			this.collapseItem(StudyTree.STUDY_ROOT_NODE);
			return;
		}

		this.expandItem(StudyTree.STUDY_ROOT_NODE);
		for (final String s : nodesToExpand) {
			final String trimmed = s.trim();
			if (!StringUtils.isNumeric(trimmed)) {
				continue;
			}

			final int itemId = Integer.parseInt(trimmed);
			this.expandItem(itemId);
		}

		this.select(null);
	}

	protected SaveTreeStateListener getSaveTreeStateListener() {
		return this.saveTreeStateListener;
	}

	protected void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	protected void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	protected void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected void setExpandListener(final StudyTreeExpandListener expandListener) {
		this.expandListener = expandListener;
	}

	protected void setClickListener(final StudyTreeItemClickListener clickListener) {
		this.clickListener = clickListener;
	}

	protected void setStudyTypeFilter(final StudyTypeDto studyTypeFilter) {
		this.studyTypeFilter = studyTypeFilter;
	}

	protected void setProgramStateManager(final UserProgramStateDataManager programStateManager) {
		this.programStateManager = programStateManager;
	}

	public StudyTypeDto getStudyTypeFilter() {
		return this.studyTypeFilter;
	}

}
