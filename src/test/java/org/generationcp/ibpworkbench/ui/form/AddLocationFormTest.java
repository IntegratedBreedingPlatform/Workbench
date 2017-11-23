package org.generationcp.ibpworkbench.ui.form;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import junit.framework.Assert;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class AddLocationFormTest {


	@Mock
	private ProgramLocationsPresenter programLocationsPresenter;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private AddLocationForm addLocationForm;

	@Before
	public void setUp() {

		addLocationForm = new AddLocationForm(programLocationsPresenter);

		Mockito.when(programLocationsPresenter.getUDFByLocationAndLType()).thenReturn(new ArrayList<UserDefinedField>());
		Mockito.when(programLocationsPresenter.getCountryList()).thenReturn(new ArrayList<Country>());

	}


	@Test
	public void testAttachField() {

		LocationFormFieldFactory locationFormFieldFactory = (LocationFormFieldFactory) addLocationForm.getFormFieldFactory();

		Assert.assertEquals(locationFormFieldFactory.getLocationName(), addLocationForm.getGrid().getComponent(1, 0));
		Assert.assertEquals(locationFormFieldFactory.getLocationAbbreviation(), addLocationForm.getGrid().getComponent(1, 1));
		Assert.assertEquals(locationFormFieldFactory.getlType(), addLocationForm.getGrid().getComponent(1, 2));
		Assert.assertEquals(locationFormFieldFactory.getCountry(), addLocationForm.getGrid().getComponent(1, 3));
		Assert.assertEquals(locationFormFieldFactory.getProvince(), addLocationForm.getGrid().getComponent(1, 4));
		Assert.assertEquals(locationFormFieldFactory.getLatitude(), addLocationForm.getGrid().getComponent(1, 5));
		Assert.assertEquals(locationFormFieldFactory.getLongitude(), addLocationForm.getGrid().getComponent(2, 5));
		Assert.assertEquals(locationFormFieldFactory.getAltitude(), addLocationForm.getGrid().getComponent(3, 5));
		Assert.assertEquals(locationFormFieldFactory.getCropAccessible(), addLocationForm.getGrid().getComponent(1, 7));

	}


}
