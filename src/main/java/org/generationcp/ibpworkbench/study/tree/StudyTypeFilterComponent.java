
package org.generationcp.ibpworkbench.study.tree;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.util.List;

@Configurable
public class StudyTypeFilterComponent extends HorizontalLayout implements InitializingBean, GermplasmStudyBrowserLayout {

	public static final String ALL = "All";
	// Dummy StudyTypeDto for "All" option in ComboBox
	public static final StudyTypeDto ALL_OPTION = new StudyTypeDto(1, "Studies", StudyTypeFilterComponent.ALL);
	private static final long serialVersionUID = 1L;

	@Resource
	private StudyDataManager studyDataManager;

	private ComboBox studyTypeComboBox;
	private final StudyTypeChangeListener listener;
	private Label studyTypeLabel;

	public StudyTypeFilterComponent(final StudyTypeChangeListener listener) {
		super();
		this.listener = listener;
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
		this.studyTypeComboBox = new ComboBox();
		this.studyTypeComboBox.setWidth("200px");
		this.studyTypeComboBox.setNullSelectionAllowed(false);
		this.studyTypeComboBox.setNewItemsAllowed(false);
		this.studyTypeComboBox.setImmediate(true);

		this.studyTypeLabel = new Label("Study type");
		this.studyTypeLabel.setDebugId("studyTypeLabel");
		this.studyTypeLabel.setStyleName("label-bold");
		this.studyTypeLabel.setWidth("75px");

	}

	@Override
	public void initializeValues() {
		this.studyTypeComboBox.addItem(StudyTypeFilterComponent.ALL_OPTION);
		this.studyTypeComboBox.setItemCaption(StudyTypeFilterComponent.ALL_OPTION, StudyTypeFilterComponent.ALL_OPTION.getName());
		final List<StudyTypeDto> studyTypes = this.studyDataManager.getAllVisibleStudyTypes();
		for (final StudyTypeDto type : studyTypes) {
			this.studyTypeComboBox.addItem(type);
			this.studyTypeComboBox.setItemCaption(type, type.getLabel());
		}
		this.studyTypeComboBox.select(StudyTypeFilterComponent.ALL_OPTION);
	}

	@Override
	public void addListeners() {
		this.studyTypeComboBox.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(final ValueChangeEvent event) {

				if (event.getProperty() == null) {
					return;
				}
				if (event.getProperty().getValue() == null) {
					return;
				}

				StudyTypeFilterComponent.this.listener
						.studyTypeChange((StudyTypeDto) StudyTypeFilterComponent.this.studyTypeComboBox.getValue());
			}
		});

	}

	@Override
	public void layoutComponents() {
		this.setWidth("300px");
		this.addComponent(this.studyTypeLabel);
		this.addComponent(this.studyTypeComboBox);
	}

	public ComboBox getStudyTypeComboBox() {
		return this.studyTypeComboBox;
	}

	public boolean isAllOptionChosen(final StudyType type) {
		return StudyTypeFilterComponent.ALL_OPTION.equals(type);
	}

	protected void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

}
