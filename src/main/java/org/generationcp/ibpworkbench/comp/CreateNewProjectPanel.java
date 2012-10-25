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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CropTypeComboAction;
import org.generationcp.ibpworkbench.model.formfieldfactory.ProjectFormFieldFactory;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

@Deprecated
@Configurable
public class CreateNewProjectPanel extends VerticalLayout implements InitializingBean{

	private static final Logger LOG = LoggerFactory.getLogger(CreateNewProjectPanel.class);
    private static final long serialVersionUID = 1L;
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    private Label newProjectTitle;
    
    private Form projectForm;
    
    private ProjectFormFieldFactory projectFormFieldFactory;
    
    private Button cancelButton;

    private Button saveProjectButton;
    
    private Button showLocationWindowButton;

    private Component buttonArea;
	private GridLayout gridMethodLayout;
	private Select selectMethodType;
	private Select selectMethodGroup;
	private TwinColSelect selectMethods;
	private Button btnFilterMethod;
	private VerticalLayout methodsLayout;
    
    private final static String[] VISIBLE_ITEM_PROPERTIES = new String[] { "projectName", "startDate", "cropType", "template", "members", "locations" };

    public CreateNewProjectPanel() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        assemble();

        projectForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
        
    }

    public Button getShowLocationWindowButton() {
        return showLocationWindowButton;
    }
    
    public Button getSaveProjectButton() {
        return saveProjectButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Form getProjectForm() {
        return projectForm;
    }
    
    protected void initializeComponents() {
        newProjectTitle = new Label("Create New Project");
        newProjectTitle.setStyleName("gcp-content-title");

        addComponent(newProjectTitle);
        
        BeanItem<Project> projectBean = new BeanItem<Project>(new Project());
        
        projectFormFieldFactory = new ProjectFormFieldFactory();

        projectForm = new Form();
        projectForm.setItemDataSource(projectBean);
        projectForm.setFormFieldFactory(projectFormFieldFactory);
        addComponent(projectForm);
        addComponent(layoutMethodsArea());        
        
        cancelButton = new Button("Cancel");
        saveProjectButton = new Button("Save");
        
        showLocationWindowButton = new Button("Add Location");
        
        buttonArea = layoutButtonArea();
        addComponent(buttonArea);
        
    }
    
    private Component layoutMethodsArea() {
    	
    	methodsLayout= new VerticalLayout();
    	gridMethodLayout= new GridLayout();
    	gridMethodLayout.setRows(2);
    	gridMethodLayout.setColumns(7);
    	gridMethodLayout.setSpacing(true);
    	
    	selectMethodType= new Select();
    	selectMethodType.addItem("");
    	selectMethodType.addItem("GEN");
    	selectMethodType.setItemCaption("GEN", "Generative");
    	selectMethodType.addItem("DER");
    	selectMethodType.setItemCaption("DER", "Derivative");
    	selectMethodType.addItem("MAN");
    	selectMethodType.setItemCaption("MAN", "Maintenance");
    	selectMethodType.select("GEN");
    	selectMethodType.setNullSelectionAllowed(false);
    	
    	selectMethodGroup= new Select();
    	selectMethodGroup.addItem("");
    	selectMethodGroup.addItem("S");
    	selectMethodGroup.setItemCaption("S","Self Fertilizing");
    	selectMethodGroup.addItem("O");
    	selectMethodGroup.setItemCaption("O","Cross Pollinating");
    	selectMethodGroup.addItem("C");
    	selectMethodGroup.setItemCaption("C","Clonolly Propogating");
    	selectMethodGroup.addItem("G");
    	selectMethodGroup.setItemCaption("G","All System");
    	selectMethodGroup.select("");
    	selectMethodGroup.setNullSelectionAllowed(false);
    	
    	btnFilterMethod=new Button("Filter");
    	btnFilterMethod.addListener(new ClickListener() {
			
    		@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
    			selectMethods.removeAllItems();
		        CropType cropType = (CropType) projectForm.getItemProperty("cropType").getValue();
		        if (cropType != null) {
		            try {
		                Container container = createMethodsContainer(cropType);
		                selectMethods.setContainerDataSource(container);
		
		                for (Object itemId : container.getItemIds()) {
		                    Method method = (Method) itemId;
		                    
		                    selectMethods.setItemCaption(itemId, method.getMname());
		                }
		            }
		            catch (MiddlewareQueryException e) {
		                LOG.error("Error encountered while getting central methods", e);
		                throw new InternationalizableException(e, Message.DATABASE_ERROR, 
		                                                       Message.CONTACT_ADMIN_ERROR_DESC);
		            }
		        }
			}
    	});
    	
    	gridMethodLayout.addComponent(new Label("Select Methods Type To Filter"),1,1);
    	gridMethodLayout.addComponent(selectMethodType, 2, 1);
     	gridMethodLayout.addComponent(new Label("Select Methods Group To Filter"),3,1);
    	gridMethodLayout.addComponent(selectMethodGroup, 4, 1);
    	gridMethodLayout.addComponent(btnFilterMethod,5,1);
    	
        selectMethods = new TwinColSelect("");
        selectMethods.setLeftColumnCaption("Available Methods");
        selectMethods.setRightColumnCaption("Selected Methods");
        selectMethods.setRows(10);
        selectMethods.setWidth("700px");
        selectMethods.setMultiSelect(true);
        selectMethods.setNullSelectionAllowed(true);

        methodsLayout.addComponent(gridMethodLayout);
        methodsLayout.addComponent(selectMethods);

        return methodsLayout;
		
	}

	protected void initializeValues() {
        //set default value of Start Date to the current date
        projectForm.getField("startDate").setValue(new Date());
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);

        // set the save/cancel buttons
        setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);
    }
    
    protected void initializeActions() {
        Field field = projectForm.getField("cropType");
        CropTypeComboAction cropTypeComboAction = projectFormFieldFactory.getCropTypeComboAction();
//        cropTypeComboAction.setSourcePanel(this);
        field.addListener(cropTypeComboAction);
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        showLocationWindowButton = new Button("Add Location");
        cancelButton = new Button("Cancel");
        saveProjectButton = new Button("Save");
        
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveProjectButton);
        buttonLayout.addComponent(showLocationWindowButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();
    }
    
    public void refreshVisibleItems(){
        projectForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
    }
    
    private Container createMethodsContainer(CropType cropType) throws MiddlewareQueryException {
        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForCropType(cropType);
        
        BeanItemContainer<Method> beanItemContainer = new BeanItemContainer<Method>(Method.class);
        if (managerFactory == null) {
            return beanItemContainer;
        }
        String methodType=selectMethodType.getValue().toString();
        String methodGroup=selectMethodGroup.getValue().toString();
        
        List<Method> methodList=null;
        if(selectMethodType.getValue().toString()!="" && selectMethodGroup.getValue().toString()==""){
          methodList = managerFactory.getGermplasmDataManager().getMethodsByType(selectMethodType.getValue().toString());
        }else if(selectMethodType.getValue().toString()=="" && selectMethodGroup.getValue().toString()!=""){
        	methodList = managerFactory.getGermplasmDataManager().getMethodsByGroup(selectMethodGroup.getValue().toString());
        }else if(selectMethodType.getValue().toString()!="" && selectMethodGroup.getValue().toString()!=""){
        	methodList = managerFactory.getGermplasmDataManager().getMethodsByGroupAndType(methodGroup, methodType);
        }else{
        	methodList = managerFactory.getGermplasmDataManager().getAllMethods();
        }
        
        for (Method method : methodList) {
            beanItemContainer.addBean(method);
        }
        
        return beanItemContainer;
    }

	public TwinColSelect getSelectMethods() {
		return selectMethods;
	}

}
