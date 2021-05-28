/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.data.Validator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.UploadBreedingViewOutputAction;
import org.generationcp.ibpworkbench.ui.common.UploadField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.easyuploads.FileFactory;

import java.io.File;
import java.util.Map;

/**
 * @author Aldrin Batac
 *
 */
@Configurable
public class FileUploadBreedingViewOutputWindow extends BaseSubWindow implements InitializingBean {

	public static final String NOT_VALID = "NOT_VALID";

	private static final String BMS_UPLOAD_CONTAINER = "bms-upload-container";

	private static final Logger LOG = LoggerFactory.getLogger(FileUploadBreedingViewOutputWindow.class);

	private static final long serialVersionUID = 3983198771242295731L;

	private Label description;

	private Button cancelButton;

	private Button uploadButton;

	private Component buttonArea;

	private VerticalLayout layout;

	private final Window window;

	private CustomUploadField uploadZip;

	private Label uploadZipLabel;

	private final int studyId;

	private final Project project;

	private final Map<String, Boolean> variatesStateMap;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

	private final InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	public FileUploadBreedingViewOutputWindow(final Window window, final int studyId, final Project project,
			final Map<String, Boolean> variatesStateMap) {
		this.window = window;
		this.studyId = studyId;
		this.project = project;
		this.variatesStateMap = variatesStateMap;
	}

	public void show() {
		this.window.getParent().addWindow(this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initialize();
	}

	private void initialize() {
		/*
		 * Make the window modal, which will disable all other components while it is visible
		 */
		this.addStyleName(Reindeer.WINDOW_LIGHT);

		this.setModal(true);

		/* Make the sub window 50% the size of the browser window */
		this.setWidth("750px");
		/*
		 * Center the window both horizontally and vertically in the browser window
		 */
		this.center();

		this.assemble();

		this.setCaption(this.messageSource.getMessage(Message.BV_UPLOAD_HEADER));

	}

	protected void initializeComponents() {
		this.layout = new VerticalLayout();
		this.layout.setDebugId("layout");
		this.layout.setWidth("100%");
		this.setContent(this.layout);
		this.setParentWindow(this.window);

		this.uploadZip = new CustomUploadField();
		this.uploadZip.setDebugId("uploadZip");
		this.uploadZip.setFieldType(UploadField.FieldType.FILE);
		this.uploadZip.setNoFileSelectedText("");
		this.uploadZip.setSelectedFileText("");
		this.uploadZip.setDeleteCaption(this.messageSource.getMessage(Message.CLEAR));
		this.uploadZip.setFileFactory(new CustomFileFactory());
		this.uploadZip.getRootLayout().setStyleName(FileUploadBreedingViewOutputWindow.BMS_UPLOAD_CONTAINER);
		this.uploadZip.getRootLayout().setWidth("100%");
		this.uploadZip.setButtonCaption(this.messageSource.getMessage(Message.BROWSE));
		this.uploadZip.setDeleteButtonListener(new DeleteButtonListener());

		this.uploadZipLabel = new Label(this.messageSource.getMessage(Message.BV_UPLOAD_ZIP), Label.CONTENT_XHTML);
		this.uploadZipLabel.setDebugId("uploadZipLabel");

		this.description = new Label(this.messageSource.getMessage(Message.BV_UPLOAD_DESCRIPTION), Label.CONTENT_XHTML);
		this.description.setDebugId("description");
		this.description.setStyleName(Bootstrap.Typography.TEXT_LEFT.styleName());

		this.layout.addComponent(this.description);
		this.layout.addComponent(this.uploadZipLabel);
		this.layout.addComponent(this.uploadZip);

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setDebugId("cancelButton");
		this.uploadButton = new Button("Upload");
		this.uploadButton.setDebugId("uploadButton");
		this.uploadButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.buttonArea = this.layoutButtonArea();

		this.layout.addComponent(this.buttonArea);
		this.layout.setComponentAlignment(this.buttonArea, Alignment.BOTTOM_CENTER);

	}

	protected void initializeLayout() {
		this.layout.setSpacing(true);
		this.layout.setMargin(true);
	}

	protected void initializeActions() {

		this.uploadButton.addListener(new UploadBreedingViewOutputAction(this));

		this.cancelButton.addListener(new CancelButtonListener());

	}

	protected Component layoutButtonArea() {
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.uploadButton);

		return buttonLayout;
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	private final class DeleteButtonListener implements Button.ClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(final ClickEvent event) {
			// not implemented
		}
	}

	private final class CancelButtonListener implements ClickListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void buttonClick(final ClickEvent event) {
			FileUploadBreedingViewOutputWindow.this.focus();
			FileUploadBreedingViewOutputWindow.this.getParent().removeWindow(FileUploadBreedingViewOutputWindow.this);
		}
	}

	public class CustomUploadField extends UploadField {

		private static final long serialVersionUID = 1L;

		@Override
		public void uploadFinished(final Upload.FinishedEvent event) {
			super.uploadFinished(event);
			FileUploadBreedingViewOutputWindow.LOG.debug("Upload is finished");
		}

		@Override
		public void validate() {
			if (!this.isValid()) {
				throw new Validator.InvalidValueException(NOT_VALID);
			}
		}

		@Override
		public boolean isValid() {
			return this.hasFileSelected() && this.getExtension(this.getLastFileName()).toLowerCase().contains("zip");
		}

		public boolean hasFileSelected() {
			return this.getLastFileName() != null;
		}

		private String getExtension(final String f) {
			String ext = null;
			final int i = f.lastIndexOf('.');

			if (i > 0 && i < f.length() - 1) {
				ext = f.substring(i + 1).toLowerCase();
			}

			if (ext == null) {
				return "";
			}
			return ext;
		}
	}

	public class CustomFileFactory implements FileFactory {

		private File file;

		@Override
		public File createFile(final String fileName, final String mimeType) {
			final File saveDir =
					new File(new File(FileUploadBreedingViewOutputWindow.this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(
							FileUploadBreedingViewOutputWindow.this.contextUtil.getProjectInContext(), ToolName.BV_SSA)).getAbsolutePath());
			if (!saveDir.exists() || !saveDir.isDirectory()) {
				saveDir.mkdirs();
			}

			final StringBuilder sb = new StringBuilder();
			if (new File(saveDir.getAbsolutePath() + "/" + fileName).exists()) {
				for (int x = 1; x < 10000; x++) {
					final String temp = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + x + ".zip";
					if (!new File(saveDir.getAbsolutePath() + "/" + temp).exists()) {
						sb.append(fileName.substring(0, fileName.lastIndexOf(".")));
						sb.append("_" + x + ".zip");
						break;
					}
				}
			} else {
				sb.append(fileName);
			}

			this.file = new File(saveDir, sb.toString());
			return this.file;
		}

		public File getFile() {
			return this.file;
		}

	}

	public CustomUploadField getUploadZip() {
		return this.uploadZip;
	}

	public Map<String, Boolean> getVariatesStateMap() {
		return this.variatesStateMap;
	}

	public int getStudyId() {
		return this.studyId;
	}

	public Project getProject() {
		return this.project;
	}

	
	public Window getWindow() {
		return window;
	}

}
