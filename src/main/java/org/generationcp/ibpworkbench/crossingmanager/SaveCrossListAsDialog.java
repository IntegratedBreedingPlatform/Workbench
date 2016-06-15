
package org.generationcp.ibpworkbench.crossingmanager;

import org.generationcp.ibpworkbench.customcomponent.SaveListAsDialog;
import org.generationcp.ibpworkbench.customcomponent.SaveListAsDialogSource;
import org.generationcp.middleware.pojos.GermplasmList;

public class SaveCrossListAsDialog extends SaveListAsDialog {

	private static final long serialVersionUID = -4151286394925054516L;

	public SaveCrossListAsDialog(final SaveListAsDialogSource source, final GermplasmList germplasmList) {
		super(source, germplasmList);
	}

	@Override
	public void initializeValues() {
		super.initializeValues();
		this.getDetailsComponent().getListTypeField().setValue("F1");
	}

	@Override
	public String defaultListType() {
		return "F1";
	}

}
