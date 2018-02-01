package org.generationcp.ibpworkbench.validator;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;

@Configurable
@Scope("session")
public class ValidatorCounter {

	private Integer usernameCounter = 0;
	private Integer nameValidationCounter = 0;

	public Integer getUsernameCounter() {
		return this.usernameCounter;
	}

	public void setUsernameCounter(final Integer username_counter) {
		this.usernameCounter = username_counter;
	}

	public Integer getNameValidationCounter() {
		return this.nameValidationCounter;
	}

	public void setNameValidationCounter(final Integer namevalidation_counter) {
		this.nameValidationCounter = namevalidation_counter;
	}

}
