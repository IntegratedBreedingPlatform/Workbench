package org.generationcp.ibpworkbench.ui;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.programmethods.MethodView;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configurable
public class ToolsAndCropVersionsView extends VerticalLayout implements InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;
    
    private Label lblToolVersions;
    
    private Table tblTools;
    private Table tblCrops;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;


    private static final Logger LOG = LoggerFactory.getLogger(ToolsAndCropVersionsView.class);


    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }
    
    protected void initializeComponents() {
        lblToolVersions = new Label();
        lblToolVersions.setStyleName(Bootstrap.Typography.H1.styleName());
        
        initializeToolsTable();

        initializeCropsTable();
    }

    private void initializeCropsTable() {
        tblCrops = new Table();
        tblCrops.setImmediate(true);
        tblCrops.setColumnCollapsingAllowed(true);

        BeanContainer<Long,CropType> cropContainer = new BeanContainer<Long, CropType>(CropType.class);
        cropContainer.setBeanIdProperty("cropName");

        try {
            cropContainer.addAll(workbenchDataManager.getInstalledCentralCrops());

            tblCrops.setContainerDataSource(cropContainer);
            tblCrops.addGeneratedColumn("gVersion", new Table.ColumnGenerator() {
                @Override
                public Object generateCell(Table source, Object itemId, Object colId) {
                    final CropType beanItem = ((BeanContainer<Long,CropType>) source.getContainerDataSource()).getItem(itemId).getBean();

                    if (beanItem.getVersion() == null || beanItem.getVersion().trim().isEmpty())
                        return new Label("<em>Not Available</em>",Label.CONTENT_XHTML);
                    else
                        return beanItem.getVersion().trim();


                }
            });

            tblCrops.setVisibleColumns(new String[]{"cropName", "gVersion"});
            //tblCrops.setColumnWidth("gVersion",80);
            tblCrops.setColumnHeaders(new String[]{"Crop Name", "Version"});
            tblCrops.setColumnExpandRatio("cropName",0.7F);
            tblCrops.setColumnExpandRatio("gVersion",0.3F);
        } catch (MiddlewareQueryException e) {
            LOG.error("Oops, something happened!",e);
        }

    }

    protected void initializeToolsTable() {
        tblTools = new Table();
        tblTools.setImmediate(true);
        tblTools.setColumnCollapsingAllowed(true);
        
        BeanContainer<Long, Tool> toolContainer = new BeanContainer<Long, Tool>(Tool.class);
        toolContainer.setBeanIdProperty("toolId");
        
        String[] propertyNames = new String[] {"mysql","flapjack","jre","tomcat","r"};
        
        try {
        	
        	Resource resource = new ClassPathResource("/workbench_tools.properties");
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
        	Long toolId = 0L;
            List<Tool> tools = workbenchDataManager.getAllTools();
            List<String> addedToolNames = new ArrayList<String>();
            for (Tool tool : tools) {

                if (!(ToolType.ADMIN.equals(tool.getToolType()) || ToolType.WORKBENCH.equals(tool.getToolType()))
                    && !addedToolNames.contains(tool.getTitle())) {
                    addedToolNames.add(tool.getTitle());
                    
                    toolContainer.addBean(tool);
                    //System.out.println(tool);
                    toolId++;
                }

            }
            
            for(String name: propertyNames)
            {
            	 toolId++;
            	 Tool t = new Tool();
                 t.setToolName(props.getProperty("tool_name."+name));
                 t.setVersion(props.getProperty("tool_version."+name));
                 t.setParameter(props.getProperty("tool_name."+name));
                 t.setPath(props.getProperty("tool_name."+name));
                 t.setToolId(toolId);
                 t.setTitle(props.getProperty("tool_name."+name));
                 //System.out.println(props.getProperty("tool_name."+name) + " : " + props.getProperty("tool_version."+name));
                 //System.out.println(t);
                 toolContainer.addBean(t);
            }
           
        }
        catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }
        catch (IOException ioe) {
        	ioe.printStackTrace();
        }

        toolContainer.sort(new String[]{"title"},new boolean[] {true});

        tblTools.setContainerDataSource(toolContainer);
        
        String[] columns = new String[] {"title", "version"};
        tblTools.setVisibleColumns(columns);
    }
    
    protected void initializeLayout() {
        setMargin(new MarginInfo(false,true,true,true));
        setSpacing(true);
        
        final HorizontalLayout root = new HorizontalLayout();
        root.setSpacing(true);
        root.setSizeFull();

        this.addComponent(lblToolVersions);

        final VerticalLayout cropsContainer = new VerticalLayout();
        cropsContainer.setSpacing(true);
        cropsContainer.addComponent(tblCrops);
        cropsContainer.addComponent(new Label("<em>Crops with no version are installed prior to BMS 3.0</em>",Label.CONTENT_XHTML));

        root.addComponent(tblTools);
        root.addComponent(cropsContainer);
        this.addComponent(root);

        tblCrops.setWidth("100%");
        cropsContainer.setWidth("100%");
        tblTools.setWidth("100%");

        this.setWidth("100%");
    }
    
    protected void initializeActions() {
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    @Override
    public void updateLabels() {
        messageSource.setValue(lblToolVersions, Message.TOOL_VERSIONS);
        
        messageSource.setColumnHeader(tblTools, "title", Message.TOOL_NAME);
        messageSource.setColumnHeader(tblTools, "version", Message.VERSION);
    }
}
