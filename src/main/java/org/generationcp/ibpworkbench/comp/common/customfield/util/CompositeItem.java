package org.generationcp.ibpworkbench.comp.common.customfield.util;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

import java.util.*;

/**
 * Simple flat {@link Item}'s composite.
 *
 * @author Andrew Fink [aprpda@gmail.com] http://magicprinc.blogspot.com
 * @see ItemFormatter
 */
public class CompositeItem implements Item, Item.PropertySetChangeNotifier {
	private final Collection<? extends Item> items;


	public CompositeItem (Collection<? extends Item> items) {
		this(items, false);
	}//new


	public CompositeItem (Collection<? extends Item> items, boolean detectIntersection) throws IllegalArgumentException {
		this.items = items;

		if (detectIntersection) {
			Collection<?> list = getItemPropertyIds();
			HashSet<?> set = new HashSet<Object>(list);
			if (list.size() != set.size()) {
				throw new IllegalArgumentException("Intersection detected! Total IDs: "+list.size()
						+", unique IDs: "+set.size() +", delta "+ (list.size() - set.size()));
			}
		}
	}//new


	public CompositeItem (Item... items) {
		this(false, items);
	}//new


	public CompositeItem (boolean detectIntersection, Item... items) {
		this(Arrays.asList(items), detectIntersection);
	}//new


	/** Get first {@link Property} by pid */
	public Property getItemProperty (Object pid) {
		for (Item i : items) {
			Property p = i.getItemProperty(pid);
			if (p != null) {
				return p;
			}
		}
		return null;//not found
	}//getItemProperty


	public Collection<?> getItemPropertyIds () {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Item i : items) {
			list.addAll(i.getItemPropertyIds());
		}
		return Collections.unmodifiableList(list);
	}//getItemPropertyIds


	public boolean addItemProperty (Object id, Property property) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("CompositeItem.addItemProperty is unsupported: " + id + ", " + property);
	}

	public boolean removeItemProperty (Object id) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("CompositeItem.removeItemProperty is unsupported: " + id);
	}


	public void addListener (PropertySetChangeListener listener) {
		for (Item i : items) {
			if (i instanceof PropertySetChangeNotifier) {
				((PropertySetChangeNotifier) i).addListener(listener);
			}
		}
	}//addListener


	public void removeListener (PropertySetChangeListener listener) {
		for (Item i : items) {
			if (i instanceof PropertySetChangeNotifier) {
				((PropertySetChangeNotifier) i).removeListener(listener);
			}
		}
	}

}//CompositeItem