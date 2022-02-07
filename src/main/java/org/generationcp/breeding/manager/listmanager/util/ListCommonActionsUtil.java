package org.generationcp.breeding.manager.listmanager.util;

import com.vaadin.ui.Window;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;

public class ListCommonActionsUtil {

	// so class cannot be instantiated
	private ListCommonActionsUtil() {
	}

	public static void deleteGermplasmList(final GermplasmListManager germplasmListManager, final GermplasmList germplasmList,
			final ContextUtil contextUtil, final Window window, final SimpleResourceBundleMessageSource messageSource, final String item) {

		germplasmListManager.deleteGermplasmList(germplasmList);

		contextUtil.logProgramActivity("Deleted a germplasm list.",
				"Deleted germplasm list with id = " + germplasmList.getId() + " and name = " + germplasmList.getName() + ".");

		MessageNotifier.showMessage(window, messageSource.getMessage(Message.SUCCESS),
				messageSource.getMessage(Message.SUCCESSFULLY_DELETED_ITEM, item));

	}

}
