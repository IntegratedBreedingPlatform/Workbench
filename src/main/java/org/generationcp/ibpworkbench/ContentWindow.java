package org.generationcp.ibpworkbench;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.ui.*;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.actions.OpenProjectLocationAction;
import org.generationcp.ibpworkbench.actions.OpenProjectMethodsAction;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.net.URL;
import java.util.Map;

/**
 * Created by cyrus on 1/24/14.
 */
@Configurable
public class ContentWindow extends Window implements IContentWindow, InitializingBean, URIHandler, ParameterHandler {
    private final static Logger LOG = LoggerFactory.getLogger(ContentWindow.class);
    private Map<String, String[]> queryMap;

    @Autowired
    public SessionProvider sessionProvider;
    private String errorMessage;

    @Override
    public void showContent(Component content) {

        try {

            this.removeAllComponents();
        } catch (Exception e) {
            // ignore
        }

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

        this.queryMap = stringMap;

        // store values here
    }

    @Override
    public DownloadStream handleURI(URL url, String s) {
        errorMessage = null;
        LOG.debug("path: " + s);

        //setup correct session data
        sessionProvider.setSessionData(WorkbenchContentApp.get().getSessionData());

        // perform navigation here
        try {

            if (s != null) {
                if (s.equals("ProjectLocations")) {
                    sessionProvider.getSessionData().setLastOpenedProject(workbenchDataManager.getProjectById(Long.parseLong(queryMap.get("projectId")[0])));
                    sessionProvider.getSessionData().setSelectedProject(sessionProvider.getSessionData().getLastOpenedProject());

                    if (sessionProvider.getSessionData().getLastOpenedProject() == null) throw new Exception("Project does not exists");

                    new OpenProjectLocationAction(sessionProvider.getSessionData().getLastOpenedProject(),null).doAction(this,"/" + s,false);   // execute

                    return null;
                }

                else if (s.equals("ProjectMethods") ) {
                    sessionProvider.getSessionData().setLastOpenedProject(workbenchDataManager.getProjectById(Long.parseLong(queryMap.get("projectId")[0])));
                    sessionProvider.getSessionData().setSelectedProject(sessionProvider.getSessionData().getLastOpenedProject());

                    if (sessionProvider.getSessionData().getLastOpenedProject() == null) throw new Exception("Project does not exists");

                    new OpenProjectMethodsAction(sessionProvider.getSessionData().getLastOpenedProject(),null).doAction(this,"/" + s,false);

                    return null;
                }

            }

            //MessageNotifier.showError(this,"Oops Something went wrong :(","Wrong URI");
            //this.errorMessage = "Wrong URI";


        } catch (Exception e) {
            e.printStackTrace();


            // error happened
            //this.errorMessage = e.getMessage();

            //MessageNotifier.showError(this,"Oops Something went wrong :(",e.getMessage());
        }


        return null;
    }

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.addURIHandler(this);
        this.addParameterHandler(this);
    }

    public void initializeProjectSession() throws Exception {

    }
}
