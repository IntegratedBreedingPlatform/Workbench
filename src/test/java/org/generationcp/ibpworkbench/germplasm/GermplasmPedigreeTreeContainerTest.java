
package org.generationcp.ibpworkbench.germplasm;

import com.vaadin.ui.Button;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.data.initializer.GermplasmPedigreeTreeTestDataInitializer;
import org.generationcp.middleware.util.MaxPedigreeLevelReachedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(value = MockitoJUnitRunner.class)
public class GermplasmPedigreeTreeContainerTest {

	GermplasmPedigreeTreeTestDataInitializer germplasmPedigreeTreeTestDataInitializer;

	@Mock
	GermplasmQueries germplasmQueries;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@InjectMocks
	private GermplasmPedigreeTreeContainer germplasmPedigreeTreeContainer;

	@Before
	public void setUp() throws Exception {
		this.germplasmPedigreeTreeTestDataInitializer = new GermplasmPedigreeTreeTestDataInitializer();
		final GermplasmDetailsComponentTree parent = new GermplasmDetailsComponentTree(1, this.germplasmQueries);
		this.germplasmPedigreeTreeContainer =
			new GermplasmPedigreeTreeContainer(1, this.germplasmQueries, new GermplasmPedigreeTreeContainer.GermplasmPedigreeTreeActions() {

				@Override
				public void showPedigreeGraphWindow() {
				}
			});
		this.germplasmPedigreeTreeContainer.setMessageSource(messageSource);
	}

	@Test
	public void testUpdatePedigreeCountLabelPedigreeDisplayFullPedigreeNotVisible() throws Exception {
		Mockito.when(this.germplasmQueries.generatePedigreeTree(Matchers.anyInt(), Matchers.anyInt(), Matchers.eq(false)))
			.thenReturn(this.germplasmPedigreeTreeTestDataInitializer.createGermplasmPedigreeTree(10, 7));
		this.germplasmPedigreeTreeContainer.afterPropertiesSet();
		final Button displayFullPedigreeButton = this.germplasmPedigreeTreeContainer.getDisplayFullPedigreeButton();
		Assert.assertFalse("Display Full Pedigree Button should not be visible", displayFullPedigreeButton.isVisible());
	}

	@Test
	public void testUpdatePedigreeCountLabelPedigreeDisplayFullPedigreeVisible() throws Exception {
		Mockito.when(this.germplasmQueries.generatePedigreeTree(Matchers.anyInt(), Matchers.anyInt(), Matchers.eq(false)))
			.thenReturn(this.germplasmPedigreeTreeTestDataInitializer.createGermplasmPedigreeTree(10, 10));
		Mockito.when(this.germplasmQueries.getPedigreeLevelCount(Matchers.anyInt(), Matchers.eq(false)))
			.thenThrow(MaxPedigreeLevelReachedException.getInstance());
		this.germplasmPedigreeTreeContainer.afterPropertiesSet();
		final Button displayFullPedigreeButtonBeforeButtonClick = this.germplasmPedigreeTreeContainer.getDisplayFullPedigreeButton();
		Assert.assertTrue("Display Full Pedigree Button should be visible", displayFullPedigreeButtonBeforeButtonClick.isVisible());

		this.germplasmPedigreeTreeContainer.updatePedigreeCountLabel();

		final Button displayFullPedigreeButtonAfterButtonClick = this.germplasmPedigreeTreeContainer.getDisplayFullPedigreeButton();
		Assert.assertFalse("Display Full Pedigree Button should not be visible", displayFullPedigreeButtonAfterButtonClick.isVisible());
	}

	@Test
	public void testRefreshPedigreeTreeDisplayFullPedigreeButtonVisible() throws Exception {
		Mockito.when(this.germplasmQueries.generatePedigreeTree(Matchers.anyInt(), Matchers.anyInt(), Matchers.eq(false)))
			.thenReturn(this.germplasmPedigreeTreeTestDataInitializer.createGermplasmPedigreeTree(10, 10));
		Mockito.when(this.germplasmQueries.getPedigreeLevelCount(Matchers.anyInt(), Matchers.eq(false)))
			.thenThrow(MaxPedigreeLevelReachedException.getInstance());
		this.germplasmPedigreeTreeContainer.afterPropertiesSet();
		final Button displayFullPedigreeButtonBeforeButtonClick = this.germplasmPedigreeTreeContainer.getDisplayFullPedigreeButton();

		Assert.assertTrue("Display Full Pedigree Button should be visible", displayFullPedigreeButtonBeforeButtonClick.isVisible());

		this.germplasmPedigreeTreeContainer.refreshPedigreeTree();

		final Button displayFullPedigreeButtonAfterButtonClick = this.germplasmPedigreeTreeContainer.getDisplayFullPedigreeButton();
		Assert.assertTrue("Display Full Pedigree Button should be visible", displayFullPedigreeButtonAfterButtonClick.isVisible());
	}

	@Test
	public void testRefreshPedigreeTreeDisplayFullPedigreeButtonNotVisible() throws Exception {
		Mockito.when(this.germplasmQueries.generatePedigreeTree(Matchers.anyInt(), Matchers.anyInt(), Matchers.eq(false)))
			.thenReturn(this.germplasmPedigreeTreeTestDataInitializer.createGermplasmPedigreeTree(10, 10));
		Mockito.when(this.germplasmQueries.getPedigreeLevelCount(Matchers.anyInt(), Matchers.eq(false)))
			.thenThrow(MaxPedigreeLevelReachedException.getInstance()).thenReturn(1);
		this.germplasmPedigreeTreeContainer.afterPropertiesSet();
		final Button displayFullPedigreeButtonBeforeButtonClick = this.germplasmPedigreeTreeContainer.getDisplayFullPedigreeButton();

		Assert.assertTrue("Display Full Pedigree Button should be visible", displayFullPedigreeButtonBeforeButtonClick.isVisible());
		this.germplasmPedigreeTreeContainer.refreshPedigreeTree();

		final Button displayFullPedigreeButtonAfterButtonClick = this.germplasmPedigreeTreeContainer.getDisplayFullPedigreeButton();
		Assert.assertFalse("Display Full Pedigree Button should not be visible", displayFullPedigreeButtonAfterButtonClick.isVisible());
	}
}
