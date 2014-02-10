package org.generationcp.ibpworkbench;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.ui.Window;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.commons.vaadin.actions.UpdateComponentLabelsAction;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.util.Map;

/**
 * Created by cyrus on 1/24/14.
 */
public class WorkbenchContentApp extends SpringContextApplication implements IWorkbenchSession {

    private final static Logger LOG = LoggerFactory.getLogger(WorkbenchContentApp.class);

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    private UpdateComponentLabelsAction messageSourceListener;

    @Override
    public void close() {
        super.close();

        // implement this when we need to do something on session timeout

        messageSource.removeListener(messageSourceListener);

        LOG.debug("Application closed");
    }

    @Override
    protected void initSpringApplication(ConfigurableWebApplicationContext configurableWebApplicationContext) {
        setTheme("gcp-default");

        messageSourceListener = new UpdateComponentLabelsAction(this);
        messageSource.addListener(messageSourceListener);

        this.setMainWindow(new ContentWindow());
    }

    @Override
    public SessionData getSessionData() {
        return sessionData;
    }

    public static WorkbenchContentApp get() {
        return get(WorkbenchContentApp.class);
    }
}
