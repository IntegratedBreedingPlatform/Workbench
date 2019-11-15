package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.vaadin.data.Property;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDesignDetails;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BreedingViewDesignTypeValueChangeListenerTest {

	@Mock
	private SingleSiteAnalysisDesignDetails source;

	@Mock
	private Property.ValueChangeEvent event;

	@Mock
	private Property property;

	@InjectMocks
	private final BreedingViewDesignTypeValueChangeListener listener = new BreedingViewDesignTypeValueChangeListener(this.source);

	@Before
	public void init() {
		Mockito.when(this.event.getProperty()).thenReturn(this.property);
	}

	@Test
	public void testValueChangeRandomizedBlockDesign() {

		Mockito.when(this.property.getValue()).thenReturn(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName());

		this.listener.valueChange(this.event);

		Mockito.verify(this.source).displayRandomizedBlockDesignElements();

	}

	@Test
	public void testValueChangeResolvableRowColumnDesign() {

		Mockito.when(this.property.getValue()).thenReturn(ExperimentDesignType.ROW_COL.getBvName());

		this.listener.valueChange(this.event);

		Mockito.verify(this.source).displayRowColumnDesignElements();

	}

	@Test
	public void testValueChangeResolvableIncompleteBlockDesign() {

		Mockito.when(this.property.getValue()).thenReturn(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName());

		this.listener.valueChange(this.event);

		Mockito.verify(this.source).displayIncompleteBlockDesignElements();

	}

	@Test
	public void testValueChangePRepDesign() {

		Mockito.when(this.property.getValue()).thenReturn(ExperimentDesignType.P_REP.getBvName());

		this.listener.valueChange(this.event);

		Mockito.verify(this.source).displayPRepDesignElements();

	}

	@Test
	public void testValueChange() {

		Mockito.when(this.property.getValue()).thenReturn(ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getBvName());

		this.listener.valueChange(this.event);

		Mockito.verify(this.source).displayAugmentedDesignElements();

	}

}
