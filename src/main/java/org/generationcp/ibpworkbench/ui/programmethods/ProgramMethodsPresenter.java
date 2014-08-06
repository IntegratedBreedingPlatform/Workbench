package org.generationcp.ibpworkbench.ui.programmethods;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/11/13
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */

@Configurable
public class ProgramMethodsPresenter implements InitializingBean {
    private boolean isCropOnly;
    private Project project;
    private CropType cropType;
    private ProgramMethodsView view;

    private static final Logger LOG = LoggerFactory.getLogger(ProgramMethodsPresenter.class);

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SessionData sessionData;

    private GermplasmDataManager gdm;



    public ProgramMethodsPresenter(ProgramMethodsView view, Project project) {
        this.view = view;
        this.project = project;
    }

    public ProgramMethodsPresenter(ProgramMethodsView view, CropType cropType) {
        this.view = view;
        this.cropType = cropType;
        this.isCropOnly = true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (cropType != null)
            this.gdm = managerFactoryProvider.getManagerFactoryForCropType(cropType).getGermplasmDataManager();
        else
            this.gdm = managerFactoryProvider.getManagerFactoryForProject(project).getGermplasmDataManager();
    }

    public void doMoveToSelectedMethod(Integer id) {
        Method selectedMethod = null;
        try {
            selectedMethod = gdm.getMethodByID(id);

            view.addRow(convertMethod(selectedMethod),false,0);

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public Method getMethodByID(Integer id) {
        try {
            return gdm.getMethodByID(id);

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public void doRemoveSelectedMethod(Integer id) {
        Method selectedMethod = null;
        try {
            selectedMethod = gdm.getMethodByID(id);

            view.addRow(convertMethod(selectedMethod),true,0);

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public List<MethodView> getSavedProgramMethods() {
        if (cropType != null)
            return new ArrayList<MethodView>();

        List<Method> result = new ArrayList<Method>();
        try {
           List<ProgramFavorite> favorites = gdm.getProgramFavorites(FavoriteType.METHOD);

            for (ProgramFavorite favorite : favorites) {
                Method m = gdm.getMethodByID(favorite.getEntityId());

                if (m != null)
                    result.add(m);
            }

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return convertFrom(result);
    }

    public Collection<MethodView> getFilteredResults(String mgroup, String mtype, String mname,Collection<MethodView> existingItems) {
        Map<Integer,MethodView> resultsMap = new LinkedHashMap<Integer, MethodView>();

        try {
            List<MethodView> result = convertFrom(gdm.getMethodsByGroupAndTypeAndName(mgroup, mtype, mname));

            for (MethodView method : result) {
                resultsMap.put(method.getMid(),method);
            }

            // remove items already in favorites
            for (MethodView method : existingItems) {
                if (resultsMap.containsKey(method.getMid())) {
                    resultsMap.remove(method.getMid());
                }
            }

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }

        /*
        ArrayList sorted =  new ArrayList<MethodView>(resultsMap.values());

        Collections.sort(sorted,new Comparator<MethodView>() {
            @Override
            public int compare(MethodView o1, MethodView o2) {
                return o1.getMname().toUpperCase().compareTo(o2.getMname().toUpperCase());
            }
        });
        */
        return resultsMap.values();
    }

    public Collection<MethodView> getFilteredResults(String mgroup, String mtype, String mname) {
        Map<Integer,MethodView> resultsMap = new LinkedHashMap<Integer, MethodView>();

        try {
            List<MethodView> result = convertFrom(gdm.getMethodsByGroupAndTypeAndName(mgroup, mtype, mname));

            for (MethodView method : result) {
                resultsMap.put(method.getMid(),method);
            }

            // remove items already in favorites
            for (MethodView method : this.getSavedProgramMethods()) {
                if (resultsMap.containsKey(method.getMid())) {
                    resultsMap.remove(method.getMid());
                }
            }

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }

        /*
        ArrayList sorted =  new ArrayList<MethodView>(resultsMap.values());

        Collections.sort(sorted,new Comparator<MethodView>() {
            @Override
            public int compare(MethodView o1, MethodView o2) {
                return o1.getMname().toUpperCase().compareTo(o2.getMname().toUpperCase());
            }
        });
        */
        return resultsMap.values();
    }

    public MethodView editBreedingMethod(MethodView method) {
        MethodView result = null;
        try {
            result = convertMethod(gdm.editMethod(method.copy()));
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }

        if (!sessionData.getUniqueBreedingMethods().contains(result.getMname())) {
            sessionData.getUniqueBreedingMethods().add(result.getMname());
            sessionData.getProjectBreedingMethodData().put(result.getMid(), result);
        }

        view.refreshTable();

        return convertMethod(result);
    }

    public MethodView saveNewBreedingMethod(MethodView method) {
        if (!sessionData.getUniqueBreedingMethods().contains(method.getMname())) {

            sessionData.getUniqueBreedingMethods().add(method.getMname());

            Integer nextKey = sessionData.getProjectBreedingMethodData().keySet().size() + 1;

            nextKey = nextKey * -1;

            MethodView newBreedingMethod = new MethodView();

            newBreedingMethod.setMname(method.getMname());
            newBreedingMethod.setMdesc(method.getMdesc());
            newBreedingMethod.setMcode(method.getMcode());
            newBreedingMethod.setMgrp(method.getMgrp());
            newBreedingMethod.setMtype(method.getMtype());
            newBreedingMethod.setGeneq(method.getGeneq());

            newBreedingMethod.setMid(nextKey);

            sessionData.getProjectBreedingMethodData().put(nextKey, newBreedingMethod);

            LOG.info(sessionData.getProjectBreedingMethodData().toString());

            if (sessionData.getUserData() != null)
                newBreedingMethod.setUser(sessionData.getUserData().getUserid());

            newBreedingMethod.setLmid(0);
            newBreedingMethod.setMattr(0);
            newBreedingMethod.setMprgn(0);
            newBreedingMethod.setReference(0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            newBreedingMethod.setMdate(Integer.parseInt(sdf.format(new Date())));
            newBreedingMethod.setMfprg(0);

            // ADD TO MIDDLEWARE LOCAL
            try {
                newBreedingMethod.setMid(gdm.addMethod(newBreedingMethod.copy()));

            } catch (Exception e) { // we might have null exception, better be prepared
                e.printStackTrace();
            }

            newBreedingMethod.setIsnew(true);

            view.addRow(newBreedingMethod,false,0);

            /*
            User user = app.getSessionData().getUserData();
            Project currentProject = app.getSessionData().getLastOpenedProject();
            ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, "Project Methods", "Added a new Breeding Method ("+ newMethod.getMname() + ")", user, new Date());
            try {
				workbenchDataManager.addProjectActivity(projAct);
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */
            return newBreedingMethod;
        }

        return method;
    }

    public boolean saveFavoriteBreedingMethod(Collection<MethodView> selectedMethodIds) {
        return saveFavoriteBreedingMethod(selectedMethodIds, this.project, this.sessionData, this.workbenchDataManager, this.gdm);
    }

    public static boolean saveFavoriteBreedingMethod(Collection<MethodView> selectedMethodIds, Project project, SessionData sessionData, WorkbenchDataManager workbenchDataManager, GermplasmDataManager gdm) {
        List<ProgramFavorite> favorites = null;
        try {
        	favorites = gdm.getProgramFavorites(ProgramFavorite.FavoriteType.METHOD);

            //TODO: THIS IS A VERY UGLY CODE THAT WAS INHERITED IN THE OLD ProjectBreedingMethodsPanel Code, Replace the logic if possible

            for (Method m : selectedMethodIds) {
                boolean m_exists = false;

                for (ProgramFavorite favorite : favorites) {
                    if (favorite.getEntityId().equals(m.getMid())) m_exists = true;
                }

                if (!m_exists) {
                    if (sessionData.getUserData() != null)
                        workbenchDataManager.addProjectActivity(new ProjectActivity(project.getProjectId().intValue(),project,"Project Methods",String.format("Added a Breeding Method (%s) to the project",m.getMname()), sessionData.getUserData(),new Date()));
                }
            }   // code block just adds a log activity, replace by just tracking newly added methods id so no need to fetch all methods from DB

            
            gdm.deleteProgramFavorites(favorites);
            

            // Repopulate the project methods table
            List<ProgramFavorite> list = new ArrayList<ProgramFavorite>();
            int mID = 0;

            for (Method m : selectedMethodIds)   {
            	ProgramFavorite favorite = new ProgramFavorite();
                if (m.getMid() < 1) {
                    Method m2 = gdm.getMethodByID(m.getMid());

                    if (m2 == null) {
                        Method newMethod= new Method(m.getMid(), m.getMtype(), m.getMgrp(), m.getMcode(), m.getMname(), m.getMdesc(),0,0,0,0,0,0,0,m.getMdate());
                        mID = gdm.addMethod(newMethod);
                    } else {
                        mID = m2.getMid();
                    }
                } else {
                    mID = m.getMid();
                }

                favorite.setEntityType(ProgramFavorite.FavoriteType.METHOD.getName());
                favorite.setEntityId(mID);
                list.add(favorite);
            }

            gdm.saveProgramFavorites(list);
            

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            return false;
        }

        return true;
    }

    public MethodView convertMethod(Method method) {
        PropertyUtilsBean pub = new PropertyUtilsBean();
        MethodView methodView = new MethodView();
        try {
            pub.copyProperties(methodView,method);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return methodView;
    }

    public List<MethodView> convertFrom(List<Method> list) {
        List<MethodView> result = new ArrayList<MethodView>();
        for (Method method: list) {
            MethodView methodView = new MethodView();

            PropertyUtilsBean pub = new PropertyUtilsBean();
            try {
                pub.copyProperties(methodView,method);

                result.add(methodView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            return result;
    }

    public Collection<Method> convertTo(Collection<MethodView> list) {
        List<Method> result = new ArrayList<Method>();

        for (MethodView methodView:list) {
            result.add(methodView.copy());
        }
        return result;
    }

	public Map<Integer, String> getMethodClasses() {
		Map<Integer, String> methodClasses = new LinkedHashMap<Integer, String>();
		try {
			List<Term> terms = gdm.getMethodClasses();
			if(terms!=null) {
				for (Term term : terms) {
					methodClasses.put(term.getId(), term.getName());
				}
			}
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
		
		return methodClasses;
	}
}