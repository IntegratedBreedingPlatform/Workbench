
package org.generationcp.ibpworkbench.ui.window;

import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
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
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class RestoreIBDBWindow extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	private static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBWindow.class);

	private static final long serialVersionUID = 1L;

	private final Project project;

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

	private Upload upload;

	public RestoreIBDBWindow(final Project project) {
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
		try {
			final List<Project> projects = this.workbenchDataManager.getProjectsByUser(this.sessionData.getUserData());

			// set the Project Table data source
			final BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
			projectContainer.setBeanIdProperty("projectName");
			for (final Project project : projects) {
				projectContainer.addBean(project);
			}

		} catch (final MiddlewareQueryException e) {
			RestoreIBDBWindow.LOG.error("Exception", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}
	}

	protected void initializeComponents() {
		this.saveBtn = new Button(this.messageSource.getMessage("RESTORE_LABEL"));
		this.saveBtn.setDebugId("saveBtn");
		this.saveBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.saveBtn.setSizeUndefined();

		this.cancelBtn = new Button(this.messageSource.getMessage("CANCEL"));
		this.cancelBtn.setDebugId("cancelBtn");
		this.cancelBtn.setSizeUndefined();

		this.upload = new Upload(this.messageSource.getMessage("RESTORE_BMS_UPLOAD"), null);
		this.upload.setDebugId("upload");
		this.upload.setDebugId("upload");
	}

	protected void initializeLayout() {
		this.addStyleName(Reindeer.WINDOW_LIGHT);

		this.setCaption(this.messageSource.getMessage("RESTORE_IB_DB"));
		this.setWidth(RestoreIBDBWindow.WINDOW_WIDTH);
		this.setHeight(RestoreIBDBWindow.WINDOW_HEIGHT);
		this.setResizable(false);
		this.setModal(true);

		final ComponentContainer rootLayout = this.getContent();

		rootLayout.addComponent(new Label(this.messageSource.getMessage(Message.RESTORE_IBDB_TABLE_SELECT_CAPTION)));

		// bind components to layout

		final HorizontalLayout hl = new HorizontalLayout();
		hl.setDebugId("hl");
		hl.setWidth("100%");
		hl.setSpacing(true);
		hl.setMargin(true);

		final Label spacer = new Label("&nbsp;", Label.CONTENT_XHTML);
		spacer.setDebugId("spacer");
		spacer.setWidth("100%");

		hl.addComponent(spacer);
		hl.addComponent(this.cancelBtn);
		hl.addComponent(this.saveBtn);

		hl.setComponentAlignment(this.saveBtn, Alignment.MIDDLE_RIGHT);
		hl.setComponentAlignment(this.cancelBtn, Alignment.MIDDLE_RIGHT);
		hl.setExpandRatio(spacer, 1.0f);

		rootLayout.addComponent(hl);

		// add upload
		rootLayout.addComponent(this.upload);

	}

	protected void initializeActions() {

		final RestoreIBDBSaveAction restoreAction = new RestoreIBDBSaveAction(this.project, this);

		// DO button listeners + actions here
		this.cancelBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 3986272934965189089L;

			@Override
			public void buttonClick(final ClickEvent event) {
				event.getButton().getWindow().getParent().removeWindow(event.getButton().getWindow());
			}
		});

		this.saveBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 2139337955546100675L;

			@Override
			public void buttonClick(final ClickEvent event) {
				final Window sourceWindow = event.getButton().getWindow();

				ConfirmDialog.show(sourceWindow.getParent(),
						RestoreIBDBWindow.this.messageSource.getMessage(Message.RESTORE_IBDB_WINDOW_CAPTION),
						RestoreIBDBWindow.this.messageSource.getMessage(Message.RESTORE_IBDB_CONFIRM),
						RestoreIBDBWindow.this.messageSource.getMessage(Message.RESTORE),
						RestoreIBDBWindow.this.messageSource.getMessage(Message.CANCEL), restoreAction);
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
