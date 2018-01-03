package org.generationcp.ibpworkbench.ui.programmethods;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Configurable
@Scope("session")
public class BreedingMethodTracker {

	private final HashMap<Integer, MethodView> breedingMethodMaps = new HashMap<Integer, MethodView>();
	private final Set<String> uniqueBreedingMethods = new HashSet<String>();

	public HashMap<Integer, MethodView> getProjectBreedingMethodData() {
		return this.breedingMethodMaps;
	}

	public Set<String> getUniqueBreedingMethods() {
		return this.uniqueBreedingMethods;
	}

}
