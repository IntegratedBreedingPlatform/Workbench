package org.generationcp.ontology.service;

import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.service.api.OntologyService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

public class OntologyServiceTest extends IntegrationTestBase {
    
    @Autowired
    OntologyService ontologyService;
    
    @Test
    public void getStandardVariablesByPropertyTest() throws MiddlewareQueryException {

        List<StandardVariable> standardVariables = ontologyService.getStandardVariablesByProperty(1);
        assert(standardVariables.size() > 0);
    }
}
