package org.generationcp.ibpworkbench.ui.dashboard.summaryview;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 12/13/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class SummaryView extends VerticalLayout implements InitializingBean {

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
        initializeComponents();
        initializeActions();
        initializeValues();
        initializeLayout();
    }

    private void initializeLayout() {
        final HorizontalLayout headerArea = new HorizontalLayout();
        headerArea.setSizeUndefined();
        headerArea.setWidth("100%");

        final Embedded headerImg = new Embedded(null,new ThemeResource("images/recent-activity.png"));
        headerImg.setStyleName("header-img");

        final HorizontalLayout headerTitleWrap = new HorizontalLayout();
        headerTitleWrap.setSizeUndefined();
        headerTitleWrap.setSpacing(true);

        headerTitleWrap.addComponent(headerImg);
        headerTitleWrap.addComponent(header);

        headerArea.addComponent(headerTitleWrap);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSizeUndefined();

        buttonLayout.addComponent(exportBtn);
        buttonLayout.addComponent(toolsPopup);
        buttonLayout.setSpacing(true);

        headerArea.addComponent(buttonLayout);
        headerArea.setComponentAlignment(buttonLayout,Alignment.MIDDLE_RIGHT);
        headerArea.setComponentAlignment(headerTitleWrap,Alignment.BOTTOM_LEFT);
        headerArea.setMargin(true,false,false,false);

        this.addComponent(headerArea);
        this.addComponent(tblSeason);

        // set initial header
        this.updateHeaderAndTableControls(messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL),0,tblSeason);
        exportBtn.setEnabled(false);


        this.setWidth("100%");
        this.setSpacing(false);

    }

    private void initializeValues() {
    	//do nothing
    }

    private void initializeActions() {
    	//do nothing
    }

    private void initializeComponents() {
        header = new Label(messageSource.getMessage(Message.ACTIVITIES));
        header.setStyleName(Bootstrap.Typography.H2.styleName());

        final ToolsDropDown toolsDropDown = new ToolsDropDown(
                messageSource.getMessage(Message.PROGRAM_SUMMARY_ACTIVITIES)
                ,messageSource.getMessage(Message.PROGRAM_SUMMARY_TRIALS)
                ,messageSource.getMessage(Message.PROGRAM_SUMMARY_NURSERY)
                ,messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL));

        toolsPopup = new PopupView(toolsDropDown);
        toolsPopup.setStyleName("btn-dropdown");
        toolsPopup.setHideOnMouseOut(false);
        toolsPopup.addListener(new PopupView.PopupVisibilityListener() {
            @Override
            public void popupVisibilityChange(PopupView.PopupVisibilityEvent event) {
                if (!event.isPopupVisible()) {
                    int selection = toolsDropDown.getSelectedItem();

                    if (selection >= 0) {
                        try {
                            SummaryView.this.removeComponent(SummaryView.this.getComponent(1));
                        } catch (IndexOutOfBoundsException e) {
                        	LOG.debug(e.getMessage(),e);
                        }

                    } else {
                        return;
                    }

                    switch (selection) {
                        case 0:
                            SummaryView.this.addComponent(tblActivity,1);
                            SummaryView.this.updateHeaderAndTableControls(messageSource.getMessage(Message.PROGRAM_SUMMARY_ACTIVITIES),activityCount,tblActivity);
                            break;
                        case 1:
                            SummaryView.this.addComponent(tblTrial,1);
                            SummaryView.this.updateHeaderAndTableControls(messageSource.getMessage(Message.PROGRAM_SUMMARY_TRIALS),trialCount,tblTrial);
                            break;
                        case 2:
                            SummaryView.this.addComponent(tblNursery,1);
                            SummaryView.this.updateHeaderAndTableControls(messageSource.getMessage(Message.PROGRAM_SUMMARY_NURSERY),nurseryCount,tblNursery);
                            break;
                        case 3:
                            SummaryView.this.addComponent(tblSeason,1);
                            SummaryView.this.updateHeaderAndTableControls(messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL),seasonCount,tblSeason);
                            break;
                        default: break;
                    }

                }
            }
        });

        exportBtn = new Button("<span class='glyphicon glyphicon-export' style='right: 4px'></span>EXPORT");
        exportBtn.setHtmlContentAllowed(true);
        exportBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String tableName = SummaryView.this.header.getValue().toString().split("\\[")[0].trim();
                String programName = sessionData.getSelectedProject().getProjectName();

                ExcelExport export = new ExcelExport((Table) SummaryView.this.getComponent(1),tableName);
                export.setReportTitle(programName + " - " + tableName);
                export.setExportFileName((tableName + " " + programName + ".xls").replaceAll(" ","_"));
                export.setDisplayTotals(false);

                SummaryView.LOG.info("Exporting " + tableName + ": " + export.getExportFileName() + ".xls will be downloaded in a moment.");
                export.export();


            }
        });

        tblActivity = buildActivityTable();
        tblTrial = buildTrialSummaryTable();
        tblNursery = buildNurserySummaryTable();
        tblSeason = buildSeasonSummaryTable();

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

        messageSource.setColumnHeader(activityTable, "createdAt", Message.DATE);
        messageSource.setColumnHeader(activityTable, "name", Message.NAME);
        messageSource.setColumnHeader(activityTable, "description", Message.DESCRIPTION_HEADER);

        return activityTable;
    }

    private PagedTable buildTrialSummaryTable() {
        final PagedTable trialSummaryTable = new PagedTableWithUpdatedControls();
        trialSummaryTable.setImmediate(true);

        
        BeanContainer<Integer, StudyDetails> container = new BeanContainer<Integer, StudyDetails>(StudyDetails.class);
        container.setBeanIdProperty("id");
        trialSummaryTable.setContainerDataSource(container);

        String[] columns = getTblTrialColumns();
        trialSummaryTable.setVisibleColumns(columns);

        // LAYOUT
        trialSummaryTable.setWidth("100%");

        messageSource.setColumnHeader(trialSummaryTable, columns[0], Message.NAME_LABEL);
        messageSource.setColumnHeader(trialSummaryTable, columns[1], Message.TITLE_LABEL);
        messageSource.setColumnHeader(trialSummaryTable, columns[2], Message.OBJECTIVE_LABEL);
        messageSource.setColumnHeader(trialSummaryTable, columns[3], Message.START_DATE_LABEL);
        messageSource.setColumnHeader(trialSummaryTable, columns[4], Message.END_DATE_LABEL);
        messageSource.setColumnHeader(trialSummaryTable, columns[5], Message.PI_NAME_LABEL);
        messageSource.setColumnHeader(trialSummaryTable, columns[6], Message.SITE_NAME_LABEL);
       
        return trialSummaryTable;
    }

    private PagedTable buildNurserySummaryTable() {
        final PagedTable nurserySummaryTable = new PagedTableWithUpdatedControls();
        nurserySummaryTable.setImmediate(true);

        BeanContainer<Integer, StudyDetails> container = new BeanContainer<Integer, StudyDetails>(StudyDetails.class);
        container.setBeanIdProperty("id");
        nurserySummaryTable.setContainerDataSource(container);

        String[] columns = getTblNurseryColumns();
        nurserySummaryTable.setVisibleColumns(columns);
        // LAYOUT
        nurserySummaryTable.setWidth("100%");

        messageSource.setColumnHeader(nurserySummaryTable, columns[0], Message.NAME_LABEL);
        messageSource.setColumnHeader(nurserySummaryTable, columns[1], Message.TITLE_LABEL);
        messageSource.setColumnHeader(nurserySummaryTable, columns[2], Message.OBJECTIVE_LABEL);
        messageSource.setColumnHeader(nurserySummaryTable, columns[3], Message.START_DATE_LABEL);
        messageSource.setColumnHeader(nurserySummaryTable, columns[4], Message.END_DATE_LABEL);
        messageSource.setColumnHeader(nurserySummaryTable, columns[5], Message.PI_NAME_LABEL);
        messageSource.setColumnHeader(nurserySummaryTable, columns[6], Message.SITE_NAME_LABEL);
        
        return nurserySummaryTable;
    }

    private PagedTable buildSeasonSummaryTable() {
        final PagedTable seasonSummaryTable = new PagedTableWithUpdatedControls();
        seasonSummaryTable.setImmediate(true);

        BeanContainer<Integer, StudyDetails> container = new BeanContainer<Integer, StudyDetails>(StudyDetails.class);
        container.setBeanIdProperty("id");
        seasonSummaryTable.setContainerDataSource(container);

        String[] columns = getTblSeasonColumns();
        seasonSummaryTable.setVisibleColumns(columns);
        // LAYOUT
        seasonSummaryTable.setWidth("100%");

        messageSource.setColumnHeader(seasonSummaryTable, columns[0], Message.NAME_LABEL);
        messageSource.setColumnHeader(seasonSummaryTable, columns[1], Message.TITLE_LABEL);
        messageSource.setColumnHeader(seasonSummaryTable, columns[2], Message.OBJECTIVE_LABEL);
        messageSource.setColumnHeader(seasonSummaryTable, columns[3], Message.START_DATE_LABEL);
        messageSource.setColumnHeader(seasonSummaryTable, columns[4], Message.END_DATE_LABEL);
        messageSource.setColumnHeader(seasonSummaryTable, columns[5], Message.PI_NAME_LABEL);
        messageSource.setColumnHeader(seasonSummaryTable, columns[6], Message.SITE_NAME_LABEL);
        messageSource.setColumnHeader(seasonSummaryTable, columns[7], Message.STUDY_TYPE_LABEL);
        
        return seasonSummaryTable;
    }

    public void updateActivityTable(List<ProjectActivity> activityList) {
        Object[] oldColumns = tblActivity.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);

        BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
        container.setBeanIdProperty("projectActivityId");
        tblActivity.setContainerDataSource(container);

        for (ProjectActivity activity : activityList) {
            container.addBean(activity);
        }

        activityCount = activityList.size();


        tblActivity.setContainerDataSource(container);
        tblActivity.setVisibleColumns(columns);

        // add controls
        updateHeaderAndTableControls(messageSource.getMessage(Message.PROGRAM_SUMMARY_ACTIVITIES),activityCount,tblActivity);
    }

    public void updateTrialSummaryTable(StudyDetailsQueryFactory factory) {
    	LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);
    	String[] columns = getTblTrialColumns();

        for (String columnId : columns) {
        	container.addContainerProperty(columnId, String.class, null);
        }
        
        container.getQueryView().getItem(0);
        
        trialCount = factory.getNumberOfItems();
        
        tblTrial.setContainerDataSource(container);
        tblTrial.setVisibleColumns(columns);
        tblTrial.setImmediate(true);
        // add controls
        updateHeaderAndTableControls(messageSource.getMessage(Message.PROGRAM_SUMMARY_TRIALS),trialCount,tblTrial);

    }

    public void updateNurserySummaryTable(StudyDetailsQueryFactory factory) {
    	LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);
    	String[] columns = getTblNurseryColumns();

        for (String columnId : columns) {
        	container.addContainerProperty(columnId, String.class, null);
        }
        
        container.getQueryView().getItem(0);
        
        nurseryCount = factory.getNumberOfItems();
        
        tblNursery.setContainerDataSource(container);
        tblNursery.setVisibleColumns(columns);
        tblNursery.setImmediate(true);
        // add controls
        updateHeaderAndTableControls(messageSource.getMessage(Message.PROGRAM_SUMMARY_NURSERY),nurseryCount,tblNursery);


    }

    public void updateSeasonSummaryTable(StudyDetailsQueryFactory factory) {
    	LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);
    	String[] columns = getTblSeasonColumns();

        for (String columnId : columns) {
        	container.addContainerProperty(columnId, String.class, null);
        }
        
        container.getQueryView().getItem(0);
        
        seasonCount = factory.getNumberOfItems();
        
        tblSeason.setContainerDataSource(container);
        tblSeason.setVisibleColumns(columns);
        tblSeason.setImmediate(true);
        // add controls
        updateHeaderAndTableControls(messageSource.getMessage(Message.PROGRAM_SUMMARY_ALL),seasonCount,tblSeason);
    }

    private class ToolsDropDown implements PopupView.Content {
        private Button[] choices;
        private Integer selectedItem = -1;
        private final VerticalLayout root = new VerticalLayout();
        public ToolsDropDown(String... selections) {
            choices = new Button[selections.length];

            for (int i = 0; i < selections.length; i++) {
                choices[i] = new Button(selections[i]);
                choices[i].setStyleName(Reindeer.BUTTON_LINK);
                choices[i].addListener(new ChoiceListener(i));
                choices[i].setWidth("100%"); 
                choices[i].setHeight("26px");
                root.addComponent(choices[i]);
            }

            root.setSizeUndefined();
            root.setWidth("200px");

        }

        @Override
        public String getMinimizedValueAsHTML() {
            return "<span class='glyphicon glyphicon-cog' style='right: 6px; top: 2px; font-size: 13px; font-weight: 300'></span>Actions";
        }

        @Override
        public Component getPopupComponent() {
            return root;
        }

        public Integer getSelectedItem() {
            int value = selectedItem;

            // reset the selected item
            selectedItem = -1;

            return value;
        }

        private class ChoiceListener implements Button.ClickListener {

            private int choice;

            public ChoiceListener(int choice) {
                this.choice = choice;
            }

            @Override
            public void buttonClick(Button.ClickEvent event) {
                selectedItem = choice;
                toolsPopup.setPopupVisible(false);
            }
        }
    }

    private void updateHeaderAndTableControls(String label,int count,PagedTable table) {
        if (this.getComponent(1).equals(table)) {

            if (count > 0) {
                header.setValue(label + " [" + count + "]");
            } else {
                header.setValue(label);
            }

            if (this.getComponentCount() > 2) {
                SummaryView.this.replaceComponent(this.getComponent(2), table.createControls());
            } else if (this.getComponentCount() == 2) {
                SummaryView.this.addComponent(table.createControls());
            }


        }

        if (sessionData.getSelectedProject() != null) {
            exportBtn.setEnabled(true);
        }

        table.setPageLength(10);
    }

	public String[] getTblTrialColumns() {
		return new String[] {STUDY_NAME,TITLE,OBJECTIVE,START_DATE,END_DATE,PI_NAME,SITE_NAME};
	}
    
	public String[] getTblNurseryColumns() {
		return new String[] {STUDY_NAME,TITLE,OBJECTIVE,START_DATE,END_DATE,PI_NAME,SITE_NAME};
	}
	
	public String[] getTblSeasonColumns() {
		return new String[] {STUDY_NAME,TITLE,OBJECTIVE,START_DATE,END_DATE,PI_NAME,SITE_NAME,STUDY_TYPE};
	}

    private class PagedTableWithUpdatedControls extends PagedTable {

        public PagedTableWithUpdatedControls() {
            this.setHeight("270px");
        }

        @Override
        public HorizontalLayout createControls() {
            HorizontalLayout controls = super.createControls();

            controls.setMargin(new MarginInfo(true,false,true,false));

            Iterator<Component> iterator= controls.getComponentIterator();

            while (iterator.hasNext()) {
                Component c = iterator.next();
                if (c instanceof HorizontalLayout) {
                    Iterator<Component> iterator2 = ((HorizontalLayout)c).getComponentIterator();

                    while (iterator2.hasNext()) {
                        Component d = iterator2.next();

                        if (d instanceof Button) {
                            d.setStyleName("");
                        }
                        if (d instanceof TextField) {
                            ((TextField)d).setWidth("30px");
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
