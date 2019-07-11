package org.generationcp.ibpworkbench.ui.programmembers;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleSelectionWindow extends BaseSubWindow {

	public static final String REQUIRED_LABEL_FORMAT = "<b>%s</b> <span style='color: red'>*</span>";

	private static final long serialVersionUID = 3983198771242295731L;

	private Button cancelButton;

	private Button selectButton;

	private Component buttonArea;

	private VerticalLayout layout;

	private ProgramMembersPanel programMembersPanel;

	private ComboBox rolesComboBox;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;


	public RoleSelectionWindow(final ProgramMembersPanel programMembersPanel, final Button.ClickListener clickSelectButtonListener) {
		this.programMembersPanel = programMembersPanel;
		this.assemble();
		this.getSelectButton().addListener(clickSelectButtonListener);
	}

	protected void initializeComponents() {
		final BeanContainer<String, Role> userRoleBeanContainer = new BeanContainer<>( Role.class);

		userRoleBeanContainer.setBeanIdProperty("id");
		userRoleBeanContainer.addAll(this.getProgramMembersPanel().getRoles());
		this.setRolesComboBox(new ComboBox());
		this.getRolesComboBox().setInputPrompt("Please select a role");
		this.getRolesComboBox().setDebugId("role");
		this.getRolesComboBox().setWidth("200px");
		this.getRolesComboBox().setContainerDataSource(userRoleBeanContainer);
		this.getRolesComboBox().setItemCaptionMode( AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		this.getRolesComboBox().setItemCaptionPropertyId("name");
		this.getRolesComboBox().setNullSelectionAllowed(false);
	}

	protected Label createLabel(final String caption) {
		final Label label = new Label();
		label.setDebugId("label");
		label.setContentMode(Label.CONTENT_XHTML);
		label.setWidth("100px");
		label.setValue(String.format(REQUIRED_LABEL_FORMAT, caption));
		return label;

	}

	protected void initializeLayout() {
		this.buttonArea = this.layoutButtonArea();
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setModal(true);
		this.setWidth("600px");
		this.setResizable(false);
		this.center();
		this.setCaption("Assign role");

		this.layout = new VerticalLayout();
		this.layout.setDebugId("AddRoleWindow_layout");
		this.layout.setWidth("100%");
		this.layout.setHeight("250px");

		final Panel panel = new Panel();
		panel.setDebugId("RoleSelectionWindow");
		panel.setStyleName("form-panel");
		panel.setSizeFull();

		final HorizontalLayout hLayoutLabelRoleComboBox = new HorizontalLayout();

		hLayoutLabelRoleComboBox.addComponent(new Label("Select a Program role for the user(s) selected in order to access current program:"));
		hLayoutLabelRoleComboBox.setHeight("50px");
		hLayoutLabelRoleComboBox.setSpacing(true);

		final HorizontalLayout hLayoutRolesComboBox = new HorizontalLayout();
		hLayoutRolesComboBox.setDebugId("RoleSelectionWindow_vl");
		final Label comboLabel = this.createLabel("Role Name: ");
		hLayoutRolesComboBox.addComponent(comboLabel);
		hLayoutRolesComboBox.addComponent(this.getRolesComboBox());
		hLayoutRolesComboBox.setExpandRatio(this.getRolesComboBox(), 1.0F);
		hLayoutRolesComboBox.setComponentAlignment(this.getRolesComboBox(), Alignment.MIDDLE_CENTER);
		final HorizontalLayout hLayoutLabelMandatory = new HorizontalLayout();
		hLayoutLabelMandatory.addComponent(
			new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>", Label.CONTENT_XHTML));
		hLayoutLabelMandatory.setHeight("30px");
		hLayoutLabelMandatory.setSpacing(true);

		panel.addComponent(hLayoutLabelRoleComboBox);
		panel.addComponent(hLayoutRolesComboBox);
		this.layout.addComponent(panel);
		this.layout.addComponent(this.buttonArea);

		this.layout.setExpandRatio(panel, 1.0F);
		this.layout.setComponentAlignment(this.buttonArea, Alignment.MIDDLE_CENTER);

		this.layout.setSpacing(true);
		this.layout.setMargin(true);

		this.setContent(this.layout);
	}

	protected void initializeActions() {
	}

	protected Component layoutButtonArea() {
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("RoleSelectionPopup_buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.setCancelButton(new Button("Cancel"));
		this.getCancelButton().setDebugId("cancelButton");
		this.getCancelButton().addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6449306339821569356L;

			@Override
			public void buttonClick(final Button.ClickEvent clickEvent) {
				RoleSelectionWindow.this.getParent().removeWindow(RoleSelectionWindow.this);
			}
		});

		this.setSelectButton(new Button("Assign role"));
		this.getSelectButton().setDebugId("selectButton");
		this.getSelectButton().addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		buttonLayout.addComponent(this.getCancelButton());
		buttonLayout.addComponent(this.getSelectButton());

		return buttonLayout;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	public ComboBox getRolesComboBox() {
		return rolesComboBox;
	}

	public void setRolesComboBox(ComboBox rolesComboBox) {
		this.rolesComboBox = rolesComboBox;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(Button cancelButton) {
		this.cancelButton = cancelButton;
	}

	public Button getSelectButton() {
		return selectButton;
	}

	public void setSelectButton(Button selectButton) {
		this.selectButton = selectButton;
	}

	public void setProgramMembersPanel(final ProgramMembersPanel programMembersPanel) {
		this.programMembersPanel = programMembersPanel;
	}

	public ProgramMembersPanel getProgramMembersPanel() {
		return programMembersPanel;
	}
}
