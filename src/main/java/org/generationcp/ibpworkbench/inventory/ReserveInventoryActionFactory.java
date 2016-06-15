package org.generationcp.ibpworkbench.inventory;

public class ReserveInventoryActionFactory {

	public ReserveInventoryAction createInstance(final ReserveInventorySource source) {
		return new ReserveInventoryAction(source);
	}

}
