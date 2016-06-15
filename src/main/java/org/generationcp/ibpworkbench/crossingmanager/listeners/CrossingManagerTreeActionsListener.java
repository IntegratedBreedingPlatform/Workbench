
package org.generationcp.ibpworkbench.crossingmanager.listeners;

import org.generationcp.ibpworkbench.listeners.ListTreeActionsListener;

public interface CrossingManagerTreeActionsListener extends ListTreeActionsListener {

	public void addListToFemaleList(Integer germplasmListId);

	public void addListToMaleList(Integer germplasmListId);

}
