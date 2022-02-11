
package org.generationcp.breeding.manager.util;

import com.vaadin.ui.Window;
import org.dellroad.stuff.vaadin.ContextApplication;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class BreedingManagerUtil {

	private static final Logger LOG = LoggerFactory.getLogger(BreedingManagerUtil.class);
	public static final String[] USER_DEF_FIELD_CROSS_NAME = {"CROSS NAME", "CROSSING NAME"};

	private BreedingManagerUtil() {

	}

	/**
	 * Get the id for UserDefinedField of Germplasm Name type for Crossing Name (matches upper case of UserDefinedField either fCode or
	 * fName). Query is: <b> SELECT fldno FROM udflds WHERE UPPER(fname) IN ('CROSSING NAME', 'CROSS NAME') OR UPPER(fcode) IN ('CROSSING
	 * NAME', 'CROSS NAME'); </b>
	 * 
	 * @param germplasmListManager
	 * @return @
	 */
	public static Integer getIDForUserDefinedFieldCrossingName(final GermplasmListManager germplasmListManager) {

		final List<UserDefinedField> nameTypes = germplasmListManager.getGermplasmNameTypes();
		for (final UserDefinedField type : nameTypes) {
			for (final String crossNameValue : BreedingManagerUtil.USER_DEF_FIELD_CROSS_NAME) {
				if (crossNameValue.equalsIgnoreCase(type.getFcode()) || crossNameValue.equalsIgnoreCase(type.getFname())) {
					return type.getFldno();
				}
			}
		}

		return null;
	}

	/**
	 * Get the id for UserDefinedField of Germplasm Name type for Crossing Name (matches upper case of UserDefinedField either fCode or
	 * fName). Query is: <b> SELECT fldno FROM udflds WHERE UPPER(fname) IN ('CROSSING NAME', 'CROSS NAME') OR UPPER(fcode) IN ('CROSSING
	 * NAME', 'CROSS NAME'); </b> If any error occurs, shows error message in passed in Window instance
	 * 
	 * @param germplasmListManager - instance of GermplasmListManager
	 * @param window - window where error message will be shown
	 * @param messageSource - resource bundle where the error message will be retrieved from
	 * @return
	 */
	public static Integer getIDForUserDefinedFieldCrossingName(final GermplasmListManager germplasmListManager, final Window window,
			final SimpleResourceBundleMessageSource messageSource) {

		try {

			return BreedingManagerUtil.getIDForUserDefinedFieldCrossingName(germplasmListManager);

		} catch (final MiddlewareQueryException e) {
			BreedingManagerUtil.LOG.error(e.getMessage(), e);
			if (window != null && messageSource != null) {
				MessageNotifier.showError(window, messageSource.getMessage(Message.ERROR_DATABASE),
						messageSource.getMessage(Message.ERROR_IN_GETTING_CROSSING_NAME_TYPE));
			}
		}

		return null;
	}

	public static String getLocationNameDisplay(final Location loc) {
		String locNameDisplay = loc.getLname();
		if (loc.getLabbr() != null && !"".equalsIgnoreCase(loc.getLabbr()) && !"-".equalsIgnoreCase(loc.getLabbr())) {
			locNameDisplay += " - (" + loc.getLabbr() + ")";
		}
		return locNameDisplay;
	}

	public static String getTypeString(final String typeCode, final List<UserDefinedField> listTypes) {
		try {
			for (final UserDefinedField listType : listTypes) {
				if (listType.getFcode().equals(typeCode)) {
					return listType.getFname();
				}
			}
		} catch (final MiddlewareQueryException ex) {
			BreedingManagerUtil.LOG.error("Error in getting list types.", ex);
			return "Error in getting list types.";
		}

		return "Germplasm List";
	}

	public static String getDescriptionForDisplay(final GermplasmList germplasmList) {
		String description = "-";
		if (germplasmList != null && germplasmList.getDescription() != null && germplasmList.getDescription().length() != 0) {
			description = germplasmList.getDescription().replaceAll("<", "&lt;");
			description = description.replaceAll(">", "&gt;");
			if (description.length() > 27) {
				description = description.substring(0, 27) + "...";
			}
		}
		return description;
	}

	public static HttpServletRequest getApplicationRequest() {
		return ContextApplication.currentRequest();
	}

}
