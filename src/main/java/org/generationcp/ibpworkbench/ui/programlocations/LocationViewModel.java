package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.BeanFormState;
import org.generationcp.middleware.pojos.Location;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class LocationViewModel implements BeanFormState {
	private Integer locationId;
	private String locationName;
	public String locationAbbreviation;

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
    private String provinceFullName;
    private Boolean isSelected = false;


	/*
	private String latitude;
	private String longtitude;
	private String altitude;
	*/

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


	/*
	public String getLatitude() {
		return latitude;
	}


	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}



	public String getLongtitude() {
		return longtitude;
	}



	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}



	public String getAltitude() {
		return altitude;
	}



	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	*/

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
}