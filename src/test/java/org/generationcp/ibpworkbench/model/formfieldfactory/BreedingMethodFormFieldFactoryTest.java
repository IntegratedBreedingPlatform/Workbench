package org.generationcp.ibpworkbench.model.formfieldfactory;

import com.vaadin.data.Validator;
import com.vaadin.ui.Field;
import junit.framework.Assert;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BreedingMethodFormFieldFactoryTest {

	public static final String PROGRAM_UUID = "932489324-3824738250-fadsfjkahs";
	public static final String METHOD_NAME = "MethodName";
	public static final String METHOD_CODE = "MethodCode";

	@Mock
	private Field methodNameField;

	@Mock
	private Field methodCodeField;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private ContextUtil contextUtil;

	private BreedingMethodFormFieldFactory breedingMethodFormFieldFactory;

	@Before
	public void init() {

		this.breedingMethodFormFieldFactory = new BreedingMethodFormFieldFactory(new HashMap<Integer, String>());
		this.breedingMethodFormFieldFactory.setEditMode(true);
		this.breedingMethodFormFieldFactory.setContextUtil(this.contextUtil);
		this.breedingMethodFormFieldFactory.setGermplasmDataManager(this.germplasmDataManager);
		this.breedingMethodFormFieldFactory.setMethodName(this.methodNameField);
		this.breedingMethodFormFieldFactory.setMethodCode(this.methodCodeField);
		this.breedingMethodFormFieldFactory.setMethodId(1234);

		final Project project = new Project();
		project.setUniqueID(PROGRAM_UUID);
		when(this.contextUtil.getProjectInContext()).thenReturn(project);

	}

	@Test
	public void testMethodNameValidatorValidateSuccess() {

		when(this.germplasmDataManager.getMethodByName(METHOD_NAME)).thenReturn(null);

		final BreedingMethodFormFieldFactory.MethodNameValidator validator = this.breedingMethodFormFieldFactory.new MethodNameValidator();

		try {
			validator.validate(METHOD_NAME);
		} catch (final Validator.InvalidValueException e) {
			Assert.fail("Should not throw an InvalidValueException");
		}

	}

	@Test
	public void testMethodNameValidatorValidateThrowError() {

		final int methodId = 123;
		final Method method = new Method();
		method.setMname(METHOD_NAME);
		method.setMid(methodId);

		when(this.germplasmDataManager.getMethodByName(METHOD_NAME)).thenReturn(method);
		when(this.methodNameField.isModified()).thenReturn(true);

		final BreedingMethodFormFieldFactory.MethodNameValidator validator = this.breedingMethodFormFieldFactory.new MethodNameValidator();

		try {
			validator.validate(METHOD_NAME);
			Assert.fail("Expected to throw an InvalidValueException");
		} catch (final Validator.InvalidValueException e) {
			assertEquals(String.format(BreedingMethodFormFieldFactory.MethodNameValidator.BREEDING_METHOD_ALREADY_EXISTS, METHOD_NAME),
					e.getMessage());
		}

	}

	@Test
	public void testMethodNameValidatorMethodAlreadyExists() {

		final int methodId = 123;
		final Method method = new Method();
		method.setMname(METHOD_NAME);
		method.setMid(methodId);

		when(this.germplasmDataManager.getMethodByName(METHOD_NAME)).thenReturn(method);
		when(this.methodNameField.isModified()).thenReturn(true);

		final BreedingMethodFormFieldFactory.MethodNameValidator validator = this.breedingMethodFormFieldFactory.new MethodNameValidator();

		assertFalse(validator.isValid(METHOD_NAME));

	}

	@Test
	public void testMethodNameValidatorMethodNotYetExists() {

		when(this.germplasmDataManager.getMethodByName(METHOD_NAME)).thenReturn(null);

		final BreedingMethodFormFieldFactory.MethodNameValidator validator = this.breedingMethodFormFieldFactory.new MethodNameValidator();

		assertTrue(validator.isValid(METHOD_NAME));

	}

	@Test
	public void testMethodCodeValidatorValidateSuccess() {

		when(this.germplasmDataManager.getMethodByCode(METHOD_CODE)).thenReturn(null);

		final BreedingMethodFormFieldFactory.MethodCodeValidator validator = this.breedingMethodFormFieldFactory.new MethodCodeValidator();

		try {
			validator.validate(METHOD_CODE);
		} catch (final Validator.InvalidValueException e) {
			Assert.fail("Should not throw an InvalidValueException");
		}

	}

	@Test
	public void testMethodCodeValidatorValidateThrowError() {

		final int methodId = 123;
		final Method method = new Method();
		method.setMname(METHOD_NAME);
		method.setMid(methodId);
		method.setMcode(METHOD_CODE);

		when(this.germplasmDataManager.getMethodByCode(METHOD_CODE)).thenReturn(method);
		when(this.methodCodeField.isModified()).thenReturn(true);

		final BreedingMethodFormFieldFactory.MethodCodeValidator validator = this.breedingMethodFormFieldFactory.new MethodCodeValidator();

		try {
			validator.validate(METHOD_CODE);
			Assert.fail("Expected to throw an InvalidValueException");
		} catch (final Validator.InvalidValueException e) {
			assertEquals(
					String.format(BreedingMethodFormFieldFactory.MethodCodeValidator.BREEDING_METHOD_WITH_CODE_ALREADY_EXISTS, METHOD_CODE),
					e.getMessage());
		}

	}

	@Test
	public void testMethodCodeValidatorMethodAlreadyExists() {

		final int methodId = 123;
		final Method method = new Method();
		method.setMname(METHOD_NAME);
		method.setMid(methodId);
		method.setMcode(METHOD_CODE);

		when(this.germplasmDataManager.getMethodByCode(METHOD_CODE)).thenReturn(method);
		when(this.methodCodeField.isModified()).thenReturn(true);

		final BreedingMethodFormFieldFactory.MethodCodeValidator validator = this.breedingMethodFormFieldFactory.new MethodCodeValidator();

		assertFalse(validator.isValid(METHOD_CODE));

	}

	@Test
	public void testMethodCodeValidatorMethodNotYetExists() {

		when(this.germplasmDataManager.getMethodByCode(METHOD_CODE)).thenReturn(null);

		final BreedingMethodFormFieldFactory.MethodCodeValidator validator = this.breedingMethodFormFieldFactory.new MethodCodeValidator();

		assertTrue(validator.isValid(METHOD_CODE));

	}

}
