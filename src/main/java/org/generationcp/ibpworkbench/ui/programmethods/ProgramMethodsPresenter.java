package org.generationcp.ibpworkbench.ui.programmethods;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.IWorkbenchSession;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.lang.reflect.InvocationTargetException;
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
    private Project project;
    private CropType cropType;
    private ProgramMethodsView view;

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
           List<Integer> projectMethodsIds = workbenchDataManager.getMethodIdsByProjectId(project.getProjectId(), 0, Integer.MAX_VALUE);

            for (Integer id : projectMethodsIds) {
                Method m = gdm.getMethodByID(id);

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

    public boolean saveProgramMethod(Collection<MethodView> selectedMethodIds) {
        return saveProgramMethod(selectedMethodIds,this.project,this.sessionData,this.workbenchDataManager,this.gdm);
    }

    public static boolean saveProgramMethod(Collection<MethodView> selectedMethodIds,Project project,SessionData sessionData,WorkbenchDataManager workbenchDataManager,GermplasmDataManager gdm) {
        List<ProjectMethod> projectMethods = null;
        try {
            projectMethods = workbenchDataManager.getProjectMethodByProject(project,0,Integer.MAX_VALUE);

            //TODO: THIS IS A VERY UGLY CODE THAT WAS INHERITED IN THE OLD ProjectBreedingMethodsPanel Code, Replace the logic if possible

            for (Method m : selectedMethodIds) {
                boolean m_exists = false;

                for (ProjectMethod pmethod : projectMethods) {
                    if (pmethod.getMethodId().equals(m.getMid())) m_exists = true;
                }

                if (!m_exists) {
                    if (sessionData.getUserData() != null)
                        workbenchDataManager.addProjectActivity(new ProjectActivity(project.getProjectId().intValue(),project,"Project Methods",String.format("Added a Breeding Method (%s) to the project",m.getMname()), sessionData.getUserData(),new Date()));
                }
            }   // code block just adds a log activity, replace by just tracking newly added methods id so no need to fetch all methods from DB

            // delete all project methods
            for (ProjectMethod projectMethod : projectMethods) {
                workbenchDataManager.deleteProjectMethod(projectMethod);
            }

            // Repopulate the project methods table
            List<ProjectMethod> projectMethodList = new ArrayList<ProjectMethod>();
            int mID = 0;

            for (Method m : selectedMethodIds)   {
                ProjectMethod projectMethod = new ProjectMethod();
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

                projectMethod.setMethodId(mID);
                projectMethod.setProject(project);
                projectMethodList.add(projectMethod);
            }

            workbenchDataManager.addProjectMethod(projectMethodList);

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

}