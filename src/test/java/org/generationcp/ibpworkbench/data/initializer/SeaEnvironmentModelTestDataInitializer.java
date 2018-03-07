package org.generationcp.ibpworkbench.data.initializer;

import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;

public class SeaEnvironmentModelTestDataInitializer {
	
	public static SeaEnvironmentModel createSeaEnvironmentModel() {
		final SeaEnvironmentModel seaEnvironmentModel = new SeaEnvironmentModel();
		seaEnvironmentModel.setEnvironmentName("WARDA");
		seaEnvironmentModel.setTrialno("1");
		seaEnvironmentModel.setActive(true);
		return seaEnvironmentModel;
	}
}
