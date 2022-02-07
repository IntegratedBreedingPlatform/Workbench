
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

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class Util {

	private Util() {
		// do nothing
	}

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

}
