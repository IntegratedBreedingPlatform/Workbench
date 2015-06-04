
package org.generationcp;

import java.util.Locale;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;

public class MessageResourceUtil {

	private MessageResourceUtil() {
		// private constructor for utility class
	}

	public static SimpleResourceBundleMessageSource getMessageResource() {
		SimpleResourceBundleMessageSource messageSource = new SimpleResourceBundleMessageSource();
		messageSource.setBasenames("I18NMessages", "VaadinCommonMessages");
		messageSource.setLocale(Locale.ENGLISH);
		return messageSource;
	}
}
