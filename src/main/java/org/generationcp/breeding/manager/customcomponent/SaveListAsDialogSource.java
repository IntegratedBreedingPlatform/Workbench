
package org.generationcp.breeding.manager.customcomponent;

import com.vaadin.ui.Component;
import org.generationcp.middleware.pojos.GermplasmList;

public interface SaveListAsDialogSource {

	void saveList(GermplasmList list);

	void setCurrentlySavedGermplasmList(GermplasmList list);

	Component getParentComponent();
	
	void updateListUI();
}
