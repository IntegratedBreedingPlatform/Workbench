package org.generationcp.ibpworkbench.ui.common.customfield.beanfield;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.generationcp.ibpworkbench.ui.common.customfield.PropertyConverter;


import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

/**
 * Converter that maps between an item identifier (a selection field value) and
 * a bean instance in an underlying property using a container.
 * 
 * Supports containers that use either BeanItem or JPAContainerItem.
 * 
 * @see BeanSetFieldPropertyConverter
 * 
 * @author Henri Sara
 * 
 * @param <BEAN_TYPE>
 * @param <ID_TYPE>
 */
public class BeanFieldPropertyConverter<BEAN_TYPE, ID_TYPE> extends
        PropertyConverter<BEAN_TYPE, ID_TYPE> {

    private static final String JPA_CONTAINER_ITEM_CLASS = "com.vaadin.addon.jpacontainer.JPAContainerItem";
    private static final String JPA_ITEM_GET_ENTITY_METHOD = "getEntity";

    private final Container container;
    private final Object idPropertyId;

    public BeanFieldPropertyConverter(Class<BEAN_TYPE> beanType,
            Container container, Object idPropertyId) {
        super(beanType);

        this.container = container;
        this.idPropertyId = idPropertyId;
    }

    /**
     * Convert a bean instance to its identifier.
     */
    @Override
    public ID_TYPE format(BEAN_TYPE propertyValue) {
        if (propertyValue != null) {
            // reflection to avoid dependencies
            return getIdForBean(propertyValue, idPropertyId);
        } else {
            return null;
        }
    }

    /**
     * Return the bean instance for an identifier.
     */
    @Override
    public BEAN_TYPE parse(Object fieldValue) throws ConversionException {
        Item item = container.getItem(fieldValue);
        return getBeanForItem(item);
    }

    /**
     * Extract the bean instance from an item. The item can be a
     * {@link BeanItem} or a {@code JPAContainerItem}, and reflection is used to
     * avoid explicit dependencies to {@code JPAContainer}.
     * 
     * @param item
     *            a {@link BeanItem} or a {@code JPAContainerItem}
     * @return the bean corresponding to the item
     */
    static <BEAN_TYPE> BEAN_TYPE getBeanForItem(Item item) {
        if (item instanceof BeanItem) {
            // assume the bean type is E
            return ((BeanItem<BEAN_TYPE>) item).getBean();
        } else if (item != null
                && JPA_CONTAINER_ITEM_CLASS.equals(item.getClass().getName())) {
            // use reflection to avoid a JPAContainer dependency
            return callGetter(item, JPA_ITEM_GET_ENTITY_METHOD);
        } else {
            return null;
        }
    }

    /**
     * Extracts the bean identifier for an item by calling a getter with
     * reflection.
     * 
     * @param value
     * @param idPropertyId
     *            property identifier for the property containing the bean
     *            identifier
     * @return
     */
    static <BEAN_TYPE, ID_TYPE> ID_TYPE getIdForBean(BEAN_TYPE value,
            Object idPropertyId) {
        ID_TYPE id = callGetter(value, "get"
                + idPropertyId.toString().substring(0, 1).toUpperCase()
                + idPropertyId.toString().substring(1));
        return id;
    }

    // helper method
    private static <T> T callGetter(Object obj, String methodName)
            throws ConversionException {
        try {
            Method method = obj.getClass().getMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(obj);
        } catch (SecurityException e) {
            throw new ConversionException(e);
        } catch (NoSuchMethodException e) {
            throw new ConversionException(e);
        } catch (IllegalArgumentException e) {
            throw new ConversionException(e);
        } catch (IllegalAccessException e) {
            throw new ConversionException(e);
        } catch (InvocationTargetException e) {
            throw new ConversionException(e);
        }
    }

}
