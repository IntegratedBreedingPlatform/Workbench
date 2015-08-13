
package org.generationcp.ibpworkbench.ui.window;

import java.text.SimpleDateFormat;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class RestoreIBDBWindow extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	private static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBWindow.class);

	private static final long serialVersionUID = 1L;

	private final Project project;

	// Components
	private ComponentContainer rootLayout;
	private Button cancelBtn;
	private Button saveBtn;

	private static final String WINDOW_WIDTH = "400px";
	private static final String WINDOW_HEIGHT = "430px";

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	private List<Project> projects;

	private Table table;

	private BeanContainer<String, ProjectBackup> projectBackupContainer;

	private Upload upload;

	public RestoreIBDBWindow(Project project) {
		this.project = project;
	}

	/**
	 * Assemble the UI after all dependencies has been set.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void initializeData() {
		User currentUser = this.sessionData.getUserData();

		try {
			this.projects = this.workbenchDataManager.getProjectsByUser(currentUser);

			// set the Project Table data source
			BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
			projectContainer.setBeanIdProperty("projectName");
			for (Project project : this.projects) {
				projectContainer.addBean(project);
			}

			this.projectBackupContainer = new BeanContainer<String, ProjectBackup>(ProjectBackup.class);
			this.projectBackupContainer.setBeanIdProperty("projectBackupId");

			this.table.setContainerDataSource(this.projectBackupContainer);
			this.table.setVisibleColumns(new String[] {"backupTime", "backupPath"});
			this.table.setColumnHeader("backupTime", "Backup Time");
			this.table.setColumnHeader("backupPath", "Backup Path");

			// init table contents
			Project p = this.sessionData.getSelectedProject();
			for (ProjectBackup pb : this.workbenchDataManager.getProjectBackups(p)) {
				this.projectBackupContainer.addBean(pb);
			}

			if (this.table.getItemIds().isEmpty()) {
				this.saveBtn.setEnabled(false);
			}

			this.table.setValue(this.table.firstItemId());

		} catch (MiddlewareQueryException e) {
			RestoreIBDBWindow.LOG.error("Exception", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}
	}

	protected void initializeComponents() {
		this.saveBtn = new Button("Restore");
		this.saveBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.saveBtn.setSizeUndefined();

		this.cancelBtn = new Button("Cancel");
		this.cancelBtn.setSizeUndefined();

		// Backup Table
		this.table = new Table() {

			/**
			 *
			 */
			private static final long serialVersionUID = 83415233737294478L;

			@Override
			protected String formatPropertyValue(Object rowId, Object colId, Property property) {

				if (property.getType() == java.util.Date.class) {
					SimpleDateFormat sdf = DateUtil.getSimpleDateFormat(DateUtil.FRONTEND_DATE_FORMAT);
					return property.getValue() == null ? "" : sdf.format((java.util.Date) property.getValue());
				}

				return super.formatPropertyValue(rowId, colId, property);
			}
		};

		this.table.setSelectable(true);
		this.table.setImmediate(true);
		this.table.setWidth("100%");
		this.table.setHeight("200px");

		this.upload = new Upload("Or upload an IB local backup file here:", null);
	}

	protected void initializeLayout() {
		this.addStyleName(Reindeer.WINDOW_LIGHT);

		this.setCaption("Restore IB Database");
		this.setWidth(RestoreIBDBWindow.WINDOW_WIDTH);
		this.setHeight(RestoreIBDBWindow.WINDOW_HEIGHT);
		this.setResizable(false);
		this.setModal(true);

		this.rootLayout = this.getContent();

		this.rootLayout.addComponent(new Label(this.messageSource.getMessage(Message.RESTORE_IBDB_TABLE_SELECT_CAPTION)));

		this.rootLayout.addComponent(this.table);

		// bind components to layout

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);
		hl.setMargin(true);

		Label spacer = new Label("&nbsp;", Label.CONTENT_XHTML);
		spacer.setWidth("100%");

		hl.addComponent(spacer);
		hl.addComponent(this.cancelBtn);
		hl.addComponent(this.saveBtn);

		hl.setComponentAlignment(this.saveBtn, Alignment.MIDDLE_RIGHT);
		hl.setComponentAlignment(this.cancelBtn, Alignment.MIDDLE_RIGHT);
		hl.setExpandRatio(spacer, 1.0f);

		this.rootLayout.addComponent(hl);

		// add upload
		this.rootLayout.addComponent(this.upload);

	}

	protected void initializeActions() {

		final RestoreIBDBSaveAction restoreAction = new RestoreIBDBSaveAction(this.project, this.table, this);

		// DO button listeners + actions here
		this.cancelBtn.addListener(new Button.ClickListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 3986272934965189089L;

			@Override
			public void buttonClick(ClickEvent event) {
				event.getButton().getWindow().getParent().removeWindow(event.getButton().getWindow());
			}
		});

		this.saveBtn.addListener(new Button.ClickListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 2139337955546100675L;

			@Override
			public void buttonClick(ClickEvent event) {
				final Window sourceWindow = event.getButton().getWindow();

				ConfirmDialog.show(sourceWindow.getParent(),
						RestoreIBDBWindow.this.messageSource.getMessage(Message.RESTORE_IBDB_WINDOW_CAPTION),
						RestoreIBDBWindow.this.messageSource.getMessage(Message.RESTORE_IBDB_CONFIRM),
						RestoreIBDBWindow.this.messageSource.getMessage(Message.RESTORE),
						RestoreIBDBWindow.this.messageSource.getMessage(Message.CANCEL), restoreAction);
			}
		});

		// Table actions
		this.table.addListener(new Property.ValueChangeListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = -8127163440035052055L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				RestoreIBDBWindow.LOG.info("Backup Table > Item selected");

				RestoreIBDBWindow.this.saveBtn.setEnabled(true);
			}
		});

	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeData();
		this.initializeActions();
	}

	@Override
	public void attach() {
		super.attach();

		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		this.messageSource.setCaption(this, Message.RESTORE_IBDB_WINDOW_CAPTION);
		this.messageSource.setCaption(this.saveBtn, Message.RESTORE);
		this.messageSource.setCaption(this.cancelBtn, Message.CANCEL);

		this.messageSource.setCaption(this.upload, Message.UPLOAD_IBDB_CAPTION);
	}
}
