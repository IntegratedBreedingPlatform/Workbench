
package org.generationcp.ibpworkbench.listmanager;

import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.customfields.ListTreeTableComponent;
import org.generationcp.ibpworkbench.listeners.ListTreeActionsListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ListManagerTreeComponent extends ListTreeTableComponent implements InitializingBean {

	private static final long serialVersionUID = -1013380483927558222L;

	public ListManagerTreeComponent() {
		super();
	}

	public ListManagerTreeComponent(final ListTreeActionsListener treeActionListener, Integer listId) {
		super(treeActionListener, listId);
	}

	@Override
	protected boolean doIncludeActionsButtons() {
		return true;
	}

	@Override
	protected boolean doIncludeRefreshButton() {
		return false;
	}

	@Override
	protected boolean isTreeItemsDraggable() {
		return true;
	}

	@Override
	protected boolean doShowFoldersOnly() {
		return false;
	}

	@Override
	public boolean usedInSubWindow() {
		return true;
	}

	@Override
	public String getTreeHeading() {
		return this.messageSource.getMessage(Message.ALL_LISTS);
	}

	@Override
	public String getTreeStyleName() {
		return "listManagerTree";
	}

}
