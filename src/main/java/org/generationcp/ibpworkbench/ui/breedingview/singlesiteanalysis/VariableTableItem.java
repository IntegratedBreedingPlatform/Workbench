/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.io.Serializable;

public class VariableTableItem implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6817357350462208956L;

	private Integer id;
	private Integer variableId;
	private String name;
	private String displayName;
	private String description;
	private Integer traitid;
	private String trname;
	private Integer scaleid;
	private String scname;
	private Integer tmethid;
	private String tmname;
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
		this.trname = "";
		this.scname = "";
		this.tmname = "";
		this.active = false;
		this.disabled = false;

	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTraitid() {
		return this.traitid;
	}

	public void setTraitid(Integer traitid) {
		this.traitid = traitid;
	}

	public String getTrname() {
		return this.trname;
	}

	public void setTrname(String trname) {
		this.trname = trname;
	}

	public Integer getScaleid() {
		return this.scaleid;
	}

	public void setScaleid(Integer scaleid) {
		this.scaleid = scaleid;
	}

	public String getScname() {
		return this.scname;
	}

	public void setScname(String scname) {
		this.scname = scname;
	}

	public Integer getTmethid() {
		return this.tmethid;
	}

	public void setTmethid(Integer tmethid) {
		this.tmethid = tmethid;
	}

	public String getTmname() {
		return this.tmname;
	}

	public void setTmname(String tmname) {
		this.tmname = tmname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.id == null ? 0 : this.id.hashCode());
		result = prime * result + (this.name == null ? 0 : this.name.hashCode());
		result = prime * result + (this.scaleid == null ? 0 : this.scaleid.hashCode());
		result = prime * result + (this.scname == null ? 0 : this.scname.hashCode());
		result = prime * result + (this.tmethid == null ? 0 : this.tmethid.hashCode());
		result = prime * result + (this.tmname == null ? 0 : this.tmname.hashCode());
		result = prime * result + (this.traitid == null ? 0 : this.traitid.hashCode());
		result = prime * result + (this.trname == null ? 0 : this.trname.hashCode());
		result = prime * result + (this.description == null ? 0 : this.description.hashCode());
		result = prime * result + (this.active == null ? 0 : this.active.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		VariableTableItem other = (VariableTableItem) obj;
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
		if (this.scaleid == null) {
			if (other.scaleid != null) {
				return false;
			}
		} else if (!this.scaleid.equals(other.scaleid)) {
			return false;
		}
		if (this.scname == null) {
			if (other.scname != null) {
				return false;
			}
		} else if (!this.scname.equals(other.scname)) {
			return false;
		}
		if (this.tmethid == null) {
			if (other.tmethid != null) {
				return false;
			}
		} else if (!this.tmethid.equals(other.tmethid)) {
			return false;
		}
		if (this.tmname == null) {
			if (other.tmname != null) {
				return false;
			}
		} else if (!this.tmname.equals(other.tmname)) {
			return false;
		}
		if (this.traitid == null) {
			if (other.traitid != null) {
				return false;
			}
		} else if (!this.traitid.equals(other.traitid)) {
			return false;
		}
		if (this.trname == null) {
			if (other.trname != null) {
				return false;
			}
		} else if (!this.trname.equals(other.trname)) {
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

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getDatatype() {
		return this.datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public Integer getVariableId() {
		return this.variableId;
	}

	public void setVariableId(Integer variableId) {
		this.variableId = variableId;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isNonNumeric() {
		return this.isNonNumeric;
	}

	public void setNonNumeric(boolean isNonNumeric) {
		this.isNonNumeric = isNonNumeric;
	}

	public boolean isNumericCategoricalVariate() {
		return this.isNumericCategoricalVariate;
	}

	public void setNumericCategoricalVariate(boolean isNumericCategoricalVariate) {
		this.isNumericCategoricalVariate = isNumericCategoricalVariate;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
