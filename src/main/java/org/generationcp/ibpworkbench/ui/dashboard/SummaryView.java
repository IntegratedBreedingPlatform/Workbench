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
        headerArea.setComponentAlignment(toolsPopup,Alignment.MIDDLE_RIGHT);


        this.addComponent(headerArea);
        this.addComponent(tblActivity);

        this.setWidth("100%");

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
                toolsDropDown.getSelectedItem();
            }
        });

        tblActivity = buildActivityTable();

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

    private class ToolsDropDown implements PopupView.Content {
        private Button[] choices;
        private Integer selectedItem = 0;
        private final VerticalLayout root = new VerticalLayout();
        public ToolsDropDown(String... selections) {
            choices = new Button[selections.length];

            for (int i = 0; i < selections.length; i++) {
                choices[i] = new Button(selections[i]);
                choices[i].setStyleName(Reindeer.BUTTON_LINK);
                choices[i].addListener(new ChoiceListener(i));

                root.addComponent(choices[i]);
            }

            root.setSizeUndefined();
            root.setSpacing(true);
            root.setMargin(true);

        }

        @Override
        public String getMinimizedValueAsHTML() {
            return "tools";
        }

        @Override
        public Component getPopupComponent() {
            return root;
        }

        public Integer getSelectedItem() {
            return selectedItem;
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
