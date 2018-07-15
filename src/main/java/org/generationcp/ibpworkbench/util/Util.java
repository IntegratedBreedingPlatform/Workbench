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

package org.generationcp.ibpworkbench.util;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dellroad.stuff.vaadin.ContextApplication;
import org.generationcp.ibpworkbench.exception.GermplasmStudyBrowserException;
import org.generationcp.middleware.domain.oms.TermId;

import com.vaadin.Application;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window;

public class Util {

	public static final String USER_HOME = "user.home";

	public static boolean isTabExist(TabSheet tabSheet, String tabCaption) {

		int countTabSheet = tabSheet.getComponentCount();

		for (int i = 0; i < countTabSheet; i++) {
			Tab tab = tabSheet.getTab(i);
			if (tab.getCaption().equals(tabCaption)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAccordionDatasetExist(Accordion accordion, String accordionCaption) {
		int countAccordionTab = accordion.getComponentCount();

		for (int i = 0; i < countAccordionTab; i++) {
			Tab tab = accordion.getTab(i);
			if (tab.getCaption().equals(accordionCaption)) {
				return true;
			}
		}

		return false;
	}

	public static Tab getTabAlreadyExist(TabSheet tabSheet, String tabCaption) {

		for (int i = 0; i < tabSheet.getComponentCount(); i++) {
			Tab tab = tabSheet.getTab(i);
			if (tab.getCaption().equals(tabCaption)) {
				return tab;
			}
		}
		return null;

	}

	public static Tab getTabToFocus(TabSheet tabSheet, String tabCaption) {
		Tab tabToFocus = tabSheet.getTab(0);
		boolean rightTab = false;
		for (int i = 0; i < tabSheet.getComponentCount(); i++) {
			Tab tab = tabSheet.getTab(i);
			if (rightTab) {
				tabToFocus = tab;
				return tabToFocus;
			}
			if (tab.getCaption().equals(tabCaption)) {
				if (i == tabSheet.getComponentCount() - 1) {
					return tabToFocus;
				} else {
					rightTab = true;
				}
			}

			tabToFocus = tab;
		}
		return null;

	}

	public static void closeAllTab(TabSheet tabSheet) {

		for (int i = tabSheet.getComponentCount() - 1; i >= 0; i--) {
			tabSheet.removeTab(tabSheet.getTab(i));
		}

	}

	/**
	 * Validates if an existing path is a directory
	 * 
	 * @param path
	 * @return true if the given path is a directory
	 */
	public static boolean isDirectory(String path) {
		boolean isValid = true;
		File f = new File(path);
		// The directory does not exist
		if (!f.exists()) {
			isValid = false;
		// The path is not a directory (it is a file)
		} else if (!f.isDirectory()) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Gets the desktop directory path or the base directory of the application
	 * 
	 * @param application
	 * @return file pointing to desktop or application path
	 * 
	 */
	public static File getDefaultBrowseDirectory(Application application) throws GermplasmStudyBrowserException {

		// Initially gets the Desktop path of the user
		String desktopPath = System.getProperty(Util.USER_HOME) + File.separator + "Desktop";
		File file = new File(desktopPath);

		// If desktop path is inaccessible, get the applicaton's base directory
		if (!Util.isDirectory(desktopPath) || file == null || !file.canRead() || file.getAbsolutePath() == null) {
			file = application.getContext().getBaseDirectory();
		}

		if (file != null && file.canRead() && file.getAbsolutePath() != null) {
			return file;
		} else {
			throw new GermplasmStudyBrowserException("No valid default directories found");
		}

	}

	/**
	 * Gets the directory based on the given path string
	 * 
	 * @param path
	 * @return file pointing to the path
	 * @throws GermplasmStudyBrowserException
	 */
	public static File getDefaultBrowseDirectory(String path) throws GermplasmStudyBrowserException {
		File file = new File(path);

		if (file != null && Util.isDirectory(path) && file.canRead() && file.getAbsolutePath() != null) {
			return file;
		} else {
			throw new GermplasmStudyBrowserException("Invalid path");
		}

	}

	/**
	 * Gets one directory up the tree
	 * 
	 * @param path
	 * @return
	 */
	public static String getOneFolderUp(String path) {
		String newPath = path;

		if (path != null && path.length() > 0) {
			try {
				newPath = path.substring(0, path.lastIndexOf(File.separator));
			} catch (StringIndexOutOfBoundsException e) {
				newPath = "";
			}
		}
		// already at the root directory
		if (StringUtils.isEmpty(newPath)) {
			newPath = File.separator;
		}

		return newPath;

	}

	/**
	 * Returns true if given data type id is the id of one of the following data types: - Numeric Variable - Numeric DBID variable - Date
	 * variable -
	 * 
	 * @param dataTypeId
	 * @return
	 */
	public static boolean isNumericVariable(int dataTypeId) {
		return ArrayUtils
				.contains(new int[] {TermId.NUMERIC_VARIABLE.getId(), TermId.NUMERIC_DBID_VARIABLE.getId(), TermId.DATE_VARIABLE.getId()},
						dataTypeId);
	}

	public static boolean showExportExcelDownloadFile(FileResource fileDownloadResource, Window window) {
		if (window != null && fileDownloadResource != null) {
			window.open(fileDownloadResource, null, false);
			return true;
		}
		return false;
	}

	public static HttpServletRequest getApplicationRequest() {
		return ContextApplication.currentRequest();
	}
}
