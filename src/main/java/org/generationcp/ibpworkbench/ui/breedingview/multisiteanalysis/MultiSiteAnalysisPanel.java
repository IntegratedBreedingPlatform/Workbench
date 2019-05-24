/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.SelectStudyDialog;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Multisite analysis component
 *
 * @author Aldrich Abrogena
 */
@Configurable
public class MultiSiteAnalysisPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent, IBPWorkbenchLayout {

	private static final long serialVersionUID = 1L;

	private final Map<Integer, Table> studyTables = new HashMap<Integer, Table>();
	private TabSheet studiesTabsheet;
	private VerticalLayout tabSheetContainer;

	private Button browseLink;

	private HorizontalLayout titleLayout;
	private Label toolTitle;
	private HeaderLabelLayout heading;

	protected Boolean setAll = true;
	protected Boolean fromOthers = true;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private final Project project;

	private static final Logger LOG = LoggerFactory.getLogger(MultiSiteAnalysisPanel.class);

	public MultiSiteAnalysisPanel(final Project project) {
		MultiSiteAnalysisPanel.LOG.debug("Project is " + project.getProjectName());
		this.project = project;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();

		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setValue(this.toolTitle, Message.TITLE_GXE);
	}

	@Override
	public void instantiateComponents() {
		this.setTitleContent();

		final ThemeResource resource = new ThemeResource("../vaadin-retro/images/search-nurseries.png");
		final Label headingLabel = new Label("Select Data for Analysis");
		headingLabel.setDebugId("headingLabel");
		headingLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		headingLabel.addStyleName("label-bold");
		this.heading = new HeaderLabelLayout(resource, headingLabel);
		this.heading.setDebugId("heading");

		this.browseLink = new Button();
		this.browseLink.setDebugId("browseLink");
		this.browseLink.setImmediate(true);
		this.browseLink.setStyleName("link");
		this.browseLink.setCaption("Browse");
		this.browseLink.setWidth("48px");

		this.setStudiesTabsheet(this.generateTabSheet());

		this.tabSheetContainer = new VerticalLayout();
		this.tabSheetContainer.setDebugId("tabSheetContainer");
		this.tabSheetContainer.addComponent(this.getStudiesTabsheet());
		this.tabSheetContainer.setMargin(true, false, false, false);
	}

	private void setTitleContent() {
		this.titleLayout = new HorizontalLayout();
		this.titleLayout.setDebugId("titleLayout");
		this.titleLayout.setSpacing(true);

		this.toolTitle = new Label();
		this.toolTitle.setDebugId("toolTitle");
		this.toolTitle.setContentMode(Label.CONTENT_XHTML);
		this.toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		this.toolTitle.setHeight("26px");
		this.toolTitle.setWidth("278px");

		this.titleLayout.addComponent(this.toolTitle);
		this.titleLayout.addComponent(new HelpButton(HelpModule.MULTI_SITE_ANALYSIS, "Go to Multi-Site Analysis Tutorial"));
	}

	@Override
	public void initializeValues() {
		// no UI components needs values on this UI object creation
	}

	@Override
	public void addListeners() {
		this.browseLink.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(final ClickEvent event) {

				final SelectStudyDialog dialog = new SelectStudyDialog(event.getComponent().getWindow(), MultiSiteAnalysisPanel.this,
					MultiSiteAnalysisPanel.this.project);
				event.getComponent().getWindow().addWindow(dialog);
			}

		});
	}

	@Override
	public void layoutComponents() {
		this.setSpacing(true);
		this.setMargin(new MarginInfo(false, true, true, true));
		this.setSizeUndefined();
		this.setWidth("100%");

		final HorizontalLayout browseLabelLayout = new HorizontalLayout();
		browseLabelLayout.setDebugId("browseLabelLayout");
		browseLabelLayout.addComponent(this.browseLink);
		browseLabelLayout.addComponent(new Label("for a study to work with."));
		browseLabelLayout.setSizeUndefined();

		final VerticalLayout selectDataForAnalysisLayout = new VerticalLayout();
		selectDataForAnalysisLayout.setDebugId("selectDataForAnalysisLayout");
		selectDataForAnalysisLayout.addComponent(this.heading);
		selectDataForAnalysisLayout.addComponent(browseLabelLayout);

		this.addComponent(this.titleLayout);
		this.addComponent(selectDataForAnalysisLayout);
	}

	protected void repaintTab(final Component comp, final Study study) {

		if (comp != null) {
			final VerticalLayout container = (VerticalLayout) comp;
			container.setSpacing(true);
			container.setMargin(true, false, false, false);
			container.removeAllComponents();

			final Label tabTitle = new Label("&nbsp;&nbsp;" + "Adjusted means dataset", Label.CONTENT_XHTML);
			tabTitle.setDebugId("tabTitle");
			tabTitle.setStyleName(Bootstrap.Typography.H1.styleName());

			container.addComponent(tabTitle);

			container.addComponent(this.studyTables.get(study.getId()));
			container.setExpandRatio(this.studyTables.get(study.getId()), 1.0F);

			container.setSizeFull();
		}
	}

	public void generateTabContent(
		final Study study, final String selectedEnvFactorName, final String selectedGenotypeFactorName,
		final String selectedEnvGroupFactorName, final Map<String, Boolean> variatesCheckboxState,
		final MultiSiteAnalysisSelectPanel gxeSelectEnvironmentPanel) {

		if (selectedEnvFactorName == null || "".equals(selectedEnvFactorName)) {
			return;
		}

		final MultiSiteParameters multiSiteParameters = new MultiSiteParameters();
		multiSiteParameters.setSelectedEnvironmentFactorName(selectedEnvFactorName);
		multiSiteParameters.setSelectedGenotypeFactorName(selectedGenotypeFactorName);
		multiSiteParameters.setSelectedEnvGroupFactorName(selectedEnvGroupFactorName);
		multiSiteParameters.setProject(this.project);
		multiSiteParameters.setStudy(study);

		final MultiSiteAnalysisGxePanel tabContainer =
			new MultiSiteAnalysisGxePanel(this.getStudyDataManager(), gxeSelectEnvironmentPanel, variatesCheckboxState,
				multiSiteParameters);
		tabContainer.setVisible(true);

		this.getStudiesTabsheet().setVisible(true);
		this.getStudiesTabsheet().replaceComponent(this.getStudiesTabsheet().getSelectedTab(), tabContainer);
		this.getStudiesTabsheet().getTab(tabContainer).setClosable(true);
		this.getStudiesTabsheet().setSelectedTab(tabContainer);
	}

	public void openStudyMeansDataset(final Study study) {

		if (this.getComponentIndex(this.tabSheetContainer) == -1) {
			this.addComponent(this.tabSheetContainer);
		}

		for (final Iterator<Component> tabs = this.getStudiesTabsheet().getComponentIterator(); tabs.hasNext(); ) {
			final Component tab = tabs.next();
			final Study tabStudyData = (Study) ((VerticalLayout) tab).getData();
			if (tabStudyData.getId() == study.getId()) {
				this.studiesTabsheet.setSelectedTab(tab);
				return;
			}

		}

		try {

			if (study == null) {
				return;
			}
			MultiSiteAnalysisPanel.LOG.debug("selected from folder tree:" + study.toString());

			List<DataSet> dataSets = null;
			try {
				dataSets = this.getStudyDataManager().getDataSetsByType(study.getId(), DatasetTypeEnum.MEANS_DATA.getId());
			} catch (final MiddlewareException e) {
				MultiSiteAnalysisPanel.LOG.error("Error getting means dataset", e);
			}

			if (dataSets != null && study.getName() != null && !dataSets.isEmpty()) {

				final MultiSiteAnalysisSelectPanel selectEnvironmentPanel =
					new MultiSiteAnalysisSelectPanel(this.getStudyDataManager(), this.project, study, this);

				selectEnvironmentPanel.setCaption(study.getName());
				this.getStudiesTabsheet().addTab(selectEnvironmentPanel);
				this.getStudiesTabsheet().getTab(selectEnvironmentPanel).setClosable(true);

			} else {
				throw new MiddlewareQueryException("This study doesnt have an existing MEANS dataset.");
			}

		} catch (final NumberFormatException e) {
			MultiSiteAnalysisPanel.LOG.error(e.getMessage(), e);
		}

		this.requestRepaintAll();

	}

	protected TabSheet generateTabSheet() {
		final TabSheet tab = new TabSheet();
		tab.setDebugId("tab");

		tab.setImmediate(true);
		tab.setStyleName(Reindeer.TABSHEET_MINIMAL);
		tab.setStyleName("panel-border");
		tab.setHeight("100%");

		return tab;
	}

	public TabSheet getStudiesTabsheet() {
		return this.studiesTabsheet;
	}

	public void setStudiesTabsheet(final TabSheet studiesTabsheet) {
		this.studiesTabsheet = studiesTabsheet;
	}

	public StudyDataManager getStudyDataManager() {
		return this.studyDataManager;
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}
}
