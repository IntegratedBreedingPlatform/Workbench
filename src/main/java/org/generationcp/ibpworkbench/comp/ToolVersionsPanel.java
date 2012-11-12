package org.generationcp.ibpworkbench.comp;

import java.util.List;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class ToolVersionsPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;
    
    private Label lblToolVersions;
    
    private Table tblTools;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }
    
    protected void initializeComponents() {
        lblToolVersions = new Label();
        lblToolVersions.setStyleName("gcp-content-title");
        
        initializeToolsTable();
    }
    
    protected void initializeToolsTable() {
        tblTools = new Table();
        tblTools.setImmediate(true);
        tblTools.setColumnCollapsingAllowed(true);
        
        BeanContainer<Long, Tool> toolContainer = new BeanContainer<Long, Tool>(Tool.class);
        toolContainer.setBeanIdProperty("toolId");
        
        try {
            List<Tool> tools = workbenchDataManager.getAllTools();
            for (Tool tool : tools) {
                toolContainer.addBean(tool);
            }
        }
        catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }
        
        tblTools.setContainerDataSource(toolContainer);
        
        String[] columns = new String[] {"title", "version"};
        tblTools.setVisibleColumns(columns);
    }
    
    protected void initializeLayout() {
        setMargin(true);
        setSpacing(true);
        
        addComponent(lblToolVersions);
        
        tblTools.setWidth("100%");
        addComponent(tblTools);
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
