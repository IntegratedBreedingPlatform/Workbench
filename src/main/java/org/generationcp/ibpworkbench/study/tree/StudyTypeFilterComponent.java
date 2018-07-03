package org.generationcp.ibpworkbench.study.tree;

import org.generationcp.ibpworkbench.GermplasmStudyBrowserLayout;
import org.generationcp.ibpworkbench.study.constants.StudyTypeFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@Configurable
public class StudyTypeFilterComponent extends HorizontalLayout implements InitializingBean, GermplasmStudyBrowserLayout {

	private static final long serialVersionUID = 1L;
	private ComboBox studyTypeComboBox;
	private StudyTypeChangeListener listener;
	private Label studyTypeLabel;
	
	public StudyTypeFilterComponent(StudyTypeChangeListener listener) {
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
		for (final StudyTypeFilter type : StudyTypeFilter.values()) {
			this.studyTypeComboBox.addItem(type);
			this.studyTypeComboBox.setItemCaption(type, type.getLabel());
		}
		this.studyTypeComboBox.select(StudyTypeFilter.ALL);
		
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

				listener.studyTypeChange((StudyTypeFilter)studyTypeComboBox.getValue());
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
		return studyTypeComboBox;
	}

}
