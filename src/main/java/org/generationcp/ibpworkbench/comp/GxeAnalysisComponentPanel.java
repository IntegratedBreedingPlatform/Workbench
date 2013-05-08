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
import java.util.Iterator;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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
    
    private Table tblDataSet;
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
    	
    	
    	accord.addTab(tblDataSet);
    	accord.getTab(tblDataSet).setCaption("Table");
    	tab.addTab(accord);
    	tab.getTab(accord).setCaption(caption);
    	
    	return accord;
    }
    protected TabSheet generateTabSheet()
    {
    	TabSheet tab = new TabSheet();
    	initializeMembersTable();
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
    	
    	tblDataSet = new Table();
    	tblDataSet.setImmediate(true);
        
        final List<Object> columnIds = new ArrayList<Object>();
       
        
        List<String> columnHeaders = new ArrayList<String>();
       
        
        
        
        // prepare the container
        IndexedContainer container = new IndexedContainer();
        //container.addContainerProperty("userId", Integer.class, null);
        
        
        for (String role : stringList) {
        	
        	if(role.equalsIgnoreCase("Environment"))
            {
	            columnIds.add(role);
	            columnHeaders.add(role);
	            container.addContainerProperty(role, String.class, role);
	            
            }else{
	            columnIds.add(role);
	            columnHeaders.add(role);
	            container.addContainerProperty(role, Boolean.class, Boolean.TRUE);
	           
            }
        }
        
        
        tblDataSet.setContainerDataSource(container);
        
        tblDataSet.setVisibleColumns(columnIds.toArray(new Object[0]));
        tblDataSet.setColumnHeaders(columnHeaders.toArray(new String[0]));
        
        tblDataSet.setEditable(true);
        tblDataSet.setTableFieldFactory(new TableFieldFactory() {
            private static final long serialVersionUID = 1L;

            @Override
            public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                int columnIndex = columnIds.indexOf(propertyId);
               
                CheckBox cb;
                //itemId <-- row
                //propertyId <-- column
                
                System.out.println("itemId " + itemId);
                System.out.println("propertyId " + propertyId);
                
                if (!propertyId.toString().equalsIgnoreCase("environment")) {
                	//String caption = container.getItem(itemId).getItemProperty("caption_" + propertyId.toString()).getValue().toString();
                	String caption =  propertyId.toString();
                	
                	if(itemId.toString().equalsIgnoreCase("FirstRow") && propertyId.toString().equalsIgnoreCase(" "))
                	{
                		//propertyId
                		//select the whole column dapat ito
                		cb = new CheckBox("select all");
                		
                		cb.addListener(new Property.ValueChangeListener() {            
                            /**
							 * 
							 */
							private static final long serialVersionUID = 1L;

							@Override
                            public void valueChange(ValueChangeEvent event) {
                                // TODO Auto-generated method stub
                            	try{
	                               System.out.println(event);
	                               Container container = tblDataSet.getContainerDataSource();
	                               Collection<?> items =  container.getItemIds();
	                               
	                               
	                               for ( Iterator<?> myitems = items.iterator(); myitems.hasNext(); ) 
	                            	    
	                               {
	                            	   String key = (String) myitems.next();
	                            	   Item item = container.getItem(key);
	                            	   
	                            	   for (String role : stringList) 
	                            	   {
	                                  	 
	                                  	 if(role.equalsIgnoreCase("environment"))
	                                  	 {
	                                  		 //item.getItemProperty(role).setValue(" ");
	                                  	 }else if(role.equalsIgnoreCase(" ") && key.equalsIgnoreCase("FirstRow"))
	                                  	 {
	                                  		//item.getItemProperty(role).setValue(false);
	                                  		// item.getItemProperty(role).setValue(event.getProperty().getValue());
	                                  	 }else
	                                  		//item.getItemProperty(role).setValue(false);
	                                  		 item.getItemProperty(role).setValue(event.getProperty().getValue());
	                            	   }
	                               }
	                               
	                             //  System.out.println("property " + event.getProperty().getValue());
	                             requestRepaintAll();
	                            
                            }catch(Exception e)
                            { e.printStackTrace();}
                            }});
                		return cb;
                	}
                	else if(itemId.toString().equalsIgnoreCase("FirstRow"))
                	{
                		//propertyId
                		//select the whole column dapat ito
                		cb = new CheckBox();
                		
                		class CheckboxListener implements ValueChangeListener{
                			/**
							 * 
							 */
							private static final long serialVersionUID = 1L;
							private String propertyId;
                			public CheckboxListener (String propertyId)
                			{
                				this.propertyId = propertyId;
                			}
                            @Override
                            public void valueChange(ValueChangeEvent event) {
                                // TODO Auto-generated method stub
                            	try{
 	                               System.out.println(event);
 	                               Container container = tblDataSet.getContainerDataSource();
 	                               Collection<?> items =  container.getItemIds();
 	                               
 	                               
 	                               for ( Iterator<?> myitems = items.iterator(); myitems.hasNext(); ) 
 	                            	    
 	                               {
 	                            	   String key = (String) myitems.next();
 	                            	   Item item = container.getItem(key);
 	                            	   
 	                            	   if(!key.equalsIgnoreCase("FirstRow"))
 	                                  	item.getItemProperty(this.propertyId).setValue(event.getProperty().getValue());
 	                                  	
 	                               }
 	                               
 	                             //  System.out.println("property " + event.getProperty().getValue());
 	                             requestRepaintAll();
 	                            
                             }catch(Exception e)
                             { e.printStackTrace();}
                             }
							
                		}
                		
                		cb.addListener(new CheckboxListener(propertyId.toString()));
                		
                		return cb;
                	}else if(propertyId.toString().equalsIgnoreCase(" "))
                	{
                		//itemId
                		//select the whole row dapat ito
                		cb = new CheckBox();
                		class CheckboxListener implements ValueChangeListener{
                			/**
							 * 
							 */
							private static final long serialVersionUID = 1L;
							private String ItemId;
                			public CheckboxListener (String ItemId)
                			{
                				this.ItemId = ItemId;
                			}
                            @Override
                            public void valueChange(ValueChangeEvent event) {
                                // TODO Auto-generated method stub
                            	try{
 	                               System.out.println(event);
 	                               Container container = tblDataSet.getContainerDataSource();
 	                               Collection<?> items =  container.getItemIds();
 	                               
 	                               
 	                              
 	                               Item item = container.getItem(this.ItemId);
 	                            	   
 	                            	  for (String role : stringList) 
	                            	   {
	                                  	 
	                                  	 if(role.equalsIgnoreCase("environment"))
	                                  	 {
	                                  		 //item.getItemProperty(role).setValue(" ");
	                                  	 }else
	                                  		//item.getItemProperty(role).setValue(false);
	                                  		 item.getItemProperty(role).setValue(event.getProperty().getValue());
	                            	   }
 	                               
 	                               
 	                             //  System.out.println("property " + event.getProperty().getValue());
 	                             requestRepaintAll();
 	                            
                             }catch(Exception e)
                             { e.printStackTrace();}
                             }
							
                		}
                		
                		cb.addListener(new CheckboxListener(itemId.toString()));
                		return cb;
                	}else
                	{
                		cb = new CheckBox(caption);
                		return cb;
                	}
                     
                     
                }
                return null;
            }
        });
    }
    
    protected void initializeHeader() 
    {
    	 Container container = tblDataSet.getContainerDataSource();
    
         Collection<?> itemIds = container.getItemIds();
         String obj = "FirstRow";
         
         //Item item = container.addItem(obj);
         
         int cnt = 0;
         
         Item item = container.addItem(obj);
    
         for (String role : stringList) {
        	 
        	 if(role.equalsIgnoreCase("environment"))
        	 {
        		 item.getItemProperty(role).setValue(" ");
        		 
        		 
        	 }else if(role.equalsIgnoreCase(" "))
        	 {
        		 item.getItemProperty(role).setValue(true);
        	 }
        	 else
        	 {
        		 item.getItemProperty(role).setValue(true);
        	 }
        	 
        	 
         }
         
         TableFieldFactory tff = tblDataSet.getTableFieldFactory();
         
        
    }

	private void addNewDataSetItem(String StudyName, String StudyTab)
	{
		Container container = tblDataSet.getContainerDataSource();
	    
        Collection<?> itemIds = container.getItemIds();
        String obj = ""+countme;
        
        Item item = container.addItem(obj);
        
        for (String role : stringList) {
       	 
       	 if(role.equalsIgnoreCase("environment"))
       	 {
       		 item.getItemProperty(role).setValue(" ");
       		 
       		 
       	 }else if(role.equalsIgnoreCase(" "))
       	 {
       		 item.getItemProperty(role).setValue(true);
       	 }
       	 else
       	 {
       		 item.getItemProperty(role).setValue(true);
       	 }
       	
       }
        countme++;
	}
	private void setDataSetHeaders(String[] list)
	{
		stringList = list;
		
	}
    private void initializeMembersTable() {
    	
    	setDataSetHeaders(new String[] {" ","environment","height","maturity","rust","height 1"});
    	initializeTable();
    	initializeHeader();
    	addNewDataSetItem("environment","height");
    	addNewDataSetItem("environment","height");
    	//addNewDataSetItem("environment","height");
    
    
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
