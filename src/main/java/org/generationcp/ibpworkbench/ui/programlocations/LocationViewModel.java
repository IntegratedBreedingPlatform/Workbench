
package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.BeanFormState;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class LocationViewModel implements BeanFormState {

	private Integer locationId;
	private String locationName = "";
	public String locationAbbreviation = "";

	public Integer ltype;
	public String ltypeStr;

	private Integer cntryid;
	private Integer provinceId;

	private String cntryFullName;
	private Boolean isSelected = false;
	private Boolean isEnabled = true;

	private Double latitude;
	private Double longitude;
	private Double altitude;

	private String programUUID;

	private Boolean cropAccessible = false;

	public LocationViewModel() {
	}

	public Integer getLtype() {
		return this.ltype;
	}

	public void setLtype(Integer ltype) {
		this.ltype = ltype;
	}



	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAbbreviation() {
		return this.locationAbbreviation;
	}

	public void setLocationAbbreviation(String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}

	public String getLtypeStr() {
		return this.ltypeStr;
	}

	public void setLtypeStr(String ltypeStr) {
		this.ltypeStr = ltypeStr;
	}

	public String getCntryFullName() {
		return this.cntryFullName;
	}

	public void setCntryFullName(String cntryFullName) {
		this.cntryFullName = cntryFullName;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return this.altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public static List<LocationViewModel> generateRandomData(int itemCount) {
		List<LocationViewModel> list = new ArrayList<LocationViewModel>();

		PodamFactory factory = new PodamFactoryImpl();

		for (int i = 0; i < itemCount; i++) {
			list.add(factory.manufacturePojo(LocationViewModel.class));
		}

		return list;
	}

	@Override
	public String toString() {
		return "LocationViewModel: [locationId=" + this.locationId + ", locationName=" + this.locationName + ", locationAbbreviation="
				+ this.locationAbbreviation + ", ltypeStr=" + this.ltypeStr + ", cntryFullName=" + this.cntryFullName + "]";
	}

	@Override
	public boolean isActive() {
		return this.isSelected;
	}

	@Override
	public void setActive(Boolean val) {
		this.isSelected = val;
	}

	public Integer getCntryid() {
		return this.cntryid;
	}

	public void setCntryid(Integer cntryid) {
		this.cntryid = cntryid;
	}

	public Integer getProvinceId() {
		return this.provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Boolean getSelected() {
		return this.isSelected;
	}

	public void setSelected(Boolean selected) {
		this.isSelected = selected;
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public void setEnabled(Boolean val) {
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
		LocationViewModel other = (LocationViewModel) obj;
		if (this.locationId == null) {
			if (other.locationId != null) {
				return false;
			}
		} else if (!this.locationId.equals(other.locationId)) {
			return false;
		}
		return true;
	}

	public String getProgramUUID() {
		return programUUID;
	}

	public void setProgramUUID(String programUUID) {
		this.programUUID = programUUID;
	}

	public Boolean getCropAccessible() {
		return cropAccessible;
	}

	public void setCropAccessible(Boolean cropAccessible) {
		this.cropAccessible = cropAccessible;
	}
}
