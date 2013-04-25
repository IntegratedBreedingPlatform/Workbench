package org.generationcp.ibpworkbench.comp.common.customfield.util;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;

import java.util.*;

/**
 * It's hard to use {@link PropertyFormatter} alone, so ItemFormatter allows to
 * wrap your Item and replace several properties with formatted properties.
 * 
 * @author Andrew Fink [aprpda@gmail.com] http://magicprinc.blogspot.com
 * 
 * @see com.vaadin.data.util.PropertyFormatter
 * @see org.vaadin.addon.customfield.PropertyConverter
 */
public class ItemFormatter implements Item, Item.PropertySetChangeNotifier,
        Cloneable, Item.Viewer {
    private Item item;
    private Map<Object, ? extends PropertyFormatter> instances = Collections.emptyMap();
    private Map<Object, Class<? extends PropertyFormatter>> classes = Collections.emptyMap();
	  private boolean formatNullProperties;


    public ItemFormatter() {
    }// new

    public ItemFormatter(Item item) {
        setItemDataSource(item);
    }// new


    public void setItemDataSource(Item newDataSource) {
        item = newDataSource;
    }

    public Item getItemDataSource() {
        return item;
    }

	  /** Set Map[PID, PropertyFormatter instances], Instances will be used directly "as is",
	   * but multithread unsafe */
    public void setFormatterInstances(Map<Object, ? extends PropertyFormatter> asis) {
	    if (asis == null) {
		    throw new NullPointerException("setFormatterInstances");
	    }
	    instances = asis;
    }

		@SuppressWarnings("unchecked")
		public ItemFormatter addFormatter (Object pid, PropertyFormatter propertyFormatterInstance) {
			if (instances == (Map<Object,? extends PropertyFormatter>) Collections.EMPTY_MAP) {
				instances = new LinkedHashMap<Object,PropertyFormatter>();
			}
			((Map) instances).put(pid, propertyFormatterInstance);

			return this;
		}

	  /** Set Map[PID, Class of PropertyFormatter], Instances will be created from Class each call to getItemProperty,
	   * but thread safe */
    public void setFormatterClasses(Map<Object, Class<? extends PropertyFormatter>> classes) {
	    if (classes == null) {
		    throw new NullPointerException("setFormatterClasses");
	    }
	    this.classes = classes;
    }

		public boolean getFormatNullProperties() { return formatNullProperties;}


		public void setFormatNullProperties (boolean dontSkip) {
			formatNullProperties = dontSkip;
		}


		public Property getItemProperty(Object pid) throws IllegalStateException {
        final Property property = item.getItemProperty(pid);
				if (property == null && !formatNullProperties) {
					return null;
				}//not exists

        PropertyFormatter propertyFormatter = instances.get(pid);
        if (propertyFormatter != null) {
            propertyFormatter.setPropertyDataSource(property);
            return propertyFormatter;
        }

        Class<? extends PropertyFormatter> propertyFormatterClass = classes.get(pid);
        if (propertyFormatterClass != null) {
            try {
                propertyFormatter = propertyFormatterClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalStateException(
                        "getItemProperty: can't create "
                                + propertyFormatterClass.getName()
                                + " instance for PID=" + pid, e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "getItemProperty: can't create "
                                + propertyFormatterClass.getName()
                                + " instance for PID=" + pid + ": IAE", e);
            }// t

            propertyFormatter.setPropertyDataSource(property);
            return propertyFormatter;
        }// i

        return property;// as is
    }//getItemProperty


    public Collection<?> getItemPropertyIds() {
        return item.getItemPropertyIds();
    }

    public boolean addItemProperty(Object pid, Property property)
            throws UnsupportedOperationException {
        return item.addItemProperty(pid, property);
    }

    public boolean removeItemProperty(Object pid)
            throws UnsupportedOperationException {
        return item.removeItemProperty(pid);
    }

    public void addListener(PropertySetChangeListener listener) {
        if (item instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) item).addListener(listener);
        }
    }// addListener

    public void removeListener(PropertySetChangeListener listener) {
        if (item instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) item).removeListener(listener);
        }
    }// removeListener

}// ItemFormatter