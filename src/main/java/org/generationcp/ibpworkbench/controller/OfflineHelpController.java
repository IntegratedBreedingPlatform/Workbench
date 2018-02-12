package org.generationcp.ibpworkbench.controller;

import java.util.Properties;

import javax.annotation.Resource;

import org.generationcp.commons.help.document.HelpDocumentUtil;
import org.generationcp.commons.help.document.HelpModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by cyrus on 8/6/15.
 */
@Controller
@RequestMapping(OfflineHelpController.URL)
public class OfflineHelpController {

	public static final String URL = "/help";
	private static final Logger LOG = LoggerFactory.getLogger(OfflineHelpController.URL);

	@Resource
	private Properties helpProperties;

	@Resource
	private Properties workbenchProperties;

	@RequestMapping(value = "/getUrl/{helpDomain}")
	@ResponseBody
	public String getUrl(@PathVariable String helpDomain) {
		try {
			HelpModule helpModule = HelpModule.valueOf(helpDomain);
			assert helpModule != null;

			String helpUrl = helpProperties.getProperty(helpModule.getPropertyName());
			if (HelpDocumentUtil.isIBPDomainReachable(HelpDocumentUtil.getOnLineLink(helpUrl))) {
				return HelpDocumentUtil.getOnLineLink(helpUrl);
			} else if (this.isDocumentsFolderFound()) {
				return String.format("/BMS_HTML/%s.html", helpUrl);
			}

		} catch (Exception e) {
			// exception is valid in case of no-net or link un-available so we just log.debug it
			LOG.debug(e.getMessage(),e);
		}
		return "";
	}

	private boolean isDocumentsFolderFound() {
		return HelpDocumentUtil.isDocumentsFolderFound();
	}

	@RequestMapping(value = "/headerText")
	@ResponseBody
	public String getHeader() {
		String version = this.workbenchProperties.getProperty("workbench.version", "");
		return String.format("<h1>%s</h1><h2>%s</h2>", "BREEDING MANAGEMENT SYSTEM | WORKBENCH", version);
	}
}
