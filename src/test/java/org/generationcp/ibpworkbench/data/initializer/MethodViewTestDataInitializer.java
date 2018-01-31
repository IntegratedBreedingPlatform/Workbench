package org.generationcp.ibpworkbench.data.initializer;

import org.generationcp.ibpworkbench.ui.programmethods.MethodView;

public class MethodViewTestDataInitializer {
	public static MethodView createMethodView() {
		final MethodView methodView = new MethodView();
		methodView.setMname("method name");
		methodView.setMcode("Method Code");
		return methodView;
	}
}
