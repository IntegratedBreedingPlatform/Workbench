
package org.generationcp.ibpworkbench.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
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

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

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
	private static final String CROP_NAME = "cropName";
	private static final String G_VERSION = "gVersion";
	private static final String TITLE = "title";
	private static final String TOOL_NAME_PREFIX = "tool_name.";
	private static final String TOOL_VERSION_PREFIX = "tool_version.";
	private static final String VERSION = "version";

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void initializeComponents() {
		this.lblToolVersions = new Label();
		this.lblToolVersions.setStyleName(Bootstrap.Typography.H1.styleName());

		this.initializeToolsTable();

		this.initializeCropsTable();
	}

	private void initializeCropsTable() {
		this.tblCrops = new Table();
		this.tblCrops.setImmediate(true);
		this.tblCrops.setColumnCollapsingAllowed(true);

		BeanContainer<Long, CropType> cropContainer = new BeanContainer<Long, CropType>(CropType.class);
		cropContainer.setBeanIdProperty(ToolsAndCropVersionsView.CROP_NAME);

		try {
			cropContainer.addAll(this.workbenchDataManager.getInstalledCentralCrops());

			this.tblCrops.setContainerDataSource(cropContainer);
			this.tblCrops.addGeneratedColumn(ToolsAndCropVersionsView.G_VERSION, new Table.ColumnGenerator() {

				/**
				 *
				 */
				 private static final long serialVersionUID = -3892464608867001492L;

				@Override
				public Object generateCell(Table source, Object itemId, Object colId) {
					final CropType beanItem = ((BeanContainer<Long, CropType>) source.getContainerDataSource()).getItem(itemId).getBean();

					if (beanItem.getVersion() == null || beanItem.getVersion().trim().isEmpty()) {
						return new Label("<em>Not Available</em>", Label.CONTENT_XHTML);
					} else {
						return beanItem.getVersion().trim();
					}

				}
			});

			this.tblCrops.setVisibleColumns(new String[] {ToolsAndCropVersionsView.CROP_NAME, ToolsAndCropVersionsView.G_VERSION});
			this.tblCrops.setColumnHeaders(new String[] {"Crop Name", ToolsAndCropVersionsView.VERSION});
			this.tblCrops.setColumnExpandRatio(ToolsAndCropVersionsView.CROP_NAME, 0.7F);
			this.tblCrops.setColumnExpandRatio(ToolsAndCropVersionsView.G_VERSION, 0.3F);
		} catch (MiddlewareQueryException e) {
			ToolsAndCropVersionsView.LOG.error("Oops, something happened!", e);
		}

	}

	protected void initializeToolsTable() {
		this.tblTools = new Table();
		this.tblTools.setImmediate(true);
		this.tblTools.setColumnCollapsingAllowed(true);

		BeanContainer<Long, Tool> toolContainer = new BeanContainer<Long, Tool>(Tool.class);
		toolContainer.setBeanIdProperty("toolId");

		String[] propertyNames = new String[] {"mysql", "flapjack", "jre", "tomcat", "r"};

		try {

			Resource resource = new ClassPathResource("/workbench_tools.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			Long toolId = 0L;
			List<Tool> tools = this.workbenchDataManager.getAllTools();
			List<String> addedToolNames = new ArrayList<String>();
			for (Tool tool : tools) {

				if (!(ToolType.ADMIN.equals(tool.getToolType()) || ToolType.WORKBENCH.equals(tool.getToolType()))
						&& !addedToolNames.contains(tool.getTitle())) {
					addedToolNames.add(tool.getTitle());

					toolContainer.addBean(tool);
					toolId++;
				}

			}

			for (String name : propertyNames) {
				toolId++;
				Tool t = new Tool();
				t.setToolName(props.getProperty(ToolsAndCropVersionsView.TOOL_NAME_PREFIX + name));
				t.setVersion(props.getProperty(ToolsAndCropVersionsView.TOOL_VERSION_PREFIX + name));
				t.setParameter(props.getProperty(ToolsAndCropVersionsView.TOOL_NAME_PREFIX + name));
				t.setPath(props.getProperty(ToolsAndCropVersionsView.TOOL_NAME_PREFIX + name));
				t.setToolId(toolId);
				t.setTitle(props.getProperty(ToolsAndCropVersionsView.TOOL_NAME_PREFIX + name));
				toolContainer.addBean(t);
			}

		} catch (MiddlewareQueryException e) {
			ToolsAndCropVersionsView.LOG.error(e.getMessage(), e);
		} catch (IOException ioe) {
			ToolsAndCropVersionsView.LOG.error(ioe.getMessage(), ioe);
		}

		toolContainer.sort(new String[] {ToolsAndCropVersionsView.TITLE}, new boolean[] {true});

		this.tblTools.setContainerDataSource(toolContainer);

		String[] columns = new String[] {ToolsAndCropVersionsView.TITLE, ToolsAndCropVersionsView.VERSION};
		this.tblTools.setVisibleColumns(columns);
	}

	protected void initializeLayout() {
		this.setMargin(new MarginInfo(false, true, true, true));
		this.setSpacing(true);

		final HorizontalLayout root = new HorizontalLayout();
		root.setSpacing(true);
		root.setSizeFull();

		this.addComponent(this.lblToolVersions);

		final VerticalLayout cropsContainer = new VerticalLayout();
		cropsContainer.setSpacing(true);
		cropsContainer.addComponent(this.tblCrops);
		cropsContainer.addComponent(new Label("<em>Not available</em> means crop is installed prior to version BMS 3.0",
				Label.CONTENT_XHTML));

		root.addComponent(this.tblTools);
		root.addComponent(cropsContainer);
		this.addComponent(root);

		this.tblCrops.setWidth("100%");
		cropsContainer.setWidth("100%");
		this.tblTools.setWidth("100%");

		this.setWidth("100%");
	}

	protected void initializeActions() {
		// do nothing
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	@Override
	public void attach() {
		super.attach();

		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setValue(this.lblToolVersions, Message.TOOL_VERSIONS);

		this.messageSource.setColumnHeader(this.tblTools, ToolsAndCropVersionsView.TITLE, Message.TOOL_NAME);
		this.messageSource.setColumnHeader(this.tblTools, ToolsAndCropVersionsView.VERSION, Message.VERSION);
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
