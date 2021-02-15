package org.generationcp.ibpworkbench.controller;

import org.generationcp.commons.util.FileNameGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(GenerateFileNameController.URL)
public class GenerateFileNameController {

	public static final String URL = "/fileNameGenerator";

	@RequestMapping(value = "/generateFileName/{fileType}")
	@ResponseBody
	public String getExportFileName(@PathVariable final String fileType, @RequestParam final String fileName) {
		return FileNameGenerator.generateFileName(fileName  + "." +fileType);
	}
}
