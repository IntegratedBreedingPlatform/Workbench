package org.generationcp.browser.study.listeners;

import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.ShortcutAction;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

@Configurable
public class ViewStudyDetailsButtonClickListener implements ClickListener {

	private static final long serialVersionUID = -2009510049166285893L;
	private static final Logger LOG = LoggerFactory.getLogger(ViewStudyDetailsButtonClickListener.class);
	public static final String STUDY_BROWSER_LINK = 
			"http://localhost:18080/GermplasmStudyBrowser/main/study-";
	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
	private int studyId;
	private String studyName;

	
	public ViewStudyDetailsButtonClickListener(int studyId, String studyName) {
		super();
		this.studyId = studyId;
		this.studyName = studyName;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Tool tool = null;
        try {
            tool = workbenchDataManager.getToolWithName(ToolName.study_browser.toString());
        } catch (MiddlewareQueryException qe) {
            LOG.error("QueryException", qe);
        }
        
        String addtlParams = getAdditionalParams();
        
        ExternalResource studyLink = null;
        if (tool == null) {
            studyLink = new ExternalResource(STUDY_BROWSER_LINK + studyId + "?restartApplication"+
            		addtlParams);
        } else {
            studyLink = new ExternalResource(tool.getPath().replace("study/", "study-") + studyId + "?restartApplication"+
            		addtlParams);
        }
        LOG.debug(studyLink.getURL());
        renderStudyDetailsWindow(studyLink, event.getComponent().getWindow());

	}
	
	protected void renderStudyDetailsWindow(ExternalResource studyLink, Window window){
		String windowTitle = "Study Information: " + studyName;
        final Window studyWindow = new BaseSubWindow(windowTitle);
        final Embedded studyInfo = new Embedded(null, studyLink);
        studyInfo.setType(Embedded.TYPE_BROWSER);
        studyInfo.setSizeFull();
        
        AbsoluteLayout layoutForStudy = new AbsoluteLayout();
        layoutForStudy.setMargin(false);
        layoutForStudy.setWidth("100%");
        layoutForStudy.setHeight("100%");
        layoutForStudy.addStyleName("no-caption");
        layoutForStudy.addComponent(studyInfo, "top:0; left:0;");
        
        studyWindow.setContent(layoutForStudy);
        studyWindow.setWidth("90%");
        studyWindow.setHeight("90%");
        studyWindow.center();
        studyWindow.setResizable(false);
        studyWindow.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);

        studyWindow.setModal(true);
        studyWindow.addStyleName("graybg");
        
        window.addWindow(studyWindow);
	}
	
	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager){
		this.workbenchDataManager = workbenchDataManager;
	}
	
	protected String getAdditionalParams(){
		return ToolUtil.getWorkbenchContextParameters();
	}
	

}
