package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

public class SingleSiteAnalysisStudyDetailsComponentTest {

	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	private SingleSiteAnalysisStudyDetailsComponent studyDetailsComponent;

	public static final String DATASET_NAME = RandomStringUtils.randomAlphanumeric(20);
	public static final String STUDY_NAME =RandomStringUtils.randomAlphanumeric(20);
	public static final String OBJECTIVE =RandomStringUtils.randomAlphanumeric(20);
	public static final String DESCRIPTION =RandomStringUtils.randomAlphanumeric(20);
	public static final String ANALYSIS_NAME =RandomStringUtils.randomAlphanumeric(20);

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.studyDetailsComponent = new SingleSiteAnalysisStudyDetailsComponent(DATASET_NAME, DESCRIPTION, OBJECTIVE, STUDY_NAME,
				ANALYSIS_NAME, true);
		this.studyDetailsComponent.setMessageSource(messageSource);
		this.studyDetailsComponent.instantiateComponents();
	}
	
	@Test
	public void testInitialization() {
		this.studyDetailsComponent.initializeValues();
		Assert.assertEquals("Field Trial", this.studyDetailsComponent.getProjectTypeValue());
		Assert.assertEquals(DATASET_NAME, this.studyDetailsComponent.getDatasetNameValue());
		Assert.assertEquals(STUDY_NAME, this.studyDetailsComponent.getStudyNameValue());
		Assert.assertEquals(DESCRIPTION, this.studyDetailsComponent.getDescriptionValue());
		Assert.assertEquals(OBJECTIVE, this.studyDetailsComponent.getObjectiveValue());
		Assert.assertEquals(ANALYSIS_NAME, this.studyDetailsComponent.getTxtAnalysisName());
	}

}
