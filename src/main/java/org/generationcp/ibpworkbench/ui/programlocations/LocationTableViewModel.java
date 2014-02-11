package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.List;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class LocationTableViewModel {
	private Integer locationId;
	private String locationName;
	public String locationAbbreviation;
	public String ltype;

	private String cntryFullName;
	
	/*
	private String latitude;
	private String longtitude;
	private String altitude;
	*/
	

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



	public String getLtype() {
		return ltype;
	}



	public void setLtype(String ltype) {
		this.ltype = ltype;
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

	public static List<LocationTableViewModel> generateRandomData(int itemCount) {
		List<LocationTableViewModel> list = new ArrayList<LocationTableViewModel>();
		
		PodamFactory factory = new PodamFactoryImpl();
		
		for (int i = 0; i < itemCount; i++) {
			list.add(factory.manufacturePojo(LocationTableViewModel.class));
		}

		return list;
	}

	@Override
	public String toString() {
		return "LocationTableViewModel: [locationId="+ locationId + ", locationName="+ locationName + ", locationAbbreviation="+ locationAbbreviation +", ltype="+ ltype + ", cntryFullName=" + cntryFullName +"]";
	}
	
	
	
}