
package org.generationcp.ibpworkbench.controller;

import java.util.Properties;

import javax.annotation.Resource;

import org.generationcp.commons.help.document.HelpDocumentUtil;
import org.generationcp.commons.help.document.HelpModule;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by cyrus on 8/6/15.
 */
@Controller
@RequestMapping(HelpController.URL)
public class HelpController {

	public static final String URL = "/help";

	@Resource
	private Properties helpProperties;

	@Resource
	private Properties workbenchProperties;

	@RequestMapping(value = "/getUrl/{helpDomain}")
	@ResponseBody
	public String getUrl(@PathVariable final String helpDomain) {
		final HelpModule helpModule = HelpModule.valueOf(helpDomain);
		assert helpModule != null;

		final String helpUrl = this.helpProperties.getProperty(helpModule.getPropertyName());
		return HelpDocumentUtil.getOnLineLink(helpUrl);

	}

}
