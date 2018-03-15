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

import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.listeners.StudyButtonClickListener;
import org.generationcp.ibpworkbench.study.util.DatasetExporterException;
import org.generationcp.ibpworkbench.study.util.TableViewerCellSelectorUtil;
import org.generationcp.ibpworkbench.study.util.TableViewerExporter;
import org.generationcp.ibpworkbench.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.Layout.MarginInfo;
import com.vaadin.ui.VerticalLayout;

/**
 * This creates a Vaadin sub-window that displays the Table Viewer.
 *
 * @author Mark Agarrado
 *
 */
@Configurable
public class TableViewerComponent extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	protected static final String FILENAME_PREFIX = "TVDataset";
	private static final Logger LOG = LoggerFactory.getLogger(TableViewerComponent.class);
	private static final long serialVersionUID = 477658402146083181L;
	public static final String TABLE_VIEWER_WINDOW_NAME = "table-viewer";
	public static final String EXPORT_EXCEL_BUTTON_ID = "Export Dataset to Excel";

	private final TableViewerCellSelectorUtil tableViewerCellSelectorUtil;
	private final TableViewerDatasetTable displayTable;
	private Button exportExcelButton;
	private String studyName;
	private VerticalLayout verticalLayout;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private TableViewerExporter tableViewerExporter;

	public TableViewerComponent(final TableViewerDatasetTable displayTable) {
		this.displayTable = displayTable;
		this.tableViewerCellSelectorUtil = new TableViewerCellSelectorUtil(this, displayTable);
		this.tableViewerExporter = new TableViewerExporter(this.displayTable, this.tableViewerCellSelectorUtil);
	}

	public TableViewerComponent(final TableViewerDatasetTable displayTable, final String studyName) {
		this.displayTable = displayTable;
		this.tableViewerCellSelectorUtil = new TableViewerCellSelectorUtil(this, displayTable);
		this.tableViewerExporter = new TableViewerExporter(this.displayTable, this.tableViewerCellSelectorUtil);
		this.studyName = studyName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeValues();
		this.initializeLayout();
		this.initializeActions();
	}

	protected void initializeComponents() {
		// "Export to Fieldbook Excel File"
		this.exportExcelButton = new Button();
		this.exportExcelButton.setData(TableViewerComponent.EXPORT_EXCEL_BUTTON_ID);
		this.exportExcelButton.addListener(new StudyButtonClickListener(this));
		this.exportExcelButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.verticalLayout = new VerticalLayout();

		this.verticalLayout.addComponent(this.exportExcelButton);
		this.verticalLayout.addComponent(this.displayTable);
		this.verticalLayout.setSpacing(true);

		this.addComponent(this.verticalLayout);
	}

	protected void initializeValues() {
		// for now, not needed to add any code here, we just dont want to initialize any values for now
	}

	protected void initializeLayout() {
		this.setName(TableViewerComponent.TABLE_VIEWER_WINDOW_NAME);
		this.setCaption(this.messageSource.getMessage(Message.TABLE_VIEWER_CAPTION));
		this.setSizeFull();
		this.center();
		this.setResizable(true);
		this.setScrollable(true);
		this.setModal(true);

		// enable basic edit options on the specified table
		this.displayTable.setColumnCollapsingAllowed(true);
		this.displayTable.setColumnReorderingAllowed(true);
		// display all rows of the table to the browser
		this.displayTable.setPageLength(18);
		// to make scrollbars appear on the Table component
		this.displayTable.setSizeFull();

		this.exportExcelButton.setCaption(this.messageSource.getMessage(Message.EXPORT_TO_EXCEL));

		this.verticalLayout.setMargin(new MarginInfo(true, true, true, true));

	}

	protected void initializeActions() {
		// attach listener code here
	}

	@Override
	public void updateLabels() {
		// for now, not needed to add any code here
	}

	public void exportToExcelAction() {

		final String filename = TableViewerComponent.FILENAME_PREFIX;

		try {
			final String temporaryFileName = this.tableViewerExporter.exportToExcel(filename);

			String downloadFilename;
			if (this.studyName != null) {
				downloadFilename = filename + "_" + this.studyName.replace(" ", "_").trim() + ".xlsx";
			} else {
				downloadFilename = filename + ".xlsx";
			}

			final VaadinFileDownloadResource fileDownloadResource =
					new VaadinFileDownloadResource(new File(temporaryFileName), downloadFilename, this.getApplication());

			Util.showExportExcelDownloadFile(fileDownloadResource, this.getParent().getWindow());

		} catch (final DatasetExporterException e) {
			TableViewerComponent.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(this.getApplication().getWindow(GermplasmStudyBrowserApplication.STUDY_WINDOW_NAME), e.getMessage(),
					"");
		}
	}

	public void setTableViewerExporter(final TableViewerExporter tableViewerExporter) {
		this.tableViewerExporter = tableViewerExporter;
	}

	public void setStudyName(final String studyName) {
		this.studyName = studyName;
	}
}
