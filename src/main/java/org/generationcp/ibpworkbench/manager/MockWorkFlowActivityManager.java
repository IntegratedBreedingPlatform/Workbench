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

package org.generationcp.ibpworkbench.manager;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkFlowActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MockWorkFlowActivityManager implements IWorkFlowActivityManager{

    private static final Logger LOG = LoggerFactory.getLogger(MockWorkFlowActivityManager.class);
    private List<WorkFlowActivity> activities;
    
    private static MockWorkFlowActivityManager INSTANCE;
    
    private MockWorkFlowActivityManager() throws InternationalizableException  {
        activities = new ArrayList<WorkFlowActivity>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            WorkFlowActivity activity1 = new WorkFlowActivity();
            activity1.setActivityId(1L);
            activity1.setTitle("Load Datasets");
            activity1.setDate(sdf.parse("2012-06-01"));
            activity1.setDueDate(sdf.parse("2012-07-01"));

            WorkFlowActivity activity2 = new WorkFlowActivity();
            activity2.setActivityId(2L);
            activity2.setTitle("Phenotypic analysis");
            activity2.setDate(sdf.parse("2012-07-01"));
            activity2.setDueDate(sdf.parse("2012-08-01"));

            WorkFlowActivity activity3 = new WorkFlowActivity();
            activity3.setActivityId(3L);
            activity3.setTitle("Genotypic analysis");
            activity3.setDate(sdf.parse("2012-08-01"));
            activity3.setDueDate(sdf.parse("2012-09-01"));

            WorkFlowActivity activity4 = new WorkFlowActivity();
            activity4.setActivityId(4L);
            activity4.setTitle("QTL analysis");
            activity4.setDate(sdf.parse("2012-02-01"));
            activity4.setDueDate(sdf.parse("2012-03-01"));

            activities.add(activity1);
            activities.add(activity2);
            activities.add(activity3);
            activities.add(activity4);
        } catch (ParseException e) {
            LOG.error("ParseException", e);
            throw new InternationalizableException(
                    e, Message.PARSE_ERROR, Message.WORKFLOW_DATE_PARSE_ERROR_DESC);
        }
    }

    @Override
    public List<WorkFlowActivity> getUpcomingActivities(Project project) {
        for (WorkFlowActivity activity : activities) {
            activity.setProject(project);
        }

        return activities;
    }

    public static MockWorkFlowActivityManager getInstance() throws InternationalizableException {
        if (INSTANCE == null) {
            INSTANCE = new MockWorkFlowActivityManager();
        }
        return INSTANCE;
    }

}
