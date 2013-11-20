package org.generationcp.ibpworkbench.ui;

import java.util.Map;

import org.generationcp.middleware.domain.dms.Study;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class StudiesTabCloseListener implements CloseHandler {

	/**
	 * 
	 */
	private Map<Integer, Table> studyTables;
	
	private static final long serialVersionUID = -255689777984511357L;
	
	public StudiesTabCloseListener(Map<Integer, Table> studyTables) {
		this.studyTables = studyTables;
	}

	@Override
	public void onTabClose(TabSheet tabsheet, Component tabContent) {
		// TODO Auto-generated method stub
		studyTables.remove(((Study)((VerticalLayout)tabContent).getData()).getId());
		tabsheet.removeComponent(tabContent);
		
	}

}
