package org.generationcp.ibpworkbench.ui.programlocations;

import org.generationcp.middleware.pojos.BeanFormState;
import org.generationcp.middleware.pojos.Location;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;

public class LocationViewModel implements BeanFormState {
	

	private Integer locationId;
	private String locationName = "";
	public String locationAbbreviation = "";

    public Integer getLtype() {
        return ltype;
    }

    public void setLtype(Integer ltype) {
        this.ltype = ltype;
    }

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

    public LocationViewModel() {}
    public LocationViewModel(Location location) {

    }

	

	public Integer getLocationId() {
		return locationId;
	}



	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}



	public String getLocationName() {
		return locationName;
	}



	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}



	public String getLocationAbbreviation() {
		return locationAbbreviation;
	}



	public void setLocationAbbreviation(String locationAbbreviation) {
		this.locationAbbreviation = locationAbbreviation;
	}



	public String getLtypeStr() {
		return ltypeStr;
	}



	public void setLtypeStr(String ltypeStr) {
		this.ltypeStr = ltypeStr;
	}



	public String getCntryFullName() {
		return cntryFullName;
	}



	public void setCntryFullName(String cntryFullName) {
		this.cntryFullName = cntryFullName;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
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
		return "LocationViewModel: [locationId="+ locationId + ", locationName="+ locationName + ", locationAbbreviation="+ locationAbbreviation +", ltypeStr="+ ltypeStr + ", cntryFullName=" + cntryFullName +"]";
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
        return cntryid;
    }

    public void setCntryid(Integer cntryid) {
        this.cntryid = cntryid;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return isEnabled;
	}

	@Override
	public void setEnabled(Boolean val) {
		this.isEnabled = val;
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((locationId == null) ? 0 : locationId.hashCode());
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
		if (getClass() != obj.getClass()) {
            return false;
        }
		LocationViewModel other = (LocationViewModel) obj;
		if (locationId == null) {
			if (other.locationId != null) {
                return false;
            }
		} else if (!locationId.equals(other.locationId)) {
            return false;
        }
		return true;
	}
	

}