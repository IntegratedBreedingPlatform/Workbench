package org.generationcp.ibpworkbench.ui.programlocations;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.form.LocationForm;
import org.generationcp.ibpworkbench.ui.window.ConfirmLocationsWindow;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EditLocationsWindowTest {

	private static final String WARNING = "Warning";
	private static final String LOCATION_IS_USED_IN_OTHER_PROGRAM = "Location is used in other program";
	private static final String PROJECT_LOCATIONS_LINK = "Project Locations Link";
	@Mock
	private ProgramLocationsPresenter presenter;

	@Mock
	private Table sourceTable;

	@Mock
	private LocationForm locationForm;

	@Mock
	private Component component;

	@Mock
	private Window window;

	@Mock
	private Window parent;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private LocationDataManager locationDataManager;

	private EditLocationsWindow editLocationsWindow;

	@Before
	public void init() {

		this.editLocationsWindow = new EditLocationsWindow(new LocationViewModel(), this.presenter, this.sourceTable);
		this.editLocationsWindow.setMessageSource(this.messageSource);
		this.editLocationsWindow.setContextUtil(this.contextUtil);
		this.editLocationsWindow.setParent(this.parent);
		this.editLocationsWindow.setLocationForm(this.locationForm);
		this.editLocationsWindow.setLocationDataManager(this.locationDataManager);

		when(this.component.getWindow()).thenReturn(this.window);
		when(this.messageSource.getMessage(Message.WARNING)).thenReturn(WARNING);
		when(this.messageSource.getMessage(Message.LOCATION_IS_USED_IN_OTHER_PROGRAM)).thenReturn(LOCATION_IS_USED_IN_OTHER_PROGRAM);
		when(this.messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK)).thenReturn(PROJECT_LOCATIONS_LINK);
		when(this.presenter.getLocationDetailsByLocId(anyInt())).thenReturn(new LocationViewModel());
	}

	@Test
	public void testUpdateLocationActionHasDuplicateLocationName() {

		final List<Location> existingLocationWithSameName = new ArrayList<>();
		existingLocationWithSameName.add(new Location(1));

		final String locationName = "locationName";

		when(this.locationForm.getLocationNameValue()).thenReturn(locationName);
		when(this.locationForm.isLocationNameModified()).thenReturn(true);
		when(this.presenter.getExistingLocations(locationName)).thenReturn(existingLocationWithSameName);

		final EditLocationsWindow.UpdateLocationAction updateLocationAction = this.editLocationsWindow.new UpdateLocationAction();

		updateLocationAction.buttonClick(null);

		// If the Location Name in the form has duplicate name in existing locations,
		// Confirm Locations dialog should be displayed.
		verify(this.parent).addWindow(any(ConfirmLocationsWindow.class));

	}

	@Test
	public void testUpdateLocationActionHasDuplicateLocationNameButLocationNameIsNotModified() {

		final List<Location> existingLocationWithSameName = new ArrayList<>();
		existingLocationWithSameName.add(new Location(1));

		final String locationName = "locationName";
		final LocationViewModel locationViewModel = new LocationViewModel();
		final BeanItem item = mock(BeanItem.class);
		when(this.locationForm.getItemDataSource()).thenReturn(item);
		when(item.getBean()).thenReturn(locationViewModel);
		when(this.locationForm.getLocationNameValue()).thenReturn(locationName);
		when(this.locationForm.isLocationNameModified()).thenReturn(false);
		// Set an existing location list with items to simulate location name with duplicate name in existing locations.
		when(this.presenter.getExistingLocations(locationName)).thenReturn(existingLocationWithSameName);

		final EditLocationsWindow.UpdateLocationAction updateLocationAction = this.editLocationsWindow.new UpdateLocationAction();

		updateLocationAction.buttonClick(null);

		// If the Location Name in the form has duplicate name in existing locations BUT the location name in the form
		// hasnt been modified, then DO NOT show the Confirm Locations dialog.
		verify(this.parent, never()).addWindow(any(ConfirmLocationsWindow.class));

		verify(this.locationForm).commit();
		verify(this.presenter).updateLocation(eq(locationViewModel), anyBoolean());
		verify(this.contextUtil)
				.logProgramActivity(PROJECT_LOCATIONS_LINK, "Updated location (" + locationViewModel.getLocationName() + ")");
		verify(this.parent).removeWindow(this.editLocationsWindow);

	}

	@Test
	public void testUpdateLocationActionNoDuplicateName() {

		final String locationName = "locationName";
		final LocationViewModel locationViewModel = new LocationViewModel();
		final BeanItem item = mock(BeanItem.class);
		when(this.locationForm.getItemDataSource()).thenReturn(item);
		when(item.getBean()).thenReturn(locationViewModel);

		when(this.locationForm.getLocationNameValue()).thenReturn(locationName);
		// Set an empty existing location list to simulate location name with no duplicate in existing locations.
		when(this.presenter.getExistingLocations(locationName)).thenReturn(new ArrayList<Location>());

		final EditLocationsWindow.UpdateLocationAction updateLocationAction = this.editLocationsWindow.new UpdateLocationAction();

		updateLocationAction.buttonClick(null);

		// If the Location Name in the form has no duplicate name then just save the location.
		verify(this.parent, never()).addWindow(any(ConfirmLocationsWindow.class));

		verify(this.locationForm).commit();
		verify(this.presenter).updateLocation(eq(locationViewModel), anyBoolean());
		verify(this.contextUtil)
				.logProgramActivity(PROJECT_LOCATIONS_LINK, "Updated location (" + locationViewModel.getLocationName() + ")");
		verify(this.parent).removeWindow(this.editLocationsWindow);

	}

	@Test
	public void testSaveLocation() {

		final LocationViewModel locationViewModel = new LocationViewModel();
		final BeanItem item = mock(BeanItem.class);
		when(this.locationForm.getItemDataSource()).thenReturn(item);
		when(item.getBean()).thenReturn(locationViewModel);

		final EditLocationsWindow.UpdateLocationAction updateLocationAction = this.editLocationsWindow.new UpdateLocationAction();

		updateLocationAction.saveLocation();

		verify(this.locationForm).commit();
		verify(this.presenter).updateLocation(eq(locationViewModel), anyBoolean());
		verify(this.contextUtil)
				.logProgramActivity(PROJECT_LOCATIONS_LINK, "Updated location (" + locationViewModel.getLocationName() + ")");
		verify(this.parent).removeWindow(this.editLocationsWindow);

	}

	@Test
	public void testWindowOnFocusListenerLocationUsedInOtherProgram() {

		final FieldEvents.FocusEvent focusEvent = mock(FieldEvents.FocusEvent.class);
		when(focusEvent.getComponent()).thenReturn(this.component);
		when(this.locationForm.isLocationUsedInAnyProgram()).thenReturn(true);

		final EditLocationsWindow.WindowOnFocusListener listener = this.editLocationsWindow.new WindowOnFocusListener();

		listener.focus(focusEvent);

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);

		verify(this.window).showNotification(captor.capture());

		final Window.Notification notification = captor.getValue();

		assertEquals(WARNING, notification.getCaption());
		assertEquals("</br>" + LOCATION_IS_USED_IN_OTHER_PROGRAM, notification.getDescription());

	}

	@Test
	public void testWindowOnFocusListenerLocationNotUsedInOtherProgram() {

		final FieldEvents.FocusEvent focusEvent = mock(FieldEvents.FocusEvent.class);

		final EditLocationsWindow.WindowOnFocusListener listener = this.editLocationsWindow.new WindowOnFocusListener();

		listener.focus(focusEvent);

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);

		verify(this.window, Mockito.never()).showNotification(captor.capture());

	}


	@Test
	public void testUpdateLocationActionHasDuplicateLocationAbbr() {

		final String locationName = "location name";
		when(this.locationForm.getLocationNameValue()).thenReturn(locationName);
		when(this.presenter.getExistingLocations(locationName)).thenReturn(new ArrayList<Location>());

		final String locationAbbr = "LABBR";
		when(this.locationForm.getLocationAbbreviationValue()).thenReturn(locationAbbr);
		when(this.locationForm.isLocationAbbreviationModified()).thenReturn(true);
		when(this.locationDataManager.countByLocationAbbreviation(locationAbbr)).thenReturn(new Long(1));
		final EditLocationsWindow.UpdateLocationAction updateLocationAction = this.editLocationsWindow.new UpdateLocationAction();

		updateLocationAction.buttonClick(null);
		Mockito.verify(this.messageSource, Mockito.times(1)).getMessage(Message.ERROR);
		Mockito.verify(this.messageSource, Mockito.times(1)).getMessage(Message.ADD_LOCATION_EXISTING_LOCABBR_ERROR, locationAbbr);
	}

	@Test
	public void testUpdateLocationActionLocationAbbreviationFieldNotModified() {

		final String locationName = "location name";
		when(this.locationForm.getLocationNameValue()).thenReturn(locationName);
		when(this.presenter.getExistingLocations(locationName)).thenReturn(new ArrayList<Location>());

		final LocationViewModel locationViewModel = new LocationViewModel();
		final BeanItem item = mock(BeanItem.class);
		when(this.locationForm.getItemDataSource()).thenReturn(item);
		when(item.getBean()).thenReturn(locationViewModel);

		when(this.locationForm.isLocationAbbreviationModified()).thenReturn(false);
		final EditLocationsWindow.UpdateLocationAction updateLocationAction = this.editLocationsWindow.new UpdateLocationAction();

		updateLocationAction.buttonClick(null);

		verify(this.parent, never()).addWindow(any(ConfirmLocationsWindow.class));

		verify(this.locationForm).commit();
		verify(this.presenter).updateLocation(eq(locationViewModel), anyBoolean());
		verify(this.contextUtil)
			.logProgramActivity(PROJECT_LOCATIONS_LINK, "Updated location (" + locationViewModel.getLocationName() + ")");
		verify(this.parent).removeWindow(this.editLocationsWindow);
	}
}
