package org.generationcp.ibpworkbench;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import java.net.URL;
import java.util.Map;

/**
 * Created by cyrus on 1/24/14.
 */
@Configurable
public class ContentWindow extends Window implements IContentWindow, InitializingBean, URIHandler, ParameterHandler {
    private final static Logger LOG = LoggerFactory.getLogger(ContentWindow.class);


    @Override
    public void showContent(Component content) {
        this.removeAllComponents();

        if (content instanceof ComponentContainer) {
            this.setContent((ComponentContainer)content);
        } else {
            this.addComponent(content);
        }

    }

    @Override
    public void handleParameters(Map<String, String[]> stringMap) {
        for (String key : stringMap.keySet()) {

            String values = "";
            for (String val : stringMap.get(key)) {
                values += val + " ";
            }

            LOG.debug("query: " + key + " value(s): " + values);
        }

        // store values here
    }

    @Override
    public DownloadStream handleURI(URL url, String s) {

        LOG.debug("path: " + s);
        // perform navigation here

        this.assemble();

        return null;
    }

    private void assemble() {
        this.addComponent(new Label("test"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.addURIHandler(this);
        this.addParameterHandler(this);
    }
}
