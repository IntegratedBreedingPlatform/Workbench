
package org.generationcp.ibpworkbench.ui.programmethods;

	import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.actions.CancelBreedingMethodAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class AddBreedingMethodsWindow extends BaseSubWindow {

	private static final long serialVersionUID = 3983198771242295731L;

	private static final Logger LOG = LoggerFactory.getLogger(AddBreedingMethodsWindow.class);

	private MethodView methodView;

	private BreedingMethodForm breedingMethodForm;

	private Button cancelButton;

	private Button addBreedingMethodButton;

	private Component buttonArea;

	private VerticalLayout layout;

	private final ProgramMethodsView projectBreedingMethodsPanel;

	private final static String[] VISIBLE_ITEM_PROPERTIES = new String[] {"methodName", "methodDescription", "methodType", "methodCode"};

	@Resource
	private SimpleResourceBundleMessageSource messageSource;

	public AddBreedingMethodsWindow(ProgramMethodsView projectBreedingMethodsPanel) {
		this.projectBreedingMethodsPanel = projectBreedingMethodsPanel;
		this.assemble();
	}

	public AddBreedingMethodsWindow(ProgramMethodsView projectBreedingMethodsPanel, MethodView methodView) {
		this.projectBreedingMethodsPanel = projectBreedingMethodsPanel;
		this.methodView = methodView;
		this.assemble();
	}

	protected void initializeComponents() {

		if (this.methodView != null) {
			this.breedingMethodForm = new BreedingMethodForm(this.projectBreedingMethodsPanel.retrieveMethodClasses(), this.methodView);
		} else {
			this.breedingMethodForm = new BreedingMethodForm(this.projectBreedingMethodsPanel.retrieveMethodClasses());
		}

		this.cancelButton = new Button("Cancel");
		this.addBreedingMethodButton = new Button("Save");
		this.addBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.buttonArea = this.layoutButtonArea();
	}

	protected void initializeLayout() {
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setModal(true);
		this.setWidth("700px");
		this.setResizable(false);
		this.center();
		this.setCaption("Add Breeding Method");

		this.addStyleName(Reindeer.WINDOW_LIGHT);

		this.layout = new VerticalLayout();
		this.layout.setWidth("100%");
		this.layout.setHeight("450px");

		final Panel p = new Panel();
		p.setStyleName("form-panel");
		p.setSizeFull();

		final VerticalLayout vl = new VerticalLayout();
		vl.setSizeFull();
		vl.addComponent(new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>",
				Label.CONTENT_XHTML));
		vl.addComponent(this.breedingMethodForm);
		vl.setExpandRatio(this.breedingMethodForm, 1.0F);

		p.addComponent(vl);
		this.layout.addComponent(p);
		this.layout.addComponent(this.buttonArea);

		this.layout.setExpandRatio(p, 1.0F);
		this.layout.setComponentAlignment(this.buttonArea, Alignment.MIDDLE_CENTER);

		this.layout.setSpacing(true);
		this.layout.setMargin(true);

		this.setContent(this.layout);
	}

	protected void initializeActions() {

		this.addBreedingMethodButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -3013536199242402259L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				try {
					AddBreedingMethodsWindow.this.breedingMethodForm.commit();

					@SuppressWarnings("unchecked")
					MethodView bean = ((BeanItem<MethodView>) AddBreedingMethodsWindow.this.breedingMethodForm.getItemDataSource()).getBean();
					if (StringUtils.isEmpty(bean.getMtype())) {
						MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(),
								"Please select a Generation Advancement Type");
						return;
					}

					AddBreedingMethodsWindow.this.projectBreedingMethodsPanel.presenter.saveNewBreedingMethod(bean);

					AddBreedingMethodsWindow.this.getParent().removeWindow(AddBreedingMethodsWindow.this);

				} catch (Validator.InvalidValueException  e) {
					MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), e.getLocalizedMessage());

					LOG.warn(e.getMessage(),e);

				} catch (RuntimeException e) {
					MessageNotifier.showError(clickEvent.getComponent().getWindow(),messageSource.getMessage("DATABASE_ERROR"),messageSource.getMessage("CONTACT_ADMIN_ERROR_DESC"));
					LOG.error(e.getMessage(),e);
				}


			}
		});

		this.cancelButton.addListener(new CancelBreedingMethodAction(this));
	}

	protected Component layoutButtonArea() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button("Cancel");
		this.addBreedingMethodButton = new Button("Save");
		this.addBreedingMethodButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.addBreedingMethodButton);

		return buttonLayout;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	public void refreshVisibleItems() {
		this.breedingMethodForm.setVisibleItemProperties(AddBreedingMethodsWindow.VISIBLE_ITEM_PROPERTIES);
	}
}
