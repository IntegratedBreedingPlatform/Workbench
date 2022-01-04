package org.generationcp.ibpworkbench.ui.programmethods;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/11/13 Time: 9:48 AM To change this template use File | Settings | File Templates.
 */

@Configurable
public class ProgramMethodsPresenter {

	private Project project;
	private CropType cropType;
	private final ProgramMethodsView view;

	private static final Logger LOG = LoggerFactory.getLogger(ProgramMethodsPresenter.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager gerplasmDataManager;

	@Autowired
	private BreedingMethodTracker breedingMethodTracker;

	@Autowired
	private ContextUtil contextUtil;

	public ProgramMethodsPresenter(final ProgramMethodsView view, final Project project) {
		this.view = view;
		this.project = project;
	}

	public ProgramMethodsPresenter(final ProgramMethodsView view, final CropType cropType) {
		this.view = view;
		this.cropType = cropType;
	}

	public List<MethodView> getSavedProgramMethods() {
		if (this.cropType != null) {
			return new ArrayList<>();
		}

		final List<Method> result = new ArrayList<>();
		try {
			final List<ProgramFavorite> favorites =
					this.gerplasmDataManager.getProgramFavorites(FavoriteType.METHODS, this.project.getUniqueID());

			for (final ProgramFavorite favorite : favorites) {
				final Method m = this.gerplasmDataManager.getMethodByID(favorite.getEntityId());

				if (m != null) {
					result.add(m);
				}
			}

		} catch (final MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return this.convertFrom(result);
	}

	public Collection<MethodView> getFilteredResults(final String mgroup, final String mtype, final String mname) {
		final Map<Integer, MethodView> resultsMap = new LinkedHashMap<Integer, MethodView>();

		try {
			final List<MethodView> result =
					this.convertFrom(this.gerplasmDataManager.getMethodsByGroupAndTypeAndName(mgroup, mtype, mname));

			for (final MethodView method : result) {
				resultsMap.put(method.getMid(), method);
			}

		} catch (final MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return resultsMap.values();
	}

	public MethodView editBreedingMethod(final MethodView method) {
		MethodView result = null;
		try {
			method.setMprgn(this.getMprgn(method.getMtype()));
			result = this.convertMethod(this.gerplasmDataManager.editMethod(method.copy()));
		} catch (final MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		if (!this.breedingMethodTracker.getUniqueBreedingMethods().contains(result.getMname())) {
			this.breedingMethodTracker.getUniqueBreedingMethods().add(result.getMname());
			this.breedingMethodTracker.getProjectBreedingMethodData().put(result.getMid(), result);
		}

		this.view.refreshTable();

		return this.convertMethod(result);
	}

	public boolean isExistingMethod(final String methodName) {
		final Method existingMethod;
		try {
			existingMethod = this.gerplasmDataManager.getMethodByName(methodName);

			if (existingMethod.getMname() != null && existingMethod.getMname().length() > 0) {
				return true;
			}

		} catch (final MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return false;
	}

	public MethodView saveNewBreedingMethod(final MethodView method) {

		if (!this.isExistingMethod(method.getMname())) {
			final MethodView newBreedingMethod = new MethodView();

			newBreedingMethod.setMname(method.getMname());
			newBreedingMethod.setMdesc(method.getMdesc());
			newBreedingMethod.setMcode(method.getMcode());
			newBreedingMethod.setMgrp(method.getMgrp());
			newBreedingMethod.setMtype(method.getMtype());
			newBreedingMethod.setGeneq(method.getGeneq());
			newBreedingMethod.setUser(this.contextUtil.getCurrentWorkbenchUserId());
			newBreedingMethod.setLmid(0);
			newBreedingMethod.setMattr(0);
			newBreedingMethod.setMprgn(this.getMprgn(newBreedingMethod.getMtype()));
			newBreedingMethod.setReference(0);

			newBreedingMethod.setMdate(DateUtil.getCurrentDateAsIntegerValue());
			newBreedingMethod.setMfprg(0);

			// ADD TO MIDDLEWARE LOCAL
			newBreedingMethod.setMid(this.gerplasmDataManager.addMethod(newBreedingMethod.copy()));
			newBreedingMethod.setIsnew(true);

			LOG.trace("Added breeding method (" + newBreedingMethod.getMname() + " id:" + newBreedingMethod.getMid() + ")");

			this.view.addRow(newBreedingMethod, false, 0);

			return newBreedingMethod;
		}

		return method;
	}

	public boolean saveFavoriteBreedingMethod(final Collection<MethodView> selectedMethodIds) {
		return ProgramMethodsPresenter
				.saveFavoriteBreedingMethod(selectedMethodIds, this.project, this.contextUtil, this.workbenchDataManager,
						this.gerplasmDataManager);
	}

	public static boolean saveFavoriteBreedingMethod(final Collection<MethodView> selectedMethodIds, final Project project,
			final ContextUtil contextUtil, final WorkbenchDataManager workbenchDataManager, final GermplasmDataManager gdm) {
		List<ProgramFavorite> favorites = null;
		try {
			favorites = gdm.getProgramFavorites(ProgramFavorite.FavoriteType.METHODS, project.getUniqueID());

			// TODO: THIS IS A VERY UGLY CODE THAT WAS INHERITED IN THE OLD ProjectBreedingMethodsPanel Code, Replace the logic if possible

			for (final Method m : selectedMethodIds) {
				boolean mExists = false;

				for (final ProgramFavorite favorite : favorites) {
					if (favorite.getEntityId().equals(m.getMid())) {
						mExists = true;
					}
				}

				if (!mExists) {
					contextUtil.logProgramActivity("Project Methods",
							String.format("Added a Breeding Method (%s) to the project", m.getMname()));
				}
			}
			// code block just adds a log activity, replace by just tracking newly added methods id so no need to fetch all methods from DB

			gdm.deleteProgramFavorites(favorites);

			// Repopulate the project methods table
			final List<ProgramFavorite> list = new ArrayList<ProgramFavorite>();
			int mID = 0;

			for (final Method m : selectedMethodIds) {
				final ProgramFavorite favorite = new ProgramFavorite();
				if (m.getMid() < 1) {
					final Method m2 = gdm.getMethodByID(m.getMid());

					if (m2 == null) {
						final Method newMethod =
								new Method(m.getMid(), m.getMtype(), m.getMgrp(), m.getMcode(), m.getMname(), m.getMdesc(), 0, 0, 0, 0, 0,
										0, 0, m.getMdate());
						mID = gdm.addMethod(newMethod);
					} else {
						mID = m2.getMid();
					}
				} else {
					mID = m.getMid();
				}

				favorite.setEntityType(ProgramFavorite.FavoriteType.METHODS);
				favorite.setEntityId(mID);
				favorite.setUniqueID(project.getUniqueID());
				list.add(favorite);
			}

			gdm.saveProgramFavorites(list);

		} catch (final MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	public MethodView convertMethod(final Method method) {
		final PropertyUtilsBean pub = new PropertyUtilsBean();
		final MethodView methodView = new MethodView();
		try {
			pub.copyProperties(methodView, method);

		} catch (final Exception e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return methodView;
	}

	public List<MethodView> convertFrom(final List<Method> list) {
		final List<MethodView> result = new ArrayList<MethodView>();
		for (final Method method : list) {
			final MethodView methodView = new MethodView();

			final PropertyUtilsBean pub = new PropertyUtilsBean();
			try {
				pub.copyProperties(methodView, method);

				result.add(methodView);
			} catch (final Exception e) {
				ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
			}
		}
		return result;
	}

	public Collection<Method> convertTo(final Collection<MethodView> list) {
		final List<Method> result = new ArrayList<Method>();

		for (final MethodView methodView : list) {
			result.add(methodView.copy());
		}
		return result;
	}

	public Map<Integer, String> getMethodClasses() {
		final Map<Integer, String> methodClasses = new LinkedHashMap<Integer, String>();
		try {
			final List<Term> terms = this.gerplasmDataManager.getMethodClasses();
			if (terms != null) {
				for (final Term term : terms) {
					methodClasses.put(term.getId(), term.getName());
				}
			}
		} catch (final MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return methodClasses;
	}

	public int getMprgn(final String mtype) {
		int mprgn = 0;
		final MethodType methodType = MethodType.getMethodType(mtype);
		switch(methodType) {
			case GENERATIVE:
				mprgn = 2;
				break;
			case DERIVATIVE:
			case MAINTENANCE:
				mprgn = -1;
				break;
			default:
				mprgn = 0;
				break;
		}
		return mprgn;
	}

	public void setGermplasmDataManager(final GermplasmDataManager gerplasmDataManager) {
		this.gerplasmDataManager = gerplasmDataManager;
	}
	
	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setBreedingMethodTracker(final BreedingMethodTracker breedingMethodTracker) {
		this.breedingMethodTracker = breedingMethodTracker;
	}
}
