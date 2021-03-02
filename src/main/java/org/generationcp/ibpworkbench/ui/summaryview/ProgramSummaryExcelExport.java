package org.generationcp.ibpworkbench.ui.summaryview;

import com.vaadin.Application;
import com.vaadin.addon.tableexport.TemporaryFileDownloadResource;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import org.generationcp.ibpworkbench.ui.programadministration.ProgramAdministrationPanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

public class ProgramSummaryExcelExport extends com.vaadin.addon.tableexport.ExcelExport {

	private static final Logger LOGGER = Logger.getLogger(ProgramSummaryExcelExport.class.getName());

	private Window window;

	public ProgramSummaryExcelExport(final Window window, final Table component, final String tableName) {
		super(component, tableName);
		this.window = window;
	}

	@Override
	public void export() {
		this.convertTable();
		this.sendConverted();
	}

	@Override
	public boolean sendConverted() {
		File tempFile = null;
		FileOutputStream fileOut = null;

		try {
			tempFile = File.createTempFile("tmp", ".xls");
			fileOut = new FileOutputStream(tempFile);
			this.workbook.write(fileOut);
			if (this.mimeType == null) {
				this.setMimeType(EXCEL_MIME_TYPE);
			}

			boolean success = this.sendConvertedFileToUser(this.getTable().getApplication(), tempFile, this.exportFileName);
			boolean var5 = success;
			return var5;
		} catch (IOException var13) {
			LOGGER.warning("Converting to XLS failed with IOException " + var13);
		} finally {
			tempFile.deleteOnExit();

			try {
				fileOut.close();
			} catch (IOException var12) {
			}

		}

		return false;
	}

	@Override
	protected boolean sendConvertedFileToUser(final Application app, final File fileToExport, final String exportFileName) {

		try {
			TemporaryFileDownloadResource resource = new TemporaryFileDownloadResource(app, exportFileName, this.mimeType, fileToExport);
			this.window.open(resource, this.exportWindow);
			return true;
		} catch (FileNotFoundException var6) {
			LOGGER.warning("Sending file to user failed with FileNotFoundException " + var6);
			return false;
		}
	}

}
