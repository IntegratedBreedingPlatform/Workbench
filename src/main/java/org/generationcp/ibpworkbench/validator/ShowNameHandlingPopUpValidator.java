package org.generationcp.ibpworkbench.validator;

import java.util.List;

import org.generationcp.ibpworkbench.pojos.ImportedGermplasm;
import org.generationcp.middleware.components.validator.ChainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShowNameHandlingPopUpValidator extends ChainValidator<List<ImportedGermplasm>> {

	@Autowired
	public void setCheckNameFactorsPresentValidationRule(CheckNameFactorsPresentValidationRule checkNameFactorsPresentValidationRule) {
		this.add(checkNameFactorsPresentValidationRule);
	}
}
