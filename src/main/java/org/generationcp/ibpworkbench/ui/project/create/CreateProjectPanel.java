/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The create project panel
 *
 * @author Joyce Avestro
 *
 */
@Configurable
public class CreateProjectPanel extends Panel implements InitializingBean {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(CreateProjectPanel.class);

	protected ProjectBasicDetailsComponent projectBasicDetailsComponent;

	protected HorizontalLayout newProjectTitleArea;
	protected Button cancelButton;
	protected Button saveProjectButton;
	protected Component buttonArea;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private PlatformTransactionManager transactionManager;

	private Label heading;

	AddProgramPresenter presenter;

	public CreateProjectPanel() {
	}

	public CreateProjectPanel(final AddProgramPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void afterPropertiesSet() {
		this.assemble();
	}

	protected void initializeComponents() {

		this.heading = new Label("<span class=\"bms-fa-text-o\" style=\"color: #009DDA; font-size: 23px \" ></span>&nbsp;Basic Details",
				Label.CONTENT_XHTML);
		this.heading.setStyleName(Bootstrap.Typography.H4.styleName());

		this.newProjectTitleArea = new HorizontalLayout();
		this.newProjectTitleArea.setDebugId("newProjectTitleArea");
		this.newProjectTitleArea.setSpacing(true);

		this.projectBasicDetailsComponent = new ProjectBasicDetailsComponent(this);
		this.projectBasicDetailsComponent.setDebugId("projectBasicDetailsComponent");
		this.buttonArea = this.layoutButtonArea();

	}

	protected void initializeLayout() {
		final VerticalLayout root = new VerticalLayout();
		root.setDebugId("root");
		root.setMargin(new Layout.MarginInfo(true, true, true, true));
		root.setSpacing(true);
		root.addComponent(this.heading);
		root.addComponent(this.projectBasicDetailsComponent);
		root.addComponent(this.buttonArea);
		root.setComponentAlignment(this.buttonArea, Alignment.TOP_CENTER);

		this.setScrollable(false);
		this.setSizeFull();
		this.setContent(root);
		this.setStyleName(Reindeer.PANEL_LIGHT);
	}

	protected void initializeActions() {

		this.saveProjectButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override

			public void buttonClick(final Button.ClickEvent clickEvent) {
				try {
					final TransactionTemplate transactionTemplate = new TransactionTemplate(CreateProjectPanel.this.transactionManager);
					transactionTemplate.execute(new TransactionCallbackWithoutResult() {

						@Override
						protected void doInTransactionWithoutResult(final TransactionStatus status) {
							final Project newlyCreatedProgram = CreateProjectPanel.this.presenter.doAddNewProgram();

							MessageNotifier.showMessage(clickEvent.getComponent().getWindow(),
									CreateProjectPanel.this.messageSource.getMessage(Message.SUCCESS),
									newlyCreatedProgram.getProjectName() + " program has been successfully created.");

							CreateProjectPanel.this.presenter.enableProgramMethodsAndLocationsTab();
						}
					});

				} catch (final Exception e) {

					if ("basic_details_invalid".equals(e.getMessage())) {
						return;
					}

					CreateProjectPanel.LOG.error("Oops there might be serious problem on creating the program, investigate it!", e);

					MessageNotifier.showError(clickEvent.getComponent().getWindow(),
							CreateProjectPanel.this.messageSource.getMessage(Message.DATABASE_ERROR),
							CreateProjectPanel.this.messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));

				}
			}
		});

		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent clickEvent) {

				CreateProjectPanel.this.presenter.resetBasicDetails();
				CreateProjectPanel.this.presenter.disableProgramMethodsAndLocationsTab();
			}
		});
	}

	protected Component layoutButtonArea() {
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button("Reset");
		this.cancelButton.setDebugId("cancelButton");
		this.saveProjectButton = new Button("Save");
		this.saveProjectButton.setDebugId("saveProjectButton");
		this.saveProjectButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.saveProjectButton);

		return buttonLayout;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();

	}

	public void cropTypeChanged(final CropType newCropType) {
		this.presenter.disableProgramMethodsAndLocationsTab();
	}

	public ProjectBasicDetailsComponent getProjectBasicDetailsComponent() {
		return projectBasicDetailsComponent;
	}
}
