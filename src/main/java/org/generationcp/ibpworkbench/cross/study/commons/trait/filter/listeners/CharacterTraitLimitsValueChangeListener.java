
package org.generationcp.ibpworkbench.cross.study.commons.trait.filter.listeners;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Window;
import org.generationcp.ibpworkbench.cross.study.util.TraitFilterUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CharacterTraitLimitsValueChangeListener implements ValueChangeListener {

	private static final long serialVersionUID = 4832016453511271830L;

	private final Window parentWindow;
	private final List<String> acceptedLimits;

	public CharacterTraitLimitsValueChangeListener(Window parentWindow, List<String> acceptedLimits) {
		this.parentWindow = parentWindow;
		this.acceptedLimits = acceptedLimits;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		String value = (String) event.getProperty().getValue();
		StringTokenizer tokenizer = new StringTokenizer(value, ",");
		List<String> givenLimits = new ArrayList<String>();

		while (tokenizer.hasMoreTokens()) {
			String limit = tokenizer.nextToken().trim();
			givenLimits.add(limit);
		}

		List<String> fromValidation = TraitFilterUtil.validateCharacterTraitLimits(this.acceptedLimits, givenLimits);

		if (!fromValidation.isEmpty()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("The following limits are invalid: ");
			for (String invalidLimit : fromValidation) {
				buffer.append(invalidLimit);
				buffer.append(", ");
			}

			MessageNotifier.showError(this.parentWindow, "Error with entered limits", buffer.toString().trim());
		}
	}

}
