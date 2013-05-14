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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.util.GxEUtility;
import org.generationcp.ibpworkbench.util.TableItems;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
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
    
    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private StudyDataManager studyDataManagerV2;
    
    
    private Project project;
    
    private  List<Role> inheritedRoles;

    public GxeAnalysisComponentPanel(Project project) {
    	LOG.debug("Project is " + project.getProjectName());
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
    protected void refreshStudies() throws MiddlewareQueryException
    {
    	ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
    	StudyDataManager studyDataManager = managerFactory.getNewStudyDataManager();
        
    	List<FolderReference> listFolder = studyDataManager.getRootFolders(Database.CENTRAL);
		
    	 /* Add planets as root items in the tree. */
    	for(FolderReference folderParent : listFolder)
		{
        	studiesTree.addItem(folderParent.getName());
        	List<Reference> children  = studyDataManager.getChildrenOfFolder(folderParent.getId());
			if (children.size() == 0) {
                // The planet has no moons so make it a leaf.
            	studiesTree.setChildrenAllowed(folderParent.getName(), false);
            } else {
                // Add children (moons) under the planets.
                for (Reference childStudy: children) {
                	 
                    // Add the item as a regular item.
                    studiesTree.addItem(childStudy.getName());
                    
                    // Set it to be a child.
                    studiesTree.setParent(childStudy.getName(), folderParent.getName());
                    
                    // Make the moons look like leaves.
                    studiesTree.setChildrenAllowed(childStudy.getName(), false);
                }

                // Expand the subtree.
               // studiesTree.expandItemsRecursively(studies);
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
       try {
		refreshStudies();
	} catch (MiddlewareQueryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       
        studiesPanel.addComponent(studiesTree);
        
        studiesPanel.setWidth("200px");
        studiesPanel.setHeight("700px");
        
        
        
        horizontal.addComponent(studiesPanel);
        horizontal.addComponent(generateTabSheet());
        
      
        addComponent(horizontal);
        
        Button button = new Button("Test GXE to CSV");
        
        button.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				File csvFile =GxEUtility.generateGxEInputCSV(event.getComponent().getApplication().getMainWindow(),
						tblDataSet.getContainerDataSource(),
						project,
						new String[] {"environment","genotype","height","maturity","rust","height 1"});	// NOTE: the string array are the table headers
						
				LOG.debug(csvFile.getAbsolutePath());
				
				MessageNotifier.showMessage(event.getComponent().getWindow(),"GxE file saved (NOTE: ADD I18N)","Successfully created GxE CSV input file for the breeding_view (NOTE: ADD I18N)");
			}
		});
        
        addComponent(button);
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
    	
    	for (Integer x = 0; x <= 1000; x++){
    		Integer y = 0;
    		TableItems[] row = new TableItems[stringList.length];
    		for (String col : stringList){
    			
    			TableItems item = new TableItems();
    			item.setColumnId(col);
    			item.setRowId(x);
    			if (col.equalsIgnoreCase("environment") || col.equalsIgnoreCase("genotype")){
    				item.setType("String");
    				item.setLabel("Data" + x);
    			}else if (col.equalsIgnoreCase(" ")){
    				item.setType("checkbox_row");
    				item.setLabel(" ");
    				item.setValue(true);
    			}else{
    				item.setType("CheckBox");
    				item.setLabel(GxEUtility.randomInRange(0, 100).toString());
    				item.setValue(true);
    			}
    			row[y] = item ; y++;
    		}
    		addNewDataSetItem("environment","height", row);
    	}
    	
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
