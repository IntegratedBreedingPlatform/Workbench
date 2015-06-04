
package org.generationcp.ibpworkbench.ui.common;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by cyrus on 9/10/14.
 */
@Configurable
public class InputPopup extends BaseSubWindow implements InitializingBean {

	private static final long serialVersionUID = -6445403140342610657L;
	private static final Logger LOG = LoggerFactory.getLogger(InputPopup.class);

	private Label label;
	private TextField field;
	private final String windowName, defaultFieldValue, fieldName;
	private Button okBtn;
	private Button cancelBtn;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public InputPopup(String windowName, String fieldName, String defaultFieldValue) {
		this.windowName = windowName;
		this.fieldName = fieldName;
		this.defaultFieldValue = defaultFieldValue;
		this.setOverrideFocus(true);
	}

	@Override
	public void afterPropertiesSet() {
		try {
			this.assemble();
		} catch (Exception e) {
			InputPopup.LOG.error(e.getMessage(), e);
		}
	}

	protected void assemble() throws Exception {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	private void initializeComponents() {
		this.label = new Label();

		if (this.fieldName != null) {
			this.label.setValue(this.fieldName);
		}

		this.field = new TextField();

		if (this.defaultFieldValue != null) {
			this.field.setValue(this.defaultFieldValue);
		}

		this.field.setCursorPosition(this.field.getValue() == null ? 0 : this.field.getValue().toString().length());
		this.field.focus();

		this.okBtn = new Button(this.messageSource.getMessage(Message.OK));
		this.okBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		this.okBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.cancelBtn = new Button(this.messageSource.getMessage(Message.CANCEL));

		// set debug ids
		this.field.setDebugId("vaadin-itemname-txt");
	}

	private void initializeLayout() {
		this.setCaption(this.windowName);

		// set defaults, this can be modified in the implementing class
		this.setModal(true);
		this.setWidth("300px");
		this.setHeight("150px");
		this.setResizable(false);

		this.label.setValue(this.messageSource.getMessage(Message.ITEM_NAME));

		final HorizontalLayout formContainer = new HorizontalLayout();
		formContainer.setSpacing(true);

		formContainer.addComponent(this.label);
		formContainer.addComponent(this.field);

		final HorizontalLayout btnContainer = new HorizontalLayout();
		btnContainer.setSpacing(true);
		btnContainer.setWidth("100%");

		final Label spacer = new Label("");
		btnContainer.addComponent(spacer);
		btnContainer.setExpandRatio(spacer, 1.0F);

		btnContainer.addComponent(this.okBtn);
		btnContainer.addComponent(this.cancelBtn);

		final VerticalLayout rootContainer = new VerticalLayout();

		rootContainer.setSpacing(true);
		rootContainer.setMargin(true);

		rootContainer.addComponent(formContainer);
		rootContainer.addComponent(btnContainer);

		this.setContent(rootContainer);
	}

	protected void initializeActions() {
		// cancelBtn
		this.cancelBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 7893112609017442016L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				clickEvent.getComponent().getWindow().getParent().removeWindow(InputPopup.this);
			}
		});
	}

	public void clearFieldVal() {
		this.field.setValue("");
		this.field.focus();
		this.field.setCursorPosition(this.field.getValue() == null ? 0 : this.field.getValue().toString().length());

	}

	public void setOkListener(Button.ClickListener listener) {
		this.okBtn.addListener(listener);
	}

	public String getFieldVal() {
		return this.field.getValue().toString();
	}

	@Override
	public void attach() {
		super.attach();
		this.field.focus();
	}
}
