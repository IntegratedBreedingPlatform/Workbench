
package org.generationcp.ibpworkbench.listmanager.dialog;

import java.util.List;

import org.generationcp.ibpworkbench.listmanager.ListManagerMain;

public interface AddEntryDialogSource {

	public void finishAddingEntry(Integer gid);

	public void finishAddingEntry(List<Integer> gids);

	public ListManagerMain getListManagerMain();

}
