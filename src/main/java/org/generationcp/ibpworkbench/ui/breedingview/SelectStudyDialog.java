package org.generationcp.ibpworkbench.ui.breedingview;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.themes.Reindeer;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.StudyTreeExpandAction;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.MultiSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.util.DatasetUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@Configurable
public class SelectStudyDialog extends Window implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = -7651767452229107837L;

	private final static Logger LOG = LoggerFactory.getLogger(SelectStudyDialog.class);

	public static final String CLOSE_SCREEN_BUTTON_ID = "StudyInfoDialog Close Button ID";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;


	private Window parentWindow;
	private Button cancelButton;
	private Button selectButton;
	private TreeTable treeTable;
	private VerticalLayout rootLayout;


	private StudyDataManagerImpl studyDataManager;
	private Component source;

	private ThemeResource folderResource;
	private ThemeResource studyResource;
	private ThemeResource dataSetResource;

	private Label lblStudyTreeDetailDescription;

	public SelectStudyDialog(Window parentWindow, Component source, StudyDataManagerImpl studyDataManager){
		this.parentWindow = parentWindow;
		this.studyDataManager = studyDataManager;
		this.source = source;
	}

	protected void assemble() {
		initializeComponents();
		initializeLayout();
		initializeActions();
	}

	protected void initializeComponents(){

		//set as modal window, other components are disabled while window is open
		this.setModal(true);
		// define window size, set as not resizable
		this.setWidth("1100px");
		this.setHeight("650px");
		this.setResizable(false);
		this.setScrollable(false);
		this.setClosable(true);
		this.setStyleName(Reindeer.WINDOW_LIGHT);
		//setCaption("Study Information");
		// center window within the browser
		center();

		lblStudyTreeDetailDescription = new Label();
		
		folderResource = new ThemeResource("../vaadin-retro/svg/folder-icon.svg");
		studyResource = new ThemeResource("../vaadin-retro/svg/study-icon.svg");
		dataSetResource = new ThemeResource("../vaadin-retro/svg/dataset-icon.svg");
		
		treeTable = createStudyTreeTable(Database.LOCAL);
	}

	protected void initializeActions(){
		cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				parentWindow.removeWindow(SelectStudyDialog.this);

			}
		});

		selectButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if (treeTable.getValue() == null) return;
				Reference studyRef = (Reference) treeTable.getValue();
				openStudy(studyRef);
			}
		});
		

	}

	protected Integer getPlotDataSetId(Integer studyId) {
		try {
			return DatasetUtil.getPlotDataSetId(getStudyDataManager(),studyId);
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
			MessageNotifier
			.showWarning(
					getWindow(),
					messageSource.getMessage(Message.ERROR_DATABASE),
					messageSource
					.getMessage(Message.ERROR_IN_GETTING_VARIABLES_OF_DATASET));
		}
		return null;
	}

	protected void initializeLayout(){

		rootLayout = new VerticalLayout();
		rootLayout.setMargin(true);
		rootLayout.setSpacing(true);
		rootLayout.setWidth("100%");
		rootLayout.setHeight("100%");

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		cancelButton = new Button("Cancel");
		cancelButton.setData(CLOSE_SCREEN_BUTTON_ID);

		selectButton = new Button("Select");
		selectButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		selectButton.setEnabled(false);

		rootLayout.addComponent(lblStudyTreeDetailDescription);
		rootLayout.addComponent(treeTable);

		buttonLayout.addComponent(cancelButton);
		buttonLayout.addComponent(selectButton);

		rootLayout.addComponent(buttonLayout);
		rootLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);

		addComponent(rootLayout);

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assemble();
	}

	private TreeTable createStudyTreeTable(Database database) {

		final TreeTable tr = new TreeTable();

		tr.addContainerProperty("Study Name", String.class, "sname");
		tr.addContainerProperty("Title", String.class, "title");
		tr.addContainerProperty("Objective", String.class, "description");

		List<FolderReference> folderRef = null;

		try {
			folderRef = getStudyDataManager().getRootFolders(database);
		} catch (MiddlewareQueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			if (getWindow() != null) {
				MessageNotifier
				.showWarning(
						getWindow(),
						messageSource
						.getMessage(Message.ERROR_DATABASE),
						messageSource
						.getMessage(Message.ERROR_IN_GETTING_TOP_LEVEL_STUDIES));
			}
		}

		for (FolderReference fr : folderRef) {

			Study study = null;
			try {
				if (isStudy(fr)){
					study = getStudyDataManager().getStudy(fr.getId());
				}
			} catch (MiddlewareQueryException e) {
				e.printStackTrace();
			}

			Object[] cells = new Object[3];
			cells[0] = " " + fr.getName();
			cells[1] = (study != null) ? study.getTitle() : "";
			cells[2] = (study != null) ? study.getObjective() : "";

			Object itemId = tr.addItem(cells, fr);
			
			if (!isFolder(fr.getId())){
				tr.setItemIcon(itemId, studyResource);
				tr.setChildrenAllowed(itemId, false);
			}else{
				tr.setItemIcon(itemId, folderResource);
			}

		}

		// reserve excess space for the "treecolumn"
		tr.setSizeFull();
		tr.setColumnExpandRatio("Study Name", 1);
		tr.setColumnExpandRatio("Title", 1);
		tr.setColumnExpandRatio("Objective", 1);
		tr.setSelectable(true);

		tr.addListener(new StudyTreeExpandAction(this, tr));
		tr.addListener(new ItemClickListener(){

			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {
				
				Reference r = (Reference) event.getItemId();
				boolean isStudy = isStudy(r);
				
				if (event.isDoubleClick() && isStudy){
					openStudy(r);
				}else{
					if (tr.isCollapsed(r)){
						tr.setCollapsed(r, false);
					}else{
						tr.setCollapsed(r, true);
					}
				
					if (isStudy){
						selectButton.setEnabled(true);
					}else{
						selectButton.setEnabled(false);
					}
				}
				
			}
			
		});
		return tr;
	}
	
	protected boolean isStudy(Reference r) {
		if(r instanceof StudyReference) {
			return true;
		}
		
		try {
			return getStudyDataManager().isStudy(r.getId());
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void openStudy(Reference r){
		if(source instanceof SingleSiteAnalysisPanel) {
			Integer dataSetId = getPlotDataSetId(r.getId());
			if(dataSetId!=null) {
				((SingleSiteAnalysisPanel)source).showDatasetVariatesDetails(dataSetId);
				parentWindow.removeWindow(SelectStudyDialog.this);
			}
		} else if(source instanceof MultiSiteAnalysisPanel) {
			Study study = null;
			try {
				study = getStudyDataManager().getStudy(r.getId());
				((MultiSiteAnalysisPanel)source).openStudyMeansDataset(study);
				parentWindow.removeWindow(SelectStudyDialog.this);
			} catch (MiddlewareQueryException e) {
				e.printStackTrace();
				if (study != null) {
					MessageNotifier.showError(this, "MEANS dataset doesn't exist", study.getName() + " doesn't have an existing MEANS dataset.");
				}
				else {
					MessageNotifier.showError(this, "MEANS dataset doesn't exist", "The selected Study doesn't have an existing MEANS dataset.");
				}				
			}	
 		}		
	}

	public void queryChildrenStudies(Reference parentFolderReference,
			TreeTable tr) throws InternationalizableException {

		List<Reference> childrenReference = new ArrayList<Reference>();

		try {

			childrenReference = getStudyDataManager().getChildrenOfFolder(
					parentFolderReference.getId());

		} catch (MiddlewareQueryException e) {
			// LOG.error(e.toString() + "\n" + e.getStackTrace());
			e.printStackTrace();
			MessageNotifier
			.showWarning(
					getWindow(),
					messageSource.getMessage(Message.ERROR_DATABASE),
					messageSource
					.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
		}

		for (java.util.Iterator<Reference> i = childrenReference.iterator(); i
				.hasNext();) {

			Reference r = i.next();

			Object[] cells = new Object[3];

			Study s = null;
			try {
				s = this.getStudyDataManager().getStudy(r.getId());
			} catch (MiddlewareQueryException e) {
			}

			cells[0] = " " + r.getName();
			cells[1] = (s != null) ? s.getTitle() : "";
			cells[2] = (s != null) ? s.getObjective() : "";

			tr.addItem(cells, r);
			tr.setParent(r, parentFolderReference);
			if (hasChildStudy(r.getId())) {
				tr.setChildrenAllowed(r, true);
				tr.setItemIcon(r, getThemeResourceByReference(r));
			} else {
				tr.setChildrenAllowed(r, false);
				tr.setItemIcon(r, getThemeResourceByReference(r));
			}
		}

	}
	
	private ThemeResource getThemeResourceByReference(Reference r){
		
		if (r instanceof FolderReference){
			LOG.debug("r is FolderReference");
			return folderResource;
		}else if (r instanceof StudyReference){
			LOG.debug("r is StudyReference");
			return studyResource;
		}else if (r instanceof DatasetReference){
			LOG.debug("r is DatasetReference");
			return dataSetResource;
		}else{
			return folderResource; 
		}
			
	}

	public void queryChildrenDatasets(Reference parentFolderReference,
			TreeTable tr) throws InternationalizableException {

		List<DatasetReference> childrenReference = new ArrayList<DatasetReference>();

		try {

			childrenReference = getStudyDataManager().getDatasetReferences(
					parentFolderReference.getId());

		} catch (MiddlewareQueryException e) {
			// LOG.error(e.toString() + "\n" + e.getStackTrace());
			e.printStackTrace();
			MessageNotifier
			.showWarning(
					getWindow(),
					messageSource.getMessage(Message.ERROR_DATABASE),
					messageSource
					.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
		}

		for (java.util.Iterator<DatasetReference> i = childrenReference
				.iterator(); i.hasNext();) {

			Reference r = i.next();

			Object[] cells = new Object[3];

			cells[0] = " " + r.getName();
			cells[1] = "";
			cells[2] = "";

			if (r instanceof DatasetReference)
				LOG.debug("r is DatasetReference");

			tr.addItem(cells, r);
			tr.setParent(r, parentFolderReference);
			tr.setChildrenAllowed(r, false);
			tr.setItemIcon(r, getThemeResourceByReference(r));

		}

	}

	private boolean hasChildStudy(int folderId) {

		List<Reference> children = new ArrayList<Reference>();

		try {
			children = getStudyDataManager().getChildrenOfFolder(folderId);
		} catch (MiddlewareQueryException e) {
			// LOG.error(e.toString() + "\n" + e.getStackTrace());
			MessageNotifier
			.showWarning(
					getWindow(),
					messageSource.getMessage(Message.ERROR_DATABASE),
					messageSource
					.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
			children = new ArrayList<Reference>();
		}
		if (!children.isEmpty()) {
			return true;
		}
		return false;
	}

	public Boolean isFolder(Integer studyId) {
	        try {
	            boolean isStudy = studyDataManager.isStudy(studyId);
	            return !isStudy;
	        } catch (MiddlewareQueryException e) {
	        	return false;
	        }
	    }

	@Override
	public void attach() {
		super.attach();
		updateLabels();
	}


	@Override
	public void updateLabels() {
		if(source instanceof SingleSiteAnalysisPanel) {
			messageSource.setCaption(this,Message.BV_STUDY_TREE_TITLE);
			messageSource.setValue(lblStudyTreeDetailDescription,Message.BV_STUDY_TREE_DESCRIPTION);		
		}
		else if(source instanceof MultiSiteAnalysisPanel) {
			messageSource.setCaption(this, Message.GXE_SELECT_DATA_FOR_ANALYSIS_HEADER);
			messageSource.setCaption(lblStudyTreeDetailDescription, Message.GXE_SELECT_DATA_FOR_ANALYSIS_DESCRIPTION);
		}		
	}

	private StudyDataManagerImpl getStudyDataManager() {
		return studyDataManager;
	}
}
