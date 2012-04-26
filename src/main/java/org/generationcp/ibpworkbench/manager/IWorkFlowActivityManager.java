package org.generationcp.ibpworkbench.manager;

import java.util.List;

import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkFlowActivity;

public interface IWorkFlowActivityManager {
    public List<WorkFlowActivity> getUpcomingActivities(Project project);
}
