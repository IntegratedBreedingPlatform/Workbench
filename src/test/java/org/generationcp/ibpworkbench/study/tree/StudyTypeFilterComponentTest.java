package org.generationcp.ibpworkbench.study.tree;

import org.generationcp.ibpworkbench.study.constants.StudyTypeFilter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.ComboBox;

import junit.framework.Assert;

public class StudyTypeFilterComponentTest {
	
	@Mock
	private StudyTypeChangeListener listener;
	
	@InjectMocks
	private StudyTypeFilterComponent filterComponent;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.filterComponent.afterPropertiesSet();
	}
	
	@Test
	public void testAfterPropertiesSet() {
		final ComboBox studyTypeComboBox = this.filterComponent.getStudyTypeComboBox();
		Assert.assertNotNull(studyTypeComboBox);
		Assert.assertEquals(StudyTypeFilter.values().length, studyTypeComboBox.size());
		for (final StudyTypeFilter filter : StudyTypeFilter.values()) {
			studyTypeComboBox.containsId(filter);
		}
		Assert.assertEquals(StudyTypeFilter.ALL, studyTypeComboBox.getValue());
	}
	
	@Test
	public void testStudyTypeFilterValueChange() {
		this.filterComponent.getStudyTypeComboBox().setValue(StudyTypeFilter.TRIAL);
		Mockito.verify(this.listener).studyTypeChange(StudyTypeFilter.TRIAL);
	}

}
