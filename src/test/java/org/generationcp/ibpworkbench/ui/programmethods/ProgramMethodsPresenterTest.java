package org.generationcp.ibpworkbench.ui.programmethods;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProgramMethodsPresenterTest {

	private static final int NO_OF_METHODS = 5;
	private static final int NO_OF_METHODS_WITH_PROGRAM_UUID = 3;

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

	private static final String DUMMY_PROGRAM_UUID = "1234567890";
	private static final Integer NO_OF_FAVORITES = 2;
	private Project project;

	private ProgramMethodsPresenter controller;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.project = this.getProject(ProgramMethodsPresenterTest.DUMMY_PROGRAM_UUID);
		this.controller = Mockito.spy(new ProgramMethodsPresenter(this.programMethodsView, this.project));
		this.controller.setGerplasmDataManager(this.gerplasmDataManager);
	}

	private Project getProject(final String dummyProgramUuid) {
		final Project project = new Project();
		project.setProjectId(1L);
		project.setProjectName("Project Name");
		project.setUniqueID(ProgramMethodsPresenterTest.DUMMY_PROGRAM_UUID);
		return project;
	}

	@Test
	public void testGetFilteredResults() {
		final String mgroup = "C";
		final String mtype = "GEN";
		final String mname = "Method Name";

		Collection<MethodView> result = null;
		try {
			this.setupGetFilteredResults(mgroup, mtype, mname, ProgramMethodsPresenterTest.DUMMY_PROGRAM_UUID);
			result = this.controller.getFilteredResults(mgroup, mtype, mname);
		} catch (final MiddlewareQueryException e) {
			Assert.fail();
		}

		final Integer expectedNoOfResults = ProgramMethodsPresenterTest.NO_OF_METHODS - 1;
		Assert.assertTrue("Expecting the results returned " + expectedNoOfResults + " but returned " + result.size(),
				expectedNoOfResults.equals(result.size()));

	}

	@Test
	public void testGetSavedProgramMethods() {
		final String entityType = "C";
		List<MethodView> results = new ArrayList<MethodView>();
		final String mgroup = "C";
		final String mtype = "GEN";
		final String mname = "Method Name";

		try {
			this.setupGetFilteredResults(mgroup, mtype, mname, ProgramMethodsPresenterTest.DUMMY_PROGRAM_UUID);
			this.setUpFavoriteMethods(entityType);
			results = this.controller.getSavedProgramMethods();
		} catch (final MiddlewareQueryException e) {
			Assert.fail();
		}

		Assert.assertTrue("Expecting to return " + ProgramMethodsPresenterTest.NO_OF_FAVORITES + " but returned " + results.size(),
				ProgramMethodsPresenterTest.NO_OF_FAVORITES == results.size());
	}

	@Test
	public void testIsExistingMethod_ReturnsTrueForExistingMethod() throws MiddlewareQueryException {
		final String methodName = "My New Method";
		final Method existingMethod = new Method();
		existingMethod.setMname(methodName);

		Mockito.when(this.gerplasmDataManager.getMethodByName(methodName, this.project.getUniqueID())).thenReturn(existingMethod);
		Assert.assertTrue("Expected to return true for existing method but didn't.", this.controller.isExistingMethod(methodName));
	}

	@Test
	public void testIsExistingMethod_ReturnsFalseForNonExistingMethod() throws MiddlewareQueryException {
		final String methodName = "My New Method";

		Mockito.when(this.gerplasmDataManager.getMethodByName(methodName, this.project.getUniqueID())).thenReturn(new Method());
		Assert.assertFalse("Expected to return true for existing method but didn't.", this.controller.isExistingMethod(methodName));
	}

	private void setUpFavoriteMethods(final String entityType) throws MiddlewareQueryException {
		final List<ProgramFavorite> favorites = new ArrayList<ProgramFavorite>();

		for (int i = 0; i < ProgramMethodsPresenterTest.NO_OF_FAVORITES; i++) {
			final Integer methodId = i + 1;
			final ProgramFavorite favorite = new ProgramFavorite();
			favorite.setEntityId(methodId);
			favorite.setEntityType(entityType);
			favorite.setUniqueID(ProgramMethodsPresenterTest.DUMMY_PROGRAM_UUID);

			favorites.add(favorite);
		}

		Mockito.when(this.gerplasmDataManager.getProgramFavorites(FavoriteType.METHOD, ProgramMethodsPresenterTest.DUMMY_PROGRAM_UUID))
				.thenReturn(favorites);

	}

	public void setupGetFilteredResults(String mgroup, String mtype, String mname, final String programUUID)
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

		for (int i = 0; i < ProgramMethodsPresenterTest.NO_OF_METHODS_WITH_PROGRAM_UUID; i++) {
			final Method method = methods.get(i);
			method.setUniqueID(ProgramMethodsPresenterTest.DUMMY_PROGRAM_UUID);
		}

		final Method method = methods.get(ProgramMethodsPresenterTest.NO_OF_METHODS_WITH_PROGRAM_UUID);
		method.setUniqueID("9876543210");

		Mockito.when(this.gerplasmDataManager.getMethodsByGroupAndTypeAndName(mgroup, mtype, mname)).thenReturn(methods);
	}
}
