
package org.generationcp.ibpworkbench.ui.breedingview;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.MultiSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.DatasetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;

@Configurable
public class SelectStudyDialog extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	public static final String OBJECTIVE = "Objective";
	public static final String TITLE = "Title";
	public static final String STUDY_NAME = "Study Name";

	private final class TreeTableItemClickListener implements ItemClickListener {

		private final TreeTable tr;
		private static final long serialVersionUID = 1L;

		private TreeTableItemClickListener(final TreeTable tr) {
			this.tr = tr;
		}

		@Override
		public void itemClick(final ItemClickEvent event) {

			final Reference r = (Reference) event.getItemId();
			final boolean isStudy = r.isStudy();

			if (event.isDoubleClick() && isStudy) {
				SelectStudyDialog.this.openStudy(r);
			} else {
				if (this.tr.isCollapsed(r)) {
					this.tr.setCollapsed(r, false);
				} else {
					this.tr.setCollapsed(r, true);
				}

				if (isStudy) {
					SelectStudyDialog.this.selectButton.setEnabled(true);
				} else {
					SelectStudyDialog.this.selectButton.setEnabled(false);
				}
			}

		}
	}

	private static final long serialVersionUID = -7651767452229107837L;

	private static final Logger LOG = LoggerFactory.getLogger(SelectStudyDialog.class);

	public static final String CLOSE_SCREEN_BUTTON_ID = "StudyInfoDialog Close Button ID";

	@Autowired
	protected SimpleResourceBundleMessageSource messageSource;

	@Autowired
	protected StudyDataManager studyDataManager;

	protected Window parentWindow;
	protected Button cancelButton;
	protected Button selectButton;
	protected BreedingViewTreeTable treeTable;
	protected VerticalLayout rootLayout;

	protected Component source;

	protected ThemeResource folderResource;
	protected ThemeResource studyResource;
	protected ThemeResource dataSetResource;

	protected Label lblStudyTreeDetailDescription;

	private final Project currentProject;

	public SelectStudyDialog(final Window parentWindow, final Component source, final Project currentProject) {
		this.parentWindow = parentWindow;
		this.source = source;
		this.currentProject = currentProject;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	protected void initializeComponents() {

		// set as modal window, other components are disabled while window is
		// open
		this.setModal(true);
		// define window size, set as not resizable
		this.setWidth("1100px");
		this.setHeight("650px");
		this.setResizable(false);
		this.setScrollable(false);
		this.setClosable(true);
		this.setStyleName(Reindeer.WINDOW_LIGHT);

		// center window within the browser
		this.center();

		this.lblStudyTreeDetailDescription = new Label();
		this.lblStudyTreeDetailDescription.setDebugId("lblStudyTreeDetailDescription");

		this.folderResource = new ThemeResource("../vaadin-retro/svg/folder-icon.svg");
		this.studyResource = new ThemeResource("../vaadin-retro/svg/study-icon.svg");
		this.dataSetResource = new ThemeResource("../vaadin-retro/svg/dataset-icon.svg");

		this.treeTable = this.createStudyTreeTable();
		this.treeTable.reinitializeTree();

		this.addListener(new SaveBreedingViewStudyTreeState(this.treeTable));
	}

	protected void initializeActions() {
		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final ClickEvent event) {
				SelectStudyDialog.this.parentWindow.removeWindow(SelectStudyDialog.this);

			}
		});

		this.selectButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final ClickEvent event) {

				if (SelectStudyDialog.this.treeTable.getValue() == null) {
					return;
				}
				final Reference studyRef = (Reference) SelectStudyDialog.this.treeTable.getValue();
				SelectStudyDialog.this.openStudy(studyRef);
			}
		});

	}

	protected Integer getPlotDataSetId(final Integer studyId) {
		try {
			return DatasetUtil.getPlotDataSetId(this.getStudyDataManager(), studyId);
		} catch (final MiddlewareException e) {
			SelectStudyDialog.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_VARIABLES_OF_DATASET));
		}
		return null;
	}

	protected void initializeLayout() {

		this.rootLayout = new VerticalLayout();
		this.rootLayout.setDebugId("rootLayout");
		this.rootLayout.setMargin(true);
		this.rootLayout.setSpacing(true);
		this.rootLayout.setWidth("100%");
		this.rootLayout.setHeight("100%");

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setDebugId("cancelButton");
		this.cancelButton.setData(SelectStudyDialog.CLOSE_SCREEN_BUTTON_ID);

		this.selectButton = new Button("Select");
		this.selectButton.setDebugId("selectButton");
		this.selectButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.selectButton.setEnabled(false);

		this.rootLayout.addComponent(this.lblStudyTreeDetailDescription);
		this.rootLayout.addComponent(this.treeTable);

		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.selectButton);

		this.rootLayout.addComponent(buttonLayout);
		this.rootLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);

		this.addComponent(this.rootLayout);

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected BreedingViewTreeTable createStudyTreeTable() {

		final BreedingViewTreeTable tr = new BreedingViewTreeTable();
		tr.setDebugId("tr");

		tr.addContainerProperty(SelectStudyDialog.STUDY_NAME, String.class, "sname");
		tr.addContainerProperty(SelectStudyDialog.TITLE, String.class, "title");
		tr.addContainerProperty(SelectStudyDialog.OBJECTIVE, String.class, "description");

		List<Reference> folderRef = null;

		try {
			folderRef = this.getStudyDataManager().getRootFolders(this.currentProject.getUniqueID(),
					StudyType.trials());
		} catch (final MiddlewareQueryException e1) {
			SelectStudyDialog.LOG.error(e1.getMessage(), e1);
			if (this.getWindow() != null) {
				MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
						this.messageSource.getMessage(Message.ERROR_IN_GETTING_TOP_LEVEL_STUDIES));
			}
		}

		for (final Reference fr : folderRef) {
			Study study = null;
			try {
				if (fr.isStudy()) {
					study = this.getStudyDataManager().getStudy(fr.getId());
				}
			} catch (final MiddlewareException e) {
				SelectStudyDialog.LOG.error(e.getMessage(), e);
			}

			final Object[] cells = new Object[3];
			cells[0] = " " + fr.getName();
			cells[1] = study != null ? study.getDescription() : "";
			cells[2] = study != null ? study.getObjective() : "";

			final Object itemId = tr.addFolderReferenceNode(cells, fr);

			if (fr.isStudy()) {
				tr.setItemIcon(itemId, this.studyResource);
				tr.setChildrenAllowed(itemId, false);
			} else {
				tr.setItemIcon(itemId, this.folderResource);
			}

		}

		// reserve excess space for the "treecolumn"
		tr.setSizeFull();
		tr.setColumnExpandRatio(SelectStudyDialog.STUDY_NAME, 1);
		tr.setColumnExpandRatio(SelectStudyDialog.TITLE, 1);
		tr.setColumnExpandRatio(SelectStudyDialog.OBJECTIVE, 1);
		tr.setSelectable(true);

		tr.addListener(new StudyTreeExpandAction(this, tr));
		tr.addListener(new TreeTableItemClickListener(tr));
		return tr;
	}

	protected void openStudy(final Reference r) {
		if (this.source instanceof SingleSiteAnalysisPanel) {
			final Integer dataSetId = this.getPlotDataSetId(r.getId());
			if (dataSetId != null) {
				((SingleSiteAnalysisPanel) this.source).showDatasetVariatesDetails(dataSetId);
				this.parentWindow.removeWindow(SelectStudyDialog.this);
			}
		} else if (this.source instanceof MultiSiteAnalysisPanel) {
			Study study = null;
			try {
				study = this.getStudyDataManager().getStudy(r.getId());
				((MultiSiteAnalysisPanel) this.source).openStudyMeansDataset(study);
				this.parentWindow.removeWindow(SelectStudyDialog.this);
			} catch (final MiddlewareException e) {
				SelectStudyDialog.LOG.error(e.getMessage(), e);
				if (study != null) {
					MessageNotifier.showError(this, "MEANS dataset doesn't exist",
							study.getName() + " doesn't have an existing MEANS dataset.");
				} else {
					MessageNotifier.showError(this, "MEANS dataset doesn't exist",
							"The selected Study doesn't have an existing MEANS dataset.");
				}
			}
		}
	}

	public void queryChildrenStudies(final Reference parentFolderReference, final BreedingViewTreeTable tr) {

		List<Reference> childrenReference = new ArrayList<>();

		try {

			childrenReference = this.getStudyDataManager().getChildrenOfFolder(parentFolderReference.getId(),
					this.currentProject.getUniqueID(), StudyType.trials());

		} catch (final MiddlewareQueryException e) {
			SelectStudyDialog.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
		}

		for (final java.util.Iterator<Reference> i = childrenReference.iterator(); i.hasNext();) {

			final Reference r = i.next();

			final Object[] cells = new Object[3];

			Study s = null;
			try {
				s = this.getStudyDataManager().getStudy(r.getId());
			} catch (final MiddlewareException e) {
				SelectStudyDialog.LOG.error(e.getMessage(), e);
			}

			cells[0] = " " + r.getName();
			cells[1] = s != null ? s.getDescription() : "";
			cells[2] = s != null ? s.getObjective() : "";

			if (r.isFolder()) {
				tr.addFolderReferenceNode(cells, r);
			} else {
				tr.addItem(cells, r);
			}

			tr.setParent(r, parentFolderReference);
			if (this.hasChildStudy(r.getId())) {
				tr.setChildrenAllowed(r, true);
				tr.setItemIcon(r, this.getThemeResourceByReference(r));
			} else {
				tr.setChildrenAllowed(r, false);
				tr.setItemIcon(r, this.getThemeResourceByReference(r));
			}
		}

	}

	private ThemeResource getThemeResourceByReference(final Reference r) {

		if (r instanceof FolderReference) {
			SelectStudyDialog.LOG.debug("r is FolderReference");
			return this.folderResource;
		} else if (r instanceof StudyReference) {
			SelectStudyDialog.LOG.debug("r is StudyReference");
			return this.studyResource;
		} else if (r instanceof DatasetReference) {
			SelectStudyDialog.LOG.debug("r is DatasetReference");
			return this.dataSetResource;
		} else {
			return this.folderResource;
		}

	}

	public void queryChildrenDatasets(final Reference parentFolderReference, final TreeTable tr) {

		List<DatasetReference> childrenReference = new ArrayList<>();

		try {

			childrenReference = this.getStudyDataManager().getDatasetReferences(parentFolderReference.getId());

		} catch (final MiddlewareQueryException e) {
			SelectStudyDialog.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
		}

		for (final java.util.Iterator<DatasetReference> i = childrenReference.iterator(); i.hasNext();) {

			final Reference r = i.next();

			final Object[] cells = new Object[3];

			cells[0] = " " + r.getName();
			cells[1] = "";
			cells[2] = "";

			if (r instanceof DatasetReference) {
				SelectStudyDialog.LOG.debug("r is DatasetReference");
			}

			tr.addItem(cells, r);
			tr.setParent(r, parentFolderReference);
			tr.setChildrenAllowed(r, false);
			tr.setItemIcon(r, this.getThemeResourceByReference(r));

		}

	}

	protected boolean hasChildStudy(final int folderId) {

		List<Reference> children;

		try {
			children = this.getStudyDataManager().getChildrenOfFolder(folderId, this.currentProject.getUniqueID(),
					StudyType.trials());
		} catch (final MiddlewareQueryException e) {
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
			children = new ArrayList<>();

			SelectStudyDialog.LOG.warn(e.getMessage(), e);
		}
		return !children.isEmpty();
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		if (this.source instanceof SingleSiteAnalysisPanel) {
			this.messageSource.setCaption(this, Message.BV_STUDY_TREE_TITLE);
			this.messageSource.setValue(this.lblStudyTreeDetailDescription, Message.BV_STUDY_TREE_DESCRIPTION);
		} else if (this.source instanceof MultiSiteAnalysisPanel) {
			this.messageSource.setCaption(this, Message.GXE_SELECT_DATA_FOR_ANALYSIS_HEADER);
			this.messageSource.setCaption(this.lblStudyTreeDetailDescription,
					Message.GXE_SELECT_DATA_FOR_ANALYSIS_DESCRIPTION);
		}
	}

	private StudyDataManager getStudyDataManager() {
		return this.studyDataManager;
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}
}
