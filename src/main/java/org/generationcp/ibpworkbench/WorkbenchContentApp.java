package org.generationcp.ibpworkbench;

import com.vaadin.ui.Window;
import org.dellroad.stuff.vaadin.ContextApplication;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.MultiSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.programadministration.ProgramAdministrationPanel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.ibpworkbench.ui.project.create.AddProgramView;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.annotation.Resource;

public class WorkbenchContentApp extends SpringContextApplication {

	private static final long serialVersionUID = -3098125752010885259L;
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchContentApp.class);

	@Resource
	private ContextUtil contextUtil;

	@Override
	public void close() {
		super.close();
	}

	@Override
	protected void initSpringApplication(final ConfigurableWebApplicationContext configurableWebApplicationContext) {
		this.setTheme("gcp-default");
		this.setMainWindow(new WorkbenchContentAppWindow());
	}

	@Override
	public Window getWindow(final String name) {

		final Window w = super.getWindow(name);

		if (super.getWindow(name) == null) {
			if (ToolName.BREEDING_VIEW.getName().equals(name)) {
				final Project project = this.contextUtil.getProjectInContext();
				final SingleSiteAnalysisPanel singleSiteAnalysis = new SingleSiteAnalysisPanel(project);
				singleSiteAnalysis.setDebugId("singleSiteAnalysisPanel");
				final WorkbenchContentAppWindow contentWindow = new WorkbenchContentAppWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(singleSiteAnalysis);
				return contentWindow;
			} else if (ToolName.BV_GXE.getName().equals(name)) {
				final Project project = this.contextUtil.getProjectInContext();
				final MultiSiteAnalysisPanel gxeAnalysisPanel = new MultiSiteAnalysisPanel(project);
				gxeAnalysisPanel.setDebugId("gxeAnalysisPanel");
				final WorkbenchContentAppWindow contentWindow = new WorkbenchContentAppWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(gxeAnalysisPanel);
				return contentWindow;
			} else if (ToolName.MANAGE_PROGRAMS.getName().equals(name)) {
				final ProgramAdministrationPanel projectPanel = new ProgramAdministrationPanel();
				projectPanel.setDebugId("projectPanel");
				final WorkbenchContentAppWindow contentWindow = new WorkbenchContentAppWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(projectPanel);
				return contentWindow;
			} else if (ToolName.CREATE_PROGRAMS.getName().equals(name)) {
				final AddProgramView createProjectPanel = new AddProgramView();
				createProjectPanel.setDebugId("createProjectPanel");
				final WorkbenchContentAppWindow contentWindow = new WorkbenchContentAppWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(createProjectPanel);
				return contentWindow;
			} else if (ToolName.METHOD_MANAGER.getName().equals(name)) {
				final ProgramMethodsView createProjectPanel = new ProgramMethodsView(this.contextUtil.getProjectInContext());
				createProjectPanel.setDebugId("methodsView");
				final WorkbenchContentAppWindow contentWindow = new WorkbenchContentAppWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(createProjectPanel);
				return contentWindow;
			} else if (ToolName.LOCATION_MANAGER.getName().equals(name)) {
				final ProgramLocationsView createProjectPanel = new ProgramLocationsView(this.contextUtil.getProjectInContext());
				createProjectPanel.setDebugId("locationsView");
				final WorkbenchContentAppWindow contentWindow = new WorkbenchContentAppWindow();
				this.addWindow(contentWindow);
				contentWindow.showContent(createProjectPanel);
				return contentWindow;
			}
		}

		return w;
	}

	@Override
	public void terminalError(final com.vaadin.terminal.Terminal.ErrorEvent event) {
		LOG.error("Encountered error", event.getThrowable());
	}

	public static WorkbenchContentApp get() {
		return ContextApplication.get(WorkbenchContentApp.class);
	}

}

