package org.generationcp.ontology.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.rules.TestName;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/ibpworkbench-test.xml")
public class IntegrationTestBase {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Rule
    public TestName name = new TestName();
    private long startTime;

    @Before
    public void beforeEachTest() {
        startTime = System.nanoTime();
    }

    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        LOG.debug("+++++ Test: " + getClass().getSimpleName() + "." + name.getMethodName() + " took " + ((double) elapsedTime / 1000000)
                + " ms = " + ((double) elapsedTime / 1000000000) + " s +++++");
    }
}
