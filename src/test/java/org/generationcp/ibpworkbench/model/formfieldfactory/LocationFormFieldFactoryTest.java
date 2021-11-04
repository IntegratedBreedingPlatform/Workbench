package org.generationcp.ibpworkbench.model.formfieldfactory;

import com.vaadin.data.util.BeanContainer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class LocationFormFieldFactoryTest {

	@Mock
	private ProgramLocationsPresenter presenter;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private LocationFormFieldFactory locationFormFieldFactory;

	@Before
	public void init() throws Exception {

		this.locationFormFieldFactory = new LocationFormFieldFactory(this.presenter);
		this.locationFormFieldFactory.setMessageSource(messageSource);
		this.locationFormFieldFactory.afterPropertiesSet();

	}

	@Test
	public void testRetrieveCountryValue() {

		assertNull(this.locationFormFieldFactory.retrieveCountryValue());

		final BeanContainer<String, Country> container =
				(BeanContainer<String, Country>) this.locationFormFieldFactory.getCountry().getContainerDataSource();
		final Country country = new Country();
		country.setCntryid(1);
		country.setIsoabbr("Philippines");
		country.setIsofull("Republic of the Philippines");
		container.addBean(country);
		this.locationFormFieldFactory.getCountry().select(country.getCntryid());

		assertEquals(country, this.locationFormFieldFactory.retrieveCountryValue());

	}

	@Test
	public void testRetrieveProvinceValue() {

		assertNull(this.locationFormFieldFactory.retrieveProvinceValue());

		final BeanContainer<String, Location> container =
				(BeanContainer<String, Location>) this.locationFormFieldFactory.getProvince().getContainerDataSource();
		final Location location = new Location();
		location.setLocid(1);
		location.setLname("Manila");
		container.addBean(location);
		this.locationFormFieldFactory.getProvince().select(location.getLocid());

		assertEquals(location, this.locationFormFieldFactory.retrieveProvinceValue());

	}

	@Test
	public void testRetrieveLocationTypeValue() {

		assertNull(this.locationFormFieldFactory.retrieveLocationType());

		final BeanContainer<String, UserDefinedField> container =
				(BeanContainer<String, UserDefinedField>) this.locationFormFieldFactory.getlType().getContainerDataSource();
		final UserDefinedField userDefinedField = new UserDefinedField();
		userDefinedField.setFname("Breeding Location");
		userDefinedField.setFldno(1);
		container.addBean(userDefinedField);
		this.locationFormFieldFactory.getlType().select(userDefinedField.getFldno());

		assertEquals(userDefinedField, this.locationFormFieldFactory.retrieveLocationType());

	}

}
