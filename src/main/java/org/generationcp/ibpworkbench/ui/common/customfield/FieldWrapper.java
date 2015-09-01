
package org.generationcp.ibpworkbench.ui.common.customfield;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ReadOnlyStatusChangeNotifier;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.CompositeErrorMessage;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;

/**
 * A component implementing {@link Field} allows customizing layout while wrapping another field and optionally a {@link PropertyConverter}.
 * Most of the logic is delegated to the wrapped {@link Field}.
 *
 * Subclasses must set the layout of the field with {@link #setCompositionRoot(Component)}, and can override methods to customize value
 * conversions etc.
 *
 * The default property converter calls the methods {@link #format(Object)} and {@link #parse(Object)}, which can be overridden to customize
 * conversions. Alternatively, a custom property converter can be given.
 *
 * Methods of this class can be overridden to customize other functionality.
 *
 * See also {@link CustomField}.
 *
 * @author Henri Sara
 */
public abstract class FieldWrapper<PC> extends CustomComponent implements Field, ReadOnlyStatusChangeNotifier, ReadOnlyStatusChangeListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -2543298035770705098L;

	/**
	 * Property converter that delegates conversions back to the containing class instance.
	 */
	protected class DefaultPropertyConverter extends PropertyConverter<PC, Object> {

		/**
		 *
		 */
		private static final long serialVersionUID = -8373627223952094679L;

		public DefaultPropertyConverter(Class<? extends PC> propertyClass) {
			super(propertyClass);
		}

		@Override
		public Object format(PC value) {
			return FieldWrapper.this.format(value);
		}

		@Override
		public PC parse(Object formattedValue) throws ConversionException {
			return FieldWrapper.this.parse(formattedValue);
		}
	}

	/**
	 * The {@link Field} to which most functionality is delegated.
	 */
	private Field wrappedField;

	/**
	 * Property value converter or null if none is used.
	 */
	private PropertyConverter<PC, ? extends Object> converter;

	/**
	 * The property used, either a {@link PropertyConverter} or the wrapped field.
	 */
	private Property property;

	/**
	 * Type of the data for the underlying property.
	 */
	private Class<? extends PC> propertyType;

	/**
	 * The tab order number of this field.
	 */
	private int tabIndex = 0;

	/**
	 * Create a custom field wrapping a {@link Field}.
	 * 
	 * Subclass constructors calling this constructor must create and set the layout.
	 * 
	 * When this constructor is used, value conversions are delegated to the methods {@link #format(PC)} and {@link #parse(Object)}.
	 * 
	 * @param wrappedField
	 * @param propertyType
	 */
	protected FieldWrapper(Field wrappedField, Class<? extends PC> propertyType) {
		this.wrappedField = wrappedField;
		this.propertyType = propertyType;
		this.converter = new DefaultPropertyConverter(propertyType);
		this.converter.setPropertyDataSource(wrappedField.getPropertyDataSource());
		wrappedField.setPropertyDataSource(this.converter);
		this.property = this.converter;
	}

	/**
	 * Create a custom field wrapping a {@link Field}.
	 * 
	 * Subclass constructors calling this constructor must create and set the layout.
	 * 
	 * When this constructor is used, value conversions are delegated to the methods {@link #format(PC)} and {@link #parse(Object)}.
	 * 
	 * @param wrappedField
	 * @param propertyType
	 * @param layout composition root layout, which already contains the wrapped field
	 */
	protected FieldWrapper(Field wrappedField, Class<? extends PC> propertyType, ComponentContainer layout) {
		this(wrappedField, propertyType);
		this.setCompositionRoot(layout);
	}

	/**
	 * Create a custom field wrapping a {@link Field} with a user-defined {@link PropertyConverter}.
	 * 
	 * Subclass constructors calling this constructor must create and set the layout.
	 * 
	 * When this constructor is used, the methods {@link #format(PC)} and {@link #parse(Object)} are never called.
	 * 
	 * @param wrappedField
	 * @param propertyConverter or null to bypass the use of a property converter
	 * @param propertyType
	 */
	protected FieldWrapper(Field wrappedField, PropertyConverter<PC, ? extends Object> converter, Class<? extends PC> propertyType) {
		this.wrappedField = wrappedField;
		this.converter = converter;
		this.propertyType = propertyType;
		if (converter != null) {
			converter.setPropertyDataSource(wrappedField.getPropertyDataSource());
			wrappedField.setPropertyDataSource(converter);
			this.property = converter;
		} else {
			this.property = wrappedField;
		}
	}

	/**
	 * Create a custom field wrapping a {@link Field} with a user-defined {@link PropertyConverter}.
	 * 
	 * Subclass constructors calling this constructor must create and set the layout.
	 * 
	 * When this constructor is used, the methods {@link #format(PC)} and {@link #parse(Object)} are never called.
	 * 
	 * @param wrappedField
	 * @param propertyConverter or null to bypass the use of a property converter
	 * @param propertyType
	 * @param layout composition root layout, which already contains the wrapped field
	 */
	protected FieldWrapper(Field wrappedField, PropertyConverter<PC, ? extends Object> converter, Class<? extends PC> propertyType,
			ComponentContainer layout) {
		this(wrappedField, converter, propertyType);
		this.setCompositionRoot(layout);
	}

	/**
	 * Returns the wrapped field to which operations are delegated.
	 * 
	 * @return
	 */
	protected Field getWrappedField() {
		return this.wrappedField;
	}

	/**
	 * Returns the property converter performing value conversions etc.
	 * 
	 * By default, if no property converter is given, a {@link DefaultPropertyConverter} is created, but the user can explicitly specify
	 * null as the converter when calling the constructor.
	 * 
	 * @return property converter or null if none
	 */
	protected PropertyConverter<PC, ? extends Object> getConverter() {
		return this.converter;
	}

	/**
	 * Returns the property to which operations are delegated first if it supports them. Currently, this is either the
	 * {@link PropertyConverter} used or the wrapped {@link Field}.
	 * 
	 * This method is for internal use only.
	 * 
	 * @return property, not null
	 */
	protected Property getProperty() {
		return this.property;
	}

	/**
	 * Convert an underlying property value to a field value to display.
	 * 
	 * The default conversion uses toString(), override or specify another converter to modify behavior.
	 */
	protected Object format(PC value) {
		return value != null ? value.toString() : null;
	}

	/**
	 * Convert a field value to an underlying property value.
	 * 
	 * The default is no conversion, override or specify another converter to modify behavior.
	 */
	protected PC parse(Object formattedValue) throws ConversionException {
		return (PC) formattedValue;
	}

	@Override
	public Class<? extends PC> getType() {
		return this.propertyType;
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {

		// The tab ordering number
		if (this.tabIndex != 0) {
			target.addAttribute("tabindex", this.tabIndex);
		}

		// If the field is modified, but not committed, set modified attribute
		if (this.isModified()) {
			target.addAttribute("modified", true);
		}

		// Adds the required attribute
		if (!this.isReadOnly() && this.isRequired()) {
			target.addAttribute("required", true);
		}

		// Hide the error indicator if needed
		if (this.isRequired() && this.isEmpty() && this.getComponentError() == null && this.getErrorMessage() != null) {
			target.addAttribute("hideErrors", true);
		}
		super.paintContent(target);
	}

	/**
	 * Is the field empty?
	 * 
	 * In general, "empty" state is same as null. If the wrapped field is an {@link AbstractSelect} in multiselect mode, also an empty
	 * {@link Collection} is considered to be empty.
	 * 
	 * Override if custom functionality is needed. This method should always return "true" for null values.
	 */
	protected boolean isEmpty() {
		// getValue() also handles read-through mode
		Object value = this.getValue();
		return value == null || this.wrappedField instanceof AbstractSelect && ((AbstractSelect) this.wrappedField).isMultiSelect()
				&& value instanceof Collection && ((Collection) value).isEmpty();
	}

	@Override
	public void focus() {
		super.focus();
	}

	@Override
	public boolean isInvalidCommitted() {
		return this.wrappedField.isInvalidCommitted();
	}

	@Override
	public void setInvalidCommitted(boolean isCommitted) {
		this.wrappedField.setInvalidCommitted(isCommitted);
	}

	@Override
	public void commit() throws SourceException, InvalidValueException {
		this.wrappedField.commit();
	}

	@Override
	public void discard() throws SourceException {
		this.wrappedField.discard();
	}

	@Override
	public boolean isWriteThrough() {
		return this.wrappedField.isWriteThrough();
	}

	@Override
	public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException {
		this.wrappedField.setWriteThrough(writeThrough);
	}

	@Override
	public boolean isReadThrough() {
		return this.wrappedField.isReadThrough();
	}

	@Override
	public void setReadThrough(boolean readThrough) throws SourceException {
		this.wrappedField.setReadThrough(readThrough);
	}

	@Override
	public boolean isModified() {
		return this.wrappedField.isModified();
	}

	@Override
	public void addValidator(Validator validator) {
		if (this.property instanceof Validatable) {
			((Validatable) this.property).addValidator(validator);
			this.requestRepaint();
		} else {
			this.wrappedField.addValidator(validator);
		}
	}

	@Override
	public void removeValidator(Validator validator) {
		if (this.property instanceof Validatable) {
			((Validatable) this.property).removeValidator(validator);
			this.requestRepaint();
		} else {
			this.wrappedField.removeValidator(validator);
		}
	}

	@Override
	public Collection<Validator> getValidators() {
		if (this.property instanceof Validatable) {
			return ((Validatable) this.property).getValidators();
		} else {
			return this.wrappedField.getValidators();
		}
	}

	@Override
	public boolean isValid() {
		if (this.property instanceof Validatable) {
			if (this.isEmpty()) {
				return !this.isRequired();
			}

			if (this.converter != null) {
				return this.converter.isValid(this.getValue());
			} else {
				return ((Validatable) this.getProperty()).isValid();
			}
		} else {
			return this.wrappedField.isValid();
		}
	}

	@Override
	public void validate() throws InvalidValueException {
		if (this.property instanceof Validatable) {
			if (this.isEmpty()) {
				if (this.isRequired()) {
					throw new Validator.EmptyValueException(this.getRequiredError());
				} else {
					return;
				}
			}

			if (this.converter != null) {
				this.converter.validate(this.getValue());
			} else {
				((Validatable) this.property).validate();
			}
		} else {
			this.wrappedField.validate();
		}
	}

	/**
	 * Returns the error message of the component, the wrapped field and the validation of this field (if it has a converter or other custom
	 * property). The error messages are combined if necessary.
	 * 
	 * Note that the method {@link #validate()} of this component is not called if there is no custom property/converter. This is to avoid
	 * duplicate error messages - override this method to change the behavior if necessary.
	 * 
	 * Note also that {@link AbstractComponent#setComponentError()} is not overridden, and setting the error message for this component does
	 * not affect the error message of the wrapped field. Override the setComponentError() method to modify this behavior.
	 * 
	 * If overriding this method, see {@link #getAbstractComponentErrorMessage()}, {@link #getValidationError()} and
	 * {@link #combineErrorMessages(ErrorMessage[])}.
	 */
	@Override
	public ErrorMessage getErrorMessage() {
		ErrorMessage superError = this.getAbstractComponentErrorMessage();

		// this is needed to get buffered source exceptions
		ErrorMessage fieldError = null;
		if (this.wrappedField instanceof AbstractComponent) {
			fieldError = ((AbstractComponent) this.wrappedField).getErrorMessage();
		}

		// should do this always, but that could lead to duplicate errors with
		// wrappedField
		ErrorMessage validationError = null;
		if (this.property instanceof Validatable && this.property != this.wrappedField) {
			validationError = this.getValidationError();
		}

		return this.combineErrorMessages(new ErrorMessage[] {superError, validationError, fieldError});
	}

	/**
	 * Returns the error message of this component, without taking the wrapped field into account. This is sometimes needed when overriding
	 * the behavior of {@link #getErrorMessage()}.
	 * 
	 * @return error message of this component, ignoring the wrapped field
	 */
	protected ErrorMessage getAbstractComponentErrorMessage() {
		return super.getErrorMessage();
	}

	/**
	 * Perform validation of the field and return the validation error found, if any.
	 * 
	 * @return
	 */
	protected ErrorMessage getValidationError() {
		try {
			this.validate();
		} catch (Validator.InvalidValueException e) {
			if (!e.isInvisible()) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Combine multiple {@link ErrorMessage} instances into a single message, using {@link CompositeErrorMessage} if necessary.
	 * 
	 * Any input {@link CompositeErrorMessage} instances are flattened and null messages filtered out, and empty input results in the return
	 * value null.
	 * 
	 * @param errorMessages non-null array of error messages (may contain null)
	 * @return
	 */
	protected ErrorMessage combineErrorMessages(ErrorMessage[] errorMessages) {
		// combine error messages from all sources
		List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

		if (errorMessages.length == 1 && errorMessages[0] != null) {
			return errorMessages[0];
		}

		for (ErrorMessage errorMessage : errorMessages) {
			if (errorMessage instanceof CompositeErrorMessage) {
				// flatten the hierarchy of composite errors
				Iterator<ErrorMessage> it = ((CompositeErrorMessage) errorMessage).iterator();
				while (it.hasNext()) {
					// never null for CompositeErrorMessage
					errors.add(it.next());
				}
			} else if (errorMessage != null) {
				errors.add(errorMessage);
			}
		}

		if (errors.isEmpty()) {
			return null;
		} else if (errors.size() == 1) {
			return errors.get(0);
		} else {
			return new CompositeErrorMessage(errors);
		}
	}

	@Override
	public boolean isInvalidAllowed() {
		if (this.property instanceof Validatable) {
			return ((Validatable) this.property).isInvalidAllowed();
		} else {
			return this.wrappedField.isInvalidAllowed();
		}
	}

	@Override
	public void setInvalidAllowed(boolean invalidValueAllowed) throws UnsupportedOperationException {
		if (this.property instanceof Validatable) {
			((Validatable) this.property).setInvalidAllowed(invalidValueAllowed);
		} else {
			this.wrappedField.setInvalidAllowed(invalidValueAllowed);
		}
	}

	@Override
	public PC getValue() {
		if (!this.isReadThrough() || this.isModified()) {
			// return internal value (converted)
			Object internalValue = this.getWrappedField().getValue();
			if (this.converter != null) {
				return ((PropertyConverter<PC, Object>) this.converter).parse(internalValue);
			} else if (internalValue != null && this.getType().isAssignableFrom(internalValue.getClass())) {
				return (PC) internalValue;
			} else {
				return null;
			}
		} else {
			// return property value
			if (this.converter != null) {
				return (PC) this.converter.getPropertyDataSource().getValue();
			} else {
				return (PC) this.property.getValue();
			}
		}
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
		if (this.converter != null) {
			this.converter.getPropertyDataSource().setValue(newValue);
		} else {
			this.property.setValue(newValue);
		}
	}

	@Override
	public void addListener(ValueChangeListener listener) {
		// if possible, listener for the original datasource values, not the
		// wrapped field converted values
		if (this.converter != null && this.converter.getPropertyDataSource() instanceof Property.ValueChangeNotifier) {
			((Property.ValueChangeNotifier) this.converter.getPropertyDataSource()).addListener(listener);
		} else if (this.property instanceof Property.ValueChangeNotifier) {
			((Property.ValueChangeNotifier) this.property).addListener(listener);
		} else {
			this.wrappedField.addListener(listener);
		}
	}

	@Override
	public void removeListener(ValueChangeListener listener) {
		// see addListener()
		if (this.converter != null && this.converter.getPropertyDataSource() instanceof Property.ValueChangeNotifier) {
			((Property.ValueChangeNotifier) this.converter.getPropertyDataSource()).removeListener(listener);
		} else if (this.property instanceof Property.ValueChangeNotifier) {
			((Property.ValueChangeNotifier) this.property).removeListener(listener);
		} else {
			this.wrappedField.removeListener(listener);
		}
	}

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		// this should also work if property is a PropertyConverter
		if (this.property instanceof Property.ValueChangeListener) {
			((Property.ValueChangeListener) this.property).valueChange(event);
		} else {
			this.wrappedField.valueChange(event);
		}
	}

	@Override
	public void addListener(Property.ReadOnlyStatusChangeListener listener) {
		if (this.property instanceof ReadOnlyStatusChangeNotifier) {
			((ReadOnlyStatusChangeNotifier) this.property).addListener(listener);
		}
	}

	@Override
	public void removeListener(Property.ReadOnlyStatusChangeListener listener) {
		if (this.property instanceof ReadOnlyStatusChangeNotifier) {
			((ReadOnlyStatusChangeNotifier) this.property).removeListener(listener);
		}
	}

	@Override
	public void readOnlyStatusChange(Property.ReadOnlyStatusChangeEvent event) {
		if (this.property instanceof ReadOnlyStatusChangeListener) {
			((ReadOnlyStatusChangeListener) this.property).readOnlyStatusChange(event);
		}
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		if (this.converter != null) {
			// note that assuming property == converter in this case
			this.converter.setPropertyDataSource(newDataSource);
		} else {
			this.wrappedField.setPropertyDataSource(newDataSource);
		}
	}

	/**
	 * Returns the property data source for the field.
	 * 
	 * Note that this method for {@link FieldWrapper} always returns the property converter, or the property data source of the wrapped
	 * field if there is no converter.
	 */
	@Override
	public Property getPropertyDataSource() {
		if (this.converter != null) {
			return this.converter;
		} else {
			return this.wrappedField.getPropertyDataSource();
		}
	}

	@Override
	public int getTabIndex() {
		return this.tabIndex;
	}

	@Override
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;

	}

	@Override
	public boolean isRequired() {
		if (this.property instanceof Field) {
			return ((Field) this.property).isRequired();
		} else {
			return this.wrappedField.isRequired();
		}
	}

	@Override
	public void setRequired(boolean required) {
		if (this.property instanceof Field) {
			((Field) this.property).setRequired(required);
		} else {
			this.wrappedField.setRequired(required);
		}
	}

	@Override
	public void setRequiredError(String requiredMessage) {
		if (this.property instanceof Field) {
			((Field) this.property).setRequiredError(requiredMessage);
		} else {
			this.wrappedField.setRequiredError(requiredMessage);
		}
	}

	@Override
	public String getRequiredError() {
		if (this.property instanceof Field) {
			return ((Field) this.property).getRequiredError();
		} else {
			return this.wrappedField.getRequiredError();
		}
	}

	@Override
	public boolean isReadOnly() {
		return super.isReadOnly() || this.property.isReadOnly();
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		this.property.setReadOnly(readOnly);
		super.setReadOnly(readOnly);
	}

	@Override
	public void setImmediate(boolean immediate) {
		super.setImmediate(immediate);
		if (this.wrappedField instanceof AbstractComponent) {
			((AbstractComponent) this.wrappedField).setImmediate(immediate);
		}
	}
}
