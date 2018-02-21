
package org.generationcp.ibpworkbench.ui.breedingview.metaanalysis;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.BreedingViewTreeTable;
import org.generationcp.ibpworkbench.ui.breedingview.SaveBreedingViewStudyTreeState;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class SelectDatasetDialog extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = -7651767452229107837L;

	private static final Logger LOG = LoggerFactory.getLogger(SelectDatasetDialog.class);

	public static final String CLOSE_SCREEN_BUTTON_ID = "StudyInfoDialog Close Button ID";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private final Window parentWindow;
	private Button cancelButton;
	private Button selectButton;
	private BreedingViewTreeTable treeTable;
	private VerticalLayout rootLayout;

	@Autowired
	private StudyDataManager studyDataManager;
	private final MetaAnalysisPanel metaAnalysisPanel;

	private ThemeResource folderResource;
	private ThemeResource studyResource;
	private ThemeResource dataSetResource;

	private Label lblStudyTreeDetailDescription;

	private final Project currentProject;

	public SelectDatasetDialog(Window parentWindow, MetaAnalysisPanel metaAnalysisPanel, Project currentProject) {

		this.parentWindow = parentWindow;
		this.metaAnalysisPanel = metaAnalysisPanel;
		this.currentProject = currentProject;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	protected void initializeComponents() {

		// set as modal window, other components are disabled while window is open
		this.setModal(true);
		// define window size, set as not resizable
		this.setWidth("1100px");
		this.setHeight("650px");
		this.setResizable(false);
		this.setClosable(true);
		this.setScrollable(false);
		this.setStyleName(Reindeer.WINDOW_LIGHT);
		// center window within the browser
		this.center();

		this.lblStudyTreeDetailDescription = new Label(this.messageSource.getMessage(Message.META_SELECT_DATA_FOR_ANALYSIS_DESCRIPTION));
		this.lblStudyTreeDetailDescription.setDebugId("lblStudyTreeDetailDescription");

		this.folderResource = new ThemeResource("../vaadin-retro/svg/folder-icon.svg");
		this.studyResource = new ThemeResource("../vaadin-retro/svg/study-icon.svg");
		this.dataSetResource = new ThemeResource("../vaadin-retro/svg/dataset-icon.svg");

		this.treeTable = this.createStudyTreeTable();
		treeTable.reinitializeTree();

		addListener(new SaveBreedingViewStudyTreeState(treeTable));
	}

	protected void initializeActions() {
		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				SelectDatasetDialog.this.parentWindow.removeWindow(SelectDatasetDialog.this);

			}
		});

		this.selectButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (SelectDatasetDialog.this.treeTable.getValue() == null) {
					return;
				}
				DatasetReference datasetRef = (DatasetReference) SelectDatasetDialog.this.treeTable.getValue();
				int dataSetId = datasetRef.getId();
				SelectDatasetDialog.this.metaAnalysisPanel.generateTab(dataSetId);
				SelectDatasetDialog.this.parentWindow.removeWindow(SelectDatasetDialog.this);

			}
		});

	}

	protected void initializeLayout() {

		this.rootLayout = new VerticalLayout();
		this.rootLayout.setDebugId("rootLayout");
		this.rootLayout.setMargin(true);
		this.rootLayout.setSpacing(true);
		this.rootLayout.setWidth("100%");
		this.rootLayout.setHeight("100%");

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setDebugId("cancelButton");
		this.cancelButton.setData(SelectDatasetDialog.CLOSE_SCREEN_BUTTON_ID);

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

	private BreedingViewTreeTable createStudyTreeTable() {

		final BreedingViewTreeTable tr = new BreedingViewTreeTable();
		tr.setDebugId("tr");

		tr.addContainerProperty("Study Name", String.class, "sname");
		tr.addContainerProperty("Title", String.class, "title");
		tr.addContainerProperty("Objective", String.class, "objective");

		List<Reference> folderRef = null;

		try {
			folderRef = studyDataManager.getRootFolders(this.currentProject.getUniqueID());
		} catch (MiddlewareQueryException e1) {
			SelectDatasetDialog.LOG.error(e1.getMessage(), e1);
			if (this.getWindow() != null) {
				MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
						this.messageSource.getMessage(Message.ERROR_IN_GETTING_TOP_LEVEL_STUDIES));
			}
		}

		for (Reference fr : folderRef) {

			Study study = null;
			Boolean isStudy = false;
			isStudy = fr.isStudy();
			if (isStudy) {
				study = studyDataManager.getStudy(fr.getId());
			}
			Object[] cells = new Object[3];
			cells[0] = " " + fr.getName();
			cells[1] = study != null ? study.getDescription() : "";
			cells[2] = study != null ? study.getObjective() : "";

			Object itemId = tr.addFolderReferenceNode(cells, fr);
			if (isStudy) {
				tr.setItemIcon(itemId, this.studyResource);
			} else {
				tr.setItemIcon(itemId, this.folderResource);
			}
		}

		// reserve excess space for the "treecolumn"
		tr.setSizeFull();
		tr.setColumnExpandRatio("Study Name", 1);
		tr.setColumnExpandRatio("Title", 1);
		tr.setColumnExpandRatio("Objective", 1);
		tr.setSelectable(true);

		tr.addListener(new StudyTreeExpandAction(this, tr));
		tr.addListener(new ItemClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {
				Object itemId = event.getItemId();
				if (event.isDoubleClick() && itemId instanceof DatasetReference) {
					SelectDatasetDialog.this.metaAnalysisPanel.generateTab(((DatasetReference) itemId).getId());
					SelectDatasetDialog.this.parentWindow.removeWindow(SelectDatasetDialog.this);
				} else {
					if (tr.isCollapsed(itemId)) {
						tr.setCollapsed(itemId, false);
					} else {
						tr.setCollapsed(itemId, true);
					}
					SelectDatasetDialog.this.selectButton.setEnabled(event.getItemId() instanceof DatasetReference);
				}
			}
		});
		return tr;
	}

	public void queryChildrenStudies(Reference parentFolderReference, BreedingViewTreeTable tr) {

		List<Reference> childrenReference = new ArrayList<>();

		try {

			childrenReference =
					studyDataManager.getChildrenOfFolder(parentFolderReference.getId(), this.currentProject.getUniqueID());

		} catch (MiddlewareQueryException e) {
			SelectDatasetDialog.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
		}

		for (java.util.Iterator<Reference> i = childrenReference.iterator(); i.hasNext();) {

			Reference r = i.next();

			Object[] cells = new Object[3];

			Study s = null;
			try {
				s = studyDataManager.getStudy(r.getId());
			} catch (MiddlewareQueryException e) {

				SelectDatasetDialog.LOG.error(e.getMessage(), e);
			}

			cells[0] = " " + r.getName();
			cells[1] = s != null ? s.getDescription() : "";
			cells[2] = s != null ? s.getObjective() : "";

			if (r.isFolder()) {
				tr.addFolderReferenceNode(cells, (FolderReference) r);
			} else {
				tr.addItem(cells, r);
			}

			tr.setParent(r, parentFolderReference);
			if (this.hasChildStudy(r.getId()) || this.hasChildDataset(r.getId())) {
				tr.setChildrenAllowed(r, true);
				tr.setItemIcon(r, this.getThemeResourceByReference(r));
			} else {
				tr.setChildrenAllowed(r, false);
				tr.setItemIcon(r, this.getThemeResourceByReference(r));
			}
		}

	}

	private ThemeResource getThemeResourceByReference(Reference r) {

		if (r instanceof FolderReference) {
			SelectDatasetDialog.LOG.debug("r is FolderReference");
			return this.folderResource;
		} else if (r instanceof StudyReference) {
			SelectDatasetDialog.LOG.debug("r is StudyReference");
			return this.studyResource;
		} else if (r instanceof DatasetReference) {
			SelectDatasetDialog.LOG.debug("r is DatasetReference");
			return this.dataSetResource;
		} else {
			return this.folderResource;
		}

	}

	public void queryChildrenDatasets(Reference parentFolderReference, TreeTable tr) {

		List<DatasetReference> childrenReference = new ArrayList<>();

		try {

			childrenReference = studyDataManager.getDatasetReferences(parentFolderReference.getId());

		} catch (MiddlewareQueryException e) {
			SelectDatasetDialog.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
		}

		for (java.util.Iterator<DatasetReference> i = childrenReference.iterator(); i.hasNext();) {

			Reference r = i.next();

			Object[] cells = new Object[3];

			cells[0] = " " + r.getName();
			cells[1] = "";
			cells[2] = "";

			if (r instanceof DatasetReference) {
				SelectDatasetDialog.LOG.debug("r is DatasetReference");
			}

			tr.addItem(cells, r);
			tr.setParent(r, parentFolderReference);
			tr.setChildrenAllowed(r, false);
			tr.setItemIcon(r, this.getThemeResourceByReference(r));

		}

	}

	private boolean hasChildStudy(int folderId) {

		List<Reference> children = new ArrayList<>();

		try {
			children = studyDataManager.getChildrenOfFolder(folderId, this.currentProject.getUniqueID());
		} catch (MiddlewareQueryException e) {
			SelectDatasetDialog.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
			children = new ArrayList<>();
		}
		return !children.isEmpty();
	}

	private boolean hasChildDataset(int folderId) {

		List<DatasetReference> children = new ArrayList<>();

		try {
			children = studyDataManager.getDatasetReferences(folderId);
		} catch (MiddlewareQueryException e) {
			SelectDatasetDialog.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
			children = new ArrayList<>();
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

		this.messageSource.setCaption(this, Message.BV_STUDY_TREE_TITLE);

	}

}
