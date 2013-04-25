package org.generationcp.ibpworkbench.comp.common.customfield.beanfield;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.generationcp.ibpworkbench.comp.common.customfield.PropertyConverter;

import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 * Converter that maps between a set of item identifiers (the value of a
 * multi-selection field) and a set of bean instances in an underlying property.
 * The mapping is performed using a container.
 * 
 * Supports containers that use either BeanItem or JPAContainerItem.
 * 
 * @see BeanFieldPropertyConverter
 * 
 * @author Henri Sara
 * 
 * @param <BEAN_TYPE>
 * @param <ID_TYPE>
 */
public class BeanSetFieldPropertyConverter<BEAN_TYPE, ID_TYPE> extends
        PropertyConverter<Collection, Collection<ID_TYPE>> {

    private final Container container;
    private final Object idPropertyId;
    private final Class<? extends BEAN_TYPE> beanType;

    public BeanSetFieldPropertyConverter(Class<BEAN_TYPE> beanType,
            Container container, Object idPropertyId) {
        super(Collection.class);

        this.container = container;
        this.idPropertyId = idPropertyId;
        this.beanType = beanType;
    }

    /**
     * Convert a bean instance to its identifier.
     */
    @Override
    public Collection<ID_TYPE> format(Collection values) {
        if (values != null) {
            Set<ID_TYPE> ids = new HashSet<ID_TYPE>();
            for (Object value : values) {
                if (value != null
                        && beanType.isAssignableFrom(value.getClass())) {
                    ID_TYPE id = BeanFieldPropertyConverter.getIdForBean(
                            (BEAN_TYPE) value, idPropertyId);
                    ids.add(id);
                }
            }
            return ids;
        } else {
            return null;
        }
    }

    /**
     * Return the bean instance for an identifier.
     */
    @Override
    public Collection<BEAN_TYPE> parse(Collection<ID_TYPE> fieldValue)
            throws ConversionException {
        Set<BEAN_TYPE> beans = new HashSet<BEAN_TYPE>();
        for (Object id : fieldValue) {
            Item item = container.getItem(id);
            if (item != null) {
                BEAN_TYPE bean = BeanFieldPropertyConverter
                        .getBeanForItem(item);
                if (bean != null && beanType.isAssignableFrom(bean.getClass())) {
                    beans.add(bean);
                }
            }
        }
        return beans;
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        if (getPropertyDataSource() == null) {
            return;
        }
        try {
            // null is just an ordinary value
            Collection<ID_TYPE> newIdCollection = (Collection<ID_TYPE>) newValue;
            Collection<BEAN_TYPE> oldValue = (Collection<BEAN_TYPE>) getPropertyDataSource()
                    .getValue();
            Collection<BEAN_TYPE> convertedValue;
            if (null == oldValue) {
                convertedValue = parse(newIdCollection);
                getPropertyDataSource().setValue(convertedValue);
            } else {
                // directly update old collection instead of creating a new
                // collection

                Set<ID_TYPE> oldIds = new HashSet<ID_TYPE>();
                Set<ID_TYPE> toRemoveIds = new HashSet<ID_TYPE>();
                Set<BEAN_TYPE> toAdd = new HashSet<BEAN_TYPE>();

                // remove old values from old collection
                for (BEAN_TYPE bean : oldValue) {
                    ID_TYPE idForBean = BeanFieldPropertyConverter
                            .getIdForBean(bean, idPropertyId);
                    if (!newIdCollection.contains(idForBean)) {
                        toRemoveIds.add(idForBean);
                    } else {
                        oldIds.add(idForBean);
                    }
                }
                // add new values to collection
                for (Object id : newIdCollection) {
                    if (oldIds.contains(id)) {
                        continue;
                    }
                    Item item = container.getItem(id);
                    if (item != null) {
                        BEAN_TYPE bean = BeanFieldPropertyConverter
                                .getBeanForItem(item);
                        if (bean != null
                                && beanType.isAssignableFrom(bean.getClass())) {
                            toAdd.add(bean);
                        }
                    }
                }

                convertedValue = oldValue;

                // seems to be needed by EclipseLink etc. as refresh - otherwise
                // ConcurrentModificationException
                getPropertyDataSource().setValue(convertedValue);

                Iterator<BEAN_TYPE> it = convertedValue.iterator();
                while (it.hasNext()) {
                    BEAN_TYPE bean = it.next();
                    ID_TYPE id = BeanFieldPropertyConverter.getIdForBean(bean,
                            idPropertyId);
                    if (toRemoveIds.contains(id)) {
                        it.remove();
                    }
                }
                convertedValue.addAll(toAdd);
                getPropertyDataSource().setValue(convertedValue);
            }

            if (convertedValue == null ? getValue() != null : !convertedValue
                    .equals(getValue())) {
                fireValueChange();
            }
        } catch (Exception e) {
            if (e instanceof ConversionException) {
                throw (ConversionException) e;
            } else {
                throw new ConversionException(e);
            }
        }
    }

}
