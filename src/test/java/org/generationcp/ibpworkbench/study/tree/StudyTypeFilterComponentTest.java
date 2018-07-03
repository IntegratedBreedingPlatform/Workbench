package org.generationcp.ibpworkbench.study.tree;

import java.util.Arrays;

import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.ComboBox;

import junit.framework.Assert;

public class StudyTypeFilterComponentTest {
	
	private static final StudyTypeDto TRIAL = new StudyTypeDto(1, "Trial", "T");
	private static final StudyTypeDto NURSERY = new StudyTypeDto(2, "Nursery", "N");
	
	@Mock
	private StudyTypeChangeListener listener;
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@InjectMocks
	private StudyTypeFilterComponent filterComponent;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.filterComponent.setStudyDataManager(studyDataManager);
		Mockito.doReturn(Arrays.asList(TRIAL, NURSERY)).when(this.studyDataManager).getAllVisibleStudyTypes();
		this.filterComponent.afterPropertiesSet();
	}
	
	@Test
	public void testAfterPropertiesSet() {
		final ComboBox studyTypeComboBox = this.filterComponent.getStudyTypeComboBox();
		Assert.assertNotNull(studyTypeComboBox);
		Assert.assertEquals(3, studyTypeComboBox.size());
		Assert.assertTrue(studyTypeComboBox.containsId(StudyTypeFilterComponent.ALL_OPTION));
		Assert.assertEquals(StudyTypeFilterComponent.ALL, studyTypeComboBox.getItemCaption(StudyTypeFilterComponent.ALL_OPTION));
		Assert.assertTrue(studyTypeComboBox.containsId(TRIAL));
		Assert.assertEquals(TRIAL.getLabel(), studyTypeComboBox.getItemCaption(TRIAL));
		Assert.assertTrue(studyTypeComboBox.containsId(NURSERY));
		Assert.assertEquals(NURSERY.getLabel(), studyTypeComboBox.getItemCaption(NURSERY));
		Assert.assertEquals(StudyTypeFilterComponent.ALL_OPTION, studyTypeComboBox.getValue());
	}
	
	@Test
	public void testStudyTypeFilterValueChange() {
		this.filterComponent.getStudyTypeComboBox().setValue(TRIAL);
		Mockito.verify(this.listener).studyTypeChange(TRIAL);
	}

}
