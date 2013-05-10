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
import java.util.Iterator;
import java.util.List;

import org.generationcp.ibpworkbench.util.TableItems;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
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
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
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

    protected Object[][] createStudies()
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
    	
    	return studies;
    }
    protected void refreshStudies()
    {
    	 /* Add planets as root items in the tree. */
        for (int i=0; i<studies.length; i++) {
            String study = (String) (studies[i][0]);
            studiesTree.addItem(study);
            
            if (studies[i].length == 1) {
                // The planet has no moons so make it a leaf.
            	studiesTree.setChildrenAllowed(study, false);
            } else {
                // Add children (moons) under the planets.
                for (int j=1; j<studies[i].length; j++) {
                    String childStudy = (String) studies[i][j];
                    
                    // Add the item as a regular item.
                    studiesTree.addItem(childStudy);
                    
                    // Set it to be a child.
                    studiesTree.setParent(childStudy, study);
                    
                    // Make the moons look like leaves.
                    studiesTree.setChildrenAllowed(childStudy, false);
                }

                // Expand the subtree.
                studiesTree.expandItemsRecursively(studies);
            }
        }
        
        
    }
    
    protected Accordion generateTabComponent(TabSheet tab, String caption)
    {
    	
    	Accordion accord = new Accordion();
    	tblDataSet.setWidth("800px");
    	tblDataSet.setHeight("700px");
    	
    	accord.addTab(tblDataSet);
    	accord.getTab(tblDataSet).setCaption("Table");
    	tab.addTab(accord);
    	tab.getTab(accord).setCaption(caption);
    	
    	return accord;
    }
    
    protected void initializeComponents(){

       setSpacing(true);
       setMargin(true);
       createStudies();
       
       
       HorizontalLayout horizontal = new HorizontalLayout();
       
       
       
       studiesTree = new Tree("Studies");
       studiesTree.setImmediate(true);
       studiesPanel = new Panel();
       refreshStudies();
       
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
        final List<String> columnHeaders = new ArrayList<String>();
       
        
        
        
        // prepare the container
        IndexedContainer container = new IndexedContainer();
        //container.addContainerProperty("userId", Integer.class, null);
        
        
        for (String role : stringList) {
        	
        	if(role.equalsIgnoreCase("Environment") || role.equalsIgnoreCase("genotype"))
            {
	            columnIds.add(role);
	            columnHeaders.add(role);
	            container.addContainerProperty(role, Label.class, null);
	            
            }else{
	            columnIds.add(role);
	            columnHeaders.add(role);
	           // container.addContainerProperty(role, Boolean.class, Boolean.TRUE);
	            container.addContainerProperty(role, CheckBox.class, new CheckBox());
            }
        }
        
        
        tblDataSet.setContainerDataSource(container);
        
        tblDataSet.setVisibleColumns(columnIds.toArray(new Object[0]));
        tblDataSet.setColumnHeaders(columnHeaders.toArray(new String[0]));
        
        tblDataSet.setEditable(true);
       
    }
    
    /*
     * CHANGE THIS FUNCTION IF YOU WANT A SEPARATE LIST OF HEADERS
     * */
    protected void initializeHeader() 
    {
    	 Container container = tblDataSet.getContainerDataSource();
    
         Collection<?> itemIds = container.getItemIds();
         String obj = "FirstRow";
         
         //Item item = container.addItem(obj);
         
         int cnt = 0;
         
         TableItems[] headers = new TableItems[stringList.length];
         
        
         
         //Initialize the table items 
         for (String role : stringList) 
         {
        	 headers[cnt] = new TableItems(); 
        	 headers[cnt].setColumnId(role);
        	 if(role.equalsIgnoreCase(" "))
        	 {
        		 headers[cnt].setType("checkboxall");
        		 headers[cnt].setLabel("SELECT ALL");
        		 headers[cnt].setRowId(obj);
        		 headers[cnt].setValue(true);
        		 
        	 }else if(cnt < 3)
        	 {
        		 headers[cnt].setType("String");
        		 headers[cnt].setLabel(role);
        		 headers[cnt].setRowId(obj);
        		 headers[cnt].setValue(true);
        	 }else
        	 {
        		 headers[cnt].setType("checkbox_column");
        		 headers[cnt].setLabel(role);
        		 headers[cnt].setRowId(obj);
        		 headers[cnt].setValue(true);
        	 }
        	 cnt++;		 
         }
         // set the table columns
         
         createRow("FirstRow",headers);
         
         TableFieldFactory tff = tblDataSet.getTableFieldFactory();
         
        
    }
    
    /*
     * @RowId = Id of the row to be inserted
     * @TableItems[] = list of table items object. One array of TableItems would be equal to one row
     */
    private void createRow(String RowId,TableItems[] tableItems)
    {
    	
    	Object[] obj = new Object[tableItems.length];
    	
    	for(Integer i = 0; i < tableItems.length; i++){
    	
    		
    			if(tableItems[i].getType().equalsIgnoreCase("checkbox"))
       	 		{
       	 			CheckBox cb = new CheckBox();
		       	 	cb.setCaption(tableItems[i].getLabel());
		       	 	cb.setValue(tableItems[i].getValue());
		       	 	obj[i] = cb;
       	 		}else if(tableItems[i].getType().equalsIgnoreCase("string"))
       	 		{
       	 			obj[i] = tableItems[i].getLabel();
       	 		}else if(tableItems[i].getType().equalsIgnoreCase("checkboxall"))
       	 		{
	       	 		CheckBox cb = new CheckBox();
		       	 	cb.setCaption(tableItems[i].getLabel());
		       	 	cb.setValue(tableItems[i].getValue());
		       	 	
		       		cb.setImmediate(true);
         		
		       		class CheckboxListener implements ValueChangeListener{
         			
					private static final long serialVersionUID = 1L;
					
         			public CheckboxListener ()
         			{
         				
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
                            	
                            	 
                          	   Integer key = (Integer) myitems.next();
                          	   Item item = container.getItem(key);
                          	   
                          	   for(String column: stringList)
	  	                   	   {
	  	                   		   if(column.equalsIgnoreCase(" ") && key.intValue() == 0)
	  	                   			   continue;
	  	                   		   if(column.equalsIgnoreCase("environment") || column.equalsIgnoreCase("genotype"))
	  	                   			   continue;
	  	                   		   
	  	                   		   	CheckBox ba = (CheckBox) item.getItemProperty(column).getValue();
	  	                         	ba.setValue(event.getProperty().getValue());
	  	                         	
	  	                         	
	  	                   	   }
                             }
                           requestRepaintAll();
                          
                      }catch(Exception e)
                      { e.printStackTrace();}
                      }
						
         		}
         		
         		cb.addListener(new CheckboxListener());
		       	 	
		       	 	obj[i] = cb;
       	 		}
    			
    		// column checkbox
       	 	else if(tableItems[i].getType().equalsIgnoreCase("checkbox_column"))
   	 		{
       	 		CheckBox cb = new CheckBox();
	       	 	cb.setCaption(tableItems[i].getLabel());
	       	 	cb.setValue(tableItems[i].getValue());
	       	 	
	       		cb.setImmediate(true);
     		
	       		class CheckboxListener implements ValueChangeListener{
     			
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
                        	
                        	 
                      	   Integer key = (Integer) myitems.next();
                      	   Item item = container.getItem(key);
                      	   
                      	   if(key.intValue() != 0){
                      		   CheckBox ba = (CheckBox) item.getItemProperty(this.propertyId).getValue();
                      		   ba.setValue(event.getProperty().getValue());
                           }
                         }
                         
                       //  System.out.println("property " + event.getProperty().getValue());
                       requestRepaintAll();
                      
                  }catch(Exception e)
                  { e.printStackTrace();}
                  }
					
     		}
     		
     		cb.addListener(new CheckboxListener(tableItems[i].getLabel()));
	       	 	
	       	 	obj[i] = cb;
   	 		}
    			
    		// row checkbox
       	 	else if(tableItems[i].getType().equalsIgnoreCase("checkbox_row"))
	 		{
    	 		CheckBox cb = new CheckBox();
	       	 	cb.setCaption(tableItems[i].getLabel());
	       	 	cb.setValue(tableItems[i].getValue());
	       	 	
	       		cb.setImmediate(true);
  		
	       		class CheckboxListener implements ValueChangeListener{
  			
				private static final long serialVersionUID = 1L;
				private Integer propertyId;
	  			public CheckboxListener (Integer propertyId)
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
	                      
	                      
	                   	   Item item = container.getItem(this.propertyId);
	                   	   
	                   	   for(String column: stringList)
	                   	   {
	                   		   
	                   		   if(column.equalsIgnoreCase("environment") || column.equalsIgnoreCase("genotype"))
	                   			   continue;
	                   		if(column.equalsIgnoreCase(" "))
	                   			   continue;
	                   		   	//item.getItemProperty(this.propertyId).setValue(event.getProperty().getValue());
	                         	//if()
	                   		System.out.println("column " + column);
                         	System.out.println("propertyId " + this.propertyId);
	                   		   	CheckBox ba = (CheckBox) item.getItemProperty(column).getValue();
	                         	//CheckBox aa = (CheckBox) event.getProperty().getValue();
	                         	
	                         	System.out.println("CheckBox "+ ba);
	                         	System.out.println("value " + event.getProperty().getValue());
	                         	
	                         	ba.setValue(event.getProperty().getValue());
	                         	
	                         	
	                   	   }
	                      
	                      
	                    //  System.out.println("property " + event.getProperty().getValue());
	                    requestRepaintAll();
	                   
	               }catch(Exception e)
	               { e.printStackTrace();}
	               }
						
	  		}
  		
	  		cb.addListener(new CheckboxListener(countme));
		       	 	
		       	 	obj[i] = cb;
		 		}
	    	}
    	
    	
    	tblDataSet.addItem(obj, countme);
    	
    	

    	
    }
	private void addNewDataSetItem(String StudyName, String StudyTab, TableItems[] tableItems)
	{
		Container container = tblDataSet.getContainerDataSource();
		
		countme++;
        
	    Collection<?> itemIds = container.getItemIds();
        String obj = ""+countme;
        
        createRow(obj,tableItems);
        
        
	}
	private void setDataSetHeaders(String[] list)
	{
		stringList = list;
		
	}
	
	
    private void createTableContents() {
    	
    	setDataSetHeaders(new String[] {" ","environment","genotype","height","maturity","rust","height 1"});
    	initializeTable();
    	initializeHeader();
    	TableItems[] myRow = new TableItems[stringList.length];
    	
    	int cnt = 0;
    	
    	for(String cols: stringList)
    	{
    		myRow[cnt] = new TableItems();
    		if(cols.equalsIgnoreCase("environment") || cols.equalsIgnoreCase("genotype"))
    		{
    			myRow[cnt].setColumnId(cols);
    			myRow[cnt].setRowId(1);
    			myRow[cnt].setType("String");
    			myRow[cnt].setLabel("String ito "+ cnt);
    			
    		}else if(cols.equalsIgnoreCase(" "))
    		{
    			myRow[cnt].setColumnId(cols);
    			myRow[cnt].setRowId(1);
    			myRow[cnt].setType("checkbox_row");
    			myRow[cnt].setLabel(" ");
    			myRow[cnt].setValue(true);
    		}else
    		{
    			myRow[cnt].setColumnId(cols);
    			myRow[cnt].setRowId(1);
    			myRow[cnt].setType("CheckBox");
    			myRow[cnt].setLabel("Checkbox ito "+ cnt);
    			myRow[cnt].setValue(true);
    		}
    		//myRow[cnt]
    		cnt++;
    	}
    	
    	
    	TableItems[] myRow2 = new TableItems[stringList.length];
    	
    	cnt = 0;
    	
    	for(String cols: stringList)
    	{
    		myRow2[cnt] = new TableItems();
    		if(cols.equalsIgnoreCase("environment") || cols.equalsIgnoreCase("genotype"))
    		{
    			myRow2[cnt].setColumnId(cols);
    			myRow2[cnt].setRowId(2);
    			myRow2[cnt].setType("String");
    			myRow2[cnt].setLabel("String ito "+ cnt);
    			
    		}else if(cols.equalsIgnoreCase(" "))
    		{
    			myRow2[cnt].setColumnId(cols);
    			myRow2[cnt].setRowId(2);
    			myRow2[cnt].setType("checkbox_row");
    			myRow2[cnt].setLabel(" ");
    			myRow2[cnt].setValue(true);
    		}else
    		{
    			myRow2[cnt].setColumnId(cols);
    			myRow2[cnt].setRowId(2);
    			myRow2[cnt].setType("CheckBox");
    			myRow2[cnt].setLabel("Checkbox ito "+ cnt);
    			myRow2[cnt].setValue(true);
    		}
    		//myRow[cnt]
    		cnt++;
    	}
    	
    	addNewDataSetItem("environment","height",myRow);
    	addNewDataSetItem("environment","height",myRow2);
    	
    }

   

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
      
    }
    	
    
    protected void initializeActions() {
      //  newMemberButton.addListener(new OpenNewProjectAddUserWindowAction(select));
       studiesTree.addListener(new StudiesTreeAction());
      // studiesTabsheet.addListener(new StudiesTabFocusListener()); 
       
        
    }
    
    private class StudiesTabFocusListener implements SelectedTabChangeListener
    {

		@Override
		public void selectedTabChange(SelectedTabChangeEvent event) {
			// TODO Auto-generated method stub
			System.out.println(event.getTabSheet().getSelectedTab().getCaption());
		}
    	
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
    
    protected TabSheet generateTabSheet()
    {
    	TabSheet tab = new TabSheet();
    	
    	createTableContents();
    	generateTabComponent(tab, "AKMSHSSA");
    	generateTabComponent(tab, "AKMSSSSA");
    	
    	tab.setWidth("800px");
        tab.setHeight("700px");
        
        tab.addListener(new StudiesTabFocusListener()); 
        
    	return tab;
    }
  


    


  
        
       
        
        
}
