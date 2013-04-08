package org.generationcp.ibpworkbench.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class RestoreIBDBSaveAction implements ClickListener {
	private static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBSaveAction.class);
	private Window sourceWindow;
	private Select select;
	private Table table;
	
	public RestoreIBDBSaveAction(Select select,Table table) {
		this.select = select;
		this.table = table;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		LOG.debug("onClick > do Restore IBDB");
		
		sourceWindow = event.getButton().getWindow();
	}

}
