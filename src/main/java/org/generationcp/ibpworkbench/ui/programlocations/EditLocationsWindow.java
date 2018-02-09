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

package org.generationcp.ibpworkbench.ui.programlocations;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.SaveNewLocationAction;
import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.ibpworkbench.ui.form.LocationForm;
import org.generationcp.ibpworkbench.ui.window.ConfirmLocationsWindow;
import org.generationcp.middleware.pojos.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.util.List;

@Configurable
public class EditLocationsWindow extends BaseSubWindow {

	private static final long serialVersionUID = 3983198771242295731L;
	private static final Logger LOG = LoggerFactory.getLogger(EditLocationsWindow.class);

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private SimpleResourceBundleMessageSource messageSource;

	private LocationForm locationForm;

	private Button cancelButton;

	private Button saveLocationButton;

	private Component buttonArea;

	private VerticalLayout layout;

	private final ProgramLocationsPresenter programLocationsPresenter;

	private LocationViewModel locationToEdit;

	private Table sourceTable;

	public EditLocationsWindow(final LocationViewModel locationToEdit, final ProgramLocationsPresenter programLocationsPresenter,
			final Table sourceTable) {
		this.programLocationsPresenter = programLocationsPresenter;
		this.locationToEdit = locationToEdit;
		this.sourceTable = sourceTable;
		this.assemble();
	}

	protected void initializeComponents() {

		this.locationForm = new LocationForm(locationToEdit, this.programLocationsPresenter,
				new LocationFormFieldFactory(this.programLocationsPresenter));
		this.locationForm.setDebugId("locationForm");
		this.buttonArea = this.layoutButtonArea();

		this.addListener(new WindowOnFocusListener());
	}

	protected void initializeLayout() {
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setModal(true);
		this.setWidth("600px");
		this.setResizable(false);
		this.center();
		this.setCaption("Edit Location");

		this.layout = new VerticalLayout();
		this.layout.setDebugId("EditLocationsWindow_layout");
		this.layout.setWidth("100%");
		this.layout.setHeight("450px");

		final Panel p = new Panel();
		p.setDebugId("EditLocationsWindow_p");
		p.setStyleName("form-panel");
		p.setSizeFull();

		final VerticalLayout vl = new VerticalLayout();
		vl.setDebugId("EditLocationsWindow_vl");
		vl.addComponent(
				new Label("<i><span style='color:red; font-weight:bold'>*</span> indicates a mandatory field.</i>", Label.CONTENT_XHTML));
		vl.addComponent(this.locationForm);
		vl.setExpandRatio(this.locationForm, 1.0F);

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

		this.saveLocationButton.addListener(new UpdateLocationAction());
		this.cancelButton.addListener(new Button.ClickListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 6449306339821569356L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				EditLocationsWindow.this.getParent().removeWindow(EditLocationsWindow.this);
			}
		});

	}

	protected Component layoutButtonArea() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("addLocationForm_buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setDebugId("cancelButton");
		this.saveLocationButton = new Button("Save");
		this.saveLocationButton.setDebugId("saveLocationButton");
		this.saveLocationButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.saveLocationButton);

		return buttonLayout;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	class UpdateLocationAction implements Button.ClickListener {

		@Override
		public void buttonClick(final Button.ClickEvent clickEvent) {

			try {

				final List<Location> existingLocationsWithSameName = EditLocationsWindow.this.programLocationsPresenter
						.getExistingLocations(EditLocationsWindow.this.locationForm.getLocationNameValue());

				if (!existingLocationsWithSameName.isEmpty() && EditLocationsWindow.this.locationForm.isLocationNameModified()) {
					new ConfirmLocationsWindow(EditLocationsWindow.this, existingLocationsWithSameName,
							EditLocationsWindow.this.programLocationsPresenter, new Button.ClickListener() {

						private static final long serialVersionUID = 1L;

						@Override
						public void buttonClick(final Button.ClickEvent event) {
							UpdateLocationAction.this.saveLocation();
						}
					}).show();
				} else {
					this.saveLocation();
				}

			} catch (final Validator.InvalidValueException e) {
				LOG.warn(e.getMessage(), e);
				MessageNotifier.showRequiredFieldError(clickEvent.getComponent().getWindow(), e.getLocalizedMessage());
			}
		}

		void saveLocation() {

			EditLocationsWindow.this.locationForm.commit();

			final BeanItem<LocationViewModel> locationBean =
					(BeanItem<LocationViewModel>) EditLocationsWindow.this.locationForm.getItemDataSource();
			final LocationViewModel locationViewModel = locationBean.getBean();

			EditLocationsWindow.this.programLocationsPresenter
					.updateLocation(locationViewModel, ProgramLocationsView.AVAILABLE.equals(sourceTable.getData()));

			EditLocationsWindow.this.contextUtil
					.logProgramActivity(EditLocationsWindow.this.messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK),
							"Updated location (" + locationViewModel.getLocationName() + ")");

			EditLocationsWindow.this.getParent().removeWindow(EditLocationsWindow.this);

		}

	}


	class WindowOnFocusListener implements FieldEvents.FocusListener {

		@Override
		public void focus(final FieldEvents.FocusEvent focusEvent) {
			if (locationForm.isLocationUsedInOtherProgram()) {
				MessageNotifier.showWarning(focusEvent.getComponent().getWindow(), messageSource.getMessage(Message.WARNING),
						EditLocationsWindow.this.messageSource.getMessage(Message.LOCATION_IS_USED_IN_OTHER_PROGRAM));
			}

		}

	}

	public void setLocationForm(final LocationForm locationForm) {
		this.locationForm = locationForm;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

}
