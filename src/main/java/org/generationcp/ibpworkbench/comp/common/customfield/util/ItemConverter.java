package org.generationcp.ibpworkbench.comp.common.customfield.util;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;

import java.util.*;

import org.generationcp.ibpworkbench.comp.common.customfield.PropertyConverter;

/**
 * It's hard to use {@link PropertyConverter} alone, so ItemConverter allows to
 * wrap your Item and replace several properties with converted/formatted/other properties.
 *
 * @author Andrew Fink [aprpda@gmail.com] http://magicprinc.blogspot.com
 *
 * @see PropertyConverter
 * @see ItemFormatter
 * @see Property
 */
public class ItemConverter implements Item, Item.PropertySetChangeNotifier, Cloneable, Item.Editor {

	private Item item;
	private Map<Object,? extends Property> instances = Collections.emptyMap();
	private boolean convertNullProperties;


	public ItemConverter () {
	}//new


	public ItemConverter (Item item) {
		setItemDataSource(item);
	}//new


	public void setItemDataSource (Item newDataSource) {
		item = newDataSource;
	}

	public Item getItemDataSource () {
		return item;
	}


	/** Set Map[PID, Property instances], Instances will be used directly "as is". You can use any {@link Property},
	 * {@link PropertyConverter} or {@link com.vaadin.data.util.PropertyFormatter} */
	public void setConverters (Map<Object,? extends Property> asis) {
		if (asis == null) {
			throw new NullPointerException("setConverters");
		}
		instances = asis;
	}


	@SuppressWarnings("unchecked")
	private Map<Object,Property> checkInstancesMap () {
		if (instances == (Map<Object,? extends Property>) Collections.EMPTY_MAP) {
			instances = new LinkedHashMap<Object,Property>();
		}
		return (Map<Object,Property>) instances;
	}

	public ItemConverter addConverter (Object pid, PropertyConverter<?,?> propertyConverter) {
		checkInstancesMap().put(pid, propertyConverter);
		return this;
	}//addConverter


	public ItemConverter addFormatter (Object pid, PropertyFormatter propertyFormatter) {
		checkInstancesMap().put(pid, propertyFormatter);
		return this;
	}


	public boolean getConvertNullProperties () {	return convertNullProperties;}

	public void setConvertNullProperties (boolean dontSkip) {
		convertNullProperties = dontSkip;
	}


	 public Property getItemProperty (Object pid) throws IllegalStateException {
		Property replace = instances.get(pid);

		if (replace != null) {
			final Property property = item.getItemProperty(pid);
			if (property == null && !convertNullProperties) {
				return null;//not found
			}

			if (replace instanceof PropertyConverter) {
				((PropertyConverter) replace).setPropertyDataSource(property);
				return replace;

			} else if (replace instanceof PropertyFormatter) {
				((PropertyFormatter) replace).setPropertyDataSource(property);
				return replace;

			} else if (replace instanceof Property.Viewer) {//PropertyConverter and PropertyFormatter could be impl Viewer
				((Property.Viewer) replace).setPropertyDataSource(property);
				return replace;

			} else {
				return replace;
			}
		}

		return item.getItemProperty(pid);//as is
	}//getItemProperty


	public Collection<?> getItemPropertyIds () {
		return item.getItemPropertyIds();
	}


	public boolean addItemProperty (Object pid, Property property) throws UnsupportedOperationException {
		return item.addItemProperty(pid, property);
	}


	public boolean removeItemProperty (Object pid) throws UnsupportedOperationException {
		return item.removeItemProperty(pid);
	}


	public void addListener (PropertySetChangeListener listener) {
		if (item instanceof PropertySetChangeNotifier) {
			((PropertySetChangeNotifier) item).addListener(listener);
		}
	}//addListener


	public void removeListener (PropertySetChangeListener listener) {
		if (item instanceof PropertySetChangeNotifier) {
			((PropertySetChangeNotifier) item).removeListener(listener);
		}
	}//removeListener

}//ItemConverter