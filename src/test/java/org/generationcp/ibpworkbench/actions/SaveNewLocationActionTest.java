package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.form.LocationForm;
import org.generationcp.ibpworkbench.ui.programlocations.AddLocationsWindow;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Location;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class SaveNewLocationActionTest {

	private static final String TEST_LOCATION = "Test Location";
	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private LocationForm newLocationForm;

	@Mock
	private AddLocationsWindow window;

	@Mock
	private ProgramLocationsPresenter programLocationsPresenter;

	private SaveNewLocationAction saveNewLocationAction;

	@Mock
	private LocationViewModel locationViewModelResult;

	@Mock
	private LocationDataManager locationDataManager;

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(this);
		Mockito.doNothing().when(this.contextUtil).logProgramActivity(ArgumentMatchers.<String>isNull(), ArgumentMatchers.anyString());
		this.saveNewLocationAction = new SaveNewLocationAction(this.newLocationForm, this.window, this.programLocationsPresenter);
		this.saveNewLocationAction.setMessageSource(this.messageSource);
		this.saveNewLocationAction.setContextUtil(this.contextUtil);
		this.saveNewLocationAction.setLocationDataManager(this.locationDataManager);
	}

	@Test
	public void testGetLocationFromForm() {
		final BeanItem<LocationViewModel> locationFormBean = Mockito.mock(BeanItem.class);

		final LocationViewModel lvm = new LocationViewModel();
		lvm.setLocationName(TEST_LOCATION);

		Mockito.when(locationFormBean.getBean()).thenReturn(lvm);
		Mockito.when(this.newLocationForm.getItemDataSource()).thenReturn(locationFormBean);

		this.locationViewModelResult = this.saveNewLocationAction.getLocationFromForm();

		Assert.assertEquals("location name is set", TEST_LOCATION, this.locationViewModelResult.getLocationName());
	}

	@Test
	public void testSaveLocation() {

		final LocationViewModel lvm = new LocationViewModel();
		lvm.setLocationName(TEST_LOCATION);
		lvm.setLocationAbbreviation("TSTL");

		final BeanItem<LocationViewModel> locationFormBean = Mockito.mock(BeanItem.class);
		Mockito.when(locationFormBean.getBean()).thenReturn(lvm);
		Mockito.when(this.newLocationForm.getItemDataSource()).thenReturn(locationFormBean);

		final Window mockParentWindow = Mockito.mock(Window.class);
		Mockito.when(this.window.getParent()).thenReturn(mockParentWindow);
		Mockito.when(mockParentWindow.removeWindow(this.window)).thenReturn(true);
		Mockito.when(this.programLocationsPresenter.convertLocationViewToLocation(ArgumentMatchers.eq(lvm))).thenCallRealMethod();

		// perform the test!
		this.saveNewLocationAction.saveLocation();

		// assertions
		Mockito.verify(this.programLocationsPresenter, Mockito.times(1)).addLocation(Mockito.any(Location.class));
		Mockito.verify(this.contextUtil, Mockito.times(1)).logProgramActivity(ArgumentMatchers.<String>isNull(), ArgumentMatchers.anyString());
		Mockito.verify(mockParentWindow, Mockito.times(1)).removeWindow(ArgumentMatchers.any(Window.class));
	}

	@Test
	public void testButtonClickSuccess() {
		final LocationViewModel lvm = new LocationViewModel();
		lvm.setLocationName(TEST_LOCATION);
		lvm.setLocationAbbreviation("TSTL");

		final BeanItem<LocationViewModel> locationFormBean = Mockito.mock(BeanItem.class);
		Mockito.when(locationFormBean.getBean()).thenReturn(lvm);
		Mockito.when(this.newLocationForm.getItemDataSource()).thenReturn(locationFormBean);

		final Window mockParentWindow = Mockito.mock(Window.class);
		Mockito.when(this.window.getParent()).thenReturn(mockParentWindow);
		Mockito.when(mockParentWindow.removeWindow(this.window)).thenReturn(true);
		Mockito.when(this.programLocationsPresenter.convertLocationViewToLocation(ArgumentMatchers.eq(lvm))).thenCallRealMethod();

		final Button.ClickEvent event = Mockito.mock(Button.ClickEvent.class);
		this.saveNewLocationAction.buttonClick(event);

		Mockito.verify(this.programLocationsPresenter, Mockito.times(1)).addLocation(Mockito.any(Location.class));
		Mockito.verify(this.contextUtil, Mockito.times(1)).logProgramActivity(ArgumentMatchers.<String>isNull(), ArgumentMatchers.anyString());
		Mockito.verify(mockParentWindow, Mockito.times(1)).removeWindow(ArgumentMatchers.any(Window.class));
	}

	@Test
	public void testButtonClickHasExistingLocAbbr() {
		final LocationViewModel lvm = new LocationViewModel();
		lvm.setLocationName(TEST_LOCATION);
		lvm.setLocationAbbreviation("TSTL");

		final BeanItem<LocationViewModel> locationFormBean = Mockito.mock(BeanItem.class);
		Mockito.when(locationFormBean.getBean()).thenReturn(lvm);
		Mockito.when(this.newLocationForm.getItemDataSource()).thenReturn(locationFormBean);

		Mockito.when(this.locationDataManager.countByLocationAbbreviation(ArgumentMatchers.anyString())).thenReturn(new Long(1));
		final Button.ClickEvent event = Mockito.mock(Button.ClickEvent.class);
		this.saveNewLocationAction.buttonClick(event);

		Mockito.verify(this.programLocationsPresenter, Mockito.never()).addLocation(Mockito.any(Location.class));
		Mockito.verify(this.contextUtil, Mockito.never()).logProgramActivity(ArgumentMatchers.<String>isNull(), ArgumentMatchers.anyString());
		Mockito.verify(this.messageSource, Mockito.times(1)).getMessage(Message.ERROR);
		Mockito.verify(this.messageSource, Mockito.times(1)).getMessage(Message.ADD_LOCATION_EXISTING_LOCABBR_ERROR, lvm.getLocationAbbreviation());
	}

	@Test
	public void testButtonClickHasExistingLocationName() {
		final LocationViewModel lvm = new LocationViewModel();
		lvm.setLocationName(TEST_LOCATION);
		lvm.setLocationAbbreviation("TSTL");
		lvm.setLocationId(1);

		final BeanItem<LocationViewModel> locationFormBean = Mockito.mock(BeanItem.class);
		Mockito.when(locationFormBean.getBean()).thenReturn(lvm);
		Mockito.when(this.newLocationForm.getItemDataSource()).thenReturn(locationFormBean);

		final Window mockParentWindow = Mockito.mock(Window.class);
		Mockito.when(this.window.getParent()).thenReturn(mockParentWindow);

		Mockito.when(this.programLocationsPresenter.getExistingLocations(lvm.getLocationName())).thenReturn(Arrays.asList(new Location(1)));
		final Button.ClickEvent event = Mockito.mock(Button.ClickEvent.class);
		this.saveNewLocationAction.buttonClick(event);

		Mockito.verify(this.programLocationsPresenter, Mockito.never()).addLocation(Mockito.any(Location.class));
		Mockito.verify(this.contextUtil, Mockito.never()).logProgramActivity(ArgumentMatchers.<String>isNull(), ArgumentMatchers.anyString());
		Mockito.verify(this.messageSource, Mockito.never()).getMessage(Message.ERROR);
		Mockito.verify(this.messageSource, Mockito.never()).getMessage(Message.ADD_LOCATION_EXISTING_LOCABBR_ERROR, lvm.getLocationAbbreviation());
	}
}
