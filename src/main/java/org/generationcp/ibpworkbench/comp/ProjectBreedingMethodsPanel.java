/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.comp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CancelMethodsAction;
import org.generationcp.ibpworkbench.actions.OpenAddBreedingMethodWindowAction;
import org.generationcp.ibpworkbench.actions.SaveProjectMethodsAction;
import org.generationcp.ibpworkbench.comp.common.TwoColumnSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *  @author Jeffrey Morales, Joyce Avestro
 *  
 */
@Configurable
public class ProjectBreedingMethodsPanel extends VerticalLayout implements InitializingBean {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ProjectBreedingMethodsPanel.class);

    private Project project;
    private Role role;
    private Component buttonArea;
    private Button saveLocationButton;
    private Button cancelButton;
    private Button btnFilterMethod;
    BeanItemContainer<Method> beanItemContainer;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    private GridLayout gridMethodLayout;
    private Select selectMethodType;
    private Select selectMethodGroup;
    private TwoColumnSelect selectMethods;
    private VerticalLayout methodsLayout;
    private Button addMethodsWindowButton;
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private ManagerFactory managerFactory;
	
	private Window bmPopupWindow;

	private ProjectBreedingMethodsPanel thisInstance;
	
	public ProjectBreedingMethodsPanel(Project project, Role role) {
        this.project = project;
        this.role = role;
        
        this.thisInstance = this;
    }


    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
        
        assemble();
    }
    
    protected void assemble() throws MiddlewareQueryException {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();
    }

    protected void initializeComponents() throws MiddlewareQueryException {    		
    	setSpacing(true);
        setMargin(true);
        
        addComponent(layoutMethodsArea());
        buttonArea = layoutButtonArea();
        addComponent(buttonArea);

    }

    protected void initializeValues() throws MiddlewareQueryException {

    }

    protected void initializeLayout() {
    	setSizeFull();
        setSpacing(true);
        setMargin(true);
        setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);
    }

    protected void initializeActions() {
    	addMethodsWindowButton.addListener(new OpenAddBreedingMethodWindowAction(this));
        saveLocationButton.addListener(new SaveProjectMethodsAction(this));
        cancelButton.addListener(new CancelMethodsAction(this));
        
        selectMethods.getLeftSelect().setImmediate(true);
        selectMethods.getLeftSelect().addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
			public void valueChange(ValueChangeEvent event) {
				ProjectBreedingMethodsPanel.LOG.debug("ValueChangeEvent triggered");
				
				final Window parentWindow = thisInstance.getWindow();
				
				Object selectedItem = selectMethods.getLeftSelect().getValue();
				if (selectedItem instanceof Set) {
					ProjectBreedingMethodsPanel.LOG.debug("Set returned, either items moved to right or left column is multi selected");
					
					@SuppressWarnings("unchecked")
                    Set<Method> methodSet = (Set<Method>) selectedItem;
					
					if (methodSet.size() == 0) {
						if (bmPopupWindow != null) {
							parentWindow.removeWindow(bmPopupWindow);
						}
					} else if (methodSet.size() > 0) {
					    List<Method> selectedMethods = new ArrayList<Method>(methodSet);
						openWindow(parentWindow,selectedMethods);
					}
					
				}
				
			}});        
    }
    
    private void openWindow(Window parentWindow,List<Method> selectedMethods) {
        if (bmPopupWindow != null) {
            parentWindow.removeWindow(bmPopupWindow);
        }

        bmPopupWindow = new ProjectBreedingMethodsPopup(selectedMethods);
        bmPopupWindow.setPositionX(97);
        bmPopupWindow.setPositionY(408);

        parentWindow.addWindow(bmPopupWindow);
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        addMethodsWindowButton = new Button(messageSource.getMessage(Message.METHODS_ADD_NEW)); //"Add New Breeding Methods"
        cancelButton = new Button(messageSource.getMessage(Message.CANCEL)); //"Cancel"
        saveLocationButton = new Button(messageSource.getMessage(Message.SAVE_CHANGES)); //"Save Changes"

        buttonLayout.addComponent(addMethodsWindowButton);
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveLocationButton);
        return buttonLayout;
    }

    @SuppressWarnings("unchecked")
    private Component layoutMethodsArea() throws MiddlewareQueryException {
    	 methodsLayout = new VerticalLayout();
         gridMethodLayout = new GridLayout();
         gridMethodLayout.setRows(3);
         gridMethodLayout.setColumns(4);
         gridMethodLayout.setSpacing(true);

         selectMethodType = new Select();
         selectMethodType.addItem("");
         selectMethodType.addItem("GEN");
         selectMethodType.setItemCaption("GEN", "Generative");
         selectMethodType.addItem("DER");
         selectMethodType.setItemCaption("DER", "Derivative");
         selectMethodType.addItem("MAN");
         selectMethodType.setItemCaption("MAN", "Maintenance");
         selectMethodType.select("GEN");
         selectMethodType.setNullSelectionAllowed(false);

         selectMethodGroup = new Select();
         selectMethodGroup.addItem("");
         selectMethodGroup.addItem("S");
         selectMethodGroup.setItemCaption("S", "Self Fertilizing");
         selectMethodGroup.addItem("O");
         selectMethodGroup.setItemCaption("O", "Cross Pollinating");
         selectMethodGroup.addItem("C");
         selectMethodGroup.setItemCaption("C", "Clonolly Propogating");
         selectMethodGroup.addItem("G");
         selectMethodGroup.setItemCaption("G", "All System");
         selectMethodGroup.select("");
         selectMethodGroup.setNullSelectionAllowed(false);

         btnFilterMethod = new Button("Filter");
         btnFilterMethod.addListener(new ClickListener() {

             private static final long serialVersionUID = 1L;

             @Override
             public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
             	
             	Set<Method> selectedMethod = (Set<Method>)selectMethods.getValue(); 
             	
                 selectMethods.removeAllItems();
                 CropType cropType = project.getCropType();
                 if (cropType != null) {
                     try {
                         Container container = createMethodsContainer(cropType,selectedMethod);
                         selectMethods.setContainerDataSource(container);

                         for (Object itemId : container.getItemIds()) {
                             Method method = (Method) itemId;
                             selectMethods.setItemCaption(itemId, method.getMname());
                         }
                         
                         if(selectedMethod.size() >0){
                         	for(Method method:selectedMethod){
                         		selectMethods.select(method);
                         		selectMethods.setValue(method);
                         	}
                         	
                         }
                         
                     } catch (MiddlewareQueryException e) {
                         LOG.error("Error encountered while getting central methods", e);
                         throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                     }
                 }
             }
         });

         gridMethodLayout.addComponent(new Label("Select Methods Group To Filter"), 1, 1);
         gridMethodLayout.addComponent(selectMethodGroup, 2, 1);
         gridMethodLayout.addComponent(new Label("Select Methods Type To Filter"), 1, 2);
         gridMethodLayout.addComponent(selectMethodType, 2, 2);
         gridMethodLayout.addComponent(btnFilterMethod, 3, 1);

         selectMethods = new TwoColumnSelect("");
         selectMethods.setLeftColumnCaption("Available Methods");
         selectMethods.setRightColumnCaption("Selected Methods");
         selectMethods.setRows(10);
         selectMethods.setWidth("690px");
         selectMethods.setMultiSelect(true);
         selectMethods.setNullSelectionAllowed(true);

         CropType cropType = project.getCropType();
         if (cropType != null) {
             try {
             	Set<Method> selectedMethod = (Set<Method>)selectMethods.getValue(); 
                 Container container = createMethodsContainer(cropType,selectedMethod);
                 selectMethods.setContainerDataSource(container);

                 for (Object itemId : container.getItemIds()) {
                     Method method = (Method) itemId;
                     selectMethods.setItemCaption(itemId, method.getMname());
                 }
                 populateExistingBreedingMethods();
             } catch (MiddlewareQueryException e) {
                 LOG.error("Error encountered while getting central methods", e);
                 throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
             }
         }

         selectMethods.getRightSelect().select(selectMethods.getRightSelect().getNullSelectionItemId());
         
         methodsLayout.addComponent(gridMethodLayout);
         methodsLayout.addComponent(selectMethods);

         return methodsLayout;

    }
    public ManagerFactory getManagerFactory()
    {
    	return managerFactory;
    }
    private Container createMethodsContainer(CropType cropType, Set<Method> selectedMethod) throws MiddlewareQueryException {
       // ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForCropType(cropType);
    	 ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
         
        
        beanItemContainer = new BeanItemContainer<Method>(Method.class);
        if (managerFactory == null) {
            return beanItemContainer;
        }
        String methodType = "";
        if(selectMethodType.getValue() != null){
            methodType = selectMethodType.getValue().toString();
        }
        String methodGroup = "";
        if(selectMethodGroup.getValue() != null){
            methodGroup = selectMethodGroup.getValue().toString();
        }

        List<Method> methodList = null;
        if (!methodType.equals("") && methodGroup.equals("")) {
            methodList = managerFactory.getGermplasmDataManager().getMethodsByType(methodType);
        } else if (methodType.equals("") && !methodGroup.equals("")) {
            methodList = managerFactory.getGermplasmDataManager().getMethodsByGroupIncludesGgroup(methodGroup);
        } else if (!methodType.equals("") && !methodGroup.equals("")) {
            methodList = managerFactory.getGermplasmDataManager().getMethodsByGroupAndType(methodGroup, methodType);
        } else {
            methodList = managerFactory.getGermplasmDataManager().getAllMethods();
        }

        for (Method method : methodList) {
        	if (!selectedMethod.contains(method)){
        		beanItemContainer.addBean(method);
        	}
        }

        return beanItemContainer;
    }
    

    public boolean validate() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean validateAndSave() {

        if (validate()) { // save if valid
        	Set<Method> methods = (Set<Method>) selectMethods.getValue();
            //Save project
            try {
                saveProjectMethods(methods, project);
            } catch (MiddlewareQueryException e) {
                LOG.error("Error encountered while saving project locations", e);
                throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
            }
        }
        return true; 
    }
    
    
    private void populateExistingBreedingMethods() throws MiddlewareQueryException {
        Long projectId = project.getProjectId();
        List<Integer> projectMethodsIds = workbenchDataManager.getMethodIdsByProjectId(projectId, 0, (int) workbenchDataManager.countMethodIdsByProjectId(projectId));


        Set<Method> existingProjectMethods = new HashSet<Method>(); 
        for (Integer methodsId : projectMethodsIds){
            Method method = managerFactory.getGermplasmDataManager().getMethodByID(methodsId);
            if (method != null){
                existingProjectMethods.add(method);
                //if(method.getMid() < 1){
                    selectMethods.addItem(method);
                    selectMethods.setItemCaption(method, method.getMname());
                //}
            }
        }


        // Add existing project methods to selection
        if (existingProjectMethods.size() > 0) {
            for (Method method : existingProjectMethods) {
                selectMethods.select(method);
                selectMethods.setValue(method);
            }
        }
    }
    
    
    private void saveProjectMethods(Set<Method> methods, Project projectSaved) throws MiddlewareQueryException {
        GermplasmDataManager germplasmDataManager = managerFactory.getGermplasmDataManager();

        List<ProjectMethod> projectMethods = workbenchDataManager.getProjectMethodByProject(project, 0, (int) workbenchDataManager.countMethodIdsByProjectId(project.getProjectId()));
        
        for(Method m: methods){
        	Boolean method_exists = false;
        	for (ProjectMethod pmethod : projectMethods){
        		if (pmethod.getMethodId().equals(m.getMid())) method_exists = true;
        	}
        	if (!method_exists){
        		 IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
            	 User user = app.getSessionData().getUserData();
                 ProjectActivity projAct = new ProjectActivity(new Integer(projectSaved.getProjectId().intValue()), projectSaved, "Project Methods", "Added a Breeding Method ("+ m.getMname() + ") to the project", user, new Date());
                 try {
     				workbenchDataManager.addProjectActivity(projAct);
     				} catch (MiddlewareQueryException e) {
     				// TODO Auto-generated catch block
     				e.printStackTrace();
     				}
        	}
        }
        
        
        
        for (ProjectMethod projectMethod : projectMethods){
            workbenchDataManager.deleteProjectMethod(projectMethod);
        }
        //delete all method first in the local database
        //List<Method> methodsList = germplasmDataManager.getAllMethods();
        //for (Method method : methodsList){
        //    germplasmDataManager.deleteMethod(method);
        //}

        List<ProjectMethod> projectMethodList = new ArrayList<ProjectMethod>();
        int mID = 0;
        for (Method m : methods) {
            ProjectMethod projectMethod = new ProjectMethod();
            if(m.getMid() < 1){
                //save the added  method to the local database if doesn't exist
            	Method m2 = germplasmDataManager.getMethodByID(m.getMid());
                if (m2==null){
                	
                	Method newMethod= new Method(m.getMid(), m.getMtype(), m.getMgrp(), m.getMcode(), m.getMname(), m.getMdesc(),0,0,0,0,0,0,0,m.getMdate());
                	mID = germplasmDataManager.addMethod(newMethod);
                	
                }else{
                	mID = m2.getMid();
                }
            }else{
                mID=m.getMid();
            }
            
            projectMethod.setMethodId(mID);
            projectMethod.setProject(projectSaved);
            projectMethodList.add(projectMethod);
        }
        workbenchDataManager.addProjectMethod(projectMethodList);
    }
    
    
    public TwoColumnSelect getSelect() {
        return selectMethods;
    }

    @Override
    public void attach() {
        super.attach();
    }
    
    @Override
    public void detach() {
        super.detach();
        
        final Window parentWindow = thisInstance.getApplication().getMainWindow();
        parentWindow.removeWindow(bmPopupWindow);
    }
    
    class ProjectBreedingMethodsPopup extends Window {
        private static final long serialVersionUID = 1L;
        
        private VerticalLayout main = new VerticalLayout();
    	
    	private ProjectBreedingMethodsPopup() {
    		main.setSpacing(true);
    		main.setMargin(false);
    		
    		this.setCaption("Breeding Method Details");
    		
    		this.setResizable(false);
    		this.setScrollable(true);
    		this.setDraggable(true);
    		this.setWidth("400px");
    		this.setHeight("180px");

    		
    		setContent(main);
    	}
    	
    	
    	public ProjectBreedingMethodsPopup(List<Method> selectedMethods) {
    		this();
    		
    		
    		Collections.sort(selectedMethods,new Comparator<Method>() {
				@Override
				public int compare(Method o1, Method o2) {
					return o1.getMname().compareTo(o2.getMname());
				}
			});
    		
    		for (int i = 0; i < selectedMethods.size(); i++) {
    			if (i % 2 == 0)
    				init(selectedMethods.get(i),false);
    			else {
    				init(selectedMethods.get(i),true);
    			}
    		}
    		
    		
    	}
    	
    	public ProjectBreedingMethodsPopup(Method m) {
    		this();
    		
    		init(m,false);
    	}
    
    	private void init(Method m,boolean isOdd) {
    		
    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
    		
    		Date date;
			try {
				
				String formattedDate;
				try{
					date = df.parse(String.valueOf(m.getMdate()));
					formattedDate = (new SimpleDateFormat("MM/dd/yyyy")).format(date);
				}catch(ParseException e){
					formattedDate = null;
				}
				
				setBreedingMethodDetailsValues(m.getMname(),m.getMdesc(),m.getMgrp(),m.getMcode(),m.getMtype(),formattedDate,isOdd);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	public void setBreedingMethodDetailsValues(String mtitle, String mdesc,String mgrp, String mcode,String mtype, String mdate,boolean isOdd) {
	   		 Label mtitleLbl = new Label(mtitle);
	   		 Label mdescLbl = new Label(mdesc);
	   		 Label mgrpLbl = new Label(mgrp);
	   		 Label mcodeLbl = new Label(mcode);
	   		 Label mdateLbl = new Label(mdate);
	   		 Label mtypeLbl = new Label(mtype);
	   		
			CustomLayout c = new CustomLayout("breedingMethodsPopupLayout");
   			c.addStyleName("bmPopupLayout");
   			
   			if (isOdd)
   				c.addStyleName("odd");
   			
			c.addComponent(mtitleLbl,"mtitle");
	   		c.addComponent(mdescLbl,"mdesc");
	   		c.addComponent(mgrpLbl,"mgrp");
	   		c.addComponent(mcodeLbl,"mcode");
	   		c.addComponent(mtypeLbl,"mtype");
	   		if (mdate != null)
	   		c.addComponent(mdateLbl,"mdate");
	   	
	   		main.addComponent(c);
    	}    	
    }
}
