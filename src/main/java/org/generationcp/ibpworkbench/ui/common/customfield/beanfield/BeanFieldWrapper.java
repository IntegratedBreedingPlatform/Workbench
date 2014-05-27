package org.generationcp.ibpworkbench.ui.common.customfield.beanfield;

import com.vaadin.data.Container;
import com.vaadin.ui.Field;
import org.generationcp.ibpworkbench.ui.common.customfield.FieldWrapper;

/**
 * A field that wraps another single-select field (typically a combo box) and
 * uses a container of BeanItems or JPAContainer EntityItems to convert between
 * its values (identifier of a bean) and bean instances in the underlying
 * property.
 * 
 * This makes it easier to use containers such as {@code JPAContainer} for
 * relationships on forms when the field in the underlying master entity field
 * points to an entity instance, not an id.
 * 
 * Usage:
 * 
 * <pre>
 * ComboBox select = new ComboBox(&quot;manager&quot;, personContainer);
 * select.setItemCaptionPropertyId(&quot;firstName&quot;);
 * field = new BeanFieldWrapper&lt;Person&gt;(select, Person.class, personContainer,
 *         &quot;id&quot;);
 * </pre>
 * 
 * @param <E>
 */
public class BeanFieldWrapper<E> extends FieldWrapper<E> {

    public BeanFieldWrapper(Field wrappedField, Class<E> beanType,
            Container container, Object idPropertyId) {
        super(wrappedField, new BeanFieldPropertyConverter<E, Object>(beanType,
                container, idPropertyId), beanType);

        setCompositionRoot(wrappedField);

        setImmediate(true);
    }

}
