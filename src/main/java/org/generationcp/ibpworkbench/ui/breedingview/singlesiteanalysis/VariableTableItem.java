package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.io.Serializable;

public class VariableTableItem implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6817357350462208956L;

	private Integer id;
	private String name;
	private String displayName;
	private String description;
	private String property;
	private String scale;
	private String method;
	private Boolean active;
	private String datatype;
	private boolean isNonNumeric;
	private boolean isNumericCategoricalVariate;
	private boolean disabled;

	/**
	 * Initialize fields so that the "null" String value does not appear.
	 */
	public VariableTableItem() {
		this.name = "";
		this.property = "";
		this.scale = "";
		this.method = "";
		this.active = false;
		this.disabled = false;

	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(final String property) {
		this.property = property;
	}

	public String getScale() {
		return this.scale;
	}

	public void setScale(final String scale) {
		this.scale = scale;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.id == null ? 0 : this.id.hashCode());
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
		result = prime * result + (this.property == null ? 0 : this.property.hashCode());
		result = prime * result + (this.description == null ? 0 : this.description.hashCode());
		result = prime * result + (this.active == null ? 0 : this.active.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final VariableTableItem other = (VariableTableItem) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.scale == null) {
			if (other.scale != null) {
				return false;
			}
		} else if (!this.scale.equals(other.scale)) {
			return false;
		}
		if (this.method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!this.method.equals(other.method)) {
			return false;
		}
		if (this.property == null) {
			if (other.property != null) {
				return false;
			}
		} else if (!this.property.equals(other.property)) {
			return false;
		}
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.active == null) {
			if (other.active != null) {
				return false;
			}
		} else if (!this.active.equals(other.active)) {
			return false;
		}
		return true;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(final String datatype) {
		this.datatype = datatype;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public boolean isNonNumeric() {
		return this.isNonNumeric;
	}

	public void setNonNumeric(final boolean isNonNumeric) {
		this.isNonNumeric = isNonNumeric;
	}

	public boolean isNumericCategoricalVariate() {
		return this.isNumericCategoricalVariate;
	}

	public void setNumericCategoricalVariate(final boolean isNumericCategoricalVariate) {
		this.isNumericCategoricalVariate = isNumericCategoricalVariate;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}
}
