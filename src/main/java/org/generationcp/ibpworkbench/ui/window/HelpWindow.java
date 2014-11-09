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
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
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
public class HelpWindow extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

    private static final Logger LOG = LoggerFactory.getLogger(HelpWindow.class);
    private static final long serialVersionUID = 1L;
    private static final String PDF_FILE_NAME = "BMS_User_Manual.pdf";
    private static final String HTML_DOC_URL = "http://localhost:18080/BMS_HTML/index.html";
    private static final String BMS_INSTALLATION_DIR_POSTFIX = "infrastructure/tomcat/webapps/";
    private static final String BMS_HTML = "BMS_HTML";

    // Components
    private ComponentContainer rootLayout;

    private static final String WINDOW_WIDTH = "640px";
    private static final String WINDOW_HEIGHT = "415px";

	
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
        //fix for IE
        panel.setWidth("600px");
        rootLayout.addComponent(panel);

        //detect if docs are installed 
        //if not, show a message prompting them to download and install it first
        String installationDirectory = getInstallationDirectory();
        
        if (!isDocumentsFolderFound(installationDirectory)) {
            // if the document directory does not exist,
            // it means that the BMS Documentation has not been installed
        	CustomLayout helpLayout = new CustomLayout("help_not_installed");
            panel.setContent(helpLayout);
            return;
        } else {
        	CustomLayout helpLayout = new CustomLayout("help");
            panel.setContent(helpLayout);
            
            deployDocumentsToTomcat(installationDirectory);
            
            String targetPDFPath = installationDirectory + File.separator + 
            		BMS_INSTALLATION_DIR_POSTFIX + PDF_FILE_NAME;
            Link pdfLink = buildPDFLink(targetPDFPath);
            helpLayout.addComponent(pdfLink, "pdf_link");

            Link htmlLink = buildHTMLLink();
            helpLayout.addComponent(htmlLink, "html_link");
        }
    }
    
    private boolean isDocumentsFolderFound(String installationDirectory) {
    	String docsDirectory = installationDirectory + File.separator + 
    			"Documents" + File.separator;
        File docsDirectoryFile = new File(docsDirectory);
        if (docsDirectoryFile.exists() && docsDirectoryFile.isDirectory()) {
        	return true;
        }
		return false;
	}

	private void deployDocumentsToTomcat(String installationDirectory) {
		String docsDirectory = installationDirectory + File.separator + 
				"Documents" + File.separator;
        String pdfFilepath = docsDirectory + PDF_FILE_NAME;
        String targetHTMLPath = installationDirectory + File.separator + 
        		BMS_INSTALLATION_DIR_POSTFIX + BMS_HTML;
        String targetPDFPath = installationDirectory + File.separator + 
        		BMS_INSTALLATION_DIR_POSTFIX;
    	try {
    		FileUtils.deleteDirectory(new File(targetHTMLPath));
    		FileUtils.copyDirectory(new File(getHtmlFilesLocation(docsDirectory)), 
            		new File(targetHTMLPath));
            FileUtils.copyFile(new File(pdfFilepath), 
            		new File(targetPDFPath+File.separator+PDF_FILE_NAME));
            String contextPath = TomcatUtil.getContextPathFromUrl(HTML_DOC_URL);
            String localWarPath = TomcatUtil.getLocalWarPathFromUrl(HTML_DOC_URL);
            tomcatUtil.deployLocalWar(contextPath, localWarPath);
        } catch (IOException e) {
        	LOG.error(e.getMessage(),e);
        }
	}

	private Link buildHTMLLink() {
    	Link htmlLink = new Link();
        htmlLink.setResource(new ExternalResource(HTML_DOC_URL));
        htmlLink.setCaption("BMS Manual HTML Version");
        htmlLink.setTargetName("_blank");
        htmlLink.setIcon(new ThemeResource("../gcp-default/images/html_icon.png"));
        htmlLink.addStyleName("gcp-html-link");
        return htmlLink;
	}

	private Link buildPDFLink(String targetPDFPath) {
    	Link pdfLink = new PDFLink(targetPDFPath);
        pdfLink.setCaption("BMS Manual PDF Version");
        pdfLink.setTargetName("_blank");
        pdfLink.setIcon(new ThemeResource("../gcp-default/images/pdf_icon.png"));
        pdfLink.addStyleName("gcp-pdf-link");
        return pdfLink;
	}

	private String getInstallationDirectory() {
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
        return installationDirectory;
	}

	class PDFLink extends Link {
    	
		private static final long serialVersionUID = -6907837759113491854L;
		private String pdfFilepath;
		
		public PDFLink(String pdfFilepath) {
			this.pdfFilepath = pdfFilepath;
		}
		
		@Override
        public void attach() {
            super.attach();
            StreamSource pdfSource = new StreamResource.StreamSource() {
                private static final long serialVersionUID = -8955404385754536528L;
				public InputStream getStream() {
                    try {
                        File f = new File(pdfFilepath);
                        return new FileInputStream(f);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(),e);
                        return null;
                    }
                }
            };
            StreamResource pdfResource = new StreamResource(pdfSource,
            		PDF_FILE_NAME, getApplication());
            pdfResource.getStream().setParameter(
                    "Content-Disposition", "attachment;filename=\"" + PDF_FILE_NAME + "\"");
            pdfResource.setMIMEType("application/pdf");
            pdfResource.setCacheTime(0);
            setResource(pdfResource);
        }
    }

    protected void assemble() {
        initializeLayout();
    }

    @Override
    public void updateLabels() {
    	//not implemented
    }

    private String getHtmlFilesLocation(String baseDir) {
		File baseDirFile = new File(baseDir);
        Collection<File> files = FileUtils.listFiles(baseDirFile, new RegexFileFilter("index.html$"),
                DirectoryFileFilter.DIRECTORY);

        if (files.isEmpty()) {
            return "";
        }

        for (File f : files) {
        	File parentFile = f.getParentFile();
        	if(parentFile!=null && parentFile.getParent().equals(
        			baseDirFile.getPath())) {
        		return f.getParent();
        	}
        }

        return "";

    }

}
