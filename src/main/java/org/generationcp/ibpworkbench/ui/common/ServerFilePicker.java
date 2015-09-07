
package org.generationcp.ibpworkbench.ui.common;

import java.io.File;
import java.io.FilenameFilter;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.common.customfield.CustomField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class ServerFilePicker extends CustomField implements InitializingBean {

	/**
	 *
	 */
	private static final long serialVersionUID = 7376447212907278979L;
	private final static Logger LOG = LoggerFactory.getLogger(ServerFilePicker.class);

	private HorizontalLayout root;
	private TextField pathFld;
	private Button browseBtn;
	private Window pickerWindow;
	private Label pathLbl;
	private FilesystemContainer fsContainer;
	private TreeTable treetable;
	private final Window parentWin;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public ServerFilePicker(Window parentWindow) {
		this.parentWin = parentWindow;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.root = new HorizontalLayout();
		this.root.setSpacing(true);

		this.setCompositionRoot(this.root);

		this.pathFld = new TextField();
		this.pathLbl = new Label();
		this.browseBtn = new Button("Browse");
		this.pathFld.setNullRepresentation("");

		this.root.addComponent(this.pathFld);
		this.root.addComponent(this.browseBtn);

		this.buildFilePicker();

		this.setPropertyDataSource(this.pathFld.getPropertyDataSource());

		this.browseBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ServerFilePicker.this.parentWin.getParent().addWindow(ServerFilePicker.this.pickerWindow);
			}
		});

	}

	private void buildFilePicker() {
		this.pickerWindow = new BaseSubWindow("Select an executable file");
		this.pickerWindow.addStyleName(Reindeer.WINDOW_LIGHT);
		this.pickerWindow.center();
		this.pickerWindow.setModal(true);

		this.pickerWindow.setWidth("500px");
		this.pickerWindow.setHeight("400px");
		this.pickerWindow.setResizable(false);

		final HorizontalLayout hl = new HorizontalLayout();

		hl.addComponent(new Label("Selected file: "));
		hl.addComponent(this.pathLbl);

		this.treetable = new TreeTable();

		// TODO: refactor this to make this more generic / reusable
		this.fsContainer = new FilesystemContainer(new File("tools"), new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				File pathName = new File(dir.getAbsolutePath() + File.separator + name);

				if (pathName.isDirectory()) {

					for (File children : pathName.listFiles()) {
						if (this.accept(pathName, children.getName())) {
							return true;
						}
					}

				} else if (pathName.getName().endsWith(".exe") || pathName.getName().endsWith(".bat")
						|| pathName.getName().endsWith(".com") || pathName.getName().endsWith(".sh")) {
					return true;
				}

				return false;
			}
		}, true);

		this.treetable.setContainerDataSource(this.fsContainer);

		// Set the row header icon by the file type
		this.treetable.setItemIconPropertyId("Icon");

		// Do not show the Icon column
		this.treetable.setVisibleColumns(new Object[] {"Name", "Size", "Last Modified"});
		// END-EXAMPLE: datamodel.container.filesystemcontainer.basic

		this.treetable.setImmediate(true);
		this.treetable.setWidth("100%");
		this.treetable.setHeight("240px");
		this.treetable.setSelectable(true);
		this.treetable.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				ServerFilePicker.LOG.debug(event.getProperty().getValue().toString());

				String filePath = event.getProperty().getValue().toString();

				if (!new File(filePath).isDirectory()) {
					ServerFilePicker.this.pathLbl.setValue(new File(filePath).getAbsolutePath());
				} else {
					ServerFilePicker.this.pathLbl.setValue("");
				}
			}

		});

		this.pickerWindow.addComponent(this.treetable);

		this.pickerWindow.addComponent(hl);

		final HorizontalLayout btnPanel = new HorizontalLayout();
		final Label spacer = new Label("&nbsp;", Label.CONTENT_XHTML);

		final Button selectBtn = new Button(this.messageSource.getMessage(Message.SELECT));
		final Button cancelSelectBtn = new Button(this.messageSource.getMessage(Message.CANCEL));

		selectBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		btnPanel.setWidth("100%");
		btnPanel.setSpacing(true);
		btnPanel.setMargin(true);

		btnPanel.addComponentAsFirst(spacer);
		btnPanel.addComponent(selectBtn);
		btnPanel.addComponent(cancelSelectBtn);

		btnPanel.setComponentAlignment(selectBtn, Alignment.MIDDLE_RIGHT);
		btnPanel.setComponentAlignment(cancelSelectBtn, Alignment.MIDDLE_RIGHT);
		btnPanel.setExpandRatio(spacer, 1.0f);

		this.pickerWindow.getContent().addComponent(btnPanel);

		// Listeners:
		cancelSelectBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ServerFilePicker.this.parentWin.getApplication().getMainWindow().removeWindow(ServerFilePicker.this.pickerWindow);
			}
		});

		selectBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				ServerFilePicker.this.setValue(new File(ServerFilePicker.this.treetable.getValue().toString()).getAbsolutePath());
				ServerFilePicker.this.parentWin.getApplication().getMainWindow().removeWindow(ServerFilePicker.this.pickerWindow);
			}
		});
	}

	public TextField getPathField() {
		return this.pathFld;
	}

	@Override
	public Object getValue() {
		return this.pathFld.getValue();
	}

	@Override
	public Object getData() {
		return this.pathFld.getData();
	}

	@Override
	public Class<?> getType() {
		return this.pathFld.getType();
	}

	@Override
	public void setValue(Object newValue) {
		super.setValue(newValue);
		this.pathFld.setValue(newValue);
	}

	@Override
	protected void setInternalValue(Object newValue) {
		super.setInternalValue(newValue);
		this.pathFld.setValue(newValue);
	}

	@Override
	public void setWidth(String width) {
		this.pathFld.setWidth(width);
	}

	@Override
	public void addValidator(Validator validator) {
		super.addValidator(validator);
	}

	@Override
	protected boolean isEmpty() {

		boolean isEmpty;

		if (this.getPathField().getValue() == null) {
			isEmpty = true;
		} else {
			isEmpty = this.getPathField().getValue().toString().isEmpty();
		}

		return isEmpty;
	}
}
