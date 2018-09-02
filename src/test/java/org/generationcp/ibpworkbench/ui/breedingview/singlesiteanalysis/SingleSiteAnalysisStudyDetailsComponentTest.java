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
	
	@Mock
	private SingleSiteAnalysisDetailsPanel ssaDetailsPanel;
	
	private SingleSiteAnalysisStudyDetailsComponent studyDetailsComponent;
	
	private BreedingViewInput input;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.input = new BreedingViewInput();
		this.input.setDatasetName(RandomStringUtils.randomAlphanumeric(20));
		this.input.setDatasetSource(RandomStringUtils.randomAlphanumeric(20));
		this.input.setObjective(RandomStringUtils.randomAlphanumeric(20));
		this.input.setDescription(RandomStringUtils.randomAlphanumeric(20));
		this.input.setBreedingViewAnalysisName(RandomStringUtils.randomAlphanumeric(20));
		Mockito.doReturn(this.input).when(this.ssaDetailsPanel).getBreedingViewInput();
		
		this.studyDetailsComponent = new SingleSiteAnalysisStudyDetailsComponent(this.ssaDetailsPanel);
		this.studyDetailsComponent.setMessageSource(messageSource);
		this.studyDetailsComponent.instantiateComponents();
	}
	
	@Test
	public void testInitialization() {
		this.studyDetailsComponent.initializeValues();
		Assert.assertEquals("Field Trial", this.studyDetailsComponent.getProjectTypeValue());
		Assert.assertEquals(this.input.getDatasetName(), this.studyDetailsComponent.getDatasetNameValue());
		Assert.assertEquals(this.input.getDatasetSource(), this.studyDetailsComponent.getStudyNameValue());
		Assert.assertEquals(this.input.getDescription(), this.studyDetailsComponent.getDescriptionValue());
		Assert.assertEquals(this.input.getObjective(), this.studyDetailsComponent.getObjectiveValue());
		Assert.assertEquals(this.input.getBreedingViewAnalysisName(), this.studyDetailsComponent.getTxtAnalysisName());
	}

}
