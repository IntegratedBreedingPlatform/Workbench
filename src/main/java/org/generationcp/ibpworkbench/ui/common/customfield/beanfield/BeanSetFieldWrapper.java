package org.generationcp.ibpworkbench.ui.common.customfield.beanfield;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;
import org.generationcp.ibpworkbench.ui.common.customfield.FieldWrapper;

import java.util.Collection;

/**
 * A field that wraps another multi-select field (typically a TwinColSelect) and
 * uses a container using {@link BeanItem}s or a JPAContainer to convert between
 * its values (set of identifiers) and a set of bean instances in the underlying
 * property.
 * 
 * This makes it easier to use {code JPAContainer} e.g. for relationships on
 * forms when the field in the underlying master entity field points to a set of
 * entity instances, not IDs.
 * 
 * Usage:
 * 
 * <pre>
 * TwinColSelect select = new TwinColSelect(&quot;Team members&quot;, personContainer);
 * select.setMultiSelect(true);
 * select.setItemCaptionPropertyId(&quot;firstName&quot;);
 * field = new BeanSetFieldWrapper&lt;Person&gt;(select, Person.class, personContainer,
 *         &quot;id&quot;);
 * </pre>
 * 
 * @param <E>
 */
public class BeanSetFieldWrapper<E> extends FieldWrapper<Collection> {

    public BeanSetFieldWrapper(Field wrappedField, Class<E> beanType,
            Container container, Object idPropertyId) {
        super(wrappedField, new BeanSetFieldPropertyConverter<E, Object>(
                beanType, container, idPropertyId), Collection.class);

        setCompositionRoot(wrappedField);

        setImmediate(true);
    }

}
