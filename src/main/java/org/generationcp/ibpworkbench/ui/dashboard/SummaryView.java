package org.generationcp.ibpworkbench.ui.dashboard;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    private Label header;
    private PopupView toolsPopup;

    private Table tblActivity;
    private Table tblTrial;
    private Table tblNursery;


    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            initializeComponents();
            initializeActions();
            initializeValues();
            initializeLayout();
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }
    }

    private void initializeLayout() {
        HorizontalLayout headerArea = new HorizontalLayout();
        headerArea.setSizeUndefined();
        headerArea.setWidth("100%");
        headerArea.addComponent(header);
        headerArea.addComponent(toolsPopup);
        headerArea.setComponentAlignment(header,Alignment.BOTTOM_LEFT);
        headerArea.setComponentAlignment(toolsPopup,Alignment.BOTTOM_RIGHT);


        this.addComponent(headerArea);
        this.addComponent(tblActivity);

        this.setWidth("100%");
        this.setSpacing(true);

    }

    private void initializeValues() throws MiddlewareQueryException {
    }

    private void initializeActions() {
    }

    private void initializeComponents() {
        header = new Label("Activity Summary");
        header.setStyleName(Bootstrap.Typography.H2.styleName());

        final ToolsDropDown toolsDropDown = new ToolsDropDown("Activities","Trial Summaries","Nursery Summaries");
        toolsPopup = new PopupView(toolsDropDown);
        toolsPopup.setStyleName("btn-dropdown");
        toolsPopup.setHideOnMouseOut(false);
        toolsPopup.addListener(new PopupView.PopupVisibilityListener() {
            @Override
            public void popupVisibilityChange(PopupView.PopupVisibilityEvent event) {
                if (!event.isPopupVisible()) {
                    int selection = toolsDropDown.getSelectedItem();

                    if (selection >= 0) {
                        SummaryView.this.removeComponent(tblActivity);
                        SummaryView.this.removeComponent(tblTrial);
                        SummaryView.this.removeComponent(tblNursery);
                    }

                    switch (selection) {
                        case 0:
                            SummaryView.this.addComponent(tblActivity);
                            break;
                        case 1:
                            SummaryView.this.addComponent(tblTrial);
                        case 2:
                            SummaryView.this.addComponent(tblNursery);
                            break;
                    }

                }
            }
        });

        tblActivity = buildActivityTable();
        tblTrial = buildTrialSummaryTable();
        tblNursery = buildNurserySummaryTable();

    }

    private Table buildActivityTable() {
        final Table tblActivity = new Table() {
            private static final long serialVersionUID = 1L;

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                if (property.getType() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    return property.getValue() == null ? "" : sdf.format((Date) property.getValue());
                }

                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        tblActivity.setImmediate(true);

        BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
        container.setBeanIdProperty("projectActivityId");
        tblActivity.setContainerDataSource(container);

        String[] columns = new String[] {"createdAt", "name", "description"};
        tblActivity.setVisibleColumns(columns);

        // LAYOUT
        tblActivity.setWidth("100%");

        messageSource.setColumnHeader(tblActivity, "createdAt", Message.DATE);
        messageSource.setColumnHeader(tblActivity, "name", Message.NAME);
        messageSource.setColumnHeader(tblActivity, "description", Message.DESCRIPTION_HEADER);

        return tblActivity;
    }

    private Table buildTrialSummaryTable() {
        final Table tblTrial = new Table() {
            private static final long serialVersionUID = 1L;

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                if (property.getType() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    return property.getValue() == null ? "" : sdf.format((Date) property.getValue());
                }

                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        tblTrial.setImmediate(true);

        /*
        BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
        container.setBeanIdProperty("projectActivityId");
        tblActivity.setContainerDataSource(container);

        String[] columns = new String[] {"createdAt", "name", "description"};
        tblActivity.setVisibleColumns(columns);

        // LAYOUT
        tblActivity.setWidth("100%");

        messageSource.setColumnHeader(tblActivity, "createdAt", Message.DATE);
        messageSource.setColumnHeader(tblActivity, "name", Message.NAME);
        messageSource.setColumnHeader(tblActivity, "description", Message.DESCRIPTION_HEADER);
        */
        return tblTrial;
    }

    private Table buildNurserySummaryTable() {
        final Table tblNursery = new Table() {
            private static final long serialVersionUID = 1L;

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                if (property.getType() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    return property.getValue() == null ? "" : sdf.format((Date) property.getValue());
                }

                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        tblNursery.setImmediate(true);

        /*
        BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
        container.setBeanIdProperty("projectActivityId");
        tblActivity.setContainerDataSource(container);

        String[] columns = new String[] {"createdAt", "name", "description"};
        tblActivity.setVisibleColumns(columns);

        // LAYOUT
        tblActivity.setWidth("100%");

        messageSource.setColumnHeader(tblActivity, "createdAt", Message.DATE);
        messageSource.setColumnHeader(tblActivity, "name", Message.NAME);
        messageSource.setColumnHeader(tblActivity, "description", Message.DESCRIPTION_HEADER);
        */
        return tblNursery;
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

        //TODO: update label
        //lblActivity.setValue(messageSource.getMessage(Message.ACTIVITIES) + " [" + activityList.size() + "]");

        tblActivity.setContainerDataSource(container);

        tblActivity.setVisibleColumns(columns);
    }

    public void updateTrialSummaryTable(List<Object> list) {

    }

    public void updateNurserySummmaryTable(List<Object> list) {

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
                choices[i].setWidth("100%"); choices[i].setHeight("26px");
                root.addComponent(choices[i]);
            }

            root.setSizeUndefined();
            root.setWidth("150px");
            //root.setMargin(new MarginInfo(true,false,true,false));

        }

        @Override
        public String getMinimizedValueAsHTML() {
            return "<span class='glyphicon glyphicon-cog' style='right: 6px; top: 2px; font-size: 13px; font-weight: 300'></span>TOOLS";
        }

        @Override
        public Component getPopupComponent() {
            return root;
        }

        public Integer getSelectedItem() {
            int value = selectedItem;

            selectedItem = -1;  // reset the selected item;

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
}
