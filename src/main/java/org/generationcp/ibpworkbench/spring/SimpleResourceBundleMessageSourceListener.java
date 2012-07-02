package org.generationcp.ibpworkbench.spring;

import java.util.Locale;

public interface SimpleResourceBundleMessageSourceListener {
    
    void localeChanged(Locale oldLocale, Locale newLocale);
}
