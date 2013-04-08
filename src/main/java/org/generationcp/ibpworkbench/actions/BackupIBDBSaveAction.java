package org.generationcp.ibpworkbench.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

public class BackupIBDBSaveAction implements ClickListener {
	private static final Logger LOG = LoggerFactory.getLogger(BackupIBDBSaveAction.class);
	
	private Window sourceWindow;
	private Select select;
	
	public BackupIBDBSaveAction(Select select) {
		this.select = select;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		LOG.debug("onClick > do save backup");
		
		sourceWindow = event.getButton().getWindow();
		
	}

}
