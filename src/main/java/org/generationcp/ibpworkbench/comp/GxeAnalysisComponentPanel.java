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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.SaveUsersInProjectAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;


/**
 * The third tab (Project Members) in Create Project Accordion Component.
 * 
 * @author Aldrich Abrogena
 */
@SuppressWarnings("unchecked")
@Configurable
public class GxeAnalysisComponentPanel extends VerticalLayout implements InitializingBean{
    
    private static final Logger LOG = LoggerFactory.getLogger(GxeAnalysisComponentPanel.class);
    private static final long serialVersionUID = 1L;
    
   
 //   private TwinColSelect select;
    
    private Button newMemberButton;
    private Button saveButton;
    
    private Table tblMembers;
    private Accordion accordion;
    private Tree studiesTree;
    private Object[][] studies;
    private String[] stringList;
    private Panel studiesPanel;
    private TabSheet studiesTabsheet;
    private Button previousButton;
//    private Button nextButton;
    private Component buttonArea;
    private int countme = 0;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    private Project project;
    
    private  List<Role> inheritedRoles;

    public GxeAnalysisComponentPanel(Project project) {
    	//System.out.println("Project is " + project.getProjectName());
        this.project = project;
    }


    @Override
    public void afterPropertiesSet() throws Exception {       
        assemble();
    }

    protected void assemble() {
        initializeComponents();
      
        initializeLayout();
        initializeActions();
       
    }

    protected void createStudies()
    {
    	studies = new Object[][]{
                new Object[]{"Mercury"}, 
                new Object[]{"Venus"},
                new Object[]{"Earth", "The Moon"},    
                new Object[]{"Mars", "Phobos", "Deimos"},
                new Object[]{"Jupiter", "Io", "Europa", "Ganymedes",
                                        "Callisto"},
                new Object[]{"Saturn",  "Titan", "Tethys", "Dione",
                                        "Rhea", "Iapetus"},
                new Object[]{"Uranus",  "Miranda", "Ariel", "Umbriel",
                                        "Titania", "Oberon"},
                new Object[]{"Neptune", "Triton", "Proteus", "Nereid",
                                        "Larissa"}};
    }
    protected void refreshStudies()
    {
    	 /* Add planets as root items in the tree. */
        for (int i=0; i<studies.length; i++) {
            String planet = (String) (studies[i][0]);
            studiesTree.addItem(planet);
            
            if (studies[i].length == 1) {
                // The planet has no moons so make it a leaf.
            	studiesTree.setChildrenAllowed(planet, false);
            } else {
                // Add children (moons) under the planets.
                for (int j=1; j<studies[i].length; j++) {
                    String moon = (String) studies[i][j];
                    
                    // Add the item as a regular item.
                    studiesTree.addItem(moon);
                    
                    // Set it to be a child.
                    studiesTree.setParent(moon, planet);
                    
                    // Make the moons look like leaves.
                    studiesTree.setChildrenAllowed(moon, false);
                }

                // Expand the subtree.
                studiesTree.expandItemsRecursively(studies);
            }
        }
        
        
    }
    
    protected Accordion generateTabComponent(TabSheet tab, String caption)
    {
    	
    	Accordion accord = new Accordion();
    	initializeMembersTable();
    	
    	accord.addTab(tblMembers);
    	accord.getTab(tblMembers).setCaption("Table");
    	tab.addTab(accord);
    	tab.getTab(accord).setCaption(caption);
    	
    	return accord;
    }
    protected TabSheet generateTabSheet()
    {
    	TabSheet tab = new TabSheet();
    	generateTabComponent(tab, "ANM87AMA");
    	generateTabComponent(tab, "ANM87ABA");
    	generateTabComponent(tab, "ANM87ABK");
    	generateTabComponent(tab, "AAAAAA");
    	
    	tab.setWidth("800px");
        tab.setHeight("700px");
        
    	return tab;
    }
    protected void initializeComponents(){

       setSpacing(true);
       setMargin(true);
       createStudies();
       
       initializeMembersTable();
       
       HorizontalLayout horizontal = new HorizontalLayout();
       
       
       studiesTree = new Tree("Studies");
       studiesTree.setImmediate(true);
       studiesPanel = new Panel();
       refreshStudies();
       
        
        
       
        
        
        studiesTabsheet = generateTabSheet();
        
        
        
        studiesPanel.addComponent(studiesTree);
        
        studiesPanel.setWidth("200px");
        studiesPanel.setHeight("700px");
        
        
        
        horizontal.addComponent(studiesPanel);
        horizontal.addComponent(generateTabSheet());
        
      
        addComponent(horizontal);
        
    }
    
  

   
    private void initializeTable()
    {
    	
        tblMembers = new Table();
        tblMembers.setImmediate(true);
        
        final List<Object> columnIds = new ArrayList<Object>();
       
        
        List<String> columnHeaders = new ArrayList<String>();
       
        
        
        
        // prepare the container
        IndexedContainer container = new IndexedContainer();
        //container.addContainerProperty("userId", Integer.class, null);
        //container.addContainerProperty("userName", String.class, null);
        
        for (String role : stringList) {
        	
        	if(role.equalsIgnoreCase("Environment"))
            {
	            columnIds.add("role_" +role);
	            columnHeaders.add(role);
	            container.addContainerProperty("role_" + role, String.class, role);
            }else{
	            columnIds.add("role_" +role);
	            columnHeaders.add(role);
	            container.addContainerProperty("role_" + role, Boolean.class, Boolean.TRUE);
            }
        }
        
        
        tblMembers.setContainerDataSource(container);
        
        tblMembers.setVisibleColumns(columnIds.toArray(new Object[0]));
        tblMembers.setColumnHeaders(columnHeaders.toArray(new String[0]));
        
        tblMembers.setEditable(true);
        tblMembers.setTableFieldFactory(new TableFieldFactory() {
            private static final long serialVersionUID = 1L;

            @Override
            public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                int columnIndex = columnIds.indexOf(propertyId);
               
                if (!propertyId.toString().equalsIgnoreCase("role_environment")) {
                    return new CheckBox(propertyId.toString());
                }
                return null;
            }
        });
    }
    
    protected void initializeContents() 
    {
    	 Container container = tblMembers.getContainerDataSource();
    	 
    	
    	 System.out.println("countme : "+ countme);
    	 countme++;
    	 
         Collection<?> itemIds = container.getItemIds();
         Object obj = new Object();
         
         //Item item = container.addItem(obj);
         
         int cnt = 0;
         Item item = container.addItem(obj);
         for (String role : stringList) {
        	 
        	 if(role.equalsIgnoreCase("environment"))
        	 {
        		 item.getItemProperty("role_" +role).setValue("Environment");
        	 }else
        	 {
        		 item.getItemProperty("role_" +role).setValue(true);
        	 }
        	 
         }
        
    }

	
    private void initializeMembersTable() {
    	
    	stringList = new String[] {" ","environment","height","maturity","rust","height 1"};
    	initializeTable();
    	initializeContents(); 
    	
    
    
    }

   

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
      
    }
    	
    
    protected void initializeActions() {
      //  newMemberButton.addListener(new OpenNewProjectAddUserWindowAction(select));
       studiesTree.addListener(new StudiesTreeAction());
        
        
    }
    
    private class StudiesTreeAction implements ValueChangeListener
    {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			// TODO Auto-generated method stub
			System.out.println(event);
			
		}
    	
    }



    


  
        
       
        
        
}
