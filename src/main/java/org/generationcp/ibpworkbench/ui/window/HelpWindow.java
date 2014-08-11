package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.util.tomcat.TomcatUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

@Configurable
public class HelpWindow extends Window implements InitializingBean, InternationalizableComponent {

    private static final Logger LOG = LoggerFactory.getLogger(HelpWindow.class);

    private static final long serialVersionUID = 1L;


    // Components
    private ComponentContainer rootLayout;


    private static final String WINDOW_WIDTH = "640px";
    private static final String WINDOW_HEIGHT = "415px";

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    @Qualifier("workbenchProperties")
    private Properties workbenchProperties;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;


    @Autowired
    private TomcatUtil tomcatUtil;

    public HelpWindow() {
    }

    /**
     * Assemble the UI after all dependencies has been set.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }

    @SuppressWarnings("serial")
    protected void initializeLayout() {
        this.setWidth(WINDOW_WIDTH);
        this.setHeight(WINDOW_HEIGHT);
        this.setResizable(false);
        this.setModal(true);
        this.setCaption("BREEDING MANAGEMENT SYSTEM | WORKBENCH");
        this.setStyleName("gcp-help-window");


        rootLayout = this.getContent();

        Label version = new Label(workbenchProperties.getProperty("workbench.version", ""));
        version.setStyleName("gcp-version");
        rootLayout.addComponent(version);

        Panel panel = new Panel();
        panel.setWidth("600px");//fix for IE
        rootLayout.addComponent(panel);

        //detect if docs are installed 
        //if not, show a message prompting them to download and install it first
        WorkbenchSetting setting = null;
        try {
            setting = workbenchDataManager.getWorkbenchSetting();
        } catch (MiddlewareQueryException e) {
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }

        String installationDirectory = "";
        if (setting != null) {
            installationDirectory = setting.getInstallationDirectory();
        }

        final String docsDirectory = installationDirectory + File.separator + "Documents" + File.separator;
        File docsDiretoryFile = new File(docsDirectory);

        CustomLayout helpLayout = new CustomLayout("help");
        if (!docsDiretoryFile.exists() || !docsDiretoryFile.isDirectory()) {
            // if the document directory does not exist,
            // it means that the BMS Documentation has not been installed
            helpLayout = new CustomLayout("help_not_installed");
            panel.setContent(helpLayout);
            return;
        } else {
            panel.setContent(helpLayout);

            final String pdfFilename = "BMS_User_Manual.pdf";
            final String pdfFilepath = docsDirectory + pdfFilename;
            final String htmlDocUrl = "http://localhost:18080/BMS_HTML/index.html";


            String copyTargetPath = installationDirectory + File.separator + "infrastructure/tomcat/webapps/BMS_HTML";

            try {
                FileUtils.deleteDirectory(new File(copyTargetPath));
                FileUtils.copyDirectory(new File(getHtmlFilesLocation(docsDirectory)), new File(copyTargetPath));

                String contextPath = TomcatUtil.getContextPathFromUrl(htmlDocUrl);
                String localWarPath = TomcatUtil.getLocalWarPathFromUrl(htmlDocUrl);

                tomcatUtil.deployLocalWar(contextPath, localWarPath);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Link pdfLink = new Link() {
                @Override
                public void attach() {
                    super.attach();

                    StreamSource pdfSource = new StreamResource.StreamSource() {
                        public InputStream getStream() {
                            try {
                                File f = new File(pdfFilepath);
                                FileInputStream fis = new FileInputStream(f);
                                return fis;
                            } catch (Exception e) {
                                e.printStackTrace();
                                LOG.error(e.getMessage());
                                return null;
                            }
                        }
                    };
                    StreamResource pdfResource = new StreamResource(pdfSource,
                            pdfFilename, getApplication());
                    pdfResource.getStream().setParameter(
                            "Content-Disposition", "attachment;filename=\"" + pdfFilename + "\"");
                    pdfResource.setMIMEType("application/pdf");
                    pdfResource.setCacheTime(0);
                    setResource(pdfResource);
                }
            };
            pdfLink.setCaption("BMS Manual PDF Version");
            pdfLink.setTargetName("_blank");
            pdfLink.setIcon(new ThemeResource("../gcp-default/images/pdf_icon.png"));
            pdfLink.addStyleName("gcp-pdf-link");
            helpLayout.addComponent(pdfLink, "pdf_link");

            Link htmlLink = new Link() {

            };
            htmlLink.setResource(new ExternalResource(htmlDocUrl));
            htmlLink.setCaption("BMS Manual HTML Version");
            htmlLink.setTargetName("_blank");
            htmlLink.setIcon(new ThemeResource("../gcp-default/images/html_icon.png"));
            htmlLink.addStyleName("gcp-html-link");
            helpLayout.addComponent(htmlLink, "html_link");
        }


    }

    protected void assemble() throws Exception {
        initializeLayout();
    }

    @Override
    public void attach() {
        super.attach();
    }

    @Override
    public void updateLabels() {

    }

    private String getHtmlFilesLocation(String baseDir) {

        Collection<File> files = FileUtils.listFiles(new File(baseDir), new RegexFileFilter("index.html$"),
                DirectoryFileFilter.DIRECTORY);


        if (files.size() == 0) return "";

        for (File f : files) {
            return f.getParent();
        }

        return "";

    }

}
