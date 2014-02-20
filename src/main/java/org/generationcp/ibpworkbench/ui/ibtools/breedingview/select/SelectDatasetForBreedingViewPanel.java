/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.ibtools.breedingview.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.actions.ShowDatasetVariablesDetailAction;
import org.generationcp.ibpworkbench.actions.StudyTreeExpandAction;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Jeffrey Morales
 * 
 */
@Configurable
public class SelectDatasetForBreedingViewPanel extends VerticalLayout implements
		InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;

	private Label lblPageTitle;
	private Label lblStudyTreeDetailTitle;
	private Label lblStudyTreeDetailDescription;
	private Label lblDatasetDetailTitle;
	private Label lblDatasetDetailDescription;

	private Table variates;
	private Property.ValueChangeListener selectAllListener;
	private CheckBox chkVariatesSelectAll;

	private VerticalLayout generalLayout;

	private VerticalLayout studyTreeLayout;
	private VerticalLayout studyTreeLayoutTableContainer;

	private GridLayout studyDetailsLayout;

	private HorizontalLayout datasetVariablesDetailLayout;

	private Project currentProject;

	private Study currentStudy;

	private Integer currentRepresentationId;

	private Integer currentDataSetId;

	private String currentDatasetName;

	private Button btnCancel;
	private Button btnNext;
	private Component buttonArea;

	private Database database;
	private HashMap<String, Boolean> variatesCheckboxState;
	private int numOfSelectedVariates = 0;

	private ThemeResource folderResource;
	private ThemeResource leafResource;

	private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;

	private final static Logger LOG = LoggerFactory
			.getLogger(SelectDatasetForBreedingViewPanel.class);

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private StudyDataManager studyDataManager;

	private ManagerFactory managerFactory;

	public SelectDatasetForBreedingViewPanel(Project currentProject,
			Database database) {

		this.currentProject = currentProject;
		this.database = database;

	}

	public Project getCurrentProject() {
		return currentProject;
	}

	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}

	public Study getCurrentStudy() {
		return currentStudy;
	}

	public void setCurrentStudy(Study currentStudy) {
		this.currentStudy = currentStudy;
	}

	public Integer getCurrentRepresentationId() {
		return currentRepresentationId;
	}

	public void setCurrentRepresentationId(Integer currentRepresentationId) {
		this.currentRepresentationId = currentRepresentationId;
	}

	public Integer getCurrentDataSetId() {
		return currentDataSetId;
	}

	public void setCurrentDataSetId(Integer currentDataSetId) {
		this.currentDataSetId = currentDataSetId;
	}

	public String getCurrentDatasetName() {
		return currentDatasetName;
	}

	public void setCurrentDatasetName(String currentDatasetName) {
		this.currentDatasetName = currentDatasetName;
	}

	protected void initializeComponents() {

		lblPageTitle = new Label();
		lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		folderResource = new ThemeResource("images/folder.png");
		leafResource = new ThemeResource("images/leaf_16.png");

		setVariatesCheckboxState(new HashMap<String, Boolean>());

		generalLayout = new VerticalLayout();
		generalLayout.setWidth("100%");

		studyTreeLayout = new VerticalLayout();
		studyTreeLayoutTableContainer = new VerticalLayout();

		studyDetailsLayout = new GridLayout(10, 3);

		datasetVariablesDetailLayout = new HorizontalLayout();

		studyTreeLayout.addComponent(lblPageTitle);
		studyTreeLayout.addComponent(new Label(""));

		lblStudyTreeDetailTitle = new Label();
		lblStudyTreeDetailTitle.setStyleName(Bootstrap.Typography.H2
				.styleName());
		studyTreeLayout.addComponent(lblStudyTreeDetailTitle);

		lblStudyTreeDetailDescription = new Label();
		studyTreeLayout.addComponent(lblStudyTreeDetailDescription);

		Table factors = initializeFactorsTable();

		variates = initializeVariatesTable();

		TreeTable tr = createStudyTreeTable(this.database, factors, variates);
		studyTreeLayoutTableContainer.addComponent(tr);
		studyTreeLayout.addComponent(studyTreeLayoutTableContainer);

		buttonArea = layoutButtonArea();

		datasetVariablesDetailLayout.addComponent(factors);
		datasetVariablesDetailLayout.addComponent(variates);

		VerticalLayout vContainer1 = new VerticalLayout();
		VerticalLayout vContainer2 = new VerticalLayout();
		vContainer1.setWidth("100%");
		vContainer2.setWidth("100%");

		Label lblFactors = new Label("FACTORS");
		lblFactors.setStyleName(Bootstrap.Typography.H4.styleName());
		lblFactors.setWidth("100%");
		Label lblVariates = new Label("VARIATES");
		lblVariates.setWidth("100%");
		lblVariates.setStyleName(Bootstrap.Typography.H4.styleName());
		HorizontalLayout datasetVariablesDetailHeaderLayout = new HorizontalLayout();
		datasetVariablesDetailHeaderLayout.setMargin(false, false, false, true);
		datasetVariablesDetailHeaderLayout.setWidth("100%");
		vContainer1.addComponent(lblFactors);
		vContainer2.addComponent(lblVariates);
		datasetVariablesDetailHeaderLayout.addComponent(vContainer1);
		datasetVariablesDetailHeaderLayout.addComponent(vContainer2);
		datasetVariablesDetailHeaderLayout.setExpandRatio(vContainer1, 1.0F);
		datasetVariablesDetailHeaderLayout.setExpandRatio(vContainer2, 1.0F);
		studyDetailsLayout.addComponent(datasetVariablesDetailHeaderLayout, 0,
				0, 9, 0);

		studyDetailsLayout.addComponent(datasetVariablesDetailLayout, 0, 1, 9,
				1);

		selectAllListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 344514045768824046L;

			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(ValueChangeEvent event) {

				Boolean val = (Boolean) event.getProperty().getValue();
				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) variates.getContainerDataSource();
				for (Object itemId : container.getItemIds()){
					container.getItem(itemId).getBean().setActive(val);
				}
				variates.refreshRowCache();
				for (Entry<String, Boolean> entry : variatesCheckboxState
						.entrySet()) {
					variatesCheckboxState.put(entry.getKey(), val);
				}
				if(val) {
					numOfSelectedVariates = variatesCheckboxState.size();
				} else {
					numOfSelectedVariates = 0;
				}
				toggleNextButton(val);
			}

		};

		chkVariatesSelectAll = new CheckBox();
		chkVariatesSelectAll.setImmediate(true);
		chkVariatesSelectAll.addListener(selectAllListener);

		chkVariatesSelectAll.setCaption("Select All");

		studyDetailsLayout.addComponent(chkVariatesSelectAll, 6, 2, 9, 2);

		generalLayout.addComponent(studyTreeLayout);

		VerticalLayout studyDetailsDescriptionLayout = new VerticalLayout();
		studyDetailsDescriptionLayout.setSpacing(true);
		studyDetailsDescriptionLayout.setMargin(new MarginInfo(false, true,
				false, true));
		lblDatasetDetailTitle = new Label();
		lblDatasetDetailTitle.setStyleName(Bootstrap.Typography.H2.styleName());

		studyDetailsDescriptionLayout.addComponent(lblDatasetDetailTitle);

		lblDatasetDetailDescription = new Label();
		studyDetailsDescriptionLayout.addComponent(lblDatasetDetailDescription);

		generalLayout.addComponent(studyDetailsDescriptionLayout);
		generalLayout.addComponent(studyDetailsLayout);
		generalLayout.addComponent(buttonArea);
		generalLayout.setComponentAlignment(buttonArea, Alignment.TOP_CENTER);

		addComponent(generalLayout);

	}

	protected void initializeLayout() {

		studyTreeLayout.setSpacing(true);
		studyTreeLayout.setMargin(new MarginInfo(false, true, false, true));

		studyDetailsLayout.setWidth("100%");

		datasetVariablesDetailLayout.setMargin(true);
		datasetVariablesDetailLayout.setSpacing(true);
		datasetVariablesDetailLayout.setWidth("100%");

		this.setWidth("100%");

	}

	protected void initialize() {
	}

	protected void initializeActions() {
		btnCancel.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				// new HomeAction().doAction(event.getComponent().getWindow(),
				// "/Home", true);
				SelectDatasetForBreedingViewPanel.this
						.refreshStudyTreeTable(Database.LOCAL);
				toggleNextButton(false);
			}
		});
		openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(
				this);
		btnNext.addListener(openSelectDatasetForExportAction);

	}

	protected Table initializeFactorsTable() {

		final Table tblFactors = new Table();
		tblFactors.setImmediate(true);
		tblFactors.setWidth("100%");
		tblFactors.setHeight("450px");

		BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(
				FactorModel.class);
		container.setBeanIdProperty("id");
		tblFactors.setContainerDataSource(container);

		String[] columns = new String[] { "name", "description" };
		String[] columnHeaders = new String[] { "Name", "Description" };
		tblFactors.setVisibleColumns(columns);
		tblFactors.setColumnHeaders(columnHeaders);

		tblFactors.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;

			public String generateDescription(Component source, Object itemId,
					Object propertyId) {
				BeanContainer<Integer, FactorModel> container = (BeanContainer<Integer, FactorModel>) tblFactors
						.getContainerDataSource();
				FactorModel fm = container.getItem(itemId).getBean();

				StringBuilder sb = new StringBuilder();
				sb.append(String.format(
						"<span class=\"gcp-table-header-bold\">%s</span><br>",
						fm.getName()));
				sb.append(String.format("<span>Property:</span> %s<br>",
						fm.getTrname()));
				sb.append(String.format("<span>Scale:</span> %s<br>",
						fm.getScname()));
				sb.append(String.format("<span>Method:</span> %s<br>",
						fm.getTmname()));
				sb.append(String.format("<span>Data Type:</span> %s",
						fm.getDataType()));

				return sb.toString();
			}
		});

		return tblFactors;
	}

	protected Table initializeVariatesTable() {

		variatesCheckboxState.clear();

		final Table tblVariates = new Table();
		tblVariates.setImmediate(true);
		tblVariates.setWidth("100%");
		tblVariates.setHeight("450px");
		tblVariates.setColumnExpandRatio("", 0.5f);
		tblVariates.setColumnExpandRatio("name", 1);
		tblVariates.setColumnExpandRatio("description", 4);
		tblVariates.setColumnExpandRatio("scname", 1);
		tblVariates.addGeneratedColumn("", new Table.ColumnGenerator() {

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {

				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) tblVariates
						.getContainerDataSource();
				final VariateModel vm = container.getItem(itemId).getBean();

				final CheckBox checkBox = new CheckBox();
				checkBox.setImmediate(true);
				checkBox.setVisible(true);
				checkBox.addListener(new Property.ValueChangeListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(final ValueChangeEvent event) {
						Boolean val = (Boolean) event.getProperty().getValue();
						variatesCheckboxState.put(vm.getName(), val);
						vm.setActive(val);

						if (!val) {
							chkVariatesSelectAll
									.removeListener(selectAllListener);
							chkVariatesSelectAll.setValue(val);
							chkVariatesSelectAll.addListener(selectAllListener);
							numOfSelectedVariates--;
							if(numOfSelectedVariates==0) {
								toggleNextButton(false);
							}
						} else {
							if(numOfSelectedVariates<variatesCheckboxState.size()) {//add this check to ensure that the number of selected does not exceed the total number of variates
								numOfSelectedVariates++;
							}
							toggleNextButton(true);
						}

					}
				});

				if (vm.getActive()) {
					checkBox.setValue(true);
				} else {
					checkBox.setValue(false);
				}

				return checkBox;

			}

		});

		tblVariates.setItemDescriptionGenerator(new ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;

			public String generateDescription(Component source, Object itemId,
					Object propertyId) {
				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) tblVariates.getContainerDataSource();
				VariateModel vm = container.getItem(itemId).getBean();

				StringBuilder sb = new StringBuilder();
				sb.append(String.format(
						"<span class=\"gcp-table-header-bold\">%s</span><br>",
						vm.getName()));
				sb.append(String.format("<span>Property:</span> %s<br>",
						vm.getTrname()));
				sb.append(String.format("<span>Scale:</span> %s<br>",
						vm.getScname()));
				sb.append(String.format("<span>Method:</span> %s<br>",
						vm.getTmname()));
				sb.append(String.format("<span>Data Type:</span> %s",
						vm.getDatatype()));

				return sb.toString();
			}
		});

		BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(
				VariateModel.class);
		container.setBeanIdProperty("id");
		tblVariates.setContainerDataSource(container);

		String[] columns = new String[] { "", "name", "description", "scname" };
		String[] columnHeaders = new String[] { "", "Name", "Description",
				"Scale" };
		tblVariates.setVisibleColumns(columns);
		tblVariates.setColumnHeaders(columnHeaders);
		tblVariates.setColumnWidth("", 18);
		return tblVariates;
	}

	private Table[] refreshFactorsAndVariatesTable() {
		Table toreturn[] = new Table[2];
		datasetVariablesDetailLayout.removeAllComponents();
		Table factors = initializeFactorsTable();
		variates = initializeVariatesTable();
		datasetVariablesDetailLayout.addComponent(factors);
		datasetVariablesDetailLayout.addComponent(variates);
		toreturn[0] = factors;
		toreturn[1] = variates;
		return toreturn;
	}

	protected Component layoutButtonArea() {

		HorizontalLayout buttonLayout = new HorizontalLayout();

		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);

		btnCancel = new Button();
		btnNext = new Button();
		btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		btnNext.setEnabled(false);// default

		buttonLayout.addComponent(btnCancel);
		buttonLayout.addComponent(btnNext);
		buttonLayout.setComponentAlignment(btnCancel, Alignment.TOP_CENTER);
		buttonLayout.setComponentAlignment(btnNext, Alignment.TOP_CENTER);
		return buttonLayout;
	}

	protected void assemble() {
		initialize();
		initializeComponents();
		initializeLayout();
		initializeActions();
	}

	private TreeTable createStudyTreeTable(Database database, Table factors,
			Table variates) {

		TreeTable tr = new TreeTable();

		tr.addContainerProperty("Study Name", String.class, "sname");
		tr.addContainerProperty("Title", String.class, "title");
		tr.addContainerProperty("Description", String.class, "description");

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

			Object[] cells = new Object[3];
			cells[0] = " " + fr.getName();
			cells[1] = "";
			cells[2] = fr.getDescription();

			Object itemId = tr.addItem(cells, fr);
			tr.setItemIcon(itemId, folderResource);

		}

		// reserve excess space for the "treecolumn"
		tr.setSizeFull();
		tr.setColumnExpandRatio("Study Name", 1);
		tr.setColumnExpandRatio("Title", 1);
		tr.setColumnExpandRatio("Description", 1);
		tr.setSelectable(true);

		tr.addListener(new StudyTreeExpandAction(this, tr));
		tr.addListener(new ShowDatasetVariablesDetailAction(factors, variates,
				this));
		return tr;
	}

	public void refreshStudyTreeTable(Database database) {

		Table variables[] = refreshFactorsAndVariatesTable();

		this.studyTreeLayout.removeAllComponents();
		TreeTable tr = createStudyTreeTable(database, variables[0],
				variables[1]);
		this.studyTreeLayout.addComponent(tr);

		managerFactory.close();
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
			cells[2] = r.getDescription();

			if (r instanceof FolderReference)
				LOG.debug("r is FolderReference");
			if (r instanceof StudyReference)
				LOG.debug("r is StudyReference");

			tr.addItem(cells, r);
			tr.setParent(r, parentFolderReference);
			if (hasChildStudy(r.getId()) || hasChildDataset(r.getId())) {
				tr.setChildrenAllowed(r, true);
				tr.setItemIcon(r, folderResource);
			} else {
				tr.setChildrenAllowed(r, false);
				tr.setItemIcon(r, leafResource);
			}
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
			cells[2] = r.getDescription();

			if (r instanceof DatasetReference)
				LOG.debug("r is DatasetReference");

			tr.addItem(cells, r);
			tr.setParent(r, parentFolderReference);
			tr.setChildrenAllowed(r, false);
			tr.setItemIcon(r, leafResource);

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

	private boolean hasChildDataset(int folderId) {

		List<DatasetReference> children = new ArrayList<DatasetReference>();

		try {
			children = getStudyDataManager().getDatasetReferences(folderId);
		} catch (MiddlewareQueryException e) {
			// LOG.error(e.toString() + "\n" + e.getStackTrace());
			MessageNotifier
					.showWarning(
							getWindow(),
							messageSource.getMessage(Message.ERROR_DATABASE),
							messageSource
									.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
			children = new ArrayList<DatasetReference>();
		}
		if (!children.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		managerFactory = managerFactoryProvider
				.getManagerFactoryForProject(currentProject);
		assemble();
	}

	@Override
	public void attach() {
		super.attach();
		updateLabels();
	}

	@Override
	public void updateLabels() {
		messageSource.setCaption(btnCancel, Message.RESET);
		messageSource.setCaption(btnNext, Message.NEXT);
		messageSource.setValue(lblPageTitle, Message.TITLE_SSA);
		messageSource.setValue(lblStudyTreeDetailTitle,
				Message.BV_STUDY_TREE_TITLE);
		messageSource.setValue(lblStudyTreeDetailDescription,
				Message.BV_STUDY_TREE_DESCRIPTION);
		messageSource.setValue(lblDatasetDetailTitle,
				Message.BV_DATASET_DETAIL_TITLE);
		messageSource.setValue(lblDatasetDetailDescription,
				Message.BV_DATASET_DETAIL_DESCRIPTION);
	}

	public StudyDataManager getStudyDataManager() {
		if (this.studyDataManager == null)
			this.studyDataManager = getManagerFactory()
					.getNewStudyDataManager();
		return this.studyDataManager;
	}

	public ManagerFactory getManagerFactory() {
		return managerFactory;
	}

	public HashMap<String, Boolean> getVariatesCheckboxState() {
		return variatesCheckboxState;
	}

	public void setVariatesCheckboxState(
			HashMap<String, Boolean> variatesCheckboxState) {
		this.variatesCheckboxState = variatesCheckboxState;
	}

	public void toggleNextButton(boolean enabled) {
		btnNext.setEnabled(enabled);
	}

	public int getNumOfSelectedVariates() {
		return numOfSelectedVariates;
	}

	public void setNumOfSelectedVariates(int numOfSelectedVariates) {
		this.numOfSelectedVariates = numOfSelectedVariates;
	}
	
	
}
