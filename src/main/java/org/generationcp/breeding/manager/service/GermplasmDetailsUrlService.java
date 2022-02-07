package org.generationcp.breeding.manager.service;

import com.vaadin.terminal.ExternalResource;
import org.generationcp.commons.constant.DefaultGermplasmStudyBrowserPath;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class GermplasmDetailsUrlService {

	@Autowired
	private ContextUtil contextUtil;

	public ExternalResource getExternalResource(final Integer gid, final boolean isModal) {

		return new ExternalResource(
			WorkbenchAppPathResolver.getFullWebAddress(DefaultGermplasmStudyBrowserPath.GERMPLASM_DETAILS_LINK + gid + "?cropName="
				+ this.contextUtil.getProjectInContext().getCropType().getCropName() + "&programUUID=" + this.contextUtil
				.getCurrentProgramUUID()) + (isModal ? "&modal" : ""));

	}

}
