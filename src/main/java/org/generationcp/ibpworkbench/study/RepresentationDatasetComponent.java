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

package org.generationcp.ibpworkbench.study;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.generationcp.commons.constant.AppConstants;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.FileNameGenerator;
import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.containers.RepresentationDatasetQueryFactory;
import org.generationcp.ibpworkbench.study.generator.DatasetCellStyleGenerator;
import org.generationcp.ibpworkbench.study.listeners.StudyButtonClickListener;
import org.generationcp.ibpworkbench.study.util.DatasetExporter;
import org.generationcp.ibpworkbench.study.util.DatasetExporterException;
import org.generationcp.ibpworkbench.util.Util;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.vaadin.addon.tableexport.CsvExport;
import com.vaadin.addon.tableexport.TableExport;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * This class creates the Vaadin Table where a dataset can be displayed.
 *
 * @author Kevin Manansala
 *
 */
@Configurable
public class RepresentationDatasetComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	protected static final String XLS_DOWNLOAD_FILENAME = "export";
	protected static final String TEMP_FILENAME = "dataset-temp";
	public static final String EXPORT_CSV_BUTTON_ID = "RepresentationDatasetComponent Export CSV Button";
	public static final String EXPORT_EXCEL_BUTTON_ID = "RepresentationDatasetComponent Export to FieldBook Excel File Button";
	public static final String OPEN_TABLE_VIEWER_BUTTON_ID = "RepresentationDatasetComponent Open Table Viewer Button";

	private static final Logger LOG = LoggerFactory.getLogger(RepresentationDatasetComponent.class);
	private static final long serialVersionUID = -8476739652987572690L;
	private static final List<PhenotypicType> EXCLUDED_ROLE = new ArrayList<PhenotypicType>();
	static {
		RepresentationDatasetComponent.EXCLUDED_ROLE.add(PhenotypicType.DATASET);
		RepresentationDatasetComponent.EXCLUDED_ROLE.add(PhenotypicType.STUDY);
	}

	private final String reportName;
	private final Integer studyIdHolder;
	private final Integer datasetId;
	private final StudyDataManager studyDataManager;
	// this is true if this component is created by accessing the Study Details page directly from the URL
	private final boolean fromUrl;
	private final boolean h2hCall;
	private Table datasetTable;
	// TODO - remove code for exporting to CSV as this has long been not displayed on screen
	private Button exportCsvButton;
	private Button exportExcelButton;
	private Button openTableViewerButton;
	private StringBuilder reportTitle;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Resource
	private ContextUtil contextUtil;

	private Map<String, Integer> studiesMappedByInstance = new HashMap<>();
	private DatasetExporter datasetExporter;

	// FIXME - Autowire StudyDataManager instead of passing it as parameter from other class
	public RepresentationDatasetComponent(final StudyDataManager studyDataManager, final Integer datasetId, final String datasetTitle,
			final Integer studyId, final boolean fromUrl, final boolean h2hCall) {
		this.reportName = datasetTitle;
		this.studyIdHolder = studyId;
		this.datasetId = datasetId;
		this.studyDataManager = studyDataManager;
		this.fromUrl = fromUrl;
		this.h2hCall = h2hCall;

		this.datasetExporter = new DatasetExporter(this.studyDataManager, this.studyIdHolder, this.datasetId);
	}

	// Called by StudyButtonClickListener
	public void exportToCSVAction() {
		final CsvExport csvExport;

		this.reportTitle = new StringBuilder().append(this.messageSource.getMessage(Message.REPORT_TITLE1_TEXT)).append("[")
				.append(this.studyIdHolder).append("]-").append(this.messageSource.getMessage(Message.REPORT_TITLE2_TEXT)).append("[")
				.append(this.datasetId).append("]-");

		final StringBuilder fileName = this.reportTitle.append(".csv");

		csvExport = new CsvExport(this.datasetTable, this.reportName, this.reportTitle.toString(), fileName.toString(), false);
		csvExport.excludeCollapsedColumns();
		csvExport.setMimeType(TableExport.CSV_MIME_TYPE);
		csvExport.export();
	}

	public void exportToExcelAction() {
		try {
			final String temporaryFileName =
					this.datasetExporter.exportToFieldBookExcelUsingIBDBv2(RepresentationDatasetComponent.TEMP_FILENAME);
			final VaadinFileDownloadResource fileDownloadResource = new VaadinFileDownloadResource(new File(temporaryFileName),
					FileNameGenerator.generateFileName(RepresentationDatasetComponent.XLS_DOWNLOAD_FILENAME, AppConstants.EXPORT_XLS_SUFFIX.getString()), this.getApplication());
			Util.showExportExcelDownloadFile(fileDownloadResource, this.getWindow());

		} catch (final DatasetExporterException e) {
			RepresentationDatasetComponent.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(this.getApplication().getWindow(GermplasmStudyBrowserApplication.STUDY_WINDOW_NAME), e.getMessage(),
					"");
		}

	}

	// Called by StudyButtonClickListener
	public void openTableViewerAction() {
		try {
			final long expCount = this.studyDataManager.countExperiments(this.datasetId);
			if (expCount > 1000) {
				// ask confirmation from user for generating large datasets
				final String confirmDialogCaption = this.messageSource.getMessage(Message.TABLE_VIEWER_CAPTION);
				final String confirmDialogMessage = this.messageSource.getMessage(Message.CONFIRM_DIALOG_MESSAGE_OPEN_TABLE_VIEWER);

				ConfirmDialog.show(this.getWindow(), confirmDialogCaption, confirmDialogMessage,
						this.messageSource.getMessage(Message.TABLE_VIEWER_OK_LABEL), this.messageSource.getMessage(Message.CANCEL_LABEL),
						new ConfirmDialog.Listener() {

							private static final long serialVersionUID = 1L;

							@Override
							public void onClose(final ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									RepresentationDatasetComponent.this.openTableViewer();
								}
							}
						});
			} else {
				this.openTableViewer();
			}
		} catch (final MiddlewareQueryException ex) {
			RepresentationDatasetComponent.LOG.error(ex.getMessage(), ex);
			RepresentationDatasetComponent.LOG
					.error("Error with getting experiments for dataset: " + this.datasetId + "\n" + ex.toString());
		}
	}

	private void openTableViewer() {
		final Window mainWindow = this.getWindow();
		final TableViewerDatasetTable tableViewerDataset =
				new TableViewerDatasetTable(this.studyDataManager, this.studyIdHolder, this.datasetId);
		final String studyName;
		try {
			studyName = this.studyDataManager.getStudy(this.studyIdHolder).getName();
			final Window tableViewer = new TableViewerComponent(tableViewerDataset, studyName);
			tableViewer.addStyleName(Reindeer.WINDOW_LIGHT);
			mainWindow.addWindow(tableViewer);
		} catch (final MiddlewareException e) {
			final Window tableViewer = new TableViewerComponent(tableViewerDataset);
			tableViewer.addStyleName(Reindeer.WINDOW_LIGHT);
			mainWindow.addWindow(tableViewer);
			RepresentationDatasetComponent.LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		this.datasetTable = this.generateLazyDatasetTable(false);

		this.setMargin(true);
		this.setSpacing(true);
		this.addComponent(this.datasetTable);
		this.setData(this.reportName);
		this.setWidth("97%");
		this.setHeight("97%");
		this.datasetTable.setSelectable(true);

		if (!this.h2hCall) {
			// "Export to CSV"
			this.exportCsvButton = new Button();
			this.exportCsvButton.setData(RepresentationDatasetComponent.EXPORT_CSV_BUTTON_ID);
			this.exportCsvButton.addListener(new StudyButtonClickListener(this));

			// "Export to Fieldbook Excel File"
			this.exportExcelButton = new Button();
			this.exportExcelButton.setData(RepresentationDatasetComponent.EXPORT_EXCEL_BUTTON_ID);
			this.exportExcelButton.addListener(new StudyButtonClickListener(this));

			this.openTableViewerButton = new Button();
			this.openTableViewerButton.setData(RepresentationDatasetComponent.OPEN_TABLE_VIEWER_BUTTON_ID);
			this.openTableViewerButton.addListener(new StudyButtonClickListener(this));

			final HorizontalLayout buttonLayout = new HorizontalLayout();
			buttonLayout.setSpacing(true);

			// only show Fieldbook Export to Excel button if study page not accessed directly from URL
			if (!this.fromUrl) {
				buttonLayout.addComponent(this.exportExcelButton);
				buttonLayout.addComponent(this.openTableViewerButton);
			}

			this.addComponent(buttonLayout);
		}
	}

	protected Table generateLazyDatasetTable(final boolean fromUrl) {
		// set the column header ids
		List<DMSVariableType> variables;
		final List<String> columnIds = new ArrayList<String>();
		try {
			final DataSet dataset = this.studyDataManager.getDataSet(this.datasetId);
			this.studiesMappedByInstance = this.studyDataManager.getInstanceGeolocationIdsMap(dataset.getStudyId());

			variables = dataset.getVariableTypes().getVariableTypes();
		} catch (final MiddlewareException e) {
			RepresentationDatasetComponent.LOG
					.error("Error in getting variables of dataset: " + this.datasetId + "\n" + e.toString() + "\n" + e.getStackTrace(), e);
			variables = new ArrayList<DMSVariableType>();
			if (this.getWindow() != null) {
				MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
						this.messageSource.getMessage(Message.ERROR_IN_GETTING_VARIABLES_OF_DATASET) + " " + this.datasetId);
			}
		}

		for (final DMSVariableType variable : variables) {
			if (!RepresentationDatasetComponent.EXCLUDED_ROLE.contains(variable.getStandardVariable().getPhenotypicType())) {
				final String columnId = new StringBuffer().append(variable.getId()).append("-").append(variable.getLocalName()).toString();
				if (!columnIds.contains(columnId)) {
					columnIds.add(columnId);
				}
			}
		}

		// create item container for dataset table
		final RepresentationDatasetQueryFactory factory =
				new RepresentationDatasetQueryFactory(this.studyDataManager, this.datasetId, columnIds, fromUrl, this.studyIdHolder);
		final LazyQueryContainer datasetContainer = new LazyQueryContainer(factory, false, 50);
		this.populateDatasetContainerProperties(fromUrl, columnIds, datasetContainer);

		// initialize the first batch of data to be displayed
		datasetContainer.getQueryView().getItem(0);

		// create the Vaadin Table to display the dataset, pass the container object created
		final Table newTable = new Table("", datasetContainer);
		newTable.setColumnCollapsingAllowed(true);
		newTable.setColumnReorderingAllowed(true);
		// number of rows to display in the Table
		newTable.setPageLength(15);
		// to make scrollbars appear on the Table component
		newTable.setSizeFull();

		// set column headers for the Table
		for (final DMSVariableType variable : variables) {
			final String columnId = new StringBuffer().append(variable.getId()).append("-").append(variable.getLocalName()).toString();
			final String columnHeader = variable.getLocalName();
			newTable.setColumnHeader(columnId, columnHeader);
		}

		newTable.setCellStyleGenerator(new DatasetCellStyleGenerator(newTable));

		return newTable;
	}

	protected void populateDatasetContainerProperties(final boolean fromUrl, final List<String> columnIds, final LazyQueryContainer datasetContainer) {
		// add the column ids to the LazyQueryContainer tells the container the columns to display for the Table
		for (final String columnId : columnIds) {
			if (columnId.contains(String.valueOf(TermId.GID.getId())) && !fromUrl) {
				datasetContainer.addContainerProperty(columnId, Link.class, null);
			} else if (columnId.contains(String.valueOf(TermId.FEMALE_PARENT_GID.getId())) && !fromUrl) {
				datasetContainer.addContainerProperty(columnId, Link.class, null);
			} else if (columnId.contains(String.valueOf(TermId.MALE_PARENT_GID.getId())) && !fromUrl) {
				datasetContainer.addContainerProperty(columnId, Link.class, null);
			} else {
				datasetContainer.addContainerProperty(columnId, String.class, null);
			}
		}
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setCaption(this.exportCsvButton, Message.EXPORT_TO_CSV_LABEL);
		this.messageSource.setCaption(this.exportExcelButton, Message.EXPORT_TO_EXCEL_LABEL);
		this.messageSource.setCaption(this.openTableViewerButton, Message.OPEN_TABLE_VIEWER_LABEL);
	}

	public void setDatasetExporter(final DatasetExporter datasetExporter) {
		this.datasetExporter = datasetExporter;
	}

}
