package org.generationcp.ibpworkbench.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by cyrus on 7/29/15.
 */
@Controller
@RequestMapping(AboutController.URL)
public class AboutController {
	public static final String URL = "/about";

	@Value("${bms.version}")
	private String workbenchVersion;

	@ModelAttribute("workbenchVersion")
	public String getWorkbenchVersion() {
		return workbenchVersion;
	}

	@ModelAttribute("partnerImages")
	public List<String> getPartnerImages() {
		return Arrays.asList(
				"Logo_Bioversity.jpg",
				"Logo_CAAS.png",
				"agri_food_logo.jpg",
				"Logo_CIAT.jpg",
				"Logo_CIMMYT.jpg",
				"Logo_ICRISAT.jpg",
				"Logo_INRA.jpg",
				"Logo_iPlant.jpg",
				"Logo_IRRI.jpg",
				"Logo_Leafnode.png",
				"Logo_VSNI.png",
				"Logo_Waigeningen.jpg"
		);
	}

	@ModelAttribute("supportImages")
	public List<String> getSupportImages() {
		return Arrays.asList(
				"Logo_Gates_Foundation-1.png",
				"Logo_Euro_Comm.png",
				"Logo_UKAID.png",
				"Logo_CGIAR.png",
				"Logo_SDC.png",
				"Logo_IFAD.jpg"
		);
	}

	@RequestMapping(value = "/")
	public String index() {
		return "about";
	}

}
