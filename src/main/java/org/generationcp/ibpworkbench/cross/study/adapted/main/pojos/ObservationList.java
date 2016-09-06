
package org.generationcp.ibpworkbench.cross.study.adapted.main.pojos;

import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.ObservationKey;

import java.util.ArrayList;
import java.util.List;

public class ObservationList {

	ObservationKey key;
	List<Observation> observationList = new ArrayList<Observation>();

	public ObservationList(ObservationKey key) {
		super();
		this.key = key;
	}

	public ObservationKey getKey() {
		return this.key;
	}

	public void setKey(ObservationKey key) {
		this.key = key;
	}

	public List<Observation> getObservationList() {
		return this.observationList;
	}

	public void setObservationList(List<Observation> observationList) {
		this.observationList = observationList;
	}

	public void add(Observation obs) {
		this.observationList.add(obs);
	}
}
