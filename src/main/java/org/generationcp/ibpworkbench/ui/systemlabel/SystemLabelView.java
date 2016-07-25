package org.generationcp.ibpworkbench.ui.systemlabel;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.domain.oms.Term;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

@Configurable
public class SystemLabelView extends Panel implements InitializingBean, InternationalizableComponent, IBPWorkbenchLayout {

	public static final String DEFINITION = "definition";
	public static final String NAME = "name";
	public static final String ID = "id";

	private Label heading;
	private Label headingDesc;
	private Table tblSystemLabels;
	private Button cancelButton;
	private Button saveButton;

	private SystemLabelPresenter systemLabelPresenter;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Override
	public void instantiateComponents() {

		this.tblSystemLabels = new Table();
		this.initializeTable(tblSystemLabels);
		this.systemLabelPresenter = new SystemLabelPresenter(this);
		this.heading = new Label();
		this.headingDesc = new Label();
	}

	@Override
	public void initializeValues() {

		systemLabelPresenter.loadDataSystemLabelTable();

	}

	@Override
	public void addListeners() {

		saveButton.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(final Button.ClickEvent clickEvent) {

				systemLabelPresenter.saveTerms();

			}
		});

		cancelButton.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(final Button.ClickEvent clickEvent) {

				systemLabelPresenter.loadDataSystemLabelTable();

			}
		});

	}

	@Override
	public void layoutComponents() {

		this.setStyleName(Reindeer.PANEL_LIGHT);
		this.setSizeFull();

		final HorizontalLayout titleContainer = new HorizontalLayout();
		heading.setStyleName(Bootstrap.Typography.H4.styleName());

		titleContainer.addComponent(heading);

		final VerticalLayout root = new VerticalLayout();
		root.setMargin(new Layout.MarginInfo(true, true, true, true));
		root.setSpacing(true);
		root.setSizeFull();

		root.addComponent(titleContainer);
		root.addComponent(headingDesc);
		root.addComponent(tblSystemLabels);

		final ComponentContainer buttonArea = this.layoutButtonArea();
		root.addComponent(buttonArea);
		root.setComponentAlignment(buttonArea, Alignment.TOP_CENTER);

		this.setScrollable(false);
		this.setSizeFull();
		this.setContent(root);

	}

	@Override
	public void updateLabels() {

		heading.setContentMode(Label.CONTENT_XHTML);
		this.messageSource.setValue(this.heading, Message.SYSTEM_LABEL_HEADING);
		this.messageSource.setValue(this.headingDesc, Message.SYSTEM_LABEL_HEADING_DESCRIPTION);
		this.messageSource.setCaption(this.saveButton, Message.SAVE);
		this.messageSource.setCaption(this.cancelButton, Message.RESET);

	}

	@Override
	public void afterPropertiesSet() throws Exception {

		this.instantiateComponents();
		this.initializeValues();
		this.layoutComponents();
		this.addListeners();
		this.updateLabels();

	}

	protected ComponentContainer layoutButtonArea() {
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		this.cancelButton = new Button();
		this.saveButton = new Button();

		this.saveButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		buttonLayout.addComponent(this.cancelButton);
		buttonLayout.addComponent(this.saveButton);

		return buttonLayout;
	}

	protected void initializeTable(Table table) {

		table.setContainerDataSource(new BeanItemContainer<Term>(Term.class));
		table.setEditable(true);
		table.setVisibleColumns(new Object[] {ID, DEFINITION, NAME});
		table.setColumnHeaders(new String[] {"ID", "DEFINITION", "CURRENT LABEL"});
		table.setSizeFull();
		table.setWidth("100%");
		table.setColumnExpandRatio(ID, 10 / 100);
		table.setColumnExpandRatio(DEFINITION, 50 / 100);
		table.setColumnExpandRatio(NAME, 40 / 100);
		table.setLazyLoading(false);
		table.setImmediate(true);

		table.setTableFieldFactory(new SystemLabelFieldFactory());

		// Override the creation of ID and Definition columns so that even when the table
		// is in edit mode, the columns will only display texts not editable textfields.
		table.addGeneratedColumn(ID, new Table.ColumnGenerator() {

			@Override
			public Object generateCell(final Table table, final Object o, final Object o1) {
				return ((Term) o).getId();
			}
		});
		table.addGeneratedColumn(DEFINITION, new Table.ColumnGenerator() {

			@Override
			public Object generateCell(final Table table, final Object o, final Object o1) {
				return ((Term) o).getDefinition();
			}
		});

	}

	protected void showSaveSuccessMessage() {

		String title = messageSource.getMessage(Message.SUCCESS);
		String message = messageSource.getMessage(Message.SYSTEM_LABEL_UPDATE_SUCCESS);
		MessageNotifier.showMessage(this.getWindow(), title, message);

	}

	protected void showValidationErrorMessage() {

		String title = messageSource.getMessage(Message.ERROR);
		String message = messageSource.getMessage(Message.SYSTEM_LABEL_UPDATE_ERROR);
		MessageNotifier.showError(this.getWindow(), title, message);

	}

	public Table getTblSystemLabels() {
		return tblSystemLabels;
	}
}
