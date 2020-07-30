package org.generationcp.ibpworkbench.controller;

import java.util.Arrays;

import javax.annotation.Resource;

import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/variableCache")
public class VariableCacheController {

	@Resource(name = "ontologyVariableDataManager")
	private OntologyVariableDataManager ontologyVariableDataManager;

	@ResponseBody
	@RequestMapping(value = "/{variablesIds}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteVariablesFromCache(@PathVariable final Integer[] variablesIds) {
		this.ontologyVariableDataManager.deleteVariablesFromCache(Arrays.asList(variablesIds));
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
