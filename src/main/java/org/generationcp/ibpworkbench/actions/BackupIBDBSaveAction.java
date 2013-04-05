package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

public class BackupIBDBSaveAction implements ClickListener {
	private Window sourceWindow;
	private Select select;
	
	public BackupIBDBSaveAction(Select select) {
		this.select = select;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		sourceWindow = event.getButton().getWindow();
		
		
	}

}
