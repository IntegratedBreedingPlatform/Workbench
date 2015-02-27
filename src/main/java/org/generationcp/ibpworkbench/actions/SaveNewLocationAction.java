/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.form.AddLocationForm;
import org.generationcp.ibpworkbench.ui.programlocations.AddLocationsWindow;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.ibpworkbench.ui.window.ConfirmLocationsWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Location;
import org.owasp.html.Sanitizers;
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

	private static final Logger LOG = LoggerFactory.getLogger(SaveNewLocationAction.class);
	private static final long serialVersionUID = 1L;

	private AddLocationForm newLocationForm;

	private AddLocationsWindow window;

	private ProgramLocationsPresenter programLocationsPresenter;

	@Resource
	private SessionData sessionData;

	@Resource
	private SimpleResourceBundleMessageSource messageSource;

	public SaveNewLocationAction(AddLocationForm newLocationForm, AddLocationsWindow window,
			ProgramLocationsPresenter programLocationsPresenter) {
		this.newLocationForm = newLocationForm;
		this.window = window;
		this.programLocationsPresenter = programLocationsPresenter;
	}

	@Override
	public void buttonClick(ClickEvent event) {

		try {
			newLocationForm.commit();
			LocationViewModel location = getLocationFromForm();
			List<Location> existingLocations = programLocationsPresenter
					.getExistingLocations(location.getLocationName());

			// there exists a location with the same name?
			if (existingLocations.size() > 0) {
				new ConfirmLocationsWindow(window, existingLocations, programLocationsPresenter,
						new Button.ClickListener() {

							private static final long serialVersionUID = 1L;

							@Override
							public void buttonClick(ClickEvent event) {
								saveLocation();
							}
						}).show();

			} else {
				saveLocation();
			}

		} catch (Validator.InvalidValueException e) {
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(),
					e.getLocalizedMessage());
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected void saveLocation() {
		final LocationViewModel locModel = this.getLocationFromForm();

		updateSessionData(locModel);

		// save to middleware
		try {
			Location loc = programLocationsPresenter.convertLocationViewToLocation(locModel);
			programLocationsPresenter.addLocation(loc);
			sessionData.logProgramActivity(messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK),
					"Added new Location (" + locModel.getLocationName() + ")");
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}

		window.getParent().removeWindow(window);

	}

	// FIXME: depricated for BMS-4.0 (merge-db), remove this when we replace sessionData obj.
	protected void updateSessionData(LocationViewModel locModel) {
		// increment key from the session's list of locations (correct id from local db)
		Integer nextKey = sessionData.getProjectLocationData().keySet().size() + 1;
		nextKey = nextKey * -1;
		locModel.setLocationId(nextKey);

		// add new location to session list
		sessionData.getProjectLocationData().put(nextKey, locModel);
	}

	protected LocationViewModel getLocationFromForm() {
		@SuppressWarnings("unchecked")
		BeanItem<LocationViewModel> locationBean = (BeanItem<LocationViewModel>) newLocationForm
				.getItemDataSource();
		LocationViewModel locModel = locationBean.getBean();

		// sanitize locModel
		locModel.setLocationName(Sanitizers.FORMATTING.sanitize(locModel.getLocationName()));
		locModel.setLocationAbbreviation(
				Sanitizers.FORMATTING.sanitize(locModel.getLocationAbbreviation()));

		return locModel;
	}
}
