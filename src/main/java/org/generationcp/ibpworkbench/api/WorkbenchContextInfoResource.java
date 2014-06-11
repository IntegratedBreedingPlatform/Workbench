package org.generationcp.ibpworkbench.api;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.context.ContextInfo;
import org.generationcp.ibpworkbench.SessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This resource is Workbench API to expose context/session information from Workbench for other BMS applications to
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
	public Set<ContextInfo> getContextInfo() {

		Set<ContextInfo> contextInfo = new HashSet<ContextInfo>();
		Map<Integer, SessionData> currentSessions = workbenchContext.getCurrentSessions();
		
		for (Integer key : currentSessions.keySet()) {
			SessionData data = currentSessions.get(key);
			contextInfo.add(new ContextInfo(new Long(data.getUserData().getUserid()), data.getSelectedProject().getProjectId(), 
					data.getUserData().getName(), data.getLastOpenedProject().getProjectId(), data.getSessionId()));
		}
		
		return contextInfo;
	}

}
