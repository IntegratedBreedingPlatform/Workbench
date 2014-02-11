package org.generationcp.ibpworkbench;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.ui.*;
import org.generationcp.ibpworkbench.actions.OpenProgramLocationsAction;
import org.generationcp.ibpworkbench.actions.OpenProgramMethodsAction;
import org.generationcp.ibpworkbench.ui.common.IContainerFittable;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
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
    private String path;
    private URL url;
    private IWorkbenchSession appSession;

    public ContentWindow() {
        super("Breeding Management System | Workbench");
    }

    @Override
    public void showContent(Component content) {

        try {

            this.removeAllComponents();
        } catch (Exception e) {
            // ignore
        }

        if (content instanceof ComponentContainer) {

            if (content instanceof IContainerFittable) {
                ((IContainerFittable)content).fitToContainer();
            }

            this.setContent((ComponentContainer)content);
        } else {
            this.addComponent(content);
        }

    }

    @Override
    public void attach() {
        super.attach();

        appSession = (IWorkbenchSession) this.getApplication();
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
    }

    @Override
    public DownloadStream handleURI(URL url, String s) {
       this.path = s;
       this.url = url;

        String errorMessage = "";
        LOG.debug("path: " + path);

        // perform navigation here
        try {

            if (path != null) {
                if (path.equals("ProgramLocations")) {

                    if (queryMap.get("programId") == null) { throw new Exception("Wrong query string, should be <strong>programId=[ID]<strong/>."); }

                    Project project = workbenchDataManager.getProjectById(Long.parseLong(queryMap.get("programId")[0]));

                    if (project == null) throw new Exception("No Program Exists with <strong>programId=" + queryMap.get("programId")[0] + "</strong>");

                    if (appSession.getSessionData().getLastOpenedProject() == null)
                        appSession.getSessionData().setLastOpenedProject(project);

                    if (appSession.getSessionData().getSelectedProject() == null)
                        appSession.getSessionData().setSelectedProject(project);

                    new OpenProgramLocationsAction(project ,null).doAction(this,"/" + path,false);   // execute

                    return null;
                }

                else if (path.equals("ProgramMethods") ) {

                    if (queryMap.get("programId") == null) { throw new Exception("Wrong query string, should be <strong>programId=[ID]<strong/>."); }

                    Project project = workbenchDataManager.getProjectById(Long.parseLong(queryMap.get("programId")[0]));

                    if (project == null) throw new Exception("No Program Exists with <strong>programId=" + queryMap.get("programId")[0] + "</strong>");

                    if (appSession.getSessionData().getLastOpenedProject() == null)
                        appSession.getSessionData().setLastOpenedProject(project);

                    if (appSession.getSessionData().getSelectedProject() == null)
                        appSession.getSessionData().setSelectedProject(project);

                    new OpenProgramMethodsAction(project ,null).doAction(this,"/" + path,false);

                    return null;
                }

            }

            //MessageNotifier.showError(this,"Oops Something went wrong :(","Wrong URI");
            errorMessage = "Incorrect URL. Correct format should be<br/> <strong>/ibpworkbench/content/ProgramLocations?programId=[ID]</strong> or <strong>/ibpworkbench/content/ProgramMethods?programId=[ID]</strong>";



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
        errorPage.setSizeUndefined();
        errorPage.setWidth("100%");
        errorPage.setStyleName("error-page");
        errorPage.addComponent(new Label(errorMessage, Label.CONTENT_XHTML), "error_message");

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
