package org.generationcp.ibpworkbench.ui.programadministration;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.ibpworkbench.ui.project.create.UpdateProjectPanel;
import org.generationcp.ibpworkbench.ui.summaryview.ProgramSummaryView;
import org.generationcp.ibpworkbench.ui.systemlabel.SystemLabelView;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

@Configurable
public class ProgramAdministrationPanel extends Panel implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramAdministrationPanel.class);

	private static final long serialVersionUID = 1L;

	private HorizontalLayout titleLayout;

	@Autowired
	private ContextUtil contextUtil;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Value("${workbench.is.single.user.only}")
	private String isSingleUserOnly;

	private VerticalLayout rootLayout;

	private TabSheet tabSheet;

	// TABS
	private UpdateProjectPanel basicDetailsPanel;
	private ProgramMembersPanel programMembersPanel;
	private ProgramLocationsView programLocationsView;
	private ProgramMethodsView programMethodsView;
	private SystemLabelView systemLabelPanel;
	private ProgramSummaryView programSummaryView;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
	}

	void initializeComponents() {
		this.rootLayout = new VerticalLayout();
		this.rootLayout.setDebugId("rootLayout");
		this.tabSheet = this.generateTabSheet();

		this.basicDetailsPanel = new UpdateProjectPanel();
		this.basicDetailsPanel.setDebugId("updateProjectPanel");

		final Project project = this.contextUtil.getProjectInContext();

		this.programMembersPanel = new ProgramMembersPanel(project);
		this.programMembersPanel.setDebugId("programMembersPanel");

		this.programLocationsView = new ProgramLocationsView(project);
		this.programLocationsView.setDebugId("programLocationsView");

		this.programMethodsView = new ProgramMethodsView(project);
		this.programMethodsView.setDebugId("programMethodsView");

		this.systemLabelPanel = new SystemLabelView();
		this.systemLabelPanel.setDebugId("systemLabelPanel");

		this.programSummaryView = new ProgramSummaryView();
		this.programSummaryView.setDebugId("programSummaryPanel");

	}

	void initializeLayout() {

		this.setTitleContent();

		final Label headingDesc = new Label(this.messageSource.getMessage(Message.PROGRAM_MODIFY_DETAILS));
		headingDesc.setDebugId("headingDesc");

		this.rootLayout.setMargin(new Layout.MarginInfo(false, true, true, true));
		this.rootLayout.setWidth("100%");
		this.rootLayout.setSpacing(true);

		// Basic Details tab
		this.tabSheet.addTab(this.basicDetailsPanel);
		this.tabSheet.getTab(this.basicDetailsPanel).setClosable(false);
		this.tabSheet.getTab(this.basicDetailsPanel).setCaption(this.messageSource.getMessage(Message.BASIC_DETAILS_LABEL));

		// Program Members tab - only for admin users
		try {
			this.addProgramMembersTab(this.tabSheet, this.programMembersPanel);
		} catch (final AccessDeniedException e) {
			// Do not do anything as the screen should be displayed, just this tab shouldn't appear for non-admins
			LOG.debug(e.getMessage(), e);
		}

		// Locations tab
		this.tabSheet.addTab(this.programLocationsView);
		this.tabSheet.getTab(this.programLocationsView).setClosable(false);
		this.tabSheet.getTab(this.programLocationsView).setCaption(this.messageSource.getMessage(Message.PROGRAM_LOCATIONS));

		// Breeding Methods tab
		this.tabSheet.addTab(this.programMethodsView);
		this.tabSheet.getTab(this.programMethodsView).setClosable(false);
		this.tabSheet.getTab(this.programMethodsView).setCaption(this.messageSource.getMessage(Message.BREEDING_METHODS_LABEL));

		// System Labels tab
		try {
			this.addSystemLabelsTab();
		} catch (final AccessDeniedException e) {
			// Do not do anything as the screen should be displayed, just this tab shouldn't appear for non-admins
			LOG.debug(e.getMessage(), e);
		}

		// Program Summary tab
		this.tabSheet.addTab(this.programSummaryView);
		this.tabSheet.getTab(this.programSummaryView).setClosable(false);
		this.tabSheet.getTab(this.programSummaryView).setCaption(this.messageSource.getMessage("PROGRAM_SUMMARY"));

		this.rootLayout.addComponent(this.titleLayout);
		this.rootLayout.addComponent(headingDesc);
		this.rootLayout.addComponent(this.tabSheet);

		this.setContent(this.rootLayout);
		this.setScrollable(true);
		this.setSizeFull();
	}

	private void setTitleContent() {
		this.titleLayout = new HorizontalLayout();
		this.titleLayout.setDebugId("titleLayout");
		this.titleLayout.setSpacing(true);

		final Label toolTitle = new Label(this.messageSource.getMessage(Message.PROGRAM_MANAGE_SETTINGS));
		toolTitle.setDebugId("toolTitle");
		toolTitle.setContentMode(Label.CONTENT_XHTML);
		toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		toolTitle.setWidth("376px");

		this.titleLayout.addComponent(toolTitle);
		this.titleLayout.addComponent(new HelpButton(HelpModule.MANAGE_PROGRAM_SETTINGS_MANAGE_PROGRAM_SETTING,
				this.messageSource.getMessage(Message.PROGRAM_GO_TO_TUTORIAL)));
	}

	protected TabSheet generateTabSheet() {
		final TabSheet tab = new TabSheet();
		tab.setDebugId("tab");

		tab.setImmediate(true);
		tab.setStyleName(Reindeer.TABSHEET_MINIMAL);
		tab.setStyleName("panel-border");

		return tab;
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CROP_MANAGEMENT','ROLE_MANAGE_PROGRAMS', 'ROLE_ADD_PROGRAM', 'ROLE_MANAGE_PROGRAM_SETTINGS')")
	protected void addProgramMembersTab(final TabSheet tabSheet, final ProgramMembersPanel programMembersPanel) {

		// Do not display the Program Members tab if BMS is in single user mode.
		if (!Boolean.parseBoolean(getIsSingleUserOnly())) {
			tabSheet.addTab(programMembersPanel);
			tabSheet.getTab(programMembersPanel).setClosable(false);
			tabSheet.getTab(programMembersPanel).setCaption(this.messageSource.getMessage(Message.PROGRAM_MEMBERS));
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CROP_MANAGEMENT')")
	private void addSystemLabelsTab() {
		this.tabSheet.addTab(this.systemLabelPanel);
		this.tabSheet.getTab(this.systemLabelPanel).setClosable(false);
		this.tabSheet.getTab(this.systemLabelPanel).setCaption(this.messageSource.getMessage("SYSTEM_LABELS"));
	}

	// For Test purposes only
	public TabSheet getTabSheet() {
		return this.tabSheet;
	}

	protected String getIsSingleUserOnly() {
		return isSingleUserOnly;
	}

	protected void setIsSingleUserOnly(String isSingleUserOnly) {
		this.isSingleUserOnly = isSingleUserOnly;
	}
}
