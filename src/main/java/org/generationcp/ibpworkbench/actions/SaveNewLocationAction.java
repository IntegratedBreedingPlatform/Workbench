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

package org.generationcp.ibpworkbench.actions;

import java.util.List;

import javax.annotation.Resource;

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

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * @author Jeffrey Morales, Joyce Avestro
 */

@Configurable
public class SaveNewLocationAction implements ClickListener {

	private static final Logger LOG = LoggerFactory.getLogger(SaveNewLocationAction.class);
	private static final long serialVersionUID = 1L;

	private final AddLocationForm newLocationForm;

	private final AddLocationsWindow window;

	private final ProgramLocationsPresenter programLocationsPresenter;

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
			this.newLocationForm.commit();
			LocationViewModel location = this.getLocationFromForm();
			List<Location> existingLocations = this.programLocationsPresenter.getExistingLocations(location.getLocationName());

			// there exists a location with the same name?
			if (!existingLocations.isEmpty()) {
				new ConfirmLocationsWindow(this.window, existingLocations, this.programLocationsPresenter, new Button.ClickListener() {

					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						SaveNewLocationAction.this.saveLocation();
					}
				}).show();

			} else {
				this.saveLocation();
			}

		} catch (Validator.InvalidValueException e) {
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getLocalizedMessage());
		} catch (MiddlewareQueryException e) {
			SaveNewLocationAction.LOG.error(e.getMessage(), e);
			SaveNewLocationAction.LOG.error(e.getMessage(), e);
			SaveNewLocationAction.LOG.error(e.getMessage(), e);
		}
	}

	protected void saveLocation() {
		final LocationViewModel locModel = this.getLocationFromForm();
		Location loc = this.programLocationsPresenter.convertLocationViewToLocation(locModel);
		this.programLocationsPresenter.addLocation(loc);
		this.sessionData.logProgramActivity(this.messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK), "Added new Location ("
				+ locModel.getLocationName() + ")");
		this.window.getParent().removeWindow(this.window);
	}

	protected LocationViewModel getLocationFromForm() {
		@SuppressWarnings("unchecked")
		BeanItem<LocationViewModel> locationBean = (BeanItem<LocationViewModel>) this.newLocationForm.getItemDataSource();
		LocationViewModel locModel = locationBean.getBean();

		// sanitize locModel
		locModel.setLocationName(Sanitizers.FORMATTING.sanitize(locModel.getLocationName()));
		locModel.setLocationAbbreviation(Sanitizers.FORMATTING.sanitize(locModel.getLocationAbbreviation()));

		return locModel;
	}

	void setSessionData(SessionData sessionData) {
		this.sessionData = sessionData;
	}

	void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
