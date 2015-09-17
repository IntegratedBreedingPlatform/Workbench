
package org.generationcp.ibpworkbench.ui.common.customfield;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.CompositeErrorMessage;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;

/**
 * A {@link CustomComponent} that implements the {@link Field} interface, enabling the creation of e.g. form fields by composing Vaadin
 * components. Customization of both the visual presentation and the logic of the field is possible.
 *
 * Subclasses must at least implement the method {@link #getType()} and set the composition root (typically in the constructor). In
 * addition, other methods can be overridden to customize the functionality.
 *
 * Most custom fields can simply compose a user interface that calls the methods {@link #setValue(Object)} and {@link #getValue()}.
 *
 * It is also possible to override {@link #commit()}, {@link #setPropertyDataSource(Property)} and other logic of the field.
 *
 * @author Matti Tahvonen
 * @author Henri Sara
 */
public abstract class CustomField extends CustomComponent implements Field {

	private static final long serialVersionUID = 5457282096887625533L;

	/**
	 * Value of the abstract field.
	 */
	private Object value;

	/**
	 * Connected data-source.
	 */
	private Property dataSource = null;

	/**
	 * The list of validators.
	 */
	private LinkedList<Validator> validators = null;

	/**
	 * Auto commit mode.
	 */
	private boolean writeTroughMode = true;

	/**
	 * Reads the value from data-source, when it is not modified.
	 */
	private boolean readTroughMode = true;

	/**
	 * Is the field modified but not committed.
	 */
	private boolean modified = false;

	/**
	 * Current source exception.
	 */
	private Buffered.SourceException currentBufferedSourceException = null;

	/**
	 * Are the invalid values allowed in fields ?
	 */
	private boolean invalidAllowed = true;

	/**
	 * Are the invalid values committed ?
	 */
	private boolean invalidCommitted = false;

	/**
	 * The tab order number of this field.
	 */
	private int tabIndex = 0;

	/**
	 * Required field.
	 */
	private boolean required = false;

	/**
	 * The error message for the exception that is thrown when the field is required but empty.
	 */
	private String requiredError = "";

	/**
	 * Is automatic validation enabled.
	 */
	private boolean validationVisible = true;

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

	@Override
	public abstract Class<?> getType();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.AbstractComponent#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return super.isReadOnly() || this.dataSource != null && this.dataSource.isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.BufferedValidatable#isInvalidCommitted()
	 */
	@Override
	public boolean isInvalidCommitted() {
		return this.invalidCommitted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.BufferedValidatable#setInvalidCommitted(boolean)
	 */
	@Override
	public void setInvalidCommitted(boolean isCommitted) {
		this.invalidCommitted = isCommitted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Buffered#commit()
	 */
	@Override
	public void commit() throws Buffered.SourceException, InvalidValueException {
		if (this.dataSource != null && !this.dataSource.isReadOnly()) {
			if (this.isInvalidCommitted() || this.isValid()) {
				final Object newValue = this.getValue();
				try {

					// Commits the value to datasource.
					this.dataSource.setValue(newValue);

				} catch (final Exception e) {

					// Sets the buffering state.
					this.currentBufferedSourceException = new Buffered.SourceException(this, e);
					this.requestRepaint();

					// Throws the source exception.
					throw this.currentBufferedSourceException;
				}
			} else {
				/* An invalid value and we don't allow them, throw the exception */
				this.validate();
			}
		}

		boolean repaintNeeded = false;

		// The abstract field is not modified anymore
		if (this.modified) {
			this.modified = false;
			repaintNeeded = true;
		}

		// If successful, remove set the buffering state to be ok
		if (this.currentBufferedSourceException != null) {
			this.currentBufferedSourceException = null;
			repaintNeeded = true;
		}

		if (repaintNeeded) {
			this.requestRepaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Buffered#discard()
	 */
	@Override
	public void discard() throws Buffered.SourceException {
		if (this.dataSource != null) {

			// Gets the correct value from datasource
			Object newValue;
			try {

				// Discards buffer by overwriting from datasource
				newValue = String.class == this.getType() ? this.dataSource.toString() : this.dataSource.getValue();

				// If successful, remove set the buffering state to be ok
				if (this.currentBufferedSourceException != null) {
					this.currentBufferedSourceException = null;
					this.requestRepaint();
				}
			} catch (final Exception e) {

				// Sets the buffering state
				this.currentBufferedSourceException = new Buffered.SourceException(this, e);
				this.requestRepaint();

				// Throws the source exception
				throw this.currentBufferedSourceException;
			}

			final boolean wasModified = this.isModified();
			this.modified = false;

			// If the new value differs from the previous one
			if (newValue == null && this.value != null || newValue != null && !newValue.equals(this.value)) {
				this.setInternalValue(newValue);
				this.fireValueChange(false);
			}

			// If the value did not change, but the modification status did
			else if (wasModified) {
				this.requestRepaint();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Buffered#isModified()
	 */
	@Override
	public boolean isModified() {
		return this.modified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Buffered#isWriteThrough()
	 */
	@Override
	public boolean isWriteThrough() {
		return this.writeTroughMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Buffered#setWriteThrough(boolean)
	 */
	@Override
	public void setWriteThrough(boolean writeTrough) throws Buffered.SourceException, InvalidValueException {
		if (this.writeTroughMode == writeTrough) {
			return;
		}
		this.writeTroughMode = writeTrough;
		if (this.writeTroughMode) {
			this.commit();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Buffered#isReadThrough()
	 */
	@Override
	public boolean isReadThrough() {
		return this.readTroughMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Buffered#setReadThrough(boolean)
	 */
	@Override
	public void setReadThrough(boolean readTrough) throws Buffered.SourceException {
		if (this.readTroughMode == readTrough) {
			return;
		}
		this.readTroughMode = readTrough;
		if (!this.isModified() && this.readTroughMode && this.dataSource != null) {
			this.setInternalValue(String.class == this.getType() ? this.dataSource.toString() : this.dataSource.getValue());
			this.fireValueChange(false);
		}
	}

	/**
	 * Returns the value of the Property in human readable textual format.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final Object value = this.getValue();
		if (value == null) {
			return null;
		}
		return this.getValue().toString();
	}

	/**
	 * Gets the current value of the field.
	 * 
	 * <p>
	 * This is the visible, modified and possible invalid value the user have entered to the field. In the read-through mode, the abstract
	 * buffer is also updated and validation is performed.
	 * </p>
	 * 
	 * <p>
	 * Note that the object returned is compatible with getType(). For example, if the type is String, this returns Strings even when the
	 * underlying datasource is of some other type. In order to access the datasources native type, use getPropertyDatasource().getValue()
	 * instead.
	 * </p>
	 * 
	 * <p>
	 * Note that when you extend CustomField, you must reimplement this method if datasource.getValue() is not assignable to class returned
	 * by getType() AND getType() is not String. In case of Strings, getValue() calls datasource.toString() instead of
	 * datasource.getValue().
	 * </p>
	 * 
	 * @return the current value of the field.
	 */
	@Override
	public Object getValue() {

		// Give the value from abstract buffers if the field if possible
		if (this.dataSource == null || !this.isReadThrough() || this.isModified()) {
			return this.value;
		}

		Object newValue = String.class == this.getType() ? this.dataSource.toString() : this.dataSource.getValue();
		if (newValue == null && this.value != null || newValue != null && !newValue.equals(this.value)) {
			this.setInternalValue(newValue);
			this.fireValueChange(false);
		}

		return newValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Property#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object newValue) throws Property.ReadOnlyException, Property.ConversionException {
		this.setValue(newValue, false);
	}

	/**
	 * Sets the value of the field.
	 * 
	 * @param newValue the New value of the field.
	 * @param repaintIsNotNeeded True iff caller is sure that repaint is not needed.
	 * @throws Property.ReadOnlyException
	 * @throws Property.ConversionException
	 */
	protected void setValue(Object newValue, boolean repaintIsNotNeeded) throws Property.ReadOnlyException, Property.ConversionException {

		if (newValue == null && this.value != null || newValue != null && !newValue.equals(this.value)) {

			// Read only fields can not be changed
			if (this.isReadOnly()) {
				throw new Property.ReadOnlyException();
			}

			// Repaint is needed even when the client thinks that it knows the
			// new state if validity of the component may change
			if (repaintIsNotNeeded && (this.isRequired() || this.getValidators() != null)) {
				repaintIsNotNeeded = false;
			}

			// If invalid values are not allowed, the value must be checked
			if (!this.isInvalidAllowed()) {
				final Collection<Validator> validators = this.getValidators();
				if (validators != null) {
					for (Validator v : validators) {
						v.validate(newValue);
					}
				}
			}

			// Changes the value
			this.setInternalValue(newValue);
			this.modified = this.dataSource != null;

			// In write trough mode , try to commit
			if (this.isWriteThrough() && this.dataSource != null && (this.isInvalidCommitted() || this.isValid())) {
				try {

					// Commits the value to datasource
					this.dataSource.setValue(newValue);

					// The buffer is now unmodified
					this.modified = false;

				} catch (final Exception e) {

					// Sets the buffering state
					this.currentBufferedSourceException = new Buffered.SourceException(this, e);
					this.requestRepaint();

					// Throws the source exception
					throw this.currentBufferedSourceException;
				}
			}

			// If successful, remove set the buffering state to be ok
			if (this.currentBufferedSourceException != null) {
				this.currentBufferedSourceException = null;
				this.requestRepaint();
			}

			// Fires the value change
			this.fireValueChange(repaintIsNotNeeded);
		}
	}

	@Override
	public Property getPropertyDataSource() {
		return this.dataSource;
	}

	/**
	 * <p>
	 * Sets the specified Property as the data source for the field. All uncommitted changes to the field are discarded and the value is
	 * refreshed from the new data source.
	 * </p>
	 * 
	 * <p>
	 * If the datasource has any validators, the same validators are added to the field. Because the default behavior of the field is to
	 * allow invalid values, but not to allow committing them, this only adds visual error messages to fields and do not allow committing
	 * them as long as the value is invalid. After the value is valid, the error message is not shown and the commit can be done normally.
	 * </p>
	 * 
	 * @param newDataSource the new data source Property.
	 */
	@Override
	public void setPropertyDataSource(Property newDataSource) {

		// Saves the old value
		final Object oldValue = this.value;

		// Discards all changes to old datasource
		try {
			this.discard();
		} catch (final Buffered.SourceException ignored) {
			// Do nothing
		}

		// Stops listening the old data source changes
		if (this.dataSource != null && Property.ValueChangeNotifier.class.isAssignableFrom(this.dataSource.getClass())) {
			((Property.ValueChangeNotifier) this.dataSource).removeListener(this);
		}

		// Sets the new data source
		this.dataSource = newDataSource;

		// Gets the value from source
		try {
			if (this.dataSource != null) {
				this.setInternalValue(String.class == this.getType() ? this.dataSource.toString() : this.dataSource.getValue());
			}
			this.modified = false;
		} catch (final Exception e) {
			this.currentBufferedSourceException = new Buffered.SourceException(this, e);
			this.modified = true;
		}

		// Listens the new data source if possible
		if (this.dataSource instanceof Property.ValueChangeNotifier) {
			((Property.ValueChangeNotifier) this.dataSource).addListener(this);
		}

		// Copy the validators from the data source
		if (this.dataSource instanceof Validatable) {
			final Collection<Validator> validators = ((Validatable) this.dataSource).getValidators();
			if (validators != null) {
				for (final Iterator<Validator> i = validators.iterator(); i.hasNext();) {
					this.addValidator(i.next());
				}
			}
		}

		// Fires value change if the value has changed
		if (this.value != oldValue && (this.value != null && !this.value.equals(oldValue) || this.value == null)) {
			this.fireValueChange(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Validatable#addValidator(com.vaadin.data.Validator)
	 */
	@Override
	public void addValidator(Validator validator) {
		if (this.validators == null) {
			this.validators = new LinkedList<Validator>();
		}
		this.validators.add(validator);
		this.requestRepaint();
	}

	/**
	 * Gets the validators of the field.
	 * 
	 * @return the Unmodifiable collection that holds all validators for the field, not null.
	 */
	@Override
	public Collection<Validator> getValidators() {
		if (this.validators == null || this.validators.isEmpty()) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableCollection(this.validators);
	}

	/**
	 * Removes the validator from the field.
	 * 
	 * @param validator the validator to remove.
	 */
	@Override
	public void removeValidator(Validator validator) {
		if (this.validators != null) {
			this.validators.remove(validator);
		}
		this.requestRepaint();
	}

	/**
	 * Tests the current value against all registered validators.
	 * 
	 * @return <code>true</code> if all registered validators claim that the current value is valid, <code>false</code> otherwise.
	 */
	@Override
	public boolean isValid() {

		if (this.isEmpty()) {
			return !this.isRequired();
		}

		if (this.validators == null) {
			return true;
		}

		final Object value = this.getValue();
		for (final Iterator<Validator> i = this.validators.iterator(); i.hasNext();) {
			if (!i.next().isValid(value)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks the validity of the Validatable by validating the field with all attached validators.
	 * 
	 * The "required" validation is a built-in validation feature. If the field is required, but empty, validation will throw an
	 * EmptyValueException with the error message set with setRequiredError().
	 * 
	 * @see com.vaadin.data.Validatable#validate()
	 */
	@Override
	public void validate() throws Validator.InvalidValueException {

		if (this.isEmpty()) {
			if (this.isRequired()) {
				throw new Validator.EmptyValueException(this.requiredError);
			} else {
				return;
			}
		}

		// If there is no validator, there can not be any errors
		if (this.validators == null) {
			return;
		}

		// Initialize temps
		Validator.InvalidValueException firstError = null;
		LinkedList<InvalidValueException> errors = null;
		final Object value = this.getValue();

		// Gets all the validation errors
		for (final Iterator<Validator> i = this.validators.iterator(); i.hasNext();) {
			try {
				i.next().validate(value);
			} catch (final Validator.InvalidValueException e) {
				if (firstError == null) {
					firstError = e;
				} else {
					if (errors == null) {
						errors = new LinkedList<InvalidValueException>();
						errors.add(firstError);
					}
					errors.add(e);
				}
			}
		}

		// If there were no error
		if (firstError == null) {
			return;
		}

		// If only one error occurred, throw it forwards
		if (errors == null) {
			throw firstError;
		}

		// Creates composite validator
		final Validator.InvalidValueException[] exceptions = new Validator.InvalidValueException[errors.size()];
		int index = 0;
		for (final Iterator<InvalidValueException> i = errors.iterator(); i.hasNext();) {
			exceptions[index++] = i.next();
		}

		throw new Validator.InvalidValueException(null, exceptions);
	}

	/**
	 * Fields allow invalid values by default. In most cases this is wanted, because the field otherwise visually forget the user input
	 * immediately.
	 * 
	 * @return true iff the invalid values are allowed.
	 * @see com.vaadin.data.Validatable#isInvalidAllowed()
	 */
	@Override
	public boolean isInvalidAllowed() {
		return this.invalidAllowed;
	}

	/**
	 * Fields allow invalid values by default. In most cases this is wanted, because the field otherwise visually forget the user input
	 * immediately.
	 * <p>
	 * In common setting where the user wants to assure the correctness of the datasource, but allow temporarily invalid contents in the
	 * field, the user should add the validators to datasource, that should not allow invalid values. The validators are automatically
	 * copied to the field when the datasource is set.
	 * </p>
	 * 
	 * @see com.vaadin.data.Validatable#setInvalidAllowed(boolean)
	 */
	@Override
	public void setInvalidAllowed(boolean invalidAllowed) throws UnsupportedOperationException {
		this.invalidAllowed = invalidAllowed;
	}

	/**
	 * Error messages shown by the fields are composites of the error message thrown by the superclasses (that is the component error
	 * message), validation errors and buffered source errors.
	 * 
	 * @see com.vaadin.ui.AbstractComponent#getErrorMessage()
	 */
	@Override
	public ErrorMessage getErrorMessage() {

		/*
		 * Check validation errors only if automatic validation is enabled. Empty, required fields will generate a validation error
		 * containing the requiredError string. For these fields the exclamation mark will be hidden but the error must still be sent to the
		 * client.
		 */
		ErrorMessage validationError = null;
		if (this.isValidationVisible()) {
			try {
				this.validate();
			} catch (Validator.InvalidValueException e) {
				if (!e.isInvisible()) {
					validationError = e;
				}
			}
		}

		// Check if there are any systems errors
		final ErrorMessage superError = super.getErrorMessage();

		// Return if there are no errors at all
		if (superError == null && validationError == null && this.currentBufferedSourceException == null) {
			return null;
		}

		// Throw combination of the error types
		return new CompositeErrorMessage(new ErrorMessage[] {superError, validationError, this.currentBufferedSourceException});

	}

	/* Value change events */

	private static final Method VALUE_CHANGE_METHOD;

	static {
		try {
			VALUE_CHANGE_METHOD =
					Property.ValueChangeListener.class.getDeclaredMethod("valueChange", Property.ValueChangeEvent.class);
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException("Internal error finding methods in AbstractField");
		}
	}

	/*
	 * Adds a value change listener for the field. Don't add a JavaDoc comment here, we use the default documentation from the implemented
	 * interface.
	 */
	@Override
	public void addListener(Property.ValueChangeListener listener) {
		this.addListener(AbstractField.ValueChangeEvent.class, listener, CustomField.VALUE_CHANGE_METHOD);
	}

	/*
	 * Removes a value change listener from the field. Don't add a JavaDoc comment here, we use the default documentation from the
	 * implemented interface.
	 */
	@Override
	public void removeListener(Property.ValueChangeListener listener) {
		this.removeListener(AbstractField.ValueChangeEvent.class, listener, CustomField.VALUE_CHANGE_METHOD);
	}

	/**
	 * Emits the value change event. The value contained in the field is validated before the event is created.
	 */
	protected void fireValueChange(boolean repaintIsNotNeeded) {
		this.fireEvent(new AbstractField.ValueChangeEvent(this));
		if (!repaintIsNotNeeded) {
			this.requestRepaint();
		}
	}

	/* Read-only status change events */

	/**
	 * This method listens to data source value changes and passes the changes forwards.
	 * 
	 * @param event the value change event telling the data source contents have changed.
	 */
	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		if (this.isReadThrough() || !this.isModified()) {
			this.fireValueChange(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.Component.Focusable#focus()
	 */
	@Override
	public void focus() {
		super.focus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.Component.Focusable#getTabIndex()
	 */
	@Override
	public int getTabIndex() {
		return this.tabIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	/**
	 * Sets the internal field value. This is purely used by CustomField to change the internal Field value. It does not trigger valuechange
	 * events. It can be overriden by the inheriting classes to update all dependent variables.
	 * 
	 * @param newValue the new value to be set.
	 */
	protected void setInternalValue(Object newValue) {
		this.value = newValue;
		if (this.validators != null && !this.validators.isEmpty()) {
			this.requestRepaint();
		}
	}

	/**
	 * Is this field required. Required fields must filled by the user.
	 * 
	 * If the field is required, it is visually indicated in the user interface. Furthermore, setting field to be required implicitly adds
	 * "non-empty" validator and thus isValid() == false or any isEmpty() fields. In those cases validation errors are not painted as it is
	 * obvious that the user must fill in the required fields.
	 * 
	 * On the other hand, for the non-required fields isValid() == true if the field isEmpty() regardless of any attached validators.
	 * 
	 * 
	 * @return <code>true</code> if the field is required .otherwise <code>false</code>.
	 */
	@Override
	public boolean isRequired() {
		return this.required;
	}

	/**
	 * Sets the field required. Required fields must filled by the user.
	 * 
	 * If the field is required, it is visually indicated in the user interface. Furthermore, setting field to be required implicitly adds
	 * "non-empty" validator and thus isValid() == false or any isEmpty() fields. In those cases validation errors are not painted as it is
	 * obvious that the user must fill in the required fields.
	 * 
	 * On the other hand, for the non-required fields isValid() == true if the field isEmpty() regardless of any attached validators.
	 * 
	 * @param required Is the field required.
	 */
	@Override
	public void setRequired(boolean required) {
		this.required = required;
		this.requestRepaint();
	}

	/**
	 * Set the error that is show if this field is required, but empty. When setting requiredMessage to be "" or null, no error pop-up or
	 * exclamation mark is shown for a empty required field. This faults to "". Even in those cases isValid() returns false for empty
	 * required fields.
	 * 
	 * @param requiredMessage Message to be shown when this field is required, but empty.
	 */
	@Override
	public void setRequiredError(String requiredMessage) {
		this.requiredError = requiredMessage;
		this.requestRepaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.Field#getRequiredError()
	 */
	@Override
	public String getRequiredError() {
		return this.requiredError;
	}

	/**
	 * Is the field empty?
	 * 
	 * In general, "empty" state is same as null..
	 */
	protected boolean isEmpty() {
		return this.getValue() == null;
	}

	/**
	 * Is automatic, visible validation enabled?
	 * 
	 * If automatic validation is enabled, any validators connected to this component are evaluated while painting the component and
	 * potential error messages are sent to client. If the automatic validation is turned off, isValid() and validate() methods still work,
	 * but one must show the validation in their own code.
	 * 
	 * @return True, if automatic validation is enabled.
	 */
	public boolean isValidationVisible() {
		return this.validationVisible;
	}

	/**
	 * Enable or disable automatic, visible validation.
	 * 
	 * If automatic validation is enabled, any validators connected to this component are evaluated while painting the component and
	 * potential error messages are sent to client. If the automatic validation is turned off, isValid() and validate() methods still work,
	 * but one must show the validation in their own code.
	 * 
	 * @param validateAutomatically True, if automatic validation is enabled.
	 */
	public void setValidationVisible(boolean validateAutomatically) {
		if (this.validationVisible != validateAutomatically) {
			this.requestRepaint();
			this.validationVisible = validateAutomatically;
		}
	}

	public void setCurrentBufferedSourceException(Buffered.SourceException currentBufferedSourceException) {
		this.currentBufferedSourceException = currentBufferedSourceException;
		this.requestRepaint();
	}
}
