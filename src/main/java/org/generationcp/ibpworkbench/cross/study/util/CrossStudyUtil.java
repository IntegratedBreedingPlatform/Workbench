
package org.generationcp.ibpworkbench.cross.study.util;

import com.vaadin.ui.ComboBox;
import org.generationcp.ibpworkbench.cross.study.constants.CategoricalVariatesCondition;
import org.generationcp.ibpworkbench.cross.study.constants.CharacterTraitCondition;
import org.generationcp.ibpworkbench.cross.study.constants.EnvironmentWeight;
import org.generationcp.ibpworkbench.cross.study.constants.NumericTraitCriteria;
import org.generationcp.ibpworkbench.cross.study.constants.TraitWeight;

public class CrossStudyUtil {

	/**
	 * Creates a ComboBox with values of <class>EnvironmentWeight</class> enum
	 * 
	 * @return
	 */
	public static ComboBox getWeightComboBox() {
		ComboBox combo = new ComboBox();
		combo.setDebugId("combo");
		combo.setNullSelectionAllowed(false);
		combo.setTextInputAllowed(false);
		combo.setImmediate(true);

		for (EnvironmentWeight weight : EnvironmentWeight.values()) {
			combo.addItem(weight);
			combo.setItemCaption(weight, weight.getLabel());
		}

		combo.setValue(EnvironmentWeight.IGNORED);

		combo.setEnabled(false);
		return combo;
	}

	/**
	 * Creates a combobox with values of <class>NumericTraitCriteria</class> enum
	 * 
	 * @return
	 */
	public static ComboBox getNumericTraitCombobox() {
		ComboBox combo = new ComboBox();
		combo.setDebugId("combo");
		combo.setNullSelectionAllowed(false);
		combo.setTextInputAllowed(false);
		combo.setImmediate(true);

		for (NumericTraitCriteria criteria : NumericTraitCriteria.values()) {
			combo.addItem(criteria);
			combo.setItemCaption(criteria, criteria.getLabel());
		}

		combo.setValue(NumericTraitCriteria.KEEP_ALL);

		combo.setEnabled(true);
		return combo;
	}

	/**
	 * Creates a ComboBox with values of <class>TraitWeight</class> enum
	 * 
	 * @return
	 */
	public static ComboBox getTraitWeightsComboBox() {
		ComboBox combo = new ComboBox();
		combo.setDebugId("combo");
		combo.setNullSelectionAllowed(false);
		combo.setTextInputAllowed(false);
		combo.setImmediate(true);

		for (TraitWeight weight : TraitWeight.values()) {
			combo.addItem(weight);
			combo.setItemCaption(weight, weight.getLabel());
		}

		// default value is important
		combo.setValue(TraitWeight.IMPORTANT);

		combo.setEnabled(true);
		return combo;
	}

	/**
	 * Creates a ComboBox with values of <class>CharacterTraitCondition</class> enum
	 * 
	 * @return
	 */
	public static ComboBox getCharacterTraitConditionsComboBox() {
		ComboBox combo = new ComboBox();
		combo.setDebugId("combo");
		combo.setNullSelectionAllowed(false);
		combo.setTextInputAllowed(false);
		combo.setImmediate(true);

		for (CharacterTraitCondition condition : CharacterTraitCondition.values()) {
			combo.addItem(condition);
			combo.setItemCaption(condition, condition.getLabel());
		}

		// default value is keep all
		combo.setValue(CharacterTraitCondition.KEEP_ALL);

		combo.setEnabled(true);
		return combo;
	}

	/**
	 * Creates a ComboBox with values of <class>CategoricalVariatesCondition</class> enum
	 * 
	 * @return
	 */
	public static ComboBox getCategoricalVariatesComboBox() {
		ComboBox combo = new ComboBox();
		combo.setDebugId("combo");
		combo.setNullSelectionAllowed(false);
		combo.setTextInputAllowed(false);
		combo.setImmediate(true);

		for (CategoricalVariatesCondition condition : CategoricalVariatesCondition.values()) {
			combo.addItem(condition);
			combo.setItemCaption(condition, condition.getLabel());
		}

		// default value is keep all
		combo.setValue(CategoricalVariatesCondition.KEEP_ALL);

		combo.setEnabled(false);
		return combo;
	}
}
