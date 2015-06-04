
package org.generationcp.ibpworkbench.ui.dashboard.summaryview;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.generationcp.browser.study.containers.StudyDetailsQueryFactory;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 12/13/13 Time: 3:34 PM To change this template use File | Settings | File Templates.
 */
@Configurable
public class SummaryView extends VerticalLayout implements InitializingBean {

	/**
	 *
	 */
	private static final long serialVersionUID = 7007366368354834359L;

	private static final Logger LOG = LoggerFactory.getLogger(SummaryView.class);

	private static final String STUDY_NAME = "studyName";

	private static final String TITLE = "title";

	private static final String OBJECTIVE = "objective";

	private static final String START_DATE = "startDate";

	private static final String END_DATE = "endDate";

	private static final String PI_NAME = "piName";

	private static final String SITE_NAME = "siteName";

	private static final String STUDY_TYPE = "studyType";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	private Label header;
	private PopupView toolsPopup;

	private PagedTable tblActivity;
	private PagedTable tblTrial;
	private PagedTable tblNursery;
	private PagedTable tblSeason;

	private int activityCount = 0;
	private int trialCount = 0;
	private int nurseryCount = 0;
	private int seasonCount = 0;
	private Button exportBtn;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeComponents();
		this.initializeActions();
		this.initializeValues();
		this.initializeLayout();
	}

	private void initializeLayout() {
		final HorizontalLayout headerArea = new HorizontalLayout();
		headerArea.setSizeUndefined();
		headerArea.setWidth("100%");

		final Embedded headerImg = new Embedded(null, new ThemeResource("images/recent-activity.png"));
		headerImg.setStyleName("header-img");

		final HorizontalLayout headerTitleWrap = new HorizontalLayout();
		headerTitleWrap.setSizeUndefined();
		headerTitleWrap.setSpacing(true);

		headerTitleWrap.addComponent(headerImg);
		headerTitleWrap.addComponent(this.header);

		headerArea.addComponent(headerTitleWrap);

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSizeUndefined();

		buttonLayout.addComponent(this.exportBtn);
		buttonLayout.addComponent(this.toolsPopup);
		buttonLayout.setSpacing(true);

		headerArea.addComponent(buttonLayout);
		headerArea.setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
		headerArea.setComponentAlignment(headerTitleWrap, Alignment.BOTTOM_LEFT);
		headerArea.setMargin(true, false, false, false);

		this.addComponent(headerArea);
		this.addComponent(this.tblSeason);

		// set initial header
		this.updateHeaderAndTableControls(this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL), 0, this.tblSeason);
		this.exportBtn.setEnabled(false);

		this.setWidth("100%");
		this.setSpacing(false);

	}

	private void initializeValues() {
		// do nothing
	}

	private void initializeActions() {
		// do nothing
	}

	private void initializeComponents() {
		this.header = new Label(this.messageSource.getMessage(Message.ACTIVITIES));
		this.header.setStyleName(Bootstrap.Typography.H2.styleName());

		final ToolsDropDown toolsDropDown =
				new ToolsDropDown(this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ACTIVITIES),
						this.messageSource.getMessage(Message.PROGRAM_SUMMARY_TRIALS),
						this.messageSource.getMessage(Message.PROGRAM_SUMMARY_NURSERY),
						this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL));

		this.toolsPopup = new PopupView(toolsDropDown);
		this.toolsPopup.setStyleName("btn-dropdown");
		this.toolsPopup.setHideOnMouseOut(false);
		this.toolsPopup.addListener(new PopupView.PopupVisibilityListener() {

			@Override
			public void popupVisibilityChange(PopupView.PopupVisibilityEvent event) {
				if (!event.isPopupVisible()) {
					int selection = toolsDropDown.getSelectedItem();

					if (selection >= 0) {
						try {
							SummaryView.this.removeComponent(SummaryView.this.getComponent(1));
						} catch (IndexOutOfBoundsException e) {
							SummaryView.LOG.debug(e.getMessage(), e);
						}

					} else {
						return;
					}

					switch (selection) {
						case 0:
							SummaryView.this.addComponent(SummaryView.this.tblActivity, 1);
							SummaryView.this.updateHeaderAndTableControls(
									SummaryView.this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ACTIVITIES),
									SummaryView.this.activityCount, SummaryView.this.tblActivity);
							break;
						case 1:
							SummaryView.this.addComponent(SummaryView.this.tblTrial, 1);
							SummaryView.this.updateHeaderAndTableControls(
									SummaryView.this.messageSource.getMessage(Message.PROGRAM_SUMMARY_TRIALS), SummaryView.this.trialCount,
									SummaryView.this.tblTrial);
							break;
						case 2:
							SummaryView.this.addComponent(SummaryView.this.tblNursery, 1);
							SummaryView.this.updateHeaderAndTableControls(
									SummaryView.this.messageSource.getMessage(Message.PROGRAM_SUMMARY_NURSERY),
									SummaryView.this.nurseryCount, SummaryView.this.tblNursery);
							break;
						case 3:
							SummaryView.this.addComponent(SummaryView.this.tblSeason, 1);
							SummaryView.this.updateHeaderAndTableControls(
									SummaryView.this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL), SummaryView.this.seasonCount,
									SummaryView.this.tblSeason);
							break;
						default:
							break;
					}

				}
			}
		});

		this.exportBtn = new Button("<span class='glyphicon glyphicon-export' style='right: 4px'></span>EXPORT");
		this.exportBtn.setHtmlContentAllowed(true);
		this.exportBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				String tableName = SummaryView.this.header.getValue().toString().split("\\[")[0].trim();
				String programName = SummaryView.this.sessionData.getSelectedProject().getProjectName();

				ExcelExport export = new ExcelExport((Table) SummaryView.this.getComponent(1), tableName);
				export.setReportTitle(programName + " - " + tableName);
				export.setExportFileName((tableName + " " + programName + ".xls").replaceAll(" ", "_"));
				export.setDisplayTotals(false);

				SummaryView.LOG.info("Exporting " + tableName + ": " + export.getExportFileName() + ".xls will be downloaded in a moment.");
				export.export();

			}
		});

		this.tblActivity = this.buildActivityTable();
		this.tblTrial = this.buildTrialSummaryTable();
		this.tblNursery = this.buildNurserySummaryTable();
		this.tblSeason = this.buildSeasonSummaryTable();

	}

	private PagedTable buildActivityTable() {
		final PagedTable activityTable = new PagedTableWithUpdatedControls();
		activityTable.setImmediate(true);

		BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
		container.setBeanIdProperty("projectActivityId");
		activityTable.setContainerDataSource(container);

		String[] columns = new String[] {"createdAt", "name", "description"};
		activityTable.setVisibleColumns(columns);

		// LAYOUT
		activityTable.setWidth("100%");

		this.messageSource.setColumnHeader(activityTable, "createdAt", Message.DATE);
		this.messageSource.setColumnHeader(activityTable, "name", Message.NAME);
		this.messageSource.setColumnHeader(activityTable, "description", Message.DESCRIPTION_HEADER);

		return activityTable;
	}

	private PagedTable buildTrialSummaryTable() {
		final PagedTable trialSummaryTable = new PagedTableWithUpdatedControls();
		trialSummaryTable.setImmediate(true);

		BeanContainer<Integer, StudyDetails> container = new BeanContainer<Integer, StudyDetails>(StudyDetails.class);
		container.setBeanIdProperty("id");
		trialSummaryTable.setContainerDataSource(container);

		String[] columns = this.getTblTrialColumns();
		trialSummaryTable.setVisibleColumns(columns);

		// LAYOUT
		trialSummaryTable.setWidth("100%");

		this.messageSource.setColumnHeader(trialSummaryTable, columns[0], Message.NAME_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[1], Message.TITLE_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[2], Message.OBJECTIVE_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[3], Message.START_DATE_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[4], Message.END_DATE_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[5], Message.PI_NAME_LABEL);
		this.messageSource.setColumnHeader(trialSummaryTable, columns[6], Message.SITE_NAME_LABEL);

		return trialSummaryTable;
	}

	private PagedTable buildNurserySummaryTable() {
		final PagedTable nurserySummaryTable = new PagedTableWithUpdatedControls();
		nurserySummaryTable.setImmediate(true);

		BeanContainer<Integer, StudyDetails> container = new BeanContainer<Integer, StudyDetails>(StudyDetails.class);
		container.setBeanIdProperty("id");
		nurserySummaryTable.setContainerDataSource(container);

		String[] columns = this.getTblNurseryColumns();
		nurserySummaryTable.setVisibleColumns(columns);
		// LAYOUT
		nurserySummaryTable.setWidth("100%");

		this.messageSource.setColumnHeader(nurserySummaryTable, columns[0], Message.NAME_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[1], Message.TITLE_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[2], Message.OBJECTIVE_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[3], Message.START_DATE_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[4], Message.END_DATE_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[5], Message.PI_NAME_LABEL);
		this.messageSource.setColumnHeader(nurserySummaryTable, columns[6], Message.SITE_NAME_LABEL);

		return nurserySummaryTable;
	}

	private PagedTable buildSeasonSummaryTable() {
		final PagedTable seasonSummaryTable = new PagedTableWithUpdatedControls();
		seasonSummaryTable.setImmediate(true);

		BeanContainer<Integer, StudyDetails> container = new BeanContainer<Integer, StudyDetails>(StudyDetails.class);
		container.setBeanIdProperty("id");
		seasonSummaryTable.setContainerDataSource(container);

		String[] columns = this.getTblSeasonColumns();
		seasonSummaryTable.setVisibleColumns(columns);
		// LAYOUT
		seasonSummaryTable.setWidth("100%");

		this.messageSource.setColumnHeader(seasonSummaryTable, columns[0], Message.NAME_LABEL);
		this.messageSource.setColumnHeader(seasonSummaryTable, columns[1], Message.TITLE_LABEL);
		this.messageSource.setColumnHeader(seasonSummaryTable, columns[2], Message.OBJECTIVE_LABEL);
		this.messageSource.setColumnHeader(seasonSummaryTable, columns[3], Message.START_DATE_LABEL);
		this.messageSource.setColumnHeader(seasonSummaryTable, columns[4], Message.END_DATE_LABEL);
		this.messageSource.setColumnHeader(seasonSummaryTable, columns[5], Message.PI_NAME_LABEL);
		this.messageSource.setColumnHeader(seasonSummaryTable, columns[6], Message.SITE_NAME_LABEL);
		this.messageSource.setColumnHeader(seasonSummaryTable, columns[7], Message.STUDY_TYPE_LABEL);

		return seasonSummaryTable;
	}

	public void updateActivityTable(List<ProjectActivity> activityList) {
		Object[] oldColumns = this.tblActivity.getVisibleColumns();
		String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);

		BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
		container.setBeanIdProperty("projectActivityId");
		this.tblActivity.setContainerDataSource(container);

		for (ProjectActivity activity : activityList) {
			container.addBean(activity);
		}

		this.activityCount = activityList.size();

		this.tblActivity.setContainerDataSource(container);
		this.tblActivity.setVisibleColumns(columns);

		// add controls
		this.updateHeaderAndTableControls(this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ACTIVITIES), this.activityCount,
				this.tblActivity);
	}

	public void updateTrialSummaryTable(StudyDetailsQueryFactory factory) {
		LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);
		String[] columns = this.getTblTrialColumns();

		for (String columnId : columns) {
			container.addContainerProperty(columnId, String.class, null);
		}

		container.getQueryView().getItem(0);

		this.trialCount = factory.getNumberOfItems();

		this.tblTrial.setContainerDataSource(container);
		this.tblTrial.setVisibleColumns(columns);
		this.tblTrial.setImmediate(true);
		// add controls
		this.updateHeaderAndTableControls(this.messageSource.getMessage(Message.PROGRAM_SUMMARY_TRIALS), this.trialCount, this.tblTrial);

	}

	public void updateNurserySummaryTable(StudyDetailsQueryFactory factory) {
		LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);
		String[] columns = this.getTblNurseryColumns();

		for (String columnId : columns) {
			container.addContainerProperty(columnId, String.class, null);
		}

		container.getQueryView().getItem(0);

		this.nurseryCount = factory.getNumberOfItems();

		this.tblNursery.setContainerDataSource(container);
		this.tblNursery.setVisibleColumns(columns);
		this.tblNursery.setImmediate(true);
		// add controls
		this.updateHeaderAndTableControls(this.messageSource.getMessage(Message.PROGRAM_SUMMARY_NURSERY), this.nurseryCount,
				this.tblNursery);

	}

	public void updateSeasonSummaryTable(StudyDetailsQueryFactory factory) {
		LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);
		String[] columns = this.getTblSeasonColumns();

		for (String columnId : columns) {
			container.addContainerProperty(columnId, String.class, null);
		}

		container.getQueryView().getItem(0);

		this.seasonCount = factory.getNumberOfItems();

		this.tblSeason.setContainerDataSource(container);
		this.tblSeason.setVisibleColumns(columns);
		this.tblSeason.setImmediate(true);
		// add controls
		this.updateHeaderAndTableControls(this.messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL), this.seasonCount, this.tblSeason);
	}

	private class ToolsDropDown implements PopupView.Content {

		/**
		 *
		 */
		private static final long serialVersionUID = -6646207856427647783L;
		private final Button[] choices;
		private Integer selectedItem = -1;
		private final VerticalLayout root = new VerticalLayout();

		public ToolsDropDown(String... selections) {
			this.choices = new Button[selections.length];

			for (int i = 0; i < selections.length; i++) {
				this.choices[i] = new Button(selections[i]);
				this.choices[i].setStyleName(BaseTheme.BUTTON_LINK);
				this.choices[i].addListener(new ChoiceListener(i));
				this.choices[i].setWidth("100%");
				this.choices[i].setHeight("26px");
				this.root.addComponent(this.choices[i]);
			}

			this.root.setSizeUndefined();
			this.root.setWidth("200px");

		}

		@Override
		public String getMinimizedValueAsHTML() {
			return "<span class='glyphicon glyphicon-cog' style='right: 6px; top: 2px; font-size: 13px; font-weight: 300'></span>Actions";
		}

		@Override
		public Component getPopupComponent() {
			return this.root;
		}

		public Integer getSelectedItem() {
			int value = this.selectedItem;

			// reset the selected item
			this.selectedItem = -1;

			return value;
		}

		private class ChoiceListener implements Button.ClickListener {

			/**
			 *
			 */
			private static final long serialVersionUID = -1931124754200308585L;
			private final int choice;

			public ChoiceListener(int choice) {
				this.choice = choice;
			}

			@Override
			public void buttonClick(Button.ClickEvent event) {
				ToolsDropDown.this.selectedItem = this.choice;
				SummaryView.this.toolsPopup.setPopupVisible(false);
			}
		}
	}

	private void updateHeaderAndTableControls(String label, int count, PagedTable table) {
		if (this.getComponent(1).equals(table)) {

			if (count > 0) {
				this.header.setValue(label + " [" + count + "]");
			} else {
				this.header.setValue(label);
			}

			if (this.getComponentCount() > 2) {
				SummaryView.this.replaceComponent(this.getComponent(2), table.createControls());
			} else if (this.getComponentCount() == 2) {
				SummaryView.this.addComponent(table.createControls());
			}

		}

		if (this.sessionData.getSelectedProject() != null) {
			this.exportBtn.setEnabled(true);
		}

		table.setPageLength(10);
	}

	public String[] getTblTrialColumns() {
		return new String[] {SummaryView.STUDY_NAME, SummaryView.TITLE, SummaryView.OBJECTIVE, SummaryView.START_DATE,
				SummaryView.END_DATE, SummaryView.PI_NAME, SummaryView.SITE_NAME};
	}

	public String[] getTblNurseryColumns() {
		return new String[] {SummaryView.STUDY_NAME, SummaryView.TITLE, SummaryView.OBJECTIVE, SummaryView.START_DATE,
				SummaryView.END_DATE, SummaryView.PI_NAME, SummaryView.SITE_NAME};
	}

	public String[] getTblSeasonColumns() {
		return new String[] {SummaryView.STUDY_NAME, SummaryView.TITLE, SummaryView.OBJECTIVE, SummaryView.START_DATE,
				SummaryView.END_DATE, SummaryView.PI_NAME, SummaryView.SITE_NAME, SummaryView.STUDY_TYPE};
	}

	private class PagedTableWithUpdatedControls extends PagedTable {

		/**
		 *
		 */
		private static final long serialVersionUID = -3917644602215395122L;

		public PagedTableWithUpdatedControls() {
			this.setHeight("270px");
		}

		@Override
		public HorizontalLayout createControls() {
			HorizontalLayout controls = super.createControls();

			controls.setMargin(new MarginInfo(true, false, true, false));

			Iterator<Component> iterator = controls.getComponentIterator();

			while (iterator.hasNext()) {
				Component c = iterator.next();
				if (c instanceof HorizontalLayout) {
					Iterator<Component> iterator2 = ((HorizontalLayout) c).getComponentIterator();

					while (iterator2.hasNext()) {
						Component d = iterator2.next();

						if (d instanceof Button) {
							d.setStyleName("");
						}
						if (d instanceof TextField) {
							((TextField) d).setWidth("30px");
						}

					}
				}
			}
			return controls;
		}

		@Override
		protected String formatPropertyValue(Object rowId, Object colId, Property property) {
			if (property.getType() == Date.class) {
				SimpleDateFormat sdf = DateUtil.getSimpleDateFormat(DateUtil.FRONTEND_TIMESTAMP_FORMAT);
				return property.getValue() == null ? "" : sdf.format((Date) property.getValue());
			}

			return super.formatPropertyValue(rowId, colId, property);
		}

	}

}
