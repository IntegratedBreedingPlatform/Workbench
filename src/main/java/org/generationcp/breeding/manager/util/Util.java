
package org.generationcp.breeding.manager.util;

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

import com.vaadin.Application;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import org.generationcp.breeding.manager.exception.BreedingManagerException;
import org.generationcp.breeding.manager.listmanager.util.GermplasmListTreeUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

	private static final Logger LOG = LoggerFactory.getLogger(Util.class);

	public static final String USER_HOME = "user.home";

	private Util() {
		// do nothing
	}

	public static boolean isTabExist(final TabSheet tabSheet, final String tabCaption) {

		final int countTabSheet = tabSheet.getComponentCount();
		for (int i = 0; i < countTabSheet; i++) {
			final Tab tab = tabSheet.getTab(i);
			if (tab.getCaption().equals(tabCaption)) {
				return true;
			}
		}
		return false;

	}

	public static boolean isTabDescriptionExist(final TabSheet tabSheet, final String tabDescription) {

		final int countTabSheet = tabSheet.getComponentCount();
		for (int i = 0; i < countTabSheet; i++) {
			final Tab tab = tabSheet.getTab(i);

			final String currentTabDescription = tab.getDescription();
			if (currentTabDescription != null && currentTabDescription.equals(tabDescription)) {
				return true;
			}
		}
		return false;

	}

	public static boolean isAccordionDatasetExist(final Accordion accordion, final String accordionCaption) {
		final int countAccordionTab = accordion.getComponentCount();

		for (int i = 0; i < countAccordionTab; i++) {
			final Tab tab = accordion.getTab(i);
			if (tab.getCaption().equals(accordionCaption)) {
				return true;
			}
		}

		return false;
	}

	public static Tab getTabAlreadyExist(final TabSheet tabSheet, final String tabCaption) {

		for (int i = 0; i < tabSheet.getComponentCount(); i++) {
			final Tab tab = tabSheet.getTab(i);
			if (tab.getCaption().equals(tabCaption)) {
				return tab;
			}
		}
		return null;

	}

	public static Tab getTabWithDescription(final TabSheet tabSheet, final String tabDescription) {

		for (int i = 0; i < tabSheet.getComponentCount(); i++) {
			final Tab tab = tabSheet.getTab(i);
			final String description = tab.getDescription();
			if (description != null && description.equals(tabDescription)) {
				return tab;
			}
		}
		return null;

	}

	public static Tab getTabToFocus(final TabSheet tabSheet, final String tabCaption) {
		Tab tabToFocus = tabSheet.getTab(0);
		boolean rightTab = false;
		for (int i = 0; i < tabSheet.getComponentCount(); i++) {
			final Tab tab = tabSheet.getTab(i);
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

	public static void closeAllTab(final TabSheet tabSheet) {

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
	public static boolean isDirectory(final String path) {
		boolean isValid = true;
		final File f = new File(path);
		if (!f.exists()) {
			isValid = false;
		} else if (!f.isDirectory()) {
			// The path is not a directory (it is a file)
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Gets the desktop directory path or the base directory of the application
	 *
	 * @param application
	 * @return file pointing to desktop or application path
	 */
	public static File getDefaultBrowseDirectory(final Application application) throws BreedingManagerException {

		// Initially gets the Desktop path of the user
		final String desktopPath = System.getProperty(Util.USER_HOME) + File.separator + "Desktop";
		File file = new File(desktopPath);

		// If desktop path is inaccessible, get the applicaton's base directory
		if (!Util.isDirectory(desktopPath) || !file.canRead() || file.getAbsolutePath() == null) {
			file = application.getContext().getBaseDirectory();
		}

		if (file != null && file.canRead() && file.getAbsolutePath() != null) {
			return file;
		} else {
			throw new BreedingManagerException("No valid default directories found");
		}

	}

	/**
	 * Gets the directory based on the given path string
	 *
	 * @param path
	 * @return file pointing to the path
	 * @throws BreedingManagerException
	 */
	public static File getDefaultBrowseDirectory(final String path) throws BreedingManagerException {
		final File file = new File(path);

		if (Util.isDirectory(path) && file.canRead() && file.getAbsolutePath() != null) {
			return file;
		} else {
			throw new BreedingManagerException("Invalid path");
		}

	}

	/**
	 * Gets one directory up the tree
	 *
	 * @param path
	 * @return
	 */
	public static String getOneFolderUp(final String path) {
		String newPath = path;

		if (path != null && path.length() > 0) {
			try {
				newPath = path.substring(0, path.lastIndexOf(File.separator));
			} catch (final StringIndexOutOfBoundsException e) {
				Util.LOG.error(e.getMessage(), e);
				newPath = "";
			}
		}

		// already at the root directory
		if ("".equalsIgnoreCase(newPath)) {
			newPath = File.separator;
		}

		return newPath;

	}

	/**
	 * Generates a string concatenation of full path of a folder eg. output "Program Lists > Folder 1 > Sub Folder 1 >"
	 * <p>
	 * where "Sub Folder 1" is the name of the folder
	 *
	 * @param germplasmListManager
	 * @param folder
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public static String generateListFolderPathLabel(final GermplasmListManager germplasmListManager, final GermplasmList folder)
		throws MiddlewareQueryException {

		final Deque<GermplasmList> parentFolders = new ArrayDeque<>();
		GermplasmListTreeUtil.traverseParentsOfList(germplasmListManager, folder, parentFolders);

		final StringBuilder locationFolderString = new StringBuilder();
		locationFolderString.append("Program Lists");

		while (!parentFolders.isEmpty()) {
			locationFolderString.append(" > ");
			final GermplasmList parentFolder = parentFolders.pop();
			locationFolderString.append(parentFolder.getName());
		}

		if (folder != null) {
			locationFolderString.append(" > ");
			locationFolderString.append(folder.getName());
		}

		String returnString = locationFolderString.toString();
		if (folder != null && folder.getName().length() >= 47) {
			returnString = folder.getName().substring(0, 47);

		} else if (locationFolderString.length() > 47) {
			final int lengthOfFolderName = folder.getName().length();
			returnString = locationFolderString.substring(0, 47 - lengthOfFolderName - 6) + "... > " + folder.getName();
		}

		returnString += " > ";

		return returnString;
	}

	public static Map<Integer, GermplasmList> getGermplasmLists(final GermplasmListManager germplasmListManager,
		final List<Integer> germplasmListIds) {
		final Map<Integer, GermplasmList> germplasmListsMap = new HashMap<>();
		List<GermplasmList> lists = new ArrayList<>();

		try {
			lists = germplasmListManager.getAllGermplasmLists(0, Integer.MAX_VALUE);
			for (final GermplasmList list : lists) {
				final Integer listId = list.getId();
				if (germplasmListIds.contains(listId)) {
					germplasmListsMap.put(listId, list);
				}
			}
		} catch (final MiddlewareQueryException e) {
			Util.LOG.error("Error retrieving all germplasm list", e);
		}
		return germplasmListsMap;
	}

}
