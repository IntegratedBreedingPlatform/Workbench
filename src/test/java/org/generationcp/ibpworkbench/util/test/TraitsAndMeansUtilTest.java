package org.generationcp.ibpworkbench.util.test;

import org.generationcp.ibpworkbench.model.TraitsAndMeans;
import org.generationcp.ibpworkbench.util.TraitsAndMeansCSVUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class TraitsAndMeansUtilTest {
    @Autowired
    private TraitsAndMeansCSVUtil traitsAndMeansCSVUtil;

    @Test
    public void csvToList() throws Exception {
        List<TraitsAndMeans> traitsAndMeansList = traitsAndMeansCSVUtil.csvToList("Burkina_trait_means.csv");
        assertNotNull(traitsAndMeansList);
        assertTrue(traitsAndMeansList.size() > 0);
    }
}
