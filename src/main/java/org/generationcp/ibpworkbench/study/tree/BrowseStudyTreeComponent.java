/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.study.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.commons.vaadin.util.SaveTreeStateListener;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.StudyBrowserMain;
import org.generationcp.ibpworkbench.study.StudyBrowserMainLayout;
import org.generationcp.ibpworkbench.study.StudyTabSheet;
import org.generationcp.ibpworkbench.util.Util;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class BrowseStudyTreeComponent extends VerticalLayout
		implements InitializingBean, InternationalizableComponent, GermplasmStudyBrowserLayout, StudyTypeChangeListener {

	private static final long serialVersionUID = -3481988646509402160L;

	private static final Logger LOG = LoggerFactory.getLogger(BrowseStudyTreeComponent.class);

	public static final String REFRESH_BUTTON_ID = "StudyTreeComponent Refresh Button";

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private VerticalLayout treeContainer;
	private StudyTree studyTree;
	private StudyTabSheet tabSheetStudy;

	private final StudyBrowserMain studyBrowserMain;
	private StudyBrowserMainLayout studyBrowserMainLayout;

	private Button refreshButton;

	private Integer rootNodeProjectId;
	private Map<Integer, Integer> parentChildItemIdMap;
	private StudyTreeButtonsPanel buttonsPanel;
	private StudyTypeFilterComponent studyTypeFilterComponent;
	private boolean isFirstTimeOpening;

	public BrowseStudyTreeComponent(final StudyBrowserMain studyBrowserMain) {
		this.studyBrowserMain = studyBrowserMain;
	}

	@Override
	public void afterPropertiesSet() {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	@Override
	public void instantiateComponents() {

		this.studyBrowserMainLayout = this.studyBrowserMain.getMainLayout();

		this.tabSheetStudy = new StudyTabSheet();
		this.studyTypeFilterComponent = new StudyTypeFilterComponent(this);

		this.studyTree = new StudyTree(this, this.getFilteredStudyType());
		this.buttonsPanel = new StudyTreeButtonsPanel(this);

		this.createRefreshButton();
	}

	protected void createRefreshButton() {
		this.refreshButton = new Button();
		this.refreshButton.setData(BrowseStudyTreeComponent.REFRESH_BUTTON_ID);
		this.refreshButton.addStyleName(Bootstrap.Buttons.INFO.styleName());
	}

	@Override
	public void initializeValues() {
		// do nothing
	}

	@Override
	public void addListeners() {
		this.refreshButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6234584415922997899L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				BrowseStudyTreeComponent.this.refreshTree();
			}
		});

	}

	public void refreshTree() {
		// Reset selected study type to "All"
		this.studyTypeFilterComponent.getStudyTypeComboBox().select(StudyTypeFilterComponent.ALL_OPTION);
		this.createTree();
		this.studyTree.expandSavedTreeState();
	}

	@Override
	public void layoutComponents() {
		this.setSpacing(true);

		this.treeContainer = new VerticalLayout();
		this.treeContainer.addComponent(this.studyTree);

		this.addComponent(this.buttonsPanel);
		this.addComponent(this.treeContainer);
		this.addComponent(this.refreshButton);
	}

	public void createTree() {
		this.treeContainer.removeComponent(this.studyTree);
		this.studyTree.removeAllItems();

		this.studyTree = new StudyTree(this, this.getFilteredStudyType());
		this.studyTree.setNullSelectionAllowed(false);
		this.buttonsPanel.setStudyTree(this.studyTree);

		this.treeContainer.addComponent(this.studyTree);
	}

	public Boolean studyExists(final int studyId) {
		try {
			final DmsProject study = this.studyDataManager.getProject(studyId);
			if (study == null) {
				return false;
			} else {
				if (!this.studyTree.hasChildStudy(studyId) && !this.studyTree.isFolder(studyId)) {
					return true;
				}
				return false;
			}
		} catch (final MiddlewareQueryException e) {
			BrowseStudyTreeComponent.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDY_DETAIL_BY_ID),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDY_DETAIL_BY_ID));
			return false;
		}
	}

	public void createStudyInfoTab(final int studyId) {
		final String studyName = this.getStudyName(studyId);
		if (!Util.isTabExist(this.tabSheetStudy, studyName)) {
			this.tabSheetStudy.createStudyInfoTab(studyId, studyName, this.studyBrowserMainLayout);
			this.studyBrowserMainLayout.addStudyInfoTabSheet(this.tabSheetStudy);
			this.studyBrowserMainLayout.showDetailsLayout();
		} else {
			final Tab tab = Util.getTabAlreadyExist(this.tabSheetStudy, studyName);
			this.tabSheetStudy.setSelectedTab(tab.getComponent());
		}
	}

	private String getStudyName(final int studyId) {
		try {
			final DmsProject studyDetails = this.studyDataManager.getProject(studyId);
			if (studyDetails != null) {
				return studyDetails.getName();
			} else {
				return null;
			}

		} catch (final MiddlewareQueryException e) {
			throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_IN_GETTING_STUDY_DETAIL_BY_ID);
		}
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setCaption(this.refreshButton, Message.REFRESH_LABEL);
	}

	public StudyTabSheet getTabSheetStudy() {
		return this.tabSheetStudy;
	}

	public void showChild(final Integer childItemId) {
		this.buildChildMap(childItemId, true);
		final Integer rootItemId = this.rootNodeProjectId;

		this.studyTree.expandItem(StudyTree.STUDY_ROOT_NODE);

		if (rootItemId != null) {
			this.studyTree.addChildren(rootItemId);
			this.studyTree.expandItem(rootItemId);
		}

		Integer currentItemId = this.parentChildItemIdMap.get(rootItemId);
		if (currentItemId != null) {
			this.studyTree.addChildren(currentItemId);
			this.studyTree.expandItem(currentItemId);
		}

		while (this.parentChildItemIdMap.get(currentItemId) != childItemId && currentItemId != null) {
			currentItemId = this.parentChildItemIdMap.get(currentItemId);
			if (currentItemId != null) {
				this.studyTree.addChildren(currentItemId);
				this.studyTree.expandItem(currentItemId);
			}
		}
		this.studyTree.selectItem(childItemId);

	}

	private void buildChildMap(final Integer studyId, final Boolean endNode) {
		if (endNode) {
			this.parentChildItemIdMap = new HashMap<Integer, Integer>();
		}
		try {
			final DmsProject studyParent = this.studyDataManager.getParentFolder(studyId);
			if (studyParent != null && (studyId < 0 && studyParent.getProjectId() != 1 || studyId > 0)) {
				final int parentProjectId = studyParent.getProjectId();
				this.parentChildItemIdMap.put(parentProjectId, studyId);
				this.buildChildMap(studyParent.getProjectId(), false);
			} else {
				this.rootNodeProjectId = studyId;
			}
		} catch (final MiddlewareQueryException e) {
			BrowseStudyTreeComponent.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
		}
	}

	public void updateButtons(final Object itemId) {
		this.buttonsPanel.updateButtons(itemId, this.isFirstTimeOpening);

	}

	/*
	 * Update the tab header and displayed study name with new name. This is called by rename function in study tree
	 */
	public void renameStudyTab(final String oldName, final String newName) {
		this.tabSheetStudy.renameStudyTab(oldName, newName);
	}

	public void openStudy(final Integer studyId) {
		if (this.studyExists(studyId)) {
			this.studyTree.studyTreeItemClickAction(studyId);
			this.showChild(studyId);
		} else {
			MessageNotifier.showError(this.getWindow(), this.messageSource.getMessage(Message.ERROR_INTERNAL),
					this.messageSource.getMessage(Message.NO_STUDIES_FOUND));
		}
	}

	@Override
	public void studyTypeChange(final StudyTypeDto type) {
		// Save the list of expanded nodes prior to recreating tree
		final List<String> expandedNodeIds = this.getSaveTreeStateListener().getExpandedIds();
		this.createTree();
		this.studyTree.expandNodes(expandedNodeIds);
	}

	protected StudyTypeDto getFilteredStudyType() {
		return (StudyTypeDto) this.studyTypeFilterComponent.getStudyTypeComboBox().getValue();
	}

	public StudyTree getStudyTree() {
		return this.studyTree;
	}

	public StudyBrowserMain getParentComponent() {
		return this.studyBrowserMain;
	}

	protected StudyBrowserMainLayout getStudyBrowserMainLayout() {
		return this.studyBrowserMainLayout;
	}

	protected void setStudyBrowserMainLayout(final StudyBrowserMainLayout studyBrowserMainLayout) {
		this.studyBrowserMainLayout = studyBrowserMainLayout;
	}

	protected StudyTypeFilterComponent getStudyTypeFilterComponent() {
		return this.studyTypeFilterComponent;
	}

	public SaveTreeStateListener getSaveTreeStateListener() {
		return this.studyTree.getSaveTreeStateListener();
	}

	protected StudyTreeButtonsPanel getButtonsPanel() {
		return this.buttonsPanel;
	}

	protected Button getRefreshButton() {
		return this.refreshButton;
	}

	protected void setStudyTypeFilterComponent(final StudyTypeFilterComponent studyTypeFilterComponent) {
		this.studyTypeFilterComponent = studyTypeFilterComponent;
	}

	protected void setStudyTree(final StudyTree studyTree) {
		this.studyTree = studyTree;
	}

	protected void setTreeContainer(final VerticalLayout treeContainer) {
		this.treeContainer = treeContainer;
	}

	protected void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	protected void setTabSheetStudy(final StudyTabSheet tabSheetStudy) {
		this.tabSheetStudy = tabSheetStudy;
	}

	protected void setButtonsPanel(final StudyTreeButtonsPanel buttonsPanel) {
		this.buttonsPanel = buttonsPanel;
	}

	public boolean isFirstTimeOpening() {
		return this.isFirstTimeOpening;
	}

	public void setFirstTimeOpening(final boolean firstTimeOpening) {
		this.isFirstTimeOpening = firstTimeOpening;
	}
}
