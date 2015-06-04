
package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.form.AddLocationForm;
import org.generationcp.ibpworkbench.ui.programlocations.AddLocationsWindow;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.pojos.Location;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Window;

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
	private final SaveNewLocationAction saveActionDUT = Mockito.spy(new SaveNewLocationAction(this.newLocationForm, this.window,
			this.programLocationsPresenter));
	private LocationViewModel locationViewModelResult;

	@Before
	public void testGetLocationFromForm() throws Exception {
		BeanItem<LocationViewModel> locationFormBean = Mockito.mock(BeanItem.class);
		Mockito.when(locationFormBean.getBean()).thenReturn(this.generateXSSVulnerableLocation());
		Mockito.when(this.newLocationForm.getItemDataSource()).thenReturn(locationFormBean);

		this.locationViewModelResult = this.saveActionDUT.getLocationFromForm();

		Assert.assertNotSame("should not equal", SaveNewLocationActionTest.XSS_VULNERABLE_STRING,
				this.locationViewModelResult.getLocationName());

		Assert.assertEquals("location name is sanitized", SaveNewLocationActionTest.SANITIZED_LOCATION_NAME,
				this.locationViewModelResult.getLocationName());

	}

	@Test
	public void testSaveLocation() throws Exception {
		Mockito.doNothing().when(this.saveActionDUT).updateSessionData(Matchers.any(LocationViewModel.class));
		Mockito.doNothing().when(this.sessionData).logProgramActivity(Matchers.anyString(), Matchers.anyString());

		Location loc = new Location();
		loc.setLname(this.locationViewModelResult.getLocationName());
		loc.setLabbr(this.locationViewModelResult.getLocationAbbreviation());

		Mockito.when(this.programLocationsPresenter.convertLocationViewToLocation(this.locationViewModelResult)).thenReturn(loc);

		Window mockParentWindow = Mockito.mock(Window.class);
		Mockito.when(this.window.getParent()).thenReturn(mockParentWindow);
		Mockito.when(mockParentWindow.removeWindow(this.window)).thenReturn(true);

		// perform the test!
		this.saveActionDUT.saveLocation();

		// assertions
		Mockito.verify(this.saveActionDUT, Mockito.times(1)).updateSessionData(this.locationViewModelResult);
		Mockito.verify(this.programLocationsPresenter, Mockito.times(1)).addLocation(loc);
		Mockito.verify(this.sessionData, Mockito.times(1)).logProgramActivity(Matchers.anyString(), Matchers.anyString());
		Mockito.verify(mockParentWindow, Mockito.times(1)).removeWindow(Matchers.any(Window.class));
	}

	private LocationViewModel generateXSSVulnerableLocation() {
		LocationViewModel lvm = new LocationViewModel();
		lvm.setLocationName(SaveNewLocationActionTest.XSS_VULNERABLE_STRING);
		lvm.setLocationAbbreviation("tst");

		return lvm;
	}
}
