package org.generationcp.ibpworkbench.ui.programmethods;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.data.initializer.MethodViewTestDataInitializer;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProgramMethodsPresenterTest {

	private static final Integer USER_ID = 1;
	private static final int NO_OF_METHODS = 5;

	@Mock
	private ManagerFactoryProvider managerFactoryProvider;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private GermplasmDataManager gerplasmDataManager;

	@Mock
	private ProgramMethodsView programMethodsView;
	
	@Mock
	private BreedingMethodTracker breedingMethodTracker;

	private static final String DUMMY_PROGRAM_UUID = "1234567890";
	private static final Integer NO_OF_FAVORITES = 2;
	private Project project;

	private ProgramMethodsPresenter controller;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.project = ProjectTestDataInitializer.getProject(ProgramMethodsPresenterTest.DUMMY_PROGRAM_UUID);
		this.controller = Mockito.spy(new ProgramMethodsPresenter(this.programMethodsView, this.project));
		this.controller.setGermplasmDataManager(this.gerplasmDataManager);
		this.controller.setContextUtil(this.contextUtil);
		this.controller.setBreedingMethodTracker(this.breedingMethodTracker);
		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(USER_ID);
	}

	@Test
	public void testGetFilteredResults() {
		final String mgroup = "C";
		final String mtype = MethodType.GENERATIVE.getCode();
		final String mname = "Method Name";

		Collection<MethodView> result = null;
		try {
			this.setupGetFilteredResults(mgroup, mtype, mname);
			result = this.controller.getFilteredResults(mgroup, mtype, mname);
		} catch (final MiddlewareQueryException e) {
			Assert.fail();
		}

		final Integer expectedNoOfResults = ProgramMethodsPresenterTest.NO_OF_METHODS;
		Assert.assertTrue("Expecting the results returned " + expectedNoOfResults + " but returned " + result.size(),
				expectedNoOfResults.equals(result.size()));

	}

	@Test
	public void testIsExistingMethod_ReturnsTrueForExistingMethod() throws MiddlewareQueryException {
		final String methodName = "My New Method";
		final Method existingMethod = new Method();
		existingMethod.setMname(methodName);

		Mockito.when(this.gerplasmDataManager.getMethodByName(methodName)).thenReturn(existingMethod);
		Assert.assertTrue("Expected to return true for existing method but didn't.", this.controller.isExistingMethod(methodName));
	}

	@Test
	public void testIsExistingMethod_ReturnsFalseForNonExistingMethod() throws MiddlewareQueryException {
		final String methodName = "My New Method";

		Mockito.when(this.gerplasmDataManager.getMethodByName(methodName)).thenReturn(new Method());
		Assert.assertFalse("Expected to return true for existing method but didn't.", this.controller.isExistingMethod(methodName));
	}
	
	@Test
	public void testSaveNewBreedingMethod() {
		final MethodView method = MethodViewTestDataInitializer.createMethodView();
		Mockito.when(this.gerplasmDataManager.getMethodByName(Matchers.anyString())).thenReturn(new Method());
		final MethodView result = this.controller.saveNewBreedingMethod(method);
		Assert.assertEquals(method.getMname(), result.getMname());
		Assert.assertEquals(method.getMcode(), result.getMcode());
		Assert.assertEquals(ProgramMethodsPresenterTest.USER_ID, result.getUser());
		
	}

	public void setupGetFilteredResults(String mgroup, String mtype, String mname)
			throws MiddlewareQueryException {
		mgroup = mgroup != null ? mgroup : "";
		mtype = mtype != null ? mtype : "";
		mname = mname != null ? mname : "";

		final List<Method> methods = new ArrayList<Method>();

		for (int i = 0; i < ProgramMethodsPresenterTest.NO_OF_METHODS; i++) {
			final Integer methodId = i + 1;
			final Method method = new Method();
			method.setMid(methodId);
			method.setMgrp(mgroup);
			method.setMtype(mtype);
			method.setMname(mname + " " + methodId);

			methods.add(method);

			Mockito.when(this.gerplasmDataManager.getMethodByID(methodId)).thenReturn(method);
		}

		Mockito.when(this.gerplasmDataManager.getMethodsByGroupAndTypeAndName(mgroup, mtype, mname)).thenReturn(methods);
	}

	@Test
	public void testSaveNewBreedingMethodGenerative() {
		final MethodView method = MethodViewTestDataInitializer.createMethodView(MethodType.GENERATIVE.getCode());
		Mockito.when(this.gerplasmDataManager.getMethodByName(Matchers.anyString())).thenReturn(new Method());
		final MethodView result = this.controller.saveNewBreedingMethod(method);
		Assert.assertEquals(method.getMname(), result.getMname());
		Assert.assertEquals(method.getMcode(), result.getMcode());
		Assert.assertEquals("Expected value of mprgn is 2", 2, result.getMprgn().intValue());
		Assert.assertEquals(ProgramMethodsPresenterTest.USER_ID, result.getUser());
	}

	@Test
	public void testSaveNewBreedingMethodDerivative() {
		final MethodView method = MethodViewTestDataInitializer.createMethodView(MethodType.DERIVATIVE.getCode());
		Mockito.when(this.gerplasmDataManager.getMethodByName(Matchers.anyString())).thenReturn(new Method());
		final MethodView result = this.controller.saveNewBreedingMethod(method);
		Assert.assertEquals(method.getMname(), result.getMname());
		Assert.assertEquals(method.getMcode(), result.getMcode());
		Assert.assertEquals("Expected value of mprgn is -1", -1, result.getMprgn().intValue());
		Assert.assertEquals(ProgramMethodsPresenterTest.USER_ID, result.getUser());
	}

	@Test
	public void testSaveNewBreedingMethodMaintenance() {
		final MethodView method = MethodViewTestDataInitializer.createMethodView(MethodType.MAINTENANCE.getCode());
		Mockito.when(this.gerplasmDataManager.getMethodByName(Matchers.anyString())).thenReturn(new Method());
		final MethodView result = this.controller.saveNewBreedingMethod(method);
		Assert.assertEquals(method.getMname(), result.getMname());
		Assert.assertEquals(method.getMcode(), result.getMcode());
		Assert.assertEquals("Expected value of mprgn is -1", -1, result.getMprgn().intValue());
		Assert.assertEquals(ProgramMethodsPresenterTest.USER_ID, result.getUser());
	}

	@Test
	public void testSaveEditBreedingMethodGenerative() {
		final MethodView method = MethodViewTestDataInitializer.createMethodView(MethodType.GENERATIVE.getCode());
		final Method existing = this.getExistingMethod(method, this.controller.getMprgn(MethodType.GENERATIVE.getCode()));
		Mockito.when(this.gerplasmDataManager.editMethod(method.copy())).thenReturn(existing);
		final MethodView result = this.controller.editBreedingMethod(method);
		Assert.assertEquals(method.getMname(), result.getMname());
		Assert.assertEquals(method.getMcode(), result.getMcode());
		Assert.assertEquals("Expected value of mprgn is 2", 2, result.getMprgn().intValue());
		Assert.assertEquals(ProgramMethodsPresenterTest.USER_ID, result.getUser());
	}

	@Test
	public void testSaveEditBreedingMethodDerivative() {
		final MethodView method = MethodViewTestDataInitializer.createMethodView(MethodType.DERIVATIVE.getCode());
		final Method existing = this.getExistingMethod(method, this.controller.getMprgn(MethodType.DERIVATIVE.getCode()));
		Mockito.when(this.gerplasmDataManager.editMethod(method.copy())).thenReturn(existing);
		final MethodView result = this.controller.editBreedingMethod(method);
		Assert.assertEquals(method.getMname(), result.getMname());
		Assert.assertEquals(method.getMcode(), result.getMcode());
		Assert.assertEquals("Expected value of mprgn is -1", -1, result.getMprgn().intValue());
		Assert.assertEquals(ProgramMethodsPresenterTest.USER_ID, result.getUser());
	}


	@Test
	public void testSaveEditBreedingMethodMaintenance() {
		final MethodView method = MethodViewTestDataInitializer.createMethodView(MethodType.MAINTENANCE.getCode());
		final Method existing = this.getExistingMethod(method, this.controller.getMprgn(MethodType.MAINTENANCE.getCode()));
		Mockito.when(this.gerplasmDataManager.editMethod(method.copy())).thenReturn(existing);
		final MethodView result = this.controller.editBreedingMethod(method);
		Assert.assertEquals(method.getMname(), result.getMname());
		Assert.assertEquals(method.getMcode(), result.getMcode());
		Assert.assertEquals("Expected value of mprgn is -1", -1, result.getMprgn().intValue());
		Assert.assertEquals(ProgramMethodsPresenterTest.USER_ID, result.getUser());
	}

	private Method getExistingMethod(final MethodView method, final int mprgn){
		final Method existingMethod = method.copy();
		existingMethod.setMprgn(mprgn);
		existingMethod.setUser(this.contextUtil.getCurrentWorkbenchUserId());
		return existingMethod;
	}

}
