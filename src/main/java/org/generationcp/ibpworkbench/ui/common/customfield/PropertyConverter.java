
package org.generationcp.ibpworkbench.ui.common.customfield;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;

/**
 * Value conversion proxy for a property.
 *
 * This class implements {@link Property} and some other interfaces, and forwards calls to another property while performing value
 * conversions.
 *
 * PC if the property type, FC is the field internal value type. All values returned by the data source must be compatible with PC, and
 * values returned by the field with FC.
 */
@SuppressWarnings("serial")
public abstract class PropertyConverter<PC, FC> implements Property, Property.ValueChangeNotifier, Property.ValueChangeListener,
		Property.ReadOnlyStatusChangeListener, Property.ReadOnlyStatusChangeNotifier, Property.Viewer, Validatable {

	/**
	 *
	 */
	private static final long serialVersionUID = -168194669449393837L;

	/** The list of validators. Def null */
	private List<Validator> validators;

	/** Are invalid values allowed in fields ? */
	private boolean invalidAllowed = true;

	/** Internal list of registered value change listeners. */
	private final List<ValueChangeListener> valueChangeListeners = new LinkedList<ValueChangeListener>();

	/** Internal list of registered read-only status change listeners. */
	private final List<ReadOnlyStatusChangeListener> readOnlyStatusChangeListeners =
			new LinkedList<ReadOnlyStatusChangeListener>();

	/** Datasource that stores the actual value. */
	private Property dataSource;

	private final Class<? extends PC> propertyClass;

	/**
	 * Construct a new {@code PropertyConverter} that is not connected to any data source. Call {@link #setPropertyDataSource(Property)}
	 * later on to attach it to a property.
	 */
	protected PropertyConverter(Class<? extends PC> propertyClass) {
		this.propertyClass = propertyClass;
	}// new

	/**
	 * Construct a new formatter that is connected to given data source. Calls {@link #format(Object)} which can be a problem if the
	 * formatter has not yet been initialized.
	 * 
	 * @param propertyDataSource to connect this property to.
	 */
	public PropertyConverter(Property propertyDataSource) {
		this.setPropertyDataSource(propertyDataSource);
		this.propertyClass = (Class<? extends PC>) propertyDataSource.getType();
	}// new

	/**
	 * Gets the current data source of the formatter, if any.
	 * 
	 * @return the current data source as a Property, or <code>null</code> if none defined.
	 */
	@Override
	public Property getPropertyDataSource() {
		return this.dataSource;
	}

	/**
	 * Sets the specified Property as the data source for the formatter.
	 * 
	 * <p>
	 * Remember that new data sources getValue() must return objects that are compatible with parse() and format() methods.
	 * </p>
	 * 
	 * @param newDataSource the new data source Property.
	 */
	@Override
	public void setPropertyDataSource(Property newDataSource) {

		boolean readOnly = false;
		String prevValue = null;

		if (this.dataSource != null) {
			if (this.dataSource instanceof Property.ValueChangeNotifier) {
				((Property.ValueChangeNotifier) this.dataSource).removeListener(this);
			}
			if (this.dataSource instanceof Property.ReadOnlyStatusChangeNotifier) {
				((Property.ReadOnlyStatusChangeNotifier) this.dataSource).removeListener(this);
			}
			readOnly = this.isReadOnly();
			prevValue = this.toString();
		}

		this.dataSource = newDataSource;

		if (this.dataSource != null) {
			if (this.dataSource instanceof Property.ValueChangeNotifier) {
				((Property.ValueChangeNotifier) this.dataSource).addListener(this);
			}
			if (this.dataSource instanceof Property.ReadOnlyStatusChangeNotifier) {
				((Property.ReadOnlyStatusChangeNotifier) this.dataSource).addListener(this);
			}
		}

		if (this.isReadOnly() != readOnly) {
			this.fireReadOnlyStatusChange();
		}
		String newVal = this.toString();
		if (prevValue == null && newVal != null || prevValue != null && !prevValue.equals(newVal)) {
			this.fireValueChange();
		}
	}// setPropertyDataSource

	@Override
	public Class<? extends PC> getType() {
		return this.propertyClass;
	}

	/**
	 * Get the formatted value.
	 * 
	 * @return If the datasource returns null, this is null. Otherwise this is given by format().
	 */
	@Override
	public Object getValue() {
		if (this.dataSource == null) {
			return null;
		}
		return this.format((PC) this.dataSource.getValue());
	}

	/**
	 * Get the formatted value.
	 * 
	 * @return If the datasource returns null, this is null. Otherwise this is a String based on the result of format().
	 */
	@Override
	public String toString() {
		PC value = this.dataSource == null ? null : (PC) this.dataSource.getValue();
		if (value == null) {
			return null;
		}
		FC formattedValue = this.format(value);
		return String.valueOf(formattedValue);
	}

	/**
	 * Reflects the read-only status of the datasource. If there is no data source, returns false.
	 */
	@Override
	public boolean isReadOnly() {
		return this.dataSource != null && this.dataSource.isReadOnly();
	}

	/**
	 * This method must be implemented to format the values received from a data source for use in a field.
	 * 
	 * @see #parse(Object)
	 * 
	 * @param propertyValue Value object from the datasource. This is null or of a type compatible with getType() of the datasource.
	 * @return
	 */
	public abstract FC format(PC propertyValue);

	/**
	 * Parse a value from a field and convert it to format compatible with datasource.
	 * 
	 * The method is required to assure that parse(format(x)) equals x.
	 * 
	 * @param fieldValue field value to convert
	 * @return value compatible with datasource
	 * @throws ConversionException
	 */
	public abstract PC parse(FC fieldValue) throws ConversionException;

	/**
	 * Sets the Property's read-only mode to the specified status.
	 * 
	 * @param newStatus the new read-only status of the Property.
	 */
	@Override
	public void setReadOnly(boolean newStatus) {
		if (this.dataSource != null) {
			this.dataSource.setReadOnly(newStatus);
		}
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
		if (this.dataSource == null) {
			return;
		}
		try {
			// null is just an ordinary value
			PC convertedValue = this.parse((FC) newValue);
			this.dataSource.setValue(convertedValue);
			if (convertedValue == null ? this.getValue() != null : !convertedValue.equals(this.getValue())) {
				this.fireValueChange();
			}
		} catch (Exception e) {
			throw new ConversionException(e);
		}
	}// setValue

	// Value change and read-only status listeners and notifications

	/**
	 * An <code>Event</code> object specifying the ObjectProperty whose value has changed.
	 */
	private static class ValueChangeEvent extends java.util.EventObject implements Property.ValueChangeEvent {

		/**
		 *
		 */
		private static final long serialVersionUID = -7073151899167101068L;

		/**
		 * Constructs a new value change event for this object.
		 *
		 * @param source the source object of the event.
		 */
		protected ValueChangeEvent(PropertyConverter<?, ?> source) {
			super(source);
		}// new

		@Override
		public Property getProperty() {
			return (Property) this.getSource();
		}
	}// ValueChangeEvent

	/**
	 * An <code>Event</code> object specifying the Property whose read-only status has been changed.
	 */
	private static class ReadOnlyStatusChangeEvent extends java.util.EventObject implements Property.ReadOnlyStatusChangeEvent {

		/**
		 *
		 */
		private static final long serialVersionUID = 8402954871277385069L;

		/**
		 * Constructs a new read-only status change event for this object.
		 * 
		 * @param source source object of the event
		 */
		protected ReadOnlyStatusChangeEvent(PropertyConverter<?, ?> source) {
			super(source);
		}// new

		@Override
		public Property getProperty() {
			return (Property) this.getSource();
		}
	}// ReadOnlyStatusChangeEvent

	/**
	 * Removes a previously registered value change listener.
	 * 
	 * @param listener the listener to be removed.
	 */
	@Override
	public void removeListener(Property.ValueChangeListener listener) {
		this.valueChangeListeners.remove(listener);
	}

	/**
	 * Registers a new value change listener for this ObjectProperty.
	 * 
	 * @param listener the new Listener to be registered
	 */
	@Override
	public void addListener(Property.ValueChangeListener listener) {
		this.valueChangeListeners.add(listener);
	}

	/**
	 * Registers a new read-only status change listener for this Property.
	 * 
	 * @param listener the new Listener to be registered
	 */
	@Override
	public void addListener(Property.ReadOnlyStatusChangeListener listener) {
		this.readOnlyStatusChangeListeners.add(listener);
	}

	/**
	 * Removes a previously registered read-only status change listener.
	 * 
	 * @param listener the listener to be removed.
	 */
	@Override
	public void removeListener(Property.ReadOnlyStatusChangeListener listener) {
		this.readOnlyStatusChangeListeners.remove(listener);
	}

	/**
	 * Sends a value change event to all registered listeners.
	 */
	protected void fireValueChange() {
		final ValueChangeListener[] listeners =
				this.valueChangeListeners.toArray(new ValueChangeListener[this.valueChangeListeners.size()]);
		final Property.ValueChangeEvent event = new ValueChangeEvent(this);
		for (ValueChangeListener listener : listeners) {
			listener.valueChange(event);
		}
	}

	/**
	 * Sends a read only status change event to all registered listeners.
	 */
	protected void fireReadOnlyStatusChange() {
		final ReadOnlyStatusChangeListener[] listeners =
				this.readOnlyStatusChangeListeners.toArray(new ReadOnlyStatusChangeListener[this.readOnlyStatusChangeListeners.size()]);
		final Property.ReadOnlyStatusChangeEvent event = new ReadOnlyStatusChangeEvent(this);
		for (ReadOnlyStatusChangeListener listener : listeners) {
			listener.readOnlyStatusChange(event);
		}
	}

	/**
	 * Listens for changes in the datasource.
	 * 
	 * This should not be called directly.
	 */
	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		this.fireValueChange();
	}

	/**
	 * Listens for changes in the datasource.
	 * 
	 * This should not be called directly.
	 */
	@Override
	public void readOnlyStatusChange(Property.ReadOnlyStatusChangeEvent event) {
		this.fireReadOnlyStatusChange();
	}

	// Validatable

	@Override
	public void addValidator(Validator validator) {
		if (this.validators == null) {
			this.validators = new LinkedList<Validator>();
		}
		this.validators.add(validator);
	}

	@Override
	public void removeValidator(Validator validator) {
		if (this.validators != null) {
			this.validators.remove(validator);
		}
	}

	@Override
	public Collection<Validator> getValidators() {
		if (this.validators == null || this.validators.isEmpty()) {
			// caller friendly
			return Collections.emptySet();
		}
		return Collections.unmodifiableCollection(this.validators);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isValid() {
		if (this.validators == null || this.dataSource == null) {
			return true;
		}

		final Object value = this.getPropertyDataSource().getValue();

		if (value == null || this.getType().isAssignableFrom(value.getClass())) {
			return this.isValid((PC) value);
		} else {
			return false;
		}
	}

	public boolean isValid(PC value) {
		if (this.validators == null) {
			return true;
		}

		for (Validator validator : this.validators) {
			if (!validator.isValid(value)) {
				return false;
			}
		}

		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void validate() {
		if (this.validators == null || this.dataSource == null) {
			return;
		}
		final Object value = this.getPropertyDataSource().getValue();
		if (value == null || this.getType().isAssignableFrom(value.getClass())) {
			this.validate((PC) value);
		}
	}

	public void validate(PC value) {
		// If there is no validator, there can not be any errors
		if (this.validators == null) {
			return;
		}

		// Initialize temps
		List<InvalidValueException> errors = new ArrayList<InvalidValueException>();
		// validate the underlying value, not the formatted value

		// Gets all the validation errors
		for (Validator validator : this.validators) {
			try {
				validator.validate(value);
			} catch (Validator.InvalidValueException e) {
				errors.add(e);
			}
		}

		// If there were no error
		if (errors.isEmpty()) {
			return;
		}

		// If only one error occurred, throw it forwards
		if (errors.size() == 1) {
			throw errors.get(0);
		}

		// Creates composite validation exception
		final Validator.InvalidValueException[] exceptions = errors.toArray(new Validator.InvalidValueException[errors.size()]);

		throw new Validator.InvalidValueException(null, exceptions);
	}

	@Override
	public boolean isInvalidAllowed() {
		return this.invalidAllowed;
	}

	@Override
	public void setInvalidAllowed(boolean invalidAllowed) {
		this.invalidAllowed = invalidAllowed;
	}

}
