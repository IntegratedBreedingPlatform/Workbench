package org.generationcp.ibpworkbench.ui.common;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


/**
 * Created by cyrus on 9/10/14.
 */
@Configurable
public class InputPopup extends BaseSubWindow implements InitializingBean {
    private Label label;
    private TextField field;
    private String windowName, defaultFieldValue, fieldName;
    private Button okBtn;
    private Button cancelBtn;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;


    public InputPopup(String windowName, String fieldName,String defaultFieldValue) {
        this.windowName = windowName;
        this.fieldName = fieldName;
        this.defaultFieldValue = defaultFieldValue;
        this.setOverrideFocus(true);
    }

    @Override
    public void afterPropertiesSet() {
        try {
            assemble();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    private void initializeComponents() {
        label = new Label();

        if (fieldName != null) {
            label.setValue(fieldName);
        }

        field = new TextField();

        if (defaultFieldValue != null) {
            field.setValue(defaultFieldValue);
        }

        field.setCursorPosition(field.getValue() == null ? 0 : field.getValue().toString().length());
        field.focus();

        okBtn = new Button(messageSource.getMessage(Message.OK));
        okBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        cancelBtn = new Button(messageSource.getMessage(Message.CANCEL));

    }

    private void initializeLayout() {
        this.setCaption(windowName);

        // set defaults, this can be modified in the implementing class
        this.setModal(true);
        this.setWidth("300px");
        this.setHeight("150px");
        this.setResizable(false);

        label.setValue(messageSource.getMessage(Message.ITEM_NAME));

        final HorizontalLayout formContainer = new HorizontalLayout();
        formContainer.setSpacing(true);

        formContainer.addComponent(label);
        formContainer.addComponent(field);

        final HorizontalLayout btnContainer = new HorizontalLayout();
        btnContainer.setSpacing(true);
        btnContainer.setWidth("100%");

        final Label spacer = new Label("");
        btnContainer.addComponent(spacer);
        btnContainer.setExpandRatio(spacer, 1.0F);


        btnContainer.addComponent(okBtn);
        btnContainer.addComponent(cancelBtn);

        final VerticalLayout rootContainer = new VerticalLayout();

        rootContainer.setSpacing(true);
        rootContainer.setMargin(true);

        rootContainer.addComponent(formContainer);
        rootContainer.addComponent(btnContainer);

        this.setContent(rootContainer);
    }

    protected void initializeActions() {
        // cancelBtn
        cancelBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                clickEvent.getComponent().getWindow().getParent().removeWindow(InputPopup.this);
            }
        });
    }

    public void clearFieldVal() {
        field.setValue("");
        field.focus();
        field.setCursorPosition(field.getValue() == null ? 0 : field.getValue().toString().length());

    }

    public void setOkListener(Button.ClickListener listener) {
        okBtn.addListener(listener);
    }

    public String getFieldVal() {
        return field.getValue().toString();
    }

    @Override
    public void attach() {
        super.attach();
        field.focus();
    }
}
