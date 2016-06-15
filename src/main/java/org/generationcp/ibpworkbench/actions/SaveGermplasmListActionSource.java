
package org.generationcp.ibpworkbench.actions;

import java.util.List;

import org.generationcp.middleware.pojos.GermplasmListData;

public interface SaveGermplasmListActionSource {

	public void updateListDataTable(Integer germplasmListId, List<GermplasmListData> listDataEntries);
}
