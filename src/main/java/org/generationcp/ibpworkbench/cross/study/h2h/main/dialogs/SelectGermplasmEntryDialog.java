
package org.generationcp.ibpworkbench.cross.study.h2h.main.dialogs;

import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.cross.study.h2h.main.SpecifyGermplasmsComponent;
import org.generationcp.ibpworkbench.cross.study.h2h.main.listeners.HeadToHeadCrossStudyMainButtonClickListener;
import org.generationcp.ibpworkbench.germplasm.GermplasmSearchFormComponent;
import org.generationcp.ibpworkbench.germplasm.GermplasmSearchResultComponent;
import org.generationcp.ibpworkbench.germplasm.containers.GermplasmIndexContainer;
import org.generationcp.ibpworkbench.germplasm.listeners.GermplasmItemClickListener;
import org.generationcp.ibpworkbench.util.CloseWindowAction;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import java.util.List;

@Configurable
public class SelectGermplasmEntryDialog extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = -7651767452229107837L;
	private static final Logger LOG = LoggerFactory.getLogger(SelectGermplasmEntryDialog.class);

	private static final String GID = ColumnLabels.GID.getName();
	public static final String SEARCH_BUTTON_ID = "SelectGermplasmEntryDialog Search Button ID";
	public static final String CLOSE_SCREEN_BUTTON_ID = "SelectGermplasmEntryDialog Close Button ID";
	public static final String ADD_BUTTON_ID = "SelectGermplasmEntryDialog Add Button ID";

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private CrossStudyDataManager crossStudyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private final Component source;
	private final Window parentWindow;

	private VerticalLayout mainLayout;

	private Button searchButton;
	private Button doneButton;
	private Button cancelButton;

	private GermplasmSearchFormComponent searchComponent;
	private GermplasmSearchResultComponent resultComponent;
	private final GermplasmIndexContainer dataResultIndexContainer;

	private Integer selectedGid;
	private final boolean isTestEntry;

	private List<Integer> environmentIds;

	public SelectGermplasmEntryDialog(final Component source, Window parentWindow, final boolean isTestEntry) {
		this.source = source;
		this.parentWindow = parentWindow;
		this.isTestEntry = isTestEntry;
		this.dataResultIndexContainer = new GermplasmIndexContainer();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// set as modal window, other components are disabled while window is open
		this.setModal(true);
		// define window size, set as not resizable
		this.setWidth("600px");
		this.setHeight("530px");
		this.setResizable(false);
		this.setCaption("Select a Germplasm");
		// center window within the browser
		this.center();

		this.mainLayout = new VerticalLayout();
		this.mainLayout.setDebugId("mainLayout");
		this.mainLayout.setSpacing(true);

		final HorizontalLayout searchFormLayout = new HorizontalLayout();
		searchFormLayout.setDebugId("searchFormLayout");

		this.searchComponent = new GermplasmSearchFormComponent();
		this.searchComponent.setDebugId("searchComponent");
		searchFormLayout.addComponent(this.searchComponent);

		this.searchButton = new Button("Search");
		this.searchButton.setDebugId("searchButton");
		this.searchButton.setData(SelectGermplasmEntryDialog.SEARCH_BUTTON_ID);
		this.searchButton.addStyleName("addTopSpace");
		this.searchButton.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));
		this.searchButton.setClickShortcut(KeyCode.ENTER);
		this.searchButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		searchFormLayout.addComponent(this.searchButton);

		this.mainLayout.addComponent(searchFormLayout);

		this.resultComponent = new GermplasmSearchResultComponent(this.germplasmDataManager, SelectGermplasmEntryDialog.GID, "0");
		this.resultComponent.setDebugId("resultComponent");
		this.resultComponent.addListener(new GermplasmItemClickListener(this));
		this.resultComponent.setHeight("320px");
		this.mainLayout.addComponent(this.resultComponent);

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setDebugId("buttonLayout");
		buttonLayout.setSpacing(true);

		this.cancelButton = new Button("Close Screen");
		this.cancelButton.setDebugId("cancelButton");
		this.cancelButton.setData(SelectGermplasmEntryDialog.CLOSE_SCREEN_BUTTON_ID);
		this.cancelButton.addListener(new CloseWindowAction());

		String buttonlabel = "";
		if (this.isTestEntry) {
			buttonlabel = "Add as Test Entry";
		} else {
			buttonlabel = "Add as Standard Entry";
		}
		this.doneButton = new Button(buttonlabel);
		this.doneButton.setDebugId("doneButton");
		this.doneButton.setData(SelectGermplasmEntryDialog.ADD_BUTTON_ID);
		this.doneButton.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));
		this.doneButton.addListener(new CloseWindowAction());
		this.doneButton.setEnabled(false);
		this.doneButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		buttonLayout.addComponent(this.doneButton);
		buttonLayout.addComponent(this.cancelButton);

		this.mainLayout.addComponent(buttonLayout);
		this.mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);

		this.addComponent(this.mainLayout);
	}

	public void searchButtonClickAction() {
		this.doneButton.setEnabled(false);
		this.selectedGid = null;

		final String searchChoice = this.searchComponent.getChoice();
		final String searchValue = this.searchComponent.getSearchValue();

		if (searchValue.length() > 0) {
			boolean withNoError = true;

			if ("GID".equals(searchChoice)) {
				try {
					Integer.parseInt(searchValue);
				} catch (final NumberFormatException e) {
					withNoError = false;
					if (this.getWindow() != null) {
						MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_INVALID_FORMAT),
							this.messageSource.getMessage(Message.ERROR_INVALID_INPUT_MUST_BE_NUMERIC));
					}
				}
			}

			// TODO : perhaps default to full search to prevent NPE
			if (withNoError) {
				LazyQueryContainer dataSourceResultLazy = null;
				if (this.isTestEntry || this.environmentIds == null) {
					dataSourceResultLazy =
						this.dataResultIndexContainer.getGermplasmResultLazyContainer(this.germplasmDataManager, searchChoice,
							searchValue);
				} else {
					dataSourceResultLazy =
						this.dataResultIndexContainer.getGermplasmEnvironmentResultLazyContainer(this.crossStudyDataManager,
							searchChoice, searchValue, this.environmentIds);
				}
				this.resultComponent.setCaption("Germplasm Search Result: " + dataSourceResultLazy.size());
				if (!this.isTestEntry && this.environmentIds != null && this.environmentIds.isEmpty()) {
					this.resultComponent.setCaption("Selected Test Entries not used in Trials - no comparable data");
				}
				this.resultComponent.setContainerDataSource(dataSourceResultLazy);
				this.mainLayout.requestRepaintAll();
			}
		} else {
			MessageNotifier.showError(this.getWindow(), "Error", "Please input search string.");
		}
	}

	public void addButtonClickAction() {
		try {
			final Germplasm selectedGermplasm = this.germplasmDataManager.getGermplasmWithPrefName(this.selectedGid);
			if (this.isTestEntry) {
				((SpecifyGermplasmsComponent) this.source).addTestGermplasm(selectedGermplasm);
			} else {
				((SpecifyGermplasmsComponent) this.source).addStandardGermplasm(selectedGermplasm);
			}
		} catch (final MiddlewareQueryException ex) {
			SelectGermplasmEntryDialog.LOG.error("Error with getting germplasm with gid: " + this.selectedGid, ex);
			MessageNotifier.showError(this.getWindow(), "Database Error!", "Error with getting germplasm with gid: " + this.selectedGid
				+ ". " + this.messageSource.getMessage(Message.ERROR_REPORT_TO));
		} catch (final Exception ex) {
			SelectGermplasmEntryDialog.LOG.error("Error with setting selected germplasm.", ex);
			MessageNotifier.showError(this.getWindow(), "Application Error!", "Error with setting selected germplasm." + " "
				+ this.messageSource.getMessage(Message.ERROR_REPORT_TO));
		}
	}

	public void resultTableItemClickAction(final Table sourceTable, final Object itemId, final Item item) {
		sourceTable.select(itemId);
		this.selectedGid = Integer.valueOf(item.getItemProperty(SelectGermplasmEntryDialog.GID).toString());
		this.doneButton.setEnabled(true);
	}

	@Override
	public void updateLabels() {
		// do nothing
	}

	public void setEnvironmentIds(final List<Integer> environmentIds) {
		this.environmentIds = environmentIds;
	}

	public boolean isTestEntry() {
		return this.isTestEntry;
	}

}
