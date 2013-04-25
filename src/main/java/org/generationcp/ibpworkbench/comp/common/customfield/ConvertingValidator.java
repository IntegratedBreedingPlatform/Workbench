package org.generationcp.ibpworkbench.comp.common.customfield;

import com.vaadin.data.Validator;

/**
 * Validator wrapper that uses a {@link PropertyConverter} before calling
 * another validator. This enables using a standard Vaadin validator on a field
 * even when the field value is converted between the field and the underlying
 * property.
 * 
 * @param <PROPERTY_DATA_TYPE>
 *            value data type for the field
 * @param <FIELD_DATA_TYPE>
 *            value data type for the underlying property
 */
public class ConvertingValidator<PROPERTY_DATA_TYPE, FIELD_DATA_TYPE>
        implements Validator {

    private final Validator validator;
    private final PropertyConverter<PROPERTY_DATA_TYPE, FIELD_DATA_TYPE> converter;

    public ConvertingValidator(Validator validator,
            PropertyConverter<PROPERTY_DATA_TYPE, FIELD_DATA_TYPE> converter) {
        this.validator = validator;
        this.converter = converter;
    }

    public void validate(Object value) throws InvalidValueException {
        validator.validate(converter.parse((FIELD_DATA_TYPE) value));
    }

    public boolean isValid(Object value) {
        return validator.isValid(converter.parse((FIELD_DATA_TYPE) value));
    }

    /**
     * Returns the underlying (wrapped) validator.
     * 
     * @return Validator
     */
    public Validator getValidator() {
        return validator;
    }

}
