package org.generationcp.ibpworkbench.comp.table;

import java.util.Collection;
import java.util.Iterator;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.OptionGroup;

public class GxeCheckBoxGroupListener implements ValueChangeListener {

	private static final long serialVersionUID = 1L;

	private String[] stringList;
	private GxeTable table;
	private CheckBox source;
	public GxeCheckBoxGroupListener(String[] stringList ,GxeTable table, CheckBox source) {
			this.stringList = stringList;
			this.table = table;
			this.source = source;
	}


	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		// TODO Auto-generated method stub
		
		if (source.getCaption().equalsIgnoreCase("All rows")){
			Collection<?> items = table.getContainerDataSource().getItemIds();
			for (Iterator<?> myitems = items.iterator(); myitems.hasNext();) {

				Integer key = (Integer) myitems.next();
				Item item = table.getContainerDataSource().getItem(key);
				
				if (key > 1){
					try{
						GxeCheckBoxGroup ba = (GxeCheckBoxGroup) item.getItemProperty(" ").getValue();
						ba.setValue((Boolean) event.getProperty().getValue());
					}catch(Exception e){
						
					}
					
				}
				
			}
			
		}else if (source.getCaption().equalsIgnoreCase("All columns")){
			
			Collection<?> cols = table.getContainerDataSource().getItem(1).getItemPropertyIds();
			
			for (Iterator<?> myItems = cols.iterator(); myItems.hasNext();){
				
				Object obj = table.getContainerDataSource().getItem(1).getItemProperty(myItems.next()).getValue();
				if (obj instanceof CheckBox){
					((CheckBox) obj).setValue(event.getProperty().getValue());
				}
				
				
			}
		}else if (source.getCaption().equals(" ")){
			
			((GxeCheckBoxGroup) source.getParent()).setValue((Boolean)event.getProperty().getValue());
			
		}
		
		table.requestRepaintAll();
	
	}


}
