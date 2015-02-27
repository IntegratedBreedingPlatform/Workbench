package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Window;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.form.AddLocationForm;
import org.generationcp.ibpworkbench.ui.programlocations.AddLocationsWindow;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.pojos.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SaveNewLocationActionTest {

	public static final String XSS_VULNERABLE_STRING = ">'>\"><img src=x onerror=alert(0)>";
	public static final String SANITIZED_LOCATION_NAME = "&gt;&#39;&gt;&#34;&gt;";

	@Mock
	private SessionData sessionData;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private AddLocationForm newLocationForm;

	@Mock
	private AddLocationsWindow window;

	@Mock
	private ProgramLocationsPresenter programLocationsPresenter;

	@InjectMocks
	private SaveNewLocationAction saveActionDUT = spy(
			new SaveNewLocationAction(newLocationForm, window, programLocationsPresenter));
	private LocationViewModel locationViewModelResult;

	@Before
	public void testGetLocationFromForm() throws Exception {
		BeanItem<LocationViewModel> locationFormBean = mock(BeanItem.class);
		when(locationFormBean.getBean()).thenReturn(generateXSSVulnerableLocation());
		when(newLocationForm.getItemDataSource()).thenReturn(locationFormBean);

		locationViewModelResult = saveActionDUT.getLocationFromForm();

		assertNotSame("should not equal", XSS_VULNERABLE_STRING,
				locationViewModelResult.getLocationName());

		assertEquals("location name is sanitized", SANITIZED_LOCATION_NAME,
				locationViewModelResult.getLocationName());

	}

	@Test
	public void testSaveLocation() throws Exception {
		doNothing().when(saveActionDUT).updateSessionData(any(LocationViewModel.class));
		doNothing().when(sessionData).logProgramActivity(anyString(), anyString());

		Location loc = new Location();
		loc.setLname(locationViewModelResult.getLocationName());
		loc.setLabbr(locationViewModelResult.getLocationAbbreviation());

		when(programLocationsPresenter.convertLocationViewToLocation(locationViewModelResult))
				.thenReturn(loc);

		Window mockParentWindow = mock(Window.class);
		when(window.getParent()).thenReturn(mockParentWindow);
		when(mockParentWindow.removeWindow(window)).thenReturn(true);

		// perform the test!
		saveActionDUT.saveLocation();

		// assertions
		verify(saveActionDUT, times(1)).updateSessionData(locationViewModelResult);
		verify(programLocationsPresenter, times(1)).addLocation(loc);
		verify(sessionData, times(1)).logProgramActivity(anyString(), anyString());
		verify(mockParentWindow, times(1)).removeWindow(any(Window.class));
	}

	private LocationViewModel generateXSSVulnerableLocation() {
		LocationViewModel lvm = new LocationViewModel();
		lvm.setLocationName(XSS_VULNERABLE_STRING);
		lvm.setLocationAbbreviation("tst");

		return lvm;
	}
}