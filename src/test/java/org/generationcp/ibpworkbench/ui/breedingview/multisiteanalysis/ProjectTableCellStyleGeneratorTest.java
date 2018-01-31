package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTableCellStyleGeneratorTest {
	
	@Mock
	private ContextUtil contextUtil;
	
	@InjectMocks
	private ProjectTableCellStyleGenerator projectTableCellStyleGenerator;
	
	@Before
	public void setUp() {
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(ProjectTestDataInitializer.createProject());
	}
	
	@Test
	public void testGetStyleWhereProjectIdIsNull() {
		final String style = this.projectTableCellStyleGenerator.getStyle(null, null);
		Assert.assertEquals(ProjectTableCellStyleGenerator.PROJECT_TABLE, style);
	}
	
	@Test
	public void testGetStyleWhereProjectIdIsNotNull() {
		final String style = this.projectTableCellStyleGenerator.getStyle((long)1, null);
		Assert.assertEquals(ProjectTableCellStyleGenerator.GCP_SELECTED_PROJECT, style);
	}
	
	@Test
	public void testGetStyleWhereCurrentProjectIsNull() {
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(null);
		final String style = this.projectTableCellStyleGenerator.getStyle(null, null);
		Assert.assertEquals(ProjectTableCellStyleGenerator.PROJECT_TABLE, style);
	}
}
