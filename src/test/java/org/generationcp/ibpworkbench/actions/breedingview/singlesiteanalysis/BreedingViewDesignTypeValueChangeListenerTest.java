package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDesignDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.vaadin.data.Property;

@RunWith(MockitoJUnitRunner.class)
public class BreedingViewDesignTypeValueChangeListenerTest {

	@Mock
	private SingleSiteAnalysisDesignDetails source;

	@Mock
	private Property.ValueChangeEvent event;

	@Mock
	private Property property;


	@InjectMocks
	private BreedingViewDesignTypeValueChangeListener listener = new BreedingViewDesignTypeValueChangeListener(source);


	@Before
	public void init() {
		Mockito.when(event.getProperty()).thenReturn(this.property);
	}


	@Test
	public void testValueChangeRandomizedBlockDesign() {

		Mockito.when(property.getValue()).thenReturn(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		listener.valueChange(this.event);

		Mockito.verify(this.source).displayRandomizedBlockDesignElements();

	}

	@Test
	public void testValueChangeResolvableRowColumnDesign() {

		Mockito.when(property.getValue()).thenReturn(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());

		listener.valueChange(this.event);

		Mockito.verify(this.source).displayRowColumnDesignElements();

	}

	@Test
	public void testValueChangeResolvableIncompleteBlockDesign() {

		Mockito.when(property.getValue()).thenReturn(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());

		listener.valueChange(this.event);

		Mockito.verify(this.source).displayIncompleteBlockDesignElements();

	}

	@Test
	public void testValueChangePRepDesign() {

		Mockito.when(property.getValue()).thenReturn(DesignType.P_REP_DESIGN.getName());

		listener.valueChange(this.event);

		Mockito.verify(this.source).displayPRepDesignElements();

	}

	@Test
	public void testValueChange() {

		Mockito.when(property.getValue()).thenReturn(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName());

		listener.valueChange(this.event);

		Mockito.verify(this.source).displayAugmentedDesignElements();

	}

}
