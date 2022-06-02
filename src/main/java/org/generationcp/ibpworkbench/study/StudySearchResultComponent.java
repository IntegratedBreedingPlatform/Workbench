package org.generationcp.ibpworkbench.study;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.dellroad.stuff.vaadin.ContextApplication;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.containers.StudyDataContainerBuilder;
import org.generationcp.ibpworkbench.study.listeners.StudyItemClickListener;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;

@Configurable
public class StudySearchResultComponent extends VerticalLayout
		implements InitializingBean, InternationalizableComponent, GermplasmStudyBrowserLayout {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StudySearchResultComponent.class);

	private static final String STUDY_ID = "ID";
	private static final String STUDY_NAME = "NAME";

	private Label totalEntriesLabel;
	private Table searchResultTable;
	private StudyDataContainerBuilder studyDataContainerBuilder;

	private final StudySearchMainComponent parentComponent;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private ProgramService programService;

	public StudySearchResultComponent(final StudySearchMainComponent parentComponent) {
		this.parentComponent = parentComponent;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	@Override
	public void instantiateComponents() {
		this.studyDataContainerBuilder = new StudyDataContainerBuilder(this.studyDataManager, 0);

		// search Results
		this.totalEntriesLabel = new Label("", Label.CONTENT_XHTML);
		this.totalEntriesLabel.setWidth("120px");
		this.updateNoOfEntries(0);

		this.initializeSearchResultTable();
	}

	private void initializeSearchResultTable() {
		this.searchResultTable = new Table();
		this.searchResultTable.setWidth("98%");
		this.searchResultTable.setHeight("300px");
		this.searchResultTable.setSelectable(true);
		this.searchResultTable.setMultiSelect(false);
		this.searchResultTable.setImmediate(true);
		this.searchResultTable.setColumnReorderingAllowed(true);
		this.searchResultTable.setColumnCollapsingAllowed(true);
		this.searchResultTable.setCaption(null);

		this.searchResultTable.addContainerProperty(StudySearchResultComponent.STUDY_ID, String.class, null);
		this.searchResultTable.addContainerProperty(StudySearchResultComponent.STUDY_NAME, String.class, null);
		this.messageSource.setColumnHeader(this.searchResultTable, StudySearchResultComponent.STUDY_ID, Message.STUDY_ID_LABEL);
		this.messageSource.setColumnHeader(this.searchResultTable, StudySearchResultComponent.STUDY_NAME, Message.STUDY_NAME_LABEL);

		this.searchResultTable.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {

			private static final long serialVersionUID = 1L;

			@Override
			public String generateDescription(final Component source, final Object itemId, final Object propertyId) {
				return StudySearchResultComponent.this.messageSource.getMessage(Message.CLICK_TO_VIEW_STUDY_DETAILS);
			}
		});
	}

	public void setSearchResultDataSource(final IndexedContainer dataSource) {
		this.searchResultTable.setContainerDataSource(dataSource);
	}

	@Override
	public void initializeValues() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListeners() {
		this.searchResultTable.addListener(new StudyItemClickListener(this.parentComponent));
	}

	@Override
	public void layoutComponents() {
		this.setSpacing(true);
		this.setWidth("425px");
		this.addComponent(this.totalEntriesLabel);
		this.addComponent(this.searchResultTable);
	}

	@Override
	public void updateLabels() {
		// TODO Auto-generated method stub

	}

	public void updateNoOfEntries(final int count) {
		this.totalEntriesLabel.setValue(this.messageSource.getMessage(Message.SEARCH_RESULT_LABEL) + ": " + "  <b>" + count + "</b>");
	}

	public void resetSearchResultLayout() {
		this.removeAllComponents();
		this.addComponent(this.totalEntriesLabel);
		this.addComponent(this.searchResultTable);
	}

	public void searchStudy(final StudySearchMatchingOption studySearchMatchingOption, final String name, final String country, final Season season, final Integer date) {
		if (this.searchResultTable != null) {
			this.searchResultTable.removeAllItems();
		}

		final IndexedContainer dataSourceResult =
				this.studyDataContainerBuilder.buildIndexedContainerForStudies(studySearchMatchingOption, name, country, season, date);

		if (dataSourceResult.size() == 0) {
			this.updateNoOfEntries(0);
			MessageNotifier.showMessage(this.getWindow(), this.messageSource.getMessage(Message.NO_STUDIES_FOUND), "");
		} else {
			this.setSearchResultDataSource(dataSourceResult);
			this.updateNoOfEntries(dataSourceResult.size());
			this.resetSearchResultLayout();
			this.requestRepaint();
		}
	}

	public void studyItemClickAction(final Integer studyId) {
		this.studyDataContainerBuilder = new StudyDataContainerBuilder(this.studyDataManager, studyId);

		try {
			final Study study = this.studyDataManager.getStudy(Integer.valueOf(studyId));
			// don't show study details if study record is a Folder ("F")
			final String studyType = study.getType().getName();
			if (!this.hasChildStudy(studyId) && !this.isFolderType(studyType)) {
				this.parentComponent.createStudyInfoTab(studyId);
			}
		} catch (final NumberFormatException e) {
			StudySearchResultComponent.LOG.error(e.toString() + "\n" + e.getStackTrace());
			e.printStackTrace();
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_INVALID_FORMAT),
					this.messageSource.getMessage(Message.ERROR_IN_NUMBER_FORMAT));
		} catch (final MiddlewareException e) {
			StudySearchResultComponent.LOG.error(e.toString() + "\n" + e.getStackTrace());
			e.printStackTrace();
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDY_DETAIL_BY_ID),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDY_DETAIL_BY_ID));
		}
	}

	private Project getCurrentProject() throws MiddlewareQueryException {
		return ContextUtil.getProjectInContext(this.programService, ContextApplication.currentRequest());
	}

	private boolean hasChildStudy(final int studyId) {

		List<Reference> studyChildren = new ArrayList<>();

		try {
			studyChildren.addAll(this.studyDataManager
					.getChildrenOfFolder(Integer.valueOf(studyId), this.getCurrentProject().getUniqueID()));
		} catch (final MiddlewareQueryException e) {
			StudySearchResultComponent.LOG.error(e.toString() + "\n" + e.getStackTrace());
			MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
			studyChildren = new ArrayList<>();
		}
		if (!studyChildren.isEmpty()) {
			return true;
		}
		return false;
	}

	private boolean isFolderType(String type) {
		if (type != null) {
			type = type.toLowerCase();
			if (type.equals("f") || type.equals("folder")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

}
