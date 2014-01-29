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


    public ContentWindow() {
        super("Breeding Management System");
    }

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
        String errorMessage = "";
        LOG.debug("path: " + s);

        //setup correct session data
        sessionProvider.setSessionData(WorkbenchContentApp.get().getSessionData());

        // perform navigation here
        try {

            if (s != null) {
                if (s.equals("ProjectLocations")) {

                    if (queryMap.get("programId") == null) { throw new Exception("Wrong query string, should be <strong>programId=[ID]<strong/>."); }

                    sessionProvider.getSessionData().setLastOpenedProject(workbenchDataManager.getProjectById(Long.parseLong(queryMap.get("programId")[0])));
                    sessionProvider.getSessionData().setSelectedProject(sessionProvider.getSessionData().getLastOpenedProject());

                    if (sessionProvider.getSessionData().getLastOpenedProject() == null) throw new Exception("No Program Exists with <strong>programId=" + queryMap.get("programId")[0] + "</strong>");

                    new OpenProjectLocationAction(sessionProvider.getSessionData().getLastOpenedProject(),null).doAction(this,"/" + s,false);   // execute

                    return null;
                }

                else if (s.equals("ProjectMethods") ) {
                    sessionProvider.getSessionData().setLastOpenedProject(workbenchDataManager.getProjectById(Long.parseLong(queryMap.get("programId")[0])));
                    sessionProvider.getSessionData().setSelectedProject(sessionProvider.getSessionData().getLastOpenedProject());

                    if (sessionProvider.getSessionData().getLastOpenedProject() == null) throw new Exception("No Program Exists with <strong>programId=" + queryMap.get("programId")[0] + "</strong>");

                    new OpenProjectMethodsAction(sessionProvider.getSessionData().getLastOpenedProject(),null).doAction(this,"/" + s,false);

                    return null;
                }

            }

            //MessageNotifier.showError(this,"Oops Something went wrong :(","Wrong URI");
            errorMessage = "Incorrect URL. Correct format should be<br/> <strong>/ibpworkbench/content/ProjectLocations?programId=[ID]</strong> or <strong>/ibpworkbench/content/ProjectMethods?programId=[ID]</strong>";



        } catch (NumberFormatException e) {
            errorMessage = "The value you entered for programId is not a number.";
        }

        catch (Exception e) {


            // error happened
            errorMessage = e.getMessage();

            //MessageNotifier.showError(this,"Oops Something went wrong :(",e.getMessage());
            e.printStackTrace();

        }

        CustomLayout errorPage = new CustomLayout("error");
        errorPage.setSizeFull();
        errorPage.setStyleName("error-page");

        errorPage.addComponent(new Label(errorMessage,Label.CONTENT_XHTML),"error_message");

        this.showContent(errorPage);



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
