package org.generationcp.ibpworkbench.projectmethods;

import com.vaadin.ui.CustomComponent;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 11/11/13
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */

@Configurable
public class ProjectMethodsPresenter extends CustomComponent implements InitializingBean {
    private final Project project;
    private final Role role;
    private ProjectMethodsView view;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    private GermplasmDataManager gdm;


    public  ProjectMethodsPresenter(ProjectMethodsView view, Project project, Role role) {
        this.view = view;
        this.project = project;
        this.role = role;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.gdm = managerFactoryProvider.getManagerFactoryForProject(project).getGermplasmDataManager();
    }

    public void doMoveToSelectedMethod(Integer id) {
        Method selectedMethod = null;
        try {
            selectedMethod = gdm.getMethodByID(id);

            view.addRow(selectedMethod,false,0);

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public void doRemoveSelectedMethod(Integer id) {
        Method selectedMethod = null;
        try {
            selectedMethod = gdm.getMethodByID(id);

            view.addRow(selectedMethod,true,0);

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public ManagerFactory getManagerFactory() {
        return managerFactoryProvider.getManagerFactoryForProject(project);

    }

    public List<Method> getSavedProjectMethods() {
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

        return result;
    }

    public List<Method> getFilteredResults(String mgroup,String mtype,String mname) {
        try {
            return gdm.getMethodsByGroupAndTypeAndName(mgroup,mtype,mname);
            //return gdm.getMethodsByGroupAndType(mgroup,mtype);
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }

        return new ArrayList<Method>();
    }

    public boolean saveProjectLocation(ArrayList<Integer> selectedMethodIds) {
        List<Method> selectedMethods = new ArrayList<Method>();

        List<ProjectMethod> projectMethods = null;
        try {
            for (Integer i : selectedMethodIds) {
                selectedMethods.add(gdm.getMethodByID(i));
            }

            projectMethods = workbenchDataManager.getProjectMethodByProject(project,0,Integer.MAX_VALUE);

            //TODO: THIS IS A VERY UGLY CODE THAT WAS INHERITED IN THE OLD ProjectBreedingMethodsPanel Code, Replace the logic if possible

            for (Method m : selectedMethods) {
                boolean m_exists = false;

                for (ProjectMethod pmethod : projectMethods) {
                    if (pmethod.getMethodId().equals(m.getMid())) m_exists = true;
                }

                if (!m_exists) {
                        workbenchDataManager.addProjectActivity(new ProjectActivity(project.getProjectId().intValue(),project,"Project Methods",String.format("Added a Breeding Method (%s) to the project",m.getMname()), IBPWorkbenchApplication.get().getSessionData().getUserData(),new Date()));
                }
            }   // code block just adds a log activity, replace by just tracking newly added methods id so no need to fetch all methods from DB

            // delete all project methods
            for (ProjectMethod projectMethod : projectMethods) {
                workbenchDataManager.deleteProjectMethod(projectMethod);
            }

            // Repopulate the project methods table
            List<ProjectMethod> projectMethodList = new ArrayList<ProjectMethod>();
            int mID = 0;

            for (Method m : selectedMethods)   {
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

}