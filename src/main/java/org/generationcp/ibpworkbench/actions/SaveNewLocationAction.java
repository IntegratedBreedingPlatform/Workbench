/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.form.LocationForm;
import org.generationcp.ibpworkbench.ui.programlocations.AddLocationsWindow;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.ibpworkbench.ui.window.ConfirmLocationsWindow;
import org.generationcp.middleware.pojos.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jeffrey Morales, Joyce Avestro
 */

@Configurable
public class SaveNewLocationAction implements ClickListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(SaveNewLocationAction.class);

	private final LocationForm newLocationForm;

	private final AddLocationsWindow window;

	private final ProgramLocationsPresenter programLocationsPresenter;

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private SimpleResourceBundleMessageSource messageSource;

	public SaveNewLocationAction(final LocationForm newLocationForm, final AddLocationsWindow window,
			final ProgramLocationsPresenter programLocationsPresenter) {
		this.newLocationForm = newLocationForm;
		this.window = window;
		this.programLocationsPresenter = programLocationsPresenter;
	}

	@Override
	public void buttonClick(final ClickEvent event) {

		try {
			this.newLocationForm.commit();
			final LocationViewModel location = this.getLocationFromForm();
			final List<Location> existingLocations = this.programLocationsPresenter.getExistingLocations(location.getLocationName());

			// there exists a location with the same name?
			if (!existingLocations.isEmpty()) {
				new ConfirmLocationsWindow(this.window, existingLocations, this.programLocationsPresenter, new Button.ClickListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(final ClickEvent event) {
						SaveNewLocationAction.this.saveLocation();
					}
				}).show();

			} else {
				this.saveLocation();
			}

		} catch (final Validator.InvalidValueException e) {
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getLocalizedMessage());
			LOG.warn(e.getMessage(), e);
		}
	}

	protected void saveLocation() {
		final LocationViewModel locModel = this.getLocationFromForm();
		final Location loc = this.programLocationsPresenter.convertLocationViewToLocation(locModel);
		this.programLocationsPresenter.addLocation(loc);
		this.contextUtil.logProgramActivity(this.messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK),
				"Added new Location (" + locModel.getLocationName() + ")");
		this.window.getParent().removeWindow(this.window);
	}

	protected LocationViewModel getLocationFromForm() {
		@SuppressWarnings("unchecked") final BeanItem<LocationViewModel> locationBean =
				(BeanItem<LocationViewModel>) this.newLocationForm.getItemDataSource();
		return locationBean.getBean();
	}

	void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}
}
