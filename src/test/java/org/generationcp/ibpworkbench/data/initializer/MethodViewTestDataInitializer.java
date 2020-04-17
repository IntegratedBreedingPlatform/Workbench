package org.generationcp.ibpworkbench.data.initializer;

import org.generationcp.ibpworkbench.ui.programmethods.MethodView;

public class MethodViewTestDataInitializer {
	public static final String DERIVATIVE_TYPE = "DER";
	public static final String GENERATIVE_TYPE = "GEN";
	public static final String MAINTENANCE_TYPE = "MAN";
	public static MethodView createMethodView() {
		final MethodView methodView = new MethodView();
		methodView.setMname("method name");
		methodView.setMcode("Method Code");
		return methodView;
	}
	public static MethodView createMethodView(final String type) {
		final MethodView methodView = new MethodView();
		methodView.setMname("method name");
		methodView.setMcode("Method Code");
		methodView.setMdesc("Method Description");
		methodView.setGeneq(1);
		methodView.setMtype(type);
		return methodView;
	}
}
