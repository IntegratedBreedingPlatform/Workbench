package org.generationcp.ibpworkbench.ui.programlocations;

import org.generationcp.middleware.pojos.BeanFormState;

import java.util.ArrayList;
import java.util.List;

public class LocationViewModel implements BeanFormState {

	private Integer locationId;
	private String locationName = "";
	private String locationAbbreviation = "";

	private Integer ltype;
	private String ltypeStr;

	private Integer cntryid;
	private String cntryName;
	private String cntryFullName;

	private Integer provinceId;
	private String provinceName;

	private Boolean isSelected = false;
	private Boolean isEnabled = true;

	private Double latitude;
	private Double longitude;
	private Double altitude;

	private Boolean lDefault;

	public LocationViewModel() {
	}

	public Integer getLtype() {
		return this.ltype;
	}

	public void setLtype(final Integer ltype) {
		this.ltype = ltype;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	public void setLocationAbbreviation(final String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public String getLtypeStr() {
		return this.ltypeStr;
	}

	public void setLtypeStr(final String ltypeStr) {
		this.ltypeStr = ltypeStr;
	}

	public String getCntryFullName() {
		return this.cntryFullName;
	}

	public void setCntryFullName(final String cntryFullName) {
		this.cntryFullName = cntryFullName;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(final Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(final Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return this.altitude;
	}

	public void setAltitude(final Double altitude) {
		this.altitude = altitude;
	}

	@Override
	public String toString() {
		return new StringBuilder("LocationViewModel: [locationId=").append(this.locationId).append(", locationName=")
			.append(this.locationName).append(", locationAbbreviation=").append(this.locationAbbreviation).append(", ltypeStr=")
			.append(this.ltypeStr).append(", cntryFullName=").append(this.cntryFullName).append(", cntryName=").append(this.cntryName)
			.append(", provinceName=").append(this.provinceName).append("]").toString();
	}

	@Override
	public boolean isActive() {
		return this.isSelected;
	}

	@Override
	public void setActive(final Boolean val) {
		this.isSelected = val;
	}

	public Integer getCntryid() {
		return this.cntryid;
	}

	public void setCntryid(final Integer cntryid) {
		this.cntryid = cntryid;
	}

	public Integer getProvinceId() {
		return this.provinceId;
	}

	public void setProvinceId(final Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Boolean getSelected() {
		return this.isSelected;
	}

	public void setSelected(final Boolean selected) {
		this.isSelected = selected;
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public void setEnabled(final Boolean val) {
		this.isEnabled = val;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.locationId == null ? 0 : this.locationId.hashCode());
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
		final LocationViewModel other = (LocationViewModel) obj;
		if (this.locationId == null) {
			if (other.locationId != null) {
				return false;
			}
		} else if (!this.locationId.equals(other.locationId)) {
			return false;
		}
		return true;
	}

	public String getCntryName() {
		return cntryName;
	}

	public void setCntryName(final String cntryName) {
		this.cntryName = cntryName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(final String provinceName) {
		this.provinceName = provinceName;
	}

	public Boolean getEnabled() {
		return isEnabled;
	}

	public Boolean getlDefault() {
		return lDefault;
	}

	public void setlDefault(final Boolean lDefault) {
		this.lDefault = lDefault;
	}
}
