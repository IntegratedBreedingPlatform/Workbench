package org.generationcp.ibpworkbench.api;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.workbench.WorkbenchSessionInfo;
import org.generationcp.ibpworkbench.SessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This resource is an API of Workbench. It is used to expose information from Workbench for other BMS applications to
 * use.
 * 
 * @author Naymesh Mistry
 * 
 */
@Controller
@RequestMapping("/contextinfo")
public class WorkbenchContextInfoResource {

	@Autowired
	private WorkbenchContext workbenchContext;

	/**
	 * This JSON resource is an API that exposes current context information (such as the logged-in user information)
	 * from Workbench for other BMS applications to use.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<WorkbenchSessionInfo> getContextInfo() {

		Set<WorkbenchSessionInfo> sessionInfo = new HashSet<WorkbenchSessionInfo>();
		Map<Integer, SessionData> currentSessions = workbenchContext.getCurrentSessions();
		
		for (Integer key : currentSessions.keySet()) {
			SessionData data = currentSessions.get(key);
			sessionInfo.add(new WorkbenchSessionInfo(data.getUserData().getUserid(), data.getUserData().getName(), data
					.getSelectedProject().getProjectId(), data.getLastOpenedProject().getProjectId(), data.getSessionId()));
		}
		
		return sessionInfo;
	}

}
