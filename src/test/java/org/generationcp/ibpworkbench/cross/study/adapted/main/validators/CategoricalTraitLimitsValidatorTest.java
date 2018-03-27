package org.generationcp.ibpworkbench.cross.study.adapted.main.validators;

import org.generationcp.ibpworkbench.cross.study.constants.CategoricalVariatesCondition;
import org.generationcp.middleware.data.initializer.CategoricalValueTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.ComboBox;

public class CategoricalTraitLimitsValidatorTest {
	private CategoricalTraitLimitsValidator categoricalTraitLimitsValidator;

	@Before
	public void setUp() {
		final ComboBox conditionBox = new ComboBox();
		conditionBox.addItem(CategoricalVariatesCondition.IN);
		conditionBox.setValue(CategoricalVariatesCondition.IN);
		this.categoricalTraitLimitsValidator = new CategoricalTraitLimitsValidator(conditionBox,
				CategoricalValueTestDataInitializer.createCategoricalValueList());
	}

	@Test
	public void testIsValidTrue() {
		final Object valuesWithSpace = "1, 2, 3, 4";
		Assert.assertTrue(this.categoricalTraitLimitsValidator.isValid(valuesWithSpace));

		final Object valuesWithoutSpace = "1,2,3,4";
		Assert.assertTrue(this.categoricalTraitLimitsValidator.isValid(valuesWithoutSpace));
	}

	@Test
	public void testIsValidFalse() {
		final Object values = "1, 2, 3, 4, 5";
		Assert.assertFalse(this.categoricalTraitLimitsValidator.isValid(values));
	}
}
