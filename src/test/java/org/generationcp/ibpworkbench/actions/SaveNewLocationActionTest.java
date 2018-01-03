
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SaveNewLocationActionTest {

	public static final String TEST_LOCATION = "Test Location";
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

	private SaveNewLocationAction saveNewLocationAction;

	@Mock
	private LocationViewModel locationViewModelResult;

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(this);
		Mockito.doNothing().when(this.sessionData).logProgramActivity(Matchers.anyString(), Matchers.anyString());
		this.saveNewLocationAction = new SaveNewLocationAction(this.newLocationForm, this.window, this.programLocationsPresenter);
		this.saveNewLocationAction.setMessageSource(this.messageSource);
	}

	@Test
	public void testGetLocationFromForm() throws Exception {
		BeanItem<LocationViewModel> locationFormBean = Mockito.mock(BeanItem.class);

		LocationViewModel lvm = new LocationViewModel();
		lvm.setLocationName(TEST_LOCATION);

		Mockito.when(locationFormBean.getBean()).thenReturn(lvm);
		Mockito.when(this.newLocationForm.getItemDataSource()).thenReturn(locationFormBean);

		this.locationViewModelResult = this.saveNewLocationAction.getLocationFromForm();

		Assert.assertEquals("location name is set", TEST_LOCATION,
				this.locationViewModelResult.getLocationName());
	}

	@Test
	public void testSaveLocation() throws Exception {

		LocationViewModel lvm = new LocationViewModel();
		lvm.setLocationName(TEST_LOCATION);
		lvm.setLocationAbbreviation("TSTL");

		BeanItem<LocationViewModel> locationFormBean = Mockito.mock(BeanItem.class);
		Mockito.when(locationFormBean.getBean()).thenReturn(lvm);
		Mockito.when(this.newLocationForm.getItemDataSource()).thenReturn(locationFormBean);

		Window mockParentWindow = Mockito.mock(Window.class);
		Mockito.when(this.window.getParent()).thenReturn(mockParentWindow);
		Mockito.when(mockParentWindow.removeWindow(this.window)).thenReturn(true);

		// perform the test!
		this.saveNewLocationAction.saveLocation();

		// assertions
		Mockito.verify(this.programLocationsPresenter, Mockito.times(1)).addLocation(Mockito.any(Location.class));
		Mockito.verify(this.sessionData, Mockito.times(1)).logProgramActivity(Matchers.anyString(), Matchers.anyString());
		Mockito.verify(mockParentWindow, Mockito.times(1)).removeWindow(Matchers.any(Window.class));
	}
}
